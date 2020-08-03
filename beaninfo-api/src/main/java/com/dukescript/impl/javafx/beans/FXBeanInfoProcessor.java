package com.dukescript.impl.javafx.beans;

/*-
 * #%L
 * DukeScript JavaFX Extensions - a library from the "DukeScript" project.
 * %%
 * Copyright (C) 2020 Dukehoff GmbH
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.dukescript.api.javafx.beans.FXBeanInfo.Introspect;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = Processor.class)
@SupportedAnnotationTypes("com.dukescript.api.javafx.beans.FXBeanInfo.Introspect")
public final class FXBeanInfoProcessor extends AbstractProcessor {
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean[] ok = { true };
        TypeElement elementObject = processingEnv.getElementUtils().getTypeElement("java.lang.Object");
        for (Element e : roundEnv.getElementsAnnotatedWith(Introspect.class)) {
            if (e.getKind() != ElementKind.CLASS) {
                emitErrorOn(e, "Only use @Introspect on classes", ok);
                continue;
            }
            String pkg = findPackage(e, ok);
            if (pkg == null) {
                continue;
            }
            TypeElement clazz = (TypeElement) e;
            TypeMirror parent = clazz.getSuperclass();
            if (parent == null || processingEnv.getTypeUtils().asElement(parent).equals(elementObject)) {
                emitErrorOn(e, "Class must extend another class. Try adding: extends " + e.getSimpleName() + "BeanInfo", ok);
                continue;
            }
            final String clazzName = inPackageName(e);
            try {
                final String parentName = parent.toString();
                JavaFileObject src = processingEnv.getFiler().createSourceFile(pkg + "." + parentName, e);
                Writer w = src.openWriter();

                w.append("package ").append(pkg).append(";\n");
                w.append("/** @see ").append(clazzName).append(" */\n");
                w.append("abstract class ").append(parentName).append(" implements com.dukescript.api.javafx.beans.FXBeanInfo.Provider {\n");
                w.append("    private com.dukescript.api.javafx.beans.FXBeanInfo info;\n");
                w.append("    protected ").append(parentName).append("() {\n");
                w.append("        com.dukescript.api.javafx.beans.FXBeanInfo.newBuilder(this).build();\n");
                w.append("    }\n");
                w.append("\n");
                w.append("    @java.lang.Override\n");
                w.append("    public com.dukescript.api.javafx.beans.FXBeanInfo getFXBeanInfo() {\n");
                w.append("      if (info == null) {\n");
                w.append("        ").append(clazzName).append(" obj = (").append(clazzName).append(") this;\n");
                w.append("        com.dukescript.api.javafx.beans.FXBeanInfo.Builder b = com.dukescript.api.javafx.beans.FXBeanInfo.newBuilder(obj);\n");
                Map<String,List<Element>> props = new HashMap<>();
                registerProperties(w, "        ", clazz, props, ok);
                registerMethods(w, "        ", clazz, props, ok);

                for (Map.Entry<String, List<Element>> entry : props.entrySet()) {
                    String propertyName = entry.getKey();
                    List<Element> arr = entry.getValue();

                    if (arr.size() > 1) {
                        for (Element defined : arr) {
                            emitErrorOn(defined, propertyName + " is defined multiple times", ok);
                        }
                    }
                }

                w.append("        this.info = b.build();\n");
                w.append("      }\n");
                w.append("      return info;\n");
                w.append("    }\n");
                w.append("}\n");

                w.close();
            } catch (IOException ex) {
                ok[0] = false;
            }
        }
        return ok[0];
    }

    private String findPackage(Element e, boolean[] ok) {
        for (;;) {
            if (e.getKind() == ElementKind.PACKAGE) {
                return ((PackageElement)e).getQualifiedName().toString();
            }
            if (e.getModifiers().contains(Modifier.PRIVATE)) {
                emitErrorOn(e, "Class cannot be private", ok);
                return null;
            }
            e = e.getEnclosingElement();
        }
    }

    private String inPackageName(Element e) {
        StringBuilder sb = new StringBuilder();
        for (;;) {
            if (e.getKind() == ElementKind.PACKAGE) {
                return sb.toString();
            }
            if (sb.length() > 0) {
                sb.insert(0, '.');
            }
            sb.insert(0, e.getSimpleName());
            e = e.getEnclosingElement();
        }
    }

    private void emitErrorOn(Element e, String msg, boolean[] ok) {
        ExpectError ee = e.getAnnotation(ExpectError.class);
        if (ee != null) {
            for (String expected : ee.value()) {
                if (expected.equals(msg)) {
                    return;
                }
            }
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
        ok[0] = false;
    }

    private void registerProperties(Writer w, String prefix, TypeElement clazz, Map<String, List<Element>> props, boolean[] ok) throws IOException {
        final Elements eu = processingEnv.getElementUtils();
        final Types tu = processingEnv.getTypeUtils();
        final String valueName = "javafx.beans.value.ObservableValue";
        final TypeElement elementValue = eu.getTypeElement(valueName);
        if (elementValue == null) {
            emitErrorOn(clazz, "Cannot find " + valueName, ok);
        }
        TypeMirror superType = tu.erasure(elementValue.asType());
        for (Element e :clazz.getEnclosedElements()) {
            if (e.getKind() != ElementKind.FIELD) {
                continue;
            }
            if (e.getModifiers().contains(Modifier.PRIVATE)) {
                continue;
            }
            final TypeMirror rawType = tu.erasure(e.asType());
            final String propName = e.getSimpleName().toString();
            if (!tu.isAssignable(rawType, superType)) {
                w.append(prefix).append("b.constant(\"").append(propName).append("\", obj.").append(propName).append(");\n");
            } else {
                w.append(prefix).append("b.property(\"").append(propName).append("\", obj.").append(propName).append(");\n");
            }
            appendElement(props, propName, e);
        }
    }

    private static boolean appendElement(Map<String, List<Element>> props, final String propName, Element e) {
        List<Element> arr = props.get(propName);
        if (arr == null) {
            arr = new ArrayList<>();
            props.put(propName, arr);
        }
        arr.add(e);
        return arr.size() == 1;
    }

    private void registerMethods(Writer w, String prefix, TypeElement clazz, Map<String, List<Element>> props, boolean[] ok) throws IOException {
        final Elements eu = processingEnv.getElementUtils();
        final Types tu = processingEnv.getTypeUtils();
        final String actionEvent = "javafx.event.ActionEvent";
        final TypeElement elementActionEvent = eu.getTypeElement(actionEvent);
        if (elementActionEvent == null) {
            emitErrorOn(clazz, "Cannot find " + actionEvent, ok);
        }
        TypeMirror typeActionEvent = tu.erasure(elementActionEvent.asType());
        for (Element e :clazz.getEnclosedElements()) {
            if (e.getKind() != ElementKind.METHOD) {
                continue;
            }
            if (e.getModifiers().contains(Modifier.PRIVATE)) {
                continue;
            }
            if (e.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }

            ExecutableElement ee = (ExecutableElement) e;
            switch (ee.getParameters().size()) {
                case 0:
                    break;
                case 1:
                    if (tu.isAssignable(ee.getParameters().get(0).asType(), typeActionEvent)) {
                        break;
                    }
                default:
                    continue;
            }

            final String propName = e.getSimpleName().toString();
            w.append(prefix);
            if (!appendElement(props, propName, e)) {
                w.append("// ");
            }
            w.append("b.action(\"").append(propName).append("\", obj::").append(propName).append(");\n");
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface ExpectError {
        String[] value();
    }
}
