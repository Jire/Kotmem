package org.jire.kotmem.win32

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.platform.win32.WinNT
import org.jire.kotmem.MemoryBuffer
import org.jire.kotmem.Process

class Win32Process(id: Int, val handle: WinNT.HANDLE) : Process(id) {

	override fun read(address: Pointer, buffer: MemoryBuffer, bytes: Int) {
		if (!readProcessMemory(this, address, buffer, bytes))
			throw Win32Exception(Native.getLastError())
	}

	override fun write(address: Pointer, buffer: MemoryBuffer, bytes: Int) {
		if (!writeProcessMemory(this, address, buffer, bytes))
			throw Win32Exception(Native.getLastError())
	}

	override fun resolveModule(moduleName: String) = resolveModule(this, moduleName)

}