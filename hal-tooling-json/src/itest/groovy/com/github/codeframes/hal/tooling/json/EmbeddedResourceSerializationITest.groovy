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
import com.github.codeframes.hal.tooling.core.Embeddable
import com.github.codeframes.hal.tooling.core.Embedded
import com.github.codeframes.hal.tooling.core.HalRepresentable
import com.github.codeframes.hal.tooling.core.Link
import com.github.codeframes.hal.tooling.json.module.HalRepresentableModule
import com.github.codeframes.hal.tooling.json.util.JsonUtil
import spock.lang.Specification

class EmbeddedResourceSerializationITest extends Specification {

    ObjectMapper mapper

    def setup() {
        mapper = new ObjectMapper();
        mapper.registerModule(new HalRepresentableModule());
    }

    def "test serialisation of bean with embedded resource"() {
        when:
          def json = mapper.writeValueAsString(new BeanWithEmbeddedResource())

        then:
          json == JsonUtil.json_string('''
            {
                "_embedded": {
                    "resource": {
                        "name": "basic"
                    }
                }
            }
            ''')
    }

    static class BeanWithEmbeddedResource implements HalRepresentable {

        Embedded resource = new Embedded("resource", [name: "basic"])
    }

    def "test serialisation of bean with embeddable resource"() {
        when:
          def json = mapper.writeValueAsString(new BeanWithEmbeddableResource())

        then:
          json == JsonUtil.json_string('''
            {
                "_embedded": {
                    "resource": {
                        "name": "basic"
                    }
                }
            }
            ''')
    }

    static class BeanWithEmbeddableResource implements HalRepresentable {

        Embeddable resource = new EmbeddableResource()
    }

    static class EmbeddableResource implements Embeddable {

        @Override
        String getRel() {
            return 'resource'
        }

        String name = 'basic'
    }

    def "test serialisation of bean with embedded hal resource"() {
        when:
          def json = mapper.writeValueAsString(new BeanWithEmbeddedHalResource())

        then:
          json == JsonUtil.json_string('''
            {
                "_embedded": {
                    "resource": {
                        "_links": {
                            "self": {
                                "href": "/api/resource/1/"
                            }
                        },
                        "name": "hal"
                    }
                }
            }
            ''')
    }

    static class BeanWithEmbeddedHalResource implements HalRepresentable {

        Embedded resource = new Embedded("resource", new HalRepresentable() {
            Link self = new Link("self", "/api/resource/1/")

            String name = "hal"
        })
    }

    def "test serialisation of bean with multiple embedded hal resources"() {
        when:
          def json = mapper.writeValueAsString(new BeanWithMultipleEmbeddedHalResources())

        then:
          json == JsonUtil.json_string('''
            {
                "_embedded": {
                    "resourceA": {
                        "_links": {
                            "self": {
                                "href": "/api/resource/A/"
                            }
                        },
                        "name": "hal_A"
                    },
                    "resourceB": [
                        {
                            "_links": {
                                "self": {
                                    "href": "/api/resource/B/1/"
                                },
                                "data": {
                                    "href": "/api/root/data/1/"
                                }
                            },
                            "name": "hal_B1"
                        },
                        {
                            "_links": {
                                "self": {
                                    "href": "/api/resource/B/2/"
                                },
                                "data": [
                                    {
                                        "href": "/api/root/data/1/"
                                    }
                                ]
                            },
                            "name": "hal_B2"
                        }
                    ]
                }
            }
            ''')
    }

    static class BeanWithMultipleEmbeddedHalResources implements HalRepresentable {

        Embedded resourceA = new Embedded("resourceA", new HalRepresentable() {
            Link self = new Link("self", "/api/resource/A/")

            String name = "hal_A"
        })

        Embedded resourceB = new Embedded("resourceB", [
                new ImplicitLinkHalResource(),
                new ExplicitLinkHalResource()
        ])
    }

    @LinkSerialization(LinkSerialization.LinkSerializationMethod.IMPLICIT)
    static class ImplicitLinkHalResource implements HalRepresentable {

        List<Link> links = [new Link("self", "/api/resource/B/1/"),
                            new Link("data", "/api/root/data/1/")]

        String name = "hal_B1"
    }

    @LinkSerialization(LinkSerialization.LinkSerializationMethod.EXPLICIT)
    static class ExplicitLinkHalResource implements HalRepresentable {

        Link self = new Link("self", "/api/resource/B/2/")

        List<Link> data = [new Link("data", "/api/root/data/1/")]

        String name = "hal_B2"
    }
}