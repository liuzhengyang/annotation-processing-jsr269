package com.lzy.blog.processor;

import static javax.lang.model.element.Modifier.STATIC;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.lzy.blog.Builder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * @author liuzhengyang
 */
@SupportedAnnotationTypes({
        "com.lzy.blog.Builder"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuilderProcessor extends AbstractProcessor {

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("Processing " + annotations + roundEnv);
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Builder.class);
        elementsAnnotatedWith.forEach(element -> {
            if (element.getKind() != ElementKind.CLASS) {
                System.out.println("not class");
                return;
            }
            TypeMirror elementTypeMirror = element.asType();
            String builderClassName = element.getSimpleName() + "Builder";
            TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(builderClassName)
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC);

            MethodSpec.Builder builderMethod = MethodSpec.methodBuilder("build")
                    .returns(TypeName.get(element.asType()))
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("$T instance = new $T()", TypeName.get(elementTypeMirror), TypeName.get(elementTypeMirror));

            element.getEnclosedElements().forEach(field -> {
                if (field.getKind() == ElementKind.FIELD) {
                    boolean isStatic = field.getModifiers().contains(STATIC);
                    if (isStatic) {
                        System.out.println(field.getSimpleName() + " is static");
                        return;
                    }
                    String fieldName = field.getSimpleName().toString();
                    System.out.println(fieldName);
                    String transformedName = upperFirstChar(fieldName);

                    MethodSpec methodSpec = MethodSpec.methodBuilder("build" + transformedName)
                            .returns(TypeName.get(element.asType()))
                            .returns(ClassName.get("com.lzy.gen", builderClassName))
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(TypeName.get(field.asType()), field.getSimpleName().toString())
                            .addStatement("this.$L = $L;return this", field.getSimpleName().toString()
                                    , field.getSimpleName().toString())
                            .build();

                    FieldSpec fieldSpec = FieldSpec.builder(TypeName.get(field.asType()),
                            fieldName, Modifier.PRIVATE).build();

                    String fieldSetName = upperFirstChar(fieldName);
                    builderMethod.addStatement("instance.set$L(this.$L)", fieldSetName, fieldName);
                    typeBuilder.addField(fieldSpec);
                    typeBuilder.addMethod(methodSpec);
                }
            });

            builderMethod.addStatement("return instance");

            typeBuilder.addMethod(builderMethod.build());

            TypeSpec typeSpec = typeBuilder.build();

            try {
                JavaFile.builder("com.lzy.gen", typeSpec).build().writeTo(System.out);
                JavaFile.builder("com.lzy.gen", typeSpec).build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return true;
    }

    private static String upperFirstChar(String name) {
        if (name.length() < 1) {
            return name;
        }
        String firstChar = name.substring(0, 1).toUpperCase();
        if (name.length() > 1) {
            return firstChar + name.substring(1);
        }
        return firstChar;
    }
}
