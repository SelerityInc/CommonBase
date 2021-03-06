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

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import com.seleritycorp.common.base.config.ApplicationConfig;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.config.ConfigUtils;
import com.seleritycorp.common.base.http.client.HttpException;
import com.seleritycorp.common.base.time.Clock;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Raw client for calling calling authenticated CoreServices methods.
 */
@Singleton
public class RawAuthenticatedCoreServiceClient {
  private final RawCoreServiceClient client;
  private final AuthenticationClient authenticationClient;
  private final Clock clock;
  private final long tokenTimeoutPeriodSeconds;
  private String token;
  private long tokenTimeoutTimestamp;

  /**
   * Creates a core service caller that transparently authenticates.
   * 
   * @param appConfig The application config to use
   * @param client client for raw CoreService calls.
   * @param authenticationClient client to use for authentication
   * @param clock decides if an authentication token needs to be timed out
   */
  @Inject
  public RawAuthenticatedCoreServiceClient(@ApplicationConfig Config appConfig,
      RawCoreServiceClient client, AuthenticationClient authenticationClient, Clock clock) {
    this.client = client;
    this.authenticationClient = authenticationClient;
    this.clock = clock;

    Config config = ConfigUtils.subconfig(appConfig, "CoreServices");
    this.tokenTimeoutPeriodSeconds = config.getDurationSeconds("tokenTimeout", 180);

    this.token = null;
    this.tokenTimeoutTimestamp = 0;
  }

  /**
   * Call a CoreServices method with passing the authentication token.
   * 
   * @param method The CoreServices method to call
   * @param params The parameters to the method
   * @return The methods result object
   * @throws HttpException for network or other IO issues occur..
   * @throws CallErrorException for server and semantics errors.
   */
  public JsonElement authenticatedCall(String method, JsonElement params)
      throws HttpException, CallErrorException {
    return authenticatedCall(method, params, -1);
  }

  /**
   * Call a CoreServices method with passing the authentication token.
   * 
   * @param method The CoreServices method to call
   * @param params The parameters to the method
   * @param timeoutMillis The read timeout for new data on the connection. Use -1 for the default
   *     timeout.
   * @return The methods result object
   * @throws HttpException for network or other IO issues occur..
   * @throws CallErrorException for server and semantics errors.
   */
  public JsonElement authenticatedCall(String method, JsonElement params, int timeoutMillis)
      throws HttpException, CallErrorException {
    if (token == null || tokenTimeoutTimestamp < clock.getSecondsEpoch()) {
      // Resetting the token, so in case things go wrong from here, we have a well resetted
      // environment.
      token = null;

      token = authenticationClient.getAuthThoken();
      tokenTimeoutTimestamp = clock.getSecondsEpoch() + tokenTimeoutPeriodSeconds;
    }

    final JsonElement ret;
    try {
      ret = client.call(method, params, token, timeoutMillis);
    } catch (Exception e) {
      // Some error on the connection. Better reset the token to avoid issues with session
      // timeouts or server switches.
      token = null;
      throw e;
    }
    return ret;
  }

  /**
   * Calls the CoreServices API with authentication and custom JSON writer to write
   * the result to. Generates a new auth token if not present or invalid.
   *
   * @param method GET or POST
   * @param params HTTP Params to be sent
   * @param timeoutMillis Timeout for connection/socket timeout. Use -1 for the default
   *     timeout.
   * @param writer Writer to write the JSON result
   * @throws HttpException when network or other IO issues occur
   * @throws CallErrorException when server or semantics errors occur
   */
  public void authenticatedCall(String method,
                                JsonElement params,
                                int timeoutMillis,
                                JsonWriter writer)
          throws HttpException, CallErrorException {
    if (token == null || tokenTimeoutTimestamp < clock.getSecondsEpoch()) {
      // Resetting the token, so in case things go wrong from here, we have a well resetted
      // environment.
      token = null;

      token = authenticationClient.getAuthThoken();
      tokenTimeoutTimestamp = clock.getSecondsEpoch() + tokenTimeoutPeriodSeconds;
    }

    try {
      client.call(method, params, token, timeoutMillis, writer);
    } catch (Exception e) {
      // Some error on the connection. Better reset the token to avoid issues with session
      // timeouts or server switches.
      token = null;
      throw e;
    }
  }
}
