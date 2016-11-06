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
package com.github.codeframes.hal.tooling.json.core;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

import javax.annotation.Nullable;

public class BeanPropertyReader<T> {

    private final BeanPropertyWriter property;

    public BeanPropertyReader(BeanPropertyWriter property) {
        this.property = property;
    }

    public String getName() {
        return property.getName();
    }

    @Nullable
    public T get(final Object bean) throws JsonMappingException {
        try {
            //noinspection unchecked
            return (T) property.get(bean);
        } catch (Exception e) {
            throw new JsonMappingException(String.format("Unable to get property value: %s, for instance of: %s",
                    property.getName(), bean.getClass()), e);
        }
    }
}
