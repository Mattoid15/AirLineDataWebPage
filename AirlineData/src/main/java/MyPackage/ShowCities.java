package MyPackage;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.net.ssl.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

/**
 * Author: Matthew Lingenfelter
 * -- Purpose: Gets all the cities' data from the Oracle Database and returns it to the user's browser to be displayed.
 */
@WebServlet(name="ShowCitites")
public class ShowCities extends HttpServlet {

    /**A global string that contains the tableName for the Cityies' data. */
    private static final String tableName = "mling459Cities";

    /**
     * Accepts and handls POST requests from the HTML form.
     * @param request The Http Servlet Request information that is sent from the HTML form.
     * @param response The Http Servlet Response variable that is used to return the requested data to the user's browser.
     * @throws ServletException Throws a ServletException if any errors occur regarding the servlet handling.
     * @throws IOException Throws an IOException if any errors occur retrieving the data from the Transtats website.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");

        // Initializes local variables
        PrintWriter writer = response.getWriter();
        String state = request.getParameter("state");
        Statement stmnt = null;
        String[][] cCities = null;
        int totalTables = 0;
        int rows = -1;

        // Checks if the input string for the state is 2 characters, all states' intitials are 2 letters
        // If the user did not input a correct amount of characters, prompt the user to try again.
        if(state.length() != 2) {
            writer.println("<option value=\"-1\">Please enter just the state's initial.</option>");
            return;
        }

        // Creates the query string used to get all the cities from the state the user selected.
        String queryString = "SELECT cNameFirst, cNameLast, sName, aFare FROM \""+tableName+
            "\" WHERE sName='"+state.toUpperCase()+"' ";
        
        // Gets all the cities for a given state
        try {
            Connection con = new OracleConnection().connect();
            stmnt = con.createStatement();

            cCities = new OracleConnection().retrieveCitites(stmnt, queryString);
            stmnt.close();
        } catch(Exception e) { }

        // Check if there is data for the city, if not return error response
        if(cCities[0][0] == null) {
            writer.println("<option value=\"-1\">Could not get the cities for \""+state+"\"</option>");
            return;
        }

        // Gets the number of cities for this state
        int numCities = cCities.length;

        // Loops to populate the dropdown of cities that the user can now select from
        for(int r = 0; r < numCities; r++) {
            writer.println("<option value=\""+cCities[r][0]+"\">"+cCities[r][0]+"</option>");
        }
    }
}