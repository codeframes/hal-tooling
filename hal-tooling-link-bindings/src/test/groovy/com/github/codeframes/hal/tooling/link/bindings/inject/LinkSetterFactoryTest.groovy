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
package com.github.codeframes.hal.tooling.link.bindings.inject

import com.github.codeframes.hal.tooling.core.*
import com.github.codeframes.hal.tooling.link.bindings.CurieDef
import com.github.codeframes.hal.tooling.link.bindings.CurieDefs
import com.github.codeframes.hal.tooling.link.bindings.LinkRel
import com.github.codeframes.hal.tooling.link.bindings.LinkRels
import com.github.codeframes.hal.tooling.link.bindings.api.LiteralLinkTemplateFactory
import com.github.codeframes.hal.tooling.link.bindings.core.CurieDescriptors
import spock.lang.Specification

@SuppressWarnings(["GroovyAccessibility", "GroovyUnusedDeclaration"])
class LinkSetterFactoryTest extends Specification {

    LinkSetterFactory linkSetterFactory

    def setup() {
        linkSetterFactory = LinkSetterFactory.newInstance(new LiteralLinkTemplateFactory())
    }

    def "test getBeanLinkSetter for bean with no HAL field types"() {
        when:
          def beanLinkSetter = linkSetterFactory.getBeanLinkSetter(BeanWithNoHalFieldTypes)
        then:
          beanLinkSetter == LinkSetterFactory.NO_OP_BEAN_LINK_SETTER
    }

    def "test getElementBeanLinkSetter for bean with no HAL field types"() {
        given:
          def curieDescriptors = new CurieDescriptors()
        when:
          def beanLinkSetter = linkSetterFactory.getElementBeanLinkSetter(BeanWithNoHalFieldTypes, curieDescriptors)
        then:
          beanLinkSetter == LinkSetterFactory.NO_OP_LINK_SETTER
    }

    static class BeanWithNoHalFieldTypes implements HalRepresentable {
    }

    def "test getBeanLinkSetter for bean with Curie field type"() {
        when:
          def beanLinkSetter = linkSetterFactory.getBeanLinkSetter(BeanWithCurieFieldType)
        then:
          beanLinkSetter instanceof RootBeanLinkSetter
        and:
          with(beanLinkSetter, RootBeanLinkSetter) {
              curieDescriptors.size() == 1
              linkSetters.size() == 1
              linkSetters.find { it instanceof CurieFieldSetter }
          }
    }

    def "test getElementBeanLinkSetter for bean with Curie field type"() {
        given:
          def curieDescriptors = new CurieDescriptors()
        when:
          def beanLinkSetter = linkSetterFactory.getElementBeanLinkSetter(BeanWithCurieFieldType, curieDescriptors)
        then:
          beanLinkSetter == LinkSetterFactory.NO_OP_LINK_SETTER
    }

    static class BeanWithCurieFieldType implements HalRepresentable {
        @CurieDef(name = 'doc', value = '/docs/{rel}')
        Curie curie
    }

    def "test getBeanLinkSetter for bean with invalid use of @CurieDef"() {
        when:
          linkSetterFactory.getBeanLinkSetter(BeanWithInvalidCurieDefAnnotationUse)
        then:
          thrown(IllegalArgumentException)
    }

    static class BeanWithInvalidCurieDefAnnotationUse implements HalRepresentable {
        @CurieDef(name = 'doc', value = '/docs/{rel}')
        String curie
    }

    def "test getBeanLinkSetter for bean with List of Curie field type"() {
        when:
          def beanLinkSetter = linkSetterFactory.getBeanLinkSetter(BeanWithListOfCurieFieldType)
        then:
          beanLinkSetter instanceof RootBeanLinkSetter
        and:
          with(beanLinkSetter, RootBeanLinkSetter) {
              curieDescriptors.size() == 2
              linkSetters.size() == 1
              linkSetters.find { it instanceof CurieListFieldSetter }
          }
    }

    def "test getElementBeanLinkSetter for bean with List of Curie field type"() {
        given:
          def curieDescriptors = new CurieDescriptors()
        when:
          def beanLinkSetter = linkSetterFactory.getElementBeanLinkSetter(BeanWithListOfCurieFieldType, curieDescriptors)
        then:
          beanLinkSetter == LinkSetterFactory.NO_OP_LINK_SETTER
    }

    static class BeanWithListOfCurieFieldType implements HalRepresentable {
        @CurieDefs([
                @CurieDef(name = 'doc1', value = '/docs/1/{rel}'),
                @CurieDef(name = 'doc2', value = '/docs/2/{rel}')
        ])
        List<Curie> curies
    }

