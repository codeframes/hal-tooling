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
import spock.lang.Unroll

class UriTemplateUtilTest extends Specification {

    @Unroll("'#uri' string expansion should be recognised as a templated uri")
    def "test isTemplated for String expansion with value modifiers"() {
        when:
          def templated = UriTemplateUtil.isTemplated(uri)
        then:
          templated
        where:
          uri << [
                  '{var:3}',
                  '{list}',
                  '{list*}'
          ]
    }

    @Unroll("'#uri' reserved expansion should be recognised as a templated uri")
    def "test isTemplated for Reserved expansion with value modifiers"() {
        when:
          def templated = UriTemplateUtil.isTemplated(uri)
        then:
          templated
        where:
          uri << [
                  '{+path:6}/here',
                  '{+list}',
                  '{+list*}'
          ]
    }

    @Unroll("'#uri' fragment expansion should be recognised as a templated uri")
    def "test isTemplated for Fragment expansion with value modifiers"() {
        when:
          def templated = UriTemplateUtil.isTemplated(uri)
        then:
          templated
        where:
          uri << [
                  '{#path:6}/here',
                  '{#list}',
                  '{#list*}'
          ]
    }

    @Unroll("'#uri' label expansion should be recognised as a templated uri")
    def "test isTemplated for Label expansion, dot-prefixed"() {
        when:
          def templated = UriTemplateUtil.isTemplated(uri)
        then:
          templated
        where:
          uri << [
                  'X{.var:3}',
                  'X{.list}',
                  'X{.list*}'
          ]
    }

    @Unroll("'#uri' path segments expansion should be recognised as a templated uri")
    def "test isTemplated for Path segments, slash-prefixed"() {
        when:
          def templated = UriTemplateUtil.isTemplated(uri)
        then:
          templated
        where:
          uri << [
                  '{/var:1,var}',
                  '{/list}',
                  '{/list*}',
                  '{/list*,path:4}'
          ]
    }

    @Unroll("'#uri' path-style parameters expansion should be recognised as a templated uri")
    def "test isTemplated for Path-style parameters, semicolon-prefixed"() {
        when:
          def templated = UriTemplateUtil.isTemplated(uri)
        then:
          templated
        where:
          uri << [
                  '{;hello:5}',
                  '{;list}',
                  '{;list*}'
          ]
    }

    @Unroll("'#uri' form-style query expansion should be recognised as a templated uri")
    def "test isTemplated for Form-style query, ampersand-separated"() {
        when:
          def templated = UriTemplateUtil.isTemplated(uri)
        then:
          templated
        where:
          uri << [
                  '{?var:3}',
                  '{?list}',
                  '{?list*}'
          ]
    }

    @Unroll("'#uri' form-style query continuation expansion should be recognised as a templated uri")
    def "test isTemplated for Form-style query continuation"() {
        when:
          def templated = UriTemplateUtil.isTemplated(uri)
        then:
          templated
        where:
          uri << [
                  '{&var:3}',
                  '{&list}',
                  '{&list*}'
          ]
    }

    def "test isTemplated == false"() {
        when:
          def templated = UriTemplateUtil.isTemplated(uri)
        then:
          !templated
        where:
          uri << [
                  '',
                  'http://example.com',
                  'http://example.com?q=a'
          ]
    }
}
