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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.apache.log4j.Logger;

/**
 * Selerity specific Log extension.
 */
@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_INTERFACE",
    justification = "This Log should act as drop-in replacement for commons' Log")
public interface Log extends org.apache.commons.logging.Log {
  /**
   * Logs an error at a given level.
   *
   * @param level the level to log at.
   * @param message message to log.
   */
  void log(Level level, Object message);

  /**
   * Logs an error at a given level.
   *
   * @param level the level to log at.
   * @param message message to log.
   * @param throwable cause for the log.
   */
  void log(Level level, Object message, Throwable throwable);

  /**
   * Gets the raw Log4j logger behind this Log, if there is any
   *
   * <p>Log4j gets the line numbers wrong when wrapping the loggers directly. To allow
   * implementations to work around this issue, this method grants access to the raw
   * Log4j logger.
   * 
   * @return raw backing Log4j logger, if there is any. null otherwise.  
   */
  public Logger getLog4jLogger();
  
  /**
   * Logs objects in structured format in a single INFO line
   *
   * <p>This method is mostly useful for logs that are meant to be parsed by
   * machines. The format is friendly to `cut` and `grep`.
   *
   * <p>The following replacements take place:
   * <ol>
   * <li>Backslashes get replaced by double backslashes.</li>
   * <li>Carriage-Returns get replaced by backslash, followed by r.</li>
   * <li>Linebreaks get replaced by backslash, followed by n.</li>
   * <li>Slashes get replaced by backslash, followed by a pipe.</li>
   * </ol>
   * 
   * @param tag The tag to store the line at
   * @param version The version of the tag
   * @param objs The name and objects to log. objs is expected to hold an
   *        even number of elements, with objs[2*n] holding the name for the
   *        object at objs[2*n+1]. So an example of a call would be
   *        {@code structuredInfo("foo", 42, "bar", bar, "bar-size", bar.size());}
   */
  public void structuredInfo(String tag, int version, Object... objs);
}
