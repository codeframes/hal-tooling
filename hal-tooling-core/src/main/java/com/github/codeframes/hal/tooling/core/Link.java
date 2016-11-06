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
package com.github.codeframes.hal.tooling.core;

import com.github.codeframes.hal.tooling.utils.Validate;
import com.github.codeframes.hal.tooling.utils.UriTemplateUtil;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * HAL Link Object.
 */
@Immutable
public final class Link implements RelationType {

    private final String rel;
    private final String href;
    private final boolean templated;
    private final String type;
    private final String deprecation;
    private final String name;
    private final String profile;
    private final String title;
    private final String hreflang;

    /**
     * Convenience constructor for creating 'self' Link Objects with only mandatory properties provided.
     *
     * @param href URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or a
     *             URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> for a resource.
     *             <p>
     *             If the value is a URI Template, the Link Object constructed will have a 'templated' property
     *             of {@code true}.
     *             </p>
     */
    public Link(String href) {
        this(Rels.SELF, href, UriTemplateUtil.isTemplated(href), null, null, null, null, null, null);
    }

    /**
     * Convenience constructor for creating Link Objects with only mandatory properties provided.
     *
     * @param rel  link relation name
     * @param href URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or a
     *             URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> for a resource.
     *             <p>
     *             If the value is a URI Template, the Link Object constructed will have a 'templated' property
     *             of {@code true}.
     *             </p>
     */
    public Link(String rel, String href) {
        this(rel, href, UriTemplateUtil.isTemplated(href), null, null, null, null, null, null);
    }

    /**
     * Constructs a new Link Object.
     *
     * @param rel         link relation name
     * @param href        URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or a
     *                    URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> for a resource. If a
     *                    URI Template the templated parameter SHOULD be set tp {@code true}
     * @param templated   indicates whether href is templated.
     * @param type        used as a hint to indicate the media type expected when dereferencing the target resource. May
     *                    be {@code null} but not empty
     * @param deprecation indicates that the link is to be deprecated (i.e. removed) at a future date. Its value is
     *                    a URL that SHOULD provide further information about the deprecation. May be {@code null} but
     *                    not empty
     * @param name        used as a secondary key for selecting Link Objects which share the same relation type. May be
     *                    {@code null} but not empty
     * @param profile     a URI that hints about the profile (as defined by <a href="https://tools.ietf.org/html/rfc6906">[RFC6906]</a>)
     *                    of the target resource. May be {@code null} but not empty
     * @param title       intended for labelling the link with a human-readable identifier (as defined by
     *                    <a href="https://tools.ietf.org/html/rfc5988">[RFC5988]</a>). May be {@code null} but not empty
     * @param hreflang    intended for indicating the language of the target resource (as defined by
     *                    <a href="https://tools.ietf.org/html/rfc5988">[RFC5988]</a>). May be {@code null} but not empty
     */
    public Link(String rel,
                String href,
                boolean templated,
                @Nullable String type,
                @Nullable String deprecation,
                @Nullable String name,
                @Nullable String profile,
                @Nullable String title,
                @Nullable String hreflang) {
        this.rel = Validate.hasText(rel, "rel");
        this.href = Validate.hasText(href, "href");
        this.templated = templated;
        this.type = Validate.isNullOrHasText(type, "type");
        this.deprecation = Validate.isNullOrHasText(deprecation, "deprecation");
        this.name = Validate.isNullOrHasText(name, "name");
        this.profile = Validate.isNullOrHasText(profile, "profile");
        this.title = Validate.isNullOrHasText(title, "title");
        this.hreflang = Validate.isNullOrHasText(hreflang, "hreflang");
    }

