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

import com.github.codeframes.hal.tooling.core.Curie
import com.github.codeframes.hal.tooling.core.Link
import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext
import spock.lang.Specification

@SuppressWarnings("GroovyAccessibility")
class LinkProviderTest extends Specification {

    LinkContext mockLinkContext
    LinkContext mockNewLinkContext

    LinkProvider linkProvider

    def setup() {
        mockLinkContext = Mock(LinkContext)
        mockNewLinkContext = Mock(LinkContext)

        linkProvider = new LinkProvider(mockLinkContext)
    }

    def "test forBean"() {
        given:
          def bean = new Object()
        when:
          def beanlinkProvider = linkProvider.forBean(bean)
        then:
          1 * mockLinkContext.forBean(bean) >> mockNewLinkContext
        and:
          with(beanlinkProvider, LinkProvider) {
              linkContext == mockNewLinkContext
          }
    }

    def "test forBean with CurieDescriptors"() {
        given:
          def bean = new Object()
          def curieDescs = new CurieDescriptors()
        when:
          def beanlinkProvider = linkProvider.forBean(bean, curieDescs)
        then:
          1 * mockLinkContext.forBean(bean) >> mockNewLinkContext
        and:
          with(beanlinkProvider, LinkProvider) {
              curieDescriptors == curieDescs
              linkContext == mockNewLinkContext
          }
    }

    def "test getLink"() {
        given:
          def mockLinkDescriptor = Mock(LinkDescriptor)
          def expectedLink = new Link('self', '/')
        when:
          def link = linkProvider.getLink(mockLinkDescriptor)
        then:
          1 * mockLinkDescriptor.toLink(mockLinkContext) >> expectedLink
        and:
          link == expectedLink
    }

    def "test getLink with null link"() {
        given:
          def mockLinkDescriptor = Mock(LinkDescriptor)
        when:
          def link = linkProvider.getLink(mockLinkDescriptor)
        then:
          1 * mockLinkDescriptor.toLink(mockLinkContext) >> null
        and:
          link == null
    }

    def "test getLink for link with curie when no curie is registered"() {
        given:
          def mockLinkDescriptor = Mock(LinkDescriptor)
        when:
          linkProvider.getLink(mockLinkDescriptor)
        then:
          1 * mockLinkDescriptor.toLink(mockLinkContext) >> new Link('doc:info', '/api/info')
          1 * mockLinkDescriptor.getCurie() >> 'doc'
        and:
          thrown(IllegalArgumentException)
    }

    def "test getCurie"() {
        given:
          def mockCurieDescriptor = Mock(CurieDescriptor)
          def expectedCurie = new Curie('doc', '/docs/{rel}')
        when:
          def curie = linkProvider.getCurie(mockCurieDescriptor)
        then:
          1 * mockCurieDescriptor.toCurie(mockLinkContext) >> expectedCurie
        and:
          curie == expectedCurie
    }
}
