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
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.state.AppStateManager;

public class CommonHttpHandlerTest extends EasyMockSupport {
  private AbstractHttpHandler delegateHttpHandler;
  private AppStateManager appStateManager;
  private HttpHandlerUtils utils;

  private Request baseRequest;
  private HttpServletRequest request;
  private HttpServletResponse response;
  
  @Before
  public void setUp() {
    delegateHttpHandler = createMock(AbstractHttpHandler.class);
    appStateManager = createMock(AppStateManager.class);
    utils = createMock(HttpHandlerUtils.class);
    
    baseRequest = createMock(Request.class);
    request = createMock(HttpServletRequest.class);
    response = createMock(HttpServletResponse.class);
  }
  
  @Test
  public void testHandleStatusOk() throws Exception {
    Capture<HandleParameters> params1 = newCapture();
    Capture<HandleParameters> params2 = newCapture();

    expect(request.getMethod()).andReturn("GET");
    
    expect(utils.resolveRemoteAddr(capture(params1))).andReturn("10.0.0.1");
    utils.respond(eq("foo"), capture(params2));

    expect(appStateManager.getStatusReport()).andReturn("foo");

    expect(baseRequest.isHandled()).andReturn(true);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle("/status", baseRequest, request, response);

    verifyAll();
    
    assertParams("/status", params1, params2);
  }

  @Test
  public void testHandleStatusNotLocal() throws Exception {
    Capture<HandleParameters> params1 = newCapture();
    Capture<HandleParameters> params2 = newCapture();

    expect(request.getMethod()).andReturn("GET");
    
    expect(utils.resolveRemoteAddr(capture(params1))).andReturn("1.2.3.4");
    utils.respondForbidden(capture(params2));

    expect(baseRequest.isHandled()).andReturn(true);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle("/status", baseRequest, request, response);

    verifyAll();
    
    assertParams("/status", params1, params2);
  }

  @Test
  public void testHandleStatusNotGet() throws Exception {
    Capture<HandleParameters> params = newCapture();

    expect(request.getMethod()).andReturn("POST");
    
    utils.respondBadRequest(anyObject(String.class), capture(params));

    expect(baseRequest.isHandled()).andReturn(true);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle("/status", baseRequest, request, response);

    verifyAll();
    
    assertParams("/status", params);
  }

  @Test
  public void testHandleDelegateHandled() throws Exception {
    delegateHttpHandler.handle("/foo", baseRequest, request, response);

    expect(baseRequest.isHandled()).andReturn(true);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle("/foo", baseRequest, request, response);

    verifyAll();
  }

  @Test
  public void testHandleDelegateUnhandled() throws Exception {
    Capture<HandleParameters> params = newCapture();

    delegateHttpHandler.handle("/foo", baseRequest, request, response);

    expect(baseRequest.isHandled()).andReturn(false);

    utils.respondNotFound(capture(params));

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle("/foo", baseRequest, request, response);

    verifyAll();
    
    assertParams("/foo", params);
  }

  @SafeVarargs
  private final void assertParams(final String target, final Capture<HandleParameters>... paramCaptures) {
    for (Capture<HandleParameters> capture : paramCaptures) {
      HandleParameters params = capture.getValue();
      assertThat(params.getTarget()).isEqualTo(target);
      assertThat(params.getBaseRequest()).isSameAs(baseRequest);
      assertThat(params.getRequest()).isSameAs(request);
      assertThat(params.getResponse()).isSameAs(response);      
    }
  }

  private CommonHttpHandler createCommonHttpHandler() {
    return new CommonHttpHandler(delegateHttpHandler, appStateManager, utils);
    
  }
}
