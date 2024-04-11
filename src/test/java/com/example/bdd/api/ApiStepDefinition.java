package com.example.bdd.api;

import com.example.demo.core.usecases.RegisterABookUseCase;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONException;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiStepDefinition {
    private final MockMvc mockMvc;
    private final RegisterABookUseCase registerABookUseCase;

    private String body;
    private MockHttpServletResponse mvcResult;

    public ApiStepDefinition(MockMvc mockMvc, RegisterABookUseCase registerABookUseCase) {
        this.mockMvc = mockMvc;
        this.registerABookUseCase = registerABookUseCase;
    }

    @Given("request body is:")
    public void requestBodyIs(String body) {
        this.body = body;
    }

    @Given("book is not registered yet")
    public void bookIsNotRegistered() {
        Mockito.when(registerABookUseCase.execute(Mockito.any(), Mockito.any())).then(invocationOnMock -> {
            var argument = invocationOnMock.getArgument(1, RegisterABookUseCase.RegisterABookPresenter.class);
            return argument.presentRegistrationSuccess();
        });
    }

    @Given("book with same ISBN already exists")
    public void bookWithSameISBNAlreadyExists() {
        Mockito.when(registerABookUseCase.execute(Mockito.any(), Mockito.any())).then(invocationOnMock -> {
            var argument = invocationOnMock.getArgument(1, RegisterABookUseCase.RegisterABookPresenter.class);
            return argument.presentBookAlreadyRegistered();
        });
    }

    @When("POST to {}")
    public void postTo(String url) throws Exception {
        var result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header("content-type", "application/json")
                .content(body));
        mvcResult = result.andReturn().getResponse();
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        assertThat(mvcResult.getStatus()).isEqualTo(statusCode);
    }

    @And("the response body should be:")
    public void theResponseBodyShouldBe(String expectedBody) throws UnsupportedEncodingException, JSONException {
        String actualBody = mvcResult.getContentAsString();
        System.out.println("body: " + actualBody);
        JSONAssert.assertEquals(expectedBody, actualBody, JSONCompareMode.STRICT);
    }
}
