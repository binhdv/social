;(function($, document, window) {
  var template = null;
  var display = true;
  $(document).ready(
    function () {
      var cssCt = '.UIUserInfoMenu {visibility: visible; display:inline; float:none; position: relative; width: 1px;}\n'
                + '.UIInfoContent {position: absolute; left: -50px; top: 10px; background: #fff; font-size:11px; padding:10px; box-shadow: -1px 2px 3px #D5D5D5; border-radius: 8px 8px 8px 8px; max-width: 250px;}\n'
                + '.UIInfoContent .Avatar {width: 50px; height: 50px; border: solid 1px #b7b7b7; border-radius:5px; float:left;}\n'
                + '.UIInfoContent .RightInfo {margin-left: 60px; line-height: 14px;}\n'
                + '.UIInfoContent .FullName {font-weight: bold;color: darkorange; font-size: 11px; white-space:nowrap;}\n'
                + '.UIInfoContent .Status {color: #222222; font-size: 10px; white-space:nowrap;}\n'
                + '.UIInfoContent .More {color: #darkorange; font-size: 10px; white-space:nowrap;}\n'
                + '.UIInfoContent .LeftRowIcon {}\n'
                + '.UIInfoContent .Invite {line-height: 24px; text-align: center; margin: 6px auto; max-width:150px; border:solid 1px gray; cursor:pointer;}\n';
      $(document.head).append('<style type="text/css" id="StyleUserInfo">\n' + cssCt + '</style>');
  });

  // building template of menu
  function buildTemplate() {
    if(template === null) {
      template = $('<div class="UIInfoContent"></div>')
        .append(
          $('<div class="ClearFix"></div>')
            .append('<div class="Avatar"></div>')
            .append(
              $('<div class="RightInfo"></div>')
                .append('<div class="FullName">FullName</div>')
                .append('<div class="Status">Status</div>')
                .append('<div><a class="More" href="javascript:void(0)">More<span class="LeftRowIcon"></span></a></div>')
            )
        )
        .append('<div class="Invite">Invite to connect</div>');
    }
    return template;
  }
  // hidden all menus
  function hideAllUserInfo(event) {
    if(display === false) {
      var allMenu = $(document.body).find('div.UIUserInfoMenu');
      allMenu.find('div.UIInfoContent').animate({height:'0px', width: '0px'}, 200, 'linear', function() {
        allMenu.html('');
      });
    }
  }
  //
  function UserInfo(jelm) {
    var userInfo = {
        jElmInfo: $(jelm),
        container : $('<div class="UIUserInfoMenu"></div>'),
        userId : "",
        avatarURL : "",
        profileURL : "",
        status : "",
        template : "",
        json : {fullName:"", activityTitle: "", avatarURL: "", relationshipType:""},
        restUrl : "",
        displayInvite : true,
        init : function(event) {
          userInfo.userId = userInfo.jElmInfo.attr('rel');
          var portal = eXo.social.portal;
          this.restUrl = 'http://' + window.location.host + portal.context + '/' + portal.rest 
                      + '/social/people' + '/getPeopleInfo/'+userInfo.userId+'.json';
          $.ajax({
            type: "GET",
            url: userInfo.restUrl,
            complete: function(jqXHR) {
              if(jqXHR.readyState === 4) {
                userInfo.json = $.parseJSON(jqXHR.responseText);
                if(userInfo.getRelationStatus() != 'NoInfo' && userInfo.getRelationStatus() != 'null') {
                  userInfo.buildMenu(event);
                  alert(userInfo.getRelationStatus())
                }
              }
            }
           });
        },
        getFullName : function() {
          return userInfo.json.fullName;
        },
        getStatus : function () {
          return userInfo.json.activityTitle;
        },
        getProfileURL : function () {
          return userInfo.jElmInfo.attr('href');
        },
        getAvatarURL : function() {
          return userInfo.json.avatarURL;
        },
        getRelationStatus : function() {
          return userInfo.json.relationshipType;
        },
        inviteUser : function(event) {
          var action = $(this).attr('action');
          $.ajax({
            type: "GET",
            url: userInfo.restUrl + '?updatedType=' + action,
            complete: function(jqXHR) {
              if(jqXHR.readyState === 4) {
                userInfo.json = $.parseJSON(jqXHR.responseText);
              }
            }
           });
          display = false;
          hideAllUserInfo(event);
        },
        buildMenu : function(event) {
          display = true;
          var template = buildTemplate();
          template.find('.FullName').html(userInfo.getFullName());
          template.find('.Status').html(userInfo.getStatus());
          template.find('.Avatar').css('background', userInfo.getAvatarURL());
          template.find('.More').attr('href', userInfo.getProfileURL());
          if(userInfo.getRelationStatus() != "NoAction") {
            template.find('.Invite').show().html( function () {
                var relationStatus = userInfo.getRelationStatus();
                var action = '<span title="Invite">Invite</span>';
                //
                if (relationStatus == "pending") { // Viewing is not owner
                  action = '<span title="Accept">Accept</span>';
                  action += '|';
                  action += '<span title="Deny">Deny</span>';
                } else if (relationStatus == "waiting") { // Viewing is owner
                  action = '<span title="Revoke">Revoke</span>';
                } else if (relationStatus == "confirmed") { // Had Connection 
                  action = '<span title="Disconnect">Disconnect</span>';
                } else if (relationStatus == "ignored") { // Connection is removed
                  action = '<span title="Invite">Invite</span>';
                }
                
                $(this).attr('action', relationStatus);
                return action;
              }
           ).off('click').on('click', userInfo.inviteUser);
          } else {
            template.find('.Invite').off('click').hide().html('');
          }
          
          var container = userInfo.jElmInfo.next();
          if(container.length == 0 || container.attr('id') != ('InfoMenuOf' + userInfo.userId)) {
            userInfo.container.attr('id', 'InfoMenuOf' + userInfo.userId).append(template);
            userInfo.container.on('mouseenter', function(event) {
              display = true;
              event.stopPropagation();
            }).on('mouseleave', function(event) {
              display = false;
              window.setTimeout(function () { hideAllUserInfo(event);}, 500);
              event.stopPropagation();
            });
            userInfo.container.insertAfter(userInfo.jElmInfo);
          } else {
            // show menu existing
            container.append(template);
          }
          template.css({height:'auto', width: 'auto'});
          var w = template.width();
          var h = template.height();
          template.animate({height: h + 'px', width: w + 'px'}, 500);
        }
    };
    return userInfo;
  }
  
  
  $.fn.showUserInfo = function() {
    $(this).on('mouseover', function(event) {
      var userInfo = new UserInfo($(this));
      userInfo.init(event);
      event.stopPropagation();
    }).on('mouseout', function(event) {
      display = false;
      window.setTimeout(function () {hideAllUserInfo(event);}, 500);
      event.stopPropagation();
    });
    return $(this);
  };
  
  
})(jQuery, document, window);
