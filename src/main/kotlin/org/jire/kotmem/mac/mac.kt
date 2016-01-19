package org.jire.kotmem.mac

import com.sun.jna.*
import com.sun.jna.ptr.IntByReference
import org.jire.kotmem.NativeBuffer

object mac {

	@JvmStatic
	external fun task_for_pid(taskID: Int, pid: Int, out: IntByReference?): Int

	@JvmStatic
	external fun getpid(): Int

	@JvmStatic
	external fun mach_task_self(): Int

	@JvmStatic
	external fun vm_write(taskID: Int, address: Pointer, buffer: NativeBuffer, size: Int): Int

	@JvmStatic
	external fun vm_read(taskID: Int, address: Pointer, size: Int, buffer: NativeBuffer, ref: IntByReference?): Int

	@JvmStatic
	external fun mach_error_string(result: Int): String

	init {
		Native.register(NativeLibrary.getInstance("c"))
	}

}