    private Link(Builder builder) {
        this(builder.rel,
                builder.href,
                builder.templated,
                builder.type,
                builder.deprecation,
                builder.name,
                builder.profile,
                builder.title,
                builder.hreflang);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRel() {
        return rel;
    }

    /**
     * URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or a
     * URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> for a resource.
     *
     * @return URI or URI Template for a resource
     */
    public String getHref() {
        return href;
    }

    /**
     * Indicates whether href is templated.
     *
     * @return {@code true} if href is a URI Template else {@code false}
     */
    public boolean isTemplated() {
        return templated;
    }

    /**
     * Used as a hint to indicate the media type expected when dereferencing the target resource.
     *
     * @return link media type or {@code null} if undefined
     */
    @Nullable
    public String getType() {
        return type;
    }

    /**
     * Indicates that the link is to be deprecated (i.e. removed) at a future date. Its value is
     * a URL that SHOULD provide further information about the deprecation.
     *
     * @return link deprecation URL or {@code null} if undefined
     */
    @Nullable
    public String getDeprecation() {
        return deprecation;
    }

    /**
     * Used as a secondary key for selecting Link Objects which share the same relation type.
     *
     * @return link name or {@code null} if undefined
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * A URI that hints about the profile (as defined by <a href="https://tools.ietf.org/html/rfc6906">[RFC6906]</a>)
     * of the target resource.
     *
     * @return link profile URI or {@code null} if undefined
     */
    @Nullable
    public String getProfile() {
        return profile;
    }

    /**
     * Intended for labelling the link with a human-readable identifier (as defined by
     * <a href="https://tools.ietf.org/html/rfc5988">[RFC5988]</a>).
     *
     * @return link title or {@code null} if undefined
     */
    @Nullable
    public String getTitle() {
        return title;
    }

    /**
     * Intended for indicating the language of the target resource (as defined by
     * <a href="https://tools.ietf.org/html/rfc5988">[RFC5988]</a>).
     *
     * @return link hreflang or {@code null} if undefined
     */
    @Nullable
    public String getHreflang() {
        return hreflang;
    }

    public static final class Builder {

        private String rel;
        private String href;
        boolean templated;
        private String type;
        private String deprecation;
        private String name;
        private String profile;
        private String title;
        private String hreflang;

        /**
         * @param rel link relation name
         * @return a reference to this object.
         */
        public Builder rel(String rel) {
            this.rel = rel;
            return this;
        }

        /**
         * URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or a
         * URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> for a resource.
         * <p>
         * If the value is a URI Template then the Link Object SHOULD have a 'templated' property
         * whose value is {@code true}.
         * </p>
         *
         * @param href URI or URI Template for a resource
         * @return a reference to this object.
         */
        public Builder href(String href) {
            this.href = href;
            return this;
        }

        /**
         * Indicates whether href is templated.
         *
         * @param templated SHOULD be {@code true} if href is a URI Template else {@code false}.
         *                  Defaults to {@code false}
         * @return a reference to this object.
         */
        public Builder templated(boolean templated) {
            this.templated = templated;
            return this;
        }

        /**
         * Used as a hint to indicate the media type expected when dereferencing the target resource.
         *
         * @param type media type
         * @return a reference to this object.
         */
        public Builder type(@Nullable String type) {
            this.type = type;
            return this;
        }

        /**
         * Indicates that the link is to be deprecated (i.e. removed) at a future date. Its value is
         * a URL that SHOULD provide further information about the deprecation.
         *
         * @param deprecation URL providing further information about the deprecation.
         * @return a reference to this object.
         */
        public Builder deprecation(@Nullable String deprecation) {
            this.deprecation = deprecation;
            return this;
        }

        /**
         * Used as a secondary key for selecting Link Objects which share the same relation type.
         *
         * @param name the link name
         * @return a reference to this object.
         */
        public Builder name(@Nullable String name) {
            this.name = name;
            return this;
        }

        /**
         * A URI that hints about the profile (as defined by <a href="https://tools.ietf.org/html/rfc6906">[RFC6906]</a>)
         * of the target resource.
         *
         * @param profile the profile URI
         * @return a reference to this object.
         */
        public Builder profile(@Nullable String profile) {
            this.profile = profile;
            return this;
        }

        /**
         * Intended for labelling the link with a human-readable identifier (as defined by
         * <a href="https://tools.ietf.org/html/rfc5988">[RFC5988]</a>).
         *
         * @param title the link title
         * @return a reference to this object.
         */
        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        /**
         * Intended for indicating the language of the target resource (as defined by
         * <a href="https://tools.ietf.org/html/rfc5988">[RFC5988]</a>).
         *
         * @param hreflang the language of the target resource
         * @return a reference to this object.
         */
        public Builder hreflang(@Nullable String hreflang) {
            this.hreflang = hreflang;
            return this;
        }

        /**
         * @return a new Link instance with the provided properties as configured by this Builder
         */
        public Link build() {
            return new Link(this);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(rel, href, templated, type, deprecation, name, profile, title, hreflang);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Link other = (Link) obj;
        return Objects.equals(this.rel, other.rel)
                && Objects.equals(this.href, other.href)
                && Objects.equals(this.templated, other.templated)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.deprecation, other.deprecation)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.profile, other.profile)
                && Objects.equals(this.title, other.title)
                && Objects.equals(this.hreflang, other.hreflang);
    }

    @Override
    public String toString() {
        return "Link{" +
                "rel='" + rel + '\'' +
                ", href='" + href + '\'' +
                ", templated=" + templated +
                ", type='" + type + '\'' +
                ", deprecation='" + deprecation + '\'' +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", title='" + title + '\'' +
                ", hreflang='" + hreflang + '\'' +
                '}';
    }
}
