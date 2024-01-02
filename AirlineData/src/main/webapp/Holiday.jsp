<% response.setHeader("Access-Control-Allow-Origin", "https://acad.kutztown.edu"); 
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
    response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Expose-Headers", "Authorization");
    response.addHeader("Access-Control-Expose-Headers", "responseType");
    response.addHeader("Access-Control-Expose-Headers", "observe");
%>

<%@page contentType="text/html; charset=UTF-8" %>

<html>
   <body style="font-family:verdana;">
    <section>
        <nav>
            <form id="citiesForm" name="citiesForm" method="post">
                <p>What state is your origin city in? (Enter just the state's initials)</p>
                <input type="text" id="state" name="state" value="PA">
                <input type="button" value="Update Cities" id="UpdateCities" onclick="return updateCities();"> <br> <br>
            </form>

            <p id="loading"></p>

            <form id="holidayForm" name="holidayForm" method="post">
                <label for="city">Select a city: </labe>
                <select id="city" name="city">
                    <option value="-1"> </option>
                </select> <br> <br>

                <label for="holiday">Select a Holiday: </label>
                <select id="holiday" name="holiday">
                    <option value="Presidents' Day">Presidents' Day</option>
                    <option value="Easter">Easter</option>
                    <option value="Memorial Day">Memorial Day</option>
                    <option value="Independence Day">Independence Day</option>
                    <option value="Labor Day">Labor Day</option>
                    <option value="Thanksgiving">Thanksgiving</option>
                    <option value="Winter Break">Winter Break</option>
                </select> <br>

                <p> How would you like the days sorted? </p>
                <input type="radio" id="OnTime" name="choice" value="0">
                <label for="OnTime">Chance of the flight leaving on time</label> <br>
                <input type="radio" id="Cancelled" name="choice" value="1">
                <label for="Cancelled">Chance of the flight getting cancelled</label> <br> <br>

                <br> <input type="button" value="Get Data" id="HolidayButton" onclick="return getHolidayData();">
            </form>
        </nav>
        <article id="responseArea">
        </article>
    </section>
   </body>
</html>