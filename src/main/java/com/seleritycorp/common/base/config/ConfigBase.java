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

package com.seleritycorp.common.base.config;

/**
 * Minimalistic interface for Config sources
 * 
 * <p>If you want to provide a custom configuration source, you can implement this interface and
 * set it as parent of {@link ConfigImpl} to extend get a full-blown config object with all
 * helpers for your custom configuration source.
 */
public interface ConfigBase {
  /**
   * Gets the value of the given key as String
   * 
   * @param key key to get value for
   * @return the value at the given key. If there is no value for the given key, null is returned.
   */
  public String get(String key);
}
