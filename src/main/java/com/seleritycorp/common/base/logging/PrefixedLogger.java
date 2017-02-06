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

import com.google.inject.assistedinject.Assisted;

import org.apache.commons.logging.Log;

import javax.inject.Inject;

/**
 * Wraps a logger, adding a prefix to each logged message.
 */
public class PrefixedLogger extends CommonsLog {
  public interface Factory {
    PrefixedLogger create(String prefix, Log parentLog);
  }

  /**
   * The fully formatted prefix to add to each logged message.
   */
  private final String prefix;

  /**
   * Constucts a new Logger that prefixes each logged message.
   * 
   * @param prefix The prefix to prefix to each log message. The prefix will
   *        additionally be put in parentheses.
   * @param wrappedLog The Log instance to log the prefixed log messages with.
   * @param formatter Instance of Formating helper 
   */
  @Inject
  PrefixedLogger(@Assisted String prefix, @Assisted Log wrappedLog, Formatter formatter) {
    super(wrappedLog, formatter);
    this.prefix = prefix;
  }

  @Override
  protected String processMessage(String message) {
    return "(" + prefix + ") " + message;
  }

  protected String processStructuredData(String tag, int version, Object... objs) {
    Object[] prefixedObjs;
    int objsLength = (objs == null) ? 0 : objs.length;
    
    prefixedObjs = new Object[objsLength + 2];
    prefixedObjs[0] = "prefix";
    prefixedObjs[1] = prefix;
    System.arraycopy(objs, 0, prefixedObjs, 2, objsLength);

    return super.processStructuredData(tag, version, prefixedObjs); 
  }
}
