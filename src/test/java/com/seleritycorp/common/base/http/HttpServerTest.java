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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.junit.Test;

import com.seleritycorp.common.base.test.SettableConfig;
import com.seleritycorp.common.base.thread.ExecutorServiceFactory;
import com.seleritycorp.common.base.thread.ThreadFactoryFactory;

public class HttpServerTest {
  public boolean singleTestHandlerTry() throws Exception {
    int port = 8192 + (int) (Math.random() * 8192); 
    SettableConfig config = new SettableConfig();
    config.setInt("server.http.port", port);
    config.setInt("server.http.threads", 10);

    ThreadFactoryFactory threadFactoryFactory = new ThreadFactoryFactory();
    ExecutorServiceFactory executorServiceFactory = new ExecutorServiceFactory(
        threadFactoryFactory);
    
    AbstractHttpHandler httpHandler = new HttpHandler();
    
    HttpServer server = new HttpServer(config, httpHandler, executorServiceFactory);
    
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
  public void testHandler() throws Exception {
    Boolean success = null;
    for (int tries = 5; tries > 0 && success == null; tries--) {
      try {
        success = singleTestHandlerTry();
      } catch (Exception e) {
        if (tries != 0) {
          throw e;
        }
      }
    }
    assertThat(success).isNotNull();
    assertThat(success).isTrue();
  }

  private class HttpHandler extends AbstractHttpHandler {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
        HttpServletResponse response) throws IOException, ServletException {
      PrintWriter writer = response.getWriter();
      writer.println("bar");
      writer.close();
      response.setStatus(200);
      baseRequest.setHandled(true);
    }
  }
}
