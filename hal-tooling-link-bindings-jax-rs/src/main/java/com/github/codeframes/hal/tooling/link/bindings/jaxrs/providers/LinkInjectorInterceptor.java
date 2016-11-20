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
import com.github.codeframes.hal.tooling.link.bindings.api.LinkContextResolver;
import com.github.codeframes.hal.tooling.link.bindings.context.LinkELContext;
import com.github.codeframes.hal.tooling.link.bindings.inject.LinkInjector;
import com.github.codeframes.hal.tooling.link.bindings.jaxrs.JaxRsLinkContextResolver;
import com.github.codeframes.hal.tooling.link.bindings.jaxrs.JaxRsLinkTemplateFactory;
import com.github.codeframes.hal.tooling.link.bindings.jaxrs.context.UriParameters;
import com.github.codeframes.hal.tooling.link.bindings.jaxrs.context.JaxRsLinkELContext;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

/**
 * A discoverable JAX-RS extension providing a {@link LinkInjector} with resource method binding support for the
 * injection of links into {@link HalRepresentable} types.
 */
@Provider
public class LinkInjectorInterceptor implements WriterInterceptor {

    private final LinkInjector linkInjector = LinkInjector.instanceBuilder()
            .linkTemplateFactory(new JaxRsLinkTemplateFactory())
            .build();

    @Context
    private UriInfo uriInfo;

    /**
     * Performs link injection if the context entity is an instanceof {@link HalRepresentable} else No-Op.
     *
     * @param context invocation context
     * @throws IOException             if an IO error arises or is thrown by the wrapped
     *                                 {@code MessageBodyWriter.writeTo} method
     * @throws WebApplicationException thrown by the wrapped {@code MessageBodyWriter.writeTo} method
     */
    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException {
        final Object entity = context.getEntity();
        if (entity instanceof HalRepresentable) {
            final LinkELContext linkELContext = new JaxRsLinkELContext(entity, new UriParameters(uriInfo));
            final LinkContextResolver linkContextResolver = new JaxRsLinkContextResolver(uriInfo);
            linkInjector.injectLinks((HalRepresentable) entity, linkContextResolver, linkELContext);
        }
        context.proceed();
    }
}
