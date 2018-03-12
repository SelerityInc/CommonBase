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

import org.apache.commons.logging.LogConfigurationException;

/**
 * Factory for creating Log instances.
 */
public class LogFactory {
  private static Formatter formatter = InjectorFactory.getInjector().getInstance(Formatter.class);

  /**
   * Gets the non-permanent Log for a given Class
   *
   * <p>Non-permanent logs will get deleted after some time, and not retained
   * forever.
   *
   * @param clazz The clazz to get the {@code Log} instance for.
   * @return The non-permanent log for {@code clazz}
   * @exception LogConfigurationException if no suitable {@code Log} instance
   *            can be returned.
   */
  public static Log getLog(@SuppressWarnings("rawtypes") Class clazz)
      throws LogConfigurationException {
    return getLog(clazz.getName());
  }


  /**
   * Gets the non-permanent Log for a given name
   *
   * <p>Non-permanent logs will get deleted after some time, and not retained
   * forever.
   *
   * @param name The name to get the permanent {@code Log} instance for.
   * @return The non-permanent log for {@code name}
   * @exception LogConfigurationException if no suitable {@code Log} instance
   *            can be returned.
   */
  public static Log getLog(String name) throws LogConfigurationException {
    return new CommonsLog(org.apache.commons.logging.LogFactory.getLog(name), formatter);
  }

  /**
   * Gets the permanent Log for a given Class
   *
   * <p>Permanent logs are meant to be kept around forever. Use them only for
   * messages that are worth to keep around forever.
   *
   * <p>Messages sent to the returned Log are converted to a single line.
   *
   * @param clazz The clazz to get the {@code Log} instance for.
   * @return The permanent log for {@code clazz}
   * @exception LogConfigurationException if no suitable {@code Log} instance
   *            can be returned.
   */
  public static Log getPermanentLog(@SuppressWarnings("rawtypes") Class clazz)
      throws LogConfigurationException {
    return getPermanentLog(clazz.getName());
  }

  /**
   * Gets the permanent Logger for a given name
   *
   * <p>Permanent logs are meant to be kept around forever. Use them only for
   * messages that are worth to keep around forever.
   *
   * <p>Messages sent to the returned Log are converted to a single line.
   * 
   * @param name The name to get the permanent {@code Log} instance for.
   * @return The non-permanent log for {@code clazz}
   * @exception LogConfigurationException if no suitable {@code Log} instance
   *            can be returned.
   */
  public static Log getPermanentLog(String name) throws LogConfigurationException {
    return new FlatLog(getLog("permanent." + name), formatter);
  }
}
