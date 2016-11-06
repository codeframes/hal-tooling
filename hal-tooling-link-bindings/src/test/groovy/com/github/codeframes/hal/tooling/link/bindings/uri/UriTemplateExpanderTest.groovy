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
package com.github.codeframes.hal.tooling.link.bindings.uri

import spock.lang.Specification

class UriTemplateExpanderTest extends Specification {

    UriTemplateExpander templateExpander

    def setup() {
        templateExpander = new UriTemplateExpander()
    }

    def "test string expansion"() {
        when:
          def result = templateExpander.expand(template, values, false)
        then:
          result == expansion
        where:
          template      | values                                   | expansion
          'map?{x,y}'   | [:]                                      | 'map?{x,y}'
          'map?{x,y}'   | [x: 1024, y: 768]                        | 'map?1024,768'
          '{x,hello,y}' | [x: 1024, hello: 'Hello World!', y: 768] | '1024,Hello+World%21,768'
          '{x,hello,y}' | [x: 1024, hello: 'Hello World!']         | '1024,Hello+World%21{y}'
          '{x,hello,y}' | [hello: 'Hello World!']                  | 'Hello+World%21{x,y}'
    }

    def "test string expansion with unexpanded omitted"() {
        when:
          def result = templateExpander.expand(template, values, true)
        then:
          result == expansion
        where:
          template      | values                           | expansion
          'map?{x,y}'   | [:]                              | 'map?'
          'map?{x,y}'   | [y: 768]                         | 'map?768'
          '{x,hello,y}' | [hello: 'Hello World!', y: 768]  | 'Hello+World%21,768'
          '{x,hello,y}' | [x: 1024, hello: 'Hello World!'] | '1024,Hello+World%21'
          '{x,hello,y}' | [hello: 'Hello World!']          | 'Hello+World%21'
    }

    def "test reserved expansion"() {
        when:
          def result = templateExpander.expand(template, values, false)
        then:
          result == expansion
        where:
          template           | values                                   | expansion
          '{+x,hello,y}'     | [:]                                      | '{+x,hello,y}'
          '{+x,hello,y}'     | [x: 1024, hello: 'Hello World!', y: 768] | '1024,Hello+World%21,768'
          '{+path,x}/here'   | [path: '/foo/bar', x: 1024]              | '/foo/bar,1024/here'
          '{+path,x}/here'   | [path: '/foo/bar']                       | '/foo/bar{+x}/here'
          '{+path,x,y}/here' | [path: '/foo/bar']                       | '/foo/bar{+x,y}/here'
    }

    def "test reserved expansion with unexpanded omitted"() {
        when:
          def result = templateExpander.expand(template, values, true)
        then:
          result == expansion
        where:
          template           | values                          | expansion
          '{+x,hello,y}'     | [:]                             | ''
          '{+x,hello,y}'     | [hello: 'Hello World!', y: 768] | 'Hello+World%21,768'
          '{+path,x}/here'   | [path: '/foo/bar']              | '/foo/bar/here'
          '{+path,x}/here'   | [path: '/foo/bar']              | '/foo/bar/here'
          '{+path,x,y}/here' | [:]                             | '/here'
    }

    def "test fragment expansion"() {
        when:
          def result = templateExpander.expand(template, values, false)
        then:
          result == expansion
        where:
          template           | values                                   | expansion
          '{#x,hello,y}'     | [:]                                      | '{#x,hello,y}'
          '{#x,hello,y}'     | [x: 1024, hello: 'Hello World!', y: 768] | '#1024,Hello+World%21,768'
          '{#path,x}/here'   | [path: '/foo/bar', x: 1024]              | '#/foo/bar,1024/here'
          '{#path,x}/here'   | [path: '/foo/bar']                       | '#/foo/bar{#x}/here'
          '{#path,x,y}/here' | [path: '/foo/bar']                       | '#/foo/bar{#x,y}/here'
    }

    def "test fragment expansion with unexpanded omitted"() {
        when:
          def result = templateExpander.expand(template, values, true)
        then:
          result == expansion
        where:
          template           | values                           | expansion
          '{#x,hello,y}'     | [:]                              | ''
          '{#x,hello,y}'     | [x: 1024, hello: 'Hello World!'] | '#1024,Hello+World%21'
          '{#path,x}/here'   | [x: 1024]                        | '#1024/here'
          '{#path,x}/here'   | [path: '/foo/bar']               | '#/foo/bar/here'
          '{#path,x,y}/here' | [:]                              | '/here'
    }

    def "test label expansion"() {
        when:
          def result = templateExpander.expand(template, values, false)
        then:
          result == expansion
        where:
          template    | values            | expansion
          'X{.var}'   | [:]               | 'X{.var}'
          'X{.var}'   | [var: 'value']    | 'X.value'
          'X{.x,y}'   | [x: 1024, y: 768] | 'X.1024.768'
          'X{.x,y}'   | [x: 1024]         | 'X.1024{.y}'
          'X{.x,y,z}' | [x: 1024]         | 'X.1024{.y,z}'
    }

