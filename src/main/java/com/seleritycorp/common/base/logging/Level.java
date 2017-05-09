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

package com.seleritycorp.common.base.logging;

/**
 * Enum for passing log levels.
 */
public enum Level {
  OFF,
  FATAL,
  ERROR,
  WARN,
  INFO,
  DEBUG,
  TRACE;
  
  /**
   * Converts the instance to a log4j Level
   *
   * @return the log4j equivalent of the current level.
   */
  public org.apache.log4j.Level toLog4jLevel() {
    final org.apache.log4j.Level ret;
    switch (this) {
      case OFF:
        ret = org.apache.log4j.Level.OFF;
        break;
      case FATAL:
        ret = org.apache.log4j.Level.FATAL;
        break;
      case ERROR:
        ret = org.apache.log4j.Level.ERROR;
        break;
      case WARN:
        ret = org.apache.log4j.Level.WARN;
        break;
      case INFO:
        ret = org.apache.log4j.Level.INFO;
        break;
      case DEBUG:
        ret = org.apache.log4j.Level.DEBUG;
        break;
      case TRACE:
        ret = org.apache.log4j.Level.TRACE;
        break;
      default:
        ret = org.apache.log4j.Level.INFO;
        break;
    }
    return ret;
  }
}
