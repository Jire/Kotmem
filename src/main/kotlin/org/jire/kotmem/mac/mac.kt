package org.jire.kotmem.mac

import com.sun.jna.*
import com.sun.jna.ptr.IntByReference
import org.jire.kotmem.MemoryBuffer

object mac {

	external fun task_for_pid(taskID: Int, pid: Int, out: IntByReference?): Int

	external fun getpid(): Int

	external fun mach_task_self(): Int

	external fun vm_write(taskID: Int, address: Pointer, buffer: MemoryBuffer, size: Int): Int

	external fun vm_read(taskID: Int, address: Pointer, size: Int, buffer: MemoryBuffer, ref: IntByReference?): Int

	external fun mach_error_string(result: Int): String

	init {
		Native.register(NativeLibrary.getInstance("c"))
	}

}