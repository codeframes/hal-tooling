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

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Immutable
public class HalBeanProperties {

    private final List<BeanPropertyWriter> properties;
    private final List<BeanPropertyWriter> filteredProperties;
    private final List<BeanPropertyWriter> linkProperties;
    private final List<BeanPropertyWriter> curieProperties;
    private final List<BeanPropertyWriter> embeddableProperties;

    public HalBeanProperties(
            List<BeanPropertyWriter> properties,
            List<BeanPropertyWriter> filteredProperties,
            List<BeanPropertyWriter> linkProperties,
            List<BeanPropertyWriter> curieProperties,
            List<BeanPropertyWriter> embeddableProperties) {
        this.properties = Collections.unmodifiableList(new ArrayList<>(properties));
        this.filteredProperties = Collections.unmodifiableList(new ArrayList<>(filteredProperties));
        this.linkProperties = Collections.unmodifiableList(new ArrayList<>(linkProperties));
        this.curieProperties = Collections.unmodifiableList(new ArrayList<>(curieProperties));
        this.embeddableProperties = Collections.unmodifiableList(new ArrayList<>(embeddableProperties));
    }

    public List<BeanPropertyWriter> getProperties() {
        return properties;
    }

    public List<BeanPropertyWriter> getFilteredProperties() {
        return filteredProperties;
    }

    public boolean hasLinkProperties() {
        return !linkProperties.isEmpty();
    }

    public List<BeanPropertyWriter> getLinkProperties() {
        return linkProperties;
    }

    public boolean hasCurieProperties() {
        return !curieProperties.isEmpty();
    }

    public List<BeanPropertyWriter> getCurieProperties() {
        return curieProperties;
    }

    public boolean hasEmbeddableProperties() {
        return !embeddableProperties.isEmpty();
    }

    public List<BeanPropertyWriter> getEmbeddableProperties() {
        return embeddableProperties;
    }
}
