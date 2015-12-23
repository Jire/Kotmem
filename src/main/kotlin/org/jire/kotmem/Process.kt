package org.jire.kotmem

import com.sun.jna.Pointer
import java.nio.ByteBuffer
import java.util.*

abstract class Process {

	private val moduleCache = HashMap<String, Module>()

	operator inline fun <reified T : Any> get(address: Pointer, dataType: DataType<T>): T = lock {
		val type = T::class.java
		val bytes = dataType.bytes
		val buffer = cachedBuffer(type, bytes)
		read(address, buffer, bytes)
		buffer.rewind()
		dataType.read(buffer)
	}

	/**
	 * Reads the specified amount of bytes at the address into a non-cached `ByteBuffer`.
	 *
	 * *Warning:* This method should not be used often, it creates a buffer of the specified bytes size.
	 *
	 * @param address A pointer to the address to read from.
	 * @param bytes The amount of bytes to read into the buffer.
	 * @return The non-cached `ByteBuffer`.
	 */
	operator fun get(address: Pointer, bytes: Int): ByteBuffer {
		val buffer = ByteBuffer.allocateDirect(bytes)
		lock { read(address, buffer, bytes) }
		return buffer
	}

	operator inline fun <reified T : Any> get(address: Long, dataType: DataType<T>): T = get(cachedPointer(address), dataType)

	operator inline fun <reified T : Any> get(address: Long): T = get(address, dataTypeOf(T::class.java))

	operator inline fun <reified T : Any> get(address: Int): T = get(address.toLong())

	operator inline fun <reified T : Any> set(address: Pointer, data: T) {
		val type = T::class.java
		val dataType = dataTypeOf(type)
		val bytes = dataType.bytes
		val buf = cachedBuffer(type, bytes)
		dataType.write(buf, data)
		buf.flip()
		write(address, buf, bytes)
	}

	operator inline fun <reified T : Any> set(address: Long, data: T): Unit = set(cachedPointer(address), data)

	operator inline fun <reified T : Any> set(address: Int, data: T): Unit = set(address.toLong(), data)

	operator fun get(moduleName: String): Module {
		if (moduleCache.contains(moduleName)) return moduleCache[moduleName]!!
		val module = resolveModule(moduleName)
		moduleCache.put(moduleName, module)
		return module
	}

	protected abstract fun read(address: Pointer, buffer: ByteBuffer, bytes: Int)

	protected abstract fun write(address: Pointer, buffer: ByteBuffer, bytes: Int)

	protected abstract fun resolveModule(moduleName: String): Module

}