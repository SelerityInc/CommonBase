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

package com.seleritycorp.common.base.config;

import javax.inject.Provider;

/**
 * Provider for a Config instance that wraps the OS' environment variables.
 */
public class EnvironmentConfigProvider implements Provider<Config> {
  @Override
  public Config get() {
    ConfigBase base = new EnvironmentConfigBase();
    ConfigImpl config = new ConfigImpl();
    config.setParent(base);
    return config;
  }
}
