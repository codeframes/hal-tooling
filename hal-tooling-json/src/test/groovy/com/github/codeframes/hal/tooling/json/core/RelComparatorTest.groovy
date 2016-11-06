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
package com.github.codeframes.hal.tooling.json.core

import com.github.codeframes.hal.tooling.core.Rels
import spock.lang.Specification

class RelComparatorTest extends Specification {

    def comparator = new RelComparator()

    def "test compare for rel_1 < rel_2"() {
        when:
          def result = comparator.compare(rel_1, rel_2)

        then:
          result < 0

        where:
          rel_1       | rel_2
          Rels.SELF   | 'a'
          Rels.CURIES | 'a'
          Rels.SELF   | Rels.CURIES
    }

    def "test compare for rel_1 == rel_2"() {
        when:
          def result = comparator.compare(rel_1, rel_2)

        then:
          result == 0

        where:
          rel_1       | rel_2
          'a'         | 'a'
          Rels.SELF   | Rels.SELF
          Rels.CURIES | Rels.CURIES
    }

    def "test compare for rel_1 > rel_2"() {
        when:
          def result = comparator.compare(rel_1, rel_2)

        then:
          result > 0

        where:
          rel_1       | rel_2
          'a'         | Rels.SELF
          'a'         | Rels.CURIES
          Rels.CURIES | Rels.SELF
    }

    def "test comparator with Collections.sort"() {
        when:
          Collections.sort(list, comparator)

        then:
          list == sorted_list

        where:
          list                                         | sorted_list
          ['a', Rels.SELF]                             | [Rels.SELF, 'a']
          ['a', Rels.CURIES]                           | [Rels.CURIES, 'a']
          ['a', Rels.CURIES, Rels.SELF]                | [Rels.SELF, Rels.CURIES, 'a']
          ['a', 'b', 'c', Rels.CURIES, 'd', Rels.SELF] | [Rels.SELF, Rels.CURIES, 'a', 'b', 'c', 'd']
          []                                           | []
          [Rels.SELF, 'a']                             | [Rels.SELF, 'a']
          [Rels.CURIES, 'a']                           | [Rels.CURIES, 'a']
          [Rels.SELF, Rels.CURIES, 'a']                | [Rels.SELF, Rels.CURIES, 'a']
          [Rels.SELF, Rels.CURIES, 'a', 'b', 'c', 'd'] | [Rels.SELF, Rels.CURIES, 'a', 'b', 'c', 'd']
    }
}
