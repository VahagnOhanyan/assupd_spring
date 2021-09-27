package ru.ctp.motyrev.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import ru.ctp.motyrev.code.DBconnection;
import ru.ctp.motyrev.interfaces.impls.CollectionUserState;
import ru.ctp.motyrev.interfaces.impls.CollectionWorksBook;
import ru.ctp.motyrev.objects.UserState;
import ru.ctp.motyrev.objects.Works;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EffectivityReportController {

    @FXML
    private Button reportButton;
    @FXML
    private ComboBox quarterBox;
    @FXML
    private ComboBox yearBox;
    @FXML
    private TextField firstField;
    @FXML
    private TextField secondField;
    @FXML
    private TextField thirdField;
    @FXML
    private TextField fourthField;
    @FXML
    private ProgressIndicator reportIndicator;

    NumberFormat formatter = new DecimalFormat("#0.00");

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert warningAlert = new Alert(Alert.AlertType.WARNING);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataShort = FXCollections.observableArrayList();

    DBconnection dBconnection = new DBconnection();

    private CollectionWorksBook worksBook = new CollectionWorksBook();
    private CollectionUserState userState = new CollectionUserState();

    private final String pattern = "yyyy-MM-dd";

    @FXML
    private void initialize() {

        quarterTimeFill();

        quarterBoxFill();

        yearsFill();

        StringConverter converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern(pattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }

        };

        yearBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!yearBox.getSelectionModel().isEmpty() & !quarterBox.getSelectionModel().isEmpty()) {
                reportButton.setDisable(false);
            } else {
                reportButton.setDisable(true);
            }
        });

        quarterBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!yearBox.getSelectionModel().isEmpty() & !quarterBox.getSelectionModel().isEmpty()) {
                reportButton.setDisable(false);
            } else {
                reportButton.setDisable(true);
            }
        });

    }

    public void quarterTimeFill() {
        firstField.setText("480");
        secondField.setText("480");
        thirdField.setText("480");
        fourthField.setText("480");
    }

    public void quarterBoxFill() {
        quarterBox.getItems().addAll("I квартал", "II квартал", "III квартал", "IV квартал");
    }

    public void yearsFill() {
        yearBox.getItems().addAll("2018", "2019", "2020", "2021");
    }

    public void addData() {
        formClear();

        quarterTimeFill();

        quarterBoxFill();

        yearsFill();
    }

    public void generateEffectivityReport(ActionEvent actionEvent) {

        if (MainController.role.equalsIgnoreCase("ауп") || MainController.role.equalsIgnoreCase("admin") || MainController.role.equalsIgnoreCase("super_user")) {
            dataSelect("WITH Intens AS " +
                    "(SELECT SUM(dwk.daily_intensity) AS sumIntens, SUM(dwk.daily_intensity) AS sumUserIntens, min(dwk.daily_work_date) AS minDate, max(dwk.daily_work_date) AS maxDate, ur.user_id AS user1, tk.task_id AS task1 FROM public.daily_work dwk " +
                    "join public.user ur on ur.user_id = dwk.user_id " +
                    "join public.stage_daily sdy on sdy.daily_work_id = dwk.daily_work_id " +
                    "join public.stage st on st.stage_id = sdy.stage_id " +
                    "join public.task tk on tk.task_id = st.task_id " +
                    "join public.status sts on sts.status_id = tk.status_id " +
                    "WHERE dwk.daily_work_date <= '" + yearBox.getSelectionModel().getSelectedItem() + "-" + genQuarterEnd(quarterBox.getSelectionModel().getSelectedItem().toString()) + "' " +
                    "AND sts.status_name = 'утверждено' AND tk.task_id NOT IN " +
                    "(SELECT tsk.task_id FROM public.task tsk " +
                    "join public.stage ste on ste.task_id = tsk.task_id " +
                    "join public.stage_daily stdy on stdy.stage_id = ste.stage_id " +
                    "join public.daily_work dywk on dywk.daily_work_id = stdy.daily_work_id " +
                    "join public.status stts on stts.status_id = tsk.status_id " +
                    "WHERE dywk.daily_work_date > '" + yearBox.getSelectionModel().getSelectedItem() + "-" + genQuarterEnd(quarterBox.getSelectionModel().getSelectedItem().toString()) + "') " +
                    "AND tk.task_id IN " +
                    "(SELECT tsk1.task_id FROM public.task tsk1 " +
                    "join public.stage ste1 on ste1.task_id = tsk1.task_id " +
                    "join public.stage_daily stdy1 on stdy1.stage_id = ste1.stage_id " +
                    "join public.daily_work dywk1 on dywk1.daily_work_id = stdy1.daily_work_id " +
                    "join public.status stts1 on stts1.status_id = tsk1.status_id " +
                    "WHERE dywk1.daily_work_date <= '" + yearBox.getSelectionModel().getSelectedItem() + "-" + genQuarterEnd(quarterBox.getSelectionModel().getSelectedItem().toString()) + "' " +
                    "AND dywk1.daily_work_date >= '" + yearBox.getSelectionModel().getSelectedItem() + "-" + genQuarterStart(quarterBox.getSelectionModel().getSelectedItem().toString()) + "') " +
                    "GROUP BY user1, task1) " +
                    "SELECT u.user_fullname, t.task_number, " +
                    "(SELECT min(minDate) FROM Intens WHERE task1 = t.task_id), " +
                    "(SELECT max(maxDate) FROM Intens WHERE task1 = t.task_id), " +
                    "t.task_pa_intensity, " +
                    "(SELECT (CASE WHEN task_out = false THEN 'Нет' " +
                    "ELSE 'Да' " +
                    "END) FROM public.task WHERE task_id = t.task_id), t.task_tz_intensity, " +
                    "(SELECT (CASE WHEN t.task_out = false THEN SUM(sumIntens) " +
                            "ELSE t.task_pa_intensity+SUM(sumIntens) " +
                            "END) FROM Intens WHERE task1 = t.task_id), " +
                    "round((t.task_tz_intensity/(SELECT (CASE WHEN t.task_out = false THEN SUM(sumIntens) " +
                    "ELSE t.task_pa_intensity+SUM(sumIntens) " +
                    "END) FROM Intens WHERE task1 = t.task_id))*100, 2), " +
                    "(SELECT SUM(sumUserIntens) FROM Intens WHERE user1 = u.user_id AND task1 = t.task_id), " +
                    "round(((SELECT SUM(sumUserIntens) AS Float FROM Intens WHERE user1 = u.user_id AND task1 = t.task_id)/480)*100, 2), " +
                    "round((t.task_tz_intensity/(SELECT (CASE WHEN t.task_out = false THEN SUM(sumIntens) " +
                            "ELSE t.task_pa_intensity+SUM(sumIntens) " +
                            "END) FROM Intens WHERE task1 = t.task_id))*((SELECT SUM(sumUserIntens) AS Float FROM Intens WHERE user1 = u.user_id AND task1 = t.task_id)/480)*100, 2) FROM public.task t " +
                    "join public.stage s on s.task_id = t.task_id " +
                    "join public.status ss on ss.status_id = t.status_id " +
                    "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                    "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                    "join public.user u on u.user_id = dw.user_id " +
                    "WHERE t.task_id IN (SELECT task1 FROM Intens) " +
                    "GROUP BY u.user_fullname, u.user_id, t.task_number, t.task_id, t.task_tz_intensity " +
                    "ORDER BY u.user_fullname, t.task_number");
        } else {
            dataSelect("WITH Intens AS " +
                    "(SELECT SUM(dwk.daily_intensity) AS sumIntens, SUM(dwk.daily_intensity) AS sumUserIntens, min(dwk.daily_work_date) AS minDate, max(dwk.daily_work_date) AS maxDate, ur.user_id AS user1, tk.task_id AS task1 FROM public.daily_work dwk " +
                    "join public.user ur on ur.user_id = dwk.user_id " +
                    "join public.stage_daily sdy on sdy.daily_work_id = dwk.daily_work_id " +
                    "join public.stage st on st.stage_id = sdy.stage_id " +
                    "join public.task tk on tk.task_id = st.task_id " +
                    "join public.status sts on sts.status_id = tk.status_id " +
                    "WHERE dwk.daily_work_date <= '" + yearBox.getSelectionModel().getSelectedItem() + "-" + genQuarterEnd(quarterBox.getSelectionModel().getSelectedItem().toString()) + "' " +
                    "AND sts.status_name = 'утверждено' AND " +
                    "tk.task_id NOT IN " +
                    "(SELECT tsk.task_id FROM public.task tsk " +
                    "join public.stage ste on ste.task_id = tsk.task_id " +
                    "join public.stage_daily stdy on stdy.stage_id = ste.stage_id " +
                    "join public.daily_work dywk on dywk.daily_work_id = stdy.daily_work_id " +
                    "join public.status stts on stts.status_id = tsk.status_id " +
                    "WHERE dywk.daily_work_date > '" + yearBox.getSelectionModel().getSelectedItem() + "-" + genQuarterEnd(quarterBox.getSelectionModel().getSelectedItem().toString()) + "') " +
                    "AND tk.task_id IN " +
                    "(SELECT tsk1.task_id FROM public.task tsk1 " +
                    "join public.stage ste1 on ste1.task_id = tsk1.task_id " +
                    "join public.stage_daily stdy1 on stdy1.stage_id = ste1.stage_id " +
                    "join public.daily_work dywk1 on dywk1.daily_work_id = stdy1.daily_work_id " +
                    "join public.status stts1 on stts1.status_id = tsk1.status_id " +
                    "WHERE dywk1.daily_work_date <= '" + yearBox.getSelectionModel().getSelectedItem() + "-" + genQuarterEnd(quarterBox.getSelectionModel().getSelectedItem().toString()) + "' " +
                    "AND dywk1.daily_work_date >= '" + yearBox.getSelectionModel().getSelectedItem() + "-" + genQuarterStart(quarterBox.getSelectionModel().getSelectedItem().toString()) + "') " +
                    "AND ur.user_id IN " +
                    "(SELECT us.user_sub_id FROM public.user_subordination us " +
                    "join public.user ur1 on ur1.user_id = us.user_id " +
                    "WHERE ur1.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who + "') " +
                    "GROUP BY us.user_sub_id HAVING count(us.user_sub_id) >= 1) " +
                    "GROUP BY user1, task1), " +
                    "ALLINTENS AS " +
                            "(SELECT SUM(dwk3.daily_intensity) AS allIntens, tk3.task_id as task2 FROM public.daily_work dwk3 " +
                            "join public.user ur3 on ur3.user_id = dwk3.user_id " +
                            "join public.stage_daily sdy3 on sdy3.daily_work_id = dwk3.daily_work_id " +
                            "join public.stage st3 on st3.stage_id = sdy3.stage_id " +
                            "join public.task tk3 on tk3.task_id = st3.task_id " +
                            "join public.status sts3 on sts3.status_id = tk3.status_id " +
                            "WHERE sts3.status_name = 'утверждено' " +
                            "GROUP BY tk3.task_id) "+
                    "SELECT u.user_fullname, t.task_number, " +
                    "(SELECT min(minDate) FROM Intens WHERE task1 = t.task_id), " +
                    "(SELECT max(maxDate) FROM Intens WHERE task1 = t.task_id), " +
                    "t.task_pa_intensity, " +
                    "(SELECT (CASE WHEN task_out = false THEN 'Нет' " +
                    "ELSE 'Да' " +
                    "END) FROM public.task WHERE task_id = t.task_id), t.task_tz_intensity, " +
                    "(SELECT (CASE WHEN t.task_out = false THEN allIntens " +
                    "ELSE t.task_pa_intensity+allIntens " +
                    "END) FROM ALLINTENS WHERE task2 = t.task_id), " +
                    "round((t.task_tz_intensity/(SELECT (CASE WHEN t.task_out = false THEN allIntens " +
                            "ELSE t.task_pa_intensity+allIntens " +
                            "END) FROM ALLINTENS WHERE task2 = t.task_id))*100, 2), " +
                    "(SELECT SUM(sumUserIntens) FROM Intens WHERE user1 = u.user_id AND task1 = t.task_id), " +
                    "round(((SELECT SUM(sumUserIntens) AS Float FROM Intens WHERE user1 = u.user_id AND task1 = t.task_id)/480)*100, 2), " +
                    "round((t.task_tz_intensity/(SELECT (CASE WHEN t.task_out = false THEN allIntens " +
                            "ELSE t.task_pa_intensity+allIntens " +
                            "END) FROM ALLINTENS WHERE task2 = t.task_id))*((SELECT SUM(sumUserIntens) AS Float FROM Intens WHERE user1 = u.user_id AND task1 = t.task_id)/480)*100, 2) FROM public.task t " +
                    "join public.stage s on s.task_id = t.task_id " +
                    "join public.status ss on ss.status_id = t.status_id " +
                    "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                    "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                    "join public.user u on u.user_id = dw.user_id " +
                    "WHERE t.task_id IN (SELECT task1 FROM Intens) " +
                    "AND u.user_id IN (SELECT user1 FROM Intens) " +
                    "GROUP BY u.user_fullname, u.user_id, t.task_number, t.task_id, t.task_tz_intensity " +
                    "ORDER BY u.user_fullname, t.task_number");
        }

        InputStream inp;
        try {
            inp = this.getClass().getResource("/ru/ctp/motyrev/templates/effectivity_template.xls").openStream();
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
            HSSFCellStyle cellStyle14 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle15 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle16 = (HSSFCellStyle) wb.createCellStyle();
            HSSFCellStyle cellStyle17 = (HSSFCellStyle) wb.createCellStyle();

            Font boldFont = wb.createFont();
            boldFont.setBold(true);

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

            cellStyle14.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle14.setAlignment(HorizontalAlignment.CENTER);
            cellStyle14.setFont(boldFont);
            cellStyle14.setBorderBottom(BorderStyle.THIN);
            cellStyle14.setBorderTop(BorderStyle.THIN);
            cellStyle14.setBorderLeft(BorderStyle.THIN);
            cellStyle14.setBorderRight(BorderStyle.THIN);

            cellStyle15.setDataFormat(wb.createDataFormat().getFormat("@"));
            cellStyle15.setAlignment(HorizontalAlignment.CENTER);
            cellStyle15.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle15.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle15.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            cellStyle15.setBorderBottom(BorderStyle.THIN);
            cellStyle15.setBorderTop(BorderStyle.THIN);
            cellStyle15.setBorderLeft(BorderStyle.THIN);
            cellStyle15.setBorderRight(BorderStyle.THIN);
            cellStyle15.setWrapText(true);

            cellStyle16.setDataFormat(wb.createDataFormat().getFormat("0.00"));
            cellStyle16.setAlignment(HorizontalAlignment.CENTER);
            cellStyle16.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle16.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            cellStyle16.setBorderBottom(BorderStyle.THIN);
            cellStyle16.setBorderTop(BorderStyle.THIN);
            cellStyle16.setBorderLeft(BorderStyle.THIN);
            cellStyle16.setBorderRight(BorderStyle.THIN);

            cellStyle17.setDataFormat(wb.createDataFormat().getFormat("0.0%"));
            cellStyle17.setAlignment(HorizontalAlignment.CENTER);
            cellStyle17.setBorderBottom(BorderStyle.THIN);
            cellStyle17.setBorderTop(BorderStyle.THIN);
            cellStyle17.setBorderLeft(BorderStyle.THIN);
            cellStyle17.setBorderRight(BorderStyle.THIN);


            wb.setSheetName(0, "Эфф. сотр. за " + quarterBox.getSelectionModel().getSelectedItem() + " " + yearBox.getSelectionModel().getSelectedItem());

            Sheet firstSheet = wb.getSheetAt(0);

            Integer rows = dataSelect.size();
            Integer l = 0;
            Double percSum = 0.0;

            for ( int i = 0; i < rows; i++) {

                Row row = firstSheet.createRow(l+1);
                Row row2 = firstSheet.createRow(l+2);

                ObservableList<String> rowShort = FXCollections.observableArrayList();

                for ( int k = 0; k < dataSelect.get(i).size(); k++) {

                    if (i != 0) {
                        if (k==0) {
                            if (!dataSelect.get(i).get(k).toString().equals(dataSelect.get(i-1).get(k).toString())) {
                                row.createCell(k + 1).setCellValue(dataSelect.get(i).get(k).toString());
                                row.getCell(k + 1).setCellStyle(cellStyle14);
                                l=l+1;
                                rowShort.add(dataSelect.get(i).get(k).toString());
                            } else {
                                rowShort.add(dataSelect.get(i).get(k).toString());
                            }
                        } else {
                            if (!dataSelect.get(i).get(0).toString().equals(dataSelect.get(i-1).get(0).toString())) {
                                if ((k!=1) & (k!=2) & (k!=3) & (k!=5)) {
                                    row2.createCell(k).setCellValue(Double.parseDouble(dataSelect.get(i).get(k).toString()));
                                    row2.getCell(k).setCellStyle(cellStyle2);
                                } else {
                                    row2.createCell(k).setCellValue(dataSelect.get(i).get(k).toString());
                                    row2.getCell(k).setCellStyle(cellStyle);
                                }
                            } else {
                                if ((k!=1) & (k!=2) & (k!=3) & (k!=5)) {
                                    row.createCell(k).setCellValue(Double.parseDouble(dataSelect.get(i).get(k).toString()));
                                    row.getCell(k).setCellStyle(cellStyle2);
                                } else {
                                    row.createCell(k).setCellValue(dataSelect.get(i).get(k).toString());
                                    row.getCell(k).setCellStyle(cellStyle);
                                }

                            }
                        }
                    } else {
                        if (k==0) {
                            row.createCell(k + 1).setCellValue(dataSelect.get(i).get(k).toString());
                            row.getCell(k + 1).setCellStyle(cellStyle14);
                            l=l+1;
                            rowShort.add(dataSelect.get(i).get(k).toString());
                        } else {
                            if ((k!=1) & (k!=2) & (k!=3) & (k!=5)) {
                                row2.createCell(k).setCellValue(Double.parseDouble(dataSelect.get(i).get(k).toString()));
                                row2.getCell(k).setCellStyle(cellStyle2);
                            } else {
                                row2.createCell(k).setCellValue(dataSelect.get(i).get(k).toString());
                                row2.getCell(k).setCellStyle(cellStyle);
                            }

                        }
                    }

                    if (k == dataSelect.get(i).size() - 1) {
                        percSum = percSum + Double.parseDouble(dataSelect.get(i).get(k).toString());
                    }

                    if (i != rows-1) {
                        if ((k == dataSelect.get(i).size() - 1) & (!dataSelect.get(i).get(0).toString().equals(dataSelect.get(i + 1).get(0).toString()))) {
                            l = l + 1;
                            Row row3 = firstSheet.createRow(l+1);
                            row3.createCell(k-1).setCellValue("Итого эфф. сумм.");
                            row3.getCell(k-1).setCellStyle(cellStyle);
                            row3.createCell(k).setCellValue(percSum);
                            row3.getCell(k).setCellStyle(cellStyle2);
                            l = l + 1;
                            Row row4 = firstSheet.createRow(l+1);
                            row4.createCell(k-1).setCellValue("Показатель эфф.");
                            row4.getCell(k-1).setCellStyle(cellStyle);
                            row4.createCell(k).setCellValue(percSum - 100);
                            row4.getCell(k).setCellStyle(cellStyle2);
                            l = l + 1;
                            rowShort.add(formatter.format(percSum - 100));
                            dataShort.add(rowShort);
                            percSum = 0.0;
                        }
                    } else {
                        if (k == dataSelect.get(i).size() - 1) {
                            l = l + 1;
                            Row row3 = firstSheet.createRow(l+1);
                            row3.createCell(k-1).setCellValue("Итого эфф. сумм.");
                            row3.getCell(k-1).setCellStyle(cellStyle);
                            row3.createCell(k).setCellValue(percSum);
                            row3.getCell(k).setCellStyle(cellStyle2);
                            l = l + 1;
                            Row row4 = firstSheet.createRow(l+1);
                            row4.createCell(k-1).setCellValue("Показатель эфф.");
                            row4.getCell(k-1).setCellStyle(cellStyle);
                            row4.createCell(k).setCellValue(percSum - 100);
                            row4.getCell(k).setCellStyle(cellStyle2);
                            l = l + 1;
                            rowShort.add(formatter.format(percSum - 100));
                            dataShort.add(rowShort);
                            percSum = 0.0;
                        }

                        firstSheet.autoSizeColumn(k + 1);
                    }
                }
                l=l+1;
            }

            System.out.println(dataShort);

            Row rowHead = firstSheet.createRow(l+1);
            rowHead.createCell(1).setCellValue("Сотрудник");
            rowHead.getCell(1).setCellStyle(cellStyle14);
            rowHead.createCell(2).setCellValue("Эфф.");
            rowHead.getCell(2).setCellStyle(cellStyle14);
            l=l+1;

            for ( int p = 0; p < dataShort.size(); p++) {
                Row row = firstSheet.createRow(l+1);

                row.createCell(1).setCellValue(dataShort.get(p).get(0).toString());
                row.getCell(1).setCellStyle(cellStyle5);
                row.createCell(2).setCellValue(Double.parseDouble(dataShort.get(p).get(1).toString().replace(",",".")));
                row.getCell(2).setCellStyle(cellStyle2);
                l=l+1;
            }

            FileOutputStream fileOut;


            fileOut = new FileOutputStream("Отчет по эффективности сотрудников  за " + quarterBox.getSelectionModel().getSelectedItem() + " " + yearBox.getSelectionModel().getSelectedItem() + ".xls");

            wb.write(fileOut);
            fileOut.close();
            inp.close();

            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }

            try {
                desktop.open(new File("Отчет по эффективности сотрудников  за " + quarterBox.getSelectionModel().getSelectedItem() + " " + yearBox.getSelectionModel().getSelectedItem() + ".xls"));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }

    }

    private String genQuarterStart(String quarter) {
        String start = "01-01";

        switch (quarter) {
            case "I квартал":
                start = "01-01";
                break;
            case "II квартал":
                start = "04-01";
                break;
            case "III квартал":
                start = "07-01";
                break;
            case "IV квартал":
                start = "10-01";
                break;
        }

        return start;
    }

        private String genQuarterEnd(String quarter){
            String end = "03-31";;

            switch (quarter) {
                case "I квартал":
                    end = "03-31";
                    break;
                case "II квартал":
                    end = "06-30";
                    break;
                case "III квартал":
                    end = "09-30";
                    break;
                case "IV квартал":
                    end = "12-31";
                    break;
            }
            return end;
        }

    private ObservableList dataSelect(String k) {
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

    /*private String generatePeriod () {
        if (reportSwitch.isSelected()) {
            period = "dw.daily_work_date >= '"+ startDate.getValue() +"' AND dw.daily_work_date <= '"+ endDate.getValue() +"'";
        } else {
            period = "dw.daily_work_date <= '"+ endDate.getValue()+"'";
        }
        return period;
    }*/

    private void formClear() {
        dataShort.clear();
        dataSelect.clear();
        reportButton.setDisable(true);
        quarterBox.getItems().clear();
        yearBox.getItems().clear();
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
        formClear();
    }

}
