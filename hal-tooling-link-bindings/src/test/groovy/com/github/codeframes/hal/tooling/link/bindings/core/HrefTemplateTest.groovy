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

import com.github.codeframes.hal.tooling.link.bindings.Style
import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class HrefTemplateTest extends Specification {

    def "test construction"() {
        when:
          def hrefTemplate = new HrefTemplate(value, style)
        then:
          hrefTemplate.value == value
          hrefTemplate.style == style
          hrefTemplate.containsEL() == contains_el
          hrefTemplate.containsVariables() == contains_vars
        where:
          value                              | style               | contains_el | contains_vars | param_names
          ''                                 | Style.ABSOLUTE      | false       | false         | []
          '/rels/{rel}'                      | Style.ABSOLUTE      | false       | true          | ['rel']
          '/rels{/rel}'                      | Style.ABSOLUTE_PATH | false       | true          | ['rel']
          '/items{/item}{?date}'             | Style.ABSOLUTE_PATH | false       | true          | ['item', 'date']
          '/items{/item}?ids=${ids}'         | Style.RELATIVE_PATH | true        | true          | ['item']
          '/items{/item}?ids=${ids}{&dates}' | Style.RELATIVE_PATH | true        | true          | ['item', 'dates']
    }

    def "test resolve with absolute uri value"() {
        given:
          def value = 'http://localhost:8080/api'
          def mockLinkContext = Mock(LinkContext)
          def hrefTemplate = new HrefTemplate(value, Style.ABSOLUTE)
        when:
          def href = hrefTemplate.resolve(mockLinkContext)
        then:
          href == new Href(value, false)
    }

    def "test resolve with absolute uri value and embedded EL"() {
        given:
          def value = 'http://localhost:8080/api/${instance.id}'
          def mockLinkContext = Mock(LinkContext)
          def hrefTemplate = new HrefTemplate(value, Style.ABSOLUTE)
        when:
          def href = hrefTemplate.resolve(mockLinkContext)
        then:
          1 * mockLinkContext.evaluateAsString(value) >> 'http://localhost:8080/api/1'
        and:
          href == new Href('http://localhost:8080/api/1', false)
    }

    def "test resolve with absolute uri template value"() {
        given:
          def value = 'http://localhost:8080/api{/id}'
          def mockLinkContext = Mock(LinkContext)
          def hrefTemplate = new HrefTemplate(value, Style.ABSOLUTE, [id: '${instance.id}'], true)
        when:
          def href = hrefTemplate.resolve(mockLinkContext)
        then:
          1 * mockLinkContext.expand(value, ['id': '${instance.id}'], true) >> 'http://localhost:8080/api/1'
        and:
          href == new Href('http://localhost:8080/api/1', false)
    }

    def "test resolve with absolute uri template value and no bindings"() {
        given:
          def value = 'http://localhost:8080/api{/id}'
          def mockLinkContext = Mock(LinkContext)
          def hrefTemplate = new HrefTemplate(value, Style.ABSOLUTE)
        when:
          def href = hrefTemplate.resolve(mockLinkContext)
        then:
          1 * mockLinkContext.expand(value, [:], false) >> value
        and:
          href == new Href(value, true)
    }

    def "test resolve with relative uri value"() {
        given:
          def value = '/api'
          def mockLinkContext = Mock(LinkContext)
          def hrefTemplate = new HrefTemplate(value, Style.ABSOLUTE)
        when:
          def href = hrefTemplate.resolve(mockLinkContext)
        then:
          1 * mockLinkContext.style(Style.ABSOLUTE, '/api') >> 'http://localhost:8080/api'
        and:
          href == new Href('http://localhost:8080/api', false)
    }

    def "test equals"() {
        when:
          EqualsVerifier.forClass(HrefTemplate).usingGetClass().verify()
        then:
          noExceptionThrown()
    }
}
