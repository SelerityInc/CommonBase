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

import java.util.concurrent.Callable;

/**
 * Callable that tracks whether or not it has been called.
 * 
 * <p>This class is useful with caches when trying to track whether or not a value got loaded or
 * was present in the cache already.
 * 
 * <p>This class is not thread-safe.
 * 
 * @param <T> type of the value returned for calls.
 */
public class StaticTrackingCallable<T> implements Callable<T> {
  /* No AssistedInject Factory due to generics */
  public static class Factory {
    public <T> StaticTrackingCallable<T> create(T callResult) {
      return new StaticTrackingCallable<T>(callResult);
    }
  }

  /**
   * The value to return for calls.
   */
  private final T callResult;

  /**
   * Whether or not the Callable got called since the last reset.
   */
  boolean uncalled;

  /**
   * Creates a leading tracker returning a static value for calls.
   *
   * @param callResult The value to return for calls.
   */
  public StaticTrackingCallable(T callResult) {
    this.callResult = callResult;
    reset();
  }

  /**
   * Resets the callable's state to being uncalled. 
   */
  public void reset() {
    uncalled = true;
  }

  @Override
  public T call() {
    uncalled = false;
    return callResult;
  }
  
  /**
   * Checks if the callable has been called or not.
   * 
   * @return True if the Callable has been called. False otherwise.
   */
  public boolean isUncalled() {
    return uncalled;
  }
}