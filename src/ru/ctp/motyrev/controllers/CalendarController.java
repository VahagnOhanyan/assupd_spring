package ru.ctp.motyrev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import ru.ctp.motyrev.code.CalendarCell;
import ru.ctp.motyrev.code.DBconnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class CalendarController {
    @FXML
    private TableView<Months> calendarView;
    @FXML
    private Label yearLabel;

    public static final int START_YEAR = 2018;
    private String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    private ObservableList<Months> list;
    private TableColumn<Months, String>[] tableColumnDays = new TableColumn[31];
    private Calendar c = Calendar.getInstance();
    private Calendar calendar = Calendar.getInstance();
    private static DBconnection dBconnection;
    private static ResultSet res;
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String TYPE = "type";

    @FXML
    private void initialize() {
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

    private ObservableList<Months> getMonthList() {

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
        CalendarController.getCalendarData();
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

        closeResources();
    }

    static ResultSet getCalendarData() {
        dBconnection = new DBconnection();
        dBconnection.openDB();
        try {
            res = dBconnection.getC().prepareStatement("SELECT * FROM public.calendar", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
                    .executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void updateCalendarCell(CalendarCell<Months, String> cc, ActionEvent event) {
        dBconnection = new DBconnection();
        dBconnection.openDB();
        try {
            res = dBconnection.getC().prepareStatement("SELECT * FROM public.calendar", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
                    .executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Object source = event.getSource();
        if (!(source instanceof MenuItem)) {
            return;
        }
        MenuItem menuItem = (MenuItem) source;
        calendar.set(cc.getYear(), cc.getIndex(), Integer.parseInt(cc.getItem()));
        int m = cc.getIndex() + 1;
        boolean deleted = false;
        setBeforeFirst();
        if (menuItem.getId().equals("1")) {
            try {
                while (!res.next()) {

                    int yyyy = res.getInt(YEAR);
                    int mm = res.getInt(MONTH);
                    int dd = res.getInt(DAY);
                    String type = res.getString(TYPE);
                    if (calendar.get(Calendar.YEAR) == yyyy && calendar.get(Calendar.MONTH) + 1 == mm && calendar.get(Calendar.DAY_OF_MONTH) == dd &&
                            type.equals("workday")) {
                        deleted = true;
                        dBconnection
                                .getStmt().execute(
                                "DELETE from public.calendar where year = " + yyyy + " and month = " + mm + " and day = " + dd + " and type = " +
                                        "\'" +
                                        type + "\'");
                        dBconnection.getC().commit();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (!deleted) {
                try {
                    dBconnection.getStmt().execute(
                            "INSERT INTO public.calendar (year, month, day, type) VALUES ( " + cc.getYear() + ", " + m + ", " +
                                    Integer.parseInt(cc.getItem()) + ", " + "\'" + "holiday" + "\'" + " )");
                    dBconnection.getC().commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (!cc.getStyle().contains("#d4ebd7")) {
                cc.setStyle("-fx-alignment: CENTER;  -fx-background-color:lemonchiffon;");
            }
        } else if (menuItem.getId().equals("2")) {
            try {
                while (!res.next()) {
                    int yyyy = res.getInt(YEAR);
                    int mm = res.getInt(MONTH);
                    int dd = res.getInt(DAY);
                    String type = res.getString(TYPE);
                    if (calendar.get(Calendar.YEAR) == yyyy && calendar.get(Calendar.MONTH) + 1 == mm && calendar.get(Calendar.DAY_OF_MONTH) == dd &&
                            type.equals("holiday")) {
                        deleted = true;
                        dBconnection
                                .getStmt().execute(
                                "DELETE from public.calendar where year = " + yyyy + " and month = " + mm + " and day = " + dd + " and type = " +
                                        "\'" +
                                        type + "\'");
                        dBconnection.getC().commit();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (!deleted) {
                try {
                    dBconnection.getStmt().execute(
                            "INSERT INTO public.calendar (year, month, day, type) VALUES ( " + cc.getYear() + ", " + m + ", " +
                                    Integer.parseInt(cc.getItem()) + ", " + "\'" + "workday" + "\'" + " )");
                    dBconnection.getC().commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (!cc.getStyle().contains("#d4ebd7")) {
                cc.setStyle("-fx-alignment: CENTER;");
            }
            closeResources();
        }
    }

    private void setBeforeFirst() {
        try {
            res.beforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void closeResources() {
        dBconnection.closeDB();
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
            int order = new TimeSheetController().returnMonth(month);
            Calendar cal = Calendar.getInstance();
            cal.set(LocalDate.now().getYear(), order, 1);
            int num = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            ArrayList<String> daysList = new ArrayList<>();
            for (int j = 1; j <= num; j++) {
                daysList.add(String.valueOf(j));
            }
            days = daysList.toArray(new String[31]);
        }
    }
}
