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
package com.github.codeframes.hal.tooling.link.bindings.jaxrs.providers;

import com.github.codeframes.hal.tooling.core.HalRepresentable;
import com.github.codeframes.hal.tooling.link.bindings.inject.LinkInjector;
import com.github.codeframes.hal.tooling.link.bindings.jaxrs.JaxRsLinkContextResolver;
import com.github.codeframes.hal.tooling.link.bindings.jaxrs.context.JaxRsLinkELContext;
import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.StrictExpectations;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.WriterInterceptorContext;

public class LinkInjectorInterceptorTest {

    @Mocked
    LinkInjector mockLinkInjector;

    @Mocked
    WriterInterceptorContext mockContext;

    @Mocked
    UriInfo mockUriInfo;

    LinkInjectorInterceptor interceptor;

    @Before
    public void setUp() throws Exception {
        interceptor = new LinkInjectorInterceptor();
        Deencapsulation.setField(interceptor, mockLinkInjector);
        Deencapsulation.setField(interceptor, mockUriInfo);
    }

    @Test
    public void aroundWriteTo_with_HalRepresentable_entity() throws Exception {

        final Representation representation = new Representation();

        new StrictExpectations() {{
            mockContext.getEntity();
            result = representation;

            mockLinkInjector.injectLinks(
                    representation,
                    withInstanceOf(JaxRsLinkContextResolver.class),
                    withInstanceOf(JaxRsLinkELContext.class)
            );

            mockContext.proceed();
        }};

        interceptor.aroundWriteTo(mockContext);
    }

    @Test
    public void aroundWriteTo_with_non_HalRepresentable_entity() throws Exception {

        new StrictExpectations() {{
            mockContext.getEntity();
            result = new Object();

            mockContext.proceed();
        }};

        interceptor.aroundWriteTo(mockContext);
    }

    static class Representation implements HalRepresentable {
    }
}