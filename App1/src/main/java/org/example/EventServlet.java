package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.example.db.DBConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/event")
public class EventServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       /* resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");*/
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();

            System.out.println(System.getProperty("java.class.path"));

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
    //save a event

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");*/
        try {
            Connection connection = DBConnection.getDbConnection().getConnection();
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
       /* resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");*/
/*
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
*/

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

            Connection connection = DBConnection.getDbConnection().getConnection();

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
    }

    @SneakyThrows
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     /*   resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
*/
        Connection connection = DBConnection.getDbConnection().getConnection();
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
    }
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}