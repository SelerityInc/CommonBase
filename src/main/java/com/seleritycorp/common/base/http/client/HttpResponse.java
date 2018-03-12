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

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.inject.assistedinject.Assisted;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

public class HttpResponse {
  interface Factory {
    /**
     * Create a HttpResponse from a HttpClient response
     * 
     * @param response The HttpClient response to create a HttpResponse from.
     * @throws HttpException if reading the response failed. 
     */
    HttpResponse create(org.apache.http.HttpResponse response);
  }

  private final int statusCode;
  private final String body;

  /**
   * Create a HttpResponse from a HttpClient response
   * 
   * @param response The HttpClient response to create a HttpResponse from.
   * @throws HttpException if reading the response failed. 
   */
  @Inject
  public HttpResponse(@Assisted org.apache.http.HttpResponse response) throws HttpException {
    this.statusCode = response.getStatusLine().getStatusCode();
    try {
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        this.body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      } else {
        this.body = "";
      }
    } catch (ParseException | IOException e) {
      throw new HttpException("Failed to parse response.", e);
    }
  }

  /**
   * Gets the response's body for this response as plain string.
   *
   * <p>It is safe to call this method multiple times.
   *
   * @return the response's body.
   */
  public String getBody() {
    return body;
  }

  /**
   * Gets the response's body for this response as Gson JsonObject.
   *
   * <p>It is safe to call this method multiple times.
   * 
   * <p>Exceptions from the json parser are wrapped in {@link HttpException}.
   *
   * @return the response's body as JsonObject
   * @throws HttpException if getting the body, or parsing it as JsonObject fails.
   */
  public JsonObject getBodyAsJsonObject() throws HttpException {
    final JsonParser parser = new JsonParser();
    final JsonObject ret;
    try {
      ret = parser.parse(getBody()).getAsJsonObject();
    } catch (JsonParseException e) {
      throw new HttpException("Failed to parse server response as JSON", e);
    } catch (IllegalStateException e) {
      throw new HttpException("Parsed entity is not a JsonObject", e);
    }
    return ret;
  }

  /**
   * Gets the Http status code for this response.
   *
   * <p>To avoid comparing this value to literal ints, {@link HttpStatus} provides for predefined,
   * descriptive constants.
   *
   * @return the Http code for this response.
   */
  public int getStatusCode() {
    return statusCode;
  }
}
