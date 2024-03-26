package com.example;

import io.cucumber.java.Before;
import io.cucumber.junit.platform.engine.Constants;
import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.example.stepdefinitions"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "json:target/cucumber.json")
})
public class RunCucumberTest {

}
