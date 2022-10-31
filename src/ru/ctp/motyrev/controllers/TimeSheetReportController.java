package ru.ctp.motyrev.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.controlsfx.control.ToggleSwitch;
import ru.ctp.motyrev.code.DBconnection;
import ru.ctp.motyrev.interfaces.impls.CollectionUserState;
import ru.ctp.motyrev.interfaces.impls.CollectionWorksBook;
import ru.ctp.motyrev.objects.UserState;
import ru.ctp.motyrev.objects.Works;

import java.awt.*;
import java.awt.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimeSheetReportController {

    @FXML
    private ComboBox<String> yearBox;
    @FXML
    private ComboBox<String> halfyearBox;
    @FXML
    private ComboBox<String> quarterBox;
    @FXML
    private ComboBox<String> monthBox;
    @FXML
    private Button btnGenerate;
    @FXML
    private ComboBox periodBox;
    @FXML
    private RadioButton reportSwitch;
    /*@FXML
    private RadioButton reportSwitchForPeriod;*/
    @FXML
    private RadioButton reportSwitchForQuarter;
    @FXML
    private RadioButton reportSwitchForMonth;
    @FXML
    private RadioButton reportSwitchForYear;
    @FXML
    private RadioButton reportSwitchForHalfYear;
    /* @FXML
     private ToggleSwitch approveSwitch;*/
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;

    private String period = null;
    private String contractNumber;
    private String customer;
    private String notApproved = "";

    private static final String SUPER_USER = "Super_user";
    private static final String ADMIN = "Admin";
    private static final String DEPARTMENT_HEAD = "Начальник отдела";
    private static final String LEAD_SPECIALIST = "Ведущий специалист";

    private static final String DATA_ERROR = "Ошибка data";
    private static final String TOTAL = "Итого";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat sdf3 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private final Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private final Alert warningAlert = new Alert(Alert.AlertType.WARNING);
    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private final Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataProjects = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataRequests = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataActivity = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataActivityReq = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataApproved = FXCollections.observableArrayList();

    LocalDate firstQuarterStart = LocalDate.of(LocalDate.now().getYear(), Month.of(1), 1);
    LocalDate firstQuarterEnd = LocalDate.of(LocalDate.now().getYear(), Month.of(3), 31);
    LocalDate secondQuarterStart = LocalDate.of(LocalDate.now().getYear(), Month.of(4), 1);
    LocalDate secondQuarterEnd = LocalDate.of(LocalDate.now().getYear(), Month.of(6), 30);
    LocalDate thirdQuarterStart = LocalDate.of(LocalDate.now().getYear(), Month.of(7), 1);
    LocalDate thirdQuarterEnd = LocalDate.of(LocalDate.now().getYear(), Month.of(9), 30);
    LocalDate fourthQuarterStart = LocalDate.of(LocalDate.now().getYear(), Month.of(10), 1);
    LocalDate fourthQuarterEnd = LocalDate.of(LocalDate.now().getYear(), Month.of(12), 31);
    Calendar myCalForDiff = new GregorianCalendar();
    String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    FileOutputStream fileOut = null;
    String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator;
    DBconnection dBconnection = new DBconnection();
    private String user;
    GregorianCalendar startDateCalendar = new GregorianCalendar();
    GregorianCalendar endDateCalendar = new GregorianCalendar();
    GregorianCalendar date = new GregorianCalendar();
    ObservableList<ObservableList> data = FXCollections.observableArrayList();
    @FXML
    public ProgressBar genProgress;

    private final CollectionWorksBook worksBook = new CollectionWorksBook();
    private final CollectionUserState userState = new CollectionUserState();

    private static final String pattern = "yyyy-MM-dd";

    @FXML
    private void initialize() {

        yearBox.getItems().addAll("2018", "2019", "2020", "2021", "2022");
        halfyearBox.getItems().addAll("I", "II");
        quarterBox.getItems().addAll("I", "II", "III", "IV");
        String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        monthBox.getItems().addAll(months);
        reportSwitch.setSelected(true);

        reportSwitch.selectedProperty().addListener(observable -> {
            if (reportSwitch.isSelected()) {
                startDate.setVisible(true);
                startDate.setDisable(true);
                monthBox.setVisible(false);
                quarterBox.setVisible(false);
                halfyearBox.setVisible(false);
                yearBox.setVisible(false);
                endDate.setVisible(true);
                startDate.setValue(LocalDate.of(2018, 1, 1));
                endDate.setValue(LocalDate.now());
            }
        });

     /*   reportSwitchForPeriod.selectedProperty().addListener(observable -> {
            if (reportSwitchForPeriod.isSelected()) {
                monthBox.setVisible(false);
                quarterBox.setVisible(false);
                halfyearBox.setVisible(false);
                yearBox.setVisible(false);
                startDate.setVisible(true);
                startDate.setDisable(false);
                endDate.setVisible(true);
                endDate.setPromptText("по");
                startDate.setValue(LocalDate.now());
                endDate.setValue(LocalDate.now());
            }
        });*/

        reportSwitchForYear.selectedProperty().addListener(observable -> {
            if (reportSwitchForYear.isSelected()) {
                startDate.setVisible(true);
                startDate.setDisable(true);
                endDate.setVisible(false);
                monthBox.setVisible(false);
                quarterBox.setVisible(false);
                halfyearBox.setVisible(false);
                startDate.setValue(LocalDate.of(LocalDate.now().getYear(), Month.of(1), 1));
                yearBox.setVisible(true);
                yearBox.setLayoutX(182.0);
                yearBox.setValue(String.valueOf(LocalDate.now().getYear()));
                endDate.setValue(LocalDate.now());
            }
        });
        reportSwitchForHalfYear.selectedProperty().addListener(observable -> {
            if (reportSwitchForHalfYear.isSelected()) {
                startDate.setVisible(false);
                endDate.setVisible(false);
                monthBox.setVisible(false);
                quarterBox.setVisible(false);
                halfyearBox.setVisible(true);
                if (LocalDate.now().isBefore(LocalDate.of(LocalDate.now().getYear(), Month.of(6), 30))) {
                    halfyearBox.setValue("I");
                    startDate.setValue(LocalDate.of(LocalDate.now().getYear(), Month.of(1), 1));
                } else {
                    halfyearBox.setValue("II");
                    startDate.setValue(LocalDate.of(LocalDate.now().getYear(), Month.of(7), 1));
                }
                yearBox.setVisible(true);
                yearBox.setLayoutX(182.0);
                yearBox.setValue(String.valueOf(LocalDate.now().getYear()));
                endDate.setValue(LocalDate.now());
            }
        });

        reportSwitchForQuarter.selectedProperty().addListener(observable -> {
            if (reportSwitchForQuarter.isSelected()) {
                startDate.setVisible(false);
                endDate.setVisible(false);
                monthBox.setVisible(false);
                halfyearBox.setVisible(false);
                quarterBox.setVisible(true);
                if (LocalDate.now().isBefore(secondQuarterStart)) {
                    quarterBox.setValue("I");
                    startDate.setValue(firstQuarterStart);
                } else if (LocalDate.now().isBefore(thirdQuarterStart)) {
                    quarterBox.setValue("II");
                    startDate.setValue(secondQuarterStart);
                } else if (LocalDate.now().isBefore(fourthQuarterStart)) {
                    quarterBox.setValue("III");
                    startDate.setValue(thirdQuarterStart);
                } else {
                    quarterBox.setValue("IV");
                    startDate.setValue(fourthQuarterStart);
                }
                endDate.setValue(LocalDate.now());
                yearBox.setVisible(true);
                yearBox.setLayoutX(182.0);
                yearBox.setValue(String.valueOf(LocalDate.now().getYear()));
            }
        });

        reportSwitchForMonth.selectedProperty().addListener(observable -> {
            if (reportSwitchForMonth.isSelected()) {
                startDate.setVisible(false);
                endDate.setVisible(false);
                quarterBox.setVisible(false);
                halfyearBox.setVisible(false);
                monthBox.setVisible(true);
                monthBox.setValue(months[LocalDate.now().getMonth().ordinal()]);
                yearBox.setVisible(true);
                yearBox.setLayoutX(182.0);
                yearBox.setValue(String.valueOf(LocalDate.now().getYear()));
                startDate.setValue(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1));
                endDate.setValue(LocalDate.now());
            }
        });

        monthBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                LocalDate oldLdstart = startDate.getValue();
                LocalDate newLdstart = oldLdstart.withMonth(new TimeSheetController().returnMonth(newValue) + 1);
                startDate.setValue(newLdstart);
                defineMaxDayOfMonth(newValue);
                checkIfNowIsWithinRange();
            }
        });

        yearBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                LocalDate oldLdstart = startDate.getValue();
                LocalDate newLdstart = oldLdstart.withYear(Integer.parseInt(newValue));
                startDate.setValue(newLdstart);
                if (monthBox.isVisible()) {
                    defineMaxDayOfMonth(monthBox.getValue());
                } else if (halfyearBox.isVisible()) {
                    defineHalfYearRange(halfyearBox.getValue());
                } else if (quarterBox.isVisible()) {
                    defineQuarterRange(quarterBox.getValue());
                } else {

                    LocalDate oldLdend = endDate.getValue();
                    LocalDate newLdend = oldLdend.withYear(Integer.parseInt(newValue)).withMonth(12).withDayOfMonth(31);
                    endDate.setValue(newLdend);
                }
                checkIfNowIsWithinRange();
            }
        });
        halfyearBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                defineHalfYearRange(newValue);
                checkIfNowIsWithinRange();
            }
        });

        quarterBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                defineQuarterRange(newValue);
                checkIfNowIsWithinRange();
            }
        });

        endDate.valueProperty().addListener((observable) -> {
            System.out.println("startDate: " + startDate.getValue() + " endDate: " + endDate.getValue());
            btnGenerate.setDisable(
                    endDate.getValue() == null || (endDate.getValue() != null && endDate.getValue().isAfter(LocalDate.now())));
        });

        StringConverter converter = new StringConverter<LocalDate>() {
            final DateTimeFormatter dateFormatter =
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

        startDate.setConverter(converter);
        endDate.setConverter(converter);
    }

    private void defineQuarterRange(String value) {
        if (value.equals("I")) {
            startDate.setValue(firstQuarterStart.withYear(Integer.parseInt(yearBox.getValue())));
            endDate.setValue(firstQuarterEnd.withYear(Integer.parseInt(yearBox.getValue())));
        }
        if (value.equals("II")) {
            startDate.setValue(secondQuarterStart.withYear(Integer.parseInt(yearBox.getValue())));
            endDate.setValue(secondQuarterEnd.withYear(Integer.parseInt(yearBox.getValue())));
        }
        if (value.equals("III")) {
            startDate.setValue(thirdQuarterStart.withYear(Integer.parseInt(yearBox.getValue())));
            endDate.setValue(thirdQuarterEnd.withYear(Integer.parseInt(yearBox.getValue())));
        }
        if (value.equals("IV")) {
            startDate.setValue(fourthQuarterStart.withYear(Integer.parseInt(yearBox.getValue())));
            endDate.setValue(fourthQuarterEnd.withYear(Integer.parseInt(yearBox.getValue())));
        }
    }

    private void defineHalfYearRange(String value) {
        if (value.equals("I")) {
            startDate.setValue(firstQuarterStart.withYear(Integer.parseInt(yearBox.getValue())));
            endDate.setValue(secondQuarterEnd.withYear(Integer.parseInt(yearBox.getValue())));
        }
        if (value.equals("II")) {
            startDate.setValue(thirdQuarterStart.withYear(Integer.parseInt(yearBox.getValue())));
            endDate.setValue(fourthQuarterEnd.withYear(Integer.parseInt(yearBox.getValue())));
        }
    }

    private void defineMaxDayOfMonth(String value) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(yearBox.getValue()), new TimeSheetController().returnMonth(value), 1);
        int res = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        endDate.setValue(LocalDate.of(Integer.parseInt(yearBox.getValue()), new TimeSheetController().returnMonth(value) + 1, res));
    }

    private void checkIfNowIsWithinRange() {
        if (LocalDate.now().isAfter(startDate.getValue()) && LocalDate.now().isBefore(endDate.getValue())) {
            endDate.setValue(LocalDate.now());
        }
    }

    public void addData() {
        formClear();
        startDate.setValue(LocalDate.of(2018, 1, 1));
        endDate.setValue(LocalDate.now());
    }

    public void initTimeSheetReport(String user, GregorianCalendar date, ObservableList<ObservableList> data) {
        formClear();
        this.startDateCalendar = date;
        this.user = user;
        this.data = data;

        date.set(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH), date.get(GregorianCalendar.DAY_OF_MONTH));

        // addData(date);

    }

    public void exportToExcel() {
        InputStream inp = null;
        Workbook wb = null;
        try {
            inp = Objects.requireNonNull(this.getClass().getResource("/ru/ctp/motyrev/templates/timeSheetReport.xls")).openStream();
            wb = WorkbookFactory.create(inp);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        System.out.println("startDate: " + startDate.getValue() + " endDate: " + endDate.getValue());
        startDateCalendar.set(startDate.getValue().getYear(), startDate.getValue().getMonthValue() - 1, startDate.getValue().getDayOfMonth());
        endDateCalendar.set(endDate.getValue().getYear(), endDate.getValue().getMonthValue() - 1, endDate.getValue().getDayOfMonth());
        System.out.println("startDateCalendar: " + startDateCalendar.toString());
        System.out.println("endDateCalendar:" + endDateCalendar.toString());
        int sheetNum = 0;
        date.set(startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH), startDateCalendar.get(Calendar.DATE));
        while (date.before(endDateCalendar) || date.equals(endDateCalendar)) {

            fillCrossTab();
            Sheet sheet;
            if (sheetNum > 0) {

                wb.cloneSheet(0);
                wb.setSheetName(sheetNum + 1, months[date.get(Calendar.MONTH)] + " " + date.get(Calendar.YEAR));
                wb.setSheetOrder(months[date.get(Calendar.MONTH)] + " " + date.get(Calendar.YEAR), sheetNum);

                sheet = wb.getSheetAt(sheetNum);

                int lastRow = wb.getSheetAt(sheetNum).getLastRowNum();
                sheet.removeRow(sheet.getRow(2));
                for (int i = 4; i <= lastRow; i++) {
                    sheet.removeRow(sheet.getRow(i));
                }
            } else {
                sheet = wb.getSheetAt(sheetNum);
                wb.setSheetName(0, months[date.get(Calendar.MONTH)] + " " + date.get(Calendar.YEAR));
            }
            fillReportSheet(sheet, months[date.get(Calendar.MONTH)] + " " + date.get(Calendar.YEAR));
            date.add(Calendar.MONTH, 1);
            System.out.println("date:" + date);
            sheetNum++;
        }

        fillTotal(wb.getSheetAt(sheetNum));

        try {
            if (sdf2.format(startDateCalendar.getTime()).compareTo(sdf2.format(endDateCalendar.getTime())) == 0) {

                fileOut = new FileOutputStream(
                        path + "Табель " + user.substring(7, user.indexOf(",", 7)) + " за " + sdf2.format(endDateCalendar.getTime()) + ".xls");
            } else {
                fileOut = new FileOutputStream(
                        path + "Табель " + user.substring(7, user.indexOf(",", 7)) + " за " + sdf2.format(startDateCalendar.getTime()) + " - " +
                                sdf2.format(endDateCalendar.getTime()) + ".xls");
            }

            wb.write(fileOut);
            wb.close();
            inp.close();
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            if (desktop != null) {
                if (sdf2.format(startDateCalendar.getTime()).compareTo(sdf2.format(endDateCalendar.getTime())) == 0) {

                    desktop.open(new File(
                            path + "Табель " + user.substring(7, user.indexOf(",", 7)) + " за " + sdf2.format(endDateCalendar.getTime()) + ".xls"));
                } else {
                    desktop.open(new File(
                            path + "Табель " + user.substring(7, user.indexOf(",", 7)) + " за " + sdf2.format(startDateCalendar.getTime()) + " - " +
                                    sdf2.format(endDateCalendar.getTime()) + ".xls"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillReportSheet(Sheet sheet, String period) {
        Row row;
        row = sheet.createRow(1);
        row.createCell(1).setCellValue(user.substring(7, user.indexOf(",", 7)));
        row = sheet.createRow(2);
        row.createCell(1).setCellValue(period);
        for (int i = 1; i <= data.size(); i++) {
            System.out.println(data.get(i - 1));
            int cellNum = 1;
            row = sheet.createRow(i + 3);
            ObservableList rowData = data.get(i - 1);

            for (int j = 0; j < rowData.size(); j++) {
                if (j == 0 || j == 3) {
                    continue;
                }
                row.createCell(cellNum).setCellValue((String) rowData.get(j));
                cellNum++;
            }
        }
    }

    private void fillTotal(Sheet sheet) {
        String effectivity = "";
        ArrayList<String> tasks = new ArrayList<>();
        data.clear();
        dBconnection.openDB();

        dBconnection.query(
                "select r.request_number, t.task_number, '?', sum(dw.daily_intensity), sts.status_name, " +
                        "concat(cus.customer_name, ', ', t.task_number,', ',r.request_number,', ',sts.status_name) " +
                        "from public.task t join public.stage s on s.task_id = t.task_id " +
                        "left join public.request r on r.request_id = t.request_id  " +
                        "left join contract c on c.contract_id = r.contract_id " +
                        "left join customer cus on cus.customer_id = c.customer_id " +
                        "join status sts on sts.status_id = t.status_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                        "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                        "join public.user u on u.user_id = dw.user_id where u.user_id_number ='" + user.substring(1, user.indexOf(",")) + "' " +
                        "and to_char(dw.daily_work_date,'yyyy-MM')>='" + sdf2.format(startDateCalendar.getTime()) + "' " +
                        "and dw.daily_work_date <='" + sdf.format(endDateCalendar.getTime()) + "' " +
                        "group by r.request_number, t.task_number, sts.status_name, cus.customer_name");

        try {
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    if (i == 2) {
                        tasks.add(dBconnection.getRs().getString(i));
                    }

                    System.out.println(dBconnection.getRs().getString(i));
                    row.add(dBconnection.getRs().getString(i));
                }

                data.add(row);
                System.out.println(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int k = 0;
        for (String t : tasks) {
            dBconnection.query(

                    "SELECT round(t.task_tz_intensity/nullif(sum(dw.daily_intensity),0),2) as intense, t.task_tz_intensity, sum(dw.daily_intensity)  FROM" +
                            " public.stage s" +
                            " join public.task t on t.task_id = s.task_id" +
                            " join public.stage_type st on st.stage_type_id = s.stage_type_id" +
                            " join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                            " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id" +
                            " AND t.task_number='" + t + "'" +
                            " group by t.task_number, t.task_tz_intensity");
            try {
                while (dBconnection.getRs().next()) {
                    effectivity = dBconnection.getRs().getString(1);
                    System.out.println("effectivity: " + effectivity);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            data.get(k).set(2, effectivity.replace(".",","));

            k++;
        }

        Row row;
        row = sheet.createRow(1);
        row.createCell(1).setCellValue(user.substring(7, user.indexOf(",", 7)));
        row = sheet.createRow(2);
        if (sdf2.format(startDateCalendar.getTime()).compareTo(sdf2.format(endDateCalendar.getTime())) == 0) {
            row.createCell(1).setCellValue(sdf2.format(endDateCalendar.getTime()));
        } else {
            row.createCell(1).setCellValue(sdf2.format(startDateCalendar.getTime()) + " - " + sdf2.format(endDateCalendar.getTime()));
        }

        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 4);
            ObservableList rowData = data.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                row.createCell(j + 1).setCellValue((String) rowData.get(j));
            }
        }
    }

    private void fillCrossTab() {
        try {
            Double h = 0.0;

            dBconnection.openDB();
            data.clear();

            System.out.println("sdf2.format(date.getTime()): " + sdf2.format(date.getTime()));

            /*dBconnection.query("SELECT * " +
                    "FROM crosstab(" +
                    "'SELECT s.stage_id, t.task_number, st.stage_type_name, stn.stage_note_text, dw.day_num, dw.daily_intensity FROM " +
                    "public.stage s " +
                    "join public.task t on t.task_id = s.task_id " +
                    "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                    "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                    "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                    "join public.user u on u.user_id = dw.user_id " +
                    "left join public.stage_note stn on stn.stage_id = s.stage_id " +
                    "WHERE dw.daily_work_date >= ''" + sdf2.format(date.getTime()) + "-01" + "'' AND dw.daily_work_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + "'' AND u.user_id_number = ''"+ user.substring(1, user.indexOf(",")) +"'' " +
                    "AND (stn.stage_note_id = (SELECT max(stage_note_id) FROM public.stage_note stgn WHERE stgn.user_id = u.user_id AND stgn.stage_id = s.stage_id AND stgn.stage_note_date >= ''" + sdf2.format(date.getTime()) + "-01" +"'' AND stgn.stage_note_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"'') " +
                    "OR NOT EXISTS (SELECT stage_note_id FROM public.stage_note stgn WHERE stgn.stage_note_date >= ''" + sdf2.format(date.getTime()) + "-01" +"'' AND stgn.stage_note_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"'' " +
                    "AND stgn.stage_id = s.stage_id AND stgn.user_id = u.user_id)) " +
                    "ORDER BY t.task_number, s.stage_id', " +
                    "'SELECT d from generate_series(1,31) d') " +
                    "AS (stage_id integer, task_number text, stage_type_name text, stage_note text, day1 numeric, day2 numeric, day3 numeric, day4 numeric, day5 numeric, " +
                    "day6 numeric, day7 numeric, day8 numeric, day9 numeric, day10 numeric, " +
                    "day11 numeric, day12 numeric, day13 numeric, day14 numeric, day15 numeric, " +
                    "day16 numeric, day17 numeric, day18 numeric, day19 numeric, day20 numeric, " +
                    "day21 numeric, day22 numeric, day23 numeric, day24 numeric, day25 numeric, " +
                    "day26 numeric, day27 numeric, day28 numeric, day29 numeric, day30 numeric, day31 numeric)");*/

            dBconnection.query("SELECT * " +
                    "FROM crosstab(" +
                    "'SELECT s.stage_id, t.task_number, st.stage_type_name, ''Загр. врем. отключена'', dw.day_num, sum(dw.daily_intensity) FROM " +
                    "public.stage s " +
                    "join public.task t on t.task_id = s.task_id " +
                    "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                    "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                    "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                    "join public.user u on u.user_id = dw.user_id " +
                    "WHERE dw.daily_work_date >= ''" + sdf2.format(date.getTime()) + "-01" + "'' AND dw.daily_work_date <= ''" + sdf2.format(date.getTime()) +
                    "-" + checkMaximux() + "'' AND u.user_id_number = ''" + user.substring(1, user.indexOf(",")) + "'' " +
                    "GROUP BY t.task_number, s.stage_id, st.stage_type_name, dw.day_num " +
                    "ORDER BY t.task_number, s.stage_id', " +
                    "'SELECT d from generate_series(1,31) d') " +
                    "AS (stage_id integer, task_number text, stage_type_name text, stage_note text, day1 numeric, day2 numeric, day3 numeric, day4 numeric, day5 numeric, " +
                    "day6 numeric, day7 numeric, day8 numeric, day9 numeric, day10 numeric, " +
                    "day11 numeric, day12 numeric, day13 numeric, day14 numeric, day15 numeric, " +
                    "day16 numeric, day17 numeric, day18 numeric, day19 numeric, day20 numeric, " +
                    "day21 numeric, day22 numeric, day23 numeric, day24 numeric, day25 numeric, " +
                    "day26 numeric, day27 numeric, day28 numeric, day29 numeric, day30 numeric, day31 numeric)");

            System.out.println("Запрос выполнен: " + sdf3.format(new Date()));

            System.out.println("Заполнение ObservableList: " + sdf3.format(new Date()));

            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок

                    if (i > 4 & i <= 35) {
                        h += checkVal2(dBconnection.getRs().getString(i));
                    }

                    row.add(dBconnection.getRs().getString(i));
                }

                row.add(new DecimalFormat("#0.00").format(h));
                data.add(row);

                h = 0.0;
            }

            System.out.println("Заполнение ObservableList выполнено: " + sdf3.format(new Date()));

            System.out.println("Запрос к базе по пользовательским задачам: " + sdf3.format(new Date()));

            /*dBconnection.query("SELECT * " +
                    "FROM crosstab(" +
                    "'SELECT us.user_stage_id, ''Не проектная'', ust.user_stage_type_name, us.user_stage_note, dw.day_num, dw.daily_intensity FROM " +
                    "public.user_stage us " +
                    "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                    "join public.user_stage_daily usd on usd.user_stage_id = us.user_stage_id " +
                    "join public.daily_work dw on dw.daily_work_id = usd.daily_work_id " +
                    "join public.user u on u.user_id = dw.user_id " +
                    "left join public.user_stage_note stn on stn.user_stage_id = us.user_stage_id " +
                    "WHERE dw.daily_work_date >= ''" + sdf2.format(date.getTime()) + "-01" + "'' AND dw.daily_work_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + "'' AND u.user_id_number = ''"+ user.substring(1, user.indexOf(",")) +"'' " +
                    "AND (stn.user_stage_note_id = (SELECT max(user_stage_note_id) FROM public.user_stage_note stgn WHERE stgn.user_stage_note_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"'' " +
                    "AND stgn.user_stage_id = us.user_stage_id AND stgn.user_id = u.user_id) " +
                    "OR NOT EXISTS (SELECT user_stage_note_id FROM public.user_stage_note stgn WHERE stgn.user_stage_note_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"'' " +
                    "AND stgn.user_stage_id = us.user_stage_id AND stgn.user_id = u.user_id)) " +
                    "ORDER BY us.user_stage_id', " +
                    "'SELECT d from generate_series(1,31) d') " +
                    "AS (user_stage_id integer, user_task text, stage_type_name text, stage_note text, day1 numeric, day2 numeric, day3 numeric, day4 numeric, day5 numeric, " +
                    "day6 numeric, day7 numeric, day8 numeric, day9 numeric, day10 numeric, " +
                    "day11 numeric, day12 numeric, day13 numeric, day14 numeric, day15 numeric, " +
                    "day16 numeric, day17 numeric, day18 numeric, day19 numeric, day20 numeric, " +
                    "day21 numeric, day22 numeric, day23 numeric, day24 numeric, day25 numeric, " +
                    "day26 numeric, day27 numeric, day28 numeric, day29 numeric, day30 numeric, day31 numeric)");*/

            dBconnection.query("SELECT * " +
                    "FROM crosstab(" +
                    "'SELECT us.user_stage_id, ''Не проектная'', ust.user_stage_type_name, ''Загр. врем. отключена'', dw.day_num, dw.daily_intensity FROM " +
                    "public.user_stage us " +
                    "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                    "join public.user_stage_daily usd on usd.user_stage_id = us.user_stage_id " +
                    "join public.daily_work dw on dw.daily_work_id = usd.daily_work_id " +
                    "join public.user u on u.user_id = dw.user_id " +
                    "left join public.user_stage_note stn on stn.user_stage_id = us.user_stage_id " +
                    "WHERE dw.daily_work_date >= ''" + sdf2.format(date.getTime()) + "-01" + "'' AND dw.daily_work_date <= ''" + sdf2.format(date.getTime()) +
                    "-" + checkMaximux() + "'' AND u.user_id_number = ''" + user.substring(1, user.indexOf(",")) + "'' " +
                    "ORDER BY us.user_stage_id', " +
                    "'SELECT d from generate_series(1,31) d') " +
                    "AS (user_stage_id integer, user_task text, stage_type_name text, stage_note text, day1 numeric, day2 numeric, day3 numeric, day4 numeric, day5 numeric, " +
                    "day6 numeric, day7 numeric, day8 numeric, day9 numeric, day10 numeric, " +
                    "day11 numeric, day12 numeric, day13 numeric, day14 numeric, day15 numeric, " +
                    "day16 numeric, day17 numeric, day18 numeric, day19 numeric, day20 numeric, " +
                    "day21 numeric, day22 numeric, day23 numeric, day24 numeric, day25 numeric, " +
                    "day26 numeric, day27 numeric, day28 numeric, day29 numeric, day30 numeric, day31 numeric)");

            System.out.println("Запрос к базе выполнен: " + sdf3.format(new Date()));

            System.out.println("Дополнение ObservableList: " + sdf3.format(new Date()));

            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок

                    if (i > 4 & i < 35) {
                        h += checkVal2(dBconnection.getRs().getString(i));
                    }

                    row.add(dBconnection.getRs().getString(i));
                }
                row.add(new DecimalFormat("#0.00").format(h));
                data.add(row);

                h = 0.0;
            }

            System.out.println("Дополнение ObservableList выполнено: " + sdf3.format(new Date()));

            dBconnection.queryClose();
            dBconnection.closeDB();

            System.out.println("Метод завершен, база закрыта: " + sdf3.format(new Date()));
        } catch (Exception e) {
            errorAlert.setTitle("Ошибка базы данных");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
    }

    private Integer checkMaximux() {
        int max_date = 0;
        Calendar myCal = date;

        myCal.set(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH), 1);
        myCalForDiff.set(endDateCalendar.get(GregorianCalendar.YEAR), endDateCalendar.get(GregorianCalendar.MONTH), 1);
        if (myCal.get(GregorianCalendar.YEAR) == myCalForDiff.get(GregorianCalendar.YEAR) &&
                myCal.get(GregorianCalendar.MONTH) == myCalForDiff.get(GregorianCalendar.MONTH)) {
            max_date = endDateCalendar.get(Calendar.DAY_OF_MONTH);
        } else {
            max_date = myCal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        }
        System.out.println("max_date: " + max_date);
        System.out.println("endDateCalendar.get(Calendar.DAY_OF_MONTH): " + endDateCalendar.get(Calendar.DAY_OF_MONTH));
        return max_date;
    }

    private Double checkVal2(String s) {
        if (s != null) {
            return Double.parseDouble(s);
        } else {
            return 0.0;
        }
    }

    private ObservableList dataSelect(String k) {
        try {
            dBconnection.openDB();
            dataSelect.clear();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                dataSelect.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch (Exception e) {
            errorAlert.setTitle(DATA_ERROR);
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return dataSelect;
    }

    private ObservableList dataProjects(String k) {
        try {
            dBconnection.openDB();
            dataProjects.clear();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                dataProjects.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch (Exception e) {
            errorAlert.setTitle(DATA_ERROR);
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return dataProjects;
    }

    private ObservableList dataRequests(String k) {
        try {
            dBconnection.openDB();
            dataRequests.clear();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                dataRequests.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch (Exception e) {
            errorAlert.setTitle(DATA_ERROR);
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return dataRequests;
    }

    private ObservableList dataActivity(String k) {
        try {
            dBconnection.openDB();
            dataActivity.clear();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                dataActivity.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch (Exception e) {
            errorAlert.setTitle(DATA_ERROR);
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return dataActivity;
    }

    private ObservableList dataActivityReq(String k) {
        try {
            dBconnection.openDB();
            dataActivityReq.clear();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                dataActivityReq.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch (Exception e) {
            errorAlert.setTitle(DATA_ERROR);
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return dataActivityReq;
    }

    private ObservableList dataApproved(String k) {
        try {
            dBconnection.openDB();
            dataApproved.clear();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                dataApproved.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch (Exception e) {
            errorAlert.setTitle(DATA_ERROR);
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return dataApproved;
    }

    private String generatePeriod() {
        if (!reportSwitch.isSelected()) {
            period = "dw.daily_work_date >= '" + startDate.getValue() + "' AND dw.daily_work_date <= '" + endDate.getValue() + "'";
        } else {
            period = "dw.daily_work_date = '" + endDate.getValue() + "'";
        }
        return period;
    }

    private void formClear() {
        dataProjects.clear();
        dataSelect.clear();
        dataActivity.clear();
        dataApproved.clear();
        btnGenerate.setDisable(true);
        reportSwitch.setSelected(true);
        startDate.setVisible(true);
        startDate.setDisable(true);
        period = null;
        contractNumber = null;
        customer = null;
        notApproved = "";
        //   approveSwitch.setSelected(false);
        startDate.setValue(null);
        endDate.setValue(null);
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
        formClear();
    }
}
