package com.example.demo;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "tutorServlet", value = "/tutor-servlet")
public class TutorServlet extends HttpServlet {
    private Connection con;

    private boolean connected;

    @Override
    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String dbURL = "jdbc:mysql://127.0.0.1:3306/";
            String database = "Tutor";
            String user = "root";
            String password = "";
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

    //http://localhost:8080/demo_war/tutor-servlet/GET?targa=
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        if (!name.startsWith("GET")) {
            resp.sendError(400, "Request syntax error!");
            System.err.println("name wrong");
            return;
        }
        String targa;
        if ((targa = req.getParameter("targa")) == null){
            resp.sendError(400, "Missing parameter \"targa\"!");
            System.err.println("no targa");
            return;
        }
        String query = "SELECT timestamp FROM reg WHERE plate = ?";
        PreparedStatement stat;
        ResultSet resultSet = null;
        try {
            stat = con.prepareStatement(query);
            stat.setString(1, targa);
            resultSet = stat.executeQuery();
        } catch (SQLException e) {
            resp.sendError(500, e.getMessage());
        }

        PrintWriter writer = new PrintWriter(resp.getWriter());
        writer.println("<?xml version=\"1.1\" encoding=\"UTF-8\"?>");
        writer.println("<timestamps plate=\"" + targa + "\">");
        try {
            while (resultSet.next()) {
                writer.println("\t<timestamp>" + resultSet.getTimestamp(1) + "</timestamp>");
            }
        } catch (SQLException ignored) {}
        writer.println("</timestamps>");
    }
}