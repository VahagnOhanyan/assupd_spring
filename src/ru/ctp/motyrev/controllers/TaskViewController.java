package ru.ctp.motyrev.controllers;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.Properties;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
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
    private TitledPane shortViewPane;
    @FXML
    private ProgressIndicator progressIndicator;

    private String sql = "";
    private String sqlSum = "";
    private String tableColName;

    private String taskColWidth = "";
    private String stageColWidth= "";
    private String commentColWidth= "";
    private String procStartColWidth= "";
    private String dayColWidth= "";
    private String procEndColWidth= "";

    private GregorianCalendar date = new GregorianCalendar();

    String userprofile = System.getenv("USERPROFILE");

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
    private ObservableList<ObservableList> specData = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataSum = FXCollections.observableArrayList();

    @FXML
    private void initialize() {

    }

    public void initTaskView (String task) {
        formClear();

        data("SELECT r.request_number, r.request_description, cr.customer_name, c.contract_number, t.task_number, t.task_income_date, p.project_name, s.status_name FROM public.task t " +
                "left join public.request r on r.request_id = t.request_id " +
                "join public.project p on p.project_id = t.project_id " +
                "join public.contract_project cp on cp.project_id = p.project_id " +
                "join public.contract c on c.contract_id = cp.contract_id " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "join public.status s on s.status_id = t.status_id " +
                "WHERE t.task_number = '"+task+"'");

        taskField.setText(dataSelect.get(0).get(4).toString());

        customerField.setText(dataSelect.get(0).get(2).toString());
        contractField.setText(dataSelect.get(0).get(3).toString());

        if (dataSelect.get(0).get(0) != null) {
            requestField.setText(dataSelect.get(0).get(0).toString());
        }

        projectField.setText(dataSelect.get(0).get(6).toString());
        statusField.setText(dataSelect.get(0).get(7).toString());

        sql = "SELECT row_number() OVER(ORDER BY t.task_number), t.task_number, t.task_name, to_char(t.req_date_start, 'DD.MM.YYYY') req_start, to_char(t.req_date_end, 'DD.MM.YYYY') req_end, " +
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
                "WHERE t.task_number = '"+ task +"' " +
                "GROUP BY t.task_number, t.task_name, s.status_name, t.task_unit_plan, t.task_unit_fact, t.task_pa_intensity, t.task_tz_intensity, t.req_date_start, t.req_date_end, t.task_out " +
                "ORDER BY t.task_number";



        tableUpdate(sql);
        shortTablesLoad(task);
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
                tableColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty((String) param.getValue().get(j)));


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
                                public void updateItem(String item, boolean empty){
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
                                    }catch (NullPointerException nle) {

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
                                public void updateItem(String item, boolean empty){
                                    super.updateItem(item, empty);

                                    setPrefHeight(Control.USE_COMPUTED_SIZE);

                                    if (item == null || empty) {

                                    } else if (Double.parseDouble(item)<0){
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
                                        } else if (Double.parseDouble(getItem())<0){
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: DARKSALMON;-fx-alignment: CENTER;");
                                        } else {
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-alignment: CENTER;");
                                        }
                                    }catch (NullPointerException nle) {

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
                                public void updateItem(String item, boolean empty){
                                    super.updateItem(item, empty);

                                    setPrefHeight(Control.USE_COMPUTED_SIZE);

                                    if (item == null || empty) {

                                    } else if (Double.parseDouble(item)<0.9){
                                        setText(item);
                                        setTextFill(Color.BLACK);
                                        setStyle("-fx-background-color: DARKSALMON;-fx-alignment: CENTER;");
                                    } else if (Double.parseDouble(item)<1 & Double.parseDouble(getItem())>=0.9){
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
                                        } else if (Double.parseDouble(getItem())<0.9){
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: DARKSALMON;-fx-alignment: CENTER;");
                                        } else if (Double.parseDouble(getItem())<1 & Double.parseDouble(getItem())>=0.9){
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: yellow;-fx-alignment: CENTER;");
                                        } else {
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
                                        }
                                    }catch (NullPointerException nle) {

                                    }
                                }

                            };
                        }
                    });

                } else if (dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("delta_start") || dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("delta_end")) {
                    tableColumn.setCellFactory(new Callback<TableColumn, TableCell>() {

                        public TableCell call(TableColumn param) {

                            return new TableCell<ObservableList, String>() {

                                @Override
                                public void updateItem(String item, boolean empty){
                                    super.updateItem(item, empty);

                                    setPrefHeight(Control.USE_COMPUTED_SIZE);

                                    if (item == null || empty) {

                                    } else if (Integer.parseInt(item)<0){
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
                                        } else if (Integer.parseInt(getItem())<0){
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: DARKSALMON;-fx-alignment: CENTER;");
                                        } else {
                                            setText(getItem());
                                            setTextFill(Color.BLACK);
                                            setStyle("-fx-background-color: lightgreen;-fx-alignment: CENTER;");
                                        }
                                    }catch (NullPointerException nle) {

                                    }
                                }

                            };
                        }
                    });


                } else if (dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("req_start") || dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("req_end")) {
                    tableColumn.setCellFactory(column -> DateEditingCell.createStringEditCell());
                    tableColumn.setEditable(true);
                    tableColumn.setStyle( "-fx-alignment: CENTER;");
                } else {
                    tableColumn.setStyle("-fx-alignment: CENTER;");
                }

                tableColumn.setOnEditCommit(((EventHandler<TableColumn.CellEditEvent<ObservableList, String>>) event -> {
                    editCell(event);
                }));

                taskView.getColumns().addAll(tableColumn);
            }

            while(dBconnection.getRs().next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    row.add(dBconnection.getRs().getString(i));
                }
                data.add(row);
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        dBconnection.queryClose();
        dBconnection.closeDB();
        //Добавление данных в TableView
        taskView.setItems(data);
    }

    public void shortTablesLoad (String task) {
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
                        shortViewPane.setExpanded(true);

                        progressIndicator.progressProperty().addListener((ov, oldValue, newValue) -> {
                            Text text = (Text) progressIndicator.lookup(".percentage");
                            if(text!=null && text.getText().equals("Done")){
                                text.setText("Загружено");
                                progressIndicator.setPrefWidth(text.getLayoutBounds().getWidth());
                            }
                        });

                        double size = 0;
                        double counter = 0;

                        dBconnection.openDB();
                        specData.clear();
                        dBconnection.query("SELECT u.user_fullname, st.stage_type_name, min(dw.daily_work_date) min_start, max(dw.daily_work_date) max_start, sum(dw.daily_intensity) FROM public.task t " +
                                "join public.stage s on s.task_id = t.task_id " +
                                "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                                "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                                "join public.user u on u.user_id = dw.user_id " +
                                "WHERE t.task_number = '"+ task +"' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' " +
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

                                tableColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty((String) param.getValue().get(j)));

                                shortStageView.getColumns().addAll(tableColumn);
                            }

                            dBconnection.setServiceRs(dBconnection.getRs());

                            while(dBconnection.getRs().next()){
                                size += 1;
                            }

                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        dBconnection.query("SELECT u.user_fullname, st.stage_type_name, min(dw.daily_work_date), max(dw.daily_work_date), sum(dw.daily_intensity) FROM public.task t " +
                                "join public.stage s on s.task_id = t.task_id " +
                                "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                                "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                                "join public.user u on u.user_id = dw.user_id " +
                                "WHERE t.task_number = '"+ task +"' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' " +
                                "OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' " +
                                "OR st.stage_type_name = 'Утверждение') " +
                                "GROUP BY u.user_fullname, st.stage_type_name, st.stage_type_id " +
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

    public void savePerspective () {
        File propDir = new File(userprofile + "\\ASUPD\\config");

        if (!propDir.exists()) {
            try {
                propDir.mkdirs();
            } catch(SecurityException se){

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

    private void saveDate (String taskNumber, String field, String value) {
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
    private void generateColName (String bdColName) {
        switch (bdColName) {
            case "task_name":
                tableColName = "Наименование задачи";
                break;
            case "user_fullname":
                tableColName = "ФИО";
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
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                dataSelect.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch ( Exception e ) {
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
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                specData.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch ( Exception e ) {
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