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

import com.github.codeframes.hal.tooling.core.Curie;
import com.github.codeframes.hal.tooling.link.bindings.core.CurieDescriptor;
import com.github.codeframes.hal.tooling.link.bindings.core.FieldAccessor;
import com.github.codeframes.hal.tooling.link.bindings.core.LinkProvider;

class CurieFieldSetter implements LinkSetter {

    private final FieldAccessor fieldAccessor;
    private final CurieDescriptor curieDescriptor;

    public CurieFieldSetter(FieldAccessor fieldAccessor, CurieDescriptor curieDescriptor) {
        this.fieldAccessor = fieldAccessor;
        this.curieDescriptor = curieDescriptor;
    }

    @Override
    public void setLinks(Object instance, LinkProvider linkProvider) {
        final Curie curie = linkProvider.getCurie(curieDescriptor);
        if (curie != null) {
            fieldAccessor.setValue(instance, curie);
        }
    }
}
