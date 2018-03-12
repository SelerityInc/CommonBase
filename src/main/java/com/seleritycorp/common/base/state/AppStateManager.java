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

package com.seleritycorp.common.base.state;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.seleritycorp.common.base.config.ApplicationPaths;
import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;
import com.seleritycorp.common.base.time.Clock;
import com.seleritycorp.common.base.time.TimeUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppStateManager implements AppStateManagerAccessor {
  private static final Log log = LogFactory.getLog(AppStateManager.class);

  /**
   * The application state file.
   * <p>
   * When writing the state in {@link #writeState()}, it gets written to
   * this file.
   * </p>
   * HA fencers and other interested parties can pick it up to see how the
   * application is feeling.
   */
  final Path statePath;

  /**
   * The path for temporary/partial application state files.
   *
   * <p>As writing content is typically not atomic on most systems, the state
   * file data gets written to this temporary path and then gets moved to
   * {@link #statePath}, which is typically an atomic operation.
   */
  final Path stateTmpPath;

  /**
   * The path for the marker file, if the application is usable.
   *
   * <p>If the application considers itself usable, this file is created.
   * Otherwise, it gets deleted.
   */
  final Path stateUsablePath;

  /**
   * The path for the draining marker file.
   *
   * <p>If that path exists (e.g.: Some user or service created it), the
   * application should get drained of traffic.
   *
   * <p>The presence of this file forces the application into FAULTY state.
   */
  final Path stateDrainPath;

  /**
   * The path for the state override file.
   *
   * <p>If that path exists (e.g.: Some user or service created it), the first
   * line of its content is used as stat that is forced onto the application
   * (ignoring state from other facets).
   */
  final Path stateOverridePath;

  /**
   * The facets that contribute to the application state
   *
   * <p>The List implementations has to be thread-safe.
   */
  final ConcurrentMap<String, AppStateFacetCapsule> facets;

  /**
   * The main state of the application
   *
   * <p>This facet is not different than all other facets registered with this
   * AppStateManager. But for applications that do not wish to mess with
   * facets, it allows setting the state directly on the AppStateManager
   * instance.
   */
  final AppStatePushFacet mainFacet;

  /**
   * The draining state of the application
   *
   * <p>This facet is not different than all other facets registered with this
   * AppStateManager. The presence of {@link #stateDrainPath} feeds this
   * facet upon calling {@link readDrainingState}
   */
  final AppStatePushFacet drainFacet;

  /**
   * The override state of the application
   *
   * <p>If this facet is set in overriding state, it overrides any state from
   * other facets. This is useful to force an application into production
   * even though the app itself thinks it is in a faulty state. Another use
   * case is to force the least faulty application into production if all
   * instances are broken to some degree.
   */
  final OverridingAppStatePushFacet overrideFacet;

  /**
   * The AppState from previous call to getAppState.
   *
   * <p>This state is used to be able to detect state changes
   */
  private AppState previousState;

  /**
   * Time utils for timestamp formatting.
   */
  private final TimeUtils timeUtils;

  /**
   * Clock to set mtime from.
   */
  private final Clock clock;

  /**
   * Spawn State Manager.
   * 
   * @param paths the Application paths for this application
   * @param timeUtils the TimeUtils to use for timestamp formatting
   * @param clock the Clock to set mtime from
   */
  @Inject
  public AppStateManager(ApplicationPaths paths, TimeUtils timeUtils, Clock clock) {
    this.statePath = paths.getDataStatePath().resolve("app-state");
    String statePathString = this.statePath.toString();
    this.stateTmpPath = Paths.get(statePathString + ".tmp");
    this.stateUsablePath = Paths.get(statePathString + ".usable");
    this.stateDrainPath = Paths.get(statePathString + ".drain");
    this.stateOverridePath = Paths.get(statePathString + ".override");
    this.facets = new ConcurrentHashMap<>();
    this.previousState = AppState.INITIALIZING;
    this.timeUtils = timeUtils;
    this.clock = clock;
    this.mainFacet = this.createRegisteredAppStatePushFacet("main");
    if (this.mainFacet == null) {
      // This should never happen.
      // But if it does for whatever reason, we'd see unhelpful
      // NullPointerExceptions when setting state. So we better guard
      // against it nonetheless.
      throw new IllegalStateException(
          "Failed to register main facet " + "with AppStateManager " + this);
    }
    this.drainFacet = this.createRegisteredAppStatePushFacet("draining");
    if (this.drainFacet == null) {
      // This should never happen.
      // But if it does for whatever reason, we'd see unhelpful
      // NullPointerExceptions when setting state. So we better guard
      // against it nonetheless.
      throw new IllegalStateException(
          "Failed to register draining facet" + " with AppStateManager " + this);
    }
    this.overrideFacet = new OverridingAppStatePushFacet();
    this.registerAppStateFacet("state-override", this.overrideFacet);
  }

  /**
   * Creates a new, registered AppStatePushFacet
   *
   * <p>The facet is initially in state INITIALIZING.
   * 
   * @param name The name to register the facet at
   * @return The registered facet, or null if registering failed.
   */
  public AppStatePushFacet createRegisteredAppStatePushFacet(String name) {
    AppStatePushFacet facet = new AppStatePushFacet();
    if (!registerAppStateFacet(name, facet)) {
      facet = null;
    }
    return facet;
  }

  /**
   * Registers a AppStateFacet for this AppStateManager
   *
   * <p>Upon successful registeration, the AppStateFacet will contribute to
   * the application state reported by this AppStateManager.
   * 
   * @param name The name to register the facet under
   * @param facet The facet to register
   * @return True, if the registering was successful. False otherwise.
   */
  public boolean registerAppStateFacet(String name, AppStateFacet facet) {
    String safeName = name.replaceAll("[^a-zA-Z0-9]", "-");
    AppStateFacetCapsule capsule = new AppStateFacetCapsule(safeName, facet);
    AppStateFacetCapsule oldCapsule = facets.putIfAbsent(safeName, capsule);

    // If facets map did not have an entry at that key, or it had the very
    // same object, the facet is now registered.
    boolean ret = (oldCapsule == null) || (oldCapsule.getAppStateFacet() == facet);

    if (!ret) {
      // Registering the facet did not work. We swallow
      log.error("Could not register " + facet + " for name '" + safeName + "' as that is taken by "
          + oldCapsule.getAppStateFacet() + " already");
    }
    return ret;
  }

  @Override
  public void setMainAppState(AppState state) {
    mainFacet.setAppState(state);
  }

  @Override
  public AppState getAppState() {
    AppState state;
    if (facets.isEmpty()) {
      // This should never happen, as the main facet should always be
      // there. But, if it's missing, something went wrong. So we
      // gracefully flag an error and a faulty state.
      log.error("No facets in AppStateManager");
      state = AppState.FAULTY;
    } else if (overrideFacet.isOverride()) {
      state = overrideFacet.getAppState();
    } else {
      state = AppState.READY; // Initialize with State of lowest
      // possible weight. That way, combining other states will pick
      // the more severe states up.
      for (AppStateFacetCapsule capsule : facets.values()) {
        AppState facetState = AppState.FAULTY;;
        try {
          facetState = capsule.getAppState();
        } catch (Exception e) {
          // Keep the default state that signals issues already.
        }
        state = state.combine(facetState);
      }
    }

    if (previousState != state) {
      String message = "Application state changed from '" + previousState + "' to '" + state + "'.";
      if (previousState.isUsable() && !state.isUsable()) {
        log.error(message);
      } else {
        log.info(message);
      }
    }
    previousState = state;
    return state;
  }


  @Override
  public boolean isAppInitializing() {
    return getAppState() == AppState.INITIALIZING;
  }

  @Override
  public boolean isAppReady() {
    return getAppState() == AppState.READY;
  }

  @Override
  public boolean isAppWarning() {
    return getAppState() == AppState.WARNING;
  }

  @Override
  public boolean isAppFaulty() {
    return getAppState() == AppState.FAULTY;
  }

  @Override
  public boolean isAppUsable() {
    return getAppState().isUsable();
  }

  @Override
  public boolean isAppUnusable() {
    return !getAppState().isUsable();
  }

  /**
   * Writes the current state to the state file.
   */
  private void persistStateFile(AppState cachedState) {
    try {
      Files.write(stateTmpPath, getStatusReport().getBytes(UTF_8));
      try {
        Files.move(stateTmpPath, statePath, StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception e) {
        log.warn("Could not move " + stateTmpPath + " onto " + statePath, e);
      }
    } catch (Exception e) {
      log.warn("Could not materialize state " + stateTmpPath, e);
    }
  }

  /**
   * Manages the file that flags usablity.
   */
  private void persistUsableFile(AppState cachedState) {
    if (cachedState.isUsable()) {
      try {
        Files.createFile(stateUsablePath);
      } catch (FileAlreadyExistsException e) {
        // As the file already exists, we have to update the mtime, to
        // mark it as "recent" for HA fencers.
        FileTime time = FileTime.fromMillis(clock.getMillisEpoch());
        try {
          Files.setLastModifiedTime(stateUsablePath, time);
        } catch (Exception e2) {
          log.warn("Could not update mtime on " + stateUsablePath, e2);
        }
      } catch (Exception e) {
        log.warn("Could not delete " + stateUsablePath, e);
      }
    } else {
      try {
        // This operation is critical, as it tells HA fencers to no
        // longer consider that application healthy. Hence, if it
        // fails (other than the file not existing in first place),
        // we want to log errors to get people alerted.
        Files.deleteIfExists(stateUsablePath);
      } catch (Exception e) {
        log.error("Could not delete " + stateUsablePath, e);
      }
    }
  }

  /**
   * Persists the state to disk.
   */
  void persistState() {
    // We're caching state to avoid unnessarily calling getAppState twice
    // on each facet for each run of this method.
    AppState cachedState = getAppState();
    persistStateFile(cachedState);
    persistUsableFile(cachedState);
  }


  /**
   * Reads the drain path and uptades the drain state accordingly.
   */
  void readDrainState() {
    if (Files.exists(stateDrainPath)) {
      drainFacet.setAppState(AppState.FAULTY, "The draining file '" + stateDrainPath
          + "' exists, hence draining by marking " + "FAULTY");
    } else {
      drainFacet.setAppState(AppState.READY);
    }
  }

  /**
   * Reads the override path sets override status accordingly.
   */
  void readOverrideState() {
    AppState state = null;
    if (Files.exists(stateOverridePath)) {
      try {
        List<String> content = Files.readAllLines(stateOverridePath, Charset.defaultCharset());
        if (content.size() >= 1) {
          String trimmedContent = content.get(0).trim();
          try {
            state = AppState.valueOf(trimmedContent);
            overrideFacet.setOverride(state);
          } catch (IllegalArgumentException e) {
            overrideFacet.setOverride(AppState.FAULTY, "State '" + trimmedContent + "' (found in: "
                + stateOverridePath + " ) does not exist");
          } catch (NullPointerException e) {
            overrideFacet.setOverride(AppState.FAULTY,
                "Encountered null trying to parse " + stateOverridePath);
          }
        } else {
          // No content in the file. That's probably a gone wrong
          // attempt to drop the override, so we remove the override.
          overrideFacet.resetOverride();
        }
      } catch (IOException e) {
        // We failed to read the file.
        // So we should set the state to FAULTY. Except, if the
        // override file went away between checking existence and
        // trying to read. So we double check that to avoid
        // unintentionally forcing FAULTY state.
        if (Files.exists(stateOverridePath)) {
          // The override file went away. So we're good.
          overrideFacet.resetOverride();
        } else {
          overrideFacet.setOverride(AppState.FAULTY, "Override file exists, but cannot be read");
        }
      }
    } else {
      // override file does not exist
      overrideFacet.resetOverride();
    }
  }

  /**
   * Sets internal facets relying on paths.
   */
  void readStatePaths() {
    readDrainState();
    readOverrideState();
  }

  /**
   * Prepares a detailed multiline plain text status report of the state.
   *
   * <p>This report may run off of cached data, so it need not give second
   * precision. But it is expected to give &lt;10 seconds precision.
   *
   * <p>The main purpose of this report is to aid debugging.
   * 
   * @return The plain text status report
   */
  public String getStatusReport() {
    AppState cachedState = getAppState();

    StringBuilder sb = new StringBuilder();
    sb.append(cachedState);
    sb.append("\n");

    sb.append("\n");

    sb.append("Application state: " + cachedState);
    sb.append("\n");

    sb.append("Application state report time: ");
    sb.append(timeUtils.formatTimeNanos());
    sb.append("\n");

    sb.append("\n");

    sb.append("Application state details:");
    sb.append("\n");
    for (Map.Entry<String, AppStateFacetCapsule> entry : facets.entrySet()) {
      String name = entry.getKey();
      AppStateFacet facet = entry.getValue().getAppStateFacet();

      AppState state = AppState.FAULTY;
      String annotation = null;
      try {
        state = facet.getAppState();
        if (facet instanceof AnnotatedAppStateFacet) {
          try {
            annotation = ((AnnotatedAppStateFacet) facet).getAppStateAnnotation();
          } catch (Exception e) {
            // Not setting state to FAULTY, as the application
            // state already got rendered, and overriding it
            // would not match the state returned by this state
            // manager's getAppState.
            log.warn("Getting annotation for facet " + name + " failed", e);
            annotation = "Getting annotation threw " + e.toString();
          }
        }
      } catch (Exception e) {
        log.warn("Getting state for facet " + name + " failed", e);
        state = AppState.FAULTY;
        annotation = "Getting state threw " + e.toString();
      }
      if (annotation == null) {
        annotation = "";
      }

      sb.append(String.format("  %1$-14s %2$-14s %3$s", state.toString(), name, annotation));
      sb.append("\n");
    }

    return sb.toString();
  }

  private static class AppStateFacetCapsule {
    private final String name;
    private final AppStateFacet facet;
    private AppState previousState;

    private AppStateFacetCapsule(String name, AppStateFacet facet) {
      this.name = name;
      this.facet = facet;
      this.previousState = getAppStateUncapsuled();
    }

    /**
     * Get the facet's current state without messing with the capsule
     *
     * <p>If the facet reports the null state, it gets translated to FAULTY.
     * 
     * @return The facet's current state.
     */
    private AppState getAppStateUncapsuled() {
      AppState state = facet.getAppState();
      if (state == null) {
        state = AppState.FAULTY;
      }
      return state;
    }

    /**
     * Gets the facet's current state while keeping track of changes
     *
     * <p>Changes of state get logged.
     *
     * <p>If the facet reports the null state, it gets translated to FAULTY.
     * 
     * @return The facet's current state
     */
    public AppState getAppState() {
      AppState state = getAppStateUncapsuled();
      if (previousState != state) {
        String message = "AppStateFacet '" + name + "' changed state from '" + previousState
            + "' to '" + state + "'";
        if (previousState.isUsable() && !state.isUsable()) {
          log.error(message);
        } else {
          log.info(message);
        }
      }
      previousState = state;
      return state;
    }

    public AppStateFacet getAppStateFacet() {
      return facet;
    }
  }

  private class OverridingAppStatePushFacet extends AppStatePushFacet {
    boolean override;

    public OverridingAppStatePushFacet() {
      resetOverride();
    }

    public boolean isOverride() {
      return override;
    }

    public void setOverride(AppState state) {
      setOverride(state, "State override in place. See file" + stateOverridePath);
    }

    public void setOverride(AppState state, String annotation) {
      setAppState(state, annotation);
      override = true;
    }

    public void resetOverride() {
      setAppState(AppState.READY,
          "No overriding. Write state into " + stateOverridePath + " to override.");
      override = false;
    }
  }
}
