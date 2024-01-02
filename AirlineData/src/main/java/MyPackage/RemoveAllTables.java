package MyPackage;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

/**
 * Author: Matthew Lingenfelter
 * -- Purpose: Removes all the tables from the Oracle Database.
 */
@WebServlet(name="RemoveAllTables")
public class RemoveAllTables extends HttpServlet {
    
    /** Array of all options for the user to pick from. */
    private static String[] Holidays = {"PresidentsDay", "Easter", "MemorialDay", "IndependenceDay", "LaborDay", "Thanksgiving", "WinterBreak"};
    
    
    /** Array of all the http codes for each option. */
    private static String[] Codes = {"c4r5vqr065", "Rn56r4", "Zrz14vny", "V0qr2r0qr0pr", "Yno14", "gun0x5tv8v0t", "jv06r4", "FDFG", "FDFF", "FDFE", "FDFD", "FDEM", "FDEL"};

    /**
     * Accepts and handles POST requests from the HTML form.
     * @param request The Http Servlet Request information that is sent from the HTML form.
     * @param response The Http Servlet Response variable that is used to return the datatable to the HTML page.
     * @throws ServletException Throws ServletException if any errors occurs regarding the servlet handling.
     * @throws IOException Throws IOException if any errors occurs retrieving the data from the Transtats website.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");

        PrintWriter writer = response.getWriter();
        int all = Integer.parseInt(request.getParameter("all"));
        String tableName = request.getParameter("tableName");
        Statement stmnt = null;
        String returnString = "";

        if(all==0) { // Remove only one table
            try {
                Connection con = new OracleConnection().connect();
                stmnt = con.createStatement();

                boolean removed = new OracleConnection().removeTable(stmnt, tableName);
                if(removed) {
                    returnString += "<p>Removed: "+tableName+"</p>";
                }
            } catch(Exception e) { }
        } else { // Remove all tables
            returnString += removeAllTables(returnString);
        }

        writer.println(returnString);
    }


    /**
     * This function removes all of the tables in the Oracle Database.
     * @param returnString A string that allows this function to tell the user which table was dropped.
     * @return The returnString, used to relate information back to the user.
     */
    public static String removeAllTables(String returnString) {
        Statement stmnt = null;
        String tableName = "";
        // Connects to the Oracle Database
        try {
            Connection con = new OracleConnection().connect();
            stmnt = con.createStatement();

            // Loops through all of the "old" Oracle table names and attempts to remove them.
            // These were the original names of the tables during Phases 1 and 2.
            for(int i = 0; i < Codes.length; i++) {
                tableName = "mling459"+Codes[i];
                boolean removed = new OracleConnection().removeTable(stmnt, tableName);
                if(removed) {
                    returnString += "<p>Removed "+tableName+"</p>"; }
            }

            // Loops through all of the Oracle table names and attemps to remove them.
            for(int i = 0; i < Holidays.length; i++) {
                tableName = "mling459"+Holidays[i]+"Meta";
                boolean removed = new OracleConnection().removeTable(stmnt, tableName);
                if(removed) {
                    returnString += "<p>Removed "+tableName+"</p>"; }
            }

            // Attempts to remove the cities table.
            tableName = "mling459Cities";
            boolean removed = new OracleConnection().removeTable(stmnt, tableName);
            if(removed) {
                returnString += "<p>Removed "+tableName+"</p>"; }
        } catch(Exception e) { }
        return returnString;
    }
}