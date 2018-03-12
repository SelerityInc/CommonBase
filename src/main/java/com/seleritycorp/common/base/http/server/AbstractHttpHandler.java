/*
 * Copyright (C) 2016-2018 Selerity, Inc. (support@seleritycorp.com)
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

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

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
    try {
      HttpRequest httpRequest = null;
      try {
        httpServletResponse.setHeader("Server", "n/a");
        httpRequest = httpRequestFactory.create(target, request, httpServletRequest,
            httpServletResponse);

        handle(httpRequest);
        
        if (!httpRequest.hasBeenHandled()) {
          httpRequest.respondNotFound();
        }
      } catch (Exception exception) {
        // The handle method threw an uncaught exception.
        // We try sending a 503 gracefully. If that fails the outer try/catch block will try
        // harder.
        if (httpRequest != null) {
          httpRequest.respondInternalServerError("Uncaught " + exception.toString() + " in "
              + this.getClass(), exception);
        } else {
          throw exception;
        }
      }
    } catch (Exception exception) {
      // The error handler threw an exception. So close things as good as we can.
      lastResortCleanup(request, httpServletResponse);
    }
  }

  private void lastResortCleanup(Request request, HttpServletResponse httpServletResponse) {
    // No do not handle exceptions caught in individual statements, as this is the last resort
    // cleanup and the error got logged as good as we can already.
    //
    // Also each statement has its own try/catch block to allow carrying as many statements as
    // possible even if some might fail.
    String msg = "Internal server error.";
    try {
      httpServletResponse.reset();
    } catch (Exception exception2) {
      // Intentionally empty. See above.
    }
    try {
      httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
    } catch (Exception exception2) {
      // Intentionally empty. See above.
    }
    try {
      httpServletResponse.setHeader("Server", "n/a");
    } catch (Exception exception2) {
      // Intentionally empty. See above.
    } 

    // We do not know if the output stream got opened already. So we try to write an error
    // statement to both. Oneif them will for sure fail. The other could work, depending on the
    // response state.
    
    PrintWriter writer = null;
    try {
      writer = httpServletResponse.getWriter();
      writer.println(msg);
      // Postponing close to avoid issues if the writer and stream are closed by the same method,
      // and stream would still be open.
    } catch (Exception exception2) {
      // Intentionally empty. See above.
    }
    OutputStream stream = null;
    try {
      stream = httpServletResponse.getOutputStream();
      stream.write(msg.getBytes(StandardCharsets.UTF_8));
    } catch (Exception exception2) {
      // Intentionally empty. See above.
    }
    if (writer != null) {
      try {
        writer.close();
      } catch (Exception exception2) {
        // Intentionally empty. See above.
      } 
    }
    if (stream != null) {
      try {
        stream.close();
      } catch (Exception exception2) {
        // Intentionally empty. See above.
      } 
    }

    try {
      if (!request.isHandled()) {
        request.setHandled(true);
      }
    } catch (Exception exception2) {
      // Intentionally empty. See above.
    }
  }
}
