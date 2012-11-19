/*
* Copyright (C) 2003-2010 eXo Platform SAS.
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Affero General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see<http://www.gnu.org/licenses/>.
*/
package org.exoplatform.social.core.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.social.common.ActivityFilter;
import org.exoplatform.social.core.activity.model.ActivityStream;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.exoplatform.social.core.space.impl.DefaultSpaceApplicationHandler;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.core.storage.api.ActivityStorage;
import org.exoplatform.social.core.storage.api.IdentityStorage;
import org.exoplatform.social.core.test.AbstractCoreTest;
import org.exoplatform.social.core.test.MaxQueryNumber;
import org.exoplatform.social.core.test.QueryNumberTest;

/**
 * Unit Test for {@link org.exoplatform.social.core.storage.api.ActivityStorage}
 *
 * @author hoat_le
 *
 */
@QueryNumberTest
public class ActivityStorageTest extends AbstractCoreTest {
  private IdentityStorage identityStorage;
  private ActivityStorage activityStorage;
  private IdentityManager identityManager;
  private RelationshipManager relationshipManager;
  private List<ExoSocialActivity> tearDownActivityList;

  private Identity rootIdentity;
  private Identity johnIdentity;
  private Identity maryIdentity;
  private Identity demoIdentity;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    identityStorage = (IdentityStorage) getContainer().getComponentInstanceOfType(IdentityStorage.class);
    activityStorage = (ActivityStorage) getContainer().getComponentInstanceOfType(ActivityStorage.class);
    identityManager = (IdentityManager) getContainer().getComponentInstanceOfType(IdentityManager.class);
    relationshipManager = (RelationshipManager) getContainer().getComponentInstanceOfType(RelationshipManager.class);
    assertNotNull("identityManager must not be null", identityStorage);
    assertNotNull("activityStorage must not be null", activityStorage);
    rootIdentity = new Identity(OrganizationIdentityProvider.NAME, "root");
    johnIdentity = new Identity(OrganizationIdentityProvider.NAME, "john");
    maryIdentity = new Identity(OrganizationIdentityProvider.NAME, "mary");
    demoIdentity = new Identity(OrganizationIdentityProvider.NAME, "demo");
    identityStorage.saveIdentity(rootIdentity);
    identityStorage.saveIdentity(johnIdentity);
    identityStorage.saveIdentity(maryIdentity);
    identityStorage.saveIdentity(demoIdentity);

    assertNotNull("rootIdentity.getId() must not be null", rootIdentity.getId());
    assertNotNull("johnIdentity.getId() must not be null", johnIdentity.getId());
    assertNotNull("maryIdentity.getId() must not be null", maryIdentity.getId());
    assertNotNull("demoIdentity.getId() must not be null", demoIdentity.getId());

