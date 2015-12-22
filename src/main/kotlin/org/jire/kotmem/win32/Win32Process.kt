package org.jire.kotmem.win32

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.platform.win32.WinNT
import org.jire.kotmem.Process
import java.nio.ByteBuffer

class Win32Process(val id: Int, val handle: WinNT.HANDLE) : Process<Win32Module>() {

	override fun read(address: Pointer, buffer: ByteBuffer, bytes: Int) {
		if (!readProcessMemory(this, address, buffer, bytes))
			throw Win32Exception(Native.getLastError())
	}

	override fun write(address: Pointer, buffer: ByteBuffer, bytes: Int) {
		if (!writeProcessMemory(this, address, buffer, bytes))
			throw Win32Exception(Native.getLastError())
	}

	override fun resolveModule(moduleName: String) = resolveModule(this, moduleName)

}