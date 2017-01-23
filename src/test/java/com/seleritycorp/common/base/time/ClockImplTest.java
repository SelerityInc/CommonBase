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

package com.seleritycorp.common.base.time;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ClockImplTest {
  @Test
  public void testGetMillisEpochRange() {
    long deltaMillis = 2000;
    ClockImpl clock = new ClockImpl();

    long expectedMillis = System.currentTimeMillis();
    long actualMillis = clock.getMillisEpoch();

    long lowMillis = expectedMillis - deltaMillis;
    long highMillis = expectedMillis + deltaMillis;
    assertThat(actualMillis).isBetween(lowMillis, highMillis);
  }

  @Test
  public void testGetMillisEpochIncreasing() throws InterruptedException {
    long deltaMillis = 2000;
    ClockImpl clock = new ClockImpl();

    long millisBefore = clock.getMillisEpoch();

    long end = System.currentTimeMillis() + 100;
    while (System.currentTimeMillis() < end) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw e;
      }
    }

    long millisAfter = clock.getMillisEpoch();

    assertThat(millisAfter - millisBefore).isBetween(100L, 100L + deltaMillis);
  }

  @Test
  public void testGetNanosEpochRange() {
    long deltaMillis = 10;
    ClockImpl clock = new ClockImpl();

    long lowMillis = System.currentTimeMillis() - deltaMillis;
    long actualNanos = clock.getNanosEpoch();
    long highMillis = System.currentTimeMillis() + deltaMillis;

    assertThat(actualNanos / 1000000L).isBetween(lowMillis, highMillis);
  }

  @Test
  public void testGetNanosEpochResolution() {
    ClockImpl clock = new ClockImpl();

    long ts1 = clock.getNanosEpoch();
    long ts2 = clock.getNanosEpoch();

    assertThat(ts1).isLessThan(ts2);
  }

  @Test
  public void testReadRange() {
    long deltaMillis = 10;
    ClockImpl clock = new ClockImpl();

    long lowMillis = System.currentTimeMillis() - deltaMillis;
    long actualNanos = clock.read();
    long highMillis = System.currentTimeMillis() + deltaMillis;

    assertThat(actualNanos / 1000000L).isBetween(lowMillis, highMillis);
  }

}
