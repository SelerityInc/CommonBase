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

package com.seleritycorp.common.base.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ThreadFactory;
import javax.inject.Singleton;

/**
 * Factory for Thread Factories with named threads.
 */
@Singleton
public class ThreadFactoryFactory {
  /**
   * Creates a ThreadFactory for named threads
   *
   * <p>Thread names are prefix followed by dash followed by an ever
   * increasing number.
   *
   * @param prefix The prefix for the thread names
   * @return The ThreadFactory for named threads
   */
  ThreadFactory createThreadFactory(String prefix, boolean isDaemon) {
    return new ThreadFactoryBuilder()
        .setNameFormat(prefix + "-" + "%d")
        .setDaemon(isDaemon)
        .build();
  }

  /**
   * Creates a ThreadFactory for named user threads
   *
   * <p>Thread names are prefix followed by dash followed by an ever
   * increasing number.
   *
   * @param prefix The prefix for the thread names
   * @return The ThreadFactory for named threads
   */
  ThreadFactory createUserThreadFactory(String prefix) {
    return createThreadFactory(prefix, false);
  }

  /**
   * Creates a ThreadFactory for named daemon threads
   *
   * <p>Thread names are prefix followed by dash followed by an ever
   * increasing number.
   *
   * @param prefix The prefix for the thread names
   * @return The ThreadFactory for named threads
   */
  ThreadFactory createDaemonThreadFactory(String prefix) {
    return createThreadFactory(prefix, true);
  }
}
