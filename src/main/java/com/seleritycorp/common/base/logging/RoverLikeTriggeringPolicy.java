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

import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.time.Clock;
import org.apache.log4j.Appender;
import org.apache.log4j.rolling.TriggeringPolicy;
import org.apache.log4j.spi.LoggingEvent;

import javax.inject.Inject;

/**
 * A trigger policy mimicking Rover characteristics
 *
 * <p>Log rotation is triggered by file size and time buckets.
 */
public class RoverLikeTriggeringPolicy implements TriggeringPolicy {
  /**
   * Clock used for timing.
   */
  private final Clock clock;

  /**
   * Trigger roll-over if log file is at least that big.
   */
  private int sizeLimit;

  /**
   * Trigger roll-over after at most that many seconds.
   */
  private long timeLimitSeconds;

  /**
   * Seconds since epoch of when to force next time-based roll over.
   */
  private long nextBucketStartSeconds;

  public RoverLikeTriggeringPolicy() {
    this(InjectorFactory.getInjector().getInstance(Clock.class));
  }

  /**
   * Constructs a policy triggering for 100MB and every 24 hours.
   *
   * @param clock Clock used for timings
   */
  @Inject
  public RoverLikeTriggeringPolicy(Clock clock) {
    setSizeLimit(100000000); // 100MB default
    setTimeLimit(24 * 60 * 60); // 1 day
    this.clock = clock;
  }

  /**
   * Gets log file size limit after which the policy suggests a roll-over.
   * 
   * @return the sizeLimit
   */
  public int getSizeLimit() {
    return sizeLimit;
  }


  /**
   * Sets log file size limit after which the policy suggests a roll-over.
   * 
   * @param sizeLimit the sizeLimit to set
   */
  public void setSizeLimit(int sizeLimit) {
    this.sizeLimit = sizeLimit;
  }


  /**
   * Gets time limit after which to suggest a roll-over.
   * 
   * @return the time limit in seconds
   */
  public long getTimeLimit() {
    return timeLimitSeconds;
  }


  /**
   * Gets time limit after which to suggest a roll-over.
   * 
   * @param timeLimit the time limit in seconds
   */
  public void setTimeLimit(long timeLimit) {
    this.timeLimitSeconds = timeLimit;
  }

  /**
   * Updates the marker for the start of the next time-baset bucket.
   */
  private void updateNextBucketStart() {
    long seconds = clock.getSecondsEpoch();
    nextBucketStartSeconds = ((seconds / timeLimitSeconds) + 1) * timeLimitSeconds;
  }

  @Override
  public void activateOptions() {
    updateNextBucketStart();
  }

  @Override
  public boolean isTriggeringEvent(final Appender appender, final LoggingEvent event,
      final String filename, final long fileLength) {
    boolean timeTriggered = (clock.getSecondsEpoch() >= nextBucketStartSeconds);
    if (timeTriggered) {
      updateNextBucketStart();
    }
    return timeTriggered || fileLength >= sizeLimit;
  }
}
