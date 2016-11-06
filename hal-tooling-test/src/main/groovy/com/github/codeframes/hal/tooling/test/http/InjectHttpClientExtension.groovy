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

import com.github.codeframes.hal.tooling.test.TestProperties
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FieldInfo

@TypeChecked
@CompileStatic
class InjectHttpClientExtension extends AbstractAnnotationDrivenExtension<InjectHttpClient> {

    @Override
    void visitFieldAnnotation(InjectHttpClient injectHttpClient, FieldInfo fieldInfo) {
        install(fieldInfo)
    }

    static void install(FieldInfo fieldInfo) {
        def defaultURI = TestProperties.get(TestProperties.HTTP_CLIENT_DEFAULT_URI)
        def client = new HttpClient(defaultURI)
        def methodInterceptor = new InjectHttpClientMethodInterceptor(fieldInfo, client)
        methodInterceptor.install(fieldInfo.parent)
    }
}
