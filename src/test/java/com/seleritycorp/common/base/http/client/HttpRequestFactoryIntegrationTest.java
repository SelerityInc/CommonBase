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

package com.seleritycorp.common.base.http.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.inject.Injector;
import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.test.InjectingTestCase;

public class HttpRequestFactoryIntegrationTest extends InjectingTestCase {
  @Test
  public void testCreatePerformOk() throws Throwable {
    InjectorFactory.register(new HttpClientModule());
    Injector injector = InjectorFactory.getInjector();
    HttpRequestFactory factory = injector.getInstance(HttpRequestFactory.class);
    
    String url = "https://www.google.com/";
    HttpResponse response = null;
    try {
      response = factory.create(url).execute();
    } catch (HttpException e) {
      failInCiSkipOtherwise("Failed to fetch web ", e);
    }

    try {
      assertThat(response.getStatusCode()).isEqualTo(200);
    } catch (Error|Exception e) {
      failInCiSkipOtherwise("Respons is not a 200 response", e);
    }

    try {
      assertThat(response.getBody()).contains("Google");
    } catch (Error|Exception e) {
      failInCiSkipOtherwise("Fetched web page does not contain 'Google'", e);
    }
  }
}
