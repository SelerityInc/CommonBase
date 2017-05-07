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
import static org.easymock.EasyMock.expect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMockSupport;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.http.server.ForwardedForResolver;
import com.seleritycorp.common.base.http.server.HttpRequest;

public class HttpRequestTest extends EasyMockSupport {
  private Request request;
  private HttpServletRequest httpServletRequest;
  private HttpServletResponse httpServletResponse;
  private ForwardedForResolver forwardedForResolver;
  
  @Before
  public void setUp() {
    request = createMock(Request.class);
    httpServletRequest = createMock(HttpServletRequest.class);
    httpServletResponse = createMock(HttpServletResponse.class);
    forwardedForResolver = createMock(ForwardedForResolver.class);
  }

  @Test
  public void testGetTarget() throws IOException {
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");

    verifyAll();
    
    assertThat(httpRequest.getTarget()).isEqualTo("/foo");
  }

  @Test
  public void testRespondResponse() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    request.setHandled(true);

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respond("bar");
    
    verifyAll();
    
    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondResponseNull() throws IOException {
    request.setHandled(true);

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respond(null);
    
    verifyAll();
  }

  @Test
  public void testRespondStatusResponse() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(123);
    request.setHandled(true);

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respond(123, "bar");
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondStatus() throws IOException {
    httpServletResponse.setStatus(123);
    request.setHandled(true);

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respond(123);
    
    verifyAll();
  }

  @Test
  public void testRespondStatusResponseLogMessage() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(123);
    request.setHandled(true);

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respond(123, "bar", "baz");
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondStatusResponseLogMessageThrowable() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(123);
    request.setHandled(true);
    Throwable e = new Exception("quux");
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respond(123, "bar", "baz", e);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondForbidden() throws IOException {
    httpServletResponse.setStatus(403);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respondForbidden();
    
    verifyAll();
  }

  @Test
  public void testRespondNotFound() throws IOException {
    httpServletResponse.setStatus(404);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respondNotFound();
    
    verifyAll();
  }

  @Test
  public void testRespondBadRequest() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respondBadRequest("bar");
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondBadRequestAndLogMessage() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respondBadRequest("bar", "baz");
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondBadRequestAndLogMessageAndThrowable() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    Throwable e = new Exception("quux");
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respondBadRequest("bar", "baz", e);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondBadRequestAndThrowable() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    httpServletResponse.setStatus(400);
    request.setHandled(true);
    Throwable e = new Exception("quux");
    
    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.respondBadRequest("bar", e);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
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
  public void testSetHandled() throws IOException {
    request.setHandled(true);

    replayAll();

    HttpRequest httpRequest = createHttpRequest("/foo");
    httpRequest.setHandled();

    verifyAll();
  }

  private HttpRequest createHttpRequest(String target) {
    return new HttpRequest(target, request, httpServletRequest, httpServletResponse,
        forwardedForResolver);
  }
}
