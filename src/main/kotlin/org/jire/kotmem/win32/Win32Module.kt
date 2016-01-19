package org.jire.kotmem.win32

import com.sun.jna.Native
import com.sun.jna.platform.win32.WinDef
import org.jire.kotmem.Module

class Win32Module(process: Win32Process, val hModule: WinDef.HMODULE,
                  val lpModuleInfo: LPMODULEINFO) : Module(process, hModule.pointer) {

	override val name by lazy {
		val lpBaseName = ByteArray(256)
		Psapi.GetModuleBaseNameA(process.handle.pointer, hModule, lpBaseName, lpBaseName.size)
		Native.toString(lpBaseName)
	}

	override val size = lpModuleInfo.SizeOfImage!!

}