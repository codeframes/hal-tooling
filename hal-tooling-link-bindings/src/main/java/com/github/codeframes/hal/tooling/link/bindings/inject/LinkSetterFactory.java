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
package com.github.codeframes.hal.tooling.link.bindings.inject;

import com.github.codeframes.hal.tooling.core.*;
import com.github.codeframes.hal.tooling.link.bindings.CurieDef;
import com.github.codeframes.hal.tooling.link.bindings.CurieDefs;
import com.github.codeframes.hal.tooling.link.bindings.LinkRel;
import com.github.codeframes.hal.tooling.link.bindings.LinkRels;
import com.github.codeframes.hal.tooling.link.bindings.api.LinkTemplateFactory;
import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext;
import com.github.codeframes.hal.tooling.link.bindings.core.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

class LinkSetterFactory {

    public static final BeanLinkSetter NO_OP_BEAN_LINK_SETTER = new NoOpBeanLinkSetter();
    public static final LinkSetter NO_OP_LINK_SETTER = new NoOpLinkSetter();

    private final Map<Class<?>, BeanLinkSetter> beanLinkSetters = new HashMap<>();
    private final Map<Class<?>, Map<CurieDescriptors, LinkSetter>> elementBeanLinkSetters = new HashMap<>();
    private final LinkDescriptorFactory linkDescriptorFactory;

    private LinkSetterFactory(LinkDescriptorFactory linkDescriptorFactory) {
        this.linkDescriptorFactory = linkDescriptorFactory;
    }

    public static LinkSetterFactory newInstance(LinkTemplateFactory linkTemplateFactory) {
        return new LinkSetterFactory(new LinkDescriptorFactory(linkTemplateFactory));
    }

    public synchronized BeanLinkSetter getBeanLinkSetter(Class<? extends HalRepresentable> type) {
        BeanLinkSetter beanLinkSetter = beanLinkSetters.get(type);
        if (beanLinkSetter == null) {
            beanLinkSetter = createBeanLinkSetter(type);
            beanLinkSetters.put(type, beanLinkSetter);
        }
        return beanLinkSetter;
    }

    private BeanLinkSetter createBeanLinkSetter(Class<?> type) {
        CurieDescriptors curieDescriptors = linkDescriptorFactory.createCurieDescriptors(type);
        List<LinkSetter> linkSetters = getLinkSetters(true, type, curieDescriptors);

        if (linkSetters.isEmpty()) {
            return NO_OP_BEAN_LINK_SETTER;
        } else {
            return new RootBeanLinkSetter(curieDescriptors, linkSetters);
        }
    }

    private List<LinkSetter> getLinkSetters(boolean root, Class<?> type, CurieDescriptors curieDescriptors) {
        return getLinkSetters(root, type, new HashSet<String>(), curieDescriptors, new HashSet<String>());
    }

    private List<LinkSetter> getLinkSetters(boolean root, Class<?> type, Set<String> fieldNames, CurieDescriptors curieDescriptors, Set<String> rels) {

        final List<LinkSetter> linkSetters = new ArrayList<>();
        for (final Field field : type.getDeclaredFields()) {

            final String fieldName = field.getName();
            if (Modifier.isStatic(field.getModifiers()) || fieldNames.contains(fieldName)) {
                continue;
            }

            final LinkSetter linkSetter = createLinkSetter(root, field, curieDescriptors, rels);
            if (linkSetter != null) {
                linkSetters.add(linkSetter);
            }
            fieldNames.add(fieldName);
        }

        final Class<?> superType = type.getSuperclass();
        if (superType != Object.class) {
            linkSetters.addAll(getLinkSetters(root, superType, fieldNames, curieDescriptors, rels));
        }
        return linkSetters;
    }

    private LinkSetter createLinkSetter(boolean root, Field field, CurieDescriptors curieDescriptors, Set<String> rels) {

        LinkSetter linkSetter = null;
        if (field.isAnnotationPresent(LinkRel.class)) {
            linkSetter = createLinkRelFieldSetter(field, curieDescriptors, rels);
        } else if (field.isAnnotationPresent(LinkRels.class)) {
            linkSetter = createLinkRelsFieldSetter(field, rels);
        } else if (Embedded.class.isAssignableFrom(field.getType())) {
            linkSetter = createEmbeddedFieldLinkSetter(field, curieDescriptors);
        } else if (Embeddable.class.isAssignableFrom(field.getType())) {
            linkSetter = createEmbeddableFieldLinkSetter(field, curieDescriptors);
        } else if (root) {
            if (field.isAnnotationPresent(CurieDef.class)) {
                linkSetter = createCurieDefFieldSetter(field);
            } else if (field.isAnnotationPresent(CurieDefs.class)) {
                linkSetter = createCurieDefsFieldSetter(field);
            }
        }
        return linkSetter;
    }

