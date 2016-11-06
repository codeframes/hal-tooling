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

import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext
import com.github.codeframes.hal.tooling.link.bindings.core.CurieDescriptors
import com.github.codeframes.hal.tooling.link.bindings.core.LinkProvider
import spock.lang.Specification

class RootBeanLinkSetterTest extends Specification {

    CurieDescriptors mockCurieDescriptors
    LinkSetter mockLinkSetter
    LinkContext mockLinkContext

    RootBeanLinkSetter rootBeanLinkSetter

    def setup() {
        mockCurieDescriptors = Mock(CurieDescriptors)
        mockLinkSetter = Mock(LinkSetter)
        mockLinkContext = Mock(LinkContext)

        rootBeanLinkSetter = new RootBeanLinkSetter(mockCurieDescriptors, [mockLinkSetter])
    }

    def "test setLinks"() {
        given:
          def entity = new Object()
        when:
          rootBeanLinkSetter.setLinks(entity, mockLinkContext)
        then:
          1 * mockLinkContext.forBean(entity) >> mockLinkContext
        and:
          1 * mockLinkSetter.setLinks(entity, _ as LinkProvider)
    }
}
