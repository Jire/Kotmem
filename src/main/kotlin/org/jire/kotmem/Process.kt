package org.jire.kotmem

import com.sun.jna.Pointer

abstract class Process(val id: Int) {

	abstract val modules: Map<String, Module>

	/**
	 * Reads the specified amount of bytes at the address into a non-cached `NativeBuffer`.
	 *
	 * **Warning:** This method should not be used often, it creates a buffer of the specified bytes size.
	 *
	 * @param address A pointer to the address to read from.
	 * @param bytes The amount of bytes to read into the buffer.
	 * @return The non-cached  `NativeBuffer`.
	 */
	operator fun get(address: Pointer, bytes: Int): NativeBuffer {
		val buffer = NativeBuffer(bytes.toLong())
		read(address, buffer, bytes)
		return buffer
	}

	operator fun get(address: Long, bytes: Int) = get(cachedPointer(address), bytes)

	operator fun get(address: Int, bytes: Int) = get(address.toLong(), bytes)

	operator inline fun <reified T : Any> get(address: Pointer, dataType: DataType<T>): T {
		val type = T::class.java
		val bytes = dataType.bytes
		val buffer = cachedBuffer(type, bytes)
		read(address, buffer, bytes)
		return dataType.read(buffer)
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
		write(address, buf, bytes)
	}

	operator inline fun <reified T : Any> set(address: Long, data: T): Unit = set(cachedPointer(address), data)

	operator inline fun <reified T : Any> set(address: Int, data: T): Unit = set(address.toLong(), data)

	operator fun get(moduleName: String) = modules[moduleName]!!

	abstract fun read(address: Pointer, buffer: NativeBuffer, bytes: Int)

	abstract fun write(address: Pointer, buffer: NativeBuffer, bytes: Int)

}