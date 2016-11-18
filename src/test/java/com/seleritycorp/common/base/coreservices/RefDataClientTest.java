/*
 * Copyright (C) 2016 Selerity, Inc. (support@seleritycorp.com)
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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;

import java.io.IOException;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.seleritycorp.common.base.coreservices.CallErrorException;
import com.seleritycorp.common.base.test.SettableConfig;

public class RefDataClientTest extends EasyMockSupport {
  SettableConfig config;
  RawAuthenticatedCoreServiceClient rawClient;

  @Before
  public void setUp() throws IOException {
    config = new SettableConfig();
    config.set("CoreServices.user", "userFoo");
    config.set("CoreServices.password", "passwordFoo");

    rawClient = createMock(RawAuthenticatedCoreServiceClient.class);
  }

  @Test
  public void testGetAuthTokenOk() throws IOException, CallErrorException {
    Capture<String> methodCapture = newCapture();
    Capture<JsonElement> paramCapture = newCapture();

    JsonObject expected = new JsonObject();
    expected.addProperty("bar", "baz");

    expect(rawClient.authenticatedCall(capture(methodCapture), capture(paramCapture)))
        .andReturn(expected);

    replayAll();

    RefDataClient client = createClient();

    JsonElement actual = client.getIdentifiersForEnumType("foo");

    verifyAll();

    assertThat(methodCapture.getValue()).isEqualTo("RefDataHandler.getIdentifiersForEnumType");
    JsonArray expectedParams = new JsonArray();
    expectedParams.add("foo");
    assertThat(paramCapture.getValue()).isEqualTo(expectedParams);

    assertThat(actual).isEqualTo(expected);
  }

  private RefDataClient createClient() {
    return new RefDataClient(rawClient);
  }
}
