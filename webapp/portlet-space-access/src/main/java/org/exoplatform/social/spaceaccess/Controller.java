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

import org.exoplatform.container.PortalContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.application.RequestNavigationData;
import org.exoplatform.social.core.space.SpaceAccessType;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.spaceaccess.providers.ServiceProvider;
import org.exoplatform.social.spaceaccess.templates.access;
import org.exoplatform.social.spaceaccess.templates.invited;
import org.exoplatform.social.spaceaccess.templates.request;
import org.exoplatform.social.spaceaccess.templates.requestOK;
import org.exoplatform.social.spaceaccess.templates.requestNOK;
import org.exoplatform.social.spaceaccess.templates.requestClosed;
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
  @Inject @Path("requestOK.gtmpl") requestOK requestOK;
  @Inject @Path("requestNOK.gtmpl") requestNOK requestNOK;
  @Inject @Path("requestClosed.gtmpl") requestClosed requestClosed;
  
  @Inject SpaceService spaceService;
  
  static private final String WIKI_LINK = "http://int.exoplatform.org/portal/intranet/wiki/group/spaces/engineering/Spec_Func_-_Wiki_Page_Permalink";
  static private final String ALL_SPACE_LINK_URI = "/all-spaces";
  
  @View
  public void index() throws Exception {
    PortalRequestContext pcontext = (PortalRequestContext)(WebuiRequestContext.getCurrentInstance());
    Object spaceObject = pcontext.getRequest().getSession().getAttribute(SpaceAccessType.ACCESSED_SPACE_NAME_KEY);
    Object statusObject = pcontext.getRequest().getSession().getAttribute(SpaceAccessType.ACCESSED_TYPE_KEY);
    if (spaceObject == null) {
      if (statusObject != null && "social.space.access.space-not-found".equals(statusObject.toString()))
        requestNOK();
    }
    else {
      String status = statusObject.toString();
      String spaceName = spaceObject.toString();
      if ("social.space.access.join-space".equals(status))
        access(spaceName);
      else if ("social.space.access.closed-space".equals(status))
        requestClosed(spaceName);
      else if ("social.space.access.request-join-space".equals(status))
        request(spaceName);
      else if ("social.space.access.requested-join-space".equals(status))
        requestOK(spaceName);
      else if ("social.space.access.invited-space".equals(status))
        invited(spaceName);
      else if ("social.space.access.not-access-wiki-space".equals(status))
        wikilink();
      else spaceFound(spaceName);
    }
    pcontext.getRequest().getSession().removeAttribute(SpaceAccessType.ACCESSED_SPACE_NAME_KEY);
    pcontext.getRequest().getSession().removeAttribute(SpaceAccessType.ACCESSED_TYPE_KEY);
  }
  
  @View
  public void access(String spaceName) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("spaceName", spaceName);
    access.render(parameters);
  }
  
  @View
  public void invited(String spaceName) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("spaceName", spaceName);
    invited.render(parameters);
  }
  
  @View
  public void request(String spaceName) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("spaceName", spaceName);
    request.render(parameters);
  }
  
  @View
  public void requestOK(String spaceName) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("spaceName", spaceName);
    requestOK.render(parameters);
  }
  
  @View
  public void requestNOK() throws Exception {
    requestNOK.render();
  }
  
  @View
  public void requestClosed(String spaceName) throws Exception {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("spaceName", spaceName);
    requestClosed.render(parameters);
  }
  
  @View
  public Response.Redirect spaceFound(String spaceName) throws Exception {
    Space space = spaceService.getSpaceByPrettyName(spaceName);
    return Response.redirect(Utils.getSpaceHomeURL(space));
  }
  
  @View
  public Response.Redirect wikilink() throws Exception {
    return Response.redirect(WIKI_LINK);
  }
  
  @Action
  public Response.Redirect join(String spaceName) {
    String remoteId = Utils.getOwnerRemoteId();
    Space space = spaceService.getSpaceByPrettyName(spaceName);
    spaceService.addMember(space, remoteId);
    return Response.redirect(Utils.getSpaceHomeURL(space));
  }
  
  @Action
  public Response requestToJoin(String spaceName) {
    String remoteId = Utils.getOwnerRemoteId();
    Space space = spaceService.getSpaceByPrettyName(spaceName);
    spaceService.addPendingUser(space, remoteId);
    return Controller_.requestOK(spaceName);
  }
  
  @Action
  public Response.Redirect goToAllSpaces() {
    return Response.redirect(Utils.getURI("all-spaces"));
  }
  
  @Action
  public Response.Redirect refuse(String spaceName) {
    String remoteId = Utils.getOwnerRemoteId();
    Space space = spaceService.getSpaceByPrettyName(spaceName);
    spaceService.removeInvitedUser(space, remoteId);
    return Response.redirect(Utils.getURI("all-spaces"));
  }
  
}