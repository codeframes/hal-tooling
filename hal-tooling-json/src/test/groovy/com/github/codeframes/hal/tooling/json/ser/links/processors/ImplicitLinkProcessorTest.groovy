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
package com.github.codeframes.hal.tooling.json.ser.links.processors

import com.github.codeframes.hal.tooling.core.Link
import spock.lang.Specification

class ImplicitLinkProcessorTest extends Specification {

    def "test addLinks with single link"() {
        setup:
          def linkMap = [:]
          def bean = new Object()
          def link = new Link('rel', 'href')
          def linkProcessor = Spy(ImplicitLinkProcessor) {
              get({ it == bean }) >> link
          }

        when:
          linkProcessor.addLinks(linkMap, bean)

        then:
          linkMap == [rel: link]
    }

    def "test addLinks with null link"() {
        setup:
          def linkMap = [:]
          def bean = new Object()
          def linkProcessor = Spy(ImplicitLinkProcessor) {
              get({ it == bean }) >> null
          }

        when:
          linkProcessor.addLinks(linkMap, bean)

        then:
          linkMap == [:]
    }

    def "test addLinks with existing rel link"() {
        setup:
          def link_1 = new Link('rel', 'href_1')
          def link_2 = new Link('rel', 'href_2')
          def linkMap = [rel: link_1] as Map
          def bean = new Object()
          def linkProcessor = Spy(ImplicitLinkProcessor) {
              get({ it == bean }) >> link_2
          }

        when:
          linkProcessor.addLinks(linkMap, bean)

        then:
          linkMap == [rel: [link_1, link_2]]
    }

    def "test addLinks with existing rel links"() {
        setup:
          def link_1 = new Link('rel', 'href_1')
          def link_2 = new Link('rel', 'href_2')
          def link_3 = new Link('rel', 'href_3')
          def linkMap = [rel: [link_1, link_2]] as Map
          def bean = new Object()
          def linkProcessor = Spy(ImplicitLinkProcessor) {
              get({ it == bean }) >> link_3
          }

        when:
          linkProcessor.addLinks(linkMap, bean)

        then:
          linkMap == [rel: [link_1, link_2, link_3]]
    }
}
