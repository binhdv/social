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
package org.exoplatform.social.core.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.model.AvatarAttachment;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.exoplatform.social.core.test.AbstractCoreTest;

/**
 * Unit Tests for {@link RelationshipManager}
 *
 */
public class RelationshipManagerTest extends AbstractCoreTest {
  private RelationshipManager relationshipManager;
  private IdentityManager identityManager;

  private Identity rootIdentity,
                   johnIdentity,
                   maryIdentity,
                   demoIdentity;

  private List<Relationship> tearDownRelationshipList;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    tearDownRelationshipList = new ArrayList<Relationship>();
    relationshipManager = (RelationshipManager) getContainer().getComponentInstanceOfType(RelationshipManager.class);
    identityManager = (IdentityManager) getContainer().getComponentInstanceOfType(IdentityManager.class);
    assertNotNull("relationshipManager must not be null", relationshipManager);
    assertNotNull("identityManager must not be null", identityManager);
    rootIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "root", true);
    johnIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "john", true);
    maryIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "mary", true);
    demoIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, "demo", true);
  }

  @Override
  protected void tearDown() throws Exception {
    for (Relationship relationship : tearDownRelationshipList) {
      relationshipManager.delete(relationship);
    }

    identityManager.deleteIdentity(rootIdentity);
    identityManager.deleteIdentity(johnIdentity);
    identityManager.deleteIdentity(maryIdentity);
    identityManager.deleteIdentity(demoIdentity);

    super.tearDown();
  }

  
  /**
   * Test {@link RelationshipManager#get(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGet() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    rootToDemoRelationship = relationshipManager.get(rootIdentity, demoIdentity);
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getSender() must return: " + rootIdentity, rootIdentity, rootToDemoRelationship.getSender());
    assertEquals("rootToDemoRelationship.getReceiver() must return: " + demoIdentity, demoIdentity, rootToDemoRelationship.getReceiver());
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.PENDING, 
                 Relationship.Type.PENDING, rootToDemoRelationship.getStatus());
    
    relationshipManager.confirm(johnIdentity, rootIdentity);
    rootToJohnRelationship = relationshipManager.get(johnIdentity, rootIdentity);
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
    Relationship relationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    String relationshipId = relationship.getId();
    
    relationshipManager.confirm(johnIdentity, rootIdentity);
    relationship = relationshipManager.get(relationship.getId());
    assertNotNull("relationship must not be null", relationship);
    assertEquals("relationship.getStatus() must return: " + Relationship.Type.CONFIRMED, Relationship.Type.CONFIRMED, relationship.getStatus());
    
    relationshipManager.delete(relationship);

    relationship = relationshipManager.get(rootIdentity, johnIdentity);
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
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, rootToDemoRelationship.getStatus());
    rootToDemoRelationship.setStatus(Relationship.Type.CONFIRMED);
    relationshipManager.update(rootToDemoRelationship);
    rootToDemoRelationship = relationshipManager.get(rootIdentity, demoIdentity);
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.CONFIRMED,
                 Relationship.Type.CONFIRMED, rootToDemoRelationship.getStatus());
    
    assertEquals("maryToRootRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, maryToRootRelationship.getStatus());
    maryToRootRelationship.setStatus(Relationship.Type.IGNORED);
    relationshipManager.update(maryToRootRelationship);
    maryToRootRelationship = relationshipManager.get(maryIdentity, rootIdentity);
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
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    rootToDemoRelationship = relationshipManager.get(rootIdentity, demoIdentity);
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, rootToDemoRelationship.getStatus());
    
    maryToRootRelationship = relationshipManager.get(maryIdentity, rootIdentity);
    assertNotNull("maryToRootRelationship must not be null", maryToRootRelationship);
    assertEquals("maryToRootRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, maryToRootRelationship.getStatus());
    
    rootToJohnRelationship = relationshipManager.get(johnIdentity, rootIdentity);
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
    Relationship relationship1 = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship relationship2 = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
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
    Relationship relationship1 = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    assertEquals("RelationShip status must be PENDING",Relationship.Type.PENDING, relationship1.getStatus());
    relationshipManager.confirm(rootIdentity, demoIdentity);
    relationship1 = relationshipManager.get(rootIdentity, demoIdentity);
    assertEquals("RelationShip status must be CONFIRMED",Relationship.Type.CONFIRMED, relationship1.getStatus());
    Relationship relationship2 = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    assertEquals("RelationShip status must be CONFIRMED",Relationship.Type.CONFIRMED, relationship2.getStatus());
    
    assertEquals("relationShip1 and relationShip2 must be the same",relationship1.getId(), relationship2.getId());
    
    tearDownRelationshipList.add(relationship1);
  }
  
  /**
   * Test {@link RelationshipManager#deny(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testDeny() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    relationshipManager.confirm(johnIdentity, rootIdentity);
    relationshipManager.deny(johnIdentity, rootIdentity);
    assertNotNull(relationshipManager.get(rootToJohnRelationship.getId()));
    
    rootToJohnRelationship.setStatus(Relationship.Type.PENDING);
    relationshipManager.update(rootToJohnRelationship);
    
    relationshipManager.deny(demoIdentity, rootIdentity);
    rootToDemoRelationship = relationshipManager.get(rootIdentity, demoIdentity);
    assertNull("rootToDemoRelationship must be null", rootToDemoRelationship);
    
    relationshipManager.deny(maryIdentity, rootIdentity);
    maryToRootRelationship = relationshipManager.get(maryIdentity, rootIdentity);
    assertNull("maryToRootRelationship must be null", maryToRootRelationship);
    
    relationshipManager.deny(rootIdentity, johnIdentity);
    rootToJohnRelationship = relationshipManager.get(rootIdentity, johnIdentity);
    assertNull("rootToJohnRelationship must be null", rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#ignore(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testIgnore() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    relationshipManager.ignore(demoIdentity, rootIdentity);
    rootToDemoRelationship = relationshipManager.get(rootIdentity, demoIdentity);
    assertNull("rootToDemoRelationship must be null", rootToDemoRelationship);
    
    relationshipManager.ignore(maryIdentity, rootIdentity);
    maryToRootRelationship = relationshipManager.get(maryIdentity, rootIdentity);
    assertNull("maryToRootRelationship must be null", maryToRootRelationship);
    
    relationshipManager.ignore(rootIdentity, johnIdentity);
    rootToJohnRelationship = relationshipManager.get(rootIdentity, johnIdentity);
    assertNull("rootToJohnRelationship must be null", rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getIncomingWithListAccess(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetIncomingWithListAccess() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToDemoRelationship = relationshipManager.inviteToConnect(maryIdentity, demoIdentity);
    Relationship johnToDemoRelationship = relationshipManager.inviteToConnect(johnIdentity, demoIdentity);
    
    ListAccess<Identity> demoIncoming = relationshipManager.getIncomingWithListAccess(demoIdentity);
    assertNotNull("demoIncoming must not be null", demoIncoming);
    assertEquals("demoIncoming.getSize() must return: 3", 3, demoIncoming.getSize());
    
    //Test change avatar
    InputStream inputStream = getClass().getResourceAsStream("/eXo-Social.png");
    AvatarAttachment avatarAttachment = new AvatarAttachment(null, "avatar", "png", inputStream, null, System.currentTimeMillis());
    assertNotNull(avatarAttachment);
    
    Profile profile = maryIdentity.getProfile();
    profile.setProperty(Profile.AVATAR, avatarAttachment);
    identityManager.updateProfile(profile);
    
    Identity[] identities = demoIncoming.load(0, 10);
    demoIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, demoIdentity.getRemoteId(), true);

    assertEquals(6, identities[0].getProfile().getProperties().size());
    assertEquals(6, identities[1].getProfile().getProperties().size());
    assertEquals(6, identities[2].getProfile().getProperties().size());
    
    for (Identity identity : demoIncoming.load(0, 10)) {
      assertNotNull("identity.getProfile() must not be null", identity.getProfile());
      Identity identityLoadProfile = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, identity.getRemoteId(), true);
      assertEquals("identity.getProfile().getFullName() must return: " + identityLoadProfile.getProfile().getFullName(),
                   identityLoadProfile.getProfile().getFullName(), identity.getProfile().getFullName());
    }
    
    ListAccess<Identity> rootIncoming = relationshipManager.getIncomingWithListAccess(rootIdentity);
    assertNotNull("rootIncoming must not be null", rootIncoming);
    assertEquals("rootIncoming.getSize() must return: 0", 0, rootIncoming.getSize());
    
    ListAccess<Identity> maryIncoming = relationshipManager.getIncomingWithListAccess(maryIdentity);
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
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship rootToMaryRelationship = relationshipManager.inviteToConnect(rootIdentity, maryIdentity);
    Relationship maryToDemoRelationship = relationshipManager.inviteToConnect(maryIdentity, demoIdentity);
    Relationship demoToJohnRelationship = relationshipManager.inviteToConnect(demoIdentity, johnIdentity);
    
    ListAccess<Identity> rootOutgoing = relationshipManager.getOutgoing(rootIdentity);
    assertNotNull("rootOutgoing must not be null", rootOutgoing);
    assertEquals("rootOutgoing.getSize() must return: 2", 2, rootOutgoing.getSize());
    
    //Test change avatar
    InputStream inputStream = getClass().getResourceAsStream("/eXo-Social.png");
    AvatarAttachment avatarAttachment = new AvatarAttachment(null, "avatar", "png", inputStream, null, System.currentTimeMillis());
    assertNotNull(avatarAttachment);
    
    Profile profile = demoIdentity.getProfile();
    profile.setProperty(Profile.AVATAR, avatarAttachment);
    identityManager.updateProfile(profile);
    
    rootOutgoing = relationshipManager.getOutgoing(rootIdentity);
    Identity[] identities = rootOutgoing.load(0, 10);
    demoIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, demoIdentity.getRemoteId(), true);

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
    
    ListAccess<Identity> maryOutgoing = relationshipManager.getOutgoing(maryIdentity);
    assertNotNull("maryOutgoing must not be null", maryOutgoing);
    assertEquals("maryOutgoing.getSize() must return: 1", 1, maryOutgoing.getSize());
    
    ListAccess<Identity> demoOutgoing = relationshipManager.getOutgoing(demoIdentity);
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
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    rootToDemoRelationship = relationshipManager.get(rootIdentity, demoIdentity);
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, rootToDemoRelationship.getStatus());
    assertEquals("relationshipManager.getStatus(rootIdentity, demoIdentity) must return: " + 
                 Relationship.Type.PENDING, Relationship.Type.PENDING, 
                 relationshipManager.getStatus(rootIdentity, demoIdentity));
    
    maryToRootRelationship = relationshipManager.get(maryIdentity, rootIdentity);
    assertNotNull("maryToRootRelationship must not be null", maryToRootRelationship);
    assertEquals("maryToRootRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, maryToRootRelationship.getStatus());
    assertEquals("relationshipManager.getStatus(maryIdentity, rootIdentity) must return: " + 
                 Relationship.Type.PENDING, Relationship.Type.PENDING,
                 relationshipManager.getStatus(maryIdentity, rootIdentity));
    
    rootToJohnRelationship = relationshipManager.get(johnIdentity, rootIdentity);
    assertNotNull("rootToJohnRelationship must not be null", rootToJohnRelationship);
    assertEquals("rootToJohnRelationship.getStatus() must return: " + Relationship.Type.PENDING,
                 Relationship.Type.PENDING, rootToJohnRelationship.getStatus());
    assertEquals("relationshipManager.getStatus(rootIdentity, johnIdentity) must return: " +
                 Relationship.Type.PENDING, Relationship.Type.PENDING,
                 relationshipManager.getStatus(rootIdentity, johnIdentity));
    
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
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    ListAccess<Identity> rootRelationships = relationshipManager.getAllWithListAccess(rootIdentity);
    assertNotNull("rootRelationships must not be null", rootRelationships);
    assertEquals("rootRelationships.getSize() must return: 3", 3, rootRelationships.getSize());
    
    ListAccess<Identity> demoRelationships = relationshipManager.getAllWithListAccess(demoIdentity);
    assertNotNull("demoRelationships must not be null", demoRelationships);
    assertEquals("demoRelationships.getSize() must return: 1", 1, demoRelationships.getSize());
    
    ListAccess<Identity> johnRelationships = relationshipManager.getAllWithListAccess(johnIdentity);
    assertNotNull("johnRelationships must not be null", johnRelationships);
    assertEquals("johnRelationships.getSize() must return: 1", 1, johnRelationships.getSize());
    
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
  public void testGetRelationshipById() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    
    rootToDemoRelationship = relationshipManager.get(rootToDemoRelationship.getId());
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getSender() must return: " + rootIdentity, rootIdentity, rootToDemoRelationship.getSender());
    assertEquals("rootToDemoRelationship.getReceiver() must return: " + demoIdentity, demoIdentity, rootToDemoRelationship.getReceiver());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#deny(org.exoplatform.social.core.identity.model.Identity, org.exoplatform.social.core.identity.model.Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testDenyWithRelationship() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    relationshipManager.deny(rootIdentity, demoIdentity);
    rootToDemoRelationship = relationshipManager.get(rootIdentity, demoIdentity);
    assertNull("rootToDemoRelationship must be null", rootToDemoRelationship);
    
    relationshipManager.deny(maryIdentity, rootIdentity);
    maryToRootRelationship = relationshipManager.get(maryIdentity, rootIdentity);
    assertNull("maryToRootRelationship must be null", maryToRootRelationship);
    
    relationshipManager.deny(rootIdentity, johnIdentity);
    rootToJohnRelationship = relationshipManager.get(rootIdentity, johnIdentity);
    assertNull("rootToJohnRelationship must be null", rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#ignore(org.exoplatform.social.core.identity.model.Identity, org.exoplatform.social.core.identity.model.Identity)} }
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testIgnoreWithRelationship() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    relationshipManager.ignore(rootIdentity, demoIdentity);
    rootToDemoRelationship = relationshipManager.get(rootIdentity, demoIdentity);
    assertNull("rootToDemoRelationship must be null", rootToDemoRelationship);
    
    relationshipManager.ignore(maryIdentity, rootIdentity);
    maryToRootRelationship = relationshipManager.get(maryIdentity, rootIdentity);
    assertNull("maryToRootRelationship must be null", maryToRootRelationship);
    
    relationshipManager.ignore(rootIdentity, johnIdentity);
    rootToJohnRelationship = relationshipManager.get(rootIdentity, johnIdentity);
    assertNull("rootToJohnRelationship must be null", rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getConnections(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetContactsWithListIdentities() throws Exception {
    List<Identity> identities = new ArrayList<Identity> ();
    identities.add(rootIdentity);
    identities.add(demoIdentity);
    identities.add(johnIdentity);
    identities.add(maryIdentity);
    
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    relationshipManager.confirm(rootIdentity, demoIdentity);
    relationshipManager.confirm(maryIdentity, rootIdentity);
    relationshipManager.confirm(rootIdentity, johnIdentity);
    
    ListAccess<Identity> rootContacts = relationshipManager.getConnections(rootIdentity);
    assertNotNull("rootContacts must not be null", rootContacts);
    assertEquals("rootContacts.size() must return: 3", 3, rootContacts.getSize());
    
    ListAccess<Identity> demoContacts = relationshipManager.getConnections(demoIdentity);
    assertNotNull("demoContacts must not be null", demoContacts);
    assertEquals("demoContacts.size() must return: 1", 1, demoContacts.getSize());
    
    ListAccess<Identity> maryContacts = relationshipManager.getConnections(maryIdentity);
    assertNotNull("maryContacts must not be null", maryContacts);
    assertEquals("maryContacts.size() must return: 1", 1, maryContacts.getSize());
    
    ListAccess<Identity> johnContacts = relationshipManager.getConnections(johnIdentity);
    assertNotNull("johnContacts must not be null", johnContacts);
    assertEquals("johnContacts.size() must return: 1", 1, johnContacts.getSize());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getConnections(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetContacts() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    relationshipManager.confirm(rootIdentity, demoIdentity);
    relationshipManager.confirm(maryIdentity, rootIdentity);
    relationshipManager.confirm(rootIdentity, johnIdentity);
    
    ListAccess<Identity> rootContacts = relationshipManager.getConnections(rootIdentity);
    assertNotNull("rootContacts must not be null", rootContacts);
    assertEquals("rootContacts.size() must return: 3", 3, rootContacts.getSize());
    
    ListAccess<Identity> demoContacts = relationshipManager.getConnections(demoIdentity);
    assertNotNull("demoContacts must not be null", demoContacts);
    assertEquals("demoContacts.size() must return: 1", 1, demoContacts.getSize());
    
    ListAccess<Identity> maryContacts = relationshipManager.getConnections(maryIdentity);
    assertNotNull("maryContacts must not be null", maryContacts);
    assertEquals("maryContacts.size() must return: 1", 1, maryContacts.getSize());
    
    ListAccess<Identity> johnContacts = relationshipManager.getConnections(johnIdentity);
    assertNotNull("johnContacts must not be null", johnContacts);
    assertEquals("johnContacts.size() must return: 1", 1, johnContacts.getSize());
    
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
  public void testGetAllRelationships() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(maryIdentity, rootIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    
    ListAccess<Identity> rootRelationships = relationshipManager.getAllWithListAccess(rootIdentity);
    assertNotNull("rootRelationships must not be null", rootRelationships);
    assertEquals("rootRelationships.size() must return: 3", 3, rootRelationships.getSize());
    
    ListAccess<Identity> maryRelationships = relationshipManager.getAllWithListAccess(maryIdentity);
    assertNotNull("maryRelationships must not be null", maryRelationships);
    assertEquals("maryRelationships.size() must return: 1", 1, maryRelationships.getSize());
    
    ListAccess<Identity> demoRelationships = relationshipManager.getAllWithListAccess(demoIdentity);
    assertNotNull("demoRelationships must not be null", demoRelationships);
    assertEquals("demoRelationships.size() must return: 1", 1, demoRelationships.getSize());
    
    ListAccess<Identity> johnRelationships = relationshipManager.getAllWithListAccess(johnIdentity);
    assertNotNull("johnRelationships must not be null", johnRelationships);
    assertEquals("johnRelationships.size() must return: 1", 1, johnRelationships.getSize());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getAllWithListAccess(org.exoplatform.social.core.identity.model.Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetRelationshipsByIdentityId() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(demoIdentity, rootIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(rootIdentity, maryIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(johnIdentity, rootIdentity);
    
    ListAccess<Identity> rootRelationships = relationshipManager.getAllWithListAccess(rootIdentity);
    assertNotNull("rootRelationships must not be null", rootRelationships);
    assertEquals("rootRelationships.size() must return: 3", 3, rootRelationships.getSize());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
    tearDownRelationshipList.add(maryToRootRelationship);
    tearDownRelationshipList.add(rootToJohnRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getStatus(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetRelationship() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    
    rootToDemoRelationship = relationshipManager.get(rootIdentity, demoIdentity);
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.PENDING, Relationship.Type.PENDING, rootToDemoRelationship.getStatus());
    
    relationshipManager.confirm(rootIdentity, demoIdentity);
    
    rootToDemoRelationship = relationshipManager.get(rootIdentity, demoIdentity);
    assertNotNull("rootToDemoRelationship must not be null", rootToDemoRelationship);
    assertEquals("rootToDemoRelationship.getStatus() must return: " + Relationship.Type.CONFIRMED, Relationship.Type.CONFIRMED, rootToDemoRelationship.getStatus());
    
    tearDownRelationshipList.add(rootToDemoRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getIncoming(Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testFindRelationships() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(demoIdentity, rootIdentity);
    Relationship maryToRootRelationship = relationshipManager.inviteToConnect(rootIdentity, maryIdentity);
    Relationship rootToJohnRelationship = relationshipManager.inviteToConnect(johnIdentity, rootIdentity);
    
    ListAccess<Identity> rootRelationships = relationshipManager.getIncomingWithListAccess(rootIdentity);
    assertNotNull("rootRelationships must not be null", rootRelationships);
    assertEquals("rootRelationships.getSize() must return: 2", 2, rootRelationships.getSize());
    
    relationshipManager.confirm(rootIdentity, demoIdentity);
    
    rootRelationships = relationshipManager.getConnections(rootIdentity);
    assertNotNull("rootRelationships must not be null", rootRelationships);
    assertEquals("rootRelationships.getSize() must return: 1", 1, rootRelationships.getSize());
    
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
  public void testGetRelationshipStatus() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    assertEquals("relationshipManager.getStatus(demoIdentity, rootIdentity) must return: "
                 + Relationship.Type.PENDING, Relationship.Type.PENDING
                 , relationshipManager.getStatus(demoIdentity, rootIdentity));
    
    relationshipManager.confirm(rootIdentity, demoIdentity);
    rootToDemoRelationship = relationshipManager.get(rootIdentity, demoIdentity);
    assertEquals("relationshipManager.getStatus(demoIdentity, rootIdentity) must return: "
                 + Relationship.Type.PENDING, Relationship.Type.CONFIRMED
                 , relationshipManager.getStatus(demoIdentity, rootIdentity));
    
    tearDownRelationshipList.add(rootToDemoRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#getStatus(Identity, Identity)}
   * 
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testGetConnectionStatus() throws Exception {
    Relationship rootToDemoRelationship = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    
    assertEquals("relationshipManager.getStatus(rootIdentity, demoIdentity) must return: " +
                 Relationship.Type.PENDING, Relationship.Type.PENDING, 
                 relationshipManager.getStatus(rootIdentity, demoIdentity));
    
    relationshipManager.confirm(rootIdentity, demoIdentity);
    assertEquals("relationshipManager.getStatus(rootIdentity, demoIdentity) must return: " +
                 Relationship.Type.CONFIRMED, Relationship.Type.CONFIRMED, 
                 relationshipManager.getStatus(rootIdentity, demoIdentity));
    
    tearDownRelationshipList.add(rootToDemoRelationship);
  }
  
  /**
   * Test {@link RelationshipManager#inviteToConnect(Identity, Identity) and RelationshipManager#get(String)}
   *
   * @throws Exception
   */
  public void testIntiveAndGetByRelationshipId() throws Exception {
    Relationship invitedRelationship = relationshipManager.inviteToConnect(johnIdentity, maryIdentity);
    
    Relationship foundRelationship = relationshipManager.get(invitedRelationship.getId());
    assertNotNull("foundRelationship must not be null", foundRelationship);
    assertNotNull("foundRelationship.getId() must not be null", foundRelationship.getId());
    assertEquals(foundRelationship.getId(), invitedRelationship.getId());

    tearDownRelationshipList.add(invitedRelationship);
  }

  /**
   * Test {@link RelationshipManager#inviteToConnect(org.exoplatform.social.core.identity.model.Identity, org.exoplatform.social.core.identity.model.Identity)}
   *
   * @throws Exception
   */
  public void testSave() throws Exception {
    relationshipManager.inviteToConnect(johnIdentity, demoIdentity);
    Relationship testRelationship =  relationshipManager.get(johnIdentity, demoIdentity);

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
   * Test {@link RelationshipManager#inviteToConnect(Identity, Identity)}
   * 
   * @throws Exception
   */
  public void testInviteRelationship() throws Exception {
    Relationship relationship = relationshipManager.inviteToConnect(johnIdentity, maryIdentity);
    assertNotNull(relationship.getId());
    assertEquals(Relationship.Type.PENDING, relationship.getStatus());

    ListAccess<Identity> senderRelationships = relationshipManager.getOutgoing(johnIdentity);
    ListAccess<Identity> receiverRelationships = relationshipManager.getIncomingWithListAccess(maryIdentity);

    assertEquals(1, senderRelationships.getSize());
    assertEquals(1, receiverRelationships.getSize());

    tearDownRelationshipList.add(relationship);
  }

  /**
   * Test {@link RelationshipManager#confirm(org.exoplatform.social.core.identity.model.Identity, org.exoplatform.social.core.identity.model.Identity)}
   *
   * @throws Exception
   */
  public void testConfirm() throws Exception {
    Relationship relationship = relationshipManager.inviteToConnect(johnIdentity, demoIdentity);
    relationshipManager.confirm(johnIdentity, demoIdentity);
    relationship = relationshipManager.get(johnIdentity, demoIdentity);
    assertNotNull(relationship.getId());
    assertEquals(Relationship.Type.CONFIRMED, relationship.getStatus());

    ListAccess<Identity> senderRelationships = relationshipManager.getConnections(johnIdentity);
    ListAccess<Identity> receiverRelationships = relationshipManager.getConnections(demoIdentity);

    assertEquals(1, senderRelationships.getSize());
    assertEquals(1, receiverRelationships.getSize());

    tearDownRelationshipList.add(relationship);
  }

  /**
   * Test {@link RelationshipManager#delete(Relationship)}
   *
   * @throws Exception
   * @since 1.2.0-Beta3
   */
  public void testDelete() throws Exception {
    Relationship relationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    relationshipManager.confirm(johnIdentity, rootIdentity);
    relationshipManager.delete(relationship);

    relationship = relationshipManager.get(rootIdentity, johnIdentity);
    assertNull("relationship must be null", relationship);
  }
  
  /**
   * Test {@link RelationshipManager#delete(Relationship)}
   *
   * @throws Exception
   */
  public void testRemove() throws Exception {
    Relationship relationship = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    relationshipManager.confirm(rootIdentity, johnIdentity);
    relationshipManager.delete(relationship);

    relationship = relationshipManager.get(rootIdentity, johnIdentity);
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
   * Test relationship with caching.
   * 
   * @throws Exception
   */
  public void testSavedCached() throws Exception {
    Relationship rootDemo = relationshipManager.get(rootIdentity, demoIdentity);
    assertNull("rootDemo must be null", rootDemo);
    Relationship rootDemo2 = relationshipManager.get(demoIdentity, rootIdentity);
    assertNull("rootDemo must be null", rootDemo2);
    Relationship.Type rootDemoStatus = relationshipManager.getStatus(demoIdentity, rootIdentity);
    assertNull("rootDemoStatus must be null",rootDemoStatus);
    rootDemo = relationshipManager.inviteToConnect(rootIdentity, demoIdentity);
    assertNotNull("rootDemo.getId() must not be null", rootDemo.getId());
    assertEquals(rootDemo.getStatus(), Relationship.Type.PENDING);
    tearDownRelationshipList.add(rootDemo);

    Relationship rootMary = relationshipManager.get(rootIdentity, maryIdentity);
    Relationship.Type rootMaryStatus = relationshipManager.getStatus(maryIdentity, rootIdentity);
    assertNull("rootMary must be null", rootMary);
    assertNull("rootMaryStatus must be null", rootMaryStatus);
    rootMary = relationshipManager.inviteToConnect(rootIdentity, maryIdentity);
    assertNotNull("rootMary.getId() must not be null", rootMary.getId());
    assertEquals(Relationship.Type.PENDING, rootMary.getStatus());
    tearDownRelationshipList.add(rootMary);

    Relationship rootJohn = relationshipManager.get(rootIdentity, johnIdentity);
    assertNull("rootJohn must be null", rootJohn);
    assertNull("rootMaryStatus must be null", rootMaryStatus);
    rootJohn = relationshipManager.inviteToConnect(rootIdentity, johnIdentity);
    assertNotNull("rootJohn.getId() must not be null", rootJohn.getId());
    assertEquals(Relationship.Type.PENDING, rootJohn.getStatus());
    tearDownRelationshipList.add(rootJohn);

    Relationship demoMary = relationshipManager.get(demoIdentity, maryIdentity);
    Relationship.Type demoMaryStatus = relationshipManager.getStatus(maryIdentity, demoIdentity);
    assertNull("demoMary must be null", demoMary);
    assertNull("demoMaryStatus must be null", demoMaryStatus);
    demoMary = relationshipManager.inviteToConnect(demoIdentity, maryIdentity);
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
     Relationship johnDemoRelationship = relationshipManager.inviteToConnect(johnIdentity, demoIdentity);
     Relationship johnMaryRelationship = relationshipManager.inviteToConnect(johnIdentity, maryIdentity);
     Relationship johnRootRelationship = relationshipManager.inviteToConnect(johnIdentity, rootIdentity);

     relationshipManager.confirm(johnIdentity, demoIdentity);
     relationshipManager.confirm(johnIdentity, maryIdentity);
     relationshipManager.confirm(johnIdentity, rootIdentity);

     ListAccess<Identity> contactsList = relationshipManager.getConnections(johnIdentity);
     assertEquals(3, contactsList.getSize());
     
     //Test change avatar
     InputStream inputStream = getClass().getResourceAsStream("/eXo-Social.png");
     AvatarAttachment avatarAttachment = new AvatarAttachment(null, "avatar", "png", inputStream, null, System.currentTimeMillis());
     assertNotNull(avatarAttachment);
     
     Profile profile = demoIdentity.getProfile();
     profile.setProperty(Profile.AVATAR, avatarAttachment);
     identityManager.updateProfile(profile);
     
     Identity[] identities = contactsList.load(0, 10);
     demoIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, demoIdentity.getRemoteId(), true);

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
