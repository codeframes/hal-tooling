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
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.github.codeframes.hal.tooling.core.Embeddable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HalBeanPropertiesProvider {

    private HalBeanPropertiesProvider() {
    }

    public static HalBeanProperties getProperties(BeanSerializerBase src) {

        final BeanPropertiesExtractor beanPropertiesExtractor = new BeanPropertiesExtractor(src);
        final List<BeanPropertyWriter> beanProperties = beanPropertiesExtractor.getProperties();
        final List<BeanPropertyWriter> beanFilteredProperties = beanPropertiesExtractor.getFilteredProperties();

        final List<BeanPropertyWriter> properties = getNonHalProperties(beanProperties);
        final List<BeanPropertyWriter> filteredProperties = getNonHalProperties(beanFilteredProperties);
        final List<BeanPropertyWriter> linkProperties = getLinkProperties(beanProperties);
        final List<BeanPropertyWriter> curieProperties = getCurieProperties(beanProperties);
        final List<BeanPropertyWriter> embeddableProperties = getEmbeddableProperties(beanProperties);

        return new HalBeanProperties(properties, filteredProperties, linkProperties, curieProperties, embeddableProperties);
    }

    private static class BeanPropertiesExtractor extends BeanSerializer {

        private static final long serialVersionUID = 3182891417430716928L;

        BeanPropertiesExtractor(BeanSerializerBase src) {
            super(src);
        }

        List<BeanPropertyWriter> getProperties() {
            return asList(this._props);
        }

        List<BeanPropertyWriter> getFilteredProperties() {
            return asList(this._filteredProps);
        }

        private static List<BeanPropertyWriter> asList(BeanPropertyWriter[] properties) {
            if (properties == null) {
                return Collections.emptyList();
            }

            final List<BeanPropertyWriter> propertyList = new ArrayList<>();
            for (BeanPropertyWriter property : properties) {
                // BeanPropertyWriter[] can apparently also contain nulls
                if (property != null) {
                    propertyList.add(property);
                }
            }
            return propertyList;
        }
    }

    private static List<BeanPropertyWriter> getNonHalProperties(List<BeanPropertyWriter> properties) {
        final List<BeanPropertyWriter> nonHalProperties = new ArrayList<>(properties);
        nonHalProperties.removeAll(getLinkProperties(properties));
        nonHalProperties.removeAll(getCurieProperties(properties));
        nonHalProperties.removeAll(getEmbeddableProperties(properties));
        return nonHalProperties;
    }

    private static List<BeanPropertyWriter> getLinkProperties(List<BeanPropertyWriter> properties) {
        final List<BeanPropertyWriter> linkProperties = new ArrayList<>();
        for (BeanPropertyWriter property : properties) {
            final JavaType type = property.getType();
            if (JavaTypes.LINK.equals(type) || JavaTypes.isIterableLinkType(type)) {
                linkProperties.add(property);
            }
        }
        return linkProperties;
    }

    private static List<BeanPropertyWriter> getCurieProperties(List<BeanPropertyWriter> properties) {
        final List<BeanPropertyWriter> curieProperties = new ArrayList<>();
        for (BeanPropertyWriter property : properties) {
            final JavaType type = property.getType();
            if (JavaTypes.CURIE.equals(type) || JavaTypes.isIterableCurieType(type)) {
                curieProperties.add(property);
            }
        }
        return curieProperties;
    }

    private static List<BeanPropertyWriter> getEmbeddableProperties(List<BeanPropertyWriter> properties) {
        final List<BeanPropertyWriter> embeddableProperties = new ArrayList<>();
        for (BeanPropertyWriter property : properties) {
            final JavaType type = property.getType();
            if (type.isTypeOrSubTypeOf(Embeddable.class)) {
                embeddableProperties.add(property);
            }
        }
        return embeddableProperties;
    }
}
