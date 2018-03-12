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

package com.seleritycorp.common.base.uuid;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Test;

public class UuidGeneratorImplTest {
  @Test
  public void testGenerateSingle() {
    UuidGeneratorImpl generator = new UuidGeneratorImpl();

    UUID uuid = generator.generate();

    assertThat(uuid).isNotNull();
  }

  @Test
  public void testGenerateMultiple() {
    UuidGeneratorImpl generator = new UuidGeneratorImpl();

    UUID uuid1 = generator.generate();
    UUID uuid2 = generator.generate();

    assertThat(uuid1).isNotEqualTo(uuid2);
    assertThat(uuid1.toString()).isNotEqualTo(uuid2.toString());
  }
}
