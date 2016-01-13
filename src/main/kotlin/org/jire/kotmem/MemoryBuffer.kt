package org.jire.kotmem

import com.sun.jna.Native
import com.sun.jna.Pointer

class MemoryBuffer(val size: Long, val peer: Long = Native.malloc(size)) : Pointer(peer) {

	fun byte() = getByte(0)

	fun short() = getShort(0)

	fun int() = getInt(0)

	fun long() = getLong(0)

	fun float() = getFloat(0)

	fun double() = getDouble(0)

	fun boolean() = if (byte() > 0) true else false

	infix fun bytes(dest: ByteArray) = read(0, dest, 0, dest.size)

	infix fun byte(value: Byte) = daisy { setByte(0, value) }

	infix fun short(value: Short) = daisy { setShort(0, value) }

	infix fun int(value: Int) = daisy { setInt(0, value) }

	infix fun long(value: Long) = daisy { setLong(0, value) }

	infix fun float(value: Float) = daisy { setFloat(0, value) }

	infix fun double(value: Double) = daisy { setDouble(0, value) }

	infix fun boolean(value: Boolean) = byte(if (value) 1 else 0)

}

inline fun <T> T.daisy(body: () -> Any): T {
	body()
	return this
}