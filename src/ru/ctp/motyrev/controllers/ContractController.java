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
import java.util.Optional;

public class ContractController {

    @FXML
    private Button btnSave;
    @FXML
    private ComboBox customerBox;
    @FXML
    private TextField txtNumber;
    @FXML
    private TextArea txtName;

    private String exemp = null;

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert warningAlert = new Alert(Alert.AlertType.WARNING);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {

        customerBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!customerBox.getSelectionModel().isEmpty()) {
                txtNumber.setDisable(false);
                txtName.setDisable(false);
            } else {
                txtNumber.setDisable(true);
                txtName.setDisable(true);
            }
        });

        txtNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                btnSave.setDisable(true);
            } else {
                btnSave.setDisable(false);
            }
        });

        txtName.setWrapText(true);
    }

    public void addData() {
        formClear();
        customerBox.getItems().addAll(data("SELECT customer_id, customer_name FROM public.customer"));
    }

    public void setData (String exemp) {
        formClear();
        this.exemp = exemp;

        data("SELECT contract_number, contract_name FROM public.contract WHERE contract_id = '" + exemp + "'");

        customerBox.setDisable(true);
        txtNumber.setDisable(false);
        txtName.setDisable(false);
        btnSave.setText("Изменить");

        txtNumber.setText(dataSelect.get(0).get(0).toString());
        if (dataSelect.get(0).get(1) != null) {
            txtName.setText(dataSelect.get(0).get(1).toString());
        } else {
            txtName.setText("");
        }
    }

    public void saveData(ActionEvent actionEvent) {
        dBconnection.openDB();
        try {
            if (btnSave.getText().equals("Добавить")) {
                if (!customerBox.getSelectionModel().isEmpty() & !txtNumber.getText().equals("")) {
                        if (txtName.getText().equals("")) {
                            txtName.setText("нет");
                        }
                        dBconnection.getStmt().executeUpdate("INSERT INTO public.contract (contract_number, contract_name, customer_id) VALUES " +
                                "('" + txtNumber.getText() + "', '" + txtName.getText() + "', '" + customerBox.getSelectionModel().getSelectedItem().toString().substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "')");
                        dBconnection.getC().commit();
                } else {
                    infoAlert.setTitle("Ошибка ввода");
                    infoAlert.setContentText("Выберите заказчика и внесите номер контракта");
                    infoAlert.showAndWait();
                }
            } else {
                if (!txtNumber.getText().equals("")) {
                    confirmAlert.setTitle("Подтвердите выбор");
                    confirmAlert.setHeaderText(null);
                    confirmAlert.setContentText("Вы действительно хотите изменить параметры контракта? Это приведет к изменениям во всех связанных структурах системы.");

                    Optional<ButtonType> result = confirmAlert.showAndWait();

                    if (result.get() == ButtonType.OK) {
                        dBconnection.getStmt().executeUpdate("UPDATE public.contract SET contract_number = '" + txtNumber.getText() + "' " +
                                "WHERE contract_id = '" + exemp + "'");
                        dBconnection.getStmt().executeUpdate("UPDATE public.contract SET contract_name = '" + txtName.getText() + "' " +
                                "WHERE contract_id = '" + exemp + "'");
                        dBconnection.getC().commit();
                    }
                }
            }
        } catch (SQLException e) {
            warningAlert.setTitle("Ошибка уникальности");
            warningAlert.setHeaderText(null);
            warningAlert.setContentText("Номер контракта должен быть уникален в базе");
            warningAlert.showAndWait();
            dBconnection.closeDB();
            return;
        }
        dBconnection.closeDB();
        actionClose(actionEvent);
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
        data.clear();
        dataSelect.clear();
        txtNumber.clear();
        txtNumber.setDisable(true);
        txtName.clear();
        txtName.setDisable(true);
        btnSave.setDisable(true);
        customerBox.setDisable(false);
        customerBox.getSelectionModel().clearSelection();
        customerBox.getItems().clear();
        exemp = null;

    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        formClear();
        stage.hide();
    }

}
