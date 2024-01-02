package MyPackage;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Author: Matthew Lingenfelter, Adapted from OracleConnection.java by Dr. Spiegel.
 * -- Purpose: Provides various functions relating to using the Oracle Database, which gets implemented across 
 * multiple files.
 */
public class OracleConnection extends BigAbstract {
   
    /** A global Connection variable which is used to esstablish a connection to the Oracle Database. */
    private Connection conn;

    /** String containing the Username for the Oracle database. */
    private static final String UserName = "mling459";

    /** String containing the Password for the Oracle database. */
    private static final String Password = "ItM8DeEl";

    /**
     * An empty contructor for the OracleConnection class.
     */
    public OracleConnection() { }

    
    /**
     * This function creates a connection to the Oracle Database using my username and password.
     * @throws SQLException Throws an SQLException if an error occurs connecting to the Database.
     * @return conn The Connection variable used to access the Oracle DataBase.
     */
    public Connection connect() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch(Exception e) {
            System.out.println("mling ERROR in OracleConnection.java: Could not load the driver");
        }

        String oracleUrl = "jdbc:oracle:thin:@csdb.kutztown.edu:1521:orcl";
        conn = DriverManager.getConnection(oracleUrl, UserName, Password);

        return conn;
    }


    /**
     * This function attempts to drop a given table from the Oracle Database, returning true if the table was dropped, and 
     * false if it wasn't.
     * @param stmnt The Statement variable which is created from the Oracle Connection.
     * @param tableName A string containing the name of the table that is to be dropped.
     * @return True if the table was dropped. False if the table was not dropped.
     */
    public boolean removeTable(Statement stmnt, String tableName) {
        try {
            stmnt.executeUpdate("DROP TABLE \""+tableName+"\"");
            return true;
        } catch(Exception e) {
            return false;
        }
    }


    /**
     * This function attempts to add a table to the Oracle Database, returning true if the table was added, and false if the
     *  table could not get added to the Database.
     * @param stmnt The Statement variable which is create from the Oracle Connection.
     * @param createString A string containing the information on how the create/add the table to the Database.
     * @return True if the table was added. False if the table was not added.
     */
    public boolean addTable(Statement stmnt, String createString) {
        try {
            stmnt.executeUpdate(createString);
            return true;
        } catch(Exception e) {
            System.out.println("mling DEBUG: "+e);
            return false;
        }
    }


    /**
     * This function gets all of the cities and their data for a given state from the Oracle Database, return the data in 
     * a 2D array.
     * @param stmnt The Statement variable which is created from the Oracle Connection.
     * @param query A string containing the specific query that will be used to retrieve the desired data.
     * @return cCitites A 2D string array, containing all the cities and their data for the give state.
     */
    public String[][] retrieveCitites(Statement stmnt, String query) {
        ResultSet rs = null;
        int numCities = 0;
        String[][] cCities = null;
        try {
            rs = stmnt.executeQuery(query);
            cCities = new String[rs.getFetchSize()][3];
            while(rs.next()) {
                cCities[numCities][0] = (rs.getString("cNameFirst")+" "+rs.getString("cNameLast"));
                cCities[numCities][1] = rs.getString("sName");
                cCities[numCities][2] = rs.getString("aFare");
                numCities = numCities + 1;
            }
        } catch(Exception e) {}
        return cCities;
    }


    /**
     * This function gets all of the given holiday's data from the Oracle Database, and returns it as a 2D array.
     * @param stmnt The Statement variable which is created from the Oracle Connection.
     * @param query A string containing the specific query that will be used to retrieve the desirec data.
     * @param rows An integer representing the number of rows of data their is for this holiday.
     * @return data A 2D string array, containing all of this holiday's data.
     */
    public String[][] retrieveHoliday(Statement stmnt, String query, int rows) {
        ResultSet rs = null;
        int counter = 0;
        String[][] data = null;
        try {
            rs = stmnt.executeQuery(query);
            data = new String[rows][4];
            while(rs.next()) {
                data[counter][0] = rs.getString("sDay");
                data[counter][1] = rs.getString("sDate");
                data[counter][2] = rs.getString("cOnTime");
                data[counter][3] = rs.getString("cCancelled");
                counter = counter + 1;
            }
        } catch(Exception e) { }
        return data;
    }


    /**
     * This function gets all of the given year's data from the Oracle Database, and returns it as a 2D array.
     * @param stmnt The Statement variable which is created from the Oracle Connection.
     * @param query A string containing the specific query that will be used to retrieve the desirec data.
     * @param rows An integer representing the number of rows of data their is for this holiday.
     * @return data A 2D string array, containing all of this holiday's data.
     */
    public String[][] retrieveYear(Statement stmnt, String query, int rows) {
        ResultSet rs = null;
        int counter = 0;
        String[][] data = null;
        try{
            rs = stmnt.executeQuery(query);
            data = new String[rows][4];
            while(rs.next()) {
                data[counter][0] = rs.getString("sMonth");
                data[counter][1] = "";
                data[counter][2] = rs.getString("cOnTime");
                data[counter][3] = rs.getString("cCancelled");
                counter = counter + 1;
            }
        } catch(Exception e) {
            System.out.println("mling DEBUG: "+e);
        }
        return data;
    }
}
