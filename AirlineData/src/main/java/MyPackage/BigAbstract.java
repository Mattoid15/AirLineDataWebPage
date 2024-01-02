package MyPackage;

import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Author: Matthew Lingenfelter, Adapted from DBConnection.java by Joe Schich, and Dr. Spiegel.
 * -- Purpose: Acts as an abstract class which allows the use of my OracleConnection.java file, that allows for the creation
 *  of database connections.
 */
public abstract class BigAbstract {

    /**
     * An empty constructor for the BigAbstract class.
     */
    public BigAbstract() { }


    /**
     * An abstract function allowing other files to use the connect() function in OracleConnection.java.
     * @throws SQLException Throws an SQLException if an error occurs connecting to the Database.
     * @return conn The Connection variable used to access the Oracle DataBase.
     */
    public abstract Connection connect() throws SQLException;

    /**
     * An abstract function allowing other files to use the removeTable() function in OracleConnection.java.
     * @param stmnt The Statement variable which is created from the Oracle Connection.
     * @param tableName A string containing the name of the table that is to be dropped.
     * @return True if the table was dropped. False if the table was not dropped.
     */
    public abstract boolean removeTable(Statement stmnt, String tableName);

    /**
     * An abstract function allowing other files to use the addTable() function in OracleConnection.java.
     * @param stmnt The Statement variable which is create from the Oracle Connection.
     * @param createString A string containing the information on how the create/add the table to the Database.
     * @return True if the table was added. False if the table was not added.
     */
    public abstract boolean addTable(Statement stmnt, String createString);

    /**
     * An abstract function allowing other files to use the retrieveCitites() function in OracleConnection.java.
     * @param stmnt The Statement variable which is created from the Oracle Connection.
     * @param query A string containing the specific query that will be used to retrieve the desired data.
     * @return cCitites A 2D string array, containing all the cities and their data for the give state.
     */
    public abstract String[][] retrieveCitites(Statement stmnt, String query);

    /**
     * An abstract function allowing other files to use the retrieveHoliday() function in OracleConnection.java.
     * @param stmnt The Statement variable which is created from the Oracle Connection.
     * @param query A string containing the specific query that will be used to retrieve the desirec data.
     * @param rows An integer representing the number of rows of data their is for this holiday.
     * @return data A 2D string array, containing all of this holiday's data.
     */
    public abstract String[][] retrieveHoliday(Statement stmnt, String query, int rows);

    /**
     * This function gets all of the given year's data from the Oracle Database, and returns it as a 2D array.
     * @param stmnt The Statement variable which is created from the Oracle Connection.
     * @param query A string containing the specific query that will be used to retrieve the desirec data.
     * @param rows An integer representing the number of rows of data their is for this holiday.
     * @return data A 2D string array, containing all of this holiday's data.
     */
    public abstract String[][] retrieveYear(Statement stmnt, String query, int rows);
}
