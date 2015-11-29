package kotmem.unsafe

import com.sun.jna.*
import com.sun.jna.platform.win32.*
import com.sun.jna.ptr.*
import com.sun.jna.win32.*

object Psapi {

	fun EnumProcessModulesEx(hProcess: Pointer, lphModule: Array<WinDef.HMODULE?>,
	                         cb: Int, lpcbNeeded: IntByReference, filterFlag: Int) =
			STD.EnumProcessModulesEx(hProcess, lphModule, cb, lpcbNeeded, filterFlag)

	external fun GetModuleInformation(hProcess: Pointer, hModule: WinDef.HMODULE, lpmodinfo: LPMODULEINFO, cb: Int): Boolean

	external fun GetModuleBaseNameA(hProcess: Pointer, hModule: WinDef.HMODULE, lpBaseName: ByteArray, nSize: Int): Int

	init {
		Native.register(NativeLibrary.getInstance("Psapi"))
	}

	private val STD = Native.loadLibrary("Psapi", PsapiStd::class.java) as PsapiStd

}

private interface PsapiStd : StdCallLibrary {

	fun EnumProcessModulesEx(hProcess: Pointer, lphModule: Array<WinDef.HMODULE?>, cb: Int,
	                         lpcbNeeded: IntByReference, filterFlag: Int): Boolean

}