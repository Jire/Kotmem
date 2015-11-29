package kotmem.unsafe

import com.sun.jna.*
import com.sun.jna.platform.win32.WinNT.*

class LPMODULEINFO(@JvmField var lpBaseOfDll: HANDLE? = null, @JvmField var SizeOfImage: Int? = null,
                   @JvmField var EntryPoint: HANDLE? = null) : Structure() {

	override fun getFieldOrder() = listOf("lpBaseOfDll", "SizeOfImage", "EntryPoint")

}