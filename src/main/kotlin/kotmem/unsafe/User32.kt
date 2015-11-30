package kotmem.unsafe

import com.sun.jna.*

object User32 {

	external fun GetKeyState(vKey: Int): Short

	init {
		Native.register("user32")
	}

}