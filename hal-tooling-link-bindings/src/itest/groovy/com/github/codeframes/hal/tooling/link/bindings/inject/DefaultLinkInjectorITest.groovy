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

import com.github.codeframes.hal.tooling.core.Curie
import com.github.codeframes.hal.tooling.core.Embedded
import com.github.codeframes.hal.tooling.core.HalRepresentable
import com.github.codeframes.hal.tooling.core.Link
import com.github.codeframes.hal.tooling.link.bindings.*
import com.github.codeframes.hal.tooling.link.bindings.api.LinkContextResolver
import com.github.codeframes.hal.tooling.link.bindings.api.LiteralLinkContextResolver
import com.github.codeframes.hal.tooling.link.bindings.uri.UriValueResolver
import spock.lang.Specification

@SuppressWarnings("GroovyUnusedDeclaration")
class DefaultLinkInjectorITest extends Specification {

    LinkContextResolver linkContextResolver
    LinkInjector linkInjector

    def setup() {
        linkContextResolver = new LiteralLinkContextResolver()
        linkInjector = LinkInjector.defaultInstance()
    }

    def "test injectLinks with relative link"() {
        given:
          def bean = new RelativeLinkBean()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.rel == new Link("rel", "/rel")
    }

    static class RelativeLinkBean implements HalRepresentable {

        @LinkRel(rel = "rel", value = "/rel")
        Link rel
    }

    def "test injectLinks with absolute link"() {
        given:
          def bean = new AbsoluteLinkBean()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.abs == new Link("abs", "http://localhost:8080/abs")
    }

    static class AbsoluteLinkBean implements HalRepresentable {

        @LinkRel(rel = "abs", value = "http://localhost:8080/abs")
        Link abs
    }

    def "test injectLinks with absolute links"() {
        given:
          def bean = new AbsoluteLinksBean()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.links == [new Link("abs-1", "http://localhost:8080/abs/1/"),
                         new Link("abs-2", "http://localhost:8080/abs/2/")]
    }

    static class AbsoluteLinksBean implements HalRepresentable {

        @LinkRels([
                @LinkRel(rel = "abs-1", value = "http://localhost:8080/abs/1/"),
                @LinkRel(rel = "abs-2", value = "http://localhost:8080/abs/2/")
        ])
        List<Link> links
    }

    def "test injectLinks with curie"() {
        given:
          def bean = new CuriedLinkBean()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.curie == new Curie("doc", "http://localhost:8080/rels/{rel}")
          bean.abs == new Link("abs", "http://localhost:8080/abs")
    }

    static class CuriedLinkBean implements HalRepresentable {

        @CurieDef(name = "doc", value = "http://localhost:8080/rels/{rel}")
        Curie curie

        @LinkRel(rel = "abs", value = "http://localhost:8080/abs")
        Link abs
    }

    def "test injectLinks with curies"() {
        given:
          def bean = new CuriesBean()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.curies == [new Curie("ns1", "http://localhost:8080/ns1/rels/{rel}"),
                          new Curie("ns2", "http://localhost:8080/ns2/rels/{rel}")]
    }

    static class CuriesBean implements HalRepresentable {

        @CurieDefs([
                @CurieDef(name = "ns1", value = "http://localhost:8080/ns1/rels/{rel}"),
                @CurieDef(name = "ns2", value = "http://localhost:8080/ns2/rels/{rel}")
        ])
        List<Curie> curies
    }

    def "test injectLinks with inherited curies"() {
        given:
          def bean = new CuriesLinkBean()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.curies == [new Curie("ns1", "http://localhost:8080/ns1/rels/{rel}"),
                          new Curie("ns2", "http://localhost:8080/ns2/rels/{rel}")]
          bean.abs == new Link("ns1:abs", "http://localhost:8080/abs")
    }

    static class CuriesLinkBean extends CuriesBean {

        @LinkRel(rel = "ns1:abs", value = "http://localhost:8080/abs")
        Link abs
    }

