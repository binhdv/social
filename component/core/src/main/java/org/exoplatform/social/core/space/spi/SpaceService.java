/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.social.core.space.spi;

import java.util.List;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.social.core.space.SpaceApplicationConfigPlugin;
import org.exoplatform.social.core.space.SpaceException;
import org.exoplatform.social.core.space.SpaceFilter;
import org.exoplatform.social.core.space.SpaceListAccess;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.model.Space;

/**
 * SpaceService provides methods for working with Space.
 *
 * @author <a href="mailto:tungcnw@gmail.com">dang.tung</a>
 * @since Aug 29, 2008
 */
public interface SpaceService {

  /**
   * Will be removed by 1.3.x
   */
  @Deprecated
  final String SPACES_APP_ID = "exosocial:spaces";


  /**
   * Gets a space by its space display name.
   *
   * @param spaceDisplayName the space display name
   * @return the space with space display name that matches the string input.
   * @since 1.2.0-GA
   */
  Space getSpaceByDisplayName(String spaceDisplayName);

  /**
   * Gets a space by its pretty name.
   *
   * @param spacePrettyName the space pretty name
   * @return the space with space pretty name that matches the string input.
   * @since 1.2.0-GA
   */
  Space getSpaceByPrettyName(String spacePrettyName);

  /**
   * Gets a space by its group id.
   *
   * @param groupId the group id
   * @return the space that has group id that matches the string input.
   * @since 1.2.0-GA
   */
  Space getSpaceByGroupId(String groupId);

  /**
   * Gets a space by its id.
   *
   * @param spaceId id of that space
   * @return the space with id specified
   */
  Space getSpaceById(String spaceId);

  /**
   * Gets a space by its url.
   *
   * @param spaceUrl url of a space
   * @return the space with the space url that matched the string input
   */
  Space getSpaceByUrl(String spaceUrl);

  /**
   * Gets a space list access which contains all the spaces.
   *
   * @return the space list access for all spaces
   * @since  1.3.0-GA
   */
  //ListAccess<Space> getAllSpaces();


  /**
   * Gets a space list access which contains all the spaces.
   *
   * @return the space list access for all spaces
   * @since  1.2.0-GA
   */
  ListAccess<Space> getAllSpacesWithListAccess();


  /**
   * Gets a space list access which contains all the spaces matching the space filter.
   *
   * @param spaceFilter the space filter
   * @return the space list access for all spaces matching the space filter
   * @since  1.2.0-GA
   */
  ListAccess<Space> getAllSpacesByFilter(SpaceFilter spaceFilter);

  /**
   * Gets a spaces list access that contains all the spaces in which a user has the "member" role.
   *
   * @param userId the remote user id
   * @return the space list access
   * @since  1.2.0-GA
   */
  ListAccess<Space> getMemberSpaces(String userId);

  /**
   * Gets a space list access that contains all the spaces that a user has "member" role and matches the provided space
   * filter.
   *
   * @param userId      the remote user id
   * @param spaceFilter the space filter
   * @return the space list access
   * @since  1.2.0-GA
   */
  ListAccess<Space> getMemberSpacesByFilter(String userId, SpaceFilter spaceFilter);

  /**
   * Gets a spaces list access which contains all the spaces that a user has the access permission.
   *
   * @param userId the remote user id.
   * @return the space list access
   * @since  1.3.0-GA
   */
  //ListAccess<Space> getAccessibleSpaces(String userId);

  /**
   * Gets a spaces list access which contains all the spaces that a user has the access permission.
   *
   * @param userId the remote user id.
   * @return the space list access
   * @since  1.2.0-GA
   */
  ListAccess<Space> getAccessibleSpacesWithListAccess(String userId);

  /**
   * Gets a space list access which contains all the spaces that a user has the access permission and matches the
   * provided space filter.
   *
   * @param userId      the remote user id
   * @param spaceFilter the provided space filter
   * @return the space list access
   * @since  1.2.0-GA
   */
  ListAccess<Space> getAccessibleSpacesByFilter(String userId, SpaceFilter spaceFilter);

