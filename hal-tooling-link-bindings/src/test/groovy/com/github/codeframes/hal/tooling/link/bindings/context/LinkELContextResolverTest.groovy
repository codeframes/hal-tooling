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
package com.github.codeframes.hal.tooling.link.bindings.context

import spock.lang.Specification

import javax.el.ELContext
import javax.el.PropertyNotWritableException

class LinkELContextResolverTest extends Specification {

    ELContext mockELContext

    def setup() {
        mockELContext = Mock(ELContext)
    }

    def "test getValue with no matching objects"() {
        given:
          def base = new Object()
          def property = new Object()
          def resolver = new LinkELContextResolver([:])
        when:
          def value = resolver.getValue(mockELContext, base, property)
        then:
          value == null
    }

    def "test getValue with null base object"() {
        given:
          def property = 'name'
          def resolver = new LinkELContextResolver([name: 'bill'])
        when:
          def value = resolver.getValue(mockELContext, null, property)
        then:
          value == 'bill'
    }

    def "test getType with no matching objects"() {
        given:
          def base = new Object()
          def property = new Object()
          def resolver = new LinkELContextResolver([:])
        when:
          def type = resolver.getType(mockELContext, base, property)
        then:
          type == null
    }

    def "test getType with no matching objects and null base object"() {
        given:
          def property = new Object()
          def resolver = new LinkELContextResolver([:])
        when:
          def type = resolver.getType(mockELContext, null, property)
        then:
          type == null
    }

    def "test getType with null base object"() {
        given:
          def property = 'name'
          def resolver = new LinkELContextResolver([name: 'bill'])
        when:
          def type = resolver.getType(mockELContext, null, property)
        then:
          type == String
    }

    def "test setValue"() {
        given:
          def resolver = new LinkELContextResolver([:])
        when:
          resolver.setValue(mockELContext, null/*N/A*/, 'property', null/*N/A*/)
        then:
          thrown(PropertyNotWritableException)
    }

    def "test isReadOnly"() {
        given:
          def resolver = new LinkELContextResolver([:])
        expect:
          resolver.isReadOnly(mockELContext, base, 'property')
        where:
          base << [new Object(), null]
    }

    def "test getFeatureDescriptors"() {
        given:
          def resolver = new LinkELContextResolver([:])
        expect:
          resolver.getFeatureDescriptors(/*N/A*/ null, /*N/A*/ null) == null
    }

    def "test getCommonPropertyType"() {
        given:
          def resolver = new LinkELContextResolver([:])
        expect:
          resolver.getCommonPropertyType(/*N/A*/ null, /*N/A*/ null) == Object
    }
}
