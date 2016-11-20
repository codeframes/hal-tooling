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
package com.github.codeframes.hal.tooling.link.bindings.types;

import com.github.codeframes.hal.tooling.core.Rels;
import com.github.codeframes.hal.tooling.link.bindings.Binding;
import com.github.codeframes.hal.tooling.link.bindings.LinkRel;
import com.github.codeframes.hal.tooling.link.bindings.LinkRel.BindingOption;
import com.github.codeframes.hal.tooling.link.bindings.Style;
import com.github.codeframes.hal.tooling.utils.Validate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A POJO representation of {@link LinkRel}
 */
@Immutable
public final class LinkRelType {

    private final String rel;
    private final String href;
    private final String type;
    private final String deprecation;
    private final String name;
    private final String profile;
    private final String title;
    private final String hreflang;
    private final String curie;
    private final ResourceMethod resourceMethod;
    private final String condition;
    private final Map<String, String> bindings;
    private final Style style;
    private final Set<BindingOption> bindingOptions;

    private LinkRelType(String rel,
                        String href,
                        String type,
                        String deprecation,
                        String name,
                        String profile,
                        String title,
                        String hreflang,
                        String curie,
                        ResourceMethod resourceMethod,
                        String condition,
                        Map<String, String> bindings,
                        Style style,
                        Set<BindingOption> bindingOptions) {
        this.rel = rel;
        this.href = href;
        this.type = type;
        this.deprecation = deprecation;
        this.name = name;
        this.profile = profile;
        this.title = title;
        this.hreflang = hreflang;
        this.curie = curie;
        this.resourceMethod = resourceMethod;
        this.condition = condition;
        this.bindings = bindings;
        this.style = style;
        this.bindingOptions = bindingOptions;
    }

    /**
     * Constructs a new LinkRelType based off the given linkRel.
     *
     * @param linkRel the linkRel of which to create a LinkRelType from
     * @return a new LinkRelType based off the given linkRel.
     * @throws IllegalArgumentException if a linkRel value or a combination of values is considered to be illegal
     */
    public static LinkRelType valueOf(LinkRel linkRel) {

        String rel = Validate.hasText(linkRel.rel(), "LinkRel.rel");
        ResourceMethod resourceMethod = toResourceMethod(linkRel);

        String href = null;
        if (resourceMethod == null) {
            href = Validate.hasText(linkRel.value(), "LinkRel.value");
        } else if (!linkRel.value().isEmpty()) {
            throw new IllegalArgumentException("Cannot specify LinkRel.value and LinkRel.resource, they are mutually exclusive");
        }

        String type = nullOnEmpty(linkRel.type());
        String deprecation = nullOnEmpty(linkRel.deprecation());
        String name = nullOnEmpty(linkRel.name());
        String profile = nullOnEmpty(linkRel.profile());
        String title = nullOnEmpty(linkRel.title());
        String hreflang = nullOnEmpty(linkRel.hreflang());
        String curie = Rels.getCuriePrefix(linkRel.rel());
        String condition = nullOnEmpty(linkRel.condition());
        Map<String, String> bindings = toBindings(linkRel);
        Style style = linkRel.style();
        Set<BindingOption> bindingOptions = toBindingOptions(linkRel);

        return new LinkRelType(rel, href, type, deprecation, name, profile, title, hreflang, curie, resourceMethod, condition, bindings, style, bindingOptions);
    }

    private static ResourceMethod toResourceMethod(LinkRel linkRel) {
        ResourceMethod resourceMethod = null;
        if (LinkRel.UNSPECIFIED.class.equals(linkRel.resource())) {
            if (!LinkRel.UNSPECIFIED.equals(linkRel.method())) {
                throw new IllegalArgumentException("Cannot specify LinkRel.method without also specifying LinkRel.resource");
            }
        } else {
            resourceMethod = ResourceMethod.newInstance(linkRel.resource(), nullOnEmpty(linkRel.method()));
        }
        return resourceMethod;
    }

