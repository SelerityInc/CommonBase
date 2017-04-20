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
import com.seleritycorp.common.base.http.server.HandleParameters;
import com.seleritycorp.common.base.http.server.HttpHandlerUtils;

public class HttpHandlerUtilsTest extends EasyMockSupport {
  private Request baseRequest;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private ForwardedForResolver forwardedForResolver;
  
  @Before
  public void setUp() {
    baseRequest = createMock(Request.class);
    request = createMock(HttpServletRequest.class);
    response = createMock(HttpServletResponse.class);
    forwardedForResolver = createMock(ForwardedForResolver.class);
  }
  
  @Test
  public void testRespondResponse() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(response.getWriter()).andReturn(printWriter);
    baseRequest.setHandled(true);

    replayAll();

    utils.respond("bar", parameters);
    
    verifyAll();
    
    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondResponseNull() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    baseRequest.setHandled(true);

    replayAll();

    utils.respond(null, parameters);
    
    verifyAll();
  }

  @Test
  public void testRespondStatusResponse() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(123);
    baseRequest.setHandled(true);

    replayAll();

    utils.respond(123, "bar", parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondStatus() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    response.setStatus(123);
    baseRequest.setHandled(true);

    replayAll();

    utils.respond(123, parameters);
    
    verifyAll();
  }

  @Test
  public void testRespondStatusResponseLogMessage() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(123);
    baseRequest.setHandled(true);

    replayAll();

    utils.respond(123, "bar", "baz", parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondStatusResponseLogMessageThrowable() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(123);
    baseRequest.setHandled(true);
    Throwable e = new Exception("quux");
    
    replayAll();

    utils.respond(123, "bar", "baz", e, parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondForbidden() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    response.setStatus(403);
    baseRequest.setHandled(true);
    
    replayAll();

    utils.respondForbidden(parameters);
    
    verifyAll();
  }

  @Test
  public void testRespondNotFound() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    response.setStatus(404);
    baseRequest.setHandled(true);
    
    replayAll();

    utils.respondNotFound(parameters);
    
    verifyAll();
  }

  @Test
  public void testRespondBadRequest() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(400);
    baseRequest.setHandled(true);
    
    replayAll();

    utils.respondBadRequest("bar", parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondBadRequestAndMessage() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(400);
    baseRequest.setHandled(true);
    
    replayAll();

    utils.respondBadRequest("bar", "baz", parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondBadRequestAndMessageAndThrowable() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(400);
    baseRequest.setHandled(true);
    Throwable e = new Exception("quux");
    
    replayAll();

    utils.respondBadRequest("bar", "baz", e, parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondBadRequestAndThrowable() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(400);
    baseRequest.setHandled(true);
    Throwable e = new Exception("quux");
    
    replayAll();

    utils.respondBadRequest("bar", e, parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondNoContent() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    response.setStatus(204);
    baseRequest.setHandled(true);
    
    replayAll();

    utils.respondNoContent(parameters);
    
    verifyAll();
  }

  @Test
  public void testGetRequestBodyAsString() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    BufferedReader reader = new BufferedReader(new StringReader("bar"));
    expect(request.getReader()).andReturn(reader);

    replayAll();

    String body = utils.getRequestBodyAsString(parameters);

    verifyAll();

    assertThat(body).isEqualTo("bar");
  }

  @Test
  public void testResolveForwardedFor() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(request.getRemoteAddr()).andReturn("foo");
    expect(request.getHeader("X-Forwarded-For")).andReturn("bar");
    expect(forwardedForResolver.resolve("foo", "bar")).andReturn("baz");

    replayAll();

    String actual = utils.resolveRemoteAddr(parameters);

    verifyAll();

    assertThat(actual).isEqualTo("baz");
  }

  @Test
  public void testIsMethodGetTrue() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(request.getMethod()).andReturn("GET");

    replayAll();

    boolean actual = utils.isMethodGet(parameters);

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsMethodGetFalseLowercase() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(request.getMethod()).andReturn("get");

    replayAll();

    boolean actual = utils.isMethodGet(parameters);

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testIsMethodGetFalseDifferent() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HttpHandlerUtils utils = createHttpHandlerUtils();

    expect(request.getMethod()).andReturn("POST");

    replayAll();

    boolean actual = utils.isMethodGet(parameters);

    verifyAll();

    assertThat(actual).isFalse();
  }

  private HandleParameters createHandleParameters(String target) {
    return new HandleParameters(target, baseRequest, request, response);
  }

  private HttpHandlerUtils createHttpHandlerUtils () {
    return new HttpHandlerUtils(forwardedForResolver);
  }
}
