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

import com.seleritycorp.common.base.jmx.MBeanUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

/**
 * Factory for Thread Factories with named threads.
 */
@Singleton
public class ExecutorServiceFactory {
  private ThreadFactoryFactory threadFactoryFactory;
  private final ExecutorServiceMetrics.Factory executorServiceMetricsFactory;

  /**
   * Creates a ExecuterService factory for a given ThreadFactory factory.
   * 
   * @param threadFactoryFactory The factory to create ThreadFactories for ExecutorServices
   * @param executorServiceMetricsFactory The factory to create metrics with.
   */
  @Inject
  public ExecutorServiceFactory(ThreadFactoryFactory threadFactoryFactory,
      ExecutorServiceMetrics.Factory executorServiceMetricsFactory) {
    this.threadFactoryFactory = threadFactoryFactory;
    this.executorServiceMetricsFactory = executorServiceMetricsFactory;
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
    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount,
        0L, TimeUnit.MILLISECONDS, queue, threadFactory);
    
    ExecutorServiceMetrics metrics = executorServiceMetricsFactory.create(executor);
    String name = "com.seleritycorp.common.base.thread:type=ThreadPool,name=" + prefix;
    MBeanUtils.register(name, metrics);
    
    return executor;
  }
}
