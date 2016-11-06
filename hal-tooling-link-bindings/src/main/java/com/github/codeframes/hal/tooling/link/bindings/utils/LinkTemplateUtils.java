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
package com.github.codeframes.hal.tooling.link.bindings.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for extracting information from Link templates.
 */
public final class LinkTemplateUtils {

    private static final Pattern EXPR_LANG_PTRN = Pattern.compile("\\$\\{.*?\\}");
    private static final Pattern URI_TEMPLATE_EXPR_PTRN = Pattern.compile("(?<!\\$)\\{([?.+&;/#])?([\\w,]+)\\}");

    private LinkTemplateUtils() {
    }

    /**
     * Returns {@code true} if linkTemplate contains EL expressions, else {@code false}.
     *
     * @param linkTemplate the link template
     * @return {@code true} if linkTemplate contains EL expressions, else {@code false}
     */
    public static boolean containsEL(String linkTemplate) {
        return EXPR_LANG_PTRN.matcher(linkTemplate).find();
    }

    /**
     * Returns a list of parameter names (URI Template tokens) contained within the given linkTemplate.
     *
     * @param linkTemplate the link template
     * @return the list of parameter names contained within the given linkTemplate. If no parameters are present
     * (linkTemplate is not templated) then an empty list is returned
     */
    public static List<String> extractParameterNames(String linkTemplate) {
        final List<String> paramNames = new ArrayList<>();
        for (Matcher matcher = URI_TEMPLATE_EXPR_PTRN.matcher(linkTemplate); matcher.find(); ) {
            Collections.addAll(paramNames, matcher.group(2).split(","));
        }
        return paramNames.isEmpty() ? Collections.<String>emptyList() : Collections.unmodifiableList(paramNames);
    }

    /**
     * Returns {@code true} if linkTemplate is templated, else {@code false}.
     *
     * @param linkTemplate the link template
     * @return {@code true} if linkTemplate is templated, else {@code false}
     */
    public static boolean isTemplated(String linkTemplate) {
        return URI_TEMPLATE_EXPR_PTRN.matcher(linkTemplate).find();
    }

    /**
     * Returns {@code true} if linkTemplate represents an absolute Link Template, else {@code false}.
     *
     * @param linkTemplate the link template
     * @return {@code true} if linkTemplate represents an absolute Link Template, else {@code false}
     */
    public static boolean isAbsolute(String linkTemplate) {
        String valueWithoutExpressions = replaceExpressions(linkTemplate);
        try {
            return new URI(valueWithoutExpressions).isAbsolute();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static String replaceExpressions(String value) {
        String replacement = value;
        if (isTemplated(replacement)) {
            replacement = URI_TEMPLATE_EXPR_PTRN.matcher(replacement).replaceAll("?");
        }

        if (containsEL(replacement)) {
            replacement = EXPR_LANG_PTRN.matcher(replacement).replaceAll("?");
        }
        return replacement;
    }
}
