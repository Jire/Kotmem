@file:JvmName("Win32")

package org.jire.kotmem.win32

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Tlhelp32
import org.jire.kotmem.NativeBuffer

const val PROCESS_QUERY_INFORMATION = 0x400
const val PROCESS_VM_READ = 0x10
const val PROCESS_VM_WRITE = 0x20
const val PROCESS_VM_OPERATION = 0x8

const val PROCESS_FULL_ACCESS = PROCESS_QUERY_INFORMATION or PROCESS_VM_READ or PROCESS_VM_WRITE or PROCESS_VM_OPERATION

fun processIDByName(processName: String): Int {
	val snapshot = Kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPALL, 0)
	val entry = Tlhelp32.PROCESSENTRY32.ByReference()
	try {
		while (Kernel32.Process32Next(snapshot, entry)) {
			val entryName = Native.toString(entry.szExeFile)
			if (processName.equals(entryName))
				return entry.th32ProcessID.toInt()
		}
		throw IllegalStateException("Could not find process ID of \"$processName\"")
	} finally {
		Kernel32.CloseHandle(snapshot)
	}
}

fun openProcess(processID: Int, accessFlags: Int = PROCESS_FULL_ACCESS)
		= Win32Process(processID, Kernel32.OpenProcess(accessFlags, true, processID))

fun readProcessMemory(process: Win32Process, address: Pointer, buffer: NativeBuffer, bytes: Int) =
		Kernel32.ReadProcessMemory(process.handle.pointer, address, buffer, bytes, 0) > 0

fun writeProcessMemory(process: Win32Process, address: Pointer, buffer: NativeBuffer, bytes: Int) =
		Kernel32.WriteProcessMemory(process.handle.pointer, address, buffer, bytes, 0) > 0