  /**
   * Gets a spaces list access which contains all the spaces that a user has the setting permission.
   *
   * @param userId the remote user id
   * @return the space list access
   * @since  1.2.0-GA
   */
  ListAccess<Space> getSettingableSpaces(String userId);

  /**
   * Gets a space list access which contains all the spaces that a user has the setting permission and matches the
   * provided space filter.
   *
   * @param userId      the remote user id
   * @param spaceFilter the provided space filter
   * @return the space list access
   * @since  1.2.0-GA
   */
  ListAccess<Space> getSettingabledSpacesByFilter(String userId, SpaceFilter spaceFilter);

  /**
   * Gets a space list access which contains all the spaces that a user is invited to join.
   *
   * @param userId the remote user id
   * @return the space list access
   * @since  1.3.0-GA
   */
  //ListAccess<Space> getInvitedSpaces(String userId);

  /**
   * Gets a space list access which contains all the spaces that a user is invited to join.
   *
   * @param userId the remote user id
   * @return the space list access
   * @since  1.2.0-GA
   */
  ListAccess<Space> getInvitedSpacesWithListAccess(String userId);

  /**
   * Gets a space list access which contains all the spaces that a user is invited to join and matches the provided
   * space filter.
   *
   * @param userId      the remote user id
   * @param spaceFilter the provided space filter
   * @return the space list access
   * @since  1.2.0-GA
   */
  ListAccess<Space> getInvitedSpacesByFilter(String userId, SpaceFilter spaceFilter);

  /**
   * Gets a space list access which contains all the spaces that a user can request to join.
   *
   * @param userId the remote user id
   * @return the space list access
   * @since  1.3.0-GA
   */
  //ListAccess<Space> getPublicSpaces(String userId);

  /**
   * Gets a space list access which contains all the spaces that a user can request to join.
   *
   * @param userId the remote user id
   * @return the space list access
   * @since  1.2.0-GA
   */
  ListAccess<Space> getPublicSpacesWithListAccess(String userId);

  /**
   * Gets a space list access which contains all the spaces that a user can request to join and matches the provided
   * space filter.
   *
   * @param userId      the remote user id
   * @param spaceFilter the provided space filter
   * @return the space list access
   * @since 1.2.0-GA
   */
  ListAccess<Space> getPublicSpacesByFilter(String userId, SpaceFilter spaceFilter);

  /**
   * Gets a space list access which contains all the spaces that a user sent join-request to a space.
   *
   * @param userId the remote user id
   * @return the space list access
   * @since  1.3.0-GA
   */
  //ListAccess<Space> getPendingSpaces(String userId);

  /**
   * Gets a space list access which contains all the spaces that a user sent join-request to a space.
   *
   * @param userId the remote user id
   * @return the space list access
   * @since  1.2.0-GA
   */
  ListAccess<Space> getPendingSpacesWithListAccess(String userId);

  /**
   * Gets a space list access which contains all the spaces that a user sent join-request to a space and matches the
   * provided space filter.
   *
   * @param userId      the remote user id
   * @param spaceFilter the provided space filter
   * @return the space list access
   * @since  1.2.0-GA
   */
  ListAccess<Space> getPendingSpacesByFilter(String userId, SpaceFilter spaceFilter);

  /**
   * Creates a new space: create a group, its group navigation with pages for installing space applications.
   *
   * @param space         the space to be created
   * @param creatorUserId the remote user id
   * @return the created space
   */
  Space createSpace(Space space, String creatorUserId);

  /**
   * Updates a space's information
   *
   * @param existingSpace the existing space to be updated
   * @return the updated space
   * @since  1.2.0-GA
   */
  Space updateSpace(Space existingSpace);

  /**
   * Updates a space's avatar
   *
   * @param existingSpace the existing space to be updated
   * @return the updated space
   * @since  1.2.0-GA
   */
  Space updateSpaceAvatar(Space existingSpace);

  /**
   * Deletes a space. When deleting a space, all of its page navigations and its group will be deleted.
   *
   * @param space the space to be deleted
   */
  void deleteSpace(Space space);

  /**
   * Adds a user to pending list to request to join a space.
   *
   * @param space  the exising space
   * @param userId the remote user id
   * @since 1.2.0-GA
   */
  void addPendingUser(Space space, String userId);

