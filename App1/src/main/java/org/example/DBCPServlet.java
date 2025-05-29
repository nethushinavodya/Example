package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;
import org.example.db.DBConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/dbcp")
public class DBCPServlet extends HttpServlet {
    BasicDataSource dataSource;
    @Override
    public void init() throws ServletException {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/eventDb");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");
        dataSource.setInitialSize(50);
        dataSource.setMaxTotal(100);
    }

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = dataSource.getConnection();

            String id = req.getParameter("id");
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            String date = req.getParameter("date");
            String place = req.getParameter("place");

            PreparedStatement preparedStatement = connection.prepareStatement("insert into events values(?,?,?,?,?)");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, date);
            preparedStatement.setString(5, place);
            preparedStatement.executeUpdate();

            resp.getWriter().write("event saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = dataSource.getConnection();

            BufferedReader reader = req.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> data = mapper.readValue(requestBody, Map.class);

            try {
                String id = data.get("id");
                String name = data.get("name");
                String description = data.get("description");
                String date = data.get("date");
                String place = data.get("place");

                System.out.println(id + " " + name + " " + description + " " + date + " " + place);

                PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE events SET name = ?, description = ?, date = ?, place = ? WHERE id = ?"
                );
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, date);
                preparedStatement.setString(4, place);
                preparedStatement.setString(5, id);
                preparedStatement.executeUpdate();

                resp.getWriter().write("event updated");

            } catch (Exception e) {
                e.printStackTrace();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("error updating event");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Connection connection = dataSource.getConnection();

            String id = req.getParameter("id");
            System.out.println(id + "   deleted");
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from events where id = ?");
                preparedStatement.setString(1, id);
                preparedStatement.executeUpdate();
                resp.getWriter().write("event deleted");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}