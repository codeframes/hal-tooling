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

import com.github.codeframes.hal.tooling.core.Embeddable
import com.github.codeframes.hal.tooling.link.bindings.core.CurieDescriptors
import com.github.codeframes.hal.tooling.link.bindings.core.FieldAccessor
import com.github.codeframes.hal.tooling.link.bindings.core.LinkProvider
import spock.lang.Specification

class EmbeddableFieldLinkSetterTest extends Specification {

    LinkSetterFactory mockLinkSetterFactory
    FieldAccessor mockFieldAccessor
    LinkProvider mockLinkProvider
    LinkSetter mockLinkSetter
    CurieDescriptors mockCurieDescriptors

    EmbeddableFieldLinkSetter embeddableFieldLinkSetter

    def setup() {
        mockLinkSetterFactory = Mock(LinkSetterFactory)
        mockFieldAccessor = Mock(FieldAccessor)
        mockLinkProvider = Mock(LinkProvider)
        mockLinkSetter = Mock(LinkSetter)
        mockCurieDescriptors = Mock(CurieDescriptors)

        embeddableFieldLinkSetter = new EmbeddableFieldLinkSetter(mockLinkSetterFactory, mockFieldAccessor, mockCurieDescriptors)
    }

    def "test setLinks with null field value"() {
        given:
          def instance = new Object()
        when:
          embeddableFieldLinkSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockFieldAccessor.getValue(instance) >> null
        and:
          0 * mockLinkSetterFactory.getElementBeanLinkSetter(_ as Class, mockCurieDescriptors)
    }


    def "test setLinks with Embeddable"() {
        given:
          def instance = new Object()
          def embeddable = new AnEmbeddable()
        when:
          embeddableFieldLinkSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockFieldAccessor.getValue(instance) >> embeddable
        and:
          1 * mockLinkSetterFactory.getElementBeanLinkSetter(AnEmbeddable, mockCurieDescriptors) >> mockLinkSetter
        and:
          1 * mockLinkSetter.setLinks(embeddable, mockLinkProvider)
    }

    static class AnEmbeddable implements Embeddable {

        @Override
        String getRel() {
            return null
        }
    }
}
