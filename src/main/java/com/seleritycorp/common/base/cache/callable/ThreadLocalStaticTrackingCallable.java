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

import com.google.inject.assistedinject.Assisted;

import java.util.concurrent.Callable;

import javax.inject.Inject;

/**
 * Thread-Safe StaticTrackingCallable that is tracking per thread
 * 
 * <p>This class is useful for caches that load adhoc to determine if the loader got invoked on
 * the current thread or not.
 *
 * @param <T> type of the value returned for calls.
 */
public class ThreadLocalStaticTrackingCallable<T> implements Callable<T> {
  public static class Factory {
    private final StaticTrackingCallable.Factory factory;

    @Inject
    Factory(StaticTrackingCallable.Factory factory) {
      this.factory = factory;
    }

    public <T> ThreadLocalStaticTrackingCallable<T> create(T callResult) {
      return new ThreadLocalStaticTrackingCallable<T>(factory, callResult);
    }
  }

  private final ThreadLocal<StaticTrackingCallable<T>> callables;

  /**
   * Creates a per-thread tracking callable.
   *
   * @param factory The factory to use tracking callables for new threads.
   * @param callResult The result to yield for calls.
   */
  @Inject
  public ThreadLocalStaticTrackingCallable(StaticTrackingCallable.Factory factory,
      @Assisted final T callResult) {
    this.callables = new ThreadLocal<StaticTrackingCallable<T>>() {
      @Override
      protected StaticTrackingCallable<T> initialValue() {
          return factory.create(callResult);
      } 
    };
  }

  /**
   * Resets the callable's state to being uncalled. 
   */
  public void reset() {
    callables.get().reset();
  }

  @Override
  public T call() {
    return callables.get().call();
  }
  
  /**
   * Checks if the callable has been called or not.
   * 
   * @return True if the Callable has been called. False otherwise.
   */
  public boolean isUncalled() {
    return callables.get().isUncalled();
  }
}
