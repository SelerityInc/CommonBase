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

import static org.assertj.core.api.Assertions.assertThat;

import static com.seleritycorp.common.base.http.common.ContentType.APPLICATION_JSON;
import static com.seleritycorp.common.base.http.common.ContentType.TEXT_HTML;
import static com.seleritycorp.common.base.http.common.ContentType.TEXT_PLAIN;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.reset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.Capture;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.escape.Escaper;
import com.seleritycorp.common.base.http.common.ContentType;
import com.seleritycorp.common.base.http.server.ForwardedForResolver;
import com.seleritycorp.common.base.http.server.HttpRequest;
import com.seleritycorp.common.base.test.InjectingTestCase;
import com.seleritycorp.common.base.test.SettableConfig;
import com.seleritycorp.common.base.time.TimeUtils;

public class HttpRequestTest extends InjectingTestCase {
  private Request request;
  private HttpServletRequest httpServletRequest;
  private HttpServletResponse httpServletResponse;
  private ForwardedForResolver forwardedForResolver;
  private ContentTypeNegotiator contentTypeNegotiator;
  private Escaper escaper;
  private TimeUtils timeUtils;
  private SettableConfig config;
  
  @Before
  public void setUp() {
    request = createMock(Request.class);
    httpServletRequest = createMock(HttpServletRequest.class);
    httpServletResponse = createMock(HttpServletResponse.class);
    forwardedForResolver = createMock(ForwardedForResolver.class);
    contentTypeNegotiator = createMock(ContentTypeNegotiator.class);
    escaper = createMock(Escaper.class);
    timeUtils = createMock(TimeUtils.class);
    expect(timeUtils.formatTimeNanos()).andReturn("TIMESTAMP").anyTimes();
    config = new SettableConfig();
    config.set("server.id", "serverFoo");
    config.set("server.support.email", "foo@example.org");
  }

  @Test
  public void testGetTarget() throws IOException {
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");

    verifyAll();
    
    assertThat(httpRequest.getTarget()).isEqualTo("/foo");
  }

