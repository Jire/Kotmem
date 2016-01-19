package org.jire.kotmem.linux

import com.sun.jna.Native
import com.sun.jna.NativeLibrary

object uio {

	@JvmStatic
	external fun process_vm_readv(pid: Int, local: iovec, liovcnt: Long, remote: iovec,
	                              riovcnt: Long, flags: Long): Long

	@JvmStatic
	external fun process_vm_writev(pid: Int, local: iovec, liovcnt: Long,
	                               remote: iovec, riovcnt: Long, flags: Long): Long

	init {
		Native.register(NativeLibrary.getInstance("c"))
	}

}