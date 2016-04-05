@file:JvmMultifileClass
@file:JvmName("Kotmem")

package org.jire.kotmem

import com.sun.jna.Platform
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import org.jire.kotmem.linux.LinuxProcess
import org.jire.kotmem.mac.MacProcess
import org.jire.kotmem.mac.mac
import org.jire.kotmem.win32.User32
import org.jire.kotmem.win32.openProcess
import org.jire.kotmem.win32.processIDByName
import java.lang.Runtime.getRuntime
import java.util.*

object Processes {

	@JvmStatic
	operator fun get(processID: Int): Process = when {
		Platform.isWindows() -> openProcess(processID)
		Platform.isLinux() /*&& sudo*/ -> LinuxProcess(processID)
		Platform.isMac() /*&& sudo*/ -> {
			val out = IntByReference()
			if (mac.task_for_pid(mac.mach_task_self(), processID, out) != 0)
				throw IllegalStateException("Failed to find mach task port for process")
			MacProcess(processID, out.value)
		}
		else -> throw UnsupportedOperationException("Unsupported platform or not enough privilege")
	}

	@JvmStatic
	operator inline fun get(processID: Int, action: (Process) -> Unit): Process {
		val process = get(processID)
		action(process)
		return process
	}

	@JvmStatic
	operator fun get(processName: String) = when {
		Platform.isWindows() -> Processes[processIDByName(processName)]
		Platform.isLinux() || Platform.isMac() -> {
			val search = getRuntime().exec(arrayOf("bash", "-c",
					"ps -A | grep -m1 \"$processName\" | awk '{print $1}'"))
			Processes[Scanner(search.inputStream).nextInt()]
		}
		else -> throw UnsupportedOperationException("Unsupported platform")
	}

	@JvmStatic
	operator inline fun get(processName: String, action: (Process) -> Unit): Process {
		val process = Processes[processName]
		action(process)
		return process
	}

}

object Keys {

	@JvmStatic @JvmName("state")
	operator fun invoke(keyCode: Int) = when {
		Platform.isWindows() -> User32.GetKeyState(keyCode).toInt()
		else -> throw UnsupportedOperationException("Unsupported platform")
	}

	@JvmStatic @JvmName("isPressed")
	operator fun get(vKey: Int) = Keys(vKey) < 0

}

private val pointer = ThreadLocal.withInitial { Pointer(0) }

fun cachedPointer(address: Long): Pointer {
	val pointer = pointer.get()
	Pointer.nativeValue(pointer, address)
	return pointer
}

private val bufferByClass = ThreadLocal.withInitial { HashMap<Class<*>, NativeBuffer>() }

fun cachedBuffer(type: Class<*>, bytes: Int): NativeBuffer {
	var buf = bufferByClass.get()[type]
	if (buf == null) {
		buf = NativeBuffer(bytes.toLong())
		bufferByClass.get().put(type, buf)
	}
	return buf
}