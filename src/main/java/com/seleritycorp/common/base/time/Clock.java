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

package com.seleritycorp.common.base.time;

public interface Clock {
  /**
   * Returns the number of seconds since epoch.
   * 
   * @return the number of seconds since epoch
   */
  public long getSecondsEpoch();

  /**
   * Returns the number of milliseconds since epoch.
   * 
   * @return the number of milliseconds since epoch
   */
  public long getMillisEpoch();

  /**
   * Returns the number of nanoseconds since epoch.
   * 
   * @return the number of nanoseconds since epoch
   */
  public long getNanosEpoch();
}
