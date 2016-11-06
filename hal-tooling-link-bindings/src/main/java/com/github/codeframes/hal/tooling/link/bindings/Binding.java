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
package com.github.codeframes.hal.tooling.link.bindings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies a binding to a URI Template parameter that is to be evaluated and used for substitution.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Binding {

    /**
     * The associated URI Template parameter name of which to bind on.
     */
    String name();

    /**
     * EL expression that when evaluated is to be used for URI Template parameter substitution.
     */
    String value();
}
