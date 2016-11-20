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

import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext;
import com.github.codeframes.hal.tooling.link.bindings.core.CurieDescriptors;
import com.github.codeframes.hal.tooling.link.bindings.core.LinkProvider;

import java.util.List;

class RootBeanLinkSetter implements BeanLinkSetter {

    private final CurieDescriptors curieDescriptors;
    private final List<LinkSetter> linkSetters;

    public RootBeanLinkSetter(CurieDescriptors curieDescriptors, List<LinkSetter> linkSetters) {
        this.curieDescriptors = curieDescriptors;
        this.linkSetters = linkSetters;
    }

    @Override
    public void setLinks(Object entity, LinkContext linkContext) {
        final LinkProvider linkProvider = new LinkProvider(linkContext.forBean(entity), curieDescriptors);
        for (LinkSetter linkSetter : linkSetters) {
            linkSetter.setLinks(entity, linkProvider);
        }
    }
}
