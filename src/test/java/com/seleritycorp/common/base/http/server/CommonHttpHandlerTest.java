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

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.state.AppStateManager;
import com.seleritycorp.common.base.test.InjectingTestCase;

public class CommonHttpHandlerTest extends InjectingTestCase {
  private AbstractHttpHandler delegateHttpHandler;
  private AppStateManager appStateManager;

  private HttpRequest httpRequest;
  private HttpRequest.Factory httpRequestFactory;

  @Before
  public void setUp() {
    delegateHttpHandler = createMock(AbstractHttpHandler.class);
    appStateManager = createMock(AppStateManager.class);
    httpRequestFactory = createMock(HttpRequest.Factory.class);

    httpRequest = createMock(HttpRequest.class);

    InjectorFactory.register(new AbstractModule(){
      @Override
      protected void configure() {
        bind(HttpRequest.Factory.class).toInstance(httpRequestFactory);
      }
    });
  }
  
  @Test
  public void testHandleStatusOk() throws Exception {
    expect(httpRequest.getTarget()).andReturn("/status");
    expect(httpRequest.getResolvedRemoteAddr()).andReturn("10.0.0.1");
    expect(httpRequest.isMethodGet()).andReturn(true);

    expect(appStateManager.getStatusReport()).andReturn("foo");
    httpRequest.respondOkText("foo");

    expect(httpRequest.hasBeenHandled()).andReturn(true);

    replayAll();

    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle(httpRequest);

    verifyAll();
  }

  @Test
  public void testHandleStatusNotLocal() throws Exception {
    expect(httpRequest.getTarget()).andReturn("/status");
    expect(httpRequest.getResolvedRemoteAddr()).andReturn("1.2.3.4");
    expect(httpRequest.respondForbidden()).andReturn(getUuidGenerator().generate());
    expect(httpRequest.isMethodGet()).andReturn(true);

    expect(httpRequest.hasBeenHandled()).andReturn(true);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle(httpRequest);

    verifyAll();
  }

  @Test
  public void testHandleStatusNotGet() throws Exception {
    expect(httpRequest.getTarget()).andReturn("/status").anyTimes();
    expect(httpRequest.respondBadRequest(same(BasicErrorCode.E_WRONG_METHOD),
        anyObject(String.class))).andReturn(getUuidGenerator().generate());
    expect(httpRequest.isMethodGet()).andReturn(false);
    
    expect(httpRequest.hasBeenHandled()).andReturn(true);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle(httpRequest);

    verifyAll();
  }

  @Test
  public void testHandleDelegateHandled() throws Exception {
    expect(httpRequest.getTarget()).andReturn("/foo");
    delegateHttpHandler.handle(httpRequest);

    expect(httpRequest.hasBeenHandled()).andReturn(true);

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle(httpRequest);

    verifyAll();
  }

  @Test
  public void testHandleDelegateUnhandled() throws Exception {
    expect(httpRequest.getTarget()).andReturn("/foo");
    delegateHttpHandler.handle(httpRequest);

    expect(httpRequest.hasBeenHandled()).andReturn(false);

    expect(httpRequest.respondNotFound()).andReturn(getUuidGenerator().generate());

    replayAll();
    
    AbstractHttpHandler handler = createCommonHttpHandler();
    handler.handle(httpRequest);

    verifyAll();
  }

  private CommonHttpHandler createCommonHttpHandler() {
    CommonHttpHandler.AbstractHttpHandlerHolder holder =
        new CommonHttpHandler.AbstractHttpHandlerHolder();
    holder.value = delegateHttpHandler;
    CommonHttpHandler ret = new CommonHttpHandler(holder, appStateManager);
    ret.setHttpRequestFactory(httpRequestFactory);
    return ret;
  }
}
