@file:JvmMultifileClass
@file:JvmName("Caching")

package org.jire.kotmem

import com.sun.jna.Pointer
import java.util.*

private val pointer = ThreadLocal.withInitial { Pointer(0) }

fun cachedPointer(address: Long): Pointer {
	val pointer = pointer.get()
	Pointer.nativeValue(pointer, address)
	return pointer
}

private val bufferByClass = ThreadLocal.withInitial { HashMap<Class<*>, NativeBuffer>() }

fun cachedBuffer(type: Class<*>, bytes: Int): NativeBuffer {
	var buf = bufferByClass.get()[type]
	if (buf == null) {
		buf = NativeBuffer(bytes.toLong())
		bufferByClass.get().put(type, buf)
	}
	return buf
}