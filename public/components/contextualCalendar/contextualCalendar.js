
(function appClosure() {
    $parent = $('#widget');
    $calendar = $('<div/>').addClass('calendarHolder').appendTo($parent);
    var shouldUseMocks = window.appConfig.options.useMocks || false;
    var activeTab = 'Upcoming Meetings';
    var DATE_FORMAT = 'dddd MMM D';
    var TIME_FORMAT = 'h:mm A';
    var todayTrigger = false;
    var startIndex = 0;
    var endIndex = 15;
    var prevDate = '';
    var loadpagination = 1;
    var user_id = '';
    var tabValue = "Today";
    var search;
    var reqParams;
    var iconBarHTML = '<div class="icon-tick accept"></div><div class="icon-cross decline"></div><div class="icon-tentative tentative"></div>';
    var searchNullHTML = '<div id="searchNull"><i class="icon-search"></i><span>No items were returned in your search.</span></div>';
    var noMoreDataHTML = '---All items displayed---';
    var noUpcomingDataHTML = '<div class="no-data">No upcoming meetings</div>';
    var loadingImageHTML = '<img class="loading" src="common/images/loading.gif"/>';
    $messageShowMore = '';
    var $viewShowMore = '';
    $("#Schedule_Meetings").hide();

    var time = [];
    var append = ['AM','PM']
    for (var index = 0; index <= 1; index++) {
        for (var index1 = 1; index1 <= 12 - index ; index1++) {
            for (var index2 = 1; index2 <= 2; index2++) {
                if(index2 == 1){
                    time.push(index1.toString()+" "+ (index1 == 12 ?append[1] :append[index]))
                }else{
                    time.push(index1.toString()+".30 "+(index1 == 12 ?append[1] :append[index]));
                }
            }
        }
    }
    time.unshift( "12 AM", "12.30 AM" );

    $('.noMoreData').hide();
    function createCalendarView(appointmentData, prevDate) {
        var $view = $('<div/>').addClass('appointmentHolder');
        var organizer = appointmentData.organizer || '';
        var timestamp = appointmentData.start_date;
        var allday = appointmentData.full_time;
        var location = appointmentData.location || '';
        var title = appointmentData.title || '';
        var note = appointmentData.note || '';
        if (shouldUseMocks) {
            var invitationStatusForUser = 1;
        } else {
            var invitationStatusForUser = checkUserAccepted(appointmentData.users, user_id);
        }
       // var prevStr = prevDate ? moment.utc(prevDate).format(DATE_FORMAT) || "" : "";
        var prevStr = prevDate ? moment(prevDate).format(DATE_FORMAT) || "" : "";
       // var dateStr = moment.utc(timestamp).format(DATE_FORMAT) || "";
        var dateStr = moment(timestamp).format(DATE_FORMAT) || "";

        if (prevStr != dateStr) {
            $('<div/>').addClass('datestamp').text(dateStr).appendTo($view);
        }

        var $appointment = $('<div/>').addClass('appointment').attr('id', appointmentData.id).appendTo($view);
        var $folder = $("<input type=\"hidden\"  value=" + appointmentData.folder_id + " />").attr('id', 'folder_id_' + appointmentData.id).appendTo($appointment);
        var $appointmentContent = $('<div/>').addClass('appointmentContent').appendTo($appointment);
        var $calendarIconOnBar = $('<div/>').addClass('appointmentCalendarIcon').appendTo($appointmentContent);
        var $calendarIcon = $('<div/>').addClass('icon-calendar').appendTo($calendarIconOnBar);
        var $appointmentHeader = $('<div/>').addClass('appointmentHeader').appendTo($appointmentContent);

        var $titleHolder = $('<div/>').addClass('titleHolder').appendTo($appointmentHeader);

        var $title = $('<div/>').addClass('title').text(title || "").attr({'title':title}).appendTo($titleHolder);
        var $noteBody = $('<div/>').addClass('noteBody').attr({'id':'noteBody_'+appointmentData.id}).html(note).appendTo($appointmentHeader);
        if (note == '') {
            var $noteBody = $('<div/>').addClass('noteBody').html('(No description)').appendTo($appointmentHeader);

        }
        $messageShowMore = $noteBody;
        var $iconBarHolder = $('<div/>').addClass('iconBarHolder').prependTo($appointmentHeader);
        var $iconBar = $('<div/>').addClass('iconBar').appendTo($iconBarHolder);
        /*if (invitationStatusForUser != 1) {
            var $acceptIcon = $('<div/>').addClass('icon-tick accept').appendTo($iconBar);
        }
        if (invitationStatusForUser == 1) {
            var $acceptIcon = $('<div/>').addClass('icon-check-round-thin-filled accepted').appendTo($iconBar);
        }

        if(invitationStatusForUser == 2){
            var $declineIcon = $('<div/>').addClass('icon-cross-thin-filled decline').attr({'title':"Decline",'data-id':appointmentData.id, 'id':'icon-cross_'+appointmentData.id}).appendTo($iconBar); 
        }else{
            var $declineIcon = $('<div/>').addClass('icon-cross decline').attr({'title':"Decline",'data-id':appointmentData.id,'id':'icon-cross_'+appointmentData.id}).appendTo($iconBar);
        }
        var $declineIcon = $('<div/>').addClass('icon-cross decline').appendTo($iconBar);
        if (invitationStatusForUser != 3) {
            var $tentativeIcon = $('<div/>').addClass('icon-tentative tentative').appendTo($iconBar);
        }
        if (invitationStatusForUser == 3) {
            var $tentativeIcon = $('<div/>').addClass('icon-support tentatived').appendTo($iconBar);
        }*/

        if (invitationStatusForUser == 1) {
            var $acceptIcon = $('<div/>').addClass('icon-check-round-thin-filled accepted').attr({'title':"Accept",'data-id':appointmentData.id, 'data-startdate':appointmentData.start_date ,'id':'icon-tick_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar);
        } else {
            var $acceptIcon = $('<div/>').addClass('icon-tick accept').attr({'title':"Accept",'data-id':appointmentData.id, 'data-startdate':appointmentData.start_date , 'id':'icon-tick_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar);
        }

        if(invitationStatusForUser == 2){
            var $declineIcon = $('<div/>').addClass('icon-cross-thin-filled decline').attr({'title':"Decline",'data-id':appointmentData.id, 'data-startdate':appointmentData.start_date , 'id':'icon-cross_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar); 
        }else{
            var $declineIcon = $('<div/>').addClass('icon-cross decline').attr({'title':"Decline",'data-id':appointmentData.id,'data-startdate':appointmentData.start_date , 'id':'icon-cross_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar);
        }
       
        if (invitationStatusForUser == 3) {
            var $tentativeIcon = $('<div/>').addClass('icon-support tentatived').attr({'title':"Tentative", 'data-id':appointmentData.id,'data-startdate':appointmentData.start_date , 'id':'icon-tentative_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar);
        }else{
            var $tentativeIcon = $('<div/>').addClass('icon-tentative tentative').attr({'title':"Tentative", 'data-id':appointmentData.id,'data-startdate':appointmentData.start_date ,'id':'icon-tentative_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar);
        }


        $('<div/>').addClass('timestamp').text(allday ? 'All Day' : moment(timestamp).format(TIME_FORMAT) || "").appendTo($appointmentContent);
        $viewShowMore = $appointment;
        return $view;
    }

    function putShowMore(messageBody,event_id) {
        var element = messageBody[0];
        if ((element.offsetHeight < element.scrollHeight)) {
            // your element have overflow
            var $showMoreHolder = $('<div/>').addClass('showMoreHolder').attr({'id':'showMoreHolder_'+event_id}).appendTo($viewShowMore);
            var $showMore = $('<div/>').addClass('hidden showMore').attr({'data-id':event_id}).appendTo($showMoreHolder);
        }
    }


    function createScheduleView(scheduleSlots, prevDate) {
        var $view = $('<div/>').addClass('appointmentHolder');
        var timestamp = scheduleSlots.timestamp;
        var prevStr = prevDate ? moment(prevDate, "x").format(DATE_FORMAT) || "" : "";
        var dateStr = moment(timestamp, 'x').format(DATE_FORMAT) || "";
        if (prevStr != dateStr) {
            $('<div/>').addClass('datestamp').text(dateStr).appendTo($view);
        }
        var $scheduleslots = $('<div/>').addClass('scheduleslots').appendTo($view);
        if (scheduleSlots.timeslots.length > 0) {
            for (var i = 0; i < scheduleSlots.timeslots.length; i++) {
                var eachStartTime = scheduleSlots.timeslots[i].starttime;
                var eachEndTime = scheduleSlots.timeslots[i].endTime;
                var $timeSlots = $('<div/>').addClass('timeslots').appendTo($scheduleslots);
                var $timeSlotsContentBlock = $('<div/>').addClass('timeSlotsContentBlock').appendTo($timeSlots);
                var $timeSlotsContent = $('<span/>').addClass('timeSlotsContent').html(scheduleSlots.timeslots[i].starttime + ' - ' + scheduleSlots.timeslots[i].endtime).appendTo($timeSlotsContentBlock);
                var $inviteLink = $('<a/>').addClass('inviteLink').html('Invite >').attr('href', '#').appendTo($timeSlotsContentBlock);
            }
        }
        else {
            var $timeSlots = $('<div/>').addClass('timeslots').appendTo($scheduleslots);
            var $timeSlotsContentBlock = $('<div/>').addClass('timeSlotsContentBlock').appendTo($timeSlots);
            var $timeSlotsContent = $('<span/>').addClass('timeSlotsContent').html('WEEKEND').appendTo($timeSlotsContentBlock);
        }

        return $view;
    }

    function checkUserAccepted(appointmentUsers, loggedInUserId) {
        for (var i = 0; i < appointmentUsers.length; i++) {
            if (appointmentUsers[i].id == loggedInUserId) {
                return appointmentUsers[i].confirmation;
            }
        }
    }

    function thisWeekStartAndEndDate() {
        //var first = moment().format('x');
       // var first = moment().startOf('day').format('x');
        var first = moment().format('x')-3600000;
      
        var last = moment().add(6, 'days').endOf('day').format('x');
        var data = {
            startTimestamp: first,
            endTimestamp: last
        };
        return data;
    }

    function formatAMPM(date) {
        var hours = date.getHours();
        var minutes = date.getMinutes();
        var ampm = hours >= 12 ? 'pm' : 'am';
        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        minutes = minutes < 10 ? '0'+minutes : minutes;
        var strTime = hours + ':' + minutes + ' ' + ampm;
        return strTime;
      }

    $(document).ready(function () {
        var dateParams = thisWeekStartAndEndDate()
        reqParams = { auth: calendarService.getAuthData(), emailId: calendarService.getEmailId(), start_date_limit: dateParams.startTimestamp, end_date_limit: dateParams.endTimestamp };
        reqParams.leftHandLimit = startIndex;
        reqParams.rightHandLimit = endIndex;
        $.event.trigger("getAllCalendarData", reqParams);
        var sticky = navbar.offsetTop;
        $('#widget').bind('scroll', function () {
            if (window.pageYOffset >= sticky) {
                $('#navbar').addClass("sticky");
            } else {
                $('#navbar').removeClass("sticky");
            }
            var position = $('#widget').scrollTop();
            var bottom = $(document).height() - $('#widget').height();

            /*if (Math.round(position) >= bottom && loadpagination == 1) {
                console.log(position + " " + bottom + " " + $(document).height() + "   " + $(window).height());
                $.event.trigger({
                    type: "getPaginationLoader"
                });
            }*/
            //console.log('this scrollHeight>>>', $(this)[0].scrollHeight);
            //console.log('this scrollTop>>>', $(this).scrollTop());
            //console.log('this outerHeight>>>', $(this).outerHeight());
            //console.log('difference height and top>>>>', $(this)[0].scrollHeight - Math.round($(this).scrollTop()));
            if ($(this)[0].scrollHeight - Math.round($(this).scrollTop()) == $(this).outerHeight() && loadpagination == 1) {
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
                var dateParams = thisWeekStartAndEndDate();
                var searchString = $("#BroadSearch").val();
                if(searchString && searchString.trim().length != 0){
                    var reqParams = { auth: calendarService.getAuthData(), emailId: calendarService.getEmailId(), start_date_limit: dateParams.startTimestamp, end_date_limit: dateParams.endTimestamp,content: searchString };
                }else{
                    var reqParams = { auth: calendarService.getAuthData(), emailId: calendarService.getEmailId(), start_date_limit: dateParams.startTimestamp, end_date_limit: dateParams.endTimestamp };
                }
                if (activeTab === 'Schedule Meetings') {
                    console.log('Request Parameters schedule meetings>>>>', reqParams);
                    //$('#widget').css("display","block");
                    $(".no-data").hide();
                    $("#Schedule_Meetings").show();
                    for (var index = 0; index < time.length; index++) {
                        $("#time-select").append('<li class="time-selected">'+time[index]+'</li>');
                        
                    }
                    $('.searchHolder').hide();
                  
                    $(".spinningGif").hide();
                    $(".noMoreData").hide();
                    $(".calendarHolder").hide();
                    
                }
                else {
                    $('.searchHolder').show();
                    $(".calendarHolder").show();
                    $("#Schedule_Meetings").hide();
                    $('.scheduleMeetingHeading').hide();
                    $.event.trigger("getAllCalendarData", reqParams);
                }
            },
        }, '.subTab');

        $('body').on({
            mouseenter: function () {
                $this = $(this);
               // $(this).find(".iconBar").toggleClass('hidden');
                $(this).parent().find(".showMore, .showLess").toggleClass('hidden');
                $this.find(".iconBar").css('background-color', '#f2f2f2')

            },
            mouseleave: function () {
                $this = $(this);
               // $(this).find(".iconBar").toggleClass('hidden');
                $(this).parent().find(".showMore, .showLess").toggleClass('hidden');
                $this.find(".iconBar").css('background-color', '#fff')
            }
        }, '.appointment');

        $('body').on({
            click: function () {
                $this = $(this);
                if ($this[0].classList[0] === 'showLess') {
                    $('#noteBody_'+$this[0].dataset.id).scrollTop(0);
                }
                $('#noteBody_'+$this[0].dataset.id).toggleClass('expanded');
                $this.toggleClass('showMore').toggleClass('showLess');
            }
        }, '.showMore, .showLess');

        $('body').on({
            click: function () {
                $this = $(this);

                var acecptRequest = {
                    objectId: $this.parent().parent().parent().parent().parent()[0].id,
                    folderId: $('input#folder_id_' + $this.parent().parent().parent().parent().parent()[0].id).val(),
                    confirmation: "1",
                    confirmationMessage: ""
                };
                console.log(acecptRequest);
                $(this).removeClass("icon-tick accept").addClass("icon-check-round-thin-filled accepted");
                if ($this.parent().children()[1].className === 'icon-cross-thin-filled decline' || $this.parent().children()[1].className === 'decline icon-cross-thin-filled') {
                    $($this.parent().children()[1]).removeClass("icon-cross-thin-filled").addClass("icon-cross");
                }
                if ($this.parent().children()[2].className === 'icon-support tentatived') {
                    $($this.parent().children()[2]).removeClass("icon-support tentatived").addClass("icon-tentative tentative");
                }
                var acecptRequestPromise = calendarService.updateInvitation(acecptRequest);
                acecptRequestPromise.then(function onLoad(data) {
                    if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': calendarService.getAuthData().authData};
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=calendar", '_blank');
                        }
                    } else if (data.data) {

                    }
                }, function onError(data, b, c) {
                    console.log("ERROR Approve!", data, b, c);
                });
            },
        }, '.iconBar .accept');

        $('body').on({
            click: function () {
                $this = $(this);
                var declineRequest = {
                    objectId: $this.parent().parent().parent().parent().parent()[0].id,
                    folderId: $('input#folder_id_' + $this.parent().parent().parent().parent().parent()[0].id).val(),
                    confirmation: "2",
                    confirmationMessage: ""
                };
                $this.removeClass("icon-cross")
                $this.addClass("icon-cross-thin-filled");

                if ($this.parent().children()[2].className === 'icon-support tentatived'){
                    $(this).next().removeClass("icon-support tentatived").addClass("icon-tentative tentative");
                }

                if ($this.parent().children()[0].className === 'icon-check-round-thin-filled accepted'){
                    $(this).prev().removeClass("icon-check-round-thin-filled accepted").addClass("icon-tick accept");
                }

                console.log(declineRequest);
                var declineRequestPromise = calendarService.updateInvitation(declineRequest);
                // $($this.parent().parent().siblings('.titleHolder').children()).addClass("strikethrough");
                declineRequestPromise.then(function onLoad(data) {
                    if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': calendarService.getAuthData().authData};
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=calendar", '_blank');
                        }
                    }
                }, function onError(data, b, c) {
                    console.log("ERROR Approve!", data, b, c);
                });
            },
        }, '.iconBar .decline');

        $('body').on({
            click: function () {
                $this = $(this);
                var tentativeRequest = {
                    objectId: $this.parent().parent().parent().parent().parent()[0].id,
                    folderId: $('input#folder_id_' + $this.parent().parent().parent().parent().parent()[0].id).val(),
                    confirmation: "3",
                    confirmationMessage: ""
                };
                $(this).removeClass("icon-tentative tentative").addClass("icon-support tentatived");
                if ($this.parent().children()[1].className === 'icon-cross-thin-filled decline' || $this.parent().children()[1].className === 'decline icon-cross-thin-filled') {
                    $this.prev().removeClass("icon-cross-thin-filled").addClass("icon-cross");
                }
                if ($this.parent().children()[0].className === 'icon-check-round-thin-filled accepted') {
                    $this.prev().prev().removeClass("icon-check-round-thin-filled accepted").addClass("icon-tick accept");
                }
                var tentativeRequestPromise = calendarService.updateInvitation(tentativeRequest);
                tentativeRequestPromise.then(function onLoad(data) {
                    if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            var reqParam = { 'auth': calendarService.getAuthData().authData};
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=calendar", '_blank');
                        }
                    }
                }, function onError(data, b, c) {
                    console.log("ERROR Approve!", data, b, c);
                });
            },
        }, '.iconBar .tentative');

        //click of search cross icon
    $('body').on({
        click: function () {
            search=false;
            var dateParams = thisWeekStartAndEndDate();
            var reqParams = { auth: calendarService.getAuthData(), emailId: calendarService.getEmailId(), start_date_limit: dateParams.startTimestamp, end_date_limit: dateParams.endTimestamp };
            reqParams.leftHandLimit = startIndex;
            reqParams.rightHandLimit = endIndex;
            $.event.trigger("getAllCalendarData", reqParams);
            $('#BroadSearch').val("");
            $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
            
        },
    }, '.icon-cross-thin-filled');

    });

   //Event for pagination loader
   $(document).on('getPaginationLoader', function (event) {
        loadpagination = 0;
        startIndex = endIndex;
        endIndex += 5;
        reqParams.leftHandLimit = startIndex;
        reqParams.rightHandLimit = endIndex;
        var promiseCall = calendarService.getListAppointments(reqParams);
        promiseCall.then(function onload(data) {
            //data = [];
            console.log(data);
            if (data.data && data.data.length > 0) {
                $('.noMoreData').remove();
                
                //var proccessedData = calendarService.getCalendarData();
                var proccessedData = data.data;
                user_id = calendarService.getUserId();
                for (event in proccessedData) {
                    createCalendarView(proccessedData[event], prevDate).appendTo($calendar);
                    prevDate = proccessedData[event].start_date;
                }
                if(data.data.length<5 && $(".noMoreData").length == 0){
                    $('<div/>').addClass('noMoreData').html(noMoreDataHTML).appendTo($parent);
                }
                loadpagination = 1;
            } else {
                if($(".noMoreData").length > 0) {
                    $(".noMoreData").html(noMoreDataHTML);
                }
                
                event.preventDefault();
            }
        });
    });

    $(document).on('getAllCalendarData', function (event, param1) {
        $calendar.empty();
        $('.spinningGif').show();
        calendarService.getListAppointments(param1).then(function onload(data) {
            // if no data
            if (data.hasOwnProperty('error')) {
                var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                    var reqParam = { 'auth': calendarService.getAuthData()};
                    refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=calendar", '_blank');
                }
            } else if (data.data && data.data.length > 0) {
                $('.spinningGif').hide();
                $('.noMoreData').remove();
                if (search) {
                    $(".no-data").hide();
                }
                    
              //  var prevDate;
                var proccessedData = calendarService.getCalendarData();
                user_id = calendarService.getUserId();
                console.log(proccessedData);
                for (event in proccessedData) {
                    createCalendarView(proccessedData[event], prevDate).appendTo($calendar);
                    putShowMore($messageShowMore,proccessedData[event].id);
                    prevDate = proccessedData[event].start_date;
                }
                if(data.data.length<15 && $(".noMoreData").length == 0){
                    $('<div/>').addClass('noMoreData').html(noMoreDataHTML).appendTo($parent);
                }
                
            }
            else {
                $('.spinningGif').hide();
                if(search) {
                    if( $(".noMoreData").length == 0) {
                        $('<div/>').addClass('noMoreData').html(searchNullHTML).appendTo($parent);
                    }
                    else {
                        $(".noMoreData").html(searchNullHTML);
                    }
                } else {
                    if( $(".noMoreData").length == 0) {
                        $('<div/>').addClass('noMoreData').html(noUpcomingDataHTML).appendTo($parent);
                    }
                    else {
                        $(".noMoreData").html(noUpcomingDataHTML);
                    }
                }
                
            }
        });
    });

    $(document).on('getScheduleMeetings', function (event, param1) {
        $calendar.empty();
        var endDay = moment.unix(param1.end_date_limit / 1000);
        var startDay = moment.unix(param1.start_date_limit / 1000);
        var diffDays = endDay.diff(startDay, 'days');
       // var prevDate;
        var prevDateTimestampObject;
        console.log('getScheduleMeetings>>>>', param1);
        moment().add(6, 'days').endOf('day')
        var i = 0;
        do {
            var timeStamp;
            if (i == 0) {
                timeStamp = startDay.startOf('day');
            }
            else {
                timeStamp = prevDateTimestampObject.add(1, 'days').startOf('day');
            }
            var htmlData = getTimestampData(timeStamp);
            createScheduleView(htmlData, prevDate).appendTo($calendar);
            prevDateTimestampObject = timeStamp;
            prevDate = timeStamp.format('x');
            i++;
        }
        while (i <= diffDays);
    });

    function getTimestampData(pTimestamp) {
        var returnData = {};
        returnData.timestamp = pTimestamp.format('x');
        if (pTimestamp.format('dddd') === 'Saturday') {
            returnData.timeslots = [];
        }
        else if (pTimestamp.format('dddd') === 'Sunday') {
            returnData.timeslots = [];
        }
        else {
            returnData.timeslots = getTimeslots();
        }
        return returnData;
    }

    function getTimeslots() {
        var returnTimeslotsArray = []
        var endTime = window.appConfig.options.calendar.scheduleMeeting.endTime;
        var startTime = window.appConfig.options.calendar.scheduleMeeting.startTime;
        var durationInMinutes = window.appConfig.options.calendar.scheduleMeeting.durationInMinutes;
        var i = 0;
        do {
            var middleEndTime = moment(startTime, 'HH:mm').add(durationInMinutes, 'minutes').format('HH:mm');
            var timeslotObject = {
                starttime: moment(startTime, 'HH:mm').format('hh:mm A'),
                endtime: moment(startTime, 'HH:mm').add(durationInMinutes, 'minutes').format('hh:mm A')
            };
            returnTimeslotsArray.push(timeslotObject);
            startTime = middleEndTime;
            i++;
        }
        while (i < 9);
        return returnTimeslotsArray;
    }

    $('#BroadSearch').keyup(function (e) {
        var key = e.which;
        $this = $(this);
        startIndex = 0;
        endIndex = 15;
        
        console.log($this[0].value.length,key)

        if($this[0].value.trim().length != 0 && key== 8) {        
            $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
        }
        if ($this[0].value.length == 0 && key== 8) {
            var dateParams = thisWeekStartAndEndDate()
            var dateParams = thisWeekStartAndEndDate()
           
            $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');       
            var reqParams = { auth: calendarService.getAuthData(), emailId: calendarService.getEmailId(), start_date_limit: dateParams.startTimestamp, end_date_limit: dateParams.endTimestamp, content: $this[0].value };
            reqParams.leftHandLimit = startIndex
            reqParams.rightHandLimit = endIndex
            $.event.trigger("getAllCalendarData", reqParams);
        }
        if (key == 13) {
            startIndex = 0
            endIndex = 15
            $(".spinningGif").hide();
            $('.noMoreData').remove();
            search = true;
            if ($this[0].value && $this[0].value.trim().length == 0) {
                return 0;
            }
            if ($this[0].value.length >= 3) {
                var dateParams = thisWeekStartAndEndDate()
                var dateParams = thisWeekStartAndEndDate()
                
                var reqParams = { auth: calendarService.getAuthData(), emailId: calendarService.getEmailId(), start_date_limit: dateParams.startTimestamp, end_date_limit: dateParams.endTimestamp, content: $this[0].value };
                reqParams.leftHandLimit = startIndex
                reqParams.rightHandLimit = endIndex
                $.event.trigger("getAllCalendarData", reqParams);
                $('.searchHolder i').removeClass('icon-search').addClass('icon-cross-thin-filled');
            }
            else if ($this[0].value.length === 0) {
                var dateParams = thisWeekStartAndEndDate()
                var dateParams = thisWeekStartAndEndDate()
                var reqParams = { auth: calendarService.getAuthData(), emailId: calendarService.getEmailId(), start_date_limit: dateParams.startTimestamp, end_date_limit: dateParams.endTimestamp, content: $this[0].value };
                reqParams.leftHandLimit = startIndex
                reqParams.rightHandLimit = endIndex
                $.event.trigger("getAllCalendarData", reqParams);
                $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
            }
            else {
                $('.searchHolder i').removeClass('icon-search').addClass('icon-cross-thin-filled');
            }
        }
    });


    $('body').on({
        keydown: function () {
            $this = $(this);
            if ($this[0].value.trim().length == 0) {
                $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
            }

        },
    }, '#BroadSearch');

    // Schedule Meetings funtoionlity 

    var time = new Date();
    startTime = formatAMPM(time);
    var TIME_FORMAT = 'h:mm A'
    endTime = moment().add(30, 'm').format(TIME_FORMAT);
    $('#start-time').val(startTime)
    $('#end-time').val(endTime)
    $('#start-time').timepicker({ 'setTime': new Date(),'timeFormat': 'g:i a',});
    $('#end-time').timepicker({ 'setTime': new Date(),'timeFormat': 'g:i a',});

    $('body').on({
        click: function () {
            $this =  $(this);
            var allSMData = { 'title': $('#titleSM').val(), 'location': $('#locationSM').val(), 'description': $('#descSM').val(), 'start-time': $('#start-time').val(), 'end-time': $('#end-time').val(),'remind':$('#filled-in-box').prop("checked") }
            var emailIds = [];
            $('#attendee-email-list li').each(function(i)
            {
                emailIds.push($( this ).text());
            });
            allSMData.emailIds = emailIds;
            console.log(allSMData);
        },
    }, '#send');


    // end time
    $('body').on({
        click: function () {
            $('#end-time-list').toggle();
            $('#start-time-list').hide();
        },
    }, '#end-time');

    $('body').on({
        click: function () {
             $this =  $(this);
             $('#end-time-list').toggle();
              $("#end-time").html($this[0].innerText+"<span class='icon-arrow-down'></span>")
        },
    }, '.end-time-selected');
    


    // for date 
    $('body').on({
        click: function () {
            var $btn = $('#choose-date').pignoseCalendar({
                modal: true,
                buttons: false,
                click:function(event, context){
                    var date = context.current[0]['_d'].toString().split("00:00:00");
                    console.log("date",date[0])
                    $("#selected-date").html(date[0]);

                }
            }); 
        },
    }, '#choose-date');


    // add email id
    $('body').on({
        click: function () {
            var emailId = $("#attendee-email")[0].value;
             console.log("gfg",$("#attendee-email")[0].value);
            $("#attendee-email-list").append('<li type="square">'+emailId+' <span class="icon-cross2"></span> </li>');
            $("#attendee-email")[0].value = "";
        },
    }, '#add-attendee');

    

    // to delete the email-id
    $('body').on({
        click: function () {
            $(this).remove();
        },
    }, '#attendee-email-list li');
})();
