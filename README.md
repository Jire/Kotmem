##Kotmem
_Easy process, module and memory interfacing through Kotlin_

This library is licensed under LGPL 3.0 and was created for use by myself for creating game cheats. It is in use in 
my free CS:GO cheating platform at 
[https://abendigo.org](https://abendigo.org)  

---

###One Minute Intro

Let's open a process!

    val process = processByName("just_for_fun.exe")
    
Now let's use the process to read at some address. Note that the type can't be inferred by the compiler here, it must
 be explicit in the value declaration.

    val cafeBabe: Int = process.get(0xCAFEBABE)

We can also explicitly specify the type in the method call.

    val deadBabe = process.get<Int>(0xDEADBABE)
    
Here the compiler can infer that the type is `boolean`, thus we can omit.

    if (process.get(0xBADCAFE)) println("We're in a bad cafe!")

We're also able to write at some address. The data argument provides the type thus the type can always be inferred by
 the compiler.

    process.set(0xBADCAFE, false)

We can resolve a process' module as well. These are cached by name on first call.

    val awesomeDLL = process.resolveModule("awesome.dll")
    
With the module we are able to query its address `awesomeDLL.address` and name `awesomeDLL.name`. These are lazily 
initiated and are cached once accessed.

We can also use a module to read and write. Doing so will use the module's address as a base and an offset of such is
 supplied by the user.
 
    val faceFeed: Short = awesomeDLL.get(0xFACEFEED)
    awesomeDLL.set(0xFACEFEED, faceFeed + 1)
    
---

###Caution

This project is not yet ready for production. It currently depends on the Kotlin team to fix a few bugs so that.