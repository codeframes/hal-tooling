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
package com.github.codeframes.hal.tooling.json.module;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.github.codeframes.hal.tooling.core.HalRepresentable;
import com.github.codeframes.hal.tooling.json.ser.HalRepresentableSerializerModifier;
import com.github.codeframes.hal.tooling.json.ser.HalSerializer;
import com.github.codeframes.hal.tooling.json.ser.HalSerializerFactory;
import com.github.codeframes.hal.tooling.json.ser.config.HalSerializationConfig;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HalRepresentableSerializerModifierTest {

    final HalSerializationConfig defaultConfig = HalSerializationConfig.defaultInstance();

    @Injectable
    BeanDescription mockBeanDesc;

    @Mocked
    HalSerializerFactory mockHalSerializerFactory;

    @Test
    public void testModifySerializer(@Mocked final BeanSerializerBase mockBeanSerializer) throws Exception {

        new Expectations() {
            {
                mockBeanDesc.getBeanClass();
                result = HalRepresentable.class;
            }
        };

        JsonSerializer<?> serializer = new HalRepresentableSerializerModifier(defaultConfig)
                .modifySerializer(/*config not used*/null, mockBeanDesc, mockBeanSerializer);

        assertThat(serializer, is(instanceOf(HalSerializer.class)));
    }

    @Test
    public void testModifySerializer_when_src_JsonSerializer_not_a_BeanSerializerBase(@Mocked final JsonSerializer mockJsonSerializer) throws Exception {

        JsonSerializer<?> serializer = new HalRepresentableSerializerModifier(defaultConfig)
                .modifySerializer(/*config not used*/null, mockBeanDesc, mockJsonSerializer);

        assertThat(serializer, is(mockJsonSerializer));
    }

    @Test
    public void testModifySerializer_when_bean_class_not_a_HalRepresentable(@Mocked final BeanSerializerBase mockBeanSerializer) throws Exception {

        new Expectations() {
            {
                mockBeanDesc.getBeanClass();
                result = Object.class;
            }
        };

        JsonSerializer<?> serializer = new HalRepresentableSerializerModifier(defaultConfig)
                .modifySerializer(/*config not used*/null, mockBeanDesc, mockBeanSerializer);

        assertThat(serializer, is((JsonSerializer) mockBeanSerializer));
    }
}