##Kotmem
_Easy process, module and memory interfacing through Kotlin on the JVM_

[![Build Status](https://travis-ci.org/Jire/Kotmem.svg?branch=master)](https://travis-ci.org/Jire/Kotmem)
[![Dependency Status](https://www.versioneye.com/user/projects/578838bcc3d40f003caa2efa/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/578838bcc3d40f003caa2efa)
[![license](https://img.shields.io/github/license/Jire/Kotmem.svg)](https://github.com/Jire/Kotmem/blob/master/LICENSE)

### Gradle
```groovy
compile 'org.jire.kotmem:Kotmem:0.86'
```

### Maven
```xml
<dependency>
  <groupId>org.jire.kotmem</groupId>
  <artifactId>Kotmem</artifactId>
  <version>0.86</version>
</dependency>
```

---

###One Minute Intro

You can open a process by name:

```kotlin
val process = Processes["just_for_fun.exe"]
```

Or by ID:

```kotlin
val process = Processes[1337]
```

Now let's use the process to read at some address. Note that the type can't be inferred by the compiler here, it must
 be explicit in the value declaration.

```kotlin
val cafeBabe: Int = process[0xCAFEBABE]
```

Here the compiler can infer that the type is `Boolean`, thus we can omit.

```kotlin
if (process[0xBADCAFE]) println("We're in a bad cafe!")
```

We're also able to write at some address. The data argument provides the type thus the type can always be inferred by
 the compiler.

```kotlin
process[0xBADCAFE] = false
```

We can resolve a process' module as well. These are cached by name on first call.

```kotlin
val awesomeDLL = process["awesome.dll"]
```

With the module we are able to query its address `awesomeDLL.address` and name `awesomeDLL.name`. These are lazily 
initiated and are cached once accessed.

We can also use a module to read and write. Doing so will use the module's address as a base and an offset of such is
 supplied by the user.

```kotlin
val faceFeed: Short = awesomeDLL[0xFACEFEED]
awesomeDLL[0xFACEFEED] = faceFeed + 1
```