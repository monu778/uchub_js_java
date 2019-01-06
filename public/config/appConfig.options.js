window.appConfig = {
    options: {
        useMocks: window.location.hash === '#mock' || false,
        language: 'en',
        loadTabUrl:'https://orangehubservices.mpsvcs.com/hubservices/',
        mail: {
            baseApiUrl:"https://orangehubservices.mpsvcs.com/hubservices/rest/",
            btbcApiUrl:'https://localhost.ucclient.net:5506/connect?t='
        },
        calendar: {
            baseApiUrl:"https://orangehubservices.mpsvcs.com/hubservices/rest/",
            btbcApiUrl:'https://localhost.ucclient.net:5506/connect?t=',
            daysToDisplay: 5,
            scheduleMeeting: {
                startTime: '08.00',
                endTime: '17.00',
                durationInMinutes: 60
            }
        },
        drive: {
            baseApiUrl:"https://orangehubservices.mpsvcs.com/hubservices/rest/"
        }
    }
};