package ru.ctp.motyrev.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.ctp.motyrev.code.DBconnection;
import ru.ctp.motyrev.code.DateEditingCell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

public class TaskViewController {

    @FXML
    private TextField customerField;
    @FXML
    private TextField contractField;
    @FXML
    private TextField requestField;
    @FXML
    private TextField taskField;
    @FXML
    private TextField projectField;
    @FXML
    private TextField statusField;
    @FXML
    private DatePicker requestDate;
    @FXML
    private DatePicker taskDate;
    @FXML
    private DatePicker taskStartDate;
    @FXML
    private DatePicker taskEndDate;
    @FXML
    private TableView taskView;
    @FXML
    private TableView shortStageView;
    @FXML
    private TableView shortExecuterView;
    @FXML
    private TitledPane shortViewPane;
    @FXML
    private TitledPane summaryViewPane;
    @FXML
    private TableView summaryView;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ComboBox monthBox;
    @FXML
    private ComboBox<Integer> yearBox;
    @FXML
    private RadioButton showByUsers;
    @FXML
    private RadioButton showByStages;
    @FXML
    private RadioButton oneMonth;
    @FXML
    private RadioButton allTime;
    @FXML
    private Button btnShow;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private CheckBox hideEmpty;

    private String sql = "";
    private String sqlSum = "";
    private String tableColName;

    private String taskColWidth = "";
    private String stageColWidth = "";
    private String commentColWidth = "";
    private String procStartColWidth = "";
    private String dayColWidth = "";
    private String procEndColWidth = "";
    private String user_fullname = "";

    private GregorianCalendar date = new GregorianCalendar();
    String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    int year;
    String month;
    String userprofile = System.getenv("USERPROFILE");
    String mode = "showByUsers";
    private SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
    private SimpleDateFormat sdf3 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy");
    private SimpleDateFormat sdf5 = new SimpleDateFormat("MM");
    private DBconnection dBconnection = new DBconnection();

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> specData = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> specDataExecutor = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> specDataSummary = FXCollections.observableArrayList();
    private ObservableList<ObservableList<String>> specDataSummaryByUser = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataSum = FXCollections.observableArrayList();
    private ObservableList<ObservableList<ObservableList<String>>> specDataList = FXCollections.observableArrayList();
    ObservableList<TableColumn<ObservableList<String>, ?>> columns;
    @FXML
    private Label yearLabel;
    private String taskNumber;

