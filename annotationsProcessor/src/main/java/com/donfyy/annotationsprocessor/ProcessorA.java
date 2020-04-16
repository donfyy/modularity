package com.donfyy.annotationsprocessor;

import com.google.auto.service.AutoService;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.donfyy.annotations.BindView")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ProcessorA extends AbstractProcessor {

    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        mMessager.printMessage(Diagnostic.Kind.WARNING, "say hello");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {


//        if (CollectionUtils.isNotEmpty(set)) {
//            set.forEach(e -> {
//                mMessager.printMessage(Diagnostic.Kind.ERROR, "ProcessorA:" + e.toString());
//            });
//        }

        return false;
    }
}