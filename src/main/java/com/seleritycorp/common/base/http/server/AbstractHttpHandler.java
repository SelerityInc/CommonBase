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
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handler for http requests.
 */
public abstract class AbstractHttpHandler extends AbstractHandler {
  private HttpRequest.Factory httpRequestFactory;
  
  /*
   * We'd like to avoid method injection here and switch to constructor injection. But that would
   * mean that all implementing classes would need to follow along constructor changes, which will
   * be burdensome. Since we also want to avoid uning InjectorFactory directly to avoid fixed
   * dependencies, we resort to using method injection. 
   */
  /**
   * Sets the factory for HttpRequest wrappers.
   *  
   * @param httpRequestFactory the factory to set
   */
  @Inject
  void setHttpRequestFactory(HttpRequest.Factory httpRequestFactory) {
    this.httpRequestFactory = httpRequestFactory;
  }

  /**
   * Handles a Http request.
   * 
   * @param request The wrapped http request to handle.
   * @throws IOException see
   *    {@link #handle(String, Request, HttpServletRequest, HttpServletResponse)}
   * @throws ServletException see
   *    {@link #handle(String, Request, HttpServletRequest, HttpServletResponse)}
   */
  public abstract void handle(HttpRequest request) throws IOException,
    ServletException;

  @Override
  public void handle(String target, Request request, HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse) throws IOException, ServletException {
    httpServletResponse.setHeader("Server", "n/a");
    HttpRequest httpRequest = httpRequestFactory.create(target, request, httpServletRequest,
        httpServletResponse);
    handle(httpRequest);
  }
}