    def "test injectLinks with embedded resource"() {
        given:
          def bean = new BeanWithEmbeddedResource()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          (bean.embedded.resource as AbsoluteLinksBean).links == [new Link("abs-1", "http://localhost:8080/abs/1/"),
                                                                  new Link("abs-2", "http://localhost:8080/abs/2/")]
    }

    static class BeanWithEmbeddedResource implements HalRepresentable {

        Embedded embedded = new Embedded("resource", new AbsoluteLinksBean())
    }

    def "test injectLinks with null entity"() {
        when:
          linkInjector.injectLinks(null, linkContextResolver)
        then:
          noExceptionThrown()
    }

    def "test injectLinks with embedded resource containing null entity"() {
        given:
          def bean = new BeanWithNullEmbeddedResource()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.embedded.resource == null
    }

    static class BeanWithNullEmbeddedResource implements HalRepresentable {

        Embedded embedded = new Embedded("resource", null)
    }

    def "test injectLinks with embedded resource containing list of entities"() {
        given:
          def bean = new BeanWithEmbeddedResources()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          List<AbsoluteLinksBean> embeddedResources = bean.embedded.resource as List<AbsoluteLinksBean>
          embeddedResources[0].links == [new Link("abs-1", "http://localhost:8080/abs/1/"),
                                         new Link("abs-2", "http://localhost:8080/abs/2/")]
          embeddedResources[1].links == [new Link("abs-1", "http://localhost:8080/abs/1/"),
                                         new Link("abs-2", "http://localhost:8080/abs/2/")]
    }

    static class BeanWithEmbeddedResources implements HalRepresentable {

        Embedded embedded = new Embedded("resource", [new AbsoluteLinksBean(), new AbsoluteLinksBean()])
    }

    def "test injectLinks with embedded EL expression"() {
        given:
          def bean = new BeanWithEmbeddedELExpressions()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.link_1 == new Link("basic-embedded-el-expression", "/api/123")
          bean.link_2 == new Link("advanced-embedded-el-expression", "/api/456")
    }

    static class BeanWithEmbeddedELExpressions implements HalRepresentable {

        @LinkRel(rel = "basic-embedded-el-expression", value = '/api/${instance.id}')
        Link link_1

        @LinkRel(rel = "advanced-embedded-el-expression", value = '/api/${instance.id == "123" ? "456" : ""}')
        Link link_2

        String id = '123'
    }

    def "test injectLinks with bound EL expression"() {
        given:
          def bean = new BeanWithBoundELExpressions()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.link_1 == new Link("el-expression-one", "/api/123")
          bean.link_2 == new Link("el-expression-two", "/api/456")
    }

    static class BeanWithBoundELExpressions implements HalRepresentable {

        @LinkRel(rel = "el-expression-one", value = "/api/{id}", bindings = [@Binding(name = "id", value = '${instance.id}')])
        Link link_1

        @LinkRel(rel = "el-expression-two", value = "/api{/id}", bindings = [@Binding(name = "id", value = '${instance.id == "123" ? "456" : ""}')])
        Link link_2

        String id = "123"
    }

    def "test injectLinks with instance parameters binding option"() {
        given:
          def bean = new BeanWithInstanceParametersBindingOption()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.links == [new Link("binding-inst-param", "/api/orders/123/items/"),
                         new Link("binding-retain-unexpanded", "/api/orders/123/items/{itemId}")]
    }

    static class BeanWithInstanceParametersBindingOption implements HalRepresentable {

        @LinkRels([
                @LinkRel(rel = "binding-inst-param", value = '/api/orders/{orderId}/items/{itemId}',
                        bindingOptions = LinkRel.BindingOption.INSTANCE_PARAMETERS),
                @LinkRel(rel = "binding-retain-unexpanded", value = "/api/orders/{orderId}/items/{itemId}",
                        bindingOptions = [LinkRel.BindingOption.INSTANCE_PARAMETERS, LinkRel.BindingOption.RETAIN_UNEXPANDED])
        ])
        List<Link> links

