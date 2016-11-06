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
package com.github.codeframes.hal.tooling.link.bindings.uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> Expander. Expansion is supported up to
 * level 3.
 */
public class UriTemplateExpander {

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{([?.+/&#;])?([\\w,]+)\\}");

    private final Map<Class<?>, UriValueResolver<?>> uriValueResolvers;

    /**
     * Constructs a new UriTemplateExpander with no configured uriValueResolvers, meaning all value types are resolved
     * using {@link Object#toString() toString()}.
     */
    public UriTemplateExpander() {
        this(Collections.<Class<?>, UriValueResolver<?>>emptyMap());
    }

    /**
     * Constructs a new UriTemplateExpander with given uriValueResolvers.
     *
     * @param uriValueResolvers a list of UriValueResolver's to use for resolving value types to Strings used for
     *                          expansion parameter substitution.
     */
    public UriTemplateExpander(List<UriValueResolver<?>> uriValueResolvers) {
        this(toMap(uriValueResolvers));
    }

    private UriTemplateExpander(Map<Class<?>, UriValueResolver<?>> uriValueResolvers) {
        this.uriValueResolvers = uriValueResolvers;
    }

    private static Map<Class<?>, UriValueResolver<?>> toMap(List<UriValueResolver<?>> resolvers) {
        final Map<Class<?>, UriValueResolver<?>> map = new HashMap<>();
        for (UriValueResolver<?> resolver : resolvers) {
            map.put(resolver.getType(), resolver);
        }
        return map;
    }

    /**
     * Expands the given template and returns a URI or URI Template dependant on parameters given. Note: Expansion is
     * supported up to level 3.
     *
     * @param template         a URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> to be expanded
     * @param values           a map of values to use for parameter substitution
     * @param removeUnexpanded determines whether or not to preserve templated parameters where no substitution value is
     *                         available. {@code true} to remove, {@code false} to retain
     * @return a URI or URI Template dependant on parameters given
     */
    public String expand(String template, Map<String, Object> values, boolean removeUnexpanded) {
        StringBuilder expansionBuilder = new StringBuilder();
        int i = 0;
        for (Matcher matcher = TEMPLATE_PATTERN.matcher(template); matcher.find(); ) {

            int start = matcher.start();
            if (i != start) {
                String value = template.substring(i, start);
                expansionBuilder.append(value);
            }

            String operator = matcher.group(1) == null ? "" : matcher.group(1);
            String[] templateNames = matcher.group(2).split(",");

            expand(expansionBuilder, ExpressionOperator.forOperator(operator), templateNames, values, removeUnexpanded);

            i = matcher.end();
        }

        if (i != template.length()) {
            expansionBuilder.append(template.substring(i, template.length()));
        }
        return expansionBuilder.toString();
    }

    private void expand(StringBuilder expansionBuilder,
                        ExpressionOperator expressionOperator,
                        String[] names,
                        Map<String, Object> values,
                        boolean removeUnexpanded) {

        if (removeUnexpanded) {
            expandRemoveUnExpanded(expansionBuilder, expressionOperator, names, values);
        } else {
            expandRestoreUnExpanded(expansionBuilder, expressionOperator, names, values);
        }
    }

    private void expandRestoreUnExpanded(StringBuilder expansionBuilder,
                                         ExpressionOperator expressionOperator,
                                         String[] names,
                                         Map<String, Object> values) {

        Set<Entry<String, String>> expanded = new LinkedHashSet<>();
        Set<String> unExpanded = new LinkedHashSet<>();
        for (String name : names) {
            Object value = values.get(name);
            if (value == null) {
                unExpanded.add(name);
            } else {
                expanded.add(new SimpleImmutableEntry<>(name, toReplacementValue(value)));
            }
        }

        if (expanded.isEmpty()) {
            if (!unExpanded.isEmpty()) {
                expansionBuilder.append(expressionOperator.template(unExpanded));
            }
        } else {
            expansionBuilder.append(expressionOperator.expand(expanded));
            if (!unExpanded.isEmpty()) {
                expansionBuilder.append(expressionOperator.appendTemplate(unExpanded));
            }
        }
    }

    private void expandRemoveUnExpanded(StringBuilder expansionBuilder,
                                        ExpressionOperator expressionOperator,
                                        String[] names,
                                        Map<String, Object> values) {

        Set<Entry<String, String>> expanded = new LinkedHashSet<>();
        for (String name : names) {
            Object value = values.get(name);
            if (value != null) {
                expanded.add(new SimpleImmutableEntry<>(name, toReplacementValue(value)));
            }
        }

        if (!expanded.isEmpty()) {
            expansionBuilder.append(expressionOperator.expand(expanded));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String toReplacementValue(Object value) {
        final UriValueResolver resolver = uriValueResolvers.get(value.getClass());
        String text = value.toString();
        try {
            if (!text.contains("/")) {
                text = URLEncoder.encode(text, "UTF-8");
            }
            return resolver == null ? text : resolver.resolve(value);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Could not url encode: " + text, e);
        }
    }

    private enum ExpressionOperator {
        STRING("", "", "", ","),
        RESERVED("+", "", "+", ","),
        FRAGMENT("#", "#", "#", ","),
        LABEL(".", ".", ".", "."),
        PATH("/", "/", "/", "/"),
        PATH_STYLE_PARAMETERS(";", ";", ";", ";") {
            @Override
            String expand(Entry<String, String> value) {
                return value.getKey() + '=' + value.getValue();
            }
        },
        FORM_STYLE_QUERY("?", "?", "&", "&") {
            @Override
            String expand(Entry<String, String> value) {
                return value.getKey() + '=' + value.getValue();
            }
        },
        FORM_STYLE_QUERY_CONTINUATION("&", "&", "&", "&") {
            @Override
            String expand(Entry<String, String> value) {
                return value.getKey() + '=' + value.getValue();
            }
        };

        private final String operator;
        private final String expandedOperator;
        private final String templateOperator;
        private final String expandedSeparator;

        ExpressionOperator(String operator, String expandedOperator, String templateOperator, String expandedSeparator) {
            this.operator = operator;
            this.expandedOperator = expandedOperator;
            this.templateOperator = templateOperator;
            this.expandedSeparator = expandedSeparator;
        }

        static ExpressionOperator forOperator(String operator) {
            for (ExpressionOperator expressionOperator : values()) {
                if (expressionOperator.operator.equals(operator)) {
                    return expressionOperator;
                }
            }
            throw new IllegalArgumentException("No ExpressionOperator constant found for operator: " + operator);
        }

        String expand(Set<Entry<String, String>> values) {
            StringBuilder sb = new StringBuilder(expandedOperator);
            for (Entry<String, String> value : values) {
                sb.append(expand(value)).append(expandedSeparator);
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }

        String expand(Entry<String, String> value) {
            return value.getValue();
        }

        String template(Set<String> names) {
            return appendTemplate(names, operator, ",");
        }

        String appendTemplate(Set<String> names) {
            return appendTemplate(names, ",");
        }

        String appendTemplate(Set<String> names, String separator) {
            return appendTemplate(names, templateOperator, separator);
        }

        String appendTemplate(Set<String> names, String operator, String separator) {
            StringBuilder sb = new StringBuilder();
            sb.append('{').append(operator);
            for (String name : names) {
                sb.append(name).append(separator);
            }
            sb.setCharAt(sb.length() - 1, '}');
            return sb.toString();
        }
    }
}
