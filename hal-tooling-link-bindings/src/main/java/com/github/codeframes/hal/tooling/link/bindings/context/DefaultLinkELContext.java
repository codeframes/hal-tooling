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
package com.github.codeframes.hal.tooling.link.bindings.context;

import com.github.codeframes.hal.tooling.utils.Validate;

import javax.el.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The default LinkELContext implementation providing access to the following identifiers:
 * <table border="1"><tbody><tr>
 * <th>Identifier</th>
 * <th>Description</th>
 * <th>Expression Language</th></tr>
 * <tr>
 * <td>{@value #ENTITY_OBJECT}</td>
 * <td>The reference to the entity object (container/parent)</td>
 * <td>${{@value #ENTITY_OBJECT}}</td></tr>
 * <tr>
 * <td>{@value #ENTITY_OBJECT}</td>
 * <td>The reference to the instance object</td>
 * <td>${{@value #ENTITY_OBJECT}}</td>
 * </tr></tbody></table>
 */
public class DefaultLinkELContext extends LinkELContext {

    /**
     * The entity object identifier (container/parent).
     */
    public static final String ENTITY_OBJECT = "entity";

    private final Map<String, Object> objects;

    /**
     * Constructs a DefaultLinkELContext for the given entity.
     *
     * @param entity the entity object (container/parent)
     */
    public DefaultLinkELContext(Object entity) {
        this(toObjects(entity));
    }

    private DefaultLinkELContext(Map<String, Object> objects) {
        this.objects = Collections.unmodifiableMap(objects);
    }

    private static Map<String, Object> toObjects(Object entity) {
        final Map<String, Object> objects = new HashMap<>(2);
        objects.put(ENTITY_OBJECT, Validate.notNull(entity, ENTITY_OBJECT));
        objects.put(INSTANCE_OBJECT, Validate.notNull(entity, INSTANCE_OBJECT));
        return objects;
    }

    @Override
    public LinkELContext withInstance(Object instance) {
        final Map<String, Object> newObjects = new HashMap<>(this.objects);
        newObjects.put(INSTANCE_OBJECT, Validate.notNull(instance, INSTANCE_OBJECT));
        return new DefaultLinkELContext(newObjects);
    }

    @Override
    public ELResolver getELResolver() {
        CompositeELResolver resolver = new CompositeELResolver();
        resolver.add(new LinkELContextResolver(this.objects));
        resolver.add(new MapELResolver(true));
        resolver.add(new BeanELResolver(true));
        return resolver;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return null;
    }

    @Override
    public VariableMapper getVariableMapper() {
        return null;
    }
}
