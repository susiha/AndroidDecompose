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
- list


#### map
### 方法
### javaBean
### 闭包
