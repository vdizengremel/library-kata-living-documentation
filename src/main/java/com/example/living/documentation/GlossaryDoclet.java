package com.example.living.documentation;

import com.example.annotation.CoreConcept;
import com.example.living.documentation.Glossary;
import com.example.living.documentation.GlossaryItem;
import com.example.living.documentation.GlossaryPrinter;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import jdk.javadoc.doclet.StandardDoclet;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlossaryDoclet extends StandardDoclet implements Doclet {

    // language=json
    private static final String JSON_TO_GENERATE_GLOSSARY = """
            [
              {
                "line": 2,
                "elements": [
                  {
                    "start_timestamp": "2024-03-26T09:47:53.284Z",
                    "line": 9,
                    "name": "Root",
                    "description": "",
                    "id": "glossary;root",
                    "type": "scenario",
                    "keyword": "Scenario",
                    "steps": [],
                    "tags": [
                      {
                        "name": "@glossary"
                      }
                    ]
                  }
                ],
                "name": "Glossary",
                "description": "  Below are definitions for term used throughout this book.\\n\\n  [glossary]\\n $GLOSSARY_TEXT",
                "id": "glossary",
                "keyword": "Feature",
                "uri": "classpath:glossary/glossary.feature",
                "tags": [
                  {
                    "name": "@glossary",
                    "type": "Tag",
                    "location": {
                      "line": 1,
                      "column": 1
                    }
                  }
                ]
              }
            ]
            """;

    private DocletEnvironment environment;

    @Override
    public void init(Locale locale, Reporter reporter) {
        super.init(locale, reporter);
        System.out.println("init " + getName());
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

//    @Override
//    public Set<? extends Option> getSupportedOptions() {
//        System.out.println("getSupportedOptions");
//        return Set.of(new BaseOptions.Option(resources, "-author") {
//            public boolean process(String opt, List<String> args) {
//                BaseOptions.this.showAuthor = true;
//                return true;
//            }
//        });
//    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_21;
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        System.out.println("doclet run");

        this.environment = environment;

        String packageName = getPackageName();
        Glossary glossary = generateGlossaryFrom(packageName);
        return print(glossary);
    }

    protected String getPackageName() {
        return "com.example.demo.core.domain";
    }

    private Glossary generateGlossaryFrom(String packageName) {
        var packageElement = this.environment.getElementUtils().getPackageElement(packageName);
        Set<PackageElement> allDomainPackages = getSubPackages(packageElement);

        Glossary glossary = new Glossary();

        allDomainPackages.stream().flatMap(domainPackage -> ElementFilter.typesIn(domainPackage.getEnclosedElements())
                        .stream()
                        .filter(clazz -> hasAnnotation(clazz, CoreConcept.class)))
                .map(classElement -> {
                    String classDocumentation = getJavadocDescription(classElement);

                    Map<String, String> information = new HashMap<>();
                    for (VariableElement variable : ElementFilter.fieldsIn(classElement.getEnclosedElements())) {
                        String fieldDocumentation = getJavadocDescription(variable);
                        information.put(variable.getSimpleName().toString(), fieldDocumentation);
                    }

                    Map<String, String> operations = new HashMap<>();
                    for (ExecutableElement method : ElementFilter.methodsIn(classElement.getEnclosedElements())) {
                        String methodDocumentation = getJavadocDescription(method);
                        if(isMethodPublic(method) && !methodDocumentation.isBlank()) {
                            operations.put(method.getSimpleName().toString(), methodDocumentation);
                        }
                    }

                    return new GlossaryItem(classElement.getSimpleName().toString(), classDocumentation, information, operations);
                })
                .forEach(glossary::addItem);
        return glossary;
    }

    protected boolean print(Glossary glossary) {
        StringBuilder stringBuilder = new StringBuilder();
        GlossaryPrinter glossaryPrinter = new GlossaryPrinter(stringBuilder);
        glossaryPrinter.printGlossary(glossary);

        try (PrintWriter writer = new PrintWriter("glossary.json")) {
            writer.println(JSON_TO_GENERATE_GLOSSARY.replace("$GLOSSARY_TEXT", stringBuilder.toString()));
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("ERROR : " + e);
            this.getReporter().print(Diagnostic.Kind.ERROR, e.getMessage());
            return false;
        }
    }

    private boolean isMethodPublic(ExecutableElement method) {
        return method.getModifiers().contains(Modifier.PUBLIC);
    }

    private String getJavadocDescription(Element method) {
        String docComment = environment.getElementUtils().getDocComment(method);
        if (docComment != null) {
            // Séparer la documentation en lignes
            String[] lines = docComment.split("\\r?\\n");
            // Récupérer la première ligne (la description)
            return lines[0].replace("\"", "\\\"").trim();
        }

        return "";
    }

    private Set<PackageElement> getSubPackages(PackageElement parent) {
        Stream<PackageElement> subPackages = getAllPackages()
                .filter(e -> isSubPackage(parent, e));

        return Stream.concat(Stream.of(parent), subPackages).collect(Collectors.toUnmodifiableSet());
    }

    private Stream<PackageElement> getAllPackages() {
        Set<? extends Element> specifiedElements = environment.getSpecifiedElements();

        return (Stream<PackageElement>) specifiedElements.stream()
                .filter(e -> e.getKind() == ElementKind.PACKAGE);
    }

    private boolean isSubPackage(PackageElement parent, PackageElement candidate) {
        String parentQualifiedName = parent.getQualifiedName().toString();
        String candidateQualifiedName = candidate.getQualifiedName().toString();
        return candidateQualifiedName.startsWith(parentQualifiedName + ".");
    }

    private boolean hasAnnotation(TypeElement classElement, Class<?> annotationName) {
        return classElement.getAnnotationMirrors()
                .stream()
                .anyMatch(annotationMirror -> annotationMirror.getAnnotationType().toString().equals(annotationName.getCanonicalName()));
    }
}
