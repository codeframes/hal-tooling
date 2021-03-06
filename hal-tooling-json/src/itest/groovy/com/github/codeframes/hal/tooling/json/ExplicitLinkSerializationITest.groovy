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
package com.github.codeframes.hal.tooling.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.codeframes.hal.tooling.core.Curie
import com.github.codeframes.hal.tooling.core.HalRepresentable
import com.github.codeframes.hal.tooling.core.Link
import com.github.codeframes.hal.tooling.json.module.HalRepresentableModule
import com.github.codeframes.hal.tooling.json.util.JsonUtil
import spock.lang.Specification

@SuppressWarnings("GrMethodMayBeStatic")
class ExplicitLinkSerializationITest extends Specification {

    ObjectMapper mapper

    def setup() {
        mapper = new ObjectMapper();
        mapper.registerModule(new HalRepresentableModule());
    }

    def "test explicit serialisation of bean with no links and no embedded resources"() {
        when:
          def json = mapper.writeValueAsString(new Bean())

        then:
          json == JsonUtil.json_string('''
            {
                "text": "string"
            }
            ''')
    }

    @LinkSerialization(LinkSerialization.LinkSerializationMethod.EXPLICIT)
    static class Bean implements HalRepresentable {

        String text = "string"
    }

    def "test explicit serialisation of bean with one link property"() {
        when:
          def json = mapper.writeValueAsString(new BeanWithLinkField())

        then:
          json == JsonUtil.json_string('''
            {
                "_links": {
                    "self": {
                        "href": "/api/root/"
                    }
                }
            }
            ''')
    }

    @LinkSerialization(LinkSerialization.LinkSerializationMethod.EXPLICIT)
    static class BeanWithLinkField implements HalRepresentable {

        Link self = new Link("self", "/api/root/")
    }

    def "test explicit serialisation of bean with multiple link properties"() {
        when:
          def json = mapper.writeValueAsString(new BeanWithMultipleLinkFields())

        then:
          json == JsonUtil.json_string('''
            {
                "_links": {
                    "self": {
                        "href": "/api/root/"
                    },
                    "help": {
                        "href": "/api/root/help/"
                    },
                    "info": {
                        "href": "/api/root/info/"
                    }
                }
            }
            ''')
    }

    @LinkSerialization(LinkSerialization.LinkSerializationMethod.EXPLICIT)
    static class BeanWithMultipleLinkFields implements HalRepresentable {

        Link self = new Link("self", "/api/root/")

        Link info = new Link("info", "/api/root/info/")

        Link help = new Link("help", "/api/root/help/")
    }

    def "test explicit serialisation of bean with list of link property with single link"() {
        when:
          def json = mapper.writeValueAsString(new BeanWithListLinkFieldSingleLink())

        then:
          json == JsonUtil.json_string('''
            {
                "_links": {
                    "data": [
                        {
                            "href": "/api/root/data/1/"
                        }
                    ]
                }
            }
            ''')
    }

    @LinkSerialization(LinkSerialization.LinkSerializationMethod.EXPLICIT)
    static class BeanWithListLinkFieldSingleLink implements HalRepresentable {

        List<Link> data = [new Link("data", "/api/root/data/1/")]
    }

    def "test explicit serialisation of bean with list of link property with multiple links"() {
        when:
          def json = mapper.writeValueAsString(new BeanWithListLinkFieldMultipleLinks())

        then:
          json == JsonUtil.json_string('''
            {
                "_links": {
                    "data": [
                        {
                            "href": "/api/root/data/1/"
                        },
                        {
                            "href": "/api/root/data/2/"
                        },
                        {
                            "href": "/api/root/data/3/"
                        }
                    ]
                }
            }
            ''')
    }

    @LinkSerialization(LinkSerialization.LinkSerializationMethod.EXPLICIT)
    static class BeanWithListLinkFieldMultipleLinks implements HalRepresentable {

        List<Link> data = [new Link("data", "/api/root/data/1/"),
                           new Link("data", "/api/root/data/2/"),
                           new Link("data", "/api/root/data/3/")]
    }

    def "test explicit serialisation of bean with list of curie property with single curie"() {
        when:
          def json = mapper.writeValueAsString(new BeanWithListCurieFieldSingleCurie())

        then:
          json == JsonUtil.json_string('''
            {
                "_links": {
                    "curies": [
                        {
                            "name": "docs",
                            "href": "/api/rels/{rel}",
                            "templated": true
                        }
                    ]
                }
            }
            ''')
    }

    @LinkSerialization(LinkSerialization.LinkSerializationMethod.EXPLICIT)
    static class BeanWithListCurieFieldSingleCurie implements HalRepresentable {

        List<Curie> curies = [new Curie("docs", "/api/rels/{rel}")]
    }

    def "test explicit serialisation of bean with list of curie property with multiple curie"() {
        when:
          def json = mapper.writeValueAsString(new BeanWithListCurieFieldMultipleCurie())

        then:
          json == JsonUtil.json_string('''
            {
                "_links": {
                    "curies": [
                        {
                            "name": "itms",
                            "href": "/api/rels/items/{rel}",
                            "templated": true
                        },
                        {
                            "name": "ords",
                            "href": "/api/rels/orders/{rel}",
                            "templated": true
                        }
                    ]
                }
            }
            ''')
    }

    @LinkSerialization(LinkSerialization.LinkSerializationMethod.EXPLICIT)
    static class BeanWithListCurieFieldMultipleCurie implements HalRepresentable {

        List<Curie> curies = [new Curie("ords", "/api/rels/orders/{rel}"),
                              new Curie("itms", "/api/rels/items/{rel}")]
    }

    def "test explicit serialisation of bean with list of link property with single link and implicit serialization override"() {
        when:
          def json = mapper.writeValueAsString(new BeanWithListLinkFieldSingleLinkImplicitOverride())

        then:
          json == JsonUtil.json_string('''
            {
                "_links": {
                    "data": {
                        "href": "/api/root/data/1/"
                    }
                }
            }
            ''')
    }

    @LinkSerialization(LinkSerialization.LinkSerializationMethod.EXPLICIT)
    static class BeanWithListLinkFieldSingleLinkImplicitOverride implements HalRepresentable {

        @LinkSerialization(LinkSerialization.LinkSerializationMethod.IMPLICIT)
        List<Link> getDataLinks() {
            [new Link("data", "/api/root/data/1/")]
        }
    }
}