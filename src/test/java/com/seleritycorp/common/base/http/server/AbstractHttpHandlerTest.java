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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMockSupport;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.Test;

public class AbstractHttpHandlerTest extends EasyMockSupport {
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

    replayAll();

    HttpHandlerShim handler = createHandler();
    handler.setHttpRequestFactory(httpRequestFactory);

    handler.handle("/foo", request, httpServletRequest, httpServletResponse);
    
    verifyAll();

    assertThat(handler.getRequest()).isSameAs(httpRequest);
  }
  
  private HttpHandlerShim createHandler() {
    return new HttpHandlerShim();
  }
  
  class HttpHandlerShim extends AbstractHttpHandler {
    private HttpRequest request;
    
    public HttpRequest getRequest() {
      return request;
    }

    @Override
    public void handle(HttpRequest request) throws IOException, ServletException {
      this.request = request; 
    }
  }
}
