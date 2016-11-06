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

import com.github.codeframes.hal.tooling.core.Link;
import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Template for creating a {@link Link}.
 */
public class LinkDescriptor {

    private final String rel;
    private final HrefTemplate hrefTemplate;
    private final String type;
    private final String deprecation;
    private final String name;
    private final String profile;
    private final String title;
    private final String hreflang;
    private final String condition;
    private final String curie;

    LinkDescriptor(String rel,
                   HrefTemplate hrefTemplate,
                   String type,
                   String deprecation,
                   String name,
                   String profile,
                   String title,
                   String hreflang,
                   String condition,
                   String curie) {
        this.rel = rel;
        this.hrefTemplate = hrefTemplate;
        this.type = type;
        this.deprecation = deprecation;
        this.name = name;
        this.profile = profile;
        this.title = title;
        this.hreflang = hreflang;
        this.condition = condition;
        this.curie = curie;
    }

    /**
     * Link relation name.
     */
    public String getRel() {
        return rel;
    }

    /**
     * Link href template.
     */
    public HrefTemplate getHrefTemplate() {
        return hrefTemplate;
    }

    /**
     * Optional. Used as a hint to indicate the media type expected when dereferencing the target resource.
     *
     * @return media type or {@code null} if no type was defined
     */
    @Nullable
    public String getType() {
        return type;
    }

    /**
     * Optional. Indicates that the link is to be deprecated (i.e. removed) at a future date. Its href is
     * a URL that SHOULD provide further information about the deprecation.
     *
     * @return URL or {@code null} if no deprecation was defined
     */
    @Nullable
    public String getDeprecation() {
        return deprecation;
    }

    /**
     * Optional. Used as a secondary key for selecting Link Objects which share the same relation type.
     *
     * @return name or {@code null} if no name was defined
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Optional. A URI that hints about the profile (as defined by <a href="https://tools.ietf.org/html/rfc6906">[RFC6906]</a>)
     * of the target resource.
     *
     * @return profile or {@code null} if no profile was defined
     */
    @Nullable
    public String getProfile() {
        return profile;
    }

    /**
     * Optional. Intended for labelling the link with a human-readable identifier (as defined by
     * <a href="https://tools.ietf.org/html/rfc5988">[RFC5988]</a>).
     *
     * @return title or {@code null} if no title was defined
     */
    @Nullable
    public String getTitle() {
        return title;
    }

    /**
     * Optional. Intended for indicating the language of the target resource (as defined by
     * <a href="https://tools.ietf.org/html/rfc5988">[RFC5988]</a>).
     *
     * @return hreflang or {@code null} if no hreflang was defined
     */
    @Nullable
    public String getHreflang() {
        return hreflang;
    }

    /**
     * Optional. EL expression that evaluates to a boolean, used to determine whether or not a
     * {@link Link Link} is to be injected.
     *
     * @return EL expression or {@code null} if no condition was defined
     */
    @Nullable
    public String getCondition() {
        return condition;
    }

    /**
     * Optional. The curie name used in {@link #getRel()}
     *
     * @return curie name used in {@link #getRel()} or {@code null} if {@link #getRel()} was not prefixed with a curie
     */
    @Nullable
    public String getCurie() {
        return curie;
    }

    /**
     * Returns a Link based on this descriptor for the given linkContext.
     *
     * @param linkContext the link context for resolving the link
     * @return Link based on this descriptor for the given linkContext or {@code null} if the condition evaluates to
     * {@code false}
     */
    @Nullable
    public Link toLink(LinkContext linkContext) {
        if (condition == null || linkContext.evaluateAsBoolean(condition)) {
            final Href href = hrefTemplate.resolve(linkContext);
            return new Link(
                    rel,
                    href.getValue(),
                    href.isTemplated(),
                    type,
                    deprecation,
                    name,
                    profile,
                    title,
                    hreflang
            );
        }
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rel, hrefTemplate, type, deprecation, name, profile, title, hreflang, condition, curie);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final LinkDescriptor other = (LinkDescriptor) obj;
        return Objects.equals(this.rel, other.rel)
                && Objects.equals(this.hrefTemplate, other.hrefTemplate)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.deprecation, other.deprecation)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.profile, other.profile)
                && Objects.equals(this.title, other.title)
                && Objects.equals(this.hreflang, other.hreflang)
                && Objects.equals(this.condition, other.condition)
                && Objects.equals(this.curie, other.curie);
    }

    @Override
    public String toString() {
        return "LinkDescriptor{" +
                "rel='" + rel + '\'' +
                ", hrefTemplate=" + hrefTemplate +
                ", type='" + type + '\'' +
                ", deprecation='" + deprecation + '\'' +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", title='" + title + '\'' +
                ", hreflang='" + hreflang + '\'' +
                ", condition='" + condition + '\'' +
                ", curie='" + curie + '\'' +
                '}';
    }
}
