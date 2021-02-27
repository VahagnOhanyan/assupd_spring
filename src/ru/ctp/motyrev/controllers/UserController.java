package ru.ctp.motyrev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.ctp.motyrev.code.DBconnection;

import java.sql.SQLException;

public class UserController {

    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private ComboBox roleBox;
    @FXML
    private ComboBox siteBox;
    @FXML
    private ComboBox statusBox;
    @FXML
    private TextField txtTabel;
    @FXML
    private TextField txtSurname;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtMidname;
    @FXML
    private TextField txtTel;
    @FXML
    private TextField txtAdress;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtLogin;
    @FXML
    private TextField txtPassword;


    private String sql = null;
    private String idNumber = null;
    String id = null;

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataSelect2 = FXCollections.observableArrayList();

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {

        siteBox.getItems().addAll(data("SELECT site_name FROM public.site ORDER BY site_id"));
        statusBox.getItems().addAll(data("SELECT user_activity_name FROM public.user_activity ORDER BY user_activity_id"));
        roleBox.getItems().addAll(data("SELECT user_role_name FROM public.user_role WHERE user_role_name != 'Super_user' AND user_role_name != 'Admin' ORDER BY user_role_name"));

    }

    public void addData () {
        formClear();
        siteBox.getSelectionModel().select(0);
        statusBox.getSelectionModel().select(0);
        roleBox.getSelectionModel().select(4);
    }

    public void setData (String idNumber){
        formClear();
        this.idNumber = idNumber;

        btnSave.setText("Изменить");

        data("SELECT u.user_id_number, u.user_surname, u.user_name, u.user_midname, u.user_tel, u.user_adress, u.user_email, ui.user_login, ui.user_pass, ur.user_role_name, s.site_name, ua.user_activity_name FROM public.user u " +
                "left join public.user_info ui on ui.user_info_id = u.user_info_id " +
                "left join public.user_role ur on ur.user_role_id = ui.user_role_id " +
                "left join public.site s on s.site_id = u.site_id " +
                "left join public.user_activity ua on u.user_activity_id = ua.user_activity_id " +
                "WHERE u.user_id_number = '" + idNumber + "'");

        txtTabel.setText(dataSelect.get(0).get(0).toString());
        txtSurname.setText(dataSelect.get(0).get(1).toString());

        if (dataSelect.get(0).get(2) != null) {
                txtName.setText(dataSelect.get(0).get(2).toString());
        } else {
                txtName.setText("");
        }

        if (dataSelect.get(0).get(3) != null) {
                txtMidname.setText(dataSelect.get(0).get(3).toString());
        } else {
                txtMidname.setText("");
        }

        txtTel.setText(dataSelect.get(0).get(4).toString());
        txtAdress.setText(dataSelect.get(0).get(5).toString());
        txtEmail.setText(dataSelect.get(0).get(6).toString());

        if (dataSelect.get(0).get(7) != null) {
            txtLogin.setText(dataSelect.get(0).get(7).toString());
        } else {
            txtLogin.setText("");
        }

        if (dataSelect.get(0).get(8) != null) {
            txtPassword.setText(dataSelect.get(0).get(8).toString());
        } else {
            txtPassword.setText("");
        }

        if (dataSelect.get(0).get(9) != null) {
            roleBox.getSelectionModel().select("[" +dataSelect.get(0).get(9).toString() + "]");
        } else {
            roleBox.getSelectionModel().clearSelection();
        }

        if (dataSelect.get(0).get(10) != null) {
            siteBox.getSelectionModel().select("[" +dataSelect.get(0).get(10).toString() + "]");
        } else {
            siteBox.getSelectionModel().clearSelection();
        }

        if (dataSelect.get(0).get(11) != null) {
            statusBox.getSelectionModel().select("[" +dataSelect.get(0).get(11).toString() + "]");
        } else {
            statusBox.getSelectionModel().clearSelection();
        }
    }

