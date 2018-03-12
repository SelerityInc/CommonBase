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

import org.junit.Test;

public class LevelTest {
  @Test
  public void testToLevelOff() {
    assertThat(Level.OFF.toLog4jLevel()).isEqualTo(org.apache.log4j.Level.OFF);
  }

  @Test
  public void testToLevelFatal() {
    assertThat(Level.FATAL.toLog4jLevel()).isEqualTo(org.apache.log4j.Level.FATAL);
  }

  @Test
  public void testToLevelError() {
    assertThat(Level.ERROR.toLog4jLevel()).isEqualTo(org.apache.log4j.Level.ERROR);
  }

  @Test
  public void testToLevelWarn() {
    assertThat(Level.WARN.toLog4jLevel()).isEqualTo(org.apache.log4j.Level.WARN);
  }

  @Test
  public void testToLevelInfo() {
    assertThat(Level.INFO.toLog4jLevel()).isEqualTo(org.apache.log4j.Level.INFO);
  }

  @Test
  public void testToLevelDebug() {
    assertThat(Level.DEBUG.toLog4jLevel()).isEqualTo(org.apache.log4j.Level.DEBUG);
  }

  @Test
  public void testToLevelTrace() {
    assertThat(Level.TRACE.toLog4jLevel()).isEqualTo(org.apache.log4j.Level.TRACE);
  }
}
