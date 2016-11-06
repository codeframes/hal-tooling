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
package com.github.codeframes.hal.tooling.link.bindings.uri;

/**
 * Resolves a value of type T to a String representation which is used for parameter substitution in URI Template
 * expansion.
 *
 * @param <T> the supported type that an implementing class can resolve a given value to a String
 * @see UriTemplateExpander
 */
public interface UriValueResolver<T> {

    /**
     * Resolves a given value of type T to String representation.
     *
     * @param value the value of which to be resolved
     * @return the resolved String representation of the given value
     */
    String resolve(T value);

    /**
     * @return the supported type that can be resolved to a String
     */
    Class<T> getType();
}
