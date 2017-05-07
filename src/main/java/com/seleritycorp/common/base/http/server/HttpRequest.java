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

import com.google.inject.assistedinject.Assisted;

import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.eclipse.jetty.server.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Http Request enhanced by tooling for common work flows.
 */
public class HttpRequest {
  private static final Log log = LogFactory.getLog(HttpRequest.class);

  public interface Factory {
    /**
     * Create a HttpRequest.
     * 
     * @param target Target URL for the handle request parameters
     * @param request Base Jetty request for the handle request parameters
     * @param httpServletRequest Http request for the handle request parameters
     * @param httpServletResponse Response for the handle request parameters
     * @return The created HttpRequest
     */
    HttpRequest create(String target, Request request, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse);
  }

  private final String target;
  private final Request request;
  private final HttpServletRequest httpServletRequest;
  private final HttpServletResponse httpServletResponse;
  private final ForwardedForResolver forwardedForResolver;

  /**
   * Create a HttpRequest.
   * 
   * @param target Target URL for the handle request parameters
   * @param request Base Jetty request for the handle request parameters
   * @param httpServletRequest Http request for the handle request parameters
   * @param httpServletResponse Response for the handle request parameters
   * @param forwardedForResolver The resolver for IP addresses
   */
  @Inject
  public HttpRequest(@Assisted String target, @Assisted Request request,
      @Assisted HttpServletRequest httpServletRequest,
      @Assisted HttpServletResponse httpServletResponse,
      ForwardedForResolver forwardedForResolver) {
    this.target = target;
    this.request = request;
    this.httpServletRequest = httpServletRequest;
    this.httpServletResponse = httpServletResponse;
    this.forwardedForResolver = forwardedForResolver;
  }

  // -- Raw request objects ---------------------------------------------------

  /**
   * Gets the target URL of the handle request.
   * 
   * @return the target.
   */
  public String getTarget() {
    return target;
  }

  // -- Information extraction helpers ----------------------------------------

  /**
   * Gets the request's body as string.
   *
   * <p>This method may replace the request's line-breaks with line-breaks used on the current
   * system (E.g.: Replacing "\r\n" by "\n").
   *
   * @return The string representation of the request's body
   * @throws java.io.UnsupportedEncodingException if the character encoding is not supported.
   * @throws IllegalStateException if the request was read already.
   * @throws IOException if an input or output exception occurred
   */
  public String getRequestBodyAsString() throws IOException {
    try (BufferedReader reader = httpServletRequest.getReader()) {
      return IOUtils.toString(reader);
    }
  }
  
  /**
   * Checks if a request is a GET request.
   * 
   * @return true, if it is a GET request. false otherwise.
   */
  public boolean isMethodGet() {
    return HttpGet.METHOD_NAME.equals(httpServletRequest.getMethod());
  }

  /**
   * Checks if a request is a POST request.
   * 
   * @return true, if it is a POST request. false otherwise.
   */
  public boolean isMethodPost() {
    return HttpPost.METHOD_NAME.equals(httpServletRequest.getMethod());
  }

  /**
   * Checks if a request has been marked handlend.
   * 
   * @return true, if the request has been marked handled. false otherwise.
   */
  public boolean hasBeenHandled() {
    return request.isHandled();
  }

  /**
   * Resolves a remote address using X-Forwarded-For headers
   * 
   * <p>If requests pass through proxies, they are expected to set for which IP they proxied.
   * Not all proxies do that, and one cannot trust external proxies. But our internal proxies
   * do and we can trust them to not set bogus headers. So we use this information to determine
   * from which IP a request originates.
   *  
   * @return the IP address we attribute a request to.
   */
  public String getResolvedRemoteAddr() {
    String remoteAddr = httpServletRequest.getRemoteAddr();
    String forwardedFor = httpServletRequest.getHeader("X-Forwarded-For");
    return forwardedForResolver.resolve(remoteAddr,forwardedFor);
  }

  // -- Response helpers  -----------------------------------------------------
  
  /**
   * Sends a response to a request and marks it as handled.
   * 
   * @param response The response text. If null, no response gets written.
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respond(String response) throws IOException {
    if (response != null) {
      try (PrintWriter writer = httpServletResponse.getWriter()) {
        writer.print(response);
      }
    }
    setHandled();
  }

  /**
   * Sends a response with status code to a request and marks it as handled.
   *  
   * @param status The status code to send
   * @param response The response text
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respond(int status, String response) throws IOException {
    httpServletResponse.setStatus(status);
    respond(response);
  }

  /**
   * Sends a response with status code to a request and marks it as handled.
   *  
   * @param status The status code to send
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respond(int status)
      throws IOException {
    respond(status, null);
  }

  /**
   * Sends a response with status code to a request and marks it as handled.
   *  
   * @param status The status code to send
   * @param response The response text
   * @param logMessage A message to log for the request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respond(int status, String response, String logMessage) throws IOException {
    respond(status, response, logMessage, null);
  }

  /**
   * Sends a response with status code to a request and marks it as handled.
   *  
   * @param status The status code to send
   * @param response The response text
   * @param logMessage A message to log for the request
   * @param logThrowable A throwable to log for the request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respond(int status, String response, String logMessage, Throwable logThrowable)
      throws IOException {
    httpServletResponse.setStatus(status);
    respond(response);

    String msg = "Status " + status + " for request to " + getTarget() + ".";
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
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondNoContent() throws IOException {
    respond(204);
  }

  /**
   * Sends a '400 Bad Request' response to a request and marks it as handled.
   *  
   * @param response The response text
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondBadRequest(String response)
      throws IOException {
    respond(400, response);
  }

  /**
   * Sends a '400 Bad Request' response to a request and marks it as handled.
   *  
   * @param response The response text
   * @param logMessage A message to log for the request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondBadRequest(String response, String logMessage) throws IOException {
    respond(400, response, logMessage);
  }

  /**
   * Sends a '400 Bad Request' response to a request and marks it as handled.
   *  
   * @param response The response text
   * @param logMessage A message to log for the request
   * @param logThrowable A throwable to log for the request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondBadRequest(String response, String logMessage, Throwable logThrowable)
      throws IOException {
    respond(400, response, logMessage, logThrowable);
  }

  /**
   * Sends a '400 Bad Request' response to a request and marks it as handled.
   *  
   * @param response The response text
   * @param logThrowable A throwable to log for the request
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondBadRequest(String response, Throwable logThrowable) throws IOException {
    respond(400, response, null, logThrowable);
  }

  /**
   * Sends a '403 Forbidden' response to a request and marks it as handled.
   *  
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondForbidden()
      throws IOException {
    respond(403);
  }

  /**
   * Sends a '404 Not Found' response to a request and marks it as handled.
   *  
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondNotFound() throws IOException {
    respond(404);
  }
  
  /**
   * Marks a request as handled.
   */
  public void setHandled() {
    request.setHandled(true);
  }
}
