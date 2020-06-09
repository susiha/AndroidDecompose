task stringdemo{
    def name ='Susiha'
    println 'hello,i am $name'
    println "hello,i am $name"
}

task listdemo{
    def nums = [1,2,3,4,5,6,7]
    println nums.getClass().name
    println nums[0]
    println nums[-2]
    println nums[-2 .. -5]
    println nums[-2 .. -5].getClass().name
}

task mapdemo{

    def map = ['name':'susiha','age':32]
    println map.getClass().name
    println map['name']
    println map['age']

    map.each{
        println "key:${it.key},Value:${it.value}"
    }
}




task methodDemo1{
    method1(3,5)
    println "-----分割线------"
    method1 4,7
}
def method1(int a,int b){
    println a+b
}



task methodDemo2{
    println "result = ${method2 10,5}"
}

def method2(int a, int b){
    println "this is a add method"
    a+b
}



task javaBeanDemo{

    Person p = new Person()

    p.name = "susiha"

    println "name = ${p.name}"
    println "age = ${p.age}"

}
class Person{
    private String name

    public int getAge(){
        32
    }
}


task closureDemo{

    def result =0;
    customClosure{
        result +=it
        println "result = ${result}"
    }
}


def customClosure(closure){
    for (int i in 1 .. 10){
        closure(i)
    }
}




def method1(){
    println "Context this:${this.getClass()} in root"
    println "method1 in root"
}
class Delegate{
    def method1(){
        println "Delegate this:${this.getClass()} in delegate"
        println "method1 in delegate"
    }
    def test(Closure<Delegate> closure){
        closure(this)
    }
}
task closureDemo2{
    Delegate d = new Delegate()
    d.test{
        println "thisObject:${thisObject.getClass()}"
        println "owner:${owner.getClass()}"
        println "delegate :${delegate.getClass()}"
        method1()
        it.method1()
    }
}
