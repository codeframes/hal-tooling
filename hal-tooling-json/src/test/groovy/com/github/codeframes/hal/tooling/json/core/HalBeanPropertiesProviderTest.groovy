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
package com.github.codeframes.hal.tooling.json.core

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase
import com.fasterxml.jackson.databind.type.TypeFactory
import com.github.codeframes.hal.tooling.core.Curie
import com.github.codeframes.hal.tooling.core.Embeddable
import com.github.codeframes.hal.tooling.core.Link
import spock.lang.Shared
import spock.lang.Specification

class HalBeanPropertiesProviderTest extends Specification {

    @Shared
    def string_prop = Stub(BeanPropertyWriter) {
        getType() >> TypeFactory.defaultInstance().constructType(String)
        getAnnotation(_) >> null
    }

    @Shared
    def int_prop = Stub(BeanPropertyWriter) {
        getType() >> TypeFactory.defaultInstance().constructType(Integer)
        getAnnotation(_) >> null
    }

    @Shared
    def link_prop = Stub(BeanPropertyWriter) {
        getType() >> TypeFactory.defaultInstance().constructType(Link)
        getAnnotation(_) >> null
    }

    @Shared
    def links_prop = Stub(BeanPropertyWriter) {
        getType() >> TypeFactory.defaultInstance().constructCollectionType(List, Link)
        getAnnotation(_) >> null
    }

    @Shared
    def curie_prop = Stub(BeanPropertyWriter) {
        getType() >> TypeFactory.defaultInstance().constructType(Curie)
        getAnnotation(_) >> null
    }

    @Shared
    def curies_prop = Stub(BeanPropertyWriter) {
        getType() >> TypeFactory.defaultInstance().constructCollectionType(List, Curie)
        getAnnotation(_) >> null
    }

    @Shared
    def embeddable_prop = Stub(BeanPropertyWriter) {
        getType() >> TypeFactory.defaultInstance().constructType(Embeddable)
        getAnnotation(_) >> null
    }

    def "test link properties are correctly sorted"() {
        given:
          def serializer = Stub(BeanSerializerBase, constructorArgs: [
                  Mock(BeanSerializerBase),
                  props as BeanPropertyWriter[],
                  filtered_props as BeanPropertyWriter[]
          ])

        when:
          def halBeanProperties = HalBeanPropertiesProvider.getProperties(serializer)

        then:
          halBeanProperties.properties == expct_props
          halBeanProperties.filteredProperties == expct_filtered_props
          halBeanProperties.linkProperties == expct_link_props
          halBeanProperties.curieProperties == []
          halBeanProperties.embeddableProperties == []

        where:
          props                     | filtered_props           | expct_props   | expct_filtered_props | expct_link_props
          []                        | []                       | []            | []                   | []
          [link_prop]               | []                       | []            | []                   | [link_prop]
          [links_prop]              | []                       | []            | []                   | [links_prop]
          [string_prop, link_prop]  | []                       | [string_prop] | []                   | [link_prop]
          []                        | [link_prop]              | []            | []                   | []
          []                        | [string_prop, link_prop] | []            | [string_prop]        | []
          [string_prop, link_prop]  | [link_prop]              | [string_prop] | []                   | [link_prop]
          [string_prop, link_prop]  | [string_prop, link_prop] | [string_prop] | [string_prop]        | [link_prop]
          [string_prop, links_prop] | [string_prop, link_prop] | [string_prop] | [string_prop]        | [links_prop]
    }

    def "test curies property is correctly sorted"() {
        given:
          def serializer = Stub(BeanSerializerBase, constructorArgs: [
                  Mock(BeanSerializerBase),
                  props as BeanPropertyWriter[],
                  filtered_props as BeanPropertyWriter[]
          ])

        when:
          def halBeanProperties = HalBeanPropertiesProvider.getProperties(serializer)

        then:
          halBeanProperties.properties == expct_props
          halBeanProperties.filteredProperties == expct_filtered_props
          halBeanProperties.linkProperties == []
          halBeanProperties.curieProperties == expct_curie_props
          halBeanProperties.embeddableProperties == []

        where:
          props                   | filtered_props          | expct_props | expct_filtered_props | expct_curie_props
          [curie_prop]            | []                      | []          | []                   | [curie_prop]
          [curies_prop]           | []                      | []          | []                   | [curies_prop]
          [curies_prop, int_prop] | []                      | [int_prop]  | []                   | [curies_prop]
          []                      | [curies_prop]           | []          | []                   | []
          []                      | [curies_prop, int_prop] | []          | [int_prop]           | []
          [curies_prop, int_prop] | [curies_prop]           | [int_prop]  | []                   | [curies_prop]
          [curie_prop, int_prop]  | [curie_prop, int_prop]  | [int_prop]  | [int_prop]           | [curie_prop]
          [curies_prop, int_prop] | [curies_prop, int_prop] | [int_prop]  | [int_prop]           | [curies_prop]
    }

    def "test embeddable properties are correctly sorted"() {
        given:
          def serializer = Stub(BeanSerializerBase, constructorArgs: [
                  Mock(BeanSerializerBase),
                  props as BeanPropertyWriter[],
                  filtered_props as BeanPropertyWriter[]
          ])

        when:
          def halBeanProperties = HalBeanPropertiesProvider.getProperties(serializer)

        then:
          halBeanProperties.properties == expct_props
          halBeanProperties.filteredProperties == expct_filtered_props
          halBeanProperties.linkProperties == []
          halBeanProperties.curieProperties == []
          halBeanProperties.embeddableProperties == expct_embeddable_props

        where:
          props                          | filtered_props                 | expct_props   | expct_filtered_props | expct_embeddable_props
          [embeddable_prop]              | []                             | []            | []                   | [embeddable_prop]
          [string_prop, embeddable_prop] | []                             | [string_prop] | []                   | [embeddable_prop]
          []                             | [embeddable_prop]              | []            | []                   | []
          []                             | [string_prop, embeddable_prop] | []            | [string_prop]        | []
          [string_prop, embeddable_prop] | [embeddable_prop]              | [string_prop] | []                   | [embeddable_prop]
          [string_prop, embeddable_prop] | [string_prop, embeddable_prop] | [string_prop] | [string_prop]        | [embeddable_prop]
    }

    def "test for nullable properties"() {
        given:
          def serializer = Stub(BeanSerializerBase, constructorArgs: [
                  Mock(BeanSerializerBase),
                  props as BeanPropertyWriter[],
                  filtered_props as BeanPropertyWriter[]
          ])

        when:
          def halBeanProperties = HalBeanPropertiesProvider.getProperties(serializer)

        then:
          halBeanProperties.properties == expct_props
          halBeanProperties.filteredProperties == expct_filtered_props
          halBeanProperties.linkProperties == []
          halBeanProperties.curieProperties == []
          halBeanProperties.embeddableProperties == []

        where:
          props               | filtered_props      | expct_props   | expct_filtered_props
          []                  | []                  | []            | []
          null                | null                | []            | []
          [null]              | [null]              | []            | []
          [null, string_prop] | [null, string_prop] | [string_prop] | [string_prop]
    }
}
