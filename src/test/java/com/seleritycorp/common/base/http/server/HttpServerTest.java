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
import static org.easymock.EasyMock.anyObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.seleritycorp.common.base.http.server.AbstractHttpHandler;
import com.seleritycorp.common.base.http.server.HttpServer;
import com.seleritycorp.common.base.state.AppState;
import com.seleritycorp.common.base.state.AppStateFacetFactory;
import com.seleritycorp.common.base.state.AppStatePushFacet;
import com.seleritycorp.common.base.test.SettableConfig;
import com.seleritycorp.common.base.thread.ExecutorServiceFactory;
import com.seleritycorp.common.base.thread.ExecutorServiceMetrics;
import com.seleritycorp.common.base.thread.ThreadFactoryFactory;

public class HttpServerTest extends EasyMockSupport {

  public boolean singleTestHandlerTry() throws Exception {
    int port = getPort();

    AppStatePushFacet facet = createStrictMock(AppStatePushFacet.class);
    facet.setAppState(AppState.INITIALIZING, "Starting");
    facet.setAppState(AppState.READY, "Started");
    facet.setAppState(AppState.FAULTY, "Stopped");
    
    HttpServer server = createHttpServer(port, facet);

    try {
      server.start();

      String url = "http://localhost:" + port + "/foo";
      String response = IOUtils.toString(new URL(url).openStream());
      return response.contains("bar");
    } finally {
      server.close();
    }
  }

  @Test
  public void testHandlerStart() throws Exception {
    Boolean success = null;
    for (int tries = 5; tries > 0 && success == null; tries--) {
      try {
        resetAll();
        success = singleTestHandlerTry();
      } catch (Exception e) {
        if (tries != 0) {
          throw e;
        }
      }
    }
    assertThat(success).isNotNull();
    assertThat(success).isTrue();
    verifyAll();
  }

  @Test
  public void testHandlerStopWithoutStart() throws Exception {
    int port = getPort();

    AppStatePushFacet facet = createStrictMock(AppStatePushFacet.class);
    facet.setAppState(AppState.FAULTY, "Stopped");
    
    HttpServer server = createHttpServer(port, facet);
    server.close();

    verifyAll();
  }

  private class HttpHandler extends AbstractHttpHandler {
    @Override
    public void handle(String target, HandleParameters params) throws IOException,
        ServletException {
      PrintWriter writer = params.getResponse().getWriter();
      writer.println("bar");
      writer.close();
      params.getResponse().setStatus(200);
      params.getBaseRequest().setHandled(true);
    }
  }

  private int getPort() {
    return 8192 + (int) (Math.random() * 8192);     
  }

  private HttpServer createHttpServer(int port, AppStatePushFacet facet) throws Exception {
    SettableConfig config = new SettableConfig();
    config.setInt("server.http.port", port);
    config.setInt("server.http.threads", 10);

    AppStateFacetFactory appStateFacetFactory = createMock(AppStateFacetFactory.class);
    expect(appStateFacetFactory.createAppStatePushFacet("http-server")).andReturn(facet);

    ExecutorServiceMetrics.Factory executorServiceMetricsFactory = createMock(
        ExecutorServiceMetrics.Factory.class);
    ExecutorServiceMetrics executorServiceMetrics = createMock(ExecutorServiceMetrics.class);
    expect(executorServiceMetricsFactory.create(anyObject(ThreadPoolExecutor.class)))
      .andReturn(executorServiceMetrics);
  
    ThreadFactoryFactory threadFactoryFactory = new ThreadFactoryFactory();
    ExecutorServiceFactory executorServiceFactory = new ExecutorServiceFactory(
        threadFactoryFactory, executorServiceMetricsFactory);
    
    AbstractHttpHandler httpHandler = new HttpHandler();

    replayAll();

    HttpServer server = new HttpServer(config, httpHandler, executorServiceFactory,
        appStateFacetFactory);

    return server;
  }
}
