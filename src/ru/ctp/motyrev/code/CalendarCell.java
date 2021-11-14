package ru.ctp.motyrev.code;

import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import ru.ctp.motyrev.controllers.CalendarController;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarCell<S, T> extends TableCell<S, T> {
    /*
     Text field for editing
     TODO: allow this to be a plugable control.
    */

    private final TextField textField = new TextField();

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    private int year = LocalDate.now().getYear();

    // Converter for converting the text in the text field to the user type, and vice-versa:
    private final StringConverter<T> converter;
    SimpleDateFormat format1 = new SimpleDateFormat("dd/MM");
    static File holidayFile = new File("holidays.xlsx");
    static File workFile = new File("workdays.xlsx");
    static Workbook holidayBook = null;
    static Workbook workbook = null;
    static FileInputStream inp = null;
    static FileInputStream inpWork = null;

    static {
        try {
            inp = new FileInputStream(holidayFile);
            holidayBook = WorkbookFactory.create(inp);
            inpWork = new FileInputStream(workFile);
            workbook = WorkbookFactory.create(inpWork);
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    public CalendarCell(StringConverter<T> converter) {
        this.converter = converter;

        itemProperty().addListener((obx, oldItem, newItem) -> {

            if (newItem == null) {
                setText(null);
            } else {
                setText(converter.toString(newItem).replace(",", "."));
            }
        });
    }

    /**
     * Convenience converter that does nothing (converts Strings to themselves and vice-versa...).
     */
    public static final StringConverter<String> IDENTITY_CONVERTER = new StringConverter<String>() {

        @Override
        public String toString(String object) {
            return object;
        }

        @Override
        public String fromString(String string) {
            return string;
        }
    };

    /**
     * Convenience method for creating an CalendarCell for a String value.
     */
    public static <S> CalendarCell<S, String> createCalendarCell() {
        return new CalendarCell<>(IDENTITY_CONVERTER);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText((String) item);
        if (item != null) {
            int monthOrder = getIndex();
            if (monthOrder >= 0 && monthOrder <= 11) {
                if (checkDateForCurrent(monthOrder, Integer.parseInt((String) item))) {
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                    setStyle("-fx-alignment: CENTER;  -fx-background-color:#d4ebd7;");
                } else if ((checkDateForWeekEnd(monthOrder, Integer.parseInt((String) item)) &&
                        !checkDateForWorkday(monthOrder, Integer.parseInt((String) item))) ||
                        (checkDateForHoliday(monthOrder, Integer.parseInt((String) item)) &&
                                !checkDateForWorkday(monthOrder, Integer.parseInt((String) item)))) {
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                    setStyle("-fx-alignment: CENTER;  -fx-background-color:lemonchiffon;");
                } else {
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
            try {
                holidayBook.close();
                inp.close();
                workbook.close();
                inpWork.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ContextMenu cm1 = new ContextMenu();
            ContextMenu cm2 = new ContextMenu();
            MenuItem mi1 = new MenuItem("Установить нерабочим днём");
            mi1.setId("1");
            MenuItem mi2 = new MenuItem("Установить рабочим днём");
            mi2.setId("2");
            cm1.getItems().add(mi1);
            cm2.getItems().add(mi2);
            mi1.setOnAction(event -> new CalendarController().updateCalendarCell((CalendarCell<CalendarController.Months, String>) this, event));
            mi2.setOnAction(event -> new CalendarController().updateCalendarCell((CalendarCell<CalendarController.Months, String>) this, event));
            this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    if (this.getStyle().equals("-fx-alignment: CENTER;")) {
                        cm1.show(this, event.getScreenX(), event.getScreenY());
                    } else {
                        cm2.show(this, event.getScreenX(), event.getScreenY());
                    }
                    event.consume();
                }
            });
        }
    }

    private boolean checkDateForWeekEnd(int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        int dayofweeks = Calendar.DAY_OF_WEEK;
        return date.get(dayofweeks) == Calendar.SATURDAY
                || date.get(dayofweeks) == Calendar.SUNDAY;
    }

    public boolean checkDateForHoliday(int month, int day) {

        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        Date d = date.getTime();
        Sheet holidaysheet = holidayBook.getSheetAt(0);
        Cell cell1;
        String nextValue;
        for (int i = 1; i <= date.getActualMaximum(Calendar.DAY_OF_YEAR); i++) {
            if (checkIfRowExists(holidaysheet, i)) {
                Row row = holidaysheet.getRow(i);
                if (checkIfCellExists(row, year - CalendarController.START_YEAR)) {
                    cell1 = row.getCell(year - CalendarController.START_YEAR);
                    nextValue = cell1.getStringCellValue();
                    if (nextValue.equals(format1.format(d))) {
                        return true;
                    }
                    if (nextValue.equals("")) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean checkDateForWorkday(int month, int day) {

        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        Date d = date.getTime();
        Sheet worksheet = workbook.getSheetAt(0);
        Cell cell1;
        String nextValue;
        for (int i = 1; i <= date.getActualMaximum(Calendar.DAY_OF_YEAR); i++) {
            if (checkIfRowExists(worksheet, i)) {
                Row row = worksheet.getRow(i);
                if (checkIfCellExists(row, year - CalendarController.START_YEAR)) {
                    cell1 = row.getCell(year - CalendarController.START_YEAR);
                    nextValue = cell1.getStringCellValue();
                    if (nextValue.equals(format1.format(d))) {
                        return true;
                    }
                    if (nextValue.equals("")) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean checkDateForCurrent(int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        GregorianCalendar currentDate = new GregorianCalendar();
        return date.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH) &&
                date.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) && date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR);
    }

    private boolean checkIfCellExists(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        return cell != null;
    }

    private boolean checkIfRowExists(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            return false;
        }
        if (row.getLastCellNum() <= 0) {
            return false;
        }
        for (int cNum = row.getFirstCellNum(); cNum < row.getLastCellNum(); cNum++) {
            org.apache.poi.ss.usermodel.Cell cell = row.getCell(cNum);
            if (cell != null) {
                return true;
            }
        }
        return false;
    }

    // set the text of the text field and display the graphic
    @Override
    public void startEdit() {
        super.startEdit();
        textField.setText(converter.toString(getItem()));
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.requestFocus();
    }

    // revert to text display
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    // commits the edit. Update property if possible and revert to text display
    @Override
    public void commitEdit(T item) {

        // This block is necessary to support commit on losing focus, because the baked-in mechanism
        // sets our editing state to false before we can intercept the loss of focus.
        // The default commitEdit(...) method simply bails if we are not editing...
        if (!isEditing() && !item.equals(getItem())) {
            TableView<S> table = getTableView();
            if (table != null) {
                TableColumn<S, T> column = getTableColumn();
                CellEditEvent<S, T> event = new CellEditEvent<>(table,
                        new TablePosition<>(table, getIndex(), column),
                        TableColumn.editCommitEvent(), item);
                Event.fireEvent(column, event);
            }
        }

        super.commitEdit(item);

        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }
}