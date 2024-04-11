package com.example.bdd.api.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiStepDefinition {
    private final MockMvc mockMvc;

    private String body;
    private MockHttpServletResponse mvcResult;

    public ApiStepDefinition(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Given("request body is:")
    public void requestBodyIs(String body) {
        this.body = body;
    }

    @When("POST to {}")
    public void postTo(String url) throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header("content-type", "application/json")
                .content(body));
        mvcResult = result.andReturn().getResponse();
    }

    @When("GET {}")
    public void getFrom(String url) throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .header("content-type", "application/json"));
        mvcResult = result.andReturn().getResponse();
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        assertThat(mvcResult.getStatus()).isEqualTo(statusCode);
    }

    @And("the response body should be:")
    public void theResponseBodyShouldBe(String expectedBody) throws UnsupportedEncodingException, JSONException {
        String actualBody = mvcResult.getContentAsString();
        JSONAssert.assertEquals(expectedBody, actualBody, JSONCompareMode.STRICT);
    }
}
