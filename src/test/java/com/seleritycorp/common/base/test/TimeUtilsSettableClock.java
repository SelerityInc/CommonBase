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

package com.seleritycorp.common.base.test;

import static org.assertj.core.api.Assertions.fail;

import javax.inject.Inject;

import com.seleritycorp.common.base.time.ClockImpl;
import com.seleritycorp.common.base.time.TimeUtils;
import com.seleritycorp.common.base.time.TimeUtilsImpl;

public class TimeUtilsSettableClock implements TimeUtils {
  private final SettableStaticClock clock;
  private final TimeUtils wallClockTimeUtils;

  @Inject
  public TimeUtilsSettableClock(SettableStaticClock clock) {
    this.clock = clock;
    this.wallClockTimeUtils = new TimeUtilsImpl(new ClockImpl());
  }

  @Override
  public String formatTimeNanos() {
    return "Time{" + clock.getNanosEpoch() + "}";
  }

  @Override
  public InterruptedException sleepForMillis(long millis) {
    InterruptedException exception = null;
    long end = clock.getMillisEpoch() + millis;
    while (end > clock.getMillisEpoch()) {
      synchronized (clock) {
        try {
          clock.wait();
        } catch (InterruptedException e) {
          exception = e;
        }
      }
    }
    return exception;
  }

  /**
   * Wall clock wait for given number of milliseconds
   * 
   * <p>This method is syntactic sugar for tests.
   * 
   * @param millis Number of milliseconds to wait.
   */
  public void wallClockSleepForMillis(long millis) {
    InterruptedException exception;
    exception = wallClockTimeUtils.sleepForMillis(millis);
    if (exception != null) {
      fail("Interrupted sleep", exception);
    }
  }


  /**
   * Advances the clock and waits for Threads to pick up the new time
   * 
   * <p>This method gives other threads 50ms time to pick up the new time.
   * 
   * @param advanceMs Number of milliseconds to advance the clock by.
   */
  public void advanceClockSettled(long advanceMs) {
    advanceClockSettled(advanceMs, 50L);
  }

  /**
   * Advances the clock and waits for Threads to pick up the new time
   * 
   * @param advanceMs Number of milliseconds to advance the clock by.
   * @param settleMs Number of milliseconds to wait for threads to pick up the new time.
   */
  public void advanceClockSettled(long advanceMs, long settleMs) {
    clock.advanceMillis(advanceMs);
    wallClockSleepForMillis(50);
  }
}
