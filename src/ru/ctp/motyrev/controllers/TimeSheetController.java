package ru.ctp.motyrev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.ctp.motyrev.code.DBconnection;
import ru.ctp.motyrev.code.EditCell;
import ru.ctp.motyrev.code.WeekEndCell;
import ru.ctp.motyrev.interfaces.impls.CollectionTimeSheet;
import ru.ctp.motyrev.objects.TimeSheet;

import java.io.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

public class TimeSheetController {

    @FXML
    private Label userLbl;
    @FXML
    private Label dateLbl;
    @FXML
    private Button editBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Button forwardBtn;
    @FXML
    private Button btnShow;
    @FXML
    private Button btnTask;
    @FXML
    private TextArea noteTxt;
    @FXML
    private TextArea noteTxt2;
    @FXML
    private TextField txtAll;
    @FXML
    public ComboBox taskBox;
    @FXML
    private ComboBox stageBox;
    @FXML
    private ComboBox monthBox;
    @FXML
    private ComboBox yearBox;
    @FXML
    private Button addBtn;
    @FXML
    private Button changeBtn;
    @FXML
    private Button btnNotes;
    @FXML
    private TableView timeSheetView;
    @FXML
    private CheckBox checkLE;
    @FXML
    private CheckBox checkDH;
    @FXML
    private CheckBox checkPM;
    @FXML
    private Label leadLbl;
    @FXML
    private Label depHeadLbl;
    @FXML
    private Label managerLbl;

    private String user;
    private String workNum;
    private String workStage;
    private Integer work;

    private String taskColWidth = "";
    private String stageColWidth= "";
    private String commentColWidth= "";
    private String procStartColWidth= "";
    private String dayColWidth= "";
    private String procEndColWidth= "";

    String userprofile = System.getenv("USERPROFILE");

    GregorianCalendar date = new GregorianCalendar();
    GregorianCalendar date2 = new GregorianCalendar();

    private CollectionTimeSheet timeSheetList = new CollectionTimeSheet();

    SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat sdf3 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy");
    SimpleDateFormat sdf5 = new SimpleDateFormat("MM");


