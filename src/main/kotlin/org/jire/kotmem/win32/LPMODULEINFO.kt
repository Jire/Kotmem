package org.jire.kotmem.win32

import com.sun.jna.Structure
import com.sun.jna.platform.win32.WinNT.HANDLE

class LPMODULEINFO(@JvmField var lpBaseOfDll: HANDLE? = null, @JvmField var SizeOfImage: Int? = null,
                   @JvmField var EntryPoint: HANDLE? = null) : Structure() {

	override fun getFieldOrder() = listOf("lpBaseOfDll", "SizeOfImage", "EntryPoint")

}