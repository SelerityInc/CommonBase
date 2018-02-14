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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.inject.assistedinject.Assisted;

import com.seleritycorp.common.base.time.Clock;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.inject.Inject;

/**
 * Metrics for a cache.
 */
public class CacheMetrics implements CacheMetricsMBean {
  interface Factory {
    CacheMetrics create(Cache<? extends Object, ? extends Object> cache);
  }
  
  private final Clock clock;
  @SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC", justification = "Initial checks are "
      + "unsynchronized. But if they fail, they are repeated synchronized")
  private long nextStatsUpdateMillis = 0;
  private long statsCacheIntervalMillis = 10000;
  @SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC", justification = "Sync is only needed for"
      + "setting it.")
  private CacheStats stats;
  private final Cache<? extends Object, ? extends Object> cache;
  
  @Inject
  CacheMetrics(@Assisted final Cache<? extends Object, ? extends Object> cache, Clock clock) {
    this.cache = cache;
    this.clock = clock;
  }
  
  private CacheStats getStats() {
    long nowMillis = clock.getMillisEpoch();
    if (nextStatsUpdateMillis < nowMillis) {
      synchronized (this) {
        if (nextStatsUpdateMillis < nowMillis) {
          stats = cache.stats();
          nextStatsUpdateMillis = nowMillis + statsCacheIntervalMillis;
        }
      }
    }
    return stats;
  }

  @Override
  public long getHitCount() {
    return getStats().hitCount();
  }

  @Override
  public long getMissCount() {
    return getStats().missCount();
  }

  @Override
  public long getLoadSuccessCount() {
    return getStats().loadSuccessCount();
  }

  @Override
  public long getLoadExceptionCount() {
    return getStats().loadExceptionCount();
  }

  @Override
  public long getTotalLoadTimeNanos() {
    return getStats().totalLoadTime();
  }

  @Override
  public long getEvictionCount() {
    return getStats().evictionCount();
  }

  @Override
  public long getSize() {
    return cache.size();
  }
}
