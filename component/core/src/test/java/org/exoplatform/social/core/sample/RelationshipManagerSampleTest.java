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


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.model.AvatarAttachment;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.exoplatform.social.core.relationship.model.Relationship.Type;
import org.exoplatform.social.core.test.BaseSocialTestCase;

/**
 * Unit Tests for {@link RelationshipManager}
 * 
 * Created by The eXo Platform SAS
 * Author : thanh_vucong
 *          thanh_vucong@exoplatform.com
 * Nov 9, 2012  
 */
public class RelationshipManagerSampleTest extends BaseSocialTestCase {
  private RelationshipManager relationshipManager;
  private IdentityManager identityManager;

  private Identity root,
                   john,
                   mary,
                   demo;

  private List<Relationship> tearDownRelationshipList;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    tearDownRelationshipList = new ArrayList<Relationship>();
    relationshipManager = (RelationshipManager) getContainer().getComponentInstanceOfType(RelationshipManager.class);
    identityManager = (IdentityManager) getContainer().getComponentInstanceOfType(IdentityManager.class);
    assertNotNull("relationshipManager must not be null", relationshipManager);
    assertNotNull("identityManager must not be null", identityManager);
   
  }

  @Override
  protected void tearDown() throws Exception {
    for (Relationship relationship : tearDownRelationshipList) {
      relationshipManager.remove(relationship);
    }
    super.tearDown();
  }

  /**
   * Test {@link RelationshipManager#getAll(Identity)}
   * 
   * @throws Exception
   */
  public void testGetAll() throws Exception {
    relationshipManager.invite(john, demo);
    List<Relationship> senderRelationships = relationshipManager.getAll(john);
    List<Relationship> receiverRelationships = relationshipManager.getAll(demo);

    assertEquals(1, senderRelationships.size());
    assertEquals(1, receiverRelationships.size());

    tearDownRelationshipList.addAll(senderRelationships);
  }

  /**
   * Test {@link RelationshipManager#getAll(Identity, List)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetAllWithListIdentities() throws Exception {
    List<Identity> listIdentities = new ArrayList<Identity>();
    listIdentities.add(root);
    listIdentities.add(demo);
    listIdentities.add(john);
    listIdentities.add(mary);
    
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    List<Relationship> rootRelationships = relationshipManager.getAll(root, listIdentities);
    assertNotNull("rootRelationships must not be null", rootRelationships);
    assertEquals("rootRelationships.size() must return: 3", 3, rootRelationships.size());
    
    List<Relationship> maryRelationships = relationshipManager.getAll(mary, listIdentities);
    assertNotNull("maryRelationships must not be null", maryRelationships);
    assertEquals("maryRelationships.size() mut return: 1", 1, maryRelationships.size());
    
    List<Relationship> johnRelationships = relationshipManager.getAll(john, listIdentities);
    assertNotNull("johnRelationships must not be null", johnRelationships);
    assertEquals("johnRelationships.size() mut return: 1", 1, johnRelationships.size());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getAll(Identity, Type, List)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetAllWithTypeAndListIdentities() throws Exception {
    List<Identity> listIdentities = new ArrayList<Identity>();
    listIdentities.add(root);
    listIdentities.add(demo);
    listIdentities.add(john);
    listIdentities.add(mary);
    
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    List<Relationship> rootPendingRelationships = relationshipManager.getAll(root, Relationship.Type.PENDING, listIdentities);
    assertNotNull("rootPendingRelationships must not be null", rootPendingRelationships);
    assertEquals("rootPendingRelationships.size() must return: 3", 3, rootPendingRelationships.size());
    
    List<Relationship> maryPendingRelationships = relationshipManager.getAll(mary, Relationship.Type.PENDING, listIdentities);
    assertNotNull("maryPendingRelationships must not be null", maryPendingRelationships);
    assertEquals("maryPendingRelationships.size() mut return: 1", 1, maryPendingRelationships.size());
    
    List<Relationship> johnPendingRelationships = relationshipManager.getAll(mary, Relationship.Type.PENDING, listIdentities);
    assertNotNull("johnPendingRelationships must not be null", johnPendingRelationships);
    assertEquals("johnPendingRelationships.size() mut return: 1", 1, johnPendingRelationships.size());
    
    relationshipManager.confirm(demo, root);
    
    List<Relationship> rootConfirmedRelationships = relationshipManager.getAll(root, Relationship.Type.CONFIRMED, listIdentities);
    assertNotNull("rootConfirmedRelationships must not be null", rootConfirmedRelationships);
    assertEquals("rootConfirmedRelationships.size() must return: 1", 1, rootConfirmedRelationships.size());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#get(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGet() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    rootToDemoRelationship = relationshipManager.get(root, demo);
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getSender() must return: " + root, root, rootToDemoRelationship.getSender());
    assertEquals("rootToDemoRelationship.getReceiver() must return: " + demo, demo, rootToDemoRelationship.getReceiver());
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.PENDING, 
                 Relationship.Type.PENDING, rootToDemoRelationship.getStatus());
    
    relationshipManager.confirm(john, root);
    rootToJohnRelationship = relationshipManager.get(john, root);
    assertEquals("rootToJohnRelationship.getStatus() must return: ", Relationship.Type.CONFIRMED, 
                 rootToJohnRelationship.getStatus());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#get(String)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetWithRelationshipId() throws Exception {
    Relationship relationship = relationshipManager.inviteToConnect(root, john);
    String relationshipId = relationship.getId();
    
    relationshipManager.confirm(john, root);
    relationship = relationshipManager.get(relationship.getId());
    assertNotNull("relationship must not be null", relationship);
    assertEquals("relationship.getStatus() must return: " + Relationship.Type.CONFIRMED, Relationship.Type.CONFIRMED, relationship.getStatus());
    
    relationshipManager.delete(relationship);

    relationship = relationshipManager.get(root, john);
    assertNull("relationship must be null", relationship);
    
    relationship = relationshipManager.get(relationshipId);
    assertNull("relationship must be null", relationship);
  }
  
  /**
   * Test {@link RelationshipManager#update(Relationship)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testUpdate() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, rootToDemoRelationship.getStatus());
    rootToDemoRelationship.setStatus(Relationship.Type.CONFIRMED);
    relationshipManager.update(rootToDemoRelationship);
    rootToDemoRelationship = relationshipManager.get(root, demo);
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.CONFIRMED,
                 Relationship.Type.CONFIRMED, rootToDemoRelationship.getStatus());
    
    assertEquals("maryToRootRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, maryToRootRelationship.getStatus());
    maryToRootRelationship.setStatus(Relationship.Type.IGNORED);
    relationshipManager.update(maryToRootRelationship);
    maryToRootRelationship = relationshipManager.get(mary, root);
    assertEquals("maryToRootRelationship.getStatus() must return: " + Relationship.Type.IGNORED,
                 Relationship.Type.IGNORED, maryToRootRelationship.getStatus());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#inviteToConnect(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testInviteToConnect() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    rootToDemoRelationship = relationshipManager.get(root, demo);
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, rootToDemoRelationship.getStatus());
    
    maryToRootRelationship = relationshipManager.get(mary, root);
    assertNotNull("maryToRootRelationship must not be null", maryToRootRelationship);
    assertEquals("maryToRootRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, maryToRootRelationship.getStatus());
    
    rootToJohnRelationship = relationshipManager.get(john, root);
    assertNotNull("rootToJohnRelationship must not be null", rootToJohnRelationship);
    assertEquals("rootToJohnRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, rootToJohnRelationship.getStatus());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#inviteToConnect(Identity, Identity)}
   *
   * @throws Exception
   * @since 1.2.0-Beta3
  */
  public void testDupdicateInviteToConnect() throws Exception {
    Relationship relationship1 = relationshipManager.inviteToConnect(root, demo);
    Relationship relationship2 = relationshipManager.inviteToConnect(root, demo);
    assertEquals("relationShip1 and relationShip2 must be the same",relationship1.getId(), relationship2.getId());
    tearDownRelationshipList.add(relationship1);
  }
  
  /**
   * Test {@link RelationshipManager#inviteToConnect(Identity, Identity)}
   *
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testDupdicateInviteToConnectWithConfirmedRelationShip() throws Exception {
    Relationship relationship1 = relationshipManager.inviteToConnect(root, demo);
    assertEquals("RelationShip status must be PENDING",Relationship.Type.PENDING, relationship1.getStatus());
    relationshipManager.confirm(root, demo);
    relationship1 = relationshipManager.get(root, demo);
    assertEquals("RelationShip status must be CONFIRMED",Relationship.Type.CONFIRMED, relationship1.getStatus());
    Relationship relationship2 = relationshipManager.inviteToConnect(root, demo);
    assertEquals("RelationShip status must be CONFIRMED",Relationship.Type.CONFIRMED, relationship2.getStatus());
    
    assertEquals("relationShip1 and relationShip2 must be the same",relationship1.getId(), relationship2.getId());
    
    tearDownRelationshipList.add(relationship1);
  }
  
  /**
   * Test {@link RelationshipManager#confirm(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testConfirmWithIdentity() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    rootToJohnRelationship = relationshipManager.get(rootToJohnRelationship.getId());
    
    relationshipManager.confirm(root, demo);
    rootToDemoRelationship = relationshipManager.get(root, demo);
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.CONFIRMED,
                 Relationship.Type.CONFIRMED, rootToDemoRelationship.getStatus());
    
    relationshipManager.confirm(mary, root);
    maryToRootRelationship = relationshipManager.get(mary, root);
    assertNotNull("maryToRootRelationship must not be null", maryToRootRelationship);
    assertEquals("maryToRootRelationship.getStatus() must return: " + Relationship.Type.CONFIRMED,
                 Relationship.Type.CONFIRMED, maryToRootRelationship.getStatus());
    
    relationshipManager.confirm(root, john);
    rootToJohnRelationship = relationshipManager.get(john, root);
    assertNotNull("rootToJohnRelationship must not be null", rootToJohnRelationship);
    assertEquals("rootToJohnRelationship.getStatus() must return: " + Relationship.Type.CONFIRMED,
                 Relationship.Type.CONFIRMED, rootToJohnRelationship.getStatus());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#deny(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testDeny() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    relationshipManager.confirm(john, root);
    relationshipManager.deny(john, root);
    assertNotNull(relationshipManager.get(rootToJohnRelationship.getId()));
    
    rootToJohnRelationship.setStatus(Relationship.Type.PENDING);
    relationshipManager.update(rootToJohnRelationship);
    
    relationshipManager.deny(demo, root);
    rootToDemoRelationship = relationshipManager.get(root, demo);
    assertNull("rootToDemoRelationship must be null", rootToDemoRelationship);
    
    relationshipManager.deny(mary, root);
    maryToRootRelationship = relationshipManager.get(mary, root);
    assertNull("maryToRootRelationship must be null", maryToRootRelationship);
    
    relationshipManager.deny(root, john);
    rootToJohnRelationship = relationshipManager.get(root, john);
    assertNull("rootToJohnRelationship must be null", rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#ignore(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testIgnore() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    relationshipManager.ignore(demo, root);
    rootToDemoRelationship = relationshipManager.get(root, demo);
    assertNull("rootToDemoRelationship must be null", rootToDemoRelationship);
    
    relationshipManager.ignore(mary, root);
    maryToRootRelationship = relationshipManager.get(mary, root);
    assertNull("maryToRootRelationship must be null", maryToRootRelationship);
    
    relationshipManager.ignore(root, john);
    rootToJohnRelationship = relationshipManager.get(root, john);
    assertNull("rootToJohnRelationship must be null", rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getIncomingWithListAccess(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetIncomingWithListAccess() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToDemoRelationship = relationshipManager.inviteToConnect(mary, demo);
    Relationship johnToDemoRelationship = relationshipManager.inviteToConnect(john, demo);
    
    ListAccess<Identity> demoIncoming = relationshipManager.getIncomingWithListAccess(demo);
    assertNotNull("demoIncoming must not be null", demoIncoming);
    assertEquals("demoIncoming.getSize() must return: 3", 3, demoIncoming.getSize());
    
    //Test change avatar
    InputStream inputStream = getClass().getResourceAsStream("/eXo-Social.png");
    AvatarAttachment avatarAttachment = new AvatarAttachment(null, "avatar", "png", inputStream, null, System.currentTimeMillis());
    assertNotNull(avatarAttachment);
    
    Profile profile = mary.getProfile();
    profile.setProperty(Profile.AVATAR, avatarAttachment);
    identityManager.updateProfile(profile);
    
    Identity[] identities = demoIncoming.load(0, 10);
    demo = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, demo.getRemoteId(), true);

    assertEquals(6, identities[0].getProfile().getProperties().size());
    assertEquals(6, identities[1].getProfile().getProperties().size());
    assertEquals(6, identities[2].getProfile().getProperties().size());
    
    for (Identity identity : demoIncoming.load(0, 10)) {
      assertNotNull("identity.getProfile() must not be null", identity.getProfile());
      Identity identityLoadProfile = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, identity.getRemoteId(), true);
      assertEquals("identity.getProfile().getFullName() must return: " + identityLoadProfile.getProfile().getFullName(),
                   identityLoadProfile.getProfile().getFullName(), identity.getProfile().getFullName());
    }
    
    ListAccess<Identity> rootIncoming = relationshipManager.getIncomingWithListAccess(root);
    assertNotNull("rootIncoming must not be null", rootIncoming);
    assertEquals("rootIncoming.getSize() must return: 0", 0, rootIncoming.getSize());
    
    ListAccess<Identity> maryIncoming = relationshipManager.getIncomingWithListAccess(mary);
    assertNotNull("maryIncoming must not be null", maryIncoming);
    assertEquals("maryIncoming.getSize() must return: 0", 0, maryIncoming.getSize());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToDemoRelationship);
    tearDownRelationshipList.add(johnToDemoRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getOutgoing(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetOutgoing() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship rootToMaryRelationship = relationshipManager.inviteToConnect(root, mary);
    Relationship maryToDemoRelationship = relationshipManager.inviteToConnect(mary, demo);
    Relationship demoToJohnRelationship = relationshipManager.inviteToConnect(demo, john);
    
    ListAccess<Identity> rootOutgoing = relationshipManager.getOutgoing(root);
    assertNotNull("rootOutgoing must not be null", rootOutgoing);
    assertEquals("rootOutgoing.getSize() must return: 2", 2, rootOutgoing.getSize());
    
    //Test change avatar
    InputStream inputStream = getClass().getResourceAsStream("/eXo-Social.png");
    AvatarAttachment avatarAttachment = new AvatarAttachment(null, "avatar", "png", inputStream, null, System.currentTimeMillis());
    assertNotNull(avatarAttachment);
    
    Profile profile = demo.getProfile();
    profile.setProperty(Profile.AVATAR, avatarAttachment);
    identityManager.updateProfile(profile);
    
    rootOutgoing = relationshipManager.getOutgoing(root);
    Identity[] identities = rootOutgoing.load(0, 10);
    demo = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, demo.getRemoteId(), true);

    assertEquals(6, identities[0].getProfile().getProperties().size());
    assertEquals(6, identities[1].getProfile().getProperties().size());
    
    for (Identity identity : rootOutgoing.load(0, 10)) {
      Identity identityLoadProfile = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, identity.getRemoteId(), true);
      assertNotNull("identity.getProfile() must not be nul", identity.getProfile());
      assertNotNull("temp must not be null", identityLoadProfile);
      assertEquals("identity.getProfile().getFullName() must return: " + identityLoadProfile.getProfile().getFullName(), 
                   identityLoadProfile.getProfile().getFullName(), 
                   identity.getProfile().getFullName());
    }
    
    ListAccess<Identity> maryOutgoing = relationshipManager.getOutgoing(mary);
    assertNotNull("maryOutgoing must not be null", maryOutgoing);
    assertEquals("maryOutgoing.getSize() must return: 1", 1, maryOutgoing.getSize());
    
    ListAccess<Identity> demoOutgoing = relationshipManager.getOutgoing(demo);
    assertNotNull("demoOutgoing must not be null", demoOutgoing);
    assertEquals("demoOutgoing.getSize() must return: 1", 1, demoOutgoing.getSize());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToDemoRelationship);
    tearDownRelationshipList.add(demoToJohnRelationship);
    tearDownRelationshipList.add(rootToMaryRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getStatus(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetStatus() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    rootToDemoRelationship = relationshipManager.get(root, demo);
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, rootToDemoRelationship.getStatus());
    assertEquals("relationshipManager.getStatus(rootIdentity, demoIdentity) must return: " + 
                 Relationship.Type.PENDING, Relationship.Type.PENDING, 
                 relationshipManager.getStatus(root, demo));
    
    maryToRootRelationship = relationshipManager.get(mary, root);
    assertNotNull("maryToRootRelationship must not be null", maryToRootRelationship);
    assertEquals("maryToRootRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, maryToRootRelationship.getStatus());
    assertEquals("relationshipManager.getStatus(maryIdentity, rootIdentity) must return: " + 
                 Relationship.Type.PENDING, Relationship.Type.PENDING,
                 relationshipManager.getStatus(mary, root));
    
    rootToJohnRelationship = relationshipManager.get(john, root);
    assertNotNull("rootToJohnRelationship must not be null", rootToJohnRelationship);
    assertEquals("rootToJohnRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, rootToJohnRelationship.getStatus());
    assertEquals("relationshipManager.getStatus(rootIdentity, johnIdentity) must return: " +
                 Relationship.Type.PENDING, Relationship.Type.PENDING,
                 relationshipManager.getStatus(root, john));
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getAllWithListAccess(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetAllWithListAccess() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    ListAccess<Identity> rootRelationships = relationshipManager.getAllWithListAccess(root);
    assertNotNull("rootRelationships must not be null", rootRelationships);
    assertEquals("rootRelationships.getSize() must return: 3", 3, rootRelationships.getSize());
    
    ListAccess<Identity> demoRelationships = relationshipManager.getAllWithListAccess(demo);
    assertNotNull("demoRelationships must not be null", demoRelationships);
    assertEquals("demoRelationships.getSize() must return: 1", 1, demoRelationships.getSize());
    
    ListAccess<Identity> johnRelationships = relationshipManager.getAllWithListAccess(john);
    assertNotNull("johnRelationships must not be null", johnRelationships);
    assertEquals("johnRelationships.getSize() must return: 1", 1, johnRelationships.getSize());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getRelationshipById(String)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetRelationshipById() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    
    rootToDemoRelationship = relationshipManager.getRelationshipById(rootToDemoRelationship.getId());
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getSender() must return: " + root, root, rootToDemoRelationship.getSender());
    assertEquals("rootToDemoRelationship.getReceiver() must return: " + demo, demo, rootToDemoRelationship.getReceiver());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#deny(Relationship)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testDenyWithRelationship() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    relationshipManager.deny(rootToDemoRelationship);
    rootToDemoRelationship = relationshipManager.get(root, demo);
    assertNull("rootToDemoRelationship must be null", rootToDemoRelationship);
    
    relationshipManager.deny(maryToRootRelationship);
    maryToRootRelationship = relationshipManager.get(mary, root);
    assertNull("maryToRootRelationship must be null", maryToRootRelationship);
    
    relationshipManager.deny(rootToJohnRelationship);
    rootToJohnRelationship = relationshipManager.get(root, john);
    assertNull("rootToJohnRelationship must be null", rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#ignore(Relationship)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testIgnoreWithRelationship() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    relationshipManager.ignore(rootToDemoRelationship);
    rootToDemoRelationship = relationshipManager.get(root, demo);
    assertNull("rootToDemoRelationship must be null", rootToDemoRelationship);
    
    relationshipManager.ignore(maryToRootRelationship);
    maryToRootRelationship = relationshipManager.get(mary, root);
    assertNull("maryToRootRelationship must be null", maryToRootRelationship);
    
    relationshipManager.ignore(rootToJohnRelationship);
    rootToJohnRelationship = relationshipManager.get(root, john);
    assertNull("rootToJohnRelationship must be null", rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getPendingRelationships(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetPendingRelationships() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    List<Relationship> rootPendingRelationships = relationshipManager.getPendingRelationships(root);
    assertNotNull("rootPendingRelationships must not be null", rootPendingRelationships);
    assertEquals("rootPendingRelationships.size() must return: 2", 2, rootPendingRelationships.size());
    
    List<Relationship> maryPendingRelationships = relationshipManager.getPendingRelationships(mary);
    assertNotNull("maryPendingRelationships must not be null", maryPendingRelationships);
    assertEquals("maryPendingRelationships.size() must return: 1", 1, maryPendingRelationships.size());
    
    List<Relationship> demoPendingRelationships = relationshipManager.getPendingRelationships(demo);
    assertNotNull("demoPendingRelationships must not be null", demoPendingRelationships);
    assertEquals("demoPendingRelationships.size() must return: 0", 0, demoPendingRelationships.size());
    
    List<Relationship> johnPendingRelationships = relationshipManager.getPendingRelationships(john);
    assertNotNull("johnPendingRelationships must not be null", johnPendingRelationships);
    assertEquals("johnPendingRelationships.size() must return: 0", 0, johnPendingRelationships.size());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getPendingRelationships(Identity, boolean)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetPendingRelationshipWithSentOrReceived() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    List<Relationship> rootPendingRelationships = relationshipManager.getPendingRelationships(root, true);
    assertNotNull("rootPendingRelationships must not be null", rootPendingRelationships);
    assertEquals("rootPendingRelationships.size() must return: 3", 3, rootPendingRelationships.size());
    
    List<Relationship> maryPendingRelationships = relationshipManager.getPendingRelationships(mary, true);
    assertNotNull("maryPendingRelationships must not be null", maryPendingRelationships);
    assertEquals("maryPendingRelationships.size() must return: 1", 1, maryPendingRelationships.size());
    
    List<Relationship> demoPendingRelationships = relationshipManager.getPendingRelationships(demo, true);
    assertNotNull("demoPendingRelationships must not be null", demoPendingRelationships);
    assertEquals("demoPendingRelationships.size() must return: 1", 1, demoPendingRelationships.size());
    
    List<Relationship> johnPendingRelationships = relationshipManager.getPendingRelationships(john, true);
    assertNotNull("johnPendingRelationships must not be null", johnPendingRelationships);
    assertEquals("johnPendingRelationships.size() must return: 1", 1, johnPendingRelationships.size());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getPendingRelationships(Identity, List, boolean)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetPendingRealtionshipWithListIdentities() throws Exception {
    List<Identity> identities = new ArrayList<Identity> ();
    identities.add(root);
    identities.add(demo);
    identities.add(john);
    identities.add(mary);
    
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    List<Relationship> rootPendingRelationships = relationshipManager.getPendingRelationships(root, identities, true);
    assertNotNull("rootPendingRelationships must not be null", rootPendingRelationships);
    assertEquals("rootPendingRelationships.size() must return: 3", 3, rootPendingRelationships.size());
    
    List<Relationship> maryPendingRelationships = relationshipManager.getPendingRelationships(mary, identities, true);
    assertNotNull("maryPendingRelationships must not be null", maryPendingRelationships);
    assertEquals("maryPendingRelationships.size() must return: 1", 1, maryPendingRelationships.size());
    
    List<Relationship> demoPendingRelationships = relationshipManager.getPendingRelationships(demo, identities, true);
    assertNotNull("demoPendingRelationships must not be null", demoPendingRelationships);
    assertEquals("demoPendingRelationships.size() must return: 1", 1, demoPendingRelationships.size());
    
    List<Relationship> johnPendingRelationships = relationshipManager.getPendingRelationships(john, identities, true);
    assertNotNull("johnPendingRelationships must not be null", johnPendingRelationships);
    assertEquals("johnPendingRelationships.size() must return: 1", 1, johnPendingRelationships.size());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getContacts(Identity, List)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetContactsWithListIdentities() throws Exception {
    List<Identity> identities = new ArrayList<Identity> ();
    identities.add(root);
    identities.add(demo);
    identities.add(john);
    identities.add(mary);
    
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    relationshipManager.confirm(root, demo);
    relationshipManager.confirm(mary, root);
    relationshipManager.confirm(root, john);
    
    List<Relationship> rootContacts = relationshipManager.getContacts(root, identities);
    assertNotNull("rootContacts must not be null", rootContacts);
    assertEquals("rootContacts.size() must return: 3", 3, rootContacts.size());
    
    List<Relationship> demoContacts = relationshipManager.getContacts(demo, identities);
    assertNotNull("demoContacts must not be null", demoContacts);
    assertEquals("demoContacts.size() must return: 1", 1, demoContacts.size());
    
    List<Relationship> maryContacts = relationshipManager.getContacts(mary, identities);
    assertNotNull("maryContacts must not be null", maryContacts);
    assertEquals("maryContacts.size() must return: 1", 1, maryContacts.size());
    
    List<Relationship> johnContacts = relationshipManager.getContacts(john, identities);
    assertNotNull("johnContacts must not be null", johnContacts);
    assertEquals("johnContacts.size() must return: 1", 1, johnContacts.size());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getContacts(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetContacts() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    relationshipManager.confirm(root, demo);
    relationshipManager.confirm(mary, root);
    relationshipManager.confirm(root, john);
    
    List<Relationship> rootContacts = relationshipManager.getContacts(root);
    assertNotNull("rootContacts must not be null", rootContacts);
    assertEquals("rootContacts.size() must return: 3", 3, rootContacts.size());
    
    List<Relationship> demoContacts = relationshipManager.getContacts(demo);
    assertNotNull("demoContacts must not be null", demoContacts);
    assertEquals("demoContacts.size() must return: 1", 1, demoContacts.size());
    
    List<Relationship> maryContacts = relationshipManager.getContacts(mary);
    assertNotNull("maryContacts must not be null", maryContacts);
    assertEquals("maryContacts.size() must return: 1", 1, maryContacts.size());
    
    List<Relationship> johnContacts = relationshipManager.getContacts(john);
    assertNotNull("johnContacts must not be null", johnContacts);
    assertEquals("johnContacts.size() must return: 1", 1, johnContacts.size());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getAllRelationships(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetAllRelationships() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    List<Relationship> rootRelationships = relationshipManager.getAllRelationships(root);
    assertNotNull("rootRelationships must not be null", rootRelationships);
    assertEquals("rootRelationships.size() must return: 3", 3, rootRelationships.size());
    
    List<Relationship> maryRelationships = relationshipManager.getAllRelationships(mary);
    assertNotNull("maryRelationships must not be null", maryRelationships);
    assertEquals("maryRelationships.size() must return: 1", 1, maryRelationships.size());
    
    List<Relationship> demoRelationships = relationshipManager.getAllRelationships(demo);
    assertNotNull("demoRelationships must not be null", demoRelationships);
    assertEquals("demoRelationships.size() must return: 1", 1, demoRelationships.size());
    
    List<Relationship> johnRelationships = relationshipManager.getAllRelationships(john);
    assertNotNull("johnRelationships must not be null", johnRelationships);
    assertEquals("johnRelationships.size() must return: 1", 1, johnRelationships.size());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getRelationshipsByIdentityId(String)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetRelationshipsByIdentityId() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    List<Relationship> rootRelationships = relationshipManager.getRelationshipsByIdentityId(root.getId());
    assertNotNull("rootRelationships must not be null", rootRelationships);
    assertEquals("rootRelationships.size() must return: 3", 3, rootRelationships.size());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getIdentities(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetIdentities() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    relationshipManager.confirm(root, demo);
    relationshipManager.confirm(mary, root);
    relationshipManager.confirm(root, john);
    
    List<Identity> rootConnections = relationshipManager.getIdentities(root);
    assertNotNull("rootConnections must not be null", rootConnections);
    assertEquals("rootConnections.size() must return: 3", 3, rootConnections.size());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#create(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testCreate() throws Exception {
    Relationship demoToJohnRelationship = relationshipManager.create(demo, john);
    assertNotNull("demoToJohnRelationship must not be null", demoToJohnRelationship);
    assertEquals("demoToJohnRelationship.getSender() must return: " + demo, demo, demoToJohnRelationship.getSender());
    assertEquals("demoToJohnRelationship.getReceiver() must return: " + john, john, demoToJohnRelationship.getReceiver());
    assertEquals("demoToJohnRelationship.getStatus() must return: " + Relationship.Type.PENDING, Relationship.Type.PENDING, demoToJohnRelationship.getStatus());
  }
  
  /**
   * Test {@link RelationshipManager#getRelationship(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetRelationship() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    
    rootToDemoRelationship = relationshipManager.getRelationship(root, demo);
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.PENDING, Relationship.Type.PENDING, rootToDemoRelationship.getStatus());
    
    relationshipManager.confirm(root, demo);
    
    rootToDemoRelationship = relationshipManager.getRelationship(root, demo);
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.CONFIRMED, Relationship.Type.CONFIRMED, rootToDemoRelationship.getStatus());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#findRelationships(Identity, Type)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testFindRelationships() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(mary, root);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(root, john);
    
    List<Identity> rootRelationships = relationshipManager.findRelationships(root, Relationship.Type.PENDING);
    assertNotNull("rootRelationships must not be null", rootRelationships);
    assertEquals("rootRelationships.size() must return: 3", 3, rootRelationships.size());
    
    relationshipManager.confirm(root, demo);
    
    rootRelationships = relationshipManager.findRelationships(root, Relationship.Type.CONFIRMED);
    assertNotNull("rootRelationships must not be null", rootRelationships);
    assertEquals("rootRelationships.size() must return: 1", 1, rootRelationships.size());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getRelationshipStatus(Relationship, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetRelationshipStatus() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    assertEquals("relationshipManager.getRelationshipStatus(rootToDemoRelationship, rootIdentity) must return: "
                 + Relationship.Type.PENDING, Relationship.Type.PENDING
                 , relationshipManager.getRelationshipStatus(rootToDemoRelationship, root));
    
    relationshipManager.confirm(root, demo);
    rootToDemoRelationship = relationshipManager.get(root, demo);
    assertEquals("relationshipManager.getRelationshipStatus(rootToDemoRelationship, rootIdentity) must return: "
                 + Relationship.Type.PENDING, Relationship.Type.CONFIRMED
                 , relationshipManager.getRelationshipStatus(rootToDemoRelationship, root));
    
    tearDownRelationshipList.add(rootToDemoRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getConnectionStatus(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetConnectionStatus() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(root, demo);
    
    assertEquals("relationshipManager.getConnectionStatus(rootIdentity, demoIdentity) must return: " + 
                 Relationship.Type.PENDING, Relationship.Type.PENDING, 
                 relationshipManager.getConnectionStatus(root, demo));
    
    relationshipManager.confirm(root, demo);
    assertEquals("relationshipManager.getConnectionStatus(rootIdentity, demoIdentity) must return: " + 
                 Relationship.Type.CONFIRMED, Relationship.Type.CONFIRMED, 
                 relationshipManager.getConnectionStatus(root, demo));
    
    tearDownRelationshipList.add(rootToDemoRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#invite(Identity, Identity) and RelationshipManager#get(String)}
   *
   * @throws Exception
   */
  public void testIntiveAndGetByRelationshipId() throws Exception {
    Relationship invitedRelationship = relationshipManager.invite(john, mary);
    
    Relationship foundRelationship = relationshipManager.get(invitedRelationship.getId());
    assertNotNull("foundRelationship must not be null", foundRelationship);
    assertNotNull("foundRelationship.getId() must not be null", foundRelationship.getId());
    assertEquals(foundRelationship.getId(), invitedRelationship.getId());

    tearDownRelationshipList.add(invitedRelationship);
  }

  /**
   * Test {@link RelationshipManager#getPending(Identity)}
   *
   * @throws Exception
   */
  public void testGetPendingWithIdentity() throws Exception {
    Relationship johnDemoRelationship = relationshipManager.invite(john, demo);
    Relationship johnMaryRelationship = relationshipManager.invite(john, mary);
    Relationship johnRootRelationship = relationshipManager.invite(john, root);

    List<Relationship> foundListRelationships = relationshipManager.getPending(john);
    assertNotNull("foundListRelationships must not be null", foundListRelationships);
    assertEquals(3, foundListRelationships.size());

    tearDownRelationshipList.add(johnDemoRelationship);
    tearDownRelationshipList.add(johnMaryRelationship);
    tearDownRelationshipList.add(johnRootRelationship);
  }

  /**
   * Test {@link RelationshipManager#getPending(Identity) and RelationshipManager#getIncoming(Identity)}
   *
   * @throws Exception
   */
  public void testGetPendingAndIncoming() throws Exception {
    Relationship johnDemoRelationship = relationshipManager.invite(john, demo);
    Relationship johnMaryRelationship = relationshipManager.invite(john, mary);
    Relationship johnRootRelationship = relationshipManager.invite(john, root);

    List<Relationship> listPendingRelationship = relationshipManager.getPending(john);
    assertNotNull("listRelationshipConfirm must not be null", listPendingRelationship);
    assertEquals(3, listPendingRelationship.size());

    List<Relationship> listMaryRequireValidationRelationship = relationshipManager.getIncoming(mary);
    assertEquals(1, listMaryRequireValidationRelationship.size());

    tearDownRelationshipList.add(johnDemoRelationship);
    tearDownRelationshipList.add(johnMaryRelationship);
    tearDownRelationshipList.add(johnRootRelationship);
  }

  /**
   * Test {@link RelationshipManager#getPending(Identity) and RelationshipManager#getIncoming(Identity, List)}
   *
   * @throws Exception
   */
  public void testGetPendingAndIncomingWithListIdentities() throws Exception {
    Relationship johnDemoRelationship = relationshipManager.invite(john, demo);
    Relationship johnMaryRelationship = relationshipManager.invite(john, mary);
    Relationship johnRootRelationship = relationshipManager.invite(john, root);
    Relationship maryDemoRelationship = relationshipManager.invite(mary, demo);

    List<Identity> listIdentities = new ArrayList<Identity>();
    listIdentities.add(demo);
    listIdentities.add(mary);
    listIdentities.add(john);
    listIdentities.add(root);

    List<Relationship> listRelationshipConfirm = relationshipManager.getPending(john, listIdentities);
    assertEquals(3, listRelationshipConfirm.size());

    List<Relationship> listRelationshipNotConfirm = relationshipManager.getIncoming(demo, listIdentities);
    assertEquals(2, listRelationshipNotConfirm.size());

    tearDownRelationshipList.add(johnDemoRelationship);
    tearDownRelationshipList.add(johnMaryRelationship);
    tearDownRelationshipList.add(johnRootRelationship);
    tearDownRelationshipList.add(maryDemoRelationship);
  }

  /**
   * Test {@link RelationshipManager#getConfirmed(Identity)}
   *
   * @throws Exception
   */
  public void testGetConfirmedWithIdentity() throws Exception {
    List<Relationship> johnContacts = relationshipManager.getConfirmed(john);
    assertNotNull("johnContacts must not be null", johnContacts);
    assertEquals("johnContacts.size() must be 0", 0, johnContacts.size());

    Relationship johnDemoRelationship = relationshipManager.invite(john, demo);
    Relationship johnMaryRelationship = relationshipManager.invite(john, mary);
    Relationship johnRootRelationship = relationshipManager.invite(john, root);

    relationshipManager.confirm(johnDemoRelationship);
    relationshipManager.confirm(johnMaryRelationship);
    relationshipManager.confirm(johnRootRelationship);

    List<Relationship> contactsList = relationshipManager.getConfirmed(john);
    assertEquals(3, contactsList.size());

    tearDownRelationshipList.add(johnDemoRelationship);
    tearDownRelationshipList.add(johnMaryRelationship);
    tearDownRelationshipList.add(johnRootRelationship);
  }

  /**
   * Test {@link RelationshipManager#getConfirmed(Identity, List)}
   *
   * @throws Exception
   */
  public void testGetConfirmedWithIdentityAndListIdentity() throws Exception {
    Relationship johnDemoRelationship = relationshipManager.invite(john, demo);
    Relationship johnMaryRelationship = relationshipManager.invite(john, mary);
    Relationship johnRootRelationship = relationshipManager.invite(john, root);

    relationshipManager.confirm(johnDemoRelationship);
    relationshipManager.confirm(johnMaryRelationship);
    relationshipManager.confirm(johnRootRelationship);

    List<Identity> listIdentities = new ArrayList<Identity>();
    listIdentities.add(demo);
    listIdentities.add(mary);
    listIdentities.add(john);
    listIdentities.add(root);

    List<Relationship> contactsList = relationshipManager.getConfirmed(john, listIdentities);
    assertEquals(3, contactsList.size());
    tearDownRelationshipList.add(johnDemoRelationship);
    tearDownRelationshipList.add(johnMaryRelationship);
    tearDownRelationshipList.add(johnRootRelationship);
  }

  /**
   * Test {@link RelationshipManager#save(Relationship)}
   *
   * @throws Exception
   */
  public void testSave() throws Exception {
    Relationship testRelationship = new Relationship(john, demo, Type.PENDING);
    relationshipManager.save(testRelationship);
    assertNotNull("testRelationship.getId() must not be null", testRelationship.getId());

    tearDownRelationshipList.add(testRelationship);
  }

  /**
   *
   * @throws Exception
   */
  /*
  public void testGetManyRelationshipsByIdentityId() throws Exception {
    String providerId = OrganizationIdentityProvider.NAME;

    Identity sender = identityManager.getOrCreateIdentity(providerId,"john");
    identityManager.saveIdentity(sender);
    assertNotNull(sender.getId());

    Identity receiver = identityManager.getOrCreateIdentity(providerId,"mary");
    assertNotNull(receiver.getId());

    relationshipManager.invite(sender, receiver);

    List<Relationship> senderRelationships = relationshipManager.getAllRelationships(sender);
    List<Relationship> receiverRelationships = relationshipManager.getAllRelationships(receiver);

    assertEquals(total, senderRelationships.size());
    assertEquals(total, receiverRelationships.size());
  }
*/

  /**
   * Test {@link RelationshipManager#invite(Identity, Identity)}
   * 
   * @throws Exception
   */
  public void testInviteRelationship() throws Exception {
    Relationship relationship = relationshipManager.invite(john, mary);
    assertNotNull(relationship.getId());
    assertEquals(Relationship.Type.PENDING, relationship.getStatus());

    List<Relationship> senderRelationships = relationshipManager.getAll(john);
    List<Relationship> receiverRelationships = relationshipManager.getAll(mary);

    assertEquals(1, senderRelationships.size());
    assertEquals(1, receiverRelationships.size());

    tearDownRelationshipList.addAll(senderRelationships);
  }

  /**
   * Test {@link RelationshipManager#confirm(Relationship)}
   *
   * @throws Exception
   */
  public void testConfirm() throws Exception {
    Relationship relationship = relationshipManager.invite(john, demo);
    relationshipManager.confirm(relationship);
    relationship = relationshipManager.get(john, demo);
    assertNotNull(relationship.getId());
    assertEquals(Relationship.Type.CONFIRMED, relationship.getStatus());

    List<Relationship> senderRelationships = relationshipManager.getAll(john);
    List<Relationship> receiverRelationships = relationshipManager.getAll(demo);

    assertEquals(1, senderRelationships.size());
    assertEquals(1, receiverRelationships.size());

    tearDownRelationshipList.addAll(senderRelationships);
  }

  /**
   * Test {@link RelationshipManager#delete(Relationship)}
   *
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testDelete() throws Exception {
    Relationship relationship = relationshipManager.inviteToConnect(root, john);
    relationshipManager.confirm(john, root);
    relationshipManager.delete(relationship);

    relationship = relationshipManager.get(root, john);
    assertNull("relationship must be null", relationship);
  }
  
  /**
   * Test {@link RelationshipManager#remove(Relationship)}
   *
   * @throws Exception
   */
  public void testRemove() throws Exception {
    Relationship relationship = relationshipManager.invite(root, john);
    relationshipManager.confirm(relationship);
    relationshipManager.remove(relationship);

    relationship = relationshipManager.get(root, john);
    assertNull("relationship must be null", relationship);
  }

