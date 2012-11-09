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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.common.lifecycle.LifeCycleCompletionService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.space.SpaceAccessTest;
import org.exoplatform.social.core.space.SpaceAccessType;
import org.exoplatform.social.core.space.SpaceUtils;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.core.storage.api.IdentityStorage;
import org.exoplatform.social.core.test.BaseSocialTestCase;

/**
 * Created by The eXo Platform SAS
 * Author : thanh_vucong
 *          thanh_vucong@exoplatform.com
 * Nov 9, 2012  
 */
public class SpaceAccessSampleTest extends BaseSocialTestCase {

  private SpaceService spaceService;
  private IdentityStorage identityStorage;
  private LifeCycleCompletionService lifeCycleCompletionService;
  private List<Space> tearDownSpaceList;
  private List<Identity> tearDownUserList;

  private final Log       LOG = ExoLogger.getLogger(SpaceAccessTest.class);


  

  @Override
  public void setUp() throws Exception {
    super.setUp();
    spaceService = (SpaceService) getContainer().getComponentInstanceOfType(SpaceService.class);
    identityStorage = (IdentityStorage) getContainer().getComponentInstanceOfType(IdentityStorage.class);
    lifeCycleCompletionService = (LifeCycleCompletionService) getContainer().getComponentInstanceOfType(LifeCycleCompletionService.class);
    tearDownSpaceList = new ArrayList<Space>();
    
  }

  @Override
  public void tearDown() throws Exception {
    end();
    begin();

    for (Space space : tearDownSpaceList) {
      Identity spaceIdentity = identityStorage.findIdentity(SpaceIdentityProvider.NAME, space.getPrettyName());
      if (spaceIdentity != null) {
        identityStorage.deleteIdentity(spaceIdentity);
      }
      spaceService.deleteSpace(space);
    }
    super.tearDown();
  }
  
  public void testSpaceNotFound() throws Exception {
    Space space = spaceService.getSpaceByPrettyName("space_not_found");
    boolean gotStatus = SpaceAccessType.SPACE_NOT_FOUND.doCheck(root.getRemoteId(), space);
    assertTrue(gotStatus);
  }
  
  public void testInvitedSpace() throws Exception {
    Space space = createSpaceData("invited space", "validation", new String[] {root.getRemoteId(), john.getRemoteId()}, new String[] {root.getRemoteId(), john.getRemoteId(), mary.getRemoteId()});
    space.setInvitedUsers(new String[]{demo.getRemoteId(), jame.getRemoteId()});
    spaceService.saveSpace(space, true);
    
    space = spaceService.getSpaceByPrettyName(space.getPrettyName());
    assertNotNull(space);
    boolean gotStatus = SpaceAccessType.INVITED_SPACE.doCheck(demo.getRemoteId(), space);
    assertTrue(gotStatus);
    
    gotStatus = SpaceAccessType.INVITED_SPACE.doCheck(jame.getRemoteId(), space);
    assertTrue(gotStatus);
    tearDownSpaceList.add(space);
  }
  /**
   * RequesTED to join space.
   * @throws Exception
   */
  public void testRequestedToJoinSpace() throws Exception {
    Space space = createSpaceData("requested space", "validation", new String[] {root.getRemoteId(), john.getRemoteId()}, new String[] {root.getRemoteId(), john.getRemoteId(), mary.getRemoteId()});
    space.setPendingUsers(new String[]{demo.getRemoteId(), jame.getRemoteId()});
    spaceService.saveSpace(space, true);
    
    space = spaceService.getSpaceByPrettyName(space.getPrettyName());
    assertNotNull(space);
    boolean gotStatus = SpaceAccessType.REQUESTED_JOIN_SPACE.doCheck(demo.getRemoteId(), space);
    assertTrue(gotStatus);
    
    gotStatus = SpaceAccessType.REQUESTED_JOIN_SPACE.doCheck(jame.getRemoteId(), space);
    assertTrue(gotStatus);
    tearDownSpaceList.add(space);
  }
  
  /**
   * Request to join space
   * @throws Exception
   */
  public void testRequestToJoinSpace() throws Exception {
    Space space = createSpaceData("request space", "validation", new String[] {root.getRemoteId(), john.getRemoteId()}, new String[] {root.getRemoteId(), john.getRemoteId(), mary.getRemoteId()});
    spaceService.saveSpace(space, true);
    
    space = spaceService.getSpaceByPrettyName(space.getPrettyName());
    assertNotNull(space);
    boolean gotStatus = SpaceAccessType.REQUEST_JOIN_SPACE.doCheck(demo.getRemoteId(), space);
    assertTrue(gotStatus);
    
    gotStatus = SpaceAccessType.REQUEST_JOIN_SPACE.doCheck(jame.getRemoteId(), space);
    assertTrue(gotStatus);
    tearDownSpaceList.add(space);
  }
  
  public void testJoinSpace() throws Exception {
    Space space = createSpaceData("request space", "open", new String[] {root.getRemoteId(), john.getRemoteId()}, new String[] {root.getRemoteId(), john.getRemoteId(), mary.getRemoteId()});
    spaceService.saveSpace(space, true);
    space = spaceService.getSpaceByPrettyName(space.getPrettyName());
    assertNotNull(space);
    boolean gotStatus = SpaceAccessType.JOIN_SPACE.doCheck(demo.getRemoteId(), space);
    assertTrue(gotStatus);
    
    gotStatus = SpaceAccessType.JOIN_SPACE.doCheck(jame.getRemoteId(), space);
    assertTrue(gotStatus);
    tearDownSpaceList.add(space);
  }
  
  public void testClosedSpace() throws Exception {
    Space space = createSpaceData("request space", "close", new String[] {root.getRemoteId(), john.getRemoteId()}, new String[] {root.getRemoteId(), john.getRemoteId(), mary.getRemoteId()});
    spaceService.saveSpace(space, true);
    space = spaceService.getSpaceByPrettyName(space.getPrettyName());
    assertNotNull(space);
    boolean gotStatus = SpaceAccessType.CLOSED_SPACE.doCheck(demo.getRemoteId(), space);
    assertTrue(gotStatus);
    
    gotStatus = SpaceAccessType.CLOSED_SPACE.doCheck(jame.getRemoteId(), space);
    assertTrue(gotStatus);
    tearDownSpaceList.add(space);
  }
  
  private Space createSpaceData(String spaceName, String registration, String[] managers, String[] members) throws Exception {
    Space space2 = new Space();
    space2.setApp("Contact,Forum");
    space2.setDisplayName(spaceName);
    space2.setPrettyName(space2.getDisplayName());
    String shortName = SpaceUtils.cleanString(spaceName);
    space2.setGroupId("/spaces/" + shortName );
    space2.setUrl(shortName);
    space2.setMembers(members);
    space2.setManagers(managers);
    space2.setRegistration(registration);
    space2.setDescription("This is my second space for testing");
    space2.setType("classic");
    space2.setVisibility("public");
    space2.setPriority("2");
    return space2;
  }
}