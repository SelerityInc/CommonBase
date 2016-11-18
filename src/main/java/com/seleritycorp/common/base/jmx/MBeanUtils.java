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

package com.seleritycorp.common.base.jmx;

import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;

import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public class MBeanUtils {
  private static final Log log = LogFactory.getLog(MBeanUtils.class);

  /**
   * Registers an object as MBean.
   *
   * <p>If errors occur during MBean registration, they are logged, but not
   * brought forward to the caller.
   *
   * <p>If another MBean already exists at the given name, it is replaced by
   * our mbean.
   *
   * @param name The name to register the bean at
   * @param mbean The mbean to register
   * @return The name the mbean get registered at. null, if the
   *         registration failed.
   */
  public static ObjectName register(String name, Object mbean) {
    ObjectName ret = null;
    log.debug("Registering MBean for " + name);
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

    // First we need to get an ObjectName to register our MBean at.
    ObjectName objectName = null;
    try {
      objectName = new ObjectName(name);
    } catch (MalformedObjectNameException | NullPointerException e) {
      log.error("Cannot register MBean, as name is invalid: " + name, e);
    }

    if (objectName != null) {
      // We now have a good object name. First unregister an eventually
      // registered MBean, then register our MBean.

      // Unregister eventually previously registered MBean at that name
      unregisterIfRegistered(objectName);

      // And now for the real registering attempt
      try {
        mbs.registerMBean(mbean, objectName);
        ret = objectName;
      } catch (InstanceAlreadyExistsException e) {
        log.error("Failing to register MBean for " + name
            + ", although we unregistered that name before. It "
            + "looks like we're racing against another entity. " + "So we're giving up registering "
            + name, e);
      } catch (MBeanRegistrationException | NotCompliantMBeanException e) {
        log.error("Cannot register MBean for " + name, e);
      }
    }
    return ret;
  }

  /**
   * Registers an object as MBean with a given interface
   *
   * <p>If errors occur during MBean registration, they are logged, but not
   * brought forward to the caller.
   *
   * <p>If another MBean already exists at the given name, it is replaced by
   * our new bean.
   *
   * @param <T> Type of the implementation to register
   * @param name The name to register the bean at
   * @param implementation The implementation to register
   * @param interfaze The interface to register the implementation for.
   * @return The name the implementation got registered at. null, if the
   *         registration failed.
   */
  public static <T> ObjectName register(String name, T implementation, Class<T> interfaze) {
    ObjectName ret = null;
    StandardMBean bean = null;
    try {
      bean = new StandardMBean(implementation, interfaze);
    } catch (NotCompliantMBeanException e) {
      log.error("Failing to turn " + implementation + " into a " + "StandardMBean", e);
    }
    if (bean != null) {
      ret = register(name, bean);
    }
    return ret;
  }

  /**
   * Unregister MBeans at a given ObjectName
   *
   * <p>Errors will get logged, but not thrown.
   * 
   * @param objectName The objectName to unregister MBeans from. If null,
   *        no ObjectName will get unregistered.
   */
  public static void unregisterIfRegistered(ObjectName objectName) {
    if (objectName != null) {
      MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
      try {
        mbs.unregisterMBean(objectName);
      } catch (InstanceNotFoundException e) {
        // Instance did not exist. We only wanted to unregister if
        // objectName has been registered before, so that's ok.
      } catch (MBeanRegistrationException e) {
        // Unregistering failed for other reasons beyond our control.
        // We log, but we carry on nonetheless. We've been
        // opportunistic anyways.
        log.warn(
            "Opportunistic unregistering of MBean at " + objectName.getCanonicalName() + " failed",
            e);
      }
    }
  }
}
