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

import org.hamcrest.Matcher
import org.json.simple.parser.ContainerFactory
import org.json.simple.parser.JSONParser

/**
 * Utility class for parsing JSON to Object's.
 */
class JsonUtils {

    private static final ContainerFactory CONTAINER_FACTORY = new ContainerFactory() {

        def List creatArrayContainer() {
            return new LinkedList();
        }

        def Map createObjectContainer() {
            return new LinkedHashMap();
        }
    }

    /**
     * Returns the JSON Object realisation of the given json String, which is either a {@link java.util.Map} or a
     * {@link java.util.List}.
     *
     * @param json the json String to be converted
     * @return the JSON Object realisation of the given json String
     */
    static toJsonObj(String json) {
        JSONParser parser = new JSONParser();
        return parser.parse(json, CONTAINER_FACTORY)
    }

    /**
     * Returns the JSON Object realisation of the given json GString, which is either a {@link java.util.Map} or a
     * {@link java.util.List}. Any template tokens present will be resolved before parsing, with one exception. If the
     * token references a {@link org.hamcrest.Matcher} its replaced with a temporary place holder, the GString is then
     * parsed to provide the JSON Object and finally the {@link org.hamcrest.Matcher} is swapped around with it's
     * associated place holder. Resulting in a JSON Object with {@link org.hamcrest.Matcher}'s as field values which can
     * be used for assertions with
     * {@link com.github.codeframes.hal.tooling.test.http.HttpClientResponseMatchers#has_body(JSON)}.
     *
     * @param json the json GString to be converted
     * @return the JSON Object realisation of the given json GString
     */
    static toJsonObj(GString json) {
        if (json.getValueCount() == 0) {
            return toJsonObj(json.toString())
        } else {
            def matchers = replaceMatchersWithPlaceholders(json)

            def jsonObj = toJsonObj(json.toString())
            if (jsonObj instanceof Map) {
                replacePlaceholdersWithMatchers((Map) jsonObj, matchers)
            } else {
                replacePlaceholdersWithMatchers((List) jsonObj, matchers)
            }
            return jsonObj
        }
    }

    private static Map<String, Matcher> replaceMatchersWithPlaceholders(GString json) {
        def matchers = [:]
        def values = json.getValues()
        values.eachWithIndex { value, index ->
            if (value instanceof Matcher) {
                matchers.put("&$index".toString(), value)
                values[index] = "\"&$index\""
            }
        }
        return matchers
    }

    private static void replacePlaceholdersWithMatchers(Map jsonObj, Map<String, Matcher> matchers) {
        for (def entry in jsonObj) {
            def value = entry.value
            def matcher = checkForMatcherPlaceholder(value, matchers)
            if (matcher) {
                entry.setValue(matcher)
            }
        }
    }

    private static void replacePlaceholdersWithMatchers(List jsonArray, Map<String, Matcher> matchers) {
        for (int i = 0; i < jsonArray.size(); i++) {
            def item = jsonArray.get(i)
            def matcher = checkForMatcherPlaceholder(item, matchers)
            if (matcher) {
                jsonArray.set(i, matcher);
            }
        }
    }

    private static Matcher checkForMatcherPlaceholder(Object value, Map<String, Matcher> matchers) {
        Matcher matcher = null
        if (value instanceof Map) {
            replacePlaceholdersWithMatchers((Map) value, matchers)
        } else if (value instanceof List) {
            replacePlaceholdersWithMatchers((List) value, matchers)
        } else if (value instanceof String && value.startsWith('&')) {
            matcher = matchers.get(value)
        }
        return matcher
    }
}
