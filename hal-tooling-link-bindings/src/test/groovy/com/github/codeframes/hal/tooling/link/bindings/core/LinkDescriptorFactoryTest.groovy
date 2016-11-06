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
package com.github.codeframes.hal.tooling.link.bindings.core

import com.github.codeframes.hal.tooling.link.bindings.*
import com.github.codeframes.hal.tooling.link.bindings.api.LinkTemplateFactory
import com.github.codeframes.hal.tooling.link.bindings.types.BindingTypeBuilders
import spock.lang.Specification

@SuppressWarnings("GroovyUnusedDeclaration")
class LinkDescriptorFactoryTest extends Specification {

    def typeBuilders = new BindingTypeBuilders()

    LinkTemplateFactory mockHrefTemplateFactory

    LinkDescriptorFactory linkDescriptorFactory

    def setup() {
        mockHrefTemplateFactory = Mock(LinkTemplateFactory)

        linkDescriptorFactory = new LinkDescriptorFactory(mockHrefTemplateFactory)
    }

    def "test createLinkDescriptor"() {
        given:
          def linkRel = typeBuilders.newLinkRel(
                  rel: 'doc:rel',
                  value: '/api',
                  type: 'type',
                  deprecation: 'deprecation',
                  name: 'name',
                  profile: 'profile',
                  title: 'title',
                  hreflang: 'hreflang',
                  condition: 'condition',
                  style: Style.RELATIVE_PATH
          )
        when:
          def linkDescriptor = linkDescriptorFactory.createLinkDescriptor(linkRel)
        then:
          1 * mockHrefTemplateFactory.createLinkTemplate(_) >> '/api'
        and:
          with(linkDescriptor) {
              rel == 'doc:rel'
              hrefTemplate == new HrefTemplate('/api', Style.RELATIVE_PATH)
              type == 'type'
              deprecation == 'deprecation'
              name == 'name'
              profile == 'profile'
              title == 'title'
              hreflang == 'hreflang'
              condition == 'condition'
              curie == 'doc'
          }
    }

    def "test createLinkDescriptor with instance parameters binding option"() {
        given:
          def linkRel = typeBuilders.newLinkRel(value: '/api/{id}', bindingOptions: [LinkRel.BindingOption.INSTANCE_PARAMETERS])
        when:
          def linkDescriptor = linkDescriptorFactory.createLinkDescriptor(linkRel)
        then:
          1 * mockHrefTemplateFactory.createLinkTemplate(_) >> '/api/{id}'
        and:
          with(linkDescriptor) {
              rel == 'self'
              hrefTemplate == new HrefTemplate('/api/{id}', Style.ABSOLUTE_PATH, [id: '${instance.id}'], true)
              type == null
              deprecation == null
              name == null
              profile == null
              title == null
              hreflang == null
              condition == null
              curie == null
          }
    }

    def "test createLinkDescriptor with retain unexpanded binding option"() {
        given:
          def linkRel = typeBuilders.newLinkRel(
                  value: '/api/{id}',
                  bindings: [
                          typeBuilders.newBinding([
                                  name : 'id',
                                  value: '${instance.id}'
                          ])
                  ],
                  bindingOptions: [LinkRel.BindingOption.RETAIN_UNEXPANDED])
        when:
          def linkDescriptor = linkDescriptorFactory.createLinkDescriptor(linkRel)
        then:
          1 * mockHrefTemplateFactory.createLinkTemplate(_) >> '/api/{id}'
        and:
          with(linkDescriptor) {
              rel == 'self'
              hrefTemplate == new HrefTemplate('/api/{id}', Style.ABSOLUTE_PATH, [id: '${instance.id}'], false)
              type == null
              deprecation == null
              name == null
              profile == null
              title == null
              hreflang == null
              condition == null
              curie == null
          }
    }

    def "test createLinkDescriptors with empty LinkRels"() {
        given:
          def linkRels = Mock(LinkRels) {
              value() >> []
          }
        when:
          def linkDescriptors = linkDescriptorFactory.createLinkDescriptors(linkRels)
        then:
          linkDescriptors == []
    }

    def "test createLinkDescriptors with single LinkRel"() {
        given:
          def linkRels = Mock(LinkRels) {
              value() >> [
                      typeBuilders.newLinkRel(rel: 'self', value: '/api')
              ]
          }
        when:
          def linkDescriptors = linkDescriptorFactory.createLinkDescriptors(linkRels)
        then:
          1 * mockHrefTemplateFactory.createLinkTemplate(_) >> '/api'
        and:
          linkDescriptors == [
                  LinkDescriptorBuilder.build(
                          rel: 'self',
                          hrefTemplate: new HrefTemplate('/api', Style.ABSOLUTE_PATH)
                  )
          ]
    }

    def "test createLinkDescriptors with multiple LinkRel"() {
        given:
          def linkRels = Mock(LinkRels) {
              value() >> [
                      typeBuilders.newLinkRel(rel: 'a', value: '/api/a'),
                      typeBuilders.newLinkRel(rel: 'b', value: '/api/b')
              ]
          }
        when:
          def linkDescriptors = linkDescriptorFactory.createLinkDescriptors(linkRels)
        then:
          1 * mockHrefTemplateFactory.createLinkTemplate(_) >> '/api/a'
          1 * mockHrefTemplateFactory.createLinkTemplate(_) >> '/api/b'
        and:
          linkDescriptors == [
                  LinkDescriptorBuilder.build(
                          rel: 'a',
                          hrefTemplate: new HrefTemplate('/api/a', Style.ABSOLUTE_PATH)
                  ),
                  LinkDescriptorBuilder.build(
                          rel: 'b',
                          hrefTemplate: new HrefTemplate('/api/b', Style.ABSOLUTE_PATH)
                  )
          ]
    }

