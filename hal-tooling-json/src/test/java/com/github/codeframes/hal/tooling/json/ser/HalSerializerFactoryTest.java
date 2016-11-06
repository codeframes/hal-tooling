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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.codeframes.hal.tooling.core.Curie;
import com.github.codeframes.hal.tooling.core.Embeddable;
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
import mockit.Mocked;
import mockit.StrictExpectations;
import mockit.Verifications;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HalSerializerFactoryTest {

    static final JavaType EMBEDDABLE_TYPE = TypeFactory.defaultInstance().constructType(Embeddable.class);
    static final JavaType STRING_TYPE = TypeFactory.defaultInstance().constructType(String.class);

    final HalSerializationConfig defaultConfig = HalSerializationConfig.defaultInstance();

    @Mocked
    BeanSerializerBase mockBeanSerializerBase;

    @Mocked
    HalBeanPropertiesProvider mockHalBeanPropertiesProvider;

    @Mocked
    HalSerializer mockHalSerializer;

    @Test
    public void testNewHalSerializer() throws Exception {

        new StrictExpectations() {
            {
                HalBeanPropertiesProvider.getProperties(mockBeanSerializerBase);
                result = halBeanProperties();
            }
        };

        HalSerializerFactory.newHalSerializer(defaultConfig, mockBeanSerializerBase);

        new Verifications() {{

            new HalSerializer(
                    mockBeanSerializerBase,
                    Collections.<BeanPropertyWriter>emptyList(),
                    Collections.<BeanPropertyWriter>emptyList(),
                    LinksSerializer.NO_OP,
                    EmbeddedsSerializer.NO_OP
            );
        }};
    }

    @Test
    public void testNewHalSerializer_with_link_properties() throws Exception {

        final BeanPropertyWriter linkProp = stubBeanPropertyWriter("self", JavaTypes.LINK);

        new StrictExpectations(LinkProcessorFactory.class) {
            {
                HalBeanPropertiesProvider.getProperties(mockBeanSerializerBase);
                result = halBeanPropertiesWithLink(linkProp);

                LinkProcessorFactory.newLinkProcessor(defaultConfig, linkProp);
                result = new NoOpLinkProcessor();
            }
        };

        HalSerializerFactory.newHalSerializer(defaultConfig, mockBeanSerializerBase);

        new Verifications() {{

            new HalSerializer(
                    mockBeanSerializerBase,
                    Collections.<BeanPropertyWriter>emptyList(),
                    Collections.<BeanPropertyWriter>emptyList(),
                    withInstanceOf(DefaultLinksSerializer.class),
                    EmbeddedsSerializer.NO_OP
            );
        }};
    }

    @Test
    public void testNewHalSerializer_with_curie_properties() throws Exception {

        final BeanPropertyWriter curieProp = stubBeanPropertyWriter("curie", JavaTypes.CURIE);

        new StrictExpectations() {
            {
                HalBeanPropertiesProvider.getProperties(mockBeanSerializerBase);
                result = halBeanPropertiesWithCurie(curieProp);
            }
        };

        HalSerializerFactory.newHalSerializer(defaultConfig, mockBeanSerializerBase);

        new Verifications() {{

            new HalSerializer(
                    mockBeanSerializerBase,
                    Collections.<BeanPropertyWriter>emptyList(),
                    Collections.<BeanPropertyWriter>emptyList(),
                    withInstanceOf(DefaultLinksSerializer.class),
                    EmbeddedsSerializer.NO_OP
            );
        }};
    }

    @Test
    public void testNewHalSerializer_with_iterable_curie_properties() throws Exception {

        JavaType listOfCurie = TypeFactory.defaultInstance().constructCollectionType(List.class, Curie.class);

        final BeanPropertyWriter curiesProp = stubBeanPropertyWriter("curies", listOfCurie);

        new StrictExpectations() {
            {
                HalBeanPropertiesProvider.getProperties(mockBeanSerializerBase);
                result = halBeanPropertiesWithCurie(curiesProp);
            }
        };

        HalSerializerFactory.newHalSerializer(defaultConfig, mockBeanSerializerBase);

        new Verifications() {{

            new HalSerializer(
                    mockBeanSerializerBase,
                    Collections.<BeanPropertyWriter>emptyList(),
                    Collections.<BeanPropertyWriter>emptyList(),
                    withInstanceOf(DefaultLinksSerializer.class),
                    EmbeddedsSerializer.NO_OP
            );
        }};
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewHalSerializer_with_invalid_curie_properties() throws Exception {

        final BeanPropertyWriter curiesProp = stubBeanPropertyWriter("curies", STRING_TYPE);

        new StrictExpectations() {
            {
                HalBeanPropertiesProvider.getProperties(mockBeanSerializerBase);
                result = halBeanPropertiesWithCurie(curiesProp);
            }
        };

        HalSerializerFactory.newHalSerializer(defaultConfig, mockBeanSerializerBase);
    }

    @Test
    public void testNewHalSerializer_with_embeddable_properties() throws Exception {

        final BeanPropertyWriter embeddableProp = stubBeanPropertyWriter("resource", EMBEDDABLE_TYPE);

        new StrictExpectations() {
            {
                HalBeanPropertiesProvider.getProperties(mockBeanSerializerBase);
                result = halBeanPropertiesWithEmbedded(embeddableProp);
            }
        };

        HalSerializerFactory.newHalSerializer(defaultConfig, mockBeanSerializerBase);

        new Verifications() {{

            new HalSerializer(
                    mockBeanSerializerBase,
                    Collections.<BeanPropertyWriter>emptyList(),
                    Collections.<BeanPropertyWriter>emptyList(),
                    LinksSerializer.NO_OP,
                    withInstanceOf(DefaultEmbeddedsSerializer.class)
            );
        }};
    }

    static HalBeanProperties halBeanProperties() {
        return new HalBeanProperties(
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.<BeanPropertyWriter>emptyList());
    }

    static HalBeanProperties halBeanPropertiesWithLink(BeanPropertyWriter linkProp) {
        return new HalBeanProperties(
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.singletonList(linkProp),
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.<BeanPropertyWriter>emptyList());
    }

    static HalBeanProperties halBeanPropertiesWithCurie(BeanPropertyWriter curieProp) {
        return new HalBeanProperties(
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.singletonList(curieProp),
                Collections.<BeanPropertyWriter>emptyList());
    }

    static HalBeanProperties halBeanPropertiesWithEmbedded(BeanPropertyWriter embeddedProp) {
        return new HalBeanProperties(
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.<BeanPropertyWriter>emptyList(),
                Collections.singletonList(embeddedProp));
    }

    static BeanPropertyWriter stubBeanPropertyWriter(final String name, final JavaType type) {
        return new BeanPropertyWriter() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public JavaType getType() {
                return type;
            }
        };
    }

    static class NoOpLinkProcessor implements LinkProcessor {
        @Override
        public void addLinks(Map<String, Object> linkMap, Object bean) throws JsonMappingException {
        }
    }
}