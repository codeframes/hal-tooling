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
package com.github.codeframes.hal.tooling.link.bindings.jaxrs;

import com.github.codeframes.hal.tooling.link.bindings.api.LinkTemplateFactory;
import com.github.codeframes.hal.tooling.link.bindings.types.LinkRelType;
import com.github.codeframes.hal.tooling.link.bindings.uri.UriTemplateBuilder;
import com.github.codeframes.hal.tooling.link.bindings.utils.LinkTemplateUtils;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A JAX-RS specific LinkTemplateFactory.
 */
public class JaxRsLinkTemplateFactory implements LinkTemplateFactory {

    private static final Pattern PATH_PARAMETER_REGEX_PATTERN = Pattern.compile("\\{(([^}:]+):[^/]+)}");

    @Override
    public String createLinkTemplate(LinkRelType linkRelType) {

        String href = linkRelType.getHref();
        if (href != null && LinkTemplateUtils.isAbsolute(href)) {
            return href;
        }

        UriTemplateBuilder templateBuilder = new UriTemplateBuilder();
        LinkRelType.ResourceMethod resourceMethod = linkRelType.getResourceMethod();

        if (resourceMethod != null) {
            appendResourceClassPath(templateBuilder, resourceMethod.getResourceClass());

            String methodName = resourceMethod.getMethodName();
            if (methodName != null) {
                appendResourceMethodPath(templateBuilder, methodName, resourceMethod.getResourceClass());
            }
        } else if (href != null) {
            templateBuilder.appendPath(href);
        }
        return templateBuilder.build();
    }

    private static void appendResourceClassPath(UriTemplateBuilder templateBuilder, Class<?> resourceClass) {
        Path path = resourceClass.getAnnotation(Path.class);
        if (path != null) {
            String value = stripPathParamRegex(path.value());
            templateBuilder.appendPath(value);
        } else {
            throw new UnsupportedOperationException(String.format("No @Path found on '%s', only root resources are supported", resourceClass));
        }
    }

    private static void appendResourceMethodPath(UriTemplateBuilder templateBuilder, String methodName, Class<?> resourceClass) {
        for (Method method : getMethods(resourceClass)) {
            if (method.getName().equals(methodName)) {
                Path path = method.getAnnotation(Path.class);
                if (path != null) {
                    String value = stripPathParamRegex(path.value());
                    templateBuilder.appendPath(value);
                }
                appendQueryParams(templateBuilder, method);
            }
        }
    }

    private static String stripPathParamRegex(String path) {
        StringBuilder pathBuilder = new StringBuilder(path);
        int offset = 0;
        for (Matcher matcher = PATH_PARAMETER_REGEX_PATTERN.matcher(path); matcher.find(); ) {
            pathBuilder.replace(matcher.start(1) - offset, matcher.end(1) - offset, matcher.group(2));
            offset += matcher.group(1).length() - matcher.group(2).length();
        }
        return pathBuilder.toString();
    }

    private static List<Method> getMethods(Class<?> type) {
        List<Method> methods = new ArrayList<>();
        for (Method method : type.getMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(HttpMethod.class)) {
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    private static void appendQueryParams(UriTemplateBuilder templateBuilder, Method method) {
        for (Annotation[] paramAnnos : method.getParameterAnnotations()) {
            for (Annotation anno : paramAnnos) {
                if (anno.annotationType() == QueryParam.class) {
                    templateBuilder.appendTemplatedQueryParam(((QueryParam) anno).value());
                }
            }
        }
    }
}
