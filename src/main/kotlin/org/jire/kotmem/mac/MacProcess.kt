package org.jire.kotmem.mac

import com.sun.jna.Pointer
import org.jire.kotmem.MemoryBuffer
import org.jire.kotmem.Process

class MacProcess(id: Int, val task: Int) : Process(id) {

	override fun read(address: Pointer, buffer: MemoryBuffer, bytes: Int) {
		if (mac.vm_read(task, address, bytes, buffer, null) != bytes)
			throw IllegalStateException("Read memory failed at address ${Pointer.nativeValue(address)} bytes $bytes")
		Pointer.nativeValue(buffer, Pointer.nativeValue(buffer.getPointer(0)))
	}

	override fun write(address: Pointer, buffer: MemoryBuffer, bytes: Int) {
		if (mac.vm_write(task, address, buffer, bytes) != 0)
			throw IllegalStateException("Write memory failed at address ${Pointer.nativeValue(address)} bytes $bytes")
	}

	override fun resolveModule(moduleName: String) = TODO()

	init {
		// TODO initialize modules
	}

}