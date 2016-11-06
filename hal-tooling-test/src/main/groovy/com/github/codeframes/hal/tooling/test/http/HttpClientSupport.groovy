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

import com.github.codeframes.hal.tooling.test.json.JSON
import org.hamcrest.Matcher

/**
 * A convenient collection of methods for working with HttpClient
 */
trait HttpClientSupport {

    /**
     * An alias of {@link #hasContentType(java.lang.String)}
     */
    Matcher has_content_type(String media_type) {
        HttpClientResponseMatchers.hasContentType(media_type)
    }

    /**
     * Returns a Matcher for asserting if a provided Response has a Content-Type header that matches that of mediaType.
     *
     * @param mediaType the expected Content-Type to match against
     * @return a Matcher to assert if a provided Response has a Content-Type header that matches that of mediaType
     */
    Matcher hasContentType(String mediaType) {
        HttpClientResponseMatchers.hasContentType(mediaType)
    }

    /**
     * An alias of {@link #hasCacheControl(java.lang.String)}
     */
    Matcher has_cache_control(String cc_header) {
        HttpClientResponseMatchers.hasCacheControl(cc_header)
    }

    /**
     * Returns a Matcher for asserting if a provided Response has a Cache-Control header that matches that of ccHeader.
     *
     * @param ccHeader the expected Cache-Control to match against
     * @return a Matcher to assert if a provided Response has a Cache-Control header that matches that of ccHeader
     */
    Matcher hasCacheControl(String ccHeader) {
        HttpClientResponseMatchers.hasCacheControl(ccHeader)
    }

    /**
     * An alias of {@link #hasNoCacheControl()}
     */
    Matcher has_no_cache_control() {
        HttpClientResponseMatchers.hasNoCacheControl()
    }

    /**
     * Returns a Matcher for asserting if a provided Response has no Cache-Control header.
     *
     * @return a Matcher to assert if a provided Response has no Cache-Control header
     */
    Matcher hasNoCacheControl() {
        HttpClientResponseMatchers.hasNoCacheControl()
    }

    /**
     * An alias of {@link #hasETag()}
     */
    Matcher has_ETag() {
        HttpClientResponseMatchers.hasETag()
    }

    /**
     * Returns a Matcher for asserting if a provided Response has an ETag header.
     *
     * @return a Matcher to assert if a provided Response has an ETag header
     */
    Matcher hasETag() {
        HttpClientResponseMatchers.hasETag()
    }

    /**
     * An alias of {@link #hasStatus(int)}
     */
    Matcher has_status(int status_code) {
        HttpClientResponseMatchers.hasStatus(status_code)
    }

    /**
     * Returns a Matcher for asserting if a provided Response has a Status Code that matches that of statusCode.
     *
     * @param statusCode the expected Status Code to match against
     * @return a Matcher to assert if a provided Response has a Status Code that matches that of statusCode
     */
    Matcher hasStatus(int statusCode) {
        HttpClientResponseMatchers.hasStatus(statusCode)
    }

    /**
     * An alias of {@link #hasBody(com.github.codeframes.hal.tooling.test.json.JSON)}
     */
    Matcher has_body(JSON body) {
        HttpClientResponseMatchers.hasBody(body)
    }

    /**
     * Returns a Matcher for asserting if a provided Response has a JSON Body that matches that of body.
     *
     * @param body the expected JSON Body to match against
     * @return a Matcher to assert if a provided Response has a JSON Body that matches that of body
     */
    Matcher hasBody(JSON body) {
        HttpClientResponseMatchers.hasBody(body)
    }

    /**
     * An alias of {@link #hasBody(byte [ ])}
     */
    static Matcher has_body(byte[] body) {
        HttpClientResponseMatchers.hasBody(body)
    }

    /**
     * Returns a Matcher for asserting if a provided Response has a Byte Array Body that matches that of body.
     *
     * @param body the expected Byte Array Body to match against
     * @return a Matcher to assert if a provided Response has a Byte Array Body that matches that of body
     */
    static Matcher hasBody(byte[] body) {
        HttpClientResponseMatchers.hasBody(body)
    }
}