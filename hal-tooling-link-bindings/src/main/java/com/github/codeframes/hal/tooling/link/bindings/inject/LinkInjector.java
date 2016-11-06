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
package com.github.codeframes.hal.tooling.link.bindings.inject;

import com.github.codeframes.hal.tooling.core.HalRepresentable;
import com.github.codeframes.hal.tooling.link.bindings.CurieDef;
import com.github.codeframes.hal.tooling.link.bindings.CurieDefs;
import com.github.codeframes.hal.tooling.link.bindings.LinkRel;
import com.github.codeframes.hal.tooling.link.bindings.LinkRels;
import com.github.codeframes.hal.tooling.link.bindings.api.LinkContextResolver;
import com.github.codeframes.hal.tooling.link.bindings.api.LinkTemplateFactory;
import com.github.codeframes.hal.tooling.link.bindings.api.LiteralLinkTemplateFactory;
import com.github.codeframes.hal.tooling.link.bindings.context.DefaultLinkContext;
import com.github.codeframes.hal.tooling.link.bindings.context.DefaultLinkELContext;
import com.github.codeframes.hal.tooling.link.bindings.context.LinkContext;
import com.github.codeframes.hal.tooling.link.bindings.context.LinkELContext;
import com.github.codeframes.hal.tooling.link.bindings.uri.UriTemplateExpander;
import com.github.codeframes.hal.tooling.link.bindings.uri.UriValueResolver;
import com.github.codeframes.hal.tooling.utils.Validate;

import javax.el.ExpressionFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Responsible for injecting links ({@link com.github.codeframes.hal.tooling.core.Link Link},
 * {@link com.github.codeframes.hal.tooling.core.Curie Curie}) into beans of type {@link HalRepresentable} on fields
 * annotated with one of ({@link LinkRel LinkRel}, {@link LinkRels LinkRels}, {@link CurieDef CurieDef},
 * {@link CurieDefs CurieDefs}).
 */
public abstract class LinkInjector {

    /**
     * Injects links into the given entity on fields annotated with one of ({@link LinkRel LinkRel},
     * {@link LinkRels LinkRels}, {@link CurieDef CurieDef},
     * {@link CurieDefs CurieDefs}).
     *
     * @param entity              the bean of which to inject links
     * @param linkContextResolver responsible for resolving link contexts which is used for link styling
     */
    public abstract void injectLinks(final HalRepresentable entity, final LinkContextResolver linkContextResolver);

    /**
     * The default LinkInjector.
     */
    public static LinkInjector defaultInstance() {
        return Holder.DEFAULT_INSTANCE;
    }

    /**
     * A LinkInjectorBuilder used to customise the constructions of a LinkInjector.
     */
    public static LinkInjectorBuilder instanceBuilder() {
        return new LinkInjectorBuilder();
    }

    /**
     * Builder class for constructing LinkInjector's.
     */
    public static final class LinkInjectorBuilder {

        private ExpressionFactory expressionFactory;
        private LinkTemplateFactory linkTemplateFactory;
        private List<UriValueResolver<?>> uriValueResolvers;

        private LinkInjectorBuilder() {
        }

        /**
         * Specifies the Expression Factory to use for the parsing of any expression's declared with
         * {@link LinkRel LinkRel} annotations.
         *
         * @param expressionFactory factory to use for the parsing of expression's
         * @return {@code this}
         */
        public LinkInjectorBuilder expressionFactory(ExpressionFactory expressionFactory) {
            this.expressionFactory = Validate.notNull(expressionFactory, "LinkInjectorBuilder.expressionFactory");
            return this;
        }

        /**
         * Specifies the Link Template Factory to use for constructing Link Templates.
         *
         * @param linkTemplateFactory factory to use for the construction of Link Templates
         * @return {@code this}
         */
        public LinkInjectorBuilder linkTemplateFactory(LinkTemplateFactory linkTemplateFactory) {
            this.linkTemplateFactory = Validate.notNull(linkTemplateFactory, "LinkInjectorBuilder.linkTemplateFactory");
            return this;
        }

        /**
         * Specifies the URI Value Resolvers to use for resolving types to String representations used for parameter
         * substitution in URI Template expansion.
         *
         * @param uriValueResolvers type resolvers for converting types to Strings used for parameter substitution in
         *                          URI Template expansion.
         * @return {@code this}
         */
        public LinkInjectorBuilder uriValueResolvers(Collection<UriValueResolver<?>> uriValueResolvers) {
            this.uriValueResolvers = new ArrayList<>(Validate.notNull(uriValueResolvers, "LinkInjectorBuilder.uriValueResolvers"));
            return this;
        }

        /**
         * Returns a LinkInjector configured with the specified options of this builder.
         */
        public LinkInjector build() {
            return new DefaultLinkInjector(getExpressionFactory(), getLinkSetterFactory(), getUriTemplateExpander());
        }

        private ExpressionFactory getExpressionFactory() {
            return this.expressionFactory == null ? ExpressionFactory.newInstance() : this.expressionFactory;
        }

        private LinkSetterFactory getLinkSetterFactory() {
            return LinkSetterFactory.newInstance(
                    this.linkTemplateFactory == null ? new LiteralLinkTemplateFactory() : this.linkTemplateFactory
            );
        }

        private UriTemplateExpander getUriTemplateExpander() {
            return new UriTemplateExpander(
                    this.uriValueResolvers == null ? Collections.<UriValueResolver<?>>emptyList() : this.uriValueResolvers
            );
        }
    }

    private static final class DefaultLinkInjector extends LinkInjector {

        private final ExpressionFactory expressionFactory;
        private final UriTemplateExpander uriTemplateExpander;
        private final LinkSetterFactory linkSetterFactory;

        DefaultLinkInjector(ExpressionFactory expressionFactory, LinkSetterFactory linkSetterFactory, UriTemplateExpander uriTemplateExpander) {
            this.expressionFactory = expressionFactory;
            this.linkSetterFactory = linkSetterFactory;
            this.uriTemplateExpander = uriTemplateExpander;
        }

        @Override
        public void injectLinks(final HalRepresentable entity, final LinkContextResolver linkContextResolver) {
            if (entity != null) {
                final LinkELContext linkELContext = new DefaultLinkELContext(entity);
                final LinkContext linkContext = new DefaultLinkContext(expressionFactory, linkContextResolver, uriTemplateExpander, linkELContext);
                final BeanLinkSetter beanLinkSetter = linkSetterFactory.getBeanLinkSetter(entity.getClass());
                beanLinkSetter.setLinks(entity, linkContext);
            }
        }
    }

    private static class Holder {
        static final LinkInjector DEFAULT_INSTANCE = new LinkInjectorBuilder().build();

        private Holder() {
        }
    }
}
