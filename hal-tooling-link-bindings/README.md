# hal-tooling-link-bindings

A Module for injecting Link dependencies into `com.github.codeframes.hal.tooling.core.HalRepresentable` types
([hal-tooling-core](https://github.com/codeframes/hal-tooling/tree/master/hal-tooling-core)).

## Prerequisites

 * \>= Java 7
 * \>= Maven 3.0.5 (to build from source)

## Maven

```xml
<properties>
  ...
  <!-- Use the latest version whenever possible. -->
  <hal-tooling.version>1.1.1</hal-tooling.version>
</properties>

<dependencies>
  ...
  <dependency>
    <groupId>com.github.codeframes</groupId>
    <artifactId>hal-tooling-link-bindings</artifactId>
    <version>${hal-tooling.version}</version>
  </dependency>
  ...
</dependencies>
```

## How To Use

Link injection is provided by a `com.github.codeframes.hal.tooling.link.bindings.inject.LinkInjector`. To obtain the 
default instance simply use:

```java
LinkInjector linkInjector = LinkInjector.defaultInstance();
```
Then call the *injectLinks* method passing the `com.github.codeframes.hal.tooling.core.HalRepresentable` instance to 
have the links injected and a `com.github.codeframes.hal.tooling.link.bindings.api.LinkContextResolver` to use for 
resolving link templates (URI's ([RFC3986](https://tools.ietf.org/html/rfc3986)) or URI Template's 
([RFC6570](https://tools.ietf.org/html/rfc6570)))

```java
HalRepresentable entity = ...

linkInjector.injectLinks(entity, new LiteralLinkContextResolver());
```

### Declaring Link's for injection

Link injection is declared with the use of the following annotations:

* `com.github.codeframes.hal.tooling.link.bindings.LinkRel`
* `com.github.codeframes.hal.tooling.link.bindings.LinkRels`
* `com.github.codeframes.hal.tooling.link.bindings.CurieDef`
* `com.github.codeframes.hal.tooling.link.bindings.CurieDefs`

The annotations may only be placed on fields and must also match up with the type they are associated with, as outlined
below:

| Annotation   | Bound Type                                           |
| ------------ | ---------------------------------------------------- |
| `@LinkRel`   | `com.github.codeframes.hal.tooling.core.Link`        |
| `@LinkRels`  | `List<com.github.codeframes.hal.tooling.core.Link>`  |
| `@CurieDef`  | `com.github.codeframes.hal.tooling.core.Curie`       |
| `@CurieDefs` | `List<com.github.codeframes.hal.tooling.core.Curie>` |

### Example

```java
public class Representation implements HalRepresentable {

    @CurieDef(name = "ex", value = "http://example.com/rels/{rel}")
    private Curie curie;

    @CurieDefs({
        @CurieDef(name = "doc", value = "http://example.com/docs/rels/{rel}")
    })
    private List<Curie> curies;

    @LinkRel("/root/")
    private Link selfLink;

    @LinkRels({
        @LinkRel(rel = "ex:data", value = "/root/data/part1"),
        @LinkRel(rel = "ex:data", value = "/root/data/part2")
    })
    private List<Link> dataLinks;
}
```

## Features

### Resource Method Binding

Perhaps the primary reason for using link bindings. This option is available on the @LinkRel annotation, where instead
of providing the link href property via the *value* attribute, the *resource* and *method* attributes are used to
automatically extract the value from the RESTful API in use. Currently the only supported RESTful API is **JAX-RS 2.0**
which requires an additional dependency, please see
[hal-tooling-link-bindings-jax-rs](https://github.com/codeframes/hal-tooling/tree/master/hal-tooling-link-bindings-jax-rs) for more details.

### Link Styling

Link styling can be controlled via the *style* attribute on both the @LinkRel and @CurieDef annotations. There are 3
options available as outlined by the enum `com.github.codeframes.hal.tooling.link.bindings.Style`, internally the value 
selects which method is called on the provided `com.github.codeframes.hal.tooling.link.bindings.api.LinkContextResolver`, 
as illustrated in the table below:

| Style           | LinkContextResolver Method Used                |
| --------------- | ---------------------------------------------- |
| `ABSOLUTE`      | `String resolveAbsolute(String template);`     |
| `ABSOLUTE_PATH` | `String resolveAbsolutePath(String template);` |
| `RELATIVE_PATH` | `String resolveRelativePath(String template);` |

### Expression Language Evaluation

EL expressions may be used on two of the @LinkRel annotation attributes.

#### value

Allows any number of EL expressions to be embedded but the entire *LinkRel.**value*** attribute **MUST** be resolvable
as a `String`.

```java
public class Representation implements HalRepresentable {

    @LinkRel(rel = "el-expression-one", value = "/api/${instance.id}")
    private Link linkOne;
    // results in: new Link("el-expression-one", "/api/123")

    @LinkRel(rel = "el-expression-two", value = "/api/${instance.id == '123' ? '456' : ''}")
    private Link linkTwo;
    // results in: new Link("el-expression-two", "/api/456")

    private String id = "123";
}
```

#### bindings

Specifies a list of variable bindings to use for URI Template expansion. Where binding names are to match the URI
Template tokens to be expanded and the values the substitutions to apply, declared as EL expressions. The entire
*Binding.**value*** attribute **MUST** be resolvable as an `Object`, of which the `toString()` method is used to provide
the actual substitution value.

```java
public class Representation implements HalRepresentable {

    @LinkRel(rel = "el-expression-one", value = "/api/{id}", bindings = {@Binding(name = "id", value = "${instance.id}")})
    private Link linkOne;
    // results in: new Link("el-expression-one", "/api/123")

    @LinkRel(rel = "el-expression-two", value = "/api{/id}", bindings = {@Binding(name = "id", value = "${instance.id == '123' ? '456' : ''}")})
    private Link linkTwo;
    // results in: new Link("el-expression-one", "/api/456")

    private String id = "123";
}
```

#### condition

An EL expression to evaluate as a boolean, used to determine whether or not a `Link` is to be injected.

```java
public class Representation implements HalRepresentable {

    @LinkRel(rel = "conditional-true", value = "/api/123", condition = "${instance.id == '123'}")
    private Link linkOne;
    // results in: new Link("conditional-true", "/api/123")

    @LinkRel(rel = "conditional-false", value = "/api/456", condition = "${instance.id == '123'}")
    private Link linkTwo;
    // results in: null

    private String id = "123";
}
```

## License

[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
