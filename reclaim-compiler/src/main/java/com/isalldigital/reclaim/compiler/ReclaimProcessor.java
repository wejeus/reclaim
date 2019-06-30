package com.isalldigital.reclaim.compiler;

import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.isalldigital.reclaim.annotations.ReclaimAdapterDelegate;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


/**
 * Since Java 7 we can use: @SupportedAnnotationTypes and @SupportedSourceVersion instead of overriding
 * corresponding methods in AbstractProcessor.
 *
 * The last round contains no input, and is thus a good opportunity to release any resources the processor may have acquired.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.isalldigital.reclaim.annotations.ReclaimAdapterDelegate")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SuppressWarnings("NullAway")
public class ReclaimProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    private boolean initialRound = true;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    private DeclaredType getProviderInterface(AnnotationMirror providerAnnotation) {
        // The very simplest of way of doing this, is also unfortunately unworkable.
        // We'd like to do:
        //    ServiceProvider provider = e.getAnnotation(ServiceProvider.class);
        //    Class<?> providerInterface = provider.value();
        //
        // but unfortunately we can't load the arbitrary class at annotation
        // processing time. So, instead, we have to use the mirror to get at the
        // value (much more painful).
        Map<? extends ExecutableElement, ? extends AnnotationValue> valueIndex = providerAnnotation.getElementValues();
        AnnotationValue value = valueIndex.values().iterator().next();
        return (DeclaredType) value.getValue();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (initialRound) {
            initialRound = false;
        } else {
            return true;
        }

        List<TypeElement> cells = new LinkedList<>();
        List<TypeElement> delegates = new LinkedList<>();

//        from the round environment we can get the class being compiled as rootElement: roundEnv.getRootElements()

        for (Element annotatedElem : roundEnv.getElementsAnnotatedWith(ReclaimAdapterDelegate.class)) {

            // TODO can change to .isClass (but that includes enums, test with this!)
            if (annotatedElem.getKind() != ElementKind.CLASS) {
                error(annotatedElem, "Only classes can be annotated with @%s", ReclaimAdapterDelegate.class.getSimpleName());
                return true; // Exit processing
            }

            // Cast to TypeElement, has more type specific methods
            TypeElement typeElement = (TypeElement) annotatedElem;

            // Only public allowed since we will reference from other package
            if ( ! typeElement.getModifiers().contains(Modifier.PUBLIC)) {
                error(typeElement, "The class %s is not public.", typeElement.getQualifiedName().toString());
                return false;
            }

            // Check if it's an abstract class
            if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
                error(typeElement, "The class %s is abstract. You can't annotate abstract classes with @%",
                        typeElement.getQualifiedName().toString(), ReclaimAdapterDelegate.class.getSimpleName());
                return false;
            }


            AnnotationMirror providerAnnotation = MoreElements.getAnnotationMirror(annotatedElem, ReclaimAdapterDelegate.class).get();
            DeclaredType providerInterface = getProviderInterface(providerAnnotation);
            TypeElement providerType = (TypeElement) providerInterface.asElement();

            cells.add(providerType);
            delegates.add(typeElement);
        }

        TypeSpec typeSpec = generate(cells, delegates);
        JavaFile javaFile = JavaFile.builder("com.isalldigital.reclaim", typeSpec)
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    String.format("Unable to write file: " + e.getMessage()));
        }

        return true; // no further processing of this annotation type
    }

    public TypeSpec generate(List<TypeElement> cells, List<TypeElement> delegates) {
        // Generate class skeleton
        TypeSpec.Builder delegatesFactoryBuilder = TypeSpec.classBuilder("DelegatesFactoryImpl")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get("com.isalldigital.reclaim", "DelegatesFactory"));


        ParameterizedTypeName classToIntegerMapType = ParameterizedTypeName.get(Map.class, String.class, Integer.class);
        appendClassToIntegerStaticMapTo(delegatesFactoryBuilder, classToIntegerMapType, "cellsMap", cells);
        MethodSpec.Builder getCellsMethod = MethodSpec.methodBuilder("getCells")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return cellsMap")
                .returns(classToIntegerMapType);

        delegatesFactoryBuilder.addMethod(getCellsMethod.build());

        ParameterizedTypeName integerToClassMapType = ParameterizedTypeName.get(Map.class, Integer.class, String.class);
        appendIntegerToClassStaticMapTo(delegatesFactoryBuilder, integerToClassMapType, "delegatesMap", delegates);
        MethodSpec.Builder getDelegatesMethod = MethodSpec.methodBuilder("getDelegates")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return delegatesMap")
                .returns(integerToClassMapType);


        delegatesFactoryBuilder.addMethod(getDelegatesMethod.build());

        return delegatesFactoryBuilder.build();
    }

    private FieldSpec.Builder newStaticField(ParameterizedTypeName paramTypes, String varName) {
        return FieldSpec.builder(paramTypes, varName)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T<>()", HashMap.class);
    }

    private void appendIntegerToClassStaticMapTo(TypeSpec.Builder delegatesFactoryBuilder, ParameterizedTypeName paramTypes, String varName, List<TypeElement> typeElements) {
        FieldSpec.Builder staticField = newStaticField(paramTypes, varName);

        CodeBlock.Builder staticInitBlock = CodeBlock.builder();
        for (int i = 0; i < typeElements.size(); ++i) {
            staticInitBlock.addStatement("$L.put($L, $S)", varName, i, typeElements.get(i).getQualifiedName());
        }

        delegatesFactoryBuilder.addField(staticField.build());
        delegatesFactoryBuilder.addInitializerBlock(staticInitBlock.build());
    }

    private void appendClassToIntegerStaticMapTo(TypeSpec.Builder delegatesFactoryBuilder, ParameterizedTypeName paramTypes, String varName, List<TypeElement> typeElements) {
        FieldSpec.Builder staticField = newStaticField(paramTypes, varName);

        CodeBlock.Builder staticInitBlock = CodeBlock.builder();
        for (int i = 0; i < typeElements.size(); ++i) {
            staticInitBlock.addStatement("$L.put($S, $L)", varName, typeElements.get(i).getQualifiedName(), i);
        }

        delegatesFactoryBuilder.addField(staticField.build());
        delegatesFactoryBuilder.addInitializerBlock(staticInitBlock.build());
    }

    private void log(String s) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, s);
    }

    /**
     * Printing a message with an error kind will raise an error.
     */
    private void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }
}
