//多种方式创建任务
// ------------------- 任务名字创建方式 -----------------


def Task taskNameType = task(taskNameType)
taskNameType.doLast{
    println "taskNameType 是 ${taskNameType}"
    println "taskNameType 的 class  = ${taskNameType.getClass()}"
    println "创建方法原型为：Task task(${taskNameType.name})"
}

// -------------------- 任务名称 + Map 创建方式 --------------

// Map 配置项
// type ：基于一个存在的Task来创建 和类继承差不多 默认值 DefaultTask
// overwrite:  是否替换存在的Task 和Type配合使用 默认值false
// dependsOn： 用于配置任务的依赖 默认值[]
// action： 添加任务中一个Action或者一个闭包 默认值 null
// description: 用于配置任务的描述   默认值 null
// group:  用于配置任务的分组  默认值 null



// -------------- type 用法 -------------

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


// --------------- overwrite 用法 -------------

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


// --------------- dependsOn 用法 --------------

task typeTask2(dependsOn:[typeTask1,typeTask]){
    doLast{
        println "this is a dependsOnTask"
    }
}


// --------------- action 用法 -----------------


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


// -------------- description 用法 ------------



task typeTask4(description:"thi is a description"){
    doLast{

        println "this is a description task"
    }
}


// -------------- group 用法 ------------


task typeTask5(group:"MyTasks")



// ----------------- map 组合用法 ---------------

def map = [group:"taskMaps",description:"描述信息",action:myAction,dependsOn:[typeTask]]

task typeTask6(group:"taskMaps",description:"描述信息",action:myAction,dependsOn:[typeTask]){

    doLast{

        println "this is a map Task"

    }
}


// ----------------- project 创建方式 ---------------------


project.tasks.create("typeTask7"){

    doLast{

        println "this is a task create by project"

    }
}


// ----------------- tasks创建方式 ---------------------


tasks.create("typeTask8"){

    doLast{

        println "this is a task create by tasks"

    }

}

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

tasks.addRule("debug描述"){

    String name ->
        task(name){
            doLast{
                println "${name} 不存在！"
            }
        }


}

task typeTask11(dependsOn:taskMiss){
    println "this is task depends null"

}


























