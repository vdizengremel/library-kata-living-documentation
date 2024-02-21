package com.example.demo;

import org.testcontainers.containers.MongoDBContainer;

public class ProjectMongoContainer extends MongoDBContainer {
    private static final String IMAGE_VERSION = "mongo:latest";
    private static ProjectMongoContainer container;

    private ProjectMongoContainer() {
        super(IMAGE_VERSION);
    }

    public static ProjectMongoContainer getInstance() {
        if (container == null) {
            container = new ProjectMongoContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("MONGODB_URI", container.getConnectionString());
//        System.setProperty("DB_USERNAME", container.pas());
//        System.setProperty("DB_PASSWORD", container.getPassword());
    }
}
