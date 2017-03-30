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

package com.seleritycorp.common.base.thread;

import com.google.inject.Inject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.inject.Singleton;

/**
 * Factory for Thread Factories with named threads.
 */
@Singleton
public class ExecutorServiceFactory {
  private ThreadFactoryFactory threadFactoryFactory;

  /**
   * Creates a ExecuterService factory for a given ThreadFactory factory.
   * 
   * @param threadFactoryFactory The factory to create ThreadFactories for ExecutorServices
   */
  @Inject
  public ExecutorServiceFactory(ThreadFactoryFactory threadFactoryFactory) {
    this.threadFactoryFactory = threadFactoryFactory;
  }

  /**
   * Creates an ExecutorServices with a fixed size of daemon threads and unbound queue.
   *
   * @param prefix The prefix for the names of the ExecutorService's threads
   * @param threadCount The number of threads for the ExecutorService
   * @return The created ExecutorService
   */
  public ExecutorService createFixedUnboundedDaemonExecutorService(String prefix,
      int threadCount) {
    ThreadFactory threadFactory = threadFactoryFactory.createDaemonThreadFactory(prefix);
    return Executors.newFixedThreadPool(threadCount, threadFactory);
  }
}
