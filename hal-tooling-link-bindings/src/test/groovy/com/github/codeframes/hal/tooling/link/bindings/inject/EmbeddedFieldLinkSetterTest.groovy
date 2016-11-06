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

import com.github.codeframes.hal.tooling.core.Embedded
import com.github.codeframes.hal.tooling.link.bindings.core.CurieDescriptors
import com.github.codeframes.hal.tooling.link.bindings.core.FieldAccessor
import com.github.codeframes.hal.tooling.link.bindings.core.LinkProvider
import spock.lang.Specification

class EmbeddedFieldLinkSetterTest extends Specification {

    LinkSetterFactory mockLinkSetterFactory
    FieldAccessor mockFieldAccessor
    LinkProvider mockLinkProvider
    LinkSetter mockLinkSetter
    CurieDescriptors mockCurieDescriptors

    EmbeddedFieldLinkSetter embeddedFieldLinkSetter

    def setup() {
        mockLinkSetterFactory = Mock(LinkSetterFactory)
        mockFieldAccessor = Mock(FieldAccessor)
        mockLinkProvider = Mock(LinkProvider)
        mockLinkSetter = Mock(LinkSetter)
        mockCurieDescriptors = Mock(CurieDescriptors)

        embeddedFieldLinkSetter = new EmbeddedFieldLinkSetter(mockLinkSetterFactory, mockFieldAccessor, mockCurieDescriptors)
    }

    def "test setLinks with null field value"() {
        given:
          def instance = new Object()
        when:
          embeddedFieldLinkSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockFieldAccessor.getValue(instance) >> null
        and:
          0 * mockLinkSetterFactory.getElementBeanLinkSetter(_, mockCurieDescriptors)
    }

    def "test setLinks with embedded resource"() {
        given:
          def instance = new Object()
          def resource = new Object()
          def embedded = new Embedded('rel', resource)
        when:
          embeddedFieldLinkSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockFieldAccessor.getValue(instance) >> embedded
        and:
          1 * mockLinkSetterFactory.getElementBeanLinkSetter(resource.getClass(), mockCurieDescriptors) >> mockLinkSetter
        and:
          1 * mockLinkSetter.setLinks(resource, mockLinkProvider)
    }

    def "test setLinks with embedded List of resource"() {
        given:
          def instance = new Object()
          def resource = new Object()
          def embedded = new Embedded('rel', [resource])
        when:
          embeddedFieldLinkSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockFieldAccessor.getValue(instance) >> embedded
        and:
          1 * mockLinkSetterFactory.getElementBeanLinkSetter(resource.getClass(), mockCurieDescriptors) >> mockLinkSetter
        and:
          1 * mockLinkSetter.setLinks(resource, mockLinkProvider)
    }

    def "test setLinks with embedded List of resources"() {
        given:
          def instance = new Object()
          def resource_1 = new Object()
          def resource_2 = new Object()
          def embedded = new Embedded('rel', [resource_1, resource_2])
        when:
          embeddedFieldLinkSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockFieldAccessor.getValue(instance) >> embedded
        and:
          1 * mockLinkSetterFactory.getElementBeanLinkSetter(resource_1.getClass(), mockCurieDescriptors) >> mockLinkSetter
          1 * mockLinkSetterFactory.getElementBeanLinkSetter(resource_2.getClass(), mockCurieDescriptors) >> mockLinkSetter
        and:
          1 * mockLinkSetter.setLinks(resource_1, mockLinkProvider)
          1 * mockLinkSetter.setLinks(resource_2, mockLinkProvider)
    }
}
