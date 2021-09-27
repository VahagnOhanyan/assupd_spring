package ru.ctp.motyrev.code;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBconnection {
    Connection c = null;
    Statement stmt = null;
    Statement serviceStmt = null;
    ResultSet rs = null;
    ResultSet serviceRs = null;

    private static String host;
    private static String port;
    private static String dbName;

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    public void openDB () {
        try {
            InputStream inp;
            Properties props = new Properties();
            inp = this.getClass().getResource("/ru/ctp/motyrev/resources/dbconnection.ini").openStream();
            props.load(inp);

            host = props.getProperty("db_hostname");
            port = props.getProperty("db_port");
            dbName = props.getProperty("db_name");

            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://" + host +":" + port + "/" + dbName ,"postgres", "M6k4V@9$rXuih~!Fer");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            serviceStmt = c.createStatement();
        } catch ( Exception e ) {
            e.printStackTrace();
            errorAlert.setTitle("Ошибка подключения");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("База данных не доступна, обратитесь к системному администратору.");
            errorAlert.showAndWait();
        }
    }

    public void query (String sql) {
        try {
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void queryClose () {
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void serviceQuery (String sql) {
        try {
            serviceRs = serviceStmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void serviceQueryClose () {
        try {
            serviceRs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeDB() {
        try {
            stmt.close();
            serviceStmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

    public Statement getServiceStmt() {
        return serviceStmt;
    }

    public void setServiceStmt(Statement serviceStmt) {
        this.serviceStmt = serviceStmt;
    }

    public ResultSet getServiceRs() {
        return serviceRs;
    }

    public void setServiceRs(ResultSet serviceRs) {
        this.serviceRs = serviceRs;
    }

    public Connection getC() {
        return c;
    }

    public void setC(Connection c) {
        this.c = c;
    }

    public Statement getStmt() {
        return stmt;
    }

    public void setStmt(Statement stmt) {
        this.stmt = stmt;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }
}

