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

import java.util.regex.Matcher
import java.util.regex.Pattern

@SuppressWarnings("GrFinalVariableAccess")
@TypeChecked
@CompileStatic
class MediaType {

    private static final Pattern MEDIA_TYPE_PATTERN = ~/(\w+)\/((?:\w+\.)*\w+)(?:\+(\w+))?((?:;\s*\w+="?[\w\-\+\.]+"?)*)/

    private final String value
    private final String type
    private final String subtype
    private final String suffix
    private final String parameters

    public MediaType(String value) {
        Matcher m = value =~ MEDIA_TYPE_PATTERN
        if (m.matches()) {
            this.value = m.group(0)
            this.type = m.group(1)
            this.subtype = m.group(2)
            this.suffix = m.group(3)
            this.parameters = m.group(4)
        } else {
            throw new IllegalArgumentException(value + " is not a valid media type")
        }
    }

    public String getValue() {
        return value
    }

    public String getType() {
        return type
    }

    public String getSubtype() {
        return subtype
    }

    public String getSuffix() {
        return suffix
    }

    public String getParameters() {
        return parameters
    }

    @Override
    public String toString() {
        return value
    }
}
