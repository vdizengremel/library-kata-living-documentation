package com.example.living.documentation.annotation;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an implementation of a domain interface (like a repository).
 *
 * @see <a href="https://alistair.cockburn.us/hexagonal-architecture/">Defination of hexagonal architecture also known as ports and adapters</a>
 * @see <a href="https://refactoring.guru/design-patterns/adapter">Adapter pattern</a>
 */
@Component
@Profile("!inMemoryRepository")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Adapter {
}
