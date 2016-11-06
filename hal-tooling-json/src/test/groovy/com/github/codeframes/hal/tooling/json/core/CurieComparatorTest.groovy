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

import com.github.codeframes.hal.tooling.core.Curie
import spock.lang.Specification

class CurieComparatorTest extends Specification {

    def comparator = new CurieComparator()

    def "test comparator with Collections.sort"() {
        when:
          Collections.sort(list, comparator)

        then:
          list == sorted_list

        where:
          list                                       | sorted_list
          [curie('a')]                               | [curie('a')]
          [curie('b'), curie('a')]                   | [curie('a'), curie('b')]
          [curie('c1'), curie('b1'), curie('a1')]    | [curie('a1'), curie('b1'), curie('c1')]
          [curie('bee'), curie('cat'), curie('ant')] | [curie('ant'), curie('bee'), curie('cat')]
          []                                         | []
          [curie('a'), curie('b')]                   | [curie('a'), curie('b')]
          [curie('a1'), curie('b1'), curie('c1')]    | [curie('a1'), curie('b1'), curie('c1')]
          [curie('ant'), curie('bee'), curie('cat')] | [curie('ant'), curie('bee'), curie('cat')]
    }

    static curie(String name) {
        new Curie(name, "href")
    }
}
