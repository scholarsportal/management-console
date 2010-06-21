package org.duracloud.hello;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Andrew Woods
 *         Date: Nov 30, 2009
 */
public class HelloServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        write(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        write(request, response);
    }

    private void write(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        PrintWriter out = response.getWriter();
        out.println("Hello from DuraCloud.");
    }

}
