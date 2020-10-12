package com.donfyy.processor2;

import com.donfyy.annotations.BindView2;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

// 用于自动注册注解处理器
@AutoService(Processor.class)
// 指出此处理器要处理的注解
@SupportedAnnotationTypes("com.donfyy.annotations.BindView2")
// 指出此处理器支持的源码版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 指出此处理器可以接收的参数
@SupportedOptions("pkgName")
public class Processor2 extends AbstractProcessor {

    // 操作Element的工具类（类，函数，属性，其实都是Element）
    private Elements elementTool;
    // type(类信息)的工具类，包含用于操作TypeMirror的工具方法
    private Types typeTool;
    // Message用来打印 日志相关信息  == Log.i
    private Messager messager;  // Gradle 日志中输出
    // 文件生成器， 类 资源 等，就是最终要生成的文件 是需要Filer来完成的
    private Filer filer;
    // 做初始化工作，就相当于 Activity中的 onCreate函数一样的作用
    private Map<TypeElement, Set<ViewInfo>> mToBindMap = new HashMap<>(); //用于记录需要绑定的View的名称和对应的id

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementTool = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
//        String myvalue = processingEnvironment.getOptions().get("myvalue");
        // messager.printMessage(Diagnostic.Kind.ERROR); // 注意：会报错  Log.e
        String pkgName = processingEnvironment.getOptions().get("pkgName");
        messager.printMessage(Diagnostic.Kind.NOTE, "processor2 init pkgName = " + pkgName);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) return false;
        // 按照注解所属的Activity进行分类
        categories(roundEnv.getElementsAnnotatedWith(BindView2.class));
        // 为每个Activity生成帮助类
        for (TypeElement typeElement : mToBindMap.keySet()) {
            String code = generateCode(typeElement);    //获取要生成的帮助类中的所有代码
            String helperClassName = typeElement.getQualifiedName() + "$$Autobind"; //构建要生成的帮助类的类名

            //输出帮助类的java文件，在这个例子中就是MainActivity$$Autobind.java文件
            //输出的文件在build->source->apt->目录下
            try {
                JavaFileObject jfo = filer.createSourceFile(helperClassName, typeElement);
                Writer writer = jfo.openWriter();
                writer.write(code);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    //按不同的Activity生成不同的帮助类
    private String generateCode(TypeElement typeElement) {
        String rawClassName = typeElement.getSimpleName().toString(); //获取要绑定的View所在类的名称
        String packageName = ((PackageElement) elementTool.getPackageOf(typeElement)).getQualifiedName().toString(); //获取要绑定的View所在类的包名
        String helperClassName = rawClassName + "$$Autobind";   //要生成的帮助类的名称

        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(packageName).append(";\n");   //构建定义包的代码
        builder.append("import com.donfyy.IBindHelper;\n\n"); //构建import类的代码

        builder.append("public class ").append(helperClassName).append(" implements ").append("IBindHelper");   //构建定义帮助类的代码
        builder.append(" {\n"); //代码格式，可以忽略
        builder.append("\t@Override\n");    //声明这个方法为重写IBindHelper中的方法
        builder.append("\tpublic void inject(" + "Object" + " target ) {\n");   //构建方法的代码
        for (ViewInfo viewInfo : mToBindMap.get(typeElement)) { //遍历每一个需要绑定的view
            builder.append("\t\t"); //代码格式，可以忽略
            builder.append(rawClassName + " substitute = " + "(" + rawClassName + ")" + "target;\n");    //强制类型转换

            builder.append("\t\t"); //代码格式，可以忽略
            builder.append("substitute." + viewInfo.viewName).append(" = ");    //构建赋值表达式
            builder.append("substitute.findViewById(" + viewInfo.id + ");\n");  //构建赋值表达式
        }
        builder.append("\t}\n");    //代码格式，可以忽略
        builder.append('\n');   //代码格式，可以忽略
        builder.append("}\n");  //代码格式，可以忽略

        return builder.toString();
    }

    // 将注解按照所属的Activity分类
    private void categories(Set<? extends Element> elements) {
        // 遍历每一个element
        for (Element element : elements) {
            // 被@BindView标注的应当是变量，这里简单的强制类型转换
            VariableElement variableElement = (VariableElement) element;
            // 获取代表Activity的TypeElement
            TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();
            // views储存着一个Activity中将要绑定的view的信息
            Set<ViewInfo> views = mToBindMap.get(enclosingElement);
            // 如果views不存在就new一个
            if (views == null) {
                views = new HashSet<>();
                mToBindMap.put(enclosingElement, views);
            }
            // 获取到一个变量的注解
            BindView2 bindAnnotation = variableElement.getAnnotation(BindView2.class);
            // 取出注解中的value值，这个值就是这个view要绑定的xml中的id
            int id = bindAnnotation.value();
            // 把要绑定的View的信息存进views中
            views.add(new ViewInfo(variableElement.getSimpleName().toString(), id));
        }
    }

    //要绑定的View的信息载体
    class ViewInfo {
        String viewName;    //view的变量名
        int id; //xml中的id

        public ViewInfo(String viewName, int id) {
            this.viewName = viewName;
            this.id = id;
        }
    }

}