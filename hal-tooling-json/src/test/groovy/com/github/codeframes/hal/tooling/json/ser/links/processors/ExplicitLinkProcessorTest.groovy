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

class ExplicitLinkProcessorTest extends Specification {

    def "test addLinks with single link"() {
        setup:
          def linkMap = [:]
          def bean = new Object()
          def link = new Link('rel', 'href')
          def linkProcessor = Spy(ExplicitLinkProcessor) {
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
          def linkProcessor = Spy(ExplicitLinkProcessor) {
              get({ it == bean }) >> null
          }

        when:
          linkProcessor.addLinks(linkMap, bean)

        then:
          linkMap == [:]
    }
}
