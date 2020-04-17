package com.donfyy.annotationsprocessor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.donfyy.annotations.BindView")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ProcessorA extends AbstractProcessor {

    private Messager mMessager;
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
        Locale locale = processingEnvironment.getLocale();
        mMessager.printMessage(Diagnostic.Kind.WARNING, "say hello:" + (locale == null ? "" : locale.getDisplayName()));
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {


        if (CollectionUtils.isNotEmpty(set)) {
            set.forEach(e -> {
                mMessager.printMessage(Diagnostic.Kind.WARNING, "ProcessorA:" + e.toString());

                MethodSpec main = MethodSpec.methodBuilder("main")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(void.class)
                        .addParameter(String[].class, "args")
                        .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                        .build();
                TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(main)
                        .build();

                JavaFile javaFile = JavaFile.builder("com.donfyy.example.helloworld", helloWorld).build();

                try {
                    javaFile.writeTo(mFiler);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    mMessager.printMessage(Diagnostic.Kind.WARNING, ex.getMessage());
                }
            });
        }

        return false;
    }
}