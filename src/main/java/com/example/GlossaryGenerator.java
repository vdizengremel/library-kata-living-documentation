package com.example;

import com.example.living.documentation.Glossary;
import com.example.living.documentation.GlossaryItem;
import com.example.living.documentation.GlossaryPrinter;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlossaryGenerator {

    // language=json
    private static final String JSON_TO_GENERATE_GLOSSARY = "[\n" +
                                                            "  {\n" +
                                                            "    \"line\": 2,\n" +
                                                            "    \"elements\": [\n" +
                                                            "      {\n" +
                                                            "        \"start_timestamp\": \"2024-03-26T09:47:53.284Z\",\n" +
                                                            "        \"line\": 9,\n" +
                                                            "        \"name\": \"Root\",\n" +
                                                            "        \"description\": \"\",\n" +
                                                            "        \"id\": \"glossary;root\",\n" +
                                                            "        \"type\": \"scenario\",\n" +
                                                            "        \"keyword\": \"Scenario\",\n" +
                                                            "        \"steps\": [],\n" +
                                                            "        \"tags\": [\n" +
                                                            "          {\n" +
                                                            "            \"name\": \"@glossary\"\n" +
                                                            "          }\n" +
                                                            "        ]\n" +
                                                            "      }\n" +
                                                            "    ],\n" +
                                                            "    \"name\": \"Glossary\",\n" +
                                                            "    \"description\": \"  Below are definitions for term used throughout this book.\\n\\n  [glossary]\\n $GLOSSARY_TEXT\",\n" +
                                                            "    \"id\": \"glossary\",\n" +
                                                            "    \"keyword\": \"Feature\",\n" +
                                                            "    \"uri\": \"classpath:glossary/glossary.feature\",\n" +
                                                            "    \"tags\": [\n" +
                                                            "      {\n" +
                                                            "        \"name\": \"@glossary\",\n" +
                                                            "        \"type\": \"Tag\",\n" +
                                                            "        \"location\": {\n" +
                                                            "          \"line\": 1,\n" +
                                                            "          \"column\": 1\n" +
                                                            "        }\n" +
                                                            "      }\n" +
                                                            "    ]\n" +
                                                            "  }\n" +
                                                            "]\n";

    private static final String PACKAGE_NAME_TO_SCAN = "com.example.demo.core.domain";

    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter("target/glossary.json")) {
            Glossary glossary = process();
            StringBuilder stringBuilder = new StringBuilder();

            GlossaryPrinter glossaryPrinter = new GlossaryPrinter(stringBuilder);
            glossaryPrinter.printGlossary(glossary);

            writer.println(JSON_TO_GENERATE_GLOSSARY.replace("$GLOSSARY_TEXT", stringBuilder.toString()));

        } catch (FileNotFoundException e) {
            System.out.println("ERROR : " + e);
        }
    }

    private static Glossary process() {
        Glossary glossary = new Glossary();

        listClasses().stream()
                .filter(GlossaryGenerator::isBusinessMeaningful)
                .map(GlossaryGenerator::processClass)
                .forEach(glossary::addItem);

        return glossary;
    }

    private static GlossaryItem processClass(JavaClass clss) {
        Map<String, String> informationByName = new HashMap<>();
        Map<String, String> operationByName = new HashMap<>();

        if (clss.isEnum()) {
            informationByName = javaFieldsToMap(clss.getEnumConstants());
            for (JavaField field : clss.getEnumConstants()) {
                informationByName.put(field.getName(), "");
            }

            operationByName = printMethods(clss);
        } else if (clss.isInterface()) {
//            printMethods(clss, writer);
//            for (ClassDoc subClass : clss.getS(clss)) {
//                printSubClass(subClass);
//            }
        } else {
            informationByName = javaFieldsToMap(clss.getFields());
            operationByName = printMethods(clss);
        }

        return new GlossaryItem(clss.getSimpleName(), sanitiseComment(clss.getComment()), informationByName, operationByName);
    }

    private static Map<String, String> javaFieldsToMap(List<JavaField> javaFields) {
        Map<String, String> information = new HashMap<>();

        javaFields.forEach(javaField -> {
            information.put(javaField.getName(), getCommentText(javaField));
        });

        return information;
    }

    private static Map<String, String> printMethods(JavaClass clss) {
        List<JavaMethod> methods = clss.getMethods(false);
        var methodsToDisplay = methods.stream().filter(method -> method.isPublic() && hasComment(method)).toList();

        if (methodsToDisplay.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> operations = new HashMap<>();
        for (JavaMethod method : methodsToDisplay) {
            operations.put(method.getName(), getCommentText(method));
        }

        return operations;
    }

    private static String printMethod(JavaMethod method) {
        final String signature = method.getName(); //+ m.getCallSignature()
        //+ ": " + m.getReturns().getSimpleName();
        return ("- " + signature + getCommentText(method));
    }


    private static boolean hasComment(JavaMethod doc) {
        return Optional.ofNullable(doc.getComment()).map(String::trim).isPresent();
    }

    private static boolean isBusinessMeaningful(JavaClass clss) {
        return clss.getAnnotations().stream()
                .anyMatch(annotation ->
                        annotation.getType().getFullyQualifiedName().startsWith("com.example.annotation")
                );
    }

    private static String getCommentText(JavaAnnotatedElement element) {
        return Optional.ofNullable(element.getComment()).map(GlossaryGenerator::sanitiseComment).orElse("");
    }


    public static String sanitiseComment(String comment) {
        System.out.println(comment);
        Pattern pattern = Pattern.compile("\\{@link\\s+([^\\s}]+)\\s+([^}]+)\\}");
        Matcher matcher = pattern.matcher(comment);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(2));
        }


        matcher.appendTail(sb);
        return sb.toString().replace("\"", "\\\"");
    }

    private static Collection<JavaClass> listClasses() {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File("src/main/java"));

        JavaPackage packageByName = builder.getPackageByName(PACKAGE_NAME_TO_SCAN);
        return getAllSubPackages(packageByName).stream().flatMap(p -> p.getClasses().stream()).sorted((o1, o2) -> o1.getSimpleName().compareToIgnoreCase(o2.getSimpleName())).toList();
    }

    private static Collection<JavaPackage> getAllSubPackages(JavaPackage javaPackage) {
        Collection<JavaPackage> packageWithSubPackages = new ArrayList<>();
        packageWithSubPackages.add(javaPackage);

        var subPackages = javaPackage.getSubPackages();
        subPackages.forEach(subPackage -> packageWithSubPackages.addAll(getAllSubPackages(subPackage)));

        return packageWithSubPackages;
    }
}