    tearDownActivityList = new ArrayList<ExoSocialActivity>();
  }

  @Override
  protected void tearDown() throws Exception {
    for (ExoSocialActivity activity : tearDownActivityList) {
      activityStorage.deleteActivity(activity.getId());
    }
    identityStorage.deleteIdentity(rootIdentity);
    identityStorage.deleteIdentity(johnIdentity);
    identityStorage.deleteIdentity(maryIdentity);
    identityStorage.deleteIdentity(demoIdentity);
    /*assertEquals("assertEquals(activityStorage.getActivities(rootIdentity).size() must be 0",
           0, activityStorage.getActivities(rootIdentity).size());
    assertEquals("assertEquals(activityStorage.getActivities(johnIdentity).size() must be 0",
           0, activityStorage.getActivities(johnIdentity).size());
    assertEquals("assertEquals(activityStorage.getActivities(maryIdentity).size() must be 0",
           0, activityStorage.getActivities(maryIdentity).size());
    assertEquals("assertEquals(activityStorage.getActivities(demoIdentity).size() must be 0",
           0, activityStorage.getActivities(demoIdentity).size());*/
    super.tearDown();
  }



  /**
   * Test {@link org.exoplatform.social.core.storage.api.ActivityStorage.deleteActivity(String activityId)}
   */
  @MaxQueryNumber(200)
  public void testDeleteActivity() throws ActivityStorageException {
    final String activityTitle = "activity Title";

    //Test deleteActivity(String)
    {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle(activityTitle);
      activityStorage.saveActivity(maryIdentity, activity);

      assertNotNull("activity.getId() must not be null", activity.getId());

      activityStorage.deleteActivity(activity.getId());
      try {
        activityStorage.getActivity(activity.getId());
        fail();
      } catch (Exception ase) {
        // ok
      }
    }
    //Test deleteActivity(Activity)
    {
      ExoSocialActivity activity2 = new ExoSocialActivityImpl();
      activity2.setTitle(activityTitle);
      activityStorage.saveActivity(demoIdentity, activity2);

      assertNotNull("activity2.getId() must not be null", activity2.getId());
      activityStorage.deleteActivity(activity2.getId());
    }

  }

  /**
   * Test {@link org.exoplatform.social.core.storage.api.ActivityStorage#saveComment(org.exoplatform.social.core.activity.model.ExoSocialActivity, org.exoplatform.social.core.activity.model.ExoSocialActivity)}
   */
  @MaxQueryNumber(50)
  public void testSaveComment() throws ActivityStorageException {

    //comment on his own activity
    {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("blah blah");
      activityStorage.saveActivity(rootIdentity, activity);

      assertNotNull(activity.getReplyToId());

      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("comment blah");
      comment.setUserId(rootIdentity.getId());

      activityStorage.saveComment(activity, comment);

      assertNotNull(activity.getReplyToId());
      assertEquals(1, activity.getReplyToId().length);

      comment = activityStorage.getActivity(comment.getId());
      assertTrue(comment.isComment());
      
      tearDownActivityList.add(activity);
    }

    // comment on other users' activity
    {

    }

  }

  /**
   * Test {@link org.exoplatform.social.core.storage.api.ActivityStorage#deleteComment(String, String)}
   */
  @MaxQueryNumber(100)
  public void testDeleteComment() throws ActivityStorageException {

    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle("blah blah");
    activityStorage.saveActivity(rootIdentity, activity);

    ExoSocialActivity comment = new ExoSocialActivityImpl();
    comment.setTitle("coment blah blah");
    comment.setUserId(rootIdentity.getId());

    activityStorage.saveComment(activity, comment);

    assertNotNull("comment.getId() must not be null", comment.getId());

    activityStorage.deleteComment(activity.getId(), comment.getId());

    tearDownActivityList.add(activity);
  }

  /**
   * Test {@link org.exoplatform.social.core.storage.api.ActivityStorage#getActivity(String)}
   */
  @MaxQueryNumber(50)
  public void testGetActivity() throws ActivityStorageException {
    final String activityTitle = "activity title";
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(activityTitle);
    activityStorage.saveActivity(demoIdentity, activity);
    tearDownActivityList.add(activity);

    assertNotNull("activity.getId() must not be null", activity.getId());

    assertEquals("demoIdentity.getRemoteId() must return: " + demoIdentity.getRemoteId(), demoIdentity.getRemoteId(), activity.getStreamOwner());

    ExoSocialActivity gotActivity = activityStorage.getActivity(activity.getId());

    assertNotNull("gotActivity.getId() must not be null", gotActivity.getId());

    assertEquals("activity.getId() must return: " + activity.getId(), activity.getId(), gotActivity.getId());

    assertEquals("gotActivity.getTitle() must return: " + gotActivity.getTitle(), activityTitle, gotActivity.getTitle());


    ActivityStream activityStream = activity.getActivityStream();
    assertNotNull("activityStream.getId() must not be null", activityStream.getId());
    assertEquals("activityStream.getPrettyId() must return: " + demoIdentity.getRemoteId(), demoIdentity.getRemoteId(), activityStream.getPrettyId());
    assertEquals(ActivityStream.Type.USER, activityStream.getType());
    assertNotNull("activityStream.getPermaLink() must not be null", activityStream.getPermaLink());

  }

  /**
   * Test {@link org.exoplatform.social.core.storage.ActivityStorage#getUserActivities(org.exoplatform.social.core.identity.model.Identity, long, long)}
   *
   * and {@link org.exoplatform.social.core.storage.ActivityStorage#getUserActivities(org.exoplatform.social.core.identity.model.Identity)}
   *
   */
  @MaxQueryNumber(3600)
  public void testGetActivities() throws ActivityStorageException {
    final int totalNumber = 20;
    final String activityTitle = "activity title";
    //John posts activity to root's activity stream
    for (int i = 0; i < totalNumber; i++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle(activityTitle + i);
      activity.setUserId(johnIdentity.getId());

      activityStorage.saveActivity(rootIdentity, activity);
      tearDownActivityList.add(activity);
    }

    //Till now Root's activity stream has 10 activities posted by John
    assertEquals("John must have zero activity", 0, activityStorage.getUserActivities(johnIdentity, 0, 100).size());
    assertEquals("Root must have " + totalNumber + " activities", totalNumber,
        activityStorage.getUserActivities(rootIdentity, 0, 100).size());

    //Root posts activities to his stream
    for (int i = 0; i < totalNumber; i++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle(activityTitle + i);
      activity.setUserId(rootIdentity.getId());
      activityStorage.saveActivity(rootIdentity, activity);

      //John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("Comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);
      tearDownActivityList.add(activity);
    }
    //Till now Root's activity stream has 40 activities: 20 posted by John and 20 posted by Root
    //, each of those activities posted by Root has 1 comment by John.
    assertEquals("John must have zero activity", 0, activityStorage.getUserActivities(johnIdentity).size());
    assertEquals("Root must have " + totalNumber*2 + " activities", totalNumber*2, activityStorage.getUserActivities(rootIdentity).size());


    // Test ActivityStorage#getActivities(Identity, long, long)
    {
      List<ExoSocialActivity> gotJohnActivityList = activityStorage.getUserActivities(johnIdentity, 0, 50);
      assertEquals("gotJohnActivityList.size() should return 0", 0, gotJohnActivityList.size());

      final int limit = 34;
      assertTrue("root's activities should be greater than " + limit + " for passing test below", activityStorage.getUserActivities(rootIdentity).size() > limit);
      List<ExoSocialActivity> gotRootActivityList = activityStorage.getUserActivities(rootIdentity, 0, limit);
      assertEquals("gotRootActivityList.size() must return " + limit, limit, gotRootActivityList.size());
    }

  }

  /**
   * Test {@link org.exoplatform.social.core.storage.api.ActivityStorage#getNumberOfUserActivities(org.exoplatform.social.core.identity.model.Identity)}
   */
  @MaxQueryNumber(5450)
  public void testGetActivitiesCount() throws ActivityStorageException {

    final int totalNumber = 20;
    //create 20 activities each for root, john, mary, demo.
    for (int i = 0; i < totalNumber; i++) {
      ExoSocialActivity rootActivity = new ExoSocialActivityImpl();
      rootActivity.setTitle("Root activity" + i);
      activityStorage.saveActivity(rootIdentity, rootActivity);

      tearDownActivityList.add(rootActivity);

      ExoSocialActivity johnActivity = new ExoSocialActivityImpl();
      johnActivity.setTitle("John activity" + i);
      activityStorage.saveActivity(johnIdentity, johnActivity);

      tearDownActivityList.add(johnActivity);

      ExoSocialActivity maryActivity = new ExoSocialActivityImpl();
      maryActivity.setTitle("Mary activity" + i);
      activityStorage.saveActivity(maryIdentity, maryActivity);

      tearDownActivityList.add(maryActivity);

      ExoSocialActivity demoActivity = new ExoSocialActivityImpl();
      demoActivity.setTitle("Demo activity" + i);
      activityStorage.saveActivity(demoIdentity, demoActivity);

      tearDownActivityList.add(demoActivity);

      //John comments demo's activities
      ExoSocialActivity johnComment = new ExoSocialActivityImpl();
      johnComment.setTitle("John's comment " + i);
      johnComment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(demoActivity, johnComment);
    }

    assertEquals("activityStorage.getNumberOfUserActivities(rootIdentity) must return " + totalNumber, totalNumber,
            activityStorage.getNumberOfUserActivities(rootIdentity));
    assertEquals("activityStorage.getNumberOfUserActivities(johnIdentity) must return " + totalNumber, totalNumber,
            activityStorage.getNumberOfUserActivities(johnIdentity));
    assertEquals("activityStorage.getNumberOfUserActivities(maryIdentity) must return " + totalNumber, totalNumber,
        activityStorage.getNumberOfUserActivities(maryIdentity));
    assertEquals("activityStorage.getNumberOfUserActivities(demoIdentity) must return " + totalNumber, totalNumber,
        activityStorage.getNumberOfUserActivities(demoIdentity));

  }

  /**
   * Tests {@link ActivityStorage#getNumberOfNewerOnUserActivities(Identity, ExoSocialActivity)}.
   */
  @MaxQueryNumber(200)
  public void testGetNumberOfNewerOnUserActivities() {
    checkCleanData();
    createActivities(2, demoIdentity);
    ExoSocialActivity firstActivity = activityStorage.getUserActivities(demoIdentity, 0, 10).get(0);
    assertEquals(0, activityStorage.getNumberOfNewerOnUserActivities(demoIdentity, firstActivity));

    createActivities(1, johnIdentity);

    createActivities(1, demoIdentity);

    assertEquals(1, activityStorage.getNumberOfNewerOnUserActivities(demoIdentity, firstActivity));

  }

  /**
   * Tests {@link ActivityStorage#getNewerOnUserActivities(Identity, ExoSocialActivity, int)}.
   */
  @MaxQueryNumber(300)
  public void testGetNewerOnUserActivities() {
    checkCleanData();
    createActivities(2, demoIdentity);
    ExoSocialActivity firstActivity = activityStorage.getUserActivities(demoIdentity, 0, 10).get(0);
    assertEquals(0, activityStorage.getNewerOnUserActivities(demoIdentity, firstActivity, 10).size());
    createActivities(2, maryIdentity);
    assertEquals(0, activityStorage.getNewerOnUserActivities(demoIdentity, firstActivity, 10).size());
    createActivities(2, demoIdentity);
    assertEquals(2, activityStorage.getNewerOnUserActivities(demoIdentity, firstActivity, 10).size());
  }

  /**
   * Tests {@link ActivityStorage#getNumberOfOlderOnUserActivities(Identity, ExoSocialActivity)}.
   */
  @MaxQueryNumber(300)
  public void testGetNumberOfOlderOnUserActivities() {
    checkCleanData();
    createActivities(3, demoIdentity);
    List<ExoSocialActivity> userActivities = activityStorage.getUserActivities(demoIdentity, 0, 10);
    ExoSocialActivity secondActivity = userActivities.get(1);
    assertEquals(1, activityStorage.getNumberOfOlderOnUserActivities(demoIdentity, secondActivity));
    createActivities(2, demoIdentity);
    assertEquals(1, activityStorage.getNumberOfOlderOnUserActivities(demoIdentity, secondActivity));
    ExoSocialActivity newFirstActivity = activityStorage.getUserActivities(demoIdentity, 0, 10).get(0);
    assertEquals(4, activityStorage.getNumberOfOlderOnUserActivities(demoIdentity, newFirstActivity));
  }

  /**
   * Tests {@link ActivityStorage#getOlderOnUserActivities(Identity, ExoSocialActivity, int)}.
   */
  @MaxQueryNumber(300)
  public void testGetOlderOnUserActivities() {
    checkCleanData();
    createActivities(2, demoIdentity);
    ExoSocialActivity firstActivity = activityStorage.getUserActivities(demoIdentity, 0, 10).get(0);
    assertEquals(1, activityStorage.getOlderOnUserActivities(demoIdentity, firstActivity, 10).size());
    createActivities(2, maryIdentity);
    assertEquals(1, activityStorage.getOlderOnUserActivities(demoIdentity, firstActivity, 10).size());
    createActivities(2, demoIdentity);
    assertEquals(1, activityStorage.getOlderOnUserActivities(demoIdentity, firstActivity, 10).size());
    firstActivity = activityStorage.getUserActivities(demoIdentity, 0, 10).get(0);
    assertEquals(3, activityStorage.getOlderOnUserActivities(demoIdentity, firstActivity, 10).size());
  }

  /**
   * Tests {@link ActivityStorage#getActivityFeed(Identity, int, int)}.
   */
  @MaxQueryNumber(450)
  public void testGetActivityFeed() {
    createActivities(3, demoIdentity);
    createActivities(3, maryIdentity);
    createActivities(2, johnIdentity);

    List<ExoSocialActivity> demoActivityFeed = activityStorage.getActivityFeed(demoIdentity, 0, 10);
    assertEquals("demoActivityFeed.size() must be 3", 3, demoActivityFeed.size());

    Relationship demoMaryConnection = relationshipManager.inviteToConnect(demoIdentity, maryIdentity);
    assertEquals(3, activityStorage.getActivityFeed(demoIdentity, 0, 10).size());

    relationshipManager.confirm(demoIdentity, maryIdentity);
    List<ExoSocialActivity> demoActivityFeed2 = activityStorage.getActivityFeed(demoIdentity, 0, 10);
    assertEquals("demoActivityFeed2.size() must return 6", 6, demoActivityFeed2.size());
    List<ExoSocialActivity> maryActivityFeed = activityStorage.getActivityFeed(maryIdentity, 0, 10);
    assertEquals("maryActivityFeed.size() must return 6", 6, maryActivityFeed.size());
  }

  /**
   * Tests {@link ActivityStorage#getNumberOfActivitesOnActivityFeed(Identity)}.
   */
  @MaxQueryNumber(350)
  public void testGetNumberOfActivitesOnActivityFeed() {
    createActivities(3, demoIdentity);
    createActivities(2, maryIdentity);
    createActivities(1, johnIdentity);
    int demoActivityCount = activityStorage.getNumberOfActivitesOnActivityFeed(demoIdentity);
    assertEquals("demoActivityCount must be 3", 3, demoActivityCount);
    int maryActivityCount = activityStorage.getNumberOfActivitesOnActivityFeed(maryIdentity);
    assertEquals("maryActivityCount must be 2", 2, maryActivityCount);
    Relationship demoMaryConnection = relationshipManager.inviteToConnect(demoIdentity, maryIdentity);
    int demoActivityCount2 = activityStorage.getNumberOfActivitesOnActivityFeed(demoIdentity);
    assertEquals("demoActivityCount2 must be 3", 3, demoActivityCount2);
    relationshipManager.confirm(demoIdentity, maryIdentity);
    int demoActivityCount3 = activityStorage.getNumberOfActivitesOnActivityFeed(demoIdentity);
    assertEquals("demoActivityCount3 must be 5", 5, demoActivityCount3);
    int maryActivityCount2 = activityStorage.getNumberOfActivitesOnActivityFeed(maryIdentity);
    assertEquals("maryActivityCount2 must be 5", 5, maryActivityCount2);
  }

  /**
   * Tests {@link ActivityStorage#getNumberOfActivitesOnActivityFeed(Identity, ExoSocialActivity)}.
   */
  @MaxQueryNumber(500)
  public void testGetNumberOfNewerOnActivityFeed() {
    createActivities(3, demoIdentity);
    createActivities(2, maryIdentity);
    Relationship maryDemoConnection = relationshipManager.inviteToConnect(maryIdentity, demoIdentity);
    relationshipManager.confirm(maryIdentity, demoIdentity);
    List<ExoSocialActivity> demoActivityFeed = activityStorage.getActivityFeed(demoIdentity, 0, 10);
    ExoSocialActivity firstActivity = demoActivityFeed.get(0);
    int newDemoActivityFeed = activityStorage.getNumberOfNewerOnActivityFeed(demoIdentity, firstActivity);
    assertEquals("newDemoActivityFeed must be 0", 0, newDemoActivityFeed);
    createActivities(1, johnIdentity);
    int newDemoActivityFeed2 = activityStorage.getNumberOfNewerOnActivityFeed(demoIdentity, firstActivity);
    assertEquals("newDemoActivityFeed2 must be 0", 0, newDemoActivityFeed2);
    createActivities(1, demoIdentity);
    int newDemoActivityFeed3 = activityStorage.getNumberOfNewerOnActivityFeed(demoIdentity, firstActivity);
    assertEquals("newDemoActivityFeed3 must be 1", 1, newDemoActivityFeed3);
    createActivities(2, maryIdentity);
    int newDemoActivityFeed4 = activityStorage.getNumberOfNewerOnActivityFeed(demoIdentity, firstActivity);
    assertEquals("newDemoActivityFeed must be 3", 3, newDemoActivityFeed4);
  }

  /**
   * Tests {@link ActivityStorage#getNewerOnActivityFeed(Identity, ExoSocialActivity, int)}.
   */
  @MaxQueryNumber(450)
  public void testGetNewerOnActivityFeed() {
    createActivities(3, demoIdentity);
    ExoSocialActivity demoBaseActivity = activityStorage.getActivityFeed(demoIdentity, 0, 10).get(0);
    assertEquals(0, activityStorage.getNewerOnActivityFeed(demoIdentity, demoBaseActivity, 10).size());
    createActivities(1, demoIdentity);
    assertEquals(1, activityStorage.getNewerOnActivityFeed(demoIdentity, demoBaseActivity, 10).size());
    createActivities(2, maryIdentity);
    Relationship demoMaryConnection = relationshipManager.inviteToConnect(demoIdentity, maryIdentity);
    assertEquals(1, activityStorage.getNewerOnActivityFeed(demoIdentity, demoBaseActivity, 10).size());
    relationshipManager.confirm(demoIdentity, maryIdentity);
    createActivities(2, maryIdentity);
    assertEquals(5, activityStorage.getNewerOnActivityFeed(demoIdentity, demoBaseActivity, 10).size());
  }

  /**
   * Tests {@link ActivityStorage#getNumberOfOlderOnActivityFeed(Identity, ExoSocialActivity)}.
   */
  @MaxQueryNumber(350)
  public void testGetNumberOfOlderOnActivityFeed() {
    createActivities(3, demoIdentity);
    createActivities(2, maryIdentity);
    Relationship maryDemoConnection = relationshipManager.inviteToConnect(maryIdentity, demoIdentity);
    relationshipManager.confirm(maryIdentity, demoIdentity);
    List<ExoSocialActivity> demoActivityFeed = activityStorage.getActivityFeed(demoIdentity, 0, 10);
    ExoSocialActivity lastDemoActivity = demoActivityFeed.get(4);
    int oldDemoActivityFeed = activityStorage.getNumberOfOlderOnActivityFeed(demoIdentity, lastDemoActivity);
    assertEquals("oldDemoActivityFeed must be 0", 0, oldDemoActivityFeed);
    createActivities(1, johnIdentity);
    int oldDemoActivityFeed2 = activityStorage.getNumberOfOlderOnActivityFeed(demoIdentity, lastDemoActivity);
    assertEquals("oldDemoActivityFeed2 must be 0", 0, oldDemoActivityFeed2);
    ExoSocialActivity nextLastDemoActivity = demoActivityFeed.get(3);
    int oldDemoActivityFeed3 = activityStorage.getNumberOfOlderOnActivityFeed(demoIdentity, nextLastDemoActivity);
    assertEquals("oldDemoActivityFeed3 must be 1", 1, oldDemoActivityFeed3);
  }

  /**
   * Tests {@link ActivityStorage#getOlderOnActivityFeed(Identity, ExoSocialActivity, int)}.
   */
  @MaxQueryNumber(150)
  public void testGetOlderOnActivityFeed() {
    createActivities(3, demoIdentity);
    ExoSocialActivity demoBaseActivity = activityStorage.getActivityFeed(demoIdentity, 0, 10).get(2);
    assertEquals(0, activityStorage.getOlderOnActivityFeed(demoIdentity, demoBaseActivity, 10).size());
  }

  /**
   * Test {@link ActivityStorage#getActivitiesOfConnections(Identity, int, int)}
   */
  @MaxQueryNumber(750)
  public void testGetActivitiesOfConnections() {
    List<Relationship> relationships = new ArrayList<Relationship> ();
    
    this.createActivities(2, rootIdentity);
    this.createActivities(1, demoIdentity);
    this.createActivities(2, johnIdentity);
    this.createActivities(3, maryIdentity);
    
    List<ExoSocialActivity> activities = activityStorage.getActivitiesOfConnections(demoIdentity, 0, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 0", 0, activities.size());
    
    RelationshipManager relationshipManager = this.getRelationshipManager();
    
    Relationship rootDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    relationshipManager.confirm(rootIdentity, demoIdentity);
    relationships.add(rootDemoRelationship);
    
    activities = activityStorage.getActivitiesOfConnections(rootIdentity, 0, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 1", 1, activities.size());
    
    Relationship rootMaryRelationship = relationshipManager.inviteToConnect(rootIdentity, maryIdentity);
    relationshipManager.confirm(rootIdentity, maryIdentity);
    relationships.add(rootMaryRelationship);
    
    activities = activityStorage.getActivitiesOfConnections(rootIdentity, 0, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 4", 4, activities.size());
    
    Relationship rootJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    relationshipManager.confirm(rootIdentity, johnIdentity);
    relationships.add(rootJohnRelationship);
    
    activities = activityStorage.getActivitiesOfConnections(rootIdentity, 0, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 6", 6, activities.size());
    
    for (Relationship rel : relationships) {
      relationshipManager.delete(rel);
    }
  }

  /**
   * Test {@link ActivityStorage#getActivitiesOfConnections(Identity, int, int)} for issue SOC-1995
   * 
   * @throws Exception
   * @since 1.2.2
   */
  @MaxQueryNumber(350)
  public void testGetActivitiesOfConnectionsWithPosterIdentity() throws Exception {
    RelationshipManager relationshipManager = this.getRelationshipManager();
    List<Relationship> relationships = new ArrayList<Relationship>();
    
    Relationship johnDemoIdentity = relationshipManager.inviteToConnect(johnIdentity, demoIdentity);
    relationshipManager.confirm(demoIdentity, johnIdentity);
    johnDemoIdentity = relationshipManager.get(johnDemoIdentity.getId());
    relationships.add(johnDemoIdentity);
    
    Relationship demoMaryIdentity = relationshipManager.inviteToConnect(demoIdentity, maryIdentity);
    relationshipManager.confirm(maryIdentity, demoIdentity);
    johnDemoIdentity = relationshipManager.get(demoMaryIdentity.getId());
    relationships.add(demoMaryIdentity);
   
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle("Hello Demo from Mary");
    activity.setUserId(maryIdentity.getId());
    activityStorage.saveActivity(demoIdentity, activity);
    tearDownActivityList.add(activity);
    
    activity = activityStorage.getActivity(activity.getId());
    assertNotNull("activity must not be null", activity);
    assertEquals("activity.getStreamOwner() must return: demo", "demo", activity.getStreamOwner());
    assertEquals("activity.getUserId() must return: " + maryIdentity.getId(), maryIdentity.getId(), activity.getUserId());
    
    List<ExoSocialActivity> johnConnectionActivities = activityStorage.getActivitiesOfConnections(johnIdentity, 0, 10);
    assertNotNull("johnConnectionActivities must not be null", johnConnectionActivities);
    assertEquals("johnConnectionActivities.size() must return: 1", 1, johnConnectionActivities.size());
    
    List<ExoSocialActivity> demoConnectionActivities = activityStorage.getActivitiesOfConnections(demoIdentity, 0, 10);
    assertNotNull("demoConnectionActivities must not be null", demoConnectionActivities);
    assertEquals("demoConnectionActivities.size() must return: 0", 0, demoConnectionActivities.size());
    
    List<ExoSocialActivity> maryConnectionActivities = activityStorage.getActivitiesOfConnections(maryIdentity, 0, 10);
    assertNotNull("maryConnectionActivities must not be null", maryConnectionActivities);
    assertEquals("maryConnectionActivities.size() must return: 1", 1, maryConnectionActivities.size());
    
    for (Relationship rel : relationships) {
      relationshipManager.delete(rel);
    }
  }
  
  /**
   * Test {@link ActivityStorage#getNumberOfActivitiesOfConnections(Identity)}
   * 
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(750)
  public void testGetNumberOfActivitiesOfConnections() {
    List<Relationship> relationships = new ArrayList<Relationship> ();
    
    this.createActivities(2, rootIdentity);
    this.createActivities(1, demoIdentity);
    this.createActivities(2, johnIdentity);
    this.createActivities(3, maryIdentity);
    
    int count = activityStorage.getNumberOfActivitiesOfConnections(demoIdentity);
    assertEquals("count must be: 0", 0, count);
    
    RelationshipManager relationshipManager = this.getRelationshipManager();
    
    Relationship rootDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    relationshipManager.confirm(rootIdentity, demoIdentity);
    relationships.add(rootDemoRelationship);
    
    count = activityStorage.getNumberOfActivitiesOfConnections(rootIdentity);
    assertEquals("count must be: 1", 1, count);
    
    Relationship rootMaryRelationship = relationshipManager.inviteToConnect(rootIdentity, maryIdentity);
    relationshipManager.confirm(rootIdentity, maryIdentity);
    relationships.add(rootMaryRelationship);
    
    count = activityStorage.getNumberOfActivitiesOfConnections(rootIdentity);
    assertEquals("count must be: 4", 4, count);
    
    Relationship rootJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    relationshipManager.confirm(rootIdentity, johnIdentity);
    relationships.add(rootJohnRelationship);
    
    count = activityStorage.getNumberOfActivitiesOfConnections(rootIdentity);
    assertEquals("count must be: 6", 6, count);
    
    for (Relationship rel : relationships) {
      relationshipManager.delete(rel);
    }
  }

  /**
   * Test {@link ActivityStorage#getNumberOfNewerOnActivitiesOfConnections(Identity, ExoSocialActivity)}
   * 
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(750)
  public void testGetNumberOfNewerOnActivitiesOfConnections() {
    List<Relationship> relationships = new ArrayList<Relationship> ();
    
    this.createActivities(3, maryIdentity);
    this.createActivities(1, demoIdentity);
    this.createActivities(2, johnIdentity);
    this.createActivities(2, rootIdentity);
    
    List<ExoSocialActivity> demoActivities = activityStorage.getActivitiesOfIdentity(demoIdentity, 0, 10);
    assertNotNull("demoActivities must not be null", demoActivities);
    assertEquals("demoActivities.size() must return: 1", 1, demoActivities.size());
    
    ExoSocialActivity baseActivity = demoActivities.get(0);
    
    int count = activityStorage.getNumberOfNewerOnActivitiesOfConnections(johnIdentity, baseActivity);
    assertEquals("count must be: 0", 0, count);
    
    count = activityStorage.getNumberOfNewerOnActivitiesOfConnections(demoIdentity, baseActivity);
    assertEquals("count must be: 0", 0, count);
    
    RelationshipManager relationshipManager = this.getRelationshipManager();
    
    Relationship demoJohnRelationship = relationshipManager.inviteToConnect(demoIdentity, johnIdentity);
    relationshipManager.confirm(demoIdentity, johnIdentity);
    relationships.add(demoJohnRelationship);
    
    count = activityStorage.getNumberOfNewerOnActivitiesOfConnections(demoIdentity, baseActivity);
    assertEquals("count must be: 2", 2, count);
    
    Relationship demoMaryRelationship = relationshipManager.inviteToConnect(demoIdentity, maryIdentity);
    relationshipManager.confirm(demoIdentity, maryIdentity);
    relationships.add(demoMaryRelationship);
    
    count = activityStorage.getNumberOfNewerOnActivitiesOfConnections(demoIdentity, baseActivity);
    assertEquals("count must be: 2", 2, count);
    
    Relationship demoRootRelationship = relationshipManager.inviteToConnect(demoIdentity, rootIdentity);
    relationshipManager.confirm(demoIdentity, rootIdentity);
    relationships.add(demoRootRelationship);
    
    count = activityStorage.getNumberOfNewerOnActivitiesOfConnections(demoIdentity, baseActivity);
    assertEquals("count must be: 4", 4, count);
    
    for (Relationship rel : relationships) {
      relationshipManager.delete(rel);
    }
  }

  /**
   * Test {@link ActivityStorage#getNewerOnActivitiesOfConnections(Identity, ExoSocialActivity, int)}
   * 
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(800)
  public void testGetNewerOnActivitiesOfConnections() {
    List<Relationship> relationships = new ArrayList<Relationship> ();
    this.createActivities(3, maryIdentity);
    this.createActivities(1, demoIdentity);
    this.createActivities(2, johnIdentity);
    this.createActivities(2, rootIdentity);
    

    List<ExoSocialActivity> maryActivities = activityStorage.getActivitiesOfIdentity(maryIdentity, 0, 10);
    assertNotNull("maryActivities must not be null", maryActivities);
    assertEquals("maryActivities.size() must return: 3", 3, maryActivities.size());
    
    ExoSocialActivity baseActivity = maryActivities.get(2);
    
    List<ExoSocialActivity> activities = activityStorage.getNewerOnActivitiesOfConnections(johnIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 0", 0, activities.size());
    
    activities = activityStorage.getNewerOnActivitiesOfConnections(demoIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 0", 0, activities.size());
    
    activities = activityStorage.getNewerOnActivitiesOfConnections(maryIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 0", 0, activities.size());
    
    RelationshipManager relationshipManager = this.getRelationshipManager();
    Relationship maryDemoRelationship = relationshipManager.inviteToConnect(maryIdentity, demoIdentity);
    relationshipManager.confirm(maryIdentity, demoIdentity);
    relationships.add(maryDemoRelationship);
    
    activities = activityStorage.getNewerOnActivitiesOfConnections(maryIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 1", 1, activities.size());
    
    activities = activityStorage.getNewerOnActivitiesOfConnections(demoIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 2", 2, activities.size());
    
    Relationship maryJohnRelationship = relationshipManager.inviteToConnect(maryIdentity, johnIdentity);
    relationshipManager.confirm(maryIdentity, johnIdentity);
    relationships.add(maryJohnRelationship);
    
    activities = activityStorage.getNewerOnActivitiesOfConnections(maryIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 3", 3, activities.size());
    
    Relationship maryRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    relationshipManager.confirm(maryIdentity, rootIdentity);
    relationships.add(maryRootRelationship);
    
    activities = activityStorage.getNewerOnActivitiesOfConnections(maryIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 5", 5, activities.size());
    
    for (Relationship rel : relationships) {
      relationshipManager.delete(rel);
    }
  }

  /**
   * Test {@link ActivityStorage#getNumberOfOlderOnActivitiesOfConnections(Identity, ExoSocialActivity)}
   * 
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(750)
  public void testGetNumberOfOlderOnActivitiesOfConnections() {
    List<Relationship> relationships = new ArrayList<Relationship> ();
    
    this.createActivities(3, maryIdentity);
    this.createActivities(1, demoIdentity);
    this.createActivities(2, johnIdentity);
    this.createActivities(2, rootIdentity);
    
    List<ExoSocialActivity> rootActivities = activityStorage.getActivitiesOfIdentity(rootIdentity, 0, 10);
    assertNotNull("rootActivities must not be null", rootActivities);
    assertEquals("rootActivities.size() must return: 2", 2, rootActivities.size());
    
    ExoSocialActivity baseActivity = rootActivities.get(1);
    
    int count = activityStorage.getNumberOfOlderOnActivitiesOfConnections(rootIdentity, baseActivity);
    assertEquals("count must be: 0", 0, count);
    
    count = activityStorage.getNumberOfOlderOnActivitiesOfConnections(johnIdentity, baseActivity);
    assertEquals("count must be: 0", 0, count);
    
    RelationshipManager relationshipManager = this.getRelationshipManager();
    
    Relationship rootJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    relationshipManager.confirm(rootIdentity, johnIdentity);
    relationships.add(rootJohnRelationship);
    
    count = activityStorage.getNumberOfOlderOnActivitiesOfConnections(rootIdentity, baseActivity);
    assertEquals("count must be: 2", 2, count);
    
    Relationship rootDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    relationshipManager.confirm(rootIdentity, demoIdentity);
    relationships.add(rootDemoRelationship);
    
    count = activityStorage.getNumberOfOlderOnActivitiesOfConnections(rootIdentity, baseActivity);
    assertEquals("count must be: 3", 3, count);
    
    Relationship rootMaryRelationship = relationshipManager.inviteToConnect(rootIdentity, maryIdentity);
    relationshipManager.confirm(rootIdentity, maryIdentity);
    relationships.add(rootMaryRelationship);
    
    count = activityStorage.getNumberOfOlderOnActivitiesOfConnections(rootIdentity, baseActivity);
    assertEquals("count must be: 6", 6, count);
    
    for (Relationship rel : relationships) {
      relationshipManager.delete(rel);
    }
  }

  /**
   * Test {@link ActivityStorage#getOlderOnActivitiesOfConnections(Identity, ExoSocialActivity, int)}
   * 
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(750)
  public void testGetOlderOnActivitiesOfConnections() {
    List<Relationship> relationships = new ArrayList<Relationship> ();
    
    this.createActivities(3, maryIdentity);
    this.createActivities(1, demoIdentity);
    this.createActivities(2, johnIdentity);
    this.createActivities(2, rootIdentity);
    
    List<ExoSocialActivity> rootActivities = activityStorage.getActivitiesOfIdentity(rootIdentity, 0, 10);
    assertNotNull("rootActivities must not be null", rootActivities);
    assertEquals("rootActivities.size() must return: 2", 2, rootActivities.size());
    
    ExoSocialActivity baseActivity = rootActivities.get(1);
    
    List<ExoSocialActivity> activities;
    
    activities = activityStorage.getOlderOnActivitiesOfConnections(rootIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 0", 0, activities.size());
    
    activities = activityStorage.getOlderOnActivitiesOfConnections(johnIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 0", 0, activities.size());
    
    RelationshipManager relationshipManager = this.getRelationshipManager();
    
    Relationship rootJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    relationshipManager.confirm(rootIdentity, johnIdentity);
    relationships.add(rootJohnRelationship);
    
    activities = activityStorage.getOlderOnActivitiesOfConnections(rootIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 2", 2, activities.size());
    
    Relationship rootDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    relationshipManager.confirm(rootIdentity, demoIdentity);
    relationships.add(rootDemoRelationship);
    
    activities = activityStorage.getOlderOnActivitiesOfConnections(rootIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 3", 3, activities.size());
    
    Relationship rootMaryRelationship = relationshipManager.inviteToConnect(rootIdentity, maryIdentity);
    relationshipManager.confirm(rootIdentity, maryIdentity);
    relationships.add(rootMaryRelationship);
    
    activities = activityStorage.getOlderOnActivitiesOfConnections(rootIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 6", 6, activities.size());
    
    for (Relationship rel : relationships) {
      relationshipManager.delete(rel);
    }
  }

  /**
   * Test {@link ActivityStorage#getUserSpacesActivities(Identity, int, int)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(3064)
  public void testGetUserSpacesActivities() throws Exception {
    SpaceService spaceService = this.getSpaceService();
    Space space = this.getSpaceInstance(spaceService, 0);
    Identity spaceIdentity = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName(), false);

    int totalNumber = 10;
    
    //demo posts activities to space
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity, activity);
      tearDownActivityList.add(activity);
    }
    
    space = spaceService.getSpaceByDisplayName(space.getDisplayName());
    assertNotNull("space must not be null", space);
    assertEquals("space.getDisplayName() must return: my space 0", "my space 0", space.getDisplayName());
    assertEquals("space.getDescription() must return: add new space 0", "add new space 0", space.getDescription());
    
    List<ExoSocialActivity> demoActivities = activityStorage.getUserSpacesActivities(demoIdentity, 0, 10);
    assertNotNull("demoActivities must not be null", demoActivities);
    assertEquals("demoActivities.size() must return: 10", 10, demoActivities.size());
    
    Space space2 = this.getSpaceInstance(spaceService, 1);
    Identity spaceIdentity2 = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space2.getPrettyName(), false);
    
    //demo posts activities to space2
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity2, activity);
      tearDownActivityList.add(activity);
    }
    
    space2 = spaceService.getSpaceByDisplayName(space2.getDisplayName());
    assertNotNull("space2 must not be null", space2);
    assertEquals("space2.getDisplayName() must return: my space 1", "my space 1", space2.getDisplayName());
    assertEquals("space2.getDescription() must return: add new space 1", "add new space 1", space2.getDescription());
    
    demoActivities = activityStorage.getUserSpacesActivities(demoIdentity, 0, 20);
    assertNotNull("demoActivities must not be null", demoActivities);
    assertEquals("demoActivities.size() must return: 20", 20, demoActivities.size());
    
    demoActivities = activityStorage.getUserSpacesActivities(demoIdentity, 0, 10);
    assertNotNull("demoActivities must not be null", demoActivities);
    assertEquals("demoActivities.size() must return: 10", 10, demoActivities.size());
    
    demoActivities = activityStorage.getUserSpacesActivities(johnIdentity, 0, 10);
    assertNotNull("demoActivities must not be null", demoActivities);
    assertEquals("demoActivities.size() must return: 0", 0, demoActivities.size());
    
    spaceService.deleteSpace(space);
    spaceService.deleteSpace(space2);
  }

  /**
   * Test {@link ActivityStorage#getNumberOfUserSpacesActivities(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(3064)
  public void testGetNumberOfUserSpacesActivities() throws Exception {
    SpaceService spaceService = this.getSpaceService();
    Space space = this.getSpaceInstance(spaceService, 0);
    Identity spaceIdentity = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName(), false);
    
    int totalNumber = 10;
    
    //demo posts activities to space
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity, activity);
      tearDownActivityList.add(activity);
    }
    
    space = spaceService.getSpaceByDisplayName(space.getDisplayName());
    assertNotNull("space must not be null", space);
    assertEquals("space.getDisplayName() must return: my space 0", "my space 0", space.getDisplayName());
    assertEquals("space.getDescription() must return: add new space 0", "add new space 0", space.getDescription());
    
    int number = activityStorage.getNumberOfUserSpacesActivities(demoIdentity);
    assertEquals("number must be: 10", 10, number);
    
    Space space2 = this.getSpaceInstance(spaceService, 1);
    Identity spaceIdentity2 = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space2.getPrettyName(), false);

    //demo posts activities to space2
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity2, activity);
      tearDownActivityList.add(activity);
    }
    
    space2 = spaceService.getSpaceByDisplayName(space2.getDisplayName());
    assertNotNull("space2 must not be null", space2);
    assertEquals("space2.getDisplayName() must return: my space 1", "my space 1", space2.getDisplayName());
    assertEquals("space2.getDescription() must return: add new space 1", "add new space 1", space2.getDescription());
    
    number = activityStorage.getNumberOfUserSpacesActivities(demoIdentity);
    assertEquals("number must be: 20", 20, number);
    
    spaceService.deleteSpace(space);
    spaceService.deleteSpace(space2);
  }

  /**
   * Test {@link ActivityStorage#getNumberOfNewerOnUserSpacesActivities(Identity, ExoSocialActivity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(2902)
  public void testGetNumberOfNewerOnUserSpacesActivities() throws Exception {
    SpaceService spaceService = this.getSpaceService();
    Space space = this.getSpaceInstance(spaceService, 0);
    Identity spaceIdentity = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName(), false);
    
    int totalNumber = 10;
    
    ExoSocialActivity baseActivity = null;
    
    //demo posts activities to space
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity, activity);
      tearDownActivityList.add(activity);
      if (i == 0) {
        baseActivity = activity;
      }
    }
    
    space = spaceService.getSpaceByDisplayName(space.getDisplayName());
    assertNotNull("space must not be null", space);
    assertEquals("space.getDisplayName() must return: my space 0", "my space 0", space.getDisplayName());
    assertEquals("space.getDescription() must return: add new space 0", "add new space 0", space.getDescription());
    
    int number = activityStorage.getNumberOfNewerOnUserSpacesActivities(demoIdentity, baseActivity);
    assertEquals("number must be: 9", 9, number);
    
    Space space2 = this.getSpaceInstance(spaceService, 1);
    Identity spaceIdentity2 = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space2.getPrettyName(), false);
    
    //demo posts activities to space2
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity2, activity);
      tearDownActivityList.add(activity);
    }
    
    space2 = spaceService.getSpaceByDisplayName(space2.getDisplayName());
    assertNotNull("space2 must not be null", space2);
    assertEquals("space2.getDisplayName() must return: my space 1", "my space 1", space2.getDisplayName());
    assertEquals("space2.getDescription() must return: add new space 1", "add new space 1", space2.getDescription());
    
    number = activityStorage.getNumberOfNewerOnUserSpacesActivities(demoIdentity, baseActivity);
    assertEquals("number must be: 19", 19, number);
    
    spaceService.deleteSpace(space);
    spaceService.deleteSpace(space2);
  }

  /**
   * Test {@link ActivityStorage#getNewerOnUserSpacesActivities(Identity, ExoSocialActivity, int)} 
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(2902)
  public void testGetNewerOnUserSpacesActivities() throws Exception {
    SpaceService spaceService = this.getSpaceService();
    Space space = this.getSpaceInstance(spaceService, 0);
    Identity spaceIdentity = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName(), false);
    
    int totalNumber = 10;
    
    ExoSocialActivity baseActivity = null;
    
    //demo posts activities to space
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity, activity);
      tearDownActivityList.add(activity);
      if (i == 0) {
        baseActivity = activity;
      }
    }
    
    space = spaceService.getSpaceByDisplayName(space.getDisplayName());
    assertNotNull("space must not be null", space);
    assertEquals("space.getDisplayName() must return: my space 0", "my space 0", space.getDisplayName());
    assertEquals("space.getDescription() must return: add new space 0", "add new space 0", space.getDescription());

    List<ExoSocialActivity> activities = activityStorage.getNewerOnUserSpacesActivities(demoIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 9", 9, activities.size());
    
    Space space2 = this.getSpaceInstance(spaceService, 1);
    Identity spaceIdentity2 = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space2.getPrettyName(), false);
    
    //demo posts activities to space2
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity2, activity);
      tearDownActivityList.add(activity);
    }
    
    space2 = spaceService.getSpaceByDisplayName(space2.getDisplayName());
    assertNotNull("space2 must not be null", space2);
    assertEquals("space2.getDisplayName() must return: my space 1", "my space 1", space2.getDisplayName());
    assertEquals("space2.getDescription() must return: add new space 1", "add new space 1", space2.getDescription());
    
    activities = activityStorage.getNewerOnUserSpacesActivities(demoIdentity, baseActivity, 20);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 19", 19, activities.size());
    
    spaceService.deleteSpace(space);
    spaceService.deleteSpace(space2);
  }

  /**
   * Test {@link ActivityStorage#getNumberOfOlderOnUserSpacesActivities(Identity, ExoSocialActivity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(2902)
  public void testGetNumberOfOlderOnUserSpacesActivities() throws Exception {
    SpaceService spaceService = this.getSpaceService();
    Space space = this.getSpaceInstance(spaceService, 0);
    Identity spaceIdentity = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName(), false);
    
    int totalNumber = 10;
    
    ExoSocialActivity baseActivity = null;
    
    //demo posts activities to space
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity, activity);
      tearDownActivityList.add(activity);
      if (i == totalNumber - 1) {
        baseActivity = activity;
      }
    }
    
    space = spaceService.getSpaceByDisplayName(space.getDisplayName());
    assertNotNull("space must not be null", space);
    assertEquals("space.getDisplayName() must return: my space 0", "my space 0", space.getDisplayName());
    assertEquals("space.getDescription() must return: add new space 0", "add new space 0", space.getDescription());
    
    int number = activityStorage.getNumberOfOlderOnUserSpacesActivities(demoIdentity, baseActivity);
    assertEquals("number must be: 9", 9, number);
    
    Space space2 = this.getSpaceInstance(spaceService, 1);
    Identity spaceIdentity2 = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space2.getPrettyName(), false);
    
    //demo posts activities to space2
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity2, activity);
      tearDownActivityList.add(activity);
      if (i == totalNumber - 1) {
        baseActivity = activity;
      }
    }
    
    space2 = spaceService.getSpaceByDisplayName(space2.getDisplayName());
    assertNotNull("space2 must not be null", space2);
    assertEquals("space2.getDisplayName() must return: my space 1", "my space 1", space2.getDisplayName());
    assertEquals("space2.getDescription() must return: add new space 1", "add new space 1", space2.getDescription());
    
    number = activityStorage.getNumberOfOlderOnUserSpacesActivities(demoIdentity, baseActivity);
    assertEquals("number must be: 19", 19, number);
    
    spaceService.deleteSpace(space);
    spaceService.deleteSpace(space2);
  }

  /**
   * Test {@link ActivityStorage#getOlderOnUserSpacesActivities(Identity, ExoSocialActivity, int)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(2902)
  public void testGetOlderOnUserSpacesActivities() throws Exception {
    SpaceService spaceService = this.getSpaceService();
    Space space = this.getSpaceInstance(spaceService, 0);
    Identity spaceIdentity = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName(), false);
    
    int totalNumber = 10;
    
    ExoSocialActivity baseActivity = null;
    
    //demo posts activities to space
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity, activity);
      tearDownActivityList.add(activity);
      if (i == totalNumber - 1) {
        baseActivity = activity;
      }
    }
    
    space = spaceService.getSpaceByDisplayName(space.getDisplayName());
    assertNotNull("space must not be null", space);
    assertEquals("space.getDisplayName() must return: my space 0", "my space 0", space.getDisplayName());
    assertEquals("space.getDescription() must return: add new space 0", "add new space 0", space.getDescription());
    
    List<ExoSocialActivity> activities = activityStorage.getOlderOnUserSpacesActivities(demoIdentity, baseActivity, 10);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 9", 9, activities.size());
    
    Space space2 = this.getSpaceInstance(spaceService, 1);
    Identity spaceIdentity2 = this.identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space2.getPrettyName(), false);
    
    //demo posts activities to space2
    for (int i = 0; i < totalNumber; i ++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activity.setUserId(demoIdentity.getId());
      activityStorage.saveActivity(spaceIdentity2, activity);
      tearDownActivityList.add(activity);
      if (i == totalNumber - 1) {
        baseActivity = activity;
      }
    }
    
    space2 = spaceService.getSpaceByDisplayName(space2.getDisplayName());
    assertNotNull("space2 must not be null", space2);
    assertEquals("space2.getDisplayName() must return: my space 1", "my space 1", space2.getDisplayName());
    assertEquals("space2.getDescription() must return: add new space 1", "add new space 1", space2.getDescription());
    
    activities = activityStorage.getOlderOnUserSpacesActivities(demoIdentity, baseActivity, 20);
    assertNotNull("activities must not be null", activities);
    assertEquals("activities.size() must return: 19", 19, activities.size());
    
    spaceService.deleteSpace(space);
    spaceService.deleteSpace(space2);
  }

  /**
   * Test {@link ActivityStorage#getComments(ExoSocialActivity, int, int)}
   * 
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(250)
  public void testGetComments() {
    int totalNumber = 40;
    String activityTitle = "activity title";
    
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(activityTitle);
    activity.setUserId(rootIdentity.getId());
    activityStorage.saveActivity(rootIdentity, activity);
    tearDownActivityList.add(activity);
    
    for (int i = 0; i < totalNumber; i++) {
      //John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("Comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }
    
    List<ExoSocialActivity> comments = activityStorage.getComments(activity, 0, 40);
    assertNotNull("comments must not be null", comments);
    assertEquals("comments.size() must return: 40", 40, comments.size());
  }

  /**
   * Test {@link ActivityStorage#getNumberOfComments(ExoSocialActivity)}
   * 
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(250)
  public void testGetNumberOfComments() {
    int totalNumber = 40;
    String activityTitle = "activity title";
    
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(activityTitle);
    activity.setUserId(rootIdentity.getId());
    activityStorage.saveActivity(rootIdentity, activity);
    tearDownActivityList.add(activity);
    
    for (int i = 0; i < totalNumber; i++) {
      //John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("Comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }
    
    List<ExoSocialActivity> comments = activityStorage.getComments(activity, 0, 40);
    assertNotNull("comments must not be null", comments);
    assertEquals("comments.size() must return: 40", 40, comments.size());
    
    int number = activityStorage.getNumberOfComments(activity);
    assertEquals("number must be: 40", 40, number);
  }

  /**
   * Test {@link ActivityStorage#getNumberOfNewerComments(ExoSocialActivity, ExoSocialActivity)}
   * 
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(150)
  public void testGetNumberOfNewerComments() {
    int totalNumber = 10;
    String activityTitle = "activity title";
    
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(activityTitle);
    activity.setUserId(rootIdentity.getId());
    activityStorage.saveActivity(rootIdentity, activity);
    tearDownActivityList.add(activity);
    
    for (int i = 0; i < totalNumber; i ++) {
      //John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("john comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }
    
    for (int i = 0; i < totalNumber; i ++) {
      //John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("demo comment " + i);
      comment.setUserId(demoIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }
    
    List<ExoSocialActivity> comments = activityStorage.getComments(activity, 0, 10);
    assertNotNull("comments must not be null", comments);
    assertEquals("comments.size() must return: 10", 10, comments.size());
    
    ExoSocialActivity latestComment = comments.get(0);
    
    int number = activityStorage.getNumberOfNewerComments(activity, latestComment);
    assertEquals("number must be: 0", 0, number);
    
    ExoSocialActivity baseComment = activityStorage.getComments(activity, 0, 20).get(10);
    number = activityStorage.getNumberOfNewerComments(activity, baseComment);
    assertEquals("number must be: 10", 10, number);
    
    baseComment = activityStorage.getComments(activity, 0, 20).get(19);
    number = activityStorage.getNumberOfNewerComments(activity, baseComment);
    assertEquals("number must be: 19", 19, number);
  }

  /**
   * Test {@link ActivityStorage#getNewerComments(ExoSocialActivity, ExoSocialActivity, int)}
   * 
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(150)
  public void testGetNewerComments() {
    int totalNumber = 10;
    String activityTitle = "activity title";
    
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(activityTitle);
    activity.setUserId(rootIdentity.getId());
    activityStorage.saveActivity(rootIdentity, activity);
    tearDownActivityList.add(activity);
    
    for (int i = 0; i < totalNumber; i ++) {
      //John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("john comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }
    
    for (int i = 0; i < totalNumber; i ++) {
      //John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("demo comment " + i);
      comment.setUserId(demoIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }
    
    List<ExoSocialActivity> comments = activityStorage.getComments(activity, 0, 10);
    assertNotNull("comments must not be null", comments);
    assertEquals("comments.size() must return: 10", 10, comments.size());
    
    ExoSocialActivity latestComment = comments.get(0);
    
    List<ExoSocialActivity> newerComments = activityStorage.getNewerComments(activity, latestComment, 10);
    assertNotNull("newerComments must not be null", newerComments);
    assertEquals("newerComments.size() must return: 0", 0, newerComments.size());
    
    ExoSocialActivity baseComment = activityStorage.getComments(activity, 0, 20).get(10);
    newerComments = activityStorage.getNewerComments(activity, baseComment, 20);
    assertNotNull("newerComments must not be null", newerComments);
    assertEquals("newerComments.size() must return: 10", 10, newerComments.size());
    
    baseComment = activityStorage.getComments(activity, 0, 20).get(19);
    newerComments = activityStorage.getNewerComments(activity, baseComment, 20);
    assertNotNull("newerComments must not be null", newerComments);
    assertEquals("newerComments.size() must return: 19", 19, newerComments.size());
  }

  /**
   * Test {@link ActivityStorage#getNumberOfOlderComments(ExoSocialActivity, ExoSocialActivity)}
   * 
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(100)
  public void testGetNumberOfOlderComments() {
    int totalNumber = 10;
    String activityTitle = "activity title";
    
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(activityTitle);
    activity.setUserId(rootIdentity.getId());
    activityStorage.saveActivity(rootIdentity, activity);
    tearDownActivityList.add(activity);
    
    for (int i = 0; i < totalNumber; i ++) {
      //John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("john comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }
    
    List<ExoSocialActivity> comments = activityStorage.getComments(activity, 0, 10);
    assertNotNull("comments must not be null", comments);
    assertEquals("comments.size() must return: 10", 10, comments.size());
    
    ExoSocialActivity baseComment = comments.get(0);
    
    int number = activityStorage.getNumberOfOlderComments(activity, baseComment);
    assertEquals("number must be: 9", 9, number);
    
    baseComment = comments.get(9);
    
    number = activityStorage.getNumberOfOlderComments(activity, baseComment);
    assertEquals("number must be: 0", 0, number);
    
    baseComment = comments.get(5);
    
    number = activityStorage.getNumberOfOlderComments(activity, baseComment);
    assertEquals("number must be: 4", 4, number);
  }

  /**
   * Test {@link ActivityStorage#getOlderComments(ExoSocialActivity, ExoSocialActivity, int)}
   * 
   * @since 1.2.0-Beta3
   */
  @MaxQueryNumber(100)
  public void testGetOlderComments() {
    int totalNumber = 10;
    String activityTitle = "activity title";
    
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(activityTitle);
    activity.setUserId(rootIdentity.getId());
    activityStorage.saveActivity(rootIdentity, activity);
    tearDownActivityList.add(activity);
    
    for (int i = 0; i < totalNumber; i ++) {
      //John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("john comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }
    
    List<ExoSocialActivity> comments = activityStorage.getComments(activity, 0, 10);
    assertNotNull("comments must not be null", comments);
    assertEquals("comments.size() must return: 10", 10, comments.size());
    
    ExoSocialActivity baseComment = comments.get(0);
    
    List<ExoSocialActivity> olderComments = activityStorage.getOlderComments(activity, baseComment, 10);
    assertNotNull("olderComments must not be null", olderComments);
    assertEquals("olderComments.size() must return: 9", 9, olderComments.size());
    
    baseComment = comments.get(9);
    
    olderComments = activityStorage.getOlderComments(activity, baseComment, 10);
    assertNotNull("olderComments must not be null", olderComments);
    assertEquals("olderComments.size() must return: 0", 0, olderComments.size());
    
    baseComment = comments.get(5);
    
    olderComments = activityStorage.getOlderComments(activity, baseComment, 10);
    assertNotNull("olderComments must not be null", olderComments);
    assertEquals("olderComments.size() must return: 4", 4, olderComments.size());
  }

  /**
   *
   * 
   * @throws ActivityStorageException
   */
  @MaxQueryNumber(100)
  public void testGetStreamInfo() throws ActivityStorageException {
    checkCleanData();
    // root save on root's stream
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle("blabla");
    activity.setUpdated(new Date());
    activity.setUserId(demoIdentity.getId());
    activityStorage.saveActivity(demoIdentity, activity);

    String streamId = activity.getStreamId();
    assertNotNull("streamId must not be null", streamId);
    assertEquals(activity.getStreamOwner(), demoIdentity.getRemoteId());

    ActivityStream activityStream = activity.getActivityStream();

    assertEquals("activityStream.getId() must return: " + streamId, streamId, activityStream.getId());

    assertEquals("activityStream.getPrettyId() must return: " + demoIdentity.getRemoteId(), demoIdentity.getRemoteId(), activityStream.getPrettyId());

    assertNotNull(activityStream.getPermaLink());

    List<ExoSocialActivity> activities = activityStorage.getUserActivities(demoIdentity, 0, 100);
    assertEquals(1, activities.size());
    assertEquals(demoIdentity.getRemoteId(), activities.get(0).getStreamOwner());
    assertEquals(streamId, activities.get(0).getStreamId());

    ExoSocialActivity loaded = activityStorage.getActivity(activity.getId());
    assertEquals(demoIdentity.getRemoteId(), loaded.getStreamOwner());
    assertEquals(streamId, loaded.getStreamId());

    tearDownActivityList.add(activity);
  }

  /**
   * Test {@link ActivityStorage#getUserActivities(Identity, long, long)}
   * 
   * @throws ActivityStorageException
   */
  @MaxQueryNumber(550)
  public void testGetActivitiesByPagingWithoutCreatingComments() throws ActivityStorageException {
    checkCleanData();
    final int totalActivityCount = 9;
    final int retrievedCount = 7;

    for (int i = 0; i < totalActivityCount; i++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("blabla");
      activityStorage.saveActivity(demoIdentity, activity);
      tearDownActivityList.add(activityStorage.getUserActivities(demoIdentity, 0, 1).get(0));
    }

    List<ExoSocialActivity> activities = activityStorage.getUserActivities(demoIdentity, 0, retrievedCount);
    assertEquals(retrievedCount, activities.size());
  }

  /**
   * Test {@link ActivityStorage#getUserActivities(Identity, long, long) and 
   * ActivityStorage#saveComment(ExoSocialActivity, ExoSocialActivity)}
   * 
   * @throws ActivityStorageException
   */
  @MaxQueryNumber(150)
  public void testGetActivitiesByPagingWithCreatingComments() throws ActivityStorageException {
    checkCleanData();

    final int totalActivityCount = 2;
    final int retrievedCount = 1;

    for (int i = 0; i < totalActivityCount; i++) {
      // root save on john's stream
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("blabla");
      activity.setUserId(johnIdentity.getId());

      activityStorage.saveActivity(johnIdentity, activity);
      activity = activityStorage.getUserActivities(johnIdentity, 0, 1).get(0);
      //for teardown cleanup
      tearDownActivityList.add(activity);

      //test activity has been created
      String streamId = activity.getStreamId();
      assertNotNull(streamId);
      assertEquals(activity.getStreamOwner(), johnIdentity.getRemoteId());

      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("this is comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);

    }

    List<ExoSocialActivity> activities = activityStorage.getUserActivities(johnIdentity, 0, retrievedCount);
    assertEquals(retrievedCount, activities.size());
  }

  /**
   * 
   * 
   * @throws ActivityStorageException
   */
  @MaxQueryNumber(100)
  public void testTemplateParams() throws ActivityStorageException {
    checkCleanData();
    final String URL_PARAMS = "URL";
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle("blabla");
    activity.setUserId(rootIdentity.getId());
    activity.setUpdated(new Date());

    Map<String, String> templateParams = new HashMap<String, String>();
    templateParams.put(URL_PARAMS, "http://xxxxxxxxxxxxxxxx/xxxx=xxxxx");
    activity.setTemplateParams(templateParams);

    activityStorage.saveActivity(rootIdentity, activity);

    tearDownActivityList.add(activity);

    activity = activityStorage.getUserActivities(rootIdentity, 0, 100).get(0);
    assertNotNull("activity must not be null", activity);
    assertNotNull("activity.getTemplateParams() must not be null", activity.getTemplateParams());
    assertEquals("http://xxxxxxxxxxxxxxxx/xxxx=xxxxx", activity.getTemplateParams().get(URL_PARAMS));
  }

  /**
   * Creates activities.
   * 
   * @param number
   * @param ownerStream
   * @since 1.2.0-Beta3
   */
  private void createActivities(int number, Identity ownerStream) {
    for (int i = 0; i < number; i++) {
      ExoSocialActivity activity = new ExoSocialActivityImpl();
      activity.setTitle("activity title " + i);
      activityStorage.saveActivity(ownerStream, activity);
      tearDownActivityList.add(activity);
    }
  }

  /**
   * Checks clean data.
   * 
   * @since 1.2.0-Beta3
   */
  private void checkCleanData() {
    assertEquals("assertEquals(activityStorage.getActivities(rootIdentity).size() must be 0",
            0, activityStorage.getUserActivities(rootIdentity, 0,
            activityStorage.getNumberOfUserActivities(rootIdentity)).size());
    assertEquals("assertEquals(activityStorage.getActivities(johnIdentity).size() must be 0",
            0, activityStorage.getUserActivities(johnIdentity, 0,
            activityStorage.getNumberOfUserActivities(johnIdentity)).size());
    assertEquals("assertEquals(activityStorage.getActivities(maryIdentity).size() must be 0",
            0, activityStorage.getUserActivities(maryIdentity, 0,
            activityStorage.getNumberOfUserActivities(maryIdentity)).size());
    assertEquals("assertEquals(activityStorage.getActivities(demoIdentity).size() must be 0",
            0, activityStorage.getUserActivities(demoIdentity, 0,
            activityStorage.getNumberOfUserActivities(demoIdentity)).size());
  }

  /**
   * Deletes connections of identity.
   * 
   * @param existingIdentity
   * @since 1.2.0-Beta3
   */
  private void deleteConnections(Identity existingIdentity) throws Exception {
    ListAccess<Identity> allConnections = relationshipManager.getAllWithListAccess(existingIdentity);
    Identity[] list = allConnections.load(0, allConnections.getSize()-1);
    for (int i=0;i<list.length;i++){
      Relationship relationship = relationshipManager.get(existingIdentity, list[i]);
      relationshipManager.delete(relationship);
    }
  }
  
  /**
   * Gets the relationship manager.
   * 
   * @return
   * @since 1.2.0-Beta3
   */
  private RelationshipManager getRelationshipManager() {
    return (RelationshipManager) getContainer().getComponentInstanceOfType(RelationshipManager.class);
  }
  
  /**
   * Gets the space service.
   * 
   * @return the space service
   */
  private SpaceService getSpaceService() {
    return (SpaceService) getContainer().getComponentInstanceOfType(SpaceService.class);
  }
  
  /**
   * Gets an instance of the space.
   * 
   * @param spaceService
   * @param number
   * @return
   * @throws Exception
   * @since 1.2.0-GA
   */
  private Space getSpaceInstance(SpaceService spaceService, int number) throws Exception {
    Space space = new Space();
    space.setDisplayName("my space " + number);
    space.setPrettyName(space.getDisplayName());
    space.setRegistration(Space.OPEN);
    space.setDescription("add new space " + number);
    space.setType(DefaultSpaceApplicationHandler.NAME);
    space.setVisibility(Space.PUBLIC);
    space.setRegistration(Space.VALIDATION);
    space.setPriority(Space.INTERMEDIATE_PRIORITY);
    space.setGroupId("/space/space" + number);
    space.setUrl(space.getPrettyName());
    String[] managers = new String[] {"demo"};
    String[] members = new String[] {"demo"};
    String[] invitedUsers = new String[] {"mary"};
    String[] pendingUsers = new String[] {"john"};
    space.setInvitedUsers(invitedUsers);
    space.setPendingUsers(pendingUsers);
    space.setManagers(managers);
    space.setMembers(members);
    spaceService.saveSpace(space, true);
    return space;
  }
  
  /**
   * Tests {@link ActivityStorage#getNewerOnActivityFeed(Identity, ActivityFilter, int)}.
   */
  @MaxQueryNumber(500)
  public void testGetNewerOnActivityFeedWithActivityFilter() {
    createActivities(3, demoIdentity);
    Long sinceTime = activityStorage.getActivityFeed(demoIdentity, 0, 10).get(0).getPostedTime();
    ActivityFilter af = new ActivityFilter();
    af.setPostedTime(sinceTime);
    assertEquals(0, activityStorage.getNewerOnActivityFeed(demoIdentity, af, 10).size());
    createActivities(1, demoIdentity);
    assertEquals(1, activityStorage.getNewerOnActivityFeed(demoIdentity, af, 10).size());
    createActivities(2, maryIdentity);
    relationshipManager.inviteToConnect(demoIdentity, maryIdentity);
    assertEquals(1, activityStorage.getNewerOnActivityFeed(demoIdentity, af, 10).size());
    relationshipManager.confirm(demoIdentity, maryIdentity);
    createActivities(2, maryIdentity);
    assertEquals(5, activityStorage.getNewerOnActivityFeed(demoIdentity, af, 10).size());

    // Delete the activity at this sinceTime will don't change the result
    String id = activityStorage.getUserActivities(demoIdentity, 0, 10).get(1).getId();
    for (ExoSocialActivity activity : tearDownActivityList) {
      if (id == activity.getId()) {
        tearDownActivityList.remove(activity);
        break;
      }
    }
    activityStorage.deleteActivity(id);
    assertEquals(5, activityStorage.getNewerOnActivityFeed(demoIdentity, af, 10).size());
  }
  
  /**
   * Tests {@link ActivityStorage#getOlderOnActivityFeed(Identity, ActivityFilter, int)}.
   */
  @MaxQueryNumber(200)
  public void testGetOlderOnActivityFeedWithActivityFilter() {
    createActivities(3, demoIdentity);
    Long maxTime = activityStorage.getActivityFeed(demoIdentity, 0, 10).get(2).getPostedTime();
    ActivityFilter af = new ActivityFilter();
    af.setPostedTime(maxTime);
    assertEquals(0, activityStorage.getOlderOnActivityFeed(demoIdentity, af, 10).size());

    // Delete the activity at this maxTime will don't change the result
    String id = activityStorage.getUserActivities(demoIdentity, 0, 10).get(2).getId();
    for (ExoSocialActivity activity : tearDownActivityList) {
      if (id == activity.getId()) {
        tearDownActivityList.remove(activity);
        break;
      }
    }
    activityStorage.deleteActivity(id);
    assertEquals(0, activityStorage.getOlderOnActivityFeed(demoIdentity, af, 10).size());
  }
  
  /**
   * Test {@link ActivityStorage#getNewerComments(ExoSocialActivity, ActivityFilter, int)}
   * 
   * @since 4.0.0
   */
  @MaxQueryNumber(150)
  public void testGetNewerCommentsWithActivityFilter() {
    int totalNumber = 10;
    String activityTitle = "activity title";

    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(activityTitle);
    activity.setUserId(rootIdentity.getId());
    activityStorage.saveActivity(rootIdentity, activity);
    tearDownActivityList.add(activity);

    for (int i = 0; i < totalNumber; i++) {
      // John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("john comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }

    for (int i = 0; i < totalNumber; i++) {
      // Demo comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("demo comment " + i);
      comment.setUserId(demoIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }

    List<ExoSocialActivity> comments = activityStorage.getComments(activity, 0, 10);
    assertNotNull("comments must not be null", comments);
    assertEquals("comments.size() must return: 10", 10, comments.size());

    Long sinceTime = comments.get(0).getPostedTime();
    ActivityFilter af = new ActivityFilter();
    af.setPostedTime(sinceTime);
    List<ExoSocialActivity> newerComments = activityStorage.getNewerComments(activity,af,10);
    assertNotNull("newerComments must not be null", newerComments);
    assertEquals("newerComments.size() must return: 10", 10, newerComments.size());

    af.setPostedTime(activityStorage.getComments(activity, 0, 20).get(10).getPostedTime());
    newerComments = activityStorage.getNewerComments(activity, af, 20);
    assertNotNull("newerComments must not be null", newerComments);
    assertEquals("newerComments.size() must return: 9", 9, newerComments.size());

    af.setPostedTime(activityStorage.getComments(activity, 0, 20).get(19).getPostedTime());
    newerComments = activityStorage.getNewerComments(activity, af, 20);
    assertNotNull("newerComments must not be null", newerComments);
    assertEquals("newerComments.size() must return: 0", 0, newerComments.size());
  }

  /**
   * Test {@link ActivityStorage#getOlderComments(ExoSocialActivity, ActivityFilter, int)}
   * 
   * @since 1.2.12
   */
  @MaxQueryNumber(100)
  public void testGetOlderCommentsWithActivityFilter() {
    int totalNumber = 10;
    String activityTitle = "activity title";

    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(activityTitle);
    activity.setUserId(rootIdentity.getId());
    activityStorage.saveActivity(rootIdentity, activity);
    tearDownActivityList.add(activity);

    for (int i = 0; i < totalNumber; i++) {
      // John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("john comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }

    List<ExoSocialActivity> comments = activityStorage.getComments(activity, 0, 10);
    assertNotNull("comments must not be null", comments);
    assertEquals("comments.size() must return: 10", 10, comments.size());

    Long maxTime = comments.get(0).getPostedTime();
    ActivityFilter af = new ActivityFilter();
    af.setPostedTime(maxTime);
    List<ExoSocialActivity> olderComments = activityStorage.getOlderComments(activity, af, 10);
    assertNotNull("olderComments must not be null", olderComments);
    assertEquals("olderComments.size() must return: 0", 0, olderComments.size());

    af.setPostedTime(comments.get(9).getPostedTime());

    olderComments = activityStorage.getOlderComments(activity, af, 10);
    assertNotNull("olderComments must not be null", olderComments);
    assertEquals("olderComments.size() must return: 9", 9, olderComments.size());

    af.setPostedTime(comments.get(5).getPostedTime());

    olderComments = activityStorage.getOlderComments(activity, af, 10);
    assertNotNull("olderComments must not be null", olderComments);
    assertEquals("olderComments.size() must return: 5", 5, olderComments.size());
  }
  
  /**
   * Tests {@link ActivityStorage#getNumberOfActivitesOnActivityFeed(Identity, ActivityFilter)}.
   */
  @MaxQueryNumber(500)
  public void testGetNumberOfNewerOnActivityFeedWithActivityFilter() {
    createActivities(3, demoIdentity);
    createActivities(2, maryIdentity);
    Relationship maryDemoConnection = relationshipManager.inviteToConnect(maryIdentity, demoIdentity);
    relationshipManager.confirm(maryIdentity, demoIdentity);
    List<ExoSocialActivity> demoActivityFeed = activityStorage.getActivityFeed(demoIdentity, 0, 10);
    ActivityFilter af = new ActivityFilter();
    af.setPostedTime(demoActivityFeed.get(0).getPostedTime());
    int newDemoActivityFeed = activityStorage.getNumberOfNewerOnActivityFeed(demoIdentity, af);
    assertEquals("newDemoActivityFeed must be 0", 0, newDemoActivityFeed);
    createActivities(1, johnIdentity);
    int newDemoActivityFeed2 = activityStorage.getNumberOfNewerOnActivityFeed(demoIdentity, af);
    assertEquals("newDemoActivityFeed2 must be 0", 0, newDemoActivityFeed2);
    createActivities(1, demoIdentity);
    int newDemoActivityFeed3 = activityStorage.getNumberOfNewerOnActivityFeed(demoIdentity, af);
    assertEquals("newDemoActivityFeed3 must be 1", 1, newDemoActivityFeed3);
    createActivities(2, maryIdentity);
    int newDemoActivityFeed4 = activityStorage.getNumberOfNewerOnActivityFeed(demoIdentity, af);
    assertEquals("newDemoActivityFeed must be 3", 3, newDemoActivityFeed4);
  }
  
  /**
   * Tests {@link ActivityStorage#getNumberOfOlderOnActivityFeed(Identity, ExoSocialActivity)}.
   */
  @MaxQueryNumber(350)
  public void testGetNumberOfOlderOnActivityFeedWithActivityFilter() {
    createActivities(3, demoIdentity);
    createActivities(2, maryIdentity);
    Relationship maryDemoConnection = relationshipManager.inviteToConnect(maryIdentity, demoIdentity);
    relationshipManager.confirm(maryIdentity, demoIdentity);
    List<ExoSocialActivity> demoActivityFeed = activityStorage.getActivityFeed(demoIdentity, 0, 10);
    ActivityFilter af = new ActivityFilter();
    af.setPostedTime(demoActivityFeed.get(4).getPostedTime());
    int oldDemoActivityFeed = activityStorage.getNumberOfOlderOnActivityFeed(demoIdentity, af);
    assertEquals("oldDemoActivityFeed must be 0", 0, oldDemoActivityFeed);
    createActivities(1, johnIdentity);
    int oldDemoActivityFeed2 = activityStorage.getNumberOfOlderOnActivityFeed(demoIdentity, af);
    assertEquals("oldDemoActivityFeed2 must be 0", 0, oldDemoActivityFeed2);
    af.setPostedTime(demoActivityFeed.get(3).getPostedTime());
    int oldDemoActivityFeed3 = activityStorage.getNumberOfOlderOnActivityFeed(demoIdentity, af);
    assertEquals("oldDemoActivityFeed3 must be 1", 1, oldDemoActivityFeed3);
  }
  
  /**
   * Test {@link ActivityStorage#getNumberOfNewerComments(ExoSocialActivity, ExoSocialActivity)}
   * 
   * @since 4.0-Alpha01
   */
  @MaxQueryNumber(150)
  public void testGetNumberOfNewerCommentsWithActivityFilter() {
    int totalNumber = 10;
    String activityTitle = "activity title";
    
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(activityTitle);
    activity.setUserId(rootIdentity.getId());
    activityStorage.saveActivity(rootIdentity, activity);
    tearDownActivityList.add(activity);
    
    for (int i = 0; i < totalNumber; i ++) {
      //John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("john comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }
    
    List<ExoSocialActivity> comments = activityStorage.getComments(activity, 0, 10);
    assertNotNull("comments must not be null", comments);
    assertEquals("comments.size() must return: 10", 10, comments.size());

    ExoSocialActivity baseComment = comments.get(0);
    ActivityFilter af = new ActivityFilter();
    af.setPostedTime(baseComment.getPostedTime());
    int number = activityStorage.getNumberOfNewerComments(activity, af);
    assertEquals("number must be: 9", 9, number);
    
    baseComment = activityStorage.getComments(activity, 0, 10).get(9);
    af.setPostedTime(baseComment.getPostedTime());
    number = activityStorage.getNumberOfNewerComments(activity, af);
    assertEquals("number must be: 0", 0, number);
    
    baseComment = activityStorage.getComments(activity, 0, 10).get(5);
    af.setPostedTime(baseComment.getPostedTime());
    number = activityStorage.getNumberOfNewerComments(activity, af);
    assertEquals("number must be: 4", 4, number);
  }
  
  /**
   * Test {@link ActivityStorage#getNumberOfOlderComments(ExoSocialActivity, ExoSocialActivity)}
   * 
   * @since 4.0-Alpha01
   */
  @MaxQueryNumber(100)
  public void testGetNumberOfOlderCommentsWithActivityFilter() {
    int totalNumber = 10;
    String activityTitle = "activity title";
    
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle(activityTitle);
    activity.setUserId(rootIdentity.getId());
    activityStorage.saveActivity(rootIdentity, activity);
    tearDownActivityList.add(activity);
    
    for (int i = 0; i < totalNumber; i ++) {
      //John comments on Root's activity
      ExoSocialActivity comment = new ExoSocialActivityImpl();
      comment.setTitle("john comment " + i);
      comment.setUserId(johnIdentity.getId());
      activityStorage.saveComment(activity, comment);
    }
    
    List<ExoSocialActivity> comments = activityStorage.getComments(activity, 0, 10);
    assertNotNull("comments must not be null", comments);
    assertEquals("comments.size() must return: 10", 10, comments.size());
    
    ExoSocialActivity baseComment = comments.get(0);
    ActivityFilter af = new ActivityFilter();
    af.setPostedTime(baseComment.getPostedTime());
    int number = activityStorage.getNumberOfOlderComments(activity, af);
    assertEquals("number must be: 0", 0, number);
    
    baseComment = comments.get(9);
    af.setPostedTime(baseComment.getPostedTime());
    number = activityStorage.getNumberOfOlderComments(activity, af);
    assertEquals("number must be: 9", 9, number);
    
    baseComment = comments.get(5);
    af.setPostedTime(baseComment.getPostedTime());
    number = activityStorage.getNumberOfOlderComments(activity, af);
    assertEquals("number must be: 5", 5, number);
  }

}
