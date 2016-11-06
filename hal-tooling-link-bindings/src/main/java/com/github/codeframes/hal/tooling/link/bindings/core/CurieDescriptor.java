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

import com.github.codeframes.hal.tooling.core.Curie;
import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext;

import java.util.Objects;

/**
 * Template for creating a {@link Curie}.
 */
public class CurieDescriptor {

    private final String name;
    private final HrefTemplate hrefTemplate;

    CurieDescriptor(String name, HrefTemplate hrefTemplate) {
        this.name = name;
        this.hrefTemplate = hrefTemplate;
    }

    /**
     * Curie name.
     */
    public String getName() {
        return name;
    }

    /**
     * Curie href template.
     */
    public HrefTemplate getHrefTemplate() {
        return hrefTemplate;
    }

    /**
     * Returns a Curie based on this descriptor for the given linkContext.
     *
     * @param linkContext the link context for resolving the curie
     * @return Curie based on this descriptor for the given linkContext
     */
    public Curie toCurie(LinkContext linkContext) {
        final Href href = hrefTemplate.resolve(linkContext);
        return new Curie(name, href.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, hrefTemplate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final CurieDescriptor other = (CurieDescriptor) obj;
        return Objects.equals(this.name, other.name)
                && Objects.equals(this.hrefTemplate, other.hrefTemplate);
    }

    @Override
    public String toString() {
        return "CurieDescriptor{" +
                "name='" + name + '\'' +
                ", hrefTemplate=" + hrefTemplate +
                '}';
    }
}
