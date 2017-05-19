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

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.seleritycorp.common.base.state.AppStateManager;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Http Handler for pages and tasks on all applications. 
 */
public class CommonHttpHandler extends AbstractHttpHandler {
  interface Factory {
    CommonHttpHandler create(AbstractHttpHandler delegate);
  }

  private final AbstractHttpHandler delegateHttpHandler;
  private final AppStateManager appStateManager;
  
  @Inject
  CommonHttpHandler(@Assisted AbstractHttpHandler delegateHttpHandler,
      AppStateManager appStateManager) {
    this.delegateHttpHandler = delegateHttpHandler;
    this.appStateManager = appStateManager;
  }

  @Override
  public void handle(HttpRequest request) throws IOException,
      ServletException {
    switch (request.getTarget()) {
      case "/status":
        if (request.isMethodGet()) {
          String sender = request.getResolvedRemoteAddr();
          if (sender.startsWith("10.") || sender.startsWith("127.")) {
            request.respondOkText(appStateManager.getStatusReport());
          } else {
            request.respondForbidden();
          }
        } else {
          request.respondBadRequest(BasicErrorCode.E_WRONG_METHOD,
              "Target " + request.getTarget() + " expects GET method");
        }
        break;
      default:
        delegateHttpHandler.handle(request);
        break;
    }

    if (!request.hasBeenHandled()) {
      request.respondNotFound();
    }
  }  
}