    def "test createLinkDescriptors with self rel not defined first"() {
        given:
          def linkRels = Mock(LinkRels) {
              value() >> [
                      typeBuilders.newLinkRel(rel: 'a', value: '/api/a'),
                      typeBuilders.newLinkRel(rel: 'self', value: '/api/b')
              ]
          }
        when:
          linkDescriptorFactory.createLinkDescriptors(linkRels)
        then:
          1 * mockHrefTemplateFactory.createLinkTemplate(_) >> '/api/a'
        and:
          thrown(IllegalArgumentException)
    }

    def "test createCurieDescriptor"() {
        given:
          def curieDef = typeBuilders.newCurieDef(name: 'doc', value: '/docs/{rel}', style: Style.ABSOLUTE_PATH)
        when:
          def curieDescriptor = linkDescriptorFactory.createCurieDescriptor(curieDef)
        then:
          with(curieDescriptor) {
              name == 'doc'
              hrefTemplate == new HrefTemplate('/docs/{rel}', Style.ABSOLUTE_PATH)
          }
    }

    def "test createCurieDescriptors with empty CurieDefs"() {
        given:
          def curieDefs = Mock(CurieDefs) {
              value() >> []
          }
        when:
          def curieDescriptors = linkDescriptorFactory.createCurieDescriptors(curieDefs)
        then:
          curieDescriptors == []
    }

    def "test createCurieDescriptors with single CurieDef"() {
        given:
          def curieDefs = Mock(CurieDefs) {
              value() >> [
                      typeBuilders.newCurieDef(name: 'doc', value: '/docs/{rel}', style: Style.ABSOLUTE)
              ]
          }
        when:
          def curieDescriptors = linkDescriptorFactory.createCurieDescriptors(curieDefs)
        then:
          curieDescriptors == [
                  new CurieDescriptor('doc', new HrefTemplate('/docs/{rel}', Style.ABSOLUTE))
          ]
    }

    def "test createCurieDescriptors with multiple CurieDef"() {
        given:
          def curieDefs = Mock(CurieDefs) {
              value() >> [
                      typeBuilders.newCurieDef(name: 'doca', value: '/docs/a/{rel}', style: Style.ABSOLUTE_PATH),
                      typeBuilders.newCurieDef(name: 'docb', value: '/docs/b/{rel}', style: Style.RELATIVE_PATH)
              ]
          }
        when:
          def curieDescriptors = linkDescriptorFactory.createCurieDescriptors(curieDefs)
        then:
          curieDescriptors == [
                  new CurieDescriptor('doca', new HrefTemplate('/docs/a/{rel}', Style.ABSOLUTE_PATH)),
                  new CurieDescriptor('docb', new HrefTemplate('/docs/b/{rel}', Style.RELATIVE_PATH))
          ]
    }

    def "test createCurieDescriptors for type with no CurieDef's"() {
        when:
          def curieDescriptors = linkDescriptorFactory.createCurieDescriptors(BeanNoCuries)
        then:
          curieDescriptors.isEmpty()
    }

    static class BeanNoCuries {
    }

    def "test createCurieDescriptors for type with CurieDef"() {
        when:
          def curieDescriptors = linkDescriptorFactory.createCurieDescriptors(BeanWithCurieDef)
        then:
          curieDescriptors.size() == 1
          curieDescriptors.get('doc') == new CurieDescriptor('doc', new HrefTemplate('/docs/{rel}', Style.ABSOLUTE_PATH))
    }

    static class BeanWithCurieDef {
        @CurieDef(name = 'doc', value = '/docs/{rel}')
        def curie
    }

    def "test createCurieDescriptors for type with CurieDefs"() {
        when:
          def curieDescriptors = linkDescriptorFactory.createCurieDescriptors(BeanWithCurieDefs)
        then:
          curieDescriptors.size() == 2
          curieDescriptors.get('doca') == new CurieDescriptor('doca', new HrefTemplate('/docs/a/{rel}', Style.ABSOLUTE))
          curieDescriptors.get('docb') == new CurieDescriptor('docb', new HrefTemplate('/docs/b/{rel}', Style.RELATIVE_PATH))
    }

    static class BeanWithCurieDefs {
        @CurieDefs([
                @CurieDef(name = 'doca', value = '/docs/a/{rel}', style = Style.ABSOLUTE),
                @CurieDef(name = 'docb', value = '/docs/b/{rel}', style = Style.RELATIVE_PATH)
        ])
        def curies
    }

    def "test createCurieDescriptors for type with inherited CurieDefs"() {
        when:
          def curieDescriptors = linkDescriptorFactory.createCurieDescriptors(BeanWithInheritedCurieDefs)
        then:
          curieDescriptors.size() == 3
          curieDescriptors.get('doc') == new CurieDescriptor('doc', new HrefTemplate('/docs/{rel}', Style.ABSOLUTE_PATH))
          curieDescriptors.get('doca') == new CurieDescriptor('doca', new HrefTemplate('/docs/a/{rel}', Style.ABSOLUTE))
          curieDescriptors.get('docb') == new CurieDescriptor('docb', new HrefTemplate('/docs/b/{rel}', Style.RELATIVE_PATH))
    }

    static class BeanWithInheritedCurieDefs extends BeanWithCurieDefs {
        @CurieDef(name = 'doc', value = '/docs/{rel}')
        def curie
    }
}
