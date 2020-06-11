Android Gradle 的插件主要有三种
- App插件 对应的id:com.android.application
- library插件 对应的id:com.android.library
- test 插件 对应的id: com.android.test

### 应用Android Gradle插件
Android Gradle 是第三方插件，托管在jcenter()上所以，需要配置它的仓库和classpath依赖
```
buildscript{
   repositories{
      jcenter()
   }
   dependencies{
     classpath 'com.android.tools.build:gradle:3.2.0'
   }
}
```
好在AS在创建工程的时候已经帮忙创建好了，并放在根工程的build.gradle中，这样所有的子工程都不需要重复配置了

Android Gradle工程的配置都在android{}中，这是唯一的入口，通过它可以对Android Gradle工程进行自定义配置

android{}对应的实现是AppExtension,其继承自BaseExtension，所有的操作都可以在BaseExtension中找到
> android{}的创建时在AppPlugin中创建的,如果要查看AppPlugin，需要将com.android.tools.build:gradle:3.2.0在app/build.gradle中进行依赖，这样就可以查看appPlugin的源码了

以下是常规的android{}内的配置
#### compileSdkVersion
配置编译SDK的版本
这是一个方法，按照groovy的语法规则，参数可以直接去掉()

方法原型在BaseExtension中
```
 /** @see #getCompileSdkVersion() */
    public void compileSdkVersion(String version) {
        checkWritability();
        this.target = version;
    }

    /** @see #getCompileSdkVersion() */
    public void compileSdkVersion(int apiLevel) {
        compileSdkVersion("android-" + apiLevel);
    }

```
所以我们可以写成这样
```
compileSdkVersion 26
//或
compileSdkVersion “android-26"
```
#### buildToolsVersion
使用的 android 构建工具的版本
同样 它也有方法原型
```
    public void buildToolsVersion(String version) {
        checkWritability();
        //The underlying Revision class has the maven artifact semantic,
        // so 20 is not the same as 20.0. For the build tools revision this
        // is not the desired behavior, so normalize e.g. to 20.0.0.
        buildToolsRevision = Revision.parseRevision(version, Revision.Precision.MICRO);
    }
```

### defaultConfig
defaultConfig的实现是BaseFlavor,它实现了ProductFlavor,所以它也是一个ProductFlavor

它里面一些基本配置：
- applicationId 配置的包名
- minSdkVersion 最低支持的android 系统的版本
- targetSdkVersion 基于开发的android系统版本
- versionCode 内部版本号，一般用于内部版本控制
- versonName  版本名称  一般用于给用户看的 
- signingConfig 配置签名信息
这些在BaseFlavor或者其父类里面都有对应的方法

如果定义了productFlavor,但是没有给对应的flavor定义以上属性，都采用的是defaultConfig里面的配置
### 配置签名

Android Gradle 提供了signingConfigs{}用与生成配置信息
- storeFile 签名证书文件
- storePassword 签名证书的密码
- storeType 签名证书的类型
- keyAlias 签名证书中秘钥别名
- keyPassword 签名证书中该秘钥的密码

下面就是配置的一个签名信息a,在productFlavor或者BuildType里面直接使用 signingConfig signingConfigs.a就可以了
值得注意的是 这个定义一定要在使用之前，否者会出现找不到属性a的错误

如果需要在defaultConfig中配置签名信息，一定要让签名信息的配置出现在defaultConfig之前

```
  signingConfigs{
        a{
            storeFile file('astrore.jks')
            storePassword '123456'
            keyAlias 'keya'
            keyPassword '123456'
        }
    }
```
### 构建类型
buildTypes{} 跟signingConfigs一样 也是android的一个方法，它有几个属性
#### applicationIdSuffix
用于配置基于默认的applicationId的后缀，比如说在defaultConfig中配置的applicationId是 com.susiha.app,在debug的buildType指定
applicationIdSuffix为.debug,则构建的debug apk 的包名就是  com.susiha.app.debug
#### debuggable
是否生成一个可调式的apk 可选值 为true或false
#### jniDebuggable
是否生成可调试Jni代码的apk
#### minifyEnable
是否启用proguard混淆
#### multiDexEnabled
是否拆分多个Dex
#### proguardFile
配置混淆文件
#### proguardFiles
配置多个混淆文件
一般AS自动生成的是
```
proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
```
其中getDefaultProguardFile这个方法就是找到sdk目录下的tools/proguard目录下的文件
这个目录下有两个混淆文件
proguard-android-optimize.txt和proguard-android.txt,一个事优化过的，一个事没有优化的，这两个文件的作用就是当开启混淆时让android的一些诸如注解或者R文件避免被混淆

#### shrinkResources
配置是否自动清理未使用的资源 默认是false
#### 启用zipalign优化
zipalign是android 提供的一个整理优化apk文件的工具，它能提高系统和应用的运行效率，更快的读写apk中的资源，降低内存的使用
启用很简单，在buildType中 使zipalignEnable 为true 即可

### 自定义buildConfig
```
 buildConfigField 'String','flaver',"\"$name\""
```
就是定义字段的类型,名称 和值，可以放在ProductFlavor里面可以放在buildType里面

### 动态配置AndroidManifest.xml
对于AndroidManifest.xml中的需要根据渠道或者编译类型动态填的值 如Umeng
```
<meta-data android:name ="Umeng"，android:value ="xxxx">
```
针对不同渠道和构建类型 可以使用 manifestPlaceholders来实现
如
```
manifestPlaceholders.put("buildType",name)

```
则在AndroidManifest.xml中就相应的修改为
```
 <meta-data android:name="buildType" android:value="${buildType}"/>

```

manifestPlaceholders 可以放在productFlavor中也可以放在buildType中

### 自定义资源
与buildConfigField 一样也是接收三个参数 类型，名称和值 
```
 resValue 'string','name','xxxx'
```
同样也是可以放在productFlavor或者buildType中

### Java 编译选项

```
compileOptions{
        encoding = 'utf-8'
        sourceCompatibility = 1.7
        targetCompatibility = 1.7
    }
```

### adbOptions
```
 adbOptions{
        timeOutInMs = 5*1000
        installOptions '-r','-s'

    }
```
adbOptions 是androidGradle 对adb的控制配置 它的实现类是AdbOptions 它有两个属性
- timeOutInMs 操作adb 的超时时间
- installOptions 是个list 表示adb install的一些参数 

它有以下几个选线：
- -l:锁定该应用程序
- -r:替换已经存在的应用程序 属于强制安装
- -t:允许测试包
- -s:把应用程序安装到SD卡上
- -d:允许进行降级安装
- -g:为该应用授予所有运行时权限

### dexOptions
### 自动清理未使用的资源
一般情况minifyEnable和shrinkResources都开启的话会将无用的资源清理掉，但是在程序逻辑中可能会出现诸如反射这样的情景出现，有些资源实际上被使用了但是从检测结果上看 是未被使用的 为了防止这样的资源被清理掉需要一个创建一个keep文件
res/raw/keep.xml
```
<?xml version="1.0" encoding="utf-8"?>
<resources
    xmlns:tools="http://schemas.android.com/tools"
    tools:keep="@layout/unused*,@layout/unsed_*"
    tools:shrinkMode="safe"
    />
```
shrinkMode 有两个属性 safe(不清理)和strict(清理)

此外在productFlavor里面有一个属性 resConfigs 这个配置是让哪些资源打到Apk包内 比如说依赖的第三方有躲过语言，而我们只需要中文，以及我们只用hdpi的分辨率的图片 可以在defaultConfig里面或者具体的productFlavor里面配置
```
   resConfigs 'zh','hdpi'
```
