    def "test getBeanLinkSetter for bean with invalid use of @CurieDefs"() {
        when:
          linkSetterFactory.getBeanLinkSetter(BeanWithInvalidCurieDefsAnnotationUse)
        then:
          thrown(IllegalArgumentException)
    }

    static class BeanWithInvalidCurieDefsAnnotationUse implements HalRepresentable {
        @CurieDefs([
                @CurieDef(name = 'doc1', value = '/docs/1/{rel}'),
                @CurieDef(name = 'doc2', value = '/docs/2/{rel}')
        ])
        Set<Curie> curies
    }

    def "test getBeanLinkSetter for bean with Link field type"() {
        when:
          def beanLinkSetter = linkSetterFactory.getBeanLinkSetter(BeanWithLinkFieldType)
        then:
          beanLinkSetter instanceof RootBeanLinkSetter
        and:
          with(beanLinkSetter, RootBeanLinkSetter) {
              curieDescriptors.isEmpty()
              linkSetters.size() == 1
              linkSetters.find { it instanceof LinkFieldSetter }
          }
    }

    def "test getElementBeanLinkSetter for bean with Link field type"() {
        given:
          def curieDescriptors = new CurieDescriptors()
        when:
          def beanLinkSetter = linkSetterFactory.getElementBeanLinkSetter(BeanWithLinkFieldType, curieDescriptors)
        then:
          beanLinkSetter instanceof EmbeddedBeanLinkSetter
        and:
          with(beanLinkSetter, EmbeddedBeanLinkSetter) {
              linkSetters.size() == 1
              linkSetters.find { it instanceof LinkFieldSetter }
          }
    }

    static class BeanWithLinkFieldType implements HalRepresentable {
        @LinkRel('/api/')
        Link link
    }

    def "test getBeanLinkSetter for bean with overloaded Link field type"() {
        when:
          def beanLinkSetter = linkSetterFactory.getBeanLinkSetter(BeanWithOverloadedLinkFieldType)
        then:
          beanLinkSetter instanceof RootBeanLinkSetter
        and:
          with(beanLinkSetter, RootBeanLinkSetter) {
              curieDescriptors.isEmpty()
              linkSetters.size() == 1
              linkSetters.find { it instanceof LinkFieldSetter }
          }
    }

    static class BeanWithOverloadedLinkFieldType extends BeanWithLinkFieldType {
        @LinkRel('/api/overloaded')
        Link link
    }

    def "test getBeanLinkSetter for bean with invalid use of @LinkRel"() {
        when:
          linkSetterFactory.getBeanLinkSetter(BeanWithInvalidLinkRelAnnotationUse)
        then:
          thrown(IllegalArgumentException)
    }

    static class BeanWithInvalidLinkRelAnnotationUse implements HalRepresentable {
        @LinkRel('/api/')
        String link
    }

    def "test getBeanLinkSetter for bean with LinkRel and no matching curie"() {
        when:
          linkSetterFactory.getBeanLinkSetter(BeanWithLinkRelAndNoMatchingCurie)
        then:
          thrown(IllegalArgumentException)
    }

    static class BeanWithLinkRelAndNoMatchingCurie implements HalRepresentable {
        @LinkRel(rel = 'doc:a', value = '/api/')
        Link link
    }

    def "test getBeanLinkSetter for bean with List of Link field type"() {
        when:
          def beanLinkSetter = linkSetterFactory.getBeanLinkSetter(BeanWithListOfLinkFieldType)
        then:
          beanLinkSetter instanceof RootBeanLinkSetter
        and:
          with(beanLinkSetter, RootBeanLinkSetter) {
              curieDescriptors.isEmpty()
              linkSetters.size() == 1
              linkSetters.find { it instanceof LinkListFieldSetter }
          }
    }

    def "test getElementBeanLinkSetter for bean with List of Link field type"() {
        given:
          def curieDescriptors = new CurieDescriptors()
        when:
          def beanLinkSetter = linkSetterFactory.getElementBeanLinkSetter(BeanWithListOfLinkFieldType, curieDescriptors)
        then:
          beanLinkSetter instanceof EmbeddedBeanLinkSetter
        and:
          with(beanLinkSetter, EmbeddedBeanLinkSetter) {
              linkSetters.size() == 1
              linkSetters.find { it instanceof LinkListFieldSetter }
          }
    }

    static class BeanWithListOfLinkFieldType implements HalRepresentable {
        @LinkRels([
                @LinkRel(rel = 'a', value = '/api/a'),
                @LinkRel(rel = 'b', value = '/api/b')
        ])
        List<Link> links
    }

