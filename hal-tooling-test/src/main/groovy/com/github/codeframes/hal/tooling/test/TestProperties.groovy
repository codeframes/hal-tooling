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
package com.github.codeframes.hal.tooling.test

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

@TypeChecked
@CompileStatic
class TestProperties {

    public static final String HTTP_CLIENT_DEFAULT_URI = 'httpClient.defaultURI'

    private static final properties = [:]

    static {
        def systemProperties = System.properties
        def localProperties = getLocalProperties()
        def defaultProperties = getDefaultProperties()

        defaultProperties.each {
            def name = it.key
            def property = systemProperties[name]
            if (property == null) {
                property = localProperties[name]
                if (property == null) {
                    property = defaultProperties[name]
                }
            }
            properties[name] = property
        }
    }

    private static Properties getLocalProperties() {
        def properties = new Properties()
        def propertyFile = new File("hal-tooling-test.properties")
        if (propertyFile.exists()) {
            propertyFile.withInputStream {
                stream -> properties.load(stream)
            }
        }
        return properties
    }

    private static Map<String, String> getDefaultProperties() {
        def properties = [:]
        properties[HTTP_CLIENT_DEFAULT_URI] = 'http://localhost:8080/'
        return properties
    }

    static String get(String name) {
        return properties[name]
    }
}
