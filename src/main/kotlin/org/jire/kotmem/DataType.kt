@file:JvmMultifileClass
@file:JvmName("Kotmem")

package org.jire.kotmem

import java.nio.ByteBuffer
import java.util.*

sealed class DataType<T : Any>(val bytes: Int, private val read: (ByteBuffer) -> T,
                               private val write: (ByteBuffer, T) -> Unit) {

	object ByteDataType : DataType<Byte>(1, { it.get() }, { buf, v -> buf.put(v) })

	object ShortDataType : DataType<Short>(2, { it.short }, { buf, v -> buf.putShort(v) })

	object IntDataType : DataType<Int>(4, { it.int }, { buf, v -> buf.putInt(v) })

	object LongDataType : DataType<Long>(8, { it.long }, { buf, v -> buf.putLong(v) })

	object FloatDataType : DataType<Float>(4, { it.float }, { buf, v -> buf.putFloat(v) })

	object DoubleDataType : DataType<Double>(8, { it.double }, { buf, v -> buf.putDouble(v) })

	object BooleanDataType : DataType<Boolean>(1, { it.get() > 0 }, { buf, v ->
		buf.put((if (v) 1 else 0).toByte())
	})

	fun read(buf: ByteBuffer) = read.invoke(buf)

	fun write(buf: ByteBuffer, value: T) = write.invoke(buf, value)

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

fun <T : Any> dataTypeOf(`class`: Class<T>) = classToType.getRaw(`class`) as DataType<T>