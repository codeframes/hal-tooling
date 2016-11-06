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
import mockit.Injectable;
import mockit.Mocked;
import mockit.StrictExpectations;
import mockit.Tested;
import org.junit.Test;

import javax.el.ExpressionFactory;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class DefaultLinkContextTest {

    @Injectable
    ExpressionFactory mockExpressionFactory;
    @Injectable
    LinkELContext mockLinkELContext;
    @Injectable
    LinkContextResolver mockLinkContextResolver;
    @Injectable
    UriTemplateExpander mockUriTemplateExpander;

    @Tested
    DefaultLinkContext linkContext;

    @Test
    public void testForBean(@Mocked final LinkELContext newMockLinkELContext) throws Exception {

        final Object bean = new Object();

        new StrictExpectations() {{
            mockLinkELContext.withInstance(bean);
            result = newMockLinkELContext;
        }};

        LinkContext result = linkContext.forBean(bean);

        assertThat(result, is(notNullValue()));
        assertThat(result, not(sameInstance((LinkContext) linkContext)));
    }

    @Test
    public void testEvaluateAsBoolean() {

        final String expression = "expression";

        new StrictExpectations() {{
            mockExpressionFactory
                    .createValueExpression(mockLinkELContext, expression, boolean.class)
                    .getValue(mockLinkELContext);
            result = Boolean.TRUE;
        }};

        boolean result = linkContext.evaluateAsBoolean(expression);

        assertThat(result, is(true));
    }

    @Test
    public void testEvaluateAsString() {

        final String expression = "expression";

        new StrictExpectations() {{
            mockExpressionFactory
                    .createValueExpression(mockLinkELContext, expression, String.class)
                    .getValue(mockLinkELContext);
            result = "evaluated_string";
        }};

        String result = linkContext.evaluateAsString(expression);

        assertThat(result, is(equalTo("evaluated_string")));
    }

    @Test
    public void testExpand() {

        final String template = "/template/{id}";

        Map<String, String> bindings = new HashMap<>();
        bindings.put("id", "${id}");

        final Map<String, Object> evaluatedBindings = new HashMap<>();
        evaluatedBindings.put("id", 123);

        new StrictExpectations() {{
            mockExpressionFactory
                    .createValueExpression(mockLinkELContext, "${id}", Object.class)
                    .getValue(mockLinkELContext);
            result = 123;

            mockUriTemplateExpander.expand(template, evaluatedBindings, true);
            result = "/template/123";
        }};

        String result = linkContext.expand(template, bindings, true);

        assertThat(result, is(equalTo("/template/123")));
    }

    @Test
    public void testStyle_with_ABSOLUTE() throws Exception {

        final String template = "template";
        final String styledTemplate = "ABSOLUTE - template";

        new StrictExpectations() {{
            mockLinkContextResolver.resolveAbsolute(template);
            result = styledTemplate;
        }};

        String result = linkContext.style(Style.ABSOLUTE, template);

        assertThat(result, is(equalTo(styledTemplate)));
    }

    @Test
    public void testStyle_with_ABSOLUTE_PATH() throws Exception {

        final String template = "template";
        final String styledTemplate = "ABSOLUTE_PATH - template";

        new StrictExpectations() {{
            mockLinkContextResolver.resolveAbsolutePath(template);
            result = styledTemplate;
        }};

        String result = linkContext.style(Style.ABSOLUTE_PATH, template);

        assertThat(result, is(equalTo(styledTemplate)));
    }

    @Test
    public void testStyle_with_RELATIVE_PATH() throws Exception {

        final String template = "template";
        final String styledTemplate = "RELATIVE_PATH - template";

        new StrictExpectations() {{
            mockLinkContextResolver.resolveRelativePath(template);
            result = styledTemplate;
        }};

        String result = linkContext.style(Style.RELATIVE_PATH, template);

        assertThat(result, is(equalTo(styledTemplate)));
    }
}