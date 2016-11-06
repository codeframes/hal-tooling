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

import com.github.codeframes.hal.tooling.test.json.JSON
import com.github.codeframes.hal.tooling.test.json.JsonUtils
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovyx.net.http.EncoderRegistry
import org.apache.http.HttpEntity
import org.apache.http.entity.StringEntity
import org.codehaus.groovy.runtime.MethodClosure
import org.json.simple.JSONArray
import org.json.simple.JSONObject

@TypeChecked
@CompileStatic
class HttpClientEncoderRegistry extends EncoderRegistry {

    protected final Closure jsonEncoder = new MethodClosure(this, 'encodeJSON')
    protected final Closure streamEncoder = new MethodClosure(this, 'encodeStream')
    protected final Closure textEncoder = new MethodClosure(this, 'encodeText')
    protected final Closure formEncoder = new MethodClosure(this, 'encodeForm')
    protected final Closure xmlEncoder = new MethodClosure(this, "encodeXML");

    @Override
    public Closure getAt(Object contentType) {
        def encoder = getEncoder(new MediaType(contentType.toString()))
        if (encoder != null) {
            return encoder
        }
        return super.getAt((Object) contentType)
    }

    public Closure getEncoder(MediaType mediaType) {
        Closure encoder = null
        if ('json' == mediaType.subtype || 'json' == mediaType.suffix) {
            encoder = jsonEncoder
        } else if ('image' == mediaType.type || 'octet-stream' == mediaType.subtype) {
            encoder = streamEncoder
        } else if ('text' == mediaType.type) {
            encoder = textEncoder
        } else if ('x-www-form-urlencoded' == mediaType.subtype) {
            encoder = formEncoder
        } else if ('html' == mediaType.subtype || 'html' == mediaType.suffix
                || 'xml' == mediaType.subtype || 'xml' == mediaType.suffix) {
            encoder = xmlEncoder
        }
        return encoder
    }

    @Override
    public HttpEntity encodeJSON(Object json, Object contentType) {
        if (json instanceof String) {
            json = JsonUtils.toJsonObj(json)
        } else if (json instanceof JSON) {
            json = json.toObject()
        }

        if (json instanceof Map) {
            return new StringEntity(JSONObject.toJSONString(json))
        } else if (json instanceof List) {
            return new StringEntity(JSONArray.toJSONString(json))
        } else {
            throw new IllegalArgumentException(
                    "Request body must be of type $JSON.name, $Map.name or $List.name to encode to '$contentType'")
        }
    }
}
