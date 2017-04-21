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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.same;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
  private HandleParameters params;

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
    params = new HandleParameters("/status", baseRequest, request, response);

    expect(utils.resolveRemoteAddr(params)).andReturn("10.0.0.1");
    utils.respond("foo", params);
    expect(utils.isMethodGet(params)).andReturn(true);

    expect(appStateManager.getStatusReport()).andReturn("foo");

    expect(utils.isHandled(params)).andReturn(true);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle("/status", params);

    verifyAll();
  }

  @Test
  public void testHandleStatusNotLocal() throws Exception {
    params = new HandleParameters("/status", baseRequest, request, response);

    expect(utils.resolveRemoteAddr(params)).andReturn("1.2.3.4");
    utils.respondForbidden(params);
    expect(utils.isMethodGet(params)).andReturn(true);

    expect(utils.isHandled(params)).andReturn(true);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle("/status", params);

    verifyAll();
  }

  @Test
  public void testHandleStatusNotGet() throws Exception {
    params = new HandleParameters("/status", baseRequest, request, response);

    utils.respondBadRequest(anyObject(String.class), same(params));
    expect(utils.isMethodGet(params)).andReturn(false);
    
    expect(utils.isHandled(params)).andReturn(true);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle("/status", params);

    verifyAll();
  }

  @Test
  public void testHandleDelegateHandled() throws Exception {
    params = new HandleParameters("/foo", baseRequest, request, response);

    delegateHttpHandler.handle("/foo", params);

    expect(utils.isHandled(params)).andReturn(true);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle("/foo", params);

    verifyAll();
  }

  @Test
  public void testHandleDelegateUnhandled() throws Exception {
    params = new HandleParameters("/foo", baseRequest, request, response);

    delegateHttpHandler.handle("/foo", params);

    expect(utils.isHandled(params)).andReturn(false);

    utils.respondNotFound(params);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle("/foo", params);

    verifyAll();
  }

  private CommonHttpHandler createCommonHttpHandler() {
    return new CommonHttpHandler(delegateHttpHandler, appStateManager, utils);
    
  }
}
