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

import com.github.codeframes.hal.tooling.core.Link
import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class LinkDescriptorTest extends Specification {

    def mockLinkContext = Mock(LinkContext)

    def "test toLink"() {
        given:
          def mockHrefTemplate = Mock(HrefTemplate)
          def linkDescriptor = LinkDescriptorBuilder.build(
                  rel: 'ns:rel',
                  hrefTemplate: mockHrefTemplate,
                  type: 'type',
                  deprecation: 'deprecation',
                  name: 'name',
                  profile: 'profile',
                  title: 'title',
                  hreflang: 'hreflang',
                  condition: null,
                  curie: 'ns'
          )
        when:
          def link = linkDescriptor.toLink(mockLinkContext)
        then:
          mockHrefTemplate.resolve(mockLinkContext) >> new Href('/api/{id}', true)
        and:
          link == new Link.Builder()
                  .rel('ns:rel')
                  .href('/api/{id}')
                  .templated(true)
                  .type('type')
                  .deprecation('deprecation')
                  .name('name')
                  .profile('profile')
                  .title('title')
                  .hreflang('hreflang')
                  .build()
    }

    def "test toLink with condition evaluated to true"() {
        given:
          def mockHrefTemplate = Mock(HrefTemplate)
          def linkDescriptor = LinkDescriptorBuilder.build(
                  rel: 'rel',
                  hrefTemplate: mockHrefTemplate,
                  condition: 'condition'
          )
        when:
          def link = linkDescriptor.toLink(mockLinkContext)
        then:
          1 * mockHrefTemplate.resolve(mockLinkContext) >> new Href('/api', false)
          1 * mockLinkContext.evaluateAsBoolean('condition') >> true
        and:
          link == new Link.Builder()
                  .rel('rel')
                  .href('/api')
                  .build()
    }

    def "test toLink with condition evaluated to false"() {
        given:
          def linkDescriptor = LinkDescriptorBuilder.build(
                  rel: 'rel',
                  condition: 'condition'
          )
        when:
          def link = linkDescriptor.toLink(mockLinkContext)
        then:
          1 * mockLinkContext.evaluateAsBoolean('condition') >> false
        and:
          link == null
    }

    def "test equals"() {
        when:
          EqualsVerifier.forClass(LinkDescriptor).usingGetClass().verify()
        then:
          noExceptionThrown()
    }
}
