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

import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Utilities for handling http requests.
 */
@Singleton
public class HttpHandlerUtils {
  private static final Log log = LogFactory.getLog(HttpHandlerUtils.class);

  private final ForwardedForResolver forwardedForResolver;

  @Inject
  public HttpHandlerUtils(ForwardedForResolver forwardedForResolver) {
    this.forwardedForResolver = forwardedForResolver;
  }

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
   * @param response The response text. If null, no response gets written.
   * @param handleParameters The parameters of the handle request 
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respond(String response, HandleParameters handleParameters) throws IOException {
    if (response != null) {
      try (PrintWriter writer = handleParameters.getResponse().getWriter()) {
        writer.print(response);
      }
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
   * @param handleParameters The parameters of the handle request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respond(int status, HandleParameters handleParameters)
      throws IOException {
    respond(status, null, handleParameters);
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
   * Sends a '403 Forbidden' response to a request and marks it as handled.
   *  
   * @param handleParameters The parameters of the handle request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondForbidden(HandleParameters handleParameters)
      throws IOException {
    respond(403, handleParameters);
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
    setHandled(handleParameters);
  }

  /**
   * Resolves a remote address using X-Forwarded-For headers
   * 
   * <p>If requests pass through proxies, they are expected to set for which IP they proxied.
   * Not all proxies do that, and one cannot trust external proxies. But our internal proxies
   * do and we can trust them to not set bogus headers. So we use this information to determine
   * from which IP a request originates.
   *  
   * @param handleParameters The parameters of the handle request
   * @return the IP address we attribute a request to.
   */
  public String resolveRemoteAddr(HandleParameters handleParameters) {
    String remoteAddr = handleParameters.getRequest().getRemoteAddr();
    String forwardedFor = handleParameters.getRequest().getHeader("X-Forwarded-For");
    return forwardedForResolver.resolve(remoteAddr,forwardedFor);
  }

  /**
   * Checks if a request is a GET request.
   * 
   * @param handleParameters The parameters of the handle request
   * @return true, if it is a GET request. false otherwise.
   */
  public boolean isMethodGet(HandleParameters handleParameters) {
    return HttpGet.METHOD_NAME.equals(handleParameters.getRequest().getMethod());
  }

  /**
   * Checks if a request is a POST request.
   * 
   * @param handleParameters The parameters of the handle request
   * @return true, if it is a POST request. false otherwise.
   */
  public boolean isMethodPost(HandleParameters handleParameters) {
    return HttpPost.METHOD_NAME.equals(handleParameters.getRequest().getMethod());
  }

  /**
   * Checks if a request has been marked handlend.
   * 
   * @param handleParameters The parameters of the handle request
   * @return true, if the request has been marked handled. false otherwise.
   */
  public boolean isHandled(HandleParameters handleParameters) {
    return handleParameters.getBaseRequest().isHandled();
  }

  /**
   * Marks a request as handled.
   * 
   * @param handleParameters The parameters of the handle request
   */
  public void setHandled(HandleParameters handleParameters) {
    handleParameters.getBaseRequest().setHandled(true);
  }
}
