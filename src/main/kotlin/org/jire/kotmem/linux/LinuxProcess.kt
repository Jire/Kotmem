package org.jire.kotmem.linux

import com.sun.jna.Pointer
import org.jire.kotmem.*
import java.util.*

class LinuxProcess(id: Int, val handle: Pointer) : Process(id) {

	private val local = iovec()
	private val remote = iovec()

	override val modules by lazy {
		val map = HashMap<String, Module>()

		// TODO resolve modules

		Collections.unmodifiableMap(map)
	}

	override fun read(address: Pointer, buffer: MemoryBuffer, bytes: Int) {
		local.iov_base = buffer
		local.iov_len = bytes

		remote.iov_base = address
		remote.iov_len = bytes

		if (uio.process_vm_readv(id, local, 1, remote, 1, 0) != bytes.toLong())
			throw IllegalStateException("Read memory failed at address ${Pointer.nativeValue(address)} bytes $bytes")
	}

	override fun write(address: Pointer, buffer: MemoryBuffer, bytes: Int) {
		local.iov_base = buffer
		local.iov_len = bytes

		remote.iov_base = address
		remote.iov_len = bytes

		if (uio.process_vm_writev(id, local, 1, remote, 1, 0) != bytes.toLong())
			throw IllegalStateException("Write memory failed at address ${Pointer.nativeValue(address)} bytes $bytes")
	}

}