/*
 * Copyright (C) 2016 Selerity, Inc. (support@seleritycorp.com)
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

import com.seleritycorp.common.base.config.ApplicationConfig;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.config.ConfigUtils;
import com.seleritycorp.common.base.time.Clock;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

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
    int tokenTimeoutPeriod = config.getInt("tokenTimeout", 180);
    TimeUnit tokenTimeoutPeriodUnit = TimeUnit.valueOf(config.get("tokenTimeoutUnit", "SECONDS"));
    this.tokenTimeoutPeriodSeconds = tokenTimeoutPeriodUnit.toSeconds(tokenTimeoutPeriod);

    this.token = null;
    this.tokenTimeoutTimestamp = 0;
  }

  /**
   * Call a CoreServices method with passing the authentication token.
   * 
   * @param method The CoreServices method to call
   * @param params The parameters to the method
   * @return The methods result object
   * @throws IOException for network or other IO issues occur..
   * @throws CallErrorException for server and semantics errors.
   */
  public JsonElement authenticatedCall(String method, JsonElement params)
      throws IOException, CallErrorException {
    if (token == null || tokenTimeoutTimestamp < clock.getSecondsEpoch()) {
      // Resetting the token, so in case things go wrong from here, we have a well resetted
      // environment.
      token = null;

      token = authenticationClient.getAuthThoken();
      tokenTimeoutTimestamp = clock.getSecondsEpoch() + tokenTimeoutPeriodSeconds;
    }

    final JsonElement ret;
    try {
      ret = client.call(method, params, token, -1);
    } catch (Exception e) {
      // Some error on the connection. Better reset the token to avoid issues with session
      // timeouts or server switches.
      token = null;
      throw e;
    }
    return ret;
  }
}
