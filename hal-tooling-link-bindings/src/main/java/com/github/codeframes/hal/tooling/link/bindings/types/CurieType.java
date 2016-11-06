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
package com.github.codeframes.hal.tooling.link.bindings.types;

import com.github.codeframes.hal.tooling.link.bindings.CurieDef;
import com.github.codeframes.hal.tooling.link.bindings.Style;
import com.github.codeframes.hal.tooling.utils.Validate;

import javax.annotation.concurrent.Immutable;

/**
 * A POJO representation of {@link CurieDef}
 */
@Immutable
public final class CurieType {

    private final String name;
    private final String value;
    private final Style style;

    private CurieType(String name, String value, Style style) {
        this.name = name;
        this.value = value;
        this.style = style;
    }

    /**
     * Constructs a new CurieType based off the given curieDef.
     *
     * @param curieDef the curieDef of which to create a CurieType from
     * @return a new CurieType based off the given curieDef.
     * @throws IllegalArgumentException if the curieDef's name or value does not contain text
     */
    public static CurieType valueOf(CurieDef curieDef) {
        String name = Validate.hasText(curieDef.name(), "CurieDef.name");
        String value = Validate.hasText(curieDef.value(), "CurieDef.value");
        Style style = curieDef.style();
        return new CurieType(name, value, style);
    }

    /**
     * The curie name, what is also prefixed to any rel associated with a curie such that, {@literal <name>:rel}.
     */
    public String getName() {
        return name;
    }

    /**
     * The curie href, a URI Template for link relation documentation. Template MUST end with the token 'rel'.
     */
    public String getValue() {
        return value;
    }

    /**
     * The curie href style.
     */
    public Style getStyle() {
        return style;
    }
}
