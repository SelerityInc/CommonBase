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

import com.google.inject.assistedinject.Assisted;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;

/**
 * Metrics for an executor service.
 */
public class ExecutorServiceMetrics implements ExecutorServiceMetricsMBean {
  public interface Factory {
    ExecutorServiceMetrics create(ThreadPoolExecutor executor);
  }
  
  private final ThreadPoolExecutor executor;
  private final BlockingQueue<Runnable> queue;

  @Inject
  ExecutorServiceMetrics(@Assisted ThreadPoolExecutor executor) {
    this.executor = executor;
    this.queue = executor.getQueue();
  }

  @Override
  public int getActiveThreadCount() {
    return executor.getActiveCount();
  }

  @Override
  public int getPoolSize() {
    return executor.getPoolSize();
  }

  @Override
  public int getMinimumPoolSize() {
    return executor.getCorePoolSize();
  }

  @Override
  public int getMaximumPoolSize() {
    return executor.getMaximumPoolSize();
  }

  @Override
  public int getQueueSize() {
    return queue.size();
  }

  @Override
  public long getCompletedTaskCount() {
    return executor.getCompletedTaskCount();
  }
}
