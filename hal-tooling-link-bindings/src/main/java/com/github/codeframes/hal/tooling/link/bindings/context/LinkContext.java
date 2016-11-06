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

import com.github.codeframes.hal.tooling.link.bindings.Style;

import java.util.Map;

/**
 * A Link Context.
 */
public interface LinkContext {

    /**
     * Returns a LinkContext for the given bean.
     *
     * @param bean the bean to get a LinkContext on
     * @return LinkContext for the given bean
     */
    LinkContext forBean(Object bean);

    /**
     * Evaluates and returns the result of a boolean expression.
     *
     * @param expression an EL to evaluate as a boolean
     * @return the result of the evaluated expression
     */
    boolean evaluateAsBoolean(String expression);

    /**
     * Evaluates and returns the result of a String expression.
     *
     * @param expression an EL to evaluate as a String
     * @return the result of the evaluated expression
     */
    String evaluateAsString(String expression);

    /**
     * Expands the given template and returns a URI or URI Template dependant on parameters given.
     *
     * @param template         a URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> to expand
     * @param bindings         a Map of URI Template parameter bindings to be used in template expansion. Binding names
     *                         are to match up with an associated URI Template parameter and the values an EL expression,
     *                         that when evaluated are used for parameter substitution.
     * @param removeUnexpanded determines whether or not to preserve templated parameters where no substitution value is
     *                         available. {@code true} to remove, {@code false} to retain
     * @return a URI or URI Template dependant on parameters given
     */
    String expand(String template, Map<String, String> bindings, boolean removeUnexpanded);

    /**
     * Applies the given style to the provided template and returns the result.
     *
     * @param style    the style to apply
     * @param template a URI <a href="https://tools.ietf.org/html/rfc3986">[RFC3986]</a> or a URI Template
     *                 <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> to be styled
     * @return styled template
     */
    String style(Style style, String template);
}
