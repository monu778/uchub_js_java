
(function appClosure() {
    $parent = $('#widget');
    $mail = $('<div/>').addClass('mailHolder').appendTo($parent);
    var activeTab = 'Inbox';
    $messageShowMore = '';
    var $viewShowMore = '';

    var DATE_FORMAT = 'ddd MMM D';
    var TIME_FORMAT = 'ddd MMM D h:mm A';

    var todayTrigger = false;
    var countSeven = 0;
    var countZero = 0;
    var countOne = 0;
    var startIndex = 0;
    var endIndex = 15;
    var loadpagination = 1;
    var tabValue = "Inbox";
    var reqParams = {};

    var iconBarHTML = '<div class="icon-email flag"></div><div class="icon-archive archive"></div><div class="icon-trash delete"></div>';

    function getHeaderView(hdr, prevDate) {
        //$('.noMoreData').fadeOut();
        console.log('getHeaderView>>>', hdr);
        var $view = $('<div/>').addClass('messageHolder');
        var from = hdr.from[0][0] || hdr.from[0][1];
        var timestamp = hdr.received_date;
        var prevStr = prevDate ? moment(prevDate).format(DATE_FORMAT) || "" : "";
        var dateStr = moment(timestamp).format(DATE_FORMAT) || "";
        
        var today = moment().format(DATE_FORMAT);
        if (today) {
            console.log("dsfsfg")
            $.event.trigger({
                type: "showNoMoreDataDiv"
            });
        }
        var msgHeader = ''

        var mailDate = moment(new Date(moment(timestamp).utcOffset(0).format('YYYY MM DD').split(/\s+/).join(",")));
        var currentDate = moment(new Date(moment().utcOffset(0).format('YYYY MM DD').split(/\s+/).join(",")));
        var no_days = currentDate.diff(mailDate, 'days');
        var preMailDate = prevDate ? moment(new Date(moment(prevDate).format('YYYY MM DD').split(/\s+/).join(","))) || "" : "";

        var pre_no_days = preMailDate ? currentDate.diff(preMailDate, 'days') || "" : 0;
        apiURL = "https://orangehubservices.mpsvcs.com/hubservices/public/microapp.html#mock"
        huburl="hub://openUrl?url="+encodeURIComponent(apiURL);
        var ahref_link = "<a href=" +  apiURL + " target='_blank'>" + from + "</a>";

        if (todayTrigger) {
            if (no_days == 0) {
                msgHeader = 'Today'
                countZero++;
                if (countZero == 1)
                    $('<div/>').addClass('datestamp today').text(msgHeader).appendTo($view);
                if (hdr.seen) {
                    var $message = $('<div/>').addClass('message').attr('id', hdr.id).appendTo($view);
                }
                else {
                    var $message = $('<div/>').addClass('message unseen').attr('id', hdr.id).appendTo($view);
                }
                var $timestamp = $("<input type='hidden' value=" + hdr.timestamp + " />").attr('id', 'timestamp_' + hdr.id).appendTo($message);
                var $folder = $("<input type=\"hidden\"  value=" + hdr.folder_id + " />").attr('id', 'folder_id_' + hdr.id).appendTo($message);
                if (hdr.sequenceId) {
                    var $sequence = $("<input type=\"hidden\"  value=" + hdr.sequenceId + " />").attr('id', 'sequenceid_' + hdr.id).appendTo($message);
                }
                var $messageHeader = $('<div/>').addClass('messageHeader').attr({ "data-id": hdr.id }).appendTo($message);
                var $senderHolder = $('<div/>').addClass('senderHolder').appendTo($messageHeader);

                $('<div/>').addClass('timestamp').text(moment(timestamp).utcOffset(0).format(TIME_FORMAT) || "").appendTo($senderHolder);
                var $sender = $('<div/>').addClass('sender').text(from || "").attr("title", from).appendTo($senderHolder);
                $('<div/>').addClass('subject').text(hdr.subject || "").appendTo($messageHeader);
               // var $messageBody = $('<div/>').addClass('messageBody').html(hdr.body || "").appendTo($messageHeader);
                //$messageShowMore = $messageBody;

                var $noteBody = $('<div/>').addClass('noteBody').attr({'id':'noteBody_'+hdr.id}).html(hdr.body || "").appendTo($messageHeader);
                $messageShowMore = $noteBody;

                var $iconBarHolder = $('<div/>').addClass('iconBarHolder').prependTo($message);
                var $iconBar = $('<div/>').addClass('iconBar').appendTo($iconBarHolder);
                if (hdr.isInvite) {
                    var $invitationTickIcon = $('<div/>').addClass('icon-tick invitationApprove').attr({ title: "Accept" }).appendTo($iconBar);
                    var $invitationCrossIcon = $('<div/>').addClass('icon-cross invitationDecline').attr({ title: "decline" }).appendTo($iconBar);
                    var $invitationTentitiveIcon = $('<div/>').addClass('icon-tentative invitationTentative').attr({ title: "tentative" }).appendTo($iconBar);
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

                if (activeTab !== 'Archive' && !hdr.isInvite) {
                    var $archiveIcon = $('<div/>').addClass('icon-archive archive').attr({
                        title: 'Archive'
                    }).appendTo($iconBar);
                }

                if (!hdr.isInvite) {
                    var $deleteIcon = $('<div/>').addClass('icon-trash delete').attr({
                        title: 'Delete'
                    }).appendTo($iconBar);
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
                //var $showMoreHolder = $('<div/>').addClass('showMoreHolder').appendTo($view);
                //var $showMore = $('<div/>').addClass('hidden showMore').appendTo($showMoreHolder);
                $viewShowMore = $view;
                return $view;
            }
        }
        else {
            if (no_days == 0) {
                msgHeader = 'Today'
                countZero++;
                if (countZero == 1)
                    $('<div/>').addClass('datestamp today').text(msgHeader).appendTo($view);

            } else if (no_days == 1) {
                msgHeader = 'Yesterday'
                countOne++;
                if (countOne == 1)
                    $('<div/>').addClass('datestamp yesterday').text(msgHeader).appendTo($view);
            } else if (no_days > 1) {
                msgHeader = 'Last 7 days'
                countSeven++;
                if (countSeven == 1)
                    $('<div/>').addClass('datestamp last7').text(msgHeader).appendTo($view);
            }
            if (hdr.seen) {
                var $message = $('<div/>').addClass('message').attr('id', hdr.id).appendTo($view);
            }
            else {
                var $message = $('<div/>').addClass('message unseen').attr('id', hdr.id).appendTo($view);
            }
            var $timestamp = $("<input type='hidden' value=" + timestamp + " />").attr('id', 'timestamp_' + hdr.id).appendTo($message);
            var $folder = $("<input type=\"hidden\"  value=" + hdr.folder_id + " />").attr('id', 'folder_id_' + hdr.id).appendTo($message);
            if (hdr.sequenceId) {
                var $sequence = $("<input type=\"hidden\"  value=" + hdr.sequenceId + " />").attr('id', 'sequenceid_' + hdr.id).appendTo($message);
            }
            var $messageHeader = $('<div/>').addClass('messageHeader').attr({ "data-id": hdr.id }).appendTo($message);
            var $senderHolder = $('<div/>').addClass('senderHolder').appendTo($messageHeader);
            $('<div/>').addClass('timestamp').text(moment(timestamp).utcOffset(0).format(TIME_FORMAT) || "").appendTo($senderHolder);
            // here 
            var $sender = $('<div/>').addClass('sender').text(from || "").attr("title", from).attr('id','senderid_'+hdr.id).appendTo($senderHolder);
          // var $sender = $('<div/>').addClass('sender').html(ahref_link).attr("title", from).attr('id','senderid_'+hdr.id).appendTo($senderHolder);
            $('<div/>').addClass('subject').text(hdr.subject || "").appendTo($messageHeader);
           // var $messageBody = $('<div/>').addClass('messageBody').html(hdr.body || "").appendTo($messageHeader);
           // $messageShowMore = $messageBody;

            var $noteBody = $('<div/>').addClass('noteBody').attr({'id':'noteBody_'+hdr.id}).html(hdr.body || "").appendTo($messageHeader);
            $messageShowMore = $noteBody;

            //here changes done
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
            if (activeTab !== 'Archive' && !hdr.isInvite) {
                var $archiveIcon = $('<div/>').addClass('icon-archive archive').attr({
                    title: 'Archive'
                }).appendTo($iconBar);
            }

            if (!hdr.isInvite) {
                var $deleteIcon = $('<div/>').addClass('icon-trash delete').attr({
                    title: 'Delete'
                }).appendTo($iconBar);
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

            //var $showMoreHolder = $('<div/>').addClass('showMoreHolder').appendTo($view);
            //var $showMore = $('<div/>').addClass('hidden showMore').appendTo($showMoreHolder);
            $viewShowMore = $view;
            return $view;
        }

    }

    function putShowMore(messageBody,event_id) {
        var element = messageBody[0];
        if ((element.offsetHeight < element.scrollHeight)) {
            // your element have overflow
            var $showMoreHolder = $('<div/>').addClass('showMoreHolder').attr({'id':'showMoreHolder_'+event_id}).appendTo($viewShowMore);
            var $showMore = $('<div/>').addClass('hidden showMore').attr({'data-id':event_id}).appendTo($showMoreHolder);
        }
    }

    /*function putShowMore(messageBody) {
        var element = messageBody[0];
        if ((element.offsetHeight < element.scrollHeight)) {
            // your element have overflow
            var $showMoreHolder = $('<div/>').addClass('showMoreHolder').appendTo($viewShowMore);
            var $showMore = $('<div/>').addClass('hidden showMore').appendTo($showMoreHolder);
        }
    }*/

    //on load scroll pagination event trigger and get all mail event
    $(document).ready(function () {
        var serviceToCall = 'Inbox';
        if ($.cookie('auth') && $.cookie('auth') === mailService.getAuthData().authData) {
            serviceToCall = $.cookie('currentTab');
            $(".mail-box-btn")[0].children[0].innerHTML = serviceToCall;
        }
        else {
            $.cookie('currentTab', serviceToCall, { path: '/' });
        }
        $('#' + serviceToCall).addClass('selected');
        $('#subitem_' + serviceToCall).addClass('selected');
        switch (serviceToCall) {
            case 'Unread': $('.mail-box-list li:nth-child(2)').addClass('selected'); reqParams = { folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
            case 'Today': $('.mail-box-list li:nth-child(3)').addClass('selected'); todayTrigger = true; reqParams = { folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
            case 'Archive': $('.mail-box-list li:nth-child(1)').addClass('selected'); reqParams = { "title": "Archive", "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
            //case 'All': reqParams = { folder: "default0/Inbox", unseen: false, leftHandLimit: startIndex, rightHandLimit: endIndex };break;
            case 'Inbox': $('.mail-box-list li:nth-child(4)').addClass('selected'); reqParams = { folder: "default0/Inbox", unseen: false, sort: 610, order: 'desc', leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
        }

        $.event.trigger("getAllMail", [reqParams, serviceToCall]);
        var sticky = navbar.offsetTop;
        $(window).bind('scroll', function (event) {
            if (window.pageYOffset >= sticky) {
                $('#navbar').addClass("sticky");
            } else {
                $('#navbar').removeClass("sticky");
            }
            var position = $(window).scrollTop();
            var bottom = $(document).height() - $(window).height();

            if (position > 0 && position == bottom && loadpagination == 1) {
                console.log(position + " " + bottom + " " + $(document).height() + "   " + $(window).height());
                $.event.trigger({
                    type: "getPaginationLoader"
                });
            }
        });

        /* $('#widget').bind('scroll', function(){
             // var messages = $(".mailHolder").children();
             // var messagesLength=$(".mailHolder").children().length;
             // console.log("no of child elements:",messages)
             // var counter = 0;
             // for (i = 0; i < messagesLength; i++) {
             //     if (messages.eq(i).offset().top < $(window).scrollTop()) {
             //         counter++;
             //         console.log("count",counter)
             //     }
             // }
             if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight) {
                     $.event.trigger({
                         type: "getPaginationLoader"
                     });
             }
         });   */
    });

    //$('.mail-box-list li:nth-child(4)').addClass('selected');
    $(function () {
        //Each tab click event
        $('body').on({
            click: function (event) {
                $this = $(this);
                $(window).scrollTop(0);
                $('.subTab').removeClass('selected');
                $this.addClass('selected');
                countSeven = 0;
                countZero = 0;
                countOne = 0;
                startIndex = 0;
                endIndex = 15;
                loadpagination = 1;
                tabValue = $(this).text();
                $.cookie('currentTab', tabValue, { path: '/' });
                $.cookie('auth', mailService.getAuthData().authData, { path: '/' });

                if ($('#searchNull').css('display') == 'block') {
                    $('#searchNull').hide();
                }
                if ($('.loadingGif').css('display') == 'block') {
                    $('.loadingGif').hide();
                }
                if ($('.noMoreData').css('display') == 'block') {
                    $('.noMoreData').hide();
                }

                if ($('.noData').css('display') == 'block') {
                    $('.noData').hide();
                }
                $.event.trigger({
                    type: "showSpinningDiv"
                });

                $(".mail-box-btn")[0].children[0].innerHTML = tabValue;
                $('.subitem').removeClass('selected');

                if (tabValue == "Unread") {
                    $('.mail-box-list li:nth-child(2)').addClass('selected');
                    todayTrigger = false;
                    activeTab = "Unread";
                    if ($('#BroadSearch').val() == '') {
                        reqParams = { folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex };
                        $.event.trigger("getAllMail", reqParams);
                    }
                    else {
                        $mail.empty();
                        var tabToCall = mailService.searchMail({ folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: $('#BroadSearch').val() });
                        tabToCall.then(function onload(data) {
                            //data = [];
                            if (data && data.length > 0) {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                var prevDate;
                                for (var i in data) {
                                    getHeaderView(data[i], prevDate).appendTo($mail);
                                    putShowMore($messageShowMore,data[i].id);
                                    prevDate = data[i].received_date;
                                }
                            } else {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                $('#searchNull').show();
                            }
                        });
                    }
                } else if (tabValue == "Today") {
                    $('.mail-box-list li:nth-child(3)').addClass('selected');
                    todayTrigger = true;
                    activeTab = "Today";
                    if ($('#BroadSearch').val() == '') {
                        reqParams = { folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex };
                        $.event.trigger("getAllMail", reqParams);
                    }
                    else {
                        $mail.empty();
                        var tabToCall = mailService.searchMail({ folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: $('#BroadSearch').val() });
                        tabToCall.then(function onload(data) {
                            //data = [];
                            if (data && data.length > 0) {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                var prevDate;
                                var numberOfTodaysSearchItems = 0;
                                var todayStartTimestamp = moment().startOf('day').format('x');
                                var todayEndTimestamp = moment().endOf('day').format('x');
                                for (var i in data) {
                                    if (data[i].received_date >= todayStartTimestamp && data[i].received_date <= todayEndTimestamp) {
                                        numberOfTodaysSearchItems = numberOfTodaysSearchItems + 1;
                                        getHeaderView(data[i], prevDate).appendTo($mail);
                                        putShowMore($messageShowMore,data[i].id);
                                        prevDate = data[i].received_date;
                                    }

                                }
                                if (numberOfTodaysSearchItems === 0) {
                                    $mail.empty();
                                    $('#searchNull').show();
                                }
                            } else {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                $('#searchNull').show();
                            }
                            
                        });
                    }
                } else if (tabValue == "Archive") {
                    $('.mail-box-list li:nth-child(1)').addClass('selected');
                    todayTrigger = false;
                    activeTab = "Archive";
                    if ($('#BroadSearch').val() == '') {
                        reqParams = { "title": "Archive", "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex };
                        $.event.trigger("getAllMail", reqParams);
                    }
                    else {
                        $mail.empty();
                        var tabToCall = mailService.searchMail({ "folder": "default0/Archive", "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: $('#BroadSearch').val() });
                        tabToCall.then(function onload(data) {
                            //data = [];
                            if (data && data.length > 0) {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                var prevDate;
                                for (var i in data) {
                                    getHeaderView(data[i], prevDate).appendTo($mail);
                                    putShowMore($messageShowMore,data[i].id);
                                    prevDate = data[i].received_date;
                                }
                            } else {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                $('#searchNull').show();
                            }
                        });
                    }
                } else if (tabValue == "All") {
                    todayTrigger = false;
                    activeTab = "All";
                    reqParams = { "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }
                    $.event.trigger("getAllMail", reqParams);
                } else if (tabValue == "Inbox") {
                    $('.mail-box-list li:nth-child(4)').addClass('selected');
                    todayTrigger = false;
                    activeTab = "Inbox";
                    console.log("startIndex = " + startIndex + " endIndex " + endIndex)
                    if ($('#BroadSearch').val() == '') {
                        reqParams = { folder: "default0/Inbox", unseen: false, leftHandLimit: startIndex, rightHandLimit: endIndex }
                        $.event.trigger("getAllMail", reqParams);
                    }
                    else {
                        $mail.empty();
                        var tabToCall = mailService.searchMail({ folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: $('#BroadSearch').val() });
                        tabToCall.then(function onload(data) {
                            //data = [];
                            if (data && data.length > 0) {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                var prevDate;
                                for (var i in data) {
                                    getHeaderView(data[i], prevDate).appendTo($mail);
                                    putShowMore($messageShowMore,data[i].id);
                                    prevDate = data[i].received_date;
                                }
                            } else {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                $('#searchNull').show();
                            }
                        });
                    }
                }
            },
        }, '.subTab');

        $('body').on({
            click: function () {
                $this = $(this);
                console.log($this)
                var id = $this.parent().parent().parent()[0].id;
                var from = $('#'+'senderid_'+id).text();
                console.log(from)
                $this.parent().parent().parent().parent().prev().has('.message')
                if ($this[0].id === 'icon-email-read') {
                    if (activeTab === 'Unread') {
                        // $('#' + $this.parent().parent().parent()[0].id).remove();
                        if ($this.parent().parent().parent().parent().has('.datestamp').length > 0) {
                            $('#' + id).siblings(".showMoreHolder").remove();
                            if ($this.parent().parent().parent().parent().next().has('.datestamp').length > 0) {
                                $('#' + $this.parent().parent().parent()[0].id).remove();
                                location.reload();
                            }
                            else {
                                if ($this.parent().parent().parent().parent().next().length > 0) {
                                    $('#' + $this.parent().parent().parent()[0].id).remove();
                                }
                                else {
                                    $('#' + $this.parent().parent().parent()[0].id).remove();
                                    location.reload();
                                }
                            }

                        }
                        else {
                            if ($this.parent().parent().parent().parent().next().length > 0) {
                                if ($this.parent().parent().parent().parent().prev().has('.datestamp').length > 0 && $this.parent().parent().parent().parent().next().has('.datestamp').length > 0) {
                                    if ($this.parent().parent().parent().parent().prev().has('.message').length > 0 && $this.parent().parent().parent().parent().next().has('.message').length > 0) {
                                        $this.parent().parent().parent().parent().remove();
                                    }
                                    else {
                                        $this.parent().parent().parent().parent().remove();
                                        location.reload();
                                    }
                                }
                                else {
                                    if ($this.parent().parent().parent().parent().prev().children('.datestamp').length > 0 && !($this.parent().parent().parent().parent().next().length > 0)) {
                                        $this.parent().parent().parent().parent().remove();
                                        location.reload();
                                    }
                                    else {
                                        $this.parent().parent().parent().parent().remove();
                                    }
                                }
                            } else {
                                if ($this.parent().parent().parent().parent().prev().has('.datestamp').length > 0 && $this.parent().parent().parent().parent().prev().has('.message ').length > 0) {
                                    $this.parent().parent().parent().parent().remove();
                                }
                                else {
                                    if ($this.parent().parent().parent().parent().prev().length > 0) {
                                        $this.parent().parent().parent().parent().remove();
                                    }
                                    else {
                                        $this.parent().parent().parent().parent().remove();
                                        location.reload();
                                    }
                                }

                            }
                        }
                    }
                    else {
                        $('#' + $this.parent().parent().parent()[0].id).removeClass("message unseen").addClass("message");
                        $(this).removeClass('icon-email-read flag').addClass('icon-email flag').attr({
                            id: "icon-email",
                            title: "Mark as unread"
                        });
                    }
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
                var returnPromise = mailService.markMailReadOrUnread(requestQuery);
                returnPromise.then(function onLoad(data) {
                    if (data.hasOwnProperty('error')) {
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
                    "id": $this.parent().parent().parent()[0].id,
                    "folder": $('input#folder_id_' + $this.parent().parent().parent()[0].id).val(),
                    "destinationFolder": "default0/Archive"
                };

                var returnUpdatePromise = mailService.updateMail(requestQuery);
                returnUpdatePromise.then(function onLoad(data) {
                    if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': mailService.getAuthData().authData};
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                        }
                    } else if (data.data) {
                        $('#' + $this.parent().parent().parent()[0].id).remove();
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

                var returnUpdatePromise = mailService.updateMail(requestQuery);
                returnUpdatePromise.then(function onLoad(data) {
                    if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': mailService.getAuthData().authData};
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

        //Each item delete click event
        $('body').on({
            click: function () {
                $this = $(this);
                var selectedMessage = {
                    "folder": $('input#folder_id_' + $this.parent().parent().parent()[0].id).val(),
                    "id": $this.parent().parent().parent()[0].id,
                    "timestamp": $('input#timestamp_' + $this.parent().parent().parent()[0].id).val()
                };
                var deleteList = mailService.setToDeleteMessage(selectedMessage);
                if (deleteList.length > 0) {
                    var returnData = mailService.triggerDeleteRequest(deleteList);
                    returnData.then(function onLoad(data) {
                        if (data.hasOwnProperty('error')) {
                            var err = data.error.toLowerCase().indexOf("expired");
                            if (err > -1) {
                                var reqParam = { 'auth': mailService.getAuthData().authData};
                                refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                            }
                        } else if (data.data) {
                            if ($this.parent().parent().parent().parent().has('.datestamp').length > 0) {
                                $('#' + $this.parent().parent().parent()[0].id).siblings(".showMoreHolder").remove();
                                if ($this.parent().parent().parent().parent().next().has('.datestamp').length > 0) {
                                    $('#' + $this.parent().parent().parent()[0].id).remove();
                                    location.reload();
                                }
                                else {
                                    if ($this.parent().parent().parent().parent().next().length > 0) {
                                        $('#' + $this.parent().parent().parent()[0].id).remove();
                                    }
                                    else {
                                        $('#' + $this.parent().parent().parent()[0].id).remove();
                                        location.reload();
                                    }
                                }

                            }
                            else {
                                if ($this.parent().parent().parent().parent().next().length > 0) {
                                    if ($this.parent().parent().parent().parent().prev().has('.datestamp').length > 0 && $this.parent().parent().parent().parent().next().has('.datestamp').length > 0) {
                                        if ($this.parent().parent().parent().parent().prev().has('.message').length > 0 && $this.parent().parent().parent().parent().next().has('.message').length > 0) {
                                            $this.parent().parent().parent().parent().remove();
                                        }
                                        else {
                                            $this.parent().parent().parent().parent().remove();
                                            location.reload();
                                        }
                                    }
                                    else {
                                        $this.parent().parent().parent().parent().remove();
                                    }
                                } else {
                                    if ($this.parent().parent().parent().parent().prev().has('.datestamp').length > 0 && $this.parent().parent().parent().parent().prev().has('.message ').length > 0) {
                                        $this.parent().parent().parent().parent().remove();
                                    }
                                    else {
                                        $this.parent().parent().parent().parent().remove();
                                        location.reload();
                                    }
                                }

                            }
                        }
                    }, function onError(data, b, c) {
                        console.log("ERROR getMail!", data, b, c);
                    });
                }
            },
        }, '.iconBar .delete');

        //On list mouse enter show more and show less 
        $('body').on({
            mouseenter: function () {
                $(this).find(".showMore, .showLess").toggleClass('hidden');
                $(this).find(".timestamp").hide();
            },
            mouseleave: function () {
                $(this).find(".showMore, .showLess").toggleClass('hidden');
                $(this).find(".timestamp").show();
            }
        }, '.messageHolder');

        //Show more and show less click event
      /*  $('body').on({
            click: function () {
                $this = $(this);
                if ($this[0].classList[0] === 'showLess') {
                    $this.parent().siblings(".message").find(".messageBody").scrollTop(0);
                }
                $this.parent().siblings(".message").find(".messageBody").toggleClass('expanded');
                $this.toggleClass('showMore').toggleClass('showLess');
            },
        }, '.showMore, .showLess'); */

        $('body').on({
            click: function () {
                $this = $(this);
                console.log($this)
                console.log($this[0].dataset.id)
                if ($this[0].classList[0] === 'showLess') {
                    $('#noteBody_'+$this[0].dataset.id).scrollTop(0);
                }
                $('#noteBody_'+$this[0].dataset.id).toggleClass('expanded');
                $this.toggleClass('showMore').toggleClass('showLess');
            }
        }, '.showMore, .showLess');

        $('body').on({
            click: function (event) {

                console.log("clicked on sender")
                url = "https://mail.teaming.orange-business.com/"   
               window.location.href = "hub://openUrl?url="+encodeURIComponent(url) 
            }
        },'.sender');

        function open(url,opts) {
            window.location.href = "hub://openUrl?url="+encodeURIComponent(url)

            //window.location.href = '<a href="hub://openurl?url=">hub://openUrl?url=</a>' + encodeURIComponent(url);
        }

        //click of each mail
        $('body').on({
            click: function () {
                $this = $(this);
                var id = $this[0].dataset.id;
                console.log("for load:");
                console.log("id and folder and subject", id, $('input#folder_id_' + id).val(), $(this)[0].children[1].innerText);
                var promise = loadTab({ authData: mailService.getAuthData().authData, id: id, folder: $('input#folder_id_' + id).val(), subject: $(this)[0].children[1].innerText });
                promise.then(function onLoad(res) {
                    console.log(res);
                });
            },
        }, '.messageHeader');

        //Click on invitation approve confirmation: 1
        $('body').on({
            click: function () {
                $this = $(this);
                var id = $this.parent().parent().parent()[0].id;
                var from = $('#'+'senderid_'+id).text();

                var approveRequest = {
                    objectId: id,
                    folderId: $('input#folder_id_' + id).val(),
                    confirmation: "1",
                    confirmationMessage: "",
                    sequenceId: $('input#sequenceid_' + id).val()
                };
                var approveRequestPromise = mailService.updateMailInvitation(approveRequest);
                approveRequestPromise.then(function (data) {
                    if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': mailService.getAuthData().authData};
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=mail", '_blank');
                        }
                    } else if (data.data) {
                        $('#' + $this.parent().parent().parent()[0].id).remove();
                        $('#showMoreHolder_'+$this.parent().parent().parent()[0].id).remove();
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
                var id = $this.parent().parent().parent()[0].id;
                var from = $('#'+'senderid_'+id).text();
                var declineRequest = {
                    objectId: id,
                    folderId: $('input#folder_id_' + id).val(),
                    confirmation: "2",
                    confirmationMessage: "",
                    sequenceId: $('input#sequenceid_' + id).val()
                };
                var declineRequestPromise = mailService.updateMailInvitation(declineRequest);
                declineRequestPromise.then(function (data) {
                    $('#' + $this.parent().parent().parent()[0].id).remove();
                    $('#showMoreHolder_'+$this.parent().parent().parent()[0].id).remove();
                }, function onError(data, b, c) {
                    console.log("ERROR Decline!", data, b, c);
                });
                //

            },
        }, '.invitationDecline');

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
                var tentativeRequestPromise = mailService.updateMailInvitation(tentativeRequest);
                tentativeRequestPromise.then(function (data) {
                    $('#' + $this.parent().parent().parent()[0].id).remove();
                    $('#showMoreHolder_'+$this.parent().parent().parent()[0].id).remove();
                }, function onError(data, b, c) {
                    console.log("ERROR Tentative!", data, b, c);
                });
            },
        }, '.invitationTentative');
    });
    //Event for pagination loader
    $(document).on('getPaginationLoader', function (event) {
        $.event.trigger({
            type: "showBarLoaderDiv"
        });
        startIndex = endIndex;
        endIndex += 5;

        //using common service to different tabs
        var serviceToCall = null;
        if ($('#BroadSearch').val() == '') {
            switch (activeTab) {
                case "Unread": serviceToCall = mailService.getMail({ folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }, activeTab); break;
                case "Today": serviceToCall = mailService.getMail({ folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }, activeTab); break;
                case "Archive": serviceToCall = mailService.getMail({ "title": "Archive", "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }, activeTab); break;
                case "All": serviceToCall = mailService.getMail({ "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }, activeTab); break;
                case "Inbox": serviceToCall = mailService.getMail({ folder: "default0/Inbox", unseen: false, leftHandLimit: startIndex, rightHandLimit: endIndex }, activeTab); break;
            }
        }
        else {
            var searchtext = $('#BroadSearch').val();
            switch (activeTab) {
                case "Unread": serviceToCall = mailService.searchMail({ folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: searchtext }); break;
                case "Today": serviceToCall = mailService.searchMail({ folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: searchtext }); break;
                case "Archive": serviceToCall = mailService.searchMail({ folder: "default0/Archive", "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: searchtext }); break;
                case "All": serviceToCall = mailService.searchMail({ "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: searchtext }); break;
                case "Inbox": serviceToCall = mailService.searchMail({ folder: "default0/Inbox", unseen: false, leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: searchtext }); break;
            }
        }


        serviceToCall.then(function onload(data) {
            //data = [];
            if (data && data.length > 0) {
                $.event.trigger({
                    type: "hideAllLoader"
                });
                var prevDate;
                for (var i in data) {
                    getHeaderView(data[i], prevDate).appendTo($mail);
                    putShowMore($messageShowMore,data[i].id);
                    prevDate = data[i].received_date;
                }
            } else {
                $.event.trigger({
                    type: "showNoMoreDataDiv"
                });
                //$('.noMoreData').show();        
                loadpagination = 0;
                event.preventDefault();
            }
        });

        reqParams.leftHandLimit = startIndex;
        reqParams.rightHandLimit = endIndex;
    });

    //Get -- no more items -- messages at the last set of data
    $(document).on('showNoMoreDataDiv', function (event) {
        $('.loadingGif').hide();
        $('.spinningGif').hide();
        $('.noMoreData').show();
        $('.noData').hide();
    });

    //Get spinner loader
    $(document).on('showSpinningDiv', function (event) {
        $('.loadingGif').hide();
        $('.noMoreData').hide();
        $('.spinningGif').show();
        $('.noData').hide();
    });

    //Get spinner loader
    $(document).on('showBarLoaderDiv', function (event) {
        $('.spinningGif').hide();
        $('.noMoreData').hide();
        $('.loadingGif').show();
        $('.noData').hide();
    });

    $(document).on('hideAllLoader', function (event) {
        $('.loadingGif').hide();
        $('.spinningGif').hide();
        $('.noMoreData').hide();
        $('.noData').hide();
    });

    // Get all mail event listener
    $(document).on('getAllMail', function (event, param1, param2) {
        $.event.trigger({
            type: "showSpinningDiv"
        });
        $mail.empty();
        var curTab = param2 || activeTab;
        mailService.getMail(param1, curTab).then(function onload(data) {

            if (data && data.length > 0) {
                $.event.trigger({
                    type: "hideAllLoader"
                });
                if (curTab == "Today") {
                      
                    var prevDate;
                    var numberOfTodaysItems = 0;
                    var todayStartTimestamp = moment().startOf('day').format('x');
                    var todayEndTimestamp = moment().endOf('day').format('x');
                    for (var i in data) {
                        if (data[i].received_date >= todayStartTimestamp && data[i].received_date <= todayEndTimestamp) {
                            numberOfTodaysItems = numberOfTodaysItems + 1;
                            getHeaderView(data[i], prevDate).appendTo($mail);
                            putShowMore($messageShowMore,data[i].id);
                            prevDate = data[i].received_date;
                        }
                        console.log(numberOfTodaysItems, "TodayItemsNumber");
                    }
                    if (numberOfTodaysItems == 0) {
                      //  $('.noMoreData').text("No items found");
                      console.log("----",param1.leftHandLimit)
                      if(param1.leftHandLimit == 0) {      
                        $('.loadingGif').hide();
                        $('.spinningGif').hide();
                        $('.noData').show();
                      } else {
                        $.event.trigger({
                            type: "showNoMoreDataDiv"
                        });
                    }
                    }
                } else {
                    var prevDate;
                    for (var i = 0; i < data.length; i++) {
                        getHeaderView(data[i], prevDate).appendTo($mail);
                        putShowMore($messageShowMore,data[i].id);
                        prevDate = data[i].received_date;
                    }
                }
            }
            else {
                console.log("dfgfdg");
                if(param1.leftHandLimit == 0) {
                    $('.loadingGif').hide();
                    $('.spinningGif').hide();
                    $('.noMoreData').hide();
                    $('.noData').show();
                  } else {
                    $.event.trigger({
                        type: "showNoMoreDataDiv"
                    });
                }
            }
        });
    });

    $('#BroadSearch').keypress(function (e) {
        var key = e.which;
        startIndex = 0;
        endIndex = 15;

        if (key == 13) {

            countSeven = 0;
            countZero = 0;
            countOne = 0;
            startIndex = 0;

            $this = $(this);
            $.event.trigger({
                type: "hideAllLoader"
            });
            if ($this[0].value && $this[0].value.trim().length == 0) {
                return 0;
            }
            $('#searchNull').hide();
            if ($this[0].value.length >= 3) {
                $('.searchHolder i').removeClass('icon-search').addClass('icon-cross-thin-filled');
                $mail.empty();
                $.event.trigger({
                    type: "showSpinningDiv"
                });
                switch (activeTab) {
                    case "Unread":
                        var reqParams = { folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "Today":
                        var reqParams = { folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "Archive":
                        var reqParams = { folder: "default0/Archive", "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "All":
                        var reqParams = { "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "Inbox":
                        var reqParams = { folder: "default0/Inbox", unseen: false, leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                }
                reqParams.searchString = $this[0].value;
                mailService.searchMail(reqParams).then(function onload(data) {
                    if (data.length > 0) {
                        $.event.trigger({
                            type: "hideAllLoader"
                        });
                        //$mail.empty();
                        var prevDate;
                        for (var i in data) {
                            getHeaderView(data[i], prevDate).appendTo($mail);
                            putShowMore($messageShowMore,data[i].id);
                            prevDate = data[i].received_date;
                        }
                    }
                    else {
                        //$mail.empty();
                        $.event.trigger({
                            type: "hideAllLoader"
                        });
                        $('#searchNull').show();
                    }
                });
            }
            else if ($this[0].value.length === 0) {
                $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
                startIndex = 0;
                endIndex = 15;
                switch (activeTab) {
                    case "Unread":
                        var reqParams = { folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "Today":
                        var reqParams = { folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "Archive":
                        var reqParams = { "title": "Archive", "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "All":
                        var reqParams = { "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "Inbox":
                        var reqParams = { folder: "default0/Inbox", unseen: false, leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                }
                $('#searchNull').hide();
                $.event.trigger({
                    type: "showSpinningDiv"
                });
                $.event.trigger("getAllMail", reqParams);

            }
            else {
                $('.searchHolder i').removeClass('icon-search').addClass('icon-cross-thin-filled');
            }
        }
    });

    //click of search cross icon
    $('body').on({
        click: function () {
            $(this).removeClass('icon-cross-thin-filled').addClass('icon-search');
            $('#BroadSearch').val("");

            countSeven = 0;
            countZero = 0;
            countOne = 0;
            startIndex = 0;
            endIndex = 15;
            switch (activeTab) {
                case "Unread":
                    var reqParams = { folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                case "Today":
                    var reqParams = { folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                case "Archive":
                    var reqParams = { "title": "Archive", "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                case "All":
                    var reqParams = { "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                case "Inbox":
                    var reqParams = { folder: "default0/Inbox", unseen: false, leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
            }
            $('#searchNull').hide();
            $.event.trigger({
                type: "showSpinningDiv"
            });
            $.event.trigger("getAllMail", reqParams);
        },
    }, '.icon-cross-thin-filled');


    $('#BroadSearch').keyup(function (e) {
        $this = $(this);
        if(e.which==8) {
            if($this[0].value.trim().length != 0) {        
                $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
            }
            if ($this[0].value.trim().length == 0) {
                $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
                countSeven = 0;
                countZero = 0;
                countOne = 0;
                startIndex = 0;
                endIndex = 15;
                switch (activeTab) {
                    case "Unread":
                        var reqParams = { folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "Today":
                        var reqParams = { folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "Archive":
                        var reqParams = { "title": "Archive", "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "All":
                        var reqParams = { "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                    case "Inbox":
                        var reqParams = { folder: "default0/Inbox", unseen: false, leftHandLimit: startIndex, rightHandLimit: endIndex }; break;
                }
                $('#searchNull').hide();
                $.event.trigger({
                    type: "showSpinningDiv"
                });
                $.event.trigger("getAllMail", reqParams);
            }
        }
    });



    //change of Unread or Today
    $('body').on({
        click: function () {
            $('.mail-box-list').hide();
            $('.mail-box-btn :nth-child(2)').removeClass("icon-arrow-up");
            $('.mail-box-btn :nth-child(2)').addClass("icon-arrow-down");
            $('.subTab').removeClass('selected');
            $('.subitem').removeClass('selected');
            $this = $(this);
            $this.addClass('selected');
            var tabValue = $this[0].childNodes[0].data;
            $(".mail-box-btn")[0].children[0].innerHTML = tabValue;
            $.cookie('currentTab', tabValue, { path: '/' });
            $.cookie('auth', mailService.getAuthData().authData, { path: '/' });
            countSeven = 0;
            countZero = 0;
            countOne = 0;
            startIndex = 0;
            endIndex = 15;
            $(window).scrollTop(0);
            switch (tabValue) {
                case "Unread":
                    $('.subTabs a:nth-child(2)').addClass('selected');
                    todayTrigger = false;
                    activeTab = "Unread";
                    if ($('#BroadSearch').val() == '') {
                        reqParams = { folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex };
                        $.event.trigger("getAllMail", reqParams);
                    }
                    else {
                        $mail.empty();
                        var tabToCall = mailService.searchMail({ folder: "default0/Inbox", unseen: true, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: $('#BroadSearch').val() });
                        tabToCall.then(function onload(data) {
                            //data = [];
                            if (data && data.length > 0) {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                var prevDate;
                                for (var i in data) {
                                    getHeaderView(data[i], prevDate).appendTo($mail);
                                    putShowMore($messageShowMore,data[i].id);
                                    prevDate = data[i].received_date;
                                }
                            } else {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                $('#searchNull').show();
                            }
                        });
                    }
                    break;
                case "Today":
                    $('.subTabs a:nth-child(3)').addClass('selected');
                    todayTrigger = true;
                    activeTab = "Today";
                    if ($('#BroadSearch').val() == '') {
                        reqParams = { folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex };
                        $.event.trigger("getAllMail", reqParams);
                    }
                    else {
                        $mail.empty();
                        var tabToCall = mailService.searchMail({ folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: $('#BroadSearch').val() });
                        tabToCall.then(function onload(data) {
                            //data = [];
                            if (data && data.length > 0) {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                var prevDate;
                                var numberOfTodaysSearchItems = 0;
                                var todayStartTimestamp = moment().startOf('day').format('x');
                                var todayEndTimestamp = moment().endOf('day').format('x');
                                for (var i in data) {
                                    if (data[i].received_date >= todayStartTimestamp && data[i].received_date <= todayEndTimestamp) {
                                        numberOfTodaysSearchItems = numberOfTodaysSearchItems + 1;
                                        getHeaderView(data[i], prevDate).appendTo($mail);
                                        putShowMore($messageShowMore,data[i].id);
                                        prevDate = data[i].received_date;
                                    }

                                }
                                if (numberOfTodaysSearchItems === 0) {
                                    $mail.empty();
                                    $('#searchNull').show();
                                }
                            } else {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                $('#searchNull').show();
                            }
                        });
                    }
                    break;
                case "Archive":
                    $('.subTabs a:nth-child(1)').addClass('selected');
                    todayTrigger = false;
                    activeTab = "Archive";
                    if ($('#BroadSearch').val() == '') {
                        reqParams = { "title": "Archive", "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex };
                        $.event.trigger("getAllMail", reqParams);
                    }
                    else {
                        $mail.empty();
                        var tabToCall = mailService.searchMail({ "folder": "default0/Archive", "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: $('#BroadSearch').val() });
                        tabToCall.then(function onload(data) {
                            //data = [];
                            if (data && data.length > 0) {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                var prevDate;
                                for (var i in data) {
                                    getHeaderView(data[i], prevDate).appendTo($mail);
                                    putShowMore($messageShowMore,data[i].id);
                                    prevDate = data[i].received_date;
                                }
                            } else {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                $('#searchNull').show();
                            }
                        });
                    }
                    break;
                case "Inbox":
                    $('.subTabs a:nth-child(4)').addClass('selected');
                    todayTrigger = false;
                    activeTab = "Inbox";
                    console.log("startIndex = " + startIndex + " endIndex " + endIndex)
                    if ($('#BroadSearch').val() == '') {
                        reqParams = { folder: "default0/Inbox", unseen: false, leftHandLimit: startIndex, rightHandLimit: endIndex }
                        $.event.trigger("getAllMail", reqParams);
                    }
                    else {
                        $mail.empty();
                        var tabToCall = mailService.searchMail({ folder: "default0/Inbox", unseen: false, "sort": 610, "order": "desc", leftHandLimit: startIndex, rightHandLimit: endIndex, searchString: $('#BroadSearch').val() });
                        tabToCall.then(function onload(data) {
                            //data = [];
                            if (data && data.length > 0) {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                var prevDate;
                                for (var i in data) {
                                    getHeaderView(data[i], prevDate).appendTo($mail);
                                    putShowMore($messageShowMore,data[i].id);
                                    prevDate = data[i].received_date;
                                }
                            } else {
                                $.event.trigger({
                                    type: "hideAllLoader"
                                });
                                $('#searchNull').show();
                            }
                        });
                    }
                    break;
                default:
                    break;
            }

        },
    }, '.subitem');


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


})();