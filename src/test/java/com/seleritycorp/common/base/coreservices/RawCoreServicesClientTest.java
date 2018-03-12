/*
 * Copyright (C) 2016-2018 Selerity, Inc. (support@seleritycorp.com)
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

package com.seleritycorp.common.base.coreservices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;

import java.io.IOException;

import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.seleritycorp.common.base.coreservices.CallErrorException;
import com.seleritycorp.common.base.http.client.HttpRequest;
import com.seleritycorp.common.base.http.client.HttpRequestFactory;
import com.seleritycorp.common.base.http.client.HttpResponse;
import com.seleritycorp.common.base.meta.MetaDataFormatter;
import com.seleritycorp.common.base.test.InjectingTestCase;
import com.seleritycorp.common.base.test.SettableConfig;
import com.seleritycorp.common.base.test.SettableUuidGenerator;

public class RawCoreServicesClientTest extends InjectingTestCase {
  SettableConfig config;
  SettableUuidGenerator uuidGenerator;
  MetaDataFormatter metaDataFormatter;
  HttpRequestFactory requestFactory;

  @Before
  public void setUp() throws IOException {
    config = new SettableConfig();
    config.set("CoreServices.user", "foo");
    config.set("CoreServices.url", "bar");
    config.set("CoreServices.timeout", "1");
    config.set("CoreServices.timeoutUnit", "MINUTES");

    uuidGenerator = getUuidGenerator();
    metaDataFormatter = createMock(MetaDataFormatter.class);
    requestFactory = createMock(HttpRequestFactory.class);
  }

  @Test
  public void testCallPlain() throws Exception {
    JsonObject jsonResponse = new JsonObject();
    jsonResponse.addProperty("result", 23);

    HttpResponse response = createMock(HttpResponse.class);
    expect(response.getBodyAsJsonObject()).andReturn(jsonResponse);

    HttpRequest request = createMock(HttpRequest.class);
    expect(request.setReadTimeoutMillis(5)).andReturn(request);
    expect(request.execute()).andReturn(response);
    
    expect(metaDataFormatter.getUserAgent()).andReturn("quux");

    Capture<JsonObject> jsonCapture = newCapture();
    expect(requestFactory.createPostJson(eq("bar"), capture(jsonCapture))).andReturn(request);
    
    replayAll();

    RawCoreServiceClient client = createRawCoreServicesClient();

    JsonObject params = new JsonObject();
    params.addProperty("bar", 4711);
    JsonElement result = client.call("baz", params, null, 5);

    verifyAll();

    assertThat(result.getAsInt()).isEqualTo(23);

    JsonObject json = jsonCapture.getValue();
    assertThat(json.get("id").getAsString()).isEqualTo("00000000-0000-0000-0000-000000000001");
    assertThat(json.get("method").getAsString()).isEqualTo("baz");
    assertThat(json.getAsJsonObject("params").get("bar").getAsInt()).isEqualTo(4711);
    assertThat(json.getAsJsonObject("header").get("user").getAsString()).isEqualTo("foo");
    assertThat(json.getAsJsonObject("header").get("client").getAsString()).isEqualTo("quux");
    assertThat(json.getAsJsonObject("header").get("token")).isNull();
  }

  @Test
  public void testCallWithToken() throws Exception {
    JsonObject jsonResponse = new JsonObject();
    jsonResponse.addProperty("result", 23);

    HttpResponse response = createMock(HttpResponse.class);
    expect(response.getBodyAsJsonObject()).andReturn(jsonResponse);

    HttpRequest request = createMock(HttpRequest.class);
    expect(request.setReadTimeoutMillis(5)).andReturn(request);
    expect(request.execute()).andReturn(response);
    
    expect(metaDataFormatter.getUserAgent()).andReturn("quux");

    Capture<JsonObject> jsonCapture = newCapture();
    expect(requestFactory.createPostJson(eq("bar"), capture(jsonCapture))).andReturn(request);
    
    replayAll();

    RawCoreServiceClient client = createRawCoreServicesClient();

    JsonObject params = new JsonObject();
    params.addProperty("bar", 4711);
    JsonElement result = client.call("baz", params, "quuux", 5);

    verifyAll();

    assertThat(result.getAsInt()).isEqualTo(23);

    JsonObject json = jsonCapture.getValue();
    assertThat(json.get("id").getAsString()).isEqualTo("00000000-0000-0000-0000-000000000001");
    assertThat(json.get("method").getAsString()).isEqualTo("baz");
    assertThat(json.getAsJsonObject("params").get("bar").getAsInt()).isEqualTo(4711);
    assertThat(json.getAsJsonObject("header").get("user").getAsString()).isEqualTo("foo");
    assertThat(json.getAsJsonObject("header").get("client").getAsString()).isEqualTo("quux");
    assertThat(json.getAsJsonObject("header").get("token").getAsString()).isEqualTo("quuux");
  }

  @Test
  public void testCallNegativeTimeout() throws Exception {
    JsonObject jsonResponse = new JsonObject();
    jsonResponse.addProperty("result", 23);

    HttpResponse response = createMock(HttpResponse.class);
    expect(response.getBodyAsJsonObject()).andReturn(jsonResponse);

    HttpRequest request = createMock(HttpRequest.class);
    expect(request.setReadTimeoutMillis(60000)).andReturn(request);
    expect(request.execute()).andReturn(response);
    
    expect(metaDataFormatter.getUserAgent()).andReturn("quux");

    Capture<JsonObject> jsonCapture = newCapture();
    expect(requestFactory.createPostJson(eq("bar"), capture(jsonCapture))).andReturn(request);
    
    replayAll();

    RawCoreServiceClient client = createRawCoreServicesClient();

    JsonObject params = new JsonObject();
    params.addProperty("bar", 4711);
    JsonElement result = client.call("baz", params, null, -1);

    verifyAll();

    assertThat(result.getAsInt()).isEqualTo(23);

    JsonObject json = jsonCapture.getValue();
    assertThat(json.get("id").getAsString()).isEqualTo("00000000-0000-0000-0000-000000000001");
    assertThat(json.get("method").getAsString()).isEqualTo("baz");
    assertThat(json.getAsJsonObject("params").get("bar").getAsInt()).isEqualTo(4711);
    assertThat(json.getAsJsonObject("header").get("user").getAsString()).isEqualTo("foo");
    assertThat(json.getAsJsonObject("header").get("client").getAsString()).isEqualTo("quux");
    assertThat(json.getAsJsonObject("header").get("token")).isNull();
  }

  @Test
  public void testCallError() throws Exception {
    JsonObject jsonResponse = new JsonObject();
    jsonResponse.addProperty("error", "errorFoo");

    HttpResponse response = createMock(HttpResponse.class);
    expect(response.getBodyAsJsonObject()).andReturn(jsonResponse);

    HttpRequest request = createMock(HttpRequest.class);
    expect(request.setReadTimeoutMillis(5)).andReturn(request);
    expect(request.execute()).andReturn(response);
    
    expect(metaDataFormatter.getUserAgent()).andReturn("quux");

    Capture<JsonObject> jsonCapture = newCapture();
    expect(requestFactory.createPostJson(eq("bar"), capture(jsonCapture))).andReturn(request);
    
    replayAll();

    RawCoreServiceClient client = createRawCoreServicesClient();

    JsonObject params = new JsonObject();
    params.addProperty("bar", 4711);

    try {
      client.call("baz", params, null, 5);
      failBecauseExceptionWasNotThrown(CallErrorException.class);
    } catch (CallErrorException e) {
      assertThat(e.getMessage()).contains("errorFoo");
    }

    verifyAll();

    JsonObject json = jsonCapture.getValue();
    assertThat(json.get("id").getAsString()).isEqualTo("00000000-0000-0000-0000-000000000001");
    assertThat(json.get("method").getAsString()).isEqualTo("baz");
    assertThat(json.getAsJsonObject("params").get("bar").getAsInt()).isEqualTo(4711);
    assertThat(json.getAsJsonObject("header").get("user").getAsString()).isEqualTo("foo");
    assertThat(json.getAsJsonObject("header").get("client").getAsString()).isEqualTo("quux");
    assertThat(json.getAsJsonObject("header").get("token")).isNull();
  }

  private RawCoreServiceClient createRawCoreServicesClient() {
    return new RawCoreServiceClient(config, uuidGenerator, metaDataFormatter, requestFactory);
  }
}
