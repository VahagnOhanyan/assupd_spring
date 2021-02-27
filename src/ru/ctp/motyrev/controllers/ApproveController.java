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

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

public class ApproveController {

        @FXML
        private Label tabLbl;
        @FXML
        private TableView approveView;

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

        public void addData(String user, String month, String year) {

            formClear();

            tabLbl.setTextFill(Color.BLUE);
            tabLbl.setText("Табель: " + user + " за " + month + "." + year);




            sql = "SELECT ah.edit_user, ur.user_role_name, ah.decision, ah.approver_role, to_char(ah.approve_date, 'DD-MM-YYYY HH24:MI:SS') FROM public.approve_history ah " +
                    "join public.current_approve ca on ca.current_approve_id = ah.current_approve_id " +
                    "join public.user_role ur on ur.user_role_id = ah.user_role_id " +
                    "WHERE sheet_month = '" + month + "' AND sheet_year = '" + year + "' AND " +
                    "user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "') " +
                    "ORDER by ah.approve_date";

            tableGenerator(sql);
        }

        private void tableGenerator(String sql) {
            approveView.getSelectionModel().clearSelection();
            approveView.getColumns().clear();
            approveView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
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

                    approveView.getColumns().addAll(tableColumn);


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

                approveView.setItems(data);

                approveView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        private void generateColName (String bdColName) {
            switch (bdColName) {
                case "edit_user":
                    tableColName = "Пользователь";
                    break;
                case "user_role_name":
                    tableColName = "С ролью";
                    break;
                case "decision":
                    tableColName = "Решение";
                    break;
                case "approver_role":
                    tableColName = "За";
                    break;
                case "to_char":
                    tableColName = "Дата";
                    break;
            }
        }

        private void formClear() {
            tabLbl.setText("");
            approveView.getItems().clear();
            approveView.getColumns().clear();
            data.clear();
            sql = "";
        }

        public void actionClose(ActionEvent actionEvent) {
            Node source = (Node) actionEvent.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.hide();
        }
    }