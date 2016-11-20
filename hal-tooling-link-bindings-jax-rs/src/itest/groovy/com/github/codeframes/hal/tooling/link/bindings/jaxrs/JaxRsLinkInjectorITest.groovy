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
package com.github.codeframes.hal.tooling.link.bindings.jaxrs

import com.github.codeframes.hal.tooling.core.Curie
import com.github.codeframes.hal.tooling.core.HalRepresentable
import com.github.codeframes.hal.tooling.core.Link
import com.github.codeframes.hal.tooling.link.bindings.CurieDef
import com.github.codeframes.hal.tooling.link.bindings.CurieDefs
import com.github.codeframes.hal.tooling.link.bindings.LinkRel
import com.github.codeframes.hal.tooling.link.bindings.api.LinkContextResolver
import com.github.codeframes.hal.tooling.link.bindings.api.LiteralLinkContextResolver
import com.github.codeframes.hal.tooling.link.bindings.inject.LinkInjector
import com.github.codeframes.hal.tooling.link.bindings.jaxrs.context.JaxRsLinkELContext
import com.github.codeframes.hal.tooling.link.bindings.jaxrs.context.UriParameters
import spock.lang.Specification

import javax.ws.rs.*
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

class JaxRsLinkInjectorITest extends Specification {

    LinkContextResolver linkContextResolver
    LinkInjector linkInjector

    def setup() {
        linkContextResolver = new LiteralLinkContextResolver()
        linkInjector = LinkInjector.instanceBuilder()
                .linkTemplateFactory(new JaxRsLinkTemplateFactory())
                .build()
    }

    def "test injectLinks"() {
        given:
          def representation = new Representation()
        when:
          linkInjector.injectLinks(representation, linkContextResolver)
        then:
          representation.itemsLink == new Link('items', '/api/items/')
          representation.itemLink == new Link('item', '/api/items/get/{id}')
          representation.createItemLink == new Link('create-item', '/api/items/')
          representation.updateItemLink == new Link('update-item', '/api/items/put/{id}')
          representation.removeItemLink == new Link('remove-item', '/api/items/delete/{id}')
    }

    @Path("/api/items/")
    static interface Resource {

        @GET
        Response get()

        @GET
        @Path("/get/{id}")
        Response getItem()

        @POST
        Response addItem()

        @PUT
        @Path("/put/{id}")
        Response setItem()

        @DELETE
        @Path("/delete/{id}")
        Response deleteItem()
    }

    static class Representation implements HalRepresentable {

        @LinkRel(rel = "items", resource = Resource.class, method = "get")
        Link itemsLink

        @LinkRel(rel = "item", resource = Resource.class, method = "getItem")
        Link itemLink

        @LinkRel(rel = "create-item", resource = Resource.class, method = "addItem")
        Link createItemLink

        @LinkRel(rel = "update-item", resource = Resource.class, method = "setItem")
        Link updateItemLink

        @LinkRel(rel = "remove-item", resource = Resource.class, method = "deleteItem")
        Link removeItemLink
    }

    def "test injectLinks with curies"() {
        given:
          def curiedRepresentation = new CuriedRepresentation()
        when:
          linkInjector.injectLinks(curiedRepresentation, linkContextResolver)
        then:
          curiedRepresentation.curies == [new Curie("ex", "http://example.com/docs/rels/{rel}")]
          curiedRepresentation.selfLink == new Link('self', '/')
    }

    @Path("/")
    static interface BasicResource {

        @GET
        Response get()
    }

    static class CuriedRepresentation implements HalRepresentable {

        @CurieDefs([
                @CurieDef(name = "ex", value = "http://example.com/docs/rels/{rel}")
        ])
        List<Curie> curies

        @LinkRel(resource = BasicResource.class)
        Link selfLink
    }

    def "test injectLinks with uri parameters binding option"() {
        given:
          def mockUriInfo = Mock(UriInfo) {
              getPathParameters() >> new MultivaluedHashMap([date: '2017-01-23T00:00:00.000Z'])
              getQueryParameters() >> new MultivaluedHashMap([:])
          }
          def uriParametersBindingOptionRepresentation = new UriParametersBindingOptionRepresentation()
          def linkELContext = new JaxRsLinkELContext(uriParametersBindingOptionRepresentation, new UriParameters(mockUriInfo))
        when:
          linkInjector.injectLinks(uriParametersBindingOptionRepresentation, linkContextResolver, linkELContext)
        then:
          uriParametersBindingOptionRepresentation.link == new Link("self", "/api/date/2017-01-23T00:00:00.000Z")
    }

    static class UriParametersBindingOptionRepresentation implements HalRepresentable {

        @LinkRel(value = '/api/date/{date}', bindingOptions = [LinkRel.BindingOption.URI_PARAMETERS])
        Link link
    }
}