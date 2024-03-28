package com.example;

import org.junit.jupiter.api.Test;

import javax.tools.DocumentationTool;
import javax.tools.ToolProvider;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GlossaryDocletTest {
    private static final String BUILD_PROPERTY_FILE_LOCATION = "src/main/java";

    @Test
    void shouldRunDoclet() {
        DocumentationTool systemDocumentationTool = ToolProvider.getSystemDocumentationTool();
        String[] args = new String[]{
                "-sourcepath",
                "./src/main/java",
                "-subpackages",
                "com.example.demo",
//                "com.example.annotation",
                "-d",
                "com.example",
                "-author",
                "com.example",
                "-doctitle",
                "whatever not used just to show compatibility",
                "-windowtitle",
                "whatever not used just to show compatibility",
//                "-classdir",
//                BUILD_PROPERTY_FILE_LOCATION
        };
        DocumentationTool.DocumentationTask task = systemDocumentationTool.getTask(null, null, null,
                GlossaryDoclet.class, Arrays.asList(args), null);

        var result = task.call();
        assertThat(result).isTrue();
    }

//    class DocletForTest extends GlossaryDoclet {
//
//    }
}
