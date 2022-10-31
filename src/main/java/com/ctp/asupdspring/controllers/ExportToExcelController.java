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

import com.ctp.asupdspring.app.repo.OnecEntityRepository;
import com.ctp.asupdspring.app.repo.OnecImportedEntityRepository;
import com.ctp.asupdspring.app.repo.UserRepository;
import com.ctp.asupdspring.domain.OnecEntity;
import com.ctp.asupdspring.domain.OnecImportedEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * todo Document type ExportToExcelController
 */
@RequiredArgsConstructor
@Component
@FxmlView("exportToExcel.fxml")
public class ExportToExcelController {
    @FXML
    public ComboBox<String> monthYear;
    @FXML
    public AnchorPane anchorPahe;
    @FXML
    private Button exportToExcel;
    private Stage stage;
    private final OnecEntityRepository onecEntityRepository;
    private final OnecImportedEntityRepository onecImportedEntityRepository;
    private final UserRepository userRepository;
    FileOutputStream fileOut = null;
    String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator;
    SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    String[] stages = {"Работы по бэк-офису", "Отпуск", "Больничный", "Обучение", "IDLE", "", "Отпуск без сохранения з/п"};
    String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    ArrayList<String> monthYearList = new ArrayList<>();
    @FXML
    private void initialize() {
        stage = new Stage();
        stage.setScene(new Scene(anchorPahe));
        stage.setMinHeight(100);
        stage.setMinWidth(100);
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(new Stage());
    }
    public void show() {
        stage.show();
    }
    public void exportToExcel() {
        InputStream inp = null;
        Workbook wb = null;
        try {
            inp = Objects.requireNonNull(this.getClass().getResource("/templates/asupdAnd1cReport.xls")).openStream();
            wb = WorkbookFactory.create(inp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HSSFCellStyle cellStyle = (HSSFCellStyle) Objects.requireNonNull(wb).createCellStyle();
        HSSFCellStyle cellStyleLess = (HSSFCellStyle) wb.createCellStyle();
        HSSFCellStyle cellStyleMore = (HSSFCellStyle) wb.createCellStyle();
        cellStyleMore.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        cellStyleMore.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyleLess.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        cellStyleLess.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Sheet sheet;
        double worked1C = 0.0;
        double vacation1C = 0.0;
        double hospital1C = 0.0;

        try {
            Date dateFromQuery = new SimpleDateFormat("dd.MM.yyyy").parse(monthYear.getValue());
            sheet = wb.getSheetAt(0);
            int count = 7;
            List<OnecEntity> onecEntityList = onecEntityRepository.findByMonthyear(monthYear.getValue());
            int i = 2;
            for (OnecEntity o : onecEntityList) {

                boolean deltaExists = false;
                Row row = sheet.createRow(i);
                row.createCell(1).setCellValue(o.getNum());
                row.createCell(2).setCellValue(o.getFullname());
                row.createCell(3).setCellValue(months[Integer.parseInt(o.getMonthyear().substring(3, 5)) - 1] + " " +
                        o.getMonthyear().substring(6));
                row.createCell(4).setCellValue(o.getWorked());
                worked1C = o.getWorked();
                row.createCell(5).setCellValue(o.getHospital());
                hospital1C = o.getHospital();
                row.createCell(6).setCellValue(o.getVacation());
                vacation1C = o.getVacation();
                row.createCell(7).setCellValue(o.getTotal());
                String user = row.getCell(1).getStringCellValue();
                double worked = 0.0;
                int workedCellIndex = 0;
                int hospitalCellIndex = 0;
                int vacationCellIndex = 0;
                double vacation = 0.0;
                double hospital = 0.0;
                List<Object[]> allWorkedIntensity = userRepository.getEmployeeWorkedIntensity(user.substring(user.length() - 4), sdf.format(dateFromQuery));

                for (Object[] w : allWorkedIntensity) {
                    worked = (double) w[1];
                    row.createCell(count + 1).setCellValue(String.valueOf(worked));
                    workedCellIndex = count + 1;
                }
                List<Object[]> allStagesIntensity = userRepository.getEmployeeAllStagesIntensity(user.substring(user.length() - 4), sdf.format(dateFromQuery));

                for (Object[] w : allStagesIntensity) {
                    if (w[0].equals(stages[2]) || w[0].equals(stages[5])) {
                        hospital = (double) w[1] + hospital;
                        if (hospitalCellIndex == 0) {
                            row.createCell(count + 2).setCellValue(String.valueOf(hospital));
                            hospitalCellIndex = count + 2;
                        } else {
                            row.getCell(hospitalCellIndex).setCellValue(String.valueOf(hospital));
                        }
                    } else if (w[0].equals(stages[1]) || w[0].equals(stages[6])) {
                        vacation = (double) w[1] + vacation;
                        if (vacationCellIndex == 0) {
                            row.createCell(count + 3).setCellValue(String.valueOf(vacation));
                            vacationCellIndex = count + 3;
                        } else {
                            row.getCell(vacationCellIndex).setCellValue(String.valueOf(vacation));
                        }
                    } else if (w[0].equals(stages[3]) || w[0].equals(stages[0]) ||
                            w[0].equals(stages[4])) {
                        worked = (double) w[1] + worked;
                        row.getCell(workedCellIndex).setCellValue(String.valueOf(worked));
                    }
                }

                String total = String.valueOf(worked + vacation + hospital);
                row.createCell(count + 4).setCellValue(total);

                double workedDelta = worked - worked1C;
                double hospitalDelta = hospital - hospital1C;
                double vacationDelta = vacation - vacation1C;
                if (workedDelta != 0.0) {
                    deltaExists = true;
                }
                if (hospitalDelta != 0.0) {
                    deltaExists = true;
                }
                if (vacationDelta != 0.0) {
                    deltaExists = true;
                }
                double totalDelta = (worked + hospital + vacation) - (worked1C + hospital1C + vacation1C);
                if (!deltaExists) {
                    sheet.removeRow(row);
                    continue;
                }
                row.createCell(count + 5).setCellValue(workedDelta);
                row.getCell(count + 5).setCellStyle(workedDelta < 0.0 ? cellStyleLess : workedDelta > 0.0 ? cellStyleMore : cellStyle);
                row.createCell(count + 6).setCellValue(hospitalDelta);
                row.getCell(count + 6).setCellStyle(hospitalDelta < 0.0 ? cellStyleLess : hospitalDelta > 0.0 ? cellStyleMore : cellStyle);
                row.createCell(count + 7).setCellValue(vacationDelta);
                row.getCell(count + 7).setCellStyle(vacationDelta < 0.0 ? cellStyleLess : vacationDelta > 0.0 ? cellStyleMore : cellStyle);
                row.createCell(count + 8).setCellValue(totalDelta);
                row.getCell(count + 8).setCellStyle(totalDelta < 0.0 ? cellStyleLess : totalDelta > 0.0 ? cellStyleMore : cellStyle);
                i++;
            }

            fileOut = new FileOutputStream(path + sdf2.format(dateFromQuery) + ".xls");
            wb.write(fileOut);
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            if (desktop != null) {
                desktop.open(new File(path + sdf2.format(dateFromQuery) + ".xls"));
            }
            wb.close();
            inp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
        formClear();
    }

    public void addData() {
        formClear();
        List<OnecImportedEntity> onecImportedEntityList = onecImportedEntityRepository.findAll();
        for (OnecImportedEntity o : onecImportedEntityList) {
            monthYearList.add(o.getMonthyear());
        }
        monthYear.getItems().addAll(monthYearList);
        monthYear.getSelectionModel().selectFirst();
        exportToExcel.setDisable(monthYearList.isEmpty());
    }

    private void formClear() {
        monthYearList.clear();
        monthYear.getItems().clear();
    }
}
