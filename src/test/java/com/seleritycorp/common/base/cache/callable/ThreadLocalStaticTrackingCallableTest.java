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

package com.seleritycorp.common.base.cache.callable;

import static org.assertj.core.api.Assertions.assertThat;

import static org.easymock.EasyMock.expect;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.test.InjectingTestCase;

public class ThreadLocalStaticTrackingCallableTest extends InjectingTestCase {
  StaticTrackingCallable.Factory callableFactory;

  @Before
  public void setUp() {
    callableFactory = createMock(StaticTrackingCallable.Factory.class);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testResetUncalled() throws InterruptedException {
    ThreadLocalStaticTrackingCallable<Integer> callable =
        createThreadLocalStaticTrackingCallable();

    StaticTrackingCallable<Integer> localCallable1 = createMock(StaticTrackingCallable.class);
    expect(localCallable1.call()).andReturn(42);
    StaticTrackingCallable<Integer> localCallable2 = createMock(StaticTrackingCallable.class);
    expect(localCallable2.call()).andReturn(42);
    StaticTrackingCallable<Integer> localCallable3 = createMock(StaticTrackingCallable.class);
    expect(localCallable3.isUncalled()).andReturn(true);

    expect(callableFactory.create(4711)).andReturn(localCallable1);
    expect(callableFactory.create(4711)).andReturn(localCallable2);
    expect(callableFactory.create(4711)).andReturn(localCallable3);

    AtomicInteger passedRuns = new AtomicInteger();

    replayAll();
    
    Runnable r = new Runnable() {
      @Override
      public void run() {
        int actual1 = callable.call();

        assertThat(actual1).isEqualTo(42);
       
        passedRuns.incrementAndGet();
      }      
    };

    Thread t1 = new Thread(r);
    Thread t2 = new Thread(r);

    t1.start();
    t2.start();

    t1.join(5000);
    t2.join(5000);

    assertThat(callable.isUncalled()).isTrue();

    verifyAll();

    assertThat(passedRuns.get()).isEqualTo(2);
  }
  
  private ThreadLocalStaticTrackingCallable<Integer> createThreadLocalStaticTrackingCallable() {
    return new ThreadLocalStaticTrackingCallable<Integer>(callableFactory, 4711);
  }
}
