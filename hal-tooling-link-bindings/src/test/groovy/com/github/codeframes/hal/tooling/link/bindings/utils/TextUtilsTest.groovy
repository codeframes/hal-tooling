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

class TextUtilsTest extends Specification {

    def "test snakeCaseToCamelCase"() {
        when:
          def result = TextUtils.snakeCaseToCamelCase(value)
        then:
          result == camelCase
        where:
          value           | camelCase
          ''              | ''
          'text'          | 'text'
          'text_1'        | 'text1'
          'one_two'       | 'oneTwo'
          'one_two_three' | 'oneTwoThree'
          'a_b'           | 'aB'
          'a__b'          | 'aB'
          'a_bc'          | 'aBc'
          'a_b_c'         | 'aBC'
          'ab_cd_e_f'     | 'abCdEF'
    }
}
