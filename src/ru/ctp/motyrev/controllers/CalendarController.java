package ru.ctp.motyrev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ru.ctp.motyrev.code.CalendarCell;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class CalendarController {
    @FXML
    private TableView<Months> calendarView;

    String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

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

        public Months(String month) {
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

    @FXML
    private void initialize() {
        calendarView.getSelectionModel().setCellSelectionEnabled(true);
        calendarView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ObservableList<TableColumn<Months, ?>> columns = calendarView.getColumns();
        TableColumn<Months, String> tableColumn = new TableColumn<>("Месяц");
        ObservableList<Months> list = getMonthList();
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        tableColumn.setStyle("-fx-alignment: CENTER;");
        tableColumn.setSortable(false);
        calendarView.setItems(list);

        columns.add(tableColumn);
        TableColumn<Months, String>[] tableColumnDays = new TableColumn[31];

        for (int i = 1; i <= tableColumnDays.length; i++) {
            tableColumnDays[i - 1] = new TableColumn<>("" + i);
            tableColumnDays[i - 1].setSortable(false);
            tableColumnDays[i - 1].setCellValueFactory(new PropertyValueFactory<>("day" + i));
            tableColumnDays[i - 1].setCellFactory(column -> {
                CalendarCell<Months, String> c = CalendarCell.createCalendarCell();
                ContextMenu cm1 = new ContextMenu();
                ContextMenu cm2 = new ContextMenu();
                MenuItem mi1 = new MenuItem("Установить нерабочим днём");
                mi1.setOnAction(event -> {
                    if (!c.getStyle().contains("#d4ebd7")) {
                        c.setStyle("-fx-alignment: CENTER;  -fx-background-color:lemonchiffon;");
                    }
                });
                cm1.getItems().add(mi1);
                MenuItem mi2 = new MenuItem("Установить рабочим днём");
                mi2.setOnAction(event -> {
                    if (!c.getStyle().contains("#d4ebd7")) {
                        c.setStyle("-fx-alignment: CENTER;");
                    }
                });
                cm2.getItems().add(mi2);

                c.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        if (c.getStyle().equals("-fx-alignment: CENTER;")) {
                            cm1.show(c, event.getScreenX(), event.getScreenY());
                        } else {
                            cm2.show(c, event.getScreenX(), event.getScreenY());
                        }
                        event.consume();
                    }
                });
                return c;
            });
        }
        Collections.addAll(columns, tableColumnDays);
    }
}
