##Kotmem
_Easy process, module and memory interfacing through Kotlin_

This library is licensed under LGPL 3.0 and was created for use in my game modding platform called Abendigo which you
 can see here: [https://github.com/Jire/Abendigo](https://github.com/Jire/Abendigo)  

---

###One Minute Intro

Let's open a process!

    val process = processes["just_for_fun.exe"]
    
Now let's use the process to read at some address. Note that the type can't be inferred by the compiler here, it must
 be explicit in the value declaration.

    val cafeBabe: Int = process[0xCAFEBABE]

Here the compiler can infer that the type is `boolean`, thus we can omit.

    if (process[0xBADCAFE]) println("We're in a bad cafe!")

We're also able to write at some address. The data argument provides the type thus the type can always be inferred by
 the compiler.

    process[0xBADCAFE] = false

We can resolve a process' module as well. These are cached by name on first call.

    val awesomeDLL = process["awesome.dll"]
    
With the module we are able to query its address `awesomeDLL.address` and name `awesomeDLL.name`. These are lazily 
initiated and are cached once accessed.

We can also use a module to read and write. Doing so will use the module's address as a base and an offset of such is
 supplied by the user.
 
    val faceFeed: Short = awesomeDLL[0xFACEFEED]
    awesomeDLL[0xFACEFEED] = faceFeed + 1
    
---

###Caution!

This project is not yet ready for production. Expect lots of breaking changes.

Also currently there is a bug in Kotlin which prevents using *get* and *set* with operators. The workaround is that 
you must use explicit function calls to *get* and *set* (e.g. `val cafeBabe: Int = process.get(0xCAFEBABE)`) until the 
issue
 is fixed.