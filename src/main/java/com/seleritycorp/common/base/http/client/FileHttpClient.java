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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Singleton;

// For deprecated classes, we're using fully qualified class names instead of importing them, as
// javac 1.8.0_112 complains about importing deprecated classes, even if this file's class has a
// @SuppressWarnings("deprecation").
/**
 * HttpClient that handles local files.
 */
@SuppressWarnings("deprecation")
@Singleton
public class FileHttpClient extends CloseableHttpClient {
  private org.apache.http.params.HttpParams httpParams;
  @SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC",
      justification = "connectionManager is thread safe. We use synchronization only to avoid "
          + "loading it twice")
  private org.apache.http.conn.ClientConnectionManager connectionManager;
  private ProtocolVersion protocolVersion;

  /**
   * Creates a Http client against the local file system.
   */
  @Inject
  public FileHttpClient() {
    httpParams = null;
    connectionManager = null;
    protocolVersion = new ProtocolVersion("FILE", 0, 1);
  }

  @Override
  @SuppressFBWarnings(value = "DC_DOUBLECHECK",
      justification = "Outer check is quick and unsynchronized. Inner check is synchronized")
  public synchronized org.apache.http.params.HttpParams getParams() {
    if (httpParams == null) {
      synchronized (this) {
        if (httpParams == null) {
          httpParams = new org.apache.http.params.SyncBasicHttpParams();
        }
      }
    }
    return httpParams;
  }

  @Override
  @SuppressFBWarnings(value = "DC_DOUBLECHECK",
      justification = "Outer check is quick and unsynchronized. Inner check is synchronized")
  public org.apache.http.conn.ClientConnectionManager getConnectionManager() {
    if (connectionManager == null) {
      synchronized (this) {
        if (connectionManager == null) {
          connectionManager = new org.apache.http.impl.conn.BasicClientConnectionManager();
        }
      }
    }
    return connectionManager;
  }

  @Override
  public void close() throws IOException {
    synchronized (this) {
      if (connectionManager != null) {
        connectionManager.shutdown();
      }
    }
  }

  @Override
  public CloseableHttpResponse execute(final HttpUriRequest request, final HttpContext context)
      throws IOException, ClientProtocolException {
    return doExecute(null, request, context);
  }

  @Override
  public <T> T execute(final HttpUriRequest request,
      final ResponseHandler<? extends T> responseHandler, final HttpContext context)
          throws IOException, ClientProtocolException {
    return execute(null, request, responseHandler, context);
  }

  @Override
  protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request,
      HttpContext context) throws IOException, ClientProtocolException {
    final StatusLine statusLine;
    final FileHttpClientResponse response;
    byte[] content = null;

    String uri = request.getRequestLine().getUri();
    if (uri.startsWith("file://")) {
      File file = new File(URI.create(uri));
      uri = file.getAbsolutePath();
    }
    Path path = Paths.get(uri);

    if (request instanceof HttpGet) {
      if (Files.exists(path)) {
        if (Files.isReadable(path)) {
          if (!Files.isDirectory(path)) {
            content = Files.readAllBytes(path);
            statusLine = createStatusLine(HttpStatus.SC_OK, "OK");
          } else {
            // Trying to GET a directory
            statusLine = createStatusLine(HttpStatus.SC_BAD_REQUEST, "Bad Request");
          }
        } else {
          // User does not have sufficient privilege to access the file
          statusLine = createStatusLine(HttpStatus.SC_FORBIDDEN, "Forbidden");
        }
      } else {
        // File does not exist.
        statusLine = createStatusLine(HttpStatus.SC_NOT_FOUND, "Not Found");
      }
    } else {
      // Request is not a HttpGet request. We currently only support GET, so we flag that the
      // client sent an illegal request.
      statusLine = createStatusLine(HttpStatus.SC_BAD_REQUEST, "Bad Request");
    }

    response = new FileHttpClientResponse(statusLine);
    if (content != null) {
      response.setEntity(new ByteArrayEntity(content));
    }

    return response;
  }

  private StatusLine createStatusLine(int statusCode, String reason) {
    return new BasicStatusLine(protocolVersion, statusCode, reason);
  }

  /**
   * Basic Http Response that is closeable.
   */
  private static class FileHttpClientResponse extends BasicHttpResponse
      implements CloseableHttpResponse {
    public FileHttpClientResponse(StatusLine statusline) {
      super(statusline);
    }

    @Override
    public void close() throws IOException {
      // Nothing to do;
    }
  }
}
