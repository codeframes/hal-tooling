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
package com.github.codeframes.hal.tooling.json.core;

import java.util.Comparator;

import static com.github.codeframes.hal.tooling.core.Rels.CURIES;
import static com.github.codeframes.hal.tooling.core.Rels.SELF;

/**
 * A Comparator for sorting relation names (if present) in the following order:
 * <ol>
 * <li>{@value com.github.codeframes.hal.tooling.core.Rels#SELF}</li>
 * <li>{@value com.github.codeframes.hal.tooling.core.Rels#CURIES}</li>
 * <li>all others as defined by {@link String#compareTo(String)}</li>
 * </ol>
 */
public class RelComparator implements Comparator<String> {

    @Override
    public int compare(String rel1, String rel2) {
        final int compareTo = rel1.compareTo(rel2);
        if (compareTo == 0) {
            return 0;
        }

        if (SELF.equals(rel1)) {
            return -1;
        }

        if (SELF.equals(rel2)) {
            return 1;
        }

        if (CURIES.equals(rel1)) {
            return -1;
        }

        if (CURIES.equals(rel2)) {
            return 1;
        }
        return compareTo;
    }
}
