package com.example.demo.entrypoints;

import com.example.demo.core.domain.MemberRepository;
import com.example.demo.infrastructure.MemberInMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestConfig {
    @Bean
    @Primary
    MemberRepository createInMemory() {
        return new MemberInMemoryRepository();
    }
}
