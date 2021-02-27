package ru.ctp.motyrev.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import ru.ctp.motyrev.code.Main;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class PassChangeController {

    @FXML
    private PasswordField fldPassOld;
    @FXML
    private PasswordField fldPassNew;
    @FXML
    private PasswordField fldPassCheck;

    Connection c = null;

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    private static String host;
    private static String port;
    private static String dbName;

    @FXML
    private void initialize() {

    }

    public void actionButtonPressed (ActionEvent actionEvent) {

        Object source = actionEvent.getSource();

        if (!(source instanceof Button)) {
            return;
        }

        Button clickedButton = (Button) source;

        switch (clickedButton.getId()) {
            case "btnCancel":
                formClear();
                actionClose(actionEvent);
                break;
        }
    }

    public void changePass(ActionEvent actionEvent) {
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

                PreparedStatement passCheck = c.prepareStatement("SELECT u.user_id_number, ui.user_info_id FROM public.user u " +
                        "join public.user_info ui on ui.user_info_id = u.user_info_id " +
                        "WHERE u.user_fullname = ? AND ui.user_pass = ?");
                if (fldPassOld.getText().equals("") || fldPassNew.getText().equals("") || fldPassCheck.getText().equals("")) {
                    infoAlert.setTitle("Ошибка");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Введите данные для смены пароля");
                    infoAlert.showAndWait();
                } else if (fldPassNew.getText().equals(fldPassCheck.getText())) {
                    passCheck.setString(1, MainController.who);
                    passCheck.setString(2, fldPassOld.getText());
                    ResultSet rs = passCheck.executeQuery();
                    if (rs.next()) {
                        Statement stmt = c.createStatement();
                        stmt.executeUpdate("UPDATE public.user_info SET user_pass = '"+ fldPassNew.getText() +"' " +
                                "WHERE user_info_id = '"+ rs.getString(2) +"'");
                        c.commit();
                        stmt.close();
                        infoAlert.setTitle("Смена пароля");
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("Пароль успешно изменен в системе");
                        infoAlert.showAndWait();
                        actionClose(actionEvent);
                    } else {
                        infoAlert.setTitle("Ошибка");
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("Введен неверный пароль");
                        infoAlert.showAndWait();
                        c.close();
                        passCheck.close();
                        return;
                    }
                    rs.close();
                    passCheck.close();
                    c.close();

                } else {
                    infoAlert.setTitle("Ошибка");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Введенные пароли не совпадают");
                    infoAlert.showAndWait();
                }

        } catch (Exception e) {
            e.printStackTrace();
            errorAlert.setTitle("Ошибка подключения");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("База данных не доступна, обратитесь к системному администратору.");
            errorAlert.showAndWait();
        }

        formClear();
    }

    private void formClear() {
        fldPassNew.clear();
        fldPassOld.clear();
        fldPassCheck.clear();
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

}
