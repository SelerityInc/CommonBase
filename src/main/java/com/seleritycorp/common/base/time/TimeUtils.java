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

package com.seleritycorp.common.base.time;

public interface TimeUtils {
  /**
   * Formats the current time with nanosecond precision.
   * 
   * @return the current time as formatted String including nanoseconds.
   */
  public String formatTimeNanos();

  /**
   * Sleep for the given number of milliseconds
   *
   * <p>If the sleep is interrupted, the method returns early, but does not throw an
   * InterruptedException, but returns it. That way, we avoid a chehcked exceptions,
   * while calling code can still get the raw exception.
   * 
   * @param millis Number of milliseconds to sleep.
   * @return the InterruptedExcetpion, if the sleep got interrupted. null, if the sleep did
   *         not get interrupted.
   */
  InterruptedException sleepForMillis(long millis);
}
