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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import com.seleritycorp.common.base.config.ApplicationConfig;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.config.ConfigUtils;
import com.seleritycorp.common.base.http.client.HttpException;
import com.seleritycorp.common.base.http.client.HttpRequestFactory;
import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;
import com.seleritycorp.common.base.meta.MetaDataFormatter;
import com.seleritycorp.common.base.uuid.UuidGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Raw client for calling calling CoreServices methods.
 */
@Singleton
public class RawCoreServiceClient {
  private static final Log log = LogFactory.getLog(RawCoreServiceClient.class);

  private final UuidGenerator uuidGenerator;
  private final String apiUrl;
  private final int timeoutMillis;
  private final String user;
  private final String client;
  private final HttpRequestFactory requestFactory;
  private final GsonBuilder gsonBuilder;
  private static final int BUFFER_SIZE = 8 * 1024 * 1024; // 8MB

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
    this.gsonBuilder = new GsonBuilder();
  }

  /**
   * Calls a CoreServices method.
   * 
   * @param method The CoreServices method to call
   * @param params The parameters to pass to the CoreServices call
   * @param token The authentication token. Set to null to perform an unauthenticated call.
   * @param timeoutMillis The read timeout for new data on the connection.
   * @return The call's result JsonElement
   * @throws HttpException if network errors or parsing errors occured on the client.
   * @throws CallErrorException if the server responded with an error.
   */
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

    log.debug("Calling method " + method + " (user: " + user + ")");

    JsonObject responseObj = requestFactory.createPostJson(apiUrl, request)
          .setReadTimeoutMillis(effectiveTimeoutMillis)
          .execute()
          .getBodyAsJsonObject();

    log.debug("Method " + method + " done (user: " + user + ")");

    final JsonElement error = responseObj.get("error");
    if (error != null && !error.isJsonNull()) {
      throw new CallErrorException("Error: " + error.toString());
    }
    return responseObj.get("result");
  }

  /**
   * Calls the CoreServices API with custom JSON writer to write the result to. Uses
   * streaming APIs to handle large paylods of JSON response and write them to writer.
   *
   * @param method GET or POST
   * @param params HTTP Params to be sent
   * @param timeoutMillis Timeout for connection/socket timeout. Use -1 for the default
   *     timeout.
   * @param writer Writer to write the JSON result
   * @throws HttpException when network or other IO issues occur
   * @throws CallErrorException when server or semantics errors occur
   */
  void call(String method, JsonElement params, String token, int timeoutMillis, JsonWriter writer)
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

    log.debug("Calling method " + method + " (user: " + user + ")");

    InputStream responseStream = requestFactory.createPostJson(apiUrl, request)
             .setReadTimeoutMillis(effectiveTimeoutMillis)
             .executeAndStream()
             .getBodyAsStream();
    log.debug("Method " + method + " done (user: " + user + ")");

    if (responseStream == null) {
      log.info("Empty response while executing request to " + apiUrl);
      return;
    }

    // We're interested only in `result` and `error` properties of the response JSON
    JsonObject errorObject = null;
    try (JsonReader reader = getJsonReader(responseStream)) {
      if (reader.hasNext()) {
        if (reader.peek() == JsonToken.BEGIN_OBJECT) {
          reader.beginObject();
          while (reader.peek() != JsonToken.END_OBJECT) {
            String name = reader.nextName();
            if ("error".equals(name)) {
              if (reader.peek() == JsonToken.BEGIN_OBJECT) {
                errorObject = gsonBuilder.create().fromJson(reader, JsonObject.class);
              } else {
                reader.skipValue();
              }
            } else if ("result".equals(name)) {
              pushToWriter(reader, writer);
            } else {
              reader.skipValue();
            }
          }
          reader.endObject();
        } else {
          log.warn("Response received is not a JSON object, ignoring. Requsest:" + apiUrl);
        }
      } else {
        log.warn("Received empty response while executing the request to " + apiUrl);
      }
    } catch (IOException e) {
      throw new HttpException("Failed to execute request to '" + apiUrl + "'", e);
    }
    if (errorObject != null && !errorObject.isJsonNull()) {
      throw new CallErrorException("Error: " + errorObject.toString());
    }
  }

  JsonReader getJsonReader(InputStream stream) {
    return new JsonReader(new BufferedReader(
            new InputStreamReader(stream, StandardCharsets.UTF_8), BUFFER_SIZE));
  }

  // We just need to read only the current JSON value which cane be either an object
  // or an array or a primitive value, from the reader and write it to the writer and
  // return. If the value is an object or an array, then we use the counter, to keep
  // track of internal/nested objects or array. We increment the counter every time
  // we see a BEGIN and decrement during END.
  // TODO: How to handle invalid JSON and return gracefully?
  private void pushToWriter(JsonReader reader, JsonWriter writer) throws IOException {
    int count = 0;
    boolean isObject = reader.peek() == JsonToken.BEGIN_OBJECT;
    do {
      JsonToken jsToken = reader.peek();
      switch (jsToken) {
        case BEGIN_ARRAY:
          reader.beginArray();
          writer.beginArray();
          if (!isObject) {
            count++;
          }
          break;
        case END_ARRAY:
          reader.endArray();
          writer.endArray();
          if (!isObject) {
            count--;
          }
          break;
        case BEGIN_OBJECT:
          reader.beginObject();
          writer.beginObject();
          if (isObject) {
            count++;
          }
          break;
        case END_OBJECT:
          reader.endObject();
          writer.endObject();
          if (isObject) {
            count--;
          }
          break;
        case NAME:
          String name = reader.nextName();
          writer.name(name);
          break;
        case STRING:
          String stringValue = reader.nextString();
          writer.value(stringValue);
          break;
        case NUMBER:
          String numValue = reader.nextString();
          writer.value(new BigDecimal(numValue));
          break;
        case BOOLEAN:
          boolean boolValue = reader.nextBoolean();
          writer.value(boolValue);
          break;
        case NULL:
          reader.nextNull();
          writer.nullValue();
          break;
        case END_DOCUMENT:
          return;
        default:
          return;
      }
    } while (count != 0);
  }
}
