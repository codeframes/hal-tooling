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

import com.github.codeframes.hal.tooling.link.bindings.core.LinkProvider
import spock.lang.Specification

class EmbeddedBeanLinkSetterTest extends Specification {

    LinkSetter mockLinkSetter
    LinkProvider mockLinkProvider
    LinkProvider mockBeanLinkProvider

    def setup() {
        mockLinkSetter = Mock(LinkSetter)
        mockLinkProvider = Mock(LinkProvider)
        mockBeanLinkProvider = Mock(LinkProvider)
    }

    def "test setLinks with single link setter"() {
        given:
          def instance = new Object()
          EmbeddedBeanLinkSetter embeddedBeanLinkSetter = new EmbeddedBeanLinkSetter([mockLinkSetter])
        when:
          embeddedBeanLinkSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockLinkProvider.forBean(instance) >> mockBeanLinkProvider
        and:
          1 * mockLinkSetter.setLinks(instance, mockBeanLinkProvider)
    }

    def "test setLinks with multiple link setters"() {
        given:
          def instance = new Object()
          EmbeddedBeanLinkSetter embeddedBeanLinkSetter = new EmbeddedBeanLinkSetter([mockLinkSetter, mockLinkSetter])
        when:
          embeddedBeanLinkSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockLinkProvider.forBean(instance) >> mockBeanLinkProvider
        and:
          2 * mockLinkSetter.setLinks(instance, mockBeanLinkProvider)
    }
}
