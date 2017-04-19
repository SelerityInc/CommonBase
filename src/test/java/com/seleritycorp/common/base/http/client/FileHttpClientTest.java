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

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.seleritycorp.common.base.test.InjectingTestCase;

public class FileHttpClientTest extends InjectingTestCase {
  @Test
  public void testExecuteGetOk() throws Exception {
    Path tmpFile = this.createTempFile();
    this.writeFile(tmpFile, "foo\nbar");
    
    replayAll();

    HttpClient httpClient = createFileHttpClient();
    HttpGet method = new HttpGet("file://" + tmpFile);
    org.apache.http.HttpResponse response = httpClient.execute(method);
    HttpEntity entity = response.getEntity();

    verifyAll();
    
    assertThat(response.getStatusLine().getProtocolVersion().getProtocol()).isEqualTo("FILE");
    assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
    assertThat(response.getStatusLine().getReasonPhrase()).isEqualTo("OK");

    assertThat(entity).isNotNull();
    assertThat(entity.getContentEncoding()).isNull();
    assertThat(entity.getContentType()).isNull();

    String body = EntityUtils.toString(entity, StandardCharsets.UTF_8);
    assertThat(body).isEqualTo("foo\nbar");
  }
  
  @Test
  public void testExecuteGetNotFound() throws Exception {
    Path tmpFile = this.createTempDirectory();
    
    replayAll();

    HttpClient httpClient = createFileHttpClient();
    HttpGet method = new HttpGet("file://" + tmpFile + "/foo");
    org.apache.http.HttpResponse response = httpClient.execute(method);
    HttpEntity entity = response.getEntity();

    verifyAll();
    
    assertThat(response.getStatusLine().getProtocolVersion().getProtocol()).isEqualTo("FILE");
    assertThat(response.getStatusLine().getStatusCode()).isEqualTo(404);
    assertThat(response.getStatusLine().getReasonPhrase()).isEqualTo("Not Found");

    assertThat(entity).isNull();
  }

  @Test
  public void testExecuteGetDirectory() throws Exception {
    Path tmpFile = this.createTempDirectory();
    
    replayAll();

    HttpClient httpClient = createFileHttpClient();
    HttpGet method = new HttpGet("file://" + tmpFile);
    org.apache.http.HttpResponse response = httpClient.execute(method);
    HttpEntity entity = response.getEntity();

    verifyAll();
    
    assertThat(response.getStatusLine().getProtocolVersion().getProtocol()).isEqualTo("FILE");
    assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    assertThat(response.getStatusLine().getReasonPhrase()).isEqualTo("Bad Request");

    assertThat(entity).isNull();
  }
  
  @Test
  public void testExecuteGetInaccessible() throws Exception {
    replayAll();

    HttpClient httpClient = createFileHttpClient();
    HttpGet method = new HttpGet("file:///root");
    org.apache.http.HttpResponse response = httpClient.execute(method);
    HttpEntity entity = response.getEntity();

    verifyAll();
    
    assertThat(response.getStatusLine().getProtocolVersion().getProtocol()).isEqualTo("FILE");
    assertThat(response.getStatusLine().getStatusCode()).isEqualTo(403);
    assertThat(response.getStatusLine().getReasonPhrase()).isEqualTo("Forbidden");

    assertThat(entity).isNull();
  }
  
  @Test
  public void testExecutePostFails() throws Exception {
    Path tmpFile = this.createTempFile();
    this.writeFile(tmpFile, "foo\nbar");
    
    replayAll();

    HttpClient httpClient = createFileHttpClient();
    HttpPost method = new HttpPost("file://" + tmpFile);
    org.apache.http.HttpResponse response = httpClient.execute(method);
    HttpEntity entity = response.getEntity();

    verifyAll();
    
    assertThat(response.getStatusLine().getProtocolVersion().getProtocol()).isEqualTo("FILE");
    assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
    assertThat(response.getStatusLine().getReasonPhrase()).isEqualTo("Bad Request");

    assertThat(entity).isNull();
  }

  private FileHttpClient createFileHttpClient() {
    return new FileHttpClient();
  }
}
