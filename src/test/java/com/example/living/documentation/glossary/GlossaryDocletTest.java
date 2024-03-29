package com.example.living.documentation.glossary;

import com.example.living.documentation.glossary.Glossary;
import com.example.living.documentation.glossary.GlossaryDoclet;
import com.example.living.documentation.glossary.GlossaryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlossaryDocletTest {
    @BeforeEach
    void setUp() {
        DocletForTest.glossary = null;
    }

    @Test
    void shouldRunCustomDoclet() {
        DocumentationTool systemDocumentationTool = ToolProvider.getSystemDocumentationTool();
        String[] args = new String[]{
                "-sourcepath",
                "./src/test/java",
                "-subpackages",
                "com.example.demo",
        };

        DocumentationTool.DocumentationTask task = systemDocumentationTool.getTask(null, null, null,
                DocletForTest.class, Arrays.asList(args), null);

        var result = task.call();
        assertThat(result).isTrue();

        GlossaryItem classItem = new GlossaryItem("ImportantDomainConcept", "This is a concept to add to glossary.", Map.of("id", "", "status", "Status of the concept."), Map.of("publicOperationWithArgumentAndReturn", "Operation of the concept."));
        GlossaryItem enumItem = new GlossaryItem("EnumConcept", "Enum for glossary.", Map.of("CREATE", "", "UPDATE", ""), Collections.emptyMap());
        GlossaryItem itemWithLink = new GlossaryItem("ImportantConceptWithLink", "Concept with a <a href=\\\"https://wiki.com\\\">link</a>.", Collections.emptyMap(), Collections.emptyMap());

        assertThat(DocletForTest.glossary.items()).containsExactlyInAnyOrder(classItem, enumItem, itemWithLink);
    }

    public static class DocletForTest extends GlossaryDoclet {
        public static Glossary glossary;

        @Override
        protected String getPackageName() {
            return "com.example.living.documentation.glossary.glossarytestclasses";
        }

        @Override
        protected boolean print(Glossary glossaryToPrint) {
            glossary = glossaryToPrint;
            return true;
        }
    }
}
