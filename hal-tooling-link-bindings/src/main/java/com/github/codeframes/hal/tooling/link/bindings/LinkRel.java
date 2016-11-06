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
package com.github.codeframes.hal.tooling.link.bindings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a field to be injected with a {@link com.github.codeframes.hal.tooling.core.Link Link}. Can only be used on
 * fields of type Link.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LinkRel {

    /**
     * Rel used if one is not provided: 'self', Conveys an identifier for the link's context.
     */
    String DEFAULT_REL = "self";
    /**
     * Marker value to specify an option is not used.
     */
    String UNSPECIFIED = "";

    /**
     * Binding Options.
     */
    enum BindingOption {
        /**
         * URI Template parameters are taken as referring to representation instance parameters and are subsequently
         * used for template expansion.
         */
        INSTANCE_PARAMETERS,
        /**
         * URI Template parameters that are not expanded and preserved as template expressions.
         */
        RETAIN_UNEXPANDED
    }

    /**
     * Link relation name.
     */
    String rel() default DEFAULT_REL;

    /**
     * Href that's either a URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or a
     * URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> for a resource. Note: This option is
     * mutually exclusive with {@link #resource()}
     */
    String value() default UNSPECIFIED;

    /**
     * Used as a hint to indicate the media type expected when dereferencing the target resource.
     */
    String type() default UNSPECIFIED;

    /**
     * Indicates that the link is to be deprecated (i.e. removed) at a future date. Its value is
     * a URL that SHOULD provide further information about the deprecation.
     */
    String deprecation() default UNSPECIFIED;

    /**
     * Used as a secondary key for selecting Link Objects which share the same relation type.
     */
    String name() default UNSPECIFIED;

    /**
     * A URI that hints about the profile (as defined by <a href="https://tools.ietf.org/html/rfc6906">[RFC6906]</a>)
     * of the target resource.
     */
    String profile() default UNSPECIFIED;

    /**
     * Intended for labelling the link with a human-readable identifier (as defined by
     * <a href="https://tools.ietf.org/html/rfc5988">[RFC5988]</a>).
     */
    String title() default UNSPECIFIED;

    /**
     * Intended for indicating the language of the target resource (as defined by
     * <a href="https://tools.ietf.org/html/rfc5988">[RFC5988]</a>).
     */
    String hreflang() default UNSPECIFIED;

    /**
     * The resource class to use for providing the href, either as a URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a>
     * or a URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a>. Note: This option is mutually
     * exclusive with {@link #value()}
     */
    Class<?> resource() default UNSPECIFIED.class;

    /**
     * The resource class method to use for providing a URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a>
     * or a URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a>.
     */
    String method() default UNSPECIFIED;

    /**
     * An EL expression that evaluates to a boolean, used to determine whether or not a
     * {@link com.github.codeframes.hal.tooling.core.Link Link} is to be injected.
     */
    String condition() default UNSPECIFIED;

    /**
     * Specifies the link href style. Defaults to {@link Style#ABSOLUTE_PATH}.
     */
    Style style() default Style.ABSOLUTE_PATH;

    /**
     * Specifies URI Template parameter bindings to be used in template expansion. Binding names are to match up with
     * an associated URI Template parameter and the values an EL expression, that when evaluated are used for parameter
     * substitution.
     */
    Binding[] bindings() default {};

    /**
     * Specifies any binding options to use for URI Template parameter expansion.
     */
    BindingOption[] bindingOptions() default {};

    /**
     * Marker class to specify an option is not used.
     */
    final class UNSPECIFIED {
        private UNSPECIFIED() {
        }
    }
}
