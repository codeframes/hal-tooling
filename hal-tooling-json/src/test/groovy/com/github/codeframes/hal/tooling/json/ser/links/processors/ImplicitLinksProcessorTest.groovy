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

class ImplicitLinksProcessorTest extends Specification {

    def "test addLinks with same rel"() {
        setup:
          def linkMap = [:]
          def bean = new Object()
          def links = [new Link('rel', 'href_1'), new Link('rel', 'href_2')]
          def linkProcessor = Spy(ImplicitLinksProcessor) {
              get({ it == bean }) >> links
          }

        when:
          linkProcessor.addLinks(linkMap, bean)

        then:
          linkMap == [rel: links]
    }

    def "test addLinks with different rel"() {
        setup:
          def linkMap = [:]
          def bean = new Object()
          def link_1 = new Link('rel_1', 'href_1')
          def link_2 = new Link('rel_2', 'href_2')
          def links = [link_1, link_2]
          def linkProcessor = Spy(ImplicitLinksProcessor) {
              get({ it == bean }) >> links
          }

        when:
          linkProcessor.addLinks(linkMap, bean)

        then:
          linkMap == [rel_1: link_1, rel_2: link_2]
    }

    def "test addLinks with no iterable links"() {
        setup:
          def linkMap = [:]
          def bean = new Object()
          def links = []
          def linkProcessor = Spy(ImplicitLinksProcessor) {
              get({ it == bean }) >> links
          }

        when:
          linkProcessor.addLinks(linkMap, bean)

        then:
          linkMap == [:]
    }

    def "test addLinks with null links"() {
        setup:
          def linkMap = [:]
          def bean = new Object()
          def linkProcessor = Spy(ImplicitLinksProcessor) {
              get({ it == bean }) >> null
          }

        when:
          linkProcessor.addLinks(linkMap, bean)

        then:
          linkMap == [:]
    }
}
