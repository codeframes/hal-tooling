# hal-tooling-json

A [Jackson](https://github.com/FasterXML/jackson) Module for serializing `com.github.hal.tooling.core.HalRepresentable`
types ([hal-tooling-core](https://github.com/codeframes/hal-tooling/tree/master/hal-tooling-core)) to the JSON format of the
[HAL](https://tools.ietf.org/html/draft-kelly-json-hal) standard: **application/hal+json**

## Prerequisites

 * \>= Java 7
 * \>= Maven 3.0.5 (to build from source)

## Maven

```xml
<properties>
  ...
  <!-- Use the latest version whenever possible. -->
  <hal-tooling.version>1.1.1</hal-tooling.version>
  ...
</properties>

<dependencies>
  ...
  <dependency>
    <groupId>com.github.codeframes</groupId>
    <artifactId>hal-tooling-json</artifactId>
    <version>${hal-tooling.version}</version>
  </dependency>
  ...
</dependencies>
```

## How To Use

Register the provided module with a `com.fasterxml.jackson.databind.ObjectMapper` instance.
```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new HalRepresentableModule());
```
Use the ObjectMapper to perform object serialisation as usual only now any 
`com.github.codeframes.hal.tooling.core.HalRepresentable` type will be serialized following the 
[HAL](https://tools.ietf.org/html/draft-kelly-json-hal) standard.

## Example

The following POJO:

```java
public class OrdersRepresentation implements HalRepresentable {

    public List<Curie> getCuries() {
        return Collections.singletonList(new Curie("ex", "http://example.com/docs/rels/{rel}"));
    }

    public Link getSelfLink() {
        return new Link("self", "/orders/");
    }

    public Embedded<List<OrderRepresentation>> getOrders() {
        return new Embedded<>("ex:orders", Collections.singletonList(new OrderRepresentation()));
    }

    public static class OrderRepresentation implements HalRepresentable {

        public Link getSelfLink() {
            return new Link("self", "/orders/123");
        }

        public Link getRemoveOrderLink() {
            return new Link("ex:remove-order", "/orders/123");
        }

        public List<String> getItems() {
            return Arrays.asList("apples", "oranges", "pears");
        }

        public BigDecimal getCost() {
            return new BigDecimal("2.74");
        }
    }
}
```

When serialized will produce the following HAL representation:

```json
{
    "_links": {
        "self": {
            "href": "/orders/"
        },
        "curies": [{
            "name": "ex",
            "href": "http://example.com/docs/rels/{rel}",
            "templated": true
        }]
    },
    "_embedded": {
        "ex:orders": [{
            "_links": {
                "self": {
                    "href": "/orders/123"
                },
                "ex:remove-order": {
                    "href": "/orders/123"
                }
            },
            "items": ["apples", "oranges", "pears"],
            "cost": 2.74
        }]
    }
}
```

## Link Serialisation Options

There are two options available to control the serialization of links; Implicit and Explicit. The
option can be set in a global sense via the corresponding methods on
`com.github.codeframes.hal.tooling.json.ser.config.HalSerializationConfig`

```java
HalRepresentableModule module = new HalRepresentableModule(
        HalSerializationConfig.defaultInstance().withExplicitLinkSerialization()
);
```

```java
HalRepresentableModule module = new HalRepresentableModule(
        HalSerializationConfig.defaultInstance().withImplicitLinkSerialization()
);
```

or in a more granular way with the use of `com.github.codeframes.hal.tooling.json.LinkSerialization` annotation.

### Implicit

The Implicit option allows link properties to be defined in a less restrictive way. Link properties do not have to
be defined in a manor they are to be serialized. For example all links could be defined by a single list of links:

```java
@LinkSerialization(LinkSerializationMethod.IMPLICIT)
public class Bean implements HalRepresentable {
    public List<Link> getLinks() {
        return Arrays.asList(
            new Link("self", "/root/"),
            new Link("data", "/root/data/part1"),
            new Link("data", "/root/data/part2"));
    }
}
```
Would produce application/hal+json:
```json
{
    "_links": {
        "self": {
            "href": "/api/root/"
        },
        "data": [
            {
                "href": "/api/root/data/part1"
            },
            {
                "href": "/api/root/data/part2"
            }
        ]
    }
 }
```

### Explicit

The Explicit option specifies that link properties are to be serialized as they are defined. This is useful when a
relation, of whose value is a list of link objects, maintain a list of link objects in the serialised form, even when
only one link object is present. The Implicit method does not provide this behaviour. For example:

```java
@LinkSerialization(LinkSerializationMethod.EXPLICIT)
public class Bean implements HalRepresentable {

    public Link getSelf() {
        return new Link("self", "/root/");
    }

    public List<Link> getData() {
        return Collections.singletonList(new Link("data", "/root/data/part1"));
    }
}
```
Would produce application/hal+json:
```json
{
    "_links": {
        "self": {
            "href": "/api/root/"
        },
        "data": [
            {
                "href": "/api/root/data/part1"
            }
        ]
    }
}
```

## License

[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
