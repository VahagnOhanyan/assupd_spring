package ru.ctp.motyrev.code;

import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import ru.ctp.motyrev.controllers.CalendarController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarCell<S, T> extends TableCell<S, T> {

    private final TextField textField = new TextField();

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        CalendarCell.year = year;
    }

    public static int year = LocalDate.now().getYear();

    // Converter for converting the text in the text field to the user type, and vice-versa:
    private final StringConverter<T> converter;
    private static ResultSet res = null;

    public static void setRes(ResultSet res) {
        CalendarCell.res = res;
    }

    private CalendarCell(StringConverter<T> converter) {
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
    private static final StringConverter<String> IDENTITY_CONVERTER = new StringConverter<String>() {

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
                    setStyle("-fx-alignment: CENTER;  -fx-background-color:#ddebf7;");
                                        if ((checkDateForWeekEnd(monthOrder, Integer.parseInt((String) item)) &&
                                                !checkDateForWorkday(monthOrder, Integer.parseInt((String) item))) ||
                                                (checkDateForHoliday(monthOrder, Integer.parseInt((String) item)) &&
                                                        !checkDateForWorkday(monthOrder, Integer.parseInt((String) item))))  {
                                            setStyle("-fx-alignment: CENTER;  -fx-background-color:#fce4d6;");
                                       }
                } else if ((checkDateForWeekEnd(monthOrder, Integer.parseInt((String) item)) &&
                        !checkDateForWorkday(monthOrder, Integer.parseInt((String) item))) ||
                        (checkDateForHoliday(monthOrder, Integer.parseInt((String) item)) &&
                                !checkDateForWorkday(monthOrder, Integer.parseInt((String) item)))) {
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                    setStyle("-fx-alignment: CENTER;  -fx-background-color:#fff2cc;");
                } else {
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                    setStyle("-fx-alignment: CENTER;");
                }
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
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (this.getStyle().contains("#fff2cc;") || this.getStyle().contains("#fce4d6;")) {
                        cm2.show(this, event.getScreenX(), event.getScreenY());
                    }
                    if (this.getStyle().equals("-fx-alignment: CENTER;") || this.getStyle().contains("#ddebf7;")) {
                        cm1.show(this, event.getScreenX(), event.getScreenY());
                    }
                    event.consume();
                }
            });
        }
    }

    private static boolean checkDateForWeekEnd(int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        int dayofweeks = Calendar.DAY_OF_WEEK;
        return date.get(dayofweeks) == Calendar.SATURDAY
                || date.get(dayofweeks) == Calendar.SUNDAY;
    }

    public boolean checkDateForHoliday(int month, int day) {
        setBeforeFirst();
        try {
            while (res.next()) {

                int yyyy = res.getInt(CalendarController.YEAR);
                int mm = res.getInt(CalendarController.MONTH) - 1;
                int dd = res.getInt(CalendarController.DAY);
                String type = res.getString(CalendarController.TYPE);
                if (year == yyyy && month == mm && dd == day && type.equals("holiday")) {
                    return true;
                }
            }
        } catch (SQLException e) {
            return false;
        }

        return false;
    }

    public boolean checkDateForWorkday(int month, int day) {
        setBeforeFirst();
        try {
            while (res.next()) {
                int yyyy = res.getInt(CalendarController.YEAR);
                int mm = res.getInt(CalendarController.MONTH) - 1;
                int dd = res.getInt(CalendarController.DAY);
                String type = res.getString(CalendarController.TYPE);
                if (year == yyyy && month == mm && dd == day && type.equals("workday")) {
                    return true;
                }
            }
        } catch (SQLException e) {
            return false;
        }

        return false;
    }

    private void setBeforeFirst() {
        try {
            res.beforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkDateForCurrent(int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        GregorianCalendar currentDate = new GregorianCalendar();
        return date.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH) &&
                date.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) && date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR);
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