/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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
package org.exoplatform.social.core.activity;

import java.util.Collections;
import java.util.List;

import org.exoplatform.social.common.ActivityFilter;
import org.exoplatform.social.common.RealtimeListAccess;
import org.exoplatform.social.common.jcr.Util;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.storage.api.ActivityStorage;

/**
 * The real time list access for activities.
 *
 * @author <a href="http://hoatle.net">hoatle (hoatlevan at gmail dot com)</a>
 * @since Apr 8, 2011
 */
public class ActivitiesRealtimeListAccess implements RealtimeListAccess<ExoSocialActivity> {

  public static enum ActivityType {
    ACTIVITY_FEED,
    USER_ACTIVITIES,
    CONNECTIONS_ACTIVITIES,
    USER_SPACE_ACTIVITIES
  }

  /**
   * The activity activityStorage.
   */
  private ActivityStorage activityStorage;


  /**
   * The chosen activity type.
   */
  private ActivityType activityType;

  /**
   * The chosen identity.
   */
  private Identity ownerIdentity;
  
  private ActivityFilter activityFilter;


  /**
   * Constructor.
   *
   * @param existingActivityStorage
   * @param chosenActivityType
   * @param chosenOwnerIdentity
   */
  public ActivitiesRealtimeListAccess(final ActivityStorage existingActivityStorage,
                                      final ActivityType chosenActivityType,
                                      final Identity chosenOwnerIdentity) {
    this.activityStorage = existingActivityStorage;
    this.activityType = chosenActivityType;
    this.ownerIdentity = chosenOwnerIdentity;
  }


  public ActivitiesRealtimeListAccess(final ActivityStorage existingActivityStorage,
                                      final ActivityType chosenActivityType,
                                      final Identity chosenOwnerIdentity,
                                      final ActivityFilter activityFilter) {
    this.activityStorage = existingActivityStorage;
    this.activityType = chosenActivityType;
    this.ownerIdentity = chosenOwnerIdentity;
    this.activityFilter = activityFilter;
  }


  /**
   * {@inheritDoc}
   */
  public List<ExoSocialActivity> loadAsList(int index, int limit) {
    switch (activityType) {
      case ACTIVITY_FEED: {
        return activityStorage.getActivityFeed(ownerIdentity, index, limit);
      }
      case USER_ACTIVITIES: {
        return activityStorage.getUserActivities(ownerIdentity, index, limit);
      }
      case CONNECTIONS_ACTIVITIES: {
        return activityStorage.getActivitiesOfConnections(ownerIdentity, index, limit);
      }
      case USER_SPACE_ACTIVITIES: {
        return activityStorage.getUserSpacesActivities(ownerIdentity, index, limit);
      }
    }
    return Collections.emptyList();
  }

  /**
   * {@inheritDoc}
   */
  public ExoSocialActivity[] load(int index, int limit) {
    return Util.convertListToArray(loadAsList(index, limit), ExoSocialActivity.class);
  }

