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

import javax.annotation.Nullable;
import java.util.*;

/**
 * A CurieDescriptor container.
 */
public class CurieDescriptors implements Iterable<CurieDescriptor> {

    private final Map<String, CurieDescriptor> descriptors;

    CurieDescriptors() {
        this.descriptors = Collections.emptyMap();
    }

    CurieDescriptors(List<CurieDescriptor> descriptors) {
        Map<String, CurieDescriptor> curieDescriptors = new HashMap<>(descriptors.size());
        for (CurieDescriptor descriptor : descriptors) {
            curieDescriptors.put(descriptor.getName(), descriptor);
        }
        this.descriptors = Collections.unmodifiableMap(curieDescriptors);
    }

    /**
     * Returns the CurieDescriptor for the given name.
     *
     * @param name the curie name
     * @return CurieDescriptor for the given name or {@code null} if no match is found
     */
    @Nullable
    public CurieDescriptor get(String name) {
        return descriptors.get(name);
    }

    /**
     * @return {@code true} if no CurieDescriptor's are stored else {@code false}
     */
    public boolean isEmpty() {
        return descriptors.isEmpty();
    }

    /**
     * @return the number of CurieDescriptor's stored
     */
    public int size() {
        return descriptors.size();
    }

    @Override
    public Iterator<CurieDescriptor> iterator() {
        return descriptors.values().iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CurieDescriptors other = (CurieDescriptors) obj;
        return Objects.equals(descriptors, other.descriptors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptors);
    }

    @Override
    public String toString() {
        return "CurieDescriptors{" +
                "descriptors=" + descriptors +
                '}';
    }
}
