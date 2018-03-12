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

package com.seleritycorp.common.base.http.server;

/**
 * Interface for error codes returned from a http server.
 */
public interface ErrorCode {
  /**
   * Gets the textual identifier for the identifier.
   * 
   * <p>This string has to be all caps and contain only letters, digits and underscores.
   *
   * @return the textual identifier for the error code.
   */
  public String getIdentifier();

  /**
   * Gets the default explanation of what the error stands for.
   * 
   * <p>This explanation is sent to the caller if issue does not provide an explicit explanation.
   *
   * @return the default explanation of what the error stands for.
   */
  public String getDefaultReason();
}
