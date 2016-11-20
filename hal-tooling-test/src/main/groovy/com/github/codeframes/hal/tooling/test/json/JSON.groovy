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
package com.github.codeframes.hal.tooling.test.json

/**
 * Encapsulates a String or GString as a JSON type specifically for use with
 * {@link com.github.codeframes.hal.tooling.test.http.HttpClient} to help aid assertions. Also adds asType support to
 * String and GString for convenient type coercion via static initialization. Meaning that for the 'as' Coercion
 * operator to work with this class, this class MUST have already been loaded.
 */
class JSON {

    static {
        def stringAsType = String.metaClass.asType
        String.metaClass.asType = { Class target ->
            if (JSON == target) {
                return new JSON((String) delegate)
            } else {
                return stringAsType(target)
            }
        }

        def gStringAsType = GString.metaClass.asType
        GString.metaClass.asType = { Class target ->
            if (JSON == target) {
                return new JSON((GString) delegate)
            } else {
                return gStringAsType(target)
            }
        }
    }

    private String string
    private GString gString

    /**
     * Constructs a new JSON Object.
     *
     * @param string the String to be evaluated as a JSON object
     */
    JSON(String string) {
        this.string = string
    }

    /**
     * Constructs a new JSON Object.
     *
     * @param gString the GString to be evaluated as a JSON object. Value templating is supported
     */
    JSON(GString gString) {
        this.gString = gString
    }

    /**
     * Returns the JSON Object realisation of the textual representation which is either a {@link java.util.Map} or a
     * {@link java.util.List}.
     * <p>
     * If a GString was used to construct this Object any template tokens present will be resolved before parsing.
     * However there is one exception to this and that's if the token references a {@link org.hamcrest.Matcher}. In this
     * case the token is replaced with a temporary place holder, the GString is then parsed to provide the JSON Object
     * and finally the {@link org.hamcrest.Matcher} is swapped around with it's associated place holder. Resulting in a
     * JSON Object with {@link org.hamcrest.Matcher}'s as field values which can be used for assertions with
     * {@link com.github.codeframes.hal.tooling.test.http.HttpClientResponseMatchers#has_body(JSON)}.
     * </p>
     *
     * @return the JSON Object realisation of the textual representation or {@code null} if the textual representation
     * used to construct this object was {@code null}
     */
    Object toObject() {
        if (string != null) {
            return JsonUtils.toJsonObj(string)
        } else if (gString != null) {
            return JsonUtils.toJsonObj(gString)
        } else {
            return null
        }
    }
}
