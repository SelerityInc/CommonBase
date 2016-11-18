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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.test.InjectingTestCase;
import com.seleritycorp.common.base.test.SettableStaticClock;

public class TimeUtilsImplTest extends InjectingTestCase {
  private SettableStaticClock clock;
  private int delta;

  @Before
  public void setUp() {
    clock = getClock();
    delta = 2000;
  }

  @Test
  public void testFormatTimeNanosDefault() {
    TimeUtilsImpl timeUtils = new TimeUtilsImpl(clock);
    String actual = timeUtils.formatTimeNanos();
    assertThat(actual).isEqualTo("2004-11-09T11:33:20.123456789");
  }

  @Test
  public void testFormatTimeNanosLeadingZerosMilli() {
    TimeUtilsImpl timeUtils = new TimeUtilsImpl(clock);
    clock.setNanosEpoch(1100000000023456789L);
    String actual = timeUtils.formatTimeNanos();
    assertThat(actual).isEqualTo("2004-11-09T11:33:20.023456789");
  }

  @Test
  public void testFormatTimeNanosLeadingZerosMicro() {
    TimeUtilsImpl timeUtils = new TimeUtilsImpl(clock);
    clock.setNanosEpoch(1100000000123056789L);
    String actual = timeUtils.formatTimeNanos();
    assertThat(actual).isEqualTo("2004-11-09T11:33:20.123056789");
  }

  @Test
  public void testFormatTimeNanosLeadingZerosNano() {
    TimeUtilsImpl timeUtils = new TimeUtilsImpl(clock);
    clock.setNanosEpoch(1100000000123456089L);
    String actual = timeUtils.formatTimeNanos();
    assertThat(actual).isEqualTo("2004-11-09T11:33:20.123456089");
  }

  @Test
  public void testFormatTimeNanosTrailingZero() {
    TimeUtilsImpl timeUtils = new TimeUtilsImpl(clock);
    clock.setNanosEpoch(1100000000123456780L);
    String actual = timeUtils.formatTimeNanos();
    assertThat(actual).isEqualTo("2004-11-09T11:33:20.123456780");
  }

  @Test
  public void testSleepForMillisNegative() {
    Clock wallClock = new ClockImpl();
    TimeUtilsImpl timeUtils = new TimeUtilsImpl(wallClock);

    Long before = wallClock.getMillisEpoch();
    Exception interrupted = timeUtils.sleepForMillis(-5000);
    Long after = wallClock.getMillisEpoch();

    assertThat(interrupted).isNull();
    assertThat(after - before).isLessThan(delta);
  }

  @Test
  public void testSleepForMillisInterrupted() throws InterruptedException {
    Clock wallClock = new ClockImpl();
    TimeUtilsImpl timeUtils = new TimeUtilsImpl(wallClock);

    InterruptionHelper thread = new InterruptionHelper(timeUtils, 5000);

    Long before = wallClock.getMillisEpoch();

    thread.start();

    Thread.sleep(100);

    assertThat(thread.isAlive()).isTrue();
    thread.interrupt();

    Long after = wallClock.getMillisEpoch();

    Thread.sleep(100);

    assertThat(thread.isAlive()).isFalse();
    assertThat(thread.getException()).isNotNull();
    assertThat(after - before).isLessThan(100L + delta);
  }

  public class InterruptionHelper extends Thread {
    private final TimeUtils timeUtils;
    private final long millis;
    private InterruptedException exception;

    public InterruptionHelper(TimeUtils timeUtils, long millis) {
      this.timeUtils = timeUtils;
      this.millis = millis;
      this.exception = null;
    }

    @Override
    public void run() {
      exception = timeUtils.sleepForMillis(millis);
    }

    public InterruptedException getException() {
      return exception;
    }
  }
}
