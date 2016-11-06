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

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Map;

class LinkELContextResolver extends ELResolver {

    private final Map<String, Object> objects;

    LinkELContextResolver(Map<String, Object> objects) {
        this.objects = objects;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        if (isHandled(context, base, property)) {
            return objects.get(property.toString());
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        if (isHandled(context, base, property)) {
            final Object value = objects.get(property.toString());
            return value == null ? null : value.getClass();
        }
        return null;
    }

    private boolean isHandled(ELContext context, Object base, Object property) {
        if (base != null) {
            return false;
        }
        if (objects.containsKey(property.toString())) {
            context.setPropertyResolved(true);
            return true;
        }
        return false;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        throw new PropertyNotWritableException(property.toString());
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return true;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return Object.class;
    }
}
