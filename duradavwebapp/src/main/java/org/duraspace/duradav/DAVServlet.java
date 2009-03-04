package org.duraspace.duradav;

import java.io.*;
import javax.servlet.*;                                                         
import javax.servlet.http.*;

public class DAVServlet extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println("Hello whirled");
        out.flush();
        out.close();
    }

}

