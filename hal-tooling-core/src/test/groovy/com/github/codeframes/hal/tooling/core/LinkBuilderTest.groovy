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

class LinkBuilderTest extends Specification {

    def "test for invalid arguments provided for construction"() {
        when:
          new Link.Builder()
                  .rel(rel)
                  .href(href)
                  .type(type)
                  .deprecation(deprecation)
                  .name(name)
                  .profile(profile)
                  .title(title)
                  .hreflang(hreflang)
                  .build()
        then:
          thrown(exception_thrown)
        where:
          invalid_property | rel    | href         | type | deprecation | name | profile | title | hreflang | exception_thrown
          'rel'            | null   | '/rels/data' | null | null        | null | null    | null  | null     | NullPointerException
          'rel'            | ''     | '/rels/data' | null | null        | null | null    | null  | null     | IllegalArgumentException
          'rel'            | '   '  | '/rels/data' | null | null        | null | null    | null  | null     | IllegalArgumentException

          'href'           | 'data' | null         | null | null        | null | null    | null  | null     | NullPointerException
          'href'           | 'data' | ''           | null | null        | null | null    | null  | null     | IllegalArgumentException
          'href'           | 'data' | '   '        | null | null        | null | null    | null  | null     | IllegalArgumentException

          'type'           | 'data' | '/rels/data' | ''   | null        | null | null    | null  | null     | IllegalArgumentException
          'type'           | 'data' | '/rels/data' | '  ' | null        | null | null    | null  | null     | IllegalArgumentException

          'deprecation'    | 'data' | '/rels/data' | null | ''          | null | null    | null  | null     | IllegalArgumentException
          'deprecation'    | 'data' | '/rels/data' | null | '  '        | null | null    | null  | null     | IllegalArgumentException

          'name'           | 'data' | '/rels/data' | null | null        | ''   | null    | null  | null     | IllegalArgumentException
          'name'           | 'data' | '/rels/data' | null | null        | '  ' | null    | null  | null     | IllegalArgumentException

          'profile'        | 'data' | '/rels/data' | null | null        | null | ''      | null  | null     | IllegalArgumentException
          'profile'        | 'data' | '/rels/data' | null | null        | null | '  '    | null  | null     | IllegalArgumentException

          'title'          | 'data' | '/rels/data' | null | null        | null | null    | ''    | null     | IllegalArgumentException
          'title'          | 'data' | '/rels/data' | null | null        | null | null    | '  '  | null     | IllegalArgumentException

          'hreflang'       | 'data' | '/rels/data' | null | null        | null | null    | null  | ''       | IllegalArgumentException
          'hreflang'       | 'data' | '/rels/data' | null | null        | null | null    | null  | '  '     | IllegalArgumentException
    }
}
