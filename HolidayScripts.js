/**
 * Author: Matthew Lingenfelter
 * -- Purpose: Provides asyncronous posts to get and display data relating to different holidays.
 */

/**
 * This function displays the Holiday Form to the user without refreshing the page.
 */
function getHForm() {
    $(document).ready(function() {
        $.ajax( {
            url: "https://mling459.kutztown.edu:8443/AirlineData-1/Holiday.jsp",
            type: "post",
            success: function(msg) {
                $('#FormArea').html(msg);
            }
        });
    });
}


/**
 * This function calls the servlet to get all of the data for a given holiday. If the holiday's data is not in the
 * Oracle Database, it calls checkHolidayMeta() to add the data. Then displays the resulting data to the user
 * without refreshing the page.
 */
function getHolidayData() {
    let cname = $('#holiday').val();
    cname = cname.replace(" ", "");
    cname = cname.replace("'", "");
    cname = cname + "Meta";
    let cvalue = getCookieValue(cname);

    // check cookie value
    if(cvalue == "" || cvalue == "-1") {
        checkHolidayMeta(cname, cvalue);
        cvalue = getCookieValue(cname);
    }
    
    $('#responseArea').text("Getting Data...");
    $('#ClearArea').text("");
    $('#CookieArea').text("");
    $('#SessionArea').text("");
    $(document).ready(function() {
        $.ajax( {
            url: "https://mling459.kutztown.edu:8443/AirlineData-1/MyPackage.ShowHoliday",
            type: "post",
            data: $('#holidayForm').serialize()+"&rows="+cvalue,
            success: function(msg) {
                $('#responseArea').text("");
                $('#responseArea').html(msg);
            }
        });
    });
}


/**
 * This function checks if there are already 5 tables in the Database. If there is, it gets the first table added from 
 * the order stored in the session, and removes that table. Then it adds this holiday's data to the Database, updating
 * any cookies as necessary.
 * @param {*} cname The name of the holiday's cookie.
 * @param {*} cvalue The value of the holiday's cookie.
 */
function checkHolidayMeta(cname, cvalue) {
    let tname = "TotalTables";
    let tvalue = getCookieValue(tname);

    // check table amount, change order as needed
    if(tvalue >= 5) {
        removeFirst();
    }
    
    $('#responseArea').text("Getting Data...");
    $(document).ready(function() {
        $.ajax( {
            async: false,
            url: "https://mling459.kutztown.edu:8443/AirlineData-1/MyPackage.GetHoliday",
            type: "post",
            data: "rows="+cvalue+"&totalTables="+tvalue+"&cookieName="+cname+"&holiday="+$('#holiday').val(),
            success: function(msg) {
                $('#responseArea').text("");
                let ca = msg.split(';');
                for(let i = 0; i < ca.length; i++) {
                    document.cookie = ca[i];
                }
                // update session order
                let order = sessionStorage.getItem("order");
                if(order != "") {
                    order = order+","+cname;
                    sessionStorage.setItem("order", order);
                } else {
                    sessionStorage.setItem("order", cname);
                }
            }
        });
    });
}