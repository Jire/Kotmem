package org.jire.kotmem.win32

import com.sun.jna.*
import com.sun.jna.platform.win32.Tlhelp32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinNT.HANDLE
import com.sun.jna.win32.W32APIOptions

object Kernel32 {

	@JvmStatic
	external fun CreateToolhelp32Snapshot(dwFlags: WinDef.DWORD, th32ProcessID: Int): HANDLE

	@JvmStatic
	external fun Process32Next(hSnapshot: HANDLE, lppe: Tlhelp32.PROCESSENTRY32): Boolean

	@JvmStatic
	external fun OpenProcess(dwDesiredAccess: Int, bInheritHandle: Boolean, dwProcessId: Int): HANDLE

	@JvmStatic
	external fun CloseHandle(hObject: HANDLE): Boolean

	@JvmStatic
	external fun WriteProcessMemory(hProcess: Pointer, lpBaseAddress: Pointer, lpBuffer: Pointer,
	                                nSize: Int, lpNumberOfBytesWritten: Int): Long

	@JvmStatic
	external fun ReadProcessMemory(hProcess: Pointer, lpBaseAddress: Pointer, lpBuffer: Pointer,
	                               nSize: Int, lpNumberOfBytesWritten: Int): Long

	init {
		Native.register(NativeLibrary.getInstance("Kernel32", W32APIOptions.UNICODE_OPTIONS))
	}

}