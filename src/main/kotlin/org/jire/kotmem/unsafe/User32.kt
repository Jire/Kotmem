package org.jire.kotmem.unsafe

import com.sun.jna.Native

object User32 {

	@JvmStatic
	external fun GetKeyState(vKey: Int): Short

	init {
		Native.register("user32")
	}

}