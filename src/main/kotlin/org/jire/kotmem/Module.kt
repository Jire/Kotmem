package org.jire.kotmem

import org.jire.kotmem.unsafe.*

class Module(val process: Process, val unsafe: UnsafeModule) {

	val name by lazy { resolveModuleName(unsafe) }

	val address by lazy { resolveModuleAddress(unsafe) }

	val pointer = unsafe.module.pointer!!

	val size = unsafe.info.SizeOfImage!!

	operator inline fun <reified T : Any> get(offset: Long) = process.get<T>(address + offset)

	operator inline fun <reified T : Any> get(offset: Int): T = get(offset.toLong())

	operator inline fun <reified T : Any> set(offset: Long, data: T) = process.set(address + offset, data)

	operator inline fun <reified T : Any> set(offset: Int, data: T): Unit = set(offset.toLong(), data)

}