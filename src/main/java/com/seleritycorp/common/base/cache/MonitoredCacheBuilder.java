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

package com.seleritycorp.common.base.cache;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.inject.assistedinject.Assisted;

import com.seleritycorp.common.base.jmx.MBeanUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

/**
 * Builder for a cache that has its metrics exposed through JMX
 * 
 * <p>Additionally, the cache gets hooked up with our environment's clock.
 *
 * @param <K> Class for the keys of the cache.
 * @param <V> Class for the values of the cache.
 */
public class MonitoredCacheBuilder<K, V> {
  public interface Factory {
    MonitoredCacheBuilder<Object, Object> create(String name);
  }

  private static AtomicInteger builtCount = new AtomicInteger();

  @SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC", justification = "Synchronization is only "
      + "needed for setting")
  private String name; 
  private CacheBuilder<? super K, ? super V> builder;
  private final CacheMetrics.Factory cacheMetricsFactory;

  /**
   * Create a cache builder.
   * 
   * @param name Name to use when exposing the cache metrics
   * @param ticker The ticker to use for the cache.
   * @param cacheMetricsFactory Factory to create metrics for the cache.
   */
  @Inject
  MonitoredCacheBuilder(@Assisted String name, Ticker ticker,
      CacheMetrics.Factory cacheMetricsFactory) {
    this.name = name;
    this.cacheMetricsFactory = cacheMetricsFactory;

    builder = CacheBuilder.newBuilder();

    // Switch to our environment's ticker 
    builder.ticker(ticker);

    // Turn on recording stats
    builder.recordStats();
    
  }

  private MonitoredCacheBuilder(String name, CacheBuilder<K, V> builder,
      CacheMetrics.Factory cacheMetricsFactory) {
    this.name = name;
    this.builder = builder;
    this.cacheMetricsFactory = cacheMetricsFactory;
  }

  /**
   * Sets the default concurrency level for a cache.
   * 
   * <p>This method has the same semantics as {@link CacheBuilder#concurrencyLevel(int)}.
   *
   * @param concurrencyLevel The default concurrency level.
   * @return The builder to continue building with.
   */
  public MonitoredCacheBuilder<K, V> concurrencyLevel(int concurrencyLevel) {
    builder = builder.concurrencyLevel(concurrencyLevel);
    return this;
  }
  
  /**
   * Sets the time period after which entries should be evicted if they were not accessed recently.
   * 
   * <p>This method has the same semantics as {@link CacheBuilder#expireAfterAccess(long, TimeUnit)}
   *
   * @param duration The number of time units after which to consider an element evictable if it
   *     has not been acccessed.  
   * @param unit The units for the above duration. 
   * @return The builder to continue building with.
   */
  public MonitoredCacheBuilder<K, V> expireAfterAccess(long duration, TimeUnit unit) {
    builder = builder.expireAfterAccess(duration, unit);
    return this;
  }

  /**
   * Sets the time period after which to evict entries again.
   * 
   * <p>This method has the same semantics as {@link CacheBuilder#expireAfterWrite(long, TimeUnit)}.
   *
   * @param duration The number of time units after which to consider an element evictable.
   * @param unit The units for the above duration. 
   * @return The builder to continue building with.
   */
  public MonitoredCacheBuilder<K, V> expireAfterWrite(long duration, TimeUnit unit) {
    builder = builder.expireAfterWrite(duration, unit);
    return this;
  }

  /**
   * Sets the initial capacity for the cache.
   * 
   * <p>This method has the same semantics as {@link CacheBuilder#initialCapacity(int)}.
   *
   * @param initialCapacity The initial capacity to set for the cache.
   * @return The builder to continue building with.
   */
  public MonitoredCacheBuilder<K, V> initialCapacity(int initialCapacity) {
    builder = builder.initialCapacity(initialCapacity);
    return this;
  }

  /**
   * Sets the maximum number of elements in the cache.
   * 
   * <p>This method has the same semantics as {@link CacheBuilder#maximumSize(long)}.
   *
   * @param size The maximum capacity to set for the cache.
   * @return The builder to continue building with.
   */
  public MonitoredCacheBuilder<K, V> maximumSize(long size) {
    builder = builder.maximumSize(size);
    return this;
  }

  /**
   * Sets the time period after which loaded element should get reloaded.
   * 
   * <p>This method has the same semantics as {@link CacheBuilder#refreshAfterWrite(long, TimeUnit)}
   *
   * @param duration The number of time units after which to consider an element should be
   *     reloaded.
   * @param unit The units for the above duration. 
   * @return The builder to continue building with.
   */
  public MonitoredCacheBuilder<K, V> refreshAfterWrite(long duration, TimeUnit unit) {
    builder = builder.refreshAfterWrite(duration, unit);
    return this;
  }

  /**
   * Sets a call-back upon eviction of entries.
   * 
   * @param <K1> Class for the keys of the cache.
   * @param <V1> Class for the values of the cache.
   * @param listener The call-back to call upon eviction.
   * @return The builder to continue building with.
   */
  public <K1 extends K, V1 extends V> MonitoredCacheBuilder<K1, V1> removalListener(
      RemovalListener<? super K1, ? super V1> listener) {
    return new MonitoredCacheBuilder<K1, V1>(name, builder.removalListener(listener),
        cacheMetricsFactory);
  }

  /**
   * Sets the name for the to-be-built cache.
   *
   * @param name The name for the to-be-built cache.
   * @return The current builder.
   */
  public synchronized MonitoredCacheBuilder<K, V> name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Builds the cache that loads needed entries automatically.
   * 
   * <p>This method has the same semantics as {@link CacheBuilder#build(CacheLoader)}.
   * 
   * <p>After building a cache, the builder cannot be used to build another cache. This is to
   * avoid accidental registering of two caches using thesame name. 
   *
   * @param <K1> Class for the keys of the cache.
   * @param <V1> Class for the values of the cache.
   * @param loader The loader for new values.
   * @return The created cache.
   */
  public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(
      CacheLoader<? super K1, V1> loader) {
    LoadingCache<K1, V1> ret = builder.build(loader);
    postBuildSteps(ret);
    return ret;
  }

  /**
   * Builds the cache only yields manually set entiry.
   * 
   * <p>This method has the same semantics as {@link CacheBuilder#build()}.
   *
   * <p>After building a cache, the builder cannot be used to build another cache. This is to
   * avoid accidental registering of two caches using thesame name.
   *  
   * @param <K1> Class for the keys of the cache.
   * @param <V1> Class for the values of the cache.
   * @return The created cache.
   */
  public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
    Cache<K1, V1> ret = builder.build();
    postBuildSteps(ret);
    return ret;
  }
  
  private synchronized <K1 extends K, V1 extends V> void postBuildSteps(Cache<K1, V1> cache) {
    int count = builtCount.incrementAndGet();
    if (name == null) {
      name = "unnamed-" + count;
    }
    CacheMetrics cacheMetrics = cacheMetricsFactory.create(cache);
    String jmxName = "com.seleritycorp.common.base.cache:type=MonitoredCache,name=" + name;
    MBeanUtils.register(jmxName, cacheMetrics);
    name = null;
  }
}
