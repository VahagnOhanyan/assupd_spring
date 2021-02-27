package ru.ctp.motyrev.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.ctp.motyrev.code.DBconnection;

import java.sql.*;
import java.util.Optional;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

public class SheetController {
    @FXML
    private Label lblSn;
    @FXML
    private TableView worksView;
    @FXML
    private Button btnApprove;
    @FXML
    private Button btnDecline;
    @FXML
    private Button btnValidate;

    private String tableColName = null;
    private String exemp = null;
    private String id = null;

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();;

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {
        worksView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!worksView.getSelectionModel().isEmpty()) {
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i) == worksView.getSelectionModel().getSelectedItem()) {
                            exemp = (String) data.get(i).get(0);
                        }
                    }
            }
        });
    }

    public void addData(String id) {
        formClear();
        this.id = id;

        lblSn.setTextFill(Color.BLUE);
        lblSn.setText("Лист " + data("SELECT sheet_name FROM public.sheet WHERE sheet_id = '" + id + "'").toString().replace("[","").replace("]",""));

        generateWorksList();
    }

    public void setData(String id) {
        formClear();

        btnApprove.setDisable(true);
        btnDecline.setDisable(true);
        btnValidate.setDisable(true);

        this.id = id;

        lblSn.setTextFill(Color.BLUE);
        lblSn.setText("Лист " + data("SELECT sheet_name FROM public.sheet WHERE sheet_id = '" + id + "'").toString().replace("[","").replace("]",""));

        generateWorksList();
    }

    private void generateWorksList() {
        data.clear();
        worksView.getSelectionModel().clearSelection();
        worksView.getItems().clear();
        worksView.getColumns().clear();
        worksView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
        try {
            dBconnection.openDB();
            dBconnection.query("SELECT ps.parsing_sheet_id, ps.user_id_number, u.user_fullname, ps.year, ps.month, ps.customer, ps.work_number, ps.work_name, ps.stage, ps.intensity, ps.overtime, ps.note, s.status_name " +
                    "FROM public.parsing_sheet ps " +
                    "left join public.user u ON u.user_id_number = ps.user_id_number " +
                    "join public.status s on s.status_id = ps.status_id " +
                    "join public.sheet sh on sh.sheet_id = ps.sheet_id " +
                    "WHERE sh.sheet_id = '"+ id +"' " +
                    "ORDER BY ps.user_id_number");
            for (int i = 0; i < dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                final int j = i;
                tableColName = dBconnection.getRs().getMetaData().getColumnName(i + 1);
                generateColName(dBconnection.getRs().getMetaData().getColumnName(i + 1));
                TableColumn tableColumn = new TableColumn(tableColName);
                tableColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty((String) param.getValue().get(j)));
                if (tableColumn.getText().equals("work_number") || tableColumn.getText().equals("intensity") || tableColumn.getText().equals("overtime") || tableColumn.getText().equals("stage")
                        || tableColumn.getText().equals("customer")) {
                    tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                } else {
                    tableColumn.setCellFactory(param -> {
                        TableCell<ObservableValue, String> cell = new TableCell<>();
                        Text text = new Text();
                        cell.setGraphic(text);
                        cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                        text.textProperty().bind(cell.itemProperty());
                        text.wrappingWidthProperty().bind(tableColumn.widthProperty());
                        return cell;

                    });
                }
                /*tableColumn.setOnEditCommit(((EventHandler<TableColumn.CellEditEvent<ObservableList, String>>) event -> {
                    editCell(event);
                }));*/

                worksView.setRowFactory(row -> new TableRow<ObservableList>(){
                    private final Text newText;

                    {
                        newText = new Text();
                        newText.wrappingWidthProperty().bind(tableColumn.widthProperty());
                    }

                    @Override
                    public void updateItem(ObservableList item, boolean empty){
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setStyle("");
                        } else {
                            if (item.toString().contains("ОШИБКА") && !item.toString().contains("declined")) {
                                setStyle("-fx-background-color: red;");
                            } else if (item.toString().contains("declined")) {
                                setStyle("-fx-background-color: lightgray;");
                            } else if ((item.toString().contains("invalidated") || item.toString().contains("incorrect")) & !item.toString().contains("ОШИБКА")) {
                                setStyle("-fx-background-color: moccasin;");
                            } else if (item.toString().contains("validated") || item.toString().contains("correct")) {
                                setStyle("-fx-background-color: lightblue;");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                    @Override
                    public void updateSelected(boolean selected) {
                        super.updateSelected(selected);
                        try {
                            if (selected) {
                                setStyle("-fx-background-color: STEELBLUE;");
                            } else {
                                if (getItem().toString().contains("ОШИБКА") && !getItem().toString().contains("declined")) {
                                    setStyle("-fx-background-color: red;");
                                } else if (getItem().toString().contains("declined")) {
                                    setStyle("-fx-background-color: lightgray;");
                                } else if ((getItem().toString().contains("invalidated") || getItem().toString().contains("incorrect")) & !getItem().toString().contains("ОШИБКА")) {
                                    setStyle("-fx-background-color: moccasin;");
                                } else if (getItem().toString().contains("validated") || getItem().toString().contains("correct")) {
                                    setStyle("-fx-background-color: lightblue;");
                                } else {
                                    setStyle("");
                                }
                            }
                        }catch (NullPointerException nle) {

                        }
                    }
                });

                worksView.getColumns().addAll(tableColumn);


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
            worksView.setItems(data);
            worksView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
        } catch ( Exception e ) {
            errorAlert.setTitle("Ошибка");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
    }

    private void generateColName (String bdColName) {
        switch (bdColName) {
            case "computer_id":
                tableColName = "Идентификатор";
                break;
        }
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

    public void massDataApproval (String id) {
        this.id = id;
        data.clear();

        try {
            dBconnection.openDB();
            dBconnection.query("SELECT ps.parsing_sheet_id, ps.user_id_number, u.user_fullname, ps.year, ps.month, ps.customer, ps.work_number, ps.work_name, ps.stage, ps.intensity, ps.overtime, ps.note, s.status_name " +
                    "FROM public.parsing_sheet ps " +
                    "left join public.user u ON u.user_id_number = ps.user_id_number " +
                    "join public.status s on s.status_id = ps.status_id " +
                    "join public.sheet sh on sh.sheet_id = ps.sheet_id " +
                    "WHERE sh.sheet_id = '"+ id +"' " +
                    "ORDER BY ps.user_id_number");

            while(dBconnection.getRs().next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    row.add(dBconnection.getRs().getString(i));
                }
                data.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();

        } catch ( Exception e ) {
            errorAlert.setTitle("Ошибка");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
    }

    public void actionSave(ActionEvent actionEvent) {
        boolean validSheet = true;
        String workName;
        double monthly_sum;

        dBconnection.openDB();
        try {
            for (ObservableList work : data) {
                if (!work.get(12).toString().replace("[","").replace("]","").equals("validated")) {
                    validSheet = false;
                }
            }

            if (validSheet) {
                for (ObservableList work : data) {
                   workName = work.get(6).toString().replace("[","").replace("]","");
                   monthly_sum = Double.parseDouble(work.get(9).toString().replace("[","").replace("]","")) + Double.parseDouble(work.get(10).toString().replace("[","").replace("]",""));


                   dBconnection.getStmt().executeUpdate("INSERT INTO public.monthly_work (user_id, year, month, monthly_intensity, monthly_overtime, monthly_sum, note) VALUES " +
                            "((SELECT user_id FROM public.user WHERE user_id_number = '"+work.get(1).toString().replace("[","").replace("]","")+"'), " +
                            "'"+work.get(3).toString().replace("[","").replace("]","")+"', " +
                            "'"+work.get(4).toString().replace("[","").replace("]","")+"', " +
                            "'"+work.get(9).toString().replace("[","").replace("]","")+"', " +
                            "'"+work.get(10).toString().replace("[","").replace("]","")+"', " +
                            "'"+monthly_sum+"', " +
                            "'"+work.get(11).toString().replace("[","").replace("]","")+"')");

                   if (workName.equalsIgnoreCase("Админ. деятельность") || workName.equalsIgnoreCase("Отпуск")
                           || workName.equalsIgnoreCase("Больничный")
                           || workName.equalsIgnoreCase("Обучение") || workName.equalsIgnoreCase("IDLE")) {

                       dBconnection.getStmt().executeUpdate("INSERT INTO public.user_stage_monthly (user_stage_id, monthly_work_id) WITH " +
                               "t1 AS (SELECT us.user_stage_id FROM public.user_stage us " +
                               "join public.user u on u.user_id = us.user_id " +
                               "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                               "WHERE user_stage_type_name = '"+workName+"' " +
                               "AND user_id_number = '"+work.get(1).toString().replace("[","").replace("]","")+"'), " +
                               "t2 AS (SELECT max(monthly_work_id) FROM public.monthly_work) " +
                               "SELECT t1.user_stage_id, t2.max " +
                               "FROM t1, t2");
                   } else {
                       dBconnection.getStmt().executeUpdate("INSERT INTO public.stage_monthly (stage_id, monthly_work_id) WITH " +
                               "t1 AS (SELECT s.stage_id FROM public.stage s " +
                               "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                               "join public.task t on t.task_id = s.task_id " +
                               "WHERE stage_type_name = '"+work.get(8).toString().replace("[","").replace("]","").toLowerCase()+"' " +
                               "AND task_number = '"+workName+"'), " +
                               "t2 AS (SELECT max(monthly_work_id) FROM public.monthly_work) " +
                               "SELECT t1.stage_id, t2.max " +
                               "FROM t1, t2");
                   }
                   dBconnection.getStmt().executeUpdate("UPDATE public.parsing_sheet SET status_id = " +
                            "(SELECT status_id FROM public.status s " +
                            "join public.status_type st on status_type_id = s.status_type " +
                            "WHERE st.status_type_name = 'incoming_works' AND s.status_name = 'approved') " +
                            "WHERE sheet_id = '" + id + "'");
                }
                dBconnection.getStmt().executeUpdate("UPDATE public.sheet SET status_id = " +
                        "(SELECT status_id FROM public.status s " +
                        "join public.status_type st on status_type_id = s.status_type " +
                        "WHERE st.status_type_name = 'incoming_sheets' AND s.status_name = 'approved') " +
                        "WHERE sheet_id = '" + id + "'");
                dBconnection.getC().commit();
            } else {
                infoAlert.setTitle("Ошибка");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Не все задачи в листе валидны");
                infoAlert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
        generateWorksList();
        worksView.refresh();
    }

    public void actionDelete (ActionEvent actionEvent) {

    }

    public void actionDecline (ActionEvent actionEvent) {
        int count;
        dBconnection.openDB();
        try {
            for (ObservableList work : data) {
                if (!work.toString().contains("declined")) {
                    dBconnection.getStmt().executeUpdate("UPDATE public.parsing_sheet SET status_id = " +
                            "(SELECT status_id FROM public.status s " +
                            "join public.status_type st on status_type_id = s.status_type " +
                            "WHERE st.status_type_name = 'incoming_works' AND s.status_name = 'declined') " +
                            "WHERE parsing_sheet_id = '" + work.get(0) + "'");
                }
            }
            count = data.size();
            /*if (worksView.getSelectionModel().isEmpty()) {
                    for (ObservableList work : data) {
                        if (!work.toString().contains("declined")) {
                            dBconnection.getStmt().executeUpdate("UPDATE public.parsing_sheet SET status_id = " +
                                    "(SELECT status_id FROM public.status s " +
                                    "join public.status_type st on status_type_id = s.status_type " +
                                    "WHERE st.status_type_name = 'incoming_works' AND s.status_name = 'declined') " +
                                    "WHERE parsing_sheet_id = '" + work.get(0) + "'");
                        }
                    }
                count = data.size();
            } else {
                for (ObservableList work : data) {
                    if (work.toString().contains("declined")) {
                        count += 1;
                    }
                }
                dBconnection.getStmt().executeUpdate("UPDATE public.parsing_sheet SET status_id = " +
                        "(SELECT status_id FROM public.status s " +
                        "join public.status_type st on status_type_id = s.status_type " +
                        "WHERE st.status_type_name = 'incoming_works' AND s.status_name = 'declined') " +
                        "WHERE parsing_sheet_id = '" + exemp + "'");
            }*/
            dBconnection.getStmt().executeUpdate("UPDATE public.sheet SET status_id = " +
                    "(SELECT status_id FROM public.status s " +
                    "join public.status_type st on status_type_id = s.status_type " +
                    "WHERE st.status_type_name = 'incoming_sheets' AND s.status_name = 'declined') " +
                    "WHERE sheet_id = '" + id + "' AND sheet_works_count = '"+ count +"'");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
            dBconnection.closeDB();
            return;
        }
        generateWorksList();
        worksView.refresh();
        dBconnection.closeDB();
    }

    private void formClear() {
        data.clear();
        dataSelect.clear();
        lblSn.setText("");
        worksView.getItems().clear();
        id = null;
        tableColName = null;
        exemp = null;
        btnApprove.setDisable(false);
        btnDecline.setDisable(false);
        btnValidate.setDisable(false);

    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }
}
