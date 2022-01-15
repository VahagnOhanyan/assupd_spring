package ru.ctp.motyrev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.ListSelectionView;
import ru.ctp.motyrev.code.DBconnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExecutorController {

    @FXML
    private Button btnSave;
    @FXML
    private ListSelectionView executorView;
    @FXML
    private Label lblTask;
    @FXML
    private TextField fldSearch;
    
    String taskNum = null;

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList<String>> dataSelectSource = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> dataSelectTarget = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {
        executorView.setSourceHeader(new Label("Сотрудники"));
        executorView.setTargetHeader(new Label("Исполнители"));
        fldSearch.textProperty().addListener((observable, oldValue, newValue) ->
                executorView.getSourceItems().setAll(filterList(dataSelectSource, newValue))
        );
    }
    private ObservableList<ObservableList<String>> filterList(ObservableList<ObservableList<String>> list, String searchText){
        ObservableList<ObservableList<String>> filteredList = FXCollections.observableArrayList();
        for (ObservableList<String> l : list){
            for (String txt : l) {
                if (txt.contains(searchText))
                    filteredList.add(l);

            }
        }
        return filteredList;
    }
    public void addData(String taskNum) {
        this.taskNum = taskNum;
        lblTask.setText("Задача: " + taskNum);
        executorView.getSourceItems().setAll(dataSource("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                "WHERE u.user_id NOT IN (SELECT ur.user_id FROM public.user ur " +
                "join public.task_executor te on te.user_id = ur.user_id " +
                "join public.task t on t.task_id = te.task_id " +
                "WHERE t.task_number = '"+taskNum+"') AND u.user_fullname != 'super_user'" +
                "ORDER BY u.user_fullname"));

        executorView.getTargetItems().setAll(dataTarget("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                "WHERE u.user_id IN (SELECT ur.user_id FROM public.user ur " +
                "join public.task_executor te on te.user_id = ur.user_id " +
                "join public.task t on t.task_id = te.task_id " +
                "WHERE t.task_number = '"+taskNum+"') AND u.user_fullname != 'super_user'" +
                "ORDER BY u.user_fullname"));
    }

    public void saveData(ActionEvent actionEvent) {
        dBconnection.openDB();
        try {
            dBconnection.getStmt().executeUpdate("DELETE FROM public.task_executor " +
                    "WHERE task_id = (SELECT task_id FROM public.task WHERE task_number = '"+taskNum+"')");

            if (executorView.getTargetItems().size() != 0) {
                for (Object executor : executorView.getTargetItems()) {

                    dBconnection.getStmt().executeUpdate("INSERT INTO public.task_executor (task_id, user_id) VALUES " +
                            "((SELECT task_id FROM public.task WHERE task_number = '" + taskNum + "'), (SELECT user_id FROM public.user WHERE user_id_number = '" + executor.toString().substring(1, executor.toString().indexOf(",")) + "'))");
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
        return  data("source",k);
    }
    private ObservableList dataTarget(String k) {
        return  data("target",k);
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
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
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
        } catch ( Exception e ) {
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
        data.clear();
        dataSelectSource.clear();
        dataSelectTarget.clear();
        fldSearch.clear();
        taskNum = null;

    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
        formClear();
    }

}
