package MyPackage;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.sql.*;

/**
 * Author: Matthew Lingenfelter
 * -- Purpose: Gets all the year's data from the Oracle Database and returns it to the user's browser to be displayed.
 */
@WebServlet(name="ShowYear")
public class ShowYear extends HttpServlet {
    
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
        String city = request.getParameter("city");
        String year = request.getParameter("year");
        String sRows = request.getParameter("rows");
        String cName = request.getParameter("cookieName");
        String tableName = "mling459"+cName;
        int choice = Integer.parseInt(request.getParameter("choice"));
        Statement stmnt = null;
        int rows;

        // Sets the number of rows equal to -1 if there is no data in the Oracle Database for the cities
        // Otherwise sets rows equal to the number of rows of data
        if(sRows.equals("") || sRows == null) { rows = -1; }
        else { rows = Integer.parseInt(sRows); }

        // Checks if all the information from the form is a valid option
        if(city.equals("-1") || (city == null) || ((choice != 0) && (choice != 1))) {
            writer.println("<p>Please make sure you filled out the form correctly.</p>");
            writer.println("<p>City: "+city+"</p>");
            writer.println("<p>Year: "+year+"</p>");
            writer.println("<p>Choice: "+choice+"</p>");
            return;
        }

        // Gets the data for the selected year
        String query = "SELECT sMonth, cOnTime, cCancelled FROM \""+tableName+"\"";
        String[][] yData = null;

        // Gets the year data from the Oracle Database
        try {
            Connection con = new OracleConnection().connect();
            stmnt = con.createStatement();

            yData = new OracleConnection().retrieveYear(stmnt, query, rows);
            stmnt.close();
        } catch(Exception e) { }

        // Check if data was actually gotten
        if(yData == null) {
            writer.println("<p>Failed to get data for "+year+"</p>");
            return;
        }

        // Sort the data
        float[][] sortedIdx = QuickSort.SortData(yData, rows, choice); // probably wont work as of now======================

        // Gets the average cost for a ticket departing from a given city
        String oracleCityName = "";
        if(city.contains(" ")) {
            String[] tempCity = city.split(" ");
            if(tempCity[0].contains("'")) {
                tempCity[0] = tempCity[0].replace("'", "");
            }
            oracleCityName = tempCity[0];
        } else if(city.contains("'")) {
            oracleCityName = city.replace("'", "");
        }
        float cost = getCost(oracleCityName);

        // All the data formatted in a readable manner
        writer.println("<p>Leaving from <b>"+oracleCityName+"</b> will cost approximatly <b>$"+cost+"</b>.</p>");
        switch(choice) {
            case 1: // Cancelled
                writer.print("<p>The month that is most likey to have your flight get cancelled is <b>");
                writer.print(yData[(int)sortedIdx[0][0]][0]+" "+yData[(int)sortedIdx[0][0]][1]+"</b>.</p>");
                writer.print("<p>There is a <b>"+yData[(int)sortedIdx[0][0]][2]+" chance of getting cancelled</b> and a ");
                writer.print("<b>"+yData[(int)sortedIdx[0][0]][3]+" chance of leaving on time</b>.</p>");
                break;
            case 0: // OnTime
            default:
                writer.print("<p>The month that is most likey to have your flight leave on time is <b>");
                writer.print(yData[(int)sortedIdx[0][0]][0]+" "+yData[(int)sortedIdx[0][0]][1]+"</b>.</p>");
                writer.print("<p>There is a <b>"+yData[(int)sortedIdx[0][0]][2]+"% chance to leave on time</b> and a ");
                writer.print("<b>"+yData[(int)sortedIdx[0][0]][3]+"% chance of getting cancelled</b>.</p>");
                break;
        }
        writer.println("<p>Below are the other months of <b>"+year+"</b>, ranked from best to worst.</p>");

        //print table of days
        writer.println("<table>");
        writer.println("<tr>");
        writer.println("<td>Month</td>");
        writer.println("<td>Chance of leaving on time</td>");
        writer.println("<td>Chance of the flight being cancelled</td>");
        writer.println("</tr>");

        // Loops to add the remaining days to the table
        for(int i = 1; i < rows; i++) {
            print(writer, yData, (int)sortedIdx[i][0]);
        }

        writer.println("</table>");
        writer.println("<p>Data provided by the Bureau of Transportation Statistics</p>");//possibly add link
        writer.println("</body>");
        writer.println("</html>");
        return;
    }


    /**
     * This function gets the average fare for a plane ticket leaving from the city the user selected.
     * @param city A string indicating which city the user selected.
     * @return The average cost for this city.
     */
    private static float getCost(String city) {
        String query = "SELECT aFare FROM \"mling459Cities\" WHERE cNameFirst='"+city+"' ";
        Statement stmnt;
        ResultSet rs = null;
        float cost = -1;
        try {
            Connection con = new OracleConnection().connect();
            stmnt = con.createStatement();
            rs = stmnt.executeQuery(query);
            rs.next();
            cost = Float.valueOf(rs.getString("aFare"));
            stmnt.close();
        } catch(Exception e) { }

        return cost;
    }


    /**
     * This function adds all the data to a table that will be displayed to the user.
     * @param writer The PrintWriter variable that is used to return the data to the user.
     * @param data A 2D string array containing all the data.
     * @param r An integer indicating which row of data to add to the table.
     */ 
    private static void print(PrintWriter writer, String[][] data, int r) {
        writer.println("<tr>");
        writer.println("<td>"+data[r][0]+"</td>"); // Month
        writer.println("<td style=\"text-align:right;\">"+data[r][2]+"</td>"); // % OnTime
        writer.println("<td style=\"text-align:right;\">"+data[r][3]+"</td>"); // % Cancelled
        writer.println("</tr>");
    }
}
