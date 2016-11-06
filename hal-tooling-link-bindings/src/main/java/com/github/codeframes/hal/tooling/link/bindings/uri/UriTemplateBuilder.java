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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class used for constructing basic URI Templates <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a>.
 */
public class UriTemplateBuilder {

    private static final Pattern PATH_PATTERN = Pattern.compile("^((?:/.*)|(?:[^/].*?))(/)?$");
    private static final Pattern TEMPLATED_QUERY_PARAM_PATTERN = Pattern.compile("^\\{?(.+)}?$");

    private final StringBuilder pathBuilder = new StringBuilder();
    private final List<String> templateQueryParams = new ArrayList<>();

    /**
     * Appends a value to the path that is to be constructed.
     *
     * @param value the value to append to the resultant path, can be a URI Template
     * @return {@code this}
     */
    public UriTemplateBuilder appendPath(String value) {
        Matcher matcher = PATH_PATTERN.matcher(value.trim());
        if (matcher.matches()) {
            String param = matcher.group(1);

            if (param.startsWith("/")) {
                if (pathBuilder.length() > 0 && pathBuilder.lastIndexOf("/") == pathBuilder.length() - 1) {
                    pathBuilder.append(param.substring(1));
                } else {
                    pathBuilder.append(param);
                }
            } else {
                if (pathBuilder.length() > 0 && pathBuilder.lastIndexOf("/") == pathBuilder.length() - 1) {
                    pathBuilder.append(param);
                } else {
                    pathBuilder.append('/').append(param);
                }
            }

            if (matcher.group(2) != null) {
                pathBuilder.append('/');
            }

        } else {
            throw new IllegalArgumentException("Not a valid path: " + value);
        }
        return this;
    }

    /**
     * Appends a query parameter name to the query parameter template that is to be constructed.
     *
     * @param name the query parameter name to append to the resultant query parameter template
     * @return {@code this}
     */
    public UriTemplateBuilder appendTemplatedQueryParam(String name) {
        Matcher matcher = TEMPLATED_QUERY_PARAM_PATTERN.matcher(name.trim());
        if (matcher.matches()) {
            String param = matcher.group(1);
            templateQueryParams.add(param);
        } else {
            throw new IllegalArgumentException("Not a valid template query param: " + name);
        }
        return this;
    }

    /**
     * Returns the resultant URI Template.
     *
     * @return the resultant URI Template
     */
    public String build() {
        StringBuilder sb = new StringBuilder(pathBuilder);
        if (!templateQueryParams.isEmpty()) {
            sb.append("{?");
            Iterator<String> itr = templateQueryParams.iterator();
            sb.append(itr.next());
            while (itr.hasNext()) {
                sb.append(',').append(itr.next());
            }
            sb.append('}');
        }
        return sb.toString();
    }
}
