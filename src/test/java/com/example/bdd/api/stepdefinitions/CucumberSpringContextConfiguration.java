package com.example.bdd.api.stepdefinitions;

import com.example.demo.core.usecases.GetBookByIsbnUseCase;
import com.example.demo.core.usecases.RegisterABookUseCase;
import com.example.demo.core.usecases.RegisterMemberUseCase;
import com.example.demo.core.usecases.UpdateMemberPersonalDataUseCase;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = CucumberSpringContextConfiguration.TestConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties =
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration")
@ActiveProfiles({"inMemoryRepository"})
@AutoConfigureMockMvc
@CucumberContextConfiguration
@ComponentScan(basePackages = "com.example.demo.entrypoints")
@MockBeans({
        @MockBean(RegisterABookUseCase.class),
        @MockBean(RegisterMemberUseCase.class),
        @MockBean(UpdateMemberPersonalDataUseCase.class),
        @MockBean(GetBookByIsbnUseCase.class)
})
public class CucumberSpringContextConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(CucumberSpringContextConfiguration.class);

    /**
     * Need this method so the cucumber will recognize this class as glue and load spring context configuration
     */
    @Before
    public void setUp() {
        LOG.info("-------------- Spring Context Initialized For Executing Cucumber Tests --------------");
    }

    @SpringBootApplication
    static class TestConfig {
    }
}
