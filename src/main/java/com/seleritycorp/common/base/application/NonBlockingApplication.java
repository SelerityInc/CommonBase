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

import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Non blocking application that exits once it is notified.
 */
public abstract class NonBlockingApplication extends Application {
  private static final Log log = LogFactory.getLog(NonBlockingApplication.class);

  /**
   * Method that is run one time, once the application switched to READY state.
   * 
   * <p>This method is expected to not run forever, but eventually return.
   *  
   * @throws Exception if errors occur.
   */
  public abstract void runNonBlocking() throws Exception;

  /**
   * Runs the non-blocking part and waits for a notification to return.
   * 
   * <p>This method runs {@link #runNonBlocking()} once, and then waits for a notification on the
   * application object. Once the notification arrives, the method returns.
   *
   * @throws Exception if errors occur.
   */
  @SuppressFBWarnings(value = {"UW_UNCOND_WAIT", "WA_NOT_IN_LOOP"},
      justification = "Keeping the main thread alive (to avoid early exit) until user "
          + "interruptions occurs")
  @Override
  public final void run() throws Exception {
    runNonBlocking();

    synchronized (this) {
      try {
        this.wait();
      } catch (InterruptedException e) {
        log.info("Main method interrupted", e);
      }
    }
  }
}
