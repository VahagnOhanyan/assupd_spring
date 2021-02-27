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

public class CustomerController {

    @FXML
    private TextField txtCustomerName;
    @FXML
    private Button btnAdd;
    @FXML
    private TextArea txtFullArea;

    private String exemp;

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();

    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {

        txtFullArea.setWrapText(true);

        txtCustomerName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                btnAdd.setDisable(true);
                txtFullArea.clear();
                txtFullArea.setDisable(true);
            } else {
                btnAdd.setDisable(false);
                txtFullArea.setDisable(false);
            }
        });
    }

    public void createData() {
        formClear();
    }

    public void setData (String exemp) {
        this.exemp = exemp;

        data("SELECT customer_name, customer_full_name FROM public.customer WHERE customer_id = '" + exemp + "'");

        btnAdd.setText("Изменить");
        txtCustomerName.setText(dataSelect.get(0).get(0).toString());
        if (dataSelect.get(0).get(1) != null) {
            txtFullArea.setText(dataSelect.get(0).get(1).toString());
        } else {
            txtFullArea.setText("");
        }
    }

    public void saveData(ActionEvent actionEvent) {
        dBconnection.openDB();
        try {
            if (btnAdd.getText().equals("Добавить")) {
                if (txtFullArea.getText().equals("")) {
                    dBconnection.getStmt().executeUpdate("INSERT INTO public.customer (customer_name) VALUES ('" + txtCustomerName.getText() + "')");
                } else {
                    dBconnection.getStmt().executeUpdate("INSERT INTO public.customer (customer_name, customer_full_name) VALUES ('" + txtCustomerName.getText() + "', '" + txtFullArea.getText() + "')");
                }
            } else {

                confirmAlert.setTitle("Подтвердите выбор");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Вы действительно хотите изменить наименование заказчика? Это приведет к изменениям во всех связанных структурах системы.");

                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK){
                    dBconnection.getStmt().executeUpdate("UPDATE public.customer SET customer_name = '" + txtCustomerName.getText() + "' " +
                            "WHERE customer_id = '" + exemp + "'");
                    dBconnection.getStmt().executeUpdate("UPDATE public.customer SET customer_full_name = '" + txtFullArea.getText() + "' " +
                            "WHERE customer_id = '" + exemp + "'");
                    btnAdd.setText("Добавить");
                } else {
                    dBconnection.closeDB();
                    return;
                }

            }
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
            errorAlert.setTitle("Ошибка");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("При добавлении нового заказчика произошла ошибка, возможно такой заказчик уже есть в базе");
            errorAlert.showAndWait();
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
        txtCustomerName.clear();
        btnAdd.setDisable(true);
        txtFullArea.clear();
        txtFullArea.setDisable(true);
        btnAdd.setText("Добавить");
        exemp = "";

    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }
}
