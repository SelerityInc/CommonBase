/*
 * Copyright (C) 2016-2017 Selerity, Inc. (support@seleritycorp.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seleritycorp.common.base.http.client;

import com.google.inject.assistedinject.Assisted;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

import javax.inject.Inject;

public class HttpRequest {
  interface Factory {
    HttpRequest create(String uri);
  }

  private final String uri;
  private final HttpClient httpClient;
  private final HttpResponse.Factory responseFactory;
  private String userAgent;
  
  @Inject
  HttpRequest(@Assisted String uri, HttpClient httpClient,
      HttpResponse.Factory responseFactory) {
    this.uri = uri;
    this.httpClient = httpClient;
    this.responseFactory = responseFactory;
    this.userAgent = null;
  }

  /**
   * Sets the User-Agent header to use for this request.
   * 
   * @param userAgent The value to use as User-Agent header. If null, no User-Agent header will
   *     get sent.
   * @return The current request instance.
   */
  public HttpRequest setUserAgent(String userAgent) {
    this.userAgent = userAgent;
    return this;
  }

  /**
   * Executes the configured request.
   *
   * @return The server's response to the request.
   * @throws HttpException if an error occurs.
   */
  public HttpResponse execute() throws HttpException {
    final HttpResponse ret;
    final HttpUriRequest request;

    try {
      request = new HttpGet(uri);
    } catch (IllegalArgumentException e) {
      throw new HttpException("Failed to create URI '" + uri + "'", e);
    }

    if (userAgent != null) {
      request.setHeader(HTTP.USER_AGENT, userAgent);
    }

    final org.apache.http.HttpResponse response;
    try {
      response = httpClient.execute(request);
    } catch (IOException e) {
      throw new HttpException("Failed to execute request to '" + uri + "'", e);
    }

    ret = responseFactory.create(response);

    return ret;
  }
}
