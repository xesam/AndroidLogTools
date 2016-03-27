# Android Log

现在包含三个类：

1. 打印日志
2. 崩溃记录
3. 文件日志

# 使用方式

    compile 'dev.xesam.android:log-tools:0.1.7'

# 打印日志 L
对 android.util.Log 的简单封装，支持 d(Object... content) 的调用形式，避免对 String 的硬性要求，使用示例：

开启日志（默认不打印）：

```java
    L.enable(true);
```

打印日志：

```java
    L.d();
    L.d(null);
    L.d(null, null);
    L.d(this);
    L.d(this, this);
    L.d(1);
    L.d(1, 2);
    L.d("a");
    L.d("a", "b");
```
    
结果如下：

    D/L[empty_tag]: L[empty_content]
    D/L[null]: L[null]
    D/L[null]: L[null]
    D/MainActivity: L[empty_content]
    D/MainActivity: dev.xesam.android.logtools.demo.MainActivity@32dd3da6
    D/1: L[empty_content]
    D/1: 2
    D/a: L[empty_content]
    D/a: b
    
# 崩溃记录 CrashLog

将崩溃记录写入外部文件中，便于检查。（注意，请只在测试的时候才使用），使用示例:

在 Application 中注册:

```java
    CrashLog.init(this);
```

即可。

记录日志保存在 XXX/sdcard/Android/data/#{package_name}/files/目录之下，比如：

    crash.2015-10-06T12:03:24.txt

# FileLogger

将日志写入文件。（注意，请只在测试的时候才使用）

```java
    FileLogger.init(Context);//初始化一个文件
    
    FileLogger.log(String);//写日志
    
    FileLogger.newRound(String);//开始新一轮日志记录
    
    FileLogger.recycle(String);//回收资源
```

默认行为，每次调用 FileLogger.newRound 之后，都会根据相应的参数创建文件日志，如果想把数次启动/关闭的所有日志都打印到同一个日志文件中，可以将 append 参数置为 true。

比如：

```java
FileLogger.newRound("path", true);//开始新一轮日志记录
```

或者指定 Writer：

```java
FileLogger.newRound(new FileWriter(file, true));
```

# 更新日志

### 20160327

1. 指定文件位置
2. 增加 append 模式

### 20160310

1. 增加文件日志功能

### 20151204

1. 修正 L.w 的参数问题

### 20151110

1. 修正匿名类的 tag 为空的问题

    