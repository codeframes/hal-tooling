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

class LinkTest extends Specification {

    def "test construction for correct template resolution"() {
        when:
          def link = new Link(href)
        then:
          link.isTemplated() == templated
        where:
          href                                  | templated
          'http://example.com/relations/data'   | false
          '/relations/data'                     | false
          'http://example.com/relations{/data}' | true
          '/relations{/data}'                   | true
    }

    def "test basic construction with invalid arguments provided"() {
        when:
          new Link(rel, href)
        then:
          thrown(exception_thrown)
        where:
          rel    | href                                | exception_thrown
          null   | 'http://example.com/relations/data' | NullPointerException
          ''     | 'http://example.com/relations/data' | IllegalArgumentException
          '   '  | 'http://example.com/relations/data' | IllegalArgumentException
          'data' | null                                | NullPointerException
          'data' | ''                                  | IllegalArgumentException
          'data' | '   '                               | IllegalArgumentException
    }

    def "test construction with invalid arguments provided"() {
        when:
          new Link('rel', 'href', false, type, deprecation, name, profile, title, hreflang)
        then:
          thrown(IllegalArgumentException)
        where:
          type   | deprecation   | name   | profile   | title   | hreflang
          ''     | 'deprecation' | 'name' | 'profile' | 'title' | 'hreflang'
          '   '  | 'deprecation' | 'name' | 'profile' | 'title' | 'hreflang'

          'type' | ''            | 'name' | 'profile' | 'title' | 'hreflang'
          'type' | '   '         | 'name' | 'profile' | 'title' | 'hreflang'

          'type' | 'deprecation' | ''     | 'profile' | 'title' | 'hreflang'
          'type' | 'deprecation' | '   '  | 'profile' | 'title' | 'hreflang'

          'type' | 'deprecation' | 'name' | ''        | 'title' | 'hreflang'
          'type' | 'deprecation' | 'name' | '   '     | 'title' | 'hreflang'

          'type' | 'deprecation' | 'name' | 'profile' | ''      | 'hreflang'
          'type' | 'deprecation' | 'name' | 'profile' | '   '   | 'hreflang'

          'type' | 'deprecation' | 'name' | 'profile' | 'title' | ''
          'type' | 'deprecation' | 'name' | 'profile' | 'title' | '   '
    }

    def "test equals"() {
        when:
          EqualsVerifier.forClass(Link).usingGetClass().verify()
        then:
          noExceptionThrown()
    }
}
