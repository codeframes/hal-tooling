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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.github.codeframes.hal.tooling.json.ser.embedded.EmbeddedsSerializer;
import com.github.codeframes.hal.tooling.json.ser.links.LinksSerializer;
import mockit.Deencapsulation;
import mockit.Injectable;
import mockit.Mocked;
import mockit.StrictExpectations;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class HalSerializerTest {

    @Injectable
    BeanSerializerBase mockSerializer;

    List<BeanPropertyWriter> properties = Collections.emptyList();

    List<BeanPropertyWriter> filteredProperties = Collections.emptyList();

    @Injectable
    LinksSerializer mockLinksSerializer;

    @Injectable
    EmbeddedsSerializer mockEmbeddedsSerializer;

    HalSerializer halSerializer;

    @Before
    public void setUp() throws Exception {
        halSerializer = new HalSerializer(mockSerializer, properties, filteredProperties, mockLinksSerializer, mockEmbeddedsSerializer);
    }

    @Test
    public void testWithObjectIdWriter(@Mocked ObjectIdWriter mockObjectIdWriter) throws Exception {

        HalSerializer halSerializerWithObjectIdWriter = halSerializer.withObjectIdWriter(mockObjectIdWriter);
        ObjectIdWriter objectIdWriter = Deencapsulation.getField(halSerializerWithObjectIdWriter, "_objectIdWriter");

        assertThat(objectIdWriter, is(sameInstance(mockObjectIdWriter)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testWithIgnorals() throws Exception {
        halSerializer.withIgnorals(new String[]{"prop"});
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAsArraySerializer() throws Exception {
        halSerializer.asArraySerializer();
    }

    @Test
    public void testWithFilterId() throws Exception {

        Object filterId = new Object();

        HalSerializer halSerializerWithFilterId = halSerializer.withFilterId(filterId);
        Object propertyFilterId = Deencapsulation.getField(halSerializerWithFilterId, "_propertyFilterId");

        assertThat(propertyFilterId, is(sameInstance(filterId)));
    }

    @Test
    public void testSerialize(@Mocked final JsonGenerator mockJGen, @Mocked final SerializerProvider mockProvider) throws Exception {

        final Object bean = new Object();

        new StrictExpectations() {
            {
                mockJGen.getOutputContext().inRoot();
                result = true;

                mockJGen.writeStartObject();

                mockLinksSerializer.serializeFields(bean, mockJGen, true);

                mockEmbeddedsSerializer.serializeFields(bean, mockJGen);

                mockJGen.writeEndObject();
            }
        };

        halSerializer.serialize(bean, mockJGen, mockProvider);
    }
}