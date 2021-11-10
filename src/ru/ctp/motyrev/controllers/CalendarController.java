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
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.ctp.motyrev.code.CalendarCell;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class CalendarController {
    @FXML
    private TableView<Months> calendarView;
    @FXML
    private Label yearLabel;

    String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    ObservableList<Months> list;
    ObservableList<TableColumn<Months, ?>> columns;
    TableColumn<Months, String>[] tableColumnDays = new TableColumn[31];
    Calendar c = Calendar.getInstance();
    File xlsxFile = new File("holidays.xlsx");
    Workbook workbook = null;
    FileInputStream inp = null;
    Sheet sheet = workbook.getSheetAt(0);
     int rowNum = 1;
    @FXML
    private void initialize() {
        yearLabel.setText(String.valueOf(LocalDate.now().getYear()));
        yearLabel.setTextFill(Color.RED);
        calendarView.getSelectionModel().setCellSelectionEnabled(true);
        calendarView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        columns = calendarView.getColumns();
        TableColumn<Months, String> tableColumn = new TableColumn<>("Месяц");
        list = getMonthList();
        tableColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        tableColumn.setStyle("-fx-alignment: CENTER;");
        tableColumn.setSortable(false);
        calendarView.setItems(list);
        columns.add(tableColumn);
        fillCalendar(LocalDate.now().getYear());
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

    private void fillCalendar(int year) {
        for (int i = 1; i <= tableColumnDays.length; i++) {
            tableColumnDays[i - 1] = new TableColumn<>("" + i);
            tableColumnDays[i - 1].setSortable(false);
            tableColumnDays[i - 1].setCellValueFactory(new PropertyValueFactory<>("day" + i));
            tableColumnDays[i - 1].setCellFactory(column -> {
                CalendarCell<Months, String> cc = CalendarCell.createCalendarCell();
                cc.setYear(year);
                ContextMenu cm1 = new ContextMenu();
                ContextMenu cm2 = new ContextMenu();
                MenuItem mi1 = new MenuItem("Установить нерабочим днём");
                MenuItem mi2 = new MenuItem("Установить рабочим днём");
                mi1.setOnAction(event -> {


                    try {
                        inp = new FileInputStream(xlsxFile);
                        workbook = WorkbookFactory.create(inp);
                    } catch (IOException | InvalidFormatException e) {
                        e.printStackTrace();
                    }
                    assert workbook != null;

                  //  workbook.setSheetName(0, "holidays");
                    while (sheet.createRow(++rowNum).createCell(c.get(Calendar.YEAR) - 2018).getStringCellValue() != null && sheet.createRow(rowNum).createCell(c.get(Calendar.YEAR) - 2018).getStringCellValue() != "") {
                        System.out.println(sheet.createRow(++rowNum).createCell(c.get(Calendar.YEAR) - 2018).getStringCellValue());

                    }
                    Row row = sheet.createRow(rowNum);
                    org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(c.get(Calendar.YEAR) - 2018);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(cc.getYear(), cc.getIndex(), Integer.parseInt(cc.getItem()));
                    Date date = calendar.getTime();
                    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM");
                    String date1 = format1.format(date);
                    cell1.setCellValue(date1);
                    try {
                        FileOutputStream fileOut = new FileOutputStream(xlsxFile);
                        workbook.write(fileOut);
                        fileOut.close();
                        workbook.close();
                        inp.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (!cc.getStyle().contains("#d4ebd7")) {
                        cc.setStyle("-fx-alignment: CENTER;  -fx-background-color:lemonchiffon;");
                    }
                });
                cm1.getItems().add(mi1);
                mi2.setOnAction(event -> {
                    if (!cc.getStyle().contains("#d4ebd7")) {
                        cc.setStyle("-fx-alignment: CENTER;");
                    }
                });
                cm2.getItems().add(mi2);

                cc.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        if (cc.getStyle().equals("-fx-alignment: CENTER;")) {
                            cm1.show(cc, event.getScreenX(), event.getScreenY());
                        } else {
                            cm2.show(cc, event.getScreenX(), event.getScreenY());
                        }
                        event.consume();
                    }
                });
                return cc;
            });
        }
        Collections.addAll(columns, tableColumnDays);
    }

    public void actionChangeYear(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) {
            return;
        }
        Button clickedButton = (Button) source;
        switch (clickedButton.getId()) {
            case "backBtn":
                c.set(c.get(Calendar.YEAR) - 1, 1, 1);
                checkLeapYear(c.get(Calendar.YEAR));
                yearLabel.setText(String.valueOf(c.get(Calendar.YEAR)));
                break;
            case "forwardBtn":
                c.set(c.get(Calendar.YEAR) + 1, 1, 1);
                checkLeapYear(c.get(Calendar.YEAR));
                yearLabel.setText(String.valueOf(c.get(Calendar.YEAR)));
        }
        for (int i = 1; i <= tableColumnDays.length; i++) {
            tableColumnDays[i - 1].setCellValueFactory(new PropertyValueFactory<>("day" + i));
            tableColumnDays[i - 1].setCellFactory(column -> {
                CalendarCell<Months, String> cc = CalendarCell.createCalendarCell();
                cc.setYear(c.get(Calendar.YEAR));
                ContextMenu cm1 = new ContextMenu();
                ContextMenu cm2 = new ContextMenu();
                MenuItem mi1 = new MenuItem("Установить нерабочим днём");
                MenuItem mi2 = new MenuItem("Установить рабочим днём");
                cm1.getItems().add(mi1);
                mi2.setOnAction(event -> {
                    if (!cc.getStyle().contains("#d4ebd7")) {
                        cc.setStyle("-fx-alignment: CENTER;");
                    }
                });
                cm2.getItems().add(mi2);

                cc.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        if (cc.getStyle().equals("-fx-alignment: CENTER;")) {
                            cm1.show(cc, event.getScreenX(), event.getScreenY());
                        } else {
                            cm2.show(cc, event.getScreenX(), event.getScreenY());
                        }
                        event.consume();
                    }
                });
                return cc;
            });
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

        private String[] days;

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
}
