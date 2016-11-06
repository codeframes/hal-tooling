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
package com.github.codeframes.hal.tooling.json.ser.config;

import com.github.codeframes.hal.tooling.json.LinkSerialization.LinkSerializationMethod;

import java.io.Serializable;

/**
 * Specifies serialization options.
 */
public final class HalSerializationConfig implements Serializable {

    private static final long serialVersionUID = -2160587907895239135L;

    private final LinkSerializationMethod linkSerializationMethod;

    private HalSerializationConfig(LinkSerializationMethod linkSerializationMethod) {
        this.linkSerializationMethod = linkSerializationMethod;
    }

    /**
     * Returns a new HalSerializationConfig with the following default options:
     * <table>
     * <tr>
     * <th>Option</th>
     * <th>Value</th>
     * </tr>
     * <tr>
     * <td>LinkSerializationMethod</td>
     * <td>IMPLICIT</td>
     * </tr>
     * </table>
     *
     * @return new instance with applied default options
     */
    public static HalSerializationConfig defaultInstance() {
        return new HalSerializationConfig(LinkSerializationMethod.IMPLICIT);
    }

    /**
     * Sets the default link serialization method to implicit.
     *
     * @return new instance
     * @see LinkSerializationMethod
     */
    public HalSerializationConfig withImplicitLinkSerialization() {
        return new HalSerializationConfig(LinkSerializationMethod.IMPLICIT);
    }

    /**
     * Sets the default link serialization method to explicit.
     *
     * @return new instance
     * @see LinkSerializationMethod
     */
    public HalSerializationConfig withExplicitLinkSerialization() {
        return new HalSerializationConfig(LinkSerializationMethod.EXPLICIT);
    }

    /**
     * @return the default link serialization method to use. This is chosen if one is not provided via a
     * {@link LinkSerializationMethod LinkSerializationMethod} annotation.
     */
    public LinkSerializationMethod getLinkSerializationMethod() {
        return linkSerializationMethod;
    }
}
