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

public class ClockImplTest {
  private long delta;

  @Before
  public void setUp() {
    delta = 2000; // 2 seconds
  }

  @Test
  public void testGetMillisEpochRange() {
    ClockImpl clock = new ClockImpl();

    long expected = System.currentTimeMillis();
    long actual = clock.getMillisEpoch();

    assertThat(actual).isBetween(expected - delta, expected + delta);
  }

  @Test
  public void testGetMillisEpochIncreasing() throws InterruptedException {
    ClockImpl clock = new ClockImpl();

    long ts1 = clock.getMillisEpoch();

    long end = System.currentTimeMillis() + 100;
    while (System.currentTimeMillis() < end) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw e;
      }
    }

    long ts2 = clock.getMillisEpoch();

    assertThat(ts2 - ts1).isBetween(100L, 100L + delta);
  }

  @Test
  public void testGetNanosEpochRange() {
    ClockImpl clock = new ClockImpl();

    long lowMark = System.currentTimeMillis();
    long actual = clock.getNanosEpoch();
    long highMark = System.currentTimeMillis();

    assertThat(actual / 1000000L).isBetween(lowMark, highMark);
  }

  @Test
  public void testGetNanosEpochResolution() {
    ClockImpl clock = new ClockImpl();

    long ts1 = clock.getNanosEpoch();
    long ts2 = clock.getNanosEpoch();

    assertThat(ts1).isLessThan(ts2);
  }
}
