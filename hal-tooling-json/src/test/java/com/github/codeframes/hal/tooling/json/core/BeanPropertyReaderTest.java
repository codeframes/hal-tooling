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
import mockit.Expectations;
import mockit.Injectable;
import mockit.StrictExpectations;
import mockit.Tested;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class BeanPropertyReaderTest {

    @Injectable
    BeanPropertyWriter mockPropertyWriter;

    @Tested
    BeanPropertyReader propertyReader;

    @Test
    public void testGetName() throws Exception {

        final String expectedPropName = "prop name";

        new StrictExpectations() {
            {
                mockPropertyWriter.getName();
                result = expectedPropName;
            }
        };

        String propName = propertyReader.getName();
        assertThat(propName, is(equalTo(expectedPropName)));
    }

    @Test
    public void testGet() throws Exception {

        final Object bean = new Object();
        final Object expectedPropValue = "prop value";

        new StrictExpectations() {
            {
                mockPropertyWriter.get(bean);
                result = expectedPropValue;
            }
        };

        Object propValue = propertyReader.get(bean);
        assertThat(propValue, is(sameInstance(expectedPropValue)));
    }

    @Test(expected = JsonMappingException.class)
    public void testGet_when_exception_thrown() throws Exception {

        final Object bean = new Object();

        new Expectations() {
            {
                mockPropertyWriter.get(bean);
                result = new Exception();
            }
        };

        propertyReader.get(bean);
    }
}