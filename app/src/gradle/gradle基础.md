### settings.gradle
> settings.gradle用于配置子工程，一个子工程只有在settings.gradle中配置了，才能在构建的时候被包含起来

通常我们的settings.gradle是被android studio 默认创建好的 
```
include ':app'
rootProject.name='GradleDemo'
```

当多项目的时候 也是在includ后面追加
```
include ':app', ':lib'
rootProject.name='GradleDemo'
```
这其中所有的项目都是同级目录，如果不是同级目录，需要指定对应的目录路径
```
include ':app'
project(':app').projectDir = new File(rootDir,"app")
include ':lib'
project(':lib').projectDir = new File(rootDir,"lib")
rootProject.name='GradleDemo'
```
上面这段代码，作用前面的一致，是用另一种方式(指定目录的方式)的展示
### Task
#### 创建Task 
- 可以直接 task taskName{} 来创建Task
```
task  customTask1{
    doFirst{
      println "customTask1: doFirst！"
    }
     println "customTask1: now！"
     doLast{
      println "customTask1: doLast！"
     }
}
```
- 也可以使用TaskContainer来创建Task,在Gradle里面，project已经定义好了TaskContainer，也就是tasks
它的方法原型是：Task create(String name,Clourse clourse)

```
tasks.create("customTask2"){
   doFirst{
      println "customTask2: doFirst！"
    }
     println "customTask2: now！"
     doLast{
      println "customTask2: doLast！"
     }
}
```
#### 任务依赖
- 任务的依赖通过dependsOn指定其依赖的任务
```
task preDependsTask{
     doLast{
      println  "preDependsTask"
     }
}

task preDependsTask1{
     doLast{
      println  "preDependsTask1"      
     }
}
task dependsTask(dependsOn:preDependsTask){
     doLast{
      println  "dependsTask"
     }
 
}
task dependsAllTask{
   dependsOn dependsTask,preDependsTask1
    doLast{
     println  "dependsAllTask"
    }
}

```
- 依赖一个Task 的时候可以直接在参数中进行
- 依赖多个Task 可以在Task的闭包中使用dependsOn
- 当依赖Task的时候 必须在被依赖的Task 执行完才执行当前Task

#### 自定义属性
- 定义的Task 首先就是project的一个属性
```
task customPro{
    doLast{
      println "project 是否有属性 customPro: ${project.hasProperty('customPro')}"
    }
}
```
打印结果：
```
project 是否有属性 customPro: true
```
- 可以使用ext 添加额外的属性
```
ext.name = "susiha"
ext{ 
   age = 32
   addres = "beijing"
}
task customExtPro{
   doLast{
     println "name = ${name}"
     println "age = ${age}"
     println "addres = ${addres}"
   }
}
```

[示例代码](https://github.com/susiha/AndroidDecompose/blob/master/app/src/gradle/groovy/gradleBasics.groovy)

