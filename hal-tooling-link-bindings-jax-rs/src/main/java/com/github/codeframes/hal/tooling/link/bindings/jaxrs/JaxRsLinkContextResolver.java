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

import com.github.codeframes.hal.tooling.link.bindings.api.LinkContextResolver;
import com.github.codeframes.hal.tooling.link.bindings.utils.LinkTemplateUtils;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * A JAX-RS specific LinkContextResolver.
 */
public class JaxRsLinkContextResolver implements LinkContextResolver {

    private final UriInfo uriInfo;

    /**
     * Constructs a new JaxRsLinkContextResolver for a given request.
     *
     * @param uriInfo provides the necessary access to application and request
     *                URI information
     */
    public JaxRsLinkContextResolver(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    @Override
    public String resolveAbsolute(String template) {
        return uriInfo.getBaseUriBuilder().path(template).toTemplate();
    }

    @Override
    public String resolveAbsolutePath(String template) {
        if (LinkTemplateUtils.isAbsolute(template)) {
            return template;
        }
        final String basePath = uriInfo.getBaseUri().getPath();
        return UriBuilder.fromPath(basePath).path(template).toTemplate();
    }

    @Override
    public String resolveRelativePath(String template) {
        return UriBuilder.fromPath(template).toTemplate();
    }
}
