var storedService=null;
function openWSS() {
    //var self = this;
    console.log('hostAPI : opening WSS...', $.cookie('session_id'));
    var ws = new WebSocket('wss://localhost.ucclient.net:5506/channel?session_id=' + $.cookie('session_id'));
    ws.onopen = function (event) {
        console.log('hostAPI : ws onopen', event)
    }
    ws.onmessage = function (event) {
        console.log('hostAPI : ws onmessage : ' + JSON.stringify(event && event.data))
        var data = JSON.parse(event.data);
        if (data.notification && data.notification.event === 'APIVersion') {
            var apiVersion = data.notification.parameters && data.notification.parameters.version;
            if (apiVersion) {
                localStorage.apiVersion = parseFloat(apiVersion);
                console.log('hostAPI : apiVersion : ' + localStorage.apiVersion);
            }
        }
    }
    ws.onerror = function (event) {
        console.log('hostAPI : ws error', event);
    }
    ws.onclose = function (event) {
        console.log('hostAPI : ws close', event);
    }
}
function connect(serviceToGetSession) {
    storedService = serviceToGetSession;
    var apiUrl = window.appConfig.options.mail.btbcApiUrl+ new Date().getTime();
    //var uuid = $.uuid();
    var uuid = "5f5ea1d2-dd51-432c-81a7-7e52d2e1781a";
    //var uuid = '36a7869c-880d-4342-bb10-adac85dfc0f6';
    //var self = this;
    var config = {
        url: apiUrl,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        crossDomain: true,
        dataType: "json",
        data: JSON.stringify({
            uuid: uuid,
            version: '22.5',
            localized_strings: {
                en: {
                    name: 'OpenXchange Mail',
                    description: 'OpenXchange Mail API integration',
                    vendor: 'OpenXchange'
                }
            }
        })
    }
    console.log('hostAPI : connecting to host api...', config);
    $.ajax(config).then(function onLoad(response) {
        //storedService.setSessionId(response.session_id);
        $.cookie('session_id', response.session_id, { path: '/' });
        //alert($.cookie('session_id'));
        localStorage.sessionId = response.session_id;
        console.log('hostAPI : connecting to host api successful', localStorage.sessionId);
        openWSS();
        console.log("the service stored:",$.cookie('session_id'));
        return response;
    }, function (error) {
        //alert("error connection response>>> "+JSON.stringify(error));
        //error.then(function(err){
            if (error.data && error.data.session_id) {
                localStorage.sessionId = error.data.session_id;
                //storedService.setSessionId(error.data.session_id);
                //alert($.cookie('session_id'));
                console.log('hostAPI : connection exists and returning new session_id', localStorage.sessionId);
                openWSS();
            } else {
                console.log('hostAPI : connecting to host api failed', error);
            }
       // });
    });
}
function loadTab(params) {
    //alert($.cookie('session_id'))
    var apiUrl = window.appConfig.options.loadTabUrl;
    console.log('hostAPI : loading tab...', params);
    var url = apiUrl+"public/maildisp.html?auth="+params.authData+"&id="+params.id+"&folder="+params.folder;
    return request({cmd: 'LoadWebTab', url: encodeURIComponent(url), name: params.subject}).then(function onLoad(response){
        console.log('response from new tab', response);
    },function(error){
        console.log('error from new tab', error);
    });
  }

  function request(params) {
    //var self = this;
    // make sure to sync localStorage to get the latest sessionId
    //localStorage.$sync();
    //var uri = 'wss://localhost.ucclient.net:5506/channel?session_id='+mailService.getSessionId();
    var serialized = "&cmd="+params.cmd+"&url="+params.url+"&name="+params.name;
    var opts = {
      url: 'https://localhost.ucclient.net:5506/api?session_id='+$.cookie('session_id')+serialized
    }
    // HUB-6194 : use Hub-Token header on apiVersion >= 1.1
    // if (localStorage.apiVersion && localStorage.apiVersion >= 1.1) {
    //   opts.headers = {
    //     'Hub-Token': "0xf8e953f5"
    //   }
    // }
    console.log(opts);
    return $.ajax(opts);
  }
  connect();