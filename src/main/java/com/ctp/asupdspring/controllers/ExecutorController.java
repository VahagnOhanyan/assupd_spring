package com.ctp.asupdspring.controllers;

import com.ctp.asupdspring.app.repo.TaskExecutorRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.ListSelectionView;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@FxmlView("taskExecutor.fxml")
public class ExecutorController {
    @FXML
    public AnchorPane anchorPane;
    @FXML
    private Button btnSave;
    @FXML
    private ListSelectionView executorView;
    @FXML
    private Label lblTask;
    @FXML
    private TextField fldSearch;
    private final TaskExecutorRepository taskExecutorRepository;
    String taskNum = null;
    private Stage stage;


    private ObservableList<ObservableList<String>> dataSelectSource = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> dataSelectTarget = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        stage = new Stage();
        stage.setTitle("Исполнители");
        stage.setScene(new Scene(anchorPane));
        stage.setMinHeight(400);
        stage.setMinWidth(800);
        stage.initModality(Modality.WINDOW_MODAL);

        executorView.setSourceHeader(new Label("Сотрудники"));
        executorView.setTargetHeader(new Label("Исполнители"));
        fldSearch.textProperty().addListener((observable, oldValue, newValue) ->
                executorView.getSourceItems().setAll(filterList(dataSelectSource, newValue))

        );
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

    public void addData(String taskNum) {
        this.taskNum = taskNum;
        lblTask.setText("Задача: " + taskNum);
        executorView.getSourceItems().setAll(dataSource(taskExecutorRepository.getNoTaskExecutors(taskNum)));

        executorView.getTargetItems().setAll(dataTarget(taskExecutorRepository.getTaskExecutors(taskNum)));
    }

    public void saveData(ActionEvent actionEvent) {


            taskExecutorRepository.deleteTaskExecutors(taskNum);

            if (executorView.getTargetItems().size() != 0) {
                for (Object executor : executorView.getTargetItems()) {
                    String userIdNumber = executor.toString().substring(1, executor.toString().indexOf(","));
                    taskExecutorRepository.addTaskExecutor(taskNum, userIdNumber);

                }
            }

        actionClose(actionEvent);
    }

    private ObservableList dataSource(List<Object[]> k) {
        return data("source", k);
    }

    private ObservableList dataTarget(List<Object[]> k) {
        return data("target", k);
    }

    private ObservableList data(String type, List<Object[]> k) {
        if (type.equals("target")) {
            dataSelectTarget.clear();
        }
        if (type.equals("source")) {
            dataSelectSource.clear();
        }
        try {
            for (Object[] o : k) {

                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 0; i < o.length; i++) {
                    //Перебор колонок
                    row.add(String.valueOf(o[0]));
                }
                if (type.equals("target")) {
                    dataSelectTarget.add(row);
                }
                if (type.equals("source")) {
                    dataSelectSource.add(row);
                }
            }
        } catch (Exception e) {
           /* errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();*/
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
