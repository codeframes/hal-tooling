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

import com.github.codeframes.hal.tooling.link.bindings.types.LinkRelType;

/**
 * Factory for creating Link Templates.
 */
public interface LinkTemplateFactory {

    /**
     * Returns a Link Template based on the provided linkRelType. A Link Template can be either a
     * URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or
     * URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> for a resource. The Link Template may
     * also contain EL expressions, but the expressions MUST evaluate to Strings.
     *
     * @param linkRelType the linkRelType to construct a Link Template from
     * @return Link Template based on linkRelType
     */
    String createLinkTemplate(LinkRelType linkRelType);
}
