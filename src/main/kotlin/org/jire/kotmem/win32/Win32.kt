@file:JvmName("Win32")

package org.jire.kotmem.win32

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Tlhelp32
import com.sun.jna.platform.win32.WinDef.HMODULE
import com.sun.jna.ptr.IntByReference
import org.jire.kotmem.MemoryBuffer
import java.util.*

const val PROCESS_QUERY_INFORMATION = 0x400
const val PROCESS_VM_READ = 0x10
const val PROCESS_VM_WRITE = 0x20
const val PROCESS_VM_OPERATION = 0x8

const val PROCESS_FULL_ACCESS = PROCESS_QUERY_INFORMATION or PROCESS_VM_READ or PROCESS_VM_WRITE or PROCESS_VM_OPERATION

fun pidByName(name: String): Int {
	val snapshot = Kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPALL, 0)
	val entry = Tlhelp32.PROCESSENTRY32.ByReference()
	try {
		while (Kernel32.Process32Next(snapshot, entry)) {
			val processName = Native.toString(entry.szExeFile)
			if (name.equals(processName))
				return entry.th32ProcessID.toInt()
		}
		throw NullPointerException("Could not find ID")
	} finally {
		Kernel32.CloseHandle(snapshot)
	}
}

fun openProcess(processID: Int, accessFlags: Int = PROCESS_FULL_ACCESS)
		= Win32Process(processID, Kernel32.OpenProcess(accessFlags, true, processID))

fun resolveModules(process: Win32Process): Set<Win32Module> {
	val list = HashSet<Win32Module>()

	val hProcess = process.handle.pointer
	val modules = arrayOfNulls<HMODULE>(1024)
	val needed = IntByReference()
	if (Psapi.EnumProcessModulesEx(hProcess, modules, modules.size, needed, 1)) {
		for (i in 0..needed.value / 4) {
			val module = modules[i] ?: continue
			val info = LPMODULEINFO()
			if (Psapi.GetModuleInformation(hProcess, module, info, info.size()))
				list.add(Win32Module(process, module, info))
		}
	}

	return list
}

fun resolveModuleName(module: Win32Module): String {
	val lpBaseName = ByteArray(256)
	Psapi.GetModuleBaseNameA((module.process as Win32Process).handle.pointer,
			module.hModule, lpBaseName, lpBaseName.size)
	return Native.toString(lpBaseName)
}

fun resolveModule(process: Win32Process, moduleName: String) =
		resolveModules(process).first { moduleName == resolveModuleName(it) }

fun resolveModuleAddress(module: Win32Module) = Pointer.nativeValue(module.lpModuleInfo.lpBaseOfDll!!.pointer)

fun readProcessMemory(process: Win32Process, address: Pointer, buffer: MemoryBuffer, bytes: Int) =
		Kernel32.ReadProcessMemory(process.handle.pointer, address, buffer, bytes, 0) > 0

fun writeProcessMemory(process: Win32Process, address: Pointer, buffer: MemoryBuffer, bytes: Int) =
		Kernel32.WriteProcessMemory(process.handle.pointer, address, buffer, bytes, 0) > 0