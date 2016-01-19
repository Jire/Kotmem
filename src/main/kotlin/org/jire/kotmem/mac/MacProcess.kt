package org.jire.kotmem.mac

import com.sun.jna.Pointer
import org.jire.kotmem.*
import java.util.*

class MacProcess(id: Int, val task: Int) : Process(id) {

	override val modules by lazy {
		val map = HashMap<String, Module>()

		// TODO resolve modules

		Collections.unmodifiableMap(map)
	}

	override fun read(address: Pointer, buffer: NativeBuffer, bytes: Int) {
		if (mac.vm_read(task, address, bytes, buffer, null) != bytes)
			throw IllegalStateException("Read memory failed at address ${Pointer.nativeValue(address)} bytes $bytes")
		Pointer.nativeValue(buffer, Pointer.nativeValue(buffer.getPointer(0)))
	}

	override fun write(address: Pointer, buffer: NativeBuffer, bytes: Int) {
		if (mac.vm_write(task, address, buffer, bytes) != 0)
			throw IllegalStateException("Write memory failed at address ${Pointer.nativeValue(address)} bytes $bytes")
	}

}