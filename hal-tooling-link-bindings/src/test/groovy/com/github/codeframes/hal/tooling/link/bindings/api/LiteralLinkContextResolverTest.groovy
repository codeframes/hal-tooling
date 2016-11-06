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
package com.github.codeframes.hal.tooling.link.bindings.api

import spock.lang.Specification

class LiteralLinkContextResolverTest extends Specification {

    def resolver = new LiteralLinkContextResolver()

    def "test resolveAbsolute"() {
        given:
          def template = 'Absolute'
        when:
          def result = resolver.resolveAbsolute(template)
        then:
          result == template
    }

    def "test resolveAbsolutePath"() {
        given:
          def template = 'AbsolutePath'
        when:
          def result = resolver.resolveAbsolutePath(template)
        then:
          result == template
    }

    def "test resolveRelativePath"() {
        given:
          def template = 'RelativePath'
        when:
          def result = resolver.resolveRelativePath(template)
        then:
          result == template
    }
}
