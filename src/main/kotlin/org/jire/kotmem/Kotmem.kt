package org.jire.kotmem

import org.jire.kotmem.unsafe.*

object processes {

	operator fun get(processID: Int) = Process(openProcess(processID))

	operator inline fun get(processID: Int, action: (Process) -> Unit): Process {
		val process = get(processID)
		action.invoke(process)
		return process
	}

	operator fun get(processName: String) = get(pidByName(processName))

	operator inline fun get(processName: String, action: (Process) -> Unit): Process {
		val process = get(processName)
		action.invoke(process)
		return process
	}
	
}

fun keyState(keyCode: Int) = User32.GetKeyState(keyCode)

fun isKeyDown(keyCode: Int) = keyState(keyCode) < 0