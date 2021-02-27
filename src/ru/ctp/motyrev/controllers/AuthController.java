package ru.ctp.motyrev.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import ru.ctp.motyrev.code.Main;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class AuthController {

    @FXML
    private TextField fldLogin;
    @FXML
    private PasswordField fldPass;
    @FXML
    private ImageView logoView;

    Connection c = null;

    String userprofile = System.getenv("USERPROFILE");

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    private static String host;
    private static String port;
    private static String dbName;

    @FXML
    private void initialize() {

        fldPass.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ENTER)){

                saveLogin();
                authentication();

            }
        });

        Image im = new Image("/ru/ctp/motyrev/images/logo.png");
        logoView.setImage(im);

        InputStream inp;
        Properties props = new Properties();

        try {
            inp = new FileInputStream(userprofile + "\\ASUPD\\config\\userLogin.ini");
            props.load(inp);
            if (props.getProperty("user_login") != null) {
                fldLogin.setText(props.getProperty("user_login"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionButtonPressed (ActionEvent actionEvent) {

        Object source = actionEvent.getSource();

        if (!(source instanceof Button)) {
            return;
        }

        Button clickedButton = (Button) source;

        switch (clickedButton.getId()) {
            case "btnLogin":
                saveLogin();
                authentication();
                break;

            case "btnExit":
                System.exit(0);
                break;
        }
    }

    private void saveLogin() {

        File propDir = new File(userprofile + "\\ASUPD\\config");

        if (!propDir.exists()) {
            try {
                propDir.mkdirs();
            } catch(SecurityException se){
                se.printStackTrace();
            }
        }

        try {
            Properties properties = new Properties();
            properties.setProperty("user_login", fldLogin.getText());

            File file = new File(userprofile + "\\ASUPD\\config\\userLogin.ini");
            FileOutputStream fileOut = new FileOutputStream(file);
            properties.store(fileOut, "Autologin");
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authentication() {
        try {

            InputStream inp;
            Properties props = new Properties();
            inp = this.getClass().getResource("/ru/ctp/motyrev/resources/dbconnection.ini").openStream();
            props.load(inp);

            host = props.getProperty("db_hostname");
            port = props.getProperty("db_port");
            dbName = props.getProperty("db_name");

            Class.forName("org.postgresql.Driver");
//            c = DriverManager.getConnection("jdbc:postgresql://" + host +":" + port + "/" + dbName ,"postgres", "M6k4V@9$rXuih~!Fer");
            c = DriverManager.getConnection("jdbc:postgresql://" + host +":" + port + "/" + dbName ,"postgres", "Nwc54321123");
            c.setAutoCommit(false);
            PreparedStatement versionCheck = c.prepareStatement("SELECT system_build_version FROM public.system_build " +
                    "WHERE system_build_version = ?");

            versionCheck.setString(1, "1.0.2");
            ResultSet versionSet = versionCheck.executeQuery();
            if (versionSet.next()) {
                PreparedStatement authCheck = c.prepareStatement("SELECT ur.user_role_name, u.user_fullname FROM public.user_role ur " +
                        "join public.user_info ui on ui.user_role_id = ur.user_role_id " +
                        "join public.user u on u.user_info_id = ui.user_info_id " +
                        "WHERE ui.user_login = ? AND ui.user_pass = ?");
                if (fldLogin.getText().equals("") || fldPass.getText().equals("")) {
                    infoAlert.setTitle("Ошибка аутентификации");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Введите данные для аутентификации");
                    infoAlert.showAndWait();
                } else {
                    authCheck.setString(1, fldLogin.getText());
                    authCheck.setString(2, fldPass.getText());
                    ResultSet rs = authCheck.executeQuery();
                    if (rs.next()) {
                        MainController.role = rs.getString(1);
                        MainController.who = rs.getString(2);
                        Main.authStage.close();
                    } else {
                        infoAlert.setTitle("Ошибка аутентификации");
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("Проверьте правильность ввода данных для аутентификации");
                        infoAlert.showAndWait();
                        c.close();
                        authCheck.close();
                        versionCheck.close();
                    }
                    rs.close();
                    versionSet.close();
                    c.close();
                }
            } else {
                infoAlert.setTitle("Ошибка аутентификации");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Версия клиента не совпадает с версией базы данных. Обратитесь к системному администратору для обновления ПО.");
                infoAlert.showAndWait();
                versionSet.close();
                c.close();
                System.exit(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorAlert.setTitle("Ошибка подключения");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("База данных не доступна, обратитесь к системному администратору.");
            errorAlert.showAndWait();
        }
    }
}
