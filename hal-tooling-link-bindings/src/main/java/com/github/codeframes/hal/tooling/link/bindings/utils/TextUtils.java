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
package com.github.codeframes.hal.tooling.link.bindings.utils;

/**
 * Utility class for converting text formats.
 */
public final class TextUtils {

    private TextUtils() {
    }

    /**
     * Returns the camelCase formatted counterpart to the provided snake_case formatted value.
     *
     * @param value the snake_case formatted text to be converted
     * @return the given value in camelCase format
     */
    public static String snakeCaseToCamelCase(String value) {
        char[] chars = value.toCharArray();
        int i = 0;
        boolean toUpperCase = false;
        for (int j = 0; j < chars.length; j++) {
            char ch = chars[j];
            if (ch == '_') {
                toUpperCase = true;
            } else if (toUpperCase) {
                chars[i++] = Character.toUpperCase(chars[j]);
                toUpperCase = false;
            } else {
                chars[i++] = chars[j];
            }
        }
        return new String(chars, 0, i);
    }
}
