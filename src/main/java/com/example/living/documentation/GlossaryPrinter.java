package com.example.living.documentation;

public class GlossaryPrinter {
    protected static final String NEW_LINE = "\\n";

    private final StringBuilder stringBuilder;

    public GlossaryPrinter(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    public void printGlossary(Glossary glossary) {
        glossary.items().forEach(this::printItem);
    }

    private void printItem(GlossaryItem glossaryItem) {
        stringBuilder.append(glossaryItem.name());
        stringBuilder.append(":: ");
        stringBuilder.append(glossaryItem.definition());
        System.out.println(glossaryItem.name() + " " + glossaryItem.definition());

        printNewLine();

        if (!glossaryItem.informationDefinitionsByName().isEmpty()) {
            printNewLine();
            stringBuilder.append("Information:");
            printNewLine();
            glossaryItem.informationDefinitionsByName().forEach(this::printEntry);
        }


        if (!glossaryItem.operationDefinitionsByName().isEmpty()) {
            printNewLine();
            stringBuilder.append("Operations:");
            printNewLine();
            glossaryItem.operationDefinitionsByName().forEach(this::printEntry);
        }

        printNewLine();
    }

    private void printEntry(String key, String value) {
        stringBuilder.append("- ");
        stringBuilder.append(key);

        if (!value.isBlank()) {
            stringBuilder.append(": ");
            stringBuilder.append(value);
        }

        printNewLine();
    }

    private void printNewLine() {
        stringBuilder.append(NEW_LINE);
    }
}
