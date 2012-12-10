/**
 * UIActivityUpdates.js
 */

var UIActivityUpdates = {
  numberOfUpdatedActivities: 0,
  cookieName: '',
  cookieValue: '',
  init: function (numberOfUpdatedActivities, cookieName, cookieValue) {
    //
    UIActivityUpdates.numberOfUpdatedActivities = numberOfUpdatedActivities;
    UIActivityUpdates.cookieName = cookieName;
    UIActivityUpdates.cookieValue = cookieValue;

    //
    $.each($('#UIActivitiesLoader').find('.UIActivity'), function(i, item) {
      if(i < numberOfUpdatedActivities) {
        $(item).addClass('UpdatedActivity');
      }
    });

    function isScrolledIntoView() {
      var docViewTop = $(window).scrollTop();
      var docViewBottom = docViewTop + $(window).height();
      var elem = $('#UIActivitiesLoader').find('.UpdatedActivity:last');
      if(elem.length > 0) {
        var elemTop = elem.offset().top;
        var elemBottom = elemTop + $(elem).height();
        return ((elemBottom >= docViewTop) && (elemTop <= docViewBottom));
      }
      return false;
    }

    $(window).on('scroll', function () {
      if (isScrolledIntoView()) {
        $('#UIActivitiesLoader').find('.UpdatedActivity').removeClass('UpdatedActivity');
        $('#numberInfo').html('No');
        //document.cookie = UIActivityUpdates.cookieName + "=" + UIActivityUpdates.cookieValue;
        eXo.core.Browser.setCookie(UIActivityUpdates.cookieName, UIActivityUpdates.cookieValue, 1);
        $(window).off('scroll');
      }
    });
  }
}

window.jq = $;
_module.UIActivityUpdates = UIActivityUpdates;
