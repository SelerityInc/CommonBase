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

package com.seleritycorp.common.base.eventprocessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;

import java.util.UUID;

import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.seleritycorp.common.base.http.client.HttpException;
import com.seleritycorp.common.base.http.client.HttpRequest;
import com.seleritycorp.common.base.http.client.HttpRequestFactory;
import com.seleritycorp.common.base.test.InjectingTestCase;
import com.seleritycorp.common.base.test.SettableConfig;
import com.seleritycorp.common.base.uuid.UuidGenerator;

public class EventSenderTest extends InjectingTestCase {
  private SettableConfig config;
  private UuidGenerator uuidGenerator;
  private HttpRequestFactory httpRequestFactory;
  
  @Before
  public void setUp() {
    this.config = new SettableConfig();
    config.set("EventProcessor.beaconUrl", "foo://example.org/beacon");
    this.uuidGenerator = getUuidGenerator();
    this.httpRequestFactory = createMock(HttpRequestFactory.class);
  }

  @Test
  public void testSendOk() throws HttpException {
    JsonObject payload = new JsonObject();
    Capture<JsonObject> jsonCapture = newCapture();

    HttpRequest request1 = createMock(HttpRequest.class);
    HttpRequest request2 = createMock(HttpRequest.class);
    
    expect(httpRequestFactory.createPostJson(eq("foo://example.org/beacon"), capture(jsonCapture)))
    .andReturn(request1);
    expect(request1.setExpectedStatusCode(204)).andReturn(request2);
    expect(request2.execute()).andReturn(null);
    
    replayAll();

    EventSender sender = createEventSender();    
    UUID uuid = sender.send("foo", 42, payload);
    
    verifyAll();

    assertThat(uuid.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");

    JsonObject fullSentObject = jsonCapture.getValue();
    assertThat(fullSentObject.get("schema").getAsString()).isEqualTo("foo");
    assertThat(fullSentObject.get("schemaVersion").getAsNumber()).isEqualTo(42);
    assertThat(fullSentObject.get("uuid").getAsString()).isEqualTo("00000000-0000-0000-0000-000000000001");
    assertThat(fullSentObject.get("payload")).isEqualTo(payload);
  }

  @Test
  public void testSendException() throws HttpException {
    RuntimeException exception = new RuntimeException("catch me"); 
    JsonObject payload = new JsonObject();
    Capture<JsonObject> jsonCapture = newCapture();

    
    expect(httpRequestFactory.createPostJson(eq("foo://example.org/beacon"), capture(jsonCapture)))
      .andThrow(exception);
    
    replayAll();

    EventSender sender = createEventSender();    
    UUID uuid = sender.send("foo", 42, payload);
    
    verifyAll();

    assertThat(uuid.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");

    JsonObject fullSentObject = jsonCapture.getValue();
    assertThat(fullSentObject.get("schema").getAsString()).isEqualTo("foo");
    assertThat(fullSentObject.get("schemaVersion").getAsNumber()).isEqualTo(42);
    assertThat(fullSentObject.get("uuid").getAsString()).isEqualTo("00000000-0000-0000-0000-000000000001");
    assertThat(fullSentObject.get("payload")).isEqualTo(payload);
  }

  @Test
  public void testSendChecked() throws HttpException {
    JsonObject payload = new JsonObject();
    Capture<JsonObject> jsonCapture = newCapture();

    HttpRequest request1 = createMock(HttpRequest.class);
    HttpRequest request2 = createMock(HttpRequest.class);
    
    expect(httpRequestFactory.createPostJson(eq("foo://example.org/beacon"), capture(jsonCapture)))
    .andReturn(request1);
    expect(request1.setExpectedStatusCode(204)).andReturn(request2);
    expect(request2.execute()).andReturn(null);
    
    replayAll();

    EventSender sender = createEventSender();    
    UUID uuid = sender.sendChecked("foo", 42, payload);
    
    verifyAll();

    assertThat(uuid.toString()).isEqualTo("00000000-0000-0000-0000-000000000001");

    JsonObject fullSentObject = jsonCapture.getValue();
    assertThat(fullSentObject.get("schema").getAsString()).isEqualTo("foo");
    assertThat(fullSentObject.get("schemaVersion").getAsNumber()).isEqualTo(42);
    assertThat(fullSentObject.get("uuid").getAsString()).isEqualTo("00000000-0000-0000-0000-000000000001");
    assertThat(fullSentObject.get("payload")).isEqualTo(payload);
  }

  @Test
  public void testSendCheckedException() throws HttpException {
    HttpException exception = new HttpException("catch me"); 
    JsonObject payload = new JsonObject();
    Capture<JsonObject> jsonCapture = newCapture();

    
    HttpRequest request1 = createMock(HttpRequest.class);
    HttpRequest request2 = createMock(HttpRequest.class);
    
    expect(httpRequestFactory.createPostJson(eq("foo://example.org/beacon"), capture(jsonCapture)))
    .andReturn(request1);
    expect(request1.setExpectedStatusCode(204)).andReturn(request2);
    expect(request2.execute()).andThrow(exception);
    
    replayAll();

    EventSender sender = createEventSender();    
    try {
      sender.sendChecked("foo", 42, payload);
      failBecauseExceptionWasNotThrown(HttpException.class);
    } catch (HttpException e) {
      assertThat(e).isSameAs(exception);
    }
    
    verifyAll();

    JsonObject fullSentObject = jsonCapture.getValue();
    assertThat(fullSentObject.get("schema").getAsString()).isEqualTo("foo");
    assertThat(fullSentObject.get("schemaVersion").getAsNumber()).isEqualTo(42);
    assertThat(fullSentObject.get("uuid").getAsString()).isEqualTo("00000000-0000-0000-0000-000000000001");
    assertThat(fullSentObject.get("payload")).isEqualTo(payload);
  }

  private EventSender createEventSender() {
    return new EventSender(config, uuidGenerator, httpRequestFactory);
  }
}
