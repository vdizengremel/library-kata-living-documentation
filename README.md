# Library kata living documentation

This is an example project to test software design and living documentation.

The project has been tested under following versions :
- java 14.0.1
- maven 3.9.6

## Repository structure

Two main packages :
- `com.example.demo`: contains application code structured with `clean architecture`
- `com.example.living.documentation`: contains code that generates glossary and specific annotations

## Testing strategy

Use cases are tested with cucumber. In memory repositories are injected to avoid depending on technical details (such as database).
In memory repositories allow to avoid using mocks and ensure having correct behavior.

To ensure in memory repositories have same behavior as real implementation, same tests are run on both implementation.

## Living documentation

It is generated in `target/docs` during `mvn install`. It uses : 
- feature files used with cucumber
- code that is parsed using custom doclet to generate glossary

`cukedoctor` is used to generate documentation.
