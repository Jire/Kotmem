@file:JvmMultifileClass
@file:JvmName("Kotmem")

package org.jire.kotmem

import java.util.*

sealed class DataType<T : Any>(val bytes: Int, private val read: MemoryBuffer.() -> T,
                               private val write: MemoryBuffer.(T) -> Any) {

	object ByteDataType : DataType<Byte>(1, { byte() }, { byte(it) })

	object ShortDataType : DataType<Short>(2, { short() }, { short(it) })

	object IntDataType : DataType<Int>(4, { int() }, { int(it) })

	object LongDataType : DataType<Long>(8, { long() }, { long(it) })

	object FloatDataType : DataType<Float>(4, { float() }, { float(it) })

	object DoubleDataType : DataType<Double>(8, { double() }, { double(it) })

	object BooleanDataType : DataType<Boolean>(1, { byte() > 0 }, { byte((if (it) 1 else 0).toByte()) })

	fun read(buf: MemoryBuffer) = buf.read()

	fun write(buf: MemoryBuffer, value: T) = buf.write(value)

}

private val classToType = mapDataTypes()

private fun mapDataTypes(): HashMap<Class<*>, DataType<*>> {
	val map = HashMap<Class<*>, DataType<*>>()
	map.put(java.lang.Byte::class.java, DataType.ByteDataType)
	map.put(java.lang.Short::class.java, DataType.ShortDataType)
	map.put(java.lang.Integer::class.java, DataType.IntDataType)
	map.put(java.lang.Long::class.java, DataType.LongDataType)
	map.put(java.lang.Float::class.java, DataType.FloatDataType)
	map.put(java.lang.Double::class.java, DataType.DoubleDataType)
	map.put(java.lang.Boolean::class.java, DataType.BooleanDataType)
	return map
}

fun <T : Any> dataTypeOf(`class`: Class<T>) = classToType[`class`] as DataType<T>