// TODO: Skip this, will be implement later
//  /**
//   *
//   * @throws Exception
//   */
//  public void testIgnore() throws Exception {
//
//    Relationship relationship = relationshipManager.invite(johnIdentity, rootIdentity);
//    relationshipManager.ignore(relationship);
//    assertNotNull(relationship.getId());
//    assertEquals(Relationship.Type.IGNORED, relationship.getStatus());
//
//    List<Relationship> senderRelationships = relationshipManager.getAll(johnIdentity);
//    List<Relationship> receiverRelationships = relationshipManager.getAll(rootIdentity);
//
//    assertEquals(1, senderRelationships.size());
//    assertEquals(1, receiverRelationships.size());
//
//    tearDownRelationshipList.addAll(senderRelationships);
//  }

  /**
   * Test {@link RelationshipManager#getPending(Identity)}
   * 
   * @throws Exception
   */
  public void testGetPending() throws Exception {
    Relationship rootDemo = relationshipManager.invite(root, demo);
    assertNotNull("rootDemo.getId() must not be null", rootDemo.getId());
    Relationship rootJohn = relationshipManager.invite(root, john);
    assertNotNull("rootJohn.getId() must not be null", rootJohn.getId());
    Relationship rootMary = relationshipManager.invite(root, mary);
    assertNotNull("rootMary.getId() must not be null", rootMary.getId());
    Relationship demoMary = relationshipManager.invite(demo, mary);
    assertNotNull("demoMary.getId() must not be null", demoMary.getId());
    Relationship demoJohn = relationshipManager.invite(demo, john);
    assertNotNull("demoJohn.getId() must not be null", demoJohn.getId());
    Relationship johnDemo = relationshipManager.invite(john, demo);
    assertNotNull("johnDemo.getId() must not be null", johnDemo.getId());

    List<Relationship> rootRelationships = relationshipManager.getPending(root);
    List<Relationship> demoRelationships = relationshipManager.getPending(demo);
    List<Relationship> johnRelationships = relationshipManager.getPending(john);

    assertEquals(3, rootRelationships.size());
    assertEquals(2, demoRelationships.size());
    assertEquals(0, johnRelationships.size());

    tearDownRelationshipList.add(rootDemo);
    tearDownRelationshipList.add(rootJohn);
    tearDownRelationshipList.add(rootMary);
    tearDownRelationshipList.add(demoMary);
    tearDownRelationshipList.add(demoJohn);
  }

  /**
   * Test relationship with caching.
   * 
   * @throws Exception
   */
  public void testSavedCached() throws Exception {
    Relationship rootDemo = relationshipManager.get(root, demo);
    assertNull("rootDemo must be null", rootDemo);
    Relationship rootDemo2 = relationshipManager.get(demo, root);
    assertNull("rootDemo must be null", rootDemo2);
    Relationship.Type rootDemoStatus = relationshipManager.getStatus(demo, root);
    assertNull("rootDemoStatus must be null",rootDemoStatus);
    rootDemo = relationshipManager.invite(root, demo);
    assertNotNull("rootDemo.getId() must not be null", rootDemo.getId());
    assertEquals(rootDemo.getStatus(), Relationship.Type.PENDING);
    tearDownRelationshipList.add(rootDemo);

    Relationship rootMary = relationshipManager.get(root, mary);
    Relationship.Type rootMaryStatus = relationshipManager.getStatus(mary, root);
    assertNull("rootMary must be null", rootMary);
    assertNull("rootMaryStatus must be null", rootMaryStatus);
    rootMary = relationshipManager.invite(root, mary);
    assertNotNull("rootMary.getId() must not be null", rootMary.getId());
    assertEquals(Relationship.Type.PENDING, rootMary.getStatus());
    tearDownRelationshipList.add(rootMary);

    Relationship rootJohn = relationshipManager.get(root, john);
    assertNull("rootJohn must be null", rootJohn);
    assertNull("rootMaryStatus must be null", rootMaryStatus);
    rootJohn = relationshipManager.invite(root, john);
    assertNotNull("rootJohn.getId() must not be null", rootJohn.getId());
    assertEquals(Relationship.Type.PENDING, rootJohn.getStatus());
    tearDownRelationshipList.add(rootJohn);

    Relationship demoMary = relationshipManager.get(demo, mary);
    Relationship.Type demoMaryStatus = relationshipManager.getStatus(mary, demo);
    assertNull("demoMary must be null", demoMary);
    assertNull("demoMaryStatus must be null", demoMaryStatus);
    demoMary = relationshipManager.invite(demo, mary);
    assertNotNull("demoMary.getId() must not be null", demoMary.getId());
    assertEquals(Relationship.Type.PENDING, demoMary.getStatus());
    tearDownRelationshipList.add(demoMary);
  }
  

  /**
   * Tests getting connections of one identity with list access.
   * 
   * @throws Exception
   */
  public void testGetConnections() throws Exception {
     Relationship johnDemoRelationship = relationshipManager.invite(john, demo);
     Relationship johnMaryRelationship = relationshipManager.invite(john, mary);
     Relationship johnRootRelationship = relationshipManager.invite(john, root);

     relationshipManager.confirm(johnDemoRelationship);
     relationshipManager.confirm(johnMaryRelationship);
     relationshipManager.confirm(johnRootRelationship);

     ListAccess<Identity> contactsList = relationshipManager.getConnections(john);
     assertEquals(3, contactsList.getSize());
     
     //Test change avatar
     InputStream inputStream = getClass().getResourceAsStream("/eXo-Social.png");
     AvatarAttachment avatarAttachment = new AvatarAttachment(null, "avatar", "png", inputStream, null, System.currentTimeMillis());
     assertNotNull(avatarAttachment);
     
     Profile profile = demo.getProfile();
     profile.setProperty(Profile.AVATAR, avatarAttachment);
     identityManager.updateProfile(profile);
     
     Identity[] identities = contactsList.load(0, 10);
     demo = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, demo.getRemoteId(), true);

     assertEquals(6, identities[0].getProfile().getProperties().size());
     assertEquals(6, identities[1].getProfile().getProperties().size());
     assertEquals(6, identities[2].getProfile().getProperties().size());
     
     for (Identity identity : contactsList.load(0, 10)) {
       assertNotNull("identity.getProfile() must not be null", identity.getProfile());
       Identity identityLoadProfile = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, identity.getRemoteId(), true);
       assertEquals("identity.getProfile().getFullName() must return: " + identityLoadProfile.getProfile().getFullName(), identityLoadProfile.getProfile().getFullName(), identity.getProfile().getFullName());
     }

     tearDownRelationshipList.add(johnDemoRelationship);
     tearDownRelationshipList.add(johnMaryRelationship);
     tearDownRelationshipList.add(johnRootRelationship);
  }
}
