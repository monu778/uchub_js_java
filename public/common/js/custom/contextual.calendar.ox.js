var calendarService = (function() {

    'use strict';

    var shouldUseMocks = window.appConfig.options.useMocks || false;
    var baseApiUrl = window.appConfig.options.calendar.baseApiUrl;

    var CALENDAR_FIELD_CODES = {
        /*id: 1,
        created_by: 2,
        modified_by: 3,
        creation_date: 4,
        last_modified: 5,
        categories: 100,
        title: 200,
        start_date: 201,
        end_date: 202,
        note: 203,
        alarm: 204,
        recurrence_id: 206,
        recurrence_position: 207,
        recurrence_date_position: 208,
        recurrence_type: 209,
        change_exceptions: 210,
        delete_exceptions: 211,
        days: 212,
        day_in_month: 213,
        month: 214,
        interval: 215,
        until: 216,
        notification: 217,
        participants: 220,
        users: 221,
        uid: 223,
        organizer: 224,
        confirmations: 226,
        principal: 228,
        location: 400,
        full_time: 401,
        timezone: 408,
        recurrence_start: 410*/
        id:1,
        created_by:2,
        modified_by:3,
        creation_date:4,
        last_modified:5,
        folder_id:20,
        categories:100,
        private_flag:101,
        color_label:102,
        title:200,
        start_date:201,
        end_date:202,
        note:203,
        alarm:204,
        recurrence_type:209,
        days:212,
        day_in_month:213,
        month:214,
        interval:215,
        until:216,
        notification:217,
        participants:220,
        users: 221
    }

    // Self is reference to traditional 'this' scope considering we lost access to 'this' by following JS revealing mobule pattern
    var self = {
        calendarData: [],
        authData: '',
        user_id: ''
    }

    var setCalendarData = function(calendarData) {
        self.calendarData.push(calendarData);
    }

    var setAuthData =  function(data){
        self.authData = data;
    }
 
    var getAuthData = function(){
        return self.authData;
    }

    var setEmailId = function (data) {
        self.emailId = data;
    }

    var getEmailId = function () {
        return self.emailId;
    }

    var setUserId = function(userId) {
        self.user_id = userId;   
    }

    var getUserId = function() {
        return self.user_id;
    }

    var getCalendarData = function() {
        return self.calendarData;
    }

    var getCalendarFieldCodesString = function() {
        var arrayOfCodes = [];

        for (var code in CALENDAR_FIELD_CODES) {
            arrayOfCodes.push(CALENDAR_FIELD_CODES[code]);
        }
        return arrayOfCodes.toString();
    }

    var getCalendarFieldNamesArray = function() {
        var arrayOfNames = [];

        for (var name in CALENDAR_FIELD_CODES) {
            arrayOfNames.push(name);
        }
        return arrayOfNames;
    }

    var getListAppointments = function(passedData) {
        self.calendarData = [];
        if (shouldUseMocks) {
            var testingArray = getCalendarFieldNamesArray();
            console.log(testingArray);
            var getListAppointmentsPromise = $.ajax({
                url: 'test/calendar/all_c.json'
            });
        } else {
            // var requestData = {
            //     auth: getAuthData().authData,
            //     start_date_limit: passedData.startTimestamp,
            //     end_date_limit: passedData.endTimestamp,
            //     leftHandLimit: passedData.leftHandLimit,
            //     rightHandLimit: passedData.rightHandLimit
            // }
            var getListAppointmentsPromise = $.ajax({
                method: 'POST',
                crossDomain: true,
                url: baseApiUrl + 'calendarservice/searchcalendarcontextual',
                data: JSON.stringify(passedData),
                dataType: "json",
                contentType : "application/json",
                success:function(data,textStatus,request) {
                    setUserId(request.getResponseHeader('user_id'));
                    return data;
                }
            });
        }

        getListAppointmentsPromise.then(function onLoad(data) {
            var i, j, row;
            //console.log(request.getResponseHeader('user_id'));
            for (i in data.data) {
                row = data.data[i];
                var appointmentHeader = row;
                appointmentHeader.index = (+i) + 1 ;
                setCalendarData(appointmentHeader);
            }

        }, function onError(data, b, c) {
            console.log("ERROR getMessage!", data, b, c);
        });

        return getListAppointmentsPromise;
    }

    var updateInvitation = function(requestData) {
        requestData.auth = getAuthData();
        if (shouldUseMocks) {
            var updateInvitationRequest = $.ajax({
                url: 'test/calendar/all_c.json'
            });   
        } else {
            var updateInvitationRequest = $.ajax({
                method: 'POST',
                crossDomain: true,
                url:baseApiUrl+'calendarservice/updateinvite',
                data: JSON.stringify(requestData),
                dataType: 'json',
                contentType : "application/json"
            });
        }
        return updateInvitationRequest; 
    }

    return {
        getListAppointments: getListAppointments,
        getCalendarData : getCalendarData,
        setAuthData:setAuthData,
        getAuthData:getAuthData,
        setEmailId:setEmailId,
        getEmailId:getEmailId,
        updateInvitation:updateInvitation,
        getUserId:getUserId
    }

})();