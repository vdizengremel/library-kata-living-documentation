package com.example;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
            String result = process();
            writer.println(JSON_TO_GENERATE_GLOSSARY.replace("$GLOSSARY_TEXT", result));

        } catch (FileNotFoundException e) {
            System.out.println("ERROR : " + e);
        }
    }

    private static String process() {
        String glossary = "";
        final Collection<JavaClass> classes = listClasses();
        for (JavaClass clss : classes) {
            if (isBusinessMeaningful(clss)) {
                String item = processClass(clss);
                glossary = glossary + item + "\\n";
            }

        }

        return glossary;
    }

    private static String processClass(JavaClass clss) {
        String methodAsString = clss.getSimpleName() + ":: " + sanitiseComment(clss.getComment()) + "\\n\\n";
        System.out.println(clss.getSource().getImports());


        if (clss.isEnum()) {
            methodAsString += "Values:\\n";
            for (JavaField field : clss.getEnumConstants()) {
                methodAsString += "- " + field.getName() + "\\n";
            }

            methodAsString += "\\n\\n";
            methodAsString += printMethods(clss);
        } else if (clss.isInterface()) {
//            printMethods(clss, writer);
//            for (ClassDoc subClass : clss.getS(clss)) {
//                printSubClass(subClass);
//            }
        } else {
            methodAsString += ("Information:\\n");
            for (JavaField field : clss.getFields()) {
                methodAsString += "- " + field.getName() + getCommentText(field) + "\\n";
            }

            methodAsString += "\\n\\n";
            methodAsString += printMethods(clss);
        }

        return methodAsString;
    }

    private static String printMethods(JavaClass clss) {
        List<JavaMethod> methods = clss.getMethods(false);
        var methodsToDisplay = methods.stream().filter(method -> method.isPublic() && hasComment(method)).toList();

        if (methodsToDisplay.isEmpty()) {
            return "";
        }

        String result = "Operations:\\n";

        for (JavaMethod method : methodsToDisplay) {
            result += printMethod(method) + "\\n";
        }

        return result;
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
        return Optional.ofNullable(element.getComment()).map(GlossaryGenerator::sanitiseComment).map(comment -> ": " + comment).orElse("");
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