  /**
   * Removes a user from pending list to request to join a space.
   *
   * @param space  the existing space
   * @param userId the remote user id
   * @since 1.2.0-GA
   */
  void removePendingUser(Space space, String userId);

  /**
   * Checks if a user is in the pending list to request to join a space.
   *
   * @param space  the existing space
   * @param userId the remote user id
   * @since 1.2.0-GA
   * @return
   */
  boolean isPendingUser(Space space, String userId);

  /**
   * Adds a user to invited list to join a space.
   *
   * @param space  the existing space
   * @param userId the remote user id
   * @since 1.2.0-GA
   */
  void addInvitedUser(Space space, String userId);

  /**
   * Removes a user from the invited list to join a space.
   *
   * @param space  the existing space
   * @param userId the remote user id
   * @since 1.2.0-GA
   */
  void removeInvitedUser(Space space, String userId);

  /**
   * Checks if a user is in the invited list to join a space.
   *
   * @param space  the existing space
   * @param userId the remote user id
   * @since 1.2.0-GA
   * @return
   */
  boolean isInvitedUser(Space space, String userId);

  /**
   * Adds a user to a space, the user will get the "member" role in a space.
   *
   * @param space  the existing space
   * @param userId the remote user id
   */
  void addMember(Space space, String userId);

  /**
   * Removes a member from a space.
   *
   * @param space  the existing space
   * @param userId the remote user id
   */
  void removeMember(Space space, String userId);

  /**
   * Checks whether a user is a space's member or not.
   *
   * @param space  the existing space
   * @param userId the remote user id
   * @return true if that user is a member; otherwise, false
   */
  boolean isMember(Space space, String userId);

  /**
   * Adds a user to have the "manager" role in a space.
   *
   * @param space     the existing space
   * @param userId    the remote user id
   * @param isManager true or false to indicate a user will get "manager" role or not. If false, that user will get
   *                  "member" role.
   * @since 1.2.0-GA
   */
  void setManager(Space space, String userId, boolean isManager);

  /**
   * Checks if a user has "manager" role in a space.
   *
   * @param space  the existing space
   * @param userId the remote user id
   * @return true or false
   * @since  1.2.0-GA
   */
  boolean isManager(Space space, String userId);

  /**
   * Checks if a user is the only one who has "manager" role in a space.
   *
   * @param space  the existing space
   * @param userId the remote user id
   * @return true if that user id is the only one who has "manager" role in a space. Otherwise, return false.
   * @since  1.2.0-GA
   */
  boolean isOnlyManager(Space space, String userId);

  /**
   * Checks if a user can access a space or not. If the user is root or the space's member, return true
   *
   * @param space  the existing space
   * @param userId the remote user id
   * @return true if access permission is allowed, otherwise, false.
   */
  boolean hasAccessPermission(Space space, String userId);

  /**
   * Checks if a user can have setting permission to a space or not.
   * <p/>
   * If the user is root or the space's member, return true
   *
   * @param space  the existing space
   * @param userId the remote user id
   * @return true if setting permission is allowed, otherwise, false.
   * @since  1.2.0-GA
   */
  boolean hasSettingPermission(Space space, String userId);

  /**
   * Registers a space listener plugin to listen to space lifecyle events: create, update, install application, etc,.
   *
   * @param spaceListenerPlugin a space listener plugin
   * @since 1.2.0-GA
   */
  void registerSpaceListenerPlugin(SpaceListenerPlugin spaceListenerPlugin);

  /**
   * Unregisters an existing space listener plugin.
   *
   * @param spaceListenerPlugin
   * @since 1.2.0-GA
   */
  void unregisterSpaceListenerPlugin(SpaceListenerPlugin spaceListenerPlugin);

  /**
   * Sets space application config plugin for configuring the home and space applications.
   * <p/>
   * By configuring this, space service will know how to create a new page node with title, url, and portlet to use.
   *
   * @param spaceApplicationConfigPlugin space application config plugin
   * @since 1.2.0-GA
   */
  void setSpaceApplicationConfigPlugin(SpaceApplicationConfigPlugin spaceApplicationConfigPlugin);

