package org.jire.kotmem

abstract class Module(val process: Process<*>) {

	abstract val name: String

	abstract val address: Long

	operator inline fun <reified T : Any> get(offset: Long): T = process.get(address + offset)

	operator inline fun <reified T : Any> get(offset: Int): T = get(offset.toLong())

	operator inline fun <reified T : Any> set(offset: Long, data: T) = process.set(address + offset, data)

	operator inline fun <reified T : Any> set(offset: Int, data: T): Unit = set(offset.toLong(), data)

}