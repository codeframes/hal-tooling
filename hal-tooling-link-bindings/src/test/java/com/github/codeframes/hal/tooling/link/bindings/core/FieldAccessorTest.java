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

import mockit.*;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FieldAccessorTest {

    @Injectable
    Field mockField;

    @Tested
    FieldAccessor fieldAccessor;

    @Test
    public void testConstructor() throws Exception {

        new StrictExpectations() {{
            mockField.isAccessible();
            result = true;
        }};

        new FieldAccessor(mockField);

        new Verifications() {{
            mockField.setAccessible(true);
            times = 0;
        }};
    }

    @Test
    public void testConstructor_with_inaccessible_field() throws Exception {

        new StrictExpectations() {{
            mockField.isAccessible();
            result = false;
        }};

        new FieldAccessor(mockField);

        new Verifications() {{
            mockField.setAccessible(true);
        }};
    }

    @Test
    public void testGetValue() throws Exception {

        final Object instance = new Object();
        final Object expectedValue = new Object();

        new Expectations() {{
            mockField.get(instance);
            result = expectedValue;
        }};

        Object value = fieldAccessor.getValue(instance);

        assertThat(value, is(expectedValue));
    }

    @Test(expected = NullPointerException.class)
    public void testGetValue_with_null_instance() throws Exception {
        fieldAccessor.getValue(null);
    }

    @Test(expected = FieldAccessorException.class)
    public void testGetValue_with_IllegalArgumentException_thrown() throws Exception {

        final Object instance = new Object();

        new Expectations() {{
            mockField.get(instance);
            result = new IllegalArgumentException();
        }};

        fieldAccessor.getValue(instance);
    }

    @Test(expected = FieldAccessorException.class)
    public void testGetValue_with_IllegalAccessException_thrown() throws Exception {

        final Object instance = new Object();

        new Expectations() {{
            mockField.get(instance);
            result = new IllegalAccessException();
        }};

        fieldAccessor.getValue(instance);
    }

    @Test
    public void testSetValue() throws Exception {

        final Object instance = new Object();
        final Object value = new Object();

        new Expectations() {{
            mockField.set(instance, value);
        }};

        fieldAccessor.setValue(instance, value);
    }

    @Test(expected = NullPointerException.class)
    public void testSetValue_with_null_instance() throws Exception {
        fieldAccessor.setValue(null, new Object());
    }

    @Test(expected = FieldAccessorException.class)
    public void testSetValue_with_IllegalArgumentException_thrown() throws Exception {

        final Object instance = new Object();
        final Object value = new Object();

        new Expectations() {{
            mockField.set(instance, value);
            result = new IllegalArgumentException();
        }};

        fieldAccessor.setValue(instance, value);
    }

    @Test(expected = FieldAccessorException.class)
    public void testSetValue_with_IllegalAccessException_thrown() throws Exception {

        final Object instance = new Object();
        final Object value = new Object();

        new Expectations() {{
            mockField.set(instance, value);
            result = new IllegalAccessException();
        }};

        fieldAccessor.setValue(instance, value);
    }
}