package ru.ctp.motyrev.code;

import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class DateEditingCell<S, T> extends TableCell<S, T> {
    // Text field for editing
    // TODO: allow this to be a plugable control.
    private final DatePicker datePicker = new DatePicker();

    // Converter for converting the text in the text field to the user type, and vice-versa:
    private final StringConverter<T> converter ;

    public DateEditingCell(StringConverter<T> converter) {
        this.converter = converter ;

        itemProperty().addListener((obx, oldItem, newItem) -> {

            if (newItem == null) {
                setText(null);
            } else {
                setText(converter.toString(newItem).replace(",", "."));
            }
        });
        setGraphic(datePicker);
        setContentDisplay(ContentDisplay.TEXT_ONLY);

        datePicker.setOnAction(evt -> {
            commitEdit(this.converter.fromString(dateToString(datePicker.getValue())));
        });
        datePicker.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                commitEdit(this.converter.fromString(dateToString(datePicker.getValue())));
            }
        });
        datePicker.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                datePicker.setValue(stringToDate(converter.toString(getItem())));
                cancelEdit();
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
                getTableView().getSelectionModel().selectRightCell();
                event.consume();
            }else if (event.getCode() == KeyCode.RIGHT) {
                getTableView().getSelectionModel().selectRightCell();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {
                getTableView().getSelectionModel().selectLeftCell();
                event.consume();
            } else if (event.getCode() == KeyCode.UP) {
                getTableView().getSelectionModel().selectAboveCell();
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
                getTableView().getSelectionModel().selectBelowCell();
                event.consume();
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
     * Convenience method for creating an EditCell for a String value.
     * @return
     */
    public static <S> DateEditingCell<S, String> createStringEditCell() {
        return new DateEditingCell<S, String>(IDENTITY_CONVERTER);
    }


    // set the text of the text field and display the graphic
    @Override
    public void startEdit() {
        super.startEdit();
        datePicker.setValue(stringToDate(converter.toString(getItem())));
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        datePicker.requestFocus();
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
        if (! isEditing() && ! item.equals(getItem())) {
            TableView<S> table = getTableView();
            if (table != null) {
                TableColumn<S, T> column = getTableColumn();
                CellEditEvent<S, T> event = new CellEditEvent<>(table,
                        new TablePosition<S, T>(table, getIndex(), column),
                        TableColumn.editCommitEvent(), item);
                Event.fireEvent(column, event);
            }
        }

        super.commitEdit(item);

        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void updateItem(T item, boolean empty){
        super.updateItem(item, empty);

        if (item == null & !empty) {
            setStyle("-fx-background-color: lightyellow;-fx-alignment: CENTER;");
        } else {
            setStyle("-fx-alignment: CENTER;");
        }
    }

    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);
        try {

            if (selected) {
                setStyle("-fx-alignment: CENTER;");
            } else if (getItem() == null) {
                setStyle("-fx-background-color: lightyellow;-fx-alignment: CENTER;");
            }
        }catch (NullPointerException nle) {

        }
    }

    public String dateToString (LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if (date != null) {
            String formattedString = date.format(formatter);
            return formattedString;
        } else {
            return "";
        }
    }

    public LocalDate stringToDate (String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if (date != null) {
            LocalDate localDate = LocalDate.parse(date, formatter);
            return localDate;
        } else {
            return null;
        }
    }
}