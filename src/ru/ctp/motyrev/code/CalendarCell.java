package ru.ctp.motyrev.code;

import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.util.StringConverter;

import java.util.Calendar;
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

    private static int year;

    // Converter for converting the text in the text field to the user type, and vice-versa:
    private final StringConverter<T> converter;

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
                } else if (checkDate(monthOrder, Integer.parseInt((String) item))) {
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                    setStyle("-fx-alignment: CENTER;  -fx-background-color:lemonchiffon;");
                } else {
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        }
    }

    private boolean checkDate(int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        int dayofweeks = Calendar.DAY_OF_WEEK;
        return date.get(dayofweeks) == Calendar.SATURDAY
                || date.get(dayofweeks) == Calendar.SUNDAY;
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