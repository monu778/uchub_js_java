
(function appClosure() {
    $parent = $('#widget');
    $mail = $('<div/>').addClass('mailHolder').appendTo($parent);
    $messageShowMore = '';
    var baseApiUrl = window.appConfig.options.mail.baseApiUrl;
    var activeTab = 'Emails';
    var iconBarHTML = '<div class="icon-email flag"></div><div class="icon-archive archive"></div><div class="icon-trash delete"></div>';
    var startIndex = 0;
    var endIndex = 15;
    var loadpagination = 1;
    var reqParams = {};
    var DATE_FORMAT = 'dddd MMM D';

    var TIME_FORMAT = 'dddd MMM D h:mm A';
    var END_TIME = 'h:mm A';
    var noMoreDataHTML = '---All items displayed---';
    var searchNullHTML = '<div id="searchNull"><i class="icon-search"></i><span>No items were returned in your search.</span></div>';
    var loadingImageHTML = '<img class="loading" src="common/images/loading.gif"/>';
    //$('.loadingGif').hide();

    function getHeaderView(hdr, prevDate) {
        //$('.noMoreData').fadeOut();
        console.log('getHeaderView>>>', hdr);
        var $view = $('<div/>').addClass('messageHolder');
        var statusToUpdate = hdr.mailType;
        var statusDOM = '';
        switch (statusToUpdate) {
            case "Sent in to": statusDOM = $('<div class="statusBlue"><span>-></span>Sent</div>'); break;
            case "Received in to": statusDOM = $('<div class="status"><span><-</span>Received</div>'); break;
            case "Received in cc": statusDOM = $('<div class="status"><span><-</span>Received CC</div>'); break;
            case "Sent in cc": statusDOM = $('<div class="statusBlue"><span>-></span>Sent CC</div>'); break;
        }
        var $actualMailCard = $('<div/>').addClass('mailCard');

        var timestamp = hdr.received_date;
        var prevStr = prevDate ? moment(prevDate).format(DATE_FORMAT) || "" : "";
        var dateStr = moment(timestamp).format(DATE_FORMAT) || "";

        var today = moment().format(DATE_FORMAT);
        var msgHeader = ''

        var mailDate = moment(new Date(moment(timestamp).utcOffset(0).format('YYYY MM DD').split(/\s+/).join(",")));
        var currentDate = moment(new Date(moment().utcOffset(0).format('YYYY MM DD').split(/\s+/).join(",")));
        var no_days = currentDate.diff(mailDate, 'days');
        var preMailDate = prevDate ? moment(new Date(moment(prevDate).format('YYYY MM DD').split(/\s+/).join(","))) || "" : "";

        var pre_no_days = preMailDate ? currentDate.diff(preMailDate, 'days') || "" : 0;

        if (prevStr != dateStr) {
            $('<div/>').addClass('datestamp').text(dateStr).appendTo($view);
        }
        //console.log("varsha: ",moment(timestamp).utcOffset(0).format(TIME_FORMAT).substr(16,moment(timestamp).utcOffset(0).format(TIME_FORMAT).length-1))
        var $timeStampRec = $('<div/>').addClass('timestamp timeReceived').text(moment(timestamp).utcOffset(0).format(END_TIME) || "");
        var footerContent = $(statusDOM);
        var $mailFooterHolder = $('<div/>').addClass('mailCardFooter').html(footerContent).append($timeStampRec);
        var $messageHolderDOM = $('<div/>').addClass('messageHolderDOM').appendTo($actualMailCard)
        var $mailIconOnBar = $('<div/>').addClass('mailIcon').appendTo($messageHolderDOM);
        var $mailIcon = $('<div/>').addClass('icon-email').appendTo($mailIconOnBar);
        if (hdr.seen) {
            var $message = $('<div/>').addClass('message').attr('id', hdr.id).appendTo($messageHolderDOM);
        }
        else {
            var $message = $('<div/>').addClass('message unseen').attr('id', hdr.id).appendTo($messageHolderDOM);
        }
        var $timestamp = $("<input type='hidden' value=" + timestamp + " />").attr('id', 'timestamp_' + hdr.id).appendTo($message);
        var $folder = $("<input type=\"hidden\"  value=" + hdr.folder_id + " />").attr('id', 'folder_id_' + hdr.id).appendTo($message);
        if (hdr.sequenceId) {
            var $sequence = $("<input type=\"hidden\"  value=" + hdr.sequenceId + " />").attr('id', 'sequenceid_' + hdr.id).appendTo($message);
        }
        var $iconBarHolder = $('<div/>').addClass('iconBarHolder').prependTo($message);
        var $iconBar = $('<div/>').addClass('iconBar').appendTo($iconBarHolder);

        if (hdr.isInvite) {
            var $invitationTickIcon = $('<div/>').addClass('icon-tick invitationApprove').attr({ title: "Accept" }).appendTo($iconBar);
            var $invitationCrossIcon = $('<div/>').addClass('icon-cross invitationDecline').attr({ title: "Decline" }).appendTo($iconBar);
            var $invitationTentitiveIcon = $('<div/>').addClass('icon-tentative invitationTentative').attr({ title: "Tentative" }).appendTo($iconBar);
        }

        if (hdr.seen) {
            var $emailIcon = $('<div/>').addClass('icon-email flag').attr({
                id: "icon-email",
                title: "Mark as unread"
            }).appendTo($iconBar);
        }
        else {
            var $emailIcon = $('<div/>').addClass('icon-email-read flag').attr({
                id: "icon-email-read",
                title: 'Mark as read'
            }).appendTo($iconBar);
        }

        if (!hdr.isInvite) {
            var $archiveIcon = $('<div/>').addClass('icon-archive archive').appendTo($iconBar);
            var $deleteIcon = $('<div/>').addClass('icon-trash delete').appendTo($iconBar);
        }
        if(hdr.flagged){
            var $flaggedIcon = $('<div class="fa fa-bookmark important" aria-hidden="true"></div>').attr({
                id: "important-email_"+hdr.id,
                title: "Mark as unimportant",
                "data-id": hdr.id,
                "data-folderId":hdr.folder_id,
                "data-flagged":"true"

            }).appendTo($iconBar);
        }else{
            var $flaggedIcon = $('<div class="fa fa-bookmark-o important" aria-hidden="true"></div>').attr({
                id: "unimportant-email_"+hdr.id,
                title: "Mark as important",
                "data-id": hdr.id,
                "data-folderId":hdr.folder_id,
                "data-flagged":"false"
            }).appendTo($iconBar);
        }

        var $messageHeader = $('<div/>').addClass('messageHeader').appendTo($message);
        $('<div/>').addClass('subject bolder sender').attr({ "data-id": hdr.id, 'data-folder': hdr.folder_id }).text(hdr.subject || "").appendTo($messageHeader);
        var $messageBody = $('<div/>').addClass('messageBody').html(hdr.body || "").appendTo($messageHeader);
        $messageShowMore = $messageBody;
        $($actualMailCard).appendTo($view);
        $($mailFooterHolder).appendTo($messageHolderDOM);
        return $view;
    }

    //This is for attachment View
    function getAttachmentView(hdr, prevDate) {
        var $view = $('<div/>').addClass('messageHolder');
        var timestamp = hdr.received_date;
        var prevStr = prevDate ? moment(prevDate).format(DATE_FORMAT) || "" : "";
        var dateStr = moment(timestamp).format(DATE_FORMAT) || "";

        var today = moment().format(DATE_FORMAT);
        var msgHeader = ''

        var mailDate = moment(new Date(moment(timestamp).utcOffset(0).format('YYYY MM DD').split(/\s+/).join(",")));
        var currentDate = moment(new Date(moment().utcOffset(0).format('YYYY MM DD').split(/\s+/).join(",")));
        var no_days = currentDate.diff(mailDate, 'days');
        var preMailDate = prevDate ? moment(new Date(moment(prevDate).format('YYYY MM DD').split(/\s+/).join(","))) || "" : "";

        var pre_no_days = preMailDate ? currentDate.diff(preMailDate, 'days') || "" : 0;

        if (prevStr != dateStr) {
            $('<div/>').addClass('datestamp').text(dateStr).appendTo($view);
        }

        var $timeStampRec = $('<div/>').addClass('timestamp timeReceived').text(moment(timestamp).utcOffset(0).format(END_TIME) || "");
        var mailAttachmentLogoHTML = '<div style="height:37%"></div><div class="clearfix bshadow0 pbs icon-con-mail"><span class="icon-email red-color con-icon"></span></div><div style="height:37%"></div>';
        var $actualMailCard = $('<div/>').addClass('mailCard');
        var $messageHolderDOM = $('<div/>').addClass('messageHolderDOM').appendTo($actualMailCard);
        var $mailIconOnBar = $('<div/>').addClass('mailIcon').appendTo($messageHolderDOM);
        var $mailIcon = $('<div/>').addClass('icon-attachment icon-rotate-45').appendTo($mailIconOnBar);
        if (hdr.seen) {
            var $message = $('<div/>').addClass('message').attr('id', hdr.id).appendTo($messageHolderDOM);
        }
        else {
            var $message = $('<div/>').addClass('message unseen').attr('id', hdr.id).appendTo($messageHolderDOM);
        }
        var $timestamp = $("<input type='hidden' value=" + timestamp + " />").attr('id', 'timestamp_' + hdr.id).appendTo($message);
        var $folder = $("<input type=\"hidden\"  value=" + hdr.folder_id + " />").attr('id', 'folder_id_' + hdr.id).appendTo($message);

        var $iconBarHolder = $('<div/>').addClass('iconBarHolder').prependTo($message);
        var $iconBar = $('<div/>').addClass('iconBar hidden').appendTo($iconBarHolder);
        // var externallinkIcon = $('<div/>').addClass('icon-externallink externallink').appendTo($iconBar);
        var $messageHeader = $('<div/>').addClass('messageHeader attachmentHeader').appendTo($message);
        $('<div/>').addClass('subject bolder sender').attr({ "data-id": hdr.id, 'data-folder': hdr.folder_id }).text(hdr.subject || "").appendTo($messageHeader);
        var $messageBody = $('<div/>').addClass('messageBody').appendTo($messageHeader);
        if (hdr.attachments && hdr.attachments.length) {
            for (var i = 0; i < hdr.attachments.length; i++) {
                var fileType = hdr.attachments[i].filename.split(".");
                fileType = fileType[fileType.length - 1];
                var filename = hdr.attachments[i].filename;
                var id = hdr.attachments[i].id;
                var apiURL = baseApiUrl + 'mailservice/downloadattachment/' + hdr.id + "/" + id + "/" + filename + "?auth=" + ContextualMailService.getAuthData() + "&folderid=" + hdr.folder_id;
                var huburl = "hub://openUrl?url=" + encodeURIComponent(apiURL);
                //  var ahref_link = "<a href='javascript:void(0);' onclick='"+ContextualMailService.open(apiURL)+"'>" + filename + "</a>";
                // var ahref_link  = "<a href='javascript:void(0);' onclick=\"open('"+apiURL+"')\">" + filename + "</a>";
                var ahref_link = "<a href=" + huburl + ">" + filename + "</a>";
                console.log(ahref_link);
                switch (fileType) {
                    case 'pdf': var filesContent = $('<div/>').addClass('filecontent icon-file-pdf').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                    case 'png': var filesContent = $('<div/>').addClass('filecontent icon-file-img').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                    case 'jpg': var filesContent = $('<div/>').addClass('filecontent icon-file-img').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                    case 'zip': var filesContent = $('<div/>').addClass('filecontent icon-file-archive').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                    case 'txt': var filesContent = $('<div/>').addClass('filecontent icon-file-text').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                    case 'doc': var filesContent = $('<div/>').addClass('filecontent icon-file-doc').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                    case 'docx': var filesContent = $('<div/>').addClass('filecontent icon-file-doc').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                    case 'xls': var filesContent = $('<div/>').addClass('filecontent icon-file-xls').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                    case 'xlsx': var filesContent = $('<div/>').addClass('filecontent icon-file-xls').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                    case 'ppt': var filesContent = $('<div/>').addClass('filecontent icon-file-ppt').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                    case 'pptx': var filesContent = $('<div/>').addClass('filecontent icon-file-ppt').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                    default: var filesContent = $('<div/>').addClass('filecontent icon-file-generic').attr('id', id).html(ahref_link).appendTo($messageBody); break;
                }
            }
        }

        var $mailFooterHolder = $('<div/>').addClass('mailCardFooter').html($timeStampRec).appendTo($messageHolderDOM);
        $($actualMailCard).appendTo($view);
        return $view;
    }

    function putShowMore(messageBody) {
        var element = messageBody[0];
        if ((element.offsetHeight < element.scrollHeight)) {
            // your element have overflow
            var $showMoreHolder = $('<div/>').addClass('showMoreHolder').appendTo($(messageBody).parent().parent());
            var $showMore = $('<div/>').addClass('hidden showMore').appendTo($showMoreHolder);
        }
    }

    function open(url) {
        window.location.href = "hub://openUrl?url=" + encodeURIComponent(url);
    }

    //on load scroll pagination event trigger and get all mail event
    $(document).ready(function () {
        //,email:ContextualMailService.getEmailId()
        reqParams = { auth: ContextualMailService.getAuthData(), value: ContextualMailService.getEmailId(), leftHandLimit: startIndex.toString(), rightHandLimit: endIndex.toString() };
        //alert('Auth Data:'+ContextualMailService.getAuthData());
        //alert('Email ID:'+ContextualMailService.getEmailId());
        $.event.trigger("getAllContextualMail", reqParams);
        var sticky = navbar.offsetTop;
        $('#widget').bind('scroll', function () {
            /*console.log(window.pageYOffset);
            console.log(sticky);*/
            if (window.pageYOffset >= sticky) {
                $('#navbar').addClass("sticky");
            } else {
                $('#navbar').removeClass("sticky");
            }
            var position = $('#widget').scrollTop();
            var bottom = $(document).height() - $('#widget').height();
            /*console.log('document ready>>>', bottom);
            console.log('document ready>>>', Math.round(position));*/

            /*if (Math.round(position) >= bottom && loadpagination == 1) {
                console.log(position + " " + bottom + " " + $(document).height() + "   " + $(window).height());
                $.event.trigger({
                    type: "getPaginationLoader"
                });
            }*/
            /*console.log('this scrollHeight>>>', $(this)[0].scrollHeight);
            console.log('this scrollTop>>>', $(this).scrollTop());
            console.log('this outerHeight>>>', $(this).outerHeight());
            console.log('difference height and top>>>>', $(this)[0].scrollHeight - Math.round($(this).scrollTop()));*/
            if ($(this)[0].scrollHeight - Math.round($(this).scrollTop()) == $(this).outerHeight()) {
                console.log('what you want to do ...');
                if($(".noMoreData").length > 0) {
                    $(".noMoreData").html(loadingImageHTML);
                }
                else {
                    $('<div/>').addClass('noMoreData').html(loadingImageHTML).appendTo($parent);
                }
                $.event.trigger({
                    type: "getPaginationLoader"
                });
            }
        });
    });



    $(function () {
        $('body').on({
            click: function () {
                $this = $(this);
                $('.subTab').removeClass('selected');
                $this.addClass('selected');
                activeTab = $(this).text();
                startIndex = 0;
                endIndex = 15;
                $('#widget').scrollTop(0);
                $('.noMoreData').remove();
                
                $(".mail-box-btn")[0].children[0].innerHTML = activeTab;
                $('.subitem').removeClass('selected');
                if (activeTab === 'Emails') {
                    $('.mail-box-list li:nth-child(1)').addClass('selected');
                    reqParams = { auth: ContextualMailService.getAuthData(), value: ContextualMailService.getEmailId(), leftHandLimit: startIndex, rightHandLimit: endIndex, onlyAttachments: false };
                    if($('#BroadSearch').val() && $('#BroadSearch').val().length >= 3) {
                        reqParams.search = $('#BroadSearch').val();
                    }
                   
                    $.event.trigger("getAllContextualMail", reqParams);
                }
                else {
                    $('.mail-box-list li:nth-child(2)').addClass('selected');
                    reqParams = { auth: ContextualMailService.getAuthData(), value: ContextualMailService.getEmailId(), leftHandLimit: startIndex, rightHandLimit: endIndex, onlyAttachments: true };
                    if($('#BroadSearch').val() && $('#BroadSearch').val().length >= 3) {
                        reqParams.search = $('#BroadSearch').val();
                    }
                    $.event.trigger("getAllContextualMailAttachments", reqParams);
                    
                }
            },
        }, '.subTab');

        //On list mouse enter show more and show less 
        $('body').on({
            mouseenter: function () {
                $(this).find(" .showMore, .showLess").toggleClass('hidden');
            },
            mouseleave: function () {
                $(this).find(" .showMore, .showLess").toggleClass('hidden');
            }
        }, '.mailCard');

        //Show more and show less click event
        $('body').on({
            click: function () {
                $this = $(this);
                if ($this[0].classList[0] === 'showLess') {
                    $this.parent().siblings(".messageHeader").find(".messageBody").scrollTop(0);
                }
                $this.parent().siblings(".messageHeader").find(".messageBody").toggleClass('expanded');
                $this.parent().siblings(".messageHeader").parent().siblings(".mailIcon").toggleClass('expanded');
                $this.toggleClass('showMore').toggleClass('showLess');
            },
        }, '.showMore, .showLess');

        $('body').on({
            click: function () {
                $this = $(this);
                if ($this[0].id === 'icon-email-read') {
                    $('#' + $this.parent().parent().parent()[0].id).removeClass("message unseen").addClass("message");
                    $(this).removeClass('icon-email-read flag').addClass('icon-email flag').attr({
                        id: "icon-email",
                        title: "Mark as unread"
                    });
                    var seen = true;
                }
                else {
                    var seen = false;
                    $('#' + $this.parent().parent().parent()[0].id).removeClass("message").addClass("message unseen");
                    $(this).removeClass('icon-email flag').addClass('icon-email-read flag').attr({
                        id: "icon-email-read",
                        title: 'Mark as read'
                    });
                }
                var requestQuery = {
                    "id": $this.parent().parent().parent()[0].id,
                    "folder": $('input#folder_id_' + $this.parent().parent().parent()[0].id).val(),
                    "seen": seen
                };
                var returnPromise = ContextualMailService.markMailReadOrUnread(requestQuery);
                returnPromise.then(function onLoad(data) {
                    if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': ContextualMailService.getAuthData()};
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                        }
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
                    "id": $this.parent().parent().parent()[0].id,
                    "folder": $('input#folder_id_' + $this.parent().parent().parent()[0].id).val(),
                    "destinationFolder": "default0/Archive"
                };
                var returnUpdatePromise = ContextualMailService.updateMail(requestQuery);
                returnUpdatePromise.then(function onLoad(data) {
                    startIndex = 0;
                    endIndex = 15;
                    //var reqParams = { auth: ContextualMailService.getAuthData(), value: ContextualMailService.getEmailId() };
                    if (data && data.data) {
                        //var reqParams = { folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex };
                        //$.event.trigger("getAllContextualMail", reqParams);
                        if ($this.parent().parent().parent().parent().parent().parent().has('.datestamp').length > 0) {
                            //$('#' + $this.parent().parent().parent().parent().parent()[0].id).siblings(".showMoreHolder").remove();
                            if ($this.parent().parent().parent().parent().parent().parent().next().has('.datestamp').length > 0) {
                                $($this.parent().parent().parent().parent().parent()).remove();
                                location.reload();
                            }
                            else {
                                if ($this.parent().parent().parent().parent().parent().parent().next().length > 0) {
                                    $($this.parent().parent().parent().parent().parent()).remove();
                                }
                                else {
                                    $($this.parent().parent().parent().parent().parent()).remove();
                                    location.reload();
                                }
                            }

                        }
                        else {
                            if ($this.parent().parent().parent().parent().parent().parent().next().length > 0) {
                                if ($this.parent().parent().parent().parent().parent().parent().prev().has('.datestamp').length > 0 && $this.parent().parent().parent().parent().parent().parent().next().has('.datestamp').length > 0) {
                                    if ($this.parent().parent().parent().parent().parent().parent().prev().has('.mailCard').length > 0 && $this.parent().parent().parent().parent().parent().parent().next().has('.mailCard').length > 0) {
                                        $this.parent().parent().parent().parent().parent().parent().remove();
                                    }
                                    else {
                                        $this.parent().parent().parent().parent().parent().parent().remove();
                                        location.reload();
                                    }
                                }
                                else {
                                    $this.parent().parent().parent().parent().parent().parent().remove();
                                }
                            } else {
                                if ($this.parent().parent().parent().parent().parent().parent().prev().has('.datestamp').length > 0 && $this.parent().parent().parent().parent().parent().parent().prev().has('.mailCard ').length > 0) {
                                    $this.parent().parent().parent().parent().parent().parent().remove();
                                }
                                else {
                                    $this.parent().parent().parent().parent().parent().parent().remove();
                                    location.reload();
                                }
                            }

                        }
                    } else if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': ContextualMailService.getAuthData()};
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                        }
                    }
                }, function onError(data, b, c) {
                    console.log("ERROR getMail!", data, b, c);
                });
            },
        }, '.iconBar .archive');

        //Each item archive click event
        $('body').on({
            click: function () {
                $this = $(this);
                var allData = $this[0].dataset;
                var requestQuery = {
                    "id": allData.id,
                    "folder": allData.folderid
                };
                requestQuery.flagged = allData.flagged == "true"?false:true;

                var returnUpdatePromise = ContextualMailService.updateMail(requestQuery);
                returnUpdatePromise.then(function onLoad(data) {
                    if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': ContextualMailService.getAuthData()};
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                        }
                    } else if (data.data) {
                        console.log($this);
                        if(allData.flagged == "true"){
                            $("#important-email_"+allData.id).removeClass("fa fa-bookmark important").addClass("fa fa-bookmark-o important").attr({
                                id: "unimportant-email_"+allData.id,
                                title: "Mark as important",
                                "data-id": allData.id,
                                "data-folderId":allData.folderid,
                                "data-flagged":"false"
            
                            });
                        }else{
                            $("#unimportant-email_"+allData.id).removeClass("fa fa-bookmark-o important").addClass("fa fa-bookmark important").attr({
                                id: "important-email_"+allData.id,
                                title: "Mark as unimportant",
                                "data-id": allData.id,
                                "data-folderId":allData.folderid,
                                "data-flagged":"true"
                            });
                        }
                        //$('#' + $this.parent().parent().parent()[0].id).remove();
                    }
                }, function onError(data, b, c) {
                    console.log("ERROR getMail!", data, b, c);
                });
            },
        }, '.iconBar .important');

        //click of search cross icon
        $('body').on({
            click: function () {
                $(this).removeClass('icon-cross-thin-filled').addClass('icon-search');
                $('#BroadSearch').val("");
                
                startIndex = 0;
                endIndex = 15;
                var reqParams = { auth: ContextualMailService.getAuthData(), value: ContextualMailService.getEmailId(), leftHandLimit: startIndex.toString(), rightHandLimit: endIndex.toString() };
                if (activeTab === 'Emails') {
                    reqParams.onlyAttachments = false
                    $.event.trigger("getAllContextualMail", reqParams);
                } else {
                    reqParams.onlyAttachments = true
                    $.event.trigger("getAllContextualMailAttachments", reqParams);
                }   
            },
        }, '.icon-cross-thin-filled');

        //Each item delete click event
        $('body').on({
            click: function () {
                $this = $(this);
                var selectedMessage = {
                    "folder": $('input#folder_id_' + $this.parent().parent().parent()[0].id).val(),
                    "id": $this.parent().parent().parent()[0].id,
                    "timestamp": $('input#timestamp_' + $this.parent().parent().parent()[0].id).val()
                };
                var deleteList = ContextualMailService.setToDeleteMessage(selectedMessage);
                if (deleteList.length > 0) {
                    var returnData = ContextualMailService.triggerDeleteRequest(deleteList);
                    returnData.then(function onLoad(data) {
                        if (data && data.data) {
                            // $($this.parent().parent().parent().parent().parent()).remove();
                            if ($this.parent().parent().parent().parent().parent().parent().has('.datestamp').length > 0) {
                                //$('#' + $this.parent().parent().parent().parent().parent()[0].id).siblings(".showMoreHolder").remove();
                                if ($this.parent().parent().parent().parent().parent().parent().next().has('.datestamp').length > 0) {
                                    $($this.parent().parent().parent().parent().parent()).remove();
                                    location.reload();
                                }
                                else {
                                    if ($this.parent().parent().parent().parent().parent().parent().next().length > 0) {
                                        $($this.parent().parent().parent().parent().parent()).remove();
                                    }
                                    else {
                                        $($this.parent().parent().parent().parent().parent()).remove();
                                        location.reload();
                                    }
                                }

                            }
                            else {
                                if ($this.parent().parent().parent().parent().parent().parent().next().length > 0) {
                                    if ($this.parent().parent().parent().parent().parent().parent().prev().has('.datestamp').length > 0 && $this.parent().parent().parent().parent().parent().parent().next().has('.datestamp').length > 0) {
                                        if ($this.parent().parent().parent().parent().parent().parent().prev().has('.mailCard').length > 0 && $this.parent().parent().parent().parent().parent().parent().next().has('.mailCard').length > 0) {
                                            $this.parent().parent().parent().parent().parent().parent().remove();
                                        }
                                        else {
                                            $this.parent().parent().parent().parent().parent().parent().remove();
                                            location.reload();
                                        }
                                    }
                                    else {
                                        $this.parent().parent().parent().parent().parent().parent().remove();
                                    }
                                } else {
                                    if ($this.parent().parent().parent().parent().parent().parent().prev().has('.datestamp').length > 0 && $this.parent().parent().parent().parent().parent().parent().prev().has('.mailCard ').length > 0) {
                                        $this.parent().parent().parent().parent().parent().parent().remove();
                                    }
                                    else {
                                        $this.parent().parent().parent().parent().parent().parent().remove();
                                        location.reload();
                                    }
                                }

                            }
                        } else if (data.hasOwnProperty('error')) {
                            var err = data.error.toLowerCase().indexOf("expired");
                            if (err > -1) {
                                var reqParam = { 'auth': ContextualMailService.getAuthData()};
                                refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                            }
                        }
                    });
                }
            },
        }, '.iconBar .delete');

        // click of each mail
        $('body').on({
            click: function () {
                //if (activeTab === 'Emails') {
                $this = $(this);
                console.log("for load:");
                var allData = $this[0].dataset;
                console.log("id and folder and subject", allData.id, allData.folder, $(this)[0].innerText);
                var promise = loadTab({
                    authData: ContextualMailService.getAuthData(),
                    id: allData.id,
                    folder: allData.folder,
                    subject: $(this)[0].innerText
                });
                promise.then(function onLoad(res) {
                    console.log(res);
                });
                //}
            },
        }, '.subject');

        //Click on invitation approve confirmation: 1
        $('body').on({
            click: function () {
                $this = $(this);
                var approveRequest = {
                    objectId: $this.parent().parent().parent()[0].id,
                    folderId: $('input#folder_id_' + $this.parent().parent().parent()[0].id).val(),
                    confirmation: "1",
                    confirmationMessage: "",
                    sequenceId: $('input#sequenceid_' + $this.parent().parent().parent()[0].id).val()
                };
                var approveRequestPromise = ContextualMailService.updateMailInvitation(approveRequest);
                approveRequestPromise.then(function (data) {
                    if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': ContextualMailService.getAuthData()};
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                        }
                    } else if (data.data) {
                        $($this.parent().parent().parent().parent().parent()).remove();
                    }
                }, function onError(data, b, c) {
                    console.log("ERROR Approve!", data, b, c);
                });

            },
        }, '.invitationApprove');

        //Click on invitation decline confirmation: 2
        $('body').on({
            click: function () {
                $this = $(this);
                var declineRequest = {
                    objectId: $this.parent().parent().parent()[0].id,
                    folderId: $('input#folder_id_' + $this.parent().parent().parent()[0].id).val(),
                    confirmation: "2",
                    confirmationMessage: "",
                    sequenceId: $('input#sequenceid_' + $this.parent().parent().parent()[0].id).val()
                };
                var declineRequestPromise = ContextualMailService.updateMailInvitation(declineRequest);
                declineRequestPromise.then(function (data) {
                    $($this.parent().parent().parent().parent().parent()).remove();
                }, function onError(data, b, c) {
                    console.log("ERROR Decline!", data, b, c);
                });
                //

            },
        }, '.invitationDecline');


        $('body').on({
            click: function () {

                var url = "https://orangehubservices.mpsvcs.com/hubservices/rest/mailservice/downloadattachment/153/2/RogersKT.txt?auth=" + ContextualMailService.getAuthData() + "&folderid=default0/Sent";

                //window.location.href =  "https://orangehubservices.mpsvcs.com/hubservices/public/open.html?url="+encodeURIComponent(url);
                window.location.href = "hub://openUrl?url=" + encodeURIComponent(url);


            },
        }, '.alink');


        //Click on invitation tentetive confirmation: 3
        $('body').on({
            click: function () {
                $this = $(this);
                var tentativeRequest = {
                    objectId: $this.parent().parent().parent()[0].id,
                    folderId: $('input#folder_id_' + $this.parent().parent().parent()[0].id).val(),
                    confirmation: "3",
                    confirmationMessage: "",
                    sequenceId: $('input#sequenceid_' + $this.parent().parent().parent()[0].id).val()
                };
                var tentativeRequestPromise = ContextualMailService.updateMailInvitation(tentativeRequest);
                tentativeRequestPromise.then(function (data) {
                    $($this.parent().parent().parent().parent().parent()).remove();
                }, function onError(data, b, c) {
                    console.log("ERROR Tentative!", data, b, c);
                });
            },
        }, '.invitationTentative');

        //Show more and show less click event
        $('body').on({
            click: function () {
                $this = $(this);
                var requestData = {
                    "auth": ContextualMailService.getAuthData(),
                    "id": $this.attr('id'),
                    "mailId": $this.parent().parent().parent()[0].id,
                    "folder": $('input#folder_id_' + $this.parent().parent().parent()[0].id).val(),
                    "filename": $this.text()
                }

                var apiURL = baseApiUrl + 'mailservice/downloadattachment/' + "INBOX" + "/" + requestData.mailId + "/" + requestData.id + "/" + requestData.filename;
                $.ajax({
                    method: 'GET',
                    crossDomain: true,
                    url: apiURL,
                    //data: JSON.stringify(requestData),
                    beforeSend: function (request) {
                        request.setRequestHeader("auth", requestData.auth);
                    },
                    success: function (data) {
                        console.log("varsha:", data)
                        var url = URL.createObjectURL(data);
                        var $a = $('<a />', {
                            'href': url,
                            'download': requestData.filename,
                            'text': "click"
                        }).hide().appendTo("body")[0].click();
                    },
                    error: function (err) {
                        console.log(err);
                    }
                });
                /* var downloadAttachmentPromise = ContextualMailService.downloadAttachment(requestData);
                 downloadAttachmentPromise.then(function(data) {
                     console.log("varsha:",data)
                     var url = URL.createObjectURL(data);
                     var $a = $('<a />', {
                         'href': url,
                         'download': $this.text(),
                         'text': "click"
                     }).hide().appendTo("body")[0].click();
                 }, function onError(data, b, c) {
                     console.log("ERROR Tentative!", data, b, c);
                 });*/
            }
        }, '.filecontentTest');
    });

    //Event for pagination loader
    $(document).on('getPaginationLoader', function (event) {
        console.log('getPaginationLoader');
        startIndex = endIndex;
        endIndex += 5;
        //setTimeout(function(){
        reqParams.leftHandLimit = startIndex;
        reqParams.rightHandLimit = endIndex;

        if (activeTab == "Emails") {
            reqParams.onlyAttachments = false;
            promiseCall = ContextualMailService.getContextualMail(reqParams);
        } else {
            reqParams.onlyAttachments = true;
            promiseCall = ContextualMailService.getContextualMail(reqParams);
        }
        promiseCall.then(function onload(data) {
            //data = [];
            console.log(data);
            if (data.length > 0) {
                $('.noMoreData').remove();
                var prevDate;
                for (var i in data) {
                    if (activeTab == "Emails") {
                        getHeaderView(data[i], prevDate).appendTo($mail);
                    } else {
                        getAttachmentView(data[i], prevDate).appendTo($mail);
                    }
                    prevDate = data[i].received_date;
                }
                if(data.length<5 && $(".noMoreData").length == 0){
                    $('<div/>').addClass('noMoreData').html(noMoreDataHTML).appendTo($parent);
                }

            } else {
                //$('.noMoreData').show();
                if($(".noMoreData").length > 0) {
                    $(".noMoreData").html(noMoreDataHTML);
                }
                loadpagination = 0;
                event.preventDefault();
                //alert("Was preventDefault() called: " + event.isDefaultPrevented());

                //div no data event trigger
            }
        });
    });

    //Get -- no more items -- messages at the last set of data
    $(document).on('getNoMoreItems', function (event) {
        $('.noMoreData').show();
        $('.loadingGif').hide();
    });

    // Get all mail event listener
    $(document).on('getAllContextualMail', function (event, param1) {
        $('.noMoreData').remove();
        $mail.empty();
        $('.spinningGif').show();
        console.log("dfsfdsfds");
        ContextualMailService.getContextualMail(param1).then(function onload(data) {
            if (data && data.length > 0) {
                $("#widget").css({"display": "block"});
                $('.spinningGif').hide();
                var prevDate;
                for (var i in data) {
                    getHeaderView(data[i], prevDate).appendTo($mail);
                    putShowMore($messageShowMore);
                    prevDate = data[i].received_date;
                }
                if(data.length<15 && $(".noMoreData").length == 0){
                    $('<div/>').addClass('noMoreData').html(noMoreDataHTML).appendTo($parent);  
                }
            }
            else {
                //$("#widget").css({"display": "none"});
                //$('.noMoreData').show();
                if( $(".noMoreData").length == 0) {
                    if($('#BroadSearch').val() && $('#BroadSearch').val().length >= 3) {
                        $('<div/>').addClass('noMoreData').html(searchNullHTML).appendTo($parent);
                    }
                    else {
                        $('<div/>').addClass('noMoreData').html(noMoreDataHTML).appendTo($parent);
                    }
                    
                }
                else {
                    if($('#BroadSearch').val() && $('#BroadSearch').val().length >= 3) {
                        $(".noMoreData").html(searchNullHTML);
                    }
                    else {
                        $(".noMoreData").html(noMoreDataHTML);
                    }
                    
                }
                $('.spinningGif').hide();
            }

        });
    });

    //Get all mail attachments event listener
    $(document).on('getAllContextualMailAttachments', function (event, param1) {
        $('.noMoreData').remove();
        $mail.empty();
        $('.spinningGif').show();
        ContextualMailService.getContextualMail(param1).then(function onload(data) {
            if (data && data.length > 0) {
                $("#widget").css({"display": "block"});
                $('.spinningGif').hide();
                var prevDate;
                for (var i in data) {
                    getAttachmentView(data[i], prevDate).appendTo($mail);
                    prevDate = data[i].received_date;
                }
                if(data.length<15 && $(".noMoreData").length == 0){
                    $('<div/>').addClass('noMoreData').html(noMoreDataHTML).appendTo($parent);  
                }
            }
            else {
                //$("#widget").css({"display": "none"});
                //$('.noMoreData').show();
                if( $(".noMoreData").length == 0) {
                    if($('#BroadSearch').val() && $('#BroadSearch').val().length >= 3) {
                        $('<div/>').addClass('noMoreData').html(searchNullHTML).appendTo($parent);
                    }
                    else {
                        $('<div/>').addClass('noMoreData').html(noMoreDataHTML).appendTo($parent);
                    }
                    
                }
                else {
                    if($('#BroadSearch').val() && $('#BroadSearch').val().length >= 3) {
                        $(".noMoreData").html(searchNullHTML);
                    }
                    else {
                        $(".noMoreData").html(noMoreDataHTML);
                    }
                    
                }
                $('.spinningGif').hide();
            }

        });
    });

    $('#BroadSearch').keyup(function (e) {
        var key = e.which;
        $this = $(this);
        console.log(key,$this[0].value.length)

        if($this[0].value.trim().length != 0 && key== 8) {        
            $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
        }
        if ($this[0].value.length == 0 && key == 8) {
            startIndex = 0;
            endIndex = 15;
            $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search'); 
            console.log(activeTab)
            switch (activeTab) {
                case "Emails":
                    console.log("sdsff")
                    reqParams ={ auth: ContextualMailService.getAuthData(), value: ContextualMailService.getEmailId(), leftHandLimit: startIndex.toString(), rightHandLimit: endIndex.toString() };
                    $.event.trigger("getAllContextualMail", reqParams);
                    break;
                case "Attachments":
                    reqParams = { "onlyAttachments":true, auth: ContextualMailService.getAuthData(), value: ContextualMailService.getEmailId(), leftHandLimit: startIndex.toString(), rightHandLimit: endIndex.toString() };
                    $.event.trigger("getAllContextualMailAttachments", reqParams);
                    break;
                default:
                    console.log("No active tab")
            }
        }
        if (key == 13) {
            startIndex = 0;
            endIndex = 15;
            $('#searchNull').hide();
            $('.noMoreData').remove();
            if ($this[0].value.length >= 3) {
                $('.searchHolder i').removeClass('icon-search').addClass('icon-cross-thin-filled');
                $mail.empty();
                switch (activeTab) {
                    case "Emails":
                        var reqParams = { "auth": ContextualMailService.getAuthData(), "value": ContextualMailService.getEmailId(), "search": $this[0].value, leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "Attachments":
                        var reqParams = { "auth": ContextualMailService.getAuthData(), "value": ContextualMailService.getEmailId(), "onlyAttachments":true,"search": $this[0].value, leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                }
                ContextualMailService.getContextualMail(reqParams).then(function onload(data) {
                    if (data && data.length > 0) {
                        $('.spinningGif').hide();
                        var prevDate;
                        for (var i in data) {
                            //getHeaderView(data[i], prevDate).appendTo($mail);
                            if (activeTab == "Emails") {
                                getHeaderView(data[i], prevDate).appendTo($mail);
                            } else {
                                getAttachmentView(data[i], prevDate).appendTo($mail);
                            }
                            putShowMore($messageShowMore);
                            prevDate = data[i].received_date;
                        }
                        if(data.length<15 && $(".noMoreData").length == 0){
                            $('<div/>').addClass('noMoreData').html(noMoreDataHTML).appendTo($parent);  
                        }
                    }
                    else {
                        //$("#widget").css({"display": "none"});
                       // $('#searchNull').show();
                       if($(".noMoreData").length == 0) {
                            $('<div/>').addClass('noMoreData').html(searchNullHTML).appendTo($parent);
                        }
                        else{
                            $(".noMoreData").html(searchNullHTML);
                        } 
                        $('.spinningGif').hide();
                        //$('.noMoreData').show();
                    }

                });
            }
            else {
                $('.searchHolder i').removeClass('icon-search').addClass('icon-cross-thin-filled');
            }
        }
    });

    $('body').on({
        click: function () {
            $('.mail-box-list').toggle();
            if ($('.mail-box-btn :nth-child(2)').hasClass("icon-arrow-down")) {
                $('.mail-box-btn :nth-child(2)').removeClass("icon-arrow-down");
                $('.mail-box-btn :nth-child(2)').addClass("icon-arrow-up");
            } else {
                $('.mail-box-btn :nth-child(2)').removeClass("icon-arrow-up");
                $('.mail-box-btn :nth-child(2)').addClass("icon-arrow-down");
            }
        },
    }, '.mail-box-btn');

    $('.mail-box-list li:nth-child(1)').addClass('selected');
    //change of Unread or Today
    $('body').on({
        click: function () {
            $('.mail-box-list').hide();
            $('.mail-box-btn :nth-child(2)').removeClass("icon-arrow-up");
            $('.mail-box-btn :nth-child(2)').addClass("icon-arrow-down");
            $('.subTab').removeClass('selected');
            $this = $(this);
            $('.subitem').removeClass('selected');
            $this.addClass('selected');
            var tabValue = $this[0].childNodes[0].data;
            $(".mail-box-btn")[0].children[0].innerHTML = tabValue;
            startIndex = 0;
            endIndex = 15;
            $('.noMoreData').remove();
            $('#widget').scrollTop(0);

            if (tabValue === 'Emails') {
                $('.subTabs a:nth-child(1)').addClass('selected');
                reqParams = { auth: ContextualMailService.getAuthData(), value: ContextualMailService.getEmailId(), leftHandLimit: startIndex, rightHandLimit: endIndex, onlyAttachments: false };
                if($('#BroadSearch').val() && $('#BroadSearch').val().length >= 3) {
                    reqParams.search = $('#BroadSearch').val();
                }
                $.event.trigger("getAllContextualMail", reqParams);
            }
            else {
                $('.subTabs a:nth-child(2)').addClass('selected');
                reqParams = { auth: ContextualMailService.getAuthData(), value: ContextualMailService.getEmailId(), leftHandLimit: startIndex, rightHandLimit: endIndex, onlyAttachments: true };
                if($('#BroadSearch').val() && $('#BroadSearch').val().length >= 3) {
                    reqParams.search = $('#BroadSearch').val();
                }
                $.event.trigger("getAllContextualMailAttachments", reqParams);
            }

        },
    }, '.subitem');


})();
