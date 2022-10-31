package com.ctp.asupdspring.controllers;

import com.ctp.asupdspring.app.repo.ProjectRepository;
import com.ctp.asupdspring.app.repo.RequestRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
@FxmlView("project.fxml")
public class ProjectController {
    @FXML
    public AnchorPane anchorPane;
    @FXML
    private Button btnSave;
    @FXML
    private ComboBox customerBox;
    @FXML
    private ComboBox contractBox;
    @FXML
    private TextField txtProject;

    private String sql = null;
    private String exemp = null;
    String id = null;

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private final RequestRepository requestRepository;
    private final ProjectRepository projectRepository;
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
            if (newValue != null) {
                contractBox.getItems().clear();
                System.out.println("customerBox.getSelectionModel(): " + customerBox.getSelectionModel().getSelectedItem().toString()
                        .substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")));
                contractBox.getItems().addAll(data(projectRepository.getContracts(customerBox.getSelectionModel().getSelectedItem().toString()
                        .substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")))));
                contractBox.getSelectionModel().select(0);
                contractBox.getSelectionModel().clearSelection();
                contractBox.setDisable(false);
            } else {
                contractBox.getSelectionModel().select(0);
                contractBox.getSelectionModel().clearSelection();
                contractBox.getItems().clear();
                contractBox.setDisable(true);
            }
        });

        contractBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!contractBox.getSelectionModel().isEmpty()) {
                txtProject.setDisable(false);
            } else {
                txtProject.setDisable(true);
            }
        });

        txtProject.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                btnSave.setDisable(true);
            } else {
                btnSave.setDisable(false);
            }
        });
    }
    public void show(String title) {
        addData();
        stage.setTitle(title);
        stage.show();
    }
    public void addData() {
        formClear();
        customerBox.getItems().addAll(data(requestRepository.getCustomers()));
        customerBox.getSelectionModel().select(0);
        customerBox.getSelectionModel().clearSelection();
    }

    public void setData(String exemp) {
        formClear();
        this.exemp = exemp;

        customerBox.getItems().addAll(data(requestRepository.getCustomers()));

        dataEdit(projectRepository.getCustomerContractProjectBy(exemp));

        btnSave.setText("Изменить");

        customerBox.getSelectionModel().select("[" + data.get(0).get(0).toString() + ", " + data.get(0).get(1).toString() + "]");
        contractBox.getSelectionModel().select("[" + data.get(0).get(2).toString() + ", " + data.get(0).get(3).toString() + "]");
        txtProject.setText(data.get(0).get(4).toString());
        txtProject.setDisable(false);
    }

    public void saveData(ActionEvent actionEvent) {

        if (btnSave.getText().equals("Добавить")) {
            projectRepository.addProject(txtProject.getText(), customerBox.getSelectionModel().getSelectedItem().toString()
                    .substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")));

            if (!contractBox.getSelectionModel().isEmpty()) {
                projectRepository.addContractProject(contractBox.getSelectionModel().getSelectedItem().toString()
                        .substring(1, contractBox.getSelectionModel().getSelectedItem().toString().indexOf(",")));
            } else if (contractBox.getSelectionModel().isEmpty()) {
               /* infoAlert.setTitle("Ошибка");
                infoAlert.setContentText("Выберите нужный контракт");
                infoAlert.showAndWait();*/
                return;
            }
            projectRepository.addProjectManager(MainController.who);
        } else {
           /* confirmAlert.setTitle("Подтвердите выбор");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Вы действительно хотите изменить параметры проекта? Это приведет к изменениям во всех связанных структурах системы.");
*/
          //  Optional<ButtonType> result = confirmAlert.showAndWait();

        //    if (result.get() == ButtonType.OK) {

                System.out.println(customerBox.getSelectionModel().getSelectedItem().toString()
                        .substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")));
                System.out.println(contractBox.getSelectionModel().getSelectedItem().toString()
                        .substring(1, contractBox.getSelectionModel().getSelectedItem().toString().indexOf(",")));
                projectRepository.updateCustomerContractProject(contractBox.getSelectionModel().getSelectedItem().toString()
                                .substring(1, contractBox.getSelectionModel().getSelectedItem().toString().indexOf(",")), exemp, txtProject.getText(),
                        customerBox.getSelectionModel().getSelectedItem().toString()
                                .substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")));
       //     }
        }

        actionClose(actionEvent);
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
        } catch (Exception e) {
        /*    errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();*/
            e.printStackTrace();
        }
        return dataSelect;
    }

    private ObservableList dataEdit(List<Object[]> k) {
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
        } catch (Exception e) {
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
        txtProject.clear();
        txtProject.setDisable(true);
        btnSave.setDisable(true);
        customerBox.getSelectionModel().select(0);
        customerBox.getSelectionModel().clearSelection();
        contractBox.getSelectionModel().select(0);
        contractBox.getSelectionModel().clearSelection();
        customerBox.getItems().clear();
        contractBox.getItems().clear();
        contractBox.setDisable(true);
        sql = null;
        id = null;
        exemp = null;
        btnSave.setText("Добавить");
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        formClear();
        stage.hide();
    }


}
