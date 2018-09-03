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

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.easymock.EasyMockSupport;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpResponseStreamTest extends EasyMockSupport {
  @Test
  public void testGetBodyAsciiChars() throws Exception {
    HttpResponseStream response = createHttpResponseStream(200, "bodyFoo");
    String actual = IOUtils.toString(response.getBodyAsStream());
    
    assertThat(actual).isEqualTo("bodyFoo");
  }


  @Test
  public void testGetBodyUtf8() throws Exception {
    HttpResponseStream response = createHttpResponseStream(200, "äüß!-–—");
    String actual = IOUtils.toString(response.getBodyAsStream());
    
    assertThat(actual).isEqualTo("äüß!-–—");
  }
  
  @Test
  public void testGetBodyIso8859_1() throws Exception {
    byte[] iso8859_1 = new byte[] {(byte)0xe4, (byte)0xfc, (byte)0xdf, 0x21};
    HttpResponseStream response = createHttpResponseStream(200, iso8859_1, StandardCharsets.ISO_8859_1);
    String actual = IOUtils.toString(response.getBodyAsStream(), StandardCharsets.ISO_8859_1);
    
    assertThat(actual).isEqualTo("äüß!");
  }
  
  @Test
  public void testGetBodyNull() throws Exception {
    HttpResponseStream response = createHttpResponseStream(200, null, null);
    assertThat(response.getBodyAsStream()).isEqualTo(null);
  }

  @Test
  public void testGetStatusCodeInt() throws Exception {
    HttpResponseStream response = createHttpResponseStream(123, "");
    int actual = response.getStatusCode();

    assertThat(actual).isEqualTo(123);
  }

  private HttpResponseStream createHttpResponseStream(int status,String body) throws Exception {
    return createHttpResponseStream(status, body.getBytes(), StandardCharsets.UTF_8);
  }

  private HttpResponseStream createHttpResponseStream(int status, byte[] body, Charset charset) throws Exception {
    StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, status, "ReasonFoo");
    org.apache.http.HttpResponse backendResponse = new BasicHttpResponse(statusLine);
    if (body != null) {
      backendResponse.setEntity(new ByteArrayEntity(body, ContentType.create("foo", charset)));
    } else {
      backendResponse.setEntity(null);
    }
    return new HttpResponseStream(backendResponse);
  }
}
