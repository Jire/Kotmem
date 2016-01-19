package org.jire.kotmem.linux

import com.sun.jna.Pointer
import org.jire.kotmem.*
import java.lang.Long.parseLong
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class LinuxProcess(id: Int, val handle: Pointer) : Process(id) {

	private val local = iovec()
	private val remote = iovec()

	override val modules by lazy {
		val map = HashMap<String, Module>()

		for (line in Files.readAllLines(Paths.get("/proc/$id/maps"))) {
			val split = line.split(" ")
			val regionSplit = split[0].split("-")

			val start = parseLong(regionSplit[0], 16)
			val end = parseLong(regionSplit[1], 16)

			val offset = parseLong(split[2], 16)
			if (offset <= 0) continue

			var path = "";
			var i = 5
			while (i < split.size) {
				val s = split[i].trim { it <= ' ' }
				if (s.isEmpty() && ++i > split.size) break
				else if (s.isEmpty() && !split[i].trim { it <= ' ' }.isEmpty()) path += split[i]
				else if (!s.isEmpty()) path += split[i]
				i++
			}

			val moduleName = path.substring(path.lastIndexOf("/") + 1, path.length)
			map.put(moduleName, LinuxModule(this, Pointer.createConstant(start), moduleName, (end - start).toInt()))
		}

		Collections.unmodifiableMap(map)
	}

	override fun read(address: Pointer, buffer: NativeBuffer, bytes: Int) {
		local.iov_base = buffer
		local.iov_len = bytes

		remote.iov_base = address
		remote.iov_len = bytes

		if (uio.process_vm_readv(id, local, 1, remote, 1, 0) != bytes.toLong())
			throw IllegalStateException("Read memory failed at address ${Pointer.nativeValue(address)} bytes $bytes")
	}

	override fun write(address: Pointer, buffer: NativeBuffer, bytes: Int) {
		local.iov_base = buffer
		local.iov_len = bytes

		remote.iov_base = address
		remote.iov_len = bytes

		if (uio.process_vm_writev(id, local, 1, remote, 1, 0) != bytes.toLong())
			throw IllegalStateException("Write memory failed at address ${Pointer.nativeValue(address)} bytes $bytes")
	}

}