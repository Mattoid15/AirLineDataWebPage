<% response.setHeader("Access-Control-Allow-Origin", "https://acad.kutztown.edu"); 
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
    response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Expose-Headers", "Authorization");
    response.addHeader("Access-Control-Expose-Headers", "responseType");
    response.addHeader("Access-Control-Expose-Headers", "observe");
%>

<html>
    <body>
        <section>
            <nav>
                <form id="citiesForm" name="citiesForm" method="post">
                    <p>What state is your origin city in? (Enter just the state's initials)</p>
                    <input type="text" id="state" name="state" value="PA">
                    <input type="button" value="Update Cities" id="UpdateCities" onclick="return updateCities();"> <br> <br>
                </form>

                <p id="loading"></p>

                <form id="yearForm" name="yearForm" method="post">
                    <label for="city">Select a city: </labe>
                    <select id="city" name="city">
                        <option value="-1"> </option>
                    </select> <br> <br>

                    <label for="year">Select a Year: </label>
                    <select id="year" name="year">
                        <option value="2023">2023</option>
                        <option value="2022">2022</option>
                        <option value="2021">2021</option>
                        <option value="2020">2020</option>
                        <option value="2019">2019</option>
                        <option value="2018">2018</option>
                    </select> <br>

                    <p> How would you like the days sorted? </p>
                    <input type="radio" id="OnTime" name="choice" value="0">
                    <label for="OnTime">Chance of the flight leaving on time</label> <br>
                    <input type="radio" id="Cancelled" name="choice" value="1">
                    <label for="Cancelled">Chance of the flight getting cancelled</label> <br> <br>

                    <br> <input type="button" value=" Get Data" id="YeayButton" onclick="return getYearData();">
                </form>
            </nav>
            <article id="responseArea">
            </article>
        </section>
    </body>
</html>