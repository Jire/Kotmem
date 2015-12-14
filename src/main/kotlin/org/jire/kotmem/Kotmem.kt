package org.jire.kotmem

import org.jire.kotmem.unsafe.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

object processes {

	operator fun get(processID: Int) = Process(openProcess(processID))

	operator inline fun get(processID: Int, action: (Process) -> Unit): Process {
		val process = get(processID)
		action(process)
		return process
	}

	operator fun get(processName: String) = get(pidByName(processName))

	operator inline fun get(processName: String, action: (Process) -> Unit): Process {
		val process = get(processName)
		action(process)
		return process
	}

}

object keys {

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