  /**
   * Gets the configured space application config plugin.
   *
   * @return the configured space application config plugin
   */
  SpaceApplicationConfigPlugin getSpaceApplicationConfigPlugin();


  /**
   * Gets spaces of a user which that user can see the visible spaces.
   *
   * @param userId
   * @param spaceFilter
   * @return list of spaces
   * @throws SpaceException
   * @since 1.2.5-GA
   */
  public List<Space> getVisibleSpaces(String userId, SpaceFilter spaceFilter) throws SpaceException;
  
  /**
   * Gets spaces of a user which that user can see the visible spaces.
   * @param userId
   * @param spaceFilter
   * @return list of spaces
   * @since 1.2.5-GA
   */
  public SpaceListAccess getVisibleSpacesWithListAccess(String userId, SpaceFilter spaceFilter);



  /**
   * Creates a new space and invites all users from invitedGroupId to join this newly created space.
   *
   * @param space
   * @param creator
   * @param invitedGroupId
   * @return space
   * @throws SpaceException with possible code SpaceException.Code.SPACE_ALREADY_EXIST; UNABLE_TO_ADD_CREATOR
   */
  Space createSpace(Space space, String creator, String invitedGroupId) throws SpaceException;

  /**
   * Saves a new space or updates a space.
   *
   * @param space space is saved
   * @param isNew true if creating a new space; otherwise, update an existing space.
   * @throws SpaceException with code: SpaceException.Code.ERROR_DATASTORE
   * @deprecated Use {@link #updateSpace(org.exoplatform.social.core.space.model.Space)} instead.
   *             Will be removed by 1.3.x
   */
  void saveSpace(Space space, boolean isNew) throws SpaceException;

  /**
   * Renames a space.
   * 
   * @param space the existing space
   * @param newDisplayName  new display name
   * @throws SpaceException
   * @since 1.2.8
   */
  void renameSpace(Space space, String newDisplayName) throws SpaceException;


  /**
   * Installs an application to a space.
   *
   * @param spaceId
   * @param appId
   * @throws SpaceException with code SpaceException.Code.ERROR_DATA_STORE
   */
  void installApplication(String spaceId, String appId) throws SpaceException;

  /**
   * Installs an application to a space.
   *
   * @param space
   * @param appId
   * @throws SpaceException with code SpaceException.Code.ERROR_DATA_STORE
   */
  void installApplication(Space space, String appId) throws SpaceException;

  /**
   * Activates an installed application in a space.
   *
   * @param space
   * @param appId
   * @throws SpaceException with possible code: SpaceException.Code.UNABLE_TO_ADD_APPLICATION,
   *                                            SpaceExeption.Code.ERROR_DATA_STORE
   */
  void activateApplication(Space space, String appId) throws SpaceException;

  /**
   * Activates an installed application in a space.
   *
   * @param spaceId
   * @param appId
   * @throws SpaceException with possible code: SpaceException.Code.UNABLE_TO_ADD_APPLICATION,
   *                                            SpaceExeption.Code.ERROR_DATA_STORE
   */
  void activateApplication(String spaceId, String appId) throws SpaceException;

  /**
   * Deactivates an installed application in a space.
   *
   * @param space
   * @param appId
   * @throws SpaceException
   */
  void deactivateApplication(Space space, String appId) throws SpaceException;

  /**
   * Deactivates an installed application in a space.
   *
   * @param spaceId
   * @param appId
   * @throws SpaceException
   */
  void deactivateApplication(String spaceId, String appId) throws SpaceException;

  /**
   * Removes an installed application from a space.
   *
   * @param space
   * @param appId
   * @throws SpaceException
   */
  void removeApplication(Space space, String appId, String appName) throws SpaceException;

  /**
   * Removes an installed application from a space.
   *
   * @param spaceId
   * @param appId
   */
  void removeApplication(String spaceId, String appId, String appName) throws SpaceException;

  /**
   * Gets the portlet preferences required to use in creating the portlet application.
   *
   * @return
   * @deprecated Will be removed by 1.3.x
   */
  String [] getPortletsPrefsRequired();

}
