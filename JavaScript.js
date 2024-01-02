/**
 * Author: Matthew Lingenfelter
 *  -- Purpose: Provides genaric functions that get implemented in other javascript files
 * and used to update session information.
 */

/**
 * Calls the servlet to get all of the cities for the state given by the user.
 * If the cities' data is not in the Oracle Database, it calls checkCititesMeta() to add the data to the Database.
 * This updates the form to select a city without refreshing the page.
 */
function updateCities() {
    let cname = "CitiesMeta";
    let cvalue = getCookieValue(cname);
    if(cvalue == "" || cvalue == "-1") {
        checkCitiesMeta(cvalue);
    }
    $('#loading').text("Showing Cities...");
    $(document).ready(function() {
        $.ajax( {
            url: "https://mling459.kutztown.edu:8443/AirlineData-1/MyPackage.ShowCities",
            type: "post",
            data: $('#citiesForm').serialize(),
            success: function(data) {
                $('#loading').text("");
                $('#city').html(data);
            }
        });
    });
}


/**
 * This function checks the metadata for the cities and calls the servlet to get all the data added to the Oracle Database.
 * @param {*} cvalue The value of the CitiesMeta cookie.
 */
function checkCitiesMeta(cvalue) {
    let tname = "TotalTables";
    let tvalue = getCookieValue(tname);
    $('#loading').text("Getting Citites...");
    $('#ClearArea').text("");
    $('#CookieArea').text("");
    $('#SessionArea').text("");
    $(document).ready(function () {
        $.ajax( {
            async: false,
            url: "https://mling459.kutztown.edu:8443/AirlineData-1/MyPackage.GetCities",
            type: "post",
            data: "rows="+cvalue+"&totalTables="+tvalue,
            success: function(data) {
                let ca = data.split(';');
                for(let i = 0; i < ca.length; i++) {
                    document.cookie = ca[i];
                }
            }
        });
    });
}


/**
 * This function gets the value of a given cookie. If the cookie does not exist, it returns -1.
 * @param {*} cname The name of the cookie to return the value of.
 * @returns Either the desired cookie's value or -1 if the cookie is not there.
 */
function getCookieValue(cname) {
    let name = cname + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for(let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while(c.charAt(0) == ' ') {
            c = c.substring(1); }
        if(c.indexOf(name) == 0) {
            return c.substring(name.length, c.length); }
    }
    return "-1";
}


/**
 * This function gets the order that the tables were added to the Oracle Database, and gets the first one on the list.
 * Then, it sets the cookie's value of the first item to -1, and dropps that table from the DataBase. Finally, it updates the total
 * number of tables to account for this dropped table.
 */
function removeFirst() {
    $(document).ready(function() {
        let orderString = sessionStorage.getItem("order");
        let order = orderString.split(',');
        let removeThis = order[0];
        document.cookie = removeThis+"=-1";
        //drop table from Oracle
        $(document).ready(function() {
            $.ajax( {
                url: "https://mling459.kutztown.edu:8443/AirlineData-1/MyPackage.RemoveAllTables",
                type: "post",
                data: "all=0&tableName=mling459"+removeThis,
                success: function(msg) {
                    $('ClearArea').html(msg);
                }
            });
        });
        // update total tables
        let totalTables = getCookieValue("TotalTables") - 1;
        document.cookie = "TotalTables="+totalTables;

        let newString = order[1];
        for(let i = 2; i < order.length; i++) {
            newString = newString+","+order[i];
        }
        sessionStorage.setItem("order", newString);
    });
}


/**
 * This function removes all the cookies relating to this project, drops any tabels that are in the Oracle Database, and
 * clears the order of tables access that is stored in the session.
 */
function clearData() {
    $(document).ready(function() {
        $('#ClearArea').text("");
        $('#CookieArea').text("");
        $('#SessionArea').text("");

        // Removes all Cookies
        let decodedCookie = decodeURIComponent(document.cookie);
        let ca = decodedCookie.split(';');
        for(let i = 0; i < ca.length; i++) {
            document.cookie = ca[i] + ";expires=Thu, 01 Jan 1970 00:00:00 UTC;";
        }

        // Removes all tables in the Oracle Database
        $.ajax( {
            async: false,
            url: "https://mling459.kutztown.edu:8443/AirlineData-1/MyPackage.RemoveAllTables",
            type: "post",
            data: "all=1&tableName=all",
            success: function(msg) {
                $('#ClearArea').html(msg);
                $('#ClearArea').append("All Data Removed");
            }
        });

        // Clears session order
        sessionStorage.setItem("order", "");
    });
}

function showCookies() {
    $('#ClearArea').text("");
    $('#CookieArea').text("");
    $('#SessionArea').text("");

    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    let elm = document.getElementById("CookieArea");
    let res = "";
    
    res += "<table><tr><td>Cookie Name</td><td>Cookie Value</td></tr>";
    for(let i = 0; i < ca.length; i++) {
        let stuff = ca[i].split('=');
        res += "<tr><td>"+stuff[0]+"</td><td>"+stuff[1]+"</td><tr>"
    }
    elm.innerHTML = res;
}

function showSession() {
    $('#ClearArea').text("");
    $('#CookieArea').text("");
    $('#SessionArea').text("");

    let orderString = sessionStorage.getItem("order");
    let order = orderString.split(',');
    let elm = document.getElementById("SessionArea");
    let res = "Order the tables were added to Oracle: ";

    res += order[0];
    for(let i = 1; i < order.length; i++) {
        res += ", "+order[i];
    }
    elm.innerHTML = res;
}