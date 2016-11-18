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

package com.seleritycorp.common.base.meta;

import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BuildProperties {
  private static final Log log = LogFactory.getLog(BuildProperties.class);

  private final String resourceName;

  @SuppressFBWarnings(value = "IS2_INCONSISTENT_SYNC",
      justification = "Properties is thread safe. We use synchronization only to avoid loading "
          + "it twice")
  private Properties properties;

  /**
   * Creates an accessor for the build properties of the given artifact
   * 
   * <p>The build properties are pulled out of the version-less part of META-INF. So if an
   * application gets added twice into a jar, only the dominant one gets exposed.
   * 
   * @param groupId groupId of the artifact to get build properties for
   * @param artifactId artifactId of the artifact to get build properties for
   */
  public BuildProperties(String groupId, String artifactId) {
    StringBuilder sb = new StringBuilder("/META-INF/");

    // Resources do not handle "..", so we handle it manually, as it is needed to reach the
    // "main-application" part (see {@link ApplicationBuildProperties}).
    if (!"..".equals(groupId)) {
      sb.append("jar-info/");
      sb.append(groupId);
      sb.append('/');
    }
    sb.append(artifactId);
    sb.append("/build.properties");
    this.resourceName = sb.toString();

    this.properties = null;
  }

  private synchronized void loadProperties() {
    if (properties == null) {
      properties = new Properties();
      log.debug("Load properties from resource " + resourceName);
      try (InputStream stream = BuildProperties.class.getResourceAsStream(resourceName)) {
        properties.load(stream);
      } catch (IOException e) {
        log.error("Could not load '" + resourceName + "'", e);
      }
    }
  }

  /**
   * Fetches a given key'v value from the build properties
   * 
   * @param key The key to fetch the value for
   * @return The value for key from the build properties.
   */
  public String getProperty(String key) {
    String ret = null;

    if (properties == null) {
      loadProperties();
    }

    if (properties != null) {
      ret = properties.getProperty(key);
    }
    return ret;
  }

  public String getGroupId() {
    return getProperty("groupId");
  }

  public String getArtifactId() {
    return getProperty("artifactId");
  }

  public String getVersion() {
    return getProperty("version");
  }

  public String getName() {
    return getProperty("name");
  }

  public String getGitBranch() {
    return getProperty("git.branch");
  }

  public String getGitHashAbbreviated() {
    return getProperty("git.hash.abbreviated");
  }

  public String getGitDescription() {
    return getProperty("git.hash.abbreviated");
  }

  public String getBuildTime() {
    return getProperty("build.time");
  }
}
