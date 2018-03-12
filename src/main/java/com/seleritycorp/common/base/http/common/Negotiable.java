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

package com.seleritycorp.common.base.http.common;

import com.seleritycorp.common.base.http.server.Negotiator;

/**
 * Interface for checking if objects meets another objects specifications.
 * 
 * @param <T> class of specification objects.
 *
 * @see Negotiator
 */
public interface Negotiable<T> {
  /**
   * Checks if this objects meets the given specification.
   * 
   * @param specification The specification to check against.
   * @return True, if this object meets the specification. False otherwise.
   */
  public boolean meets(T specification);
}
