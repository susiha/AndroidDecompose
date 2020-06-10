使用Java插件需要在项目的build.gradle中
```
apply plugin:'java'
```
### Java 插件的项目结构
```
 project
    |---build.gradle
    |---src
         |---main
         |     |---java
         |     |---resources
         |---test
              |---java
              |---resources

```
main和test是Java插件内置的两个源代码集合
如果想要添加就需要用到sourceSets
### sourceSets
Java插件在Project下提供了sourceSets属性和sourceSets{}来访问和配置资源

sourceSets{}闭包中配合的都是sourceSets对象，sourceSets中的常用属性

- name 源集的名称
- output.classesDir 编译后classes的文件目录 类型是File  在gradle 5.0之后 没有该属性了，可以使用output.classesDirs(类型是FileCollection)
- output.resourcesDir 编译后生成的资源文件目录 类型是File
- compileClassPath 编译该源集所需的classpath 类型是FileCollection
- java  java源文件 类型是 SourceDirectorySet
- java.srcDirs java源文件所在目录 类型是set
- resources 资源文件 类型是 SourceDirectorySet
- resources.srcDirs 资源文件所在目录 类型是set

在build.gradle中新创建一个task 然后打印以上信息
```
task showAllSourceSetTask{
    sourceSets.all{
        println "------------------"
        println "当前目录是: ${name}"
        println "output :${output}"
        output.classesDirs.each{
            println "output.classesDir :${it}"
        }
        println "output.resourcesDir:${output.resourcesDir}"
        println "compileClasspath:${compileClasspath}"
        println "java:${java}"
        println "java.srcDirs:${java.srcDirs}"
        println "resources:${resources}"
        println "resources.srcDirs:${resources.srcDirs}"
    }
}

```

打印的结果是：
```
------------------
当前目录是: main
output :main classes
output.classesDir :/Users/yrd/yrd_workspace/JavaPlugin/lib/build/classes/java/main
output.resourcesDir:/Users/yrd/yrd_workspace/JavaPlugin/lib/build/resources/main
compileClasspath:configuration ':lib:compileClasspath'
java:main Java source
java.srcDirs:[/Users/yrd/yrd_workspace/JavaPlugin/lib/src/main/java]
resources:main resources
resources.srcDirs:[/Users/yrd/yrd_workspace/JavaPlugin/lib/src/main/resources]
------------------
当前目录是: test
output :test classes
output.classesDir :/Users/yrd/yrd_workspace/JavaPlugin/lib/build/classes/java/test
output.resourcesDir:/Users/yrd/yrd_workspace/JavaPlugin/lib/build/resources/test
compileClasspath:file collection
java:test Java source
java.srcDirs:[/Users/yrd/yrd_workspace/JavaPlugin/lib/src/test/java]
resources:test resources
resources.srcDirs:[/Users/yrd/yrd_workspace/JavaPlugin/lib/src/test/resources]
```
可以看出内置的确实只有main 和set 两个源集


在源码的src 下创建一个custom/java/目录 并创建对应的代码 
在build.gradle中创建
```
sourceSets {
    custom{
        java{
            srcDir 'src/java'
        }
    }
}
```

可以看出再打印出来的信息多了
```
当前目录是: custom
output :custom classes
output.classesDir :/Users/yrd/yrd_workspace/JavaPlugin/lib/build/classes/java/custom
output.resourcesDir:/Users/yrd/yrd_workspace/JavaPlugin/lib/build/resources/custom
compileClasspath:configuration ':lib:customCompileClasspath'
java:custom Java source
java.srcDirs:[/Users/yrd/yrd_workspace/JavaPlugin/lib/src/custom/java, /Users/yrd/yrd_workspace/JavaPlugin/lib/src/java]
resources:custom resources
resources.srcDirs:[/Users/yrd/yrd_workspace/JavaPlugin/lib/src/custom/resources]
```
这里 java.srcDirs之所以会出现两个地址，这是因为，我们在sourceSets中定义的 srcDir 'src/java'，这样一个默认的，另外一个就是 src/java下面的源码也能编译了

以为 FileCollection 是只读属性不能更改，所以在5.0 之后 output.classesDirs 也没有办法修改

### Java插件配置第三方依赖
首先需要告诉Gradle 去哪搜寻需要依赖的jar
```
repositories{
   jcenter()
   mavenCentral()
   maven{
      url ‘XXXX’
   }
}
```
这个就是告诉Gradle 可以去maven中心库和jcenter,或者一些私服库去搜索依赖的库

第二步 告诉Gradle 依赖什么
```
  dependencies{
   
   implementation group:'xxxx',name:'xxxx',version:'xxxx'
   //或者直接省略掉 group name和version
   implementation 'xxxx:xxxx:xxxx'
  
  }
```
gradle 提供的依赖配置
- compile (已经被implementation替代) 编译时依赖
- runtime  运行时依赖
- testCompile 编译测试用例时依赖
- testRuntime 测试用例运行时依赖
- archives  发布构建时依赖
- default 默认依赖配置
 

### Java 插件添加的任务

- compileJava (类型:JavaCompile) 使用javac编译java源文件
- processResources (类型:Copy) 把资源文件copy到生产的资源文件目录里
- classes (类型: Task) 组装产生的类和资源文件目录
- compileTestJava (类型:JavaCompile) 使用javac编译测试Java源文件
- processTestResources (类型:Copy) 把测试资源文件赋值到生产的资源文件目录里
- testClasses (类型: Task) 组装产生测试的类和资源文件目录
- jar (类型:Jar) 组装Jar文件
- javadoc (类型:Javadoc) 使用javadoc生成javaApi文档
- test (类型:Test) 使用JUnit或TestNG运行单元测试
- uploadArchives (类型: Upload) 上传包含Jar的构建 用archives{}配置
- clean (类型: Delete) 清理构建生成的目录文件
- cleanTaskName (类型: Delete)  删除指定任务生成的文件，比如说cleanJar 删除Jar任务生成的文件



