    private LinkSetter createLinkRelFieldSetter(Field field, CurieDescriptors curieDescriptors, Set<String> rels) {

        if (field.getType() != Link.class) {
            throw new IllegalArgumentException(String.format("A field annotated with: %s must be of type: %s, got: %s", LinkRel.class, Link.class,
                    field.getType()));
        }

        LinkRel linkRel = field.getAnnotation(LinkRel.class);
        String rel = linkRel.rel();
        if (rel.contains(":")) {
            String curie = rel.substring(0, rel.indexOf(':'));
            if (curieDescriptors.get(curie) == null) {
                throw new IllegalArgumentException("No curie found for rel: '" + rel + "'");
            }
        }

        LinkDescriptor linkDescriptor = linkDescriptorFactory.createLinkDescriptor(field.getDeclaringClass(), linkRel);
        if (!rels.add(linkDescriptor.getRel())) {
            throw new IllegalArgumentException(String.format("Duplicate rel found: '%s', on %s", linkDescriptor.getRel(), field.getDeclaringClass()));
        }
        return new LinkFieldSetter(new FieldAccessor(field), linkDescriptor);
    }

    private LinkSetter createCurieDefFieldSetter(Field field) {
        if (field.getType() != Curie.class) {
            throw new IllegalArgumentException(String.format("A field annotated with: %s must be of type: %s, got: %s", CurieDef.class, Curie.class,
                    field.getType()));
        }

        CurieDescriptor curieDescriptor = linkDescriptorFactory.createCurieDescriptor(field.getAnnotation(CurieDef.class));
        return new CurieFieldSetter(new FieldAccessor(field), curieDescriptor);
    }

    private LinkSetter createLinkRelsFieldSetter(Field field, Set<String> rels) {

        FieldAccessor fieldDescriptor = new FieldAccessor(field);
        List<LinkDescriptor> linkDescriptors = linkDescriptorFactory
                .createLinkDescriptors(field.getDeclaringClass(), field.getAnnotation(LinkRels.class));
        for (LinkDescriptor linkDescriptor : linkDescriptors) {
            if (!rels.add(linkDescriptor.getRel())) {
                throw new IllegalArgumentException(String.format("Duplicate rel found: '%s', on %s",
                        linkDescriptor.getRel(), field.getDeclaringClass()));
            }
        }

        final LinkSetter linkSetter;
        if (field.getType() == List.class) {
            linkSetter = new LinkListFieldSetter(fieldDescriptor, linkDescriptors);
        } else {
            throw new IllegalArgumentException(String.format("A field annotated with: %s must be of type: %s<%s>, got: %s",
                    LinkRels.class, List.class, Link.class, field.getType()));
        }
        return linkSetter;
    }

    private LinkSetter createCurieDefsFieldSetter(Field field) {

        FieldAccessor fieldDescriptor = new FieldAccessor(field);
        List<CurieDescriptor> curieDescriptors = linkDescriptorFactory.createCurieDescriptors(field.getAnnotation(CurieDefs.class));

        final LinkSetter linkSetter;
        if (field.getType() == List.class) {
            linkSetter = new CurieListFieldSetter(fieldDescriptor, curieDescriptors);
        } else {
            throw new IllegalArgumentException(String.format("A field annotated with: %s must be of type: %s<%s>, got: %s",
                    CurieDefs.class, List.class, Curie.class, field.getType()));
        }
        return linkSetter;
    }

    private LinkSetter createEmbeddedFieldLinkSetter(Field field, CurieDescriptors curieDescriptors) {
        return new EmbeddedFieldLinkSetter(this, new FieldAccessor(field), curieDescriptors);
    }

    private LinkSetter createEmbeddableFieldLinkSetter(Field field, CurieDescriptors curieDescriptors) {
        return new EmbeddableFieldLinkSetter(this, new FieldAccessor(field), curieDescriptors);
    }

    synchronized LinkSetter getElementBeanLinkSetter(Class<?> type, CurieDescriptors curieDescriptors) {

        Map<CurieDescriptors, LinkSetter> linkSetters = elementBeanLinkSetters.get(type);
        if (linkSetters == null) {
            linkSetters = new HashMap<>(4);
            elementBeanLinkSetters.put(type, linkSetters);
        }

        LinkSetter linkSetter = linkSetters.get(curieDescriptors);
        if (linkSetter == null) {
            linkSetter = createElementBeanLinkSetter(type, curieDescriptors);
            linkSetters.put(curieDescriptors, linkSetter);
        }
        return linkSetter;
    }

    private LinkSetter createElementBeanLinkSetter(Class<?> type, CurieDescriptors curieDescriptors) {
        List<LinkSetter> linkSetters = getLinkSetters(false, type, curieDescriptors);
        if (linkSetters.isEmpty()) {
            return NO_OP_LINK_SETTER;
        } else {
            return new EmbeddedBeanLinkSetter(linkSetters);
        }
    }

    private static class NoOpBeanLinkSetter implements BeanLinkSetter {
        @Override
        public void setLinks(Object entity, LinkContext linkContext) {
            // No-Op
        }
    }

    private static class NoOpLinkSetter implements LinkSetter {
        @Override
        public void setLinks(Object instance, LinkProvider linkProvider) {
            // No-Op
        }
    }
}
