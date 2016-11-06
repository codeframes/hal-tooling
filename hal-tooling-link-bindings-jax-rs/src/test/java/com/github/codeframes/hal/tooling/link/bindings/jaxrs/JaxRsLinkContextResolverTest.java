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
package com.github.codeframes.hal.tooling.link.bindings.jaxrs;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JaxRsLinkContextResolverTest {

    @Injectable
    UriInfo mockUriInfo;

    @Tested
    JaxRsLinkContextResolver linkContextResolver;

    @Test
    public void testResolveAbsolute() throws Exception {

        final String template = "template";

        new Expectations() {{
            mockUriInfo.getBaseUriBuilder().path(template).toTemplate();
            result = "template - resolveAbsolute";
        }};

        String resolveAbsolute = linkContextResolver.resolveAbsolute(template);
        assertThat(resolveAbsolute, is(equalTo("template - resolveAbsolute")));
    }

    @Test
    public void testResolveAbsolutePath(@Mocked final UriBuilder mockUriBuilder) throws Exception {

        final String template = "template";

        new Expectations() {{
            mockUriInfo.getBaseUri();
            result = new URI("/api");

            UriBuilder.fromPath("/api").path(template).toTemplate();
            result = "template - resolveAbsolutePath";
        }};

        String resolveAbsolutePath = linkContextResolver.resolveAbsolutePath(template);
        assertThat(resolveAbsolutePath, is(equalTo("template - resolveAbsolutePath")));
    }

    @Test
    public void testResolveAbsolutePath_with_an_absolute_template() throws Exception {

        String template = "http://localhost:8080/";

        String resolveAbsolutePath = linkContextResolver.resolveAbsolutePath(template);
        assertThat(resolveAbsolutePath, is(equalTo("http://localhost:8080/")));
    }

    @Test
    public void testResolveRelativePath(@Mocked final UriBuilder mockUriBuilder) throws Exception {

        final String template = "template";

        new Expectations() {{
            UriBuilder.fromPath(template).toTemplate();
            result = "template - resolveRelativePath";
        }};

        String resolveRelativePath = linkContextResolver.resolveRelativePath(template);
        assertThat(resolveRelativePath, is(equalTo("template - resolveRelativePath")));
    }
}