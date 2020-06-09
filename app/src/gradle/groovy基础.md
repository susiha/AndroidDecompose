### 字符串
单引号/双引号来表示字符串，这两者的区别
- 单引号:只表示字符串常量，字符串里面的表达式做运算
- 双引号:可以对字符串里面的表达式做运算
		
示例 ：
		 
```
def name ='Susiha'
  println 'hello,i am $name'
  println "hello,i am $name"
``` 

打印结果：
```
hello,i am $name
hello,i am Susiha
``` 
    
### 集合
#### list
list的定义和使用都相比较java 简单的多

示例：
```
  def nums = [1,2,3,4,5,6,7]
  println nums.getClass().name
  println nums[0]
  println nums[-2]
  println nums[-2 .. -5]
  println nums[-2 .. -5].getClass().name 
``` 
打印结果：
```
java.util.ArrayList
1
6
[6, 5, 4, 3]
java.util.ArrayList
``` 
- 可以看出我们在java中定义数组的方式，在groovy中就是list
- 可以通过数组下标来访问
- 可以通过逆序的下标来访问
- 可以使用 .. 来表示一个范围，生成的还是一个list,像上面的列子可以通过逆序访问和..快速反转一个list
- list可以通过each来遍历列表
#### map
示例：
```
task mapdemo{
  def map = ['name':'susiha','age':32]
  println map.getClass().name
  println map['name']
  println map['age']
  map.each{
    println "key:${it.key},Value:${it.value}"
  }
}
```
打印结果：
```
java.util.LinkedHashMap
susiha
32
key:name,Value:susiha
key:age,Value:32
```

- map跟list很像，可以直接通过map[key]或者 map.key来访问
- map也可以通过each来进行遍历
### 方法

- 方法的调用，可以直接在方法名称后面跟上参数，不用带()

示例：
```
task methodDemo1{
  method1(3,5)
  println "-----分割线------"
  method1 4,7
}


def method1(int a,int b){
  println a+b
}
```
打印结果：
```task methodDemo2{
 println "result = ${method2 10,5}"
}

def method2(int a, int b){
  println "this is a add method"
  a+b
}
8
-----分割线------
11
```
可以看出，在方法的调用上，带不带（），效果是一样的

- 在方法中通常可以不用关键字return ,通常是方法执行的最后一句代码作为其返回值
示例：
```
task methodDemo2{
 println "result = ${method2 10,5}"
}

def method2(int a, int b){
  println "this is a add method"
  a+b
}
```

- 如果方法的最后一个参数是闭包，可以把这个闭包放在外部去

下面代码和注释描述了常见闭包的形式的形成

```
//第一步，正常形式
list.each({println it})

//第二步：符合groovy语法，可以放在外边
list.each(){println it}

// 第三步：方法的() 可以去掉

list.each{println it}
```
### javaBean
```
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
```
- 对于JavaBean中定义的属性，可以直接使用p.name这样的方式访问，不需要get/set方法
- 对于像getAge这样的方法，虽然没有定义age属性，可以直接使用p.age 这样的形式访问，但是不能对p.age赋值，这是因为在JavaBean中没有明确的set方法
### 闭包
#### 自定义闭包
```
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
```
对于闭包中的it，其实就是在定义闭包中的closure的参数，它可以是数组字符串甚至是一个对象
#### 闭包委托
- groovy 闭包中有三个属性 thisObject，owner和delegate,当在闭包内调用方法时，由这三个属性来确定哪个对象来处理

```
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
```
打印结果：
```
thisObject:class build_b1d3e2wugefie7dstd0p1q3xh
owner:class build_b1d3e2wugefie7dstd0p1q3xh$_run_closure8
delegate :class build_b1d3e2wugefie7dstd0p1q3xh$_run_closure8
Context this:class build_b1d3e2wugefie7dstd0p1q3xh in root
method1 in root
Delegate this:class Delegate in delegate
method1 in delegate
```

- 从上面结果看 thisObject 一般是优先级最高的，它是指的是project对应的上下文，所以它与method1中的this是一致的
- owner 与delegate 在默认的情况下是一样的，delegate是可以被修改的，优先级上来说 thisObject>owner>delegate


[示例代码](https://github.com/susiha/AndroidDecompose/blob/master/app/src/gradle/groovy/groovyBasics.groovy)
