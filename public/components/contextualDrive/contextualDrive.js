(function appClosure() {
    //var ox = hub.openExchange;
    $parent = $('#widget');
    $drive = $('<div/>').addClass('driveHolder').appendTo($parent);
    var modal = document.getElementById('myModal');
    var modal1 = document.getElementById('myModal1');
    var currentFolder = '';
    var currentFile = '';
    var isFileShare = '';
    var deleteFile = 0;

    var DATE_FORMAT = 'dddd MMM D';
    var TIME_FORMAT = 'h:mm A';
    var tabValue = "My Files";
    var file;
    var currentFolder = "";
    var subTabSelected = "My Files";
    var activeTab = "My Files";
    var startIndex = 0;
    var endIndex = 15;
    var loadpagination = 1;
    var sharedFolderId = "";
    var search;
    var searchValue = "";
    var prevDate = "";
    var baseApiUrl = window.appConfig.options.drive.baseApiUrl;
    var listOfFiles = [];
    var prevDate = '';

    if ($('#searchNull').css('display') == 'block') {
        $('#searchNull').hide();
    }
    function getHeaderView(hdr,prevDate) {
        console.log('Drive Header View>>>>', hdr);
        var timestamp = hdr.lastModified;
        var prevStr = prevDate ? moment(prevDate).format(DATE_FORMAT) || "" : "";
        var dateStr = moment(timestamp).format(DATE_FORMAT) || "";
        var $view = $('<div/>').addClass('driveContentHolder');
        if (prevStr != dateStr) {
            $('<div/>').addClass('datestamp').text(dateStr).appendTo($view);
        }
        var $driveCard = $('<div/>').addClass('driveCard').appendTo($view);
        
        var $driveCardHolderDOM = $('<div/>').addClass('driveCardHolderDOM').appendTo($driveCard);
        var $driveLogo = $('<div/>').addClass('driveLogo').appendTo($driveCardHolderDOM);
        
        if (hdr.fileMIMEtype) {
            var fileType = hdr.fileMIMEtype;
            var extension =  hdr.fileName.split(".");
            if(extension[extension.length-1] == "png" || extension[extension.length-1] == "PNG" || extension[extension.length-1] == "jpg" || extension[extension.length-1] == "JPG" || extension[extension.length-1] == "jpeg"){
                fileType =  "image/png";
            }else if(extension[extension.length-1] == "pdf" ){
                fileType =  "application/pdf";
            }else if(extension[extension.length-1] == "zip" || extension[extension.length-1] == "rar" || extension[extension.length-1] == "bzip"){
                fileType =  "application/x-zip-compressed";
            }


            switch (fileType) {
                case 'application/pdf': var $fileFolderIcon = '<i class="folderIcon icon-file-pdf"></i>'; break;
                case 'image/png': 
                    // var $fileFolderIcon = '<i class="folderIcon icon-file-img"></i>'; break;
                case 'image/jpeg': 
                    // var $fileFolderIcon = '<i class="folderIcon icon-file-img"></i>'; break;
                case 'image/gif': 
                    // var $fileFolderIcon = '<i class="folderIcon icon-file-img"></i>'; break;
                case 'image/x-icon': 
                    var $fileFolderIcon = '<i class="folderIcon icon-file-img"></i>'; break;
                case 'application/x-zip-compressed': var $fileFolderIcon = '<i class="folderIcon icon-file-archive"></i>'; break;
                case 'application/x-rar-compressed': var $fileFolderIcon = '<i class="folderIcon icon-file-archive"></i>'; break;
                case 'application/x-bzip': var $fileFolderIcon = '<i class="folderIcon icon-file-archive"></i>'; break;
                case 'application/java-archive': var $fileFolderIcon = '<i class="folderIcon icon-file-archive"></i>'; break;
                case 'text/plain': var $fileFolderIcon = '<i class="folderIcon icon-file-text"></i>'; break;
                case 'application/msword': var $fileFolderIcon = '<i class="folderIcon icon-file-doc"></i>'; break;
                case 'application/rtf': var $fileFolderIcon = '<i class="folderIcon icon-file-doc"></i>'; break;
                case 'application/vnd.openxmlformats-officedocument.wordprocessingml.document': var $fileFolderIcon = '<i class="folderIcon icon-file-doc"></i>'; break;
                case 'application/vnd.ms-excel': var $fileFolderIcon = '<i class="folderIcon icon-file-xls"></i>'; break;
                case 'text/csv': var $fileFolderIcon = '<i class="folderIcon icon-file-xls"></i>'; break;
                case 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': var $fileFolderIcon = '<i class="folderIcon icon-file-xls"></i>'; break;
                case 'application/vnd.ms-powerpoint': var $fileFolderIcon = '<i class="folderIcon icon-file-ppt"></i>'; break;
                case 'application/vnd.openxmlformats-officedocument.presentationml.presentation': var $fileFolderIcon = '<i class="folderIcon icon-file-ppt"></i>'; break;
                default: var $fileFolderIcon = '<i class="folderIcon icon-file-generic"></i>'; break;
            }
        }
        else {
            var $fileFolderIcon = '<i class="folderIcon icon-box-folder"></i>';
        }
        var $driveContentLogo = $('<div/>').addClass('driveContentLogo').html($fileFolderIcon).appendTo($driveLogo);
        var $drive = $('<div/>').addClass('drive').appendTo($driveCardHolderDOM);

        var $iconBarHolder = $('<div/>').addClass('iconBarHolder').appendTo($drive);
        var $iconBar = $('<div/>').addClass('iconBar iconBarColor').appendTo($iconBarHolder);
        var $shareIcon = $('<div/>').addClass('icon-box-share share').attr("title", "Share").appendTo($iconBar);
        // var $starIcon = $('<div/>').addClass('icon-box-star-o star').attr("title", "Add Star").appendTo($iconBar);
     //   var trashIcon = $('<div/>').addClass('icon-box-trash trash').attr("title", "Move to Trash").appendTo($iconBar);
        var $driveHeader = $('<div/>').addClass('driveHeader').appendTo($drive);
        if (hdr.fileName) {
            //var $elipses = $('<div/>').addClass("elipsesSupport").html("E").appendTo($driveHeader);
            var $driveFile = $('<div/>').addClass('driveFile').text(hdr.fileName).attr("title", hdr.fileName).attr({ 'data-trash': hdr.fileObjectID, 'data-id': hdr.parentFolderId, 'data-timestamp': hdr.timestamp }).appendTo($driveHeader);
         /*   trashIcon.attr({ 
                'data-trash': hdr.fileObjectID, 
                'data-id': hdr.parentFolderId, 
                'data-timestamp': hdr.timestamp,
                'data-filename':hdr.fileName 
            }); */
            $shareIcon.attr({ 
                'data-isFileShare': true, 
                'data-folderShare': hdr.parentFolderId, 
                'data-fileShare': hdr.fileObjectID,
                'data-filename':hdr.fileName 
            });
            $view.attr({ 'data-trash': hdr.fileObjectID });
            var apiURL = baseApiUrl+'driveservice/downloadfile/'+hdr.fileName+"/"+hdr.version+"/"+hdr.createdUserId+"/"+hdr.timestamp+"?auth="+driveService.getAuthData()+"&fileObjectID="+hdr.fileObjectID+"&folderid="+hdr.parentFolderId;
            var huburl = "hub://openUrl?url=" + encodeURIComponent(apiURL);
            var ahref_link = "<a href=" + huburl + "></a>";
            console.log(ahref_link);
            var $downloadIcon = $('<div/>').addClass('icon-box-download').attr("title", "Download File").attr({
                'data-download': hdr.fileName, 
                'data-timestamp': hdr.timestamp, 
                'data-objectId': hdr.fileObjectID, 
                'data-version': hdr.version, 
                'data-create':hdr.createdUserId, 
                'data-folder': hdr.parentFolderId
            }).html(ahref_link).appendTo($iconBar);

        }
        else {
            //var $elipses = $('<div/>').addClass("elipsesSupport").html("E").appendTo($driveHeader);
            var $driveFile = $('<div/>').addClass('driveFile driveFolder').text(hdr.folder_name).attr("title", hdr.folder_name).attr({ 'id': 'driveHeader_' + hdr.folder_id, 'data-id': hdr.folder_id, 'data-create': hdr.createdUserId }).appendTo($driveHeader);
          /*  trashIcon.attr({ 
                'data-trash': hdr.fileObjectID, 
                'data-id': hdr.folder_id,
                'data-folderName':hdr.folder_name
            }); */
            $shareIcon.attr({ 
                'data-isFileShare': false, 
                'data-folderShare': hdr.folder_id, 
                'data-folderName':hdr.folder_name
            });
        }
        var timestamp = hdr.lastModified;
        var driveDate = moment(timestamp).utcOffset(0).format(TIME_FORMAT);
        var $driveTime = $('<div/>').addClass('driveTime').text(driveDate).attr("title", "Modified time: " + driveDate).appendTo($driveCardHolderDOM);

        return $view;
    }


    
    function copyToClipboard(elem) {
        setTimeout(function () {

        }, 2000);
        // create hidden text element, if it doesn't already exist
        var targetId = "_hiddenCopyText_";
        var isInput = elem.tagName === "INPUT" || elem.tagName === "TEXTAREA";
        var origSelectionStart, origSelectionEnd;
        if (isInput) {
            // can just use the original source element for the selection and copy
            target = elem;
            origSelectionStart = elem.selectionStart;
            origSelectionEnd = elem.selectionEnd;
        } else {
            // must use a temporary form element for the selection and copy
            target = document.getElementById(targetId);
            if (!target) {
                var target = document.createElement("textarea");
                target.style.position = "absolute";
                target.style.left = "-9999px";
                target.style.top = "0";
                target.id = targetId;
                document.body.appendChild(target);
            }
            target.textContent = elem.textContent;
        }
        // select the content
        var currentFocus = document.activeElement;
        target.focus();
        target.setSelectionRange(0, target.value.length);

        // copy the selection
        var succeed;
        try {
            succeed = document.execCommand("copy");
        } catch (e) {
            succeed = false;
        }
        // restore original focus
        if (currentFocus && typeof currentFocus.focus === "function") {
            currentFocus.focus();
        }

        if (isInput) {
            // restore prior selection
            elem.setSelectionRange(origSelectionStart, origSelectionEnd);
        } else {
            // clear temporary content
            target.textContent = "";
        }
        $('#copy').html('COPY');
        return succeed;
    }

    function removeEmptyString(arr) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] == "") {
                arr.splice(i, 1);
            }
        }
        return arr;
    }

    function searchOutsideMailIds(arr) {
        var returnBoolean = [];
        for (var i = 0; i < arr.length; i++) {
            if(validateEmail(arr[i])) {
                if(!/(@ucc.teaming.fr)/.test(arr[i])) {
                    returnBoolean.push(arr[i]);
                }
            }
            else {
                returnBoolean.push(arr[i]);
            }
            
        }
        return returnBoolean;
    }

    function validateEmail(email) {
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    }

    //on load scroll pagination event trigger and get My Files mail event
    $(document).ready(function () {
        var serviceToCall = 'My Files';
        reqParams = { auth: driveService.getAuthData(), leftHandLimit: startIndex, rightHandLimit: endIndex };
        $.event.trigger("getAllDriverFiles", reqParams);
    });

    $(document).find()
    $(function () {
        //Each tab click event
        $('body').on({
            click: function () {
                $this = $(this);
                startIndex = 0;
                endIndex = 15;
                $(window).scrollTop(0);
                $('.mail-box-list').hide();
                $('.subTab').removeClass('selected');
                $this.addClass('selected');
                tabValue = $(this).text();
                $(".mail-box-btn")[0].children[0].innerHTML = tabValue;
                subTabSelected = $(this).text();
                searchValue = $(document).find("#BroadSearch").val();
                if(searchValue == ""){
                    if (tabValue == "Recent") {
                        $('.subTabs a:nth-child(2)').addClass('selected');
                        activeTab = "Recent"
                        loadpagination = 1;
                        reqParams = { auth: driveService.getAuthData(), order: 'desc',leftHandLimit:startIndex,rightHandLimit:endIndex };
                        $.event.trigger("recentDriverFiles", reqParams);
                    }
                    else if (tabValue == "Shared With Me") {
                        $('.subTabs a:nth-child(3)').addClass('selected');
                        activeTab = "Shared With Me";
                        reqParams = { auth: driveService.getAuthData(),leftHandLimit:startIndex,rightHandLimit:endIndex };
                        $.event.trigger("sharedWithMeDriverFiles", reqParams);
                    }
                    else {
                        
                        activeTab = "My Files";
                        $('.subTabs a:nth-child(1)').addClass('selected');
                        loadpagination = 1;                    
                        reqParams = { auth: driveService.getAuthData(), leftHandLimit: startIndex, rightHandLimit: endIndex };
                        $.event.trigger("getAllDriverFiles", reqParams);
                    }
                } else {
                    startIndex=0;
                    endIndex=15;
                    if(subTabSelected == "Shared With Me"){
                        reqParams = { auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(), folder: "10",leftHandLimit: startIndex, rightHandLimit: endIndex};
                        if(sharedFolderId){
                            reqParams = { auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(), folder: sharedFolderId,leftHandLimit: startIndex, rightHandLimit: endIndex};
                            sharedFolderId = "";
                        }
                        $.event.trigger("searchDriverFiles", reqParams);
                    }else if(subTabSelected == "My Files"){
                        reqParams = { auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(),leftHandLimit: startIndex, rightHandLimit: endIndex};
                        if(sharedFolderId){
                            reqParams = { auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(), folder: sharedFolderId,leftHandLimit: startIndex, rightHandLimit: endIndex};
                            sharedFolderId = "";
                        }
                        $.event.trigger("searchDriverFiles", reqParams);
                    }else{
                        reqParams = { auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(),leftHandLimit: startIndex, rightHandLimit: endIndex};
                        $.event.trigger("searchDriverFiles", reqParams);
                    }
                }
            },
        }, '.subTab');

        //On list mouse enter show more and show less 
        /*$('body').on({
            mouseenter: function () {
                //$(this).find(".iconBar").toggleClass('hidden');
                $(this).find(".elipsesSupport").css("padding-left","40px");
            },
            mouseleave: function () {
                //$(this).find(".iconBar").toggleClass('hidden');
                $(this).find(".elipsesSupport").css("padding-left","0px");
            }
        }, '.driveContentHolder');*/

        $('body').on({
            click: function () {
                $("#fileButton").toggleClass("toggleDisplay");
                $("#folderButton").toggleClass("toggleDisplay");
            },
        }, '#uploadButton');

        $('body').on({
            click: function () {
                $this = $(this);
                $('#shareLinkError').hide();
                var allData = $this[0].dataset;
                if(allData.filename) {
                    $('#dynamic-title').html(allData.filename);
                }
                else{
                    $('#dynamic-title').html(allData.foldername);
                }
                var reqParams = { 'auth': driveService.getAuthData() }
                if (allData.isfileshare === "true") {
                    reqParams.folder = allData.foldershare;
                    reqParams.fileObjectID = allData.fileshare;
                    currentFile = reqParams.fileObjectID;
                    //$('#myModal').attr({'data-folder':allData.foldershare,'data-fileObjectID':allData.fileshare});
                    currentFolder = reqParams.folder
                    isFileShare = true;
                } else {
                    reqParams.folder = allData.foldershare;
                    currentFolder = reqParams.folder;
                    isFileShare = false;
                    //$('#myModal').attr({'data-folder':allData.foldershare});
                }
                driveService.getShareLink(reqParams).then(function onLoad(data) {
                    if (data.data) {
                        $("#send").html('SEND');
                        $('#myModal').show();
                        var data = data.data;
                        $('#shareLink').val(data.url);
                    }
                });
            },
        }, '.share');

        $('body').on({
            click: function () {
                $("#send").addClass('sending');
                $("#send").html('SENDING...');
                var toEmailIds = $("#shareEmail").val().split(",");
                toEmailIds = removeEmptyString(toEmailIds);
                var resultSearch = searchOutsideMailIds(toEmailIds);
                if(resultSearch.length > 0) {
                    $('#shareEmail').addClass('error-share');
                    $('#shareLinkError').show();
                    $("#send").removeClass('sending');
                    $("#send").html('SEND');
                } else {
                    var reqParams = { 'auth': driveService.getAuthData(), 'recipients': toEmailIds };
                    if (isFileShare) {
                        reqParams.fileObjectID = currentFile;
                        reqParams.folder = currentFolder;
                        for (let index = 0; index < listOfFiles.length; index++) {
                            if(listOfFiles[index].fileObjectID == reqParams.fileObjectID){
                              console.log("listOfFiles[index]",listOfFiles[index]);
                              reqParams.objectPermissions = listOfFiles[index].objectPermissions;
                              reqParams.timestamp = listOfFiles[index].timestamp;
                              break;
    
                            }
                          
                      }
                    } else {
                        reqParams.folder = currentFolder;
                    }
                    driveService.sendShareLink(reqParams).then(function onLoad(data) {
                        if (data.data) {
                            $("#send").html('SEND');
                            $("#send").css("background-color", "#03AFEF");
                            $("#shareEmail").val('');
                            $('#shareLink').val('');
                            currentFile = '';
                            currentFolder = '';
                            isFileShare = '';
                            modal.style.display = "none";
                        }
                    });
                }
            },
        }, '#send');

        $('body').on({
            click: function (e) {
                $(this).html('COPIED');
                copyToClipboard(document.getElementById("shareLink"));
            }
        }, '#copy');

        $('body').on({
            click: function (e) {
                $('#shareLinkError').hide();
                $('#shareEmail').removeClass('error-share');
                shareLinkError.style.display = "none"  
                modal.style.display = "none";
                $("#shareEmail").val('');
                $('#shareLink').val('');
            }
        }, '#close,#dismiss');
        $('body').on({
            click: function (e) {
                modal1.style.display = "none";
                deleteFile = 0;
            }
        }, '#close1,#dismiss1');

        $('#shareEmail').keyup(function(){
            if($(".error-share").length > 0){
                $(this).removeClass('error-share');
            }
            $('#shareLinkError').hide();
        });

        $('body').on({
            click: function (e) {
                if (subTabSelected == "My Files") {
                    reqParams = { auth: driveService.getAuthData(), folder: e.target.dataset.id };
                    $.event.trigger("getAllDriverFiles", reqParams);
                    currentFolder = e.target.dataset.id;
                    sharedFolderId = e.target.dataset.id;
                } else if (subTabSelected == "Shared With Me") {
                    reqParams = { auth: driveService.getAuthData(), folder: e.target.dataset.id, createdUserId: e.target.dataset.create };
                    $.event.trigger("sharedWithMeDriverFiles", reqParams);
                    currentFolder = e.target.dataset.id;
                    sharedFolderId = e.target.dataset.id;
                }
            }
        }, '.driveFolder');

        //click of search cross icon
    $('body').on({
        click: function () {
            $(this).removeClass('icon-cross-thin-filled').addClass('icon-search');
            $('#BroadSearch').val("");
            
            startIndex = 0;
            endIndex = 15;
            if (activeTab == "Recent") {
                $('.subTabs a:nth-child(2)').addClass('selected');
                activeTab = "Recent"
                loadpagination = 1;
                reqParams = { auth: driveService.getAuthData(), order: 'desc' };
                $.event.trigger("recentDriverFiles", reqParams);
            }
            else if (activeTab == "Shared With Me") {
                $('.subTabs a:nth-child(3)').addClass('selected');
                activeTab = "Shared With Me";
                reqParams = { auth: driveService.getAuthData() };
                $.event.trigger("sharedWithMeDriverFiles", reqParams);
            }
            else {
                startIndex = 0;
                endIndex = 15;
                activeTab = "My Files";
                $('.subTabs a:nth-child(1)').addClass('selected');
                loadpagination = 1;                    
                reqParams = { auth: driveService.getAuthData(), leftHandLimit: startIndex, rightHandLimit: endIndex };
                $.event.trigger("getAllDriverFiles", reqParams);
            }
            
            $.event.trigger({
                type: "showSpinningDiv"
            });
            $.event.trigger("getAllMail", reqParams);
        },
    }, '.icon-cross-thin-filled');

        $('body').on({
            click: function (e) {
                $this = $(this);
                var trashID = $(this).attr('data-trash');

                if(trashID == undefined) {
                    $('#delete_text').text("Are you sure you want to delete the folder and all the files/folders in it?")
                } else {
                    $('#delete_text').text("Are you sure you want to delete this file?")
                }
                $('#myModal1').show()
                
                $('#delete').click(function() { 
                    $('#myModal1').hide();
                    $this.parent().parent().parent().parent().remove();
                    reqParams = [{ auth: driveService.getAuthData(), fileObjectID: trashID, folder: e.target.dataset.id, timestamp: e.target.dataset.timestamp }];
                    $.event.trigger("deleteDriverFiles", reqParams);
                   
                });
            }
        }, '.trash');

        
        // $('body').on({
        //     click: function (e) {
        //         reqParams = [{ auth: driveService.getAuthData(), fileName: e.target.dataset.download, timestamp: e.target.dataset.timestamp, createdUserId: e.target.dataset.create, version: e.target.dataset.version, fileObjectID: e.target.dataset.objectid, folder: e.target.dataset.folder }];
        //         $.event.trigger("downloadAttachment", reqParams);
        //     }
        // }, '.icon-box-download');

        $('body').on({
            click: function (e) {

                reqParams = [{ auth: driveService.getAuthData(), fileName: e.target.dataset.download, timestamp: e.target.dataset.timestamp, createdUserId: e.target.dataset.create, version: e.target.dataset.version, fileObjectID: e.target.dataset.objectid, folder: e.target.dataset.folder }];
                $.event.trigger("downloadAttachment", reqParams);
                $(this)[0].children[0].click();
            }
        }, '.icon-box-download');
        

        $('body').on({
            click: function (e) {
                if(subTabSelected == "Shared With Me"){
                    reqParams = [{ auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(), folder: "10"}];
                    if(sharedFolderId){
                        reqParams = [{ auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(), folder: sharedFolderId}];
                        sharedFolderId = "";
                    }
                    $.event.trigger("searchDriverFiles", reqParams);
                }else if(subTabSelected == "My Files"){
                    reqParams = [{ auth: driveService.getAuthData(), fileName: $('#BroadSearch').val()}];
                    if(sharedFolderId){
                        reqParams = [{ auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(), folder: sharedFolderId}];
                        sharedFolderId = "";
                    }
                    $.event.trigger("searchDriverFiles", reqParams);
                }else{
                    reqParams = [{ auth: driveService.getAuthData(), fileName: $('#BroadSearch').val()}];
                    $.event.trigger("searchDriverFiles", reqParams);
                }
            }
        }, '.icon-search');

        $('#BroadSearch').keypress(function (e) {
            var key = e.which;
            $this = $(this);
            if (key == 13) {
                startIndex = 0;
                endIndex =15;
                search = true;
                if ($this[0].value && $this[0].value.trim().length == 0) {
                    return 0;
                }
                $('#searchNull').hide();
                if ($this[0].value.length >= 3) {
                    if(subTabSelected == "Shared With Me"){
                        reqParams = [{ auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(), folder: "10",leftHandLimit:startIndex,rightHandLimit:endIndex}];
                        if(sharedFolderId){
                            reqParams = [{ auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(), folder: sharedFolderId,leftHandLimit:startIndex,rightHandLimit:endIndex}];
                            sharedFolderId = "";
                        }
                        $.event.trigger("searchDriverFiles", reqParams);
                        $('.searchHolder i').removeClass('icon-search').addClass('icon-cross-thin-filled');
                    }else if(subTabSelected == "My Files"){
                        reqParams = [{ auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(),leftHandLimit:startIndex,rightHandLimit:endIndex}];
                        if(sharedFolderId){
                            reqParams = [{ auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(), folder: sharedFolderId,leftHandLimit:startIndex,rightHandLimit:endIndex}];
                            sharedFolderId = "";
                        }
                        $.event.trigger("searchDriverFiles", reqParams);
                        $('.searchHolder i').removeClass('icon-search').addClass('icon-cross-thin-filled');
                    }else{
                        reqParams = [{ auth: driveService.getAuthData(), fileName: $('#BroadSearch').val(),leftHandLimit:startIndex,rightHandLimit:endIndex}];
                        $.event.trigger("searchDriverFiles", reqParams);
                        $('.searchHolder i').removeClass('icon-search').addClass('icon-cross-thin-filled');
                    }
                }else if ($this[0].value.length === 0) {
                    $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
                }
                else {
                    $('.searchHolder i').removeClass('icon-search').addClass('icon-cross-thin-filled');
                }
            }
        });

        $('body').on({
            keyup: function (e) {
                var searchBackValue = $(document).find("#BroadSearch").val();
                startIndex = 0;
                endIndex = 5;
                if(e.which==8 && searchBackValue !== "") {        
                    $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
                }
                
                if (searchBackValue == "" && (e.which == 13 || e.which==8)) {
                    
                    if (tabValue == "Recent") {
                        loadpagination = 1;
                        reqParams = { auth: driveService.getAuthData(), order: 'desc' };
                        reqParams.leftHandLimit=startIndex;
                        reqParams.rightHandLimit=endIndex;
                        $.event.trigger("recentDriverFiles", reqParams);
                    }
                    else if (tabValue == "Shared With Me") {
                        reqParams = { auth: driveService.getAuthData() };
                        reqParams.leftHandLimit=startIndex;
                        reqParams.rightHandLimit=endIndex;
                        $.event.trigger("sharedWithMeDriverFiles", reqParams);
                    }
                    else {
                        loadpagination = 1;                    
                        reqParams = { auth: driveService.getAuthData(), leftHandLimit: startIndex, rightHandLimit: endIndex };
                        $.event.trigger("getAllDriverFiles", reqParams);
                    }
                    $('.searchHolder i').removeClass('icon-cross-thin-filled').addClass('icon-search');
                }
    
            },
        }, '#BroadSearch');

      /*  $('body').on({
            click: function () {
                $("#fileInput").trigger("click");
            }
        }, '#fileButton');*/

        $('body').on({
            change: function (e) {
                file = e.target.files[0];
                var formData = new FormData();
                formData.append("auth", driveService.getAuthData());
                formData.append("filename", file.name)
                if (currentFolder != "") {
                    formData.append("folder", currentFolder);
                }
                formData.append("file", file);
                $(".uploadStatus").css("display", "block");
                $("#actionButton").css("bottom", "45px");
                var callback = driveService.uploadFile(formData);
                callback.then(function onLoad(data) {
                    if (data.hasOwnProperty('error')) {
                        var err = data.error.toLowerCase().indexOf("expired");
                        if (err > -1) {
                            $(".uploadStatus").css("display", "none");
                            $("#actionButton").css("bottom", "0px");

                            var reqParam = { 'auth': driveService.getAuthData() };
                            refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=drive", '_blank');
                        } else {
                            $(".uploadStatusMessage").css("display", "block");
                            $("#actionButton").css("bottom", "45px");
                            $(".uploadStatus").css("display", "none");
                            $("#actionButton").css("bottom", "0px");
                            $("#statusMessage").html("Something went wrong, please try again");
                            window.setTimeout(function () {
                                $(".uploadStatusMessage").css("display", "none");
                                $("#actionButton").css("bottom", "0px");
                            }, 3000);
                        }
                    }
                    else {
                        $(".uploadStatus").css("display", "none");
                        $("#actionButton").css("bottom", "0px");
                        $(".uploadStatusMessage").css("display", "block");
                        $("#statusMessage").html("Upload Complete");
                        $("#actionButton").css("bottom", "45px");
                        window.setTimeout(function () {
                            $(".uploadStatusMessage").css("display", "none");
                            $("#actionButton").css("bottom", "0px");
                            location.reload(true);
                        }, 3000);
                    }
                }, function onError() {
                    console.log("ERROR uploadFile!");
                });
            }
        }, '#fileInput');

        // $('body').on({
        //     click: function () {
        //         $("#folderInput").trigger("click");
        //     }
        // }, '#folderButton');
    });


    // Get all mail event listener
    $(document).on('getAllDriverFiles', function (event, param1) {
        $drive.empty();
        $.event.trigger({
            type: "showSpinningDiv"
        });
        driveService.getAllDriverFiles(param1).then(function onload(data) {
            $('.spinningGif').hide();
            if (data.hasOwnProperty('error')) {
                var err = data.error.toLowerCase().indexOf("expired");
                if (err > -1) {
                    var reqParam = { 'auth': driveService.getAuthData()};
                    refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=drive", '_blank');
                }    
            }
            // data.sort(function(x, y){
            //     return x.lastModified - y.lastModified;
            // })
            // data.data.listOfFiles.sort(function(a,b){
            //     // Turn your strings into dates, and then subtract them
            //     // to get a value that is either negative, positive, or zero.
            //     return new Date(b.lastModified) - new Date(a.lastModified);
            //   });
      
            for (var i in data.data.listOfFolder) {
                getHeaderView(data.data.listOfFolder[i],prevDate).appendTo($drive);
                prevDate = data.data.listOfFolder[i].lastModified;
            }
            listOfFiles = data.data.listOfFiles;
            for (var i in data.data.listOfFiles) {
                getHeaderView(data.data.listOfFiles[i],prevDate).appendTo($drive);
                prevDate = data.data.listOfFiles[i].lastModified;
            }
            if((data.data.listOfFiles == undefined && data.data.listOfFolder == undefined) || data.data.listOfFiles.length == 0 )  {
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

            if (data.data.listOfFiles.length != 0 && data.data.listOfFiles.length < 15) {
                $.event.trigger({
                    type: "showNoMoreDataDiv"
                });
            }
        });
    });

    // Get Shared mail event listener
    $(document).on('sharedWithMeDriverFiles', function (event, param1) {
        $drive.empty();
        $.event.trigger({
            type: "showSpinningDiv"
        });
        driveService.sharedWithMeDriverFiles(param1).then(function onload(data) {
            $('.spinningGif').hide();
            if (data.hasOwnProperty('error')) {
                var err = data.error.toLowerCase().indexOf("expired");
                if (err > -1) {
                    var reqParam = { 'auth': driveService.getAuthData()};
                    refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=drive", '_blank');
                }    
            }
            
             // var prevDate;
            for (var i in data.data.listOfFolder) {
                getHeaderView(data.data.listOfFolder[i],prevDate).appendTo($drive);
                prevDate = data.data.listOfFolder[i].timestamp;
            }
            listOfFiles =  data.data.listOfFiles ;
            for (var i in data.data.listOfFiles) {
                getHeaderView(data.data.listOfFiles[i],prevDate).appendTo($drive);
                prevDate = data.data.listOfFiles[i].timestamp;
            }
            if(data.data.listOfFiles == undefined || data.data.listOfFiles.length == 0 )  {
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

            if (data.data.listOfFiles.length != 0 && data.data.listOfFiles.length < 15) {
                $.event.trigger({
                    type: "showNoMoreDataDiv"
                });
            }
        });
    });

    // Get Recent mail event listener
    $(document).on('recentDriverFiles', function (event, param1) {
        $drive.empty();
        $.event.trigger({
            type: "showSpinningDiv"
        });
        driveService.recentDriverFiles(param1).then(function onload(data) {
            $('.spinningGif').hide();
            if (data.hasOwnProperty('error')) {
                var err = data.error.toLowerCase().indexOf("expired");
                if (err > -1) {
                    var reqParam = { 'auth': driveService.getAuthData()};
                    refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=drive", '_blank');
                }    
            }
            // var prevDate;
            listOfFiles = data.data.listOfFiles;
            for (var i in data.data.listOfFiles) {
                getHeaderView(data.data.listOfFiles[i],prevDate).appendTo($drive);
                prevDate = data.data.listOfFiles[i].timestamp;
            }

            if(data.data.listOfFiles == undefined || data.data.listOfFiles == 0 )  {
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

            if (data.data.listOfFiles != 0 && data.data.listOfFiles.length < 15) {
                $.event.trigger({
                    type: "showNoMoreDataDiv"
                });
            }
        });
    });

    // Delete mail event listener
    $(document).on('deleteDriverFiles', function (event, param1) {
        driveService.deleteDriverFiles(param1).then(function onload(data) {
            if (data.hasOwnProperty('error')) {
                var err = data.error.toLowerCase().indexOf("expired");
                if (err > -1) {
                    var reqParam = { 'auth': driveService.getAuthData()};
                    refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=drive", '_blank');
                }    
            }
            console.log(data);
        });
    });

    // search mail event listener
    $(document).on('searchDriverFiles', function (event, param1) {
        $drive.empty();
        $.event.trigger({
            type: "showSpinningDiv"
        });
        driveService.searchDriverFiles(param1).then(function onload(data) {
            if (data.hasOwnProperty('error')) {
                var err = data.error.toLowerCase().indexOf("expired");
                if (err > -1) {
                    var reqParam = { 'auth': driveService.getAuthData()};
                    refreshSession.getRefreshSession("https://orangehubservices.mpsvcs.com/hubservices/public/reloadSignUp.html?auth=" + reqParam.auth+"&type=drive", '_blank');
                }    
            } else {
            
                $('.spinningGif').hide();
                //var prevDate;
                if(data.data !== undefined) {
                    for (var i in data.data.listOfFiles) {
                        getHeaderView(data.data.listOfFiles[i],prevDate).appendTo($drive);
                        prevDate = data.data[i].timestamp;
                    }
                }

                if(data.data.listOfFiles == undefined ||  data.data.listOfFiles.length == 0 )  {
                    if(param1.leftHandLimit == 0) {
                        $('.loadingGif').hide();
                        $('.spinningGif').hide();
                        $('.noMoreData').hide();
                        $('.noData').hide();
                        $('#searchNull').show()
                    } else {
                        $.event.trigger({
                            type: "showNoMoreDataDiv"
                        });
                    }   
                } 

                if (data.data.listOfFiles.length != 0 && data.data.listOfFiles.length < 15) {
                    $.event.trigger({
                        type: "showNoMoreDataDiv"
                    });
                }
            }
            
        });
    });

    // Download File
    $(document).on('downloadAttachment', function (event, param1) {
        driveService.downloadAttachment(param1).then(function onload(data) {
            
            console.log(data);
        });
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

    $(document).on('showNoMoreDataDiv', function (event) {
        $('.loadingGif').hide();
        $('.spinningGif').hide();
        $('.noMoreData').show();
        $('.noData').hide()
        $('#searchNull').hide()
    });
    $(document).on('showSpinningDiv', function (event) {
        $('.loadingGif').hide();
        $('.noMoreData').hide();
        $('.spinningGif').show();
        $('.noData').hide();
        $('#searchNull').hide();
    });
    //Get spinner loader
    $(document).on('showBarLoaderDiv', function (event) {
        $('.spinningGif').hide();
        $('.noMoreData').hide();
        $('.loadingGif').show();
        $('.noData').hide();
        $('#searchNull').hide()
    });
    $(document).on('hideAllLoader', function (event) {
        $('.loadingGif').hide();
        $('.spinningGif').hide();
        $('.noMoreData').hide();
        $('.noData').hide()
        $('#searchNull').hide()
    });

    var sticky = navbar.offsetTop;
    $(window).on('scroll', function (event) {
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
                type: "getDrivePaginationLoader"
            });
        }
    });

    $(document).on('getDrivePaginationLoader', function (event) {
        $.event.trigger({
            type: "showBarLoaderDiv"
        });
        startIndex = endIndex;
        endIndex += 5;
        searchValue = $(document).find("#BroadSearch").val();
    switch(subTabSelected){
        case "My Files" : serviceToCall = driveService.getAllDriverFiles({auth: driveService.getAuthData(), leftHandLimit: startIndex, rightHandLimit: endIndex});break;
        case "Recent" : serviceToCall = driveService.recentDriverFiles({auth: driveService.getAuthData(), order: 'desc', leftHandLimit: startIndex, rightHandLimit: endIndex});break;
        }
        if(searchValue != ""){
            serviceToCall = driveService.searchDriverFiles({auth: driveService.getAuthData(), fileName: searchValue, leftHandLimit: startIndex, rightHandLimit: endIndex});
        }
        serviceToCall.then(function onload(data) {

            // data.data.listOfFiles.sort(function(a,b){
            //     // Turn your strings into dates, and then subtract them
            //     // to get a value that is either negative, positive, or zero.
            //     return new Date(b.lastModified) - new Date(a.lastModified);
            //   });
              
            if (data.data && Array.isArray(data.data.listOfFiles)) {
                $.event.trigger({
                    type: "hideAllLoader"
                });
                // var prevDate;
                for (var i in data.data.listOfFolder) {
                    getHeaderView(data.data.listOfFolder[i],prevDate).appendTo($drive);
                    prevDate = data.data.listOfFolder[i].lastModified;
                }
                for (var i in data.data.listOfFiles) {
                    getHeaderView(data.data.listOfFiles[i],prevDate).appendTo($drive);
                    prevDate = data.data.listOfFiles[i].lastModified;
                }
            } else {
                $.event.trigger({
                    type: "showNoMoreDataDiv"
                });
                loadpagination = 0;
                event.preventDefault();
            }
        });
        reqParams.leftHandLimit = startIndex;
        reqParams.rightHandLimit = endIndex;
    });

})();