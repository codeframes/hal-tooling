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
import com.github.codeframes.hal.tooling.json.core.BeanPropertyReader;

import java.io.IOException;
import java.util.*;

public class DefaultEmbeddedsSerializer implements EmbeddedsSerializer {

    private final List<BeanPropertyReader<Embeddable>> embeddableProperties;

    public DefaultEmbeddedsSerializer(List<BeanPropertyReader<Embeddable>> embeddableProperties) {
        if (embeddableProperties.isEmpty()) {
            throw new IllegalArgumentException("embeddableProperties cannot be empty");
        }
        this.embeddableProperties = new ArrayList<>(embeddableProperties);
    }

    @Override
    public void serializeFields(Object bean, JsonGenerator jGen) throws IOException {
        final Map<String, Object> embeddeds = getEmbeddeds(bean);
        if (!embeddeds.isEmpty()) {
            jGen.writeObjectField("_embedded", embeddeds);
        }
    }

    private Map<String, Object> getEmbeddeds(final Object bean) throws JsonMappingException {
        final Map<String, Object> embeddeds = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String rel1, String rel2) {
                return rel1.compareTo(rel2);
            }
        });

        for (BeanPropertyReader<Embeddable> embeddableProperty : embeddableProperties) {
            final Embeddable embeddable = embeddableProperty.get(bean);
            if (embeddable != null) {
                embeddeds.put(embeddable.getRel(), embeddable);
            }
        }
        return embeddeds;
    }
}
