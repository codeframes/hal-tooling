# hal-tooling-link-bindings-jax-rs

An extension to the [hal-tooling-link-bindings](https://github.com/codeframes/hal-tooling/tree/master/hal-tooling-link-bindings) module that
adds support for **JAX-RS 2.0** Resource Method Binding.

## Prerequisites

 * \>= Java 7
 * \>= Maven 3.0.5 (to build from source)

## Maven

```xml
<properties>
  ...
  <!-- Use the latest version whenever possible. -->
  <hal-tooling.version>1.1.0</hal-tooling.version>
  ...
</properties>

<dependencies>
  ...
  <dependency>
    <groupId>com.github.codeframes</groupId>
    <artifactId>hal-tooling-link-bindings-jax-rs</artifactId>
    <version>${hal-tooling.version}</version>
  </dependency>
  ...
</dependencies>
```

## How To Use

Create a `com.github.codeframes.hal.tooling.link.bindings.inject.LinkInjector` using an instance of
`com.github.codeframes.hal.tooling.link.bindings.jaxrs.JaxRsLinkTemplateFactory`:

```java
LinkInjector linkInjector = LinkInjector.instanceBuilder()
        .linkTemplateFactory(new JaxRsLinkTemplateFactory())
        .build();
```

Optionally use `com.github.codeframes.hal.tooling.link.bindings.jaxrs.JaxRsLinkContextResolver` with an associated
`javax.ws.rs.core.UriInfo` for link resolving:

```java
HalRepresentable entity = ...
UriInfo uriInfo = ...

linkInjector.injectLinks(entity, new JaxRsLinkContextResolver(uriInfo));
```

For convenience the `com.github.codeframes.hal.tooling.link.bindings.jaxrs.providers.LinkInjectorInterceptor` extension 
already provides this functionality and is discoverable by the JAX-RS runtime.

## Example

Given the following resource:

```java
@Path("/api/items/")
public interface Resource {

    @GET
    Response get();

    @GET
    @Path("/get/{id}")
    Response getItem();

    @POST
    Response addItem();

    @PUT
    @Path("/put/{id}")
    Response setItem();

    @DELETE
    @Path("/delete/{id}")
    Response deleteItem();
}
```

A representation with resource method link binding:

```java
public class Representation implements HalRepresentable {

    @LinkRel(rel = "items", resource = Resource.class, method = "get")
    Link itemsLink;
    // results in: new Link("items", "/api/items/")

    @LinkRel(rel = "item", resource = Resource.class, method = "getItem")
    Link itemLink;
    // results in: new Link("item", "/api/items/get/{id}")

    @LinkRel(rel = "create-item", resource = Resource.class, method = "addItem")
    Link createItemLink;
    // results in: new Link("create-item", "/api/items/")

    @LinkRel(rel = "update-item", resource = Resource.class, method = "setItem")
    Link updateItemLink;
    // results in: new Link("update-item", "/api/items/put/{id}")

    @LinkRel(rel = "remove-item", resource = Resource.class, method = "deleteItem")
    Link removeItemLink;
    // results in: new Link("remove-item", "/api/items/delete/{id}")
}
```

## License

[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
