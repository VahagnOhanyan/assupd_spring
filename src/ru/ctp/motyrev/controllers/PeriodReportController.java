package ru.ctp.motyrev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.controlsfx.control.ToggleSwitch;
import ru.ctp.motyrev.code.DBconnection;
import ru.ctp.motyrev.interfaces.impls.CollectionWorksBook;
import ru.ctp.motyrev.objects.Works;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

public class PeriodReportController {

    @FXML
    private Button btnGenerate;
    @FXML
    private ComboBox customerBox;
    @FXML
    private ComboBox contractBox;
    @FXML
    private ComboBox periodBox;
    @FXML
    private ToggleSwitch periodSwitch;
    @FXML
    private ToggleSwitch reportSwitch;

    private String period = null;
    private String year = "2017";
    private String contractNumber;

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    DBconnection dBconnection = new DBconnection();

    private CollectionWorksBook worksBook = new CollectionWorksBook();

    @FXML
    private void initialize() {

        customerBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->  {
            if (!customerBox.getSelectionModel().isEmpty()) {
                if (!newValue.equals(oldValue)) {
                    contractBox.getItems().clear();
                    contractBox.getItems().addAll(data("SELECT contract_number FROM public.contract c " +
                            "join public.customer cr on cr.customer_id = c.customer_id " +
                            "WHERE cr.customer_name = '" + newValue.toString().replace("[", "").replace("]", "") + "'"));
                    contractBox.setDisable(false);
                    periodBox.setDisable(true);
                    periodSwitch.setSelected(false);
                    periodSwitch.setDisable(true);
                    reportSwitch.setSelected(false);
                    btnGenerate.setDisable(true);
                }
            }
        });

        contractBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!contractBox.getSelectionModel().isEmpty()) {
                if (!newValue.equals(oldValue)) {
                    contractNumber = newValue.toString().replace("[","").replace("]","");
                    periodBox.getItems().clear();
                    periodSwitch.setDisable(false);
                    periodSwitch.setSelected(false);
                    reportSwitch.setSelected(false);

                    for (int i = 1; i <= 12; i++) {
                        periodBox.getItems().add(i);
                    }

                    periodBox.setDisable(false);
                }
            }
        });

        reportSwitch.selectedProperty().addListener(observable -> {
            if (reportSwitch.isSelected()) {
                periodSwitch.setSelected(false);

            }
        });

        periodSwitch.selectedProperty().addListener(observable -> {
            if (periodSwitch.isSelected()) {
                periodBox.getItems().clear();
                periodBox.getItems().removeAll();

                periodBox.getItems().add("I квартал");
                periodBox.getItems().add("II квартал");
                periodBox.getItems().add("III квартал");
                periodBox.getItems().add("IV квартал");

            } else {
                periodBox.getItems().clear();
                periodBox.getItems().removeAll();
                for (int i = 1; i <= 12; i++) {
                    periodBox.getItems().add(i);
                }
            }
        });

        periodBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!periodBox.getSelectionModel().isEmpty()) {
                generatePeriod(newValue.toString().replace("[","").replace("]",""));
                btnGenerate.setDisable(false);
            } else {
                btnGenerate.setDisable(true);
            }
        });
    }

    public void addData() {
        formClear();
        customerBox.getItems().addAll(data("SELECT customer_name FROM public.customer"));
    }

    public void generatePeriodReport(ActionEvent actionEvent) {


        int i=3;
        double sum_intensity = 0;
        double count_pa_intensity = 0;
        double count_tz_intensity = 0;
        double count_recieve = 0;
        double count_execute = 0;
        double count_escort = 0;
        double count_check = 0;
        double count_approve = 0;
        double count_spent_aup = 0;
        double count_spent_exec = 0;
        BigDecimal sum_int;
        BigDecimal pa_int;
        BigDecimal tz_int;
        BigDecimal delta_exec_proeb = new BigDecimal("0.00");
        BigDecimal delta_aup_proeb = new BigDecimal("0.00");
        BigDecimal delta;
        BigDecimal delta_prem;
        BigDecimal count_delta = new BigDecimal("0.00");
        BigDecimal count_delta_prem = new BigDecimal("0.00");
        BigDecimal count_sum_profit = new BigDecimal("0.00");
        BigDecimal count_sum_prem = new BigDecimal("0.00");

        try {
            worksBook.fillPeriodReportCollectionDB(contractNumber, period);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            InputStream inp;
            if (!reportSwitch.isSelected()) {
                inp = new FileInputStream("src/periodReport_template.xls");
            } else {
                inp = new FileInputStream("src/currentReport_template.xls");
            }


            Workbook wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(0);

            HSSFCellStyle cellStyle = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle2 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle3 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle4 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle5 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle6 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle7 = (HSSFCellStyle) wb.createCellStyle();

            cellStyle.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            cellStyle2.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            cellStyle2.setAlignment(HorizontalAlignment.CENTER);
            cellStyle2.setBorderBottom(BorderStyle.THIN);
            cellStyle2.setBorderTop(BorderStyle.THIN);
            cellStyle2.setBorderLeft(BorderStyle.THIN);
            cellStyle2.setBorderRight(BorderStyle.THIN);

            cellStyle3.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle3.setAlignment(HorizontalAlignment.CENTER);
            cellStyle3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle3.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            cellStyle3.setBorderBottom(BorderStyle.THIN);
            cellStyle3.setBorderTop(BorderStyle.THIN);
            cellStyle3.setBorderLeft(BorderStyle.THIN);
            cellStyle3.setBorderRight(BorderStyle.THIN);

            cellStyle4.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            cellStyle4.setAlignment(HorizontalAlignment.RIGHT);
            cellStyle4.setBorderBottom(BorderStyle.THIN);
            cellStyle4.setBorderTop(BorderStyle.THIN);
            cellStyle4.setBorderLeft(BorderStyle.THIN);
            cellStyle4.setBorderRight(BorderStyle.THIN);


            cellStyle5.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle5.setAlignment(HorizontalAlignment.LEFT);
            cellStyle5.setBorderBottom(BorderStyle.THIN);
            cellStyle5.setBorderTop(BorderStyle.THIN);
            cellStyle5.setBorderLeft(BorderStyle.THIN);
            cellStyle5.setBorderRight(BorderStyle.THIN);

            cellStyle6.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            cellStyle6.setAlignment(HorizontalAlignment.RIGHT);
            cellStyle6.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle6.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
            cellStyle6.setBorderBottom(BorderStyle.THIN);
            cellStyle6.setBorderTop(BorderStyle.THIN);
            cellStyle6.setBorderLeft(BorderStyle.THIN);
            cellStyle6.setBorderRight(BorderStyle.THIN);

            cellStyle7.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            cellStyle7.setAlignment(HorizontalAlignment.RIGHT);
            cellStyle7.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle7.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            cellStyle7.setBorderBottom(BorderStyle.THIN);
            cellStyle7.setBorderTop(BorderStyle.THIN);
            cellStyle7.setBorderLeft(BorderStyle.THIN);
            cellStyle7.setBorderRight(BorderStyle.THIN);

            if (!reportSwitch.isSelected()) {
                wb.setSheetName(0, "Отчет за " + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + " " + year);
            } else {
                wb.setSheetName(0, "Отчет на конец " + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + " " + year);
            }
            Row shapka = sheet.getRow(1);
            shapka.createCell(1).setCellValue(periodBox.getSelectionModel().getSelectedItem().toString().replace("[","").replace("]","") + "." + year);
            shapka.getCell(1).setCellStyle(cellStyle);

            for (Works work : worksBook.getWorkslist()) {
                Row row = sheet.createRow(i);
                row.createCell(1).setCellValue(Integer.parseInt(work.getNum()));
                row.getCell(1).setCellStyle(cellStyle);
                row.createCell(2).setCellValue(work.getCustomer());
                row.getCell(2).setCellStyle(cellStyle);
                row.createCell(3).setCellValue(work.getContract());
                row.getCell(3).setCellStyle(cellStyle);
                row.createCell(4).setCellValue(work.getRequest());
                row.getCell(4).setCellStyle(cellStyle);
                row.createCell(5).setCellValue(work.getDesignation());
                row.getCell(5).setCellStyle(cellStyle5);
                row.createCell(7).setCellValue(Double.parseDouble(work.getPa_intensity()));
                row.getCell(7).setCellStyle(cellStyle4);
                row.createCell(8).setCellValue(Double.parseDouble(work.getTz_intensity()));
                row.getCell(8).setCellStyle(cellStyle4);
                row.createCell(10).setCellValue(Double.parseDouble(work.getStage_recieve()));
                row.getCell(10).setCellStyle(cellStyle2);
                row.createCell(11).setCellValue(Double.parseDouble(work.getStage_execute()));
                row.getCell(11).setCellStyle(cellStyle2);
                row.createCell(12).setCellValue(Double.parseDouble(work.getStage_escort()));
                row.getCell(12).setCellStyle(cellStyle2);
                row.createCell(13).setCellValue(Double.parseDouble(work.getStage_check()));
                row.getCell(13).setCellStyle(cellStyle2);
                row.createCell(14).setCellValue(Double.parseDouble(work.getStage_approve()));
                row.getCell(14).setCellStyle(cellStyle2);
                row.createCell(15).setCellValue(work.getStatus_id());
                row.getCell(15).setCellStyle(cellStyle3);
                row.createCell(16).setCellValue(Double.parseDouble(work.getStage_execute()));

                if (Double.parseDouble(work.getStage_execute()) < Double.parseDouble(work.getPa_intensity()) & Double.parseDouble(work.getPa_intensity()) != 0) {
                    row.getCell(16).setCellStyle(cellStyle4);
                } else if (Double.parseDouble(work.getStage_execute()) > Double.parseDouble(work.getPa_intensity()) & Double.parseDouble(work.getPa_intensity()) != 0){
                    row.getCell(16).setCellStyle(cellStyle6);
                    delta_exec_proeb = new BigDecimal(Double.parseDouble(work.getPa_intensity()) - Double.parseDouble(work.getStage_execute()));
                } else if (Double.parseDouble(work.getPa_intensity()) == 0){
                    row.getCell(16).setCellStyle(cellStyle4);
                }

                sum_intensity = Double.parseDouble(work.getStage_recieve()) + Double.parseDouble(work.getStage_execute()) + Double.parseDouble(work.getStage_escort()) + Double.parseDouble(work.getStage_check()) + Double.parseDouble(work.getStage_approve());

                sum_int = new BigDecimal(""+sum_intensity);
                pa_int = new BigDecimal(work.getPa_intensity());
                tz_int = new BigDecimal(work.getTz_intensity());

                row.createCell(17).setCellValue(sum_intensity-Double.parseDouble(work.getStage_execute()));
                if (sum_intensity-Double.parseDouble(work.getStage_execute()) < (pa_int.multiply(BigDecimal.valueOf(0.2))).doubleValue() & pa_int.doubleValue() != 0) {
                    row.getCell(17).setCellStyle(cellStyle4);
                } else if (sum_intensity - Double.parseDouble(work.getStage_execute()) > (pa_int.multiply(BigDecimal.valueOf(0.2))).doubleValue() & pa_int.doubleValue() != 0){
                    row.getCell(17).setCellStyle(cellStyle6);
                    delta_aup_proeb = new BigDecimal((pa_int.multiply(BigDecimal.valueOf(0.2))).doubleValue() - (sum_intensity - Double.parseDouble(work.getStage_execute())));
                } else if (pa_int.doubleValue() == 0) {
                    row.getCell(17).setCellStyle(cellStyle4);
                }

                if (!work.getPa_intensity().equals("0")) {
                    delta = sum_int.divide(pa_int.multiply(BigDecimal.valueOf(1.2)),8, BigDecimal.ROUND_HALF_UP).multiply(pa_int.multiply(BigDecimal.valueOf(1.2).multiply(BigDecimal.valueOf(0.2))));
                    delta.setScale(2, BigDecimal.ROUND_HALF_UP);


                    row.createCell(18).setCellValue(pa_int.multiply(BigDecimal.valueOf(1.2).multiply(BigDecimal.valueOf(0.2))).doubleValue());
                    row.getCell(18).setCellStyle(cellStyle4);
                    row.createCell(19).setCellValue(delta.doubleValue());
                    row.getCell(19).setCellStyle(cellStyle4);

                    if (!work.getTz_intensity().equals("0")) {
                        delta_prem = sum_int.divide(pa_int.multiply(BigDecimal.valueOf(1.2)).multiply(BigDecimal.valueOf(1.2)), 8, BigDecimal.ROUND_HALF_UP).multiply(tz_int.subtract(pa_int.multiply(BigDecimal.valueOf(1.2)).multiply(BigDecimal.valueOf(1.2))).add(delta_exec_proeb).add(delta_aup_proeb));
                        row.createCell(20).setCellValue((tz_int.subtract(pa_int.multiply(BigDecimal.valueOf(1.2)).multiply(BigDecimal.valueOf(1.2))).add(delta_exec_proeb).add(delta_aup_proeb)).doubleValue());
                        if ((tz_int.subtract(pa_int.multiply(BigDecimal.valueOf(1.2)).multiply(BigDecimal.valueOf(1.2))).add(delta_exec_proeb).add(delta_aup_proeb)).doubleValue() > 0) {
                            row.getCell(20).setCellStyle(cellStyle4);
                        } else {
                            row.getCell(20).setCellStyle(cellStyle6);
                        }

                        row.createCell(21).setCellValue(delta_prem.doubleValue());
                        if (delta_prem.doubleValue() > 0) {
                            row.getCell(21).setCellStyle(cellStyle4);
                        } else {
                            row.getCell(21).setCellStyle(cellStyle6);
                        }
                    } else {
                        delta_prem = new BigDecimal("0.00");
                        row.createCell(20).setCellValue(0.00);
                        row.getCell(20).setCellStyle(cellStyle7);
                        row.createCell(21).setCellValue(0.00);
                        row.getCell(21).setCellStyle(cellStyle7);
                    }

                } else {
                    delta = new BigDecimal("0.00");
                    delta_prem = new BigDecimal("0.00");
                    row.createCell(18).setCellValue(0.00);
                    row.getCell(18).setCellStyle(cellStyle7);
                    row.createCell(20).setCellValue(0.00);
                    row.getCell(20).setCellStyle(cellStyle7);
                    row.createCell(19).setCellValue(0.00);
                    row.getCell(19).setCellStyle(cellStyle7);
                    row.createCell(21).setCellValue(0.00);
                    row.getCell(21).setCellStyle(cellStyle7);

                }

                count_pa_intensity +=Double.parseDouble(work.getPa_intensity());
                count_tz_intensity +=Double.parseDouble(work.getTz_intensity());
                count_recieve +=Double.parseDouble(work.getStage_recieve());
                count_execute +=Double.parseDouble(work.getStage_execute());
                count_escort +=Double.parseDouble(work.getStage_escort());
                count_check +=Double.parseDouble(work.getStage_check());
                count_approve +=Double.parseDouble(work.getStage_approve());
                count_spent_exec +=Double.parseDouble(work.getStage_execute());
                count_spent_aup += sum_intensity-Double.parseDouble(work.getStage_execute());
                count_delta = count_delta.add(delta);
                count_delta_prem = count_delta_prem.add(delta_prem);
                count_sum_profit = count_sum_profit.add(pa_int.multiply(BigDecimal.valueOf(1.2).multiply(BigDecimal.valueOf(0.2))));
                if ((tz_int.compareTo(BigDecimal.valueOf(0))) == 1) {
                    count_sum_prem = count_sum_prem.add(tz_int.subtract(pa_int.multiply(BigDecimal.valueOf(1.2)).multiply(BigDecimal.valueOf(1.2))).add(delta_exec_proeb).add(delta_aup_proeb));
                } else {
                    count_sum_prem = count_sum_prem.add(BigDecimal.valueOf(0));
                }

                delta_exec_proeb = new BigDecimal("0.00");
                delta_aup_proeb = new BigDecimal("0.00");

                i++;
            }

            Row row = sheet.createRow(i+1);
            row.createCell(5).setCellValue("Итого");
            row.getCell(5).setCellStyle(cellStyle);
            row.createCell(7).setCellValue(count_pa_intensity);
            row.getCell(7).setCellStyle(cellStyle4);
            row.createCell(8).setCellValue(count_tz_intensity);
            row.getCell(8).setCellStyle(cellStyle4);
            row.createCell(10).setCellValue(count_recieve);
            row.getCell(10).setCellStyle(cellStyle2);
            row.createCell(11).setCellValue(count_execute);
            row.getCell(11).setCellStyle(cellStyle2);
            row.createCell(12).setCellValue(count_escort);
            row.getCell(12).setCellStyle(cellStyle2);
            row.createCell(13).setCellValue(count_check);
            row.getCell(13).setCellStyle(cellStyle2);
            row.createCell(14).setCellValue(count_approve);
            row.getCell(14).setCellStyle(cellStyle2);
            row.createCell(15).setCellValue("-");
            row.getCell(15).setCellStyle(cellStyle);
            row.createCell(16).setCellValue(count_spent_exec);
            row.getCell(16).setCellStyle(cellStyle4);
            row.createCell(17).setCellValue(count_spent_aup);
            row.getCell(17).setCellStyle(cellStyle4);
            row.createCell(18).setCellValue(count_sum_profit.doubleValue());
            row.getCell(18).setCellStyle(cellStyle4);
            row.createCell(19).setCellValue(count_delta.doubleValue());
            row.getCell(19).setCellStyle(cellStyle4);
            row.createCell(20).setCellValue(count_sum_prem.doubleValue());
            row.getCell(20).setCellStyle(cellStyle4);
            row.createCell(21).setCellValue(count_delta_prem.doubleValue());
            row.getCell(21).setCellStyle(cellStyle4);

            FileOutputStream fileOut;

            if (!reportSwitch.isSelected()) {
                fileOut = new FileOutputStream(contractNumber + "_отчет_за_период_" + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "_" + year + ".xls");
            } else {
                fileOut = new FileOutputStream(contractNumber + "_отчет_на_конец_" + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "_" + year + ".xls");
            }
            wb.write(fileOut);
            fileOut.close();
            inp.close();

            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }

            if (!reportSwitch.isSelected()) {
                desktop.open(new File(contractNumber + "_отчет_за_период_" + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "_" + year + ".xls"));
            } else {
                desktop.open(new File(contractNumber + "_отчет_на_конец_" + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "_" + year + ".xls"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

    }

    public void generatePeriodReport2(ActionEvent actionEvent) {


        int i=3;
        double sum_intensity = 0;
        double count_pa_intensity = 0;
        double count_tz_intensity = 0;
        double count_recieve = 0;
        double count_execute = 0;
        double count_escort = 0;
        double count_check = 0;
        double count_approve = 0;
        double count_spent_exec = 0;
        double delta;
        double count_delta = 0;

        try {
            worksBook.fillPeriodReportCollectionDB(contractNumber, period);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            InputStream inp;
            if (!reportSwitch.isSelected()) {
                inp = this.getClass().getResource("/ru/ctp/motyrev/templates/periodReport_template.xls").openStream();
            } else {
                inp = this.getClass().getResource("/ru/ctp/motyrev/templates/currentReport_template.xls").openStream();
            }


            Workbook wb = WorkbookFactory.create(inp);
            Sheet sheet = wb.getSheetAt(0);

            HSSFCellStyle cellStyle = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle2 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle3 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle4 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle5 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle6 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle7 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle8 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle9 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle10 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle11 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle12 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle13 = (HSSFCellStyle) wb.createCellStyle();

            cellStyle.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            cellStyle2.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            cellStyle2.setAlignment(HorizontalAlignment.CENTER);
            cellStyle2.setBorderBottom(BorderStyle.THIN);
            cellStyle2.setBorderTop(BorderStyle.THIN);
            cellStyle2.setBorderLeft(BorderStyle.THIN);
            cellStyle2.setBorderRight(BorderStyle.THIN);

            cellStyle3.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle3.setAlignment(HorizontalAlignment.CENTER);
            cellStyle3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle3.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            cellStyle3.setBorderBottom(BorderStyle.THIN);
            cellStyle3.setBorderTop(BorderStyle.THIN);
            cellStyle3.setBorderLeft(BorderStyle.THIN);
            cellStyle3.setBorderRight(BorderStyle.THIN);

            cellStyle4.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            cellStyle4.setAlignment(HorizontalAlignment.RIGHT);
            cellStyle4.setBorderBottom(BorderStyle.THIN);
            cellStyle4.setBorderTop(BorderStyle.THIN);
            cellStyle4.setBorderLeft(BorderStyle.THIN);
            cellStyle4.setBorderRight(BorderStyle.THIN);


            cellStyle5.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle5.setAlignment(HorizontalAlignment.LEFT);
            cellStyle5.setBorderBottom(BorderStyle.THIN);
            cellStyle5.setBorderTop(BorderStyle.THIN);
            cellStyle5.setBorderLeft(BorderStyle.THIN);
            cellStyle5.setBorderRight(BorderStyle.THIN);

            cellStyle6.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            cellStyle6.setAlignment(HorizontalAlignment.RIGHT);
            cellStyle6.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle6.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            cellStyle6.setBorderBottom(BorderStyle.THIN);
            cellStyle6.setBorderTop(BorderStyle.THIN);
            cellStyle6.setBorderLeft(BorderStyle.THIN);
            cellStyle6.setBorderRight(BorderStyle.THIN);

            cellStyle7.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            cellStyle7.setAlignment(HorizontalAlignment.RIGHT);
            cellStyle7.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle7.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            cellStyle7.setBorderBottom(BorderStyle.THIN);
            cellStyle7.setBorderTop(BorderStyle.THIN);
            cellStyle7.setBorderLeft(BorderStyle.THIN);
            cellStyle7.setBorderRight(BorderStyle.THIN);

            cellStyle8.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle8.setAlignment(HorizontalAlignment.CENTER);
            cellStyle8.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle8.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            cellStyle8.setBorderBottom(BorderStyle.THIN);
            cellStyle8.setBorderTop(BorderStyle.THIN);
            cellStyle8.setBorderLeft(BorderStyle.THIN);
            cellStyle8.setBorderRight(BorderStyle.THIN);

            cellStyle9.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle9.setAlignment(HorizontalAlignment.CENTER);
            cellStyle9.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle9.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            cellStyle9.setBorderBottom(BorderStyle.THIN);
            cellStyle9.setBorderTop(BorderStyle.THIN);
            cellStyle9.setBorderLeft(BorderStyle.THIN);
            cellStyle9.setBorderRight(BorderStyle.THIN);

            cellStyle10.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle10.setAlignment(HorizontalAlignment.CENTER);
            cellStyle10.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle10.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            cellStyle10.setBorderBottom(BorderStyle.THIN);
            cellStyle10.setBorderTop(BorderStyle.THIN);
            cellStyle10.setBorderLeft(BorderStyle.THIN);
            cellStyle10.setBorderRight(BorderStyle.THIN);

            cellStyle11.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle11.setAlignment(HorizontalAlignment.CENTER);
            cellStyle11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle11.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
            cellStyle11.setBorderBottom(BorderStyle.THIN);
            cellStyle11.setBorderTop(BorderStyle.THIN);
            cellStyle11.setBorderLeft(BorderStyle.THIN);
            cellStyle11.setBorderRight(BorderStyle.THIN);

            cellStyle12.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle12.setAlignment(HorizontalAlignment.CENTER);
            cellStyle12.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle12.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
            cellStyle12.setBorderBottom(BorderStyle.THIN);
            cellStyle12.setBorderTop(BorderStyle.THIN);
            cellStyle12.setBorderLeft(BorderStyle.THIN);
            cellStyle12.setBorderRight(BorderStyle.THIN);

            cellStyle13.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            cellStyle13.setAlignment(HorizontalAlignment.CENTER);
            cellStyle13.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle13.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            cellStyle13.setBorderBottom(BorderStyle.THIN);
            cellStyle13.setBorderTop(BorderStyle.THIN);
            cellStyle13.setBorderLeft(BorderStyle.THIN);
            cellStyle13.setBorderRight(BorderStyle.THIN);

            if (!reportSwitch.isSelected()) {
                wb.setSheetName(0, "Отчет за " + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + " " + year);
            } else {
                wb.setSheetName(0, "Отчет на конец " + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + " " + year);
            }
            Row shapka = sheet.getRow(1);
            shapka.createCell(1).setCellValue(periodBox.getSelectionModel().getSelectedItem().toString().replace("[","").replace("]","") + "." + year);
            shapka.getCell(1).setCellStyle(cellStyle);

            for (Works work : worksBook.getWorkslist()) {
                Row row = sheet.createRow(i);
                row.createCell(1).setCellValue(Integer.parseInt(work.getNum()));
                row.getCell(1).setCellStyle(cellStyle);
                row.createCell(2).setCellValue(work.getCustomer());
                row.getCell(2).setCellStyle(cellStyle);
                row.createCell(3).setCellValue(work.getContract());
                row.getCell(3).setCellStyle(cellStyle);
                row.createCell(4).setCellValue(work.getRequest());
                row.getCell(4).setCellStyle(cellStyle);
                row.createCell(5).setCellValue(work.getDesignation());
                row.getCell(5).setCellStyle(cellStyle5);
                row.createCell(6).setCellValue(Double.parseDouble(work.getStage_recieve()));
                row.getCell(6).setCellStyle(cellStyle2);
                if (work.getOutsource().equals("f")) {
                    row.createCell(7).setCellValue(Double.parseDouble(work.getStage_execute()));
                    if (Double.parseDouble(work.getPa_intensity()) - Double.parseDouble(work.getStage_execute()) >= 0 ) {
                        row.getCell(7).setCellStyle(cellStyle2);
                    } else {
                        row.getCell(7).setCellStyle(cellStyle13);
                    }

                }else {
                    row.createCell(7).setCellValue("out:" + work.getPa_intensity());
                    row.getCell(7).setCellStyle(cellStyle3);
                }

                row.createCell(8).setCellValue(Double.parseDouble(work.getStage_escort()));
                row.getCell(8).setCellStyle(cellStyle2);
                row.createCell(9).setCellValue(Double.parseDouble(work.getStage_check()));
                row.getCell(9).setCellStyle(cellStyle2);
                row.createCell(10).setCellValue(Double.parseDouble(work.getStage_approve()));
                row.getCell(10).setCellStyle(cellStyle2);

                row.createCell(11).setCellValue(work.getStatus_id());
                if (work.getStatus_id().equals("утверждено")) {
                    row.getCell(11).setCellStyle(cellStyle8);
                } else if (work.getStatus_id().equals("в работе")) {
                    row.getCell(11).setCellStyle(cellStyle9);
                } else if (work.getStatus_id().equals("проверено")) {
                    row.getCell(11).setCellStyle(cellStyle11);
                } else if (work.getStatus_id().equals("выполнено")) {
                    row.getCell(11).setCellStyle(cellStyle10);
                } else if (work.getStatus_id().equals("в ожидании")) {
                    row.getCell(11).setCellStyle(cellStyle12);
                }

                row.createCell(12).setCellValue(Double.parseDouble(work.getPa_intensity()));
                row.getCell(12).setCellStyle(cellStyle4);
                row.createCell(13).setCellValue(Double.parseDouble(work.getTz_intensity()));
                row.getCell(13).setCellStyle(cellStyle4);

                if (work.getOutsource().equals("f")) {
                    sum_intensity = Double.parseDouble(work.getStage_recieve()) + Double.parseDouble(work.getStage_execute()) + Double.parseDouble(work.getStage_escort()) + Double.parseDouble(work.getStage_check()) + Double.parseDouble(work.getStage_approve());
                }else {
                    sum_intensity = Double.parseDouble(work.getStage_recieve()) + Double.parseDouble(work.getPa_intensity()) + Double.parseDouble(work.getStage_escort()) + Double.parseDouble(work.getStage_check()) + Double.parseDouble(work.getStage_approve());
                }

                row.createCell(14).setCellValue(sum_intensity);
                row.getCell(14).setCellStyle(cellStyle4);

                delta = Double.parseDouble(work.getTz_intensity()) - sum_intensity;

                row.createCell(15).setCellValue(delta);
                if (delta > 0) {
                    row.getCell(15).setCellStyle(cellStyle4);
                } else {
                    row.getCell(15).setCellStyle(cellStyle6);
                }

                row.createCell(16).setCellValue(work.getProject());
                row.getCell(16).setCellStyle(cellStyle5);

                count_pa_intensity +=Double.parseDouble(work.getPa_intensity());
                count_tz_intensity +=Double.parseDouble(work.getTz_intensity());
                count_recieve +=Double.parseDouble(work.getStage_recieve());
                count_execute +=Double.parseDouble(work.getStage_execute());
                count_escort +=Double.parseDouble(work.getStage_escort());
                count_check +=Double.parseDouble(work.getStage_check());
                count_approve +=Double.parseDouble(work.getStage_approve());
                count_spent_exec +=sum_intensity;
                count_delta += delta;

                i++;
            }

            Row row = sheet.createRow(i+1);
            row.createCell(5).setCellValue("Итого");
            row.getCell(5).setCellStyle(cellStyle);
            row.createCell(6).setCellValue(count_recieve);
            row.getCell(6).setCellStyle(cellStyle2);
            row.createCell(7).setCellValue(count_execute);
            row.getCell(7).setCellStyle(cellStyle2);
            row.createCell(8).setCellValue(count_escort);
            row.getCell(8).setCellStyle(cellStyle2);
            row.createCell(9).setCellValue(count_check);
            row.getCell(9).setCellStyle(cellStyle2);
            row.createCell(10).setCellValue(count_approve);
            row.getCell(10).setCellStyle(cellStyle2);
            row.createCell(11).setCellValue("-");
            row.getCell(11).setCellStyle(cellStyle);
            row.createCell(12).setCellValue(count_pa_intensity);
            row.getCell(12).setCellStyle(cellStyle4);
            row.createCell(13).setCellValue(count_tz_intensity);
            row.getCell(13).setCellStyle(cellStyle4);
            row.createCell(14).setCellValue(count_spent_exec);
            row.getCell(14).setCellStyle(cellStyle4);
            row.createCell(15).setCellValue(count_delta);
            row.getCell(15).setCellStyle(cellStyle4);

            FileOutputStream fileOut;

            if (!reportSwitch.isSelected()) {
                fileOut = new FileOutputStream(contractNumber + "_отчет_за_период_" + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "_" + year + ".xls");
            } else {
                fileOut = new FileOutputStream(contractNumber + "_отчет_на_конец_" + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "_" + year + ".xls");
            }
            wb.write(fileOut);
            fileOut.close();
            inp.close();

            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }

            if (!reportSwitch.isSelected()) {
                desktop.open(new File(contractNumber + "_отчет_за_период_" + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "_" + year + ".xls"));
            } else {
                desktop.open(new File(contractNumber + "_отчет_на_конец_" + periodBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "_" + year + ".xls"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
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

    private void generatePeriod (String value) {
        if (!reportSwitch.isSelected()) {
            switch (value) {
                case "I квартал":
                    period = "mw.month >= 01 AND mw.month <= 03";
                    break;
                case "II квартал":
                    period = "mw.month >= 04 AND mw.month <= 06";
                    break;
                case "III квартал":
                    period = "mw.month >= 07 AND mw.month <= 09";
                    break;
                case "IV квартал":
                    period = "mw.month >= 10 AND mw.month <= 12";
                    break;
                case "1":
                    period = "mw.month = 01";
                    break;
                case "2":
                    period = "mw.month = 02";
                    break;
                case "3":
                    period = "mw.month = 03";
                    break;
                case "4":
                    period = "mw.month = 04";
                    break;
                case "5":
                    period = "mw.month = 05";
                    break;
                case "6":
                    period = "mw.month = 06";
                    break;
                case "7":
                    period = "mw.month = 07";
                    break;
                case "8":
                    period = "mw.month = 08";
                    break;
                case "9":
                    period = "mw.month = 09";
                    break;
                case "10":
                    period = "mw.month = 10";
                    break;
                case "11":
                    period = "mw.month = 11";
                    break;
                case "12":
                    period = "mw.month = 12";
                    break;
            }
        } else {
            switch (value) {
                case "I квартал":
                    period = "mw.month <= 03";
                    break;
                case "II квартал":
                    period = "mw.month <= 06";
                    break;
                case "III квартал":
                    period = "mw.month <= 09";
                    break;
                case "IV квартал":
                    period = "mw.month <= 12";
                    break;
                case "1":
                    period = "mw.month <= 01";
                    break;
                case "2":
                    period = "mw.month <= 02";
                    break;
                case "3":
                    period = "mw.month <= 03";
                    break;
                case "4":
                    period = "mw.month <= 04";
                    break;
                case "5":
                    period = "mw.month <= 05";
                    break;
                case "6":
                    period = "mw.month <= 06";
                    break;
                case "7":
                    period = "mw.month <= 07";
                    break;
                case "8":
                    period = "mw.month <= 08";
                    break;
                case "9":
                    period = "mw.month <= 09";
                    break;
                case "10":
                    period = "mw.month <= 10";
                    break;
                case "11":
                    period = "mw.month <= 11";
                    break;
                case "12":
                    period = "mw.month <= 12";
                    break;
            }
        }
    }

    private void formClear() {
        data.clear();
        dataSelect.clear();
        btnGenerate.setDisable(true);
        customerBox.getItems().clear();
        contractBox.getItems().clear();
        periodBox.getItems().clear();
        contractBox.setDisable(true);
        periodBox.setDisable(true);
        periodSwitch.setSelected(false);
        periodSwitch.setDisable(true);
        period=null;
        contractNumber=null;

    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

}
