/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
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
package org.exoplatform.social.service.rest.api.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsersCollections extends ResourceCollections {
  
  private List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
  
  public UsersCollections(int size, int offset, int limit) {
    super(size, offset, limit);
  }

  /**
   * @return the users
   */
  public List<Map<String, Object>> getUsers() {
    return users;
  }

  /**
   * @param users the users to set
   */
  public void setUsers(List<Map<String, Object>> users) {
    this.users = users;
  }

}
