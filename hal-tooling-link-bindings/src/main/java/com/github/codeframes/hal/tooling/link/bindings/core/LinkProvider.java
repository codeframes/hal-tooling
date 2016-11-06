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
package com.github.codeframes.hal.tooling.link.bindings.core;

import com.github.codeframes.hal.tooling.core.Curie;
import com.github.codeframes.hal.tooling.core.Link;
import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext;

import javax.annotation.Nullable;

/**
 * Provides {@link Link}'s and {@link Curie}'s based on associated descriptors; {@link LinkDescriptor},
 * {@link CurieDescriptor}.
 */
public class LinkProvider {

    private final LinkContext linkContext;
    private final CurieDescriptors curieDescriptors;

    /**
     * Constructs a new LinkProvider with the given linkContext and no registered curie descriptors.
     *
     * @param linkContext the link context for resolving links
     */
    public LinkProvider(LinkContext linkContext) {
        this(linkContext, new CurieDescriptors());
    }

    /**
     * Constructs a new LinkProvider with the given linkContext and curieDescriptors.
     *
     * @param linkContext      the link context for resolving links
     * @param curieDescriptors the curie descriptors to be registered
     */
    public LinkProvider(LinkContext linkContext, CurieDescriptors curieDescriptors) {
        this.linkContext = linkContext;
        this.curieDescriptors = curieDescriptors;
    }

    /**
     * Returns a LinkProvider for the given bean.
     *
     * @param bean the bean to obtain a LinkProvider for
     * @return LinkProvider for the given bean
     */
    public LinkProvider forBean(Object bean) {
        return forBean(bean, this.curieDescriptors);
    }

    /**
     * Returns a LinkProvider with provided curieDescriptors for the given bean.
     *
     * @param bean             the bean to obtain a LinkProvider for
     * @param curieDescriptors the curieDescriptors to be registered with the returned LinkProvider
     * @return LinkProvider with provided curieDescriptors for the given bean
     */
    public LinkProvider forBean(Object bean, CurieDescriptors curieDescriptors) {
        final LinkContext newLinkContext = this.linkContext.forBean(bean);
        return new LinkProvider(newLinkContext, curieDescriptors);
    }

    /**
     * Returns a Link based on the provided linkDescriptor.
     *
     * @param linkDescriptor the linkDescriptor to construct a Link from
     * @return Link based on linkDescriptor or {@code null} if the linkDescriptor condition evaluates to {@code false}
     * @throws IllegalArgumentException if linkDescriptor defines a rel prefixed with a curie that has not been
     *                                  registered with this LinkProvider
     */
    @Nullable
    public Link getLink(LinkDescriptor linkDescriptor) {
        final Link link = linkDescriptor.toLink(linkContext);
        final String curie = linkDescriptor.getCurie();
        if (link != null && curie != null) {
            CurieDescriptor curieDescriptor = curieDescriptors.get(curie);
            if (curieDescriptor == null) {
                throw new IllegalArgumentException(String.format(
                        "No '%s' curie registered, cannot create Link for rel: %s", curie, linkDescriptor.getRel()));
            }
        }
        return link;
    }

    /**
     * Returns a Curie based on the provided CurieDescriptor.
     *
     * @param curieDescriptor the curieDescriptor to construct a Curie from
     * @return Curie based on curieDescriptor
     */
    public Curie getCurie(CurieDescriptor curieDescriptor) {
        return curieDescriptor.toCurie(linkContext);
    }
}