        String orderId = '123'
    }

    def "test injectLinks with instance parameters snake case binding option"() {
        given:
          def bean = new BeanWithInstanceParametersSnakeCaseBindingOption()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.links == [new Link("binding-inst-param", "/api/orders/123/items/"),
                         new Link("binding-retain-unexpanded", "/api/orders/123/items/{item_id}")]
    }

    static class BeanWithInstanceParametersSnakeCaseBindingOption implements HalRepresentable {

        @LinkRels([
                @LinkRel(rel = "binding-inst-param", value = "/api/orders/{order_id}/items/{item_id}",
                        bindingOptions = LinkRel.BindingOption.INSTANCE_PARAMETERS_SNAKE_CASE)
                ,
                @LinkRel(rel = "binding-retain-unexpanded", value = "/api/orders/{order_id}/items/{item_id}",
                        bindingOptions = [LinkRel.BindingOption.INSTANCE_PARAMETERS_SNAKE_CASE, LinkRel.BindingOption.RETAIN_UNEXPANDED])
        ])
        List<Link> links

        String orderId = '123'
    }

    def "test injectLinks with UriValueResolver using instance parameters binding option"() {
        given:
          def linkInjector = LinkInjector.instanceBuilder().uriValueResolvers([new DateUriValueResolver()]).build()
          def bean = new BeanRequiringUriValueResolverUsingInstParams()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.link == new Link("self", "/api/date/1970-01-01T00:00:00Z")
    }

    static class BeanRequiringUriValueResolverUsingInstParams implements HalRepresentable {

        @LinkRel(value = '/api/date/{date}', bindingOptions = [LinkRel.BindingOption.INSTANCE_PARAMETERS])
        Link link

        Date date = new Date(0)
    }

    def "test injectLinks with UriValueResolver with bindings"() {
        given:
          def linkInjector = LinkInjector.instanceBuilder().uriValueResolvers([new DateUriValueResolver()]).build()
          def bean = new BeanRequiringUriValueResolverUsingBindings()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.link == new Link("self", "/api/date/1970-01-01T00:00:00Z")
    }

    static class BeanRequiringUriValueResolverUsingBindings implements HalRepresentable {

        @LinkRel(value = '/api/date/{date}', bindings = [@Binding(name = 'date', value = '${instance.date}')])
        Link link

        Date date = new Date(0)
    }

    def "test injectLinks with bindings override"() {
        given:
          def linkInjector = LinkInjector.instanceBuilder().uriValueResolvers([new DateUriValueResolver()]).build()
          def bean = new BeanWithBindingOverride()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.link == new Link("self", "/api/date/override")
    }

    static class BeanWithBindingOverride implements HalRepresentable {

        @LinkRel(value = '/api/date/{date}', bindings = [@Binding(name = 'date', value = 'override')])
        Link link
    }

    def "test injectLinks with instance parameters binding option bindings override"() {
        given:
          def linkInjector = LinkInjector.instanceBuilder().uriValueResolvers([new DateUriValueResolver()]).build()
          def bean = new BeanWithInstParamsBindingOverride()
        when:
          linkInjector.injectLinks(bean, linkContextResolver)
        then:
          bean.link == new Link("self", "/api/date/override")
    }

    static class BeanWithInstParamsBindingOverride implements HalRepresentable {

        @LinkRel(value = '/api/date/{date}', bindingOptions = [LinkRel.BindingOption.INSTANCE_PARAMETERS], bindings = [@Binding(name = 'date', value = 'override')])
        Link link

        Date date = new Date(0)
    }

    static class DateUriValueResolver implements UriValueResolver<Date> {

        @Override
        String resolve(Date value) {
            return value.format("yyyy-MM-dd'T'HH:mm:ssX", TimeZone.getTimeZone('UTC'))
        }

        @Override
        Class<Date> getType() {
            return Date.class
        }
    }
}