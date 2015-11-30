package kotmem

import kotmem.unsafe.*

fun processByID(id: Int) = Process(openProcess(id))

fun processByName(name: String): Process = processByID(pidByName(name))

fun keyState(keyCode: Int) = User32.GetKeyState(keyCode)

fun isKeyDown(keyCode: Int) = keyState(keyCode) < 0