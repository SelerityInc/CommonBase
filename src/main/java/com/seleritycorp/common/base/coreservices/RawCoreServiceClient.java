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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.seleritycorp.common.base.config.ApplicationConfig;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.config.ConfigUtils;
import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;
import com.seleritycorp.common.base.uuid.UuidGenerator;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RawCoreServiceClient {
  private static final Log log = LogFactory.getLog(RawCoreServiceClient.class);

  private final UuidGenerator uuidGenerator;
  private final URL apiUrl;
  private final int timeoutMillis;
  private final String user;
  private final String client;

  /**
   * Creates a raw client for CoreService calls.
   * 
   * @param appConfig The application config to use.
   * @param uuidGenerator Used for request ids.
   * @throws MalformedURLException if the CoreServices URL is malformed
   */
  @Inject
  public RawCoreServiceClient(@ApplicationConfig Config appConfig, UuidGenerator uuidGenerator)
      throws MalformedURLException {
    this.uuidGenerator = uuidGenerator;
    Config config = ConfigUtils.subconfig(appConfig, "CoreServices");
    apiUrl = new URL(config.get("url"));
    this.user = config.get("user");

    int timeout = config.getInt("timeout", 300);
    TimeUnit timeoutUnit = TimeUnit.valueOf(config.get("timeoutUnit", "SECONDS"));
    this.timeoutMillis = (int) timeoutUnit.toMillis(timeout);

    this.client = "RdsDataDownloader";
  }

  private String getRawResponse(String rawRequest, int timeoutMillis) throws IOException {
    URLConnection connection = apiUrl.openConnection();
    connection.setRequestProperty("Accept", "text/plain");
    connection.setRequestProperty("Content-type", "application/json");
    connection.setRequestProperty("User-Agent", client);
    connection.setDoOutput(true);
    connection.setReadTimeout(timeoutMillis);

    final OutputStream out = connection.getOutputStream();
    out.write(rawRequest.getBytes(StandardCharsets.UTF_8));
    out.flush();
    out.close();

    log.debug("wrote request " + rawRequest + " (timeout wanted: " + timeoutMillis + ", actual: "
        + connection.getReadTimeout() + ")");

    String response = null;
    try (final InputStream in = connection.getInputStream()) {
      response = IOUtils.toString(in, StandardCharsets.UTF_8);
    }

    return response;
  }

  public JsonElement call(String method, JsonElement params, int timeout)
      throws IOException, CallErrorException {
    return call(method, params, null, -1);
  }

  JsonElement call(String method, JsonElement params, String token, int timeoutMillis)
      throws IOException, CallErrorException {
    JsonObject header = new JsonObject();
    header.addProperty("user", user);
    if (token != null) {
      header.addProperty("token", token);
    }
    if (client != null) {
      header.addProperty("client", client);
    }

    JsonObject request = new JsonObject();
    request.addProperty("id", uuidGenerator.generate().toString());
    request.addProperty("method", method);
    request.add("params", params);
    request.add("header", header);

    final int effectiveTimeoutMillis = (timeoutMillis > 0) ? timeoutMillis : this.timeoutMillis;

    final String responseString = getRawResponse(request.toString(), effectiveTimeoutMillis);

    final JsonParser parser = new JsonParser();
    final JsonObject responseObj = parser.parse(responseString).getAsJsonObject();

    final JsonElement error = responseObj.get("error");
    if (error != null && !error.isJsonNull()) {
      throw new CallErrorException("Error: " + error.toString());
    }
    return responseObj.get("result");
  }
}
