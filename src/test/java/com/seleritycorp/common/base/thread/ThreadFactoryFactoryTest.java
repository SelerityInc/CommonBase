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

import java.util.concurrent.ThreadFactory;

import org.junit.Before;
import org.junit.Test;

public class ThreadFactoryFactoryTest {
  private RunnableShim runnable;

  @Before
  public void setUp() {
    runnable = new RunnableShim();
  }

  @Test
  public void testCreateThreadFactoryNotDaemon() throws InterruptedException {
    ThreadFactoryFactory threadFactoryFactory = createThreadFactoryFactory();
    ThreadFactory threadFactory = threadFactoryFactory.createThreadFactory("foo", false);
    Thread thread = threadFactory.newThread(runnable);
    assertThat(thread.getName()).isEqualTo("foo-0");
    assertThat(thread.isDaemon()).isFalse();
    assertThat(thread.isAlive()).isFalse();

    assertThat(runnable.getRunCount()).isZero();
    thread.run();
    thread.join(100);
    assertThat(runnable.getRunCount()).isEqualTo(1);
    
    thread = threadFactory.newThread(runnable);
    assertThat(thread.getName()).isEqualTo("foo-1");
    assertThat(thread.isDaemon()).isFalse();
    assertThat(thread.isAlive()).isFalse();

    thread.run();
    thread.join(100);
    assertThat(runnable.getRunCount()).isEqualTo(2);
  }

  @Test
  public void testCreateThreadFactoryDaemon() throws InterruptedException {
    ThreadFactoryFactory threadFactoryFactory = createThreadFactoryFactory();
    ThreadFactory threadFactory = threadFactoryFactory.createThreadFactory("foo", true);
    Thread thread = threadFactory.newThread(runnable);
    assertThat(thread.getName()).isEqualTo("foo-0");
    assertThat(thread.isDaemon()).isTrue();
    assertThat(thread.isAlive()).isFalse();

    assertThat(runnable.getRunCount()).isZero();
    thread.run();
    thread.join(100);
    assertThat(runnable.getRunCount()).isEqualTo(1);
    
    thread = threadFactory.newThread(runnable);
    assertThat(thread.getName()).isEqualTo("foo-1");
    assertThat(thread.isDaemon()).isTrue();
    assertThat(thread.isAlive()).isFalse();

    thread.run();
    thread.join(100);
    assertThat(runnable.getRunCount()).isEqualTo(2);
  }

  @Test
  public void testCreateUserThreadFactory() throws InterruptedException {
    ThreadFactoryFactory threadFactoryFactory = createThreadFactoryFactory();
    ThreadFactory threadFactory = threadFactoryFactory.createUserThreadFactory("foo");
    Thread thread = threadFactory.newThread(runnable);
    assertThat(thread.getName()).isEqualTo("foo-0");
    assertThat(thread.isDaemon()).isFalse();
    assertThat(thread.isAlive()).isFalse();

    assertThat(runnable.getRunCount()).isZero();
    thread.run();
    thread.join(100);
    assertThat(runnable.getRunCount()).isEqualTo(1);
    
    thread = threadFactory.newThread(runnable);
    assertThat(thread.getName()).isEqualTo("foo-1");
    assertThat(thread.isDaemon()).isFalse();
    assertThat(thread.isAlive()).isFalse();

    thread.run();
    thread.join(100);
    assertThat(runnable.getRunCount()).isEqualTo(2);
  }

  @Test
  public void testCreateDaemonThreadFactory() throws InterruptedException {
    ThreadFactoryFactory threadFactoryFactory = createThreadFactoryFactory();
    ThreadFactory threadFactory = threadFactoryFactory.createDaemonThreadFactory("foo");
    Thread thread = threadFactory.newThread(runnable);
    assertThat(thread.getName()).isEqualTo("foo-0");
    assertThat(thread.isDaemon()).isTrue();
    assertThat(thread.isAlive()).isFalse();

    assertThat(runnable.getRunCount()).isZero();
    thread.run();
    thread.join(100);
    assertThat(runnable.getRunCount()).isEqualTo(1);
    
    thread = threadFactory.newThread(runnable);
    assertThat(thread.getName()).isEqualTo("foo-1");
    assertThat(thread.isDaemon()).isTrue();
    assertThat(thread.isAlive()).isFalse();

    thread.run();
    thread.join(100);
    assertThat(runnable.getRunCount()).isEqualTo(2);
  }
  
  private ThreadFactoryFactory createThreadFactoryFactory() {
    return new ThreadFactoryFactory();
  }

  private class RunnableShim implements Runnable {
    private int runCount = 0;

    @Override
    public void run() {
      runCount++;
    }
    
    public int getRunCount() {
      return runCount;
    }
  }
}
