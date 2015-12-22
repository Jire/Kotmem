package org.jire.kotmem.win32

import com.sun.jna.Native

object User32 {

	@JvmStatic
	external fun GetKeyState(vKey: Int): Short

	init {
		Native.register("user32")
	}

}