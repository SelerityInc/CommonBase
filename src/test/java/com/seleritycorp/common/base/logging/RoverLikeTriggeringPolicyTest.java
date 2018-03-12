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

package com.seleritycorp.common.base.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.logging.RoverLikeTriggeringPolicy;
import com.seleritycorp.common.base.test.InjectingTestCase;
import com.seleritycorp.common.base.test.SettableStaticClock;

public class RoverLikeTriggeringPolicyTest extends InjectingTestCase {
  private RoverLikeTriggeringPolicy policy;
  Appender appender;
  LoggingEvent event;
  private SettableStaticClock clock;

  @Before
  public void setUp() {
    appender = createMock(Appender.class);
    event = createMock(LoggingEvent.class);
    clock = getClock();
  }

  @Test
  public void testSizeLimitAccessors() {
    policy = new RoverLikeTriggeringPolicy();

    policy.setSizeLimit(42);
    assertThat(policy.getSizeLimit()).isEqualTo(42);
  }

  @Test
  public void testTimeLimitAccessors() {
    policy = new RoverLikeTriggeringPolicy();

    policy.setTimeLimit(42);
    assertThat(policy.getTimeLimit()).isEqualTo(42);
  }

  @Test
  public void testIsTriggeringEventSizeLow() {
    policy = new RoverLikeTriggeringPolicy();
    policy.setSizeLimit(150);
    policy.activateOptions();

    boolean actual = policy.isTriggeringEvent(appender, event, "foo", 149);
    assertThat(actual).isFalse();
  }

  @Test
  public void testIsTriggeringEventSizeHigh() {
    policy = new RoverLikeTriggeringPolicy();
    policy.setSizeLimit(150);
    policy.activateOptions();

    boolean actual = policy.isTriggeringEvent(appender, event, "foo", 151);
    assertThat(actual).isTrue();
  }

  @Test
  public void testIsTriggeringEventTimeNotTriggeredWithinBucket() {
    policy = createPolicy();

    boolean actual = policy.isTriggeringEvent(appender, event, "foo", 0);
    assertThat(actual).isFalse();
  }

  @Test
  public void testIsTriggeringEventTimeTriggerAfterBucketChange() {
    policy = createPolicy();

    clock.advanceMillis(2000);

    boolean actual = policy.isTriggeringEvent(appender, event, "foo", 0);
    assertThat(actual).isTrue();
  }

  private RoverLikeTriggeringPolicy createPolicy() {
    policy = new RoverLikeTriggeringPolicy();
    policy.setTimeLimit(2);
    policy.activateOptions();
    return policy;
  }
}
