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
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.codeframes.hal.tooling.core.Link;

import java.io.IOException;

public class LinkSerializer extends JsonSerializer<Link> {

    @Override
    public void serialize(final Link link, final JsonGenerator jGen, final SerializerProvider provider) throws IOException {

        jGen.writeStartObject();

        jGen.writeStringField("href", link.getHref());

        if (link.isTemplated()) {
            jGen.writeBooleanField("templated", true);
        }

        writeNonNullStringField(jGen, "type", link.getType());
        writeNonNullStringField(jGen, "deprecation", link.getDeprecation());
        writeNonNullStringField(jGen, "name", link.getName());
        writeNonNullStringField(jGen, "profile", link.getProfile());
        writeNonNullStringField(jGen, "title", link.getTitle());
        writeNonNullStringField(jGen, "hreflang", link.getHreflang());

        jGen.writeEndObject();
    }

    private void writeNonNullStringField(final JsonGenerator jGen, final String name, final String value) throws IOException {
        if (value != null) {
            jGen.writeStringField(name, value);
        }
    }
}
