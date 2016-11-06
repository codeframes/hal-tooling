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

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.github.codeframes.hal.tooling.core.Link;
import com.github.codeframes.hal.tooling.json.core.BeanPropertyReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract class ImplicitLinkProcessorBase<T> extends BeanPropertyReader<T> implements LinkProcessor {

    ImplicitLinkProcessorBase(BeanPropertyWriter property) {
        super(property);
    }

    void addLink(final Map<String, Object> linkMap, final Link link) {
        final String rel = link.getRel();
        final Object linkObject = linkMap.get(rel);
        if (linkObject == null) {
            linkMap.put(rel, link);
        } else if (linkObject instanceof Link) {
            final List<Link> links = new ArrayList<>();
            links.add((Link) linkObject);
            links.add(link);
            linkMap.put(rel, links);
        } else {
            @SuppressWarnings("unchecked")
            final List<Link> links = (List<Link>) linkObject;
            links.add(link);
        }
    }

    protected void addLinks(final Map<String, Object> linkMap, final Iterable<Link> links) {
        for (Link link : links) {
            addLink(linkMap, link);
        }
    }
}
