
### 构建变体
android gradle 提供了三种变体
- applicationVariants
- libraryVarients
- testVariants
可以通过这些变体的操作来达到更改打包的名字的操作
```
 applicationVariants.all{
        variant ->
            println '---------分割线-------------'
            println "variantData ApplicationVariantImpl类的变量：${variant.variantData}"
            println "install InstallableVariantImpl类的变量:${variant.install}"
            println "signingConfig AndroidArtifactVariantImpl类的变量:${variant.signingConfig}"
            println "versionName AndroidArtifactVariantImpl类的变量:${variant.versionName}"
            println "versionCode AndroidArtifactVariantImpl类的变量:${variant.versionCode}"
            println "name BaseVariantImpl类的变量:${variant.name}"
            println "description BaseVariantImpl类的变量:${variant.description}"
            println "dirName BaseVariantImpl类的变量:${variant.dirName}"
            println "baseName BaseVariantImpl类的变量:${variant.baseName}"
            println "flavorName BaseVariantImpl类的变量:${variant.flavorName}"
            println "outputs BaseVariantImpl类的变量:${variant.outputs}"
            println "buildTypeName BaseVariantImpl类的变量:${variant.buildType.name}"
            variant.outputs.all{
                println '---------outputs分割线-------------'
                println "    it BaseVariantOutput类的变量:${it}"
                println "    name BaseVariantOutput类的变量:${it.name}"
                println "    baseName BaseVariantOutput类的变量:${it.baseName}"
                println "    dirName BaseVariantOutput类的变量:${it.dirName}"
                println "    outputFile OutputFile类的变量:${it.outputFile}"
                it.outputFileName = "gradlePlugin_${variant.flavorName}_${variant.buildType.name}_${variant.versionCode}_${buildTime()}.apk"
            }
    }
}

def buildTime(){
    def date = new Date()
    def formattedDate = date.format('yyyyMMdd')
    return formattedDate
}

```

以上打印 applicationVariant 对应的实现类，以及真实的值，可以通过productFlavor和buildType合理的构造打包的apk的名字

### 多项目打包
引用库
```
compile project(':lib')
```

一般android 库发布出来的都是release版本的 可以通过配置来修改
```
android{
   defaultPublishConfig 'debug'
}
```
也可以同时发布多个aar包 

```
  android{
     publishNonFefault true
  
  }
```


然后在使用的时候
```
compile project project(path:':lib',configuration:'flavorRelease')
```

也可以把库发布到maven私服上去 
搭建maven私服可以通过 [sonatype nexus]( https://www.sonatype.com/download-oss-sonatype)来实现，下载后运行之后 
在浏览器 http://localhost:8081 就可以看到私服的仓库了 

在lib的build.gradle中需要上传
```
version '1.0.2'
group 'com.susiha.lib'
uploadArchives{
    repositories {

        mavenDeployer{

            repository(url:"http://localhost:8081/repository/maven-releases/"){
                authentication(userName:'admin',password:'admin12345')
            }
            snapshotRepository(url: "http://localhost:8081/repository/maven-snapshots/"){
                authentication(userName:'admin',password:'admin12345')
            }
             pom.artifactId = 'androidlib'
            pom.packaging ='aar'
        }
    }

}

```

然后在root的build.gradle里面添加去哪找
```
allprojects {
    repositories {
        google()
        jcenter()
        maven{
            url 'http://localhost:8081/repository/maven-releases/'
        }
    }
}

```

然后就可以在依赖添加了
```
 implementation "com.susiha.lib:androidlib:1.0.2"

```






































