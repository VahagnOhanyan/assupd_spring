/*
 * Copyright (c) 2008-2022
 * LANIT
 * All rights reserved.
 *
 * This product and related documentation are protected by copyright and
 * distributed under licenses restricting its use, copying, distribution, and
 * decompilation. No part of this product or related documentation may be
 * reproduced in any form by any means without prior written authorization of
 * LANIT and its licensors, if any.
 *
 * $
 */
package ru.ctp.motyrev.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.ctp.motyrev.code.CSVLoader;
import ru.ctp.motyrev.code.DBconnection;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ImportOneCReportController {
    @FXML
    public Label alreadyImported;
    @FXML
    public Label noReportImported;
    @FXML
    private Button upload1C;
    @FXML
    private Button openExportToExcelWindow;

    private Stage exportToExcelStage;
    private ExportToExcelController exportToExcelController;
    private final FXMLLoader fxmlExportToExcelViewLoader = new FXMLLoader();

    private Parent fxmlExportToExcelView;
    DBconnection dBconnection = new DBconnection();
    File csvFile = null;

    @FXML
    private void initialize() {
        try {
            initLoader();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dBconnection.openDB();
        dBconnection.query("select monthyear from onec_imported");

        try {
            boolean imported = dBconnection.getRs().next();
            if (!imported) {
                noReportImported.setText("Нет загруженных отчётов");
                noReportImported.setVisible(true);
                openExportToExcelWindow.setDisable(true);
            } else {
                noReportImported.setVisible(false);
                openExportToExcelWindow.setDisable(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initLoader() throws IOException {
        fxmlExportToExcelViewLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/exportToExcel.fxml"));
        fxmlExportToExcelView = fxmlExportToExcelViewLoader.load();
        exportToExcelController = fxmlExportToExcelViewLoader.getController();
    }

    public void browse() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        csvFile = fileChooser.showOpenDialog(new Stage());
        if (csvFile == null) {
            return;
        }
        String csvFileName = csvFile.getName();
        int dotIndex = csvFileName.lastIndexOf('.');
        csvFileName = (dotIndex == -1) ? csvFileName : csvFileName.substring(0, dotIndex);
        csvFileName = csvFileName.replace("-", ".");
        dBconnection.openDB();
        dBconnection.query("select monthyear from onec_imported");
        try {
            while (dBconnection.getRs().next()) {
                if (csvFileName.equals(dBconnection.getRs().getString(1))) {
                    csvFile = null;
                    alreadyImported.setText("Отчёт за данный период уже загружен");
                    alreadyImported.setVisible(true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        upload1C.setDisable(csvFile == null);
    }

    public void upload1C() {
        dBconnection.openDB();
        CSVLoader csvLoader = new CSVLoader(dBconnection.getC());
        try {
            csvLoader.loadCSV(csvFile, "onec");
        } catch (Exception e) {
            e.printStackTrace();
        }
        upload1C.setDisable(true);
        dBconnection.openDB();
        dBconnection.query("select monthyear from onec_imported");
        try {
            boolean imported = dBconnection.getRs().next();
            if (!imported) {
                noReportImported.setText("Нет загруженных отчётов");
                noReportImported.setVisible(true);
                openExportToExcelWindow.setDisable(true);
            } else {
                noReportImported.setVisible(false);
                openExportToExcelWindow.setDisable(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    public void openExportToExcelWindow() {
        setExportToExcelStage();
    }

    private void setExportToExcelStage() {

        if (exportToExcelStage == null) {
            exportToExcelStage = new Stage();
            exportToExcelStage.setScene(new Scene(fxmlExportToExcelView));
            exportToExcelStage.setMinHeight(100);
            exportToExcelStage.setMinWidth(100);
            exportToExcelStage.setResizable(false);
            exportToExcelStage.initModality(Modality.WINDOW_MODAL);
            exportToExcelStage.initOwner(new Stage());
            
        }
        exportToExcelController.addData();
        exportToExcelStage.showAndWait();
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

    public void addData() {
    }
}
