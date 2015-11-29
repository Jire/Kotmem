package kotmem

import com.sun.jna.*
import com.sun.jna.platform.win32.*
import kotmem.unsafe.*
import java.util.*
import kotlin.reflect.*

val TYPE_TO_BYTES = mapOf(Boolean::class.qualifiedName to 1, Byte::class.qualifiedName to 1,
		Short::class.qualifiedName to 2, Int::class.qualifiedName to 4, Long::class.qualifiedName to 8,
		Float::class.qualifiedName to 4, Double::class.qualifiedName to 8)

class Process(val unsafe: UnsafeProcess) {

	val modules by lazy { HashSet<Module>().addAll(resolveModules(unsafe) as Collection<Module>) }

	private val modulesByName = HashMap<String, Module>()

	fun resolveModule(moduleName: String): Module {
		if (modulesByName.contains(moduleName)) return modulesByName[moduleName]!!
		val module = Module(this, resolveModule(unsafe, moduleName))
		modulesByName.put(moduleName, module)
		return module
	}

	val readMemory = HashMap<KClass<*>, Memory>()

	inline fun <reified T> read(address: Long): T {
		val type = T::class
		val bytes = TYPE_TO_BYTES.getRaw(type.qualifiedName) ?: throw IllegalArgumentException("Unsupported type")
		var memory = readMemory[type]
		if (memory == null) {
			memory = Memory(bytes.toLong())
			readMemory.put(type, memory)
		}
		if (!readProcessMemory(unsafe, address, memory, bytes)) throw Win32Exception(Native.getLastError())
		return when (type.qualifiedName) {
			Boolean::class.qualifiedName -> memory.getByte(0) > 0
			Byte::class.qualifiedName -> memory.getByte(0)
			Short::class.qualifiedName -> memory.getShort(0)
			Int::class.qualifiedName -> memory.getInt(0)
			Long::class.qualifiedName -> memory.getLong(0)
			Float::class.qualifiedName -> memory.getFloat(0)
			Double::class.qualifiedName -> memory.getDouble(0)
			else -> throw AssertionError("Impossible case of invalid type \"${type.qualifiedName}\"")
		} as T
	}

	inline fun <reified T> read(address: Int): T = read(address.toLong())

	inline fun <reified T> write(address: Long, data: T) {
		val type = T::class
		val bytes = TYPE_TO_BYTES.getRaw(type.qualifiedName) ?: throw IllegalArgumentException("Unsupported type")
		var memory = readMemory[type]
		if (memory == null) {
			memory = Memory(bytes.toLong())
			readMemory.put(type, memory)
		}
		memory.clear()
		when (type.qualifiedName) {
			Boolean::class.qualifiedName -> memory.setByte(0, if (data as Boolean) 1 else 0)
			Byte::class.qualifiedName -> memory.setByte(0, data as Byte)
			Short::class.qualifiedName -> memory.setShort(0, data as Short)
			Int::class.qualifiedName -> memory.setInt(0, data as Int)
			Long::class.qualifiedName -> memory.setLong(0, data as Long)
			Float::class.qualifiedName -> memory.setFloat(0, data as Float)
			Double::class.qualifiedName -> memory.setDouble(0, data as Double)
			else -> throw AssertionError("Impossible case of invalid type \"${type.qualifiedName}\"")
		}
		if (!writeProcessMemory(unsafe, address, memory, bytes)) throw Win32Exception(Native.getLastError())
	}

	inline fun <reified T> write(address: Int, data: T): Unit = write(address.toLong(), data)

}