    def "test getBeanLinkSetter for bean with invalid use of @LinkRels"() {
        when:
          linkSetterFactory.getBeanLinkSetter(BeanWithInvalidLinkRelsAnnotationUse)
        then:
          thrown(IllegalArgumentException)
    }

    static class BeanWithInvalidLinkRelsAnnotationUse implements HalRepresentable {
        @LinkRels([
                @LinkRel(rel = 'a', value = '/api/a'),
                @LinkRel(rel = 'b', value = '/api/b')
        ])
        Set<Link> links
    }

    def "test getBeanLinkSetter for bean with duplicate rels"() {
        when:
          linkSetterFactory.getBeanLinkSetter(BeanWithDuplicateRels)
        then:
          thrown(IllegalArgumentException)
    }

    static class BeanWithDuplicateRels implements HalRepresentable {
        @LinkRel(rel = 'a', value = '/api/a')
        Link link_1

        @LinkRel(rel = 'a', value = '/api/b')
        Link link_2
    }

    def "test getBeanLinkSetter for bean with duplicate rels declared with @LinkRels"() {
        when:
          linkSetterFactory.getBeanLinkSetter(BeanWithDuplicateRelsInsideLinkRels)
        then:
          thrown(IllegalArgumentException)
    }

    static class BeanWithDuplicateRelsInsideLinkRels implements HalRepresentable {
        @LinkRels([
                @LinkRel(rel = 'a', value = '/api/a'),
                @LinkRel(rel = 'a', value = '/api/b')
        ])
        List<Link> links
    }

    def "test getBeanLinkSetter for bean with Embeddable field type"() {
        when:
          def beanLinkSetter = linkSetterFactory.getBeanLinkSetter(BeanWithEmbeddableFieldType)
        then:
          beanLinkSetter instanceof RootBeanLinkSetter
        and:
          with(beanLinkSetter, RootBeanLinkSetter) {
              curieDescriptors.isEmpty()
              linkSetters.size() == 1
              linkSetters.find { it instanceof EmbeddableFieldLinkSetter }
          }
    }

    def "test getElementBeanLinkSetter for bean with Embeddable field type"() {
        given:
          def curieDescriptors = new CurieDescriptors()
        when:
          def beanLinkSetter = linkSetterFactory.getElementBeanLinkSetter(BeanWithEmbeddableFieldType, curieDescriptors)
        then:
          beanLinkSetter instanceof EmbeddedBeanLinkSetter
        and:
          with(beanLinkSetter, EmbeddedBeanLinkSetter) {
              linkSetters.size() == 1
              linkSetters.find { it instanceof EmbeddableFieldLinkSetter }
          }
    }

    static class BeanWithEmbeddableFieldType implements HalRepresentable {
        Embeddable embeddable
    }

    def "test getBeanLinkSetter for bean with Embedded field type"() {
        when:
          def beanLinkSetter = linkSetterFactory.getBeanLinkSetter(BeanWithEmbeddedFieldType)
        then:
          beanLinkSetter instanceof RootBeanLinkSetter
        and:
          with(beanLinkSetter, RootBeanLinkSetter) {
              curieDescriptors.isEmpty()
              linkSetters.size() == 1
              linkSetters.find { it instanceof EmbeddedFieldLinkSetter }
          }
    }

    def "test getElementBeanLinkSetter for bean with Embedded field type"() {
        given:
          def curieDescriptors = new CurieDescriptors()
        when:
          def beanLinkSetter = linkSetterFactory.getElementBeanLinkSetter(BeanWithEmbeddedFieldType, curieDescriptors)
        then:
          beanLinkSetter instanceof EmbeddedBeanLinkSetter
        and:
          with(beanLinkSetter, EmbeddedBeanLinkSetter) {
              linkSetters.size() == 1
              linkSetters.find { it instanceof EmbeddedFieldLinkSetter }
          }
    }

    static class BeanWithEmbeddedFieldType implements HalRepresentable {
        Embedded embedded
    }

    def "test getBeanLinkSetter for bean with multiple curie and link field types"() {
        when:
          def beanLinkSetter = linkSetterFactory.getBeanLinkSetter(BeanWithMultipleCurieAndLinkFieldTypes)
        then:
          beanLinkSetter instanceof RootBeanLinkSetter
        and:
          with(beanLinkSetter, RootBeanLinkSetter) {
              curieDescriptors.size() == 2
              linkSetters.size() == 4
              linkSetters.find { it instanceof CurieFieldSetter }
              linkSetters.find { it instanceof LinkFieldSetter }
          }
    }

