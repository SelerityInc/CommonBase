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

/**
 * Wrapper for Apache Common Loggging logs to become Selerity Log.
 */
public class CommonsLog implements Log {
  /**
   * The wrapped Log instance.
   */
  private final org.apache.commons.logging.Log wrappedLog;

  /**
   * The Formatter for log entries.
   */
  private final Formatter formatter;

  /**
   * Wraps a Commons Logging log to becomen a Selerity Log
   * 
   * @param wrappedLog The Commons Logging Log instance to wrap.
   */
  CommonsLog(org.apache.commons.logging.Log wrappedLog, Formatter logFormatter) {
    this.wrappedLog = wrappedLog;
    this.formatter = logFormatter;
  }

  @Override
  public boolean isDebugEnabled() {
    return wrappedLog.isDebugEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return wrappedLog.isErrorEnabled();
  }

  @Override
  public boolean isFatalEnabled() {
    return wrappedLog.isFatalEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return wrappedLog.isInfoEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return wrappedLog.isTraceEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return wrappedLog.isWarnEnabled();
  }

  @Override
  public void trace(Object message) {
    wrappedLog.trace(message);
  }

  @Override
  public void trace(Object message, Throwable throwable) {
    wrappedLog.trace(message, throwable);
  }

  @Override
  public void debug(Object message) {
    wrappedLog.debug(message);
  }

  @Override
  public void debug(Object message, Throwable throwable) {
    wrappedLog.debug(message, throwable);
  }

  @Override
  public void info(Object message) {
    wrappedLog.info(message);
  }

  @Override
  public void info(Object message, Throwable throwable) {
    wrappedLog.info(message, throwable);
  }

  @Override
  public void warn(Object message) {
    wrappedLog.warn(message);
  }

  @Override
  public void warn(Object message, Throwable throwable) {
    wrappedLog.warn(message, throwable);
  }

  @Override
  public void error(Object message) {
    wrappedLog.error(message);
  }

  @Override
  public void error(Object message, Throwable throwable) {
    wrappedLog.error(message, throwable);
  }

  @Override
  public void fatal(Object message) {
    wrappedLog.fatal(message);
  }

  @Override
  public void fatal(Object message, Throwable throwable) {
    wrappedLog.fatal(message, throwable);
  }

  @Override
  public void structuredInfo(String tag, int version, Object... objs) {
    wrappedLog.info(formatter.formatStructuredLine(tag, version, objs));
  }
}
