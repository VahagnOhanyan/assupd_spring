package ru.ctp.motyrev.interfaces.impls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import ru.ctp.motyrev.code.DBconnection;
import ru.ctp.motyrev.interfaces.TimeSheetInterface;
import ru.ctp.motyrev.objects.TimeSheet;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class CollectionTimeSheet implements TimeSheetInterface, Serializable {

    private String work_num;
    private String work_stage;
    private String work_note;
    private String start_perc;

    private String day1;
    private String day2;
    private String day3;
    private String day4;
    private String day5;
    private String day6;
    private String day7;
    private String day8;
    private String day9;
    private String day10;
    private String day11;
    private String day12;
    private String day13;
    private String day14;
    private String day15;
    private String day16;
    private String day17;
    private String day18;
    private String day19;
    private String day20;
    private String day21;
    private String day22;
    private String day23;
    private String day24;
    private String day25;
    private String day26;
    private String day27;
    private String day28;
    private String day29;
    private String day30;
    private String day31;

    private String end_perc;

    private ObservableList<TimeSheet> timesheetlist = FXCollections.observableArrayList();

    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy'_'HH-mm-ss");

    private PrintWriter writer;

    DBconnection dBconnection = new DBconnection();

    public void getUserTimeSheet(String year, String month) throws SQLException {

    }

    public ObservableList<TimeSheet> getTimesheetlist() {
        return timesheetlist;
    }

    public void clear() {
        timesheetlist.clear();
    }

    @Override
    public void add(TimeSheet timeSheet) {
        timesheetlist.add(timeSheet);
    }

    @Override
    public void delete(TimeSheet timeSheet) {
        timesheetlist.remove(timeSheet);
    }

}
