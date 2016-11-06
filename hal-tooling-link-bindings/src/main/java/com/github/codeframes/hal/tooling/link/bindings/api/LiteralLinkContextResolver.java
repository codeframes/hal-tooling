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
package com.github.codeframes.hal.tooling.link.bindings.api;

/**
 * A LinkContextResolver implementation that provides <b>No</b> URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a>
 * or URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> resolution.
 */
public class LiteralLinkContextResolver implements LinkContextResolver {

    /**
     * Returns the literal value, template.
     *
     * @param template a URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or a URI Template
     *                 <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> to be resolved as absolute
     * @return template
     */
    @Override
    public String resolveAbsolute(String template) {
        return template;
    }

    /**
     * Returns the literal value, template.
     *
     * @param template a URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or a URI Template
     *                 <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> to be resolved as absolute
     * @return template
     */
    @Override
    public String resolveAbsolutePath(String template) {
        return template;
    }

    /**
     * Returns the literal value, template.
     *
     * @param template a URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or a URI Template
     *                 <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> to be resolved as absolute
     * @return template
     */
    @Override
    public String resolveRelativePath(String template) {
        return template;
    }
}
