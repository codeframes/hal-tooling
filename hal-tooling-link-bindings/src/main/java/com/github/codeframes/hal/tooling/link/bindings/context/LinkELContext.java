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
package com.github.codeframes.hal.tooling.link.bindings.context;

import javax.el.ELContext;

/**
 * Link ELContext, used for EL evaluations.
 */
public abstract class LinkELContext extends ELContext {

    /**
     * The instance object identifier.
     */
    public static final String INSTANCE_OBJECT = "instance";

    /**
     * The uri object identifier.
     */
    public static final String URI_OBJECT = "uri";

    /**
     * Returns an EL for retrieving the given parameter value on the instance object.
     *
     * @param parameter the parameter to construct an expression for
     * @return EL for retrieving the given parameter value
     */
    public static String toParameterExpression(String parameter) {
        return "${" + INSTANCE_OBJECT + "." + parameter + "}";
    }

    /**
     * Returns an EL for retrieving the given parameter value on the uri object.
     *
     * @param parameter the parameter to construct an expression for
     * @return EL for retrieving the given parameter value
     */
    public static String toUriParameterExpression(String parameter) {
        return "${" + URI_OBJECT + "." + parameter + "}";
    }

    /**
     * Returns a LinkELContext for the given instance.
     *
     * @param instance the instance to get a LinkELContext on
     * @return LinkELContext for the given instance
     */
    public abstract LinkELContext withInstance(Object instance);

}
