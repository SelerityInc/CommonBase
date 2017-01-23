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
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.seleritycorp.common.base.coreservices.CallErrorException;
import com.seleritycorp.common.base.test.SettableConfig;

public class AuthenticationClientTest extends EasyMockSupport {
  SettableConfig config;
  RawCoreServiceClient rawClient;

  @Before
  public void setUp() throws IOException {
    config = new SettableConfig();
    config.set("CoreServices.user", "userFoo");
    config.set("CoreServices.password", "passwordFoo");

    rawClient = createMock(RawCoreServiceClient.class);
  }

  @Test
  public void testGetAuthTokenOk() throws IOException, CallErrorException {
    Capture<String> methodCapture = newCapture();
    Capture<JsonElement> paramCapture = newCapture();

    JsonObject ret = new JsonObject();
    ret.addProperty("id", "bar");

    expect(rawClient.call(capture(methodCapture), capture(paramCapture), anyInt())).andReturn(ret);

    replayAll();

    AuthenticationClient client = createAuthenticationClient();

    String token = client.getAuthThoken();

    verifyAll();

    assertThat(token).isEqualTo("bar");
    assertThat(methodCapture.getValue()).isEqualTo("AuthenticationHandler.authenticate");
    JsonObject expectedParams = new JsonObject();
    expectedParams.addProperty("user", "userFoo");
    expectedParams.addProperty("password", "passwordFoo");
    assertThat(paramCapture.getValue()).isEqualTo(expectedParams);
  }

  @Test
  public void testGetAuthTokenFaultyResponse() throws IOException, CallErrorException {
    Capture<String> methodCapture = newCapture();
    Capture<JsonElement> paramCapture = newCapture();

    JsonObject ret = new JsonObject();

    expect(rawClient.call(capture(methodCapture), capture(paramCapture), anyInt())).andReturn(ret);

    replayAll();

    AuthenticationClient client = createAuthenticationClient();

    try {
      client.getAuthThoken();
      failBecauseExceptionWasNotThrown(CallErrorException.class);
    } catch (CallErrorException e) {
      assertThat(e).hasMessageContaining(" id ");
    }

    verifyAll();

    assertThat(methodCapture.getValue()).isEqualTo("AuthenticationHandler.authenticate");
    JsonObject expectedParams = new JsonObject();
    expectedParams.addProperty("user", "userFoo");
    expectedParams.addProperty("password", "passwordFoo");
    assertThat(paramCapture.getValue()).isEqualTo(expectedParams);
  }

  private AuthenticationClient createAuthenticationClient() {
    return new AuthenticationClient(config, rawClient);
  }
}
