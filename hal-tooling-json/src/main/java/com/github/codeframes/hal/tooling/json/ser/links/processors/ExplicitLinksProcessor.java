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
package com.github.codeframes.hal.tooling.json.ser.links.processors;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.github.codeframes.hal.tooling.core.Link;
import com.github.codeframes.hal.tooling.json.core.BeanPropertyReader;

import java.util.Iterator;
import java.util.Map;

class ExplicitLinksProcessor extends BeanPropertyReader<Iterable<Link>> implements LinkProcessor {

    ExplicitLinksProcessor(BeanPropertyWriter property) {
        super(property);
    }

    @Override
    public void addLinks(final Map<String, Object> linkMap, final Object bean) throws JsonMappingException {
        final Iterable<Link> links = get(bean);
        if (links != null) {
            final Iterator<Link> itr = links.iterator();
            if (itr.hasNext()) {
                final Link link = itr.next();
                linkMap.put(link.getRel(), links);
            }
        }
    }
}
