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
import com.github.codeframes.hal.tooling.link.bindings.api.LinkContextResolver;
import com.github.codeframes.hal.tooling.link.bindings.uri.UriTemplateExpander;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.HashMap;
import java.util.Map;

/**
 * The default LinkContext implementation used for resolving links.
 */
public class DefaultLinkContext implements LinkContext {

    private final ExpressionFactory expressionFactory;
    private final LinkContextResolver linkContextResolver;
    private final UriTemplateExpander uriTemplateExpander;
    private final LinkELContext linkELContext;

    /**
     * Constructs a DefaultLinkContext.
     *
     * @param expressionFactory   the ExpressionFactory, used for parsing expression's into {@link ValueExpression}'s
     *                            used in the evaluation process.
     * @param linkContextResolver the Link Context Resolver for resolving URI's or URI Template's to either absolute,
     *                            absolute path or relative path forms.
     * @param uriTemplateExpander the URI Template <a href="https://tools.ietf.org/html/rfc6570">[RFC6570]</a> Expander.
     * @param linkELContext       the Link ELContext, used for EL evaluations.
     */
    public DefaultLinkContext(ExpressionFactory expressionFactory,
                              LinkContextResolver linkContextResolver,
                              UriTemplateExpander uriTemplateExpander,
                              LinkELContext linkELContext) {
        this.expressionFactory = expressionFactory;
        this.linkContextResolver = linkContextResolver;
        this.uriTemplateExpander = uriTemplateExpander;
        this.linkELContext = linkELContext;
    }

    @Override
    public LinkContext forBean(Object bean) {
        LinkELContext newLinkELContext = linkELContext.withInstance(bean);
        return new DefaultLinkContext(expressionFactory, linkContextResolver, uriTemplateExpander, newLinkELContext);
    }

    @Override
    public boolean evaluateAsBoolean(String expression) {
        ValueExpression valExpr = expressionFactory.createValueExpression(linkELContext, expression, boolean.class);
        Object value = valExpr.getValue(linkELContext);
        return Boolean.TRUE.equals(value);
    }

    @Override
    public String evaluateAsString(String expression) {
        ValueExpression valExpr = expressionFactory.createValueExpression(linkELContext, expression, String.class);
        return (String) valExpr.getValue(linkELContext);
    }

    @Override
    public String expand(String template, Map<String, String> bindings, boolean removeUnexpanded) {
        Map<String, Object> bindingParameters = new HashMap<>();
        for (Map.Entry<String, String> binding : bindings.entrySet()) {
            bindingParameters.put(binding.getKey(), evaluateAsObject(binding.getValue()));
        }
        return uriTemplateExpander.expand(template, bindingParameters, removeUnexpanded);
    }

    private Object evaluateAsObject(String expression) {
        ValueExpression valExpr = expressionFactory.createValueExpression(linkELContext, expression, Object.class);
        return valExpr.getValue(linkELContext);
    }

    @Override
    public String style(Style style, String template) {
        switch (style) {
            case ABSOLUTE:
                return linkContextResolver.resolveAbsolute(template);
            case ABSOLUTE_PATH:
                return linkContextResolver.resolveAbsolutePath(template);
            case RELATIVE_PATH:
                return linkContextResolver.resolveRelativePath(template);
            default:
                return linkContextResolver.resolveAbsolute(template);
        }
    }
}
