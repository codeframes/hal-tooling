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
package com.github.codeframes.hal.tooling.link.bindings.types

import com.github.codeframes.hal.tooling.link.bindings.Binding
import com.github.codeframes.hal.tooling.link.bindings.LinkRel
import com.github.codeframes.hal.tooling.link.bindings.Style
import spock.lang.Specification

@SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
class LinkRelTypeTest extends Specification {

    def linkRelBuilders = new BindingTypeBuilders()

    def "test valueOf with minimal options"() {
        when:
          def linkRelType = linkRelBuilders.newLinkRelType(rel: 'rel', value: '/api')
        then:
          with(linkRelType) {
              rel == 'rel'
              href == '/api'
              type == null
              deprecation == null
              name == null
              profile == null
              title == null
              hreflang == null
              curie == null
              resourceMethod == null
              condition == null
              bindings == [:]
              style == Style.ABSOLUTE_PATH
              bindingOptions == [] as Set
          }
    }

    def "test valueOf with all options except resource and method"() {
        when:
          def linkRelType = linkRelBuilders.newLinkRelType(
                  rel: 'curie:rel',
                  value: '/api',
                  type: 'type',
                  deprecation: 'deprecation',
                  name: 'name',
                  profile: 'profile',
                  title: 'title',
                  hreflang: 'hreflang',
                  condition: 'condition',
                  style: Style.ABSOLUTE,
                  bindingOptions: [LinkRel.BindingOption.INSTANCE_PARAMETERS],
                  bindings: Mock(Binding) {
                      name() >> 'name'
                      value() >> 'href'
                  })
        then:
          with(linkRelType) {
              rel == 'curie:rel'
              href == '/api'
              type == 'type'
              deprecation == 'deprecation'
              name == 'name'
              profile == 'profile'
              title == 'title'
              hreflang == 'hreflang'
              curie == 'curie'
              resourceMethod == null
              condition == 'condition'
              bindings == [name: 'href']
              style == Style.ABSOLUTE
              bindingOptions == [LinkRel.BindingOption.INSTANCE_PARAMETERS] as Set
          }
    }

    def "test valueOf with all options except value"() {
        when:
          def linkRelType = linkRelBuilders.newLinkRelType(
                  rel: 'curie:rel',
                  type: 'type',
                  deprecation: 'deprecation',
                  name: 'name',
                  profile: 'profile',
                  title: 'title',
                  hreflang: 'hreflang',
                  resource: Resource,
                  method: 'get',
                  condition: 'condition',
                  style: Style.ABSOLUTE,
                  bindingOptions: [LinkRel.BindingOption.INSTANCE_PARAMETERS],
                  bindings: Mock(Binding) {
                      name() >> 'name'
                      value() >> 'href'
                  })
        then:
          with(linkRelType) {
              rel == 'curie:rel'
              href == null
              type == 'type'
              deprecation == 'deprecation'
              name == 'name'
              profile == 'profile'
              title == 'title'
              hreflang == 'hreflang'
              curie == 'curie'
              resourceMethod.resourceClass == Resource
              resourceMethod.methodName == 'get'
              condition == 'condition'
              bindings == [name: 'href']
              style == Style.ABSOLUTE
              bindingOptions == [LinkRel.BindingOption.INSTANCE_PARAMETERS] as Set
          }
    }

    static class Resource {
        Object get() { return new Object() }

        private Object privateGet() { return new Object() }
    }

    def "test valueOf with method option without corresponding resource option"() {
        when:
          linkRelBuilders.newLinkRelType(
                  rel: 'rel',
                  value: '/api',
                  method: 'get')
        then:
          thrown(IllegalArgumentException)
    }

    def "test valueOf with value and resource option"() {
        when:
          linkRelBuilders.newLinkRelType(
                  rel: 'rel',
                  value: '/api',
                  resource: Resource)
        then:
          thrown(IllegalArgumentException)
    }

    def "test valueOf with inaccessible method option"() {
        when:
          linkRelBuilders.newLinkRelType(
                  rel: 'rel',
                  resource: Resource,
                  method: 'privateGet')
        then:
          thrown(IllegalArgumentException)
        where:
          method << ['privateGet', 'nonexistent']
    }

    def "test valueOf with illegal rel arguments"() {
        when:
          linkRelBuilders.newLinkRelType(rel: rel)
        then:
          thrown(IllegalArgumentException)
        where:
          rel << ['', '  ']
    }

    def "test valueOf with a given method name without a resource value"() {
        when:
          linkRelBuilders.newLinkRelType(rel: 'rel', method: 'method')
        then:
          thrown(IllegalArgumentException)
    }

    def "test valueOf with an illegal value argument"() {
        when:
          linkRelBuilders.newLinkRelType(rel: 'rel', value: '')
        then:
          thrown(IllegalArgumentException)
    }

    def "test valueOf with illegal binding arguments"() {
        when:
          linkRelBuilders.newLinkRelType(
                  rel: 'rel',
                  value: '/api',
                  bindings: Mock(Binding) {
                      name() >> binding_name
                      value() >> binding_value
                  })
        then:
          thrown(IllegalArgumentException)
        where:
          binding_name | binding_value
          ''           | 'href'
          '    '       | 'href'
          'name'       | ''
          'name'       | '    '
    }
}
