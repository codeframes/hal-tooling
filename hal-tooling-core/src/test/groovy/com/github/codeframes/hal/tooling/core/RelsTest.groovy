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
package com.github.codeframes.hal.tooling.core

import spock.lang.Specification

class RelsTest extends Specification {

    def "test getCuriePrefix"() {
        when:
          def result = Rels.getCuriePrefix(rel)
        then:
          result == curie_prefix
        where:
          rel                     | curie_prefix
          'ex:item'               | 'ex'
          'ns1:add-item'          | 'ns1'
          'ns2:collections/items' | 'ns2'
          'item'                  | null
    }

    def "test getName"() {
        when:
          def result = Rels.getName(rel)
        then:
          result == name
        where:
          rel                     | name
          'ex:item'               | 'item'
          'ns1:add-item'          | 'add-item'
          'ns2:collections/items' | 'collections/items'
          'item'                  | 'item'
    }
}
