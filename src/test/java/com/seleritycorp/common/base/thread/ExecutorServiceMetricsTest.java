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

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

public class ExecutorServiceMetricsTest extends EasyMockSupport {
  private ThreadPoolExecutor executor;
  private BlockingQueue<Runnable> queue;
  
  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    executor = createMock(ThreadPoolExecutor.class);
    queue = createMock(BlockingQueue.class);
    expect(executor.getQueue()).andReturn(queue).anyTimes();
  }

  @Test
  public void testGetActiveThreadCount() {
    expect(executor.getActiveCount()).andReturn(42);
    
    replayAll();
    
    ExecutorServiceMetrics metrics = createExecutorServiceMetrics();
    int actual = metrics.getActiveThreadCount();
    
    verifyAll();
    
    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetMinimumPoolSize() {
    expect(executor.getCorePoolSize()).andReturn(42);
    
    replayAll();
    
    ExecutorServiceMetrics metrics = createExecutorServiceMetrics();
    int actual = metrics.getMinimumPoolSize();
    
    verifyAll();
    
    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetMaximumPoolSize() {
    expect(executor.getMaximumPoolSize()).andReturn(42);
    
    replayAll();
    
    ExecutorServiceMetrics metrics = createExecutorServiceMetrics();
    int actual = metrics.getMaximumPoolSize();
    
    verifyAll();
    
    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetPoolSize() {
    expect(executor.getPoolSize()).andReturn(42);
    
    replayAll();
    
    ExecutorServiceMetrics metrics = createExecutorServiceMetrics();
    int actual = metrics.getPoolSize();
    
    verifyAll();
    
    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetQueueSize() {
    expect(queue.size()).andReturn(42);
    
    replayAll();
    
    ExecutorServiceMetrics metrics = createExecutorServiceMetrics();
    int actual = metrics.getQueueSize();
    
    verifyAll();
    
    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetCompletedTaskCount() {
    expect(executor.getCompletedTaskCount()).andReturn(42L);
    
    replayAll();
    
    ExecutorServiceMetrics metrics = createExecutorServiceMetrics();
    long actual = metrics.getCompletedTaskCount();
    
    verifyAll();
    
    assertThat(actual).isEqualTo(42L);
  }

  private ExecutorServiceMetrics createExecutorServiceMetrics() {
    return new ExecutorServiceMetrics(executor);
  }
}
