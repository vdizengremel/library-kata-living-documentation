package com.example.living.documentation;

import java.util.ArrayList;
import java.util.List;

public final class Glossary {
    private final List<GlossaryItem> items;

    public Glossary() {
        this.items = new ArrayList<>();
    }

    public void addItem(GlossaryItem glossaryItem) {
        items.add(glossaryItem);
    }

    public List<GlossaryItem> items() {
        return items.stream().sorted((a, b) -> a.name().compareToIgnoreCase(b.name())).toList();
    }
}
