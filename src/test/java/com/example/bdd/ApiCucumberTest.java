package com.example.bdd;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.*;
import org.springframework.boot.test.context.TestConfiguration;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("api-test")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.example.bdd"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "json:target/cucumber.json"),
        @ConfigurationParameter(key = Constants.OBJECT_FACTORY_PROPERTY_NAME, value = "io.cucumber.spring.SpringFactory")

})
public class ApiCucumberTest {
}
