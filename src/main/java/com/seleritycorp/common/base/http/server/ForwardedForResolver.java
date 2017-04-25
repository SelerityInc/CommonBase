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
 * Resolves remote addresses of requests while considering X-Forwarded-For headers.
 */
public class ForwardedForResolver {
  /**
   * Checks whether an address is a valid IP address
   * 
   * <p>Currently, only IPv4 addresses are considered valid.
   * 
   * @param ip The address to check
   * @return true, if the address is considered valid. false, otherwise.
   */
  boolean isIp(String ip) {
    boolean ret = false;
    if (ip != null && !ip.isEmpty()) {
      String[] octets = ip.split("\\.");
      if (octets.length == 4) {
        ret = true;
        for (int idx = 0; idx < 4 && ret; idx++) {
          try {
            int octet = Integer.parseInt(octets[idx]);
            ret &= (octet >= 0) && (octet <= 255);
          } catch (Exception e) {
            ret = false;
          }
        }
      }
    }
    return ret;
  }

  /**
   * Checks if an IP is trusted to set good X-Forwarded-For headers
   * 
   * @param address The address to check
   * @return true, if the ip is considered trusted to set good X-Forwarded-For headers.
   *     false, otherwise.  
   */
  boolean isTrustedIp(String ip) {
    boolean ret = false;
    ret |= ip.startsWith("10.");
    ret |= ip.startsWith("127.");
    ret |= ip.startsWith("192.168.");
    return ret;
  }

  /**
   * Resolve and address using X-Forwarded-For information
   * 
   * <p>The X-Forwarded-For is not fully resolved to avoid getting mislead by hosts setting bogus
   * X-Forwarded-For headers. So we only follow X-Forwarded-For values of trusted hosts. (Whether
   * or not a host is trusted follows the rules of {@link #isTrustedIp(String)})
   * 
   * @param remoteAddr The remote address to resolve
   * @param forwardedFor The request's corresponding X-Forwarded-For header
   * @return The resoved address
   */
  public String resolve(String remoteAddr, String forwardedFor) {
    String ret = remoteAddr;
    if (forwardedFor != null && !forwardedFor.isEmpty() && isTrustedIp(remoteAddr)) {
      // X-Forwarded-For resolving needed
      String[] headerIps = forwardedFor.split(",");
      boolean continueSearch = true;
      for (int idx = headerIps.length - 1; idx >= 0 && continueSearch; idx--) {
        String ip = headerIps[idx].trim();
        if (isIp(ip)) {
          ret = ip;
          if (!isTrustedIp(ip)) {
            continueSearch = false;
          }
        } else {
          continueSearch = false;
        }
      }
    }
    return ret;
  }
}
