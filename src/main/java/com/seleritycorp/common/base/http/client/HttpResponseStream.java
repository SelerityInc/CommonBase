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

import com.google.inject.assistedinject.Assisted;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

public class HttpResponseStream {
  interface Factory {
    /**
    * Create a {@link HttpResponseStream} from a {@link org.apache.http.HttpResponse} response.
    *
    * @param response The HttpClient response object.
    * @throws HttpException if reading the response failed.
    */
    HttpResponseStream create(org.apache.http.HttpResponse response);
  }

  private final int statusCode;
  private final InputStream stream;

  /**
  * Create a HttpResponse from a HttpClient response.
  *
  * @param response The HttpClient response to create a HttpResponse from.
  * @throws HttpException if reading the response failed.
  */
  @Inject
  public HttpResponseStream(@Assisted org.apache.http.HttpResponse response) throws HttpException {
    this.statusCode = response.getStatusLine().getStatusCode();
    try {
      if (response.getEntity() != null && response.getEntity().getContent() != null) {
        stream = response.getEntity().getContent();
      } else {
        stream = null;
      }
    } catch (IOException e) {
      throw new HttpException("Failed to parse response.", e);
    }
  }

  /**
   * Streams the response body. The caller should consume the content fully from the stream and
   * close it. Reading it multiple times or using it in multiple threads will yield undesired
   * results.
   *
   * @return Response stream
   */
  public InputStream getBodyAsStream() {
    return stream;
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

