package com.example.demo.entrypoints;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class HttpEntityFactory {
    @NotNull
    public static HttpEntity<String> httpEntityFomJson(@Language("JSON") String requestJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        return new HttpEntity<>(requestJson, headers);
    }
}
