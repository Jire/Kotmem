package org.jire.kotmem.linux

import com.sun.jna.Pointer
import org.jire.kotmem.Module

class LinuxModule(process: LinuxProcess, pointer: Pointer, override val name: String,
                  override val size: Int) : Module(process, pointer)