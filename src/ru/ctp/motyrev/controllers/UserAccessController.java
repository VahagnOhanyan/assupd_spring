package ru.ctp.motyrev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.control.ListSelectionView;
import ru.ctp.motyrev.code.DBconnection;

import java.sql.SQLException;

public class UserAccessController {

    @FXML
    private Button btnSave;
    @FXML
    private ListSelectionView accessView;
    @FXML
    private Label lblUser;

    String userNum = null;

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {

        accessView.setSourceHeader(new Label("Сотрудники"));
        accessView.setTargetHeader(new Label("В подчинении"));

    }

    public void addData(String userNum) {
        this.userNum = userNum;
        lblUser.setText("Табельный номер: " + userNum);
        accessView.getSourceItems().setAll(data("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                "WHERE u.user_id NOT IN (SELECT us.user_sub_id FROM public.user_subordination us " +
                "join public.user ur on ur.user_id = us.user_id " +
                "WHERE ur.user_id_number = '"+userNum+"') AND u.user_id_number != '"+userNum+"' AND u.user_fullname != 'super_user' " +
                "ORDER BY u.user_fullname"));

        accessView.getTargetItems().setAll(data("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                "WHERE u.user_id IN (SELECT us.user_sub_id FROM public.user_subordination us " +
                "join public.user ur on ur.user_id = us.user_id " +
                "WHERE ur.user_id_number = '"+userNum+"') AND u.user_id_number != '"+userNum+"' " +
                "ORDER BY u.user_fullname"));
    }

    public void saveData(ActionEvent actionEvent) {
        dBconnection.openDB();
        try {
            dBconnection.getStmt().executeUpdate("DELETE FROM public.user_subordination " +
                    "WHERE user_id = (SELECT user_id FROM public.user WHERE user_id_number = '"+userNum+"')");

            if (accessView.getTargetItems().size() != 0) {
                for (Object access : accessView.getTargetItems()) {

                    dBconnection.getStmt().executeUpdate("INSERT INTO public.user_subordination (user_id, user_sub_id) VALUES " +
                            "((SELECT user_id FROM public.user WHERE user_id_number = '" + userNum + "'), (SELECT user_id FROM public.user WHERE user_id_number = '" + access.toString().substring(1, access.toString().indexOf(",")) + "'))");
                }
            }

            dBconnection.getStmt().executeUpdate("INSERT INTO public.user_subordination (user_id, user_sub_id) VALUES " +
                    "((SELECT user_id FROM public.user WHERE user_id_number = '" + userNum + "'), (SELECT user_id FROM public.user WHERE user_id_number = '" + userNum + "'))");

            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void formClear() {
        data.clear();
        dataSelect.clear();
        userNum = null;

    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
        formClear();
    }

}
