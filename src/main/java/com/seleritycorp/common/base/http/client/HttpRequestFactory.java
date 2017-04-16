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

import com.seleritycorp.common.base.meta.MetaDataFormatter;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory for Http requests.
 */
@Singleton
public class HttpRequestFactory {
  private final HttpRequest.Factory requestFactory;
  private final String userAgent;

  @Inject
  HttpRequestFactory(HttpRequest.Factory requestFactory, MetaDataFormatter metaDataFormatter) {
    this.requestFactory = requestFactory;
    this.userAgent = metaDataFormatter.getUserAgent();
  }

  /**
   * Creates a standard GET request.
   *
   * @param url The URL to request
   * @return The created request
   */
  public HttpRequest create(String url) {
    HttpRequest request = requestFactory.create(url);
    request.setUserAgent(userAgent);
    return request;
  }
}
