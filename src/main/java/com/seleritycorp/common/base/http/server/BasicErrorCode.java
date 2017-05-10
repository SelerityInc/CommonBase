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

package com.seleritycorp.common.base.http.server;

/**
 * Basic error codes for http servers.
 */
public enum BasicErrorCode implements ErrorCode {
  E_NOT_FOUND("The URL could not be found."),

  E_FORBIDDEN("You are not allowed to access this URL."),

  E_INTERNAL_SERVER_ERROR("An internal server error occurred."),

  E_WRONG_METHOD("The URL was called with an unexcepted HTTP method. Please check the docs "
      + "on the allowed methods for this endpoint.");

  private final String defaultExplanation; 

  BasicErrorCode(String defaultExplanation) {
    this.defaultExplanation = defaultExplanation;
  }

  @Override
  public String getIdentifier() {
    return this.name();
  }

  @Override
  public String getDefaultReason() {
    return defaultExplanation;
  }
}
