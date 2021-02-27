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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static java.time.LocalDate.parse;

public class TaskController {

    @FXML
    private Button btnSave;
    @FXML
    private ComboBox projectBox;
    @FXML
    private ComboBox uomBox;
    @FXML
    private TextField txtTask;
    @FXML
    private TextField txtPA;
    @FXML
    private TextField txtTZ;
    @FXML
    private TextField txt_uom_plan;
    @FXML
    private TextField txt_uom_fact;
    @FXML
    private DatePicker taskDate;
    @FXML
    private TextArea txtAreaName;
    @FXML
    private TextArea txtAreaDesc;
    @FXML
    private ToggleSwitch outCheck;

    private Boolean out = false;
    private String exemp = null;

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    LocalDate taskCurrentDate = null;

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {

        txtAreaName.setWrapText(true);
        txtAreaDesc.setWrapText(true);

        projectBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!projectBox.getSelectionModel().isEmpty()) {
                txtTask.setDisable(false);
            } else {
                txtTask.setDisable(true);
            }

        });

        uomBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!uomBox.getSelectionModel().isEmpty()) {
                txt_uom_plan.setDisable(false);
                txt_uom_fact.setDisable(false);
            } else {
                txt_uom_plan.setDisable(true);
                txt_uom_fact.setDisable(true);
            }

        });

        txtPA.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue & txtPA.getText().equalsIgnoreCase("")) {
                txtPA.setText("0");
            }
        });

        txtTZ.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue & txtTZ.getText().equalsIgnoreCase("")) {
                txtTZ.setText("0");
            }
        });

        txt_uom_plan.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue & txt_uom_plan.getText().equalsIgnoreCase("")) {
                txt_uom_plan.setText("0");
            }
        });

        txt_uom_fact.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue & txt_uom_fact.getText().equalsIgnoreCase("")) {
                txt_uom_fact.setText("0");
            }
        });

        uomBox.disableProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue & !uomBox.getSelectionModel().isEmpty()) {
                txt_uom_plan.setDisable(false);
                txt_uom_fact.setDisable(false);
            } else {
                txt_uom_plan.setDisable(true);
                txt_uom_fact.setDisable(true);
            }

        });

        txtTask.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                txtAreaName.setDisable(true);
                txtAreaDesc.setDisable(true);
                btnSave.setDisable(true);
            } else {
                txtAreaName.setDisable(false);
                txtAreaDesc.setDisable(false);
            }
        });

        txtAreaName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                taskDate.setDisable(true);
                txtPA.setDisable(true);
                txtTZ.setDisable(true);
                outCheck.setDisable(true);
                uomBox.setDisable(true);
            } else {
                taskDate.setDisable(false);
                txtPA.setDisable(false);
                txtTZ.setDisable(false);
                outCheck.setDisable(false);
                uomBox.setDisable(false);
            }
        });

        txtAreaName.disableProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue & !txtAreaName.getText().equals("")) {
                taskDate.setDisable(false);
                txtPA.setDisable(false);
                txtTZ.setDisable(false);
                outCheck.setDisable(false);
                uomBox.setDisable(false);
            } else {
                taskDate.setDisable(true);
                txtPA.setDisable(true);
                txtTZ.setDisable(true);
                outCheck.setDisable(true);
                uomBox.setDisable(true);
            }
        });

        outCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(true)) {
                out = true;
            } else {
                out = false;
            }
        });

        taskDate.valueProperty().addListener(observable -> {
                btnSave.setDisable(false);
        });

        taskDate.disableProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue & taskDate.getValue() != null) {
                btnSave.setDisable(false);
            } else {
                btnSave.setDisable(true);
            }
        });

    }

    public void addData() {
        formClear();

        if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") || MainController.role.equalsIgnoreCase("АУП")) {
            projectBox.getItems().addAll(data("SELECT p.project_id, cr.customer_name, c.contract_number, p.project_name FROM public.project p " +
                    "join public.customer cr on cr.customer_id = p.customer_id " +
                    "join public.contract_project cp on cp.project_id = p.project_id " +
                    "join public.contract c on c.contract_id = cp.contract_id " +
                    "ORDER BY cr.customer_name"));
        } else {
            projectBox.getItems().addAll(data("SELECT p.project_id, cr.customer_name, c.contract_number, p.project_name FROM public.project p " +
                    "join public.customer cr on cr.customer_id = p.customer_id " +
                    "join public.contract_project cp on cp.project_id = p.project_id " +
                    "join public.contract c on c.contract_id = cp.contract_id " +
                    "join public.project_manager pm on pm.project_id = p.project_id " +
                    "join public.user u on u.user_id = pm.user_id " +
                    "WHERE u.user_fullname = '"+MainController.who+"' " +
                    "ORDER BY cr.customer_name"));
        }

        uomBox.getItems().addAll(data("SELECT task_uom_name FROM public.task_uom " +
                "ORDER BY task_uom_id"));
    }

    public void setData (String exemp){
        formClear();
        this.exemp = exemp;

        if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") || MainController.role.equalsIgnoreCase("АУП")) {
            projectBox.getItems().addAll(data("SELECT p.project_id, cr.customer_name, c.contract_number, p.project_name FROM public.project p " +
                    "join public.customer cr on cr.customer_id = p.customer_id " +
                    "join public.contract_project cp on cp.project_id = p.project_id " +
                    "join public.contract c on c.contract_id = cp.contract_id " +
                    "ORDER BY cr.customer_name"));
        } else {
            projectBox.getItems().addAll(data("SELECT p.project_id, cr.customer_name, c.contract_number, p.project_name FROM public.project p " +
                    "join public.customer cr on cr.customer_id = p.customer_id " +
                    "join public.contract_project cp on cp.project_id = p.project_id " +
                    "join public.contract c on c.contract_id = cp.contract_id " +
                    "join public.project_manager pm on pm.project_id = p.project_id " +
                    "join public.user u on u.user_id = pm.user_id " +
                    "WHERE u.user_fullname = '"+MainController.who+"' " +
                    "ORDER BY cr.customer_name"));
        }

        uomBox.getItems().addAll(data("SELECT task_uom_name FROM public.task_uom " +
                "ORDER BY task_uom_id"));

        dataEdit("SELECT t.task_number, t.task_name, t.task_description, t.task_income_date, t.task_pa_intensity, t.task_tz_intensity, t.task_out, tu.task_uom_name, t.task_unit_plan, t.task_unit_fact FROM public.task t " +
                "left join public.task_uom tu on tu.task_uom_id = t.task_uom_id " +
                "WHERE task_id = '" + exemp + "'");

        projectBox.setDisable(false);
        txtTask.setDisable(false);
        txtAreaName.setDisable(false);
        txtAreaDesc.setDisable(false);
        taskDate.setDisable(false);
        outCheck.setDisable(false);
        txtPA.setDisable(false);
        txtTZ.setDisable(false);
        btnSave.setDisable(false);
        btnSave.setText("Изменить");

        txtTask.setText(data.get(0).get(0).toString());
        txtAreaName.setText(data.get(0).get(1).toString());
        txtAreaDesc.setText(data.get(0).get(2).toString());
        taskCurrentDate = parse(data.get(0).get(3).toString());
        taskDate.setValue(taskCurrentDate);

        txtPA.setText(data.get(0).get(4).toString());
        txtTZ.setText(data.get(0).get(5).toString());

        if (data.get(0).get(6).toString().equalsIgnoreCase("t")) {
            out = true;
            outCheck.setSelected(true);
        } else {
            out = false;
            outCheck.setSelected(false);
        }

        if (!(data.get(0).get(7) == null)) {
            uomBox.getSelectionModel().select("[" + data.get(0).get(7).toString() + "]");
        }

        if (!(data.get(0).get(8) == null)) {
            txt_uom_plan.setText(data.get(0).get(8).toString());
        } else {
            txt_uom_plan.setText("0");
        }

        if (!(data.get(0).get(9) == null)) {
            txt_uom_fact.setText(data.get(0).get(9).toString());
        } else {
            txt_uom_fact.setText("0");
        }

        txt_uom_plan.setDisable(false);
        txt_uom_fact.setDisable(false);
    }

    public void saveData(ActionEvent actionEvent) {
        dBconnection.openDB();
        try {
            if (btnSave.getText().equals("Добавить")) {
                if (uomBox.getSelectionModel().isEmpty()) {
                    dBconnection.getStmt().execute("INSERT INTO public.task (task_number, task_name, task_description, status_id, task_income_date, project_id, task_pa_intensity, task_tz_intensity, task_out) VALUES " +
                            "('" + txtTask.getText().replace("Т", "T") + "', '" + txtAreaName.getText() + "', '" + txtAreaDesc.getText() + "', " +
                            "(SELECT status_id FROM public.status s " +
                            "join public.status_type st on st.status_type_id = s.status_type " +
                            "WHERE s.status_name = 'в работе' AND st.status_type_name = 'tasks'), " +
                            "'" + taskDate.getValue() + "', " +
                            "'" + projectBox.getSelectionModel().getSelectedItem().toString().substring(1, projectBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "', " +
                            "'" + txtPA.getText() + "', '" + txtTZ.getText() + "', '" + out + "')");
                } else {
                    dBconnection.getStmt().execute("INSERT INTO public.task (task_number, task_name, task_description, status_id, task_income_date, project_id, task_pa_intensity, task_tz_intensity, task_out, task_uom_id, task_unit_plan, task_unit_fact) VALUES " +
                            "('" + txtTask.getText().replace("Т", "T") + "', '" + txtAreaName.getText() + "', '" + txtAreaDesc.getText() + "', " +
                            "(SELECT status_id FROM public.status s " +
                            "join public.status_type st on st.status_type_id = s.status_type " +
                            "WHERE s.status_name = 'в работе' AND st.status_type_name = 'tasks'), " +
                            "'" + taskDate.getValue() + "', " +
                            "'" + projectBox.getSelectionModel().getSelectedItem().toString().substring(1, projectBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "', " +
                            "'" + txtPA.getText() + "', '" + txtTZ.getText() + "', '" + out + "', " +
                            "(SELECT task_uom_id FROM public.task_uom WHERE task_uom_name = '" + uomBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "'), " +
                            "'" + txt_uom_plan.getText() + "', " +
                            "'" + txt_uom_fact.getText() + "')");
                }

                dBconnection.getC().commit();
            } else {
                    confirmAlert.setTitle("Подтвердите выбор");
                    confirmAlert.setHeaderText(null);
                    confirmAlert.setContentText("Вы действительно хотите изменить параметры задачи? Это приведет к изменениям во всех связанных структурах системы.");

                    Optional<ButtonType> result = confirmAlert.showAndWait();

                    if (result.get() == ButtonType.OK) {

                        if (!projectBox.getSelectionModel().isEmpty()) {
                            dBconnection.getStmt().executeUpdate("UPDATE public.task SET project_id = '" + projectBox.getSelectionModel().getSelectedItem().toString().substring(1, projectBox.getSelectionModel().getSelectedItem().toString().indexOf(",")) + "' " +
                                    "WHERE task_id = '" + exemp + "'");
                        }

                        dBconnection.getStmt().executeUpdate("UPDATE public.task SET task_number = '" + txtTask.getText() + "' " +
                                "WHERE task_id = '" + exemp + "'");
                        dBconnection.getStmt().executeUpdate("UPDATE public.task SET task_name = '" + txtAreaName.getText() + "' " +
                                "WHERE task_id = '" + exemp + "'");
                        dBconnection.getStmt().executeUpdate("UPDATE public.task SET task_description = '" + txtAreaDesc.getText() + "' " +
                                "WHERE task_id = '" + exemp + "'");
                        dBconnection.getStmt().executeUpdate("UPDATE public.task SET task_income_date = '" + taskDate.getValue() + "' " +
                                "WHERE task_id = '" + exemp + "'");
                        dBconnection.getStmt().executeUpdate("UPDATE public.task SET task_pa_intensity = '" + txtPA.getText() + "' " +
                                "WHERE task_id = '" + exemp + "'");
                        dBconnection.getStmt().executeUpdate("UPDATE public.task SET task_tz_intensity = '" + txtTZ.getText() + "' " +
                                "WHERE task_id = '" + exemp + "'");
                        dBconnection.getStmt().executeUpdate("UPDATE public.task SET task_out = '" + out + "' " +
                                "WHERE task_id = '" + exemp + "'");

                        if (!uomBox.getSelectionModel().isEmpty()) {
                            dBconnection.getStmt().executeUpdate("UPDATE public.task SET task_uom_id = " +
                                    "(SELECT task_uom_id FROM public.task_uom WHERE task_uom_name = '" + uomBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "') " +
                                    "WHERE task_id = '" + exemp + "'");
                        }
                            dBconnection.getStmt().executeUpdate("UPDATE public.task SET task_unit_plan = '" + txt_uom_plan.getText() + "' " +
                                    "WHERE task_id = '" + exemp + "'");
                            dBconnection.getStmt().executeUpdate("UPDATE public.task SET task_unit_fact = '" + txt_uom_fact.getText() + "' " +
                                    "WHERE task_id = '" + exemp + "'");

                        dBconnection.getC().commit();
                    }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            infoAlert.setTitle("Ошибка");
            infoAlert.setContentText("Задача уже существует или произошла ошибка базы данных");
            infoAlert.showAndWait();
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
        txtTask.clear();
        txtTask.setDisable(true);
        btnSave.setDisable(true);
        btnSave.setText("Добавить");
        projectBox.getItems().clear();
        projectBox.setDisable(false);
        uomBox.getItems().clear();
        uomBox.setDisable(true);
        txtAreaName.clear();
        txtAreaName.setDisable(true);
        txtAreaDesc.clear();
        txtAreaDesc.setDisable(true);
        txtPA.setDisable(true);
        txtPA.setText("0");
        txtTZ.setDisable(true);
        txtTZ.setText("0");
        txt_uom_plan.setDisable(true);
        txt_uom_plan.setText("0");
        txt_uom_fact.setDisable(true);
        txt_uom_fact.setText("0");
        taskDate.setDisable(true);
        taskDate.setValue(null);
        outCheck.setDisable(true);
        outCheck.setSelected(false);
        out = false;
        taskCurrentDate = null;

    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        formClear();
        stage.hide();
    }

}
