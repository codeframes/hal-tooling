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
package com.github.codeframes.hal.tooling.json.ser.links.processors;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.codeframes.hal.tooling.core.Link;
import com.github.codeframes.hal.tooling.json.LinkSerialization;
import com.github.codeframes.hal.tooling.json.LinkSerialization.LinkSerializationMethod;
import com.github.codeframes.hal.tooling.json.core.JavaTypes;
import com.github.codeframes.hal.tooling.json.ser.config.HalSerializationConfig;
import mockit.Mocked;
import mockit.StrictExpectations;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LinkProcessorFactoryTest {

    static final JavaType LIST_OF_LINKS = TypeFactory.defaultInstance().constructCollectionType(List.class, Link.class);

    final HalSerializationConfig defaultConfig = HalSerializationConfig.defaultInstance();

    @Mocked
    BeanPropertyWriter mockProperty;

    @Test
    public void testNewLinkProcessor_with_implicit_link_property() throws Exception {

        new StrictExpectations() {
            {
                mockProperty.getAnnotation(LinkSerialization.class).value();
                result = LinkSerializationMethod.IMPLICIT;

                mockProperty.getType();
                result = JavaTypes.LINK;
            }
        };

        LinkProcessor linkProcessor = LinkProcessorFactory.newLinkProcessor(defaultConfig, mockProperty);
        assertThat(linkProcessor, is(instanceOf(ImplicitLinkProcessor.class)));
    }

    @Test
    public void testNewLinkProcessor_with_implicit_option_on_declaring_class() throws Exception {

        new StrictExpectations() {
            {
                mockProperty.getAnnotation(LinkSerialization.class);
                result = null;

                mockProperty.getMember().getDeclaringClass();
                result = BeanWithImplicitSerialization.class;

                mockProperty.getType();
                result = JavaTypes.LINK;
            }
        };

        LinkProcessor linkProcessor = LinkProcessorFactory.newLinkProcessor(defaultConfig, mockProperty);
        assertThat(linkProcessor, is(instanceOf(ImplicitLinkProcessor.class)));
    }

    @Test
    public void testNewLinkProcessor_with_explicit_link_property() throws Exception {

        new StrictExpectations() {
            {
                mockProperty.getAnnotation(LinkSerialization.class).value();
                result = LinkSerializationMethod.EXPLICIT;

                mockProperty.getType();
                result = JavaTypes.LINK;
            }
        };

        LinkProcessor linkProcessor = LinkProcessorFactory.newLinkProcessor(defaultConfig, mockProperty);
        assertThat(linkProcessor, is(instanceOf(ExplicitLinkProcessor.class)));
    }

    @Test
    public void testNewLinkProcessor_with_explicit_option_on_declaring_class() throws Exception {

        new StrictExpectations() {
            {
                mockProperty.getAnnotation(LinkSerialization.class);
                result = null;

                mockProperty.getMember().getDeclaringClass();
                result = BeanWithExplicitSerialization.class;

                mockProperty.getType();
                result = JavaTypes.LINK;
            }
        };

        LinkProcessor linkProcessor = LinkProcessorFactory.newLinkProcessor(defaultConfig, mockProperty);
        assertThat(linkProcessor, is(instanceOf(ExplicitLinkProcessor.class)));
    }

    @Test
    public void testNewLinkProcessor_with_no_LinkSerialization_option_defined() throws Exception {

        new StrictExpectations() {
            {
                mockProperty.getAnnotation(LinkSerialization.class);
                result = null;

                mockProperty.getMember().getDeclaringClass();
                result = BeanWithNoLinkSerializationDefined.class;

                mockProperty.getType();
                result = JavaTypes.LINK;
            }
        };

        LinkProcessor linkProcessor = LinkProcessorFactory.newLinkProcessor(defaultConfig, mockProperty);
        assertThat(linkProcessor, is(instanceOf(ImplicitLinkProcessor.class)));
    }

    @Test
    public void testNewLinkProcessor_with_implicit_links_property() throws Exception {

        new StrictExpectations() {
            {
                mockProperty.getAnnotation(LinkSerialization.class).value();
                result = LinkSerializationMethod.IMPLICIT;

                mockProperty.getType();
                result = LIST_OF_LINKS;
            }
        };

        LinkProcessor linkProcessor = LinkProcessorFactory.newLinkProcessor(defaultConfig, mockProperty);
        assertThat(linkProcessor, is(instanceOf(ImplicitLinksProcessor.class)));
    }

    @Test
    public void testNewLinkProcessor_with_explicit_links_property() throws Exception {

        new StrictExpectations() {
            {
                mockProperty.getAnnotation(LinkSerialization.class).value();
                result = LinkSerializationMethod.EXPLICIT;

                mockProperty.getType();
                result = LIST_OF_LINKS;
            }
        };

        LinkProcessor linkProcessor = LinkProcessorFactory.newLinkProcessor(defaultConfig, mockProperty);
        assertThat(linkProcessor, is(instanceOf(ExplicitLinksProcessor.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewLinkProcessor_with_invalid_property_type() {

        new StrictExpectations() {
            {
                mockProperty.getAnnotation(LinkSerialization.class).value();
                result = LinkSerializationMethod.EXPLICIT;

                mockProperty.getType();
                result = TypeFactory.defaultInstance().constructType(String.class);
            }
        };

        LinkProcessorFactory.newLinkProcessor(defaultConfig, mockProperty);
    }

    @LinkSerialization(LinkSerializationMethod.IMPLICIT)
    static class BeanWithImplicitSerialization {
    }

    @LinkSerialization(LinkSerializationMethod.EXPLICIT)
    static class BeanWithExplicitSerialization {
    }

    static class BeanWithNoLinkSerializationDefined {
    }
}