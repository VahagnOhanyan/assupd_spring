package com.ctp.asupdspring.controllers;

import com.ctp.asupdspring.app.repo.RequestRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@FxmlView("request.fxml")
public class RequestController {
    @FXML
    public AnchorPane anchorPane;
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

  /*  private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);*/

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private final RequestRepository requestRepository;
    private Stage stage;

    @FXML
    private void initialize() {
        stage = new Stage();
        stage.setScene(new Scene(anchorPane));
        stage.setMinHeight(100);
        stage.setMinWidth(200);
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);


        customerBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!customerBox.getSelectionModel().isEmpty()) {
                contractBox.getItems().clear();
                if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") || MainController.role.equalsIgnoreCase("АУП")) {
                    contractBox.getItems().addAll(data(requestRepository.getContracts(customerBox.getSelectionModel().getSelectedItem().toString().substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")))));
                } else {
                    contractBox.getItems().addAll(data(requestRepository.getUserContracts(customerBox.getSelectionModel().getSelectedItem().toString().substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")), MainController.who)));
                            ;}
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
    public void show(String title) {
        stage.setTitle(title);
        stage.show();
    }
    public void addData() {
        formClear();
        btnSave.setText("Добавить");
        customerBox.getItems().addAll(data(requestRepository.getCustomers()));
    }

    public void saveData(ActionEvent actionEvent) {

            if (btnSave.getText().equalsIgnoreCase("Добавить")) {
                if (!customerBox.getSelectionModel().isEmpty() && !txtRequest.getText().equals("")) {
                    requestRepository.addRequest(txtRequest.getText(), txtRequestDesc.getText(), contractBox.getSelectionModel().getSelectedItem().toString()
                            .substring(1, contractBox.getSelectionModel().getSelectedItem().toString().indexOf(",")));
                } else {
                 /*   infoAlert.setTitle("Ошибка ввода");
                    infoAlert.setContentText("Выберите заказчика и номер контракта");
                    infoAlert.showAndWait();*/
                }
            } else {
                if (!contractBox.getSelectionModel().isEmpty()) {
                    requestRepository.updateContract(contractBox.getSelectionModel().getSelectedItem().toString()
                            .substring(1, contractBox.getSelectionModel().getSelectedItem().toString().indexOf(",")), id);
                }

                if (!txtRequest.getText().equals("")) {
                    requestRepository.updateRequest(txtRequest.getText(), id);
                    requestRepository.updateRequestDesc(txtRequestDesc.getText(), id);
                } else {
                   /* infoAlert.setTitle("Ошибка ввода");
                    infoAlert.setContentText("Введите номер заявки");
                    infoAlert.showAndWait();*/
                }
            }



        actionClose(actionEvent);
    }

    public void setData(String id) {
        formClear();
        this.id = id;
        btnSave.setText("Изменить");
        txtRequest.setDisable(false);
        txtRequestDesc.setDisable(false);
        customerBox.getItems().addAll(data(requestRepository.getCustomers()));

        dataRequest(requestRepository.getContractsBy(id));

        txtRequest.setText(data.get(0).get(0).toString());

        if (!data.get(0).get(1).toString().equalsIgnoreCase("")) {
            txtRequestDesc.setText(data.get(0).get(1).toString());
        } else {
            txtRequestDesc.setText("");
        }

    }

    private ObservableList data(List<Object[]> k) {
        try {

            dataSelect.clear();

            for (Object[] o : k) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (Object value : o) {
                    //Перебор колонок
                    row.add(String.valueOf(value));
                }
                dataSelect.add(row);
            }

        } catch ( Exception e ) {
           /* errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();*/
            e.printStackTrace();
        }
        return dataSelect;
    }

    private ObservableList dataRequest(List<Object[]> k) {
        try {

            data.clear();
            for (Object[] o : k) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (Object value : o) {
                    //Перебор колонок
                    row.add(String.valueOf(value));
                }
                data.add(row);
            }

        } catch ( Exception e ) {
           /* errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();*/
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
