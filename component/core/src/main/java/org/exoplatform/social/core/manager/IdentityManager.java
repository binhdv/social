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

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.social.core.identity.IdentityProvider;
import org.exoplatform.social.core.identity.IdentityProviderPlugin;
import org.exoplatform.social.core.identity.SpaceMemberFilterListAccess.Type;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.profile.ProfileFilter;
import org.exoplatform.social.core.profile.ProfileListenerPlugin;
import org.exoplatform.social.core.space.model.Space;

/**
 * The Interface IdentityManager.
 *
 * @author <a href="mailto:vien_levan@exoplatform.com">vien_levan</a>
 * @author <a href="http://hoatle.net">hoatle</a>
 */
public interface IdentityManager {

  /**
   * Gets or creates an Identity object provided by a identity provider.
   *
   * @param providerId               the name of identity provider name id.
   * @param remoteId                 the identifier that identifies the identity in the specific identity provider.
   * @param forceLoadOrReloadProfile force load or reload profile
   * @return the Identity object provided from identity provider.
   */
  Identity getOrCreateIdentity(String providerId, String remoteId, boolean forceLoadOrReloadProfile);

  /**
   * Gets the stored identity by its identity id, this id is its uuid stored by JCR.
   *
   * @param identityId               the stored JCR uuid.
   * @param forceLoadOrReloadProfile force load or reload profile
   * @return the found identity object
   */
  Identity getIdentity(String identityId, boolean forceLoadOrReloadProfile);

  /**
   * Updates existing identity's properties.
   *
   * @param identity the identity to be updated.
   * @return the updated identity.
   * @since  1.2.0-GA
   */
  Identity updateIdentity(Identity identity);

  /**
   * Deletes an existing identity.
   *
   * @param identity the existing identity.
   */
  void deleteIdentity(Identity identity);

  /**
   * Hard deletes an existing identity.
   *
   * @param identity the existing identity.
   */
  void hardDeleteIdentity(Identity identity);

  /**
   * Gets identity list access which contains all the identities connected with the provided identity.
   *
   * @param identity the provided identity
   * @return the identity list access
   * @since  1.2.0-GA
   */
  ListAccess<Identity> getConnectionsWithListAccess(Identity identity);

  /**
   * Gets a profile associated with a provided identity.
   *
   * @param identity the provided identity
   * @return the profile associated with the provided identity
   * @since  1.2.0-GA
   */
  Profile getProfile(Identity identity);

  /**
   * Updates an existing profile.
   *
   * @param existingProfile the existing profile
   * @since  1.2.0-GA
   */
  void updateProfile(Profile existingProfile);

  /**
   * Gets identity list access which contains all the identities from a provided provider which are filtered by profile
   * filter.
   *
   * @param providerId               the provider name id
   * @param profileFilter            the profile filter
   * @param forceLoadOrReloadProfile force load or reload profile or not
   * @return the identity list access
   * @since 1.2.0-GA
   */
  ListAccess<Identity> getIdentitiesByProfileFilter(String providerId, ProfileFilter profileFilter,
                                                  boolean forceLoadOrReloadProfile);
  
  ListAccess<Identity> getSpaceIdentityByProfileFilter(Space space, ProfileFilter profileFilter, Type type,
                                                       boolean forceLoadorReloadProfile);
  
  /**
   * Adds an identity provider to identity manager.
   *
   * @param identityProvider an identity provider
   */
  void addIdentityProvider(IdentityProvider<?> identityProvider);

  /**
   * Remove an existing identity provider.
   *
   * @param identityProvider the existing identity provider
   * @since 1.2.0-GA
   */
  void removeIdentityProvider(IdentityProvider<?> identityProvider);

  /**
   * Registers a profile listener plugin by external compnent plugin mechanism.
   *
   * For example:
   * <pre>
   *  &lt;external-component-plugins&gt;
   *    &lt;target-component&gt;org.exoplatform.social.core.manager.IdentityManager&lt;/target-component&gt;
   *    &lt;component-plugin&gt;
   *      &lt;name&gt;ProfileUpdatesPublisher&lt;/name&gt;
   *      &lt;set-method&gt;registerProfileListener&lt;/set-method&gt;
   *      &lt;type&gt;org.exoplatform.social.core.application.ProfileUpdatesPublisher&lt;/type&gt;
   *    &lt;/component-plugin&gt;
   *  &lt;/external-component-plugins&gt;
   * </pre>
   *
   * @param profileListenerPlugin a profile listener plugin
   * @since 1.2.0-GA
   */
  void registerProfileListener(ProfileListenerPlugin profileListenerPlugin);

  /**
   * Registers one or more {@link IdentityProvider} through an
   * {@link IdentityProviderPlugin}.
   *
   * @param plugin
   */
  void registerIdentityProviders(IdentityProviderPlugin plugin);

}
