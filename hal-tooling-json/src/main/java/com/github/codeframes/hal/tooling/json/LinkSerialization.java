/*
 * Copyright © 2016 Richard Burrow (https://github.com/codeframes)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.codeframes.hal.tooling.json;

import com.github.codeframes.hal.tooling.json.ser.config.HalSerializationConfig;

import java.lang.annotation.*;

/**
 * Used to override the default serialization method for link properties. This annotation may be used on either type
 * and or method declarations, the serialization method used will always be the closest defined to a link property.
 * For example the following would serialize the self link property implicitly and the data link property explicitly:
 * <pre>{@code
 * &#064;LinkSerialization(LinkSerializationMethod.IMPLICIT)
 * public class Bean implements HalRepresentable {
 *
 *      public Link getSelf() {
 *          return new Link("self", "/root/");
 *      }
 *
 *      &#064;LinkSerialization(LinkSerializationMethod.EXPLICIT)
 *      public List<Link> getData() {
 *          return Collections.singletonList(new Link("data", "/root/data/part1"));
 *      }
 * }
 * }</pre>
 * <p/>
 * The IMPLICIT option allows link properties to be defined in a less restrictive way. Link properties do not have to
 * be defined in a manor they are to be serialized. For example all links could be defined by a single list of links:
 * <pre>{@code
 * &#064;LinkSerialization(LinkSerializationMethod.IMPLICIT)
 * public class Bean implements HalRepresentable {
 *      public List<Link> getLinks() {
 *          return Arrays.asList(
 *              new Link("self", "/root/"),
 *              new Link("data", "/root/data/part1"),
 *              new Link("data", "/root/data/part2"));
 *      }
 * }
 * }</pre>
 * Would produce application/hal+json:
 * <pre>
 * {
 *      "_links": {
 *          "self": {
 *              "href": "/api/root/"
 *          },
 *          "data": [
 *              {
 *                  "href": "/api/root/data/part1"
 *              },
 *              {
 *                  "href": "/api/root/data/part2"
 *              }
 *          ]
 *      }
 * }
 * </pre>
 * However a problem arises if it is desired to have a relation, of whose value is a list of link objects, maintain
 * a list of link objects in the serialized form, even when only one link object is present. The IMPLICIT method cannot
 * provide this behaviour, but the EXPLICIT can.
 * <p/>
 * The EXPLICIT option specifies that link properties are to be serialized as they are defined. For example:
 * <pre>{@code
 * &#064;LinkSerialization(LinkSerializationMethod.EXPLICIT)
 * public class Bean implements HalRepresentable {
 *
 *      public Link getSelf() {
 *          return new Link("self", "/root/");
 *      }
 *
 *      public List<Link> getData() {
 *          return Collections.singletonList(new Link("data", "/root/data/part1"));
 *      }
 * }
 * }</pre>
 * Would produce application/hal+json:
 * <pre>
 * {
 *      "_links": {
 *          "self": {
 *              "href": "/api/root/"
 *          },
 *          "data": [
 *              {
 *                  "href": "/api/root/data/part1"
 *              }
 *          ]
 *      }
 * }
 * </pre>
 *
 * @see HalSerializationConfig HalSerializationConfig
 */
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LinkSerialization {

    enum LinkSerializationMethod {
        IMPLICIT, EXPLICIT
    }

    /**
     * The method to use when serializing links.
     */
    LinkSerializationMethod value();
}
