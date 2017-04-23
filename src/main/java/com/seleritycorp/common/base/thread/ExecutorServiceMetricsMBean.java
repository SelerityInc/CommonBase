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

/**
 * MBean interface for executor service metrics.
 */
public interface ExecutorServiceMetricsMBean {
  /**
   * Gets the number of currently active threads of the executor service.
   * 
   * @return the number of currently active threads of the executor service.
   */
  public int getActiveThreadCount();

  /**
   * Gets the lower bound for the allowed thread pool size.
   *
   * @return the lower bound for the allowed thread pool size.
   */
  public int getMinimumPoolSize();

  /**
   * Gets the upper bound for the allowed thread pool size.
   *
   * @return the upper bound for the allowed thread pool size.
   */
  public int getMaximumPoolSize();

  /**
   * Gets the current thread pool size.
   *
   * @return the current thread pool size.
   */
  public int getPoolSize();

  /**
   * Gets the current number of queued work items.
   *
   * @return the current number of queued work items.
   */  
  public int getQueueSize();

  /**
   * Gets the approximate number of completed tasks of the executor service.
   * 
   * @return the approximate number of completed tasks of the executor service.
   */
  public long getCompletedTaskCount();
}
