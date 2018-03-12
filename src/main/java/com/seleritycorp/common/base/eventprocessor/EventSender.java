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

package com.seleritycorp.common.base.eventprocessor;

import com.google.gson.JsonObject;

import com.seleritycorp.common.base.config.ApplicationConfig;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.http.client.HttpException;
import com.seleritycorp.common.base.http.client.HttpRequestFactory;
import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;
import com.seleritycorp.common.base.uuid.UuidGenerator;

import org.apache.http.HttpStatus;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Helper to send Events to Event Processor.
 */
public class EventSender {
  private static final Log log = LogFactory.getLog(EventSender.class);

  private final UuidGenerator uuidGenerator;
  private final String targetUrl;
  private final HttpRequestFactory requestFactory;

  /**
   * Creates a Sender for EventProcessing events.
   * 
   * @param config The application config to use.
   * @param uuidGenerator Used for request ids.
   * @param requestFactory Factory for Http requests.
   */
  @Inject
  public EventSender(@ApplicationConfig Config config, UuidGenerator uuidGenerator,
      HttpRequestFactory requestFactory) {
    this.uuidGenerator = uuidGenerator;
    this.targetUrl = config.get("EventProcessor.beaconUrl",
        "https://event.seleritycorp.com/beacon");
    this.requestFactory = requestFactory;
  }

  /**
   * Send an Event to EventProcessor while ignoring eventual issues.
   *
   * <p>If there are issues with sending, they'll be logged, but they won't be escalated to the
   * caller.
   *
   * <p>Use this method for fire-and-forget events. If you want to check that atleast that the
   * Http aspect of sending worked, see {@link #sendChecked(String, int, JsonObject)} instead.
   *
   * @param schema The name of the schema to send an event for
   * @param schemaVersion The version number for the schema
   * @param payload The payload to send.
   * @return The UUID of the sent event.
   */
  public UUID send(String schema, int schemaVersion, JsonObject payload) {
    UUID uuid = uuidGenerator.generate();
    try {
      sendChecked(uuid, schema, schemaVersion, payload);
    } catch (Exception e) {
      log.info("Failed to send beacon for " + schema + " (version " + schemaVersion + ") to "
          + targetUrl, e);
    }
    return uuid;
  }

  /**
   * Send an Event to EventProcessor and escalates errors to the caller.
   *
   * <p>If no exceptions get thrown, this does not mean that the sent event passed validation.
   * It only means that on the HTTP layer everything worked. Event Processor is responding
   * before event validation takes place. So you need to check on the Event Processor side to
   * see if validation passes.
   *
   * <p>A fire-and-forget variant of sending events can be found at
   * {@link #send(String, int, JsonObject)}.
   *   
   * @param schema The name of the schema to send an event for
   * @param schemaVersion The version number for the schema
   * @param payload The payload to send.
   * @return The UUID of the sent event.
   * @throws HttpException if there are errors sending the request.
   */
  public UUID sendChecked(String schema, int schemaVersion, JsonObject payload)
      throws HttpException {
    UUID uuid = uuidGenerator.generate();
    sendChecked(uuid, schema, schemaVersion, payload);
    return uuid;
  }

  /**
   * Send an Event to EventProcessor and escalates errors to the caller.
   *
   * <p>If no exceptions get thrown, this does not mean that the sent event passed validation.
   * It only means that on the HTTP layer everything worked. Event Processor is responding
   * before event validation takes place. So you need to check on the Event Processor side to
   * see if validation passes.
   *   
   * @param schema The name of the schema to send an event for
   * @param schemaVersion The version number for the schema
   * @param payload The payload to send.
   * @throws HttpException if there are errors sending the request.
   */
  private void sendChecked(UUID uuid, String schema, int schemaVersion, JsonObject payload)
      throws HttpException {
    JsonObject data = new JsonObject();
    data.addProperty("schema", schema);
    data.addProperty("schemaVersion", schemaVersion);
    data.addProperty("uuid", uuid.toString());
    data.add("payload", payload);
    requestFactory.createPostJson(targetUrl, data)
      .setExpectedStatusCode(HttpStatus.SC_NO_CONTENT)
      .execute();
  }
}
