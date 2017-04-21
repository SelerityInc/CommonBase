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

package com.seleritycorp.common.base.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

public class NonBlockingApplicationTest {
  @Test
  public void testRunBlocking() throws Exception {
    final ApplicationShim application = new ApplicationShim();
    final AtomicBoolean succeeded = new AtomicBoolean(false);
    
    Thread t = new Thread() {
      @Override
      public void run() {
        try {
          application.run();
          succeeded.set(true);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    };
    t.start();

    long abortTs = System.currentTimeMillis() + 5000;
    
    // Wait for t to be waiting.
    while (t.getState() != Thread.State.WAITING && System.currentTimeMillis() < abortTs) {
       Thread.sleep(50);
    }
    
    assertThat(application.didRunNonBlocking()).isTrue();

    synchronized (application) {
      application.notify();
    }
    t.join(1000);
    
    assertThat(succeeded.get()).isTrue();

    assertThat(System.currentTimeMillis()).isLessThan(abortTs);
  }
  
  class ApplicationShim extends NonBlockingApplication {
    private boolean ranNonBlocking;
    
    ApplicationShim() {
      ranNonBlocking = false;
    }

    @Override
    public void runNonBlocking() throws Exception {
      ranNonBlocking = true; 
    }
    
    public boolean didRunNonBlocking() {
      return ranNonBlocking;
    }
  }
}
