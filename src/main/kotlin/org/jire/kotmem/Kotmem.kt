package org.jire.kotmem

import org.jire.kotmem.unsafe.*
import java.util.concurrent.locks.*

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

fun keyState(keyCode: Int) = User32.GetKeyState(keyCode)

fun isKeyDown(keyCode: Int) = keyState(keyCode) < 0

val kotmemLock = ReentrantLock(true)

inline fun <T> lock(body: () -> T): T = lock(kotmemLock as Lock, body)

inline fun <T> lock(lock: Lock, body: () -> T): T {
	lock.lock()
	try {
		return body.invoke()
	} finally {
		lock.unlock()
	}
}