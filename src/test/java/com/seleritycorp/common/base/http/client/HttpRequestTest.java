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

package com.seleritycorp.common.base.http.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.reset;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

public class HttpRequestTest extends EasyMockSupport {
  private HttpClient httpClient;
  private HttpResponse.Factory responseFactory;
  private HttpResponse httpResponse;
  private org.apache.http.HttpResponse backendResponse;
  private Capture<HttpUriRequest> backendRequestCapture;

  @Before
  public void setUp() throws Exception {
    httpClient = createMock(HttpClient.class);
    responseFactory = createMock(HttpResponse.Factory.class);

    httpResponse = createMock(HttpResponse.class);
    backendResponse = createMock(org.apache.http.HttpResponse.class);

    backendRequestCapture = newCapture();
    expect(httpClient.execute(capture(backendRequestCapture))).andReturn(backendResponse);
    expect(responseFactory.create(backendResponse)).andReturn(httpResponse);
  }

  @Test
  public void testExecuteOk() throws Exception {
    replayAll();
    
    HttpRequest request = createHttpRequest("foo");
    HttpResponse response = request.execute();
    
    verifyAll();
    
    assertThat(response).isEqualTo(httpResponse);

    HttpUriRequest backendRequest = backendRequestCapture.getValue();
    assertThat(backendRequest.getMethod()).isEqualTo("GET");
    assertThat(backendRequest.getURI().toString()).isEqualTo("foo");
  }
  
  @Test
  public void testExecuteMalformedUri() throws Exception {
    reset(httpClient);
    reset(responseFactory);

    replayAll();
    
    HttpRequest request = createHttpRequest("http://");
    try {
      request.execute();
      failBecauseExceptionWasNotThrown(HttpException.class);
    } catch (HttpException e) {
      assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
    }
    
    verifyAll();
  }
    
  @Test
  public void testPerformPerformingFails() throws Exception {
    reset(httpClient);
    reset(responseFactory);

    IOException expected = new IOException("catch me");

    expect(httpClient.execute(capture(backendRequestCapture))).andThrow(expected);
    
    replayAll();
    
    HttpRequest request = createHttpRequest("foo");
    try {
      request.execute();
      failBecauseExceptionWasNotThrown(HttpException.class);
    } catch (HttpException e) {
      assertThat(e.getCause()).isEqualTo(expected);
    }
    
    verifyAll();
  }

  private HttpRequest createHttpRequest(String url) throws HttpException {
    return new HttpRequest(url, httpClient, responseFactory);
  }
}
