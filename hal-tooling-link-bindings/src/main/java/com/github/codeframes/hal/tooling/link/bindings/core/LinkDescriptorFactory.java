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

import com.github.codeframes.hal.tooling.link.bindings.CurieDef;
import com.github.codeframes.hal.tooling.link.bindings.CurieDefs;
import com.github.codeframes.hal.tooling.link.bindings.LinkRel;
import com.github.codeframes.hal.tooling.link.bindings.LinkRel.BindingOption;
import com.github.codeframes.hal.tooling.link.bindings.LinkRels;
import com.github.codeframes.hal.tooling.link.bindings.api.LinkTemplateFactory;
import com.github.codeframes.hal.tooling.link.bindings.context.LinkELContext;
import com.github.codeframes.hal.tooling.link.bindings.types.CurieType;
import com.github.codeframes.hal.tooling.link.bindings.types.LinkRelType;
import com.github.codeframes.hal.tooling.link.bindings.utils.LinkTemplateUtils;
import com.github.codeframes.hal.tooling.link.bindings.utils.TextUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Factory for creating {@link LinkDescriptor}, {@link CurieDescriptor} and {@link CurieDescriptors} based on
 * associated annotations; {@link LinkRel}, {@link LinkRels}, {@link CurieDef}, {@link CurieDefs}.
 */
public class LinkDescriptorFactory {

    private final LinkTemplateFactory linkTemplateFactory;

    /**
     * Constructs a LinkDescriptorFactory with the provided linkTemplateFactory.
     *
     * @param linkTemplateFactory the factory to use for creating Link Templates
     */
    public LinkDescriptorFactory(LinkTemplateFactory linkTemplateFactory) {
        this.linkTemplateFactory = linkTemplateFactory;
    }

    /**
     * Returns a LinkDescriptor based on the provided linkRel.
     *
     * @param declaringClass the class defining the field of which the LinkRel annotation is on
     * @param linkRel        the linkRel to construct a LinkDescriptor from
     * @return LinkDescriptor based on linkRel
     * @throws LinkDescriptorFactoryException if an error occurs while introspecting the declaringClass
     */
    public LinkDescriptor createLinkDescriptor(Class<?> declaringClass, LinkRel linkRel) {
        LinkRelType linkRelType = LinkRelType.valueOf(linkRel);
        String template = linkTemplateFactory.createLinkTemplate(linkRelType);

        Map<String, String> bindings = linkRelType.getBindings();
        Set<BindingOption> bindingOptions = linkRelType.getBindingOptions();

        bindings = applyBindingOptions(declaringClass, template, bindings, bindingOptions);

        boolean removeUnexpanded = isRemoveUnexpanded(template, bindings, bindingOptions);

        HrefTemplate hrefTemplate = new HrefTemplate(template, linkRelType.getStyle(), bindings, removeUnexpanded);

        return new LinkDescriptor(
                linkRelType.getRel(),
                hrefTemplate,
                linkRelType.getType(),
                linkRelType.getDeprecation(),
                linkRelType.getName(),
                linkRelType.getProfile(),
                linkRelType.getTitle(),
                linkRelType.getHreflang(),
                linkRelType.getCondition(),
                linkRelType.getCurie());
    }

    private static Map<String, String> applyBindingOptions(Class<?> declaringClass,
                                                           String template,
                                                           Map<String, String> bindings,
                                                           Set<BindingOption> bindingOptions) {

        boolean isInstanceParameters = bindingOptions.contains(BindingOption.INSTANCE_PARAMETERS);
        boolean isInstanceParametersSnakeCase = bindingOptions.contains(BindingOption.INSTANCE_PARAMETERS_SNAKE_CASE);
        boolean isUriParameters = bindingOptions.contains(BindingOption.URI_PARAMETERS);

        if (!isInstanceParameters && !isInstanceParametersSnakeCase && !isUriParameters) {
            return bindings;
        } else {
            final List<String> parameterNames = LinkTemplateUtils.extractParameterNames(template);
            Map<String, String> paramBindings = null;

            if (isInstanceParameters || isInstanceParametersSnakeCase) {
                BeanInfo beanInfo = getBeanInfo(declaringClass);
                paramBindings = withInstanceParamBindings(beanInfo, parameterNames, bindings, isInstanceParametersSnakeCase);
            }

            if (isUriParameters) {
                if (paramBindings == null) {
                    paramBindings = new HashMap<>(bindings);
                }

                for (String parameterName : parameterNames) {
                    if (!paramBindings.containsKey(parameterName)) {
                        paramBindings.put(parameterName, LinkELContext.toUriParameterExpression(parameterName));
                    }
                }
            }
            return paramBindings == null ? bindings : paramBindings;
        }
    }

