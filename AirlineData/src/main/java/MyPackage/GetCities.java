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
 * -- Purpose: Gets all the cities' data from the Transtats website and adds it to a new table in the Oracle Database.
 */
@WebServlet(name="GetCities")
public class GetCities extends HttpServlet {

    /**A global string that contains the tableName for the Cityies' data. */
    private static final String tableName = "mling459Cities";

    /**
     * Accepts and handles POST requests from the HTML form.
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
        Statement stmnt = null;
        String sRows = request.getParameter("rows");
        String sTotalTables = request.getParameter("totalTables");
        int rows;
        int totalTables;
        String returnString = "";

        // Sets the number of rows equal to -1 if there is no data in the Oracle Database for the cities
        // Otherwise sets rows equal to the number of rows of data
        if(sRows.equals("") || sRows == null) { rows = -1; }
        else { rows = Integer.parseInt(sRows); }

        // Sets the total number of table in the Oracle Database equal to 0 if there are no tables
        // Otherwise sets totalTables equal to the number of tables in the Oracle Database
        if(sTotalTables.equals("") || sRows == null || sTotalTables.equals("-1")) { totalTables = 0; }
        else { totalTables = Integer.parseInt(sTotalTables); }

        // Gets all the city data and adds it to a new Oracle table.
        rows = OracleCities();
        totalTables = totalTables + 1;

        // Adds the updated row amount and tables in the Database to the return String to properly set the cookie values.
        returnString += "CitiesMeta="+rows+";TotalTables="+totalTables+";";
        writer.println(returnString);
    }


    /**
     * This function gets all the cities and thier data from the Transtats website, creates a new table in the Oracle 
     * Database, and adds the new data to that table. It then returns the number of rows of data for this table.
     * @throws IOException Throws an IOException if any errors occur retrieving the data from the Transtats website.
     * @return rows The number of rows of data there is for the given table.
     */
    public static int OracleCities() throws IOException {
        // This is the url for the average cost per city
        String url = "https://www.transtats.bts.gov/AverageFare/";

        // Disables the HTTPS validation check
        disableCertificateValidation();

        // Gets all the data from the website
        Document doc = Jsoup.connect(url).get();
        Element dataTD = doc.getElementById("GridView1");
        Elements tableData = dataTD.getElementsByTag("td");
        List<String> textData = new ArrayList<String>();
        textData = tableData.eachText();

        // Calculates the number of rows, or cities
        int rows = (textData.size()/6);
        String[][] data = new String[rows][6];

        // Loops to get all the data from the Transtat website
        int i = 0;
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < 6; c++) {
                data[r][c] = textData.get(i);
                i++;
            }
        }

        // Adds the cities' data to an oracle datatable for easier access
        Statement stmnt;
        try {
            Connection con = new OracleConnection().connect();
            stmnt = con.createStatement();

            // Drops the table if it previoulsy existed
            boolean dropped = new OracleConnection().removeTable(stmnt, tableName);
            if(dropped) { }

            // Creates the table that will store the citites data
            String createTable = "CREATE TABLE \""+tableName+"\" (cNameFirst NVARCHAR2(50), "+
                "cNameLast NVARCHAR2(50), sName NVARCHAR2(10), aFare NVARCHAR2(10))";
            boolean created = new OracleConnection().addTable(stmnt, createTable);
            if(created) { }

            String insertString = "";
            try {
                for(int r = 0; r < rows; r++) {
                    // Splits the city name if it contains a space
                    if(data[r][2].contains(" ")) {
                        String[] tempString = data[r][2].split(" ");

                        // Removes any ' that are in the city name
                        if(tempString[0].contains("'")) {
                            tempString[0] = tempString[0].replace("'", "");
                        }
                        if(tempString[1].contains("'")) {
                            tempString[1] = tempString[1].replace("'", "");
                        }
                        insertString = ("INSERT INTO \""+tableName+"\" VALUES('"+tempString[0]+"','"+tempString[1]+"','"+data[r][3]+"','"+data[r][5]+"')");

                        // Removes any ' that are in the city name
                    } else if(data[r][2].contains("'")) {
                        String tempString = data[r][2].replace("'", "");
                        insertString = ("INSERT INTO \""+tableName+"\" VALUES('"+tempString+"',' "+"','"+data[r][3]+"','"+data[r][5]+"')");
                    } else {
                        insertString = ("INSERT INTO \""+tableName+"\" VALUES('"+data[r][2]+"',' "+"','"+data[r][3]+"','"+data[r][5]+"')");
                    }

                    stmnt.executeUpdate(insertString);
                }
            } catch(Exception e) { }
            // Closes the connection to the Oracle Database
            stmnt.close();
        } catch (Exception e) { }
        return rows;
    }


    /**
     * This disables the certificate validation set that is needed to access https
     * websites. This was the only way I was able to get around the certificate
     * validation, and I understand that this is generally not a good idea.
     */
    public static void disableCertificateValidation() {
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