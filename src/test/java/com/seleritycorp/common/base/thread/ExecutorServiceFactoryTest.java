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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.Test;

public class ExecutorServiceFactoryTest {
  // Not mocking out this Factory as the ExecutorService relies on being able to run its runnables.
  private ThreadFactoryFactory threadFactoryFactory = new ThreadFactoryFactory();

  @Test
  public void testCreateFixedDaemonExecutorServiceSingle() throws Exception {
    ExecutorServiceFactory executorServiceFactory = createExecutorServiceFactory();

    ExecutorService executor = executorServiceFactory
        .createFixedUnboundedDaemonExecutorService("foo", 1);
    
    CallableShim callable = new CallableShim();
    callable.allowExit();
    
    Future<Boolean> future = executor.submit(callable);
    
    assertThat(future.get()).isTrue();
  }
  
  @Test
  public void testCreateFixedDaemonExecutorServiceMultiple() throws Exception {
    ExecutorServiceFactory executorServiceFactory = createExecutorServiceFactory();

    ExecutorService executor = executorServiceFactory
        .createFixedUnboundedDaemonExecutorService("foo", 3);
    
    CallableShim callable1 = new CallableShim();
    CallableShim callable2 = new CallableShim();
    
    Future<Boolean> future1 = executor.submit(callable1);
    Future<Boolean> future2 = executor.submit(callable2);
    
    callable1.allowExit();
    assertThat(future1.get()).isTrue();

    callable2.allowExit();
    assertThat(future2.get()).isTrue();
  }
  
  @Test
  public void testCreateFixedDaemonExecutorServiceQueueing() throws Exception {
    ExecutorServiceFactory executorServiceFactory = createExecutorServiceFactory();

    ExecutorService executor = executorServiceFactory
        .createFixedUnboundedDaemonExecutorService("foo", 1);
    
    CallableShim callable1 = new CallableShim();
    CallableShim callable2 = new CallableShim();
    
    Future<Boolean> future1 = executor.submit(callable1);
    Future<Boolean> future2 = executor.submit(callable2);
    
    callable1.allowExit();
    assertThat(future1.get()).isTrue();

    callable2.allowExit();
    assertThat(future2.get()).isTrue();
  }
  
  private ExecutorServiceFactory createExecutorServiceFactory() {
    return new ExecutorServiceFactory(threadFactoryFactory);
  }
  
  private class CallableShim implements Callable<Boolean> {
    boolean canExit;
    
    public CallableShim() {
      canExit = false;
    }

    public void allowExit() {
      canExit = true;
    }
    
    @Override
    public Boolean call() throws Exception {
      while (!canExit) {
        Thread.sleep(5);
      }
      return true;
    }
  }
}
