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
package com.github.codeframes.hal.tooling.json.core;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.codeframes.hal.tooling.core.Curie;
import com.github.codeframes.hal.tooling.core.Embedded;
import com.github.codeframes.hal.tooling.core.Link;

/**
 * Constants class for commonly used {@link JavaType}'s
 */
public final class JavaTypes {

    /**
     * {@link Link} type
     */
    public static final JavaType LINK = TypeFactory.defaultInstance().constructType(Link.class);

    /**
     * {@link Curie} type
     */
    public static final JavaType CURIE = TypeFactory.defaultInstance().constructType(Curie.class);

    /**
     * {@link Embedded} type
     */
    public static final JavaType EMBEDDED = TypeFactory.defaultInstance().constructType(Embedded.class);

    private JavaTypes() {
    }

    public static boolean isIterableLinkType(final JavaType javaType) {
        return Iterable.class.isAssignableFrom(javaType.getRawClass()) && javaType.getContentType().equals(LINK);
    }

    public static boolean isIterableCurieType(final JavaType javaType) {
        return Iterable.class.isAssignableFrom(javaType.getRawClass()) && javaType.getContentType().equals(CURIE);
    }
}
