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
package com.github.codeframes.hal.tooling.test.http

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovyx.net.http.ParserRegistry
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.codehaus.groovy.runtime.MethodClosure
import org.json.simple.parser.ContainerFactory
import org.json.simple.parser.JSONParser

@TypeChecked
@CompileStatic
class HttpClientParserRegistry extends ParserRegistry {

    protected final Closure jsonParser = new MethodClosure(this, 'parseJSON')
    protected final Closure streamParser = new MethodClosure(this, 'parseStream')
    protected final Closure textParser = new MethodClosure(this, 'parseText')
    protected final Closure formParser = new MethodClosure(this, 'parseForm')
    protected final Closure htmlParser = new MethodClosure(this, 'parseHTML')
    protected final Closure xmlParser = new MethodClosure(this, "parseXML")

    protected final ContainerFactory containerFactory = new ContainerFactory() {

        @Override
        List creatArrayContainer() {
            return new ArrayList()
        }

        @Override
        Map createObjectContainer() {
            return new LinkedHashMap()
        }
    }

    @Override
    public Closure getAt(Object contentType) {
        def parser = getParser(new MediaType(contentType.toString()))
        if (parser != null) {
            return parser
        }
        return super.getAt((Object) contentType)
    }

    public Closure getParser(MediaType mediaType) {
        Closure parser = null
        if ('json' == mediaType.subtype || 'json' == mediaType.suffix) {
            parser = jsonParser
        } else if ('image' == mediaType.type || 'octet-stream' == mediaType.subtype) {
            parser = streamParser
        } else if ('text' == mediaType.type) {
            parser = textParser
        } else if ('x-www-form-urlencoded' == mediaType.subtype) {
            parser = formParser
        } else if ('html' == mediaType.subtype || 'html' == mediaType.suffix) {
            parser = htmlParser
        } else if ('xml' == mediaType.subtype || 'xml' == mediaType.suffix) {
            parser = xmlParser
        }
        return parser
    }

    @Override
    public Object parseJSON(HttpResponse resp) {
        HttpEntity entity = resp.getEntity()
        JSONParser parser = new JSONParser()
        return parser.parse(new InputStreamReader(entity.content), containerFactory)
    }
}
