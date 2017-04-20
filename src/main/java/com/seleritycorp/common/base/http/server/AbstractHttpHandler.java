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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handler for http requests.
 */
public abstract class AbstractHttpHandler extends AbstractHandler {
  /**
   * Handles a Http request
   * 
   * @param target The target of the request.
   * @param params The parameters of this handle request.
   * @throws IOException see
   *    {@link #handle(String, Request, HttpServletRequest, HttpServletResponse)}
   * @throws ServletException see
   *    {@link #handle(String, Request, HttpServletRequest, HttpServletResponse)}
   */
  public abstract void handle(String target, HandleParameters params) throws IOException,
    ServletException;

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
    HandleParameters params = new HandleParameters(target, baseRequest, request, response);
    handle(target, params);
  }
}