  @Test
  public void testRespondForbiddenText() throws IOException {
    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(TEXT_PLAIN);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(403);
    httpServletResponse.setContentType(TEXT_PLAIN.toString());
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondForbidden();
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("E_FORBIDDEN");
    assertThat(responseBody).contains("/foo");
    assertThat(responseBody).contains("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("serverFoo");
    assertThat(responseBody).contains("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondForbiddenHtml() throws IOException {
    expect(escaper.html("E_FORBIDDEN")).andReturn("(escQUUX)").anyTimes();
    expect(escaper.html("00000000-0000-0000-0000-000000000001")).andReturn("(escUUID)").anyTimes();
    expect(escaper.html("You are not allowed to access this URL. URL: /foo")).andReturn("(escRsn)")
      .anyTimes();
    expect(escaper.html("serverFoo")).andReturn("(escServerId)").anyTimes();
    expect(escaper.html("foo@example.org")).andReturn("(escSupport)").anyTimes();
    expect(escaper.html("/foo")).andReturn("(escTarget)").anyTimes();

    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(TEXT_HTML);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(403);
    httpServletResponse.setContentType(TEXT_HTML.toString());
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondForbidden();
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("(escTarget)");
    assertThat(responseBody).doesNotContain("/foo");
    assertThat(responseBody).contains("(escQUUX)");
    assertThat(responseBody).doesNotContain("E_FORBIDDEN");
    assertThat(responseBody).contains("(escUUID)");
    assertThat(responseBody).doesNotContain("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("(escRsn)");
    assertThat(responseBody).doesNotContain("The URL could not be found. URL: /foo");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("(escServerId)");
    assertThat(responseBody).doesNotContain("serverFoo");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondForbiddenJson() throws IOException {
    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(APPLICATION_JSON);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(403);
    httpServletResponse.setContentType(APPLICATION_JSON.toString());
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondForbidden();
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("E_FORBIDDEN");
    assertThat(responseBody).contains("/foo");
    assertThat(responseBody).contains("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("serverFoo");
    assertThat(responseBody).contains("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondNotFoundText() throws IOException {
    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(TEXT_PLAIN);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(404);
    httpServletResponse.setContentType(TEXT_PLAIN.toString());
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondNotFound();
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("E_NOT_FOUND");
    assertThat(responseBody).contains("/foo");
    assertThat(responseBody).contains("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("serverFoo");
    assertThat(responseBody).contains("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondNotFoundHtml() throws IOException {
    expect(escaper.html("E_NOT_FOUND")).andReturn("(escQUUX)").anyTimes();
    expect(escaper.html("00000000-0000-0000-0000-000000000001")).andReturn("(escUUID)").anyTimes();
    expect(escaper.html("The URL could not be found. URL: /foo")).andReturn("(escRsn)").anyTimes();
    expect(escaper.html("serverFoo")).andReturn("(escServerId)").anyTimes();
    expect(escaper.html("foo@example.org")).andReturn("(escSupport)").anyTimes();
    expect(escaper.html("/foo")).andReturn("(escTarget)").anyTimes();

    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(TEXT_HTML);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(404);
    httpServletResponse.setContentType(TEXT_HTML.toString());
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondNotFound();
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("(escTarget)");
    assertThat(responseBody).doesNotContain("/foo");
    assertThat(responseBody).contains("(escQUUX)");
    assertThat(responseBody).doesNotContain("E_NOT_FOUND");
    assertThat(responseBody).contains("(escUUID)");
    assertThat(responseBody).doesNotContain("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("(escRsn)");
    assertThat(responseBody).doesNotContain("The URL could not be found. URL: /foo");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("(escServerId)");
    assertThat(responseBody).doesNotContain("serverFoo");
    assertThat(responseBody).contains("(escSupport)");
    assertThat(responseBody).doesNotContain("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondNotFoundJson() throws IOException {
    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(APPLICATION_JSON);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(404);
    httpServletResponse.setContentType(APPLICATION_JSON.toString());
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondNotFound();
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("E_NOT_FOUND");
    assertThat(responseBody).contains("/foo");
    assertThat(responseBody).contains("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("serverFoo");
    assertThat(responseBody).contains("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondBadRequest2ParamsText() throws IOException {
    ErrorCode errorCode = createMock(ErrorCode.class);
    expect(errorCode.getIdentifier()).andReturn("quux").anyTimes();

    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(TEXT_PLAIN);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setContentType(TEXT_PLAIN.toString());
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondBadRequest(errorCode, "bar");
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("quux");
    assertThat(responseBody).contains("bar");
    assertThat(responseBody).contains("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("serverFoo");
    assertThat(responseBody).contains("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondBadRequest2ParamsHtml() throws IOException {
    expect(escaper.html("bar")).andReturn("(escBAR)").anyTimes();
    expect(escaper.html("quux")).andReturn("(escQUUX)").anyTimes();
    expect(escaper.html("00000000-0000-0000-0000-000000000001")).andReturn("(escUUID)").anyTimes();
    expect(escaper.html("serverFoo")).andReturn("(escServerId)").anyTimes();
    expect(escaper.html("foo@example.org")).andReturn("(escSupport)").anyTimes();
    expect(escaper.html("/foo")).andReturn("(escTarget)").anyTimes();

    ErrorCode errorCode = createMock(ErrorCode.class);
    expect(errorCode.getIdentifier()).andReturn("quux").anyTimes();

    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(TEXT_HTML);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setContentType(TEXT_HTML.toString());
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondBadRequest(errorCode, "bar");
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("(escTarget)");
    assertThat(responseBody).doesNotContain("/foo");
    assertThat(responseBody).contains("(escQUUX)");
    assertThat(responseBody).doesNotContain("quux");
    assertThat(responseBody).contains("(escBAR)");
    assertThat(responseBody).doesNotContain("bar");
    assertThat(responseBody).contains("(escUUID)");
    assertThat(responseBody).doesNotContain("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("(escServerId)");
    assertThat(responseBody).doesNotContain("serverFoo");
    assertThat(responseBody).contains("(escSupport)");
    assertThat(responseBody).doesNotContain("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondBadRequest2ParamsJson() throws IOException {
    ErrorCode errorCode = createMock(ErrorCode.class);
    expect(errorCode.getIdentifier()).andReturn("quux").anyTimes();

    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(APPLICATION_JSON);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setContentType(APPLICATION_JSON.toString());
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondBadRequest(errorCode, "bar");
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("quux");
    assertThat(responseBody).contains("bar");
    assertThat(responseBody).contains("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("serverFoo");
    assertThat(responseBody).contains("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondBadRequest2ParamsTextSecondNull() throws IOException {
    ErrorCode errorCode = createMock(ErrorCode.class);
    expect(errorCode.getIdentifier()).andReturn("quux").anyTimes();
    expect(errorCode.getDefaultReason()).andReturn("bar").anyTimes();

    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(TEXT_PLAIN);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setContentType(TEXT_PLAIN.toString());
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondBadRequest(errorCode, null);
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("quux");
    assertThat(responseBody).contains("bar");
    assertThat(responseBody).contains("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("serverFoo");
    assertThat(responseBody).contains("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondBadRequest2ParamsHtmlSecondNull() throws IOException {
    expect(escaper.html("bar")).andReturn("(escBAR)").anyTimes();
    expect(escaper.html("quux")).andReturn("(escQUUX)").anyTimes();
    expect(escaper.html("00000000-0000-0000-0000-000000000001")).andReturn("(escUUID)").anyTimes();
    expect(escaper.html("serverFoo")).andReturn("(escServerId)").anyTimes();
    expect(escaper.html("foo@example.org")).andReturn("(escSupport)").anyTimes();
    expect(escaper.html("/foo")).andReturn("(escTarget)").anyTimes();

    ErrorCode errorCode = createMock(ErrorCode.class);
    expect(errorCode.getIdentifier()).andReturn("quux").anyTimes();
    expect(errorCode.getDefaultReason()).andReturn("bar").anyTimes();

    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(TEXT_HTML);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setContentType(TEXT_HTML.toString());
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondBadRequest(errorCode, null);
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("(escTarget)");
    assertThat(responseBody).doesNotContain("/foo");
    assertThat(responseBody).contains("(escQUUX)");
    assertThat(responseBody).doesNotContain("quux");
    assertThat(responseBody).contains("(escBAR)");
    assertThat(responseBody).doesNotContain("bar");
    assertThat(responseBody).contains("(escUUID)");
    assertThat(responseBody).doesNotContain("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("(escServerId)");
    assertThat(responseBody).doesNotContain("serverFoo");
    assertThat(responseBody).contains("(escSupport)");
    assertThat(responseBody).doesNotContain("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondBadRequest2ParamsJsonSecondNull() throws IOException {
    ErrorCode errorCode = createMock(ErrorCode.class);
    expect(errorCode.getIdentifier()).andReturn("quux").anyTimes();
    expect(errorCode.getDefaultReason()).andReturn("bar").anyTimes();

    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(APPLICATION_JSON);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setContentType(APPLICATION_JSON.toString());
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondBadRequest(errorCode, null);
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("quux");
    assertThat(responseBody).contains("bar");
    assertThat(responseBody).contains("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("serverFoo");
    assertThat(responseBody).contains("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondBadRequest1ParamText() throws IOException {
    ErrorCode errorCode = createMock(ErrorCode.class);
    expect(errorCode.getIdentifier()).andReturn("quux").anyTimes();
    expect(errorCode.getDefaultReason()).andReturn("bar").anyTimes();

    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(TEXT_PLAIN);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setContentType(TEXT_PLAIN.toString());
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondBadRequest(errorCode, null);
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("quux");
    assertThat(responseBody).contains("bar");
    assertThat(responseBody).contains("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("serverFoo");
    assertThat(responseBody).contains("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondBadRequest1ParamHtml() throws IOException {
    expect(escaper.html("bar")).andReturn("(escBAR)").anyTimes();
    expect(escaper.html("quux")).andReturn("(escQUUX)").anyTimes();
    expect(escaper.html("00000000-0000-0000-0000-000000000001")).andReturn("(escUUID)").anyTimes();
    expect(escaper.html("serverFoo")).andReturn("(escServerId)").anyTimes();
    expect(escaper.html("foo@example.org")).andReturn("(escSupport)").anyTimes();
    expect(escaper.html("/foo")).andReturn("(escTarget)").anyTimes();

    ErrorCode errorCode = createMock(ErrorCode.class);
    expect(errorCode.getIdentifier()).andReturn("quux").anyTimes();
    expect(errorCode.getDefaultReason()).andReturn("bar").anyTimes();

    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(TEXT_HTML);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setContentType(TEXT_HTML.toString());
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondBadRequest(errorCode, null);
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("(escTarget)");
    assertThat(responseBody).doesNotContain("/foo");
    assertThat(responseBody).contains("(escQUUX)");
    assertThat(responseBody).doesNotContain("quux");
    assertThat(responseBody).contains("(escBAR)");
    assertThat(responseBody).doesNotContain("bar");
    assertThat(responseBody).contains("(escUUID)");
    assertThat(responseBody).doesNotContain("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("(escServerId)");
    assertThat(responseBody).doesNotContain("serverFoo");
    assertThat(responseBody).contains("(escSupport)");
    assertThat(responseBody).doesNotContain("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondBadRequest1ParamJson() throws IOException {
    ErrorCode errorCode = createMock(ErrorCode.class);
    expect(errorCode.getIdentifier()).andReturn("quux").anyTimes();
    expect(errorCode.getDefaultReason()).andReturn("bar").anyTimes();

    expect(httpServletRequest.getMethod()).andReturn("METHOD_FOO");
    expect(httpServletRequest.getHeader("Accept")).andReturn("text/foo");

    expect(contentTypeNegotiator.negotiate("text/foo", TEXT_PLAIN, TEXT_HTML, APPLICATION_JSON))
      .andReturn(APPLICATION_JSON);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setContentType(APPLICATION_JSON.toString());
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    UUID incidentId = httpRequest.respondBadRequest(errorCode, null);
    
    verifyAll();

    String responseBody = stringWriter.toString(); 
    assertThat(responseBody).contains("quux");
    assertThat(responseBody).contains("bar");
    assertThat(responseBody).contains("00000000-0000-0000-0000-000000000001");
    assertThat(responseBody).contains("TIMESTAMP");
    assertThat(responseBody).contains("serverFoo");
    assertThat(responseBody).contains("foo@example.org");

    assertThat(incidentId.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
  }

  @Test
  public void testRespondNoContent() throws IOException {
    httpServletResponse.setStatus(204);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respondNoContent();
    
    verifyAll();
  }

  @Test
  public void testGetRequestBodyAsString() throws IOException {
    BufferedReader reader = new BufferedReader(new StringReader("bar"));
    expect(httpServletRequest.getReader()).andReturn(reader);

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    String body = httpRequest.getRequestBodyAsString();

    verifyAll();

    assertThat(body).isEqualTo("bar");
  }

  @Test
  public void testGetResolvedRemoteAddr() throws IOException {
    expect(httpServletRequest.getRemoteAddr()).andReturn("foo");
    expect(httpServletRequest.getHeader("X-Forwarded-For")).andReturn("bar");
    expect(forwardedForResolver.resolve("foo", "bar")).andReturn("baz");

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    String actual = httpRequest.getResolvedRemoteAddr();

    verifyAll();

    assertThat(actual).isEqualTo("baz");
  }

  @Test
  public void testIsMethodGetTrue() throws IOException {
    expect(httpServletRequest.getMethod()).andReturn("GET");

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    boolean actual = httpRequest.isMethodGet();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsMethodGetFalseLowercase() throws IOException {
    expect(httpServletRequest.getMethod()).andReturn("get");

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    boolean actual = httpRequest.isMethodGet();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testIsMethodGetFalseDifferent() throws IOException {
    expect(httpServletRequest.getMethod()).andReturn("POST");

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    boolean actual = httpRequest.isMethodGet();

    verifyAll();

    assertThat(actual).isFalse();
  }


  @Test
  public void testIsMethodPostTrue() throws IOException {
    expect(httpServletRequest.getMethod()).andReturn("POST");

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    boolean actual = httpRequest.isMethodPost();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsMethodPostFalseLowercase() throws IOException {
    expect(httpServletRequest.getMethod()).andReturn("post");

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    boolean actual = httpRequest.isMethodPost();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testIsMethodPostFalseDifferent() throws IOException {
    expect(httpServletRequest.getMethod()).andReturn("GET");

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    boolean actual = httpRequest.isMethodPost();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testHasBeenHandledTrue() throws IOException {
    expect(request.isHandled()).andReturn(true);

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    boolean actual = httpRequest.hasBeenHandled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testHasBeenHandledFalse() throws IOException {
    expect(request.isHandled()).andReturn(false);

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    boolean actual = httpRequest.hasBeenHandled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testGetMostSuitableResponseContentType() throws IOException {
    reset(request);
    expect(httpServletRequest.getHeader("Accept")).andReturn("bar");

    Capture<ContentType> fallbackCapture = newCapture();
    Capture<ContentType> candidatesCapture = newCapture();
    
    expect(contentTypeNegotiator.negotiate(eq("bar"), capture(fallbackCapture),
        capture(candidatesCapture))).andReturn(ContentType.IMAGE_PNG);

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    ContentType actual = httpRequest.getMostSuitableResponseContentType(
        ContentType.APPLICATION_JSON, ContentType.TEXT_HTML);

    verifyAll();

    assertThat(actual).isEqualTo(ContentType.IMAGE_PNG);
    assertThat(fallbackCapture.getValue()).isEqualTo(ContentType.APPLICATION_JSON);
    assertThat(candidatesCapture.getValue()).isEqualTo(ContentType.TEXT_HTML);
  }

  @Test
  public void testSetHandled() throws IOException {
    request.setHandled(true);

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.setHandled();

    verifyAll();
  }

  private HttpRequest createHttpRequest(String target) {
    return new HttpRequest(target, request, httpServletRequest, httpServletResponse,
        forwardedForResolver, contentTypeNegotiator, getUuidGenerator(), escaper, timeUtils, config);
  }
}
