package com.example.test;

import com.example.stepdefinitions.MemberStepDefinitions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.HashMap;
import java.util.Map;

public class MemberPersonas {
    private static final Config members
            = ConfigFactory.load("testdata/members");

    private final Map<String, String> memberIdsByName;

    public MemberPersonas() {
        memberIdsByName = new HashMap<>();
    }

    public MemberStepDefinitions.AddMemberUseCaseCommandForTest findByName(String name) {
        Config config = members.getConfig(name);
        return MemberStepDefinitions.AddMemberUseCaseCommandForTest.builder()
                .firstName(config.getString("firstName"))
                .lastName(config.getString("lastName"))
                .email(config.getString("email"))
                .build();

    }

    public void keepIdFor(String name, String memberId) {
        memberIdsByName.put(name, memberId);
    }

    public String getIdFor(String name) {
        return memberIdsByName.get(name);
    }
}
