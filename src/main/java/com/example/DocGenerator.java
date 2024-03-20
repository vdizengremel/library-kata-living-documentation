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

public class DocGenerator {

    private static final String PACKAGE_NAME_TO_SCAN = "com.example.demo.core.domain";

    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter("glossary.md")) {
            writer.println("# " + "Glossary");
            process(writer);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR : " + e);
        }
    }

    private static void process(PrintWriter writer) {
        final Collection<JavaClass> classes = listClasses();
        for (JavaClass clss : classes) {
            if (isBusinessMeaningful(clss)) {
                processClass(clss, writer);
            }

        }
    }

    private static void processClass(JavaClass clss, PrintWriter writer) {
        writer.println("");
        writer.println("## *" + clss.getSimpleName() + "*");
        writer.println(sanitiseComment(clss.getComment()));
        writer.println("");
        if (clss.isEnum()) {
            writer.println("Values:");
            for (JavaField field : clss.getEnumConstants()) {
                printEnumConstant(field, writer);
            }
            printMethods(clss, writer);
        } else if (clss.isInterface()) {
            printMethods(clss, writer);
//            for (ClassDoc subClass : clss.getS(clss)) {
//                printSubClass(subClass);
//            }
        } else {
            writer.println("Information:");
            for (JavaField field : clss.getFields()) {
                printField(field, writer);
            }

            printMethods(clss, writer);
        }
    }

    private static void printMethods(JavaClass clss, PrintWriter writer) {
        List<JavaMethod> methods = clss.getMethods(false);
        var methodsToDisplay = methods.stream().filter(method -> method.isPublic() && hasComment(method)).toList();

        if (methodsToDisplay.isEmpty()) {
            return;
        }

        writer.println("");
        writer.println("Operations:");
        for (JavaMethod method : methodsToDisplay) {
            printMethod(method, writer);
        }
    }

    private static void printField(JavaField field, PrintWriter writer) {
        writer.println("- " + field.getName() + getCommentText(field));
    }

    private static void printEnumConstant(JavaField field, PrintWriter writer) {
        writer.println("- " + field.getName());
    }

    private static void printMethod(JavaMethod method, PrintWriter writer) {
        final String signature = method.getName(); //+ m.getCallSignature()
        //+ ": " + m.getReturns().getSimpleName();
        writer.println("- " + signature + getCommentText(method));
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
        return Optional.ofNullable(element.getComment()).map(DocGenerator::sanitiseComment).map(comment -> ": " + comment).orElse("");
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
        return sb.toString();
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
