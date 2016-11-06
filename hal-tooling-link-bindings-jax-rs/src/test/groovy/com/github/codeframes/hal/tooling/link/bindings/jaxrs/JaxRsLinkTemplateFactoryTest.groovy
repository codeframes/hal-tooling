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

import com.github.codeframes.hal.tooling.link.bindings.types.BindingTypeBuilders
import spock.lang.Specification

import javax.ws.rs.*
import javax.ws.rs.core.Response

class JaxRsLinkTemplateFactoryTest extends Specification {

    def linkRelBuilders = new BindingTypeBuilders()

    JaxRsLinkTemplateFactory factory

    def setup() {
        factory = new JaxRsLinkTemplateFactory()
    }

    def "test createLinkTemplate with absolute value"() {
        given:
          def linkRelType = linkRelBuilders.newLinkRelType(value: 'http://localhost:8080/api/')
        when:
          def linkTemplate = factory.createLinkTemplate(linkRelType)
        then:
          linkTemplate == 'http://localhost:8080/api/'
    }

    def "test createLinkTemplate with relative value"() {
        given:
          def linkRelType = linkRelBuilders.newLinkRelType(value: '/api/')
        when:
          def linkTemplate = factory.createLinkTemplate(linkRelType)
        then:
          linkTemplate == '/api/'
    }

    def "test createLinkTemplate with resource"() {
        given:
          def linkRelType = linkRelBuilders.newLinkRelType(resource: Resource)
        when:
          def linkTemplate = factory.createLinkTemplate(linkRelType)
        then:
          linkTemplate == '/api/items/'
    }

    def "test createLinkTemplate with non root resource"() {
        given:
          def linkRelType = linkRelBuilders.newLinkRelType(resource: NonRootResource)
        when:
          factory.createLinkTemplate(linkRelType)
        then:
          thrown(UnsupportedOperationException)
    }

    def "test createLinkTemplate with no param resource method"() {
        given:
          def linkRelType = linkRelBuilders.newLinkRelType(resource: Resource, method: 'post')
        when:
          def linkTemplate = factory.createLinkTemplate(linkRelType)
        then:
          linkTemplate == '/api/items/'
    }

    def "test createLinkTemplate with @PathParam resource method"() {
        given:
          def linkRelType = linkRelBuilders.newLinkRelType(resource: Resource, method: 'getById')
        when:
          def linkTemplate = factory.createLinkTemplate(linkRelType)
        then:
          linkTemplate == '/api/items/{id}'
    }

    def "test createLinkTemplate with @PathParam resource method using regular expressions"() {
        given:
          def linkRelType = linkRelBuilders.newLinkRelType(resource: Resource, method: 'getByDate')
        when:
          def linkTemplate = factory.createLinkTemplate(linkRelType)
        then:
          linkTemplate == '/api/items/{date}'
    }

    def "test createLinkTemplate with @QueryParam resource method"() {
        given:
          def linkRelType = linkRelBuilders.newLinkRelType(resource: Resource, method: 'find')
        when:
          def linkTemplate = factory.createLinkTemplate(linkRelType)
        then:
          linkTemplate == '/api/items/{?ids}'
    }

    @Path('/api/items/')
    static interface Resource {
        @GET
        @Path('{id}')
        Response getById(@PathParam('id') String id)

        @GET
        @Path('{date:\\d{4}-\\d{2}-\\d{2}}')
        Response getByDate(@PathParam('date') String date)

        @POST
        Response post()

        @GET
        Response find(@QueryParam('ids') List<String> ids)
    }

    static interface NonRootResource {
    }
}
