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

package com.seleritycorp.common.base.cache;

/**
 * MBean interface for cache metrics.
 */
public interface CacheMetricsMBean {
  /**
   * Gets the total number of cache hits of the monitored cache.
   * 
   * @return The total number of cache hits of the monitored cache.
   */
  public long getHitCount();

  /**
   * Gets the total number of cache misses of the monitored cache.
   * 
   * @return The total number of cache misses of the monitored cache.
   */
  public long getMissCount();

  /**
   * Gets the total number of successfully loaded entries of the monitored cache.
   * 
   * @return The total number of successfully loaded entries of the monitored cache.
   */
  public long getLoadSuccessCount();

  /**
   * Gets the total number of entries that failed to load for the monitored cache.
   * 
   * @return The total number of entries that failed to load for the monitored cache.
   */
  public long getLoadExceptionCount();
  
  /**
   * Gets the total time spent loading entries for the monitored cache.
   * 
   * @return The total time spent loading entries for the monitored cache.
   */
  public long getTotalLoadTimeNanos();
  
  /**
   * Gets the number of items evicted from the monitored cache.
   * 
   * @return The number of items evicted from the monitored cache.
   */
  public long getEvictionCount();

  /**
   * Gets the approximate number of items in the cache.
   * 
   * @return The approximate number of items in the cache.
   */
  public long getSize();
}
