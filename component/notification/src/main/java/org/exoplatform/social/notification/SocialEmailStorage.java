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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

import org.exoplatform.commons.api.notification.NotificationMessage;


public class SocialEmailStorage {
  
  public enum CONNECTOR_TYPE  {
    ACTIVITY, PROFILE, RELATIONSHIP, SPACE
  }
  
  final Map<CONNECTOR_TYPE, Queue<NotificationMessage>> pendingMessages = new LinkedHashMap<CONNECTOR_TYPE, Queue<NotificationMessage>>();

  public void add(NotificationMessage message, CONNECTOR_TYPE type) {
    pendingMessages.get(type).add(message);
  }

  public void addAll(Collection<NotificationMessage> messages, CONNECTOR_TYPE type) {
    pendingMessages.get(type).addAll(messages);
  }

  public Collection<NotificationMessage> getEmailNotification(CONNECTOR_TYPE type) {
    Queue<NotificationMessage> messagesQueue = pendingMessages.get(type);
    Collection<NotificationMessage> pending = new ArrayList<NotificationMessage>(messagesQueue);
    //
    messagesQueue.clear();
    return pending;
  }
}