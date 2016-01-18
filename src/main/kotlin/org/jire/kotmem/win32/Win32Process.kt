package org.jire.kotmem.win32

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.*
import com.sun.jna.ptr.IntByReference
import org.jire.kotmem.MemoryBuffer
import org.jire.kotmem.Process
import java.util.*

class Win32Process(id: Int, val handle: WinNT.HANDLE) : Process(id) {

	override val modules by lazy {
		val map = HashMap<String, Win32Module>()

		val hProcess = handle.pointer
		val modules = arrayOfNulls<WinDef.HMODULE>(1024)
		val needed = IntByReference()
		if (Psapi.EnumProcessModulesEx(hProcess, modules, modules.size, needed, 1)) {
			for (i in 0..needed.value / 4) {
				val module = modules[i] ?: continue
				val info = LPMODULEINFO()
				if (Psapi.GetModuleInformation(hProcess, module, info, info.size())) {
					val win32Module = Win32Module(this, module, info)
					map.put(win32Module.name, win32Module)
				}
			}
		}

		Collections.unmodifiableMap(map)
	}

	override fun read(address: Pointer, buffer: MemoryBuffer, bytes: Int) {
		if (!readProcessMemory(this, address, buffer, bytes))
			throw Win32Exception(Native.getLastError())
	}

	override fun write(address: Pointer, buffer: MemoryBuffer, bytes: Int) {
		if (!writeProcessMemory(this, address, buffer, bytes))
			throw Win32Exception(Native.getLastError())
	}

}