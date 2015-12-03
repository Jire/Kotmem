package org.jire.kotmem.unsafe

import com.sun.jna.Native

object User32 {

	external fun GetKeyState(vKey: Int): Short

	init {
		Native.register("user32")
	}

}