    def "test label expansion with unexpanded omitted"() {
        when:
          def result = templateExpander.expand(template, values, true)
        then:
          result == expansion
        where:
          template    | values            | expansion
          'X{.var}'   | [:]               | 'X'
          'X{.x,y}'   | [y: 768]          | 'X.768'
          'X{.x,y}'   | [x: 1024]         | 'X.1024'
          'X{.x,y,z}' | [x: 1024, z: 256] | 'X.1024.256'
    }

    def "test path expansion"() {
        when:
          def result = templateExpander.expand(template, values, false)
        then:
          result == expansion
        where:
          template          | values                  | expansion
          '{/var}'          | [:]                     | '{/var}'
          '{/var}'          | [var: 'value']          | '/value'
          '{/var,x}/here'   | [var: 'value', x: 1024] | '/value/1024/here'
          '{/var,x}/here'   | [var: 'value']          | '/value{/x}/here'
          '{/var,x,y}/here' | [var: 'value']          | '/value{/x,y}/here'
    }

    def "test path expansion with unexpanded omitted"() {
        when:
          def result = templateExpander.expand(template, values, true)
        then:
          result == expansion
        where:
          template          | values            | expansion
          '{/var}'          | [:]               | ''
          '{/var,x}/here'   | [x: 1024]         | '/1024/here'
          '{/var,x}/here'   | [var: 'value']    | '/value/here'
          '{/var,x,y}/here' | [x: 1024, y: 768] | '/1024/768/here'
    }

    def "test path-style parameter expansion"() {
        when:
          def result = templateExpander.expand(template, values, false)
        then:
          result == expansion
        where:
          template       | values                       | expansion
          '{;x,y}'       | [:]                          | '{;x,y}'
          '{;x,y}'       | [x: 1024, y: 768]            | ';x=1024;y=768'
          '{;x,y,empty}' | [x: 1024, y: 768, empty: ''] | ';x=1024;y=768;empty='
          '{;x,y,empty}' | [x: 1024, empty: '']         | ';x=1024;empty={;y}'
          '{;x,y,empty}' | [empty: '']                  | ';empty={;x,y}'
    }

    def "test path-style parameter expansion with unexpanded omitted"() {
        when:
          def result = templateExpander.expand(template, values, true)
        then:
          result == expansion
        where:
          template       | values               | expansion
          '{;x,y}'       | [y: 768]             | ';y=768'
          '{;x,y,empty}' | [x: 1024, empty: ''] | ';x=1024;empty='
          '{;x,y,empty}' | [empty: '']          | ';empty='
          '{;x,y,empty}' | [:]                  | ''
    }

    def "test form-style query expansion"() {
        when:
          def result = templateExpander.expand(template, values, false)
        then:
          result == expansion
        where:
          template       | values                       | expansion
          '{?x,y}'       | [:]                          | '{?x,y}'
          '{?x,y}'       | [x: 1024, y: 768]            | '?x=1024&y=768'
          '{?x,y,empty}' | [x: 1024, y: 768, empty: ''] | '?x=1024&y=768&empty='
          '{?x,y,empty}' | [y: 768, empty: '']          | '?y=768&empty={&x}'
          '{?x,y,empty}' | [empty: '']                  | '?empty={&x,y}'
    }

    def "test form-style query expansion with unexpanded omitted"() {
        when:
          def result = templateExpander.expand(template, values, true)
        then:
          result == expansion
        where:
          template       | values               | expansion
          '{?x,y}'       | [y: 768]             | '?y=768'
          '{?x,y,empty}' | [x: 1024, empty: ''] | '?x=1024&empty='
          '{?x,y,empty}' | [empty: '']          | '?empty='
          '{?x,y,empty}' | [:]                  | ''
    }

    def "test form-style query continuation expansion"() {
        when:
          def result = templateExpander.expand(template, values, false)
        then:
          result == expansion
        where:
          template         | values                       | expansion
          '?fixed=yes{&x}' | [:]                          | '?fixed=yes{&x}'
          '?fixed=yes{&x}' | [x: 1024]                    | '?fixed=yes&x=1024'
          '{&x,y,empty}'   | [x: 1024, y: 768, empty: ''] | '&x=1024&y=768&empty='
          '{&x,y,empty}'   | [x: 1024, empty: '']         | '&x=1024&empty={&y}'
          '{&x,y,empty}'   | [empty: '']                  | '&empty={&x,y}'
    }

    def "test form-style query continuation expansion with unexpanded omitted"() {
        when:
          def result = templateExpander.expand(template, values, true)
        then:
          result == expansion
        where:
          template         | values               | expansion
          '?fixed=yes{&x}' | [:]                  | '?fixed=yes'
          '{&x,y,empty}'   | [x: 1024, empty: ''] | '&x=1024&empty='
          '{&x,y,empty}'   | [x: 1024]            | '&x=1024'
          '{&x,y,empty}'   | [empty: '']          | '&empty='
    }
}
