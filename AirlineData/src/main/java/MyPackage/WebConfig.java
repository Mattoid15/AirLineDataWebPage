package MyPackage;

import java.io.*;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.*;
import MyPackage.RemoveAllTables;


/**
 * Author: Matthew Lingenfelter, Adapted from WebConfig.java by Dr.Spiegel.
 * -- Purpose: Handles cross domain filter chaining.
 */
public class WebConfig implements Filter {
    /**
     * Initializes the WebConfig class.
     * @param filterConfig The FilterConfig variable needed for the WebConfig to work.
     * @throws ServletException Throws ServletException if any errors occurs regarding the servlet handling.
     */
    public void init(FilterConfig filterConfig) throws ServletException { }

    /**
     * This chains the request to allow for cross domain access by adding the appropriate headers to the request.
     * @param req The ServletRequest variable.
     * @param res The ServletReponse variable used the set and add the appropriate headers.
     * @param chain The FilterChain variable used to chain the incoming request.
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        response.setHeader("Access-Control-Allow-Origin", "https://acad.kutztown.edu");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        response.addHeader("Access-Control-Expose-Headers", "responseType");
        response.addHeader("Access-Control-Expose-Headers", "observe");

        if(!(request.getMethod().equalsIgnoreCase("OPTIONS"))) {
            try {
                chain.doFilter(req, res);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            response.setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,PUT");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Access-Control-Expose-Headers"+"Authorization, Access-Control-Allow-Origin, access-control-allow-origin, content-type,"+ "access-control-request-headers,access-control-request-method,accept,authorization,x-requested-with,responseType,observe");
            response.setHeader("Access-Control-Allow-Origin", "https://acad.kutztown.edu");
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    /**
     * This function is used to remove all the tables in the Oracle Database. It runs when the servlet is destroyed.
     */
    public void destroy(ServletRequest req) {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession();
        // Removed all the tables from the Oracle Database.
        //RemoveAllTables.RemoveAllTables();
        session.invalidate();
     }
}
