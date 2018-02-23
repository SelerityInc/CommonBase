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

import org.junit.Test;

import com.seleritycorp.common.base.cache.callable.StaticTrackingCallable;
import com.seleritycorp.common.base.test.InjectingTestCase;

public class StaticTrackingCallableTest extends InjectingTestCase {
  @Test
  public void testResetUncalled() {
    StaticTrackingCallable<Integer> callable = createStaticTrackingCallable(1);

    assertThat(callable.isUncalled()).isTrue();

    callable.reset();

    assertThat(callable.isUncalled()).isTrue();
  }

  @Test
  public void testCallSingle1() {
    StaticTrackingCallable<Integer> callable = createStaticTrackingCallable(1);

    assertThat(callable.isUncalled()).isTrue();

    int actual = callable.call();
    
    assertThat(callable.isUncalled()).isFalse();
    assertThat(actual).isEqualTo(1);
  }

  @Test
  public void testCallSingle2() {
    StaticTrackingCallable<Integer> callable = createStaticTrackingCallable(2);
    
    assertThat(callable.isUncalled()).isTrue();

    int actual = callable.call();
    
    assertThat(callable.isUncalled()).isFalse();
    assertThat(actual).isEqualTo(2);
  }

  @Test
  public void testCallMultiple() {
    StaticTrackingCallable<Integer> callable = createStaticTrackingCallable(3);

    assertThat(callable.isUncalled()).isTrue();

    int actual1 = callable.call();
    
    assertThat(callable.isUncalled()).isFalse();
    assertThat(actual1).isEqualTo(3);

    int actual2 = callable.call();
    
    assertThat(callable.isUncalled()).isFalse();
    assertThat(actual2).isEqualTo(3);
  }

  @Test
  public void testCallMultipleReset() {
    StaticTrackingCallable<Integer> callable = createStaticTrackingCallable(3);

    assertThat(callable.isUncalled()).isTrue();

    int actual1 = callable.call();
    
    assertThat(callable.isUncalled()).isFalse();
    assertThat(actual1).isEqualTo(3);
    
    callable.reset();

    assertThat(callable.isUncalled()).isTrue();

    int actual2 = callable.call();
    
    assertThat(callable.isUncalled()).isFalse();
    assertThat(actual2).isEqualTo(3);
  }

  private StaticTrackingCallable<Integer> createStaticTrackingCallable(Integer i) {
    return new StaticTrackingCallable<Integer>(i);
  }
}
