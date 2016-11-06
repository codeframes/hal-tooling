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
package com.github.codeframes.hal.tooling.link.bindings.context

import com.github.codeframes.hal.tooling.link.bindings.api.LinkContextResolver
import com.github.codeframes.hal.tooling.link.bindings.api.LiteralLinkContextResolver
import com.github.codeframes.hal.tooling.link.bindings.uri.UriTemplateExpander
import spock.lang.Specification

import javax.el.ExpressionFactory

class DefaultLinkContextITest extends Specification {

    DefaultLinkContext linkContext

    def setup() {
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance()
        LinkContextResolver linkContextResolver = new LiteralLinkContextResolver()
        UriTemplateExpander uriTemplateExpander = new UriTemplateExpander()

        def entity = [bool: true, name: 'bob', map: [k: 'v'], list: [1, 2, 3]]
        LinkELContext linkELContext = new DefaultLinkELContext(entity as Object)

        linkContext = new DefaultLinkContext(expressionFactory, linkContextResolver, uriTemplateExpander, linkELContext)
    }

    def "test evaluateAsBoolean"() {
        when:
          def result = linkContext.evaluateAsBoolean(expression)
        then:
          result == expected_result
        where:
          expression                | expected_result
          ''                        | false
          'false'                   | false
          'true'                    | true
          '${entity.bool == true}'  | true
          '${entity.name == "bob"}' | true
    }

    def "test evaluateAsString"() {
        when:
          def result = linkContext.evaluateAsString(expression)
        then:
          result == expected_result
        where:
          expression                                                | expected_result
          ''                                                        | ''
          '/api/${entity.name == "bob" ? "bob" : "bill"}{/id}'      | '/api/bob{/id}'
          '/api/${entity.bool == false ? "bob" : "bill"}{/id}'      | '/api/bill{/id}'
          '/api/${entity.not_exist == null ? "{id}" : "bill{/id}"}' | '/api/{id}'
          '${entity.not_exist}'                                     | ''
    }

    def "test expand"() {
        when:
          def result = linkContext.expand(template, bindings, true)
        then:
          result == expected_result
        where:
          template                    | bindings                                            | expected_result
          ''                          | [:]                                                 | ''
          '/customers/{name}'         | [name: 'ben']                                       | '/customers/ben'
          '/customers/{name}'         | [name: '${entity.name}']                            | '/customers/bob'
          '/customers/?value={v}'     | [v: '${entity.map["k"]}']                           | '/customers/?value=v'
          '/{name}?enabled={enabled}' | [name: '${entity.name}', enabled: '${entity.bool}'] | '/bob?enabled=true'
    }
}
