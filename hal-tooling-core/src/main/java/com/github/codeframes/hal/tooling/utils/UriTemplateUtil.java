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
package com.github.codeframes.hal.tooling.utils;

import java.util.regex.Pattern;

/**
 * Utility class for working with URI Templates <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a>
 */
public final class UriTemplateUtil {

    private static final Pattern IS_TEMPLATED_PATTERN = Pattern.compile("\\{[\\w?+./&;#][-\\w.*,:]*\\}");

    private UriTemplateUtil() {
    }

    /**
     * @param uri the value to check
     * @return {@code true} if uri represents a URI Template else {@code false}
     */
    public static boolean isTemplated(String uri) {
        return IS_TEMPLATED_PATTERN.matcher(uri).find();
    }
}
