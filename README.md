# Knight

Knight 是一个模块内、模块间的数据通信框架，适合于面向接口编程的功能模块，对业务代码侵入基本为零，很好的解决了耦合和模块间通信问题。

### 用法

首先要在 gradle 中引入 Knight 插件：
```gradle
dependencies {
    classpath "com.android.tools.build:gradle:4.1.1"
    classpath 'com.lzx.knight.plugin:knight:1.0.0'
}
```

然后在 app 的 gradle 中使用插件：
```gradle
plugins {
    //...
    id 'knight-android'
}
```

### 1. 如果你的工程没分成多个模块，只是简单常规的工程结构

在你的 app gradle 里面引入 knight api 库：

```gradle
implementation project(':knight')
```

Knight 库非常简单，一共有 3 个类提供对外使用：

```kotlin
@KnightService
@KnightImpl
Knight
```

1. @KnightService 注解，用于标记功能接口
2. @KnightImpl 注解，用于标记功能接口的实现类
3. Knight Api 类，用于提供 api 方法

例子：比如有一个 IM 功能，需要对外提供发消息功能

第一步，先定义好接口，并且使用 @KnightService 标记接口。
```kotlin
@KnightService
interface ISendMessage {
    fun sendMessage(context: Context, msg: String)
}
```

第二步，实现接口功能，并且使用 @KnightImpl 标记
```kotlin
@KnightImpl
class SendMessageImpl : ISendMessage {
    override fun sendMessage(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
```

第三步，在你想要的地方调用发消息功能
```kotlin
Knight.of(ISendMessage::class.java)?.sendMessage(this, "大家好")
```

总体来说分为以上三步走，通过 Knight#of 方法，即可调用定义好的接口功能。可以看到整个过程没有耦合，调用时只需要调用接口方法接口即可，
不需要关心接口的实现，达到代码隔离效果。


**问题：如果一个接口有多个实现，调用的时候怎么知道调的是哪个接口的实现方法？**

Knight 支持这种情况，只需要稍微再写多一点代码即可，比如上面例子，ISendMessage 的实现除了 SendMessageImpl 外，还有 SendMessage2Impl。
那么在使用 @KnightImpl 标记的时候需要添加多一个 key 去区分它们：
```kotlin
@KnightImpl(register = "SendMessageOne")
class SendMessageImpl : ISendMessage {
    override fun sendMessage(context: Context, msg: String) {
        Toast.makeText(context, "我是实现一", Toast.LENGTH_SHORT).show()
    }
}

@KnightImpl(register = "SendMessageTwo")
class SendMessage2Impl : ISendMessage {
    override fun sendMessage(context: Context, msg: String) {
        Toast.makeText(context, "我是实现二", Toast.LENGTH_SHORT).show()
    }
}
```

在调用的时候，通过 Knight#of 方法的第二个参数传入 key，去取不同的实现：
```kotlin
Knight.of(ISendMessage::class.java, "SendMessageOne")?.sendMessage(this, "我调用的是实现一")

Knight.of(ISendMessage::class.java, "SendMessageTwo")?.sendMessage(this, "我调用的是实现二")
```

** 问题：标记的功能接口能继承其他接口吗？ **

可以。只要正确标记好 @KnightService 即可，如下：
```kotlin
interface BaseUserInterface {
    fun getUserInfo(): UserInfo?
}

@KnightService
interface IUserManager : BaseUserInterface{
    fun getUserName(): String?
}
```
如果我们要调用的功能接口是 IUserManager，那么注解就应该标记在它身上，BaseUserInterface 则不需要标记。


** 问题：接上面问题，如果基础接口方法太多，我不需要所有方法都要实现，怎么做？ **

```kotlin
interface BaseUserInterface {
    fun getUserInfo(): UserInfo?
    fun saveUserInfo(info: UserInfo)
    //...
}
```
如果基础接口有 N 个方法，我不需要都实现，怎么办？其实 @KnightService 是支持抽象类的。

```kotlin
@KnightService
abstract class IUserManager : BaseUserInterface {

    override fun saveUserInfo(context: Context) {
    }

    override fun getUserInfo(): UserInfo?  = null
}
```

这时候，我们可以创建一个抽象类，继承 BaseUserInterface，然后将所有方法都空实现，留给具体的实现类去选择实现相应的方法即可。
@KnightService 标记在这个抽象类上。

然后具体的实现：

```kotlin
@KnightImpl(register = "GetUserImpl")
class GetUserImpl : IUserManager() {
    override fun getUserInfo(): UserInfo = UserInfo().apply { username = "小明" }
}

//...

@KnightImpl(register = "SaveUserImpl")
class SaveUserImpl : IUserManager() {
    override fun saveUserInfo(context: Context) {
        Toast.makeText(context, "保存用户信息成功", Toast.LENGTH_SHORT).show()
    }
}
```

### 2. 如果你的工程分成多个模块，需要模块间通信。

基本的操作跟上面一样，没区别，这里给出一个模块划分的方案，会更适合 Knight 框架的使用。

既然是面向接口编程，那么每个模块中，需要对外提供的功能都应该抽象出一个接口来，然后单独放在一个 module 里面，
具体的功能实现在各自的模块中，任何模块如果需要使用模块的功能，只需要依赖接口模块即可。

具体的思想可以看以下文章：

[Android-模块化-面向接口编程](https://tech.youzan.com/android-mo-kuai-hua-mian-xiang-jie-kou-bian-cheng/)

[利用ASM实现的轻量级跨Module依赖注入框架](https://juejin.cn/post/6844904074094051342)
