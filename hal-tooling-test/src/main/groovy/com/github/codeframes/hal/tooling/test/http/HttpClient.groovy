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

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.client.ClientProtocolException

/**
 * A modified RESTClient that returns HttpResponseDecorator's on error responses instead of throwing
 * HttpResponseException's. Also includes enhanced encoding/parsing of requests/responses that considers type, subtype
 * and suffix of media types.
 */
class HttpClient extends RESTClient {

    /**
     * See {@link groovyx.net.http.HTTPBuilder#HTTPBuilder(Object)}
     * @param defaultURI default request URI (String, URI, URL or {@link groovyx.net.http.URIBuilder})
     * @throws URISyntaxException
     */
    def HttpClient(defaultURI) throws URISyntaxException {
        super(defaultURI)
        this.encoders = new HttpClientEncoderRegistry()
        this.parsers = new HttpClientParserRegistry()
    }

    /**
     * Convenience method to perform an HTTP GET request. It will use the HTTPBuilder's {@link #getHandler() registered
     * response handlers} to handle success or failure status codes. By default, the
     * {@link #defaultSuccessHandler(groovyx.net.http.HttpResponseDecorator, Object)} <code>success</code> response
     * handler will return a decorated response object that can be used to read response headers and data.
     * <p>
     * A 'failed' response (i.e. any HTTP status code > 399) will be handled by the registered 'failure' handler.
     * The {@link #defaultFailureHandler(groovyx.net.http.HttpResponseDecorator, Object) default failure handler}
     * throws a {@link HttpResponseException} which is caught to return the contained decorated response object instead.
     * </p>
     *
     * @see #defaultSuccessHandler(groovyx.net.http.HttpResponseDecorator, Object)
     * @see #defaultFailureHandler(groovyx.net.http.HttpResponseDecorator, Object)
     * @param args named parameters - see {@link groovyx.net.http.HTTPBuilder.RequestConfigDelegate#setPropertiesFromMap(Map)}
     * @return a {@link groovyx.net.http.HttpResponseDecorator}, unless the default success handler is overridden
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    @Override
    Object get(Map<String, ?> args) throws URISyntaxException, ClientProtocolException, IOException {
        try {
            return super.get(args)
        } catch (HttpResponseException ex) {
            return ex.response
        }
    }

    /**
     * Convenience method to perform a POST request. It will use the HTTPBuilder's {@link #getHandler() registered
     * response handlers} to handle success or failure status codes. By default, the
     * {@link #defaultSuccessHandler(groovyx.net.http.HttpResponseDecorator, Object)} <code>success</code> response
     * handler will return a decorated response object that can be used to read response headers and data.
     * <p>
     * The request body (specified by a <code>body</code> named parameter) will be encoded based on the
     * <code>requestContentType</code> named parameter, or if none is given, the default
     * {@link groovyx.net.http.HTTPBuilder#setContentType(Object) content-type} for this instance.
     * </p><p>
     * A 'failed' response (i.e. any HTTP status code > 399) will be handled by the registered 'failure' handler.
     * The {@link #defaultFailureHandler(groovyx.net.http.HttpResponseDecorator, Object) default failure handler}
     * throws a {@link HttpResponseException} which is caught to return the contained decorated response object instead.
     * </p>
     *
     * @param args named parameters - see {@link groovyx.net.http.HTTPBuilder.RequestConfigDelegate#setPropertiesFromMap(Map)}
     * @return a {@link groovyx.net.http.HttpResponseDecorator}, unless the default success handler is overridden.
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Override
    Object post(Map<String, ?> args) throws URISyntaxException, ClientProtocolException, IOException {
        copyRequestContentTypeToHeaders(args)
        try {
            return super.post(args)
        } catch (HttpResponseException ex) {
            return ex.response
        }
    }

    /**
     * Convenience method to perform a PUT request. It will use the HTTPBuilder's {@link #getHandler() registered
     * response handlers} to handle success or failure status codes. By default, the
     * {@link #defaultSuccessHandler(groovyx.net.http.HttpResponseDecorator, Object)} <code>success</code> response
     * handler will return a decorated response object that can be used to read response headers and data.
     * <p>
     * The request body (specified by a <code>body</code> named parameter) will be encoded based on the
     * <code>requestContentType</code> named parameter, or if none is given, the default
     * {@link groovyx.net.http.HTTPBuilder#setContentType(Object) content-type} for this instance.
     * </p><p>
     * A 'failed' response (i.e. any HTTP status code > 399) will be handled by the registered 'failure' handler.
     * The {@link #defaultFailureHandler(groovyx.net.http.HttpResponseDecorator, Object) default failure handler}
     * throws a {@link HttpResponseException} which is caught to return the contained decorated response object instead.
     * </p>
     *
     * @param args named parameters - see {@link groovyx.net.http.HTTPBuilder.RequestConfigDelegate#setPropertiesFromMap(Map)}
     * @return a {@link groovyx.net.http.HttpResponseDecorator}, unless the default success handler is overridden.
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Override
    Object put(Map<String, ?> args) throws URISyntaxException, ClientProtocolException, IOException {
        copyRequestContentTypeToHeaders(args)
        try {
            return super.put(args)
        } catch (HttpResponseException ex) {
            return ex.response
        }
    }

    /**
     * Convenience method to perform a PATCH request. It will use the HTTPBuilder's {@link #getHandler() registered
     * response handlers} to handle success or failure status codes. By default, the
     * {@link #defaultSuccessHandler(groovyx.net.http.HttpResponseDecorator, Object)} <code>success</code> response
     * handler will return a decorated response object that can be used to read response headers and data.
     * <p>
     * The request body (specified by a <code>body</code> named parameter) will be encoded based on the
     * <code>requestContentType</code> named parameter, or if none is given, the default
     * {@link groovyx.net.http.HTTPBuilder#setContentType(Object) content-type} for this instance.
     * </p><p>
     * A 'failed' response (i.e. any HTTP status code > 399) will be handled by the registered 'failure' handler.
     * The {@link #defaultFailureHandler(groovyx.net.http.HttpResponseDecorator, Object) default failure handler}
     * throws a {@link HttpResponseException} which is caught to return the contained decorated response object instead.
     * </p>
     *
     * @param args named parameters - see {@link groovyx.net.http.HTTPBuilder.RequestConfigDelegate#setPropertiesFromMap(Map)}
     * @return a {@link groovyx.net.http.HttpResponseDecorator}, unless the default success handler is overridden.
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Override
    Object patch(Map<String, ?> args) throws URISyntaxException, ClientProtocolException, IOException {
        copyRequestContentTypeToHeaders(args)
        try {
            return super.patch(args)
        } catch (HttpResponseException ex) {
            return ex.response
        }
    }

    static copyRequestContentTypeToHeaders(Map<String, ?> args) {
        if (args['requestContentType']) {
            def headers = args['headers']
            if (!headers) {
                headers = [:]
                args['headers'] = headers
            }
            headers['Content-Type'] = args['requestContentType']
        }
    }

    /**
     * Perform a HEAD request, often used to check preconditions before sending a large PUT or POST request. It will use
     * the HTTPBuilder's {@link #getHandler() registered response handlers} to handle success or failure status codes.
     * By default, the {@link #defaultSuccessHandler(groovyx.net.http.HttpResponseDecorator, Object)}
     * <code>success</code> response handler will return a decorated response object that can be used to read response
     * headers and data.
     * <p>
     * A 'failed' response (i.e. any HTTP status code > 399) will be handled by the registered 'failure' handler.
     * The {@link #defaultFailureHandler(groovyx.net.http.HttpResponseDecorator, Object) default failure handler}
     * throws a {@link HttpResponseException} which is caught to return the contained decorated response object instead.
     * </p>
     *
     * @param args named parameters - see {@link groovyx.net.http.HTTPBuilder.RequestConfigDelegate#setPropertiesFromMap(Map)}
     * @return a {@link groovyx.net.http.HttpResponseDecorator}, unless the default success handler is overridden.
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Override
    Object head(Map<String, ?> args) throws URISyntaxException, ClientProtocolException, IOException {
        try {
            return super.head(args)
        } catch (HttpResponseException ex) {
            return ex.response
        }
    }

    /**
     * Perform a DELETE request. This method does not accept a <code>body</code> argument. It will use the HTTPBuilder's
     * {@link #getHandler() registered response handlers} to handle success or failure status codes. By default, the
     * {@link #defaultSuccessHandler(groovyx.net.http.HttpResponseDecorator, Object)} <code>success</code> response
     * handler will return a decorated response object that can be used to read response headers and data.
     * <p>
     * A 'failed' response (i.e. any HTTP status code > 399) will be handled by the registered 'failure' handler.
     * The {@link #defaultFailureHandler(groovyx.net.http.HttpResponseDecorator, Object) default failure handler}
     * throws a {@link HttpResponseException} which is caught to return the contained decorated response object instead.
     * </p>
     *
     * @param args named parameters - see {@link groovyx.net.http.HTTPBuilder.RequestConfigDelegate#setPropertiesFromMap(Map)}
     * @return a {@link groovyx.net.http.HttpResponseDecorator}, unless the default success handler is overridden.
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Override
    Object delete(Map<String, ?> args) throws URISyntaxException, ClientProtocolException, IOException {
        try {
            return super.delete(args)
        } catch (HttpResponseException ex) {
            return ex.response
        }
    }

    /**
     * Perform an OPTIONS request. It will use the HTTPBuilder's {@link #getHandler() registered response handlers} to
     * handle success or failure status codes. By default, the
     * {@link #defaultSuccessHandler(groovyx.net.http.HttpResponseDecorator, Object)} <code>success</code> response
     * handler will return a decorated response object that can be used to read response headers and data.
     * <p>
     * A 'failed' response (i.e. any HTTP status code > 399) will be handled by the registered 'failure' handler.
     * The {@link #defaultFailureHandler(groovyx.net.http.HttpResponseDecorator, Object) default failure handler}
     * throws a {@link HttpResponseException} which is caught to return the contained decorated response object instead.
     * </p>
     *
     * @param args named parameters - see {@link groovyx.net.http.HTTPBuilder.RequestConfigDelegate#setPropertiesFromMap(Map)}
     * @return a {@link groovyx.net.http.HttpResponseDecorator}, unless the default success handler is overridden.
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Override
    Object options(Map<String, ?> args) throws ClientProtocolException, IOException, URISyntaxException {
        try {
            return super.options(args)
        } catch (HttpResponseException ex) {
            return ex.response
        }
    }
}
