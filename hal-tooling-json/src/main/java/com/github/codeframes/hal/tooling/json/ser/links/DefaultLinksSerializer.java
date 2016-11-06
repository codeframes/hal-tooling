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
package com.github.codeframes.hal.tooling.json.ser.links;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.codeframes.hal.tooling.core.Curie;
import com.github.codeframes.hal.tooling.json.core.BeanPropertyReader;
import com.github.codeframes.hal.tooling.json.core.CurieComparator;
import com.github.codeframes.hal.tooling.json.core.RelComparator;
import com.github.codeframes.hal.tooling.json.ser.links.processors.LinkProcessor;

import java.io.IOException;
import java.util.*;

public class DefaultLinksSerializer implements LinksSerializer {

    private static final RelComparator REL_COMPARATOR = new RelComparator();
    private static final CurieComparator CURIE_COMPARATOR = new CurieComparator();

    private final List<LinkProcessor> linkProcessors;
    private final List<BeanPropertyReader> curieProperties;

    public DefaultLinksSerializer(List<LinkProcessor> linkProcessors, List<BeanPropertyReader> curieProperties) {
        this.linkProcessors = new ArrayList<>(linkProcessors);
        this.curieProperties = new ArrayList<>(curieProperties);
    }

    @Override
    public void serializeFields(final Object bean, final JsonGenerator jGen, final boolean inRoot) throws IOException {
        final Map<String, Object> links = getLinkObjects(bean, inRoot);
        if (!links.isEmpty()) {
            jGen.writeObjectFieldStart("_links");
            for (Map.Entry<String, Object> entry : links.entrySet()) {
                jGen.writeObjectField(entry.getKey(), entry.getValue());
            }
            jGen.writeEndObject();
        }
    }

    private Map<String, Object> getLinkObjects(final Object bean, final boolean inRoot) throws JsonMappingException {
        final Map<String, Object> linkMap = new TreeMap<>(REL_COMPARATOR);
        if (inRoot) {
            final Set<Curie> curies = getCuries(bean);
            if (!curies.isEmpty()) {
                linkMap.put("curies", curies);
            }
        }

        for (LinkProcessor linkProcessor : linkProcessors) {
            linkProcessor.addLinks(linkMap, bean);
        }
        return linkMap;
    }

    @SuppressWarnings("unchecked")
    private Set<Curie> getCuries(final Object bean) throws JsonMappingException {
        final Set<Curie> curies;
        if (curieProperties.isEmpty()) {
            curies = Collections.emptySet();
        } else {
            curies = new TreeSet<>(CURIE_COMPARATOR);
            for (BeanPropertyReader curieProperty : curieProperties) {
                Object curieObject = curieProperty.get(bean);
                if (curieObject instanceof Curie) {
                    curies.add((Curie) curieObject);
                } else if (curieObject instanceof Iterable) {
                    for (Curie curie : (Iterable<Curie>) curieObject) {
                        curies.add(curie);
                    }
                }
            }
        }
        return curies;
    }
}
