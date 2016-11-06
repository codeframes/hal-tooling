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
package com.github.codeframes.hal.tooling.json.ser.embedded;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.codeframes.hal.tooling.core.Embeddable;
import com.github.codeframes.hal.tooling.core.Embedded;
import com.github.codeframes.hal.tooling.json.core.BeanPropertyReader;
import mockit.Mocked;
import mockit.StrictExpectations;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.*;

public class DefaultEmbeddedsSerializerTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstruction_with_no_embedded_properties() throws Exception {
        new DefaultEmbeddedsSerializer(Collections.<BeanPropertyReader<Embeddable>>emptyList());
    }

    @Test
    public void testSerializeFields(@Mocked final JsonGenerator mockJGen) throws Exception {

        Embeddable embeddable1 = new Embedded<>("resource_1", new Object());
        Embeddable embeddable2 = new Embedded<>("resource_3", new Object());

        List<BeanPropertyReader<Embeddable>> embeddableProperties = Arrays.asList(
                stubProperty("resource_A", embeddable1),
                stubProperty("resource_B", null),
                stubProperty("resource_C", embeddable2)
        );

        final Map<String, Object> embeddables = new LinkedHashMap<>();
        embeddables.put("resource_1", embeddable1);
        embeddables.put("resource_3", embeddable2);

        new StrictExpectations() {
            {
                mockJGen.writeObjectField("_embedded", embeddables);
            }
        };

        Object bean = new Object();

        DefaultEmbeddedsSerializer serializer = new DefaultEmbeddedsSerializer(embeddableProperties);
        serializer.serializeFields(bean, mockJGen);
    }

    @Test
    public void testSerializeFields_with_no_embedded_resources(@Mocked final JsonGenerator mockJGen) throws Exception {

        List<BeanPropertyReader<Embeddable>> embeddableProperties = Collections.singletonList(
                stubProperty("resource", null)
        );

        new StrictExpectations() {
            {
                mockJGen.writeObjectField("_embedded", any);
                times = 0;
            }
        };

        Object bean = new Object();

        DefaultEmbeddedsSerializer serializer = new DefaultEmbeddedsSerializer(embeddableProperties);
        serializer.serializeFields(bean, mockJGen);
    }

    static BeanPropertyReader<Embeddable> stubProperty(final String name, final Embeddable embeddable) {
        return new BeanPropertyReader<Embeddable>(null) {
            @Override
            public String getName() {
                return name;
            }

            @Nullable
            @Override
            public Embeddable get(Object bean) throws JsonMappingException {
                return embeddable;
            }
        };
    }
}