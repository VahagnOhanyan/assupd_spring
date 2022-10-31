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
package com.ctp.asupdspring.controllers;

import com.ctp.asupdspring.app.repo.OnecImportedEntityRepository;
import com.ctp.asupdspring.domain.OnecImportedEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
@RequiredArgsConstructor
@Component
@FxmlView("importOneCReport.fxml")
public class ImportOneCReportController {
    @FXML
    public Label alreadyImported;
    @FXML
    public Label noReportImported;
    @FXML
    public AnchorPane anchorPahe;
    @FXML
    private Button upload1C;
    @FXML
    private Button openExportToExcelWindow;
    private final OnecImportedEntityRepository onecImportedEntityRepository;
    private Stage stage;
    private final CSVLoader csvLoader;
    private final FxWeaver fxWeaver;
    File csvFile = null;

    @FXML
    private void initialize() {
        stage = new Stage();
        stage.setScene(new Scene(anchorPahe));
        stage.setMinHeight(100);
        stage.setMinWidth(200);
        stage.setResizable(false);
        stage.setTitle("Отчёт табелей АСУ ПД vs 1С");
        stage.initModality(Modality.WINDOW_MODAL);

        List<OnecImportedEntity> onecImportedEntities = onecImportedEntityRepository.findAll();

        boolean imported = !onecImportedEntities.isEmpty();
        if (!imported) {
            noReportImported.setText("Нет загруженных отчётов");
            noReportImported.setVisible(true);
            openExportToExcelWindow.setDisable(true);
        } else {
            noReportImported.setVisible(false);
            openExportToExcelWindow.setDisable(false);
        }
    }
    public void show() {
        stage.show();
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
        List<OnecImportedEntity> onecImportedEntities = onecImportedEntityRepository.findAll();

        for (OnecImportedEntity o : onecImportedEntities) {
            if (csvFileName.equals(o.getMonthyear())) {
                csvFile = null;
                alreadyImported.setText("Отчёт за данный период уже загружен");
                alreadyImported.setVisible(true);
            }
        }

        upload1C.setDisable(csvFile == null);
    }

    public void upload1C() {

        try {
            csvLoader.loadCSV(csvFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        upload1C.setDisable(true);
        List<OnecImportedEntity> onecImportedEntities = onecImportedEntityRepository.findAll();
        boolean imported = !onecImportedEntities.isEmpty();
        if (!imported) {
            noReportImported.setText("Нет загруженных отчётов");
            noReportImported.setVisible(true);
            openExportToExcelWindow.setDisable(true);
        } else {
            noReportImported.setVisible(false);
            openExportToExcelWindow.setDisable(false);
        }
    }

    public void openExportToExcelWindow() {
        setExportToExcelStage();
    }

    private void setExportToExcelStage() {
        ExportToExcelController exportToExcelController = fxWeaver.loadController(ExportToExcelController.class);
        exportToExcelController.addData();
        exportToExcelController.show();


    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

    public void addData() {
    }
}
