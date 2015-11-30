package kotmem.unsafe

import com.sun.jna.*
import com.sun.jna.platform.win32.*
import com.sun.jna.platform.win32.WinDef.*
import com.sun.jna.ptr.*
import java.nio.*
import java.util.*
import java.util.concurrent.locks.*

const val PROCESS_QUERY_INFORMATION = 0x400
const val PROCESS_VM_READ = 0x10
const val PROCESS_VM_WRITE = 0x20
const val PROCESS_VM_OPERATION = 0x8

const val PROCESS_FULL_ACCESS = PROCESS_QUERY_INFORMATION or PROCESS_VM_READ or PROCESS_VM_WRITE or PROCESS_VM_OPERATION

fun <T : PointerType?> combinePointerTypes(objects: Array<T>): Pointer {
	val memory = Memory(Pointer.SIZE * objects.size.toLong())
	var offset = 0L
	for (obj in objects) {
		memory.setPointer(Pointer.SIZE * offset, if (obj == null) null else obj.getPointer())
		offset++
	}
	return memory
}

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
		= UnsafeProcess(processID, Kernel32.OpenProcess(accessFlags, true, processID))

fun resolveModules(process: UnsafeProcess): Set<UnsafeModule> {
	val list = HashSet<UnsafeModule>()

	val hProcess = process.handle.pointer
	val modules = arrayOfNulls<WinDef.HMODULE>(1024)
	val needed = IntByReference()
	if (Psapi.EnumProcessModulesEx(hProcess, modules, modules.size, needed, 1)) {
		for (i in 0..needed.value / 4) {
			val module = modules[i] ?: continue
			val info = LPMODULEINFO()
			if (Psapi.GetModuleInformation(hProcess, module, info, info.size()))
				list.add(UnsafeModule(process, module, info))
		}
	}

	return list
}

fun resolveModuleName(module: UnsafeModule): String {
	val lpBaseName = ByteArray(256)
	Psapi.GetModuleBaseNameA(module.process.handle.pointer, module.module, lpBaseName, lpBaseName.size)
	return Native.toString(lpBaseName)
}

fun resolveModule(process: UnsafeProcess, moduleName: String) =
		resolveModules(process).first { moduleName == resolveModuleName(it) }

fun resolveModuleAddress(module: UnsafeModule) = Pointer.nativeValue(module.info.lpBaseOfDll?.pointer)

fun readProcessMemory(process: UnsafeProcess, address: Long, buffer: ByteBuffer, bytes: Int) =
		Kernel32.ReadProcessMemory(process.handle.pointer, address, buffer, bytes, 0) > 0

fun writeProcessMemory(process: UnsafeProcess, address: Long, buffer: ByteBuffer, bytes: Int) =
		Kernel32.WriteProcessMemory(process.handle.pointer, address, buffer, bytes, 0) > 0

class UnsafeProcess(val id: Int, val handle: WinNT.HANDLE)
class UnsafeModule(val process: UnsafeProcess, val module: HMODULE, val info: LPMODULEINFO)

val lock = ReentrantLock(true)

inline fun <T> lock(body: () -> T): T = lock(lock as Lock, body)

inline fun <T> lock(lock: Lock, body: () -> T): T {
	lock.lock()
	try {
		return body.invoke()
	} finally {
		lock.unlock()
	}
}