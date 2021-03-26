# Knight

Knight 是一个模块内、模块间的数据通信框架，适合于面向接口编程，组件化的功能模块，对业务代码侵入基本为零，很好的解决了耦合和模块间通信问题。

## 用法介绍

Knight 很简单，你需要用到的类只有 3 个，分别是 Knight，KnightService 和 KnightImpl。其中 KnightService 和 KnightImpl 是注解。

1. @KnightService 注解，用于标记功能接口
2. @KnightImpl 注解，用于标记功能接口的实现类
3. Knight Api 类，用于提供 api 方法

#### 如何引入

在你的 app build.gradle 中引入插件 knight-android

```kotlin
plugins {
    id 'knight-android'
}

或者

apply plugin: "knight-android"
```

然后 dependencies 中引入 knight

```kotlin
dependencies {
    implementation project(':knight')
}
```

如果你的工程是多 module，比如 demo 中的结构，你需要在每个需要使用 Knight 的模块中都分别引用 implementation project(':knight') 即可。
具体可以查看 demo 代码。

#### 如何使用

例子：比如有一个用户模块，需要对外提供修改用户信息，读取用户信息，保存用户信息等功能

第一步，先定义好接口，并且使用 @KnightService 标记接口。

```kotlin
@KnightService
interface IUser {
    fun modifyUser(context: Context, nickname: String)
    fun readUser() : User
    fun saveUser(user : User)
}
```

第二步，实现接口功能，并且使用 @KnightImpl 标记

```kotlin
@KnightImpl
class UserManager : IUser {
    override fun modifyUser(context: Context, nickname: String) {
        //。。。具体实现...
    }

    override fun readUser() : User {
        //。。。具体实现...
    }

    override fun saveUser(user : User) {
        //。。。具体实现...
    }
}
```

第三步，在你想要的地方调用发消息功能

```kotlin
Knight.of(IUser::class.java)?.modifyUser(this, "我叫小明")
```

总体来说分为以上三步走，通过 Knight#of 方法，即可调用定义好的接口功能。可以看到整个过程没有耦合，调用时只需要调用接口方法接口即可，
不需要关心接口的实现，达到代码隔离效果。


#### 更复杂的情况

以上功能是基于接口实现的，那么更复杂的情况就是一个接口多个实现，抽象类实现了接口，具体方法实现抽象类，接口继承接口，抽象类继承抽象类等情况。

举例如下：
```kotlin

interface BaseUserInterface { ... }

interface UserInterface : BaseUserInterface { ... }

abstract BaseAbsUserInterface : UserInterface { ... }

abstract AbsUserInterface : BaseAbsUserInterface { ... }

class UserImplA : BaseUserInterface{ }

class UserImplB : BaseUserInterface{ }

class UserImplC : UserInterface{ }

class UserImplC : AbsUserInterface{ }
...
```

以上情况是我们在项目中经常可以遇见的，针对上面的情况，应该如何加注解？答案是都可以加：
```kotlin
@KnightService
interface BaseUserInterface { ... }

@KnightService
interface UserInterface : BaseUserInterface { ... }

@KnightService
abstract BaseAbsUserInterface : UserInterface { ... }

@KnightService
abstract AbsUserInterface : BaseAbsUserInterface { ... }

@KnightImpl(key = "UserImplA")
class UserImplA : BaseUserInterface{ }

@KnightImpl(key = "UserImplB")
class UserImplB : BaseUserInterface{ }

@KnightImpl
class UserImplC : UserInterface{ }

@KnightImpl
class UserImplD : AbsUserInterface{ }
...
```

首先是一个接口多个实现的问题，因为每个实现对应的接口都一样，所以需要在 KnightImpl 注解中添加一个 key 参数用作区分，然后在调用 API 的时候
通过第二个参数获取不同的实现：
```kotlin
Knight.of(BaseUserInterface::class.java , "UserImplA")?.modifyUser(this, "我调用的是 UserImplA 的方法")

Knight.of(BaseUserInterface::class.java , "UserImplB")?.modifyUser(this, "我调用的是 UserImplB 的方法")
```

对于其他情况，参数如常传入即可：
```kotlin
Knight.of(UserInterface::class.java)?.modifyUser(this, "我调用的是 UserImplC 的方法")

Knight.of(AbsUserInterface::class.java)?.modifyUser(this, "我调用的是 UserImplD 的方法")
...
```


### 原理简介

Knight 的原理很简单，利用 AMS 扫描被注解标记的类，根据注解的情况，生成对应的 key-value 表。
其中 key 是接口全类名，如果有 key 值，key 是接口全类名+key后缀。
其中 value 是接口对应的实现类全类名。

然后通过 Knight.of 方法传人的参数获取 key 对应的 value 值，然后通过反射实例化实现类，达到调用效果。

具体的思想可以看以下文章：

[Android-模块化-面向接口编程](https://tech.youzan.com/android-mo-kuai-hua-mian-xiang-jie-kou-bian-cheng/)

具体例子可下载代码查看。


