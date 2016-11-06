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
import com.github.codeframes.hal.tooling.core.Link;
import com.github.codeframes.hal.tooling.json.core.BeanPropertyReader;
import com.github.codeframes.hal.tooling.json.ser.links.processors.LinkProcessor;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.StrictExpectations;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class DefaultLinksSerializerTest {

    @Mocked
    JsonGenerator mockJGen;

    final Object bean = new Object();

    @Test
    public void testSerializeFields_with_no_links_no_curies_not_in_root() throws Exception {

        new StrictExpectations() {
            {
                mockJGen.writeObjectFieldStart("_links");
                times = 0;
            }
        };

        DefaultLinksSerializer serializer = new DefaultLinksSerializer(Collections.<LinkProcessor>emptyList(), Collections.<BeanPropertyReader>emptyList());
        serializer.serializeFields(bean, mockJGen, false);
    }

    @Test
    public void testSerializeFields_with_no_links_no_curies_in_root() throws Exception {

        new StrictExpectations() {
            {
                mockJGen.writeObjectFieldStart("_links");
                times = 0;
            }
        };

        DefaultLinksSerializer serializer = new DefaultLinksSerializer(Collections.<LinkProcessor>emptyList(), Collections.<BeanPropertyReader>emptyList());
        serializer.serializeFields(bean, mockJGen, true);
    }

    @Test
    public void testSerializeFields_with_link_no_curies_not_in_root() throws Exception {

        final Link link = new Link("rel", "href");

        LinkProcessor mockLinkProcessor = new MockUp<LinkProcessor>() {
            @Mock
            void addLinks(Map<String, Object> linkMap, Object bean) throws JsonMappingException {
                linkMap.put(link.getRel(), link);
            }
        }.getMockInstance();

        new StrictExpectations() {
            {
                mockJGen.writeObjectFieldStart("_links");
                mockJGen.writeObjectField("rel", link);
                mockJGen.writeEndObject();
            }
        };

        DefaultLinksSerializer serializer = new DefaultLinksSerializer(Collections.singletonList(mockLinkProcessor), Collections.<BeanPropertyReader>emptyList());
        serializer.serializeFields(bean, mockJGen, false);
    }

    @Test
    public void testSerializeFields_with_links_no_curies_not_in_root() throws Exception {

        final Link link1 = new Link("rel_1", "href_1");
        final Link link2 = new Link("rel_2", "href_2");

        LinkProcessor mockLinkProcessor = new MockUp<LinkProcessor>() {
            @Mock
            void addLinks(Map<String, Object> linkMap, Object bean) throws JsonMappingException {
                linkMap.put(link1.getRel(), link1);
                linkMap.put(link2.getRel(), link2);
            }
        }.getMockInstance();

        new StrictExpectations() {
            {
                mockJGen.writeObjectFieldStart("_links");
                mockJGen.writeObjectField("rel_1", link1);
                mockJGen.writeObjectField("rel_2", link2);
                mockJGen.writeEndObject();
            }
        };

        DefaultLinksSerializer serializer = new DefaultLinksSerializer(Collections.singletonList(mockLinkProcessor), Collections.<BeanPropertyReader>emptyList());
        serializer.serializeFields(bean, mockJGen, false);
    }

    @Test
    public void testSerializeFields_with_curies_no_links_not_in_root(@Mocked final BeanPropertyReader mockCurieProperty) throws Exception {

        new StrictExpectations() {
            {
                mockJGen.writeObjectFieldStart("_links");
                times = 0;
            }
        };

        DefaultLinksSerializer serializer = new DefaultLinksSerializer(Collections.<LinkProcessor>emptyList(), Collections.singletonList(mockCurieProperty));
        serializer.serializeFields(bean, mockJGen, false);
    }

    @Test
    public void testSerializeFields_with_curie_no_links_in_root(@Mocked final BeanPropertyReader mockCurieProperty) throws Exception {

        final Curie curie = new Curie("name", "href");

        new StrictExpectations() {
            {
                mockCurieProperty.get(bean);
                result = curie;

                mockJGen.writeObjectFieldStart("_links");
                mockJGen.writeObjectField("curies", Collections.singleton(curie));
                mockJGen.writeEndObject();
            }
        };

        DefaultLinksSerializer serializer = new DefaultLinksSerializer(Collections.<LinkProcessor>emptyList(), Collections.singletonList(mockCurieProperty));
        serializer.serializeFields(bean, mockJGen, true);
    }

    @Test
    public void testSerializeFields_with_curies_no_links_in_root(@Mocked final BeanPropertyReader mockCuriesProperty) throws Exception {

        final Curie curie = new Curie("name", "href");
        final Set<Curie> curies = Collections.singleton(curie);

        new StrictExpectations() {
            {
                mockCuriesProperty.get(bean);
                result = curies;

                mockJGen.writeObjectFieldStart("_links");
                mockJGen.writeObjectField("curies", curies);
                mockJGen.writeEndObject();
            }
        };

        DefaultLinksSerializer serializer = new DefaultLinksSerializer(Collections.<LinkProcessor>emptyList(), Collections.singletonList(mockCuriesProperty));
        serializer.serializeFields(bean, mockJGen, true);
    }
}