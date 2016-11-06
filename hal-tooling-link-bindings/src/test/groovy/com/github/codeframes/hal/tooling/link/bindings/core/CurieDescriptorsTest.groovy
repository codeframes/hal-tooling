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
package com.github.codeframes.hal.tooling.link.bindings.core

import com.github.codeframes.hal.tooling.link.bindings.Style
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class CurieDescriptorsTest extends Specification {

    def "test get"() {
        when:
          def curieDescriptors = new CurieDescriptors(descriptors)
        then:
          curieDescriptors.get(name) == descriptor
        where:
          descriptors                              | name        | descriptor
          []                                       | 'not_exist' | null
          [curieDescriptor('name', '/docs/{rel}')] | 'not_exist' | null
          [curieDescriptor('name', '/docs/{rel}')] | 'name'      | curieDescriptor('name', '/docs/{rel}')
    }

    def "test size and isEmpty"() {
        when:
          def curieDescriptors = new CurieDescriptors(descriptors)
        then:
          curieDescriptors.size() == size
        and:
          curieDescriptors.isEmpty() == isEmpty
        where:
          descriptors                                                                        | size | isEmpty
          []                                                                                 | 0    | true
          [curieDescriptor('name', '/docs/{rel}')]                                           | 1    | false
          [curieDescriptor('ns1', '/docs/1/{rel}'), curieDescriptor('ns2', '/docs/2/{rel}')] | 2    | false
    }

    def "test equals"() {
        when:
          EqualsVerifier.forClass(CurieDescriptors).usingGetClass().verify()
        then:
          noExceptionThrown()
    }

    static CurieDescriptor curieDescriptor(String name, String value) {
        return new CurieDescriptor(name, new HrefTemplate(value, Style.RELATIVE_PATH))
    }
}
