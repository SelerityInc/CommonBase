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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.Capture;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.test.InjectingTestCase;

public class AbstractHttpHandlerTest extends InjectingTestCase {
  private HttpRequest.Factory httpRequestFactory;
  private HttpRequest httpRequest;
  private Request request;
  private HttpServletRequest httpServletRequest;
  private HttpServletResponse httpServletResponse;

  @Before
  public void setUp() {
    httpRequestFactory = createMock(HttpRequest.Factory.class);
    httpRequest = createMock(HttpRequest.class);
    request = createMock(Request.class);
    httpServletRequest = createMock(HttpServletRequest.class);
    httpServletResponse = createMock(HttpServletResponse.class);
  }

  @Test
  public void testHandlePassdown() throws Exception {
    expect(httpRequestFactory.create("/foo", request, httpServletRequest, httpServletResponse))
      .andReturn(httpRequest);
    httpServletResponse.setHeader("Server", "n/a");

    replayAll();

    HttpHandlerShim handler = createHandler();
    handler.setHttpRequestFactory(httpRequestFactory);

    handler.handle("/foo", request, httpServletRequest, httpServletResponse);
    
    verifyAll();

    assertThat(handler.getRequest()).isSameAs(httpRequest);
  }

  @Test
  public void testHandleExceptionInDelegateHandle() throws Exception {
    IOException exception = new IOException("catch me");
    UUID uuid = getUuidGenerator().generate();
    expect(httpRequestFactory.create("/foo", request, httpServletRequest, httpServletResponse))
      .andReturn(httpRequest);
    httpServletResponse.setHeader("Server", "n/a");
    Capture<String> logMessageCapture = newCapture();
    Capture<IOException> logExceptionCapture = newCapture();
    expect(httpRequest.respondInternalServerError(capture(logMessageCapture),
        capture(logExceptionCapture))).andReturn(uuid);
    
    replayAll();

    HttpHandlerShim handler = createHandler(exception);
    handler.setHttpRequestFactory(httpRequestFactory);

    handler.handle("/foo", request, httpServletRequest, httpServletResponse);
    
    verifyAll();

    assertThat(handler.getRequest()).isSameAs(httpRequest);
    assertThat(logMessageCapture.getValue()).contains("catch me");
    assertThat(logExceptionCapture.getValue()).isSameAs(exception);
  }
  
  @Test
  public void testHandleExceptionBeforeHttpRequestSettingFallbockPrintWriter() throws Exception {
    PrintWriter printWriter = createMock(PrintWriter.class);
    printWriter.println("Internal server error.");
    printWriter.close();

    RuntimeException exception = new RuntimeException("catch me");
    httpServletResponse.setHeader("Server", "n/a");
    expectLastCall().andThrow(exception);
    
    httpServletResponse.reset();
    httpServletResponse.setStatus(500);
    httpServletResponse.setHeader("Server", "n/a");
    expect(httpServletResponse.getWriter()).andReturn(printWriter);
    expect(httpServletResponse.getOutputStream()).andThrow(new IOException("catch me too"));

    expect(request.isHandled()).andReturn(false);
    request.setHandled(true);

    replayAll();

    HttpHandlerShim handler = createHandler(exception);
    handler.setHttpRequestFactory(httpRequestFactory);

    handler.handle("/foo", request, httpServletRequest, httpServletResponse);
    
    verifyAll();

    assertThat(handler.getRequest()).isNull();
  }
  
  @Test
  public void testHandleExceptionInExceptionHandlingFallbackOutputStream() throws Exception {
    Capture<byte[]> contentCapture = newCapture();
    ServletOutputStream stream = createMock(ServletOutputStream.class);
    stream.write(capture(contentCapture));
    stream.close();

    IOException exception = new IOException("catch me");
    IOException exception2 = new IOException("catch me again");
    expect(httpRequestFactory.create("/foo", request, httpServletRequest, httpServletResponse))
      .andReturn(httpRequest);
    httpServletResponse.setHeader("Server", "n/a");
    Capture<String> logMessageCapture = newCapture();
    Capture<IOException> logExceptionCapture = newCapture();
    expect(httpRequest.respondInternalServerError(capture(logMessageCapture),
        capture(logExceptionCapture))).andThrow(exception2);
    
    httpServletResponse.reset();
    httpServletResponse.setStatus(500);
    httpServletResponse.setHeader("Server", "n/a");
    expect(httpServletResponse.getWriter()).andThrow(new IOException("catch me too"));
    expect(httpServletResponse.getOutputStream()).andReturn(stream);

    expect(request.isHandled()).andReturn(true);
    
    replayAll();

    HttpHandlerShim handler = createHandler(exception);
    handler.setHttpRequestFactory(httpRequestFactory);

    handler.handle("/foo", request, httpServletRequest, httpServletResponse);
    
    verifyAll();

    assertThat(handler.getRequest()).isSameAs(httpRequest);
    assertThat(logMessageCapture.getValue()).contains("catch me");
    assertThat(logExceptionCapture.getValue()).isSameAs(exception);
    assertThat(new String(contentCapture.getValue())).isEqualTo("Internal server error.");
  }
  
  private HttpHandlerShim createHandler() {
    return createHandler((RuntimeException)null);
  }
  
  private HttpHandlerShim createHandler(IOException exception) {
    return new HttpHandlerShim(exception);
  }
  
  private HttpHandlerShim createHandler(RuntimeException exception) {
    return new HttpHandlerShim(exception);
  }
  
  class HttpHandlerShim extends AbstractHttpHandler {
    private HttpRequest request;
    private final IOException ioException;
    private final RuntimeException runtimeException;

    HttpHandlerShim(IOException exception) {
      this.request = null;
      this.ioException = exception;
      this.runtimeException = null;
    }

    HttpHandlerShim(RuntimeException exception) {
      this.request = null;
      this.ioException = null;
      this.runtimeException = exception;
    }

    public HttpRequest getRequest() {
      return request;
    }

    @Override
    public void handle(HttpRequest request) throws IOException, ServletException {
      this.request = request;
      if (ioException != null) {
        throw ioException;
      }
      if (runtimeException != null) {
        throw runtimeException;
      }
    }
  }
}
