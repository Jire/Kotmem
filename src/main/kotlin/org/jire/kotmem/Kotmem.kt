@file:JvmMultifileClass
@file:JvmName("Kotmem")

package org.jire.kotmem

import com.sun.jna.Pointer
import org.jire.kotmem.win32.*
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

object Processes {

	@JvmStatic
	operator fun get(processID: Int): Process = openProcess(processID) // TODO choose platform

	@JvmStatic
	operator inline fun get(processID: Int, action: (Process) -> Unit): Process {
		val process = get(processID)
		action(process)
		return process
	}

	@JvmStatic
	operator fun get(processName: String) = get(pidByName(processName))

	@JvmStatic
	operator inline fun get(processName: String, action: (Process) -> Unit): Process {
		val process = get(processName)
		action(process)
		return process
	}

}

object Keys {

	// TODO make this multi-platform

	@JvmStatic fun state(vKey: Int) = User32.GetKeyState(vKey)

	operator fun invoke(vKey: Int) = state(vKey)

	@JvmStatic operator fun get(vKey: Int) = state(vKey) < 0

}

var kotmemLock: Lock = ReentrantLock(true)

inline fun <T> lock(body: () -> T): T = lock(kotmemLock, body)

inline fun <T> lock(lock: Lock, body: () -> T): T {
	lock.lock()
	try {
		return body.invoke()
	} finally {
		lock.unlock()
	}
}

private val pointer = ThreadLocal.withInitial { Pointer(0) }

fun cachedPointer(address: Long): Pointer {
	val pointer = pointer.get()
	Pointer.nativeValue(pointer, address)
	return pointer
}

private val bufferByClass = HashMap<Class<*>, NativeBuffer>()

fun cachedBuffer(type: Class<*>, bytes: Int): NativeBuffer {
	var buf = bufferByClass[type]
	if (buf == null) {
		buf = NativeBuffer(bytes.toLong())
		bufferByClass.put(type, buf)
	}
	return buf
}