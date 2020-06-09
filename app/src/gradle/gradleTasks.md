
### 创建Task方式
#### 任务名字创建
```
def Task taskNameType = task(taskNameType)
taskNameType.doLast{
   println "taskNameType 是 ${taskNameType}"
   println "taskNameType 的 class  = ${taskNameType.getClass()}"
   println "创建方法原型为：Task task(${taskNameType.name})"
}
```
打印结果：
```
taskNameType 是 task ':taskNameType'
taskNameType 的 class  = class org.gradle.api.DefaultTask_Decorated
创建方法原型为：Task task(taskNameType)
```
- 从结果打印上来看 taskNameType本身是task':taskNameType'属于java.lang.Object
- taskNameType的class来看 是class org.gradle.api.DefaultTask_Decorated类型的
#### 任务名字+Map参数创建
> Map 的可选项为:

- type ：基于一个存在的Task来创建 和类继承差不多 默认值 DefaultTask
- overwrite:  是否替换存在的Task 和Type配合使用 默认值false
- dependsOn： 用于配置任务的依赖 默认值[]
- action： 添加任务中一个Action或者一个闭包 默认值 null
- description: 用于配置任务的描述   默认值 null
- group:  用于配置任务的分组  默认值 null

##### type类型示例
```
public class TypeParent extends DefaultTask{
    @TaskAction
    protected void parentAction() {
     println "this is a action from Parent"   
    }
}
 def Task typeTask = task("typeTask",type:TypeParent)
 typeTask.doLast{
    println "this is a doLast from typeTask"
 }
```
执行typeTask 打印结果：
```
this is a action from Parent
this is a action from typeTask
```
可以看到执行typeTask，也会执行到TypeParent的一个Action,这就像是继承一样
##### overwrite类型示例
当tasks 中已经存在一个同名的task的时候，如果再创建一个相同名字的task会报错，这时候如果，不想使旧的task可以使用overwrite

```
 task typeTask1{
     doLast{
       println "this is a old overwriteTask"
     }
 }
  task typeTask1(overwrite:true){
     doLast{
       println "this is a new  overwriteTask"
     }
 }
```
这样执行 typeTask1打印的结果就是  this is a new  overwriteTask

##### dependsOn 类型示例
```
 task typeTask2(dependsOn:[typeTask1,typeTask]){
     doLast{
         println "this is a dependsOnTask" 
     }
 }
```
打印结果：
```
> Task :typeTask
this is a action from Parent
this is a doLast from typeTask

> Task :typeTask1
this is a new  overwriteTask

> Task :typeTask2
this is a dependsOnTask
```
##### action 类型示例

```
 def myAction  = new Action<Task>(){
       @Override
       void execute(Task task) {
           println "this is a customAction"
       }
 }
 task typeTask3(action:myAction){
     doLast{
        println "this is a action Task"
     }
 }
```
打印结果：
```
> Task :typeTask3
this is a customAction
this is a action Task
```
##### description 类型示例
```
 task typeTask4(description:"thi is a description"){
      doLast{
        println "this is a description task"
      }
 }
```
然后通过 gradle tasks --all 命令查看所有task
```
Other tasks
-----------
taskNameType
typeTask
typeTask1
typeTask2
typeTask3
typeTask4 - thi is a description
```
都在 Other tasks 的组内
##### group 类型示例
```
 task typeTask5(group:"MyTasks")
```
通过 gradle tasks --all 查看所有task
```
MyTasks tasks
-------------
typeTask5

Other tasks
-----------
taskNameType
typeTask
typeTask1
typeTask2
typeTask3
typeTask4 - thi is a description
```

##### Map 组合示例
```
 task typeTask6(group:"taskMaps",description:"描述信息",action:myAction,dependsOn:[typeTask]){
       doLast{
         println "this is a map Task"
       
       }
 }
```
#### project 方式创建
```
project.tasks.create("typeTask7"){
 doLast{
  println "this is a task create by project"
 }
}
```

#### tasks 方式创建
```
 tasks.create("typeTask8"){
 doLast{
  println "this is a task create by tasks"
 }
 }
```
### 访问 task 方式
- task 是project的一个属性，可以直接通过 task来访问 如：task.doLast{}
- task 创建后放在tasks中的，可以使用tasks来访问，如tasks['typeTask8'].doLast{}
- 通过tasks 的get方法来访问 getByPath(path--path可以是路径也可以是task名称)/getByName(name --- task名称) ---- 找不到任务会抛出UnKnownTaskException
- 通过tasks的find方法来访问，findByPath/findByName,与get方法一样，不同点是如果找不到任务 不会抛出异常而是返回null