    public void saveData(ActionEvent actionEvent) {

        if (txtAdress.getText().equals("")) {
            txtAdress.setText("нет");
        }

        if (txtEmail.getText().equals("")) {
            txtEmail.setText("нет");
        }

        if (txtTel.getText().equals("")) {
            txtTel.setText("нет");
        }

        if (btnSave.getText().equalsIgnoreCase("Добавить")) {

            if (!txtTabel.getText().equals("") & !txtSurname.getText().equals("") & !txtLogin.getText().equals("") & !txtPassword.getText().equals("") & !siteBox.getSelectionModel().isEmpty() & !roleBox.getSelectionModel().isEmpty() & !statusBox.getSelectionModel().isEmpty()) {

                dBconnection.openDB();
                try {
                    dBconnection.getStmt().executeUpdate("WITH t2 AS (INSERT INTO public.user_info " +
                            "(user_login, user_pass, user_role_id) " +
                            "SELECT '" + txtLogin.getText() + "', '" + txtPassword.getText() + "', user_role_id " +
                            "FROM public.user_role WHERE user_role_name = '" + roleBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "' " +
                            "RETURNING user_info_id) " +
                            "INSERT INTO public.user (user_id_number, user_surname, user_name, user_midname, user_fullname, user_tel, user_adress, user_email, user_info_id, site_id, user_activity_id) " +
                            "SELECT '" + txtTabel.getText() + "', '" + txtSurname.getText() + "', '" + txtName.getText() + "', '" + txtMidname.getText() + "', " +
                            "'" + txtSurname.getText() + " " + txtName.getText() + " " + txtMidname.getText() + "', '" + txtTel.getText() + "', " +
                            "'" + txtAdress.getText() + "', '" + txtEmail.getText() + "', t2.user_info_id, " +
                            "(SELECT site_id FROM public.site WHERE site_name = '" + siteBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "'), " +
                            "(SELECT user_activity_id FROM public.user_activity WHERE user_activity_name = '" + statusBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "') " +
                            "FROM t2");
                    dBconnection.getC().commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    infoAlert.setTitle("Ошибка");
                    infoAlert.setContentText("Табельный номер или логин уже существует");
                    infoAlert.showAndWait();
                    dBconnection.closeDB();
                    return;
                }
                dBconnection.closeDB();
                actionClose(actionEvent);
            } else {
                infoAlert.setTitle("Недостаточно данных");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Заполните все обязательные поля");
                infoAlert.showAndWait();
                return;
            }

        } else {
            dataSelect.clear();
            data("SELECT user_info_id FROM public.user WHERE user_id_number = '"+ idNumber +"'");

            dBconnection.openDB();

            if ((dataSelect.size()) > 0 & (dataSelect.get(0).get(0) != null)) {

                if (!txtTabel.getText().equals("") & !txtSurname.getText().equals("") & !txtLogin.getText().equals("") & !txtPassword.getText().equals("")) {

                    try {
                        dBconnection.getStmt().executeUpdate("UPDATE public.user SET (user_id_number, user_surname, user_name, user_midname, user_fullname, user_tel, user_adress, user_email, site_id, user_activity_id) = " +
                                "('" + txtTabel.getText() + "', '" + txtSurname.getText() + "', '" + txtName.getText() + "', '" + txtMidname.getText() + "', " +
                                "'" + txtSurname.getText() + " " + txtName.getText() + " " + txtMidname.getText() + "', '" + txtTel.getText() + "', " +
                                "'" + txtAdress.getText() + "', '" + txtEmail.getText() + "', " +
                                "(SELECT site_id FROM public.site WHERE site_name = '" + siteBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "'), " +
                                "(SELECT user_activity_id FROM public.user_activity WHERE user_activity_name = '" + statusBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "')) " +
                                "WHERE user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + idNumber + "')");

                        dBconnection.getStmt().executeUpdate("UPDATE public.user_info SET (user_login, user_pass, user_role_id) = " +
                                "('" + txtLogin.getText() + "', '" + txtPassword.getText() + "', " +
                                "(SELECT user_role_id FROM public.user_role WHERE user_role_name = " +
                                "'" + roleBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "')) " +
                                "WHERE user_info_id = '" + dataSelect.get(0).get(0) + "'");

                        dBconnection.getC().commit();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        infoAlert.setTitle("Ошибка");
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("Табельный номер или логин уже существует");
                        infoAlert.showAndWait();
                        dBconnection.closeDB();
                        return;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        infoAlert.setTitle("Ошибка");
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("Заполните все обязательные поля");
                        infoAlert.showAndWait();
                        dBconnection.closeDB();
                        return;
                    }

                } else {
                    infoAlert.setTitle("Недостаточно данных");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Заполните все обязательные поля");
                    infoAlert.showAndWait();
                    dBconnection.closeDB();
                    return;
                }
            } else {
                try {
                    if (!txtTabel.getText().equals("") & !txtSurname.getText().equals("") & !txtLogin.getText().equals("") & !txtPassword.getText().equals("")) {
                            dBconnection.getStmt().executeUpdate("WITH t2 AS (INSERT INTO public.user_info " +
                                    "(user_login, user_pass, user_role_id) " +
                                    "SELECT '" + txtLogin.getText() + "', '" + txtPassword.getText() + "', user_role_id " +
                                    "FROM public.user_role WHERE user_role_name = '" + roleBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "' " +
                                    "RETURNING user_info_id) " +
                                    "UPDATE public.user SET (user_id_number, user_surname, user_name, user_midname, user_fullname, user_tel, user_adress, user_email, user_info_id) = " +
                                    "(SELECT '" + txtTabel.getText() + "', '" + txtSurname.getText() + "', '" + txtName.getText() + "', '" + txtMidname.getText() + "', " +
                                    "'" + txtSurname.getText() + " " + txtName.getText() + " " + txtMidname.getText() + "', '" + txtTel.getText() + "', " +
                                    "'" + txtAdress.getText() + "', '" + txtEmail.getText() + "', t2.user_info_id " +
                                    "FROM t2) WHERE user_id_number = '" + idNumber + "'");
                            dBconnection.getC().commit();

                    } else if (!txtTabel.getText().equals("") & !txtSurname.getText().equals("") & (txtLogin.getText().equals("") | txtPassword.getText().equals(""))) {
                        dBconnection.getStmt().executeUpdate("UPDATE public.user SET (user_id_number, user_surname, user_name, user_midname, user_fullname, user_tel, user_adress, user_email) = " +
                                "('" + txtTabel.getText() + "', '" + txtSurname.getText() + "', '" + txtName.getText() + "', '" + txtMidname.getText() + "', " +
                                "'" + txtSurname.getText() + " " + txtName.getText() + " " + txtMidname.getText() + "', '" + txtTel.getText() + "', " +
                                "'" + txtAdress.getText() + "', '" + txtEmail.getText() + "') WHERE user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + idNumber + "')");
                        dBconnection.getC().commit();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    infoAlert.setTitle("Ошибка");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Табельный номер или логин уже существует");
                    infoAlert.showAndWait();
                    dBconnection.closeDB();
                    return;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    infoAlert.setTitle("Ошибка");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Заполните все обязательные поля");
                    infoAlert.showAndWait();
                    dBconnection.closeDB();
                    return;
                }
            }
            dBconnection.closeDB();
            actionClose(actionEvent);
        }
    }

    private ObservableList data(String k) {
        try {
            dBconnection.openDB();
            dataSelect.clear();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                dataSelect.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch ( Exception e ) {
            errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return dataSelect;
    }

    private void formClear() {
        btnSave.setText("Добавить");
        dataSelect.clear();
        dataSelect2.clear();
        siteBox.getSelectionModel().select(0);
        statusBox.getSelectionModel().select(0);
        roleBox.getSelectionModel().select(4);
        siteBox.getSelectionModel().clearSelection();
        roleBox.getSelectionModel().clearSelection();
        statusBox.getSelectionModel().clearSelection();
        txtTabel.clear();
        txtSurname.clear();
        txtName.clear();
        txtMidname.clear();
        txtTel.clear();
        txtAdress.clear();
        txtEmail.clear();
        txtLogin.clear();
        txtPassword.clear();
        sql=null;
        id = null;
        idNumber = null;

    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        formClear();
        stage.hide();
    }

}
