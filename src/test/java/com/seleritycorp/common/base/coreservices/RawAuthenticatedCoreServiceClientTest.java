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

package com.seleritycorp.common.base.coreservices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.anyInt;

import java.io.IOException;

import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.seleritycorp.common.base.coreservices.CallErrorException;
import com.seleritycorp.common.base.test.InjectingTestCase;
import com.seleritycorp.common.base.test.SettableConfig;
import com.seleritycorp.common.base.test.SettableStaticClock;

public class RawAuthenticatedCoreServiceClientTest extends InjectingTestCase {
  SettableConfig config;
  SettableStaticClock clock;
  RawCoreServiceClient rawClient;
  AuthenticationClient authenticationClient;

  @Before
  public void setUp() throws IOException {
    clock = getClock();

    config = new SettableConfig();
    config.set("CoreServices.user", "userFoo");
    config.set("CoreServices.password", "passwordFoo");
    config.set("CoreServices.tokenTimeout", "5");
    config.set("CoreServices.tokenTimeoutUnite", "SECONDS");

    rawClient = createMock(RawCoreServiceClient.class);
    authenticationClient = createMock(AuthenticationClient.class);
  }

  @Test
  public void testAuthenticatedCallSingleCall() throws Exception {
    JsonObject params = new JsonObject();
    params.addProperty("bar", "baz");

    JsonElement expected = new JsonObject();

    expect(authenticationClient.getAuthThoken()).andReturn("quux");

    Capture<String> methodCapture = newCapture();
    Capture<JsonElement> paramCapture = newCapture();
    Capture<String> tokenCapture = newCapture();
    expect(rawClient.call(capture(methodCapture), capture(paramCapture), capture(tokenCapture),
        anyInt())).andReturn(expected);

    replayAll();

    RawAuthenticatedCoreServiceClient client = createClient();

    JsonElement actual = client.authenticatedCall("foo", params);

    verifyAll();

    assertThat(methodCapture.getValue()).isEqualTo("foo");
    assertThat(paramCapture.getValue()).isEqualTo(params);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testAuthenticatedCallTokenReuseOkSameTime() throws Exception {
    JsonObject params = new JsonObject();
    params.addProperty("bar", "baz");

    JsonElement expected = new JsonObject();

    expect(authenticationClient.getAuthThoken()).andReturn("quux").once();

    Capture<String> methodCapture1 = newCapture();
    Capture<JsonElement> paramCapture1 = newCapture();
    Capture<String> tokenCapture1 = newCapture();
    expect(rawClient.call(capture(methodCapture1), capture(paramCapture1), capture(tokenCapture1),
        anyInt())).andReturn(expected);

    Capture<String> methodCapture2 = newCapture();
    Capture<JsonElement> paramCapture2 = newCapture();
    Capture<String> tokenCapture2 = newCapture();
    expect(rawClient.call(capture(methodCapture2), capture(paramCapture2), capture(tokenCapture2),
        anyInt())).andReturn(expected);

    replayAll();

    RawAuthenticatedCoreServiceClient client = createClient();

    JsonElement actual1 = client.authenticatedCall("foo1", params);
    JsonElement actual2 = client.authenticatedCall("foo2", params);

    verifyAll();

    assertThat(methodCapture1.getValue()).isEqualTo("foo1");
    assertThat(paramCapture1.getValue()).isEqualTo(params);
    assertThat(actual1).isEqualTo(expected);

    assertThat(methodCapture2.getValue()).isEqualTo("foo2");
    assertThat(paramCapture2.getValue()).isEqualTo(params);
    assertThat(actual2).isEqualTo(expected);
  }

  @Test
  public void testAuthenticatedCallTokenReuseOkTimeIncrease() throws Exception {
    JsonObject params = new JsonObject();
    params.addProperty("bar", "baz");

    JsonElement expected = new JsonObject();

    expect(authenticationClient.getAuthThoken()).andReturn("quux").once();

    Capture<String> methodCapture1 = newCapture();
    Capture<JsonElement> paramCapture1 = newCapture();
    Capture<String> tokenCapture1 = newCapture();
    expect(rawClient.call(capture(methodCapture1), capture(paramCapture1), capture(tokenCapture1),
        anyInt())).andReturn(expected);

    Capture<String> methodCapture2 = newCapture();
    Capture<JsonElement> paramCapture2 = newCapture();
    Capture<String> tokenCapture2 = newCapture();
    expect(rawClient.call(capture(methodCapture2), capture(paramCapture2), capture(tokenCapture2),
        anyInt())).andReturn(expected);

    replayAll();

    RawAuthenticatedCoreServiceClient client = createClient();

    JsonElement actual1 = client.authenticatedCall("foo1", params);

    clock.advanceMillis(4500);

    JsonElement actual2 = client.authenticatedCall("foo2", params);

    verifyAll();

    assertThat(methodCapture1.getValue()).isEqualTo("foo1");
    assertThat(paramCapture1.getValue()).isEqualTo(params);
    assertThat(actual1).isEqualTo(expected);

    assertThat(methodCapture2.getValue()).isEqualTo("foo2");
    assertThat(paramCapture2.getValue()).isEqualTo(params);
    assertThat(actual2).isEqualTo(expected);
  }

  @Test
  public void testAuthenticatedCallTokenReuseTokenTimeout() throws Exception {
    JsonObject params = new JsonObject();
    params.addProperty("bar", "baz");

    JsonElement expected = new JsonObject();

    expect(authenticationClient.getAuthThoken()).andReturn("quux").times(2);

    Capture<String> methodCapture1 = newCapture();
    Capture<JsonElement> paramCapture1 = newCapture();
    Capture<String> tokenCapture1 = newCapture();
    expect(rawClient.call(capture(methodCapture1), capture(paramCapture1), capture(tokenCapture1),
        anyInt())).andReturn(expected);

    Capture<String> methodCapture2 = newCapture();
    Capture<JsonElement> paramCapture2 = newCapture();
    Capture<String> tokenCapture2 = newCapture();
    expect(rawClient.call(capture(methodCapture2), capture(paramCapture2), capture(tokenCapture2),
        anyInt())).andReturn(expected);

    replayAll();

    RawAuthenticatedCoreServiceClient client = createClient();

    JsonElement actual1 = client.authenticatedCall("foo1", params);

    clock.advanceMillis(6000);

    JsonElement actual2 = client.authenticatedCall("foo2", params);

    verifyAll();

    assertThat(methodCapture1.getValue()).isEqualTo("foo1");
    assertThat(paramCapture1.getValue()).isEqualTo(params);
    assertThat(actual1).isEqualTo(expected);

    assertThat(methodCapture2.getValue()).isEqualTo("foo2");
    assertThat(paramCapture2.getValue()).isEqualTo(params);
    assertThat(actual2).isEqualTo(expected);
  }

  @Test
  public void testAuthenticatedCallTokenReuseFailure() throws Exception {
    JsonObject params = new JsonObject();
    params.addProperty("bar", "baz");

    JsonElement expected = new JsonObject();

    expect(authenticationClient.getAuthThoken()).andReturn("quux").times(2);

    CallErrorException thrownE = new CallErrorException("catch me");
    Capture<String> methodCapture1 = newCapture();
    Capture<JsonElement> paramCapture1 = newCapture();
    Capture<String> tokenCapture1 = newCapture();
    expect(rawClient.call(capture(methodCapture1), capture(paramCapture1), capture(tokenCapture1),
        anyInt())).andThrow(thrownE);

    Capture<String> methodCapture2 = newCapture();
    Capture<JsonElement> paramCapture2 = newCapture();
    Capture<String> tokenCapture2 = newCapture();
    expect(rawClient.call(capture(methodCapture2), capture(paramCapture2), capture(tokenCapture2),
        anyInt())).andReturn(expected);

    replayAll();

    RawAuthenticatedCoreServiceClient client = createClient();

    try {
      client.authenticatedCall("foo1", params);
      failBecauseExceptionWasNotThrown(CallErrorException.class);
    } catch (CallErrorException e) {
      assertThat(e).hasMessageContaining("catch me");
    }

    JsonElement actual2 = client.authenticatedCall("foo2", params);

    verifyAll();

    assertThat(methodCapture1.getValue()).isEqualTo("foo1");
    assertThat(paramCapture1.getValue()).isEqualTo(params);

    assertThat(methodCapture2.getValue()).isEqualTo("foo2");
    assertThat(paramCapture2.getValue()).isEqualTo(params);
    assertThat(actual2).isEqualTo(expected);
  }

  private RawAuthenticatedCoreServiceClient createClient() {
    return new RawAuthenticatedCoreServiceClient(config, rawClient, authenticationClient, clock);
  }
}
