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

package com.seleritycorp.common.base.test;

import com.google.common.base.Ticker;
import com.seleritycorp.common.base.time.Clock;

public class SettableStaticClock extends Ticker implements Clock {
  long nanos;

  public SettableStaticClock() {
    reset();
  }

  @Override
  public long getSecondsEpoch() {
    return nanos / 1000000000L;
  }

  @Override
  public long getMillisEpoch() {
    return nanos / 1000000L;
  }

  @Override
  public long getNanosEpoch() {
    return nanos;
  }

  @Override
  public long read() {
    return getNanosEpoch();
  }

  /**
   * Resets the Clock's time to the default time.
   */
  public void reset() {
    // Per default, set to 2004-11-09T11:33:20.123456789
    setNanosEpoch(1100000000123456789L);
  }

  /**
   * Sets the Clock's time to the given nanoseconds past epoch
   * 
   * @param nanos nanoseconds after epoch to set.
   */
  public synchronized void setNanosEpoch(long nanos) {
    this.nanos = nanos;
    notifyAll();
  }

  /**
   * Advances the Clock's time by the given number of milliseconds.
   * 
   * @param millis Number of milliseconds to advance the clock by.
   */
  public void advanceMillis(long millis) {
    setNanosEpoch(nanos + millis * 1000000L);
  }
}
