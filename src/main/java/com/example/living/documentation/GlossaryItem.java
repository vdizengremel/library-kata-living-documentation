package com.example.living.documentation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record GlossaryItem(String name,
                           String definition,
                           Map<String, String> informationDefinitionsByName,
                           Map<String, String> operationDefinitionsByName) {
    public GlossaryItem {
        informationDefinitionsByName = sortByKey(informationDefinitionsByName);
        operationDefinitionsByName = sortByKey(operationDefinitionsByName);
    }

    static Map<String, String> sortByKey(Map<String, String> mapToSort) {
        return mapToSort.entrySet().stream().sorted((a, b) -> a.getKey().compareToIgnoreCase(b.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> {
                    throw new RuntimeException();
                }, LinkedHashMap::new));
    }
}
