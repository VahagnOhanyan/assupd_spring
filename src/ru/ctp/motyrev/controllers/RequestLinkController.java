package ru.ctp.motyrev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.ListSelectionView;
import ru.ctp.motyrev.code.DBconnection;

import java.sql.SQLException;

public class RequestLinkController {

    @FXML
    private Button btnSave;
    @FXML
    private ListSelectionView taskView;
    @FXML
    private Label lblRequest;
    @FXML
    private TextField fldSearch;

    String requestNum = null;
    String requestNumber = null;

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList<String>> dataSelectSource = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> dataSelectTarget = FXCollections.observableArrayList();

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {

        taskView.setSourceHeader(new Label("Свободные задачи"));
        taskView.setTargetHeader(new Label("Задачи в заявке"));
        fldSearch.textProperty().addListener((observable, oldValue, newValue) ->
                taskView.getSourceItems().setAll(filterList(dataSelectSource, newValue)));
    }

    private ObservableList<ObservableList<String>> filterList(ObservableList<ObservableList<String>> list, String searchText) {
        ObservableList<ObservableList<String>> filteredList = FXCollections.observableArrayList();
        if (searchText.trim().equals("")) {
            return dataSelectSource;
        }
        for (ObservableList<String> l : list) {
            for (String txt : l) {
                if (txt.toLowerCase().contains(searchText.toLowerCase())) {
                    filteredList.add(l);
                }
            }
        }
        return filteredList;
    }

    public void addData(String requestNum) {
        dBconnection.openDB();
        dBconnection.query("select request_number from request r WHERE r.request_id = " + requestNum);
        try {
            while (dBconnection.getRs().next()) {
                requestNumber = dBconnection.getRs().getString(1);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch (Exception e) {
            errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        this.requestNum = requestNum;
        lblRequest.setText("Номер заявки: " + requestNumber);
        taskView.getSourceItems().setAll(dataSource("SELECT t.task_number, t.task_name FROM public.task t " +
                "join public.project p on p.project_id = t.project_id " +
                "join public.contract_project cp on cp.project_id = p.project_id " +
                "join public.contract c on c.contract_id = cp.contract_id " +
                "join public.request r on r.contract_id = c.contract_id " +
                "WHERE r.request_id = '" + requestNum + "' AND t.request_id IS NULL"));

        taskView.getTargetItems().setAll(dataTarget("SELECT task_number, task_name FROM public.task " +
                "WHERE request_id = '" + requestNum + "'"));
    }

    public void saveData(ActionEvent actionEvent) {
        dBconnection.openDB();
        try {
            if (taskView.getTargetItems().size() != 0) {
                for (Object taskTarget : taskView.getTargetItems()) {

                    dBconnection.getStmt().executeUpdate("UPDATE public.task SET request_id = '" + requestNum + "' " +
                            "WHERE task_number = '" + taskTarget.toString().substring(1, taskTarget.toString().indexOf(",")) + "'");
                }
            }

            dBconnection.getC().commit();

            if (taskView.getSourceItems().size() != 0) {
                for (Object taskSource : taskView.getSourceItems()) {

                    dBconnection.getStmt().executeUpdate("UPDATE public.task SET request_id = default " +
                            "WHERE task_number = '" + taskSource.toString().substring(1, taskSource.toString().indexOf(",")) + "'");
                }
            }

            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
        actionClose(actionEvent);
    }

    private ObservableList dataSource(String k) {
        return data("source", k);
    }

    private ObservableList dataTarget(String k) {
        return data("target", k);
    }

    private ObservableList data(String type, String k) {
        if (type.equals("target")) {
            dataSelectTarget.clear();
        }
        if (type.equals("source")) {
            dataSelectSource.clear();
        }
        try {
            dBconnection.openDB();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                if (type.equals("target")) {
                    dataSelectTarget.add(row);
                }
                if (type.equals("source")) {
                    dataSelectSource.add(row);
                }
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch (Exception e) {
            errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        if (type.equals("target")) {
            return dataSelectTarget;
        }
        if (type.equals("source")) {
            return dataSelectSource;
        }
        return null;
    }

    public void formClear() {
        dataSelectSource.clear();
        dataSelectTarget.clear();
        fldSearch.clear();
        requestNum = null;
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
        formClear();
    }
}
