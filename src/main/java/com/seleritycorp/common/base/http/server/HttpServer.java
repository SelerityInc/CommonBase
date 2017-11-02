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

import com.seleritycorp.common.base.config.ApplicationConfig;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;
import com.seleritycorp.common.base.state.AppState;
import com.seleritycorp.common.base.state.AppStateFacetFactory;
import com.seleritycorp.common.base.state.AppStatePushFacet;
import com.seleritycorp.common.base.thread.ExecutorServiceFactory;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

/**
 * General purpose server responding to http requests.
 */
public class HttpServer implements AutoCloseable {
  private static final Log log = LogFactory.getLog(HttpServer.class);

  private Server server;
  private ServerConnector serverConnector;
  private AppStatePushFacet facet;
  private boolean isStarted;

  @Inject
  HttpServer(@ApplicationConfig Config config, @BaseHttpHandler AbstractHttpHandler httpHandler,
      ExecutorServiceFactory executorServiceFactory, AppStateFacetFactory appStateFacetFactory) {
    facet = appStateFacetFactory.createAppStatePushFacet("http-server");
    
    int threadCount = config.getInt("server.http.threads", 32);    
    ExecutorService executorService = executorServiceFactory
        .createFixedUnboundedDaemonExecutorService("http-server", threadCount);
    ExecutorThreadPool executorThreadPool = new ExecutorThreadPool(executorService);

    server = new Server(executorThreadPool);  
    
    serverConnector = new ServerConnector(server);
    serverConnector.setReuseAddress(true);
    serverConnector.setPort(config.getInt("server.http.port", 8080));
    server.setConnectors(new Connector[] {serverConnector});
    
    server.setHandler(httpHandler);
    
    isStarted = false;
  }

  /**
   * Starts the Http server
   * 
   * @throws Exception If the server fails to start. 
   */
  public void start() throws Exception {
    facet.setAppState(AppState.INITIALIZING, "Starting");
    try {
      server.start();
      isStarted = true;
      facet.setAppState(AppState.READY, "Started");
    } catch (Exception e) {
      facet.setAppState(AppState.FAULTY, "Starting failed. " + e.getMessage());
    }
  }
  
  @Override
  public void close() {
    facet.setAppState(AppState.FAULTY, "Stopped");
    serverConnector.close();
    try {
      server.stop();
      if (isStarted) {
        try {
          server.join();
        } catch (InterruptedException e) {
          facet.setAppState(AppState.FAULTY, "Joining failed. " + e.getMessage());
          log.warn("Failed to join after stopping http server", e);
        }
      }
    } catch (Exception e) {
      facet.setAppState(AppState.FAULTY, "Stopping failed. " + e.getMessage());
      log.warn("Failed to stop http server", e);
    }
  }
}
