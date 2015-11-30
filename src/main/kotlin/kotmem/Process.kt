package kotmem

import com.sun.jna.*
import com.sun.jna.platform.win32.*
import kotmem.unsafe.*
import java.nio.*
import java.util.*
import kotlin.reflect.*

val TYPE_TO_BYTES = mapOf(Boolean::class.qualifiedName to 1, Byte::class.qualifiedName to 1,
		Short::class.qualifiedName to 2, Int::class.qualifiedName to 4, Long::class.qualifiedName to 8,
		Float::class.qualifiedName to 4, Double::class.qualifiedName to 8)

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

	inline fun <reified T> read(address: Long): T {
		val type = T::class
		val bytes = TYPE_TO_BYTES.getRaw(type.qualifiedName)!!
		val memory = memoryOf(type, bytes)
		if (!readProcessMemory(unsafe, address, memory, bytes))
			throw Win32Exception(Native.getLastError())
		memory.rewind()
		return when (type.qualifiedName) {
			Boolean::class.qualifiedName -> memory.get() > 0
			Byte::class.qualifiedName -> memory.get()
			Short::class.qualifiedName -> memory.short
			Int::class.qualifiedName -> memory.int
			Long::class.qualifiedName -> memory.long
			Float::class.qualifiedName -> memory.float
			Double::class.qualifiedName -> memory.double
			else -> throw AssertionError("Impossible case of invalid type \"${type.qualifiedName}\"")
		} as T
	}

	inline fun <reified T> read(address: Int): T = read(address.toLong())

	inline fun <reified T> write(address: Long, data: T) = lock {
		val type = T::class
		val bytes = TYPE_TO_BYTES.getRaw(type.qualifiedName)!!
		val memory = memoryOf(type, bytes)
		when (type.qualifiedName) {
			Boolean::class.qualifiedName -> memory.put((if (data as Boolean) 1 else 0).toByte())
			Byte::class.qualifiedName -> memory.put(data as Byte)
			Short::class.qualifiedName -> memory.putShort(data as Short)
			Int::class.qualifiedName -> memory.putInt(data as Int)
			Long::class.qualifiedName -> memory.putLong(data as Long)
			Float::class.qualifiedName -> memory.putFloat(data as Float)
			Double::class.qualifiedName -> memory.putDouble(data as Double)
			else -> throw AssertionError("Impossible case of invalid type \"${type.qualifiedName}\"")
		}
		memory.flip()
		if (!writeProcessMemory(unsafe, address, memory, bytes))
			throw Win32Exception(Native.getLastError())
	}

	inline fun <reified T> write(address: Int, data: T): Unit = write(address.toLong(), data)

	fun resolveModule(moduleName: String): Module {
		if (modulesByName.contains(moduleName)) return modulesByName[moduleName]!!
		val module = Module(this, resolveModule(unsafe, moduleName))
		modulesByName.put(moduleName, module)
		return module
	}

}