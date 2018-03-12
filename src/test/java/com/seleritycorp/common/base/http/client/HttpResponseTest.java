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

package com.seleritycorp.common.base.http.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.seleritycorp.common.base.http.client.HttpResponse;

public class HttpResponseTest extends EasyMockSupport {
  @Test
  public void testGetBodyAsciiChars() throws Exception {
    HttpResponse response = createHttpResponse(200, "bodyFoo");
    String actual = response.getBody();
    
    assertThat(actual).isEqualTo("bodyFoo");
  }

  @Test
  public void testGetBodyUtf8() throws Exception {
    HttpResponse response = createHttpResponse(200, "äüß!-–—");
    String actual = response.getBody();
    
    assertThat(actual).isEqualTo("äüß!-–—");
  }
  
  @Test
  public void testGetBodyIso8859_1() throws Exception {
    byte[] iso8859_1 = new byte[] {(byte)0xe4, (byte)0xfc, (byte)0xdf, 0x21};
    HttpResponse response = createHttpResponse(200, iso8859_1, StandardCharsets.ISO_8859_1);
    String actual = response.getBody();
    
    assertThat(actual).isEqualTo("äüß!");
  }
  
  @Test
  public void testGetBodyNull() throws Exception {
    HttpResponse response = createHttpResponse(200, null, null);
    String actual = response.getBody();
    assertThat(actual).isEqualTo("");
  }

  public void testGetBodyAsJsonObjectOk() throws Exception {
    HttpResponse response = createHttpResponse(200, "{\"foo\": 4711}");

    JsonObject actual = response.getBodyAsJsonObject();

    JsonObject expected = new JsonObject();
    expected.addProperty("foo", 4711);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testGetBodyAsJsonObjectIsJsonArray() throws Exception {
    HttpResponse response = createHttpResponse(200, "[1,2]");

    try {
      response.getBodyAsJsonObject();
      failBecauseExceptionWasNotThrown(HttpException.class);
    } catch (HttpException e) {
      assertThat(e.getCause()).isInstanceOf(IllegalStateException.class);
    }
  }

  @Test
  public void testGetBodyAsJsonObjectInvalidJson() throws Exception {
    HttpResponse response = createHttpResponse(200, "{foo: bar baz}");

    try {
      response.getBodyAsJsonObject();
      failBecauseExceptionWasNotThrown(HttpException.class);
    } catch (HttpException e) {
      assertThat(e.getCause()).isInstanceOf(JsonParseException.class);
    }

    verifyAll();
  }

  @Test
  public void testGetStatusCodeInt() throws Exception {
    HttpResponse response = createHttpResponse(123, "");
    int actual = response.getStatusCode();

    assertThat(actual).isEqualTo(123);
  }
  
  private HttpResponse createHttpResponse(int status, String body) throws Exception {
    return createHttpResponse(status, body.getBytes(), StandardCharsets.UTF_8);
  }

  private HttpResponse createHttpResponse(int status, byte[] body, Charset charset) throws Exception {
    StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, status, "ReasonFoo");
    org.apache.http.HttpResponse backendResponse = new BasicHttpResponse(statusLine);
    if (body != null) {
      backendResponse.setEntity(new ByteArrayEntity(body, ContentType.create("foo", charset)));
    }
    return new HttpResponse(backendResponse);
  }
}
