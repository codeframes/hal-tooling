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
package com.github.codeframes.hal.tooling.link.bindings.core;

import com.github.codeframes.hal.tooling.link.bindings.Style;
import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext;
import com.github.codeframes.hal.tooling.link.bindings.utils.LinkTemplateUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class HrefTemplate {

    private final String value;
    private final Style style;
    private final boolean containsEL;
    private final boolean containsVariables;
    private final Map<String, String> bindings;
    private final boolean removeUnexpanded;

    HrefTemplate(String value, Style style) {
        this(value, style, Collections.<String, String>emptyMap(), false);
    }

    HrefTemplate(String value, Style style, Map<String, String> bindings, boolean removeUnexpanded) {
        this.value = value;
        this.style = style;
        this.containsEL = LinkTemplateUtils.containsEL(value);
        this.containsVariables = !LinkTemplateUtils.extractParameterNames(value).isEmpty();
        this.bindings = Collections.unmodifiableMap(new HashMap<>(bindings));
        this.removeUnexpanded = removeUnexpanded;
    }

    String getValue() {
        return value;
    }

    Style getStyle() {
        return style;
    }

    boolean containsEL() {
        return containsEL;
    }

    boolean containsVariables() {
        return containsVariables;
    }

    Href resolve(LinkContext linkContext) {
        String template = value;
        // First process any embedded EL expressions
        if (containsEL()) {
            template = linkContext.evaluateAsString(template);
        }

        // Now process any embedded URI template parameters
        boolean templated = false;
        if (containsVariables()) {
            template = linkContext.expand(template, bindings, removeUnexpanded);
            templated = LinkTemplateUtils.isTemplated(template);
        }

        if (!LinkTemplateUtils.isAbsolute(template)) {
            template = linkContext.style(style, template);
        }

        return new Href(template, templated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, style, containsEL, containsVariables, bindings, removeUnexpanded);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final HrefTemplate other = (HrefTemplate) obj;
        return Objects.equals(this.value, other.value)
                && Objects.equals(this.style, other.style)
                && Objects.equals(this.containsEL, other.containsEL)
                && Objects.equals(this.containsVariables, other.containsVariables)
                && Objects.equals(this.bindings, other.bindings)
                && Objects.equals(this.removeUnexpanded, other.removeUnexpanded);
    }

    @Override
    public String toString() {
        return "HrefTemplate{" +
                "value='" + value + '\'' +
                ", style=" + style +
                ", containsEL=" + containsEL +
                ", containsVariables=" + containsVariables +
                ", bindings=" + bindings +
                ", removeUnexpanded=" + removeUnexpanded +
                '}';
    }
}
