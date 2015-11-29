package kotmem

import kotmem.unsafe.*

class Module(val process: Process, val unsafe: UnsafeModule) {

	val name by lazy { resolveModuleName(unsafe) }

	val address by lazy { resolveModuleAddress(unsafe) }

	inline fun <reified T> read(offset: Long) = process.read<T>(address + offset)

	inline fun <reified T> write(offset: Long, data: T) = process.write(address + offset, data)

}