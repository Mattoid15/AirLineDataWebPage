package MyPackage;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import javax.net.ssl.*;
import javax.servlet.annotation.WebServlet;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;
import java.sql.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Author: Matthew Lingenfelter
 * -- Purpose: Gets all the year's data from the Transtats website and adds it to a new table in the Oracle Database.
 */
@WebServlet(name="GetYear")
public class GetYear extends HttpServlet {

    /** Array of each year the user can pick from */
    private static String[] Years = {"2023", "2022", "2021", "2020", "2019", "2018"};
    
    /** Array of all the http codes for each option. */
    private static String[] Codes = {"FDFG", "FDFF", "FDFE", "FDFD", "FDEM", "FDEL"};
    
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

        // Initializes local variables
        PrintWriter writer = response.getWriter();
        String sRows = request.getParameter("rows");
        String sTotalTables = request.getParameter("totalTables");
        String year = request.getParameter("year");
        String cookieName = request.getParameter("cookieName");
        String tableName = "mling459"+cookieName;
        String returnString = "";
        int rows;
        int totalTables;
        
        // Sets the number of rows equal to -1 if there is no data in the Oracle Database for the cities
        // Otherwise sets rows equal to the number of rows of data
        if(sRows.equals("") || sRows == null) { rows = -1; }
        else { rows = Integer.parseInt(sRows); }

        // Sets the total number of table in the Oracle Database equal to 0 if there are no tables
        // Otherwise sets totalTables equal to the number of tables in the Oracle Database
        if(sTotalTables.equals("") || sRows == null) { totalTables = 0; }
        else { totalTables = Integer.parseInt(sTotalTables); }

        // Gets all the data for this year and adds it to a new Oracle table.
        rows = GetData(year, tableName);
        totalTables = totalTables + 1;

        // Adds the updated row amount and tables in the Database to the return String to properly set the cooki values.
        returnString += "TotalTables="+totalTables+";"+cookieName+"="+rows+";";
        writer.println(returnString);
    }


    /**
     * This function gets all the year's data from the Transtats website, and creates a new table in the Oracle
     * Database, and adds the new data to that table. It then returns the number of rows of data for this table.
     * @param year A string that states which year the user selected.
     * @param tableName A string that contains the Oracle table name for this holiday.
     * @throws IOException Throws an IOException if any errors occur retrieving the data from the Transtats website.
     * @return rows The number of rows of data there is for the given table.
     */
    private static int GetData(String year, String tableName) throws IOException {
        String code = "";
        Statement stmnt = null;

        // Gets the url code associated with this year
        for(int i = 0; i < Years.length; i++) {
            if(year.equals(Years[i])) {
                code = Codes[i];
                break;
            }
        }

        // Builds the url to get the data from
        String url = "https://www.transtats.bts.gov/Marketing_Monthly.aspx?5ry_lrn4="
            +code+"&N44_Qry=E&5ry_Pn44vr4=DDD&5ry_Nv42146=DDD&heY_fryrp6lrn4=FDFG&heY_fryrp6Z106u=I";
        
        // Disables the HTTPS validation check
        disableCertificateValidation();

        // Connects to website using Jsoup
        Document doc = Jsoup.connect(url).get();

        // Gets all the data for the selected year
        Element tData = doc.getElementById("GridView1");
        Elements tableData = tData.getElementsByTag("td");
        String[] textData = tableData.text().split("\\s+");

        // Calculates the number of months, the current year may not have data for all 12 months
        int rows = (textData.length/9)-1;
        String[][] data = new String[rows][9];

        // Loops to get all the data for the year and build a table to store it
        int i = 0;
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < 9; c++) {
                data[r][c] = textData[i];
                i++;
            }
        }

        // Connects to the Oracle Database
        try {
            Connection con = new OracleConnection().connect();
            stmnt = con.createStatement();

            // Drops the table is it previously existed
            boolean dropped = new OracleConnection().removeTable(stmnt, tableName);

            String query = "CREATE TABLE \""+tableName+"\" (sMonth NVARCHAR2(10), "+
            "cOnTime NVARCHAR2(10), cCancelled NVARCHAR2(10))";

            // Creates the table that will store this year's data
            boolean create = new OracleConnection().addTable(stmnt, query);

            // Adds the data to the table
            for(int r = 0; r < rows; r++) {
                query = "INSERT INTO \""+tableName+"\" VALUES('"+
                data[r][0]+"', '"+data[r][2]+"', '"+data[r][6]+"')";
                stmnt.executeUpdate(query);
            }

            //Closes the connection to the Oracle Database.
            stmnt.close();
        } catch(Exception e) { }

        return rows;
    }


    /**
     * This disables the certificate validation set that is needed to access https
     * websites. This was the only way I was able to get around the certificate
     * validation, and I understand that this is generally not a good idea.
     */
   private static void disableCertificateValidation() {
       // Creates a trust manager that does not validate certificate chains
       TrustManager[] trustAllCerts = new TrustManager[] {
           new X509TrustManager() {
               public X509Certificate[] getAcceptedIssuers() {
                   return new X509Certificate[0];
               }
               public void checkClientTrusted(X509Certificate[] certs, String authType) {}
               public void checkServerTrusted(X509Certificate[] certs, String authType) {}
           }
       };
       // Ignore differences between given hostname and certificate hostname
       HostnameVerifier hv = new HostnameVerifier() {
           public boolean verify(String hostname, SSLSession session) {
               return true;
           }
       };
       // Install the all-trusting trust manager
       try {
           SSLContext sc = SSLContext.getInstance("SSL");
           sc.init(null, trustAllCerts, new SecureRandom());
           HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
           HttpsURLConnection.setDefaultHostnameVerifier(hv);
       } catch (Exception e) {}
   }
}
