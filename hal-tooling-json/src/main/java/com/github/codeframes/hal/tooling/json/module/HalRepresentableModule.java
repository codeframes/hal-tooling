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
package com.github.codeframes.hal.tooling.json.module;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.codeframes.hal.tooling.core.Embeddable;
import com.github.codeframes.hal.tooling.json.ser.HalRepresentableSerializerModifier;
import com.github.codeframes.hal.tooling.json.ser.HalSerializers;
import com.github.codeframes.hal.tooling.json.ser.config.HalSerializationConfig;
import com.github.codeframes.hal.tooling.json.ser.embedded.EmbeddableMixIn;

/**
 * A Jackson Module providing serialization support for {@link com.github.codeframes.hal.tooling.core.HalRepresentable HalRepresentable}
 * marked beans into the <b>application/hal+json</b> media type format.<br/>
 * The module needs to be registered to an {@link com.fasterxml.jackson.databind.ObjectMapper ObjectMapper}.
 * <p/>
 * Example:
 * <pre>{@code
 * ObjectMapper mapper = new ObjectMapper();
 * mapper.registerModule(new HalRepresentableModule());
 * }</pre>
 */
public class HalRepresentableModule extends SimpleModule {

    private static final long serialVersionUID = 3629200267739999260L;

    private final HalSerializationConfig serializationConfig;

    public HalRepresentableModule() {
        this(HalSerializationConfig.defaultInstance());
    }

    public HalRepresentableModule(HalSerializationConfig serializationConfig) {
        this.serializationConfig = serializationConfig;
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addBeanSerializerModifier(new HalRepresentableSerializerModifier(serializationConfig));
        context.addSerializers(new HalSerializers());
        context.setMixInAnnotations(Embeddable.class, EmbeddableMixIn.class);
    }
}