    def "test getElementBeanLinkSetter for bean with multiple curie and link field types"() {
        given:
          def curieDescriptors = new CurieDescriptors()
        when:
          def beanLinkSetter = linkSetterFactory.getElementBeanLinkSetter(BeanWithMultipleCurieAndLinkFieldTypes, curieDescriptors)
        then:
          beanLinkSetter instanceof EmbeddedBeanLinkSetter
        and:
          with(beanLinkSetter, EmbeddedBeanLinkSetter) {
              linkSetters.size() == 2
              linkSetters.find { it instanceof LinkFieldSetter }
          }
    }

    static class BeanWithMultipleCurieAndLinkFieldTypes implements HalRepresentable {
        @CurieDef(name = 'doca', value = '/docs/a/{rel}')
        Curie curie_a

        @CurieDef(name = 'docb', value = '/docs/b/{rel}')
        Curie curie_b

        @LinkRel(rel = '1', value = '/api/1/')
        Link link_1

        @LinkRel(rel = '2', value = '/api/2/')
        Link link_2
    }

    def "test getBeanLinkSetter for bean with all HAL field types"() {
        when:
          def beanLinkSetter = linkSetterFactory.getBeanLinkSetter(BeanWithAllHalFieldTypes)
        then:
          beanLinkSetter instanceof RootBeanLinkSetter
        and:
          with(beanLinkSetter, RootBeanLinkSetter) {
              curieDescriptors.size() == 3
              linkSetters.size() == 6
              linkSetters.find { it instanceof CurieFieldSetter }
              linkSetters.find { it instanceof CurieListFieldSetter }
              linkSetters.find { it instanceof LinkFieldSetter }
              linkSetters.find { it instanceof LinkListFieldSetter }
              linkSetters.find { it instanceof EmbeddableFieldLinkSetter }
              linkSetters.find { it instanceof EmbeddedFieldLinkSetter }
          }
    }

    def "test getElementBeanLinkSetter for bean with all HAL field types"() {
        given:
          def curieDescriptors = new CurieDescriptors()
        when:
          def beanLinkSetter = linkSetterFactory.getElementBeanLinkSetter(BeanWithAllHalFieldTypes, curieDescriptors)
        then:
          beanLinkSetter instanceof EmbeddedBeanLinkSetter
        and:
          with(beanLinkSetter, EmbeddedBeanLinkSetter) {
              linkSetters.size() == 4
              linkSetters.find { it instanceof LinkFieldSetter }
              linkSetters.find { it instanceof LinkListFieldSetter }
              linkSetters.find { it instanceof EmbeddableFieldLinkSetter }
              linkSetters.find { it instanceof EmbeddedFieldLinkSetter }
          }
    }

    static class BeanWithAllHalFieldTypes implements HalRepresentable {
        @CurieDef(name = 'doca', value = '/docs/a/{rel}')
        Curie curie_a

        @CurieDefs([
                @CurieDef(name = 'docb', value = '/docs/b/{rel}'),
                @CurieDef(name = 'docc', value = '/docs/c/{rel}')
        ])
        List<Curie> curies

        @LinkRel(rel = '1', value = '/api/1/')
        Link link_1

        @LinkRels([
                @LinkRel(rel = '2', value = '/api/2/'),
                @LinkRel(rel = '3', value = '/api/3/')
        ])
        List<Link> links

        Embeddable embeddable

        Embedded embedded
    }

    def "test getBeanLinkSetter for bean with static HAL field types"() {
        when:
          def beanLinkSetter = linkSetterFactory.getBeanLinkSetter(BeanWithStaticHalFieldTypes)
        then:
          beanLinkSetter == LinkSetterFactory.NO_OP_BEAN_LINK_SETTER
    }

    static class BeanWithStaticHalFieldTypes implements HalRepresentable {
        @CurieDef(name = 'doca', value = '/docs/a/{rel}')
        static Curie curie_a

        @CurieDefs([
                @CurieDef(name = 'docb', value = '/docs/b/{rel}'),
                @CurieDef(name = 'docc', value = '/docs/c/{rel}')
        ])
        static List<Curie> curies

        @LinkRel(rel = '1', value = '/api/1/')
        static Link link_1

        @LinkRels([
                @LinkRel(rel = '2', value = '/api/2/'),
                @LinkRel(rel = '3', value = '/api/3/')
        ])
        static List<Link> links

        static Embeddable embeddable

        static Embedded embedded
    }
}
