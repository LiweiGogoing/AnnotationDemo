package com.liwei.complier;

import com.liwei.bindview_annotation.BindView;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class BindViewProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        //初始化我们需要的基础工具
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(BindView.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        Map<TypeElement, ArrayList<BindViewInfo>> bindViewMap = new HashMap<>();
        for (Element element : elements) {
            // 判断注解修饰是否为属性，不为属性则直接结束
            if (element.getKind() != ElementKind.FIELD) {
                error(element.getSimpleName().toString() + "are not filed, can not use @Bindview");
                return false;
            }
            // 获取注解的值，这里是view的Id
            int resId = element.getAnnotation(BindView.class).value();

            // 获取属性的类
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            if (!bindViewMap.containsKey(typeElement)) {
                bindViewMap.put(typeElement, new ArrayList<BindViewInfo>());
            }

            ArrayList<BindViewInfo> bindViewInfos = bindViewMap.get(typeElement);
            // 添加处理list
            bindViewInfos.add(new BindViewInfo(resId, element.getSimpleName().toString()));
        }
        generateClass(bindViewMap);
        return false;
    }

    private void generateClass(Map<TypeElement, ArrayList<BindViewInfo>> hashMap) {

        if (hashMap == null || hashMap.isEmpty()) {
            return;
        }

        Set<TypeElement> typeElements = hashMap.keySet();
        for (TypeElement typeElement : typeElements) {
            generateJavaClassBySb(typeElement, hashMap.get(typeElement));
        }
    }

    private void generateJavaClassBySb(TypeElement typeElement, List<BindViewInfo> bindViewInfos) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("package ");
            sb.append(getPackageName(typeElement.getQualifiedName().toString()) + ";\n");
            sb.append("import com.liwei.viewbinder.IViewBinder;\n");
            sb.append("public class " + typeElement.getSimpleName() + "$$ViewBinder<T extends " + typeElement.getSimpleName() + "> implements IViewBinder<T> {\n");
            sb.append("@Override\n");
            sb.append("public void bind(T activity) {\n");

            for (BindViewInfo bindViewInfo : bindViewInfos) {
                sb.append("activity." + bindViewInfo.name + "=activity.findViewById(" + bindViewInfo.id + ");\n");
            }
            sb.append("}\n}");
            JavaFileObject sourceFile = mFiler.createSourceFile(typeElement.getQualifiedName().toString() + "$$ViewBinder");
            Writer writer = sourceFile.openWriter();
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPackageName(String className) {
        if (className == null || className.equals("")) {
            return "";
        }
        return className.substring(0, className.lastIndexOf("."));
    }

    private void error(String msg) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    private void info(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
