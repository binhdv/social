/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.core.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestSuite;

import org.exoplatform.component.test.KernelBootstrap;
import org.exoplatform.social.core.application.ProfileUpdatesPublisherTest;
import org.exoplatform.social.core.application.RelationshipPublisherTest;
import org.exoplatform.social.core.application.SpaceActivityPublisherTest;
import org.exoplatform.social.core.listeners.SocialUserProfileEventListenerImplTest;
import org.exoplatform.social.core.manager.ActivityManagerTest;
import org.exoplatform.social.core.manager.IdentityManagerTest;
import org.exoplatform.social.core.manager.RelationshipManagerTest;
import org.exoplatform.social.core.processor.MentionsProcessorTest;
import org.exoplatform.social.core.processor.OSHtmlSanitizerProcessorTest;
import org.exoplatform.social.core.processor.TemplateParamsProcessorTest;
import org.exoplatform.social.core.service.LinkProviderTest;
import org.exoplatform.social.core.space.SpaceAccessTest;
import org.exoplatform.social.core.space.SpaceLifeCycleTest;
import org.exoplatform.social.core.space.SpaceUtilsRestTest;
import org.exoplatform.social.core.space.spi.SpaceServiceTest;
import org.exoplatform.social.core.storage.ActivityStorageTest;
import org.exoplatform.social.core.storage.IdentityStorageTest;
import org.exoplatform.social.core.storage.RelationshipStorageTest;
import org.exoplatform.social.core.storage.SpaceStorageTest;
import org.exoplatform.social.core.storage.cache.CachedActivityStorageTestCase;
import org.exoplatform.social.core.storage.cache.CachedIdentityStorageTestCase;
import org.exoplatform.social.core.storage.cache.CachedRelationshipStorageTestCase;
import org.exoplatform.social.core.storage.cache.CachedSpaceStorageTestCase;
import org.exoplatform.social.core.storage.impl.ActivityStorageImplTestCase;
import org.exoplatform.social.core.storage.impl.IdentityStorageImplTestCase;
import org.exoplatform.social.core.storage.impl.RelationshipStorageImplTestCase;
import org.exoplatform.social.core.test.AbstractCoreTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Created by The eXo Platform SAS
 * Author : thanh_vucong
 *          thanh_vucong@exoplatform.com
 * Nov 8, 2012  
 */
@RunWith(Suite.class)

@SuiteClasses({
  //ActivityManagerTest.class,
  //ActivityStorageTest.class,
  //ActivityStorageImplTestCase.class,
  //CachedActivityStorageTestCase.class,
  //IdentityManagerTest.class,
  //IdentityStorageImplTestCase.class,
  //IdentityStorageTest.class,
  //CachedIdentityStorageTestCase.class,
  //SpaceServiceTest.class,
  SpaceStorageTest.class,
  CachedSpaceStorageTestCase.class
  //SpaceAccessTest.class
  //RelationshipManagerTest.class,
  //CachedRelationshipStorageTestCase.class,
  //RelationshipStorageTest.class,
  //RelationshipPublisherTest.class,
  //RelationshipStorageImplTestCase.class,
  //SpaceUtilsRestTest.class,
  //SpaceActivityPublisherTest.class,
  //SpaceLifeCycleTest.class,
  //SocialUserProfileEventListenerImplTest.class,
  //OSHtmlSanitizerProcessorTest.class,
  //TemplateParamsProcessorTest.class,
  //ProfileUpdatesPublisherTest.class,
  //MentionsProcessorTest.class,
  //LinkProviderTest.class
  })
/**
@SuiteClasses({
  ActivityManagerSampleTest.class,
  RelationshipManagerSampleTest.class,
  SpaceServiceSampleTest.class
})**/
public class InitContainerTestSuite {

  /** . */
  private static KernelBootstrap bootstrap;

  /** . */
  private static final Map<Class<?>, AtomicLong> counters = new HashMap<Class<?>, AtomicLong>();

  
  @BeforeClass
  public static void setUp() throws Exception {
    beforeSetup();
    System.out.println("setting up");
  }

  @AfterClass
  public static void tearDown() {
    System.out.println("tearing down");
    afterTearDown();
  }
  
  private static void beforeSetup() throws Exception {
    Class<?> key = AbstractCoreTest.class;
    //Class<?> key = BaseSocialTestCase.class;

    //
    if (!counters.containsKey(key))
    {
       counters.put(key, new AtomicLong(new TestSuite(AbstractCoreTest.class).testCount()));
       //counters.put(key, new AtomicLong(new TestSuite(BaseSocialTestCase.class).testCount()));

       //
       bootstrap = new KernelBootstrap(Thread.currentThread().getContextClassLoader());

       // Configure ourselves
       bootstrap.addConfiguration(AbstractCoreTest.class);
       //bootstrap.addConfiguration(BaseSocialTestCase.class);

       //
       bootstrap.boot();
       AbstractCoreTest.socialBootstrap = bootstrap;
       //BaseSocialTestCase.socialBootstrap = bootstrap;
    }
  }
  
  private static void afterTearDown() {
    Class<?> key = AbstractCoreTest.class;
    //Class<?> key = BaseSocialTestCase.class;

    //
    if (counters.get(key).decrementAndGet() == 0)
    {
       bootstrap.dispose();

       //
       bootstrap = null;
       
       AbstractCoreTest.socialBootstrap = null;
       //BaseSocialTestCase.socialBootstrap = null;
       
    }
  }
}
