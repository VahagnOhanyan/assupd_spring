package ru.ctp.motyrev.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.ctp.motyrev.code.DBconnection;
import ru.ctp.motyrev.objects.TimeSheet;


import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

public class CommentsController {

        @FXML
        private Label lblTask;
        @FXML
        private Label lblStage;
        @FXML
        private TableView commentsView;

        private String tableColName;
        private String sql;

        private ObservableList<ObservableList> data = FXCollections.observableArrayList();

        private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

        DBconnection dBconnection = new DBconnection();

        @FXML
        private void initialize() {



        }

        public void addData(String task, String stage, String user) {

            formClear();

            lblTask.setTextFill(Color.BLUE);
            lblTask.setText("Задача: " + task);
            lblStage.setTextFill(Color.BLUE);
            lblStage.setText("Этап: " + stage);


            if (!task.equals("Не проектная")) {
                sql = "SELECT to_char(sn.stage_note_date, 'DD-MM-YYYY HH24:MI:SS'), sn.stage_note_text, u.user_fullname FROM public.stage_note sn " +
                        "left join public.user u on u.user_id = sn.user_id " +
                        "WHERE stage_id = (SELECT stage_id FROM public.stage s " +
                        "join public.task t on t.task_id = s.task_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "WHERE t.task_number = '" + task + "' AND st.stage_type_name = '" + stage + "') AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'";
            } else {
                sql = "SELECT to_char(sn.user_stage_note_date, 'DD-MM-YYYY HH24:MI:SS'), sn.user_stage_note_text, u.user_fullname FROM public.user_stage_note sn " +
                        "left join public.user u on u.user_id = sn.user_id " +
                        "WHERE user_stage_id = (SELECT user_stage_id FROM public.user_stage s " +
                        "join public.user ur on ur.user_id = s.user_id " +
                        "join public.user_stage_type st on st.user_stage_type_id = s.user_stage_type_id " +
                        "WHERE ur.user_id_number = '" + user.substring(1, user.indexOf(",")) + "' AND st.user_stage_type_name = '" + stage + "')";
            }
            tableGenerator(sql);
        }

    private void tableGenerator(String sql) {
        commentsView.getSelectionModel().clearSelection();
        commentsView.getColumns().clear();
        commentsView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
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

                commentsView.getColumns().addAll(tableColumn);


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

            commentsView.setItems(data);

            commentsView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void generateColName (String bdColName) {
        switch (bdColName) {
            case "to_char":
                tableColName = "Дата";
                break;
            case "stage_note_text":
                tableColName = "Комментарий";
                break;
            case "user_fullname":
                tableColName = "Пользователь";
                break;
            case "user_stage_note_text":
                tableColName = "Комментарий";
                break;
        }
    }

        private void formClear() {
            commentsView.getColumns().clear();
            data.clear();
            sql = "";
        }

        public void actionClose(ActionEvent actionEvent) {
            Node source = (Node) actionEvent.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.hide();
        }
    }