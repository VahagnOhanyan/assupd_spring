package ru.ctp.motyrev.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.ctp.motyrev.code.DBconnection;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

public class TaskPaneController {

    @FXML
    private TableView taskView;

    public static String workNum;
    private String tableColName;
    private String sql;

    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {

        taskView.setOnMouseClicked(event -> {
            Object src = event.getSource();
            if (!(src instanceof TableView))  {
                return;
            }

            if (event.getClickCount() == 2) {
                for (int i = 0; i <= data.size() - 1; i++) {
                    if (data.get(i) == taskView.getSelectionModel().getSelectedItem()) {
                        workNum = "[" + (String) data.get(i).get(0) + "]";
                        actionClose(event);
                    }
                }
            }
        });

    }

    public void addData(String user) {

        formClear();

        tableGenerator("SELECT task_number, task_name FROM public.task t " +
                "join public.task_executor te on te.task_id = t.task_id " +
                "join public.user u on u.user_id = te.user_id " +
                "join public.status s on s.status_id = t.status_id " +
                "WHERE u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "' AND s.status_name != 'утверждено' " +
                "ORDER BY task_number");
    }

    private void tableGenerator(String sql) {
        taskView.getSelectionModel().clearSelection();
        taskView.getColumns().clear();
        taskView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
        data.clear();
        dBconnection.openDB();
        dBconnection.query(sql);

        try {
            for (int i = 0; i < dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                final int j = i;
                tableColName = dBconnection.getRs().getMetaData().getColumnName(i + 1);
                generateColName(dBconnection.getRs().getMetaData().getColumnName(i + 1));
                TableColumn tableColumn = new TableColumn(tableColName);

                tableColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty((String) param.getValue().get(j)));

                tableColumn.setCellFactory(new Callback<TableColumn, TableCell>() {

                    public TableCell call(TableColumn param) {

                        return new TableCell<ObservableList, String>() {

                            private final Text newText;

                            {
                                newText = new Text();
                                newText.wrappingWidthProperty().bind(tableColumn.widthProperty());
                            }

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);

                                setPrefHeight(Control.USE_COMPUTED_SIZE);

                                if (empty || item == null) {
                                    newText.setText("");
                                    setGraphic(newText);
                                    setStyle("");
                                } else {
                                    newText.setText(item);
                                    setGraphic(newText);
                                }
                            }
                        };
                    }
                });

                taskView.getColumns().addAll(tableColumn);


            }
            //наполнение observableList данными из базы
            while(dBconnection.getRs().next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    row.add(dBconnection.getRs().getString(i));
                }
                data.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
            //Добавление данных в TableView

            taskView.setItems(data);

            taskView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void generateColName (String bdColName) {
        switch (bdColName) {
            case "task_number":
                tableColName = "Номер задачи";
                break;
            case "task_name":
                tableColName = "Наименование задачи";
                break;
        }
    }

    private void formClear() {
        taskView.getColumns().clear();
        data.clear();
        sql = "";
    }

    public void actionClose(MouseEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }
}