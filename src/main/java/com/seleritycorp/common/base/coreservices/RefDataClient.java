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

package com.seleritycorp.common.base.coreservices;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import com.seleritycorp.common.base.config.ApplicationConfig;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.http.client.HttpException;
import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;

import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * CoreServices client for RDS (Reference Data Service) calls.
 */
@Singleton
public class RefDataClient {
  private static final Log log = LogFactory.getLog(RefDataClient.class);

  private final RawAuthenticatedCoreServiceClient client;
  private final int timeoutMillis;

  /**
   * Creates a client for fetching reference data.
   *
   * @param client The client to call the methods on.
   * @param config The config to use for this instance. 
   */
  @Inject
  public RefDataClient(RawAuthenticatedCoreServiceClient client,
      @ApplicationConfig Config config) {
    this.client = client;
    this.timeoutMillis = (int) config.getDurationMillis("RefDataClient.timeout",
        1200, TimeUnit.SECONDS);
  }

  /**
   * Gets the identifier Json for a given enumType.
   * 
   * @param enumType The enumType to fetch the identifiers for.
   * @return The found identifiers.
   * @throws HttpException for network or other IO issues occur..
   * @throws CallErrorException for server and semantics errors.
   */
  public JsonElement getIdentifiersForEnumType(String enumType)
      throws HttpException, CallErrorException {
    JsonArray params = new JsonArray();
    params.add(enumType);

    log.debug("Getting identifiers for enum type " + enumType + " (timeout: "
        + timeoutMillis + "ms)");

    JsonElement ret = client.authenticatedCall("RefDataHandler.getIdentifiersForEnumType", params,
        timeoutMillis);

    log.debug("Getting identifiers for enum type " + enumType + " done");
    
    return ret;
  }

  /**
   * Gets the identifier Json for a given enumType.
   *
   * @param enumType The enumType to fetch the identifiers for.
   * @param writer Writer to write the JSON result
   * @throws HttpException when network or other IO issues occur
   * @throws CallErrorException when server or semantics errors occur
   */
  public void getIdentifiersForEnumType(String enumType, JsonWriter writer)
          throws HttpException, CallErrorException {
    JsonArray params = new JsonArray();
    params.add(enumType);

    log.debug("Getting identifiers for enum type " + enumType + " (timeout: "
            + timeoutMillis + "ms)");

    client.authenticatedCall("RefDataHandler.getIdentifiersForEnumType", params,
            timeoutMillis, writer);

    log.debug("Getting identifiers for enum type " + enumType + " done");
  }
}
