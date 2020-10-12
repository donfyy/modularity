# 组件化

Android Studio 将模块作为项目管理的基本单元。

模块是源代码和构建设置的集合，用于将项目划分为独立的功能单元。

一个安卓项目可以包含一个或多个模块，一个模块可以作为另一个模块的依赖项。每个模块可以单独构建，测试和调试。

在Android Studio中有如下几种不同的模块

- 应用模块

在模块的构建文件中，声明如下语句，应用application插件
```groovy
apply plugin: 'com.android.application'
```

- 库模块

库模块分为两种，Android库和Java库。

如果是Android库，需要在构建文件中声明如下语句，应用library插件

```groovy
apply plugin: 'com.android.library'
```

如果是Java库，则需要在构建文件中声明如下语句，应用java-library插件

```groovy
apply plugin: 'java-library'
```

上面列出的两种类型的模块是我们最关心的，其余的模块类型可以参阅官方文档。

这里我们只关心应用模块和Android库模块。

组件化基于模块化，每一个组件都是一个模块，但是组件的类型是可变的，在开发时组件可以作为应用模块被单独构建打包与测试，
在发布应用时，所有的组件又作为Android库模块被主应用模块所依赖，然后与主应用模块一同被打包。从而达到解藕与加速开发的目的。

一般来说组件之间又如下两个规则

- 只有上层组件可以依赖下层组件
- 同层组件之间相互独立，但可相互通信

好处在于解藕、加速开发，灵活的对业务模块进行组装和拆分。

Gradle 是一个自动化构建工具，构建脚本使用groovy 与 kotlin DSL。

groovy 是一个弱类型语言，但具有静态类型及编译的能力。基于jvm平台。

### 最佳实践

- 将所有模块共享的属性配置在顶层构建文件中

- 将与开发环境相关的配置放在模块级构建文件中

```groovy
    // 通过buildConfigField在BuildConfig类中添加字段
    buildConfigField("String", "SERVER_URL", "\"${url.debug}\"")
```

依赖项规范
```groovy
// 标准写法
implementation group: 'com.android.support', name:'appcompat-v7', version:'28.0.0'

// 简写
implementation 'com.android.support:appcompat-v7:28.0.0'
```
### 项目详细部署

通常会将一个应用分成三层，主应用模块，业务模块，功能模块，依次从左到右依赖。

在顶层构建文件中的ext代码块中定义属性 isRelease ，该属性为true则表示将组件置为Android库模块，
为false则表示将组件置为应用模块。以达到切换模块类型的目的。主应用模块和各组件都要依据该属性切换配置。

### 组件间交互

方式一 使用 EventBus的方式，缺点是：EventBean维护成本太高，不好去管理：

方式二 使用广播的方式，缺点是：不好管理，都统一发出去了

方式三 使用隐士意图方式，缺点是：在AndroidManifest.xml里面配置xml写的太多了

方式四 使用类加载方式，缺点就是，容易写错包名类名，缺点较少

方式五 使用全局Map的方式，缺点是，要注册很多的对象

#### APT

注解处理器，用来在编译期处理源代码中的注解。

#### JavaPoet

JavaPoet是一个用来生成Java代码的框架，使用oop思想对类、方法、修饰符等做了封装，具有不易出错易使用的有点。

### 编写一个注解处理器

1.新建Java库 processor2

2.在processor2中添加如下依赖项

```groovy
    // 要处理的注解库
    implementation project(":annotations")
    // 依赖AutoService
    compileOnly'com.google.auto.service:auto-service:1.0-rc6'
    // 用于生成注解处理器的注册文件
    annotationProcessor'com.google.auto.service:auto-service:1.0-rc6'
    // 用来生成Java源码
    implementation 'com.squareup:javapoet:1.11.1'
```

3.新建类Processor2如下

```java

// 用于自动注册注解处理器
@AutoService(Processor.class)
// 指出此处理器要处理的注解
@SupportedAnnotationTypes("com.donfyy.annotations.BindView2")
// 指出此处理器支持的源码版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class Processor2 extends AbstractProcessor {
    
    // 初始化注解处理器
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 获取到处理注解的工具
        elementTool = processingEnvironment.getElementUtils();
        // 获取到打印日志的工具
        messager = processingEnvironment.getMessager();
        // 获取写出源码的工具
        filer = processingEnvironment.getFiler();
    }
    // 处理注解，若注解被处理返回true，否则返回false
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
```

AutoService会被注解处理器处理，然后生成META-INF/services/javax.annotation.processing.Processor文件，
该文件用于向apt注册注解处理器类，内容如下

```
com.donfyy.processor2.Processor2
```

也就是Processor2的完全类名。

4.在模块中声明注解处理器

```groovy
    annotationProcessor project(":processor2")
```

至此，完成了apt开发的一个固定流程，核心在于Processor2中对于注解的处理，这里根据业务逻辑进行编码。

## 参考资料

- [groovy](http://www.groovy-lang.org/index.html)
- [Gradle](https://gradle.org/)
- [模块化、组件化、插件化、热修复](https://blog.csdn.net/csdn_aiyang/article/details/103735995)
- [项目概览](https://developer.android.com/studio/projects)
- [Android APT 实例讲解](https://developer.aliyun.com/article/722451)
- [javapoet](https://github.com/square/javapoet)