    @FXML
    private void initialize() {
        summaryView.getColumns().clear();
        columns = summaryView.getColumns();
        ToggleGroup toggleGroup = new ToggleGroup();
        showByUsers.setToggleGroup(toggleGroup);
        showByStages.setToggleGroup(toggleGroup);
        ToggleGroup toggleGroup2 = new ToggleGroup();
        oneMonth.setToggleGroup(toggleGroup2);
        allTime.setToggleGroup(toggleGroup2);
        allTime.setSelected(true);
        showByUsers.setSelected(true);
        btnShow.setDisable(true);
        shortViewPane.setExpanded(true);
        summaryViewPane.setExpanded(false);
        showByStages.setSelected(false);
        startDate.setValue(LocalDate.of(2018, Month.JANUARY, 1));
        endDate.setValue(LocalDate.now());
        monthBox.getItems().addAll(months);
        yearBox.getItems().addAll(2018, 2019, 2020, 2021, 2022);
        monthBox.setValue(months[LocalDate.now().getMonth().ordinal()]);
        yearBox.setValue(LocalDate.now().getYear());
        yearBox.setDisable(true);
        monthBox.setDisable(true);
        hideEmpty.setSelected(true);
        dataSum.clear();
        specDataSummary.clear();
        specDataSummaryByUser.clear();
        specData.clear();
        specDataExecutor.clear();
        year = LocalDate.now().getYear();
        month = months[LocalDate.now().getMonth().ordinal()];

        hideEmpty.pressedProperty().addListener(observable -> {
            btnShow.setDisable(false);
            summaryView.setEditable(false);
            summaryView.getSelectionModel().clearSelection();
            summaryView.getColumns().clear();
            summaryView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
        });
        monthBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            month = (String) newValue;
            startDate.setValue(LocalDate.of(yearBox.getValue(), new TimeSheetController().returnMonth(month) + 1, 1));
            endDate.setValue(LocalDate.of(yearBox.getValue(), new TimeSheetController().returnMonth(month) + 1, defineMaxDayOfMonth(month)));
            btnShow.setDisable(false);
            summaryView.setEditable(false);
            summaryView.getSelectionModel().clearSelection();
            summaryView.getColumns().clear();
            summaryView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
        });

        yearBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            year = newValue;
            startDate.setValue(LocalDate.of(year, new TimeSheetController().returnMonth(month) + 1, 1));
            endDate.setValue(LocalDate.of(year, new TimeSheetController().returnMonth(month) + 1, defineMaxDayOfMonth(month)));
            btnShow.setDisable(false);
            summaryView.setEditable(false);
            summaryView.getSelectionModel().clearSelection();
            summaryView.getColumns().clear();
            summaryView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
        });

        showByStages.selectedProperty().addListener(observable -> {
            if (showByStages.isSelected()) {
                summaryView.setEditable(false);
                summaryView.getSelectionModel().clearSelection();
                summaryView.getColumns().clear();
                summaryView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
                btnShow.setDisable(false);
                // summaryViewTablesLoad(taskNumber);
                mode = "showByStages";
            }
        });

        showByUsers.selectedProperty().addListener(observable -> {
            if (showByUsers.isSelected()) {

                summaryView.setEditable(false);
                summaryView.getSelectionModel().clearSelection();
                summaryView.getColumns().clear();
                summaryView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
                btnShow.setDisable(false);
                // summaryViewTablesLoadByUsers(taskNumber);
                mode = "showByUsers";
            }
        });
        oneMonth.selectedProperty().addListener(observable -> {
            if (oneMonth.isSelected()) {

                summaryView.setEditable(false);
                summaryView.getSelectionModel().clearSelection();
                summaryView.getColumns().clear();
                summaryView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
                btnShow.setDisable(false);
                monthBox.setDisable(false);
                yearBox.setDisable(false);
                startDate.setValue(LocalDate.of(year, new TimeSheetController().returnMonth(month) + 1, 1));
                endDate.setValue(LocalDate.of(year, new TimeSheetController().returnMonth(month) + 1, defineMaxDayOfMonth(month)));
                // summaryViewTablesLoadByUsers(taskNumber);

            }
        });
        allTime.selectedProperty().addListener(observable -> {
            if (allTime.isSelected()) {

                summaryView.setEditable(false);
                summaryView.getSelectionModel().clearSelection();
                summaryView.getColumns().clear();
                summaryView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
                btnShow.setDisable(false);
                monthBox.setDisable(true);
                yearBox.setDisable(true);
                startDate.setValue(LocalDate.of(2018, Month.JANUARY, 1));
                endDate.setValue(LocalDate.now());
                // summaryViewTablesLoadByUsers(taskNumber);

            }
        });
    }

    public void initTaskView(String task) {
        formClear();

        data("SELECT r.request_number, r.request_description, cr.customer_name, c.contract_number, t.task_number, t.task_income_date, p.project_name, s.status_name FROM public.task t " +
                "left join public.request r on r.request_id = t.request_id " +
                "join public.project p on p.project_id = t.project_id " +
                "join public.contract_project cp on cp.project_id = p.project_id " +
                "join public.contract c on c.contract_id = cp.contract_id " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "join public.status s on s.status_id = t.status_id " +
                "WHERE t.task_number = '" + task + "'");

        taskField.setText(dataSelect.get(0).get(4).toString());

        customerField.setText(dataSelect.get(0).get(2).toString());
        contractField.setText(dataSelect.get(0).get(3).toString());

        if (dataSelect.get(0).get(0) != null) {
            requestField.setText(dataSelect.get(0).get(0).toString());
        }

        projectField.setText(dataSelect.get(0).get(6).toString());
        statusField.setText(dataSelect.get(0).get(7).toString());

        sql =
                "SELECT row_number() OVER(ORDER BY t.task_number), t.task_number, t.task_name, to_char(t.req_date_start, 'DD.MM.YYYY') req_start, to_char(t.req_date_end, 'DD.MM.YYYY') req_end, " +
                        "to_char(min(dw.daily_work_date), 'DD.MM.YYYY') min_start, t.req_date_start-min(dw.daily_work_date) delta_start, to_char(max(dw.daily_work_date), 'DD.MM.YYYY') max_start, " +
                        "t.req_date_end-max(dw.daily_work_date) delta_end, s.status_name, t.task_unit_plan, " +
                        "t.task_unit_fact, (round(t.task_unit_fact/nullif(t.task_unit_plan, 0), 2))*100 vip, " +
                        "t.task_pa_intensity, t.task_tz_intensity, round(t.task_tz_intensity/nullif(t.task_pa_intensity, 0), 2) KaPe, " +
                        "sum(dw.daily_intensity), " +
                        "(CASE WHEN t.task_out = false THEN 0 " +
                        "ELSE t.task_pa_intensity " +
                        "END) outsource, " +
                        "(CASE WHEN t.task_out = false THEN round(t.task_tz_intensity/nullif(sum(dw.daily_intensity), 0), 2) " +
                        "ELSE round(t.task_tz_intensity/(nullif(sum(dw.daily_intensity), 0)+t.task_pa_intensity), 2) " +
                        "END) effect, " +
                        "(CASE WHEN t.task_out = false THEN t.task_tz_intensity-sum(dw.daily_intensity) " +
                        "ELSE t.task_tz_intensity-sum(dw.daily_intensity)-t.task_pa_intensity " +
                        "END) delta FROM public.task t " +
                        "join public.status s on s.status_id = t.status_id " +
                        "left join public.stage st on st.task_id = t.task_id " +
                        "left join public.stage_daily sd on sd.stage_id = st.stage_id " +
                        "left join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                        "WHERE t.task_number = '" + task + "' " +
                        "GROUP BY t.task_number, t.task_name, s.status_name, t.task_unit_plan, t.task_unit_fact, t.task_pa_intensity, t.task_tz_intensity, t.req_date_start, t.req_date_end, t.task_out " +
                        "ORDER BY t.task_number";

        tableUpdate(sql);
        shortTablesLoad(task);
        shortExecutorTablesLoad(task);
        // summaryViewTablesLoad(task);
        summaryViewTablesLoadByUsersHideEmpty(task);
    }

    private void tableUpdate(String sql) {
/*        InputStream inp;
        Properties props = new Properties();

        try {
            inp = new FileInputStream(userprofile + "\\ASUPD\\config\\taskViewConfig.ini");
            props.load(inp);
            if (props.getProperty("task_col_width") != null) {
                taskColWidth = props.getProperty("task_col_width");
            }
            if (props.getProperty("stage_col_width") != null) {
                stageColWidth = props.getProperty("stage_col_width");
            }
            if (props.getProperty("com_col_width") != null) {
                commentColWidth = props.getProperty("com_col_width");
            }
            if (props.getProperty("proc_start_col_width") != null) {
                procStartColWidth = props.getProperty("proc_start_col_width");
            }
            if (props.getProperty("day_col_width") != null) {
                dayColWidth = props.getProperty("day_col_width");
            }
            if (props.getProperty("proc_end_col_width") != null) {
                procEndColWidth = props.getProperty("proc_end_col_width");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        dBconnection.openDB();
        dBconnection.query(sql);

        taskView.getSelectionModel().setCellSelectionEnabled(true);
        taskView.setEditable(true);
        taskView.getColumns().clear();
        taskView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);

        try {
            for (int i = 0; i < dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                final int j = i;

                tableColName = dBconnection.getRs().getMetaData().getColumnName(i + 1);
                generateColName(dBconnection.getRs().getMetaData().getColumnName(i + 1));
                TableColumn tableColumn = new TableColumn(tableColName);
                tableColumn.setEditable(false);
                tableColumn.setCellValueFactory(
                        (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(
                                (String) param.getValue().get(j)));

                if (dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("task_name")) {
                    tableColumn.setPrefWidth(200);
                    tableColumn.setCellFactory(new Callback<TableColumn, TableCell>() {

                        public TableCell call(TableColumn param) {

                            return new TableCell<ObservableList, String>() {

                                private final Text newText;

                                {
                                    newText = new Text();
                                    newText.wrappingWidthProperty().bind(tableColumn.widthProperty());
                                }

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);

                                    setPrefHeight(Control.USE_COMPUTED_SIZE);

                                    if (empty || item == null) {
                                        newText.setText("");
                                        setGraphic(newText);
                                        setStyle("");
                                    } else {
                                        newText.setText(item);
                                        setGraphic(newText);
                                    }
                                }

                                @Override
                                public void updateSelected(boolean selected) {
                                    super.updateSelected(selected);

                                    if (selected) {
                                        newText.setFill(Color.WHITE);
                                        setGraphic(newText);
                                    } else {
                                        newText.setFill(Color.BLACK);
                                        setGraphic(newText);
                                    }
                                }
                            };
                        }
                    });
                } else if (dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("status_name")) {
                    tableColumn.setCellFactory(new Callback<TableColumn, TableCell>() {

                        public TableCell call(TableColumn param) {

                            return new TableCell<ObservableList, String>() {

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);

                                    setPrefHeight(Control.USE_COMPUTED_SIZE);

                                    if (item == null || empty) {

                                    } else {
                                        setText(item);
                                        setTextFill(Color.BLACK);
                                        if (item.toString().contains("утверждено")) {
                                            setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
                                        } else if (item.toString().contains("в ожидании")) {
                                            setStyle("-fx-background-color: orange;-fx-alignment: CENTER;");
                                        } else if (item.toString().contains("проверено")) {
                                            setStyle("-fx-background-color: turquoise;-fx-alignment: CENTER;");
                                        } else if (item.toString().contains("выполнено")) {
                                            setStyle("-fx-background-color: powderblue;-fx-alignment: CENTER;");
                                        } else if (item.toString().contains("Уволен")) {
                                            setStyle("-fx-background-color: LAVENDERBLUSH;-fx-alignment: CENTER;");
                                        } else {
                                            setStyle("-fx-alignment: CENTER;");
                                        }
                                    }
                                }

                                @Override
                                public void updateSelected(boolean selected) {
                                    super.updateSelected(selected);
                                    try {

                                        if (selected) {
                                            setText(getItem());
                                            setStyle("-fx-alignment: CENTER;");
                                            setTextFill(Color.WHITE);
                                        } else {
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            if (getItem().contains("утверждено")) {
                                                setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
                                            } else if (getItem().contains("в ожидании")) {
                                                setStyle("-fx-background-color: orange;-fx-alignment: CENTER;");
                                            } else if (getItem().contains("проверено")) {
                                                setStyle("-fx-background-color: turquoise;-fx-alignment: CENTER;");
                                            } else if (getItem().contains("выполнено")) {
                                                setStyle("-fx-background-color: powderblue;-fx-alignment: CENTER;");
                                            } else if (getItem().contains("Уволен")) {
                                                setStyle("-fx-background-color: LAVENDERBLUSH;-fx-alignment: CENTER;");
                                            } else {
                                                setStyle("-fx-alignment: CENTER;");
                                            }
                                        }
                                    } catch (NullPointerException nle) {

                                    }
                                }
                            };
                        }
                    });
                } else if (dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("delta")) {
                    tableColumn.setCellFactory(new Callback<TableColumn, TableCell>() {

                        public TableCell call(TableColumn param) {

                            return new TableCell<ObservableList, String>() {

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);

                                    setPrefHeight(Control.USE_COMPUTED_SIZE);

                                    if (item == null || empty) {

                                    } else if (Double.parseDouble(item) < 0) {
                                        setText(item);
                                        setTextFill(Color.BLACK);
                                        setStyle("-fx-background-color: DARKSALMON;-fx-alignment: CENTER;");
                                    } else {
                                        setText(item);
                                        setTextFill(Color.BLACK);
                                        setStyle("-fx-alignment: CENTER;");
                                    }
                                }

                                @Override
                                public void updateSelected(boolean selected) {
                                    super.updateSelected(selected);
                                    try {

                                        if (selected) {
                                            setText(getItem());
                                            setStyle("-fx-alignment: CENTER;");
                                            setTextFill(Color.WHITE);
                                        } else if (Double.parseDouble(getItem()) < 0) {
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: DARKSALMON;-fx-alignment: CENTER;");
                                        } else {
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-alignment: CENTER;");
                                        }
                                    } catch (NullPointerException nle) {

                                    }
                                }
                            };
                        }
                    });

                    tableColumn.widthProperty().addListener((observable, oldValue, newValue) -> {
                        if (Double.parseDouble(newValue.toString()) < 100) {
                            tableColumn.setPrefWidth(Double.parseDouble(newValue.toString()) + 20);
                        }
                    });
                } else if (dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("effect")) {
                    tableColumn.setCellFactory(new Callback<TableColumn, TableCell>() {

                        public TableCell call(TableColumn param) {

                            return new TableCell<ObservableList, String>() {

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);

                                    setPrefHeight(Control.USE_COMPUTED_SIZE);

                                    if (item == null || empty) {

                                    } else if (Double.parseDouble(item) < 0.9) {
                                        setText(item);
                                        setTextFill(Color.BLACK);
                                        setStyle("-fx-background-color: DARKSALMON;-fx-alignment: CENTER;");
                                    } else if (Double.parseDouble(item) < 1 & Double.parseDouble(getItem()) >= 0.9) {
                                        setText(getItem());
                                        setTextFill(Color.BLACK);
                                        setStyle("-fx-background-color: yellow;-fx-alignment: CENTER;");
                                    } else {
                                        setText(item);
                                        setTextFill(Color.BLACK);
                                        setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
                                    }
                                }

                                @Override
                                public void updateSelected(boolean selected) {
                                    super.updateSelected(selected);
                                    try {

                                        if (selected) {
                                            setText(getItem());
                                            setStyle("-fx-alignment: CENTER;");
                                            setTextFill(Color.WHITE);
                                        } else if (Double.parseDouble(getItem()) < 0.9) {
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: DARKSALMON;-fx-alignment: CENTER;");
                                        } else if (Double.parseDouble(getItem()) < 1 & Double.parseDouble(getItem()) >= 0.9) {
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: yellow;-fx-alignment: CENTER;");
                                        } else {
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
                                        }
                                    } catch (NullPointerException nle) {

                                    }
                                }
                            };
                        }
                    });
                } else if (dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("delta_start") ||
                        dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("delta_end")) {
                    tableColumn.setCellFactory(new Callback<TableColumn, TableCell>() {

                        public TableCell call(TableColumn param) {

                            return new TableCell<ObservableList, String>() {

                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);

                                    setPrefHeight(Control.USE_COMPUTED_SIZE);

                                    if (item == null || empty) {

                                    } else if (Integer.parseInt(item) < 0) {
                                        setText(item);
                                        setTextFill(Color.BLACK);
                                        setStyle("-fx-background-color: DARKSALMON;-fx-alignment: CENTER;");
                                    } else {
                                        setText(item);
                                        setTextFill(Color.BLACK);
                                        setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
                                    }
                                }

                                @Override
                                public void updateSelected(boolean selected) {
                                    super.updateSelected(selected);
                                    try {

                                        if (selected) {
                                            setText(getItem());
                                            setStyle("-fx-alignment: CENTER;");
                                            setTextFill(Color.WHITE);
                                        } else if (Integer.parseInt(getItem()) < 0) {
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: DARKSALMON;-fx-alignment: CENTER;");
                                        } else {
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
                                        }
                                    } catch (NullPointerException nle) {

                                    }
                                }
                            };
                        }
                    });
                } else if (dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("req_start") ||
                        dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("req_end")) {
                    tableColumn.setCellFactory(column -> DateEditingCell.createStringEditCell());
                    tableColumn.setEditable(true);
                    tableColumn.setStyle("-fx-alignment: CENTER;");
                } else {
                    tableColumn.setStyle("-fx-alignment: CENTER;");
                }

                tableColumn.setOnEditCommit(((EventHandler<TableColumn.CellEditEvent<ObservableList, String>>) event -> {
                    editCell(event);
                }));

                taskView.getColumns().addAll(tableColumn);
            }

            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    row.add(dBconnection.getRs().getString(i));
                }
                data.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dBconnection.queryClose();
        dBconnection.closeDB();
        //Добавление данных в TableView
        taskView.setItems(data);
    }

    public void shortExecutorTablesLoad(String task) {

        shortExecuterView.setEditable(false);
        shortExecuterView.getSelectionModel().clearSelection();
        shortExecuterView.getColumns().clear();
        shortExecuterView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);

        Task<Integer> shortTableCalculate = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        shortViewPane.setExpanded(true);
                        summaryViewPane.setExpanded(false);
                        progressIndicator.progressProperty().addListener((ov, oldValue, newValue) -> {
                            Text text = (Text) progressIndicator.lookup(".percentage");
                            if (text != null && text.getText().equals("Done")) {
                                text.setText("Загружено");
                                progressIndicator.setPrefWidth(text.getLayoutBounds().getWidth());
                            }
                        });

                        double size = 0;
                        double counter = 0;

                        dBconnection.openDB();
                        shortExecuterView.getColumns().clear();
                        specDataExecutor.clear();
                        dBconnection.query(
                                "SELECT u.user_fullname as executor, min(dw.daily_work_date) min_start, max(dw.daily_work_date) max_start, sum(dw.daily_intensity) FROM public.task t\n" +
                                        " join public.stage s on s.task_id = t.task_id\n" +
                                        " join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                        " join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                        " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                        " join public.user u on u.user_id = dw.user_id\n" +
                                        " WHERE t.task_number = '" + task + "'\n" +
                                        " GROUP BY u.user_fullname");
                        try {
                            for (int i = 0; i < dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                                final int j = i;
                                tableColName = dBconnection.getRs().getMetaData().getColumnName(i + 1);
                                generateColName(dBconnection.getRs().getMetaData().getColumnName(i + 1));
                                TableColumn tableColumn = new TableColumn(tableColName);
                                tableColumn.setSortable(false);

                                if (!dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("executor")) {
                                    tableColumn.setStyle("-fx-alignment: CENTER;");
                                } else {

                                }

                                tableColumn.setCellValueFactory(
                                        (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(
                                                (String) param.getValue().get(j)));

                                shortExecuterView.getColumns().addAll(tableColumn);
                            }

                            dBconnection.setServiceRs(dBconnection.getRs());

                            while (dBconnection.getRs().next()) {
                                size += 1;
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        dBconnection.query(
                                "SELECT u.user_fullname, min(dw.daily_work_date) min_start, max(dw.daily_work_date) max_start, sum(dw.daily_intensity) FROM public.task t\n" +
                                        " join public.stage s on s.task_id = t.task_id\n" +
                                        " join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                        " join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                        " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                        " join public.user u on u.user_id = dw.user_id\n" +
                                        " WHERE t.task_number = '" + task + "'\n" +
                                        " GROUP BY u.user_fullname");

                        Double sum = 0.0;

                        try {
                            while (dBconnection.getRs().next()) {
                                ObservableList<String> row = FXCollections.observableArrayList();
                                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                                    row.add(dBconnection.getRs().getString(i));
                                    if (i == dBconnection.getRs().getMetaData().getColumnCount()) {
                                        sum = sum + Double.parseDouble(dBconnection.getRs().getString(i));
                                    }

                                    counter += 1;
                                }
                                specDataExecutor.add(row);

                                updateProgress(counter, size);
                            }

                            ObservableList<String> row = FXCollections.observableArrayList();
                            for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                                if (i == dBconnection.getRs().getMetaData().getColumnCount() - 1) {
                                    row.add("Итого");
                                } else if (i == dBconnection.getRs().getMetaData().getColumnCount()) {
                                    row.add("" + String.format("%.1f", sum));
                                } else {
                                    row.add("");
                                }
                            }
                            if (sum > 0) {
                                specDataExecutor.add(row);
                            }

                            sum = 0.0;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        dBconnection.queryClose();
                        dBconnection.closeDB();

                        shortExecuterView.setItems(specDataExecutor);
                    }
                });
                return 0;
            }
        };
        progressIndicator.progressProperty().bind(shortTableCalculate.progressProperty());
        new Thread(shortTableCalculate).start();
    }

    public void shortTablesLoad(String task) {
        shortStageView.setEditable(false);
        shortStageView.getSelectionModel().clearSelection();
        shortStageView.getColumns().clear();
        shortStageView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);

        Task<Integer> shortTableCalculate = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        // shortViewPane.setExpanded(true);
                        // summaryViewPane.setExpanded(false);
                        progressIndicator.progressProperty().addListener((ov, oldValue, newValue) -> {
                            Text text = (Text) progressIndicator.lookup(".percentage");
                            if (text != null && text.getText().equals("Done")) {
                                text.setText("Загружено");
                                progressIndicator.setPrefWidth(text.getLayoutBounds().getWidth());
                            }
                        });

                        double size = 0;
                        double counter = 0;

                        dBconnection.openDB();
                        shortStageView.getColumns().clear();
                        specData.clear();
                        dBconnection.query(
                                "SELECT u.user_fullname, st.stage_type_name, min(dw.daily_work_date) min_start, max(dw.daily_work_date) max_start, sum(dw.daily_intensity) FROM public.task t " +
                                        "join public.stage s on s.task_id = t.task_id " +
                                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                                        "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                                        "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                                        "join public.user u on u.user_id = dw.user_id " +
                                        "WHERE t.task_number = '" + task +
                                        "' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' " +
                                        "OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' " +
                                        "OR st.stage_type_name = 'Утверждение') " +
                                        "GROUP BY u.user_fullname, st.stage_type_name, st.stage_type_id " +
                                        "ORDER BY st.stage_type_id");
                        try {
                            for (int i = 0; i < dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                                final int j = i;
                                tableColName = dBconnection.getRs().getMetaData().getColumnName(i + 1);
                                generateColName(dBconnection.getRs().getMetaData().getColumnName(i + 1));
                                TableColumn tableColumn = new TableColumn(tableColName);
                                tableColumn.setSortable(false);

                                if (!dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("user_fullname")) {
                                    tableColumn.setStyle("-fx-alignment: CENTER;");
                                } else {

                                }

                                tableColumn.setCellValueFactory(
                                        (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(
                                                (String) param.getValue().get(j)));

                                shortStageView.getColumns().addAll(tableColumn);
                            }

                            dBconnection.setServiceRs(dBconnection.getRs());

                            while (dBconnection.getRs().next()) {
                                size += 1;
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        dBconnection.query(
                                "SELECT u.user_fullname, st.stage_type_name, min(dw.daily_work_date), max(dw.daily_work_date), sum(dw.daily_intensity) FROM public.task t " +
                                        "join public.stage s on s.task_id = t.task_id " +
                                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                                        "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                                        "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                                        "join public.user u on u.user_id = dw.user_id " +
                                        "WHERE t.task_number = '" + task +
                                        "' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' " +
                                        "OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' " +
                                        "OR st.stage_type_name = 'Утверждение') " +
                                        "GROUP BY u.user_fullname, st.stage_type_id " +
                                        "ORDER BY st.stage_type_id");

                        Double sum = 0.0;

                        try {
                            while (dBconnection.getRs().next()) {
                                ObservableList<String> row = FXCollections.observableArrayList();
                                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                                    row.add(dBconnection.getRs().getString(i));
                                    if (i == dBconnection.getRs().getMetaData().getColumnCount()) {
                                        sum = sum + Double.parseDouble(dBconnection.getRs().getString(i));
                                    }

                                    counter += 1;
                                }
                                specData.add(row);

                                updateProgress(counter, size);
                            }

                            ObservableList<String> row = FXCollections.observableArrayList();
                            for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                                if (i == dBconnection.getRs().getMetaData().getColumnCount() - 1) {
                                    row.add("Итого");
                                } else if (i == dBconnection.getRs().getMetaData().getColumnCount()) {
                                    row.add("" + String.format("%.1f", sum));
                                } else {
                                    row.add("");
                                }
                            }
                            if (sum > 0) {
                                specData.add(row);
                            }

                            sum = 0.0;
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        dBconnection.queryClose();
                        dBconnection.closeDB();

                        shortStageView.setItems(specData);
                    }
                });
                return 0;
            }
        };
        progressIndicator.progressProperty().bind(shortTableCalculate.progressProperty());
        new Thread(shortTableCalculate).start();
    }

    private void summaryViewTablesLoadHideEmpty(String task) {
        taskNumber = task;
        Task<Integer> summaryTableCalculate = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        // summaryViewPane.setExpanded(true);
                        shortViewPane.setExpanded(false);
                        progressIndicator.progressProperty().addListener((ov, oldValue, newValue) -> {
                            Text text = (Text) progressIndicator.lookup(".percentage");
                            if (text != null && text.getText().equals("Done")) {
                                text.setText("Загружено");
                                progressIndicator.setPrefWidth(text.getLayoutBounds().getWidth());
                            }
                        });

                        double size = 0;
                        double counter = 0;

                        dBconnection.openDB();
                        summaryView.getColumns().clear();
                        specDataSummary.clear();
                        specDataSummaryByUser.clear();
                        ObservableList<String> row = FXCollections.observableArrayList();
                        TableColumn<ObservableList<String>, String>[] tableColumnDays = new TableColumn[0];

                        LocalDate startDt = LocalDate.now();
                        LocalDate prevDt = LocalDate.now();
                        int nextdayOfMonth = 0;
                        Integer daycount = 0;
                        //  if (allTime.isSelected()) {
                        dBconnection.query(
                                "SELECT count(distinct dw.daily_work_date) FROM\n" +
                                        " public.stage s\n" +
                                        " join public.task t on t.task_id = s.task_id\n" +
                                        " join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                        " join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                        " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                        " WHERE t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                        "' AND dw.daily_work_date <= '" + endDate.getValue() + "' ");
                        int maxcount = 0;
                        try {

                            while (dBconnection.getRs().next()) {
                                maxcount = Integer.parseInt(dBconnection.getRs().getString(1));
                                tableColumnDays = new TableColumn[maxcount + 4];
                            }
                        } catch (SQLException e) {

                        }
                        if (maxcount != 0) {
                            dBconnection.query(
                                    "SELECT dw.daily_work_date FROM\n" +
                                            " public.stage s\n" +
                                            " join public.task t on t.task_id = s.task_id\n" +
                                            " join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                            " join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                            " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                            " WHERE t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                            "' AND dw.daily_work_date <= '" + endDate.getValue() + "' " +
                                            " GROUP BY dw.daily_work_date");

                            ArrayList<String> dates = new ArrayList<>();
                            dates.add("");
                            dates.add("");
                            dates.add("");
                            try {
                                while (dBconnection.getRs().next()) {
                                    dates.add(dBconnection.getRs().getString(1));
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            dates.add("");
                            for (int i = 1; i <= tableColumnDays.length; i++) {
                                final int m = i;
                                if (i == 1) {
                                    tableColumnDays[i - 1] = new TableColumn<>("№ п/п");
                                    tableColumnDays[i - 1].setSortable(false);
                                }
                                if (i == 2) {
                                    tableColumnDays[i - 1] = new TableColumn<>("ФИО");
                                    tableColumnDays[i - 1].setSortable(false);
                                }
                                if (i == 3) {
                                    tableColumnDays[i - 1] = new TableColumn<>("Этап");
                                    tableColumnDays[i - 1].setSortable(false);
                                } else if ((i != 1) && (i != 2) && (i != tableColumnDays.length)) {
                                    String start_date = dates.get(i - 1);
                                    String year = start_date.substring(0, 4);
                                    String month = start_date.substring(5, 7);
                                    String day = start_date.substring(8);
                                    startDt = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                               /* startDate.setValue(startDt);
                                endDate.setValue(LocalDate.now());*/
                                    nextdayOfMonth = startDt.getDayOfMonth();
                                    if (prevDt == null || prevDt.getMonth().getValue() != startDt.getMonth().getValue() ||
                                            prevDt.getYear() != startDt.getYear()) {
                                        tableColumnDays[i - 1] = new TableColumn<>(
                                                "" + nextdayOfMonth + " " + months[startDt.getMonth().getValue() - 1] + " " + startDt.getYear());
                                    } else {
                                        tableColumnDays[i - 1] = new TableColumn<>("" + nextdayOfMonth);
                                    }
                                    tableColumnDays[i - 1].setSortable(false);
                                    prevDt = startDt;
                                } else if (i == tableColumnDays.length) {
                                    tableColumnDays[i - 1] = new TableColumn<>("общ");
                                    tableColumnDays[i - 1].setSortable(false);
                                }
                                row.add("");
                                tableColumnDays[i - 1].setCellValueFactory(param -> new SimpleStringProperty(
                                        param.getValue().get(m - 1)));
                            }
                            Collections.addAll(columns, tableColumnDays);

                            dBconnection.query(
                                    "SELECT st.stage_type_name FROM public.task t\n" +
                                            "join public.stage s on s.task_id = t.task_id\n" +
                                            "join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                            "join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                            "join public.user u on u.user_id = dw.user_id\n" +
                                            "WHERE t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                            "' AND dw.daily_work_date <= '" + endDate.getValue() + "' " +
                                            " GROUP BY st.stage_type_name, st.stage_type_id" +
                                            " ORDER BY st.stage_type_id");

                            ObservableList<String> stages = FXCollections.observableArrayList();
                            try {

                                while (dBconnection.getRs().next()) {
                                    stages.add(0, dBconnection.getRs().getString(1));
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }

                            String stage_name = "";
                            double mainTotal = 0.0;
                            ObservableList<String> mainsumr = FXCollections.observableArrayList(row);
                            for (int i = 0; i < mainsumr.size(); i++) {
                                if (i == 0) {
                                    continue;
                                }
                                if (i == 1) {
                                    mainsumr.set(i, "Итого");
                                    continue;
                                }
                                if (i == 2) {
                                    mainsumr.set(i, "");
                                    continue;
                                }
                                mainsumr.set(i, "0.0");
                            }
                            for (int s = 0; s < stages.size(); s++) {
                                ObservableList<String> sumr = FXCollections.observableArrayList(row);
                                for (int i = 0; i < sumr.size(); i++) {
                                    if (i == 0) {
                                        continue;
                                    }
                                    if (i == 1) {
                                        sumr.set(i, "Сумма");
                                        continue;
                                    }
                                    if (i == 2) {
                                        sumr.set(i, "");
                                        continue;
                                    }
                                    sumr.set(i, "0.0");
                                }
                                specDataSummary.add(0, sumr);
                                stage_name = stages.get(s);
                                dBconnection.query(
                                        "SELECT u.user_fullname, st.stage_type_name, dw.daily_work_date, dw.daily_intensity, sum(sum(dw.daily_intensity)) OVER (PARTITION BY u.user_fullname) FROM\n" +
                                                " public.stage s\n" +
                                                " join public.task t on t.task_id = s.task_id\n" +
                                                " join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                                " join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                                " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                                " join public.user u on u.user_id = dw.user_id\n" +
                                                " WHERE st.stage_type_name = '" + stage_name +
                                                "' AND t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                                "' AND dw.daily_work_date <= '" + endDate.getValue() + "' " +
                                                "GROUP BY u.user_fullname,st.stage_type_name, dw.daily_work_date, dw.daily_intensity");

                                try {
                                    String sum = "";
                                    double total = 0;
                                    int number = 0;
                                    int number_col = 3;
                                    user_fullname = "";
                                    String stageNameAdded = "";
                                    // boolean isAdded = false;

                                    while (dBconnection.getRs().next()) {
                                        if (user_fullname.equals("") || !user_fullname.equals(dBconnection.getRs().getString(1))) {
                                            sum = dBconnection.getRs().getString(5);
                                            total = Double.parseDouble(sum) + total;
                                            ObservableList<String> r = FXCollections.observableArrayList(row);
                                            specDataSummary.add(number, r);
                                            specDataSummary.get(number).set(row.size() - 1, sum);
                                            user_fullname = dBconnection.getRs().getString(1);
                                            if (!stageNameAdded.equals(dBconnection.getRs().getString(2)) || stageNameAdded.equals("")) {
                                                stageNameAdded = stage_name;
                                                specDataSummary.get(number).set(0, String.valueOf(stages.size() - s));
                                            }
                                            specDataSummary.get(number).set(1, user_fullname);
                                            specDataSummary.get(number).set(2, dBconnection.getRs().getString(2));
                                            number++;
                                            number_col = 3;
                                        }
                                        size += 1;
                                        String workDate = dBconnection.getRs().getString(3);
                                        for (int i = 3; i < dates.size(); i++) {
                                            if (dates.get(i).equals(workDate)) {
                                                specDataSummary.get(number - 1).set(i, dBconnection.getRs().getString(4));
                                                double sumValueDbl = Double.parseDouble(specDataSummary.get(number).get(i));
                                                double mainsumValueDbl = Double.parseDouble(mainsumr.get(i));
                                                String sumValue = specDataSummary.get(number - 1).get(i);
                                                if (sumValue.equals("")) {
                                                    sumValueDbl = 0;
                                                } else {
                                                    sumValueDbl = Double.parseDouble(sumValue) + sumValueDbl;
                                                }
                                                specDataSummary.get(number).set(i, String.valueOf(sumValueDbl));
                                                mainsumValueDbl = mainsumValueDbl + Double.parseDouble(sumValue);
                                                mainsumr.set(i, String.valueOf(mainsumValueDbl));
                                            }
                                        }

                                        counter += 1;
                                        updateProgress(counter, size);
                                        number_col++;
                                    }
                                    mainTotal = mainTotal + total;
                                    specDataSummary.get(number).set(row.size() - 1, String.valueOf(total));
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                            }
                            mainsumr.set(mainsumr.size() - 1, String.valueOf(mainTotal));
                            specDataSummary.add(mainsumr);
                        } else {

                            size = 100;
                            counter = 100;
                            updateProgress(counter, size);
                        }
                        dBconnection.queryClose();
                        dBconnection.closeDB();
                        summaryView.setItems(specDataSummary);
                    }
                });
                return 0;
            }
        };
        progressIndicator.progressProperty().

                bind(summaryTableCalculate.progressProperty());
        new

                Thread(summaryTableCalculate).

                start();
    }

    private void summaryViewTablesLoad(String task) {
        taskNumber = task;

        Task<Integer> summaryTableCalculate = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        //  summaryViewPane.setExpanded(true);
                        shortViewPane.setExpanded(false);
                        progressIndicator.progressProperty().addListener((ov, oldValue, newValue) -> {
                            Text text = (Text) progressIndicator.lookup(".percentage");
                            if (text != null && text.getText().equals("Done")) {
                                text.setText("Загружено");
                                progressIndicator.setPrefWidth(text.getLayoutBounds().getWidth());
                            }
                        });

                        double size = 0;
                        double counter = 0;

                        dBconnection.openDB();
                        summaryView.getColumns().clear();
                        specDataSummaryByUser.clear();
                        specDataSummary.clear();
                        dataSum.clear();
                        ObservableList<String> row = FXCollections.observableArrayList();
                        TableColumn<ObservableList<String>, String>[] tableColumnDays;
                        LocalDate startDt = LocalDate.now();
                        LocalDate prevDt = null;
                        int nextdayOfMonth = 0;
                        dBconnection.query(
                                "SELECT count(distinct dw.daily_work_date) FROM\n" +
                                        " public.stage s\n" +
                                        " join public.task t on t.task_id = s.task_id\n" +
                                        " join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                        " join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                        " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                        " WHERE t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                        "' AND dw.daily_work_date <= '" + endDate.getValue() + "' ");
                        int maxcount = 0;
                        try {

                            while (dBconnection.getRs().next()) {
                                maxcount = Integer.parseInt(dBconnection.getRs().getString(1));
                            }
                        } catch (SQLException e) {

                        }
                        if (maxcount != 0) {
                            if (allTime.isSelected()) {
                                int yearscount = 0;

                                dBconnection.query("SELECT  t.task_income_date FROM public.task t WHERE  t.task_number = '" + task + "'");
                                try {

                                    while (dBconnection.getRs().next()) {
                                        String start_date = dBconnection.getRs().getString(1);
                                        String year = start_date.substring(0, 4);
                                        String month = start_date.substring(5, 7);
                                        String day = start_date.substring(8);
                                        yearscount = LocalDate.now().getYear() - Integer.parseInt(year);
                                        startDt = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

                                        startDate.setValue(startDt);
                                        endDate.setValue(LocalDate.now());
                                        //    yearscount = endDate.getValue().getYear() - startDate.getValue().getYear();
                                        nextdayOfMonth = startDt.getDayOfMonth();
                                    }
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                                long dc = ChronoUnit.DAYS.between(startDate.getValue(), endDate.getValue());
                                tableColumnDays = new TableColumn[(int) dc + 25];
                            } else {
                                startDt = startDate.getValue();
                                nextdayOfMonth = startDate.getValue().getDayOfMonth();
                                Integer daycount = endDate.getValue().getDayOfMonth() - nextdayOfMonth + 1;
                                tableColumnDays = new TableColumn[daycount + 4];
                            }
                            for (int i = 1; i <= tableColumnDays.length; i++) {
                                final int m = i;
                                if (i == 1) {
                                    tableColumnDays[i - 1] = new TableColumn<>("№ п/п");
                                    tableColumnDays[i - 1].setSortable(false);
                                }
                                if (i == 2) {
                                    tableColumnDays[i - 1] = new TableColumn<>("ФИО");
                                    tableColumnDays[i - 1].setSortable(false);
                                }
                                if (i == 3) {
                                    tableColumnDays[i - 1] = new TableColumn<>("Этап");
                                    tableColumnDays[i - 1].setSortable(false);
                                } else if ((i != 1) && (i != 2) && (i != tableColumnDays.length)) {
                                    if (prevDt == null || prevDt.getMonth().getValue() != startDt.getMonth().getValue() ||
                                            prevDt.getYear() != startDt.getYear()) {
                                        tableColumnDays[i - 1] = new TableColumn<>(
                                                "" + nextdayOfMonth + " " + months[startDt.getMonth().getValue() - 1] + " " + startDt.getYear());
                                    } else {
                                        tableColumnDays[i - 1] = new TableColumn<>("" + nextdayOfMonth);
                                    }
                                    tableColumnDays[i - 1].setSortable(false);
                                    prevDt = startDt;
                                    startDt = startDt.plusDays(1);
                                    nextdayOfMonth = startDt.getDayOfMonth();
                                } else if (i == tableColumnDays.length) {
                                    tableColumnDays[i - 1] = new TableColumn<>("общ");
                                    tableColumnDays[i - 1].setSortable(false);
                                }
                                row.add("");
                                tableColumnDays[i - 1].setCellValueFactory(param -> new SimpleStringProperty(
                                        param.getValue().get(m - 1)));
                            }
                            Collections.addAll(columns, tableColumnDays);

                            dBconnection.query(
                                    "SELECT st.stage_type_name FROM public.task t\n" +
                                            "join public.stage s on s.task_id = t.task_id\n" +
                                            "join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                            "join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                            "join public.user u on u.user_id = dw.user_id\n" +
                                            "WHERE t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                            "' AND dw.daily_work_date <= '" + endDate.getValue() + "' " +
                                            " GROUP BY st.stage_type_name, st.stage_type_id" +
                                            " ORDER BY st.stage_type_id");

                            ObservableList<String> stages = FXCollections.observableArrayList();
                            try {

                                while (dBconnection.getRs().next()) {
                                    stages.add(0, dBconnection.getRs().getString(1));
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }

                            String stage_name = "";
                            double mainTotal = 0.0;
                            ObservableList<String> mainsumr = FXCollections.observableArrayList(row);
                            for (int i = 0; i < mainsumr.size(); i++) {
                                if (i == 0) {
                                    continue;
                                }
                                if (i == 1) {
                                    mainsumr.set(i, "Итого");
                                    continue;
                                }
                                if (i == 2) {
                                    mainsumr.set(i, "");
                                    continue;
                                }
                                mainsumr.set(i, "0.0");
                            }
                            for (int s = 0; s < stages.size(); s++) {
                                double total = 0;
                                ObservableList<String> sumr = FXCollections.observableArrayList(row);
                                for (int i = 0; i < sumr.size(); i++) {
                                    if (i == 0) {
                                        continue;
                                    }
                                    if (i == 1) {
                                        sumr.set(i, "Сумма");
                                        continue;
                                    }
                                    if (i == 2) {
                                        sumr.set(i, "");
                                        continue;
                                    }
                                    sumr.set(i, "0.0");
                                }
                                specDataSummary.add(0, sumr);
                                stage_name = stages.get(s);
                                dBconnection.query(
                                        "SELECT u.user_fullname, st.stage_type_name, dw.daily_work_date, dw.daily_intensity, sum(sum(dw.daily_intensity)) OVER (PARTITION BY u.user_fullname) FROM\n" +
                                                "                                                    public.stage s\n" +
                                                "                                                    join public.task t on t.task_id = s.task_id\n" +
                                                "                                                    join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                                "                                                    join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                                "                                                    join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                                "                                                    join public.user u on u.user_id = dw.user_id\n" +
                                                "                                                    WHERE st.stage_type_name = '" + stage_name +
                                                "' AND t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                                "' AND dw.daily_work_date <= '" + endDate.getValue() + "' " +
                                                "                                                    GROUP BY u.user_fullname,st.stage_type_name, dw.daily_work_date, dw.daily_intensity");

                                try {

                                    String sum = "";
                                    int number = 0;
                                    user_fullname = "";
                                    String stageNameAdded = "";
                                    // boolean isAdded = false;
                                    while (dBconnection.getRs().next()) {
                                        if (user_fullname.equals("") || !user_fullname.equals(dBconnection.getRs().getString(1))) {
                                            sum = dBconnection.getRs().getString(5);
                                            total = Double.parseDouble(sum) + total;
                                            ObservableList<String> r = FXCollections.observableArrayList(row);
                                            specDataSummary.add(number, r);
                                            specDataSummary.get(number).set(row.size() - 1, sum);
                                            user_fullname = dBconnection.getRs().getString(1);
                                            if (!stageNameAdded.equals(dBconnection.getRs().getString(2)) || stageNameAdded.equals("")) {
                                                stageNameAdded = stage_name;
                                                specDataSummary.get(number).set(0, String.valueOf(stages.size() - s));
                                            }
                                            specDataSummary.get(number).set(1, user_fullname);
                                            specDataSummary.get(number).set(2, dBconnection.getRs().getString(2));
                                            number++;
                                        }
                                        size += 1;
                                        if (sum.equals("")) {
                                            sum = dBconnection.getRs().getString(5);
                                            total = Double.parseDouble(sum) + total;
                                        }
                                        String workDate = dBconnection.getRs().getString(3);
                                        String year = workDate.substring(0, 4);
                                        String month = workDate.substring(5, 7);
                                        String day = workDate.substring(8);
                                        LocalDate wd = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                                        long p2 = ChronoUnit.DAYS.between(startDate.getValue(), wd);
                                        specDataSummary.get(number - 1).set((int) (p2 + 3), dBconnection.getRs().getString(4));
                                        double sumValueDbl = Double.parseDouble(specDataSummary.get(number).get((int) (p2 + 3)));
                                        double mainsumValueDbl = Double.parseDouble(mainsumr.get((int) (p2 + 3)));
                                        String sumValue = specDataSummary.get(number - 1).get((int) (p2 + 3));
                                        if (sumValue.equals("")) {
                                            sumValueDbl = 0;
                                        } else {
                                            sumValueDbl = Double.parseDouble(sumValue) + sumValueDbl;
                                        }
                                        specDataSummary.get(number).set((int) (p2 + 3), String.valueOf(sumValueDbl));
                                        mainsumValueDbl = mainsumValueDbl + Double.parseDouble(sumValue);
                                        mainsumr.set((int) (p2 + 3), String.valueOf(mainsumValueDbl));
                                        counter += 1;
                                        updateProgress(counter, size);
                                    }

                                    specDataSummary.get(number).set(row.size() - 1, String.valueOf(total));
                                    mainTotal = mainTotal + total;
                                    mainsumr.set(mainsumr.size() - 1, String.valueOf(mainTotal));
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                            }

                            specDataSummary.add(mainsumr);
                        } else {
                            size = 100;
                            counter = 100;
                            updateProgress(counter, size);
                        }
                        dBconnection.queryClose();
                        dBconnection.closeDB();
                        summaryView.setItems(specDataSummary);
                    }
                });
                return 0;
            }
        };
        progressIndicator.progressProperty().bind(summaryTableCalculate.progressProperty());
        new Thread(summaryTableCalculate).start();
    }

    private void summaryViewTablesLoadByUsersHideEmpty(String task) {
        taskNumber = task;
        Task<Integer> summaryTableCalculate = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        // summaryViewPane.setExpanded(true);
                        shortViewPane.setExpanded(false);
                        progressIndicator.progressProperty().addListener((ov, oldValue, newValue) -> {
                            Text text = (Text) progressIndicator.lookup(".percentage");
                            if (text != null && text.getText().equals("Done")) {
                                text.setText("Загружено");
                                progressIndicator.setPrefWidth(text.getLayoutBounds().getWidth());
                            }
                        });

                        double size = 0;
                        double counter = 0;

                        dBconnection.openDB();
                        summaryView.getColumns().clear();
                        specDataSummaryByUser.clear();
                        specDataSummary.clear();
                        ObservableList<String> row = FXCollections.observableArrayList();
                        TableColumn<ObservableList<String>, String>[] tableColumnDays = new TableColumn[0];

                        LocalDate startDt = LocalDate.now();
                        LocalDate prevDt = LocalDate.now();
                        int nextdayOfMonth = 0;
                        Integer daycount = 0;
                        //   if (allTime.isSelected()) {
                        dBconnection.query(
                                "SELECT count(distinct dw.daily_work_date) FROM\n" +
                                        " public.stage s\n" +
                                        " join public.task t on t.task_id = s.task_id\n" +
                                        " join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                        " join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                        " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                        " WHERE t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                        "' AND dw.daily_work_date <= '" + endDate.getValue() + "' ");
                        int maxcount = 0;
                        try {

                            while (dBconnection.getRs().next()) {
                                maxcount = Integer.parseInt(dBconnection.getRs().getString(1));
                                tableColumnDays = new TableColumn[maxcount + 3];
                            }
                        } catch (SQLException e) {

                        }
                        if (maxcount != 0) {
                            dBconnection.query(
                                    "SELECT dw.daily_work_date FROM\n" +
                                            " public.stage s\n" +
                                            " join public.task t on t.task_id = s.task_id\n" +
                                            " join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                            " join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                            " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                            " WHERE t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                            "' AND dw.daily_work_date <= '" + endDate.getValue() + "' " +
                                            " GROUP BY dw.daily_work_date");

                            ArrayList<String> datesByUsers = new ArrayList<>();
                            datesByUsers.add("");
                            datesByUsers.add("");
                            try {
                                while (dBconnection.getRs().next()) {
                                    datesByUsers.add(dBconnection.getRs().getString(1));
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            datesByUsers.add("");
                            for (int i = 1; i <= tableColumnDays.length; i++) {
                                final int m = i;
                                if (i == 1) {
                                    tableColumnDays[i - 1] = new TableColumn<>("№ п/п");
                                    tableColumnDays[i - 1].setSortable(true);
                                }
                                if (i == 2) {
                                    tableColumnDays[i - 1] = new TableColumn<>("ФИО");
                                    tableColumnDays[i - 1].setSortable(true);
                                } else if (i != 1 && i != tableColumnDays.length) {
                                    String start_date = datesByUsers.get(i - 1);
                                    String year = start_date.substring(0, 4);
                                    String month = start_date.substring(5, 7);
                                    String day = start_date.substring(8);
                                    startDt = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                                    // startDate.setValue(startDt);
                                    // endDate.setValue(LocalDate.now());
                                    nextdayOfMonth = startDt.getDayOfMonth();
                                    if (prevDt == null || prevDt.getMonth().getValue() != startDt.getMonth().getValue() ||
                                            prevDt.getYear() != startDt.getYear()) {
                                        tableColumnDays[i - 1] = new TableColumn<>(
                                                "" + nextdayOfMonth + " " + months[startDt.getMonth().getValue() - 1] + " " + startDt.getYear());
                                    } else {
                                        tableColumnDays[i - 1] = new TableColumn<>("" + nextdayOfMonth);
                                    }
                                    tableColumnDays[i - 1].setSortable(false);
                                    prevDt = startDt;
                                } else if (i == tableColumnDays.length) {
                                    tableColumnDays[i - 1] = new TableColumn<>("общ");
                                    tableColumnDays[i - 1].setSortable(false);
                                }

                                row.add("");
                                tableColumnDays[i - 1].setCellValueFactory(param -> new SimpleStringProperty(
                                        param.getValue().get(m - 1)));
                            }
                            row.add("");
                            Collections.addAll(columns, tableColumnDays);

                            dBconnection.query(
                                    "SELECT u.user_fullname FROM public.task t " +
                                            "join public.stage s on s.task_id = t.task_id " +
                                            "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                                            "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                                            "join public.user u on u.user_id = dw.user_id " +
                                            "WHERE t.task_number = '" + task + "' AND dw.daily_work_date >= '" + startDate.getValue() +
                                            "' AND dw.daily_work_date <= '" + endDate.getValue() + "' " +
                                            " AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' " +
                                            "OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' " +
                                            "OR st.stage_type_name = 'Утверждение') " +
                                            "GROUP BY u.user_fullname, t.task_name");

                            ObservableList<String> users = FXCollections.observableArrayList();
                            try {

                                while (dBconnection.getRs().next()) {
                                    users.add(dBconnection.getRs().getString(1));
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            ObservableList<String> sumr = FXCollections.observableArrayList(row);
                            for (int i = 0; i < sumr.size(); i++) {
                                if (i == 0) {
                                    continue;
                                }
                                if (i == 1) {
                                    sumr.set(i, "Итого");
                                    continue;
                                }
                                sumr.set(i, "0.0");
                            }
                            specDataSummaryByUser.add(sumr);
                            double total = 0;
                            for (int u = 0; u < users.size(); u++) {
                                user_fullname = users.get(u);

                                ObservableList<String> r = FXCollections.observableArrayList(row);

                                specDataSummaryByUser.add(u, r);

                                specDataSummaryByUser.get(u).set(0, String.valueOf(u + 1));
                                specDataSummaryByUser.get(u).set(1, user_fullname);
                                dBconnection.query(
                                        "SELECT u.user_fullname,  dw.daily_work_date, dw.daily_intensity, sum(sum(dw.daily_intensity)) OVER (PARTITION BY u.user_fullname) FROM\n" +
                                                "                                                    public.stage s\n" +
                                                "                                                    join public.task t on t.task_id = s.task_id\n" +
                                                "                                                    join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                                "                                                    join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                                "                                                    join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                                "                                                    join public.user u on u.user_id = dw.user_id\n" +
                                                "                                                    WHERE u.user_fullname = '" + user_fullname +
                                                "' AND t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                                "' AND dw.daily_work_date <= '" + endDate.getValue() + "' " +
                                                "                                                    GROUP BY u.user_fullname, dw.daily_work_date, dw.daily_intensity");

                                try {
                                    String sum = "";
                                    while (dBconnection.getRs().next()) {
                                        size += 1;
                                        if (sum.equals("")) {
                                            sum = dBconnection.getRs().getString(4);
                                            total = Double.parseDouble(sum) + total;
                                        }
                                        String workDate = dBconnection.getRs().getString(2);
                                        for (int i = 2; i < datesByUsers.size(); i++) {
                                            if (datesByUsers.get(i).equals(workDate)) {
                                                specDataSummaryByUser.get(u).set(i, dBconnection.getRs().getString(3));
                                                double sumValueDbl = Double.parseDouble(specDataSummaryByUser.get(u + 1).get(i));
                                                String sumValue = specDataSummaryByUser.get(u).get(i);
                                                if (sumValue.equals("")) {
                                                    sumValueDbl = 0;
                                                } else {
                                                    sumValueDbl = Double.parseDouble(sumValue) + sumValueDbl;
                                                }
                                                specDataSummaryByUser.get(u + 1).set(i, String.valueOf(sumValueDbl));
                                            }
                                        }
                                        counter += 1;
                                        updateProgress(counter, size);
                                    }
                                    specDataSummaryByUser.get(u).set(row.size() - 2, sum);
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                            }

                            specDataSummaryByUser.get(users.size()).set(row.size() - 2, String.valueOf(total));
                        } else {
                            size = 100;
                            counter = 100;
                            updateProgress(counter, size);
                        }
                        dBconnection.queryClose();
                        dBconnection.closeDB();
                        summaryView.setItems(specDataSummaryByUser);
                    }
                });
                return 0;
            }
        };
        progressIndicator.progressProperty().

                bind(summaryTableCalculate.progressProperty());
        new

                Thread(summaryTableCalculate).

                start();
    }

    private void summaryViewTablesLoadByUsers(String task) {

        taskNumber = task;
        Task<Integer> summaryTableCalculate = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        // summaryViewPane.setExpanded(true);
                        shortViewPane.setExpanded(false);
                        progressIndicator.progressProperty().addListener((ov, oldValue, newValue) -> {
                            Text text = (Text) progressIndicator.lookup(".percentage");
                            if (text != null && text.getText().equals("Done")) {
                                text.setText("Загружено");
                                progressIndicator.setPrefWidth(text.getLayoutBounds().getWidth());
                            }
                        });

                        double size = 0;
                        double counter = 0;

                        dBconnection.openDB();
                        summaryView.getColumns().clear();
                        specDataSummaryByUser.clear();
                        specDataSummary.clear();
                        ObservableList<String> row = FXCollections.observableArrayList();
                        TableColumn<ObservableList<String>, String>[] tableColumnDays;
                        LocalDate startDt = LocalDate.now();
                        LocalDate prevDt = null;
                        int nextdayOfMonth = 0;
                        dBconnection.query(
                                "SELECT count(distinct dw.daily_work_date) FROM\n" +
                                        " public.stage s\n" +
                                        " join public.task t on t.task_id = s.task_id\n" +
                                        " join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                        " join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                        " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                        " WHERE t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                        "' AND dw.daily_work_date <= '" + endDate.getValue() + "' ");
                        int maxcount = 0;
                        try {

                            while (dBconnection.getRs().next()) {
                                maxcount = Integer.parseInt(dBconnection.getRs().getString(1));
                            }
                        } catch (SQLException e) {

                        }
                        if (maxcount != 0) {
                            if (allTime.isSelected()) {
                                int yearscount = 0;

                                // LocalDate taskIncomeDate = LocalDate.now();
                                dBconnection.query("SELECT  t.task_income_date FROM public.task t WHERE  t.task_number = '" + task + "'");
                                try {

                                    while (dBconnection.getRs().next()) {
                                        String start_date = dBconnection.getRs().getString(1);
                                        String year = start_date.substring(0, 4);
                                        String month = start_date.substring(5, 7);
                                        String day = start_date.substring(8);
                                        startDt = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                                        startDate.setValue(startDt);
                                        endDate.setValue(LocalDate.now());
                                        //   yearscount = endDate.getValue().getYear() - startDate.getValue().getYear();
                                        nextdayOfMonth = startDt.getDayOfMonth();
                                    }
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                                long dc = ChronoUnit.DAYS.between(startDate.getValue(), endDate.getValue());
                                tableColumnDays = new TableColumn[(int) dc + 25];
                                //tableColumnDays = new TableColumn[370 * (yearscount + 1)];
                            } else {
                                startDt = startDate.getValue();
                                nextdayOfMonth = startDate.getValue().getDayOfMonth();
                                Integer daycount = endDate.getValue().getDayOfMonth() - nextdayOfMonth + 1;
                                tableColumnDays = new TableColumn[daycount + 3];
                            }
                            for (int i = 1; i <= tableColumnDays.length; i++) {
                                final int m = i;
                                if (i == 1) {
                                    tableColumnDays[i - 1] = new TableColumn<>("№ п/п");
                                    tableColumnDays[i - 1].setSortable(true);
                                }
                                if (i == 2) {
                                    tableColumnDays[i - 1] = new TableColumn<>("ФИО");
                                    tableColumnDays[i - 1].setSortable(true);
                                } else if (i != 1 && i != tableColumnDays.length) {
                                    if (prevDt == null || prevDt.getMonth().getValue() != startDt.getMonth().getValue() ||
                                            prevDt.getYear() != startDt.getYear()) {
                                        tableColumnDays[i - 1] = new TableColumn<>(
                                                "" + nextdayOfMonth + " " + months[startDt.getMonth().getValue() - 1] + " " + startDt.getYear());
                                    } else {
                                        tableColumnDays[i - 1] = new TableColumn<>("" + nextdayOfMonth);
                                    }

                                    tableColumnDays[i - 1].setSortable(false);
                                    prevDt = startDt;
                                    startDt = startDt.plusDays(1);
                                    nextdayOfMonth = startDt.getDayOfMonth();
                                } else if (i == tableColumnDays.length) {
                                    tableColumnDays[i - 1] = new TableColumn<>("общ");
                                    tableColumnDays[i - 1].setSortable(false);
                                }
                                row.add("");
                                tableColumnDays[i - 1].setCellValueFactory(param -> new SimpleStringProperty(
                                        param.getValue().get(m - 1)));
                            }
                            Collections.addAll(columns, tableColumnDays);

                            dBconnection.query(
                                    "SELECT u.user_fullname FROM public.task t " +
                                            "join public.stage s on s.task_id = t.task_id " +
                                            "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                                            "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                                            "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                                            "join public.user u on u.user_id = dw.user_id " +
                                            "WHERE t.task_number = '" + task + "' AND dw.daily_work_date >= '" + startDate.getValue() +
                                            "' AND dw.daily_work_date <= '" + endDate.getValue() + "' " +
                                            " AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' " +
                                            "OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' " +
                                            "OR st.stage_type_name = 'Утверждение') " +
                                            "GROUP BY u.user_fullname, t.task_name");

                            ObservableList<String> users = FXCollections.observableArrayList();
                            try {

                                while (dBconnection.getRs().next()) {
                                    users.add(dBconnection.getRs().getString(1));
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            ObservableList<String> sumr = FXCollections.observableArrayList(row);
                            for (int i = 0; i < sumr.size(); i++) {
                                if (i == 0) {
                                    continue;
                                }
                                if (i == 1) {
                                    sumr.set(i, "Итого");
                                    continue;
                                }
                                sumr.set(i, "0.0");
                            }
                            specDataSummaryByUser.add(sumr);
                            double total = 0;
                            for (int u = 0; u < users.size(); u++) {
                                user_fullname = users.get(u);
                                ObservableList<String> r = FXCollections.observableArrayList(row);

                                specDataSummaryByUser.add(u, r);

                                specDataSummaryByUser.get(u).set(0, String.valueOf(u + 1));
                                specDataSummaryByUser.get(u).set(1, user_fullname);
                                dBconnection.query(
                                        "SELECT u.user_fullname,  dw.daily_work_date, dw.daily_intensity, sum(sum(dw.daily_intensity)) OVER (PARTITION BY u.user_fullname) FROM\n" +
                                                "                                                    public.stage s\n" +
                                                "                                                    join public.task t on t.task_id = s.task_id\n" +
                                                "                                                    join public.stage_type st on st.stage_type_id = s.stage_type_id\n" +
                                                "                                                    join public.stage_daily sd on sd.stage_id = s.stage_id\n" +
                                                "                                                    join public.daily_work dw on dw.daily_work_id = sd.daily_work_id\n" +
                                                "                                                    join public.user u on u.user_id = dw.user_id\n" +
                                                "                                                    WHERE u.user_fullname = '" + user_fullname +
                                                "' AND t.task_number = '" + task + "'" + " AND dw.daily_work_date >= '" + startDate.getValue() +
                                                "' AND dw.daily_work_date <= '" + endDate.getValue() + "' " +
                                                "                                                    GROUP BY u.user_fullname, dw.daily_work_date, dw.daily_intensity");

                                try {
                                    String sum = "";
                                    while (dBconnection.getRs().next()) {
                                        size += 1;
                                        if (sum.equals("")) {
                                            sum = dBconnection.getRs().getString(4);
                                            total = Double.parseDouble(sum) + total;
                                        }

                                        String workDate = dBconnection.getRs().getString(2);
                                        String year = workDate.substring(0, 4);
                                        String month = workDate.substring(5, 7);
                                        String day = workDate.substring(8);
                                        LocalDate wd = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                                        long p2 = ChronoUnit.DAYS.between(startDate.getValue(), wd);
                                        specDataSummaryByUser.get(u).set((int) (p2 + 2), dBconnection.getRs().getString(3));
                                        double sumValueDbl = Double.parseDouble(specDataSummaryByUser.get(u + 1).get((int) (p2 + 2)));
                                        String sumValue = specDataSummaryByUser.get(u).get((int) (p2 + 2));
                                        if (sumValue.equals("")) {
                                            sumValueDbl = 0;
                                        } else {
                                            sumValueDbl = Double.parseDouble(sumValue) + sumValueDbl;
                                        }
                                        specDataSummaryByUser.get(u + 1).set((int) (p2 + 2), String.valueOf(sumValueDbl));
                                        counter += 1;
                                        updateProgress(counter, size);
                                    }
                                    specDataSummaryByUser.get(u).set(row.size() - 1, sum);
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                }
                            }
                            specDataSummaryByUser.get(users.size()).set(row.size() - 1, String.valueOf(total));
                        } else {
                            size = 100;
                            counter = 100;
                            updateProgress(counter, size);
                        }
                        dBconnection.queryClose();
                        dBconnection.closeDB();
                        summaryView.setItems(specDataSummaryByUser);
                    }
                });
                return 0;
            }
        };
        progressIndicator.progressProperty().

                bind(summaryTableCalculate.progressProperty());
        new

                Thread(summaryTableCalculate).

                start();
    }

    public void show(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) {
            return;
        }
        Button clickedButton = (Button) source;
        switch (clickedButton.getId()) {
            case "btnShow":
                summaryView.getColumns().clear();
                if (mode.equals("showByStages")) {
                    if (hideEmpty.isSelected()) {
                        summaryViewTablesLoadHideEmpty(taskNumber);
                    } else {
                        summaryViewTablesLoad(taskNumber);
                    }
                    System.out.println("showByStages");
                }
                if (mode.equals("showByUsers")) {
                    if (hideEmpty.isSelected()) {
                        summaryViewTablesLoadByUsersHideEmpty(taskNumber);
                    } else {
                        summaryViewTablesLoadByUsers(taskNumber);
                    }
                    System.out.println("showByUsers");
                }
                summaryViewPane.setExpanded(true);
                shortViewPane.setExpanded(false);
                break;
        }
    }

    private Integer checkMaximux() {
        Calendar myCal = date;
        myCal.set(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH), 1);
        int max_date = myCal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        return max_date;
    }

    private int defineMaxDayOfMonth(String value) {
        Calendar cal = Calendar.getInstance();
        cal.set(yearBox.getValue(), new TimeSheetController().returnMonth(value), 1);
        int res = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return res;
    }

    private String checkVal(String s) {
        if (s != null) {
            return s;
        } else {
            return "";
        }
    }

    private Double checkDoubleVal(String s) {
        if (s != null) {
            return Double.parseDouble(s);
        } else {
            return 0.0;
        }
    }

    public void savePerspective() {
        File propDir = new File(userprofile + "\\ASUPD\\config");

        if (!propDir.exists()) {
            try {
                propDir.mkdirs();
            } catch (SecurityException se) {

            }
        }

        try {
            Properties properties = new Properties();
            properties.setProperty("task_col_width", taskColWidth);
            properties.setProperty("stage_col_width", stageColWidth);
            properties.setProperty("com_col_width", commentColWidth);
            properties.setProperty("proc_start_col_width", procStartColWidth);
            properties.setProperty("day_col_width", dayColWidth);
            properties.setProperty("proc_end_col_width", procEndColWidth);

            File file = new File(userprofile + "\\ASUPD\\config\\sheetConfig.ini");
            FileOutputStream fileOut = new FileOutputStream(file);
            properties.store(fileOut, "Tabel Perspective");
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editCell(TableColumn.CellEditEvent<ObservableList, String> event) {
        String value = event.getNewValue();
        Integer columnIndex = event.getTablePosition().getColumn();
        try {
            String taskNumber = event.getRowValue().get(1).toString();
            String field = "";

            event.getRowValue().set(columnIndex, value);

            if (columnIndex == 3) {
                field = "req_date_start";
            } else {
                field = "req_date_end";
            }
            saveDate(taskNumber, field, value);
        } catch (NullPointerException npe) {

        }

        //        taskView.refresh();
        data.clear();
        dataSum.clear();
        taskView.getItems().clear();
        tableUpdate(sql);
    }

    private void saveDate(String taskNumber, String field, String value) {
        dBconnection.openDB();
        try {
            if (!value.equals("")) {
                dBconnection.getStmt().execute("UPDATE public.task SET " + field + " = '" + value + "' " +
                        "WHERE task_number = '" + taskNumber + "'");
            } else {
                dBconnection.getStmt().execute("UPDATE public.task SET " + field + " = null " +
                        "WHERE task_number = '" + taskNumber + "'");
            }
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    // генерация русскоязычных названий колонок таблицы
    private void generateColName(String bdColName) {
        if (bdColName.startsWith("day")) {
            tableColName = bdColName.substring(3);
        }
        switch (bdColName) {
            case "task_name":
                tableColName = "Наименование задачи";
                break;
            case "user_fullname":
                tableColName = "ФИО";
                break;
            case "executor":
                tableColName = "Исполнитель";
                break;
            case "task_number":
                tableColName = "Номер задачи";
                break;
            case "stage_type_name":
                tableColName = "Этап";
                break;
            case "row_number":
                tableColName = "№ п/п";
                break;
            case "req_start":
                tableColName = "Дата нач. план";
                break;
            case "req_end":
                tableColName = "Дата заверш. план";
                break;
            case "min_start":
                tableColName = "Дата нач. факт";
                break;
            case "max_start":
                tableColName = "Дата заверш. факт";
                break;
            case "delta_start":
                tableColName = "Δ нач.";
                break;
            case "delta_end":
                tableColName = "Δ заверш.";
                break;
            case "status_name":
                tableColName = "Статус";
                break;
            case "vip":
                tableColName = "%, вып.";
                break;
            case "kape":
                tableColName = "Кп";
                break;
            case "effect":
                tableColName = "Эффективность";
                break;
            case "delta":
                tableColName = "Δ, ч";
                break;
            case "task_unit_plan":
                tableColName = "Ед. план";
                break;
            case "task_unit_fact":
                tableColName = "Ед. факт";
                break;
            case "task_pa_intensity":
                tableColName = "Тр-ть план. внутр.";
                break;
            case "task_tz_intensity":
                tableColName = "Тр-ть план. внешн.";
                break;
            case "sum":
                tableColName = "Фактические затраты, ч";
                break;
            case "outsource":
                tableColName = "Аутсорс";
                break;
        }
    }

    private ObservableList data(String k) {
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
            errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return dataSelect;
    }

    private ObservableList specData(String k) {
        try {
            dBconnection.openDB();
            specData.clear();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                specData.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch (Exception e) {
            errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return specData;
    }

    public void formClear() {
        shortViewPane.setExpanded(true);
        taskView.getItems().clear();
        data.clear();
        dataSum.clear();
        dataSelect.clear();
        specData.clear();
        specDataSummary.clear();
        specDataSummaryByUser.clear();
        specDataExecutor.clear();
        taskField.clear();
        projectField.clear();
        statusField.clear();
        customerField.clear();
        contractField.clear();
        requestField.clear();
        requestDate.setValue(null);

        taskColWidth = "";
        stageColWidth = "";
        commentColWidth = "";
        procStartColWidth = "";
        dayColWidth = "";
        procEndColWidth = "";
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }
}