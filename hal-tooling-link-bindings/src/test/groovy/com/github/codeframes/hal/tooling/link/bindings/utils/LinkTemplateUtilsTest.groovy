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
package com.github.codeframes.hal.tooling.link.bindings.utils

import spock.lang.Specification
import spock.lang.Unroll

class LinkTemplateUtilsTest extends Specification {

    @Unroll
    def "test that '#link_template' contains Expression Language notation"() {
        expect:
          LinkTemplateUtils.containsEL(link_template)
        where:
          link_template << [
                  '${arg}',
                  '/api/query=${q}',
                  '/api/query=123#${f}'
          ]
    }

    @Unroll
    def "test that '#link_template' does NOT contain Expression Language notation"() {
        expect:
          !LinkTemplateUtils.containsEL(link_template)
        where:
          link_template << [
                  '',
                  '{arg}',
                  '/api/{?query}'
          ]
    }

    @Unroll
    def "test that '#link_template' contains the parameter names: #names"() {
        when:
          def result = LinkTemplateUtils.extractParameterNames(link_template)
        then:
          result == names
        where:
          link_template                | names
          ''                           | []
          '${arg}'                     | []
          '{arg}'                      | ['arg']
          '/api/{a}'                   | ['a']
          '/api{/b}'                   | ['b']
          '/api/{?query}'              | ['query']
          '/api/{?q1,q2}'              | ['q1', 'q2']
          '/api/?query=123{&id}'       | ['id']
          '/api/?query=123{#fragment}' | ['fragment']
          '/api/{+path,p}'             | ['path', 'p']
          '/api/{.x,y}'                | ['x', 'y']
          '/api/{;z}'                  | ['z']
    }

    @Unroll
    def "test that '#link_template' is templated"() {
        expect:
          LinkTemplateUtils.isTemplated(link_template)
        where:
          link_template << [
                  '{arg}',
                  '/api/{arg}',
                  '/api{/arg}',
                  '/api/{?query}',
                  '/api/{?q1,q2}',
                  '/api/?query=123{&id}',
                  '/api/?query=123{#fragment}',
                  '/api/{+path,x}',
                  '/api/{.x,y}',
                  '/api/{;x}'
          ]
    }

    @Unroll
    def "test that '#link_template' is NOT a templated"() {
        expect:
          !LinkTemplateUtils.isTemplated(link_template)
        where:
          link_template << [
                  '',
                  '${arg}',
                  'api/',
                  '/api/',
                  '/api/${arg}',
                  '/api/?query=${arg}'
          ]
    }

    @Unroll
    def "test that '#link_template' is absolute"() {
        expect:
          LinkTemplateUtils.isAbsolute(link_template)
        where:
          link_template << [
                  'http://localhost:8080/',
                  'https://localhost:8080/',
                  'http://localhost:8080/{arg}',
                  'http://localhost:8080{/arg}',
                  'http://localhost:8080/{?query}',
                  'http://localhost:8080/{?q1,q2}',
                  'http://localhost:8080/?query=123{&id}',
                  'http://localhost:8080/?query=123{#fragment}',
                  'http://localhost:8080/{+path,x}',
                  'http://localhost:8080/{.x,y}',
                  'http://localhost:8080/{;x}',
                  'http://localhost:8080/?query=${arg}'
          ]
    }

    @Unroll
    def "test that '#link_template' is NOT absolute"() {
        expect:
          !LinkTemplateUtils.isAbsolute(link_template)
        where:
          link_template << [
                  '/',
                  'api/',
                  '/api/',
                  '/api/{arg}',
                  '/api{/arg}',
                  '/api/{?query}',
                  '/api/{?q1,q2}',
                  '/api/?query=123{&id}',
                  '/api/?query=123{#fragment}',
                  '/api/{+path,x}',
                  '/api/{.x,y}',
                  '/api/{;x}',
                  '/api/?query=${arg}',
                  '\\\\'
          ]
    }
}
