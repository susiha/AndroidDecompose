
### 应用二进制插件
> 二进制插件就是实现了org.gradle.api.Plugin，接口的插件，我们自定义的插件就是这样的插件，它可以有plugin id
```
apply plugin:'java'
```
这样就把java插件应用到项目中来了,其中‘java’ 就是Java插件的plugin id,它是唯一的，对于Gradle自带的核心插件都有一个容易记的短名,那就是plugin id

这里 ‘java’ 对应的类型是 org.gradle.api.plugins.JavaPlugin,所以也可以直接通过类型应用这个插件
```
apply plugin:org.gradle.api.plugins.JavaPlugin
```
这里org.gradle.api.plugins是默认导入的，因此也可以直接写成
```
apply plugin:JavaPlugin
```
### 应用脚本插件
```
apply from :'version.gradle'
```
应用脚本插件使用的是from,后面跟的脚本可以是本地的脚本，也可以是网络的

### 应用第三方发布的插件
如Android Gradle 就是android 发布的第三方插件，这样必须在buildscript中配置classpath才能使用
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
buildscript{} 是在构建项目之前，为项目进行前期准备和初始化相关配置的地方，这里一般在AS中创建好了classpath

如果还需要应用别的第三方插件，也需要在这个地方配置

### 自定义插件
- 首先创建一个groovy 工程
```
apply plugin:‘groovy’

dependencies{
    compile gradleApi()
    compile localGroovy()
}
```
- 其次实现插件类

```
 class myPlugin implements Plugin<Project>{
     @override
     void apply(Project target){
       ....
     }
 }

```
- 最后定义plugin id
Gradle 是通过META-INF里的properties来发现对应插件实现类的,所以首先定义plugin id，假设 为了不重复 加上包名定义的pugin id 叫 ‘com.susiha.customPlugin’

那么就需要在src/main/resources/META-INF/gradle-plugins/目录下创建一个plugin id的properties的文件，即这个新建的文件路径是

src/main/resources/META-INF/gradle-plugins/com.susiha.customPlugin.properties

这个文件的内容是
```
  implemention-class = 包路径.myPlugin
```
其中 implemention-class 是不变的，后面的值就是自定义plugin的实现类

- 定义好的plugin 可以上传到本地 也可以打成jar 包，这样别人放在libs中，可以直接在bulidscript中配置完就可以使用了
```

  buildscript{
       dependencies{
           classpath files('libs/myPlugin.jar')
       }
   }
   
   apply plugin:'com.susiha.customPlugin'
   

```


