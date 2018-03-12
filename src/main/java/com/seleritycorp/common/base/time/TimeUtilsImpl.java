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

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.inject.Inject;

public class TimeUtilsImpl implements TimeUtils {
  /**
   * Date format to be used.
   * 
   * <p>As SimpleDateFormat is not thread-safe, we generate one per thread to avoid locking on it.
   */
  protected static final ThreadLocal<SimpleDateFormat> ISO8601_FORMAT_PLUS_MS =
      new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
          SimpleDateFormat initValue = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
          initValue.setTimeZone(TimeZone.getTimeZone("UTC"));
          return initValue;
        }
      };

  /**
   * When sleeping, sleep at least that long (in milliseconds).
   */
  private static final long MIN_SLEEP_MILLIS = 5;

  /**
   * The clock used for all timings.
   */
  private final Clock clock;

  @Inject
  public TimeUtilsImpl(Clock clock) {
    this.clock = clock;
  }

  @Override
  public String formatTimeNanos() {
    long nanos = clock.getNanosEpoch();
    long millis = nanos / 1000000L;

    String ret = ISO8601_FORMAT_PLUS_MS.get().format(millis);

    long microsAndNanosWoMillis = nanos % 1000000L;
    ret += Long.toString(1000000L + microsAndNanosWoMillis).substring(1);
    return ret;
  }

  @Override
  public InterruptedException sleepForMillis(long millis) {
    InterruptedException ret = null;
    long end = clock.getMillisEpoch() + millis;
    try {
      while (end >= clock.getMillisEpoch()) {
        long duration = Math.max(end - clock.getMillisEpoch(), MIN_SLEEP_MILLIS);
        Thread.sleep(duration);
      }
    } catch (InterruptedException e) {
      ret = e;
    }
    return ret;
  }
}
