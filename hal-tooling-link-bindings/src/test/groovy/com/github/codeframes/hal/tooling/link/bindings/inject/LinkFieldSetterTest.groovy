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

import com.github.codeframes.hal.tooling.core.Link
import com.github.codeframes.hal.tooling.link.bindings.core.FieldAccessor
import com.github.codeframes.hal.tooling.link.bindings.core.LinkDescriptor
import com.github.codeframes.hal.tooling.link.bindings.core.LinkProvider
import spock.lang.Specification

class LinkFieldSetterTest extends Specification {

    FieldAccessor mockFieldAccessor
    LinkDescriptor mockLinkDescriptor
    LinkProvider mockLinkProvider

    LinkFieldSetter linkSetter

    def setup() {
        mockFieldAccessor = Mock(FieldAccessor)
        mockLinkDescriptor = Mock(LinkDescriptor)
        mockLinkProvider = Mock(LinkProvider)

        linkSetter = new LinkFieldSetter(mockFieldAccessor, mockLinkDescriptor)
    }

    def "test setLinks"() {
        given:
          def instance = new Object()
          def link = new Link('self', '/')
        when:
          linkSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockLinkProvider.getLink(mockLinkDescriptor) >> link
        and:
          1 * mockFieldAccessor.setValue(instance, link)
    }

    def "test setLinks with null Link"() {
        given:
          def instance = new Object()
        when:
          linkSetter.setLinks(instance, mockLinkProvider)
        then:
          1 * mockLinkProvider.getLink(mockLinkDescriptor) >> null
        and:
          0 * mockFieldAccessor.setValue(instance, null)
    }
}