### task 执行顺序
```
 task typeTask9(action:myAction){
      println "this is config"
      getTaskActions().each{
          println it
      }
      doFirst{
          println "this is doFirst"
          getTaskActions().each{
              println it
          }
      }
     doLast{
          println "this is doLast"
          getTaskActions().each{
              println it
          }
     } 
 }
```
其中 myAction 就是前面定义的Action 
执行typeTask9 结果展示：
```
> Configure project :
this is config
org.gradle.api.internal.AbstractTask$TaskActionWrapper@f085f45

> Task :typeTask9
this is doFirst
org.gradle.api.internal.AbstractTask$ClosureTaskAction@1fd1c66a
org.gradle.api.internal.AbstractTask$TaskActionWrapper@f085f45
org.gradle.api.internal.AbstractTask$ClosureTaskAction@5c0018e8
this is a customAction
this is doLast
org.gradle.api.internal.AbstractTask$ClosureTaskAction@1fd1c66a
org.gradle.api.internal.AbstractTask$TaskActionWrapper@f085f45
org.gradle.api.internal.AbstractTask$ClosureTaskAction@5c0018e8
```
可以看出在配置阶段，只有一个Action 就是通过action添加的myAction
Task内部维护了一个Action的列表，其执行顺序也是通过遍历这个列表的顺序，以下源码皆摘抄与AbstractTask
首先在配置阶段 因为我们配置了action, 首先会执行
```
  @Override
    public void prependParallelSafeAction(final Action<? super Task> action) {
        if (action == null) {
            throw new InvalidUserDataException("Action must not be null!");
        }
        getTaskActions().add(0, wrap(action));
    }
```
这样把配置的action添加到列表的第一位

接下来看一下doFirst和doLast源码
```
  @Override
    public Task doFirst(final Closure action) {
        hasCustomActions = true;
        if (action == null) {
            throw new InvalidUserDataException("Action must not be null!");
        }
        taskMutator.mutate("Task.doFirst(Closure)", new Runnable() {
            public void run() {
                getTaskActions().add(0, convertClosureToAction(action, "doFirst {} action"));
            }
        });
        return this;
    }
```
```
 @Override
    public Task doLast(final Closure action) {
        hasCustomActions = true;
        if (action == null) {
            throw new InvalidUserDataException("Action must not be null!");
        }
        taskMutator.mutate("Task.doLast(Closure)", new Runnable() {
            public void run() {
                getTaskActions().add(convertClosureToAction(action, "doLast {} action"));
            }
        });
        return this;
    }
```
它们都是把闭包转换为Action之后添加到Action列表中,所不同的是doFirst会添加到列表的第一位，doLast会添加到列表的末尾位，这样列表中的Action的顺序就是
doFirst -> 配置Action -> doLast ，这也符合我们看到的执行的顺序

### 任务排序

通常会用依赖来指定任务的执行顺序，但是有些情况，任务之间没有必要使用依赖关系，但是又希望按照期望的顺序执行，这会用到两个方法
- taskB.shouldRunAfter(taskA) 表示taskB应该在taskA执行后执行，但是也有可能不一定
- taskB.mustRunAfter(taskA)  表示taskB必须在taskA之后执行，这个是严格的


### 任务的启用和禁用
task 有个enabled的属性，默认是true 是开启的，如果想要禁用某个task可以使用诸如：task.enabled = false
### onlyIf断言
```
 typeTask9.enabled = false
 task typeTask10{
      doLast{
        println "this is a onlyIfTask"
      }
 }
 typeTask10.onlyIf{
     println "this is onlyIf"
    typeTask9.enabled
     
 }
```
onlyIf的意识是只有这个闭包的返回值是true的情况下，task 才会执行，
所以运行typeTask10的结果是
```
> Task :typeTask10 SKIPPED
this is onlyIf
```
### task 规则
一般情况 如果我们使用一个不存在的task的时候 会报错
```
task typeTask11(dependsOn:taskMiss){ 
     println "this is task depends null"
 
 }
```
这里taskMiss 没有创建 也就是在tasks里面并没有这个task,所以这个会报错说project 没有这个属性

这里提一点 就是我们创建的task 都存在于TaskContaner里管理，当我们需要使用某一个task的时候 其实 它相当于去这里面去找
```
  public T findByName(String name){
     T value = findByNameWithoutRues(name);
     if(value!=null){
        return value;
     }
     
     applyRules(name);
     
    return findByWithoutRules(name)
  }

```
这一段代码是摘抄于别人的，我没有找到相关的源码，这个意思就是如果找到，说明有相应的task,就返回，如果找不到，就接收一定的规则

我们上面的代码是肯定找不到的，但是又不想报错，如果只是打印错误信息的话 我们就需要添加一个规则 
```
 tasks.addRule("debug描述"){
    String name ->
       task(name){
          doLast{
            println "${name} 不存在！"
          }
       }
 }
```
这样我们在执行上面的代码时 虽然找不到相应的task,但是并没有报错，而是只打印的了错误信息
























































