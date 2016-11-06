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
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.codeframes.hal.tooling.core.Link;
import mockit.Mocked;
import mockit.StrictExpectations;
import org.junit.Test;

public class LinkSerializerTest {

    @Mocked
    JsonGenerator mockJGen;

    SerializerProvider provider_not_used = null;

    @Test
    public void testSerialize_with_basic_link() throws Exception {

        Link link = new Link("a_rel", "a_href");

        new StrictExpectations() {
            {
                mockJGen.writeStartObject();

                mockJGen.writeStringField("href", "a_href");

                mockJGen.writeEndObject();
            }
        };

        LinkSerializer linkSerializer = new LinkSerializer();
        linkSerializer.serialize(link, mockJGen, provider_not_used);
    }

    @Test
    public void testSerialize_with_templated_link() throws Exception {

        Link link = new Link("a_rel", "a_href/{templated}");

        new StrictExpectations() {
            {
                mockJGen.writeStartObject();

                mockJGen.writeStringField("href", "a_href/{templated}");
                mockJGen.writeBooleanField("templated", true);

                mockJGen.writeEndObject();
            }
        };

        LinkSerializer linkSerializer = new LinkSerializer();
        linkSerializer.serialize(link, mockJGen, provider_not_used);
    }

    @Test
    public void testSerialize_with_full_property_set_link() throws Exception {

        Link link = new Link.Builder()
                .rel("a_rel")
                .href("a_href/{templated}")
                .templated(true)
                .type("a_type")
                .deprecation("a_deprecation")
                .name("a_name")
                .profile("a_profile")
                .title("a_title")
                .hreflang("a_hreflang")
                .build();

        new StrictExpectations() {
            {
                mockJGen.writeStartObject();

                mockJGen.writeStringField("href", "a_href/{templated}");
                mockJGen.writeBooleanField("templated", true);
                mockJGen.writeStringField("type", "a_type");
                mockJGen.writeStringField("deprecation", "a_deprecation");
                mockJGen.writeStringField("name", "a_name");
                mockJGen.writeStringField("profile", "a_profile");
                mockJGen.writeStringField("title", "a_title");
                mockJGen.writeStringField("hreflang", "a_hreflang");

                mockJGen.writeEndObject();
            }
        };

        LinkSerializer linkSerializer = new LinkSerializer();
        linkSerializer.serialize(link, mockJGen, provider_not_used);
    }
}