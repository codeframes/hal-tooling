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

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Helper class for accessing field values.
 */
public class FieldAccessor {

    private final Field field;

    /**
     * Constructs a new FieldAccessor for the given field.
     *
     * @param field the field to be accessed
     */
    public FieldAccessor(Field field) {
        this.field = field;
        ensureAccessibleField();
    }

    private void ensureAccessibleField() {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            }
        });
    }

    /**
     * Gets the field value on the provided instance.
     *
     * @param instance the instance of which to get the value for
     * @return the field value on the given instance
     * @throws NullPointerException   if instance in {@code null}
     * @throws FieldAccessorException if the field cannot be accessed
     */
    public Object getValue(Object instance) {
        if (instance == null) {
            throw new NullPointerException(String.format("Cannot get '%s' field value for a null instance", field.getName()));
        }
        try {
            return field.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new FieldAccessorException(String.format("Unable to get '%s' field value for instance of %s",
                    field.getName(), instance.getClass()), e
            );
        }
    }

    /**
     * Sets the field to the given value on the provided instance.
     *
     * @param instance the instance of which to set the value on
     * @param value    the value of which to set
     * @throws NullPointerException   if instance in {@code null}
     * @throws FieldAccessorException if the field cannot be accessed
     */
    public void setValue(Object instance, Object value) {
        if (instance == null) {
            throw new NullPointerException(String.format("Cannot set '%s' field value for a null instance", field.getName()));
        }
        try {
            field.set(instance, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new FieldAccessorException(String.format("Unable to set '%s' field value to: %s, for instance of %s",
                    field.getName(), value, instance.getClass()), e
            );
        }
    }
}
