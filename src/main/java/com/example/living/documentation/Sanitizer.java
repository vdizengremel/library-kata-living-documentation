package com.example.living.documentation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sanitizer {
    public static String sanitizeComment(String comment) {
        Pattern pattern = Pattern.compile("\\{@link\\s+([^\\s}]+)\\s+([^}]+)\\}");
        Matcher matcher = pattern.matcher(comment);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(2));
        }


        matcher.appendTail(sb);
        return replaceAnchorTag(sb.toString());
    }

    private static String replaceAnchorTag(String input) {
        String regex = "<a\\s+href=\\\\\"(.*?)\\\\\">(.*?)<\\/a>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // Remplacement du lien par le texte souhaité
        String replaced = matcher.replaceAll("$1[$2,window=_blank]");
        return replaced;
//
//        Pattern pattern = Pattern.compile("<a\\s+href=\\\"([^\"]+)\\\">([^<]+)</a>");
//        Matcher matcher = pattern.matcher(input);
//        StringBuffer sb = new StringBuffer();
//
//        while (matcher.find()) {
//            String url = matcher.group(1);
//            String text = matcher.group(2);
//            matcher.appendReplacement(sb, url + "[" + text + "]");
//        }
//
//        matcher.appendTail(sb);
//        return sb.toString();
    }
}
