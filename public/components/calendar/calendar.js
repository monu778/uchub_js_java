(function appClosure() {
    
    $parent = $('#widget');
    $calendar = $('<div/>').addClass('calendarHolder').appendTo($parent);
    var shouldUseMocks = window.appConfig.options.useMocks || false;
    var DATE_FORMAT = 'dddd MMM D';
    var TIME_FORMAT = 'h:mm A';
    var tabValue = "Today";
    var todayTrigger = false;
    var startIndex = 0;
    var endIndex = 15;
    var loadpagination = 1;
    var user_id = '';
    var prevDate = '';
    var iconBarHTML = '<div class="icon-tick accept" title="Accept"></div><div class="icon-cross decline" title="Decline"></div><div class="icon-tentative tentative" title="Tentative"></div>';
    var noMoreDataHTML = '---All items displayed---';
    var noDataHTML = '---No items displayed---';
    var loadingImageHTML = '<img class="loading" src="common/images/loading.gif"/>';
    var searchNullHTML = '<div id="searchNull"><i class="icon-search"></i><span>No items were returned in your search.</span></div>';
    var noDataTodayHTML = '<div class="no-data">No more meetings today. Now you can get some work done!</div>';
    var noDataWeekHTML = '<div class="no-data">No more meetings this week. Now you can get some work done!</div>';
    $messageShowMore = '';
    var $viewShowMore = '';

    function createCalendarView(appointmentData, prevDate) {
        if (shouldUseMocks) {
            var invitationStatusForUser = 1;
        } else {
            var invitationStatusForUser = checkUserAccepted(appointmentData.users, user_id);
        }
        var $view = $('<div/>').addClass('appointmentHolder');
        var organizer = appointmentData.organizer || '';
        var timestamp = appointmentData.start_date;
        var allday = appointmentData.full_time;
        var location = appointmentData.location || '';
        var title = appointmentData.title || '';
        var note = appointmentData.note || '';
        console.log(prevStr)
       // var prevStr = prevDate ? moment.utc(prevDate).format(DATE_FORMAT) || "" : "";
        var prevStr = prevDate ? moment(prevDate).format(DATE_FORMAT) || "" : "";
       // var dateStr = moment.utc(timestamp).format(DATE_FORMAT) || "";
       var dateStr = moment(timestamp).format(DATE_FORMAT) || "";
        console.log(prevStr , dateStr )
        if (appointmentData.folder_id) {
            $('.spinningGif').hide();
        }

        if (prevStr != dateStr && tabValue !== "Today") {
            $('<div/>').addClass('datestamp').text(dateStr).appendTo($view);
        }

        var $appointment = $('<div/>').addClass('appointment').attr({'id':'appointment_'+appointmentData.id}).appendTo($view);
        var $folder = $("<input type=\"hidden\"  value=" + appointmentData.folder_id + " />").attr({'id':'folder_id_' + appointmentData.id}).appendTo($appointment);
        var $appointmentHeader = $('<div/>').addClass('appointmentHeader').attr({'id':'appointmentHeader_'+appointmentData.id,'data-id':appointmentData.id}).appendTo($appointment);

        var $titleHolder = $('<div/>').addClass('titleHolder').attr({'id':'titleHolder_'+appointmentData.id}).appendTo($appointmentHeader);

        
        $('<div/>').addClass('timestamp').text(allday ? 'All Day' : moment(timestamp).format(TIME_FORMAT) || "").appendTo($titleHolder);
        var $title = $('<div/>').addClass('title').text(title || "").attr({'title':title}).appendTo($titleHolder);


        $('<div/>').addClass('location').attr({'id':'location_'+appointmentData.id}).text(location).appendTo($appointmentHeader);
        if(note == " " || note == "") {
            note = "(No Description)";
        }
        var $noteBody = $('<div/>').addClass('noteBody').attr({'id':'noteBody_'+appointmentData.id}).html(note).appendTo($appointmentHeader);
        $messageShowMore = $noteBody;
        var $iconBarHolder = $('<div/>').addClass('iconBarHolder').attr({'id':'iconBarHolder_'+appointmentData.id}).prependTo($appointment);
        var $iconBar = $('<div/>').addClass('iconBar').appendTo($iconBarHolder);
       
        if (invitationStatusForUser == 1) {
            var $acceptIcon = $('<div/>').addClass('icon-check-round-thin-filled accepted').attr({'title':"Accept",'data-id':appointmentData.id,'data-startdate':appointmentData.start_date ,'id':'icon-tick_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar);
        }else{
            var $acceptIcon = $('<div/>').addClass('icon-tick accept').attr({'title':"Accept",'data-id':appointmentData.id, 'data-startdate':appointmentData.start_date , 'id':'icon-tick_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar);
        }

        if(invitationStatusForUser == 2){
            var $declineIcon = $('<div/>').addClass('icon-cross-thin-filled decline').attr({'title':"Decline",'data-id':appointmentData.id,'data-startdate':appointmentData.start_date ,'id':'icon-cross_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar); 
        }else{
            var $declineIcon = $('<div/>').addClass('icon-cross decline').attr({'title':"Decline",'data-id':appointmentData.id,'data-startdate':appointmentData.start_date ,'id':'icon-cross_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar);
        }
       
        if (invitationStatusForUser == 3) {
            var $tentativeIcon = $('<div/>').addClass('icon-support tentatived').attr({'title':"Tentative", 'data-id':appointmentData.id,'data-startdate':appointmentData.start_date ,'id':'icon-tentative_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar);
        }else{
            var $tentativeIcon = $('<div/>').addClass('icon-tentative tentative').attr({'title':"Tentative", 'data-id':appointmentData.id,'data-startdate':appointmentData.start_date ,'id':'icon-tentative_'+appointmentData.id+'_'+appointmentData.start_date}).appendTo($iconBar);
        }

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

    function checkUserAccepted(appointmentUsers, loggedInUserId) {
        for (var i = 0; i < appointmentUsers.length; i++) {
            if (appointmentUsers[i].id == loggedInUserId) {
                return appointmentUsers[i].confirmation;
            }
        }
    }

    function thisWeekStartAndEndDate() {
        //var first = moment().format('x');
        //var first = moment().startOf('day').format('x');
        var first = moment().format('x')-3600000
        var last = moment().add(6, 'days').endOf('day').format('x');
        var data = {
            startTimestamp: first,
            endTimestamp: last
        };
        return data;
    }

    function todayStartAndEnd() {
        //var startOfToday = moment().startOf('day').format('x');
        var startOfToday  = moment().format('x')-3600000;
        var endOfToday = moment().endOf('day').format('x');
        var data = {
            startTimestamp: startOfToday,
            endTimestamp: endOfToday
        };
        return data;
    }

    $(document).ready(function () {
        if(tabValue === 'Today') {
            $('.mail-box-list li:nth-child(1)').addClass('selected');
        }
        var dateObject = todayStartAndEnd();
        dateObject.leftHandLimit = startIndex;
        dateObject.rightHandLimit = endIndex;
        $.event.trigger("getAllCalendarData", dateObject);
        var sticky = navbar.offsetTop;
        var lastScrollTop = 0;
        $('#widget').bind('scroll', function () {
            if (window.pageYOffset >= sticky) {
                $('#navbar').addClass("sticky");
            } else {
                $('#navbar').removeClass("sticky");
            }
            /*var position = $('#widget').scrollTop();
            if (position < lastScrollTop){
                $('.noMoreData').hide();
            }
            lastScrollTop = position;*/
            var bottom = $(document).height() - $('#widget').height();
            /*if (position > 0 && position == bottom && loadpagination == 1) {
                console.log(position + " " + bottom + " " + $(document).height() + "   " + $(window).height());
                $.event.trigger({
                    type: "getPaginationLoader"
                });
            }*/
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
            if ($(this)[0].scrollHeight - Math.round($(this).scrollTop()) == $(this).outerHeight() && loadpagination == 1) {
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
        //Each tab click event
        $('body').on({
            click: function () {
                $this = $(this);
                $('.subTab').removeClass('selected');
                $('#widget').scrollTop(0);
                $this.addClass('selected');
                $('.noMoreData').remove();
                prevDate='';
                tabValue = $(this).text();
                startIndex = 0;
                endIndex = 15;
                loadpagination = 1;
                $('.subitem').removeClass('selected');
                $(".mail-box-btn")[0].children[0].innerHTML = tabValue;
                if (tabValue == "Today") {
                    $('.mail-box-list li:nth-child(1)').addClass('selected');
                    todayTrigger = true;
                    tabValue = "Today";
                    var dateObject = todayStartAndEnd();
                    dateObject.leftHandLimit = startIndex
                    dateObject.rightHandLimit = endIndex;
                    console.log($('#calendarSearch').val());
                    if($('#calendarSearch').val() && $('#calendarSearch').val().length >= 3) {
                        dateObject.content = $('#calendarSearch').val();
                        $.event.trigger("getSearchAppointments", dateObject);
                    }
                    else{
                        $.event.trigger("getAllCalendarData", dateObject);
                    }
                   
                }
                else {
                    $('.mail-box-list li:nth-child(2)').addClass('selected');
                    todayTrigger = false;
                    tabValue = "This Week";
                    var dateObject = thisWeekStartAndEndDate();
                    dateObject.leftHandLimit = startIndex;
                    dateObject.rightHandLimit = endIndex;
                    if($('#calendarSearch').val() && $('#calendarSearch').val().length >= 3) {
                        dateObject.content = $('#calendarSearch').val();
                        $.event.trigger("getSearchAppointments", dateObject);
                    }
                    else {
                        $.event.trigger("getAllCalendarData", dateObject);
                    }
                    
                }
            },
        }, '.subTab');

        $('body').on({
            click: function () {
                $this = $(this);
                var acecptRequest = {
                    objectId: $this[0].dataset.id,
                    folderId: $('input#folder_id_' + $this[0].dataset.id).val(),
                    confirmation: "1",
                    confirmationMessage: ""
                };
                $(this).removeClass("icon-tick accept").addClass("icon-check-round-thin-filled accepted");
                
                if($('#icon-cross_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).hasClass("icon-cross-thin-filled")){
                    $('#icon-cross_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).removeClass("icon-cross-thin-filled");
                    $('#icon-cross_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).addClass("icon-cross");
                }
              
                if ($('#icon-tentative_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).hasClass('icon-support tentatived')) {
                    $('#icon-tentative_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).removeClass("icon-support tentatived").addClass("icon-tentative tentative");
                }
                var acecptRequestPromise = calendarService.updateInvitation(acecptRequest);
                acecptRequestPromise.then(function onLoad(data) {

                }, function onError(data, b, c) {
                    console.log("ERROR Approve!", data, b, c);
                });
            },
        }, '.iconBar .accept');

        $('body').on({
            click: function () {
                $this = $(this);
                var declineRequest = {
                    objectId: $this[0].dataset.id,
                    folderId: $('input#folder_id_' + $this[0].dataset.id).val(),
                    confirmation: "2",
                    confirmationMessage: ""
                };
                $this.removeClass("icon-cross")
                $this.addClass("icon-cross-thin-filled");

                if($('#icon-tentative_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).hasClass("icon-support tentatived")){
                    $('#icon-tentative_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).removeClass(" icon-support tentatived");
                    $('#icon-tentative_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).addClass("icon-tentative tentative");
                }

                if($('#icon-tick_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).hasClass("icon-check-round-thin-filled accepted")){
                    $('#icon-tick_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).removeClass("icon-check-round-thin-filled accepted");
                    $('#icon-tick_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).addClass("icon-tick accept");
                }
                var declineRequestPromise = calendarService.updateInvitation(declineRequest);
                declineRequestPromise.then(function onLoad(data) {

                }, function onError(data, b, c) {
                    console.log("ERROR Approve!", data, b, c);
                });
            },
        }, '.iconBar .decline');

        $('body').on({
            click: function () {
                $this = $(this);
                var tentativeRequest = {
                    objectId: $this[0].dataset.id,
                    folderId: $('input#folder_id_' + $this[0].dataset.id).val(),
                    confirmation: "3",
                    confirmationMessage: ""
                };
                $(this).removeClass("icon-tentative tentative").addClass("icon-support tentatived");


                if($('#icon-cross_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).hasClass("icon-cross-thin-filled")){
                    $('#icon-cross_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).removeClass("icon-cross-thin-filled");
                    $('#icon-cross_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).addClass("icon-cross");
                }

                if ($('#icon-tick_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).hasClass('icon-check-round-thin-filled accepted')) {
                    $('#icon-tick_'+$this[0].dataset.id+'_'+$this[0].dataset.startdate).removeClass("icon-check-round-thin-filled accepted").addClass("icon-tick accept");
                }
                var tentativeRequestPromise = calendarService.updateInvitation(tentativeRequest);
                tentativeRequestPromise.then(function onLoad(data) {

                }, function onError(data, b, c) {
                    console.log("ERROR Approve!", data, b, c);
                });
            },
        }, '.iconBar .tentative');

        $('body').on({
            click: function () {
                $this = $(this);
                console.log("Launch called");
            },
        }, '.iconBar .launch');

        $('body').on({
            mouseenter: function () {
                $this = $(this);
               // $(this).find(".iconBar").toggleClass('hidden');
                $(this).parent().find(".showMore, .showLess").toggleClass('hidden');
               // $this.find(".timestamp").hide();
               $this.find(".iconBar").css('background-color', '#f2f2f2')

            },
            mouseleave: function () {
                $this = $(this);
               // $(this).find(".iconBar").toggleClass('hidden');
                $(this).parent().find(".showMore, .showLess").toggleClass('hidden');
                $this.find(".timestamp").show();
                $this.find(".iconBar").css('background-color', '#fff')
            }
        }, '.appointment');

        //Show more and show less click event
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

    })

    $('#calendarSearch').keypress(function (e) {
        var key = e.which;
        $this = $(this);
        startIndex = 0;
        endIndex = 15;
        prevDate='';
        if (key == 13) {
            startIndex = 0;
            endIndex = 15;
            
            if($this[0].value && $this[0].value.trim().length == 0){
                return 0;
            }
            $('#searchNull').hide();
            if ($this[0].value.length >= 3) {
                $('.searchHolder i').removeClass('icon-search').addClass('icon-cross-thin-filled');
                $('.searchHolder i').attr('id','searchCross');
                if (tabValue == "Today") {
                    var dateObject = todayStartAndEnd();
                    dateObject.leftHandLimit = startIndex;
                    dateObject.rightHandLimit = endIndex; 
                    dateObject.content = $this[0].value;
                    $.event.trigger("getSearchAppointments", dateObject);
                }
                else {
                    var dateObject = thisWeekStartAndEndDate();
                    dateObject.leftHandLimit = startIndex;
                    dateObject.rightHandLimit = endIndex;
                    dateObject.content = $this[0].value;
                    $.event.trigger("getSearchAppointments", dateObject);
                }
            }
            else if ($this[0].value.length === 0) {
                $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
                $('.searchHolder i').attr('id','');
                if (tabValue == "Today") {
                    var dateObject = todayStartAndEnd();
                    dateObject.leftHandLimit = startIndex;
                    dateObject.rightHandLimit = endIndex;
                    $.event.trigger("getAllCalendarData", dateObject);
                }
                else {
                    var dateObject = thisWeekStartAndEndDate();
                    dateObject.leftHandLimit = startIndex;
                    dateObject.rightHandLimit = endIndex;
                    $.event.trigger("getAllCalendarData", dateObject);
                }
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
            $(this).attr('id','');
            $('#calendarSearch').val("");
            startIndex = 0;
            endIndex = 15;
            if (tabValue == "Today") {
                var dateObject = todayStartAndEnd();
                dateObject.leftHandLimit = startIndex;
                dateObject.rightHandLimit = endIndex;
                $.event.trigger("getAllCalendarData", dateObject);
            }
            else {
                var dateObject = thisWeekStartAndEndDate();
                dateObject.leftHandLimit = startIndex;
                dateObject.rightHandLimit = endIndex;
                $.event.trigger("getAllCalendarData", dateObject);
            }
        },
    }, '#searchCross');


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

      //change of Unread or Today
      $('body').on({
        click: function () {
            $('.mail-box-list').hide();
            $('.mail-box-btn :nth-child(2)').removeClass("icon-arrow-up");
            $('.mail-box-btn :nth-child(2)').addClass("icon-arrow-down");
            $('.subTab').removeClass('selected');
            $('.subitem').removeClass('selected');
            $this = $(this);
            $('#widget').scrollTop(0);
            $this.addClass('selected');
            var selectedTabValue = $this[0].childNodes[0].data;
            $(".mail-box-btn")[0].children[0].innerHTML = selectedTabValue;
            startIndex = 0;
            endIndex = 15;
            loadpagination = 1;
            if (selectedTabValue === "Today") {
                $('.subTabs a:nth-child(1)').addClass('selected');
                todayTrigger = true;
                tabValue = "Today";
                var dateObject = todayStartAndEnd();
                dateObject.leftHandLimit = startIndex
                dateObject.rightHandLimit = endIndex;
                if($('#calendarSearch').val() && $('#calendarSearch').val().length >= 3) {
                    dateObject.content = $('#calendarSearch').val();
                    $.event.trigger("getSearchAppointments", dateObject);
                }
                else {
                    $.event.trigger("getAllCalendarData", dateObject);
                }
            }
            else {
                $('.subTabs a:nth-child(2)').addClass('selected');
                todayTrigger = false;
                tabValue = "This Week";
                var dateObject = thisWeekStartAndEndDate();
                dateObject.leftHandLimit = startIndex;
                dateObject.rightHandLimit = endIndex;
                if($('#calendarSearch').val() && $('#calendarSearch').val().length >= 3) {
                    dateObject.content = $('#calendarSearch').val();
                    $.event.trigger("getSearchAppointments", dateObject);
                }
                else {
                    $.event.trigger("getAllCalendarData", dateObject);
                }
            }
          
        },
    }, '.subitem');
    
    $('#calendarSearch').keyup(function (e) {
        $this = $(this);
        if(e.which===8) {

            if($this[0].value.trim().length != 0) {        
                $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
            }
            if ($this[0].value.trim().length == 0) {
                $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
                $('#searchCross').attr('id','');
                startIndex = 0;
                endIndex = 15;
                if (tabValue == "Today") {
                    var dateObject = todayStartAndEnd();
                    dateObject.leftHandLimit = startIndex;
                    dateObject.rightHandLimit = endIndex;
                    $.event.trigger("getAllCalendarData", dateObject);
                }
                else {
                    var dateObject = thisWeekStartAndEndDate();
                    dateObject.leftHandLimit = startIndex;
                    dateObject.rightHandLimit = endIndex;
                    $.event.trigger("getAllCalendarData", dateObject);
                }
            }
        }
    });


    // Get all mail event listener
    $(document).on('getAllCalendarData', function (event, param1) {
        $calendar.empty();
        $('.spinningGif').show();
        calendarService.getListAppointments(param1).then(function onload(data) {
            if(data.hasOwnProperty('error')){
                var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1){
                        var reqParam = { 'auth': calendarService.getAuthData().authData};
                        //refreshSession.getRefreshSession(reqParam);
                        refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth="+reqParam.auth+"&type=calendar",'_blank');
                    }
            }else if(data.data && data.data.length > 0){
                $('.spinningGif').hide();
                $('.noMoreData').remove();
                //var prevDate;
                var proccessedData = calendarService.getCalendarData();
                user_id = calendarService.getUserId();
                for (event in proccessedData) {
                    createCalendarView(proccessedData[event], prevDate).appendTo($calendar);
                    putShowMore($messageShowMore,proccessedData[event].id);
                    prevDate = proccessedData[event].start_date;
                }  
                if(data.data.length<15 && $(".noMoreData").length == 0){
                    $('<div/>').addClass('noMoreData').html(noMoreDataHTML).appendTo($parent);  
                }
            }
            else{
                if(tabValue == "Today"){
                    if( $(".noMoreData").length == 0) {
                        $('<div/>').addClass('noMoreData').html(noDataTodayHTML).appendTo($parent);
                    }
                    else {
                        $(".noMoreData").html(noDataTodayHTML);
                    }
                }
                else{
                    if( $(".noMoreData").length == 0) {
                        $('<div/>').addClass('noMoreData').html(noDataWeekHTML).appendTo($parent);
                    }
                    else {
                        $(".noMoreData").html(noDataWeekHTML);
                    }
                }
                $('.spinningGif').hide();
            }
        });
    });


    //Search Event 
    $(document).on('getSearchAppointments', function (event, param1) {
        $calendar.empty();
        $('.noMoreData').remove();
        $('.spinningGif').show();
        calendarService.getSearchAppointments(param1).then(function onload(data) {
            if(data.hasOwnProperty('error')){
                var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1){
                        var reqParam = { 'auth': calendarService.getAuthData().authData};
                        //refreshSession.getRefreshSession(reqParam);
                        refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth="+reqParam.auth+"&type=calendar",'_blank');
                    }
            }else if(data.data && data.data.length > 0){
                $('.spinningGif').hide();
                var proccessedData = calendarService.getCalendarData();
                user_id = calendarService.getUserId();
                for (event in proccessedData) {
                    createCalendarView(proccessedData[event], prevDate).appendTo($calendar);
                    putShowMore($messageShowMore,proccessedData[event].id);
                    prevDate = proccessedData[event].start_date;
                }  
                if(data.data.length<15 && $(".noMoreData").length == 0){
                    $('<div/>').addClass('noMoreData').html(noMoreDataHTML).appendTo($parent);
                }
            }
            else{
                $('.spinningGif').hide();
                if($(".noMoreData").length == 0) {
                    $('<div/>').addClass('noMoreData').html(searchNullHTML).appendTo($parent);
                }
                else{
                    $(".noMoreData").html(searchNullHTML);
                } 
            }
        });
    });

    //Event for pagination loader
    $(document).on('getPaginationLoader', function (event) {
        console.log('Search prevDate>>>', prevDate);
        startIndex = endIndex;
        endIndex += 5;
        loadpagination = 0;
        //using common service to different tabs
        var serviceToCall = null;
        switch (tabValue) {
            case "This Week":
                var dateObject = thisWeekStartAndEndDate();
                dateObject.leftHandLimit = startIndex;
                dateObject.rightHandLimit = endIndex;
                if($('#calendarSearch').val() && $('#calendarSearch').val().length >= 3) {
                    dateObject.content = $('#calendarSearch').val();
                    //$.event.trigger("getSearchAppointments", dateObject);
                    serviceToCall = calendarService.getSearchAppointments(dateObject);
                }
                else {
                    serviceToCall = calendarService.getListAppointments(dateObject);
                  //  $.event.trigger("getAllCalendarData", dateObject);
                }
                //serviceToCall = calendarService.getListAppointments(dateObject);
                break;
            case "Today":
                var dateObject = todayStartAndEnd();
                dateObject.leftHandLimit = startIndex;
                dateObject.rightHandLimit = endIndex;
                if($('#calendarSearch').val() && $('#calendarSearch').val().length >= 3) {
                    dateObject.content = $('#calendarSearch').val();
                  //  $.event.trigger("getSearchAppointments", dateObject);
                  serviceToCall = calendarService.getSearchAppointments(dateObject);
                }
                else {
                  //  $.event.trigger("getAllCalendarData", dateObject);
                    serviceToCall = calendarService.getListAppointments(dateObject);
                }
                //
                break;
        }
        console.log("serviceToCall>>", serviceToCall);
       serviceToCall.then(function onload(data) {
            //data = [];
           
            if(data.hasOwnProperty('error')){
                var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1){
                        var reqParam = { 'auth': calendarService.getAuthData().authData};
                        refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth="+reqParam.auth+"&type=calendar");
                    }
            }
            else if (data.data && data.data.length > 0) {
                $('.noMoreData').remove();
                //var proccessedData = calendarService.getCalendarData();
                var proccessedData = data.data;
                console.log("This is proccessedData", proccessedData);
                //var prevDate;
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

        //reqParams.leftHandLimit = startIndex;
        //reqParams.rightHandLimit = endIndex;
    });

})();