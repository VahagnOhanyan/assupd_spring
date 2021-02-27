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

public class RequestController {

    @FXML
    private Button btnSave;
    @FXML
    private ComboBox customerBox;
    @FXML
    private ComboBox contractBox;
    @FXML
    private TextField txtRequest;
    @FXML
    private TextArea txtRequestDesc;

    String id = null;

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {

        customerBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!customerBox.getSelectionModel().isEmpty()) {
                contractBox.getItems().clear();
                if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") || MainController.role.equalsIgnoreCase("АУП")) {
                    contractBox.getItems().addAll(data("SELECT ct.contract_id, ct.contract_number FROM public.contract ct " +
                            "join public.customer cr on cr.customer_id = ct.customer_id AND cr.customer_id = " +
                            "'" + customerBox.getSelectionModel().getSelectedItem().toString().substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "' " +
                            "ORDER BY ct.contract_id"));
                } else {
                    contractBox.getItems().addAll(data("SELECT ct.contract_id, ct.contract_number FROM public.contract ct " +
                            "join public.customer cr on cr.customer_id = ct.customer_id AND cr.customer_id = " +
                            "'" + customerBox.getSelectionModel().getSelectedItem().toString().substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "' " +
                            "join public.contract_project cp on cp.contract_id = ct.contract_id " +
                            "join public.project p on p.project_id = cp.project_id " +
                            "join public.project_manager pm on pm.project_id = p.project_id " +
                            "join public.user u on u.user_id = pm.user_id " +
                            "WHERE u.user_fullname = '"+MainController.who+"' " +
                            "GROUP BY ct.contract_id, ct.contract_number " +
                            "ORDER BY ct.contract_id"));
                }
                contractBox.setDisable(false);
            } else {
                contractBox.getSelectionModel().clearSelection();
                contractBox.getItems().clear();
                contractBox.setDisable(true);
            }
        });

        contractBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!contractBox.getSelectionModel().isEmpty()) {
                txtRequest.setDisable(false);
            } else {
                txtRequest.setDisable(true);
            }
        });

        txtRequest.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                txtRequestDesc.setDisable(true);
                btnSave.setDisable(true);
            } else {
                txtRequestDesc.setDisable(false);
                btnSave.setDisable(false);
            }
        });

        txtRequestDesc.setWrapText(true);
    }

    public void addData() {
        formClear();
        btnSave.setText("Добавить");
        customerBox.getItems().addAll(data("SELECT customer_id, customer_name FROM public.customer"));
    }

    public void saveData(ActionEvent actionEvent) {
        dBconnection.openDB();
        try {

            if (btnSave.getText().equalsIgnoreCase("Добавить")) {
                if (!customerBox.getSelectionModel().isEmpty() && !txtRequest.getText().equals("")) {
                        dBconnection.getStmt().executeUpdate("INSERT INTO public.request (request_number, request_description, contract_id) VALUES " +
                                "('" + txtRequest.getText() + "', '" + txtRequestDesc.getText() + "', " +
                                "'" + contractBox.getSelectionModel().getSelectedItem().toString().substring(1, contractBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "')");

                } else {
                    infoAlert.setTitle("Ошибка ввода");
                    infoAlert.setContentText("Выберите заказчика и номер контракта");
                    infoAlert.showAndWait();
                }
            } else {
                if (!contractBox.getSelectionModel().isEmpty()) {
                    dBconnection.getStmt().executeUpdate("UPDATE public.request SET contract_id = '" + contractBox.getSelectionModel().getSelectedItem().toString().substring(1, contractBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "' " +
                            "WHERE request_id = '" + id + "'");
                }

                if (!txtRequest.getText().equals("")) {
                    dBconnection.getStmt().executeUpdate("UPDATE public.request SET request_number = '" + txtRequest.getText() + "' " +
                            "WHERE request_id = '" + id + "'");
                    dBconnection.getStmt().executeUpdate("UPDATE public.request SET request_description = '" + txtRequestDesc.getText() + "' " +
                            "WHERE request_id = '" + id + "'");
                } else {
                    infoAlert.setTitle("Ошибка ввода");
                    infoAlert.setContentText("Введите номер заявки");
                    infoAlert.showAndWait();
                }
            }
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
            dBconnection.closeDB();
            return;
        }

        dBconnection.closeDB();
        actionClose(actionEvent);
    }

    public void setData(String id) {
        formClear();
        this.id = id;
        btnSave.setText("Изменить");
        txtRequest.setDisable(false);
        txtRequestDesc.setDisable(false);
        customerBox.getItems().addAll(data("SELECT customer_id, customer_name FROM public.customer"));

        dataRequest("SELECT r.request_number, r.request_description, c.contract_id FROM public.request r " +
                "join public.contract c on c.contract_id = r.contract_id " +
                "WHERE r.request_id = '"+ id +"'");

        txtRequest.setText(data.get(0).get(0).toString());

        if (!data.get(0).get(1).toString().equalsIgnoreCase("")) {
            txtRequestDesc.setText(data.get(0).get(1).toString());
        } else {
            txtRequestDesc.setText("");
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

    private ObservableList dataRequest(String k) {
        try {
            dBconnection.openDB();
            data.clear();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                data.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch ( Exception e ) {
            errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return data;
    }

    private void formClear() {
        data.clear();
        dataSelect.clear();
        txtRequest.clear();
        txtRequest.setDisable(true);
        txtRequestDesc.clear();
        txtRequestDesc.setDisable(true);
        btnSave.setDisable(true);
        customerBox.getSelectionModel().clearSelection();
        customerBox.getItems().clear();
        contractBox.getItems().clear();
        contractBox.setDisable(true);
        id = null;

    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        formClear();
        stage.hide();
    }

}
