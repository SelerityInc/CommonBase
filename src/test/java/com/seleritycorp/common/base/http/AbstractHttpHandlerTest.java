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

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMockSupport;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.Test;

public class AbstractHttpHandlerTest extends EasyMockSupport {
  private Request baseRequest;
  private HttpServletRequest request;
  private HttpServletResponse response;
  
  @Before
  public void setUp() {
    baseRequest = createMock(Request.class);
    request = createMock(HttpServletRequest.class);
    response = createMock(HttpServletResponse.class);
  }
  
  @Test
  public void testRespond() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HandlerShim handler = new HandlerShim();

    expect(response.getWriter()).andReturn(printWriter);
    baseRequest.setHandled(true);

    replayAll();

    handler.respond("bar", parameters);
    
    verifyAll();
    
    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondWithStatus() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HandlerShim handler = new HandlerShim();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(123);
    baseRequest.setHandled(true);

    replayAll();

    handler.respond(123, "bar", parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondWithStatusAndMessage() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HandlerShim handler = new HandlerShim();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(123);
    baseRequest.setHandled(true);

    replayAll();

    handler.respond(123, "bar", "baz", parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondWithStatusAndMessageAndThrowable() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HandlerShim handler = new HandlerShim();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(123);
    baseRequest.setHandled(true);
    Throwable e = new Exception("quux");
    
    replayAll();

    handler.respond(123, "bar", "baz", e, parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondNotFound() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HandlerShim handler = new HandlerShim();

    response.setStatus(404);
    baseRequest.setHandled(true);
    
    replayAll();

    handler.respondNotFound(parameters);
    
    verifyAll();
  }

  @Test
  public void testRespondBadRequest() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HandlerShim handler = new HandlerShim();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(400);
    baseRequest.setHandled(true);
    
    replayAll();

    handler.respondBadRequest("bar", parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondBadRequestAndMessage() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HandlerShim handler = new HandlerShim();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(400);
    baseRequest.setHandled(true);
    
    replayAll();

    handler.respondBadRequest("bar", "baz", parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondBadRequestAndMessageAndThrowable() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HandlerShim handler = new HandlerShim();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(400);
    baseRequest.setHandled(true);
    Throwable e = new Exception("quux");
    
    replayAll();

    handler.respondBadRequest("bar", "baz", e, parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondBadRequestAndThrowable() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    HandleParameters parameters = createHandleParameters("/foo");
    HandlerShim handler = new HandlerShim();

    expect(response.getWriter()).andReturn(printWriter);
    response.setStatus(400);
    baseRequest.setHandled(true);
    Throwable e = new Exception("quux");
    
    replayAll();

    handler.respondBadRequest("bar", e, parameters);
    
    verifyAll();

    assertThat(stringWriter.toString()).isEqualTo("bar");
  }

  @Test
  public void testRespondNoContent() throws IOException {
    HandleParameters parameters = createHandleParameters("/foo");
    HandlerShim handler = new HandlerShim();

    response.setStatus(204);
    baseRequest.setHandled(true);
    
    replayAll();

    handler.respondNoContent(parameters);
    
    verifyAll();
  }

  private HandleParameters createHandleParameters(String target) {
    return new HandleParameters(target, baseRequest, request, response);
  }
  
  private class HandlerShim extends AbstractHttpHandler {
    @Override
    public void respond(String response, HandleParameters handleParameters) throws IOException {
      super.respond(response, handleParameters);
    }

    @Override
    public void respond(int status, String response, HandleParameters handleParameters)
        throws IOException {    
      super.respond(status, response, handleParameters);
    }
    
    @Override
    public void respond(int status, String response, String logMessage, HandleParameters handleParameters)
        throws IOException {    
      super.respond(status, response, logMessage, handleParameters);
    }
    
    @Override
    public void respond(int status, String response, String logMessage, Throwable logThrowable,
        HandleParameters handleParameters) throws IOException {    
      super.respond(status, response, logMessage, logThrowable, handleParameters);
    }
    
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
        HttpServletResponse response) throws IOException, ServletException {
    }
  }
}
