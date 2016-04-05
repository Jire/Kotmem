package org.jire.kotmem

import com.sun.jna.Platform
import org.jire.kotmem.win32.User32

object Keys {

	@JvmStatic @JvmName("state")
	operator fun invoke(keyCode: Int) = when {
		Platform.isWindows() -> User32.GetKeyState(keyCode).toInt()
		else -> throw UnsupportedOperationException("Unsupported platform")
	}

	@JvmStatic @JvmName("isPressed")
	operator fun get(vKey: Int) = Keys(vKey) < 0

}