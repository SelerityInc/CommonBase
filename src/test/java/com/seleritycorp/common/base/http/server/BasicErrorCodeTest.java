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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class BasicErrorCodeTest {
  @Test
  public void testDefaultReasonWrongMethod() {
    assertThat(BasicErrorCode.E_WRONG_METHOD.getDefaultReason()).isNotEmpty();
  }

  @Test
  public void testDefaultReasonNotFound() {
    assertThat(BasicErrorCode.E_NOT_FOUND.getDefaultReason()).isNotEmpty();
  }

  @Test
  public void testDefaultReasonForbidden() {
    assertThat(BasicErrorCode.E_FORBIDDEN.getDefaultReason()).isNotEmpty();
  }
}
