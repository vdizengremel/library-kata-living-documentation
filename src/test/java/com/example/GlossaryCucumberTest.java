package com.example;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("glossary")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.example.stepdefinitions"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "json:target/glossary.json")
})
public class GlossaryCucumberTest {

}
