package ru.ctp.motyrev.controllers;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.ctp.motyrev.code.DBconnection;
import ru.ctp.motyrev.code.DateEditingCell;
import ru.ctp.motyrev.code.EditCell;
import ru.ctp.motyrev.code.WeekEndCell;
import ru.ctp.motyrev.objects.TimeSheet;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.Properties;

import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

public class RequestViewController {

    @FXML
    private TextField customerField;
    @FXML
    private TextField contractField;
    @FXML
    private TextField requestField;
    @FXML
    private TextArea requestDescrArea;
    @FXML
    private DatePicker requestDate;
    @FXML
    private TableView requestView;
    @FXML
    private TableView requestViewSum;
    @FXML
    private Button dateSaveButton;
    @FXML
    private CheckBox mpCheck;
    @FXML
    private CheckBox aupCheck;
    @FXML
    private CheckBox approveCheck;
    @FXML
    private Label mpLabel;
    @FXML
    private Label aupLabel;
    @FXML
    private Label approveLabel;

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
        requestField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                requestDate.setEditable(false);
            } else {
                requestDate.setEditable(true);
            }
        });

        requestDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                dateSaveButton.setDisable(false);
            } else {
                dateSaveButton.setDisable(true);
            }
        });

        Image saveImage = new Image("/ru/ctp/motyrev/images/save.png");
        dateSaveButton.graphicProperty().setValue(new ImageView(saveImage));

        requestViewSum.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                // Get the table header
                Pane header = (Pane)requestViewSum.lookup("TableHeaderRow");
                if(header!=null && header.isVisible()) {
                    header.setMaxHeight(0);
                    header.setMinHeight(0);
                    header.setPrefHeight(0);
                    header.setVisible(false);
                    header.setManaged(false);
                }
            }
        });

        mpCheck.pressedProperty().addListener(observable ->  {
            confirmAlert.setTitle("Согласование");
            confirmAlert.setHeaderText(null);
            if (mpCheck.isSelected()) {
                if (!MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование с ролью Менеджер проекта?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    mpCheck.setSelected(false);
                    mpActionDelete();
                    editState();
                }
            } else {
                if (!MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                    confirmAlert.setContentText("Вы действительно хотите согласовать заявку с ролью Менеджер проекта?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите согласовать заявку?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    mpCheck.setSelected(true);
                    mpActionAdd();
                    editState();
                }
            }
            approveState();
        });

        aupCheck.pressedProperty().addListener(observable ->  {
            confirmAlert.setTitle("Согласование");
            confirmAlert.setHeaderText(null);
            if (aupCheck.isSelected()) {
                if (!MainController.role.equalsIgnoreCase("АУП")) {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование с ролью АУП?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    aupCheck.setSelected(false);
                    aupActionDelete();
                }
            } else {
                if (!MainController.role.equalsIgnoreCase("АУП")) {
                    confirmAlert.setContentText("Вы действительно хотите согласовать заявку с ролью АУП?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите согласовать заявку?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    aupCheck.setSelected(true);
                    aupActionAdd();
                }
            }
            approveState();
        });

        approveCheck.pressedProperty().addListener(observable ->  {
            confirmAlert.setTitle("Согласование");
            confirmAlert.setHeaderText(null);
            if (approveCheck.isSelected()) {
                if (!MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование с ролью Менеджер проекта?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    approveCheck.setSelected(false);
                    approveActionDelete();
                }
            } else {
                if (!MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                    confirmAlert.setContentText("Вы действительно хотите согласовать заявку с ролью Менеджер проекта?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите согласовать заявку?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    approveCheck.setSelected(true);
                    approveActionAdd();
                }
            }
            approveState();
        });
    }

    public void setScrollBarBinding() {
        bindScrollBars(requestView, requestViewSum, Orientation.HORIZONTAL);
    }

    public void initRequestView (String request) {
        formClear();

        data("SELECT r.request_number, r.request_description, cr.customer_name, c.contract_number FROM public.request r " +
                "join public.contract c on c.contract_id = r.contract_id " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "WHERE r.request_id = '"+request+"'");

        customerField.setText(dataSelect.get(0).get(2).toString());
        contractField.setText(dataSelect.get(0).get(3).toString());
        requestField.setText(dataSelect.get(0).get(0).toString());
        requestDescrArea.setWrapText(true);
        requestDescrArea.setText(dataSelect.get(0).get(1).toString());

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
                "join public.stage st on st.task_id = t.task_id " +
                "left join public.stage_daily sd on sd.stage_id = st.stage_id " +
                "left join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                "WHERE request_id = '"+ request +"' " +
                "GROUP BY t.task_number, t.task_name, s.status_name, t.task_unit_plan, t.task_unit_fact, t.task_pa_intensity, t.task_tz_intensity, t.req_date_start, t.req_date_end, t.task_out " +
                "ORDER BY t.task_number";

        sqlSum = "SELECT '-', 'Итого', '-', to_char(min(t.req_date_start), 'DD.MM.YYYY') req_start, to_char(max(t.req_date_end), 'DD.MM.YYYY') req_end, " +
                "to_char(min(dw.daily_work_date), 'DD.MM.YYYY') min_start, min(t.req_date_start)-min(dw.daily_work_date) delta_start, to_char(max(dw.daily_work_date), 'DD.MM.YYYY') max_start, " +
                "max(t.req_date_end)-max(dw.daily_work_date) delta_end, " +
                "(SELECT CASE WHEN min(s.status_hierarchy) = 1 THEN 'в ожидании' " +
                "WHEN min(s.status_hierarchy) = 2 THEN 'в работе' " +
                "WHEN min(s.status_hierarchy) = 3 THEN 'выполнено' " +
                "WHEN min(s.status_hierarchy) = 4 THEN 'проверено' " +
                "WHEN min(s.status_hierarchy) = 5 THEN 'утверждено' " +
                "ELSE 'задачи не назначены' " +
                "END FROM public.task tk join public.status s on s.status_id = tk.status_id WHERE tk.request_id = '"+ request +"'), " +
                "(SELECT sum(task_unit_plan) FROM public.task WHERE request_id = '"+ request +"'), " +
                "(SELECT sum(task_unit_fact) FROM public.task WHERE request_id = '"+ request +"'), " +
                "(round((SELECT sum(task_unit_fact) FROM public.task WHERE request_id = '"+ request +"')/nullif((SELECT sum(task_unit_plan) FROM public.task WHERE request_id = '"+ request +"'), 0), 2))*100 vip, " +
                "(SELECT sum(task_pa_intensity) FROM public.task WHERE request_id = '"+ request +"') sum_pa, (SELECT sum(task_tz_intensity) FROM public.task WHERE request_id = '"+ request +"') sum_tz, " +
                "round((SELECT sum(task_tz_intensity) FROM public.task WHERE request_id = '"+ request +"')/nullif((SELECT sum(task_pa_intensity) FROM public.task WHERE request_id = '"+ request +"'), 0), 2) KaPe, sum(dw.daily_intensity), " +
                "(CASE WHEN (SELECT sum(task_pa_intensity) FROM public.task WHERE request_id = '"+ request +"' AND task_out = true) != 0 THEN (SELECT sum(task_pa_intensity) FROM public.task WHERE request_id = '"+ request +"' AND task_out = true) ELSE 0 END) outsource, " +
                "round((SELECT sum(task_tz_intensity) FROM public.task WHERE request_id = '"+ request +"')/(nullif(sum(dw.daily_intensity), 0)+(CASE WHEN (SELECT sum(task_pa_intensity) FROM public.task WHERE request_id = '"+ request +"' AND task_out = true) != 0 THEN (SELECT sum(task_pa_intensity) FROM public.task WHERE request_id = '"+ request +"' AND task_out = true) ELSE 0 END)), 2) effect, " +
                "(SELECT sum(task_tz_intensity) FROM public.task WHERE request_id = '"+ request +"')-sum(dw.daily_intensity)-(CASE WHEN (SELECT sum(task_pa_intensity) FROM public.task WHERE request_id = '"+ request +"' AND task_out = true) != 0 THEN (SELECT sum(task_pa_intensity) FROM public.task WHERE request_id = '"+ request +"' AND task_out = true) ELSE 0 END) delta FROM public.task t " +
                "join public.stage st on st.task_id = t.task_id " +
                "join public.stage_daily sd on sd.stage_id = st.stage_id " +
                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                "WHERE t.request_id = '"+ request +"'";

        tableUpdate(sql, sqlSum);

        approveStatus();

        approveState();

    }

    @SuppressWarnings("rawtypes")
    private void bindScrollBars(TableView<?> tableView1, TableView<?> tableView2, Orientation orientation) {

        // Get the scrollbar of first table
        VirtualFlow vf = (VirtualFlow)tableView1.getChildrenUnmodifiable().get(1);
        ScrollBar scrollBar1 = null;
        for (final Node subNode: vf.getChildrenUnmodifiable()) {
            if (subNode instanceof ScrollBar &&
                    ((ScrollBar)subNode).getOrientation() == orientation) {
                scrollBar1 = (ScrollBar)subNode;
            }
        }

        // Get the scrollbar of second table
        vf = (VirtualFlow)tableView2.getChildrenUnmodifiable().get(1);
        ScrollBar scrollBar2 = null;
        for (final Node subNode: vf.getChildrenUnmodifiable()) {
            if (subNode instanceof ScrollBar &&
                    ((ScrollBar)subNode).getOrientation() == orientation) {
                scrollBar2 = (ScrollBar)subNode;
            }
        }

        // bind the hidden scrollbar valueProterty the visible scrollbar
        scrollBar1.valueProperty().bindBidirectional(scrollBar2.valueProperty());
    }

    private void tableUpdate(String sql, String sqlSum) {
/*        InputStream inp;
        Properties props = new Properties();

        try {
            inp = new FileInputStream(userprofile + "\\ASUPD\\config\\requestViewConfig.ini");
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
        dBconnection.serviceQuery(sqlSum);

        requestView.getSelectionModel().setCellSelectionEnabled(true);
        requestView.setEditable(true);
        requestView.getColumns().clear();
        requestView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);

        requestViewSum.getSelectionModel().setCellSelectionEnabled(true);
        requestViewSum.getColumns().clear();
        requestViewSum.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);

        try {
            for (int i = 0; i < dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                final int j = i;

                tableColName = dBconnection.getRs().getMetaData().getColumnName(i + 1);
                generateColName(dBconnection.getRs().getMetaData().getColumnName(i + 1));
                TableColumn tableColumn = new TableColumn(tableColName);
                tableColumn.setEditable(false);
                tableColumn.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty((String) param.getValue().get(j)));

                TableColumn tableColumn2 = new TableColumn(dBconnection.getServiceRs().getMetaData().getColumnName(i + 1));
                tableColumn2.setEditable(false);
                tableColumn2.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty((String) param.getValue().get(j)));

                if (dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("task_name")) {
                    tableColumn.setPrefWidth(400);
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

                    tableColumn2.setCellFactory(new Callback<TableColumn, TableCell>() {

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

                    tableColumn2.setCellFactory(new Callback<TableColumn, TableCell>() {

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

                    tableColumn2.setCellFactory(new Callback<TableColumn, TableCell>() {

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

                    tableColumn2.setCellFactory(new Callback<TableColumn, TableCell>() {

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

                if (!dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("delta") | !dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("effect") |
                        !dBconnection.getRs().getMetaData().getColumnName(i + 1).equals("status_name")) {
                    tableColumn2.setStyle("-fx-alignment: CENTER;");
                }

                tableColumn2.prefWidthProperty().bind(tableColumn.widthProperty());

                tableColumn.setOnEditCommit(((EventHandler<TableColumn.CellEditEvent<ObservableList, String>>) event -> {
                    editCell(event);
                }));

                requestView.getColumns().addAll(tableColumn);
                requestViewSum.getColumns().addAll(tableColumn2);
            }

            while(dBconnection.getRs().next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    row.add(dBconnection.getRs().getString(i));
                }
                data.add(row);
            }

            while(dBconnection.getServiceRs().next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getServiceRs().getMetaData().getColumnCount(); i++){
                    row.add(dBconnection.getServiceRs().getString(i));
                }
                dataSum.add(row);
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        dBconnection.queryClose();
        dBconnection.closeDB();
        //Добавление данных в TableView
        requestView.setItems(data);
        requestViewSum.setItems(dataSum);
    }

    private void approveStatus () {
        mpCheck.setSelected(false);
        aupCheck.setSelected(false);
        approveCheck.setSelected(false);

        specData.clear();
        specData("SELECT ra.mp_create, ra.aup_approve, ra.customer_approve, ra.mp_fullname, ra.aup_fullname, ra.employee_fullname FROM public.request_approve ra " +
                "join public.request r on r.request_id = ra.request_id " +
                "WHERE r.request_number = '" + requestField.getText() + "'");

        if (specData.size() > 0) {
            if (specData.get(0).get(0).equals("t")) {
                mpCheck.setSelected(true);
            }

            if (specData.get(0).get(1).equals("t")) {
                aupCheck.setSelected(true);
            }

            if (specData.get(0).get(2).equals("t")) {
                approveCheck.setSelected(true);
            }
        }
    }

    private void approveState() {
        mpLabel.setText("");
        mpLabel.setTextFill(Color.DARKBLUE);
        aupLabel.setText("");
        aupLabel.setTextFill(Color.DARKBLUE);
        approveLabel.setText("");
        approveLabel.setTextFill(Color.DARKBLUE);
        mpCheck.setDisable(true);
        aupCheck.setDisable(true);
        approveCheck.setDisable(true);

        specData.clear();
        specData("SELECT ra.mp_create, ra.aup_approve, ra.customer_approve, ra.mp_fullname, ra.aup_fullname, ra.employee_fullname FROM public.request_approve ra " +
                "join public.request r on r.request_id = ra.request_id " +
                "WHERE r.request_number = '" + requestField.getText() + "'");

        if ((MainController.role.equalsIgnoreCase("Менеджер проекта") | MainController.role.equalsIgnoreCase("АУП")) & !mpCheck.isSelected()) {
            mpCheck.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Менеджер проекта") & mpCheck.isSelected() & !aupCheck.isSelected()) {
            mpCheck.setDisable(false);
        }else if (MainController.role.equalsIgnoreCase("АУП") & mpCheck.isSelected() & !aupCheck.isSelected()) {
            mpCheck.setDisable(false);
            aupCheck.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Менеджер проекта") & mpCheck.isSelected() & aupCheck.isSelected() & !approveCheck.isSelected()) {
            approveCheck.setDisable(false);
        }else if (MainController.role.equalsIgnoreCase("АУП") & mpCheck.isSelected() & aupCheck.isSelected() & !approveCheck.isSelected()) {
            aupCheck.setDisable(false);
            approveCheck.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Super_user") & approveCheck.isSelected() & aupCheck.isSelected() & mpCheck.isSelected()) {
            approveCheck.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Super_user") & !approveCheck.isSelected() & aupCheck.isSelected() & mpCheck.isSelected()) {
            approveCheck.setDisable(false);
            aupCheck.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Super_user") & !approveCheck.isSelected() & !aupCheck.isSelected() & mpCheck.isSelected()) {
            mpCheck.setDisable(false);
            aupCheck.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Super_user") & !approveCheck.isSelected() & !aupCheck.isSelected() & !mpCheck.isSelected()) {
            mpCheck.setDisable(false);
        }

        if (mpCheck.isSelected()) {
            requestView.setEditable(false);
        } else {
            requestView.setEditable(true);
        }


        if (specData.size() > 0) {
            if (specData.get(0).get(0).equals("t")) {
                if (specData.get(0).get(3) != null) {
                    mpLabel.setText(specData.get(0).get(3).toString());
                } else {
                    mpLabel.setText("Нет");
                }
            }

            if (specData.get(0).get(1).equals("t")) {
                if (specData.get(0).get(4) != null) {
                    aupLabel.setText(specData.get(0).get(4).toString());
                } else {
                    aupLabel.setText("Нет");
                }
            }

            if (specData.get(0).get(2).equals("t")) {
                if (specData.get(0).get(5) != null) {
                    approveLabel.setText(specData.get(0).get(5).toString());
                } else {
                    approveLabel.setText("Нет");
                }
            }
        }

    }

    private void mpActionAdd() {
        specData.clear();
        specData("SELECT ra.mp_create, ra.aup_approve, ra.customer_approve FROM public.request_approve ra " +
                "join public.request r on r.request_id = ra.request_id " +
                "WHERE r.request_number = '" + requestField.getText() + "'");

        dBconnection.openDB();
        try {
            if (specData.size() == 0) {
                dBconnection.getStmt().execute("INSERT INTO public.request_approve (request_id, mp_create, mp_fullname) VALUES " +
                        "((SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "'), 'true', '"+ MainController.who +"')");
            } else {
                dBconnection.getStmt().execute("UPDATE public.request_approve SET mp_create = 'true' " +
                        "WHERE request_id = (SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "')");
                dBconnection.getStmt().execute("UPDATE public.request_approve SET mp_fullname = '"+ MainController.who +"' " +
                        "WHERE request_id = (SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "')");
            }
            dBconnection.getC().commit();
            dBconnection.getStmt().execute("INSERT INTO public.request_approve_history (edit_user, user_role_id, decision, approver_role, approve_date, request_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Создано', " +
                    "'Менеджер проекта', current_timestamp, " +
                    "(SELECT ra.request_approve_id FROM public.request_approve ra " +
                    "join public.request r on r.request_id = ra.request_id " +
                    "WHERE r.request_number = '" + requestField.getText() + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void mpActionDelete() {
        dBconnection.openDB();
        try {
            dBconnection.getStmt().execute("UPDATE public.request_approve SET mp_create = 'false' " +
                    "WHERE request_id = (SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "')");
            dBconnection.getStmt().execute("INSERT INTO public.request_approve_history (edit_user, user_role_id, decision, approver_role, approve_date, request_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Отмена создания', " +
                    "'Менеджер проекта', current_timestamp, " +
                    "(SELECT ra.request_approve_id FROM public.request_approve ra " +
                    "join public.request r on r.request_id = ra.request_id " +
                    "WHERE r.request_number = '" + requestField.getText() + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void aupActionAdd() {
        specData.clear();
        specData("SELECT ra.mp_create, ra.aup_approve, ra.customer_approve FROM public.request_approve ra " +
                "join public.request r on r.request_id = ra.request_id " +
                "WHERE r.request_number = '" + requestField.getText() + "'");

        dBconnection.openDB();
        try {
            if (specData.size() == 0) {
                dBconnection.getStmt().execute("INSERT INTO public.request_approve (request_id, aup_approve, aup_fullname) VALUES " +
                        "((SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "'), 'true', '"+ MainController.who +"')");
            } else {
                dBconnection.getStmt().execute("UPDATE public.request_approve SET aup_approve = 'true' " +
                        "WHERE request_id = (SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "')");
                dBconnection.getStmt().execute("UPDATE public.request_approve SET aup_fullname = '"+ MainController.who +"' " +
                        "WHERE request_id = (SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "')");
            }
            dBconnection.getC().commit();
            dBconnection.getStmt().execute("INSERT INTO public.request_approve_history (edit_user, user_role_id, decision, approver_role, approve_date, request_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Согласовано ЦТП', " +
                    "'АУП', current_timestamp, " +
                    "(SELECT ra.request_approve_id FROM public.request_approve ra " +
                    "join public.request r on r.request_id = ra.request_id " +
                    "WHERE r.request_number = '" + requestField.getText() + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void aupActionDelete() {
        dBconnection.openDB();
        try {
            dBconnection.getStmt().execute("UPDATE public.request_approve SET aup_approve = 'false' " +
                    "WHERE request_id = (SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "')");
            dBconnection.getStmt().execute("INSERT INTO public.request_approve_history (edit_user, user_role_id, decision, approver_role, approve_date, request_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Отмена согласования ЦТП', " +
                    "'АУП', current_timestamp, " +
                    "(SELECT ra.request_approve_id FROM public.request_approve ra " +
                    "join public.request r on r.request_id = ra.request_id " +
                    "WHERE r.request_number = '" + requestField.getText() + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void approveActionAdd() {
        specData.clear();
        specData("SELECT ra.mp_create, ra.aup_approve, ra.customer_approve FROM public.request_approve ra " +
                "join public.request r on r.request_id = ra.request_id " +
                "WHERE r.request_number = '" + requestField.getText() + "'");

        dBconnection.openDB();
        try {
            if (specData.size() == 0) {
                dBconnection.getStmt().execute("INSERT INTO public.request_approve (request_id, customer_approve, employee_fullname) VALUES " +
                        "((SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "'), 'true', '"+ MainController.who +"')");
            } else {
                dBconnection.getStmt().execute("UPDATE public.request_approve SET customer_approve = 'true' " +
                        "WHERE request_id = (SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "')");
                dBconnection.getStmt().execute("UPDATE public.request_approve SET employee_fullname = '"+ MainController.who +"' " +
                        "WHERE request_id = (SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "')");
            }
            dBconnection.getC().commit();
            dBconnection.getStmt().execute("INSERT INTO public.request_approve_history (edit_user, user_role_id, decision, approver_role, approve_date, request_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Утверждено заказчиком', " +
                    "'МП/АУП', current_timestamp, " +
                    "(SELECT ra.request_approve_id FROM public.request_approve ra " +
                    "join public.request r on r.request_id = ra.request_id " +
                    "WHERE r.request_number = '" + requestField.getText() + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void approveActionDelete() {
        dBconnection.openDB();
        try {
            dBconnection.getStmt().execute("UPDATE public.request_approve SET customer_approve = 'false' " +
                    "WHERE request_id = (SELECT request_id FROM public.request WHERE request_number = '" + requestField.getText() + "')");
            dBconnection.getStmt().execute("INSERT INTO public.request_approve_history (edit_user, user_role_id, decision, approver_role, approve_date, request_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Отмена утверждения заказчиком', " +
                    "'МП/АУП', current_timestamp, " +
                    "(SELECT ra.request_approve_id FROM public.request_approve ra " +
                    "join public.request r on r.request_id = ra.request_id " +
                    "WHERE r.request_number = '" + requestField.getText() + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void editState() {
        requestView.setEditable(true);
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

//        requestView.refresh();
        data.clear();
        dataSum.clear();
        requestView.getItems().clear();
        requestViewSum.getItems().clear();
        tableUpdate(sql, sqlSum);
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
            case "task_number":
                tableColName = "Номер задачи";
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
        requestView.getItems().clear();
        requestViewSum.getItems().clear();
        data.clear();
        dataSum.clear();
        dataSelect.clear();
        specData.clear();
        customerField.clear();
        contractField.clear();
        requestField.clear();
        requestDate.setValue(null);
        requestDescrArea.clear();

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
