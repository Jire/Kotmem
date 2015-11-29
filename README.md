**Kotmem**  
_Easy process, module and memory interfacing through Kotlin_

This library is licensed under LGPL 3.0 and was created for use by myself for creating game cheats. It is in use at 
[https://abendigo.org](https://abendigo.org)

Let's open a process!

    val process = processByName("just_for_fun.exe")
    
Now let's use the process to read at some address. Note that the type can't be inferred by the compiler here, it must
 be explicit in the value declaration.

    val cafeBabe: Int = pc.read(0xCAFEBABE)

We can also explicitly specify the type in the method call.

    val deadBabe = pc.read<Int>(0xDEADBABE)
    
Here the compiler can infer that the type is `boolean`, thus we can omit.

    if (pc.read(0xBADCAFE)) println("We're in a bad cafe!")

We're also able to write at some address. The data argument provides the type thus the type can always be inferred by
 the compiler.

    pc.write(0xBADCAFE, false)

We can resolve a process' module as well. These are cached by name on first call.

    val awesomeDLL = pc.resolveModule("awesome.dll")
    
With the module we are able to query its address `awesomeDLL.address` and name `awesomeDLL.name`. These are lazily 
initiated and are cached once accessed.

We can also use a module to read and write. Doing so will use the module's address as a base and an offset of such is
 supplied by the user.
 
    val faceFeed: Short = awesomeDLL.read(0xFACEFEED)
    awesomeDLL.write(0xFACEFEED, faceFeed + 1)