    private static String nullOnEmpty(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private static Map<String, String> toBindings(LinkRel linkRel) {
        Binding[] bindings = linkRel.bindings();
        if (bindings.length == 0) {
            return Collections.emptyMap();
        } else {
            Map<String, String> bindingsMap = new HashMap<>(bindings.length);
            for (Binding binding : bindings) {
                String name = Validate.hasText(binding.name(), "Binding.name");
                String value = Validate.hasText(binding.value(), "Binding.value");
                bindingsMap.put(name, value);
            }
            return Collections.unmodifiableMap(bindingsMap);
        }
    }

    private static Set<BindingOption> toBindingOptions(LinkRel linkRel) {
        final Set<BindingOption> bindingOptions;
        List<BindingOption> bos = Arrays.asList(linkRel.bindingOptions());
        if (bos.isEmpty()) {
            bindingOptions = Collections.emptySet();
        } else {
            bindingOptions = EnumSet.copyOf(bos);
        }

        if (bindingOptions.contains(BindingOption.INSTANCE_PARAMETERS)
                && bindingOptions.contains(BindingOption.INSTANCE_PARAMETERS_SNAKE_CASE)) {
            throw new IllegalArgumentException(String.format("Cannot specify both LinkRel.bindings %s and %s, they are mutually exclusive",
                    BindingOption.INSTANCE_PARAMETERS, BindingOption.INSTANCE_PARAMETERS_SNAKE_CASE));
        }
        return bindingOptions;
    }

    /**
     * Link relation name.
     */
    public String getRel() {
        return rel;
    }

    /**
     * Mutually exclusive with {@link #getResourceMethod()}. A URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a>
     * or a URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> for a resource.
     *
     * @return URI/URI Template or {@code null} if a resource method is defined
     */
    @Nullable
    public String getHref() {
        return href;
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
     * Optional. The curie name used in {@link #getRel()}
     *
     * @return curie name used in {@link #getRel()} or {@code null} if {@link #getRel()} was not prefixed with a curie
     */
    @Nullable
    public String getCurie() {
        return curie;
    }

    /**
     * Mutually exclusive with {@link #getHref()}. A resource method to use for providing a URI
     * <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or a URI Template
     * <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a>.
     *
     * @return the resource method providing the href or {@code null} if not defined
     */
    @Nullable
    public ResourceMethod getResourceMethod() {
        return resourceMethod;
    }

    /**
     * Optional. EL expression that evaluates to a boolean, used to determine whether or not a
     * {@link com.github.codeframes.hal.tooling.core.Link Link} is to be injected.
     *
     * @return EL expression or {@code null} if no condition was defined
     */
    @Nullable
    public String getCondition() {
        return condition;
    }

    /**
     * Optional URI Template parameter bindings. The bindings are used in template expansion where binding names are to
     * match up with an associated URI Template parameter and values an EL expression, that when evaluated are used
     * for parameter substitution.
     *
     * @return a Map of URI Template parameter bindings where; Key: parameter name, Value: EL expression, that when
     * evaluated is used for parameter substitution. If no bindings have been specified returns an empty Map.
     */
    public Map<String, String> getBindings() {
        return bindings;
    }

    /**
     * The link href style.
     */
    public Style getStyle() {
        return style;
    }

    /**
     * Optional Set of BindingOption's to control the behaviour of template expansion.
     *
     * @return the Set of BindingOption's or an empty Set if no binding options are defined
     */
    public Set<BindingOption> getBindingOptions() {
        return bindingOptions;
    }

    /**
     * Encapsulates a resource method.
     */
    @Immutable
    public static final class ResourceMethod {

        private final Class<?> resourceClass;
        private final String methodName;

        private ResourceMethod(Class<?> resourceClass, String methodName) {
            this.resourceClass = resourceClass;
            this.methodName = methodName;
        }

        static ResourceMethod newInstance(Class<?> resourceClass, @Nullable String methodName) {
            if (methodName != null && !hasMethod(resourceClass, methodName)) {
                throw new IllegalArgumentException(
                        String.format("No accessible '%s' method was found on type: %s", methodName, resourceClass));
            }
            return new ResourceMethod(resourceClass, methodName);
        }

        private static boolean hasMethod(Class<?> clazz, String name) {
            for (Method method : clazz.getMethods()) {
                if (name.equals(method.getName())) {
                    return true;
                }
            }
            return false;
        }

        /**
         * The resource class to use for providing a URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or
         * a URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a>.
         */
        public Class<?> getResourceClass() {
            return resourceClass;
        }

        /**
         * An optional resource class method to use for providing a URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a>
         * or a URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a>.
         *
         * @return a method name or {@code null}
         */
        @Nullable
        public String getMethodName() {
            return methodName;
        }
    }
}
