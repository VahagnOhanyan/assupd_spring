package ru.ctp.motyrev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import ru.ctp.motyrev.code.DBconnection;

import java.sql.SQLException;
import java.util.Optional;

public class ProjectController {

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

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {

        customerBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                contractBox.getItems().clear();
                contractBox.getItems().addAll(data("SELECT ct.contract_id, ct.contract_number FROM public.contract ct " +
                        "join public.customer cr on cr.customer_id = ct.customer_id AND cr.customer_id = " +
                        "'"+ customerBox.getSelectionModel().getSelectedItem().toString().substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) +"'"));
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

    public void addData() {
        formClear();
        customerBox.getItems().addAll(data("SELECT customer_id, customer_name FROM public.customer"));
        customerBox.getSelectionModel().select(0);
        customerBox.getSelectionModel().clearSelection();
    }

    public void setData(String exemp) {
        formClear();
        this.exemp = exemp;

        customerBox.getItems().addAll(data("SELECT customer_id, customer_name FROM public.customer"));

        dataEdit("SELECT cr.customer_id, cr.customer_name, c.contract_id, c.contract_number, p.project_name from public.project p " +
                "join public.contract_project cp on cp.project_id = p.project_id " +
                "join public.contract c on c.contract_id = cp.contract_id " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "WHERE p.project_id = '" + exemp + "'");

        btnSave.setText("Изменить");

        customerBox.getSelectionModel().select("[" + data.get(0).get(0).toString() + ", " + data.get(0).get(1).toString() + "]");
        contractBox.getSelectionModel().select("[" + data.get(0).get(2).toString() + ", " + data.get(0).get(3).toString() + "]");
        txtProject.setText(data.get(0).get(4).toString());
        txtProject.setDisable(false);
    }

    public void saveData(ActionEvent actionEvent) {
            dBconnection.openDB();
            if (btnSave.getText().equals("Добавить")) {
                try {
                    dBconnection.getStmt().executeUpdate("INSERT INTO public.project (project_name, customer_id, status_id) VALUES " +
                            "('" + txtProject.getText() + "', '" + customerBox.getSelectionModel().getSelectedItem().toString().substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "', " +
                            "(SELECT status_id FROM public.status s " +
                            "join public.status_type st on st.status_type_id = s.status_type " +
                            "WHERE st.status_type_name = 'projects' AND s.status_name = 'new'))");

                    if (!contractBox.getSelectionModel().isEmpty()) {
                        dBconnection.getStmt().executeUpdate("INSERT INTO public.contract_project (contract_id, project_id, status_id, active_from, active_to) VALUES " +
                                "('" + contractBox.getSelectionModel().getSelectedItem().toString().substring(1, contractBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "', " +
                                "(SELECT max(project_id) FROM public.project), " +
                                "(SELECT status_id FROM public.status s " +
                                "join public.status_type st on st.status_type_id = s.status_type " +
                                "WHERE st.status_type_name = 'links' AND s.status_name = 'linked'), current_timestamp, '2099-10-11 00:00:00')");
                    } else if (contractBox.getSelectionModel().isEmpty()) {
                        infoAlert.setTitle("Ошибка");
                        infoAlert.setContentText("Выберите нужный контракт");
                        infoAlert.showAndWait();
                        return;
                    }
                    dBconnection.getStmt().executeUpdate("INSERT INTO public.project_manager (project_id, user_id) VALUES " +
                            "((SELECT max(project_id) FROM public.project), " +
                            "(SELECT user_id FROM public.user " +
                            "WHERE user_fullname = '" + MainController.who + "'))");

                    dBconnection.getC().commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    dBconnection.closeDB();
                    return;
                }
            } else {
                confirmAlert.setTitle("Подтвердите выбор");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Вы действительно хотите изменить параметры проекта? Это приведет к изменениям во всех связанных структурах системы.");

                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    try {

                        System.out.println(customerBox.getSelectionModel().getSelectedItem().toString().substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")));
                        System.out.println(contractBox.getSelectionModel().getSelectedItem().toString().substring(1, contractBox.getSelectionModel().getSelectedItem().toString().indexOf(",")));

                        dBconnection.getStmt().executeUpdate("UPDATE public.contract_project SET contract_id = '" + contractBox.getSelectionModel().getSelectedItem().toString().substring(1, contractBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "' " +
                                "WHERE project_id = '" + exemp + "'");
                        dBconnection.getStmt().executeUpdate("UPDATE public.project SET project_name = '" + txtProject.getText() + "' " +
                                "WHERE project_id = '" + exemp + "'");
                        dBconnection.getStmt().executeUpdate("UPDATE public.project SET customer_id = '" + customerBox.getSelectionModel().getSelectedItem().toString().substring(1, customerBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "' " +
                                "WHERE project_id = '" + exemp + "'");

                        dBconnection.getC().commit();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        dBconnection.closeDB();
                        return;
                    }
                }
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

    private ObservableList dataEdit(String k) {
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
        sql=null;
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