    DBconnection dBconnection = new DBconnection();

    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();
    private ObservableList<ObservableList> specData = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data2 = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data3 = FXCollections.observableArrayList();

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);


    private Parent fxmlComments;
    private FXMLLoader fxmlCommentsLoader = new FXMLLoader();
    private CommentsController commentsController;
    private Stage commentsStage;

    private Parent fxmlApproveHistory;
    private FXMLLoader fxmlApproveHistoryLoader = new FXMLLoader();
    private ApproveController approveHistoryController;
    private Stage approveHistoryStage;

    private Parent fxmlTaskPane;
    private FXMLLoader fxmlTaskPaneLoader = new FXMLLoader();
    private TaskPaneController taskPaneController;
    private Stage taskPaneStage;


    @FXML
    private void initialize() {

        noteTxt.setWrapText(true);
        noteTxt2.setWrapText(true);

        taskBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            addBtn.setDisable(true);
            noteTxt.clear();
            noteTxt.setDisable(true);
            if (!taskBox.getSelectionModel().isEmpty() && !newValue.equals(oldValue)) {
                stageBox.getItems().clear();
                if (taskBox.getSelectionModel().getSelectedItem().equals("[Не проектная]")) {
                    stageBox.getItems().setAll(data("SELECT user_stage_type_name FROM public.user_stage_type"));
                } else {
                    stageBox.getItems().setAll(data("SELECT stage_type_name FROM public.stage_type"));
                }

                stageBox.setDisable(false);
            } else if (taskBox.getSelectionModel().isEmpty()) {
                stageBox.getItems().clear();
                stageBox.setDisable(true);
            }
        });

        stageBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!stageBox.getSelectionModel().isEmpty() && !newValue.equals(oldValue)) {

                if (!taskBox.getSelectionModel().getSelectedItem().equals("[Не проектная]")) {
                    noteTxt.clear();
                    noteTxt.setDisable(false);
                    noteTxt.setText(data("SELECT stn.stage_note_text FROM public.stage_note stn " +
                            "join public.stage s on s.stage_id = stn.stage_id " +
                            "join public.user u on u.user_id = stn.user_id " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                            "WHERE t.task_number = '" + taskBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "' AND " +
                            "st.stage_type_name = '" + stageBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "' AND " +
                            "u.user_id_number = '"+ user.substring(1, user.indexOf(",")) +"' AND " +
                            "stn.stage_note_date = (SELECT max(stgn.stage_note_date) FROM public.stage_note stgn WHERE stgn.stage_note_date < '" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"' AND " +
                            "stgn.stage_id = s.stage_id AND stgn.user_id = u.user_id)").toString().replace("[", "").replace("]", ""));
                    if (noteTxt.getText().equals("null")) {
                        noteTxt.setText("");
                    }
                } else if (taskBox.getSelectionModel().getSelectedItem().equals("[Не проектная]")) {
//                    addBtn.setDisable(false);
                    noteTxt.clear();
                    noteTxt.setDisable(false);
                    noteTxt.setText(data("SELECT stn.user_stage_note_text FROM public.user_stage_note stn " +
                            "join public.user_stage s on s.user_stage_id = stn.user_stage_id " +
                            "join public.user u on u.user_id = stn.user_id " +
                            "join public.user_stage_type st on st.user_stage_type_id = s.user_stage_type_id " +
                            "WHERE st.user_stage_type_name = '" + stageBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "' AND " +
                            "u.user_id_number = '"+ user.substring(1, user.indexOf(",")) +"' AND " +
                            "stn.user_stage_note_date = (SELECT max(stgn.user_stage_note_date) FROM public.user_stage_note stgn WHERE stgn.user_stage_note_date < '" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"' AND " +
                            "stgn.user_stage_id = s.user_stage_id AND stgn.user_id = u.user_id)").toString().replace("[", "").replace("]", ""));

                    if (noteTxt.getText().equals("null")) {
                        noteTxt.setText("");
                    }
                } else {
                    addBtn.setDisable(false);
                }

                String selected = taskBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "");

                for (TimeSheet timeSheet : timeSheetList.getTimesheetlist()) {
                    if (timeSheet.getWork_num().equalsIgnoreCase(selected)) {
                        if (newValue.toString().equals("[" + timeSheet.getWork_stage() + "]")) {

                            infoAlert.setTitle("Не допустимое значение");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("Данный этап уже добавлен в табель");
                            infoAlert.showAndWait();

                            noteTxt.clear();
                            noteTxt.setDisable(true);
                            addBtn.setDisable(true);
                            break;
                        }
                    }
                }

            }
        });

        noteTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 4) {
                addBtn.setDisable(false);
            } else {
                addBtn.setDisable(true);
            }
        });

        fxmlCommentsLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/comments.fxml"));
        try {
            fxmlComments = fxmlCommentsLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        commentsController = fxmlCommentsLoader.getController();

        fxmlApproveHistoryLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/approveHistory.fxml"));
        try {
            fxmlApproveHistory = fxmlApproveHistoryLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        approveHistoryController = fxmlApproveHistoryLoader.getController();

        fxmlTaskPaneLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/taskPane.fxml"));
        try {
            fxmlTaskPane = fxmlTaskPaneLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        taskPaneController = fxmlTaskPaneLoader.getController();

        timeSheetView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!timeSheetView.getSelectionModel().isEmpty()) {
                for (TimeSheet timeSheet : timeSheetList.getTimesheetlist()) {
                    if (timeSheet.equals(timeSheetView.getSelectionModel().getSelectedItem()) & !timeSheet.getWork_num().equals("") & !timeSheet.getWork_num().equals("Готово")) {
                        btnNotes.setDisable(false);
                        workNum = timeSheet.getWork_num();
                        workStage = timeSheet.getWork_stage();
                    } else if (timeSheet.equals(timeSheetView.getSelectionModel().getSelectedItem()) & (timeSheet.getWork_num().equals("") || timeSheet.getWork_num().equals("Готово")
                    )){
                        btnNotes.setDisable(true);
                    }
                }
            }
        });

        checkLE.pressedProperty().addListener(observable ->  {
            confirmAlert.setTitle("Согласование");
            confirmAlert.setHeaderText(null);
            if (checkLE.isSelected()) {
                if (!MainController.role.equalsIgnoreCase("Ведущий специалист")) {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование с ролью Ведущий специалист?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    checkLE.setSelected(false);
                    leadActionDelete();
                    editState();
                }
            } else {
                if (!MainController.role.equalsIgnoreCase("Ведущий специалист")) {
                    confirmAlert.setContentText("Вы действительно хотите согласовать табель с ролью Ведущий специалист?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите согласовать табель?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    checkLE.setSelected(true);
                    leadActionAdd();
                    editState();
                }
            }
            approveState();
        });

        checkDH.pressedProperty().addListener(observable ->  {
            confirmAlert.setTitle("Согласование");
            confirmAlert.setHeaderText(null);
            if (checkDH.isSelected()) {
                if (!MainController.role.equalsIgnoreCase("Начальник отдела")) {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование с ролью Начальник отдела?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    checkDH.setSelected(false);
                    depHeadActionDelete();
                }
            } else {
                if (!MainController.role.equalsIgnoreCase("Начальник отдела")) {
                    confirmAlert.setContentText("Вы действительно хотите согласовать табель с ролью Начальник отдела?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите согласовать табель?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    checkDH.setSelected(true);
                    depHeadActionAdd();
                }
            }
            approveState();
        });

        checkPM.pressedProperty().addListener(observable ->  {
            confirmAlert.setTitle("Согласование");
            confirmAlert.setHeaderText(null);
            if (checkPM.isSelected()) {
                if (!MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование с ролью Менеджер проекта?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите отменить согласование?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    checkPM.setSelected(false);
                    managerActionDelete();
                }
            } else {
                if (!MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                    confirmAlert.setContentText("Вы действительно хотите согласовать табель с ролью Менеджер проекта?");
                } else {
                    confirmAlert.setContentText("Вы действительно хотите согласовать табель?");
                }
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.get() == ButtonType.OK) {
                    checkPM.setSelected(true);
                    managerActionAdd();
                }
            }
            approveState();
        });

        monthBox.getItems().addAll("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь");
        yearBox.getItems().addAll("2018", "2019", "2020", "2021");

    }

    public void showComments() {

        if (commentsStage == null) {
            commentsStage = new Stage();
            commentsStage.setTitle("Мои комментарии");
            commentsStage.setScene(new Scene(fxmlComments));
            commentsStage.setMinHeight(200);
            commentsStage.setMinWidth(630);
            commentsStage.setResizable(true);
            commentsStage.initModality(Modality.APPLICATION_MODAL);
        }
        commentsController.addData(workNum, workStage, user);
        commentsStage.showAndWait();
    }

    public void showApproveHistory() {

        if (approveHistoryStage == null) {
            approveHistoryStage = new Stage();
            approveHistoryStage.setTitle("История согласования");
            approveHistoryStage.setScene(new Scene(fxmlApproveHistory));
            approveHistoryStage.setMinHeight(300);
            approveHistoryStage.setMinWidth(1000);
            approveHistoryStage.setResizable(true);
            approveHistoryStage.initModality(Modality.APPLICATION_MODAL);
        }
        approveHistoryController.addData(user, sdf5.format(date.getTime()), sdf4.format(date.getTime()));
        approveHistoryStage.showAndWait();
    }

    public void showTaskPane() {

        if (taskPaneStage == null) {
            taskPaneStage = new Stage();
            taskPaneStage.setTitle("Выбор задачи");
            taskPaneStage.setScene(new Scene(fxmlTaskPane));
            taskPaneStage.setMinHeight(300);
            taskPaneStage.setMinWidth(1000);
            taskPaneStage.setResizable(true);
            taskPaneStage.initModality(Modality.APPLICATION_MODAL);
        }
        taskPaneController.addData(user);
        taskPaneStage.setOnHiding(arg0 -> chooseTask());
        taskPaneStage.showAndWait();
    }

    private void editState() {
        editBtn.setText("Редактировать");
        editBtn.setTextFill(Color.BLACK);
        taskBox.setDisable(true);
        taskBox.getSelectionModel().clearSelection();
        taskBox.getItems().clear();
        btnTask.setDisable(true);
        stageBox.setDisable(true);
        stageBox.getSelectionModel().clearSelection();
        stageBox.getItems().clear();
        noteTxt.setDisable(true);
        noteTxt.clear();
        timeSheetView.setEditable(false);
    }

    public void initTimeSheet (String user) {
        formClear();

        this.user = user;

        date.set(date2.get(GregorianCalendar.YEAR), date2.get(GregorianCalendar.MONTH), date2.get(GregorianCalendar.DAY_OF_MONTH));

        addData(date);

    }

    public void changeDate (ActionEvent actionEvent) {

        if (!monthBox.getSelectionModel().isEmpty() & !yearBox.getSelectionModel().isEmpty()) {
            formClear();

            date.set(Integer.parseInt(yearBox.getSelectionModel().getSelectedItem().toString()), returnMonth(monthBox.getSelectionModel().getSelectedItem().toString()), 1);

            addData(date);
        } else {
            infoAlert.setTitle("Пустое значение");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Выберите месяц и год");
            infoAlert.showAndWait();
        }

    }

    public void actionChangeDate (ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) {
            return;
        }
        Button clickedButton = (Button) source;

        switch (clickedButton.getId()) {
            case "backBtn":
                formClear();

                if (date.get(GregorianCalendar.MONTH) == 0) {
                    date.set(date.get(GregorianCalendar.YEAR)-1, 11, date.get(GregorianCalendar.DAY_OF_MONTH));
                } else {
                    date.set(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH)-1, date.get(GregorianCalendar.DAY_OF_MONTH));
                }
                addData(date);
                break;
            case "forwardBtn":
                formClear();

                if (date.get(GregorianCalendar.MONTH) == 11) {
                    date.set(date.get(GregorianCalendar.YEAR)+1, 0, date.get(GregorianCalendar.DAY_OF_MONTH));
                } else {
                    date.set(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH)+1, date.get(GregorianCalendar.DAY_OF_MONTH));
                }
                addData(date);
                break;
        }
    }

    private void addData (GregorianCalendar date) {

        if (!MainController.role.equals("Сотрудник")) {
            monthBox.setDisable(false);
            yearBox.setDisable(false);
            btnShow.setDisable(false);
            backBtn.setDisable(false);
            forwardBtn.setDisable(false);
        }

        userLbl.setTextFill(Color.BLUE);
        userLbl.setText(user.substring(user.indexOf(",")+2, user.lastIndexOf("]")));

        dateLbl.setTextFill(Color.RED);
        dateLbl.setText(sdf.format(date.getTime()) + " " + sdf4.format(date.getTime()));

        System.out.println("Старт работы табеля: " + sdf3.format(new Date()));

        fillCrossTab();

        System.out.println("Crosstab заполнен: " + sdf3.format(new Date()));

        fillCurrentTimeSheet();

        System.out.println("Timesheet заполнен: " + sdf3.format(new Date()));

        approveStatus();

        System.out.println("Статусы заполнены: " + sdf3.format(new Date()));

        approveState();

        System.out.println("Табель загружен: " + sdf3.format(new Date()));
    }

    private void approveStatus () {
        checkLE.setSelected(false);
        checkDH.setSelected(false);
        checkPM.setSelected(false);

        specData.clear();
        specData("SELECT ca.lead_approve, ca.dep_head_approve, ca.manager_approve, ca.lead_fullname, ca.dep_head_fullname, ca.manager_fullname FROM public.current_approve ca " +
                "join public.user u on u.user_id = ca.user_id " +
                "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'");

        if (specData.size() > 0) {
            if (specData.get(0).get(0).equals("t")) {
                checkLE.setSelected(true);
            }

            if (specData.get(0).get(1).equals("t")) {
                checkDH.setSelected(true);
            }

            if (specData.get(0).get(2).equals("t")) {
                checkPM.setSelected(true);
            }
        }
    }

    private void approveState() {
        leadLbl.setText("");
        leadLbl.setTextFill(Color.DARKBLUE);
        depHeadLbl.setText("");
        depHeadLbl.setTextFill(Color.DARKBLUE);
        managerLbl.setText("");
        managerLbl.setTextFill(Color.DARKBLUE);
        checkLE.setDisable(true);
        checkDH.setDisable(true);
        checkPM.setDisable(true);

        specData.clear();
        specData("SELECT ca.lead_approve, ca.dep_head_approve, ca.manager_approve, ca.lead_fullname, ca.dep_head_fullname, ca.manager_fullname FROM public.current_approve ca " +
                "join public.user u on u.user_id = ca.user_id " +
                "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'");

        if (MainController.role.equalsIgnoreCase("Ведущий специалист") & !checkLE.isSelected()) {
            checkLE.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Начальник отдела") & !checkLE.isSelected()) {
            checkLE.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Начальник отдела") & checkLE.isSelected() & !checkDH.isSelected()) {
            checkLE.setDisable(false);
            checkDH.setDisable(false);
        } else if ((MainController.role.equalsIgnoreCase("Менеджер проекта") | MainController.role.equalsIgnoreCase("АУП")) & !checkLE.isSelected()) {
            checkLE.setDisable(false);
        } else if ((MainController.role.equalsIgnoreCase("Менеджер проекта") | MainController.role.equalsIgnoreCase("АУП")) & checkLE.isSelected() & !checkDH.isSelected()) {
            checkLE.setDisable(false);
            checkDH.setDisable(false);
        } else if ((MainController.role.equalsIgnoreCase("Менеджер проекта") | MainController.role.equalsIgnoreCase("АУП")) & checkLE.isSelected() & checkDH.isSelected() & !checkPM.isSelected()) {
            checkDH.setDisable(false);
            checkPM.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Super_user") & checkPM.isSelected() & checkDH.isSelected() & checkLE.isSelected()) {
            checkPM.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Super_user") & !checkPM.isSelected() & checkDH.isSelected() & checkLE.isSelected()) {
            checkPM.setDisable(false);
            checkDH.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Super_user") & !checkPM.isSelected() & !checkDH.isSelected() & checkLE.isSelected()) {
            checkLE.setDisable(false);
            checkDH.setDisable(false);
        } else if (MainController.role.equalsIgnoreCase("Super_user") & !checkPM.isSelected() & !checkDH.isSelected() & !checkLE.isSelected()) {
            checkLE.setDisable(false);
        }

        if (checkLE.isSelected()) {
            editBtn.setDisable(true);
        } else {
            editBtn.setDisable(false);
        }

        if (specData.size() > 0) {
            if (specData.get(0).get(0).equals("t")) {
                if (specData.get(0).get(3) != null) {
                    leadLbl.setText(specData.get(0).get(3).toString());
                } else {
                    leadLbl.setText("Нет");
                }
            }

            if (specData.get(0).get(1).equals("t")) {
                if (specData.get(0).get(4) != null) {
                    depHeadLbl.setText(specData.get(0).get(4).toString());
                } else {
                    depHeadLbl.setText("Нет");
                }
            }

            if (specData.get(0).get(2).equals("t")) {
                if (specData.get(0).get(5) != null) {
                    managerLbl.setText(specData.get(0).get(5).toString());
                } else {
                    managerLbl.setText("Нет");
                }
            }
        }

    }

    private void leadActionAdd() {
        specData.clear();
        specData("SELECT ca.lead_approve, ca.dep_head_approve, ca.manager_approve FROM public.current_approve ca " +
                "join public.user u on u.user_id = ca.user_id " +
                "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'");

        dBconnection.openDB();
        try {
            if (specData.size() == 0) {
                dBconnection.getStmt().execute("INSERT INTO public.current_approve (user_id, sheet_month, sheet_year, lead_approve, lead_fullname) VALUES " +
                        "((SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "'), '" + sdf5.format(date.getTime()) + "', " +
                        "'" + sdf4.format(date.getTime()) + "', 'true', '"+ MainController.who +"')");
            } else {
                dBconnection.getStmt().execute("UPDATE public.current_approve SET lead_approve = 'true' " +
                        "WHERE sheet_month = '" + sdf5.format(date.getTime()) + "' AND sheet_year = '" + sdf4.format(date.getTime()) + "' AND " +
                        "user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "')");
                dBconnection.getStmt().execute("UPDATE public.current_approve SET lead_fullname = '"+ MainController.who +"' " +
                        "WHERE sheet_month = '" + sdf5.format(date.getTime()) + "' AND sheet_year = '" + sdf4.format(date.getTime()) + "' AND " +
                        "user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "')");
            }
            dBconnection.getC().commit();
            dBconnection.getStmt().execute("INSERT INTO public.approve_history (edit_user, user_role_id, decision, approver_role, approve_date, current_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Согласовано', " +
                    "'Ведущий специалист', current_timestamp, " +
                    "(SELECT ca.current_approve_id FROM public.current_approve ca " +
                    "join public.user u on u.user_id = ca.user_id " +
                    "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void leadActionDelete() {
        specData.clear();
        specData("SELECT ca.lead_approve, ca.dep_head_approve, ca.manager_approve FROM public.current_approve ca " +
                "join public.user u on u.user_id = ca.user_id " +
                "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'");

        dBconnection.openDB();
        try {
            dBconnection.getStmt().execute("UPDATE public.current_approve SET lead_approve = 'false' " +
                    "WHERE sheet_month = '" + sdf5.format(date.getTime()) + "' AND sheet_year = '" + sdf4.format(date.getTime()) + "' AND " +
                    "user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "')");
            dBconnection.getStmt().execute("INSERT INTO public.approve_history (edit_user, user_role_id, decision, approver_role, approve_date, current_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Отмена согласования', " +
                    "'Ведущий специалист', current_timestamp, " +
                    "(SELECT ca.current_approve_id FROM public.current_approve ca " +
                    "join public.user u on u.user_id = ca.user_id " +
                    "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void depHeadActionAdd() {
        specData.clear();
        specData("SELECT ca.lead_approve, ca.dep_head_approve, ca.manager_approve FROM public.current_approve ca " +
                "join public.user u on u.user_id = ca.user_id " +
                "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'");

        dBconnection.openDB();
        try {
            if (specData.size() == 0) {
                dBconnection.getStmt().execute("INSERT INTO public.current_approve (user_id, sheet_month, sheet_year, dep_head_approve, dep_head_fullname) VALUES " +
                        "((SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "'), '" + sdf5.format(date.getTime()) + "', " +
                        "'" + sdf4.format(date.getTime()) + "', 'true', '"+ MainController.who +"')");
            } else {
                dBconnection.getStmt().execute("UPDATE public.current_approve SET dep_head_approve = 'true' " +
                        "WHERE sheet_month = '" + sdf5.format(date.getTime()) + "' AND sheet_year = '" + sdf4.format(date.getTime()) + "' AND " +
                        "user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "')");
                dBconnection.getStmt().execute("UPDATE public.current_approve SET dep_head_fullname = '"+ MainController.who +"' " +
                        "WHERE sheet_month = '" + sdf5.format(date.getTime()) + "' AND sheet_year = '" + sdf4.format(date.getTime()) + "' AND " +
                        "user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "')");
            }
            dBconnection.getC().commit();
            dBconnection.getStmt().execute("INSERT INTO public.approve_history (edit_user, user_role_id, decision, approver_role, approve_date, current_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Согласовано', " +
                    "'Начальник отдела', current_timestamp, " +
                    "(SELECT ca.current_approve_id FROM public.current_approve ca " +
                    "join public.user u on u.user_id = ca.user_id " +
                    "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void depHeadActionDelete() {
        specData.clear();
        specData("SELECT ca.lead_approve, ca.dep_head_approve, ca.manager_approve FROM public.current_approve ca " +
                "join public.user u on u.user_id = ca.user_id " +
                "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'");

        dBconnection.openDB();
        try {
            dBconnection.getStmt().execute("UPDATE public.current_approve SET dep_head_approve = 'false' " +
                    "WHERE sheet_month = '" + sdf5.format(date.getTime()) + "' AND sheet_year = '" + sdf4.format(date.getTime()) + "' AND " +
                    "user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "')");
            dBconnection.getStmt().execute("INSERT INTO public.approve_history (edit_user, user_role_id, decision, approver_role, approve_date, current_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Отмена согласования', " +
                    "'Начальник отдела', current_timestamp, " +
                    "(SELECT ca.current_approve_id FROM public.current_approve ca " +
                    "join public.user u on u.user_id = ca.user_id " +
                    "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void managerActionAdd() {
        specData.clear();
        specData("SELECT ca.lead_approve, ca.dep_head_approve, ca.manager_approve FROM public.current_approve ca " +
                "join public.user u on u.user_id = ca.user_id " +
                "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'");

        dBconnection.openDB();
        try {
            if (specData.size() == 0) {
                dBconnection.getStmt().execute("INSERT INTO public.current_approve (user_id, sheet_month, sheet_year, manager_approve, manager_fullname) VALUES " +
                        "((SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "'), '" + sdf5.format(date.getTime()) + "', " +
                        "'" + sdf4.format(date.getTime()) + "', 'true', '"+ MainController.who +"')");
            } else {
                dBconnection.getStmt().execute("UPDATE public.current_approve SET manager_approve = 'true' " +
                        "WHERE sheet_month = '" + sdf5.format(date.getTime()) + "' AND sheet_year = '" + sdf4.format(date.getTime()) + "' AND " +
                        "user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "')");
                dBconnection.getStmt().execute("UPDATE public.current_approve SET manager_fullname = '"+ MainController.who +"' " +
                        "WHERE sheet_month = '" + sdf5.format(date.getTime()) + "' AND sheet_year = '" + sdf4.format(date.getTime()) + "' AND " +
                        "user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "')");
            }
            dBconnection.getC().commit();
            dBconnection.getStmt().execute("INSERT INTO public.approve_history (edit_user, user_role_id, decision, approver_role, approve_date, current_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Согласовано', " +
                    "'Менеджер проекта', current_timestamp, " +
                    "(SELECT ca.current_approve_id FROM public.current_approve ca " +
                    "join public.user u on u.user_id = ca.user_id " +
                    "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void managerActionDelete() {
        specData.clear();
        specData("SELECT ca.lead_approve, ca.dep_head_approve, ca.manager_approve FROM public.current_approve ca " +
                "join public.user u on u.user_id = ca.user_id " +
                "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'");

        dBconnection.openDB();
        try {
            dBconnection.getStmt().execute("UPDATE public.current_approve SET manager_approve = 'false' " +
                    "WHERE sheet_month = '" + sdf5.format(date.getTime()) + "' AND sheet_year = '" + sdf4.format(date.getTime()) + "' AND " +
                    "user_id = (SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "')");
            dBconnection.getStmt().execute("INSERT INTO public.approve_history (edit_user, user_role_id, decision, approver_role, approve_date, current_approve_id) VALUES " +
                    "('"+ MainController.who +"', " +
                    "(SELECT user_role_id FROM public.user_role WHERE user_role_name = '" + MainController.role + "'), 'Отмена согласования', " +
                    "'Менеджер проекта', current_timestamp, " +
                    "(SELECT ca.current_approve_id FROM public.current_approve ca " +
                    "join public.user u on u.user_id = ca.user_id " +
                    "WHERE ca.sheet_month = '"+ sdf5.format(date.getTime()) +"' AND ca.sheet_year = '"+ sdf4.format(date.getTime()) +"' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "'))");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    private void fillCurrentTimeSheet(){
        for (int i=0; i<data.size(); i++) {

            TimeSheet timeSheet = new TimeSheet();

            timeSheet.setWork_num(data.get(i).get(1).toString());
            timeSheet.setWork_stage(data.get(i).get(2).toString());
            timeSheet.setWork_note(checkVal((String) data.get(i).get(3)));
            timeSheet.setDay1(checkVal((String) data.get(i).get(4)));
            timeSheet.setDay2(checkVal((String) data.get(i).get(5)));
            timeSheet.setDay3(checkVal((String) data.get(i).get(6)));
            timeSheet.setDay4(checkVal((String) data.get(i).get(7)));
            timeSheet.setDay5(checkVal((String) data.get(i).get(8)));
            timeSheet.setDay6(checkVal((String) data.get(i).get(9)));
            timeSheet.setDay7(checkVal((String) data.get(i).get(10)));
            timeSheet.setDay8(checkVal((String) data.get(i).get(11)));
            timeSheet.setDay9(checkVal((String) data.get(i).get(12)));
            timeSheet.setDay10(checkVal((String) data.get(i).get(13)));
            timeSheet.setDay11(checkVal((String) data.get(i).get(14)));
            timeSheet.setDay12(checkVal((String) data.get(i).get(15)));
            timeSheet.setDay13(checkVal((String) data.get(i).get(16)));
            timeSheet.setDay14(checkVal((String) data.get(i).get(17)));
            timeSheet.setDay15(checkVal((String) data.get(i).get(18)));
            timeSheet.setDay16(checkVal((String) data.get(i).get(19)));
            timeSheet.setDay17(checkVal((String) data.get(i).get(20)));
            timeSheet.setDay18(checkVal((String) data.get(i).get(21)));
            timeSheet.setDay19(checkVal((String) data.get(i).get(22)));
            timeSheet.setDay20(checkVal((String) data.get(i).get(23)));
            timeSheet.setDay21(checkVal((String) data.get(i).get(24)));
            timeSheet.setDay22(checkVal((String) data.get(i).get(25)));
            timeSheet.setDay23(checkVal((String) data.get(i).get(26)));
            timeSheet.setDay24(checkVal((String) data.get(i).get(27)));
            timeSheet.setDay25(checkVal((String) data.get(i).get(28)));
            timeSheet.setDay26(checkVal((String) data.get(i).get(29)));
            timeSheet.setDay27(checkVal((String) data.get(i).get(30)));
            timeSheet.setDay28(checkVal((String) data.get(i).get(31)));
            timeSheet.setDay29(checkVal((String) data.get(i).get(32)));
            timeSheet.setDay30(checkVal((String) data.get(i).get(33)));
            timeSheet.setDay31(checkVal((String) data.get(i).get(34)));
            timeSheet.setEnd_perc(checkVal((String) data.get(i).get(35)));
            timeSheetList.add(timeSheet);
        }

        if (timeSheetList.getTimesheetlist().size() > 0) {
            tableUpdate();
            sumTimeSheet();
        }
    }

    private String checkVal(String s) {
        if (s != null) {
            return s;
        } else {
            return "";
        }

    }

    private Double checkVal2(String s) {
        if (s != null) {
            return Double.parseDouble(s);
        } else {
            return 0.0;
        }

    }

    private Double checkDoubleVal(String s) {
        if (s != null) {
            return Double.parseDouble(s);
        } else {
            return 0.0;
        }

    }

    public void allowEdit(ActionEvent actionEvent) throws SQLException {
        if (editBtn.getText().equalsIgnoreCase("редактировать")) {
            dBconnection.openDB();
            dBconnection.query("SELECT tv.timesheet_view_id, tv.edit_user, date_trunc('second', tv.edit_start) FROM public.timesheet_view tv " +
                        "join public.user u on u.user_id = tv.user_id " +
                        "WHERE u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "' " +
                        "AND tv.edit_start < current_timestamp AND edit_end > current_timestamp");
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                data3.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
            if (data3.size() == 0 || data3.get(0).get(1).toString().equals(MainController.who)) {

                dBconnection.openDB();
                dBconnection.getStmt().execute("INSERT INTO public.timesheet_view (user_id, edit_user, edit_start, edit_end) VALUES " +
                        "((SELECT user_id FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "'), " +
                        "'"+MainController.who+"', current_timestamp, current_timestamp + interval '3 hours')");
                dBconnection.getC().commit();
                dBconnection.closeDB();

                timeSheetView.setEditable(true);
                taskBox.getItems().add("[Не проектная]");
                taskBox.getItems().addAll(data("SELECT task_number FROM public.task t " +
                        "join public.task_executor te on te.task_id = t.task_id " +
                        "join public.user u on u.user_id = te.user_id " +
                        "join public.status s on s.status_id = t.status_id " +
                        "WHERE u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "' AND s.status_name != 'утверждено' " +
                        "ORDER BY task_number"));
                taskBox.setDisable(false);
                btnTask.setDisable(false);
                taskBox.getSelectionModel().select(0);
                taskBox.getSelectionModel().clearSelection();
                editBtn.setTextFill(Color.RED);
                editBtn.setText("Готово");
            } else {
                infoAlert.setTitle("Табель на редактировании");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("В данный момент табель редактирует " + data3.get(0).get(1).toString() + ", редактирование начато " + data3.get(0).get(2).toString());
                infoAlert.showAndWait();
            }
        } else {

            dBconnection.openDB();
            dBconnection.getStmt().executeUpdate("DELETE FROM public.timesheet_view WHERE edit_user = '"+MainController.who+"'");
            dBconnection.getC().commit();
            dBconnection.closeDB();

            editBtn.setTextFill(Color.BLACK);
            editBtn.setText("Редактировать");
            taskBox.getSelectionModel().select(0);
            taskBox.getSelectionModel().clearSelection();
            taskBox.getItems().clear();
            taskBox.setDisable(true);
            btnTask.setDisable(true);
            stageBox.getItems().clear();
            stageBox.setDisable(true);
            noteTxt.clear();
            noteTxt.setDisable(true);
            addBtn.setDisable(true);
            timeSheetView.setEditable(false);
            noteTxt2.setVisible(false);
            changeBtn.setVisible(false);
        }

        data3.clear();
    }

    public void addTimeSheet () {
        removeSum();

        TimeSheet timeSheet = new TimeSheet();

        timeSheet.setWork_num(taskBox.getSelectionModel().getSelectedItem().toString().replace("[","").replace("]",""));
        timeSheet.setWork_stage(stageBox.getSelectionModel().getSelectedItem().toString().replace("[","").replace("]",""));
        timeSheet.setWork_note(noteTxt.getText());

        dBconnection.openDB();
        if (taskBox.getSelectionModel().getSelectedItem().toString().equals("[Не проектная]")) {
            try {
                dBconnection.query("SELECT user_stage_id from public.user_stage us " +
                        "join public.user u on u.user_id = us.user_id " +
                        "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                        "WHERE u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "' " +
                        "AND ust.user_stage_type_name = '" + stageBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "'");
                if (!dBconnection.getRs().next()) {
                    dBconnection.getStmt().execute("INSERT INTO public.user_stage (user_id, user_stage_type_id, user_stage_note) " +
                            "WITH t1 AS (SELECT user_id from public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "'), " +
                            "t2 AS (SELECT user_stage_type_id FROM public.user_stage_type WHERE user_stage_type_name = '" + stageBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "') " +
                            "SELECT t1.user_id, t2.user_stage_type_id, '" + noteTxt.getText() + "' " +
                            "FROM t1, t2");

                    System.out.println("В первый заход");
                    dBconnection.getC().commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                dBconnection.getStmt().execute("UPDATE public.user_stage SET user_stage_note = '" + noteTxt.getText() + "' " +
                        "WHERE user_stage_id = (SELECT user_stage_id FROM public.user_stage s " +
                        "join public.user u on u.user_id = s.user_id " +
                        "join public.user_stage_type st on st.user_stage_type_id = s.user_stage_type_id " +
                        "WHERE u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "' AND " +
                        "st.user_stage_type_name = '" + stageBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "')");

                dBconnection.getStmt().execute("INSERT INTO public.user_stage_note (user_stage_note_date, user_stage_note_text, user_stage_id, user_id) VALUES " +
                        "(current_timestamp, '"+ noteTxt.getText() +"', (SELECT user_stage_id FROM public.user_stage s " +
                        "join public.user u on u.user_id = s.user_id " +
                        "join public.user_stage_type st on st.user_stage_type_id = s.user_stage_type_id " +
                        "WHERE u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "' AND " +
                        "st.user_stage_type_name = '" + stageBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "'), " +
                        "(SELECT user_id FROM public.user " +
                        "WHERE user_fullname = '" + MainController.who + "'))");

                System.out.println("Во второй заход");

                dBconnection.getC().commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            try {
                dBconnection.query("SELECT stage_id from public.stage s " +
                        "join public.task t on t.task_id = s.task_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "WHERE t.task_number = '" + taskBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "' " +
                        "AND st.stage_type_name = '" + stageBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "'");
                if (!dBconnection.getRs().next()) {
                    dBconnection.getStmt().execute("INSERT INTO public.stage (task_id, stage_type_id, stage_note) " +
                            "WITH t1 AS (SELECT task_id from public.task WHERE task_number = '" + taskBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "'), " +
                            "t2 AS (SELECT stage_type_id FROM public.stage_type WHERE stage_type_name = '" + stageBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "') " +
                            "SELECT t1.task_id, t2.stage_type_id, '" + noteTxt.getText() + "' " +
                            "FROM t1, t2");
                    dBconnection.getC().commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                dBconnection.getStmt().execute("UPDATE public.stage SET stage_note = '" + noteTxt.getText() + "' " +
                        "WHERE stage_id = (SELECT stage_id FROM public.stage s " +
                        "join public.task t on t.task_id = s.task_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "WHERE t.task_number = '" + taskBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "' AND " +
                        "st.stage_type_name = '" + stageBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "')");

                dBconnection.getStmt().execute("INSERT INTO public.stage_note (stage_note_date, stage_note_text, stage_id, user_id) VALUES " +
                        "(current_timestamp, '"+ noteTxt.getText() +"', (SELECT stage_id FROM public.stage s " +
                        "join public.task t on t.task_id = s.task_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "WHERE t.task_number = '" + taskBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "' AND " +
                        "st.stage_type_name = '" + stageBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "'), " +
                        "(SELECT user_id FROM public.user " +
                        "WHERE user_fullname = '" + MainController.who + "'))");

                dBconnection.getC().commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        dBconnection.closeDB();

        timeSheetList.add(timeSheet);

        taskBox.getSelectionModel().clearSelection();
        if (timeSheetList.getTimesheetlist().size() == 1) {
            tableUpdate();
        }

        sumTimeSheet();
    }

    private void sumTimeSheet () {

        if (!timeSheetList.getTimesheetlist().get(timeSheetList.getTimesheetlist().size()-1).getWork_stage().equals("Итого")) {

            try {
                dBconnection.openDB();
                data2.clear();
                dBconnection.query("SELECT * " +
                        "FROM crosstab(" +
                        "'SELECT ''Итого'', dw.day_num, sum(dw.daily_intensity) FROM " +
                        "public.daily_work dw " +
                        "join public.user u on u.user_id = dw.user_id " +
                        "WHERE dw.daily_work_date >= ''" + sdf2.format(date.getTime()) + "-01" + "'' AND dw.daily_work_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + "'' AND u.user_id_number = ''" + user.substring(1, user.indexOf(",")) + "''" +
                        "GROUP BY dw.day_num', " +
                        "'SELECT d from generate_series(1,31) d') " +
                        "AS (task_number text, day1 numeric, day2 numeric, day3 numeric, day4 numeric, day5 numeric, " +
                        "day6 numeric, day7 numeric, day8 numeric, day9 numeric, day10 numeric, " +
                        "day11 numeric, day12 numeric, day13 numeric, day14 numeric, day15 numeric, " +
                        "day16 numeric, day17 numeric, day18 numeric, day19 numeric, day20 numeric, " +
                        "day21 numeric, day22 numeric, day23 numeric, day24 numeric, day25 numeric, " +
                        "day26 numeric, day27 numeric, day28 numeric, day29 numeric, day30 numeric, day31 numeric)");
                while (dBconnection.getRs().next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                        //Перебор колонок
                        row.add(dBconnection.getRs().getString(i));
                    }
                    data2.add(row);
                }
                dBconnection.queryClose();
                dBconnection.closeDB();

                if (data2.size() != 0) {
                    TimeSheet nulTimeSheet = new TimeSheet();
                    timeSheetList.add(nulTimeSheet);

                    TimeSheet timeSheet = new TimeSheet();

                    txtAll.clear();
                    Double total = 0.0;

                    timeSheet.setWork_stage(data2.get(0).get(0).toString());
                    timeSheet.setDay1(checkVal((String) data2.get(0).get(1)));
                    total += checkDoubleVal((String) data2.get(0).get(1));
                    timeSheet.setDay2(checkVal((String) data2.get(0).get(2)));
                    total += checkDoubleVal((String) data2.get(0).get(2));
                    timeSheet.setDay3(checkVal((String) data2.get(0).get(3)));
                    total += checkDoubleVal((String) data2.get(0).get(3));
                    timeSheet.setDay4(checkVal((String) data2.get(0).get(4)));
                    total += checkDoubleVal((String) data2.get(0).get(4));
                    timeSheet.setDay5(checkVal((String) data2.get(0).get(5)));
                    total += checkDoubleVal((String) data2.get(0).get(5));
                    timeSheet.setDay6(checkVal((String) data2.get(0).get(6)));
                    total += checkDoubleVal((String) data2.get(0).get(6));
                    timeSheet.setDay7(checkVal((String) data2.get(0).get(7)));
                    total += checkDoubleVal((String) data2.get(0).get(7));
                    timeSheet.setDay8(checkVal((String) data2.get(0).get(8)));
                    total += checkDoubleVal((String) data2.get(0).get(8));
                    timeSheet.setDay9(checkVal((String) data2.get(0).get(9)));
                    total += checkDoubleVal((String) data2.get(0).get(9));
                    timeSheet.setDay10(checkVal((String) data2.get(0).get(10)));
                    total += checkDoubleVal((String) data2.get(0).get(10));
                    timeSheet.setDay11(checkVal((String) data2.get(0).get(11)));
                    total += checkDoubleVal((String) data2.get(0).get(11));
                    timeSheet.setDay12(checkVal((String) data2.get(0).get(12)));
                    total += checkDoubleVal((String) data2.get(0).get(12));
                    timeSheet.setDay13(checkVal((String) data2.get(0).get(13)));
                    total += checkDoubleVal((String) data2.get(0).get(13));
                    timeSheet.setDay14(checkVal((String) data2.get(0).get(14)));
                    total += checkDoubleVal((String) data2.get(0).get(14));
                    timeSheet.setDay15(checkVal((String) data2.get(0).get(15)));
                    total += checkDoubleVal((String) data2.get(0).get(15));
                    timeSheet.setDay16(checkVal((String) data2.get(0).get(16)));
                    total += checkDoubleVal((String) data2.get(0).get(16));
                    timeSheet.setDay17(checkVal((String) data2.get(0).get(17)));
                    total += checkDoubleVal((String) data2.get(0).get(17));
                    timeSheet.setDay18(checkVal((String) data2.get(0).get(18)));
                    total += checkDoubleVal((String) data2.get(0).get(18));
                    timeSheet.setDay19(checkVal((String) data2.get(0).get(19)));
                    total += checkDoubleVal((String) data2.get(0).get(19));
                    timeSheet.setDay20(checkVal((String) data2.get(0).get(20)));
                    total += checkDoubleVal((String) data2.get(0).get(20));
                    timeSheet.setDay21(checkVal((String) data2.get(0).get(21)));
                    total += checkDoubleVal((String) data2.get(0).get(21));
                    timeSheet.setDay22(checkVal((String) data2.get(0).get(22)));
                    total += checkDoubleVal((String) data2.get(0).get(22));
                    timeSheet.setDay23(checkVal((String) data2.get(0).get(23)));
                    total += checkDoubleVal((String) data2.get(0).get(23));
                    timeSheet.setDay24(checkVal((String) data2.get(0).get(24)));
                    total += checkDoubleVal((String) data2.get(0).get(24));
                    timeSheet.setDay25(checkVal((String) data2.get(0).get(25)));
                    total += checkDoubleVal((String) data2.get(0).get(25));
                    timeSheet.setDay26(checkVal((String) data2.get(0).get(26)));
                    total += checkDoubleVal((String) data2.get(0).get(26));
                    timeSheet.setDay27(checkVal((String) data2.get(0).get(27)));
                    total += checkDoubleVal((String) data2.get(0).get(27));
                    timeSheet.setDay28(checkVal((String) data2.get(0).get(28)));
                    total += checkDoubleVal((String) data2.get(0).get(28));
                    timeSheet.setDay29(checkVal((String) data2.get(0).get(29)));
                    total += checkDoubleVal((String) data2.get(0).get(29));
                    timeSheet.setDay30(checkVal((String) data2.get(0).get(30)));
                    total += checkDoubleVal((String) data2.get(0).get(30));
                    timeSheet.setDay31(checkVal((String) data2.get(0).get(31)));
                    total += checkDoubleVal((String) data2.get(0).get(31));
                    timeSheetList.add(timeSheet);

                    txtAll.setText(total.toString());

                }

            } catch (Exception e) {
                errorAlert.setTitle("Ошибка data");
                errorAlert.setContentText(e.getMessage());
                errorAlert.showAndWait();
                e.printStackTrace();
            }
        }
    }


    private void tableUpdate() {

        InputStream inp;
        Properties props = new Properties();

        try {
            inp = new FileInputStream(userprofile + "\\ASUPD\\config\\sheetConfig.ini");
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
        }


        timeSheetView.getSelectionModel().setCellSelectionEnabled(true);
        timeSheetView.getColumns().clear();
        timeSheetView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);

        TableColumn tableCol1 = new TableColumn("Задача");
        if (taskColWidth.equals("")) {
            tableCol1.setPrefWidth(200);
        } else {
            tableCol1.setPrefWidth(Double.parseDouble(taskColWidth));
        }
        tableCol1.setCellValueFactory(new PropertyValueFactory<TimeSheet, String>("work_num"));
        tableCol1.setSortable(false);
        tableCol1.widthProperty().addListener((observable, oldValue, newValue) -> {
            taskColWidth = newValue.toString();
        });

        TableColumn tableCol2 = new TableColumn("Этап");
        if (stageColWidth.equals("")) {
            tableCol2.setPrefWidth(160);
        } else {
            tableCol2.setPrefWidth(Double.parseDouble(stageColWidth));
        }
        tableCol2.setCellValueFactory(new PropertyValueFactory<TimeSheet, String>("work_stage"));
        tableCol2.setSortable(false);
        tableCol2.widthProperty().addListener((observable, oldValue, newValue) -> {
            stageColWidth = newValue.toString();
        });

        TableColumn tableCol3 = new TableColumn("Комментарий");
        if (commentColWidth.equals("")) {
            tableCol3.setPrefWidth(300);
        } else {
            tableCol3.setPrefWidth(Double.parseDouble(commentColWidth));
        }
        tableCol3.setCellValueFactory(new PropertyValueFactory<TimeSheet, String>("work_note"));

        tableCol3.widthProperty().addListener((observable, oldValue, newValue) -> {
            commentColWidth = newValue.toString();
        });

        tableCol3.setCellFactory(new Callback<TableColumn, TableCell>() {

            public TableCell call(TableColumn param) {

                return new TableCell<ObservableList, String>() {

                    private final Text newText;

                    {
                        newText = new Text();
                        newText.wrappingWidthProperty().bind(tableCol3.widthProperty());
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

                            if (editBtn.getText().equals("Готово")) {
                                for (TimeSheet timeSheet : timeSheetList.getTimesheetlist()) {
                                    if (timeSheet.equals(timeSheetView.getSelectionModel().getSelectedItem()) & !timeSheet.getWork_num().equals("") & !timeSheet.getWork_num().equals("Готово") & !timeSheet.getWork_num().equals("Не проектная")) {
                                        noteTxt2.setVisible(true);
                                        changeBtn.setVisible(true);
                                        taskBox.setDisable(true);
                                        btnTask.setDisable(true);
                                        stageBox.setDisable(true);
                                        noteTxt2.setText(data("SELECT stn.stage_note_text FROM public.stage_note stn " +
                                                        "join public.stage s on s.stage_id = stn.stage_id " +
                                                        "join public.user u on u.user_id = stn.user_id " +
                                                        "join public.task t on t.task_id = s.task_id " +
                                                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                                                        "WHERE t.task_number = '" + timeSheet.getWork_num() + "' AND " +
                                                        "st.stage_type_name = '" + timeSheet.getWork_stage() + "' AND " +
                                                        "u.user_id_number = '"+ user.substring(1, user.indexOf(",")) +"' AND " +
                                                        "stn.stage_note_date = (SELECT max(stgn.stage_note_date) FROM public.stage_note stgn WHERE stgn.stage_note_date < '" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"' AND " +
                                                        "stgn.stage_id = s.stage_id AND stgn.user_id = u.user_id)").toString().replace("[", "").replace("]", ""));

                                        workNum = timeSheet.getWork_num();
                                        workStage = timeSheet.getWork_stage();
                                    } else if (timeSheet.equals(timeSheetView.getSelectionModel().getSelectedItem()) & timeSheet.getWork_num().equals("Не проектная")) {
                                        noteTxt2.setVisible(true);
                                        changeBtn.setVisible(true);
                                        taskBox.setDisable(true);
                                        btnTask.setDisable(true);
                                        stageBox.setDisable(true);
                                        noteTxt2.setText(data("SELECT stn.user_stage_note_text FROM public.user_stage_note stn " +
                                                "join public.user_stage s on s.user_stage_id = stn.user_stage_id " +
                                                "join public.user u on u.user_id = stn.user_id " +
                                                "join public.user_stage_type st on st.user_stage_type_id = s.user_stage_type_id " +
                                                "WHERE st.user_stage_type_name = '" + timeSheet.getWork_stage() + "' AND " +
                                                "u.user_id_number = '"+ user.substring(1, user.indexOf(",")) +"' AND " +
                                                "stn.user_stage_note_date = (SELECT max(stgn.user_stage_note_date) FROM public.user_stage_note stgn WHERE stgn.user_stage_note_date < '" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"' AND " +
                                                "stgn.user_stage_id = s.user_stage_id AND stgn.user_id = u.user_id)").toString().replace("[", "").replace("]", ""));

                                        workNum = timeSheet.getWork_num();
                                        workStage = timeSheet.getWork_stage();
                                    } else if (timeSheet.equals(timeSheetView.getSelectionModel().getSelectedItem()) & (timeSheet.getWork_num().equals("") | timeSheet.getWork_num().equals("Готово"))) {
                                        noteTxt2.setVisible(false);
                                        changeBtn.setVisible(false);
                                        taskBox.setDisable(false);
                                        btnTask.setDisable(false);
                                    }
                                }
                            }

                        } else {
                           if (timeSheetView.getFocusModel().getFocusedCell().getColumn() != 2) {
                               noteTxt2.setVisible(false);
                               changeBtn.setVisible(false);
                               if (editBtn.getText().equalsIgnoreCase("Готово")) {
                                   taskBox.setDisable(false);
                                   btnTask.setDisable(false);
                               }
                           }
                            newText.setFill(Color.BLACK);
                            setGraphic(newText);
                        }
                    }

                };
            }
        });
        tableCol3.setSortable(false);

        TableColumn tableCol4 = new TableColumn("%, нач.");
        if (procStartColWidth.equals("")) {
            tableCol4.setPrefWidth(70);
        } else {
            tableCol4.setPrefWidth(Double.parseDouble(procStartColWidth));
        }
        tableCol4.setCellValueFactory(new PropertyValueFactory<TimeSheet, String>("start_perc"));
        /*tableCol4.setCellFactory(column -> EditCell.createStringEditCell());*/
        tableCol4.setStyle( "-fx-alignment: CENTER;");
        tableCol4.setSortable(false);

        tableCol4.widthProperty().addListener((observable, oldValue, newValue) -> {
            procStartColWidth = newValue.toString();
        });


        timeSheetView.getColumns().addAll(tableCol1, tableCol2, tableCol3, tableCol4);

            for (int i = 1; i <= date.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {

                TableColumn tableColumn = new TableColumn("" + i);
                if (dayColWidth.equals("")) {
                    tableColumn.setPrefWidth(45);
                } else {
                    tableColumn.setPrefWidth(Double.parseDouble(dayColWidth));
                }

                tableColumn.widthProperty().addListener((observable, oldValue, newValue) -> {
                    dayColWidth = newValue.toString();
                });

                tableColumn.setCellValueFactory(new PropertyValueFactory<TimeSheet, String>("day" + i));

                if (checkDate(i)) {
                    tableColumn.setCellFactory(column -> WeekEndCell.createStringEditCell());

                } else {
                    tableColumn.setCellFactory(column -> EditCell.createStringEditCell());
                }


                tableColumn.setOnEditCommit(((EventHandler<TableColumn.CellEditEvent<TimeSheet, String>>) event -> {
                   if (timeSheetList.getTimesheetlist().size()!=0) {
                       if (!timeSheetList.getTimesheetlist().get(timeSheetView.getSelectionModel().getSelectedIndex()).getWork_stage().equals("Итого") /*& !timeSheetList.getTimesheetlist().get(timeSheetView.getSelectionModel().getSelectedIndex()).getWork_stage().equals("")*/) {

                           removeSum();
                           editCell(event);
                           sumTimeSheet();
                       }
                       timeSheetView.refresh();
                   }

                }));
                tableColumn.setStyle( "-fx-alignment: CENTER;");
                tableColumn.setSortable(false);

                timeSheetView.getColumns().addAll(tableColumn);
            }

        TableColumn tableCol5 = new TableColumn("Итого");
        if (procEndColWidth.equals("")) {
            tableCol5.setPrefWidth(70);
        } else {
            tableCol5.setPrefWidth(Double.parseDouble(procEndColWidth));
        }
        tableCol5.setCellValueFactory(new PropertyValueFactory<TimeSheet, String>("end_perc"));
        tableCol5.setCellFactory(column -> EditCell.createStringEditCell());
        tableCol5.setStyle( "-fx-alignment: CENTER;");
        tableCol5.setSortable(false);

        tableCol5.widthProperty().addListener((observable, oldValue, newValue) -> {
            procEndColWidth = newValue.toString();
        });

        timeSheetView.getColumns().addAll(tableCol5);
            //Добавление данных в TableView
        timeSheetView.setItems(timeSheetList.getTimesheetlist());
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

    public void updateNote () {
        if (!noteTxt2.getText().equals("")) {


            try {
                dBconnection.openDB();

                if (!workNum.equals("Не проектная")) {

                    dBconnection.getStmt().execute("UPDATE public.stage SET stage_note = '" + noteTxt2.getText() + "' " +
                            "WHERE stage_id = (SELECT s.stage_id FROM public.stage s " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                            "WHERE st.stage_type_name = '" + workStage + "' AND t.task_number = '" + workNum + "')");

                    dBconnection.getStmt().execute("INSERT INTO public.stage_note (stage_note_date, stage_note_text, stage_id, user_id) VALUES " +
                            "(current_timestamp, '" + noteTxt2.getText() + "', (SELECT stage_id FROM public.stage s " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                            "WHERE t.task_number = '" + workNum + "' AND " +
                            "st.stage_type_name = '" + workStage + "'), " +
                            "(SELECT user_id FROM public.user " +
                            "WHERE user_fullname = '" + MainController.who + "'))");
                } else {
                    dBconnection.getStmt().execute("UPDATE public.user_stage SET user_stage_note = '" + noteTxt2.getText() + "' " +
                            "WHERE user_stage_id = (SELECT s.user_stage_id FROM public.user_stage s " +
                            "join public.user u on u.user_id = s.user_id " +
                            "join public.user_stage_type st on st.user_stage_type_id = s.user_stage_type_id " +
                            "WHERE st.user_stage_type_name = '" + workStage + "' AND u.user_id_number = '"+ user.substring(1, user.indexOf(",")) +"')");

                    dBconnection.getStmt().execute("INSERT INTO public.user_stage_note (user_stage_note_date, user_stage_note_text, user_stage_id, user_id) VALUES " +
                            "(current_timestamp, '" + noteTxt2.getText() + "', (SELECT user_stage_id FROM public.user_stage s " +
                            "join public.user u on u.user_id = s.user_id " +
                            "join public.user_stage_type st on st.user_stage_type_id = s.user_stage_type_id " +
                            "WHERE u.user_id_number = '"+ user.substring(1, user.indexOf(",")) +"' AND " +
                            "st.user_stage_type_name = '" + workStage + "'), " +
                            "(SELECT user_id FROM public.user " +
                            "WHERE user_fullname = '" + MainController.who + "'))");
                }
                dBconnection.getC().commit();
                dBconnection.closeDB();

                timeSheetView.getItems().clear();
                timeSheetView.getColumns().clear();
                timeSheetList.getTimesheetlist().clear();

                fillCrossTab();

                fillCurrentTimeSheet();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            infoAlert.setTitle("Введите комментарий");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Комментарий не может быть пустым");
            infoAlert.showAndWait();
        }
    }

    private boolean checkDate(int i) {
        Calendar date2 = date;

        date2.set(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH), i);

        if (date2.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY || date2.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) {
            return true;
        } else {
            return false;
        }
    }

    private void fillCrossTab() {
        try {
            Double h=0.0;

            System.out.println("Запрос к базе по задачам: " + sdf3.format(new Date()));

            dBconnection.openDB();
            data.clear();

            System.out.println("База открыта: " + sdf3.format(new Date()));

            dBconnection.query("SELECT * " +
                    "FROM crosstab(" +
                    "'SELECT s.stage_id, t.task_number, st.stage_type_name, stn.stage_note_text, dw.day_num, dw.daily_intensity FROM " +
                    "public.stage s " +
                    "join public.task t on t.task_id = s.task_id " +
                    "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                    "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                    "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                    "join public.user u on u.user_id = dw.user_id " +
                    "left join public.stage_note stn on stn.stage_id = s.stage_id " +
                    "WHERE dw.daily_work_date >= ''" + sdf2.format(date.getTime()) + "-01" + "'' AND dw.daily_work_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + "'' AND u.user_id_number = ''"+ user.substring(1, user.indexOf(",")) +"'' " +
                    "AND (stn.stage_note_id = (SELECT max(stage_note_id) FROM public.stage_note stgn WHERE stgn.user_id = u.user_id AND stgn.stage_id = s.stage_id AND stgn.stage_note_date >= ''" + sdf2.format(date.getTime()) + "-01" +"'' AND stgn.stage_note_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"'') " +
                    "OR NOT EXISTS (SELECT stage_note_id FROM public.stage_note stgn WHERE stgn.stage_note_date >= ''" + sdf2.format(date.getTime()) + "-01" +"'' AND stgn.stage_note_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"'' " +
                    "AND stgn.stage_id = s.stage_id AND stgn.user_id = u.user_id)) " +
                    "ORDER BY t.task_number, s.stage_id', " +
                    "'SELECT d from generate_series(1,31) d') " +
                    "AS (stage_id integer, task_number text, stage_type_name text, stage_note text, day1 numeric, day2 numeric, day3 numeric, day4 numeric, day5 numeric, " +
                    "day6 numeric, day7 numeric, day8 numeric, day9 numeric, day10 numeric, " +
                    "day11 numeric, day12 numeric, day13 numeric, day14 numeric, day15 numeric, " +
                    "day16 numeric, day17 numeric, day18 numeric, day19 numeric, day20 numeric, " +
                    "day21 numeric, day22 numeric, day23 numeric, day24 numeric, day25 numeric, " +
                    "day26 numeric, day27 numeric, day28 numeric, day29 numeric, day30 numeric, day31 numeric)");

            System.out.println("Запрос выполнен: " + sdf3.format(new Date()));

            System.out.println("Заполнение ObservableList: " + sdf3.format(new Date()));

            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок

                    if (i>4 & i<=35) {
                        h+=checkVal2(dBconnection.getRs().getString(i));
                    }

                    row.add(dBconnection.getRs().getString(i));
                }

                row.add(new DecimalFormat("#0.00").format(h));
                data.add(row);

                h=0.0;
            }

            System.out.println("Заполнение ObservableList выполнено: " + sdf3.format(new Date()));

            System.out.println("Запрос к базе по пользовательским задачам: " + sdf3.format(new Date()));

            dBconnection.query("SELECT * " +
                    "FROM crosstab(" +
                    "'SELECT us.user_stage_id, ''Не проектная'', ust.user_stage_type_name, us.user_stage_note, dw.day_num, dw.daily_intensity FROM " +
                    "public.user_stage us " +
                    "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                    "join public.user_stage_daily usd on usd.user_stage_id = us.user_stage_id " +
                    "join public.daily_work dw on dw.daily_work_id = usd.daily_work_id " +
                    "join public.user u on u.user_id = dw.user_id " +
                    "left join public.user_stage_note stn on stn.user_stage_id = us.user_stage_id " +
                    "WHERE dw.daily_work_date >= ''" + sdf2.format(date.getTime()) + "-01" + "'' AND dw.daily_work_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + "'' AND u.user_id_number = ''"+ user.substring(1, user.indexOf(",")) +"'' " +
                    "AND (stn.user_stage_note_id = (SELECT max(user_stage_note_id) FROM public.user_stage_note stgn WHERE stgn.user_stage_note_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"'' " +
                    "AND stgn.user_stage_id = us.user_stage_id AND stgn.user_id = u.user_id) " +
                    "OR NOT EXISTS (SELECT user_stage_note_id FROM public.user_stage_note stgn WHERE stgn.user_stage_note_date <= ''" + sdf2.format(date.getTime()) +"-"+ checkMaximux() + " 23:59:59" +"'' " +
                    "AND stgn.user_stage_id = us.user_stage_id AND stgn.user_id = u.user_id)) " +
                    "ORDER BY us.user_stage_id', " +
                    "'SELECT d from generate_series(1,31) d') " +
                    "AS (user_stage_id integer, user_task text, stage_type_name text, stage_note text, day1 numeric, day2 numeric, day3 numeric, day4 numeric, day5 numeric, " +
                    "day6 numeric, day7 numeric, day8 numeric, day9 numeric, day10 numeric, " +
                    "day11 numeric, day12 numeric, day13 numeric, day14 numeric, day15 numeric, " +
                    "day16 numeric, day17 numeric, day18 numeric, day19 numeric, day20 numeric, " +
                    "day21 numeric, day22 numeric, day23 numeric, day24 numeric, day25 numeric, " +
                    "day26 numeric, day27 numeric, day28 numeric, day29 numeric, day30 numeric, day31 numeric)");

            System.out.println("Запрос к базе выполнен: " + sdf3.format(new Date()));

            System.out.println("Дополнение ObservableList: " + sdf3.format(new Date()));

            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок

                    if (i>4 & i<35) {
                        h+=checkVal2(dBconnection.getRs().getString(i));
                    }

                    row.add(dBconnection.getRs().getString(i));
                }
                row.add(new DecimalFormat("#0.00").format(h));
                data.add(row);

                h=0.0;
            }

            System.out.println("Дополнение ObservableList выполнено: " + sdf3.format(new Date()));

            dBconnection.queryClose();
            dBconnection.closeDB();

            System.out.println("Метод завершен, база закрыта: " + sdf3.format(new Date()));

        } catch ( Exception e ) {
            errorAlert.setTitle("Ошибка базы данных");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
    }

    private Integer checkMaximux() {
        Calendar myCal = date;
        myCal.set(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH), 1);
        int max_date = myCal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        return max_date;
    }


    public void editCell(TableColumn.CellEditEvent<TimeSheet, String> event) {
        String replace = event.getNewValue().replace(",", ".");
        String column = "";
        String oldValue ="";

        if (event.getTablePosition().getColumn() > 3 & event.getTablePosition().getColumn() < timeSheetView.getColumns().size()) {
            try {
                if (replace.equals("") || (Double.parseDouble(replace) > 0 && Double.parseDouble(replace) <= 16)) {
                    switch (event.getTablePosition().getColumn()) {
                        case 4:
                            oldValue = event.getRowValue().getDay1();
                            event.getRowValue().setDay1(replace);
                            column = "1";
                            break;
                        case 5:
                            oldValue = event.getRowValue().getDay2();
                            event.getRowValue().setDay2(replace);
                            column = "2";
                            break;
                        case 6:
                            oldValue = event.getRowValue().getDay3();
                            event.getRowValue().setDay3(replace);
                            column = "3";
                            break;
                        case 7:
                            oldValue = event.getRowValue().getDay4();
                            event.getRowValue().setDay4(replace);
                            column = "4";
                            break;
                        case 8:
                            oldValue = event.getRowValue().getDay5();
                            event.getRowValue().setDay5(replace);
                            column = "5";
                            break;
                        case 9:
                            oldValue = event.getRowValue().getDay6();
                            event.getRowValue().setDay6(replace);
                            column = "6";
                            break;
                        case 10:
                            oldValue = event.getRowValue().getDay7();
                            event.getRowValue().setDay7(replace);
                            column = "7";
                            break;
                        case 11:
                            oldValue = event.getRowValue().getDay8();
                            event.getRowValue().setDay8(replace);
                            column = "8";
                            break;
                        case 12:
                            oldValue = event.getRowValue().getDay9();
                            event.getRowValue().setDay9(replace);
                            column = "9";
                            break;
                        case 13:
                            oldValue = event.getRowValue().getDay10();
                            event.getRowValue().setDay10(replace);
                            column = "10";
                            break;
                        case 14:
                            oldValue = event.getRowValue().getDay11();
                            event.getRowValue().setDay11(replace);
                            column = "11";
                            break;
                        case 15:
                            oldValue = event.getRowValue().getDay12();
                            event.getRowValue().setDay12(replace);
                            column = "12";
                            break;
                        case 16:
                            oldValue = event.getRowValue().getDay13();
                            event.getRowValue().setDay13(replace);
                            column = "13";
                            break;
                        case 17:
                            oldValue = event.getRowValue().getDay14();
                            event.getRowValue().setDay14(replace);
                            column = "14";
                            break;
                        case 18:
                            oldValue = event.getRowValue().getDay15();
                            event.getRowValue().setDay15(replace);
                            column = "15";
                            break;
                        case 19:
                            oldValue = event.getRowValue().getDay16();
                            event.getRowValue().setDay16(replace);
                            column = "16";
                            break;
                        case 20:
                            oldValue = event.getRowValue().getDay17();
                            event.getRowValue().setDay17(replace);
                            column = "17";
                            break;
                        case 21:
                            oldValue = event.getRowValue().getDay18();
                            event.getRowValue().setDay18(replace);
                            column = "18";
                            break;
                        case 22:
                            oldValue = event.getRowValue().getDay19();
                            event.getRowValue().setDay19(replace);
                            column = "19";
                            break;
                        case 23:
                            oldValue = event.getRowValue().getDay20();
                            event.getRowValue().setDay20(replace);
                            column = "20";
                            break;
                        case 24:
                            oldValue = event.getRowValue().getDay21();
                            event.getRowValue().setDay21(replace);
                            column = "21";
                            break;
                        case 25:
                            oldValue = event.getRowValue().getDay22();
                            event.getRowValue().setDay22(replace);
                            column = "22";
                            break;
                        case 26:
                            oldValue = event.getRowValue().getDay23();
                            event.getRowValue().setDay23(replace);
                            column = "23";
                            break;
                        case 27:
                            oldValue = event.getRowValue().getDay24();
                            event.getRowValue().setDay24(replace);
                            column = "24";
                            break;
                        case 28:
                            oldValue = event.getRowValue().getDay25();
                            event.getRowValue().setDay25(replace);
                            column = "25";
                            break;
                        case 29:
                            oldValue = event.getRowValue().getDay26();
                            event.getRowValue().setDay26(replace);
                            column = "26";
                            break;
                        case 30:
                            oldValue = event.getRowValue().getDay27();
                            event.getRowValue().setDay27(replace);
                            column = "27";
                            break;
                        case 31:
                            oldValue = event.getRowValue().getDay28();
                            event.getRowValue().setDay28(replace);
                            column = "28";
                            break;
                        case 32:
                            oldValue = event.getRowValue().getDay29();
                            event.getRowValue().setDay29(replace);
                            column = "29";
                            break;
                        case 33:
                            oldValue = event.getRowValue().getDay30();
                            event.getRowValue().setDay30(replace);
                            column = "30";
                            break;
                        case 34:
                            oldValue = event.getRowValue().getDay31();
                            event.getRowValue().setDay31(replace);
                            column = "31";
                            break;
                    }

                    saveIntensity(column, oldValue, replace, event.getRowValue());
                } else {
                    if (!infoAlert.isShowing()) {
                        infoAlert.setTitle("Не допустимое значение");
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("Введите число из разрешенного диапазона [0,1-16]");
                        infoAlert.showAndWait();
                    }
                }
            } catch (Exception e) {
                if (!infoAlert.isShowing()) {
                    infoAlert.setTitle("Не допустимое значение");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Введите число из разрешенного диапазона [0,1-16], либо выберите строку с данными");
                    infoAlert.showAndWait();
                }
            }
        }
    }

    private void saveIntensity (String column, String oldValue, String value, TimeSheet timeSheet) {

        String sheetDate = date.get(GregorianCalendar.YEAR) + "-" + (date.get(GregorianCalendar.MONTH)+1) + "-" + Integer.parseInt(column);

        if (timeSheet.getWork_num().equals("Не проектная")){
            try {
                if (oldValue.equals("") & !value.equals("")) {
                    dBconnection.openDB();
                    dBconnection.getStmt().execute("WITH t2 AS (INSERT INTO public.daily_work " +
                            "(user_id, daily_work_date, daily_intensity, daily_overtime, day_num) " +
                            "SELECT user_id, '" + sheetDate + "', '" + value + "', '0', '" + Integer.parseInt(column) + "' " +
                            "FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "' RETURNING daily_work_id) " +
                            "INSERT INTO public.user_stage_daily (user_stage_id, daily_work_id) " +
                            "WITH t1 AS (SELECT us.user_stage_id FROM public.user_stage us " +
                            "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                            "join public.user u on u.user_id = us.user_id " +
                            "WHERE ust.user_stage_type_name = '" + timeSheet.getWork_stage() + "' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "') " +
                            "SELECT t1.user_stage_id, t2.daily_work_id " +
                            "FROM t1, t2");
                    dBconnection.getC().commit();
                    dBconnection.closeDB();
                } else if (!oldValue.equals("") & value.equals("")) {
                    dBconnection.openDB();
                    dBconnection.getStmt().execute("DELETE FROM public.daily_work " +
                            "WHERE daily_work_id = (SELECT dwk.daily_work_id FROM public.daily_work dwk " +
                            "join public.user_stage_daily usd on usd.daily_work_id = dwk.daily_work_id " +
                            "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                            "join public.user u on u.user_id = us.user_id " +
                            "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                            "WHERE ust.user_stage_type_name = '" + timeSheet.getWork_stage() + "' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "' " +
                            "AND dwk.daily_work_date = '" + sheetDate + "')");
                    dBconnection.getC().commit();
                    dBconnection.closeDB();
                } else if (!oldValue.equals("") & !value.equals("")) {
                    dBconnection.openDB();
                    dBconnection.getStmt().execute("UPDATE public.daily_work SET daily_intensity = '" + value + "' " +
                            "WHERE daily_work_id = (SELECT dwk.daily_work_id FROM public.daily_work dwk " +
                            "join public.user_stage_daily usd on usd.daily_work_id = dwk.daily_work_id " +
                            "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                            "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                            "join public.user u on u.user_id = us.user_id " +
                            "WHERE ust.user_stage_type_name = '" + timeSheet.getWork_stage() + "' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "' " +
                            "AND dwk.daily_work_date = '" + sheetDate + "')");
                    dBconnection.getC().commit();
                    dBconnection.closeDB();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else {
            try {
                if (oldValue.equals("") & !value.equals("")) {
                    dBconnection.openDB();
                    dBconnection.getStmt().execute("WITH t2 AS (INSERT INTO public.daily_work " +
                            "(user_id, daily_work_date, daily_intensity, daily_overtime, day_num) " +
                            "SELECT user_id, '" + sheetDate + "', '" + value + "', '0', '" + Integer.parseInt(column) + "' " +
                            "FROM public.user WHERE user_id_number = '" + user.substring(1, user.indexOf(",")) + "' RETURNING daily_work_id) " +
                            "INSERT INTO public.stage_daily (stage_id, daily_work_id) " +
                            "WITH t1 AS (SELECT s.stage_id FROM public.stage s " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                            "WHERE t.task_number = '" + timeSheet.getWork_num() + "' AND st.stage_type_name = '" + timeSheet.getWork_stage() + "') " +
                            "SELECT t1.stage_id, t2.daily_work_id " +
                            "FROM t1, t2");
                    dBconnection.getC().commit();
                    dBconnection.closeDB();
                } else if (!oldValue.equals("") & value.equals("")) {
                    dBconnection.openDB();
                    dBconnection.getStmt().execute("DELETE FROM public.daily_work " +
                            "WHERE daily_work_id = (SELECT dwk.daily_work_id FROM public.daily_work dwk " +
                            "join public.stage_daily sd on sd.daily_work_id = dwk.daily_work_id " +
                            "join public.stage s on s.stage_id = sd.stage_id " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                            "join public.user u on u.user_id = dwk.user_id " +
                            "WHERE t.task_number = '" + timeSheet.getWork_num() + "' AND st.stage_type_name = '" + timeSheet.getWork_stage() + "' " +
                            "AND dwk.daily_work_date = '" + sheetDate + "' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "')");
                    dBconnection.getC().commit();
                    dBconnection.closeDB();
                } else if (!oldValue.equals("") & !value.equals("")) {
                    dBconnection.openDB();
                    dBconnection.getStmt().execute("UPDATE public.daily_work SET daily_intensity = '" + value + "' " +
                            "WHERE daily_work_id = (SELECT dwk.daily_work_id FROM public.daily_work dwk " +
                            "join public.stage_daily sd on sd.daily_work_id = dwk.daily_work_id " +
                            "join public.stage s on s.stage_id = sd.stage_id " +
                            "join public.task t on t.task_id = s.task_id " +
                            "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                            "join public.user u on u.user_id = dwk.user_id " +
                            "WHERE t.task_number = '" + timeSheet.getWork_num() + "' AND st.stage_type_name = '" + timeSheet.getWork_stage() + "' " +
                            "AND dwk.daily_work_date = '" + sheetDate + "' AND u.user_id_number = '" + user.substring(1, user.indexOf(",")) + "')");
                    dBconnection.getC().commit();
                    dBconnection.closeDB();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeSum() {
        if (timeSheetList.getTimesheetlist().size()!= 0) {
            if (timeSheetList.getTimesheetlist().get(timeSheetList.getTimesheetlist().size()-1).getWork_stage().equals("Итого")) {
                timeSheetList.getTimesheetlist().remove(timeSheetList.getTimesheetlist().size() - 2, timeSheetList.getTimesheetlist().size());
            }
        }
    }

    public void chooseTask() {

        taskBox.getSelectionModel().select(taskPaneController.workNum);

        addBtn.setDisable(true);
        noteTxt.clear();
        noteTxt.setDisable(true);
        stageBox.getItems().setAll(data("SELECT stage_type_name FROM public.stage_type"));
        stageBox.setDisable(false);

    }

    public int returnMonth (String month) {
        Integer m = 0;

        switch (month) {
            case "Январь":
                m = 0;
                break;
            case "Февраль":
                m = 1;
                break;
            case "Март":
                m = 2;
                break;
            case "Апрель":
                m = 3;
                break;
            case "Май":
                m = 4;
                break;
            case "Июнь":
                m = 5;
                break;
            case "Июль":
                m = 6;
                break;
            case "Август":
                m = 7;
                break;
            case "Сентябрь":
                m = 8;
                break;
            case "Октябрь":
                m = 9;
                break;
            case "Ноябрь":
                m = 10;
                break;
            case "Декабрь":
                m = 11;
                break;

        }
        return m;
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
        editBtn.setTextFill(Color.BLACK);
        editBtn.setText("Редактировать");
        btnNotes.setDisable(true);
        taskBox.getSelectionModel().select(0);
        taskBox.getSelectionModel().clearSelection();
        taskBox.getItems().clear();
        taskBox.setDisable(true);
        btnTask.setDisable(true);
        stageBox.getItems().clear();
        stageBox.setDisable(true);
        noteTxt.clear();
        noteTxt.setDisable(true);
        addBtn.setDisable(true);
        data.clear();
        data2.clear();
        dataSelect.clear();
        specData.clear();
        timeSheetList.clear();
        timeSheetView.setEditable(false);
        noteTxt2.setVisible(false);
        changeBtn.setVisible(false);
        txtAll.clear();

        taskColWidth = "";
        stageColWidth = "";
        commentColWidth = "";
        procStartColWidth = "";
        dayColWidth = "";
        procEndColWidth = "";

        dBconnection.openDB();
        try {
            dBconnection.getStmt().executeUpdate("DELETE FROM public.timesheet_view WHERE edit_user = '"+MainController.who+"'");
            dBconnection.getC().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dBconnection.closeDB();
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }
}
