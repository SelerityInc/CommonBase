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

package com.seleritycorp.common.base.http.server;

import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Capsule for parameters of handling a request.
 */
public class HandleParameters {
  private final String target;
  private final Request baseRequest;
  private final HttpServletRequest request;
  private final HttpServletResponse response;

  /**
   * Create a capsule for the parameters of a handle request.
   * 
   * @param target Target URL for the handle request parameters
   * @param baseRequest Base Jetty request for the handle request parameters
   * @param request Http request for the handle request parameters
   * @param response Response for the handle request parameters
   */
  public HandleParameters(String target, Request baseRequest, HttpServletRequest request,
      HttpServletResponse response) {
    this.target = target;
    this.baseRequest = baseRequest;
    this.request = request;
    this.response = response;
  }

  /**
   * Gets the target URL of the handle request.
   * 
   * @return the target.
   */
  public String getTarget() {
    return target;
  }

  /**
   * Gets the base Jetty request of the handle request.
   * 
   * @return the baseRequest.
   */
  public Request getBaseRequest() {
    return baseRequest;
  }

  /**
   * Gets the http request of the handle request.
   *
   * @return the request.
   */
  public HttpServletRequest getRequest() {
    return request;
  }

  /**
   * Gets the response of the handle request.
   * 
   * @return the response.
   */
  public HttpServletResponse getResponse() {
    return response;
  }
}
