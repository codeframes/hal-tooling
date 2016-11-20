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
package com.github.codeframes.hal.tooling.link.bindings.jaxrs.context;

import com.github.codeframes.hal.tooling.link.bindings.context.DefaultLinkELContext;
import com.github.codeframes.hal.tooling.utils.Validate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An LinkELContext implementation providing access to the following identifiers:
 * <table border="1"><tbody><tr>
 * <th>Identifier</th>
 * <th>Description</th>
 * <th>Expression Language</th></tr>
 * <tr>
 * <td>{@value #ENTITY_OBJECT}</td>
 * <td>The reference to the entity object (container/parent)</td>
 * <td>${{@value #ENTITY_OBJECT}}</td></tr>
 * <tr>
 * <td>{@value #INSTANCE_OBJECT}</td>
 * <td>The reference to the instance object</td>
 * <td>${{@value #INSTANCE_OBJECT}}</td></tr>
 * <tr>
 * <td>{@value #URI_OBJECT}</td>
 * <td>The reference to the uri parameters object</td>
 * <td>${{@value #URI_OBJECT}}</td>
 * </tr></tbody></table>
 */
public class JaxRsLinkELContext extends DefaultLinkELContext {

    /**
     * Constructs a JaxRsLinkELContext for the given entity and uriParameters.
     *
     * @param entity        the entity object (container/parent)
     * @param uriParameters the object providing uri request parameters
     */
    public JaxRsLinkELContext(Object entity, UriParameters uriParameters) {
        this(entity, uriParameters, Collections.<String, Object>emptyMap());
    }

    /**
     * Constructs a JaxRsLinkELContext for the given entity, uriParameters and additional object identifiers.
     *
     * @param entity        the entity object (container/parent)
     * @param uriParameters the object providing uri request parameters
     * @param identifiers   a map of additional object identifiers to make available to Link EL expressions
     */
    public JaxRsLinkELContext(Object entity, UriParameters uriParameters, Map<String, Object> identifiers) {
        super(toObjects(entity, uriParameters, identifiers));
    }

    private static Map<String, Object> toObjects(Object entity, UriParameters uriParameters, Map<String, Object> identifiers) {
        final Map<String, Object> objects = new HashMap<>(3 + identifiers.size());
        objects.put(ENTITY_OBJECT, Validate.notNull(entity, ENTITY_OBJECT));
        objects.put(INSTANCE_OBJECT, Validate.notNull(entity, INSTANCE_OBJECT));
        objects.put(URI_OBJECT, Validate.notNull(uriParameters, URI_OBJECT));
        for (Map.Entry<String, Object> entry : identifiers.entrySet()) {
            objects.put(entry.getKey(), Validate.notNull(entry.getValue(), entry.getKey()));
        }
        return objects;
    }
}
