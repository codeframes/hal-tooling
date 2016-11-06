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
package com.github.codeframes.hal.tooling.json.ser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.github.codeframes.hal.tooling.core.Curie;
import com.github.codeframes.hal.tooling.core.Embeddable;
import com.github.codeframes.hal.tooling.json.core.BeanPropertyReader;
import com.github.codeframes.hal.tooling.json.core.HalBeanProperties;
import com.github.codeframes.hal.tooling.json.core.HalBeanPropertiesProvider;
import com.github.codeframes.hal.tooling.json.core.JavaTypes;
import com.github.codeframes.hal.tooling.json.ser.config.HalSerializationConfig;
import com.github.codeframes.hal.tooling.json.ser.embedded.DefaultEmbeddedsSerializer;
import com.github.codeframes.hal.tooling.json.ser.embedded.EmbeddedsSerializer;
import com.github.codeframes.hal.tooling.json.ser.links.DefaultLinksSerializer;
import com.github.codeframes.hal.tooling.json.ser.links.LinksSerializer;
import com.github.codeframes.hal.tooling.json.ser.links.processors.LinkProcessor;
import com.github.codeframes.hal.tooling.json.ser.links.processors.LinkProcessorFactory;

import java.util.ArrayList;
import java.util.List;

public final class HalSerializerFactory {

    private HalSerializerFactory() {
    }

    static HalSerializer newHalSerializer(final HalSerializationConfig halSerializationConfig,
                                          final BeanSerializerBase serializer) {

        final HalBeanProperties halBeanProperties = HalBeanPropertiesProvider.getProperties(serializer);

        final List<BeanPropertyWriter> properties = halBeanProperties.getProperties();
        final List<BeanPropertyWriter> filteredProperties = halBeanProperties.getFilteredProperties();

        final LinksSerializer linksSerializer = getLinksSerializer(halSerializationConfig, halBeanProperties);
        final EmbeddedsSerializer embeddedsSerializer = getEmbeddedSerializer(halBeanProperties);

        return new HalSerializer(serializer, properties, filteredProperties, linksSerializer, embeddedsSerializer);
    }

    private static LinksSerializer getLinksSerializer(final HalSerializationConfig halSerializationConfig,
                                                      final HalBeanProperties halBeanProperties) {
        final LinksSerializer linksSerializer;
        if (halBeanProperties.hasLinkProperties() || halBeanProperties.hasCurieProperties()) {

            final List<BeanPropertyWriter> linkProperties = halBeanProperties.getLinkProperties();

            final List<LinkProcessor> linkProcessors = new ArrayList<>(linkProperties.size());
            for (BeanPropertyWriter linkProperty : linkProperties) {
                linkProcessors.add(LinkProcessorFactory.newLinkProcessor(halSerializationConfig, linkProperty));
            }
            linksSerializer = new DefaultLinksSerializer(linkProcessors, toCuriePropertyReaders(halBeanProperties.getCurieProperties()));
        } else {
            linksSerializer = LinksSerializer.NO_OP;
        }
        return linksSerializer;
    }

    private static List<BeanPropertyReader> toCuriePropertyReaders(final List<BeanPropertyWriter> curiePropertyWriters) {
        List<BeanPropertyReader> curiePropertyReaders = new ArrayList<>(curiePropertyWriters.size());
        for (BeanPropertyWriter curieProperty : curiePropertyWriters) {
            if (isCurieType(curieProperty)) {
                curiePropertyReaders.add(new BeanPropertyReader<Curie>(curieProperty));
            } else {
                curiePropertyReaders.add(new BeanPropertyReader<Iterable<Curie>>(curieProperty));
            }
        }
        return curiePropertyReaders;
    }

    private static boolean isCurieType(final BeanPropertyWriter property) {
        final JavaType type = property.getType();
        if (JavaTypes.CURIE.equals(type)) {
            return true;
        } else if (JavaTypes.isIterableCurieType(type)) {
            return false;
        } else {
            throw new IllegalArgumentException(
                    String.format("BeanPropertyWriter must refer to a type of: %s or an Iterable thereof", Curie.class));
        }
    }

    private static EmbeddedsSerializer getEmbeddedSerializer(final HalBeanProperties halBeanProperties) {
        if (halBeanProperties.hasEmbeddableProperties()) {
            final List<BeanPropertyReader<Embeddable>> embeddableProperties = toEmbeddablePropertyReaders(halBeanProperties.getEmbeddableProperties());
            return new DefaultEmbeddedsSerializer(embeddableProperties);
        } else {
            return EmbeddedsSerializer.NO_OP;
        }
    }

    private static List<BeanPropertyReader<Embeddable>> toEmbeddablePropertyReaders(final List<BeanPropertyWriter> embeddableProperties) {
        final List<BeanPropertyReader<Embeddable>> embeddablePropertyReaders = new ArrayList<>(embeddableProperties.size());
        for (BeanPropertyWriter embeddableProperty : embeddableProperties) {
            embeddablePropertyReaders.add(new BeanPropertyReader<Embeddable>(embeddableProperty));
        }
        return embeddablePropertyReaders;
    }
}
