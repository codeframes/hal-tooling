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
package com.github.codeframes.hal.tooling.json.representations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.codeframes.hal.tooling.core.Embedded;
import com.github.codeframes.hal.tooling.core.HalRepresentable;
import com.github.codeframes.hal.tooling.core.Link;
import com.github.codeframes.hal.tooling.utils.Validate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A Jackson serializable representation for the
 * <a href="http://nocarrier.co.uk/profiles/vnd.error/">application/vnd.error+json</a> media type. Used to express
 * errors in a standardised format that can be understood by many client applications. The media type is 100% compatible
 * with the HAL specification.
 */
@Immutable
public class VndError implements HalRepresentable {

    private final Link aboutLink;
    private final Link describesLink;
    private final Link helpLink;
    private final String logref;
    private final String message;
    private final String path;
    private final Embedded<List<VndError>> errors;

    /**
     * Constructs a new VndError Object.
     *
     * @param aboutLink     links to a resource that this error is related to
     * @param describesLink present if this error representation describes another representation of the error on the
     *                      server side
     * @param helpLink      links to a document describing the error
     * @param logref        numeric/alpha/alphanumeric identifier referring to the specific error on the server side for
     *                      logging purposes
     * @param message       a human readable message related to this error which may be displayed to the user of the api
     * @param path          expresses a JSON Pointer to a field in the related resource (contained in the 'about' link
     *                      relation) that this error is relevant for
     * @param errors        additional nested errors
     * @throws IllegalArgumentException if a given parameter is illegal
     */
    public VndError(@Nullable Link aboutLink,
                    @Nullable Link describesLink,
                    @Nullable Link helpLink,
                    @Nullable String logref,
                    String message,
                    @Nullable String path,
                    @Nullable List<VndError> errors) {
        this.aboutLink = verifyLinkRel("about", aboutLink);
        this.describesLink = verifyLinkRel("describes", describesLink);
        this.helpLink = verifyLinkRel("help", helpLink);
        this.logref = logref;
        this.message = Validate.hasText(message, "message");
        this.path = path;
        this.errors = toEmbeddedErrors(errors);
    }

    private VndError(Builder builder) {
        this(builder.aboutLink,
                builder.describesLink,
                builder.helpLink,
                builder.logref,
                builder.message,
                builder.path,
                builder.errors);
    }

    protected static Link verifyLinkRel(String rel, Link link) {
        if (link != null && !rel.equals(link.getRel())) {
            throw new IllegalArgumentException(String.format("Link.rel MUST be '%s', but got: '%s'", rel, link.getRel()));
        }
        return link;
    }

    @Nullable
    protected static Embedded<List<VndError>> toEmbeddedErrors(@Nullable List<VndError> errors) {
        if (errors != null && !errors.isEmpty()) {
            return new Embedded<>("errors", Collections.unmodifiableList(new ArrayList<>(errors)));
        }
        return null;
    }

    /**
     * Links to a resource that this error is related to. See
     * <a href="https://tools.ietf.org/html/rfc6903#section-2">[RFC6903]</a> for further details.
     *
     * @return about link or {@code null} if undefined
     */
    @Nullable
    public Link getAboutLink() {
        return aboutLink;
    }

    /**
     * Present if this error representation describes another representation of the error on the server side. See
     * <a href="https://tools.ietf.org/html/rfc6892">[RFC6892]</a> for further details.
     *
     * @return describes link or {@code null} if undefined
     */
    @Nullable
    public Link getDescribesLink() {
        return describesLink;
    }

    /**
     * Links to a document describing the error. This has the same definition as the help link relation in the
     * <a href="https://www.w3.org/TR/html5/links.html#link-type-help">HTML5 specification</a>.
     *
     * @return help link or {@code null} if undefined
     */
    @Nullable
    public Link getHelpLink() {
        return helpLink;
    }

