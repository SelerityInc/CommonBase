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

package com.seleritycorp.common.base.http.server;

import com.seleritycorp.common.base.inject.FactoryModule;

/**
 * Configures injection for http server classes.
 */
public class HttpServerModule extends FactoryModule {
  @Override
  protected void configure() {
    installFactory(HttpRequest.Factory.class);
    bind(AbstractHttpHandler.class).annotatedWith(BaseHttpHandler.class).to(
        CommonHttpHandler.class);
  }
}
