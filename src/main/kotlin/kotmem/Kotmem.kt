package kotmem

import kotmem.unsafe.*

fun processByID(id: Int) = Process(openProcess(id))

inline fun processByID(id: Int, action: (Process) -> Unit): Process {
	val process = processByID(id)
	action.invoke(process)
	return process
}

fun processByName(name: String): Process = processByID(pidByName(name))

inline fun processByName(name: String, action: (Process) -> Unit): Process {
	val process = processByName(name)
	action.invoke(process)
	return process
}

fun keyState(keyCode: Int) = User32.GetKeyState(keyCode)

fun isKeyDown(keyCode: Int) = keyState(keyCode) < 0