    /**
     * A (numeric/alpha/alphanumeric) identifier referring to the specific error on the server side for logging purposes
     * (i.e. a request number).
     *
     * @return error identifier or {@code null} if undefined
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getLogref() {
        return logref;
    }

    /**
     * A human readable message related to this error which may be displayed to the user of the api.
     *
     * @return human readable error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Expresses a JSON Pointer <a href="https://tools.ietf.org/html/rfc6901">[RFC6901]</a> to a field in the related
     * resource (contained in the 'about' link relation) that this error is relevant for.
     *
     * @return JSON Pointer or {@code null} if undefined
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getPath() {
        return path;
    }

    /**
     * Additional nested errors.
     *
     * @return list of additional nested errors or {@code null} if undefined
     */
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Embedded<List<VndError>> getErrors() {
        return errors;
    }

    public static final class Builder {

        private Link aboutLink;
        private Link describesLink;
        private Link helpLink;
        private String logref;
        private String message;
        private String path;
        private List<VndError> errors;

        /**
         * OPTIONAL. Links to a resource that this error is related to. See
         * <a href="https://tools.ietf.org/html/rfc6903#section-2">[RFC6903]</a> for further details.
         *
         * @param aboutLink the about link
         * @return a reference to this object.
         */
        public Builder aboutLink(@Nullable Link aboutLink) {
            this.aboutLink = aboutLink;
            return this;
        }

        /**
         * OPTIONAL. Present if this error representation describes another representation of the error on the server
         * side. See <a href="https://tools.ietf.org/html/rfc6892">[RFC6892]</a> for further details.
         *
         * @param describesLink the describes link
         * @return a reference to this object.
         */
        public Builder describesLink(@Nullable Link describesLink) {
            this.describesLink = describesLink;
            return this;
        }

        /**
         * OPTIONAL. Links to a document describing the error. This has the same definition as the help link relation in
         * the <a href="https://www.w3.org/TR/html5/links.html#link-type-help">HTML5 specification</a>.
         *
         * @param helpLink the help link
         * @return a reference to this object.
         */
        public Builder helpLink(@Nullable Link helpLink) {
            this.helpLink = helpLink;
            return this;
        }

        /**
         * OPTIONAL. A (numeric/alpha/alphanumeric) identifier referring to the specific error on the server side for
         * logging purposes (i.e. a request number).
         *
         * @param logref the error identifier
         * @return a reference to this object.
         */
        public Builder logref(@Nullable String logref) {
            this.logref = logref;
            return this;
        }

        /**
         * A human readable message related to this error which may be displayed to the user of the api.
         *
         * @param message the human readable error message
         * @return a reference to this object.
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * OPTIONAL. Expresses a JSON Pointer <a href="https://tools.ietf.org/html/rfc6901">[RFC6901]</a> to a field in
         * the related resource (contained in the 'about' link relation) that this error is relevant for.
         *
         * @param path the JSON Pointer
         * @return a reference to this object.
         */
        public Builder path(@Nullable String path) {
            this.path = path;
            return this;
        }

        /**
         * OPTIONAL. Additional list of nested errors. If the provided list is empty, errors will be set to {@code null}.
         *
         * @param errors the list of errors
         * @return a reference to this object.
         */
        public Builder errors(@Nullable List<VndError> errors) {
            this.errors = errors;
            return this;
        }

        /**
         * @return a new VndError instance with the provided properties as configured by this Builder
         * @throws IllegalArgumentException if a given parameter is illegal
         */
        public VndError build() {
            return new VndError(this);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(aboutLink, describesLink, helpLink, logref, message, path, errors);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VndError other = (VndError) obj;
        return Objects.equals(aboutLink, other.aboutLink) &&
                Objects.equals(describesLink, other.describesLink) &&
                Objects.equals(helpLink, other.helpLink) &&
                Objects.equals(logref, other.logref) &&
                Objects.equals(message, other.message) &&
                Objects.equals(path, other.path) &&
                Objects.equals(errors, other.errors);
    }

    @Override
    public String toString() {
        return "VndError{" +
                "aboutLink=" + aboutLink +
                ", describesLink=" + describesLink +
                ", helpLink=" + helpLink +
                ", logref='" + logref + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", errors=" + errors +
                '}';
    }
}
