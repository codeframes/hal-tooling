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
package com.github.codeframes.hal.tooling.core;

import com.github.codeframes.hal.tooling.utils.Validate;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * HAL Curie Object.
 */
@Immutable
public final class Curie {

    private final String name;
    private final String href;

    /**
     * @param name the name of the Curie. The name must be unique among any other Curie(s) that may be present on
     *             the root Resource Object. This name is used for abbreviation of relations, for example:
     *             <p/>
     *             Given the curie:
     *             <p>
     *             {
     *             "name": "exp",
     *             "href": "http://example.com/relations/{rel}",
     *             "templated": true
     *             }
     *             </p><p/>
     *             And the relation:
     *             <p>
     *             "http://example.com/relations/data"
     *             </p><p/>
     *             Would be abbreviated as:
     *             <p>
     *             "exp:data"
     *             </p>
     * @param href URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> with the token
     *             'rel', that when expanded with a given relation name, provides a URI to relevant documentation
     *             about that link relation.
     */
    public Curie(String name, String href) {
        this.name = Validate.hasText(name, "name");
        this.href = Validate.hasText(href, "href");
    }

    /**
     * @return the curie name
     */
    public String getName() {
        return name;
    }

    /**
     * @return URI Template for link relation documentation
     */
    public String getHref() {
        return href;
    }

    /**
     * @return {@code true}, by definition a curie's href is always templated
     */
    public boolean isTemplated() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, href);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Curie other = (Curie) obj;
        return Objects.equals(this.name, other.name)
                && Objects.equals(this.href, other.href);
    }

    @Override
    public String toString() {
        return "Curie{" +
                "name='" + name + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
