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

import com.github.codeframes.hal.tooling.link.bindings.CurieDef
import com.github.codeframes.hal.tooling.link.bindings.LinkRel
import com.github.codeframes.hal.tooling.link.bindings.Style
import spock.lang.Ignore
import spock.lang.Specification

@Ignore
class BindingTypeBuilders extends Specification {

    LinkRel newLinkRel(args) {
        def linkRel = Mock(LinkRel) {
            rel() >> { args['rel'] ?: LinkRel.DEFAULT_REL }
            value() >> { args['value'] ?: '' }
            type() >> { args['type'] ?: '' }
            deprecation() >> { args['deprecation'] ?: '' }
            name() >> { args['name'] ?: '' }
            profile() >> { args['profile'] ?: '' }
            title() >> { args['title'] ?: '' }
            hreflang() >> { args['hreflang'] ?: '' }
            resource() >> { args['resource'] ?: LinkRel.UNSPECIFIED.class }
            method() >> { args['method'] ?: '' }
            condition() >> { args['condition'] ?: '' }
            style() >> { args['style'] ?: Style.ABSOLUTE_PATH }
            bindings() >> { args['bindings'] ?: [] }
            bindingOptions() >> { args['bindingOptions'] ?: [] }
        }
        return linkRel
    }

    LinkRelType newLinkRelType(args) {
        return LinkRelType.valueOf(newLinkRel(args))
    }

    CurieDef newCurieDef(args) {
        def curieDef = Mock(CurieDef) {
            name() >> { args['name'] }
            value() >> { args['value'] }
            style() >> { args['style'] ?: Style.ABSOLUTE_PATH }
        }
        return curieDef
    }

    Binding newBinding(args) {
        def binding = Mock(Binding) {
            name() >> { args['name'] }
            value() >> { args['value'] }
        }
        return binding
    }
}
