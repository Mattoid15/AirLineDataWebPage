/**
 * Author: Matthew Lingenfelter
 * -- Purpose: Provides asyncronous posts to get and display data relating to different years.
 */

/**
 * This function displays the Year Form to the user without refreshing the page.
 */
function getYForm() {
    $(document).ready(function() {
        $.ajax( {
            url: "https://mling459.kutztown.edu:8443/AirlineData-1/Year.jsp",
            type: "post",
            success: function(msg) {
                $('#FormArea').html(msg);
            }
        });
    });
}


/**
 * This function calls the servlet to get all the data for a given year. If the year's data is not in the
 * Oracle Database, it calls checkYearMeta() to add the data. Then displays the resulting data to the user
 * without refreshing the page.
 */
function getYearData() {
    let cname = $('#year').val() + "Meta";
    let cvalue = getCookieValue(cname);

    // Check cookie value
    if(cvalue == "" || cvalue == "-1") {
        checkYearMeta(cname, cvalue);
        cvalue = getCookieValue(cname);
    }

    $('#responseArea').text("Getting Data...");
    $('#ClearArea').text("");
    $('#CookieArea').text("");
    $('#SessionArea').text("");
    $(document).ready(function() {
        $.ajax( {
            url: "https://mling459.kutztown.edu:8443/AirlineData-1/MyPackage.ShowYear",
            type: "post",
            data: $('#yearForm').serialize()+"&rows="+cvalue+"&cookieName="+cname,
            success: function(msg) {
                $('#responseArea').text("");
                $('#responseArea').html(msg);
            }
        });
    });
}


/**
 * This function checks if there are already 5 tables in the Database. If there is, it gets the first table added from
 * the order stored in the session, and removes that table. Then it adds this year's data to the Datbase, updating
 * any cookies as necessary.
 * @param {*} cname The name of the year's cookie.
 * @param {*} cvalue The value of the year's cookie.
 */
function checkYearMeta(cname, cvalue) {
    let tname = "TotalTables";
    let tvalue = getCookieValue(tname);
    cname = $('#year').val() + "Meta";
    cvalue = getCookieValue(cname);
    // Check table amount, change order as needed
    if(tvalue >= 5) {
        removeFirst();
    }

    $('#responseArea').text("Getting Data...");
    $('#ClearArea').text("");
    $('#CookieArea').text("");
    $('#SessionArea').text("");
    $(document).ready(function() {
        $.ajax( {
            async: false,
            url: "https://mling459.kutztown.edu:8443/AirlineData-1/MyPackage.GetYear",
            type: "post",
            data: $('#yearForm').serialize()+"&rows="+cvalue+"&totalTables="+tvalue+"&cookieName="+cname,//+"&year="+$('#year').val(),
            success: function(msg) {
                $('#responseAres').text("");
                let ca = msg.split(';');
                for(let i = 0; i < ca.length; i++) {
                    document.cookie = ca[i];
                }
                // Update session order
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