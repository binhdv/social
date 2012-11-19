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

import java.util.Date;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.common.RealtimeListAccess;
import org.exoplatform.social.core.ActivityProcessor;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.ActivitiesRealtimeListAccess;
import org.exoplatform.social.core.activity.ActivitiesRealtimeListAccess.ActivityType;
import org.exoplatform.social.common.ActivityFilter;
import org.exoplatform.social.core.activity.CommentsRealtimeListAccess;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.core.storage.ActivityStorageException;
import org.exoplatform.social.core.storage.api.ActivityStorage;

/**
 * Class ActivityManagerImpl implements ActivityManager without caching.
 *
 * @author <a href="mailto:vien_levan@exoplatform.com">vien_levan</a>
 * @author <a href="hoatle.net">hoatle (hoatlevan at gmail dot com)</a>
 * @since Nov 24, 2010
 * @since 1.2.0-GA
 */
public class ActivityManagerImpl implements ActivityManager {
  /** Logger */
  private static final Log               LOG = ExoLogger.getLogger(ActivityManagerImpl.class);

  /** The activityStorage. */
  protected ActivityStorage activityStorage;

  /** identityManager to get identity for saving and getting activities */
  protected IdentityManager              identityManager;

  /** spaceService */
  protected SpaceService                 spaceService;

  /**
   * Default limit for deprecated methods to get maximum number of activities.
   */
  private static final int DEFAULT_LIMIT = 20;

  /**
   * Instantiates a new activity manager.
   *
   * @param activityStorage
   * @param identityManager
   */
  public ActivityManagerImpl(ActivityStorage activityStorage, IdentityManager identityManager) {
    this.activityStorage = activityStorage;
    this.identityManager = identityManager;
  }

  /**
   * {@inheritDoc}
   */
  public void saveActivityNoReturn(Identity streamOwner, ExoSocialActivity newActivity) {
    long currentTime = System.currentTimeMillis();
    newActivity.setUpdated(new Date(currentTime));
    //new activity
    if (newActivity.getId() == null) {
      newActivity.setPostedTime(currentTime);
    }
    activityStorage.saveActivity(streamOwner, newActivity);
  }

  /**
   * {@inheritDoc}
   */
  public void saveActivityNoReturn(ExoSocialActivity newActivity) {
    Identity owner = getStreamOwner(newActivity);
    saveActivityNoReturn(owner, newActivity);
  }

  /**
   * {@inheritDoc}
   */
  public void saveActivity(Identity streamOwner, String activityType, String activityTitle) {
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setType(activityType);
    activity.setTitle(activityTitle);
    saveActivityNoReturn(streamOwner, activity);
  }

  /**
   * {@inheritDoc}
   */
  public ExoSocialActivity getActivity(String activityId) {
    return activityStorage.getActivity(activityId);
  }

  /**
   * {@inheritDoc}
   */
  public ExoSocialActivity getParentActivity(ExoSocialActivity comment) {
    return activityStorage.getParentActivity(comment);
  }

  /**
   * {@inheritDoc}
   */
  public void updateActivity(ExoSocialActivity existingActivity) {
    activityStorage.updateActivity(existingActivity);
  }

  /**
   * {@inheritDoc}
   */
  public void deleteActivity(ExoSocialActivity existingActivity) {
    Validate.notNull(existingActivity.getId(), "existingActivity.getId() must not be null!");
    deleteActivity(existingActivity.getId());
  }

  /**
   * {@inheritDoc}
   */
  public void deleteActivity(String activityId) {
    activityStorage.deleteActivity(activityId);
  }

  /**
   * {@inheritDoc}
   */
  public void saveComment(ExoSocialActivity existingActivity, ExoSocialActivity newComment) throws
          ActivityStorageException {
    activityStorage.saveComment(existingActivity, newComment);
  }

  /**
   * {@inheritDoc}
   */
  public RealtimeListAccess<ExoSocialActivity> getCommentsWithListAccess(ExoSocialActivity existingActivity) {
    return new CommentsRealtimeListAccess(activityStorage, existingActivity);
  }

  /**
   * {@inheritDoc}
   */
  public void deleteComment(String activityId, String commentId) {
    activityStorage.deleteComment(activityId, commentId);
  }

