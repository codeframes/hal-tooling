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

import com.github.codeframes.hal.tooling.core.Link;
import com.github.codeframes.hal.tooling.link.bindings.core.FieldAccessor;
import com.github.codeframes.hal.tooling.link.bindings.core.LinkDescriptor;
import com.github.codeframes.hal.tooling.link.bindings.core.LinkProvider;

class LinkFieldSetter implements LinkSetter {

    private final FieldAccessor fieldAccessor;
    private final LinkDescriptor linkDescriptor;

    public LinkFieldSetter(FieldAccessor fieldAccessor, LinkDescriptor linkDescriptor) {
        this.fieldAccessor = fieldAccessor;
        this.linkDescriptor = linkDescriptor;
    }

    @Override
    public void setLinks(Object instance, LinkProvider linkProvider) {
        final Link link = linkProvider.getLink(linkDescriptor);
        if (link != null) {
            fieldAccessor.setValue(instance, link);
        }
    }
}
