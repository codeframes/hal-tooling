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
package com.github.codeframes.hal.tooling.test.http

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.SpecInfo

@TypeChecked
@CompileStatic
class InjectHttpClientMethodInterceptor extends AbstractMethodInterceptor {

    private final FieldInfo fieldInfo
    private final HttpClient httpClient

    InjectHttpClientMethodInterceptor(FieldInfo fieldInfo, HttpClient httpClient) {
        this.fieldInfo = fieldInfo
        this.httpClient = httpClient
    }

    @Override
    void interceptInitializerMethod(IMethodInvocation invocation) throws Throwable {
        fieldInfo.writeValue(invocation.instance, httpClient)
        invocation.proceed()
    }

    void install(SpecInfo specInfo) {
        specInfo.addInitializerInterceptor(this)
    }
}
