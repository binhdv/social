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
package org.exoplatform.social.spaceaccess;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import juzu.Action;
import juzu.Path;
import juzu.Response;
import juzu.View;

import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.application.RequestNavigationData;
import org.exoplatform.social.core.space.SpaceAccessType;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.spaceaccess.providers.ServiceProvider;
import org.exoplatform.social.spaceaccess.templates.access;
import org.exoplatform.social.spaceaccess.templates.wikilink;
import org.exoplatform.social.spaceaccess.templates.invited;
import org.exoplatform.social.spaceaccess.templates.request;
import org.exoplatform.social.spaceaccess.templates.requested;
import org.exoplatform.social.spaceaccess.templates.requestOK;
import org.exoplatform.social.spaceaccess.templates.requestNotFound;
import org.exoplatform.social.spaceaccess.templates.requestClosed;
import org.exoplatform.social.spaceaccess.templates.spaceFound;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Created by The eXo Platform SAS
 * Author : quangpld
 *          quangpld@exoplatform.com
 * Oct 18, 2012
 */
public class Controller {
  
  @Inject @Path("access.gtmpl") access access;
  @Inject @Path("invited.gtmpl") invited invited;
  @Inject @Path("request.gtmpl") request request;
  @Inject @Path("requested.gtmpl") requested requested;
  @Inject @Path("requestOK.gtmpl") requestOK requestOK;
  @Inject @Path("requestNotFound.gtmpl") requestNotFound requestNotFound;
  @Inject @Path("requestClosed.gtmpl") requestClosed requestClosed;
  @Inject @Path("spaceFound.gtmpl") spaceFound spaceFound;
  @Inject @Path("wikilink.gtmpl") wikilink wikilink;
  
  @Inject SpaceService spaceService;
  
  @View
  public void index() throws Exception {
    PortalRequestContext pcontext = (PortalRequestContext)(WebuiRequestContext.getCurrentInstance());
    String status = pcontext.getRequest().getSession().getAttribute(SpaceAccessType.ACCESSED_TYPE_KEY).toString();
    String space = pcontext.getRequest().getSession().getAttribute(SpaceAccessType.ACCESSED_SPACE_NAME_KEY).toString();
    if ("social.space.access.join-space".equals(status))
      access(space);
    else if ("social.space.access.space-not-found".equals(status))
      requestNotFound();
    else if ("social.space.access.closed-space".equals(status))
      requestClosed(space);
    else if ("social.space.access.request-join-space".equals(status))
      request(space);
    else if ("social.space.access.requested-join-space".equals(status))
      requested(space);
    else if ("social.space.access.invited-space".equals(status))
      invited(space);
    else if ("social.space.access.not-access-wiki-space".equals(status))
      wikilink(space);
    else spaceFound(space);
  }
  
  @View
  public void access(String space) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("space", space);
    access.render(parameters);
  }
  
  @View
  public void invited(String space) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("space", space);
    invited.render(parameters);
  }
  
  @View
  public void requested(String space) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("space", space);
    requested.render(parameters);
  }
  
  @View
  public void request(String space) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("space", space);
    request.render(parameters);
  }
  
  @View
  public void requestOK(String space) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("space", space);
    requestOK.render(parameters);
  }
  
  @View
  public void requestNotFound(String space) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("space", space);
    requestNotFound.render(parameters);
  }
  
  @View
  public void requestNotFound() throws Exception {
    requestNotFound.render();
  }
  
  @View
  public void requestClosed(String space) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("space", space);
    requestClosed.render(parameters);
  }
  
  @View
  public void spaceFound(String space) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("space", space);
    spaceFound.render(parameters);
  }
  
  @View
  public void wikilink(String space) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("space", space);
    wikilink.render(parameters);
  }
  
  @Action
  public Response.Redirect join(String space) {
    String remoteId = Utils.getOwnerRemoteId();
    Space sp = spaceService.getSpaceByPrettyName(space);
    spaceService.addMember(sp, remoteId);
    return Response.redirect("http://localhost:8080/socialdemo/g/:spaces:"+space+"/"+space);
  }
  
  @Action
  public Response requestToJoin(String space) {
    String remoteId = Utils.getOwnerRemoteId();
    Space sp = spaceService.getSpaceByPrettyName(space);
    spaceService.addInvitedUser(sp, remoteId);
    return Controller_.requestOK(space);
  }
  
}
