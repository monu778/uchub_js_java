var mailObj = (function mailDisplay() {
    // Get the modal
    var modal = document.getElementById('myModal');
    var self = {};
    var seen = false;

    var baseApiUrl = window.appConfig.options.mail.baseApiUrl;
    var shouldUseMocks = window.appConfig.options.useMocks || false;
    var storedMailData = null;
    var isClickedTooltip = false;

    console.log('mail message>>>>>>', shouldUseMocks);

    var getMessage = function (auth, id, folder) {
        //console.log(auth + " "+id + " "+folder);	
        if (shouldUseMocks) {
            var getMessageRequest = $.ajax({
                url: 'test/9.json'
            });
        }
        else {
            var getMessageRequest = $.ajax({
                method: 'POST',
                url: baseApiUrl + 'mailservice/getmail',
                data: JSON.stringify({
                    id: id,
                    folder: folder,
                    auth: auth,
                    view: "html"
                }),
                dataType: 'json',
                contentType: "application/json"
            });
        }


        getMessageRequest.then(function (data) {
            //setMessageData(data.data);
            storedMailData = data.data;
            seen = true;
            var requestQuery = {
                "id": mailService.getMailDispId(),
                "folder": mailService.getMailDispFolder(),
                "seen": seen
            };
            console.log("mark read and unread request: ", requestQuery)
            //var returnPromise = mailService.markMailReadOrUnread(requestQuery);
            // returnPromise.then(function onLoad(data) {
            //     console.log("response from mark read or unread",data)
            // }, function onError(data, b, c) {
            //     console.log("ERROR getMail!", data, b, c);
            // });
        }, function onError(data, b, c) {
            console.log("ERROR getMessage!", data, b, c);
        });

        return getMessageRequest;
    }

    var forwardMail = function (reqParams) {
        //console.log("forwardMail: ",reqParams);
        var getMessageRequest = $.ajax({
            method: 'POST',
            url: baseApiUrl + 'mailservice/forwardmail',
            data: JSON.stringify(reqParams),
            dataType: 'json',
            contentType: "application/json"
        });

        getMessageRequest.then(function (data) {
            console.log("response forward mail: ", data)
        }, function onError(data, b, c) {
            console.log("ERROR getMessage!", data, b, c);
        });

        return getMessageRequest;
    }

    var replyMail = function (reqParams) {
        //console.log("replyMail: ",reqParams);
        var getMessageRequest = $.ajax({
            method: 'POST',
            url: baseApiUrl + 'mailservice/replymail',
            data: JSON.stringify(reqParams),
            dataType: 'json',
            contentType: "application/json"
        });
        getMessageRequest.then(function (data) {
            console.log("response forward mail: ", data)
        }, function onError(data, b, c) {
            console.log("ERROR getMessage!", data, b, c);
        });

        return getMessageRequest;
    }

    var replyAllMail = function (reqParams) {
        //console.log("replyMail: ",reqParams);
        var getMessageRequest = $.ajax({
            method: 'POST',
            url: baseApiUrl + 'mailservice/replyallmail',
            data: JSON.stringify(reqParams),
            dataType: 'json',
            contentType: "application/json"
        });
        getMessageRequest.then(function (data) {
            console.log("response forward mail: ", data)
        }, function onError(data, b, c) {
            console.log("ERROR getMessage!", data, b, c);
        });

        return getMessageRequest;
    }

    $(function () {
        //event listner for click on reply, replyAll and forward
        $('body').on({
            click: function () {
                $this = $(this);
                console.log("reply,replyall,forward", $this[0].id);
                var title = "";
                var toAddr = "";
                var ccAddr = "";
                $('.invalid-mail').hide();
                switch ($this[0].id) {
                    case 'Reply': title = "Reply Message"; toAddr = storedMailData.from[0][1]; break;
                    case 'ReplyAll': title = "Reply All"; toAddr = storedMailData.from[0][1];
                        var mailCC = '';
                        for (ele of storedMailData.to) {
                            mailCC += ele[1] + ",";
                        }
                        for (eachCc of storedMailData.cc) {
                            mailCC += eachCc[1] + ",";
                        }
                        ccAddr = mailCC; break;
                    case 'Forward': title = "Forward Message"; break;
                }
                modal.style.display = "block";
                $("#modalTitle").text(title);
                $("#messageRespond").val("");
                $("#toRespond").val(toAddr);
                $("#ccRespond").val(ccAddr);
                $(".word-wrap").parent().hide();
            },
        }, '#Reply, #ReplyAll, #Forward');

        $("#close").click(function () {
            modal.style.display = "none";
        });

        $("#myModal").dblclick(function (event) {
            event.preventDefault();
        });

        $('body').on({
            click: function () {
                //isClickedTooltip=true;
                //$('#allCcAddress').addClass('on');
                if (!isClickedTooltip) {
                    isClickedTooltip = true;
                    $('#allCcAddress').tooltip({
                        content: function () {
                            return $(this).prop('title');
                        }
                    }).tooltip("open");
                } else {
                    isClickedTooltip = false;
                    $('#allCcAddress').tooltip("close");
                }
                var tool = $('#allCcAddress').tooltip();
                tool.unbind('mouseenter mouseleave');
            },
            mouseenter: function (e) {
                if (isClickedTooltip) {
                    //isClickedTooltip=false;
                    e.preventDefault();
                }
            },
            mouseleave: function (e) {
                if (isClickedTooltip) {
                    //isClickedTooltip=false;
                    e.preventDefault();
                }
            }
        }, '#allCcAddress');

        function removeEmptyString(arr) {
            for (var i = 0; i < arr.length; i++) {
                if (arr[i] == "") {
                    arr.splice(i, 1);
                }
            }
            return arr;
        }

        $("#send").click(function () {
            modal.style.display = "none";
            console.log("forward mail: ", mailService.getAuthData().authData, mailService.getMailDispId());
            var messageEle = $("#messageRespond");
            var toEle = $("#toRespond");
            var ccEle = $("#ccRespond");
            var toEmailIds = toEle.val().split(",");
            var ccEmailIds = ccEle.val().split(",");
            toEmailIds = removeEmptyString(toEmailIds);
            ccEmailIds = removeEmptyString(ccEmailIds);
            console.log("forward mail: ", mailService.getAuthData().authData, mailService.getMailDispId(), messageEle.val(), toEmailIds, ccEmailIds);
            switch ($("#modalTitle").text()) {
                case "Reply Message":
                    if (toEmailIds.length == 0) {
                        modal.style.display = "block";
                        $('.invalid-mail').show();
                    }
                    else {
                        $('.invalid-mail').hide();
                        $.event.trigger("replyMail", { id: mailService.getMailDispId(), auth: mailService.getAuthData().authData, message: messageEle.val(), cc: ccEmailIds });
                    }
                    break;
                case "Reply All":
                    if (toEmailIds.length == 0) {
                        $('.invalid-mail').show();
                        modal.style.display = "block";
                    }
                    else {
                        $('.invalid-mail').hide();
                        $.event.trigger("replyAllMail", { id: mailService.getMailDispId(), auth: mailService.getAuthData().authData, message: messageEle.val(), to: toEmailIds, cc: ccEmailIds });
                    }
                    break;
                case "Forward Message":
                    if (toEmailIds.length == 0) {
                        $('.invalid-mail').show();
                        modal.style.display = "block";
                    }
                    else {
                        $('.invalid-mail').hide();
                        $.event.trigger("forwardMail", { id: mailService.getMailDispId(), auth: mailService.getAuthData().authData, message: messageEle.val(), to: toEmailIds, cc: ccEmailIds });
                    }
                    break;
            }
        });

        $('body').on({
            click: function () {
                $this = $(this);
                if ($this[0].id === 'icon-email-read') {
                    seen = true;
                    $(this).removeClass('icon-email-read flag').addClass('icon-email flag').attr({
                        id: "icon-email",
                        title: "Mark as unread"
                    });
                }
                else {
                    seen = false;
                    $(this).removeClass('icon-email flag').addClass('icon-email-read flag').attr({
                        id: "icon-email-read",
                        title: 'Mark as read'
                    });
                }
                var requestQuery = {
                    "id": mailService.getMailDispId(),
                    "folder": mailService.getMailDispFolder(),
                    "seen": seen
                };
                console.log("mark read and unread request: ", requestQuery)
                var returnPromise = mailService.markMailReadOrUnread(requestQuery);
                returnPromise.then(function onLoad(data) {
                    console.log("response from mark read or unread", data)
                    if (data.hasOwnProperty('error') && data.code == "SES-0203") {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': mailService.getAuthData().authData};
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                        }
                        //window.open("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth="+mailService.getAuthData().authData,'_blank');
                    }
                }, function onError(data, b, c) {
                    console.log("ERROR getMail!", data, b, c);
                });
            },
        }, '.iconBar .flag');

        //Each item archive click event
        $('body').on({
            click: function () {
                $this = $(this);
                var requestQuery = {
                    "id": mailService.getMailDispId(),
                    "folder": mailService.getMailDispFolder(),
                    "destinationFolder": "default0/Archive"
                };
                console.log("req params from archive: ", requestQuery)
                var returnUpdatePromise = mailService.updateMail(requestQuery);
                returnUpdatePromise.then(function onLoad(data) {
                    console.log("response from archive", data);
                }, function onError(data, b, c) {
                    console.log("ERROR getMail!", data, b, c);
                });
            },
        }, '.iconBar .archive');

        // When the user clicks anywhere outside of the modal, close it
        // window.onclick = function (event) {
        //     if (event.target == modal) {
        //         modal.style.display = "none";
        //     }
        // }
    });

    // Get all mail event listener
    $(document).on('forwardMail', function (event, param) {
        //console.log(param)
        forwardMail(param).then(function (data) {
            if (data.hasOwnProperty('error')) {
                var err = data.error.toLowerCase().indexOf("expired");
                if (err > -1) {
                    var reqParam = { 'auth': mailService.getAuthData().authData};
                    refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                }
            } else if (data.data) {
                console.log("response from forwardMail: ", data);
                $("#messageRespond").val("");
                $("#toRespond").val("");
                $("#ccRespond").val("");
            }
        });
    });

    // Get all mail event listener
    $(document).on('replyMail', function (event, param) {
        console.log(param)
        replyMail(param).then(function (data) {
            if (data.hasOwnProperty('error')) {
                var err = data.error.toLowerCase().indexOf("expired");
                if (err > -1) {
                    var reqParam = { 'auth': mailService.getAuthData().authData};
                    refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                }
            } else if (data.data) {
                console.log("response from forwardMail: ", data);
                $("#messageRespond").val("");
                $("#toRespond").val("");
                $("#ccRespond").val("");
            }
        });
    });

    // Get all mail event listener
    $(document).on('replyAllMail', function (event, param) {
        console.log(param)
        replyAllMail(param).then(function (data) {
            if (data.hasOwnProperty('error')) {
                var err = data.error.toLowerCase().indexOf("expired");
                if (err > -1) {
                    var reqParam = { 'auth': mailService.getAuthData().authData};
                    refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                }
            } else if (data.data) {
                console.log("response from forwardMail: ", data);
                $("#messageRespond").val("");
                $("#toRespond").val("");
                $("#ccRespond").val("");
            }
        });
    });

    return {
        getMessage: getMessage
    }
})();


