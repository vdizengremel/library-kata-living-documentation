package com.example.bdd.feature;

public class CucumberUtils {
    public static PresenterException catchPresenterException(Runnable fn) {
        try {
            fn.run();
            return null;
        } catch (PresenterException presenterException) {
            return presenterException;
        }
    }
}
