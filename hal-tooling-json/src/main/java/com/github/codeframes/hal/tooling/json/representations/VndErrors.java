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
package com.github.codeframes.hal.tooling.json.representations;

import com.github.codeframes.hal.tooling.core.Embedded;
import com.github.codeframes.hal.tooling.core.HalRepresentable;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A Jackson serializable variant of {@link VndError} representing a list of errors.
 */
@Immutable
public class VndErrors implements HalRepresentable {

    private final Embedded<List<VndError>> errors;

    /**
     * Constructs a new VndErrors Object.
     *
     * @param errors the list of errors
     */
    public VndErrors(List<VndError> errors) {
        this.errors = new Embedded<>("errors", Collections.unmodifiableList(new ArrayList<>(errors)));
    }

    /**
     * The list of errors.
     *
     * @return the list of errors
     */
    public Embedded<List<VndError>> getErrors() {
        return errors;
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VndErrors other = (VndErrors) obj;
        return Objects.equals(errors, other.errors);
    }

    @Override
    public String toString() {
        return "VndErrors{" +
                "errors=" + errors +
                '}';
    }
}
