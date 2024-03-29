package com.example.living.documentation.glossarytestclasses;

import com.example.annotation.CoreConcept;

/**
 * This is a concept to add to glossary.
 */
@CoreConcept
public class ImportantDomainConcept {
    private String id;

    /**
     * Status of the concept.
     */
    private String status;

    /**
     * Operation of the concept.
     *
     * @param arg1 first arg
     * @param arg2 second arg
     * @return result
     */
    public boolean publicOperationWithArgumentAndReturn(String arg1, String arg2) {
        return privateMethodThatShouldNotBeInGlossary();
    }

    public boolean publicOperationWithoutCommentThatShouldNotAppearInGlossary(String arg1, String arg2) {
        return privateMethodThatShouldNotBeInGlossary();
    }

    private boolean privateMethodThatShouldNotBeInGlossary() {
        return true;
    }
}
