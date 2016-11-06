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

import javax.annotation.Nullable;

/**
 * Utility class for validating arguments.
 */
public final class Validate {

    private Validate() {
    }

    /**
     * Validates that a given value is either {@code null} or contains text.
     * Value must be {@code null} or contain at least one non-whitespace character.
     *
     * @param value   the value to check
     * @param argName the argument name, used in exception message if validation fails
     * @return value
     * @throws IllegalArgumentException if value is not {@code null} and does not contain text
     */
    public static <T extends CharSequence> T isNullOrHasText(@Nullable final T value, final String argName) {
        return value == null ? null : hasText(value, argName);
    }

    /**
     * Validates that a given value contains text.
     * Value must not be {@code null} and must contain at least one non-whitespace character.
     *
     * @param value   the value to check
     * @param argName the argument name, used in exception message if validation fails
     * @return value
     * @throws NullPointerException     if value is {@code null}
     * @throws IllegalArgumentException if value does not contain text
     */
    public static <T extends CharSequence> T hasText(final T value, final String argName) {
        if (value == null) {
            throw new NullPointerException(String.format("'%s' argument must contain text; cannot be null", argName));
        }

        int length = value.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(value.charAt(i))) {
                    return value;
                }
            }
        }
        throw new IllegalArgumentException(String.format("'%s' argument must contain text; cannot be empty or blank", argName));
    }

    /**
     * Validates that a given value is not {@code null}
     *
     * @param value   the value to check
     * @param argName the argument name, used in exception message if validation fails
     * @return value
     * @throws NullPointerException if value is {@code null}
     */
    public static <T> T notNull(final T value, final String argName) {
        if (value == null) {
            throw new NullPointerException(String.format("'%s' argument cannot be null", argName));
        }
        return value;
    }
}
