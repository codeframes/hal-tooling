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
package com.github.codeframes.hal.tooling.json.representations

import com.github.codeframes.hal.tooling.core.Embedded
import com.github.codeframes.hal.tooling.core.Link
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class VndErrorTest extends Specification {

    def "test verifyLinkRel"() {
        given:
          def rel = 'self';
          def link = new Link(rel, '/')
        when:
          def result = VndError.verifyLinkRel(rel, link)
        then:
          result.is(link)
    }

    def "test verifyLinkRel when link is null"() {
        given:
          def rel = 'self';
        when:
          def result = VndError.verifyLinkRel(rel, null)
        then:
          result == null
    }

    def "test verifyLinkRel when rel's don't match"() {
        given:
          def rel = 'self';
          def link = new Link('not_self', '/')
        when:
          VndError.verifyLinkRel(rel, link)
        then:
          thrown(IllegalArgumentException)
    }

    def "test toEmbeddedErrors"() {
        given:
          def errors = [new VndError.Builder()
                                .message('error')
                                .build()]
        when:
          def result = VndError.toEmbeddedErrors(errors)
        then:
          result == new Embedded<>("errors", errors)
    }

    def "test toEmbeddedErrors when errors is null"() {
        when:
          def result = VndError.toEmbeddedErrors(null)
        then:
          result == null
    }

    def "test toEmbeddedErrors when errors is empty"() {
        when:
          def result = VndError.toEmbeddedErrors([])
        then:
          result == null
    }

    def "test equals"() {
        given:
          def error_1 = new VndError.Builder().message('error one').build()
          def error_2 = new VndError.Builder().message('error two').build()
        when:
          EqualsVerifier.forClass(VndError).usingGetClass()
                  .withPrefabValues(VndError, error_1, error_2)
                  .verify()
        then:
          noExceptionThrown()
    }
}
