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

import static com.seleritycorp.common.base.http.common.ContentType.APPLICATION_JSON;
import static com.seleritycorp.common.base.http.common.ContentType.TEXT_HTML;
import static com.seleritycorp.common.base.http.common.ContentType.TEXT_PLAIN;

import com.google.gson.JsonObject;
import com.google.inject.assistedinject.Assisted;

import com.seleritycorp.common.base.escape.Escaper;
import com.seleritycorp.common.base.http.common.ContentType;
import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;
import com.seleritycorp.common.base.time.TimeUtils;
import com.seleritycorp.common.base.uuid.UuidGenerator;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

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
  private final ContentTypeNegotiator contentTypeNegotiator;
  private final UuidGenerator uuidGenerator;
  private final Escaper escaper;
  private final TimeUtils timeUtils;

  /**
   * Create a HttpRequest.
   * 
   * @param target Target URL for the handle request parameters
   * @param request Base Jetty request for the handle request parameters
   * @param httpServletRequest Http request for the handle request parameters
   * @param httpServletResponse Response for the handle request parameters
   * @param forwardedForResolver The resolver for IP addresses
   * @param contentTypeNegotiator The negotiator for ContentType handling.
   * @param uuidGenerator The generator for incident ids.
   * @param escaper Escaping for formatting output.
   * @param timeUtils utils for formatting times.
   */
  @Inject
  public HttpRequest(@Assisted String target, @Assisted Request request,
      @Assisted HttpServletRequest httpServletRequest,
      @Assisted HttpServletResponse httpServletResponse,
      ForwardedForResolver forwardedForResolver, ContentTypeNegotiator contentTypeNegotiator,
      UuidGenerator uuidGenerator, Escaper escaper, TimeUtils timeUtils) {
    this.target = target;
    this.request = request;
    this.httpServletRequest = httpServletRequest;
    this.httpServletResponse = httpServletResponse;
    this.forwardedForResolver = forwardedForResolver;
    this.contentTypeNegotiator = contentTypeNegotiator;
    this.uuidGenerator = uuidGenerator;
    this.escaper = escaper;
    this.timeUtils = timeUtils;
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

  /**
   * Gets the most suitable ContentType for the response.
   *
   * <p>The client's preference is read from the request's headers, and it gets compared against
   * the candidates offered by the server. The best match will get returned.
   *
   * @param fallback The fallback value to use if there is no match between the client's
   *     preferences and the server's offerings.
   * @param candidates The offerings from the server.
   * @return The best match between the client's preferences and the server's offerings. If there
   *     is no match at all, the fallback value will get returned.
   */
  public ContentType getMostSuitableResponseContentType(ContentType fallback,
      ContentType... candidates) {
    String acceptHeader = httpServletRequest.getHeader("Accept");
    ContentType negotiated = contentTypeNegotiator.negotiate(acceptHeader, fallback, candidates);
    return negotiated;
  }

  // -- Response helpers  -----------------------------------------------------
  
  /**
   * Sends a response with status code to a request and marks it as handled.
   *  
   * @param status The status code to send
   * @param contentType The ContentType for the response.
   * @param response The response text
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  private void respond(int status, ContentType contentType, String response) throws IOException {
    httpServletResponse.setStatus(status);

    if (response != null) {
      if (contentType != null) {
        httpServletResponse.setContentType(contentType.toString());
      }
      try (PrintWriter writer = httpServletResponse.getWriter()) {
        writer.print(response);
      }
    }
    setHandled();
  }

  private UUID logRequest(int status, ErrorCode errorCode, String clientExplanation) {
    final UUID incidentId = uuidGenerator.generate();
    log.structuredInfo("http-server-incident", 1,
        "incidentId", incidentId,
        "method", httpServletRequest.getMethod(),
        "target", getTarget(),
        "status", status,
        "errorCode", (errorCode != null) ? errorCode.getIdentifier() : null,
        "clientExplanation", clientExplanation,    
        "devExplanation", null,    
        "throwable", null);
    return incidentId;
  }
  
  private UUID respondGenericIssue(int status, ErrorCode errorCode, String clientExplanation)
      throws IOException {
    final UUID incidentId = logRequest(status, errorCode, clientExplanation);

    ContentType responseContentType = getMostSuitableResponseContentType(TEXT_PLAIN, TEXT_HTML,
        APPLICATION_JSON);
    String errorCodeIdentifier = (errorCode != null) ? errorCode.getIdentifier() : null;
    String msg = "";
    if (TEXT_PLAIN.equals(responseContentType)) {
      msg += "Error code: " + errorCodeIdentifier + "\n";
      msg += "Explanation: " + clientExplanation + "\n";
      msg += "Incident id: " + incidentId + "\n";
      msg += "Server timestamp: " + timeUtils.formatTimeNanos() + "\n";
    } else if (TEXT_HTML.equals(responseContentType)) {
      msg += "<html>\n";
      msg += "  <body>\n";
      msg += "    <p>An error occurred</p>\n";
      msg += "    <p>" + escaper.html(clientExplanation) + "</p>\n";
      msg += "    <table>\n";
      msg += "      <tr><th style=\"text-align:left;\">Error code</th><td><pre>"
          + escaper.html(errorCodeIdentifier) + "</pre></td></tr>\n";
      msg += "      <tr><th style=\"text-align:left;\">Explanation</th><td>" 
          + escaper.html(clientExplanation) + "</td></tr>\n";
      msg += "      <tr><th style=\"text-align:left;\">Incident id</th><td><pre>"
          + escaper.html(incidentId.toString()) + "</pre></td></tr>\n";
      msg += "      <tr><th style=\"text-align:left;\">Server time</th><td><pre>"
          + timeUtils.formatTimeNanos() + "</pre></td></tr>\n";
      msg += "    </table>\n";
      msg += "  </body>\n";
      msg += "<html>\n";
    } else if (APPLICATION_JSON.equals(responseContentType)) {
      JsonObject object = new JsonObject();
      object.addProperty("errorCode", errorCodeIdentifier);
      object.addProperty("explanation", clientExplanation);
      object.addProperty("incidentId", incidentId.toString());
      object.addProperty("serverTimestamp", timeUtils.formatTimeNanos());
      msg = object.toString();
    } else {
      // This branch should never be reached. It's only hear for extra safety. 
      msg = "Unkonwn ContentType " + responseContentType;
    }
    respond(status, responseContentType, msg);
    return incidentId;
  }

  /**
   * Sends a '200 OK' response with a plain text response.
   *  
   * @param response The text response to send
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondOkText(String response) throws IOException {
    respond(HttpStatus.OK_200, ContentType.TEXT_PLAIN, response);
  }

  /**
   * Sends a '204 No Content' response to a request and marks it as handled.
   *  
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public void respondNoContent() throws IOException {
    respond(HttpStatus.NO_CONTENT_204, null, null);
  }

  /**
   * Sends a '400 Bad Request' response to a request and marks it as handled.
   *  
   * @param errorCode The error describing why the client request is considered bad.
   * @param clientExplanation An explanation of the issue to show to the client/user.
   * @return The issue id associated with this response.
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public UUID respondBadRequest(ErrorCode errorCode, String clientExplanation) throws IOException {
    if (clientExplanation == null) {
      clientExplanation = errorCode.getDefaultReason();
    }
    final int status = HttpStatus.BAD_REQUEST_400;
    return respondGenericIssue(status, errorCode, clientExplanation);
  }

  /**
   * Sends a '400 Bad Request' response to a request and marks it as handled.
   *  
   * @param errorCode The error describing why the client request is considered bad.
   * @return The issue id associated with this response.
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public UUID respondBadRequest(ErrorCode errorCode) throws IOException {
    return respondBadRequest(errorCode, null);
  }

  /**
   * Sends a '403 Forbidden' response to a request and marks it as handled.
   *  
   * @return The issue id associated with this response.
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public UUID respondForbidden()
      throws IOException {
    ErrorCode errorCode = BasicErrorCode.E_FORBIDDEN;
    String clientExplanation = errorCode.getDefaultReason() + " URL: " + getTarget();
    final int status = HttpStatus.FORBIDDEN_403;
    return respondGenericIssue(status, errorCode, clientExplanation);
  }

  /**
   * Sends a '404 Not Found' response to a request and marks it as handled.
   *  
   * @return The issue id associated with this response.
   * @throws java.io.UnsupportedEncodingException if the character encoding is unusable.
   * @throws IllegalStateException if a response was sent already.
   * @throws IOException if an input/output error occurs
   */
  public UUID respondNotFound() throws IOException {
    ErrorCode errorCode = BasicErrorCode.E_NOT_FOUND;
    String clientExplanation = errorCode.getDefaultReason() + " URL: " + getTarget();
    final int status = HttpStatus.NOT_FOUND_404;
    return respondGenericIssue(status, errorCode, clientExplanation);
  }
  
  /**
   * Marks a request as handled.
   */
  public void setHandled() {
    request.setHandled(true);
  }
}
