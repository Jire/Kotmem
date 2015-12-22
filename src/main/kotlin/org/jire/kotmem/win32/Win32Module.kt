package org.jire.kotmem.win32

import com.sun.jna.platform.win32.WinDef
import org.jire.kotmem.Module

class Win32Module(process: Win32Process, val hModule: WinDef.HMODULE,
                  val lpModuleInfo: LPMODULEINFO) : Module(process) {

	override val name by lazy { resolveModuleName(this) }

	override val address by lazy { resolveModuleAddress(this) }

	val pointer = hModule.pointer!!

	val size = lpModuleInfo.SizeOfImage!!

}