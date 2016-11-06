# hal-tooling-test

A collection of [Groovy](http://www.groovy-lang.org/) utilities and [Spock Framework](http://docs.spockframework.org/) 
extensions used for testing RESTful API's.

## Prerequisites

 * \>= Java 7
 * \>= Maven 3.0.5 (to build from source)

## Maven

```xml
<properties>
  ...
  <!-- Use the latest version whenever possible. -->
  <hal-tooling.version>1.0.0</hal-tooling.version>
  ...
</properties>

<dependencies>
  ...
  <dependency>
    <groupId>com.github.codeframes</groupId>
    <artifactId>hal-tooling-test</artifactId>
    <version>${hal-tooling.version}</version>
    <scope>test</scope>
  </dependency>
  ...
</dependencies>
```

## How To Use

### HttpClient

To obtain an instance of the `com.github.codeframes.hal.tooling.test.http.HttpClient` within a Spock Specification, 
simply declare an instance variable of it and annotate it with 
`com.github.codeframes.hal.tooling.test.spock.extensions.http.InjectHttpClient`.

```groovy
class Test extends Specification {

    @InjectHttpClient
    HttpClient client
}
```

This will inject an `HttpClient` instance with the default URI set to **http://localhost:8080/**. When a HTTP call is 
made and the given *path* is not absolute, this value is used to resolve the request to an absolute URI.

The default URI can be changed with one of the following (in priority order):

 1. Don't use @InjectHttpClient, programmatically construct an HttpClient instance and specify the defaultURI as a 
    constructor argument:
    ```groovy
    HttpClient client = new HttpClient('http://localhost:8081/')
    ```
 
 2. Set the System property *httpClient.defaultURI*
    ```sh
    java -DhttpClient.defaultURI="http://localhost:8081/" ...
    ```
        
 3. Add a **hal-tooling-test.properties** file to the classpath and set the property *httpClient.defaultURI*
    ```properties
    httpClient.defaultURI=http://localhost:8081/
    ```

### HttpClientResponseMatchers

A collection of Hamcrest Matcher's for asserting HttpClient responses. Emphasis has been given to readability and with
the use of static imports and Spockframework's `spock.util.matcher.HamcrestSupport` it should provide a concise means by
which to test API endpoints with. The `com.github.codeframes.hal.tooling.test.http.HttpClientSupport` Trait also includes 
these methods.

#### Example

```groovy
when:
  def response = client.get(path: '/')
then:
  expect response, has_status(200)
  expect response, has_no_cache_control()
  expect response, has_content_type('application/hal+json;charset=UTF-8')
  expect response, has_body('''
    {
       "_links": {
          "self": {
             "href": "/"
          }
       }
    }
  ''' as JSON)
```

#### Methods

All methods are provided with both a camel case and snake case named versions. Apart from the name they are identical.
So why? And why snake case? Within the context of a Spock Specification using snake case helps the tests read more like
English, keeping them closer to acceptance criteria. However this is opinionated, which is why both are provided.

Most of the methods are self explanatory but here are the few which require particular attention:

##### has_body(JSON body)

As the method signature suggests, this is used to assert if the response body contains JSON, matching that to which is 
given. However it should be noted that this match is **not strict**. All json fields declared in *body* must exist and
be equal to those found in the response object. However any additional fields found in the response object that have not 
been declared in the given body are ignored and play not part in the assertion. The reason for this is that most of the
time having additional fields in the response object would not be considered as a 'breaking change' for clients of an 
API. 

To help with response body assertions the JSON object can be constructed using a GString allowing the expected JSON to
contain templated values. For example the following would assert that a response body contain a date field equal to the
current date in the format yyyy-MM-dd:

```groovy
def today = new Date().format('yyyy-MM-dd')

has_body("""
  {
    "date": "$today"
  }
""" as JSON)
```

**Note:** Custom type coercion is provided for `com.github.codeframes.hal.tooling.test.json.JSON` from `java.lang.String` 
and `groovy.lang.GString`.

To assert more dynamic response bodies embedded Hamcrest Matcher's can be used with the JSON GString variant. Given the
following Matcher declaration, note the variable *is_uuid* is a `org.hamcrest.Matcher` object:

```groovy
static is_uuid = [
        matches         : { actual ->
            try {
                UUID.fromString(actual)
                return true
            } catch (IllegalArgumentException ignore) {
                return false
            }
        },
        describeTo      : { description -> description.appendText("value to be a UUID") },
        describeMismatch: { item, description -> description.appendText("was ").appendValue(item) }
] as BaseMatcher
```

Could be used to assert the following logref value is indeed a UUID:

```groovy
has_body("""
  {
    "logref": $is_uuid,
    "message": "items may not be empty"
  }
""" as JSON)
```

Taking this a little further and we can embed a Matcher to assert a JSON list contains Object's in no particular order:

```groovy
has_body("""
  {
    "items": ${ul('''[
      {
        "name": "cpu",
        "cost": 959.95
      },
      {
        "name": "gpu",
        "cost": 659.99
      }
    ]''')}
  }
""" as JSON)
```

The *ul* (unordered list) Matcher is available in `com.github.codeframes.hal.tooling.test.json.JsonObjectMatchers`

## License

[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
