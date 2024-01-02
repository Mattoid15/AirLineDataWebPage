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
 * -- Purpose: Gets all the holiday's data from the Transtats website and adds it to a new table in the Oracle Database.
 */
@WebServlet(name="GetHoliday")
public class GetHoliday extends HttpServlet {

    /** Array of each Holiday the user can pick from. */
    private static String[] Holidays = {"Presidents' Day", "Easter", "Memorial Day", "Independence Day", "Labor Day", "Thanksgiving", "Winter Break"};
    
    /** Array of all the http codes for each option. */
    private static String[] Codes = {"c4r5vqr065", "Rn56r4", "Zrz14vny", "V0qr2r0qr0pr", "Yno14", "gun0x5tv8v0t", "jv06r4", "FDFG", "FDFF", "FDFE", "FDFD", "FDEM", "FDEL"};

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
        String sRows = request.getParameter("rows");
        String sTotalTables = request.getParameter("totalTables");
        String holiday = request.getParameter("holiday");
        String cookieName = request.getParameter("cookieName");
        String tableName = "mling459"+cookieName;
        int rows;
        int totalTables;
        String returnString = "";

        // Sets the number of rows equal to -1 if there is no data in the Oracle Database for the cities
        // Otherwise sets rows equal to the number of rows of data
        if(sRows.equals("") || sRows == null) { rows = -1; }
        else { rows = Integer.parseInt(sRows); }

        // Sets the total number of table in the Oracle Database equal to 0 if there are no tables
        // Otherwise sets totalTables equal to the number of tables in the Oracle Database
        if(sTotalTables.equals("") || sRows == null) { totalTables = 0; }
        else { totalTables = Integer.parseInt(sTotalTables); }

        // Gets all the data for this holiday and adds it to a new Oracle table.
        rows = GetData(holiday, tableName);
        totalTables = totalTables + 1;

        // Adds the updated row amount and tables in the Database to the return String to properly set the cookie values.
        returnString += "TotalTables="+totalTables+";"+cookieName+"="+rows+";";
        writer.println(returnString);
    }


    /**
     * This function gets all the holiday's data from the Transtats website, creates a new table in the Oracle
     * Database, and adds the new data to that table. It then returns the number of rows of data for this table.
     * @param holiday A string that states which holiday the user selected.
     * @param tableName A string that contains the Oracle table name for this holiday.
     * @throws IOException Throws an IOException if any errors occur retrieving the data from the Transtats website.
     * @return rows The number of rows of data there is for the given table.
     */
    public static int GetData(String holiday, String tableName) throws IOException {
        String code = "";
        Statement stmnt = null;

        // Gets the url code associtated with this holiday
        for(int i = 0; i < Holidays.length; i++) {
            if(holiday.equals(Holidays[i])) {
                code = Codes[i];
                break;
            }
        }

        // Builds the url to get the data from
        String url = "https://www.transtats.bts.gov/HolidayDelay_Detail.asp?lrn4=FDFF"
            +"&pn44vr4=DDD&nv42146=DDD&U1yvqnB="+code;

        // Disables the HTTPS validation check
        disableCertificateValidation();

        // Connects to the website using Jsoup
        Document doc = Jsoup.connect(url).get();

        // Gets all the data relating to the selected holiday
        Elements dataTD = doc.getElementsByClass("dataTD");
        Elements dataTDRight = doc.getElementsByClass("dataTDRight");
        String[] splitTD = dataTD.text().split("\\s+");
        String[] splitTDRight = dataTDRight.text().split("\\s+");

        // Calculates the number of days there is data for, equates to number of rows
        int rows = (splitTDRight.length/13);
        String[][] data = new String[rows][15];
     
        // Loops to get the Date and Date of Week
        int i = 4;
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < 2; c++) {
                if(splitTD[i].matches("\\W+"))
                    break;
                data[r][c] = splitTD[i];
                i++;
            }
        }

        // Loops to get the remaining data for the holiday
        i = 0;
        for(int r = 0; r < rows; r++) {
            for(int c = 2; c < 15; c++) {
                data[r][c] = splitTDRight[i];
                i++;
            }
        }

        // Connects to the Oracle Database
        try {
            Connection con = new OracleConnection().connect();
            stmnt = con.createStatement();

            // Drops the table if it previoulsy existed
            boolean dropped = new OracleConnection().removeTable(stmnt, tableName);
            if(dropped) { }

            String query = "CREATE TABLE \""+tableName+"\" (sDay NVARCHAR2(10), "+
            "sDate NVARCHAR2(10), cOnTime NVARCHAR2(10), cCancelled NVARCHAR2(10))";

            // Creates the table that will store this holiday's data
            boolean created = new OracleConnection().addTable(stmnt, query);
            if(created) { }

            // Adds the data to the table
            for(int r = 0; r < rows; r++) {
                query = "INSERT INTO \""+tableName+"\" VALUES('"+data[r][1]+"','"+data[r][0]+"','"+data[r][4]+"','"+data[r][8]+"')";
                stmnt.executeUpdate(query);
            }

            // Closes the connection to the Oracle Database.
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
