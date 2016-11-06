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
package com.github.codeframes.hal.tooling.link.bindings.inject

import com.github.codeframes.hal.tooling.core.Curie
import com.github.codeframes.hal.tooling.link.bindings.core.CurieDescriptor
import com.github.codeframes.hal.tooling.link.bindings.core.FieldAccessor
import com.github.codeframes.hal.tooling.link.bindings.core.LinkProvider
import spock.lang.Specification

class CurieFieldSetterTest extends Specification {

    FieldAccessor mockFieldAccessor
    LinkProvider mockLinkProvider
    CurieDescriptor mockCurieDescriptor

    CurieFieldSetter curieFieldSetter

    def setup() {
        mockFieldAccessor = Mock(FieldAccessor)
        mockLinkProvider = Mock(LinkProvider)
        mockCurieDescriptor = Mock(CurieDescriptor)

        curieFieldSetter = new CurieFieldSetter(mockFieldAccessor, mockCurieDescriptor)
    }

    def "test setLinks with null curie"() {
        given:
          def instance = new Object()
        when:
          curieFieldSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockLinkProvider.getCurie(mockCurieDescriptor) >> null
        and:
          0 * mockFieldAccessor.setValue(instance, null)
    }

    def "test setLinks with curie"() {
        given:
          def instance = new Object()
          def curie = new Curie('doc', '/')
        when:
          curieFieldSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockLinkProvider.getCurie(mockCurieDescriptor) >> curie
        and:
          1 * mockFieldAccessor.setValue(instance, curie)
    }
}
