package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/inbuild")
public class InBuildDBCP extends HttpServlet {
    @Resource(name = "java:comp/env/jdbc/pool")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = dataSource.getConnection();
            ResultSet resultSet = connection.prepareStatement("select * from events").executeQuery();

            List<Map<String, String>> eList = new ArrayList<>();
            while (resultSet.next()) {
                Map<String,String> event = new HashMap<String,String>();
                event.put("eid", resultSet.getString("id"));
                event.put("ename", resultSet.getString("name"));
                event.put("edescription", resultSet.getString("description"));
                event.put("edate", resultSet.getString("date"));
                event.put("eplace", resultSet.getString("place"));
                eList.add(event);
            }
            resp.setContentType("application/json");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(resp.getWriter(), eList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
