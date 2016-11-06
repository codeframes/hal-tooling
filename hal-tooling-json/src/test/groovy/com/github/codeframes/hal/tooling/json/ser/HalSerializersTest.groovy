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
package com.github.codeframes.hal.tooling.json.ser

import com.fasterxml.jackson.databind.type.TypeFactory
import com.github.codeframes.hal.tooling.core.Link
import com.github.codeframes.hal.tooling.json.ser.links.LinkSerializer
import spock.lang.Specification

class HalSerializersTest extends Specification {

    static final LINK_TYPE = TypeFactory.defaultInstance().constructType(Link)
    static final STRING_TYPE = TypeFactory.defaultInstance().constructType(String)
    static final LINK_LIST_TYPE = TypeFactory.defaultInstance().constructCollectionType(List, Link)

    def halSerializers = new HalSerializers()

    def "test findSerializer for Link type"() {
        when:
          def serializer = halSerializers.findSerializer(/*config not used*/ null, LINK_TYPE, /*beanDesc not used*/ null)
        then:
          serializer instanceof LinkSerializer
    }

    def "test findSerializer for non Link type"() {
        when:
          def serializer = halSerializers.findSerializer(/*config not used*/ null, type, /*beanDesc not used*/ null)
        then:
          serializer == null
        where:
          type << [STRING_TYPE, LINK_LIST_TYPE]
    }
}
