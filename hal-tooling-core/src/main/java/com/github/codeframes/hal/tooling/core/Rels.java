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
package com.github.codeframes.hal.tooling.core;

import javax.annotation.Nullable;

/**
 * Constants class for relation names.
 */
public final class Rels {

    /**
     * Relation whose target is the resource's URI.
     */
    public static final String SELF = "self";

    /**
     * Relation located on the root resource whose target is a set of Link Objects.
     * These links contain a URI Template with the token 'rel', and are named
     * via the "name" property. Curies are used to prefix custom link relation types
     * for brevity.
     */
    public static final String CURIES = "curies";

    private Rels() {
    }

    @Nullable
    public static String getCuriePrefix(String rel) {
        if (rel.contains(":")) {
            return rel.substring(0, rel.indexOf(':'));
        } else {
            return null;
        }
    }

    public static String getName(String rel) {
        if (rel.contains(":")) {
            return rel.substring(rel.indexOf(':') + 1);
        } else {
            return rel;
        }
    }
}
