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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import ru.ctp.motyrev.code.DBconnection;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * todo Document type ExportToExcelController
 */
public class ExportToExcelController {
    @FXML
    public ComboBox<String> monthYear;
    @FXML
    private Button exportToExcel;

    DBconnection dBconnection = new DBconnection();
    DBconnection dBconnection2 = new DBconnection();
    FileOutputStream fileOut = null;
    String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator;
    SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    String[] stages = {"Работы по бэк-офису", "Отпуск", "Больничный", "Обучение", "IDLE", "", "Отпуск без сохранения з/п"};
    String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    ArrayList<String> monthYearList = new ArrayList<>();

    public void exportToExcel() {
        InputStream inp = null;
        Workbook wb = null;
        try {
            inp = Objects.requireNonNull(this.getClass().getResource("/ru/ctp/motyrev/templates/asupdAnd1cReport.xls")).openStream();
            wb = WorkbookFactory.create(inp);
        } catch (IOException | InvalidFormatException e) {
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
            dBconnection.openDB();
            dBconnection.query("select * from onec where monthyear = '" + monthYear.getValue() + "'");
            int i = 2;
            int count = dBconnection.getRs().getMetaData().getColumnCount();

            while (dBconnection.getRs().next()) {
                boolean deltaExists = false;
                Row row = sheet.createRow(i);
                for (int j = 1; j <= count; j++) {
                    if (j <= 2) {
                        row.createCell(j).setCellValue(dBconnection.getRs().getString(j));
                    } else if (j == 3) {
                        row.createCell(j).setCellValue(months[Integer.parseInt(dBconnection.getRs().getString(j).substring(3, 5)) - 1] + " " +
                                dBconnection.getRs().getString(j).substring(6));
                    } else {
                        row.createCell(j).setCellValue(String.valueOf(dBconnection.getRs().getDouble(j)));
                        if (j == 4) {
                            worked1C = dBconnection.getRs().getDouble(j);
                        }
                        if (j == 5) {
                            hospital1C = dBconnection.getRs().getDouble(j);
                        }
                        if (j == 6) {
                            vacation1C = dBconnection.getRs().getDouble(j);
                        }
                    }
                }
                String user = row.getCell(1).getStringCellValue();
                double worked = 0.0;
                int workedCellIndex = 0;
                int hospitalCellIndex = 0;
                int vacationCellIndex = 0;
                double vacation = 0.0;
                double hospital = 0.0;

                dBconnection2.openDB();
                dBconnection2.query("select 'Явка', sum(dw.daily_intensity) from public.task t join public.stage s on s.task_id = t.task_id " +
                        " join public.stage_type st on st.stage_type_id = s.stage_type_id" +
                        " join public.stage_daily sd on sd.stage_id = s.stage_id" +
                        " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id" +
                        " join public.user u on u.user_id = dw.user_id where u.user_id_number = '" + user.substring(user.length() - 4) +
                        "' and to_char(dw.daily_work_date,'MM-yyyy')= '" + sdf.format(dateFromQuery) + "'");

                while (dBconnection2.getRs().next()) {
                    worked = dBconnection2.getRs().getDouble(2);
                    row.createCell(count + 1).setCellValue(String.valueOf(worked));
                    workedCellIndex = count + 1;
                }

                dBconnection2.query("select st.user_stage_type_name, sum(dw.daily_intensity) from public.user_stage s\n" +
                        " join public.user_stage_type st on st.user_stage_type_id = s.user_stage_type_id\n" +
                        " join public.user_stage_daily sd on sd.user_stage_id = s.user_stage_id\n" +
                        " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                        " join public.user u on u.user_id = dw.user_id where u.user_id_number = '" + user.substring(user.length() - 4) +
                        "' and to_char(dw.daily_work_date,'MM-yyyy')= '" + sdf.format(dateFromQuery) + "'" + " group by st.user_stage_type_name");

                while (dBconnection2.getRs().next()) {
                    if (dBconnection2.getRs().getString(1).equals(stages[2]) || dBconnection2.getRs().getString(1).equals(stages[5])) {
                        hospital = dBconnection2.getRs().getDouble(2) + hospital;
                        if (hospitalCellIndex == 0) {
                            row.createCell(count + 2).setCellValue(String.valueOf(hospital));
                            hospitalCellIndex = count + 2;
                        } else {
                            row.getCell(hospitalCellIndex).setCellValue(String.valueOf(hospital));
                        }
                    } else if (dBconnection2.getRs().getString(1).equals(stages[1]) || dBconnection2.getRs().getString(1).equals(stages[6])) {
                        vacation = dBconnection2.getRs().getDouble(2) + vacation;
                        if (vacationCellIndex == 0) {
                            row.createCell(count + 3).setCellValue(String.valueOf(vacation));
                            vacationCellIndex = count + 3;
                        } else {
                            row.getCell(vacationCellIndex).setCellValue(String.valueOf(vacation));
                        }
                    } else if (dBconnection2.getRs().getString(1).equals(stages[3]) || dBconnection2.getRs().getString(1).equals(stages[0]) ||
                            dBconnection2.getRs().getString(1).equals(stages[4])) {
                        worked = dBconnection2.getRs().getDouble(2) + worked;
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
                    dBconnection2.queryClose();
                    dBconnection2.closeDB();
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
                dBconnection2.queryClose();
                dBconnection2.closeDB();
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
            dBconnection.queryClose();
            dBconnection.closeDB();
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
        dBconnection.openDB();
        dBconnection.query("select * from onec_imported");
        try {
            while (dBconnection.getRs().next()) {
                monthYearList.add(dBconnection.getRs().getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        monthYear.getItems().addAll(monthYearList);
        monthYear.getSelectionModel().selectFirst();
        exportToExcel.setDisable(monthYearList.isEmpty());
        dBconnection.queryClose();
        dBconnection.closeDB();
    }

    private void formClear() {
        monthYearList.clear();
        monthYear.getItems().clear();
    }
}
