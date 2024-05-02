zelador
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.zelador/com.io7m.zelador.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.zelador%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.zelador/com.io7m.zelador?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/zelador/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/zelador.svg?style=flat-square)](https://codecov.io/gh/io7m-com/zelador)

![com.io7m.zelador](./src/site/resources/zelador.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/zelador/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/zelador/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/zelador/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/zelador/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/zelador/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/zelador/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/zelador/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/zelador/actions?query=workflow%3Amain.windows.temurin.lts)|

## zelador

A minimalist JUnit 5 extension to register `AutoCloseable` resources that
will be destroyed after tests finish executing.

### Features

  * Written in pure Java 17.
  * [OSGi](https://www.osgi.org/) ready.
  * [JPMS](https://en.wikipedia.org/wiki/Java_Platform_Module_System) ready.
  * ISC license.
  * High-coverage automated test suite.

### Motivation

For whatever reason, [JUnit 5](https://junit.org/junit5/) does not include
any utility to register per-test resources and automatically have them
cleaned up.

The `zelador` package provides a tiny [JUnit 5](https://junit.org/junit5/)
extension that allows for registering `AutoCloseable` objects that must be
closed after tests finish executing.

### Building

```
$ mvn clean verify
```

### Usage

Annotate your test suite with `@ExtendWith(ZeladorExtension.class)`. This
will allow tests to get access to an injected `CloseableResourcesType`
instance that can be used to register resources to be closed.

```
@ExtendWith(ZeladorExtension.class)
public final class ExampleTest
{
  @Test
  public void test0(
    final CloseableResourcesType resources)
  {
    var output = resources.addPerTestResource(Files.newOutputStream(...));
    ...
  }

  @Test
  public void test1(
    final CloseableResourcesType resources)
  {
    var output = resources.addPerTestClassResource(Files.newOutputStream(...));
    ...
  }
}
```

Resources added with `addPerTestResource` will be closed after the individual
test has finished (as if it had been closed in an `@AfterEach` method).

Resources added with `addPerTestClassResource` will be closed after all tests 
in the class have finished (as if it had been closed in an `@AfterAll` method).

`AutoCloseable` is a functional interface and, as such, it's trivial to pass
objects to the `CloseableResourcesType` class that do not implement
`AutoCloseable` by simply using a method reference:

```
class NotReallyCloseable {
  public void finish() { ... }
}

NotReallyCloseable x;

resources.addPerTestResource(x::finish);
```


