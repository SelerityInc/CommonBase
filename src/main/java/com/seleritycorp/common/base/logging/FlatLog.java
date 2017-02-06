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
public class FlatLog extends CommonsLog {
  /**
   * Wraps a Log to force single-line messages.
   *
   * <p>Line breaks contained in user-provided log messages will get converted
   * to \n.
   * 
   * @param wrappedLog The log to log single-line messages to
   * @param formatter Instance of Formating helper 
   */
  public FlatLog(Log wrappedLog, Formatter formatter) {
    super(wrappedLog, formatter);
  }

  @Override
  protected Event processEvent(Event event) {
    event.setMessage(toSingleLine(event.getMessage(), event.getThrowable()));
    event.unsetThrowable();
    return event;
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
}
