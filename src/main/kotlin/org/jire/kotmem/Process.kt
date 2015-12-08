package org.jire.kotmem

import com.sun.jna.Native
import com.sun.jna.platform.win32.Win32Exception
import org.jire.kotmem.unsafe.*
import java.nio.*
import java.util.*
import kotlin.reflect.KClass

val TYPE_TO_BYTES = mapOf(Boolean::class.qualifiedName to 1, Byte::class.qualifiedName to 1,
		Short::class.qualifiedName to 2, Int::class.qualifiedName to 4, Long::class.qualifiedName to 8,
		Float::class.qualifiedName to 4, Double::class.qualifiedName to 8)

val TYPE_TO_QN = mapOf(Boolean::class to Boolean::class.qualifiedName, Byte::class to Byte::class.qualifiedName,
		Short::class to Short::class.qualifiedName, Int::class to Int::class.qualifiedName,
		Long::class to Long::class.qualifiedName, Float::class to Float::class.qualifiedName,
		Double::class to Double::class.qualifiedName)

class Process(val unsafe: UnsafeProcess) {

	val modules by lazy { HashSet<Module>().addAll(resolveModules(unsafe) as Collection<Module>) }

	private val modulesByName = HashMap<String, Module>()
	private val memoryCache = HashMap<KClass<*>, ByteBuffer>()

	fun memoryOf(type: KClass<*>, bytes: Int): ByteBuffer {
		var memory = memoryCache[type]
		if (memory == null) {
			memory = ByteBuffer.allocateDirect(bytes)
			memoryCache.put(type, memory)
		} else memory.clear()
		return memory!!.order(ByteOrder.nativeOrder())
	}

	operator inline fun <reified T> get(address: Long): T {
		val type = T::class
		val qn = TYPE_TO_QN.getRaw(type)!!
		val bytes = TYPE_TO_BYTES.getRaw(qn)!!
		val memory = memoryOf(type, bytes)
		if (!readProcessMemory(unsafe, address, memory, bytes))
			throw Win32Exception(Native.getLastError())
		memory.rewind()
		return when (qn) {
			TYPE_TO_QN.getRaw(Boolean::class) -> memory.get() > 0
			TYPE_TO_QN.getRaw(Byte::class) -> memory.get()
			TYPE_TO_QN.getRaw(Short::class) -> memory.short
			TYPE_TO_QN.getRaw(Int::class) -> memory.int
			TYPE_TO_QN.getRaw(Long::class) -> memory.long
			TYPE_TO_QN.getRaw(Float::class) -> memory.float
			TYPE_TO_QN.getRaw(Double::class) -> memory.double
			else -> throw AssertionError("Impossible case of invalid type \"${type.simpleName}\"")
		} as T
	}

	operator inline fun <reified T> get(address: Int): T = get(address.toLong())

	operator inline fun <reified T> set(address: Long, data: T) = lock {
		val type = T::class
		val qn = TYPE_TO_QN.getRaw(type)!!
		val bytes = TYPE_TO_BYTES.getRaw(qn)!!
		val memory = memoryOf(type, bytes)
		when (qn) {
			TYPE_TO_QN.getRaw(Boolean::class) -> memory.put((if (data as Boolean) 1 else 0).toByte())
			TYPE_TO_QN.getRaw(Byte::class) -> memory.put(data as Byte)
			TYPE_TO_QN.getRaw(Short::class) -> memory.putShort(data as Short)
			TYPE_TO_QN.getRaw(Int::class) -> memory.putInt(data as Int)
			TYPE_TO_QN.getRaw(Long::class) -> memory.putLong(data as Long)
			TYPE_TO_QN.getRaw(Float::class) -> memory.putFloat(data as Float)
			TYPE_TO_QN.getRaw(Double::class) -> memory.putDouble(data as Double)
			else -> throw AssertionError("Impossible case of invalid type \"${type.simpleName}\"")
		}
		memory.flip()
		if (!writeProcessMemory(unsafe, address, memory, bytes))
			throw Win32Exception(Native.getLastError())
	}

	operator inline fun <reified T> set(address: Int, data: T): Unit = set(address.toLong(), data)

	operator fun get(moduleName: String): Module {
		if (modulesByName.contains(moduleName)) return modulesByName[moduleName]!!
		val module = Module(this, resolveModule(unsafe, moduleName))
		modulesByName.put(moduleName, module)
		return module
	}

}