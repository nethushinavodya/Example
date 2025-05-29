package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/lifecycle")
public class ServletLifeCycles extends HttpServlet {
//init method is called when the servlet is started
    @Override
    public void init() throws ServletException {
        System.out.println("servlet lifecycle init");
    }
//do get method is called when the servlet is accessed
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("servlet lifecycle do get");
    }
//destroy method is called when the servlet is stopped
    @Override
    public void destroy() {
        System.out.println("servlet lifecycle destroy");
    }
}
