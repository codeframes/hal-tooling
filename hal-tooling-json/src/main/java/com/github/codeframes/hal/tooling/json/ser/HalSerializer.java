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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@SuppressWarnings("serial")
public class HalSerializer extends BeanSerializerBase {

    private final LinksSerializer linksSerializer;
    private final EmbeddedsSerializer embeddedsSerializer;

    HalSerializer(BeanSerializerBase serializer,
                  List<BeanPropertyWriter> properties,
                  List<BeanPropertyWriter> filteredProperties,
                  LinksSerializer linksSerializer,
                  EmbeddedsSerializer embeddedsSerializer) {
        super(serializer, toArray(properties), toArray(filteredProperties));
        this.linksSerializer = linksSerializer;
        this.embeddedsSerializer = embeddedsSerializer;
    }

    private HalSerializer(HalSerializer src, ObjectIdWriter objectIdWriter, Object filterId) {
        super(src, objectIdWriter, filterId);
        this.linksSerializer = src.linksSerializer;
        this.embeddedsSerializer = src.embeddedsSerializer;
    }

    private static BeanPropertyWriter[] toArray(List<BeanPropertyWriter> properties) {
        return properties.toArray(new BeanPropertyWriter[properties.size()]);
    }

    @Override
    public HalSerializer withObjectIdWriter(ObjectIdWriter objectIdWriter) {
        return new HalSerializer(this, objectIdWriter, _propertyFilterId);
    }

    @Override
    protected HalSerializer withIgnorals(String[] toIgnore) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected HalSerializer asArraySerializer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HalSerializer withFilterId(Object filterId) {
        return new HalSerializer(this, _objectIdWriter, filterId);
    }

    @Override
    public void serialize(final Object bean, final JsonGenerator jGen, final SerializerProvider provider) throws IOException {

        final boolean inRoot = jGen.getOutputContext().inRoot();

        jGen.writeStartObject();

        linksSerializer.serializeFields(bean, jGen, inRoot);

        serializeFields(bean, jGen, provider);

        embeddedsSerializer.serializeFields(bean, jGen);

        jGen.writeEndObject();
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        throw new NotSerializableException(HalSerializer.class.getName());
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        throw new NotSerializableException(HalSerializer.class.getName());
    }


    @Override
    public String toString() {
        return "HalSerializer for " + handledType().getName();
    }
}