    private static BeanInfo getBeanInfo(Class<?> beanClass) {
        try {
            return Introspector.getBeanInfo(beanClass);
        } catch (IntrospectionException e) {
            throw new LinkDescriptorFactoryException(
                    String.format("Could not introspect class: %s, required for link binding resolution", beanClass), e);
        }
    }

    private static Map<String, String> withInstanceParamBindings(BeanInfo beanInfo,
                                                                 List<String> parameterNames,
                                                                 Map<String, String> bindings,
                                                                 boolean viewParamsAsSnakeCase) {

        Map<String, String> paramBindings = new HashMap<>(bindings);
        for (String parameterName : parameterNames) {
            if (paramBindings.containsKey(parameterName)) {
                continue;
            }

            final String parameter;
            if (viewParamsAsSnakeCase) {
                parameter = TextUtils.snakeCaseToCamelCase(parameterName);
            } else {
                parameter = parameterName;
            }

            if (hasProperty(beanInfo, parameter)) {
                paramBindings.put(parameterName, LinkELContext.toParameterExpression(parameter));
            }
        }
        return paramBindings;
    }

    private static boolean hasProperty(BeanInfo beanInfo, String property) {
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            String name = propertyDescriptor.getName();
            if (name.equals(property)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRemoveUnexpanded(String template, Map<String, String> bindings, Set<BindingOption> bindingOptions) {
        return !(bindings.isEmpty() && !template.isEmpty()) && !bindingOptions.contains(BindingOption.RETAIN_UNEXPANDED);
    }

    /**
     * Returns a List of LinkDescriptor's based on the provided linkRels.
     *
     * @param declaringClass the class defining the field of which the LinkRels annotation is on
     * @param linkRels       the linkRels to construct a List of LinkDescriptor's from
     * @return List of LinkDescriptor's based on linkRels
     * @throws IllegalArgumentException if a 'self' rel is present and has not been declared first
     */
    public List<LinkDescriptor> createLinkDescriptors(Class<?> declaringClass, LinkRels linkRels) {
        List<LinkDescriptor> linkDescriptors = new ArrayList<>();
        Iterator<LinkRel> linkRelsItr = Arrays.asList(linkRels.value()).iterator();
        if (linkRelsItr.hasNext()) {
            linkDescriptors.add(createLinkDescriptor(declaringClass, linkRelsItr.next()));
            while (linkRelsItr.hasNext()) {
                LinkRel linkRel = linkRelsItr.next();
                if (linkRel.rel().equals(LinkRel.DEFAULT_REL)) {
                    throw new IllegalArgumentException("self rel MUST be declared first when using " + LinkRels.class.getName());
                }
                linkDescriptors.add(createLinkDescriptor(declaringClass, linkRel));
            }
        }
        return linkDescriptors;
    }

    /**
     * Returns a CurieDescriptor based on the provided curieDef.
     *
     * @param curieDef the curieDef to construct a CurieDescriptor from
     * @return CurieDescriptor based on curieDef
     */
    public CurieDescriptor createCurieDescriptor(CurieDef curieDef) {
        CurieType curieType = CurieType.valueOf(curieDef);
        return new CurieDescriptor(curieType.getName(), new HrefTemplate(curieType.getValue(), curieType.getStyle()));
    }

    /**
     * Returns a List of CurieDescriptor's based on the provided curieDefs.
     *
     * @param curieDefs the curieDefs to construct a List of CurieDescriptor's from
     * @return List of CurieDescriptor's based on curieDefs
     */
    public List<CurieDescriptor> createCurieDescriptors(CurieDefs curieDefs) {
        List<CurieDescriptor> curieDescriptors = new ArrayList<>();
        for (CurieDef curieDef : curieDefs.value()) {
            curieDescriptors.add(createCurieDescriptor(curieDef));
        }
        return curieDescriptors;
    }

    /**
     * Returns CurieDescriptors for all {@link CurieDef} and {@link CurieDefs} found on the given type's inheritance
     * hierarchy.
     *
     * @param type the type of which to construct CurieDescriptors for
     * @return CurieDescriptors containing any {@link CurieDef} or {@link CurieDefs} found
     */
    public CurieDescriptors createCurieDescriptors(Class<?> type) {
        return new CurieDescriptors(findCurieDescriptors(type));
    }

    private List<CurieDescriptor> findCurieDescriptors(Class<?> type) {
        List<CurieDescriptor> curieDescriptors = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(CurieDef.class)) {
                CurieDef curieDef = field.getAnnotation(CurieDef.class);
                curieDescriptors.add(createCurieDescriptor(curieDef));
            } else if (field.isAnnotationPresent(CurieDefs.class)) {
                CurieDefs curieDefs = field.getAnnotation(CurieDefs.class);
                curieDescriptors.addAll(createCurieDescriptors(curieDefs));
            }
        }

        Class<?> superType = type.getSuperclass();
        if (superType != Object.class) {
            curieDescriptors.addAll(findCurieDescriptors(superType));
        }
        return curieDescriptors;
    }
}
