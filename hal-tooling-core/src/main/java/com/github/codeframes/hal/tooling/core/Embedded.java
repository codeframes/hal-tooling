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

import java.util.Objects;

/**
 * HAL Embedded resource container. To be used when it is not possible or convenient to implement
 * the {@link Embeddable} interface on an object, marking it as an embedded resource.
 *
 * @param <T> the type of embedded resource
 */
public final class Embedded<T> implements Embeddable {

    private final String rel;
    private final T resource;

    /**
     * @param rel      the link relation name
     * @param resource the embedded resource.
     */
    public Embedded(String rel, T resource) {
        this.rel = rel;
        this.resource = resource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRel() {
        return rel;
    }

    /**
     * @return the embedded resource.
     */
    public T getResource() {
        return resource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rel, resource);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Embedded other = (Embedded) obj;
        return Objects.equals(this.rel, other.rel)
                && Objects.equals(this.resource, other.resource);
    }

    @Override
    public String toString() {
        return "Embedded{" +
                "rel='" + rel + '\'' +
                ", resource=" + resource +
                '}';
    }
}
