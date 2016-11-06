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
package com.github.codeframes.hal.tooling.link.bindings.inject;

import com.github.codeframes.hal.tooling.core.Embedded;
import com.github.codeframes.hal.tooling.link.bindings.core.CurieDescriptors;
import com.github.codeframes.hal.tooling.link.bindings.core.FieldAccessor;
import com.github.codeframes.hal.tooling.link.bindings.core.LinkProvider;

import java.util.List;

class EmbeddedFieldLinkSetter implements LinkSetter {

    private final LinkSetterFactory linkSetterFactory;
    private final FieldAccessor fieldAccessor;
    private final CurieDescriptors curieDescriptors;

    public EmbeddedFieldLinkSetter(LinkSetterFactory linkSetterFactory, FieldAccessor fieldAccessor, CurieDescriptors curieDescriptors) {
        this.linkSetterFactory = linkSetterFactory;
        this.fieldAccessor = fieldAccessor;
        this.curieDescriptors = curieDescriptors;
    }

    @Override
    public void setLinks(Object instance, LinkProvider linkProvider) {
        final Embedded embedded = (Embedded) fieldAccessor.getValue(instance);
        if (embedded != null) {
            Object embeddedResource = embedded.getResource();
            if (embeddedResource instanceof List) {
                final List<?> list = (List<?>) embeddedResource;
                for (Object listItem : list) {
                    final LinkSetter linkSetter = linkSetterFactory.getElementBeanLinkSetter(listItem.getClass(), curieDescriptors);
                    linkSetter.setLinks(listItem, linkProvider);
                }
            } else if (embeddedResource != null) {
                final LinkSetter linkSetter = linkSetterFactory.getElementBeanLinkSetter(embeddedResource.getClass(), curieDescriptors);
                linkSetter.setLinks(embeddedResource, linkProvider);
            }
        }
    }
}
