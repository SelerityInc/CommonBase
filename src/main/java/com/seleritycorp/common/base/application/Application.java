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

package com.seleritycorp.common.base.application;

/**
 * Base class for Applications
 * 
 * <p>Applications are typically run through ApplicationRunner, which handles basic state setting,
 * command line registering etc.
 */
public abstract class Application {
  /**
   * Initialization of the application.
   * 
   * <p>This method should initialize the application as far as possible. After calling to init,
   * the application should be usable (servers started and listening, required files read, ...)
   *
   * @throws Exception if errors occur.
   */
  public void init() throws Exception {
    // Empty placeholder for derivative classes to override if needed.
  }

  /**
   * The application's main loop.
   * 
   * <p>This method is expected to be blocking. If the method returns, the application will be
   * shut down.
   *
   * @throws Exception if errors occur.
   */
  public abstract void run() throws Exception;

  /**
   * Stopping and cleaning up of the application.
   *
   * @throws Exception if errors occur.
   */
  public void shutdown() throws Exception {
    // Empty placeholder for derivative classes to override if needed.
  }
}
