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

package com.seleritycorp.common.base.http.server;

import com.google.inject.Injector;
import com.google.inject.Provides;

import com.seleritycorp.common.base.inject.FactoryModule;
import com.seleritycorp.common.base.inject.InjectorFactory;

/**
 * Configures injection for http server classes.
 */
public class HttpServerModule extends FactoryModule {
  @Override
  protected void configure() {
    installFactory(HttpRequest.Factory.class);
    // We'd like to bind CommonHttpHandler to the @BaseHttpHandler AbstractHttpHandler directly.
    // But we need to pass another AbstractHttpHandler for constructing. This other
    // AbstractHttpHandler only gets configured in a later module (or maybe not at all). We could
    // not find a simpler working solution than adding a factory to CommonHttpHandler and
    // injecting the other AbstractHttpHandler manually at runtime.
    // But that at least works reliably.
    installFactory(CommonHttpHandler.Factory.class);
  }

  @Provides
  @BaseHttpHandler
  AbstractHttpHandler provideBaseHttpHandler(CommonHttpHandler.Factory factory) {
    Injector injector = InjectorFactory.getInjector();

    AbstractHttpHandler delegate = injector.getInstance(AbstractHttpHandler.class);
    return factory.create(delegate);
  }
}
