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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Log wrapper that turns multi-line messages into single-line messages.
 */
public class FlatLog implements Log {
  /**
   * The Log used by FlatLog to actually do the logging.
   */
  private final Log log;

  /**
   * Wraps a Log to force single-line messages.
   *
   * <p>Line breaks contained in user-provided log messages will get converted
   * to \n.
   * 
   * @param log The log to log single-line messages to
   */
  public FlatLog(Log log) {
    this.log = log;
  }

  /**
   * Converts a possibly multi-lined message to a single-line.
   *
   * <p>Eventual line breaks get converted to \n.
   * 
   * @param message The possibly multi-lined message to convert
   */
  private String toSingleLine(Object message) {
    String ret = message.toString();
    ret = ret.replaceAll("\n", "\\\\n");
    return ret;
  }

  /**
   * Converts a possibly multi-lined message to a single-line.
   *
   * <p>Eventual line breaks get converted to \n.
   * 
   * @param message The possibly multi-lined message to convert
   * @param t The Throwable for the message.
   */
  private String toSingleLine(Object message, Throwable throwable) {
    String multiLineMessage = message.toString();
    if (throwable != null) {
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      throwable.printStackTrace(printWriter);
      multiLineMessage += "\\n" + throwable.toString() + "\\n" + stringWriter.toString();
    }
    return toSingleLine(multiLineMessage);
  }

  @Override
  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return log.isErrorEnabled();
  }

  @Override
  public boolean isFatalEnabled() {
    return log.isFatalEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return log.isInfoEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return log.isTraceEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return log.isWarnEnabled();
  }

  @Override
  public void trace(Object message) {
    log.trace(toSingleLine(message));
  }

  @Override
  public void trace(Object message, Throwable throwable) {
    log.trace(toSingleLine(message, throwable));
  }

  @Override
  public void debug(Object message) {
    log.debug(toSingleLine(message));
  }

  @Override
  public void debug(Object message, Throwable throwable) {
    log.debug(toSingleLine(message, throwable));
  }

  @Override
  public void info(Object message) {
    log.info(toSingleLine(message));
  }

  @Override
  public void info(Object message, Throwable throwable) {
    log.info(toSingleLine(message, throwable));
  }

  @Override
  public void warn(Object message) {
    log.warn(toSingleLine(message));
  }

  @Override
  public void warn(Object message, Throwable throwable) {
    log.warn(toSingleLine(message, throwable));
  }

  @Override
  public void error(Object message) {
    log.error(toSingleLine(message));
  }

  @Override
  public void error(Object message, Throwable throwable) {
    log.error(toSingleLine(message, throwable));
  }

  @Override
  public void fatal(Object message) {
    log.fatal(toSingleLine(message));
  }

  @Override
  public void fatal(Object message, Throwable throwable) {
    log.fatal(toSingleLine(message, throwable));
  }

  @Override
  public void structuredInfo(String tag, int version, Object... objs) {
    // structuredInfo is required to log to a single line anyways with the
    // same escaping, so me can just push down the values and need not care
    // about toSingleLine.
    log.structuredInfo(tag, version, objs);
  }
}
