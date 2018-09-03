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
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.io.StringWriter;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.seleritycorp.common.base.test.SettableConfig;

public class RefDataClientTest extends EasyMockSupport {
  SettableConfig config;
  RawAuthenticatedCoreServiceClient rawClient;

  @Before
  public void setUp() throws IOException {
    config = new SettableConfig();
    config.set("RefDataClient.timeout", "42");

    rawClient = createMock(RawAuthenticatedCoreServiceClient.class);
  }

  @Test
  public void testGetAuthTokenOk() throws Exception {
    Capture<String> methodCapture = newCapture();
    Capture<JsonElement> paramCapture = newCapture();

    JsonObject expected = new JsonObject();
    expected.addProperty("bar", "baz");

    expect(rawClient.authenticatedCall(capture(methodCapture), capture(paramCapture), eq(42000)))
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

  @Test
  public void testGetAuthTokenOkWithWriter() throws Exception {
    StringWriter output = new StringWriter();
    JsonWriter writer = new JsonWriter(output);
    Capture<String> methodCapture = newCapture();
    Capture<JsonElement> paramCapture = newCapture();
    Capture<JsonWriter> writerCapture = newCapture();

    JsonObject expected = new JsonObject();
    expected.addProperty("bar", "baz");

    rawClient.authenticatedCall(capture(methodCapture), capture(paramCapture), eq(42000), capture(writerCapture));
    expectLastCall().andAnswer(new IAnswer<Object>() {
      @Override
      public Object answer() throws Throwable {
        ((JsonWriter) EasyMock.getCurrentArguments()[3]).beginObject().name("bar").value("baz").endObject();
        return null;
      }
    });

    replayAll();

    RefDataClient client = createClient();

    client.getIdentifiersForEnumType("foo", writer);

    verifyAll();

    JsonElement actual = new JsonParser().parse(output.toString()).getAsJsonObject();
    assertThat(methodCapture.getValue()).isEqualTo("RefDataHandler.getIdentifiersForEnumType");
    JsonArray expectedParams = new JsonArray();
    expectedParams.add("foo");
    assertThat(paramCapture.getValue()).isEqualTo(expectedParams);

    assertThat(actual).isEqualTo(expected);
  }

  private RefDataClient createClient() {
    return new RefDataClient(rawClient, config);
  }
}
