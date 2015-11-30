package kotmem.unsafe

import com.sun.jna.*
import com.sun.jna.platform.win32.*
import com.sun.jna.platform.win32.WinNT.*
import com.sun.jna.win32.*
import java.nio.*

object Kernel32 {

	external fun CreateToolhelp32Snapshot(dwFlags: WinDef.DWORD, th32ProcessID: Int): HANDLE

	external fun Process32Next(hSnapshot: HANDLE, lppe: Tlhelp32.PROCESSENTRY32): Boolean

	external fun OpenProcess(dwDesiredAccess: Int, bInheritHandle: Boolean, dwProcessId: Int): HANDLE

	external fun CloseHandle(hObject: HANDLE): Boolean

	external fun WriteProcessMemory(hProcess: Pointer, lpBaseAddress: Long,
	                                lpBuffer: ByteBuffer, nSize: Int, lpNumberOfBytesWritten: Int): Long

	external fun ReadProcessMemory(hProcess: Pointer, lpBaseAddress: Long,
	                               lpBuffer: ByteBuffer, nSize: Int, lpNumberOfBytesWritten: Int): Long

	init {
		Native.register(NativeLibrary.getInstance("Kernel32", W32APIOptions.UNICODE_OPTIONS))
	}

}