  /**
   * {@inheritDoc}
   */
  public int getSize() {
    switch (activityType) {
      case ACTIVITY_FEED: {
        return activityStorage.getNumberOfActivitesOnActivityFeed(ownerIdentity);
      }
      case USER_ACTIVITIES: {
        return activityStorage.getNumberOfUserActivities(ownerIdentity);
      }
      case CONNECTIONS_ACTIVITIES: {
        return activityStorage.getNumberOfActivitiesOfConnections(ownerIdentity);
      }
      case USER_SPACE_ACTIVITIES: {
        return activityStorage.getNumberOfUserSpacesActivities(ownerIdentity);
      }
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  public List<ExoSocialActivity> loadNewer(ExoSocialActivity baseActivity, int length) {
    switch (activityType) {
      case ACTIVITY_FEED: {
        return activityStorage.getNewerOnActivityFeed(ownerIdentity, baseActivity, length);
      }
      case USER_ACTIVITIES: {
        return activityStorage.getNewerOnUserActivities(ownerIdentity, baseActivity, length);
      }
      case CONNECTIONS_ACTIVITIES: {
        return activityStorage.getNewerOnActivitiesOfConnections(ownerIdentity, baseActivity, length);
      }
      case USER_SPACE_ACTIVITIES: {
        return activityStorage.getNewerOnUserSpacesActivities(ownerIdentity, baseActivity, length);
      }
    }
    return Collections.emptyList();
  }

  /**
   * {@inheritDoc}
   */
  public int getNumberOfNewer(ExoSocialActivity baseActivity) {
     switch (activityType) {
      case ACTIVITY_FEED: {
        return activityStorage.getNumberOfNewerOnActivityFeed(ownerIdentity, baseActivity);
      }
      case USER_ACTIVITIES: {
        return activityStorage.getNumberOfNewerOnUserActivities(ownerIdentity, baseActivity);
      }
      case CONNECTIONS_ACTIVITIES: {
        return activityStorage.getNumberOfNewerOnActivitiesOfConnections(ownerIdentity, baseActivity);
      }
      case USER_SPACE_ACTIVITIES: {
        return activityStorage.getNumberOfNewerOnUserSpacesActivities(ownerIdentity, baseActivity);
      }
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  public List<ExoSocialActivity> loadOlder(ExoSocialActivity baseActivity, int length) {
    switch (activityType) {
      case ACTIVITY_FEED: {
        return activityStorage.getOlderOnActivityFeed(ownerIdentity, baseActivity, length);
      }
      case USER_ACTIVITIES: {
        return activityStorage.getOlderOnUserActivities(ownerIdentity, baseActivity, length);
      }
      case CONNECTIONS_ACTIVITIES: {
        return activityStorage.getOlderOnActivitiesOfConnections(ownerIdentity, baseActivity, length);
      }
      case USER_SPACE_ACTIVITIES: {
        return activityStorage.getOlderOnUserSpacesActivities(ownerIdentity, baseActivity, length);
      }
    }
    return Collections.emptyList();
  }

  /**
   * {@inheritDoc}
   */
  public int getNumberOfOlder(ExoSocialActivity baseActivity) {
     switch (activityType) {
      case ACTIVITY_FEED: {
        return activityStorage.getNumberOfOlderOnActivityFeed(ownerIdentity, baseActivity);
      }
      case USER_ACTIVITIES: {
        return activityStorage.getNumberOfOlderOnUserActivities(ownerIdentity, baseActivity);
      }
      case CONNECTIONS_ACTIVITIES: {
        return activityStorage.getNumberOfOlderOnActivitiesOfConnections(ownerIdentity, baseActivity);
      }
      case USER_SPACE_ACTIVITIES: {
        return activityStorage.getNumberOfOlderOnUserSpacesActivities(ownerIdentity, baseActivity);
      }
    }
    return 0;
  }
  
  /**
   * {@inheritDoc}
   */
  public List<ExoSocialActivity> loadNewer(ActivityFilter activityFilter, int limit) {
    switch (activityType) {
      case ACTIVITY_FEED: {
        return activityStorage.getNewerOnActivityFeed(ownerIdentity, activityFilter, limit);
      }
      case USER_ACTIVITIES: {
        //return activityStorage.getNewerOnUserActivities(ownerIdentity, activityFilter, length);
        return null;
      }
      case CONNECTIONS_ACTIVITIES: {
        //return activityStorage.getNewerOnActivitiesOfConnections(ownerIdentity, activityFilter, length);
        return null;
      }
      case USER_SPACE_ACTIVITIES: {
        //return activityStorage.getNewerOnUserSpacesActivities(ownerIdentity, activityFilter, length);
        return null;
      }
    }
    return Collections.emptyList();
  }

  /**
   * {@inheritDoc}
   */
  public int getNumberOfNewer(ActivityFilter activityFilter) {
     switch (activityType) {
      case ACTIVITY_FEED: {
        return activityStorage.getNumberOfNewerOnActivityFeed(ownerIdentity, activityFilter);
      }
      case USER_ACTIVITIES: {
        //return activityStorage.getNumberOfNewerOnUserActivities(ownerIdentity, activityFilter);
        return 0;
      }
      case CONNECTIONS_ACTIVITIES: {
        //return activityStorage.getNumberOfNewerOnActivitiesOfConnections(ownerIdentity, activityFilter);
        return 0;
      }
      case USER_SPACE_ACTIVITIES: {
        //return activityStorage.getNumberOfNewerOnUserSpacesActivities(ownerIdentity, activityFilter);
        return 0;
      }
    }
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  public List<ExoSocialActivity> loadOlder(ActivityFilter activityFilter, int limit) {
    switch (activityType) {
      case ACTIVITY_FEED: {
        return activityStorage.getOlderOnActivityFeed(ownerIdentity, activityFilter, limit);
      }
      case USER_ACTIVITIES: {
        //return activityStorage.getOlderOnUserActivities(ownerIdentity, activityFilter, length);
        return null;
      }
      case CONNECTIONS_ACTIVITIES: {
        //return activityStorage.getOlderOnActivitiesOfConnections(ownerIdentity, activityFilter, length);
        return null;
      }
      case USER_SPACE_ACTIVITIES: {
        //return activityStorage.getOlderOnUserSpacesActivities(ownerIdentity, activityFilter, length);
        return null;
      }
    }
    return Collections.emptyList();
  }

  /**
   * {@inheritDoc}
   */
  public int getNumberOfOlder(ActivityFilter activityFilter) {
     switch (activityType) {
      case ACTIVITY_FEED: {
        return activityStorage.getNumberOfOlderOnActivityFeed(ownerIdentity, activityFilter);
      }
      case USER_ACTIVITIES: {
        //return activityStorage.getNumberOfOlderOnUserActivities(ownerIdentity, activityFilter);
        return 0;
      }
      case CONNECTIONS_ACTIVITIES: {
        //return activityStorage.getNumberOfOlderOnActivitiesOfConnections(ownerIdentity, activityFilter);
        return 0;
      }
      case USER_SPACE_ACTIVITIES: {
        //return activityStorage.getNumberOfOlderOnUserSpacesActivities(ownerIdentity, activityFilter);
        return 0;
      }
    }
    return 0;
  }
}
