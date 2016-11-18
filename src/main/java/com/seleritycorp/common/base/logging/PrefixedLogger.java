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

package com.seleritycorp.common.base.logging;

import com.google.inject.assistedinject.Assisted;

import org.apache.commons.logging.Log;

import javax.inject.Inject;

/**
 * Wraps a logger, adding a prefix to each logged message.
 */
public class PrefixedLogger implements Log {
  public interface Factory {
    PrefixedLogger create(String prefix, Log parentLog);
  }

  /**
   * The fully formatted prefix to add to each logged message.
   */
  private final String prefix;

  /**
   * The wrapped Log instance.
   */
  private final Log parentLog;

  /**
   * Constucts a new Logger that prefixes each logged message.
   * 
   * @param prefix The prefix to prefix to each log message. The prefix will
   *        additionally be put in parentheses.
   * @param parentLog The Log instance to log the prefixed log messages with.
   */
  @Inject
  PrefixedLogger(@Assisted String prefix, @Assisted Log parentLog) {
    this.prefix = "(" + prefix + ") ";
    this.parentLog = parentLog;
  }

  @Override
  public boolean isDebugEnabled() {
    return parentLog.isDebugEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return parentLog.isErrorEnabled();
  }

  @Override
  public boolean isFatalEnabled() {
    return parentLog.isFatalEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return parentLog.isInfoEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return parentLog.isTraceEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return parentLog.isWarnEnabled();
  }

  @Override
  public void trace(Object message) {
    parentLog.trace(prefix + message);
  }

  @Override
  public void trace(Object message, Throwable throwable) {
    parentLog.trace(prefix + message, throwable);
  }

  @Override
  public void debug(Object message) {
    parentLog.debug(prefix + message);
  }

  @Override
  public void debug(Object message, Throwable throwable) {
    parentLog.debug(prefix + message, throwable);
  }

  @Override
  public void info(Object message) {
    parentLog.info(prefix + message);
  }

  @Override
  public void info(Object message, Throwable throwable) {
    parentLog.info(prefix + message, throwable);
  }

  @Override
  public void warn(Object message) {
    parentLog.warn(prefix + message);
  }

  @Override
  public void warn(Object message, Throwable throwable) {
    parentLog.warn(prefix + message, throwable);
  }

  @Override
  public void error(Object message) {
    parentLog.error(prefix + message);
  }

  @Override
  public void error(Object message, Throwable throwable) {
    parentLog.error(prefix + message, throwable);
  }

  @Override
  public void fatal(Object message) {
    parentLog.fatal(prefix + message);
  }

  @Override
  public void fatal(Object message, Throwable throwable) {
    parentLog.fatal(prefix + message, throwable);
  }
}
