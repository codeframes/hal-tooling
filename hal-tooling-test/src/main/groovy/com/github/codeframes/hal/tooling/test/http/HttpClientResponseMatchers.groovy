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
import com.github.codeframes.hal.tooling.test.json.JsonObjectMatchers
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

/**
 * A collection of Hamcrest Matcher's for HttpClient Response assertions.
 */
class HttpClientResponseMatchers {

    static {
        // Force JSON static initializer. If only using JSON with the 'as' keyword for type coercion from String or
        // GString, the associated asType methods will not have been added resulting in error.
        new JSON('{}')
    }

    /**
     * An alias of {@link #hasContentType(java.lang.String)}
     */
    static Matcher has_content_type(String media_type) {
        hasContentType(media_type)
    }

    /**
     * Returns a Matcher for asserting if a provided Response has a Content-Type header that matches that of mediaType.
     *
     * @param mediaType the expected Content-Type to match against
     * @return a Matcher to assert if a provided Response has a Content-Type header that matches that of mediaType
     */
    static Matcher hasContentType(String mediaType) {
        [
                matches         : { actual -> actual.headers.'Content-Type' == mediaType },
                describeTo      : { description -> description.appendText("Content-Type of ").appendValue(mediaType) },
                describeMismatch: { item, description ->
                    description.appendText("was ").appendValue(item.headers.'Content-Type')
                }
        ] as BaseMatcher
    }

    /**
     * An alias of {@link #hasCacheControl(java.lang.String)}
     */
    static Matcher has_cache_control(String cc_header) {
        hasCacheControl(cc_header)
    }

    /**
     * Returns a Matcher for asserting if a provided Response has a Cache-Control header that matches that of ccHeader.
     *
     * @param ccHeader the expected Cache-Control to match against
     * @return a Matcher to assert if a provided Response has a Cache-Control header that matches that of ccHeader
     */
    static Matcher hasCacheControl(String ccHeader) {
        [
                matches         : { actual -> actual.headers.'Cache-Control' == ccHeader },
                describeTo      : { description ->
                    description.appendText("Cache-Control of ").appendValue(ccHeader)
                },
                describeMismatch: { item, description ->
                    description.appendText("was ").appendValue(item.headers.'Cache-Control')
                }
        ] as BaseMatcher
    }

    /**
     * An alias of {@link #hasNoCacheControl()}
     */
    static Matcher has_no_cache_control() {
        hasNoCacheControl()
    }

    /**
     * Returns a Matcher for asserting if a provided Response has no Cache-Control header.
     *
     * @return a Matcher to assert if a provided Response has no Cache-Control header
     */
    static Matcher hasNoCacheControl() {
        [
                matches         : { actual -> actual.headers.'Cache-Control' == null },
                describeTo      : { description -> description.appendText("response should have no cache control") },
                describeMismatch: { item, description ->
                    description.appendText("was ").appendValue(item.headers.'Cache-Control')
                }
        ] as BaseMatcher
    }

    /**
     * An alias of {@link #hasETag()}
     */
    static Matcher has_ETag() {
        hasETag()
    }

    /**
     * Returns a Matcher for asserting if a provided Response has an ETag header.
     *
     * @return a Matcher to assert if a provided Response has an ETag header
     */
    static Matcher hasETag() {
        [
                matches         : { actual -> actual.headers.'ETag' != null },
                describeTo      : { description -> description.appendText("response should contain an ETag header") },
                describeMismatch: { item, description -> description.appendText("no ETag header was found") }
        ] as BaseMatcher
    }

    /**
     * An alias of {@link #hasStatus(int)}
     */
    static Matcher has_status(int status_code) {
        hasStatus(status_code)
    }

    /**
     * Returns a Matcher for asserting if a provided Response has a Status Code that matches that of statusCode.
     *
     * @param statusCode the expected Status Code to match against
     * @return a Matcher to assert if a provided Response has a Status Code that matches that of statusCode
     */
    static Matcher hasStatus(int statusCode) {
        [
                matches         : { actual -> actual.status == statusCode },
                describeTo      : { description -> description.appendText("status code of ").appendValue(statusCode) },
                describeMismatch: { item, description -> description.appendText("was ").appendValue(item.status) }
        ] as BaseMatcher
    }

    /**
     * An alias of {@link #hasBody(com.github.codeframes.hal.tooling.test.json.JSON)}
     */
    static Matcher has_body(JSON body) {
        hasBody(body)
    }

    /**
     * Returns a Matcher for asserting if a provided Response has a JSON Body that matches that of body.
     *
     * @param body the expected JSON Body to match against
     * @return a Matcher to assert if a provided Response has a JSON Body that matches that of body
     */
    static Matcher hasBody(JSON body) {
        def matcher
        def json = body.toObject()
        if (json instanceof Map) {
            matcher = JsonObjectMatchers.equalTo(json)
        } else {
            matcher = JsonObjectMatchers.equalTo((List) json)
        }

        [
                matches         : { actual -> matcher.matches(actual.data) },
                describeTo      : { Description description -> matcher.describeTo(description) },
                describeMismatch: { Object item, Description description -> matcher.describeMismatch(item, description) }
        ] as BaseMatcher
    }

    /**
     * An alias of {@link #hasBody(byte [ ])}
     */
    static Matcher has_body(byte[] body) {
        hasBody(body)
    }

    /**
     * Returns a Matcher for asserting if a provided Response has a Byte Array Body that matches that of body.
     *
     * @param body the expected Byte Array Body to match against
     * @return a Matcher to assert if a provided Response has a Byte Array Body that matches that of body
     */
    static Matcher hasBody(byte[] body) {
        def bytes
        [
                matches         : { actual -> (bytes = actual.data.bytes) == body },
                describeTo      : { description -> description.appendText("content body of ").appendValue(body) },
                describeMismatch: { item, description -> description.appendText("was ").appendValue(bytes) }
        ] as BaseMatcher
    }
}
