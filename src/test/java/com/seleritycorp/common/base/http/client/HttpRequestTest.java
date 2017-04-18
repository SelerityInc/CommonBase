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
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
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

  @Test
  public void testExecuteGetWithData() throws Exception {
    reset(httpClient);
    reset(responseFactory);

    replayAll();
    
    HttpRequest request = createHttpRequest("foo").addData("bar");

    try {
      request.execute();
      failBecauseExceptionWasNotThrown(HttpException.class);
    } catch (HttpException e) {
      assertThat(e.getMessage()).contains("data");
    }
    
    verifyAll();
  }

  @Test
  public void testExecuteExpecedStatusCode() throws Exception {
    expect(httpResponse.getStatusCode()).andReturn(123);

    replayAll();
    
    HttpRequest request = createHttpRequest("foo").setExpectedStatusCode(123);

    HttpResponse response = request.execute();

    verifyAll();
    
    assertThat(response).isEqualTo(httpResponse);

    HttpUriRequest backendRequest = backendRequestCapture.getValue();
    assertThat(backendRequest.getMethod()).isEqualTo("GET");
    assertThat(backendRequest.getURI().toString()).isEqualTo("foo");

  }

  @Test
  public void testExecuteUnexpecedStatusCode() throws Exception {
    expect(httpResponse.getStatusCode()).andReturn(200);

    replayAll();
    
    HttpRequest request = createHttpRequest("foo").setExpectedStatusCode(123);

    try {
      request.execute();
      failBecauseExceptionWasNotThrown(HttpException.class);
    } catch (HttpException e) {
      assertThat(e.getMessage()).contains("123");
      assertThat(e.getMessage()).contains("200");
    }
    
    verifyAll();
  }

  @Test
  public void testSetUserAgentPlain() throws Exception {
    replayAll();
    
    HttpRequest request = createHttpRequest("foo");
    HttpRequest requestAfterSetting = request.setUserAgent("foo");
    HttpResponse response = request.execute();
    
    verifyAll();
    
    assertThat(request).isSameAs(requestAfterSetting);
    assertThat(response).isEqualTo(httpResponse);

    HttpUriRequest backendRequest = backendRequestCapture.getValue();
    assertThat(backendRequest.getMethod()).isEqualTo("GET");
    assertThat(backendRequest.getURI().toString()).isEqualTo("foo");
    assertThat(backendRequest.getHeaders("User-Agent")).hasSize(1);    
    assertThat(backendRequest.getFirstHeader("User-Agent").getValue()).isEqualTo("foo");    
  }

  @Test
  public void testSetUserAgentOverwrite() throws Exception {
    replayAll();
    
    HttpRequest request = createHttpRequest("foo");
    HttpRequest requestAfterSetting1 = request.setUserAgent("foo1");
    HttpRequest requestAfterSetting2 = request.setUserAgent("foo2");
    HttpResponse response = request.execute();
    
    verifyAll();
    
    assertThat(request).isSameAs(requestAfterSetting1);
    assertThat(request).isSameAs(requestAfterSetting2);
    assertThat(response).isEqualTo(httpResponse);

    HttpUriRequest backendRequest = backendRequestCapture.getValue();
    assertThat(backendRequest.getMethod()).isEqualTo("GET");
    assertThat(backendRequest.getURI().toString()).isEqualTo("foo");
    assertThat(backendRequest.getHeaders("User-Agent")).hasSize(1);    
    assertThat(backendRequest.getFirstHeader("User-Agent").getValue()).isEqualTo("foo2");    
  }

  @Test
  public void testSetUserAgentReset() throws Exception {
    replayAll();
    
    HttpRequest request = createHttpRequest("foo");
    HttpRequest requestAfterSetting1 = request.setUserAgent("foo1");
    HttpRequest requestAfterSetting2 = request.setUserAgent(null);
    HttpResponse response = request.execute();
    
    verifyAll();
    
    assertThat(request).isSameAs(requestAfterSetting1);
    assertThat(request).isSameAs(requestAfterSetting2);
    assertThat(response).isEqualTo(httpResponse);

    HttpUriRequest backendRequest = backendRequestCapture.getValue();
    assertThat(backendRequest.getMethod()).isEqualTo("GET");
    assertThat(backendRequest.getURI().toString()).isEqualTo("foo");
    assertThat(backendRequest.getHeaders("User-Agent")).hasSize(0);    
  }

  @Test
  public void testSetReadTimeout() throws Exception {
    replayAll();
    
    HttpRequest request = createHttpRequest("foo");
    HttpRequest requestAfterSetting = request.setReadTimeoutMillis(4711);
    HttpResponse response = request.execute();
    
    verifyAll();
    
    assertThat(request).isSameAs(requestAfterSetting);
    assertThat(response).isEqualTo(httpResponse);

    HttpUriRequest backendRequestRaw = backendRequestCapture.getValue();
    assertThat(backendRequestRaw).isInstanceOf(HttpRequestBase.class);
    HttpRequestBase backendRequest = (HttpRequestBase) backendRequestRaw;
    assertThat(backendRequest.getMethod()).isEqualTo("GET");
    assertThat(backendRequest.getURI().toString()).isEqualTo("foo");
    assertThat(backendRequest.getConfig().getSocketTimeout()).isEqualTo(4711);    
  }

  @Test
  public void testSetMethodPost() throws Exception {
    replayAll();
    
    HttpRequest request = createHttpRequest("foo");
    HttpRequest requestAfterSetting = request.setMethodPost();
    HttpResponse response = request.execute();
    
    verifyAll();
    
    assertThat(request).isSameAs(requestAfterSetting);
    assertThat(response).isEqualTo(httpResponse);

    HttpUriRequest backendRequest = backendRequestCapture.getValue();
    assertThat(backendRequest.getMethod()).isEqualTo("POST");
    assertThat(backendRequest.getURI().toString()).isEqualTo("foo");
  }

  @Test
  public void testAddDataSingle() throws Exception {
    replayAll();
    
    HttpRequest request = createHttpRequest("foo");
    HttpRequest requestAfterSetting1 = request.setMethodPost();
    HttpRequest requestAfterSetting2 = request.addData("foo=bar%");
    HttpResponse response = request.execute();
    
    verifyAll();
    
    assertThat(request).isSameAs(requestAfterSetting1);
    assertThat(request).isSameAs(requestAfterSetting2);
    assertThat(response).isEqualTo(httpResponse);

    HttpUriRequest backendRequestRaw = backendRequestCapture.getValue();
    assertThat(backendRequestRaw).isInstanceOf(HttpEntityEnclosingRequestBase.class);
    HttpEntityEnclosingRequestBase backendRequest =
        (HttpEntityEnclosingRequestBase) backendRequestRaw;
    assertThat(backendRequest.getMethod()).isEqualTo("POST");
    assertThat(backendRequest.getURI().toString()).isEqualTo("foo");
    HttpEntity entity = backendRequest.getEntity();
    assertThat(entity.getContentType().getValue()).isEqualTo("text/plain; charset=UTF-8");
    assertThat(IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8)).isEqualTo("foo=bar%");
  }

  @Test
  public void testAddDataSingleWithContentType() throws Exception {
    replayAll();
    
    HttpRequest request = createHttpRequest("foo");
    HttpRequest requestAfterSetting1 = request.setMethodPost();
    HttpRequest requestAfterSetting2 = request.setContentType(ContentType.APPLICATION_JSON);
    HttpRequest requestAfterSetting3 = request.addData("foo=bar%");
    HttpResponse response = request.execute();
    
    verifyAll();
    
    assertThat(request).isSameAs(requestAfterSetting1);
    assertThat(request).isSameAs(requestAfterSetting2);
    assertThat(request).isSameAs(requestAfterSetting3);
    assertThat(response).isEqualTo(httpResponse);

    HttpUriRequest backendRequestRaw = backendRequestCapture.getValue();
    assertThat(backendRequestRaw).isInstanceOf(HttpEntityEnclosingRequestBase.class);
    HttpEntityEnclosingRequestBase backendRequest =
        (HttpEntityEnclosingRequestBase) backendRequestRaw;
    assertThat(backendRequest.getMethod()).isEqualTo("POST");
    assertThat(backendRequest.getURI().toString()).isEqualTo("foo");
    HttpEntity entity = backendRequest.getEntity();
    assertThat(entity.getContentType().getValue()).isEqualTo("application/json; charset=UTF-8");
    assertThat(IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8)).isEqualTo("foo=bar%");
  }

  @Test
  public void testAddDataAppending() throws Exception {
    replayAll();
    
    HttpRequest request = createHttpRequest("foo");
    HttpRequest requestAfterSetting1 = request.setMethodPost();
    HttpRequest requestAfterSetting2 = request.addData("foo=bar%");
    HttpRequest requestAfterSetting3 = request.addData("baz&quux");
    HttpResponse response = request.execute();
    
    verifyAll();
    
    assertThat(request).isSameAs(requestAfterSetting1);
    assertThat(request).isSameAs(requestAfterSetting2);
    assertThat(request).isSameAs(requestAfterSetting3);
    assertThat(response).isEqualTo(httpResponse);

    HttpUriRequest backendRequestRaw = backendRequestCapture.getValue();
    assertThat(backendRequestRaw).isInstanceOf(HttpEntityEnclosingRequestBase.class);
    HttpEntityEnclosingRequestBase backendRequest =
        (HttpEntityEnclosingRequestBase) backendRequestRaw;
    assertThat(backendRequest.getMethod()).isEqualTo("POST");
    assertThat(backendRequest.getURI().toString()).isEqualTo("foo");
    HttpEntity entity = backendRequest.getEntity();
    assertThat(entity.getContentType().getValue()).isEqualTo("text/plain; charset=UTF-8");
    assertThat(IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8)).isEqualTo("foo=bar%&baz&quux");
  }

  @Test
  public void testAddDataAppendingWithContentType() throws Exception {
    replayAll();
    
    HttpRequest request = createHttpRequest("foo");
    HttpRequest requestAfterSetting1 = request.setMethodPost();
    HttpRequest requestAfterSetting2 = request.addData("foo=bar%");
    HttpRequest requestAfterSetting3 = request.setContentType(ContentType.APPLICATION_JSON);
    HttpRequest requestAfterSetting4 = request.addData("baz&quux");
    HttpResponse response = request.execute();
    
    verifyAll();
    
    assertThat(request).isSameAs(requestAfterSetting1);
    assertThat(request).isSameAs(requestAfterSetting2);
    assertThat(request).isSameAs(requestAfterSetting3);
    assertThat(request).isSameAs(requestAfterSetting4);
    assertThat(response).isEqualTo(httpResponse);

    HttpUriRequest backendRequestRaw = backendRequestCapture.getValue();
    assertThat(backendRequestRaw).isInstanceOf(HttpEntityEnclosingRequestBase.class);
    HttpEntityEnclosingRequestBase backendRequest =
        (HttpEntityEnclosingRequestBase) backendRequestRaw;
    assertThat(backendRequest.getMethod()).isEqualTo("POST");
    assertThat(backendRequest.getURI().toString()).isEqualTo("foo");
    HttpEntity entity = backendRequest.getEntity();
    assertThat(entity.getContentType().getValue()).isEqualTo("application/json; charset=UTF-8");
    assertThat(IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8)).isEqualTo("foo=bar%&baz&quux");
  }

  private HttpRequest createHttpRequest(String url) throws HttpException {
    return new HttpRequest(url, httpClient, responseFactory);
  }
}
