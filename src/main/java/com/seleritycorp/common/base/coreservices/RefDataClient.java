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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RefDataClient {
  private final RawAuthenticatedCoreServiceClient client;

  @Inject
  public RefDataClient(RawAuthenticatedCoreServiceClient client) {
    this.client = client;
  }

  /**
   * Gets the identifier Json for a given enumType.
   * 
   * @param enumType The enumType to fetch the identifiers for.
   * @return The found identifiers.
   * @throws IOException for network or other IO issues occur..
   * @throws CallErrorException for server and semantics errors.
   */
  public JsonElement getIdentifiersForEnumType(String enumType)
      throws IOException, CallErrorException {
    JsonArray params = new JsonArray();
    params.add(enumType);

    return client.authenticatedCall("RefDataHandler.getIdentifiersForEnumType", params);
  }
}
