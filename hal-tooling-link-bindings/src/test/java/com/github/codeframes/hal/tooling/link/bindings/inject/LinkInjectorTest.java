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

import com.github.codeframes.hal.tooling.core.HalRepresentable;
import com.github.codeframes.hal.tooling.link.bindings.api.LinkTemplateFactory;
import com.github.codeframes.hal.tooling.link.bindings.api.LiteralLinkContextResolver;
import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext;
import com.github.codeframes.hal.tooling.link.bindings.uri.UriValueResolver;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Test;

import javax.el.ExpressionFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class LinkInjectorTest {

    @Test
    public void testInjectLinks(@Mocked final LinkSetterFactory mockLinkSetterFactory) throws Exception {

        final Entity entity = new Entity();

        new Expectations() {{
            mockLinkSetterFactory.getBeanLinkSetter(Entity.class)
                    .setLinks(entity, (LinkContext) any);
        }};

        LinkInjector linkInjector = LinkInjector.defaultInstance();

        linkInjector.injectLinks(entity, new LiteralLinkContextResolver());
    }

    @Test
    public void testInjectLinks_with_null_entity(@Mocked final BeanLinkSetter mockBeanLinkSetter) throws Exception {

        LinkInjector linkInjector = LinkInjector.defaultInstance();

        linkInjector.injectLinks(null, new LiteralLinkContextResolver());

        new Verifications() {{
            mockBeanLinkSetter.setLinks(any, (LinkContext) any);
            times = 0;
        }};
    }

    private static class Entity implements HalRepresentable {
    }

    @Test
    public void testDefaultInstance() throws Exception {

        assertNotNull(LinkInjector.defaultInstance());
    }

    @Test
    public void testInstanceBuilder() throws Exception {

        LinkInjector linkInjector = LinkInjector.instanceBuilder().build();

        assertNotNull(linkInjector);
    }

    @Test
    public void testInstanceBuilder_with_available_options(@Mocked ExpressionFactory mockExpressionFactory,
                                                           @Mocked LinkTemplateFactory mockLinkTemplateFactory) throws Exception {

        List<UriValueResolver<?>> uriValueResolvers = new ArrayList<>();

        LinkInjector linkInjector = LinkInjector.instanceBuilder()
                .expressionFactory(mockExpressionFactory)
                .linkTemplateFactory(mockLinkTemplateFactory)
                .uriValueResolvers(uriValueResolvers)
                .build();

        assertNotNull(linkInjector);
    }

    @Test(expected = NullPointerException.class)
    public void testInstanceBuilder_with_null_expressionFactory() throws Exception {

        LinkInjector.instanceBuilder().expressionFactory(null);
    }

    @Test(expected = NullPointerException.class)
    public void testInstanceBuilder_with_null_linkTemplateFactory() throws Exception {

        LinkInjector.instanceBuilder().linkTemplateFactory(null);
    }

    @Test(expected = NullPointerException.class)
    public void testInstanceBuilder_with_null_uriValueResolvers() throws Exception {

        LinkInjector.instanceBuilder().uriValueResolvers(null);
    }
}