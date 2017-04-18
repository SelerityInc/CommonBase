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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.seleritycorp.common.base.config.ApplicationConfig;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.config.ConfigUtils;
import com.seleritycorp.common.base.http.client.HttpException;
import com.seleritycorp.common.base.http.client.HttpRequestFactory;
import com.seleritycorp.common.base.meta.MetaDataFormatter;
import com.seleritycorp.common.base.uuid.UuidGenerator;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RawCoreServiceClient {
  private final UuidGenerator uuidGenerator;
  private final String apiUrl;
  private final int timeoutMillis;
  private final String user;
  private final String client;
  private final HttpRequestFactory requestFactory;

  /**
   * Creates a raw client for CoreService calls.
   * 
   * @param appConfig The application config to use.
   * @param uuidGenerator Used for request ids.
   * @param metaDataFormatter Formatter for the used user agent.
   * @param requestFactory Factory for Http requests.
   */
  @Inject
  public RawCoreServiceClient(@ApplicationConfig Config appConfig, UuidGenerator uuidGenerator,
      MetaDataFormatter metaDataFormatter, HttpRequestFactory requestFactory) {
    this.uuidGenerator = uuidGenerator;
    Config config = ConfigUtils.subconfig(appConfig, "CoreServices");
    this.apiUrl = config.get("url");
    this.user = config.get("user");

    this.timeoutMillis = (int) config.getDurationMillis("timeout", 300, TimeUnit.SECONDS);

    this.client = metaDataFormatter.getUserAgent();
    this.requestFactory = requestFactory;
  }

  JsonElement call(String method, JsonElement params, String token, int timeoutMillis)
      throws HttpException, CallErrorException {
    JsonObject header = new JsonObject();
    header.addProperty("user", user);
    if (token != null) {
      header.addProperty("token", token);
    }
    header.addProperty("client", client);

    JsonObject request = new JsonObject();
    request.addProperty("id", uuidGenerator.generate().toString());
    request.addProperty("method", method);
    request.add("params", params);
    request.add("header", header);

    final int effectiveTimeoutMillis = (timeoutMillis > 0) ? timeoutMillis : this.timeoutMillis;

    JsonObject responseObj = requestFactory.createPostJson(apiUrl, request)
          .setReadTimeoutMillis(effectiveTimeoutMillis)
          .execute()
          .getBodyAsJsonObject();

    final JsonElement error = responseObj.get("error");
    if (error != null && !error.isJsonNull()) {
      throw new CallErrorException("Error: " + error.toString());
    }
    return responseObj.get("result");
  }
}
