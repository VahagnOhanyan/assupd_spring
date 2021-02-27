package ru.ctp.motyrev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.ctp.motyrev.code.DBconnection;

import java.sql.SQLException;
import java.util.Optional;

public class SiteController {

    @FXML
    private TextField siteName;
    @FXML
    private TextField shortSiteName;
    @FXML
    private Button addBtn;

    private String exemp;

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();

    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {

        siteName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                addBtn.setDisable(true);
                shortSiteName.clear();
                shortSiteName.setDisable(true);
            } else {
                shortSiteName.setDisable(false);
            }
        });

        shortSiteName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                addBtn.setDisable(true);
            } else {
                addBtn.setDisable(false);
            }
        });
    }

    public void createData() {
        formClear();
    }

    public void setData (String exemp) {
        this.exemp = exemp;

        data("SELECT site_name, site_code FROM public.site WHERE site_id = '" + exemp + "'");

        addBtn.setText("Изменить");
        siteName.setText(dataSelect.get(0).get(0).toString());
        if (dataSelect.get(0).get(1) != null) {
            shortSiteName.setText(dataSelect.get(0).get(1).toString());
        } else {
            shortSiteName.setText("");
        }
    }

    public void saveData(ActionEvent actionEvent) {
        dBconnection.openDB();
        try {
            if (addBtn.getText().equals("Добавить")) {
                    dBconnection.getStmt().executeUpdate("INSERT INTO public.site (site_name, site_code) VALUES ('" + siteName.getText() + "', '" + shortSiteName.getText() + "')");
            } else {

                confirmAlert.setTitle("Подтвердите выбор");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Вы действительно хотите изменить наименование площадки? Это приведет к изменениям во всех связанных структурах системы.");

                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK){
                    dBconnection.getStmt().executeUpdate("UPDATE public.site SET site_name = '" + siteName.getText() + "' " +
                            "WHERE site_id = '" + exemp + "'");
                    dBconnection.getStmt().executeUpdate("UPDATE public.site SET site_code = '" + shortSiteName.getText() + "' " +
                            "WHERE site_id = '" + exemp + "'");
                    addBtn.setText("Добавить");
                } else {
                    dBconnection.closeDB();
                    return;
                }

            }
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
            errorAlert.setTitle("Ошибка");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("При внесении данных о площадке произошла ошибка, данные уже есть в базе");
            errorAlert.showAndWait();
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

    private void formClear() {
        siteName.clear();
        addBtn.setDisable(true);
        shortSiteName.clear();
        shortSiteName.setDisable(true);
        addBtn.setText("Добавить");
        exemp = "";

    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }
}
