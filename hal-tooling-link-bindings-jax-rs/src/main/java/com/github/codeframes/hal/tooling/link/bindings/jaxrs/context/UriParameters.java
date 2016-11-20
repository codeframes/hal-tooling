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
package com.github.codeframes.hal.tooling.link.bindings.jaxrs.context;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A stripped down implementation of a Map, to serve as a means to simply retrieve uri parameter values, used in link
 * binding EL expressions. Mutator and iteration methods are not supported and if invoked will result in an
 * {@link UnsupportedOperationException}.
 */
public class UriParameters implements Map<String, Object> {

    private final MultivaluedMap<String, String> pathParameters;
    private final MultivaluedMap<String, String> queryParameters;

    /**
     * Constructs a UriParameters for the given uriInfo.
     *
     * @param uriInfo the object providing the necessary URI request information.
     */
    public UriParameters(UriInfo uriInfo) {
        this.pathParameters = uriInfo.getPathParameters();
        this.queryParameters = uriInfo.getQueryParameters();
    }

    @Override
    public int size() {
        return pathParameters.size() + queryParameters.size();
    }

    @Override
    public boolean isEmpty() {
        return pathParameters.isEmpty() || queryParameters.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return pathParameters.containsKey(key) || queryParameters.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return pathParameters.containsValue(value) || queryParameters.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        List<String> values = pathParameters.get(key);
        if (values == null) {
            values = queryParameters.get(key);
        }

        if (values != null && values.size() == 1) {
            return values.get(0);
        }
        return values;
    }

    /**
     * Not supported, throws {@link UnsupportedOperationException}
     */
    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported, throws {@link UnsupportedOperationException}
     */
    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported, throws {@link UnsupportedOperationException}
     */
    @Override
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported, throws {@link UnsupportedOperationException}
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported, throws {@link UnsupportedOperationException}
     */
    @Override
    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported, throws {@link UnsupportedOperationException}
     */
    @Override
    public Collection<Object> values() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported, throws {@link UnsupportedOperationException}
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
