package kotmem

import kotmem.unsafe.*

fun processByID(id: Int) = Process(openProcess(id))

fun processByName(name: String): Process = processByID(pidByName(name))