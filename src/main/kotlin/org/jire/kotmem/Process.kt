package org.jire.kotmem

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Win32Exception
import org.jire.kotmem.unsafe.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class Process(val unsafe: UnsafeProcess) {

	val modules by lazy { HashSet<Module>().addAll(resolveModules(unsafe) as Collection<Module>) }

	private val moduleCache = HashMap<String, Module>()
	private val bufferCache = HashMap<Class<*>, ByteBuffer>()
	private val pointer = ThreadLocal.withInitial { Pointer(0) }

	fun bufferOf(type: Class<*>, bytes: Int): ByteBuffer {
		var buf = bufferCache[type]
		if (buf == null) {
			buf = ByteBuffer.allocateDirect(bytes)
			bufferCache.put(type, buf)
		} else buf.clear()
		return buf!!.order(ByteOrder.nativeOrder())
	}

	fun pointerOf(address: Long): Pointer {
		val pointer = pointer.get()
		Pointer.nativeValue(pointer, address)
		return pointer
	}

	fun pointerOf(address: Int) = pointerOf(address.toLong())

	operator inline fun <reified T : Any> get(address: Int): T = get(address.toLong())

	operator inline fun <reified T : Any> get(address: Long): T = get(address, dataTypeOf(T::class.java).bytes)

	operator inline fun <reified T : Any> get(address: Long, size: Int): T = get(pointerOf(address), size)

	operator inline fun <reified T : Any> get(address: Pointer, bytes: Int) = lock {
		val type = T::class.java
		val dataType = dataTypeOf(type)
		val buf = bufferOf(type, bytes)
		if (!readProcessMemory(unsafe, address, buf, bytes))
			throw Win32Exception(Native.getLastError())
		buf.rewind()
		dataType.read(buf)
	}

	operator inline fun <reified T : Any> set(address: Long, data: T): Unit = set(pointerOf(address), data)

	operator inline fun <reified T : Any> set(address: Pointer, data: T) = lock {
		val type = T::class.java
		val dataType = dataTypeOf(type)
		val bytes = dataType.bytes
		val buf = bufferOf(type, bytes)
		dataType.write(buf, data)
		buf.flip()
		if (!writeProcessMemory(unsafe, address, buf, bytes))
			throw Win32Exception(Native.getLastError())
	}

	operator inline fun <reified T : Any> set(address: Int, data: T): Unit = set(address.toLong(), data)

	operator fun get(moduleName: String): Module {
		if (moduleCache.contains(moduleName)) return moduleCache[moduleName]!!
		val module = Module(this, resolveModule(unsafe, moduleName))
		moduleCache.put(moduleName, module)
		return module
	}

}