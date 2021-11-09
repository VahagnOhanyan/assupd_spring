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
import org.apache.poi.ss.usermodel.*;
import org.controlsfx.control.ToggleSwitch;
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
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class ResourceReportController {

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
    @FXML
    private RadioButton reportSwitchForPeriod;
    @FXML
    private RadioButton reportSwitchForQuarter;
    @FXML
    private RadioButton reportSwitchForMonth;
    @FXML
    private RadioButton reportSwitchForYear;
    @FXML
    private RadioButton reportSwitchForHalfYear;
    @FXML
    private ToggleSwitch approveSwitch;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private Label txtLabel;

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

    DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy");
    DateTimeFormatter sdf2 = DateTimeFormatter.ofPattern("MM");
    DateTimeFormatter sdf3 = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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

    DBconnection dBconnection = new DBconnection();

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
                startDate.setValue(LocalDate.of(2018,1,1));
                endDate.setValue(LocalDate.now());
            }
        });

        reportSwitchForPeriod.selectedProperty().addListener(observable -> {
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
        });

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
        startDate.setValue(LocalDate.of(2018,1,1));
        endDate.setValue(LocalDate.now());
    }

    public void generateResourceReport(ActionEvent actionEvent) {

        txtLabel.setText("Ожидайте!");

        Task<Integer> taskResourceCalculate = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                btnGenerate.setDisable(true);
                double size = 6;
                double counter = 0;

                if (MainController.role.equalsIgnoreCase(SUPER_USER) || MainController.role.equalsIgnoreCase(ADMIN) ||
                        MainController.role.equalsIgnoreCase("АУП")) {

                    dataSelect("SELECT c.contract_number, cr.customer_name FROM public.contract c " +
                            "join public.customer cr on cr.customer_id = c.customer_id " +
                            "join public.contract_project cp on cp.contract_id = c.contract_id " +
                            "join public.project p on p.project_id = cp.project_id " +
                            "join public.task t on t.project_id = p.project_id " +
                            "join public.stage s on s.task_id = t.task_id " +
                            "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "WHERE " + generatePeriod() + " AND " +
                            "u.user_id IN (SELECT user_id FROM public.user) " +
                            "GROUP BY c.contract_number, cr.customer_name HAVING count(c.contract_id)>=1 " +
                            "ORDER BY cr.customer_name, c.contract_number");

                    dataProjects("SELECT cr.customer_name, c.contract_number, p.project_name, p.project_id  FROM public.contract c " +
                            "join public.customer cr on cr.customer_id = c.customer_id " +
                            "join public.contract_project cp on cp.contract_id = c.contract_id " +
                            "join public.project p on p.project_id = cp.project_id " +
                            "join public.task t on t.project_id = p.project_id " +
                            "join public.stage s on s.task_id = t.task_id " +
                            "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "WHERE " + generatePeriod() + " AND " +
                            "u.user_id IN (SELECT user_id FROM public.user) " +
                            "GROUP BY c.contract_number, cr.customer_name, p.project_name, p.project_id HAVING count(p.project_id)>=1 " +
                            "ORDER BY cr.customer_name, c.contract_number");

                    dataRequests("SELECT cr.customer_name, c.contract_number, r.request_number, r.request_id, " +
                            "CASE WHEN r.request_number ~ E'^\\\\d+$' THEN CAST (r.request_number AS INTEGER) " +
                            "ELSE " +
                            "0 " +
                            "END as sort FROM public.request r " +
                            "join public.contract c on c.contract_id = r.contract_id " +
                            "join public.customer cr on cr.customer_id = c.customer_id " +
                            "join public.task t on t.request_id = r.request_id " +
                            "join public.stage s on s.task_id = t.task_id " +
                            "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "WHERE " + generatePeriod() + " AND " +
                            "u.user_id IN (SELECT user_id FROM public.user) " +
                            "GROUP BY c.contract_number, cr.customer_name, r.request_number, r.request_id HAVING count(r.request_id)>=1 " +
                            "ORDER BY cr.customer_name, c.contract_number, sort, r.request_number");
                } else if (MainController.role.equalsIgnoreCase("Менеджер проекта")) {

                    dataSelect("SELECT c.contract_number, cr.customer_name FROM public.contract c " +
                            "join public.customer cr on cr.customer_id = c.customer_id " +
                            "join public.contract_project cp on cp.contract_id = c.contract_id " +
                            "join public.project p on p.project_id = cp.project_id " +
                            "join public.task t on t.project_id = p.project_id " +
                            "join public.project_manager pm on pm.project_id = p.project_id " +
                            "join public.stage s on s.task_id = t.task_id " +
                            "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "WHERE " + generatePeriod() + " AND " +
                            "u.user_id IN (SELECT user_id FROM public.user) AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" +
                            MainController.who + "') " +
                            "GROUP BY c.contract_number, cr.customer_name HAVING count(c.contract_id)>=1 " +
                            "ORDER BY cr.customer_name, c.contract_number");

                    dataProjects("SELECT cr.customer_name, c.contract_number, p.project_name, p.project_id  FROM public.contract c " +
                            "join public.customer cr on cr.customer_id = c.customer_id " +
                            "join public.contract_project cp on cp.contract_id = c.contract_id " +
                            "join public.project p on p.project_id = cp.project_id " +
                            "join public.project_manager pm on pm.project_id = p.project_id " +
                            "join public.task t on t.project_id = p.project_id " +
                            "join public.stage s on s.task_id = t.task_id " +
                            "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "WHERE " + generatePeriod() + " AND " +
                            "u.user_id IN (SELECT user_id FROM public.user) AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" +
                            MainController.who + "') " +
                            "GROUP BY c.contract_number, cr.customer_name, p.project_name, p.project_id HAVING count(p.project_id)>=1 " +
                            "ORDER BY cr.customer_name, c.contract_number");

                    dataRequests("SELECT cr.customer_name, c.contract_number, r.request_number, r.request_id, " +
                            "CASE WHEN r.request_number ~ E'^\\\\d+$' THEN CAST (r.request_number AS INTEGER) " +
                            "ELSE " +
                            "0 " +
                            "END as sort FROM public.request r " +
                            "join public.contract c on c.contract_id = r.contract_id " +
                            "join public.contract_project cp on cp.contract_id = c.contract_id " +
                            "join public.project p on p.project_id = cp.project_id " +
                            "join public.project_manager pm on pm.project_id = p.project_id " +
                            "join public.customer cr on cr.customer_id = c.customer_id " +
                            "join public.task t on t.request_id = r.request_id " +
                            "join public.stage s on s.task_id = t.task_id " +
                            "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "WHERE " + generatePeriod() + " AND " +
                            "u.user_id IN (SELECT user_id FROM public.user) AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" +
                            MainController.who + "') " +
                            "GROUP BY c.contract_number, cr.customer_name, r.request_number, r.request_id HAVING count(r.request_id)>=1 " +
                            "ORDER BY cr.customer_name, c.contract_number, sort, r.request_number");
                } else if (MainController.role.equalsIgnoreCase(DEPARTMENT_HEAD) || MainController.role.equalsIgnoreCase(LEAD_SPECIALIST)) {

                    dataSelect("SELECT c.contract_number, cr.customer_name FROM public.contract c " +
                            "join public.customer cr on cr.customer_id = c.customer_id " +
                            "join public.contract_project cp on cp.contract_id = c.contract_id " +
                            "join public.project p on p.project_id = cp.project_id " +
                            "join public.task t on t.project_id = p.project_id " +
                            "join public.stage s on s.task_id = t.task_id " +
                            "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                            "WHERE " + generatePeriod() + " AND " +
                            "(us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +
                            "') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who + "')) " +
                            "GROUP BY c.contract_number, cr.customer_name HAVING count(c.contract_id)>=1 " +
                            "ORDER BY cr.customer_name, c.contract_number");

                    dataProjects("SELECT cr.customer_name, c.contract_number, p.project_name, p.project_id FROM public.contract c " +
                            "join public.customer cr on cr.customer_id = c.customer_id " +
                            "join public.contract_project cp on cp.contract_id = c.contract_id " +
                            "join public.project p on p.project_id = cp.project_id " +
                            "join public.task t on t.project_id = p.project_id " +
                            "join public.stage s on s.task_id = t.task_id " +
                            "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                            "WHERE " + generatePeriod() + " AND " +
                            "(us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +
                            "') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who + "')) " +
                            "GROUP BY c.contract_number, cr.customer_name, p.project_name, p.project_id HAVING count(p.project_id)>=1 " +
                            "ORDER BY cr.customer_name, c.contract_number");

                    dataRequests("SELECT cr.customer_name, c.contract_number, r.request_number, r.request_id, " +
                            "CASE WHEN r.request_number ~ E'^\\\\d+$' THEN CAST (r.request_number AS INTEGER) " +
                            "ELSE " +
                            "0 " +
                            "END as sort FROM public.request r " +
                            "join public.contract c on c.contract_id = r.contract_id " +
                            "join public.customer cr on cr.customer_id = c.customer_id " +
                            "join public.task t on t.request_id = r.request_id " +
                            "join public.stage s on s.task_id = t.task_id " +
                            "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                            "WHERE " + generatePeriod() + " AND " +
                            "(us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +
                            "') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who + "')) " +
                            "GROUP BY c.contract_number, cr.customer_name, r.request_number, r.request_id HAVING count(r.request_id)>=1 " +
                            "ORDER BY cr.customer_name, c.contract_number, sort, r.request_number");
                } else {
                    dataSelect("");

                    dataProjects("");

                    dataRequests("");
                }

                size = size + dataSelect.size();

                counter += 1;
                updateProgress(counter, size);
                Thread.sleep(20);

                InputStream inp;
                inp = this.getClass().getResource("/ru/ctp/motyrev/templates/actualResReport_template.xls").openStream();

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
                cellStyle14.setAlignment(HorizontalAlignment.RIGHT);
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

                if (reportSwitch.isSelected()) {
                    wb.setSheetName(0, "Ресурсный отчет за " + endDate.getValue());
                }
                if (reportSwitchForYear.isSelected()) {
                    wb.setSheetName(0, "Ресурсный отчет за " + yearBox.getValue() + " год");
                }
                if (reportSwitchForHalfYear.isSelected()) {
                    wb.setSheetName(0, "Ресурсный отчет за " + halfyearBox.getValue() + " полугодие " + yearBox.getValue() + " года");
                }
                if (reportSwitchForQuarter.isSelected()) {
                    wb.setSheetName(0, "Ресурсный отчет за " + quarterBox.getValue() + " квартал " + yearBox.getValue() + " года");
                }
                if (reportSwitchForMonth.isSelected()) {
                    wb.setSheetName(0, "Ресурсный отчет за " + monthBox.getValue() + " " + yearBox.getValue() + " года");
                }
                if (reportSwitchForPeriod.isSelected()) {
                    wb.setSheetName(0, "Ресурсный отчет c " + startDate.getValue() + " по " + endDate.getValue());
                }
                counter += 1;
                updateProgress(counter, size);
                Thread.sleep(20);

                int j = 2;
                double count_full_fact = 0;
                double count_full_outsource = 0;
                double count_full_delta = 0;

                for (int k = 0; k < dataSelect.size(); k++) {

                    if (dataSelect.size() - 1 > k) {
                        wb.cloneSheet(k + 3);
                    }

                    Sheet nextSheet = wb.getSheetAt(k + 3);

                    contractNumber = dataSelect.get(k).get(0).toString();
                    customer = dataSelect.get(k).get(1).toString();

                    wb.setSheetName(k + 3, "Отчет по " + contractNumber.replace("/", "-"));

                    int i = 3;
                    double sum_intensity = 0;
                    double count_pa_intensity = 0;
                    double count_tz_intensity = 0;
                    double count_recieve = 0;
                    double count_execute = 0;
                    double count_escort = 0;
                    double count_check = 0;
                    double count_approve = 0;
                    double count_spent = 0;
                    double count_outsource = 0;
                    double delta = 0;
                    double count_delta = 0;

                    try {
                        worksBook.fillPeriodReportCollectionDB(contractNumber, generatePeriod());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    for (Works work : worksBook.getWorkslist()) {

                        Row row = nextSheet.createRow(i);
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
                            if (Double.parseDouble(work.getPa_intensity()) - Double.parseDouble(work.getStage_execute()) >= 0) {
                                row.getCell(7).setCellStyle(cellStyle2);
                            } else {
                                row.getCell(7).setCellStyle(cellStyle13);
                            }
                        } else {
                            row.createCell(7).setCellValue("outsource:" + work.getPa_intensity());
                            row.getCell(7).setCellStyle(cellStyle3);

                            count_outsource += Double.parseDouble(work.getPa_intensity());
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

                        row.createCell(12).setCellValue(work.getUom() + ": " + Double.parseDouble(work.getUom_plan()));
                        row.getCell(12).setCellStyle(cellStyle2);

                        row.createCell(13).setCellValue(Double.parseDouble(work.getUom_fact()));
                        row.getCell(13).setCellStyle(cellStyle2);

                        if (Double.parseDouble(work.getUom_plan()) > 0) {
                            row.createCell(14).setCellValue(Double.parseDouble(work.getUom_fact()) / Double.parseDouble(work.getUom_plan()));
                        } else {
                            row.createCell(14).setCellValue(0);
                        }
                        row.getCell(14).setCellStyle(cellStyle17);

                        row.createCell(15).setCellValue(Double.parseDouble(work.getPa_intensity()));
                        row.getCell(15).setCellStyle(cellStyle4);
                        row.createCell(16).setCellValue(Double.parseDouble(work.getTz_intensity()));
                        row.getCell(16).setCellStyle(cellStyle4);

                        if (work.getOutsource().equals("f")) {
                            sum_intensity = Double.parseDouble(work.getStage_recieve()) + Double.parseDouble(work.getStage_execute()) +
                                    Double.parseDouble(work.getStage_escort()) + Double.parseDouble(work.getStage_check()) +
                                    Double.parseDouble(work.getStage_approve());
                            count_spent += sum_intensity;
                        } else {
                            sum_intensity = Double.parseDouble(work.getStage_recieve()) + Double.parseDouble(work.getPa_intensity()) +
                                    Double.parseDouble(work.getStage_escort()) + Double.parseDouble(work.getStage_check()) +
                                    Double.parseDouble(work.getStage_approve());
                            count_spent += sum_intensity - Double.parseDouble(work.getPa_intensity());
                        }

                        row.createCell(17).setCellValue(sum_intensity);
                        row.getCell(17).setCellStyle(cellStyle4);

                        if (sum_intensity != 0) {
                            row.createCell(18).setCellValue(Double.parseDouble(work.getTz_intensity()) / sum_intensity);
                        } else {
                            row.createCell(18).setCellValue(0);
                        }

                        if ((sum_intensity != 0) && ((Double.parseDouble(work.getTz_intensity()) / sum_intensity) >= 1)) {
                            row.getCell(18).setCellStyle(cellStyle16);
                        } else if ((sum_intensity != 0) && ((Double.parseDouble(work.getTz_intensity()) / sum_intensity) < 1) &&
                                ((Double.parseDouble(work.getTz_intensity()) / sum_intensity) > 0)) {
                            row.getCell(18).setCellStyle(cellStyle13);
                        } else if ((sum_intensity == 0) || (Double.parseDouble(work.getTz_intensity()) == 0)) {
                            row.getCell(18).setCellStyle(cellStyle2);
                        }

                        delta = Double.parseDouble(work.getTz_intensity()) - sum_intensity;

                        row.createCell(19).setCellValue(delta);
                        if (delta > 0) {
                            row.getCell(19).setCellStyle(cellStyle4);
                        } else {
                            row.getCell(19).setCellStyle(cellStyle6);
                        }

                        count_pa_intensity += Double.parseDouble(work.getPa_intensity());
                        count_tz_intensity += Double.parseDouble(work.getTz_intensity());
                        count_recieve += Double.parseDouble(work.getStage_recieve());
                        count_execute += Double.parseDouble(work.getStage_execute());
                        count_escort += Double.parseDouble(work.getStage_escort());
                        count_check += Double.parseDouble(work.getStage_check());
                        count_approve += Double.parseDouble(work.getStage_approve());
                        /*count_spent += sum_intensity;*/
                        count_delta += delta;

                        i++;
                    }

                    Row row = nextSheet.createRow(i + 1);
                    row.createCell(5).setCellValue(TOTAL);
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
                    row.createCell(12).setCellValue("-");
                    row.getCell(12).setCellStyle(cellStyle);
                    row.createCell(13).setCellValue("-");
                    row.getCell(13).setCellStyle(cellStyle);
                    row.createCell(14).setCellValue("-");
                    row.getCell(14).setCellStyle(cellStyle);
                    row.createCell(15).setCellValue(count_pa_intensity);
                    row.getCell(15).setCellStyle(cellStyle4);
                    row.createCell(16).setCellValue(count_tz_intensity);
                    row.getCell(16).setCellStyle(cellStyle4);
                    row.createCell(17).setCellValue(count_spent);
                    row.getCell(17).setCellStyle(cellStyle4);

                    if (count_spent != 0) {
                        row.createCell(18).setCellValue(count_tz_intensity / count_spent);
                    } else {
                        row.createCell(18).setCellValue(0);
                    }

                    if ((count_spent != 0) && (count_tz_intensity / count_spent >= 1)) {
                        row.getCell(18).setCellStyle(cellStyle16);
                    } else if ((count_spent != 0) && (count_tz_intensity / count_spent < 1) && (count_tz_intensity / count_spent > 0)) {
                        row.getCell(18).setCellStyle(cellStyle13);
                    } else if ((count_spent == 0) || (count_tz_intensity == 0)) {
                        row.getCell(18).setCellStyle(cellStyle2);
                    }

                    row.createCell(19).setCellValue(count_delta);
                    row.getCell(19).setCellStyle(cellStyle4);

                    Row firstSheetRow = sheet.createRow(j);
                    firstSheetRow.createCell(1).setCellValue(j - 1);
                    firstSheetRow.getCell(1).setCellStyle(cellStyle);
                    firstSheetRow.createCell(2).setCellValue("" + customer);
                    firstSheetRow.getCell(2).setCellStyle(cellStyle5);
                    firstSheetRow.createCell(3).setCellValue("" + contractNumber);
                    firstSheetRow.getCell(3).setCellStyle(cellStyle5);
                    firstSheetRow.createCell(4).setCellValue(count_recieve);
                    firstSheetRow.getCell(4).setCellStyle(cellStyle2);
                    firstSheetRow.createCell(5).setCellValue(count_execute);
                    firstSheetRow.getCell(5).setCellStyle(cellStyle2);
                    firstSheetRow.createCell(6).setCellValue(count_escort);
                    firstSheetRow.getCell(6).setCellStyle(cellStyle2);
                    firstSheetRow.createCell(7).setCellValue(count_check);
                    firstSheetRow.getCell(7).setCellStyle(cellStyle2);
                    firstSheetRow.createCell(8).setCellValue(count_approve);
                    firstSheetRow.getCell(8).setCellStyle(cellStyle2);
                    firstSheetRow.createCell(9).setCellValue("-");
                    firstSheetRow.getCell(9).setCellStyle(cellStyle);
                    firstSheetRow.createCell(10).setCellValue(count_pa_intensity);
                    firstSheetRow.getCell(10).setCellStyle(cellStyle4);
                    firstSheetRow.createCell(11).setCellValue(count_tz_intensity);
                    firstSheetRow.getCell(11).setCellStyle(cellStyle4);
                    firstSheetRow.createCell(12).setCellValue(count_spent);
                    firstSheetRow.getCell(12).setCellStyle(cellStyle4);
                    firstSheetRow.createCell(13).setCellValue(count_outsource);
                    firstSheetRow.getCell(13).setCellStyle(cellStyle4);

                    firstSheetRow.createCell(15).setCellValue(count_delta);
                    firstSheetRow.getCell(15).setCellStyle(cellStyle4);

                    j++;

                    count_full_fact += count_spent;
                    count_full_outsource += count_outsource;
                    count_full_delta += count_delta;

                    counter += 1;
                    updateProgress(counter, size);
                    Thread.sleep(20);
                }

                counter += 1;
                updateProgress(counter, size);
                Thread.sleep(20);

                try {
                    userState.fillUserResourceReportCollectionDB(generatePeriod());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                double count_project = 0;
                double count_adm = 0;
                double count_idle = 0;
                double count_boln = 0;
                double count_study = 0;
                double count_otpusk = 0;

                double count_request = 0;

                int i = 1;
                int p = 2;

                Sheet userSheet = wb.getSheetAt(1);
                Sheet reqSheet = wb.getSheetAt(2);

                Row rowHead = userSheet.getRow(1);
                Row rowReqHead = reqSheet.getRow(1);

                int o = 0;
                int r = 0;

                for (int l = 0; l < dataProjects.size(); l++) {
                    rowHead.createCell(l + 4).setCellValue(dataProjects.get(l).get(0).toString().replace("[", "").replace("]", "") + "\n" +
                            dataProjects.get(l).get(1).toString().replace("[", "").replace("]", "") + "\n" +
                            dataProjects.get(l).get(2).toString().replace("[", "").replace("]", ""));
                    rowHead.getCell(l + 4).setCellStyle(cellStyle15);
                    o = l;
                }

                for (int l = 0; l < dataRequests.size(); l++) {
                    rowReqHead.createCell(l + 4).setCellValue(dataRequests.get(l).get(0).toString().replace("[", "").replace("]", "") + "\n" +
                            dataRequests.get(l).get(1).toString().replace("[", "").replace("]", "") + "\n" +
                            dataRequests.get(l).get(2).toString().replace("[", "").replace("]", ""));
                    rowReqHead.getCell(l + 4).setCellStyle(cellStyle15);
                    r = l;
                }

                rowHead.createCell(o + 5).setCellValue("Проектная\nработа");
                rowHead.getCell(o + 5).setCellStyle(cellStyle15);
                rowHead.createCell(o + 6).setCellValue("Работы по бек-\nофису");
                rowHead.getCell(o + 6).setCellStyle(cellStyle15);
                rowHead.createCell(o + 7).setCellValue("Больничный");
                rowHead.getCell(o + 7).setCellStyle(cellStyle15);
                rowHead.createCell(o + 8).setCellValue("Обучение");
                rowHead.getCell(o + 8).setCellStyle(cellStyle15);
                rowHead.createCell(o + 9).setCellValue("Отпуск");
                rowHead.getCell(o + 9).setCellStyle(cellStyle15);
                rowHead.createCell(o + 10).setCellValue("IDLE");
                rowHead.getCell(o + 10).setCellStyle(cellStyle15);
                rowHead.createCell(o + 11).setCellValue(TOTAL);
                rowHead.getCell(o + 11).setCellStyle(cellStyle15);

                if (!dataRequests.isEmpty()) {
                    rowReqHead.createCell(r + 5).setCellValue(TOTAL);
                    rowReqHead.getCell(r + 5).setCellStyle(cellStyle15);
                } else {
                    rowReqHead.createCell(r + 4).setCellValue(TOTAL);
                    rowReqHead.getCell(r + 4).setCellStyle(cellStyle15);
                }

                if (MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                    dataActivity("SELECT u.user_id_number, p.project_id, sum(daily_intensity) FROM public.daily_work dw " +
                            "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "join public.stage s on s.stage_id = sd.stage_id " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.project p on p.project_id = t.project_id " +
                            "join public.project_manager pm on pm.project_id = p.project_id " +
                            "WHERE " + period + " AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who + "') " +
                            "GROUP BY u.user_id_number, p.project_id");

                    dataActivityReq("SELECT u.user_id_number, r.request_id, sum(daily_intensity) FROM public.daily_work dw " +
                            "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "join public.stage s on s.stage_id = sd.stage_id " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.project p on p.project_id = t.project_id " +
                            "join public.project_manager pm on pm.project_id = p.project_id " +
                            "join public.request r on r.request_id = t.request_id " +
                            "WHERE " + period + " AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who + "') " +
                            "GROUP BY u.user_id_number, r.request_id");
                } else {

                    dataActivity("SELECT u.user_id_number, p.project_id, sum(daily_intensity) FROM public.daily_work dw " +
                            "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "join public.stage s on s.stage_id = sd.stage_id " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.project p on p.project_id = t.project_id " +
                            "WHERE " + period + " " +
                            "GROUP BY u.user_id_number, p.project_id");

                    dataActivityReq("SELECT u.user_id_number, r.request_id, sum(daily_intensity) FROM public.daily_work dw " +
                            "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                            "join public.user u on u.user_id = dw.user_id " +
                            "join public.stage s on s.stage_id = sd.stage_id " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.request r on r.request_id = t.request_id " +
                            "WHERE " + period + " " +
                            "GROUP BY u.user_id_number, r.request_id");
                }

                for (UserState userState : userState.getUserstatelist()) {

                    Row row = userSheet.createRow(p);
                    Row rowReq = reqSheet.createRow(p);

                    for (int l = 0; l < dataProjects.size(); l++) {

                        for (int f = 0; f < dataActivity.size(); f++) {

                            if (dataActivity.get(f).get(0).toString().equals(userState.getUser_id()) &
                                    dataActivity.get(f).get(1).toString().equals(dataProjects.get(l).get(3).toString())) {
                                row.createCell(l + 4).setCellValue(Double.parseDouble(dataActivity.get(f).get(2).toString()));
                                row.getCell(l + 4).setCellStyle(cellStyle16);
                                break;
                            } else if (f == dataActivity.size() - 1) {
                                row.createCell(l + 4).setCellValue("-");
                                row.getCell(l + 4).setCellStyle(cellStyle2);
                            }
                        }
                    }

                    for (int l = 0; l < dataRequests.size(); l++) {

                        for (int f = 0; f < dataActivityReq.size(); f++) {

                            if (dataActivityReq.get(f).get(0).toString().equals(userState.getUser_id()) &
                                    dataActivityReq.get(f).get(1).toString().equals(dataRequests.get(l).get(3).toString())) {
                                rowReq.createCell(l + 4).setCellValue(Double.parseDouble(dataActivityReq.get(f).get(2).toString()));
                                rowReq.getCell(l + 4).setCellStyle(cellStyle16);
                                break;
                            } else if (f == dataActivityReq.size() - 1) {
                                rowReq.createCell(l + 4).setCellValue("-");
                                rowReq.getCell(l + 4).setCellStyle(cellStyle2);
                            }
                        }
                    }

                    row.createCell(1).setCellValue(i);
                    row.getCell(1).setCellStyle(cellStyle);
                    row.createCell(2).setCellValue(userState.getUser_id());
                    row.getCell(2).setCellStyle(cellStyle4);
                    row.createCell(3).setCellValue(userState.getUser_fio());
                    row.getCell(3).setCellStyle(cellStyle5);

                    rowReq.createCell(1).setCellValue(i);
                    rowReq.getCell(1).setCellStyle(cellStyle);
                    rowReq.createCell(2).setCellValue(userState.getUser_id());
                    rowReq.getCell(2).setCellStyle(cellStyle4);
                    rowReq.createCell(3).setCellValue(userState.getUser_fio());
                    rowReq.getCell(3).setCellStyle(cellStyle5);

                    row.createCell(o + 5).setCellValue(Double.parseDouble(userState.getStage_project()));
                    row.getCell(o + 5).setCellStyle(cellStyle2);
                    if (!dataRequests.isEmpty()) {
                        rowReq.createCell(r + 5).setCellValue(Double.parseDouble(userState.getStage_request()));
                        rowReq.getCell(r + 5).setCellStyle(cellStyle2);
                    } else {
                        rowReq.createCell(r + 4).setCellValue(Double.parseDouble(userState.getStage_request()));
                        rowReq.getCell(r + 4).setCellStyle(cellStyle2);
                    }

                    row.createCell(o + 6).setCellValue(Double.parseDouble(userState.getStage_adm()));
                    row.getCell(o + 6).setCellStyle(cellStyle2);
                    row.createCell(o + 7).setCellValue(Double.parseDouble(userState.getStage_boln()));
                    row.getCell(o + 7).setCellStyle(cellStyle2);
                    row.createCell(o + 8).setCellValue(Double.parseDouble(userState.getStage_study()));
                    row.getCell(o + 8).setCellStyle(cellStyle2);
                    row.createCell(o + 9).setCellValue(Double.parseDouble(userState.getStage_otpusk()));
                    row.getCell(o + 9).setCellStyle(cellStyle2);
                    row.createCell(o + 10).setCellValue(Double.parseDouble(userState.getStage_idle()));
                    row.getCell(o + 10).setCellStyle(cellStyle2);

                    row.createCell(o + 11).setCellValue(Double.parseDouble(userState.getStage_project())
                            + Double.parseDouble(userState.getStage_adm())
                            + Double.parseDouble(userState.getStage_boln())
                            + Double.parseDouble(userState.getStage_study())
                            + Double.parseDouble(userState.getStage_otpusk())
                            + Double.parseDouble(userState.getStage_idle()));
                    row.getCell(o + 11).setCellStyle(cellStyle2);

                    p++;

                    count_project += Double.parseDouble(userState.getStage_project());
                    count_adm += Double.parseDouble(userState.getStage_adm());
                    count_idle += Double.parseDouble(userState.getStage_idle());
                    count_boln += Double.parseDouble(userState.getStage_boln());
                    count_study += Double.parseDouble(userState.getStage_study());
                    count_otpusk += Double.parseDouble(userState.getStage_otpusk());

                    count_request += Double.parseDouble(userState.getStage_request());

                    i++;
                }

                count_full_fact += count_adm + count_idle + count_boln + count_study + count_otpusk;
                count_full_delta += -count_adm + -count_idle + -count_boln + -count_study + -count_otpusk;

                dataActivity.clear();
                dataActivityReq.clear();

                if (MainController.role.equalsIgnoreCase(SUPER_USER) || MainController.role.equalsIgnoreCase(ADMIN) ||
                        MainController.role.equalsIgnoreCase("АУП")) {
                    dataActivity(
                            "SELECT c.customer_name, ct.contract_number, p.project_name, p.project_id, sum(daily_intensity) FROM public.daily_work dw " +
                                    "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                                    "join public.stage s on s.stage_id = sd.stage_id " +
                                    "join public.task t on t.task_id = s.task_id " +
                                    "join public.project p on p.project_id = t.project_id " +
                                    "join public.customer c on c.customer_id = p.customer_id " +
                                    "join public.contract_project cp on cp.project_id = p.project_id " +
                                    "join public.contract ct on ct.contract_id = cp.contract_id " +
                                    "WHERE " + period + " " +
                                    "GROUP BY c.customer_name, ct.contract_number, p.project_name, p.project_id " +
                                    "ORDER BY c.customer_name, ct.contract_number");

                    dataActivityReq("SELECT c.customer_name, ct.contract_number, r.request_number, r.request_id, sum(daily_intensity), " +
                            "CASE WHEN r.request_number~E'^\\d+$' THEN CAST (r.request_number AS INTEGER) " +
                            "ELSE " +
                            "0 " +
                            "END as sort FROM public.daily_work dw " +
                            "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                            "join public.stage s on s.stage_id = sd.stage_id " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.request r on r.request_id = t.request_id " +
                            "join public.contract ct on ct.contract_id = r.contract_id " +
                            "join public.customer c on c.customer_id = ct.customer_id " +
                            "WHERE " + period + " " +
                            "GROUP BY c.customer_name, ct.contract_number, r.request_number, r.request_id " +
                            "ORDER BY c.customer_name, ct.contract_number, sort");
                } else if (MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                    dataActivity(
                            "SELECT c.customer_name, ct.contract_number, p.project_name, p.project_id, sum(daily_intensity) FROM public.daily_work dw " +
                                    "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                                    "join public.stage s on s.stage_id = sd.stage_id " +
                                    "join public.task t on t.task_id = s.task_id " +
                                    "join public.project p on p.project_id = t.project_id " +
                                    "join public.project_manager pm on pm.project_id = p.project_id " +
                                    "join public.customer c on c.customer_id = p.customer_id " +
                                    "join public.contract_project cp on cp.project_id = p.project_id " +
                                    "join public.contract ct on ct.contract_id = cp.contract_id " +
                                    "WHERE " + period + " AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +
                                    "') " +
                                    "GROUP BY c.customer_name, ct.contract_number, p.project_name, p.project_id " +
                                    "ORDER BY c.customer_name, ct.contract_number");

                    dataActivityReq("SELECT c.customer_name, ct.contract_number, r.request_number, r.request_id, sum(daily_intensity), " +
                            "CASE WHEN r.request_number~E'^\\d+$' THEN CAST (r.request_number AS INTEGER) " +
                            "ELSE " +
                            "0 " +
                            "END as sort FROM public.daily_work dw " +
                            "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                            "join public.stage s on s.stage_id = sd.stage_id " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.project p on p.project_id = t.project_id " +
                            "join public.project_manager pm on pm.project_id = p.project_id " +
                            "join public.request r on r.request_id = t.request_id " +
                            "join public.contract ct on ct.contract_id = r.contract_id " +
                            "join public.customer c on c.customer_id = ct.customer_id " +
                            "WHERE " + period + " AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who + "') " +
                            "GROUP BY c.customer_name, ct.contract_number, r.request_number, r.request_id " +
                            "ORDER BY c.customer_name, ct.contract_number, sort");
                } else if (MainController.role.equalsIgnoreCase(DEPARTMENT_HEAD) || MainController.role.equalsIgnoreCase(LEAD_SPECIALIST)) {
                    dataActivity(
                            "SELECT c.customer_name, ct.contract_number, p.project_name, p.project_id, sum(daily_intensity) FROM public.daily_work dw " +
                                    "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                                    "join public.stage s on s.stage_id = sd.stage_id " +
                                    "join public.task t on t.task_id = s.task_id " +
                                    "join public.project p on p.project_id = t.project_id " +
                                    "join public.customer c on c.customer_id = p.customer_id " +
                                    "join public.contract_project cp on cp.project_id = p.project_id " +
                                    "join public.contract ct on ct.contract_id = cp.contract_id " +
                                    "join public.user u on u.user_id = dw.user_id " +
                                    "WHERE " + period + " AND " +
                                    "u.user_id IN (SELECT ur.user_id FROM public.user ur " +
                                    "left join public.user_subordination us on us.user_sub_id = ur.user_id " +
                                    "WHERE (us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +
                                    "') OR ur.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who + "')) " +
                                    "GROUP BY ur.user_id HAVING count(ur.user_id) >= 1) " +
                                    "GROUP BY c.customer_name, ct.contract_number, p.project_name, p.project_id " +
                                    "ORDER BY c.customer_name, ct.contract_number");

                    dataActivityReq(
                            "SELECT c.customer_name, ct.contract_number, r.request_number, r.request_id, sum(daily_intensity) FROM public.daily_work dw " +
                                    "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                                    "join public.stage s on s.stage_id = sd.stage_id " +
                                    "join public.task t on t.task_id = s.task_id " +
                                    "join public.request r on r.request_id = t.request_id " +
                                    "join public.contract ct on ct.contract_id = r.contract_id " +
                                    "join public.customer c on c.customer_id = ct.customer_id " +
                                    "join public.user u on u.user_id = dw.user_id " +
                                    "WHERE " + period + " AND " +
                                    "u.user_id IN (SELECT ur.user_id FROM public.user ur " +
                                    "left join public.user_subordination us on us.user_sub_id = ur.user_id " +
                                    "WHERE (us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +
                                    "') OR ur.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who + "')) " +
                                    "GROUP BY ur.user_id HAVING count(ur.user_id) >= 1) " +
                                    "GROUP BY c.customer_name, ct.contract_number, r.request_number, r.request_id " +
                                    "ORDER BY c.customer_name, ct.contract_number");
                } else {
                    dataActivity("");

                    dataActivityReq("");
                }

                Row project = userSheet.createRow(p + 1);
                Row request = reqSheet.createRow(p + 1);

                for (int k = 0; k < dataProjects.size(); k++) {

                    project.createCell(k + 4).setCellValue("-");
                    project.getCell(k + 4).setCellStyle(cellStyle2);

                    for (ObservableList observableList : dataActivity) {

                        if (observableList.get(3).equals(dataProjects.get(k).get(3))) {
                            project.getCell(k + 4).setCellValue(Double.parseDouble(observableList.get(4).toString()));
                            project.getCell(k + 4).setCellStyle(cellStyle2);
                            break;
                        }
                    }

                    userSheet.autoSizeColumn(k + 4);
                }

                for (int k = 0; k < dataRequests.size(); k++) {

                    request.createCell(k + 4).setCellValue("-");
                    request.getCell(k + 4).setCellStyle(cellStyle2);

                    for (ObservableList observableList : dataActivityReq) {

                        if (observableList.get(3).equals(dataRequests.get(k).get(3))) {
                            request.getCell(k + 4).setCellValue(Double.parseDouble(observableList.get(4).toString()));
                            request.getCell(k + 4).setCellStyle(cellStyle2);
                            break;
                        }
                    }

                    reqSheet.autoSizeColumn(k + 4);
                }

                counter += 1;
                updateProgress(counter, size);
                Thread.sleep(20);

                project.createCell(o + 5).setCellValue(count_project);
                project.getCell(o + 5).setCellStyle(cellStyle2);

                if (!dataRequests.isEmpty()) {
                    request.createCell(r + 5).setCellValue(count_request);
                    request.getCell(r + 5).setCellStyle(cellStyle2);
                } else {
                    request.createCell(r + 4).setCellValue(count_request);
                    request.getCell(r + 4).setCellStyle(cellStyle2);
                }

                project.createCell(o + 6).setCellValue(count_adm);
                project.getCell(o + 6).setCellStyle(cellStyle2);
                project.createCell(o + 7).setCellValue(count_boln);
                project.getCell(o + 7).setCellStyle(cellStyle2);
                project.createCell(o + 8).setCellValue(count_study);
                project.getCell(o + 8).setCellStyle(cellStyle2);
                project.createCell(o + 9).setCellValue(count_otpusk);
                project.getCell(o + 9).setCellStyle(cellStyle2);
                project.createCell(o + 10).setCellValue(count_idle);
                project.getCell(o + 10).setCellStyle(cellStyle2);
                project.createCell(o + 11).setCellValue(count_project
                        + count_adm
                        + count_idle
                        + count_boln
                        + count_study
                        + count_otpusk);
                project.getCell(o + 11).setCellStyle(cellStyle2);

                userSheet.autoSizeColumn(1);
                userSheet.autoSizeColumn(2);
                userSheet.autoSizeColumn(3);

                reqSheet.autoSizeColumn(1);
                reqSheet.autoSizeColumn(2);
                reqSheet.autoSizeColumn(3);

                userSheet.autoSizeColumn(o + 5);

                if (!dataRequests.isEmpty()) {
                    reqSheet.autoSizeColumn(r + 5);
                } else {
                    reqSheet.autoSizeColumn(r + 4);
                }

                userSheet.autoSizeColumn(o + 6);
                userSheet.autoSizeColumn(o + 7);
                userSheet.autoSizeColumn(o + 8);
                userSheet.autoSizeColumn(o + 9);
                userSheet.autoSizeColumn(o + 10);
                userSheet.autoSizeColumn(o + 11);

                Row adm = sheet.createRow(j + 1);
                adm.createCell(11).setCellValue("Работы по бэк-офису");
                adm.getCell(11).setCellStyle(cellStyle14);
                adm.createCell(12).setCellValue(count_adm);
                adm.getCell(12).setCellStyle(cellStyle4);
                adm.createCell(15).setCellValue(0 - count_adm);
                adm.getCell(15).setCellStyle(cellStyle4);

                Row boln = sheet.createRow(j + 2);
                boln.createCell(11).setCellValue("Больничный");
                boln.getCell(11).setCellStyle(cellStyle14);
                boln.createCell(12).setCellValue(count_boln);
                boln.getCell(12).setCellStyle(cellStyle4);
                boln.createCell(15).setCellValue(0 - count_boln);
                boln.getCell(15).setCellStyle(cellStyle4);

                Row study = sheet.createRow(j + 3);
                study.createCell(11).setCellValue("Обучение");
                study.getCell(11).setCellStyle(cellStyle14);
                study.createCell(12).setCellValue(count_study);
                study.getCell(12).setCellStyle(cellStyle4);
                study.createCell(15).setCellValue(0 - count_study);
                study.getCell(15).setCellStyle(cellStyle4);

                Row otpusk = sheet.createRow(j + 4);
                otpusk.createCell(11).setCellValue("Отпуск");
                otpusk.getCell(11).setCellStyle(cellStyle14);
                otpusk.createCell(12).setCellValue(count_otpusk);
                otpusk.getCell(12).setCellStyle(cellStyle4);
                otpusk.createCell(15).setCellValue(0 - count_otpusk);
                otpusk.getCell(15).setCellStyle(cellStyle4);

                Row idle = sheet.createRow(j + 5);
                idle.createCell(11).setCellValue("IDLE");
                idle.getCell(11).setCellStyle(cellStyle14);
                idle.createCell(12).setCellValue(count_idle);
                idle.getCell(12).setCellStyle(cellStyle4);
                idle.createCell(15).setCellValue(0 - count_idle);
                idle.getCell(15).setCellStyle(cellStyle4);

                Row itog = sheet.createRow(j + 7);
                itog.createCell(11).setCellValue(TOTAL);
                itog.getCell(11).setCellStyle(cellStyle14);
                itog.createCell(12).setCellValue(count_full_fact);
                itog.getCell(12).setCellStyle(cellStyle4);
                itog.createCell(13).setCellValue(count_full_outsource);
                itog.getCell(13).setCellStyle(cellStyle4);
                itog.createCell(15).setCellValue(count_full_delta);
                itog.getCell(15).setCellStyle(cellStyle4);

                sheet.autoSizeColumn(1);
                sheet.autoSizeColumn(2);
                sheet.autoSizeColumn(3);
                sheet.autoSizeColumn(4);
                sheet.autoSizeColumn(5);
                sheet.autoSizeColumn(6);
                sheet.autoSizeColumn(7);
                sheet.autoSizeColumn(8);
                sheet.autoSizeColumn(9);
                sheet.autoSizeColumn(10);
                sheet.autoSizeColumn(11);
                sheet.autoSizeColumn(12);
                sheet.autoSizeColumn(13);
                sheet.autoSizeColumn(15);

                counter += 1;
                updateProgress(counter, size);
                Thread.sleep(20);

                FileOutputStream fileOut = null;

                if (reportSwitch.isSelected()) {
                    fileOut = new FileOutputStream("Ресурсный_отчет_на_" + endDate.getValue() + ".xls");
                }
                if (reportSwitchForYear.isSelected()) {
                    fileOut = new FileOutputStream("Ресурсный_отчет_на_" + yearBox.getValue() + "_год" + ".xls");
                }
                if (reportSwitchForHalfYear.isSelected()) {
                    fileOut = new FileOutputStream("Ресурсный_отчет_на_" + halfyearBox.getValue() + "_" + yearBox.getValue() + "_года" + ".xls");
                }
                if (reportSwitchForQuarter.isSelected()) {
                    fileOut = new FileOutputStream("Ресурсный_отчет_на_" + quarterBox.getValue() + "_" + yearBox.getValue() + "_года" + ".xls");
                }
                if (reportSwitchForMonth.isSelected()) {
                    fileOut = new FileOutputStream("Ресурсный_отчет_на_" + monthBox.getValue() + "_" + yearBox.getValue() + "_года" + ".xls");
                }
                if (reportSwitchForPeriod.isSelected()) {
                    fileOut = new FileOutputStream("Ресурсный_отчет_c_" + startDate.getValue() + "_по_" + endDate.getValue() + ".xls");
                }
                wb.write(fileOut);
                fileOut.close();
                inp.close();

                counter += 1;
                updateProgress(counter, size);
                Thread.sleep(20);

                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        txtLabel.setText("");

                        if (approveSwitch.isSelected()) {

                            if (MainController.role.equalsIgnoreCase(SUPER_USER) || MainController.role.equalsIgnoreCase(ADMIN) ||
                                    MainController.role.equalsIgnoreCase("АУП")) {
                                dataApproved(
                                        "SELECT u.user_fullname, to_char(dw.daily_work_date, 'MM-YYYY'), ca.sheet_month, ca.sheet_year, ca.lead_approve, ca.dep_head_approve, ca.manager_approve FROM public.user u " +
                                                "join public.daily_work dw on dw.user_id = u.user_id " +
                                                "left join public.current_approve ca on ca.user_id = u.user_id AND ca.sheet_month = to_char(dw.daily_work_date, 'MM') AND ca.sheet_year = to_char(dw.daily_work_date, 'YYYY') " +
                                                "WHERE " + generatePeriod() + " " +
                                                "GROUP BY u.user_fullname, to_char(dw.daily_work_date, 'MM-YYYY'), ca.sheet_month, ca.sheet_year, ca.lead_approve, ca.dep_head_approve, ca.manager_approve " +
                                                "ORDER BY u.user_fullname");
                            } else if (MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                                dataApproved(
                                        "SELECT u.user_fullname, to_char(dw.daily_work_date, 'MM-YYYY'), ca.sheet_month, ca.sheet_year, ca.lead_approve, ca.dep_head_approve, ca.manager_approve FROM public.user u " +
                                                "join public.daily_work dw on dw.user_id = u.user_id " +
                                                "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                                                "join public.stage s on s.stage_id = sd.stage_id " +
                                                "join public.task t on t.task_id = s.task_id " +
                                                "join public.project p on p.project_id = t.project_id " +
                                                "join public.project_manager pm on pm.project_id = p.project_id " +
                                                "left join public.current_approve ca on ca.user_id = u.user_id AND ca.sheet_month = to_char(dw.daily_work_date, 'MM') AND ca.sheet_year = to_char(dw.daily_work_date, 'YYYY') " +
                                                "WHERE " + generatePeriod() + " AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" +
                                                MainController.who + "') " +
                                                "GROUP BY u.user_fullname, to_char(dw.daily_work_date, 'MM-YYYY'), ca.sheet_month, ca.sheet_year, ca.lead_approve, ca.dep_head_approve, ca.manager_approve " +
                                                "ORDER BY u.user_fullname");
                            } else if (MainController.role.equalsIgnoreCase(DEPARTMENT_HEAD) || MainController.role.equalsIgnoreCase(LEAD_SPECIALIST)) {
                                dataApproved(
                                        "SELECT u.user_fullname, to_char(dw.daily_work_date, 'MM-YYYY'), ca.sheet_month, ca.sheet_year, ca.lead_approve, ca.dep_head_approve, ca.manager_approve FROM public.user u " +
                                                "join public.daily_work dw on dw.user_id = u.user_id " +
                                                "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                                                "left join public.current_approve ca on ca.user_id = u.user_id AND ca.sheet_month = to_char(dw.daily_work_date, 'MM') AND ca.sheet_year = to_char(dw.daily_work_date, 'YYYY') " +
                                                "WHERE " + generatePeriod() + " AND " +
                                                "(us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +
                                                "') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who + "')) " +
                                                "GROUP BY u.user_fullname, to_char(dw.daily_work_date, 'MM-YYYY'), ca.sheet_month, ca.sheet_year, ca.lead_approve, ca.dep_head_approve, ca.manager_approve " +
                                                "ORDER BY u.user_fullname");
                            } else {
                                dataApproved("");
                            }

                            for (ObservableList observableList : dataApproved) {

                                if (observableList.get(2) == null || observableList.get(4).equals("f") || observableList.get(5).equals("f") ||
                                        observableList.get(6).equals("f")) {
                                    notApproved = notApproved + "\n" + observableList.get(0) + " за " + observableList.get(1);
                                }
                            }

                            TextArea textArea = new TextArea(notApproved);
                            textArea.setEditable(false);
                            textArea.setWrapText(true);

                            textArea.setMaxWidth(Double.MAX_VALUE);
                            textArea.setMaxHeight(Double.MAX_VALUE);
                            GridPane.setVgrow(textArea, Priority.ALWAYS);
                            GridPane.setHgrow(textArea, Priority.ALWAYS);

                            GridPane expContent = new GridPane();
                            expContent.setMaxWidth(Double.MAX_VALUE);
                            expContent.add(textArea, 0, 1);

                            warningAlert.setTitle("Неутвержденные данные");
                            warningAlert.setHeaderText(null);
                            warningAlert.setContentText(
                                    "В отчете присутствуют данные, не проверенные ответственным руководителем. Отчет будет открыт после закрытия данного окна.\n\nДля просмотра деталей разверните форму ниже:");
                            warningAlert.getDialogPane().setExpandableContent(expContent);
                            warningAlert.showAndWait();
                            notApproved = "";
                            textArea.setText("");

                            if (MainController.role.equalsIgnoreCase(SUPER_USER) || MainController.role.equalsIgnoreCase(ADMIN) ||
                                    MainController.role.equalsIgnoreCase("АУП")) {
                                dataApproved(
                                        "SELECT u.user_fullname, to_char(dw.daily_work_date, 'MM'), to_char(dw.daily_work_date, 'YYYY') FROM public.user u " +
                                                "join public.daily_work dw on dw.user_id = u.user_id " +
                                                "join public.user_activity ua on ua.user_activity_id = u.user_Activity_id " +
                                                "WHERE " + generatePeriod() + " AND ua.user_activity_name = 'Активен' " +
                                                "GROUP BY u.user_fullname, to_char(dw.daily_work_date, 'MM'), to_char(dw.daily_work_date, 'YYYY') " +
                                                "ORDER BY u.user_fullname, to_char(dw.daily_work_date, 'YYYY'), to_char(dw.daily_work_date, 'MM')");
                            } else if (MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                                dataApproved(
                                        "SELECT u.user_fullname, to_char(dw.daily_work_date, 'MM'), to_char(dw.daily_work_date, 'YYYY') FROM public.user u " +
                                                "join public.daily_work dw on dw.user_id = u.user_id " +
                                                "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                                                "join public.stage s on s.stage_id = sd.stage_id " +
                                                "join public.task t on t.task_id = s.task_id " +
                                                "join public.project p on p.project_id = t.project_id " +
                                                "join public.project_manager pm on pm.project_id = p.project_id " +
                                                "join public.user_activity ua on ua.user_activity_id = u.user_Activity_id " +
                                                "WHERE " + generatePeriod() +
                                                " AND ua.user_activity_name = 'Активен' AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" +
                                                MainController.who + "') " +
                                                "GROUP BY u.user_fullname, to_char(dw.daily_work_date, 'MM'), to_char(dw.daily_work_date, 'YYYY') " +
                                                "ORDER BY u.user_fullname, to_char(dw.daily_work_date, 'YYYY'), to_char(dw.daily_work_date, 'MM')");
                            } else if (MainController.role.equalsIgnoreCase(DEPARTMENT_HEAD) || MainController.role.equalsIgnoreCase(LEAD_SPECIALIST)) {
                                dataApproved(
                                        "SELECT u.user_fullname, to_char(dw.daily_work_date, 'MM'), to_char(dw.daily_work_date, 'YYYY') FROM public.user u " +
                                                "join public.daily_work dw on dw.user_id = u.user_id " +
                                                "join public.user_activity ua on ua.user_activity_id = u.user_Activity_id " +
                                                "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                                                "left join public.current_approve ca on ca.user_id = u.user_id AND ca.sheet_month = to_char(dw.daily_work_date, 'MM') AND ca.sheet_year = to_char(dw.daily_work_date, 'YYYY') " +
                                                "WHERE " + generatePeriod() + " AND ua.user_activity_name = 'Активен' AND " +
                                                "(us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +
                                                "') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who + "')) " +
                                                "GROUP BY u.user_fullname, to_char(dw.daily_work_date, 'MM'), to_char(dw.daily_work_date, 'YYYY') " +
                                                "ORDER BY u.user_fullname, to_char(dw.daily_work_date, 'YYYY'), to_char(dw.daily_work_date, 'MM')");
                            } else {
                                dataApproved("");
                            }

                            String userTabels = "";

                            for (int k = 0; k < dataApproved.size(); k++) {
                                if (!sdf.format(startDate.getValue()).equals(sdf.format(endDate.getValue()))) {
                                    if (dataApproved.get(k).get(2).toString().equals(sdf.format(startDate.getValue()))) {

                                        if (!userTabels.contains(dataApproved.get(k).get(0).toString())) {
                                            userTabels = dataApproved.get(k).get(0).toString();

                                            for (int i = Integer.parseInt(sdf2.format(startDate.getValue())); i <= 12; i++) {
                                                if (i > 9) {
                                                    userTabels = userTabels + "," + i;
                                                } else {
                                                    userTabels = userTabels + ",0" + i;
                                                }
                                            }

                                            userTabels = userTabels.replace(dataApproved.get(k).get(1).toString(), ",");
                                        } else {
                                            userTabels = userTabels.replace(dataApproved.get(k).get(1).toString(), ",");
                                        }

                                        if (k != dataApproved.size() - 1) {
                                            if (!dataApproved.get(k).get(0).toString().equals(dataApproved.get(k + 1).get(0).toString()) |
                                                    !dataApproved.get(k).get(2).toString().equals(dataApproved.get(k + 1).get(2))) {
                                                notApproved =
                                                        (notApproved + userTabels + "." + dataApproved.get(k).get(2).toString() + "\n").replace(",,", "");
                                            }
                                        } else {
                                            notApproved = (notApproved + userTabels + "." + dataApproved.get(k).get(2).toString() + "\n").replace(",,", "");
                                        }
                                    } else if (dataApproved.get(k).get(2).toString().equals(sdf.format(endDate.getValue()))) {
                                        if (!userTabels.contains(dataApproved.get(k).get(0).toString())) {
                                            userTabels = dataApproved.get(k).get(0).toString();

                                            for (int i = 1; i <= Integer.parseInt(sdf2.format(endDate.getValue())); i++) {
                                                if (i > 9) {
                                                    userTabels = userTabels + "," + i;
                                                } else {
                                                    userTabels = userTabels + ",0" + i;
                                                }
                                            }

                                            userTabels = userTabels.replace(dataApproved.get(k).get(1).toString(), ",");
                                        } else {
                                            userTabels = userTabels.replace(dataApproved.get(k).get(1).toString(), ",");
                                        }

                                        if (k != dataApproved.size() - 1) {
                                            if (!dataApproved.get(k).get(0).toString().equals(dataApproved.get(k + 1).get(0).toString()) |
                                                    !dataApproved.get(k).get(2).toString().equals(dataApproved.get(k + 1).get(2))) {
                                                notApproved =
                                                        (notApproved + userTabels + "." + dataApproved.get(k).get(2).toString() + "\n").replace(",,", "");
                                            }
                                        } else {
                                            notApproved = (notApproved + userTabels + "." + dataApproved.get(k).get(2).toString() + "\n").replace(",,", "");
                                        }
                                    } else {
                                        if (!userTabels.contains(dataApproved.get(k).get(0).toString())) {
                                            userTabels = dataApproved.get(k).get(0).toString();

                                            for (int i = 1; i <= 12; i++) {
                                                if (i > 9) {
                                                    userTabels = userTabels + "," + i;
                                                } else {
                                                    userTabels = userTabels + ",0" + i;
                                                }
                                            }

                                            userTabels = userTabels.replace(dataApproved.get(k).get(1).toString(), ",");
                                        } else {
                                            userTabels = userTabels.replace(dataApproved.get(k).get(1).toString(), ",");
                                        }

                                        if (k != dataApproved.size() - 1) {
                                            if (!dataApproved.get(k).get(0).toString().equals(dataApproved.get(k + 1).get(0).toString()) |
                                                    !dataApproved.get(k).get(2).toString().equals(dataApproved.get(k + 1).get(2))) {
                                                notApproved =
                                                        (notApproved + userTabels + "." + dataApproved.get(k).get(2).toString() + "\n").replace(",,", "");
                                            }
                                        } else {
                                            notApproved = (notApproved + userTabels + "." + dataApproved.get(k).get(2).toString() + "\n").replace(",,", "");
                                        }
                                    }
                                } else {

                                    if (!userTabels.contains(dataApproved.get(k).get(0).toString())) {
                                        userTabels = dataApproved.get(k).get(0).toString();

                                        for (int i = Integer.parseInt(sdf2.format(startDate.getValue()));
                                                i <= Integer.parseInt(sdf2.format(endDate.getValue())); i++) {
                                            if (i > 9) {
                                                userTabels = userTabels + "," + i;
                                            } else {
                                                userTabels = userTabels + ",0" + i;
                                            }
                                        }

                                        userTabels = userTabels.replace(dataApproved.get(k).get(1).toString(), ",");
                                    } else {
                                        userTabels = userTabels.replace(dataApproved.get(k).get(1).toString(), ",");
                                    }

                                    if (k != dataApproved.size() - 1) {
                                        if (!dataApproved.get(k).get(0).toString().equals(dataApproved.get(k + 1).get(0).toString()) |
                                                !dataApproved.get(k).get(2).toString().equals(dataApproved.get(k + 1).get(2))) {
                                            notApproved = (notApproved + userTabels + "." + dataApproved.get(k).get(2).toString() + "\n").replace(",,", "");
                                        }
                                    } else {
                                        notApproved = (notApproved + userTabels + "." + dataApproved.get(k).get(2).toString() + "\n").replace(",,", "");
                                    }
                                }
                            }

                            for (ObservableList observableList : dataApproved) {

                                if (notApproved.contains(observableList.get(0) + "." + sdf.format(startDate.getValue()))) {
                                    notApproved = notApproved.replace(observableList.get(0) + "." + sdf.format(startDate.getValue()) + "\n", "");
                                }

                                if (notApproved.contains(observableList.get(0) + "." + sdf.format(endDate.getValue()))) {
                                    notApproved = notApproved.replace(observableList.get(0) + "." + sdf.format(endDate.getValue()) + "\n", "");
                                }

                                if (notApproved.contains(observableList.get(0) + ",")) {
                                    notApproved = notApproved.replace(observableList.get(0) + ",", observableList.get(0) + " за ");
                                }
                            }

                            textArea.setText(notApproved);

                            warningAlert.setTitle("Пустые табели");
                            warningAlert.setHeaderText(null);
                            warningAlert.setContentText(
                                    "По следующим сотрудникам не все данные заполнены в системе.\n\nДля просмотра деталей разверните форму ниже:");
                            warningAlert.getDialogPane().setExpandableContent(expContent);
                            warningAlert.showAndWait();
                            notApproved = "";
                        }

                        Desktop desktop = null;
                        if (Desktop.isDesktopSupported()) {
                            desktop = Desktop.getDesktop();
                        }

                        try {
                            if (reportSwitch.isSelected()) {
                                desktop.open(new File("Ресурсный_отчет_на_" + endDate.getValue() + ".xls"));
                            }
                            if (reportSwitchForYear.isSelected()) {
                                desktop.open(new File("Ресурсный_отчет_на_" + yearBox.getValue() + "_год" + ".xls"));
                            }
                            if (reportSwitchForHalfYear.isSelected()) {
                                desktop.open(new File("Ресурсный_отчет_на_" + halfyearBox.getValue() + "_" + yearBox.getValue() + "_года" + ".xls"));
                            }
                            if (reportSwitchForQuarter.isSelected()) {
                                desktop.open(new File("Ресурсный_отчет_на_" + quarterBox.getValue() + "_" + yearBox.getValue() + "_года" + ".xls"));
                            }
                            if (reportSwitchForMonth.isSelected()) {
                                desktop.open(new File("Ресурсный_отчет_на_" + monthBox.getValue() + "_" + yearBox.getValue() + " года" + ".xls"));
                            }
                            if (reportSwitchForPeriod.isSelected()) {
                                desktop.open(new File("Ресурсный_отчет_c_" + startDate.getValue() + "_по_" + endDate.getValue() + ".xls"));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }

                        txtLabel.setText("");
                        formClear();
                    }
                });

                return 0;
            }
        };

        genProgress.progressProperty().bind(taskResourceCalculate.progressProperty());
        new Thread(taskResourceCalculate).start();
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
        txtLabel.setText("");
        approveSwitch.setSelected(false);
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
