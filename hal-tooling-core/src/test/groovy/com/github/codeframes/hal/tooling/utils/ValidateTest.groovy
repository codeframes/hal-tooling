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
package com.github.codeframes.hal.tooling.utils

import spock.lang.Specification

import static org.hamcrest.CoreMatchers.containsString
import static spock.util.matcher.HamcrestSupport.that

class ValidateTest extends Specification {

    def "test isNullOrHasText success"() {
        when:
          def result = Validate.isNullOrHasText(value, 'arg1')
        then:
          result.is(value)
        where:
          value << ['text' as CharSequence, null, new StringBuilder('text')]
    }

    def "test isNullOrHasText failure"() {
        when:
          Validate.isNullOrHasText(value, 'arg1')
        then:
          def thrown_exception = thrown(IllegalArgumentException)
        and:
          that thrown_exception.message, containsString('arg1')
        where:
          value << ['' as CharSequence, '   ' as CharSequence, new StringBuilder('')]
    }

    def "test hasText success"() {
        when:
          def result = Validate.hasText(value, 'arg2')
        then:
          result.is(value)
        where:
          value << ['text' as CharSequence, new StringBuilder('text')]
    }

    def "test hasText failure"() {
        when:
          Validate.hasText(value, 'arg2')
        then:
          def thrown_exception = thrown(expected_exception)
        and:
          that thrown_exception.message, containsString('arg2')
        where:
          value   | expected_exception
          ''      | IllegalArgumentException
          '     ' | IllegalArgumentException
          null    | NullPointerException
    }

    def "test notNull success"() {
        when:
          def result = Validate.notNull(value, 'arg3')
        then:
          result.is(value)
        where:
          value << ['', new Object(), new Date()]
    }

    def "test notNull failure"() {
        when:
          Validate.notNull(null, 'arg3')
        then:
          def thrown_exception = thrown(NullPointerException)
        and:
          that thrown_exception.message, containsString('arg3')
    }
}
