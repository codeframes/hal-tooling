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
package com.github.codeframes.hal.tooling.link.bindings.uri

import spock.lang.Specification

class UriTemplateBuilderTest extends Specification {

    def "test appendPath with 1 append"() {
        when:
          def uri_template = new UriTemplateBuilder().appendPath(value).build()
        then:
          uri_template == expected_uri_template
        where:
          value       | expected_uri_template
          '/'         | '/'
          'api'       | '/api'
          '/api'      | '/api'
          '/api/'     | '/api/'
          '/{id}/'    | '/{id}/'
          '{id}/123/' | '/{id}/123/'
    }

    def "test appendPath with 2 appends"() {
        when:
          def uri_template = new UriTemplateBuilder()
                  .appendPath(value_1)
                  .appendPath(value_2)
                  .build()
        then:
          uri_template == expected_uri_template
        where:
          value_1    | value_2    | expected_uri_template
          'api'      | '{id}'     | '/api/{id}'
          '/api'     | '{id}'     | '/api/{id}'
          '/api/'    | '/{id}'    | '/api/{id}'
          '/{id}/'   | '/item/'   | '/{id}/item/'
          '{id}/123/' | 'item/abc' | '/{id}/123/item/abc'
    }

    def "test appendPath with an illegal argument"() {
        when:
          new UriTemplateBuilder().appendPath(value).build()
        then:
          thrown(IllegalArgumentException)
        where:
          value << ['', '   ']
    }

    def "test appendTemplatedQueryParam with 1 append"() {
        when:
          def uri_template = new UriTemplateBuilder().appendTemplatedQueryParam(value).build()
        then:
          uri_template == expected_uri_template
        where:
          value   | expected_uri_template
          'q'     | '{?q}'
          'limit' | '{?limit}'
    }

    def "test appendTemplatedQueryParam with 2 appends"() {
        when:
          def uri_template = new UriTemplateBuilder()
                  .appendTemplatedQueryParam(value_1)
                  .appendTemplatedQueryParam(value_2)
                  .build()
        then:
          uri_template == expected_uri_template
        where:
          value_1 | value_2 | expected_uri_template
          'q1'    | 'q2'    | '{?q1,q2}'
          'limit' | 'order' | '{?limit,order}'
    }

    def "test appendTemplatedQueryParam with an illegal argument"() {
        when:
          new UriTemplateBuilder().appendTemplatedQueryParam(value).build()
        then:
          thrown(IllegalArgumentException)
        where:
          value << ['', '   ']
    }
}
