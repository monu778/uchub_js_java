var driveService = (function() {

    'use strict';

    var shouldUseMocks = window.appConfig.options.useMocks || false;
    var baseApiUrl = window.appConfig.options.drive.baseApiUrl;
    console.log("global access",baseApiUrl);

    // Self is reference to traditional 'this' scope considering we lost access to 'this' by following JS revealing mobule pattern
    var self = {
        authData: '',
        emailId:''
    };

    var setAuthData =  function(data){
        self.authData = data;
     }
 
     var getAuthData = function(){
         return self.authData;
     }

     var setEmailId =  function(data){
        self.emailId = data;
     }
 
     var getEmailId = function(){
         return self.emailId;
     }
     
     
     var getAllDriverFiles = function(reqParams){
        if (shouldUseMocks) {
            var getDrivePromise = $.ajax({
                url: 'test/drive/all.json'
            });   
        } else {
            reqParams.toUserName = getEmailId();
            var getDrivePromise = $.ajax({
                method: "POST",
                crossDomain: true,
                url:baseApiUrl+"driveservice/contextualdata",
                data: JSON.stringify(reqParams),
                dataType: "json",
                contentType : "application/json"
               });
        }
        getDrivePromise.then(function onLoad(data) {
            if(Array.isArray(data.data.listOfFiles)){
                if(data.data.listOfFiles.length == 0 && data.data.listOfFolder.length == 0){
                    $.event.trigger({
                        type: "showNoMoreDataDiv"
                    });
                }
            }
            console.log("drive response",data);
        }, function onError(data, b, c) {
            console.log("ERROR getDrive!", data, b, c);
            //promise.resolve(data);
        });
        return getDrivePromise;
     }

     var uploadFile = function(formData){
         var uploadDriveFilePromise = $.ajax({
            method: "POST",
            crossDomain: true,
            url:baseApiUrl + "driveservice/uploaddrivefile",
            contentType: false,
            processData: false,
            data: formData
           });
           uploadDriveFilePromise.then(function onLoad(data) {

           }, function onError() {
            console.log("ERROR uploadFile!");
        });
        return uploadDriveFilePromise;
    }
    
     var sharedWithMeDriverFiles = function(reqParams){
        if (shouldUseMocks) {
            var getSharedDrivePromise = $.ajax({
                url: 'test/drive/all.json'
            });   
        }
        else {
            var getSharedDrivePromise = $.ajax({
                method: "POST",
                crossDomain: true,
                url:baseApiUrl+"driveservice/sharedwithme",
                data: JSON.stringify(reqParams),
                dataType: "json",
                contentType : "application/json"
            });
        }
        return getSharedDrivePromise;
     }

     var deleteDriverFiles = function(reqParams){
        var reqParamsArray = []; 
        reqParams.toUserName = getEmailId();
        reqParamsArray.push(reqParams);
        var deleteDrivePromise = $.ajax({
            method: "POST",
            crossDomain: true,
            url:baseApiUrl+"driveservice/deletefile",
            data: JSON.stringify(reqParamsArray),
            dataType: "json",
            contentType : "application/json"
        });      
        return deleteDrivePromise;
     }

    var getShareLink = function(reqParams){
        reqParams.toUserName = getEmailId();
        var getShareLink = $.ajax({
            method: "POST",
            crossDomain: true,
            url:baseApiUrl+"driveservice/getdocumentlink",
            data: JSON.stringify(reqParams),
            dataType: "json",
            contentType : "application/json"
        });          
        return getShareLink;
     }

     var sendShareLink = function(reqParams){
        var sendShareLink = $.ajax({
            method: "POST",
            crossDomain: true,
            url:baseApiUrl+"driveservice/sharedocument",
            data: JSON.stringify(reqParams),
            dataType: "json",
            contentType : "application/json"
           });  
        return sendShareLink;
     }

     var recentDriverFiles = function(reqParams){
        var recentDrivePromise = $.ajax({
            method: "POST",
            crossDomain: true,
            url:baseApiUrl+"driveservice/recentfiles",
            data: JSON.stringify(reqParams),
            dataType: "json",
            contentType : "application/json"
           });
           recentDrivePromise.then(function onLoad(data) {
        }, function onError(data, b, c) {
            console.log("ERROR getDrive!", data, b, c);
            //promise.resolve(data);
        });
        return recentDrivePromise;
     }

     var searchDriverFiles = function(reqParams){
        reqParams.toUserName = getEmailId();
        var searchDrivePromise = $.ajax({
            method: "POST",
            crossDomain: true,
            url:baseApiUrl+"driveservice/contextualdata",
            data: JSON.stringify(reqParams),
            dataType: "json",
            contentType : "application/json"
        });          
        return searchDrivePromise;
     }

     var downloadAttachment = function(requestData) {
        if (shouldUseMocks) {
            var downloadRequest = $.ajax({
                url: 'test/all.json'
            });   
        } else {
            var apiURL = baseApiUrl+'driveservice/downloadfile/'+requestData.fileName+"/"+requestData.version+"/"+requestData.createdUserId+"/"+requestData.timestamp+"?auth="+requestData.auth+"&fileObjectID="+requestData.fileObjectID+"&folderid="+requestData.folder;
            var downloadRequest = $.ajax({
                method: 'GET',
                crossDomain: true,
                url:"hub://openUrl?url="+encodeURIComponent(apiURL),
                //data: JSON.stringify(requestData),
                beforeSend: function (request) {
                    request.setRequestHeader("auth", requestData.auth);
                },
                success:function(data){
                    console.log("downloadFile:",data)
                    if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': driveService.getAuthData()};
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=drive", '_blank');
                        }    
                    } else {
                        var url = URL.createObjectURL(data);
                        var $a = $('<a />', {
                            'href': url,
                            'download': requestData.filename,
                            'text': "click"
                        }).hide().appendTo("body")[0].click();
                    }
                },
                error: function (err) {
                    console.log(err);
                }
            });
        }
        return downloadRequest; 
    }

    // var syncDriveFolders = function() {
    //     return $.ajax({
    //         method: 'GET',
    //         url: hostUrl + '/drive',
    //         data: {
    //             action: 'syncfolders',
    //             session: sessionId,
    //             root: ''
    //         },
    //         dataType: 'json',
    //         xhrFields: {
    //             withCredentials: true
    //         }
    //     }).then(function onLoad(data) {
    //         return data.data;
    //     }, function onError(data, b, c) {
    //         console.log("ERROR getMessage!", data, b, c);
    //     });
    // }

    return {
        //syncDriveFolders: syncDriveFolders,
        setAuthData:setAuthData,
        getAuthData:getAuthData,
        getAllDriverFiles:getAllDriverFiles,
        uploadFile:uploadFile,
        sharedWithMeDriverFiles:sharedWithMeDriverFiles,
        deleteDriverFiles:deleteDriverFiles,
        getShareLink:getShareLink,
        sendShareLink:sendShareLink,
        recentDriverFiles:recentDriverFiles,
        searchDriverFiles:searchDriverFiles,
        downloadAttachment:downloadAttachment,
        setEmailId:setEmailId,
        getEmailId:getEmailId
    }

})();