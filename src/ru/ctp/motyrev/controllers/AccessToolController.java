package ru.ctp.motyrev.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.ctp.motyrev.code.DBconnection;
import ru.ctp.motyrev.code.Validator;

import java.io.File;
import java.sql.SQLException;

import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

public class AccessToolController {

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> accessData = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    private String prevNode = "";
    private String sql = "";
    private String tableColName;
    private String rightNumber = "";


    private String root;

    @FXML
    private TableView rightsView;
    @FXML
    private TreeView<String> roleView;

    DBconnection dBconnection = new DBconnection();

    @FXML
    private void initialize() {
        data("SELECT user_role_name FROM public.user_role WHERE user_role_name != 'Super_user'");

        initListeners();
        fillTreeView();
    }

    private void initListeners() {

        roleView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            tableShowContent(newValue.getValue());
        });
    }

    private void fillTreeView() {
        try {
            Image rootNodeImage = new Image("/ru/ctp/motyrev/images/role_user.png");
            Image nodeImage = new Image("/ru/ctp/motyrev/images/roleIcon.png");

            TreeItem<String> rootItem = new TreeItem<String>("Роли", new ImageView(rootNodeImage));
            rootItem.setExpanded(true);

            for ( int i = 0; i < dataSelect.size(); i++) {
                TreeItem<String> ti = new TreeItem<String>(dataSelect.get(i).toString().replace("[","").replace("]",""), new ImageView(nodeImage));
                rootItem.getChildren().add(ti);
            }

            roleView.setRoot(rootItem);

            // Выделяем корневой узел на старте
            roleView.getSelectionModel().select(roleView.getRow(rootItem));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tableShowContent(String nodeName) {

        if (nodeName != prevNode) {

            if (!nodeName.equals("Роли")) {
                prevNode = nodeName;

                sql = "SELECT ra.role_access_id, m.module_name, at.access_type_name FROM public.role_access ra " +
                        "join public.user_role ur on ur.user_role_id = ra.user_role_id " +
                        "join public.module m on m.module_id = ra.module_id " +
                        "join public.access_type at on at.access_type_id = ra.access_type_id " +
                        "WHERE ur.user_role_name = '"+ nodeName +"' " +
                        "ORDER BY m.module_id";

                rightsView.getItems().clear();
                tableGenerator(sql);
            }
        }
    }

    private void tableGenerator(String sql) {

        rightsView.setEditable(true);
        rightsView.getSelectionModel().clearSelection();
        rightsView.getColumns().clear();
        rightsView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
        data.clear();
        accessData("SELECT access_type_name FROM public.access_type ORDER BY access_type_id");
        dBconnection.openDB();
        dBconnection.query(sql);

        try {
            for (int i = 0; i < dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                final int k=i;
                tableColName = dBconnection.getRs().getMetaData().getColumnName(i + 1);
                generateColName(dBconnection.getRs().getMetaData().getColumnName(i + 1));
                TableColumn tableColumn = new TableColumn(tableColName);


                if ((i+1)%3==0) {
                    tableColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, Object>, ObservableValue<Object>>) param -> new SimpleObjectProperty(param.getValue().get(k)));
                    tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(accessData));
                    tableColumn.setOnEditCommit(((EventHandler<TableColumn.CellEditEvent<ObservableList, Object>>) event -> {

                        for (int j = 0; j <= data.size() - 1; j++) {
                            if (data.get(j) == rightsView.getSelectionModel().getSelectedItem()) {

                                dBconnection.openDB();
                                try {
                                    dBconnection.getStmt().execute("UPDATE public.role_access SET access_type_id = " +
                                            "(SELECT access_type_id FROM public.access_type WHERE access_type_name = '"+ event.getNewValue().toString().replace("[","").replace("]","") +"') " +
                                            "WHERE role_access_id = '"+ data.get(j).get(0) +"'");
                                    dBconnection.getC().commit();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                dBconnection.closeDB();
                                break;
                            }
                        }
                    }));
                } else {
                    tableColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty((String) param.getValue().get(k)));
                }
                tableColumn.setStyle( "-fx-alignment: CENTER;");

                rightsView.getColumns().addAll(tableColumn);


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
            rightsView.setItems(data);
            /*rightsView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);*/
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void generateColName (String bdColName) {
        switch (bdColName) {
            case "role_access_id":
                tableColName = "ИД";
                break;
            case "module_name":
                tableColName = "Функциональность";
                break;
            case "access_type_name":
                tableColName = "Тип доступа";
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

    private ObservableList accessData(String k) {
        try {
            dBconnection.openDB();
            accessData.clear();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                accessData.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch ( Exception e ) {
            errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return accessData;
    }

}
