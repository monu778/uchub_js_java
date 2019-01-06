var mailService = (function() {

    'use strict';

    var shouldUseMocks = window.appConfig.options.useMocks || false;
    var baseApiUrl = window.appConfig.options.mail.baseApiUrl;
    console.log("global access",baseApiUrl);

    // Self is reference to traditional 'this' scope considering we lost access to 'this' by following JS revealing mobule pattern
    var self = {
        messageData: [],
        authData: '',
        deleteList: [],
        lastDeletedTimestamp: 1507563183000,
        finalDeleteData: [],
        session_id:'',
        mailDispId:'',
        mailDispFolder:''
    };

    var MAIL_FIELD_CODES = {
        color_label: 102,
        id: 600,
        folder_id: 601,
        attachment: 602,
        from: 603,
        to: 604,
        cc: 605,
        bcc: 606,
        subject: 607,
        size: 608,
        sent_date: 609,
        received_date: 610,
        flags: 611,
        level: 612, // Number	Zero-based nesting level in a thread.
        disp_notification_to: 613, // String	Content of message's header “Disposition-Notification-To”
        priority: 614, // Number	Value of message's “X-Priority” header. See X-Priority header.
        msg_ref: 615, // String	Message reference on reply/forward.
        flag_seen: 651,
        account_name: 652,
        account_id: 653,
        user: 'user', // Array	An array with user-defined flags as strings.
        headers: 'headers', // Object	An object with a field for every non-standard header. The header name is the field name. The header value is the value of the field as string.
        attachments: 'attachments', // Array	Each element is an attachment as described in Attachment. The first element is the mail text. If the mail has multiple representations (multipart-alternative), then the alternatives are placed after the mail text and have the field disp set to alternative.
        // nested_msgs	Array	Each element is a mail object as described in this table, except for fields id, folder_id and attachment.
        // truncated	boolean	true/false if the mail content was trimmed. Since v7.6.1
        // source	String	RFC822 source of the mail. Only present for action=get&attach_src=true
        cid: 'cid', //	String The value of the "Content-ID" header, if the header is present.
        original_id: 654, // String	The original mail identifier (e.g. if fetched from "virtual/all" folder).
        original_folder_id: 655, // String	The original folder identifier (e.g. if fetched from "virtual/all" folder).
        content_type: 656, // String	The Content-Type of a mail; e.g. multipart/mixed; boundary="-0123456abcdefg--".
        answered: 657, // String	Special field to sort mails by answered status.
        forwarded: 658, // String	Special field to sort mails by forwarded status. Note that mail service needs either support a system flag or a $Forwarded user flag
        draft: 659, // String	Special field to sort mails by draft flag.
        flagged: 660, // String	Special field to sort mails by flagged status.
        date: 661
    };

    var MAIL_FLAGS = {
        0x001: 'answered',
        0x002: 'deleted',
        0x004: 'draft',
        0x008: 'flagged',
        0x010: 'recent',
        0x020: 'seen',
        0x040: 'user',
        0x080: 'spam',
        0x100: 'forwarded'
    };

    var setMailDispId = function (data) {
        self.mailDispId = data;
    }

    var getMailDispId = function () {
        return self.mailDispId;
    }

    var setMailDispFolder = function (data) {
        self.mailDispFolder = data;
    }

    var getMailDispFolder = function () {
        return self.mailDispFolder;
    }

    var setSessionId = function(data){
        self.session_id = data;
    }

    var getSessionId = function(){
        return self.session_id;
    }

    var setAuthData =  function(data){
       self.authData = data;
    }

    var getAuthData = function(){
        return self;
    }

    var setMessageData = function(data) {
        self.messageData.push(data);
    }

    var getMessageData = function() {
        console.log('getMessageData>>>>>', self.messageData);
        return self.messageData.shift();
    }

    var getMessageParser = function(messageHeader) {
        return function(self) {
            var data = self.data;
            console.log('getMessageParser>>>>', data);
            var body;
            if(data.isInvite) {
                messageHeader.isInvite = data.isInvite;
            }

            messageHeader.flagged = data.flagged;
            
            if(data.sequenceId) {
                messageHeader.sequenceId = data.sequenceId;
            }
            
            for (var i in data.attachments || []) {
                if (!body || data.attachments[i].content_type == 'text/plain')
                    {
                        if(data.attachments[i].content) {
                            body = data.attachments[i].content;
                        }
                    }
                    
            }

            messageHeader.body = body || "<i>(no message) </i>";

            if (Autolinker !== undefined && $.isFunction(Autolinker.link)) {
                messageHeader.body = Autolinker.link(messageHeader.body);
            }
        };
    }

    var getMail = function(passedReuestConfig,activeTab) {
        var apiUrl = null;
        switch(activeTab){
            case "Unread":apiUrl = baseApiUrl+'mailservice/allmails';break;
            case "Today":apiUrl = baseApiUrl+'mailservice/allmails';break;
            case "Archive":apiUrl = baseApiUrl+'mailservice/allfoldermails';break;
            case "All":apiUrl = baseApiUrl+'mailservice/allfoldermails';break;
            case "Inbox":apiUrl = baseApiUrl+'mailservice/allmails';break;
        }
        console.log("apiUrl>>",apiUrl,activeTab)
        //var keys = 'id,original_id,folder_id,to,from,subject,flags,size,attachment,received_date,msg_ref,headers,attachments'.split(',');
		//var keys = 'id,folder_id,original_id,from,to,size,subject,attachment,msg_ref,headers,received_date,flags,attachments'.split(',');
		var keys = 'id,folder_id,attachment,from,to,cc,bcc,subject,size,sent_date,received_date,flags,level,priority,attachments'.split(',');

        var codes = [];
        for (var i in keys) {
            codes.push(MAIL_FIELD_CODES[keys[i]]);
        }
        
        var promise = new $.Deferred();

        if (shouldUseMocks) {
            var getMailPromise = $.ajax({
                url: 'test/all.json'
            });
            
        } else {
			var authdata = getAuthData();
			
			var tempReqData2 =  {
				auth: getAuthData().authData
            };

            passedReuestConfig.auth = getAuthData().authData;
            console.log('mail service>>>>>',passedReuestConfig);
            var getMailPromise = $.ajax({
                 type: "POST",
                 crossDomain: true,
                 url:apiUrl,
                 data: JSON.stringify(passedReuestConfig),
                 dataType: "json",
                 contentType : "application/json"
                });
        }

        getMailPromise.then(function onLoad(data) {
            if(data.hasOwnProperty('error')){
                var err = data.error.toLowerCase().indexOf("expired");
                    if(err > -1){
                        var reqParam = { 'auth': getAuthData().authData};
                        refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth="+reqParam.auth+"&type=mail",'_blank');
                    }
            }else if(data.data){
                var i, j, row;
                var messages = [];
                var promises = [];

                for (i in data.data) {
                    row = data.data[i];
                    var messageHeader = data.data[i];
                    messageHeader.index =  (+i) + 1 ;

                    // for (j in row) {
                    //     messageHeader[keys[j]] = row[j];
                    // }
                    console.log("testing",messageHeader);
                    promises.push(getMessage(messageHeader.id || messageHeader.index, messageHeader.folder_id).then(getMessageParser(messageHeader, i)));

                    var encodedFlags = messageHeader.flags || 0;
                    console.log(encodedFlags);
                    for (var flag in MAIL_FLAGS) {
                        if (encodedFlags & flag)
                            messageHeader[MAIL_FLAGS[flag]] = true;
                    }
                    console.log(messageHeader);
                    messages.push(messageHeader);

                }
                $.when.apply($, promises).then(function () {
                    promise.resolve(messages);
                });          
            }
        }, function onError(data, b, c) {
            console.log("ERROR getMail!", data, b, c);
            promise.fail(data);
        });

        return promise;
    }

    var getFolderInfo = function() {

        if (shouldUseMocks) {
            var getFolderInfoRequest = $.ajax({
                url: '../../test/mail/count.json'
            });
        } else {
            var getFolderInfoRequest = $.ajax({
                method: 'POST',
                url: baseApiUrl+'mailservice/getmail',
                data: JSON.stringify({
                    action: 'count',
                    folder: 'default0/INBOX',
                    auth: getAuthData().authData,
                }),
                dataType: 'json',
                contentType : "application/json"
            });
        }
        getFolderInfoRequest.then(function onLoad(data) {
            if(data.hasOwnProperty('error')){
                var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1){
                        var reqParam = { 'auth': getAuthData().authData};
                        refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth="+reqParam.auth+"&type=mail",'_blank');
                    }
            }
        }, function onError(data, b, c) {
            console.log("ERROR getFolderInfo!", data, b, c);
        });

        return getFolderInfoRequest;
    }

    var getMessage = function(id, folder) {

        if (shouldUseMocks) {
            var getMessageRequest = $.ajax({
                url: 'test/' + id + '.json'
            });
        } else {
            var getMessageRequest = $.ajax({
                method: 'POST',
                url: baseApiUrl+'mailservice/getmail',
                data: JSON.stringify({
                    id: id,
                    folder: folder,
                    auth: getAuthData().authData,
                    unseen: true
                }),
                dataType: 'json',
                contentType : "application/json"
            });
        }

        getMessageRequest.then(function(data) {
            if(data.hasOwnProperty('error')){
                var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1){
                        var reqParam = { 'auth': getAuthData().authData};
                        refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth="+reqParam.auth+"&type=mail",'_blank');
                    }
            }else if(data.data){
                setMessageData(data.data);
            }
            console.log("!getMessageRequest", data.data);
        }, function onError(data, b, c) {
            console.log("ERROR getMessage!", data, b, c);
        });

        return getMessageRequest;
    }


    var setToDeleteMessage = function(selectedObject) {
        self.deleteList.push(selectedObject);
        return  self.deleteList;
    }

    var triggerDeleteRequest = function(finalRequestData) {
        if(shouldUseMocks) {
            //$.event.trigger("getAllMail", [{folder:"default0/Inbox",auth:getAuthData().authData}]);
            console.log('Mail has been deleted');
        }
        else
        {
            var tempArray = {}; 
            tempArray['deleteList'] = finalRequestData;
            tempArray['auth'] = getAuthData().authData;
            //tempArray['timestamp'] = self.lastDeletedTimestamp;
            var sendDeleteRequest = $.ajax({
                method: 'POST',
                url:baseApiUrl+'mailservice/deletemail',
                data: JSON.stringify(tempArray),
                dataType: 'json',
                contentType : "application/json"
            });
            sendDeleteRequest.then(function(data) {
                //$.event.trigger("getAllMail", [{folder:"default0/Inbox",auth:getAuthData().authData}]);
                console.log("!delete request response", data.data);
            }, function onError(data, b, c) {
                console.log("ERROR deleteMessage!", data, b, c);
            });
            return sendDeleteRequest;
        }  
    }

    //Search Mail Service
    var searchMail = function(passedObject) {
        var keys = 'id,folder_id,attachment,from,to,cc,bcc,subject,size,sent_date,received_date,flags,level,priority,attachments'.split(',');

        var codes = [];
        for (var i in keys) {
            codes.push(MAIL_FIELD_CODES[keys[i]]);
        }
        
        var promise = new $.Deferred();

        if(shouldUseMocks) {
            console.log(passedObject);
            var searchRequest = $.ajax({
                url: 'test/all.json'
            });
        }
        else {
            var tempSearchRequest = passedObject;
            tempSearchRequest['auth'] = getAuthData().authData;
            //tempSearchRequest['searchString'] = searchString;
            //console.log(tempSearchRequest);
            var searchRequest = $.ajax({
                method: 'POST',
                crossDomain: true,
                url:baseApiUrl+'mailservice/searchmails',
                data: JSON.stringify(tempSearchRequest),
                dataType: 'json',
                contentType : "application/json"
            });
        }
        searchRequest.then(function onLoad(data) {
            if(data.hasOwnProperty('error')){
                var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1){
                        var reqParam = { 'auth': getAuthData().authData};
                        refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth="+reqParam.auth+"&type=mail",'_blank');
                    }
            }else if(data.data){
                var i, j, row;
                var messages = [];
                var promises = [];

                for (i in data.data) {
                    row = data.data[i];
                    var messageHeader = data.data[i];
                    messageHeader.index = (+i) + 1;

                    promises.push(getMessage(messageHeader.id || messageHeader.index, messageHeader.folder_id).then(getMessageParser(messageHeader, i)));

                    var encodedFlags = messageHeader.flags || 0;
                    console.log(encodedFlags);
                    for (var flag in MAIL_FLAGS) {
                        if (encodedFlags & flag)
                            messageHeader[MAIL_FLAGS[flag]] = true;
                    }
                    console.log(messageHeader);
                    messages.push(messageHeader);

                }
                $.when.apply($, promises).then(function () {
                    promise.resolve(messages);
                });
            }
        }, function onError(data, b, c) {
            console.log("ERROR getMail!", data, b, c);
            promise.fail(data);
        });

        return promise; 
    }


    //mark read or unread
    var markMailReadOrUnread = function(passesRequestConfig) {
        passesRequestConfig.auth = getAuthData().authData;
        if (shouldUseMocks) {
            var markReadUnreadRequest = $.ajax({
                url: 'test/all.json'
            });   
        } else {
            var markReadUnreadRequest = $.ajax({
                method: 'POST',
                crossDomain: true,
                url:baseApiUrl+'mailservice/markReadUnread',
                data: JSON.stringify(passesRequestConfig),
                dataType: 'json',
                contentType : "application/json"
            });
        }
        return markReadUnreadRequest;
    }

    var updateMail = function(passesRequestConfig) {
        passesRequestConfig.auth = getAuthData().authData;
        if (shouldUseMocks) {
            var updateMailRequest = $.ajax({
                url: 'test/all.json'
            });   
        } else {
            var updateMailRequest = $.ajax({
                method: 'POST',
                crossDomain: true,
                url:baseApiUrl+'mailservice/updatemail',
                data: JSON.stringify(passesRequestConfig),
                dataType: 'json',
                contentType : "application/json"
            });
        }
        return updateMailRequest; 
    }

    var updateMailInvitation = function(requestData) {
        requestData.auth = getAuthData().authData;
        if (shouldUseMocks) {
            var updateMailInvitationRequest = $.ajax({
                url: 'test/all.json'
            });   
        } else {
            var updateMailInvitationRequest = $.ajax({
                method: 'POST',
                crossDomain: true,
                url:baseApiUrl+'mailservice/updateinvite',
                data: JSON.stringify(requestData),
                dataType: 'json',
                contentType : "application/json"
            });
        }
        return updateMailInvitationRequest; 
    }

    return {
        getFolderInfo: getFolderInfo,
        getMail: getMail,
        getMessage: getMessage,
        setAuthData: setAuthData,
        setToDeleteMessage: setToDeleteMessage,
        triggerDeleteRequest: triggerDeleteRequest,
        searchMail: searchMail,
        markMailReadOrUnread: markMailReadOrUnread,
        setSessionId:setSessionId,
        getSessionId:getSessionId,
        updateMail: updateMail,
        getAuthData:getAuthData,
        setMailDispId:setMailDispId,
        getMailDispId:getMailDispId,
        setMailDispFolder:setMailDispFolder,
        getMailDispFolder:getMailDispFolder,
        updateMailInvitation:updateMailInvitation
    }

})();