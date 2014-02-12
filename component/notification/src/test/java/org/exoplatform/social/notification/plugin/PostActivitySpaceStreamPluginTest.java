/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
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
package org.exoplatform.social.notification.plugin;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.model.NotificationKey;
import org.exoplatform.commons.api.notification.plugin.AbstractNotificationPlugin;
import org.exoplatform.commons.notification.impl.NotificationContextImpl;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.notification.AbstractPluginTest;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          thanhvc@exoplatform.com
 * Aug 20, 2013  
 */
public class PostActivitySpaceStreamPluginTest extends AbstractPluginTest {

  @Override
  public AbstractNotificationPlugin getPlugin() {
    return pluginService.getPlugin(NotificationKey.key(PostActivitySpaceStreamPlugin.ID));
  }

  public void testDigestWithDeletedActivity() throws Exception {
    Space space = getSpaceInstance(1);
    spaceService.addMember(space, demoIdentity.getRemoteId());
    spaceService.addMember(space, johnIdentity.getRemoteId());
    spaceService.addMember(space, maryIdentity.getRemoteId());
    
    Identity spaceIdentity = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName(), true);
    
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setTitle("demo post activity on space 1");
    activity.setUserId(demoIdentity.getId());
    activityManager.saveActivityNoReturn(spaceIdentity, activity);
    tearDownActivityList.add(activity);
    
    ExoSocialActivity activity2 = new ExoSocialActivityImpl();
    activity2.setTitle("john post activity on space 1");
    activity2.setUserId(johnIdentity.getId());
    activityManager.saveActivityNoReturn(spaceIdentity, activity2);
    tearDownActivityList.add(activity2);
    
    //Digest
    List<NotificationInfo> list = assertMadeNotifications(6);
    
    NotificationContext ctx = NotificationContextImpl.cloneInstance();
    list.set(0, list.get(0).setTo(rootIdentity.getRemoteId()));
    ctx.setNotificationInfos(list);
    
    //demo delete his activity
    activityManager.deleteActivity(activity);
    tearDownActivityList.remove(activity);
    
    Writer writer = new StringWriter();
    getPlugin().buildDigest(ctx, writer);
    assertDigest(writer, "John Anthony posted in my space 1.");
  }
}
