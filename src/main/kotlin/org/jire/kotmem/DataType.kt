package org.jire.kotmem

import java.nio.ByteBuffer
import kotlin.reflect.KClass

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

private val kClassToType = mapOf(java.lang.Byte::class to DataType.ByteDataType,
		java.lang.Short::class to DataType.ShortDataType, java.lang.Integer::class to DataType.IntDataType,
		java.lang.Long::class to DataType.LongDataType, java.lang.Float::class to DataType.FloatDataType,
		java.lang.Double::class to DataType.DoubleDataType, java.lang.Boolean::class to DataType.BooleanDataType)

fun <T : Any> dataTypeOf(kClass: KClass<T>) = kClassToType.getRaw(kClass) as DataType<T>