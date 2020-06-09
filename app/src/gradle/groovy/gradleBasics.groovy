// ---------- 自定义Task ---------
task  customTask1{
    doFirst{
        println "customTask1: doFirst！"
    }
    doLast{
        println "customTask1: doLast！"
    }
}
tasks.create("customTask2"){
    doFirst{
        println "customTask2: doFirst！"
    }
    doLast{
        println "customTask2: doLast！"
    }
}

// ------------Task 依赖 --------

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

// ------------ 自定义属性 -----------------

task customPro{
    doLast{
        println "project 是否有属性 customPro: ${project.hasProperty('customPro')}"
    }
}

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











































































