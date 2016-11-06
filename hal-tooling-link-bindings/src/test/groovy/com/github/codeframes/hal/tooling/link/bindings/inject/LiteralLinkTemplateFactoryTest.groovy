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

import com.github.codeframes.hal.tooling.link.bindings.api.LiteralLinkTemplateFactory
import com.github.codeframes.hal.tooling.link.bindings.types.BindingTypeBuilders
import spock.lang.Specification

class LiteralLinkTemplateFactoryTest extends Specification {

    def linkRelBuilders = new BindingTypeBuilders()

    def "test createLinkTemplate"() {
        given:
          def linkRelType = linkRelBuilders.newLinkRelType(value: 'LiteralValue')
          def factory = new LiteralLinkTemplateFactory()
        when:
          def linkTemplate = factory.createLinkTemplate(linkRelType)
        then:
          linkTemplate == 'LiteralValue'
    }
}
