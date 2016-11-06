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

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class CurieTest extends Specification {

    def "test for invalid arguments provided for construction"() {
        when:
          new Curie(name, href)
        then:
          thrown(exception_thrown)
        where:
          name  | href                                 | exception_thrown
          null  | 'http://example.com/relations/{rel}' | NullPointerException
          ''    | 'http://example.com/relations/{rel}' | IllegalArgumentException
          '   ' | 'http://example.com/relations/{rel}' | IllegalArgumentException
          'doc' | null                                 | NullPointerException
          'doc' | ''                                   | IllegalArgumentException
          'doc' | '   '                                | IllegalArgumentException
    }

    def "test equals"() {
        when:
          EqualsVerifier.forClass(Curie).usingGetClass().verify()
        then:
          noExceptionThrown()
    }
}
