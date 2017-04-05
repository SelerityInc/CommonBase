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

package com.seleritycorp.common.base.http;

import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Utilities for handling http requests.
 */
public class HttpHandlerUtils {
  private static final Log log = LogFactory.getLog(HttpHandlerUtils.class);

  /**
   * Gets the request's body as string.
   *
   * <p>This method may replace the request's line-breaks with line-breaks used on the current
   * system (E.g.: Replacing "\r\n" by "\n").
   *
   * @param handleParameters This parameters' request will get its body extracted.
   * @return The string representation of the request's body
   * @throws java.io.UnsupportedEncodingException if the character encoding is not supported.
   * @throws IllegalStateException if the request was read already.
   * @throws IOException if an input or output exception occurred
   */
  public String getRequestBodyAsString(HandleParameters handleParameters) throws IOException {
    try (BufferedReader reader = handleParameters.getRequest().getReader()) {
      return IOUtils.toString(reader);
    }
  }

  /**
   * Sends a response to a request and marks it as handled.
   * 
   * @param response The response text
   * @param handleParameters The parameters of the handle request 
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respond(String response, HandleParameters handleParameters) throws IOException {
    try (PrintWriter writer = handleParameters.getResponse().getWriter()) {
      writer.print(response);
    }
    handleParameters.getBaseRequest().setHandled(true);
  }

  /**
   * Sends a response with status code to a request and marks it as handled.
   *  
   * @param status The status code to send
   * @param response The response text
   * @param handleParameters The parameters of the handle request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respond(int status, String response, HandleParameters handleParameters)
      throws IOException {
    handleParameters.getResponse().setStatus(status);
    respond(response, handleParameters);
  }

  /**
   * Sends a response with status code to a request and marks it as handled.
   *  
   * @param status The status code to send
   * @param response The response text
   * @param logMessage A message to log for the request
   * @param handleParameters The parameters of the handle request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respond(int status, String response, String logMessage,
      HandleParameters handleParameters) throws IOException {
    respond(status, response, logMessage, null, handleParameters);
  }

  /**
   * Sends a response with status code to a request and marks it as handled.
   *  
   * @param status The status code to send
   * @param response The response text
   * @param logMessage A message to log for the request
   * @param logThrowable A throwable to log for the request
   * @param handleParameters The parameters of the handle request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respond(int status, String response, String logMessage, Throwable logThrowable,
      HandleParameters handleParameters) throws IOException {
    handleParameters.getResponse().setStatus(status);
    respond(response, handleParameters);

    String msg = "Status " + status + " for request to " + handleParameters.getTarget() + ".";
    if (logMessage != null) {
      msg += " " + logMessage;
    }
    if (logThrowable == null) {
      log.info(msg);
    } else {
      log.info(msg, logThrowable);
    }
  }

  /**
   * Sends a '204 Bad Request' response to a request and marks it as handled.
   *  
   * @param handleParameters The parameters of the handle request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondNoContent(HandleParameters handleParameters) throws IOException {
    handleParameters.getResponse().setStatus(204);
    handleParameters.getBaseRequest().setHandled(true);
  }

  /**
   * Sends a '400 Bad Request' response to a request and marks it as handled.
   *  
   * @param response The response text
   * @param handleParameters The parameters of the handle request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondBadRequest(String response, HandleParameters handleParameters)
      throws IOException {
    respond(400, response, handleParameters);
  }

  /**
   * Sends a '400 Bad Request' response to a request and marks it as handled.
   *  
   * @param response The response text
   * @param logMessage A message to log for the request
   * @param handleParameters The parameters of the handle request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondBadRequest(String response, String logMessage,
      HandleParameters handleParameters) throws IOException {
    respond(400, response, logMessage, handleParameters);
  }

  /**
   * Sends a '400 Bad Request' response to a request and marks it as handled.
   *  
   * @param response The response text
   * @param logMessage A message to log for the request
   * @param logThrowable A throwable to log for the request
   * @param handleParameters The parameters of the handle request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondBadRequest(String response, String logMessage, Throwable logThrowable,
      HandleParameters handleParameters) throws IOException {
    respond(400, response, logMessage, logThrowable, handleParameters);
  }

  /**
   * Sends a '400 Bad Request' response to a request and marks it as handled.
   *  
   * @param response The response text
   * @param logThrowable A throwable to log for the request
   * @param handleParameters The parameters of the handle request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondBadRequest(String response, Throwable logThrowable,
      HandleParameters handleParameters) throws IOException {
    respond(400, response, null, logThrowable, handleParameters);
  }

  /**
   * Sends a '404 Not Found' response to a request and marks it as handled.
   *  
   * @param handleParameters The parameters of the handle request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondNotFound(HandleParameters handleParameters) throws IOException {
    handleParameters.getResponse().setStatus(404);
    handleParameters.getBaseRequest().setHandled(true);
  }
}
