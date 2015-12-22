package org.jire.kotmem.win32

import com.sun.jna.*
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary

object Psapi {

	@JvmStatic
	fun EnumProcessModulesEx(hProcess: Pointer, lphModule: Array<WinDef.HMODULE?>,
	                         cb: Int, lpcbNeeded: IntByReference, filterFlag: Int) =
			STD.EnumProcessModulesEx(hProcess, lphModule, cb, lpcbNeeded, filterFlag)

	@JvmStatic
	external fun GetModuleInformation(hProcess: Pointer, hModule: WinDef.HMODULE, lpmodinfo: LPMODULEINFO, cb: Int): Boolean

	@JvmStatic
	external fun GetModuleBaseNameA(hProcess: Pointer, hModule: WinDef.HMODULE, lpBaseName: ByteArray, nSize: Int): Int

	init {
		Native.register(NativeLibrary.getInstance("Psapi"))
	}

}

private val STD = Native.loadLibrary("Psapi", PsapiStd::class.java) as PsapiStd

private interface PsapiStd : StdCallLibrary {

	fun EnumProcessModulesEx(hProcess: Pointer, lphModule: Array<WinDef.HMODULE?>, cb: Int,
	                         lpcbNeeded: IntByReference, filterFlag: Int): Boolean

}