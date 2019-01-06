var refreshSession = (function() {
    'use strict';

    var baseApiUrl = window.appConfig.options.mail.baseApiUrl;
    
    var getRefreshSession = function(url){
        window.location.href = 'hub://openUrl?url=' + encodeURIComponent(url);
        //window.open(window.location.href);
    }
    return{
        getRefreshSession:getRefreshSession
    }
})();