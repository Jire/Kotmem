package org.jire.kotmem.linux

import com.sun.jna.Pointer
import com.sun.jna.Structure

class iovec(@JvmField var iov_base: Pointer? = null, @JvmField var iov_len: Int? = null) : Structure() {

	override fun getFieldOrder() = arrayListOf("iov_base", "iov_len")

}