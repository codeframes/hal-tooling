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
package com.github.codeframes.hal.tooling.json.ser.links.processors;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.github.codeframes.hal.tooling.core.Link;
import com.github.codeframes.hal.tooling.json.LinkSerialization;
import com.github.codeframes.hal.tooling.json.LinkSerialization.LinkSerializationMethod;
import com.github.codeframes.hal.tooling.json.core.JavaTypes;
import com.github.codeframes.hal.tooling.json.ser.config.HalSerializationConfig;

public final class LinkProcessorFactory {

    private LinkProcessorFactory() {
    }

    public static LinkProcessor newLinkProcessor(HalSerializationConfig halSerializationConfig,
                                                 BeanPropertyWriter property) {
        final LinkSerializationMethod serializationMethod = getSerializationMethod(halSerializationConfig, property);
        if (serializationMethod == LinkSerializationMethod.IMPLICIT) {
            return newImplicitLinkProcessor(property);
        } else {
            return newExplicitLinkProcessor(property);
        }
    }

    private static LinkSerializationMethod getSerializationMethod(HalSerializationConfig halSerializationConfig,
                                                                  BeanPropertyWriter property) {
        LinkSerialization linkSerialization = property.getAnnotation(LinkSerialization.class);
        if (linkSerialization == null) {
            linkSerialization = property.getMember().getDeclaringClass().getAnnotation(LinkSerialization.class);
        }

        if (linkSerialization == null) {
            return halSerializationConfig.getLinkSerializationMethod();
        } else {
            return linkSerialization.value();
        }
    }

    private static LinkProcessor newImplicitLinkProcessor(BeanPropertyWriter property) {
        if (isLinkType(property)) {
            return new ImplicitLinkProcessor(property);
        } else {
            return new ImplicitLinksProcessor(property);
        }
    }

    private static LinkProcessor newExplicitLinkProcessor(BeanPropertyWriter property) {
        if (isLinkType(property)) {
            return new ExplicitLinkProcessor(property);
        } else {
            return new ExplicitLinksProcessor(property);
        }
    }

    private static boolean isLinkType(BeanPropertyWriter property) {
        final JavaType type = property.getType();
        if (JavaTypes.LINK.equals(type)) {
            return true;
        } else if (JavaTypes.isIterableLinkType(type)) {
            return false;
        } else {
            throw new IllegalArgumentException(
                    String.format("BeanPropertyWriter must refer to a type of: %s or an Iterable thereof", Link.class));
        }
    }
}
