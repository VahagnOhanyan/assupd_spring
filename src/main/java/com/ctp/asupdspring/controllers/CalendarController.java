package com.ctp.asupdspring.controllers;

import com.ctp.asupdspring.app.repo.CalendarService;
import com.ctp.asupdspring.domain.CalendarEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
@Component
@FxmlView("calendar.fxml")
public class CalendarController {
    @FXML
    private TableView<Months> calendarView;
    @FXML
    private Label yearLabel;
    public static GregorianCalendar date = new GregorianCalendar();
    private Stage stage;
    public static final int START_YEAR = 2018;
    private String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    private ObservableList<Months> list;
    private TableColumn<Months, String>[] tableColumnDays = new TableColumn[31];
    private Calendar c = Calendar.getInstance();
    private Calendar calendar = Calendar.getInstance();
    //private static DBconnection dBconnection;
    private static List<CalendarEntity> res;
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String TYPE = "type";
    @Autowired
    CalendarService calendarService;
    @FXML
    AnchorPane calendarPane;

    @FXML
    private void initialize() {
        stage = new Stage();
        stage.setScene(new Scene(calendarPane));
        stage.setMinHeight(100);
        stage.setMinWidth(100);
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);

        yearLabel.setText(String.valueOf(LocalDate.now().getYear()));
        yearLabel.setTextFill(Color.RED);
        calendarView.getSelectionModel().setCellSelectionEnabled(true);
        calendarView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ObservableList<TableColumn<Months, ?>> columns = calendarView.getColumns();
        TableColumn<Months, String> tableColumn = new TableColumn<>("Месяц");
        list = getMonthList();
        tableColumn.setCellValueFactory(new PropertyValueFactory<>(MONTH));
        tableColumn.setStyle("-fx-alignment: CENTER;");
        tableColumn.setSortable(false);
        calendarView.setItems(list);
        columns.add(tableColumn);
        fillCalendar(LocalDate.now().getYear(), "create");
        Collections.addAll(columns, tableColumnDays);
    }

    public void show() {
        stage.show();
    }

    public ObservableList<Months> getMonthList() {

        Months m1 = new Months(months[0]);
        Months m2 = new Months(months[1]);
        Months m3 = new Months(months[2]);
        Months m4 = new Months(months[3]);
        Months m5 = new Months(months[4]);
        Months m6 = new Months(months[5]);
        Months m7 = new Months(months[6]);
        Months m8 = new Months(months[7]);
        Months m9 = new Months(months[8]);
        Months m10 = new Months(months[9]);
        Months m11 = new Months(months[10]);
        Months m12 = new Months(months[11]);
        return FXCollections.observableArrayList(m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12);
    }

    private void fillCalendar(int year, String action) {
        res = getCalendarData();
        CalendarCell.setRes(res);
        for (int i = 1; i <= tableColumnDays.length; i++) {
            if (action.equals("create")) {
                tableColumnDays[i - 1] = new TableColumn<>("" + i);
                tableColumnDays[i - 1].setSortable(false);
            }
            tableColumnDays[i - 1].setCellValueFactory(new PropertyValueFactory<>("day" + i));
            tableColumnDays[i - 1].setCellFactory(column -> {
                CalendarCell<Months, String> cc = CalendarCell.createCalendarCell();
                cc.setYear(year);
                return cc;
            });
        }
    }

    List<CalendarEntity> getCalendarData() {
        return calendarService.getAllCalendar();
    }

    public void updateCalendarCell(CalendarCell<Months, String> cc, ActionEvent event) {
        res = getCalendarData();
        Object source = event.getSource();
        if (!(source instanceof MenuItem)) {
            return;
        }
        MenuItem menuItem = (MenuItem) source;
        calendar.set(cc.getYear(), cc.getIndex(), Integer.parseInt(cc.getItem()));
        int m = cc.getIndex() + 1;
        boolean deleted = false;
        if (menuItem.getId().equals("1")) {

            for (CalendarEntity re : res) {
                int yyyy = re.getYear();
                int mm = re.getMonth() - 1;
                int dd = re.getDay();
                String type = re.getType();
                if (calendar.get(Calendar.YEAR) == yyyy && calendar.get(Calendar.MONTH) + 1 == mm && calendar.get(Calendar.DAY_OF_MONTH) == dd &&
                        type.equals("workday")) {
                    deleted = true;
                    calendarService.deleteCalendarBy(yyyy, mm, dd, type);
                }
            }

            if (!deleted) {
                calendarService.insertCalendarBy(cc.getYear(), m, Integer.parseInt(cc.getItem()), "holiday");
            }
            if (!cc.getStyle().contains("#ddebf7")) {
                cc.setStyle("-fx-alignment: CENTER;  -fx-background-color:#fff2cc;");
            } else {
                cc.setStyle("-fx-alignment: CENTER;  -fx-background-color:#fce4d6;");
            }
        } else if (menuItem.getId().equals("2")) {
            for (CalendarEntity re : res) {
                int yyyy = re.getYear();
                int mm = re.getMonth() - 1;
                int dd = re.getDay();
                String type = re.getType();
                if (calendar.get(Calendar.YEAR) == yyyy && calendar.get(Calendar.MONTH) + 1 == mm && calendar.get(Calendar.DAY_OF_MONTH) == dd &&
                        type.equals("holiday")) {
                    deleted = true;
                    calendarService.deleteCalendarBy(yyyy, mm, dd, type);
                }
            }

            if (!deleted) {
                calendarService.insertCalendarBy(cc.getYear(), m, Integer.parseInt(cc.getItem()), "workday");
            }
            if (cc.getStyle().contains("#fff2cc")) {
                cc.setStyle("-fx-alignment: CENTER;");
            }
            if (cc.getStyle().contains("#fce4d6")) {
                cc.setStyle("-fx-alignment: CENTER;  -fx-background-color:#ddebf7;");
            }
        }
    }

    public void actionChangeYear(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) {
            return;
        }
        Button clickedButton = (Button) source;
        if (clickedButton.getId().equals("backBtn")) {
            c.set(c.get(Calendar.YEAR) - 1, 1, 1);
            checkLeapYear(c.get(Calendar.YEAR));
            yearLabel.setText(String.valueOf(c.get(Calendar.YEAR)));
            fillCalendar(c.get(Calendar.YEAR), "");
        } else if (clickedButton.getId().equals("forwardBtn")) {
            c.set(c.get(Calendar.YEAR) + 1, 1, 1);
            checkLeapYear(c.get(Calendar.YEAR));
            yearLabel.setText(String.valueOf(c.get(Calendar.YEAR)));
            fillCalendar(c.get(Calendar.YEAR), "");
        }
    }

    private void checkLeapYear(int year) {
        boolean isNextYearLeap = LocalDate.of(year, 1, 1).isLeapYear();
        Months feb = list.get(1);
        if (isNextYearLeap) {
            feb.days[28] = "29";
        } else {
            feb.days[28] = null;
        }
    }
    public static boolean checkDate(int i) {
        Calendar date2 = date;

        date2.set(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH), i);
        int dayofweeks = GregorianCalendar.DAY_OF_WEEK;
        if (date2.get(dayofweeks) == GregorianCalendar.SATURDAY
                || date2.get(dayofweeks) == GregorianCalendar.SUNDAY) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkDateForCurrent(int i) {
        Calendar date3 = date;

        date3.set(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH), i);
        GregorianCalendar currentDate = new GregorianCalendar();
        if ((date3.get(GregorianCalendar.DAY_OF_MONTH) == currentDate.get(GregorianCalendar.DAY_OF_MONTH)) &&
                (date3.get(GregorianCalendar.MONTH) == currentDate.get(GregorianCalendar.MONTH)) &&
                (date3.get(GregorianCalendar.YEAR) == currentDate.get(GregorianCalendar.YEAR))) {
            return true;
        } else {
            return false;
        }
    }
    public static class Months {

        private final String[] days;

        public String getDay1() {
            return days[0];
        }

        public String getDay2() {
            return days[1];
        }

        public String getDay3() {
            return days[2];
        }

        public String getDay4() {
            return days[3];
        }

        public String getDay5() {
            return days[4];
        }

        public String getDay6() {
            return days[5];
        }

        public String getDay7() {
            return days[6];
        }

        public String getDay8() {
            return days[7];
        }

        public String getDay9() {
            return days[8];
        }

        public String getDay10() {
            return days[9];
        }

        public String getDay11() {
            return days[10];
        }

        public String getDay12() {
            return days[11];
        }

        public String getDay13() {
            return days[12];
        }

        public String getDay14() {
            return days[13];
        }

        public String getDay15() {
            return days[14];
        }

        public String getDay16() {
            return days[15];
        }

        public String getDay17() {
            return days[16];
        }

        public String getDay18() {
            return days[17];
        }

        public String getDay19() {
            return days[18];
        }

        public String getDay20() {
            return days[19];
        }

        public String getDay21() {
            return days[20];
        }

        public String getDay22() {
            return days[21];
        }

        public String getDay23() {
            return days[22];
        }

        public String getDay24() {
            return days[23];
        }

        public String getDay25() {
            return days[24];
        }

        public String getDay26() {
            return days[25];
        }

        public String getDay27() {
            return days[26];
        }

        public String getDay28() {
            return days[27];
        }

        public String getDay29() {
            return days[28];
        }

        public String getDay30() {
            return days[29];
        }

        public String getDay31() {
            return days[30];
        }

        public String getMonth() {
            return month;
        }

        String month;

        Months(String month) {
            this.month = month;
            int order = returnMonth(month);
            Calendar cal = Calendar.getInstance();
            cal.set(LocalDate.now().getYear(), order, 1);
            int num = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            ArrayList<String> daysList = new ArrayList<>();
            for (int j = 1; j <= num; j++) {
                daysList.add(String.valueOf(j));
            }
            days = daysList.toArray(new String[31]);
        }
        public static int returnMonth(String month) {
            Integer m = 0;

            switch (month) {
                case "Январь":
                    m = 0;
                    break;
                case "Февраль":
                    m = 1;
                    break;
                case "Март":
                    m = 2;
                    break;
                case "Апрель":
                    m = 3;
                    break;
                case "Май":
                    m = 4;
                    break;
                case "Июнь":
                    m = 5;
                    break;
                case "Июль":
                    m = 6;
                    break;
                case "Август":
                    m = 7;
                    break;
                case "Сентябрь":
                    m = 8;
                    break;
                case "Октябрь":
                    m = 9;
                    break;
                case "Ноябрь":
                    m = 10;
                    break;
                case "Декабрь":
                    m = 11;
                    break;
            }
            return m;
        }
    }


}
