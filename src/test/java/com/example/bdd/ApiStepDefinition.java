package com.example.bdd;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.demo.entrypoints.HttpEntityFactory.httpEntityFomJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiStepDefinition{
    private final MockMvc mockMvc;

    public ApiStepDefinition(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @When("posting to {}")
    public void postingTo(String url) throws Exception {
        var request = httpEntityFomJson("""
                    {
                        "isbn": "2070541274",
                        "title": "Harry Potter",
                        "author": "J. K. Rowling"
                    }
                    """);

        System.out.println("/////////////////////////////////////////////// when posting");
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/book/")
                        .header("content-type", "application/json")
                .content("""
                    {
                        "isbn": "2070541274",
                        "title": "Harry Potter",
                        "author": "J. K. Rowling"
                    }
                    """));
        result.andDo(result1 -> {
            var body = result1.getResponse().getContentAsString();
            assertThat(body).isEqualTo("");
        });
        result.andExpect( status().isOk());
//        var response = restTemplate.postForEntity("/book/", request, String.class);
    }


    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {

    }
}