  /**
   * {@inheritDoc}
   */
  public void deleteComment(ExoSocialActivity existingActivity, ExoSocialActivity existingComment) {
    deleteComment(existingActivity.getId(), existingComment.getId());
  }


  /**
   * {@inheritDoc}
   */
  public void saveLike(ExoSocialActivity existingActivity, Identity identity) {

    existingActivity.setTitle(null);
    existingActivity.setBody(null);

    String[] identityIds = existingActivity.getLikeIdentityIds();
    if (ArrayUtils.contains(identityIds, identity.getId())) {
      LOG.warn("activity is already liked by identity: " + identity);
      return;
    }
    identityIds = (String[]) ArrayUtils.add(identityIds, identity.getId());
    existingActivity.setLikeIdentityIds(identityIds);
    updateActivity(existingActivity);
  }

  /**
   * {@inheritDoc}
   */
  public void deleteLike(ExoSocialActivity activity, Identity identity) {
    activity.setTitle(null);
    activity.setBody(null);
    String[] identityIds = activity.getLikeIdentityIds();
    if (ArrayUtils.contains(identityIds, identity.getId())) {
      identityIds = (String[]) ArrayUtils.removeElement(identityIds, identity.getId());
      activity.setLikeIdentityIds(identityIds);
      updateActivity(activity);
    } else {
      LOG.warn("activity is not liked by identity: " + identity);
    }
  }


  /**
   * {@inheritDoc}
   */
  public RealtimeListAccess<ExoSocialActivity> getActivitiesWithListAccess(Identity existingIdentity) {
    return new ActivitiesRealtimeListAccess(activityStorage, ActivityType.USER_ACTIVITIES, existingIdentity);
  }


  /**
   * {@inheritDoc}
   */
  public RealtimeListAccess<ExoSocialActivity> getActivitiesOfConnectionsWithListAccess(Identity existingIdentity) {
    return new ActivitiesRealtimeListAccess(activityStorage, ActivityType.CONNECTIONS_ACTIVITIES, existingIdentity);
  }

  /**
   * {@inheritDoc}
   */
  public RealtimeListAccess<ExoSocialActivity> getActivitiesOfUserSpacesWithListAccess(Identity existingIdentity) {
    return new ActivitiesRealtimeListAccess(activityStorage, ActivityType.USER_SPACE_ACTIVITIES, existingIdentity);
  }

  /**
   * {@inheritDoc}
   */
  public RealtimeListAccess<ExoSocialActivity> getActivityFeedWithListAccess(Identity existingIdentity) {
    return new ActivitiesRealtimeListAccess(activityStorage, ActivityType.ACTIVITY_FEED, existingIdentity);
  }


  /**
   * {@inheritDoc}
   */
  public void addProcessor(ActivityProcessor processor) {
    activityStorage.getActivityProcessors().add(processor);
    LOG.debug("added activity processor " + processor.getClass());
  }

  /**
   * {@inheritDoc}
   */
  public void addProcessorPlugin(BaseActivityProcessorPlugin plugin) {
    this.addProcessor(plugin);
  }

  /**
   * Validates the start and limit for duplicated method.
   * The limit must be greater than or equal to start.
   * The limit must be equal to or greater than start by {@link #DEFAULT_LIMIT}
   *
   * @param start
   * @param limit
   */
  private void validateStartLimit(long start, long limit) {
    Validate.isTrue(limit >= start, "'limit' must be greater than or equal to 'start'");
    Validate.isTrue(limit - start <= DEFAULT_LIMIT, "'limit - start' must be less than or equal to " + DEFAULT_LIMIT);
  }

  /**
   * Gets stream owner from identityId = newActivity.userId.
   * @param newActivity the new activity
   * @return the identity stream owner
   */
  private Identity getStreamOwner(ExoSocialActivity newActivity) {
    Validate.notNull(newActivity.getUserId(), "activity.getUserId() must not be null!");
    return identityManager.getIdentity(newActivity.getUserId(), false);
  }

  public RealtimeListAccess<ExoSocialActivity> getFeed(Identity existingIdentity, ActivityFilter activityFilter) {
    return new ActivitiesRealtimeListAccess(activityStorage, ActivityType.ACTIVITY_FEED,existingIdentity, activityFilter);
  }

}
