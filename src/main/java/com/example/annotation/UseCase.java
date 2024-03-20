package com.example.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a use case as defined in <a href="https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html">Clean Architecture</a>.
 */
@Service
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseCase {
}
