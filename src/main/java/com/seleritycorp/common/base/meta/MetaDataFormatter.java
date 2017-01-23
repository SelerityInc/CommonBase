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

package com.seleritycorp.common.base.meta;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Formatter for different aspects of meta data.
 */
@Singleton
public class MetaDataFormatter {
  private final ApplicationBuildProperties applicationBuildProperties;

  @Inject
  public MetaDataFormatter(ApplicationBuildProperties applicationBuildProperties) {
    this.applicationBuildProperties = applicationBuildProperties;
  }

  /**
   * Formats an HTTP User-Agent header describing this application.
   * 
   * <p>The formatted user agent contains name and version numbers of the application,
   * information about the used OS, and information about the used Java version.
   * 
   * @return the formatted user agent
   */
  public String getUserAgent() {
    StringBuilder sb = new StringBuilder();

    // This application itself
    sb.append(applicationBuildProperties.getArtifactId());
    sb.append('/');
    sb.append(applicationBuildProperties.getVersion());

    // Start of details about this application
    sb.append(" (");

    // The build number
    sb.append("build ");
    sb.append(applicationBuildProperties.getGitDescription());
    sb.append("/");
    sb.append(applicationBuildProperties.getBuildTime());

    sb.append("; ");

    // The operating system
    sb.append(System.getProperty("os.name"));
    sb.append(' ');
    sb.append(System.getProperty("os.version"));
    sb.append(' ');
    sb.append(System.getProperty("os.arch"));

    sb.append("; ");

    // Java platform
    sb.append(System.getProperty("java.vendor"));
    sb.append(" Java ");
    sb.append(System.getProperty("java.version"));

    // End of details about this application
    sb.append(")");

    return sb.toString();
  }
}
