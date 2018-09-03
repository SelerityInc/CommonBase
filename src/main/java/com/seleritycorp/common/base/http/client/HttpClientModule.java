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

import com.seleritycorp.common.base.inject.FactoryModule;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Module to set up dependency injection for Http client tooling.
 */
public class HttpClientModule extends FactoryModule {
  @Override
  protected void configure() {
    installFactory(HttpRequest.Factory.class);
    installFactory(HttpResponse.Factory.class);
    installFactory(HttpResponseStream.Factory.class);

    bind(HttpClient.class).toInstance(HttpClients.createSystem());
  }
}
