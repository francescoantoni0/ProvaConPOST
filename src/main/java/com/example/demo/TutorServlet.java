package com.example.demo;


import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.ws.rs.GET;

@WebServlet(name = "tutorServlet", value = "/tutor-servlet")
public class TutorServlet extends HttpServlet {
    private Connection con;
    final private String dbURL = "jdbc:mysql://127.0.0.1:3306/", database = "Tutor", user = "root", password = "";

    private boolean connected;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(dbURL + database, user, password);
            connected = true;
        } catch (ClassNotFoundException | SQLException e) {
            connected = false;
        }
    }

    @Override
    public void destroy() {
        try {
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //http://localhost:8080/demo_war/tutor-servlet/POST?targa=
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!connected) {
            resp.sendError(500, "DBMS server error!");
            return;
        }

        String reqURL = req.getRequestURL().toString();
        String[] urlSection = reqURL.split("/");
        String name = urlSection[urlSection.length - 1];
        if (name == null) {
            resp.sendError(400, "Request syntax error!");
            System.err.println("name = null");
            return;
        }
        if (name.isEmpty()) {
            resp.sendError(400, "Request syntax error!");
            System.err.println("name empty");
            return;
        }

        if (!name.startsWith("POST")) {
            resp.sendError(400, "Request syntax error!");
            System.err.println("name wrong");
            return;
        }

        String targa = req.getParameter("targa");
        String query = "INSERT INTO reg(plate, timestamp) VALUES (?, current_timestamp)";
        PreparedStatement stat;
        try {
            stat = con.prepareStatement(query);
            stat.setString(1, targa);
            stat.executeUpdate();
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        }
    }
}