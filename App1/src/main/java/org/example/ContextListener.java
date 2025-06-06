package org.example;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.SQLException;

@WebListener
public class ContextListener implements ServletContextListener {

    //contextInitialized method is called when the servlet is started here the connection pool is created
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/eventDb");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");
        dataSource.setInitialSize(50);
        dataSource.setMaxTotal(100);

        ServletContext context = sce.getServletContext();
        context.setAttribute("dataSource", dataSource);

        System.out.println("context initialized connection pool created");
    }
    //contextDestroyed method is called when the servlet is stopped here the connection pool is destroyed
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            System.out.println("context destroyed");
            ServletContext context = sce.getServletContext();
            BasicDataSource dataSource = (BasicDataSource) context.getAttribute("dataSource");
            dataSource.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
