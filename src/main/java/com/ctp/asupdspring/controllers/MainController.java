package com.ctp.asupdspring.controllers;

import com.ctp.asupdspring.app.repo.*;
import com.ctp.asupdspring.domain.CalendarEntity;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.ToggleSwitch;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static com.ctp.asupdspring.controllers.AlertController.*;
import static com.ctp.asupdspring.controllers.AlertController.AlertType.INFO;
import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

@RequiredArgsConstructor
@Component
@FxmlView("main.fxml")
public class MainController {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final RequestRepository requestRepository;
    private final ProjectRepository projectRepository;
    private final ContractRepository contractRepository;
    private final TimesheetViewRepository timesheetViewRepository;
    private final CustomerRepository customerRepository;
    private final SiteRepository siteRepository;
    private final StatusRepository statusRepository;

    // TimeSheetController timeSheetController2 = new TimeSheetController();
    GregorianCalendar date = new GregorianCalendar();
    GregorianCalendar date2 = new GregorianCalendar();
    GregorianCalendar current = new GregorianCalendar();
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy");
    SimpleDateFormat sdf5 = new SimpleDateFormat("MM");
    SimpleDateFormat sdf6 = new SimpleDateFormat("dd.MM.yyyy");
    FxControllerAndView<AlertController, VBox> alertDialog;
    private final CalendarService calendarService;

    //основные массивы заполнения данных
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataSelect = FXCollections.observableArrayList();

    //массивы заполнения филтров перспективы задач
    private ObservableList<ObservableList> customers = FXCollections.observableArrayList();
    private ObservableList<ObservableList> contracts = FXCollections.observableArrayList();
    private ObservableList<ObservableList> projects = FXCollections.observableArrayList();

    //ключи основной фильтрации
    private ObservableList<ObservableList> filterKeys = FXCollections.observableArrayList();

    //массивы основной фильтрации
    private ObservableList<ObservableList> backupList = FXCollections.observableArrayList();
    private ObservableList<ObservableList> filteredBackup = FXCollections.observableArrayList();

    //ключи фильтрации перспективы задач
    private ObservableList taskfilterKeys = FXCollections.observableArrayList();

    private Stage mainStage;

    private String prevNode = "";
    private String searchNode = "";
    private List<Object[]> sql = null;
    private String tableColName;
    private String workNumber;
    private String workName;
    private String workId;
    private String exemp;

    public static String role;
    public static String who;
    public static Map<String, Integer> authorities;

    private String root;

    private Integer count;

    @FXML
    private TableView dataView;
    @FXML
    private TreeView<String> treeView;
    @FXML
    private Label lblQty;
    @FXML
    private Label systemLabel;
    @FXML
    private TextField fldSearch;
    @FXML
    private ToolBar sheetTools;
    @FXML
    private ToolBar emplTools;
    @FXML
    private ComboBox taskStatusBox;
    @FXML
    private CheckComboBox siteFilterBox;
    @FXML
    private CheckComboBox roleFilterBox;
    @FXML
    private CheckComboBox stateFilterBox;
    @FXML
    private CheckComboBox customerFilterBox;
    @FXML
    private CheckComboBox contractFilterBox;
    @FXML
    private CheckComboBox projectFilterBox;
    @FXML
    private ToggleSwitch inDetails;
    @FXML
    private Button backBtn;
    @FXML
    private Button forwardBtn;
    @FXML
    private ComboBox<String> yearBox;
    @FXML
    private ComboBox<String> monthBox;
    @FXML
    private Button btnEditValue;
    @FXML
    private Button btnEditLinks;
    @FXML
    private Button openViewButton;
    @FXML
    private Button deleteButton;
    @FXML
    private MenuButton mBtnCreate;
    @FXML
    private MenuButton mBtnReports;
    @FXML
    private MenuItem menuAccess;
    @FXML
    private MenuItem menuClose;
    @FXML
    private MenuItem menuChangePass;
    @FXML
    private Label stateLabel;
    @FXML
    private Label siteFilterLabel;
    @FXML
    private Label roleFilterLabel;

    MenuItem menuEditRow = new MenuItem("Редактировать запись");

    private Stage sheetStage;
    private Stage projectStage;
    private Stage requestStage;
    private Stage taskStage;
    private Stage periodReportStage;
    private Stage resourceReportStage;
    private Stage customerStage;
    private Stage timeSheetStage;
    private Stage userStage;
    private Stage executorStage;
    private Stage contractStage;
    private Stage projectAccessStage;
    private Stage userAccessStage;
    private Stage accessToolStage;
    private Stage requestLinkStage;
    private Stage passChangeStage;
    private Stage requestViewStage;
    private Stage siteStage;
    private Stage taskViewStage;
    private Stage effectivityReportStage;
    public Stage calendarStage;
    private Stage importOneCReportStage;
    private Object[] metaData;
    @FXML
    private Node node;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    List<CalendarEntity> calendarResult;
    ArrayList<Integer> holidays = new ArrayList();
    ArrayList<Integer> workdays = new ArrayList();
    private static final DecimalFormat df = new DecimalFormat("0.0");
    private final FxWeaver fxWeaver;

    @FXML
    private void initialize() {
        fillTreeView();
        initLoader();
        initListeners();

        sheetTools.setVisible(false);
        emplTools.setVisible(false);

        openViewButton.setDisable(true);
        deleteButton.setDisable(true);

        root = who + " (" + role + ")";

        Image createImage = new Image("/static/images/newObject.png");
        Image editImage = new Image("/static/images/edit.png");
        Image linksImage = new Image("/static/images/links.png");
        Image reportImage = new Image("/static/images/report.png");
        Image viewImage = new Image("/static/images/view.png");
        Image deleteImage = new Image("static/images/delete.png");

        mBtnCreate.graphicProperty().setValue(new ImageView(createImage));
        btnEditValue.graphicProperty().setValue(new ImageView(editImage));
        btnEditLinks.graphicProperty().setValue(new ImageView(linksImage));
        mBtnReports.graphicProperty().setValue(new ImageView(reportImage));
        openViewButton.graphicProperty().setValue(new ImageView(viewImage));
        deleteButton.graphicProperty().setValue(new ImageView(deleteImage));

        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                role.equalsIgnoreCase("Менеджер проекта")) {

        } else {
            mBtnCreate.setDisable(true);
        }

        if (role.equalsIgnoreCase("сотрудник")) {
            mBtnReports.setDisable(true);
        }
        if (!role.equalsIgnoreCase("ауп") && !role.equalsIgnoreCase("Сотрудник")) {
            inDetails.setVisible(true);
        } else {
            inDetails.setVisible(false);
        }
    }

    private void initLoader() {
    }

    private void initListeners() {

        monthBox.getItems().addAll(months);
        yearBox.getItems().addAll("2018", "2019", "2020", "2021", "2022");
        inDetails.selectedProperty().addListener(observable -> {
            holidays.clear();
            workdays.clear();
            yearBox.setValue(String.valueOf(LocalDate.now().getYear()));
            monthBox.setValue(months[LocalDate.now().getMonth().ordinal()]);
            yearBox.setVisible(inDetails.isSelected());
            monthBox.setVisible(inDetails.isSelected());
            backBtn.setVisible(inDetails.isSelected());
            forwardBtn.setVisible(inDetails.isSelected());
            siteFilterBox.setVisible(!inDetails.isSelected());
            siteFilterLabel.setVisible(!inDetails.isSelected());
            roleFilterBox.setVisible(!inDetails.isSelected());
            roleFilterLabel.setVisible(!inDetails.isSelected());
            tableShowContent("Табель", true);
        });
        yearBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            monthBox.setValue(months[LocalDate.now().getMonth().ordinal()]);
            date.set(Integer.parseInt(yearBox.getValue()), CalendarController.Months.returnMonth(monthBox.getValue()), date.getActualMaximum(Calendar.MONTH));
            tableShowContent("Табель", true);
        });
        monthBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            yearBox.setValue(String.valueOf(LocalDate.now().getYear()));
            date.set(Integer.parseInt(yearBox.getValue()), CalendarController.Months.returnMonth(monthBox.getValue()), date.getActualMaximum(Calendar.MONTH));
            tableShowContent("Табель", true);
        });
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            fldSearch.clear();

            systemLabel.setTextFill(Color.RED);
            systemLabel.setText("Фильтр не применен");

            if (newValue.getValue().contains("Мои задачи") || newValue.getValue().contains("В работе") || newValue.getValue().contains("Выполнено") ||
                    newValue.getValue().contains("Проверено") || newValue.getValue().contains("Утверждено") || newValue.getValue().contains("В ожидании")) {
                sheetTools.setVisible(true);

                customerFilterBox.getCheckModel().clearChecks();
                contractFilterBox.getCheckModel().clearChecks();
                projectFilterBox.getCheckModel().clearChecks();
                taskStatusBox.getItems().clear();
                customerFilterBox.getItems().clear();
                contractFilterBox.getItems().clear();
                projectFilterBox.getItems().clear();
                customers.clear();
                contracts.clear();
                projects.clear();
                sql = statusRepository.getStatusByType("tasks");
                metaData = sql.remove(0);
                taskStatusBox.getItems().addAll(data(sql));
                sql = customerRepository.getAllCustomersNames();
                metaData = sql.remove(0);
                customers = data(sql);
                customerFilterBox.getItems().addAll(customers);
                sql = customerRepository.getCustomersWithContracts();
                metaData = sql.remove(0);
                filteredFill(contracts, sql);

                for (ObservableList contract : contracts) {
                    contractFilterBox.getItems().add(contract.get(1));
                }
/*
                filteredFill(projects, "SELECT cr.customer_name, c.contract_number, p.project_name FROM public.project p " +
                        "join public.contract_project cp on cp.project_id = p.project_id " +
                        "join public.contract c on c.contract_id = cp.contract_id " +
                        "join public.customer cr on cr.customer_id = c.customer_id " +
                        "ORDER BY cr.customer_name, c.contract_number, p.project_name");*/

                for (ObservableList project : projects) {
                    projectFilterBox.getItems().add(project.get(2));
                }
            } else {
                sheetTools.setVisible(false);
            }

            if (newValue.getValue().contains("Сотрудники") || newValue.getValue().contains("Табель")) {
                emplTools.setVisible(true);

                siteFilterBox.getCheckModel().clearChecks();
                roleFilterBox.getCheckModel().clearChecks();
                stateFilterBox.getCheckModel().clearChecks();

                siteFilterBox.getItems().clear();
                roleFilterBox.getItems().clear();
                stateFilterBox.getItems().clear();

              /*  siteFilterBox.getItems().addAll(data("SELECT site_name FROM public.site ORDER BY site_id"));
                roleFilterBox.getItems().addAll(data(
                        "SELECT user_role_name FROM public.user_role WHERE user_role_name != 'Super_user' AND user_role_name != 'Admin' ORDER BY user_role_name"));
                stateFilterBox.getItems().addAll(data("SELECT user_activity_name FROM public.user_activity ORDER BY user_activity_id"));*/

                if (newValue.getValue().contains("Табель")) {
                    stateFilterBox.setVisible(false);
                    stateLabel.setVisible(false);
                } else {
                    stateFilterBox.setVisible(true);
                    stateLabel.setVisible(true);
                }
            } else {
                emplTools.setVisible(false);
            }

            if (newValue.getValue().contains("Заявки") || newValue.getValue().contains("Табель") || newValue.getValue().contains("Мои задачи")) {
                openViewButton.setDisable(false);
            } else {
                openViewButton.setDisable(true);
            }

            if (newValue.getValue().contains("Мои задачи")) {
                deleteButton.setDisable(false);
            } else {
                deleteButton.setDisable(true);
            }

            tableShowContent(newValue.getValue(), false);
        });

        customerFilterBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
            taskfilterKeys = customerFilterBox.getCheckModel().getCheckedItems();

            contractFilterBox.getItems().clear();
            projectFilterBox.getItems().clear();

            if (!customerFilterBox.getCheckModel().isEmpty()) {
                for (Object customer : taskfilterKeys) {

                    for (ObservableList contract : contracts) {
                        if (customer.toString().replace("[", "").replace("]", "").equalsIgnoreCase(contract.get(0).toString())) {
                            contractFilterBox.getItems().add(contract.get(1));
                        }
                    }

                    for (ObservableList project : projects) {
                        if (customer.toString().replace("[", "").replace("]", "").equalsIgnoreCase(project.get(0).toString())) {
                            projectFilterBox.getItems().add(project.get(2));
                        }
                    }
                }
            } else {

                for (ObservableList contract : contracts) {
                    contractFilterBox.getItems().add(contract.get(1));
                }

                for (ObservableList project : projects) {
                    projectFilterBox.getItems().add(project.get(2));
                }
            }

            contractFilterBox.autosize();
            projectFilterBox.autosize();
        });

        contractFilterBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
            taskfilterKeys = contractFilterBox.getCheckModel().getCheckedItems();

            projectFilterBox.getItems().clear();

            if (!contractFilterBox.getCheckModel().isEmpty()) {
                for (Object contract : taskfilterKeys) {
                    for (ObservableList project : projects) {
                        if (contract.toString().replace("[", "").replace("]", "").equalsIgnoreCase(project.get(1).toString())) {
                            projectFilterBox.getItems().add(project.get(2));
                        }
                    }
                }
            } else {
                for (ObservableList project : projects) {
                    projectFilterBox.getItems().add(project.get(2));
                }
            }

            projectFilterBox.autosize();
        });

        data.addListener((ListChangeListener) c -> lblQty.setText("Количество записей: " + data.size()));

        dataView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!dataView.getSelectionModel().isEmpty()) {
                if (prevNode.equals("Мои задачи") || prevNode.equals("В работе") || prevNode.equals("Выполнено") || prevNode.equals("Проверено") ||
                        prevNode.equals("Утверждено") || prevNode.equals("В ожидании")) {
                    for (int i = 0; i <= data.size() - 1; i++) {
                        if (data.get(i) == dataView.getSelectionModel().getSelectedItem()) {
                            workNumber = (String) data.get(i).get(5);
                        }
                    }
                }

                if (prevNode.equals("Заказчики") || prevNode.equals("Мои проекты") || prevNode.equals("Сотрудники") || prevNode.equals("Площадки") ||
                        prevNode.equals("Контракты") || prevNode.equals("Мои задачи") || prevNode.equals("В работе") || prevNode.equals("Выполнено") ||
                        prevNode.equals("Проверено") || prevNode.equals("Утверждено") || prevNode.equals("В ожидании") || prevNode.equals("Заявки")) {
                    for (int i = 0; i <= data.size() - 1; i++) {
                        if (data.get(i) == dataView.getSelectionModel().getSelectedItem()) {
                            exemp = (String) data.get(i).get(0);
                        }
                    }
                }
            }

            if (newValue.equals(null)) {
                menuEditRow.setVisible(false);
            } else {
                menuEditRow.setVisible(true);
            }
        });

        dataView.setOnMouseClicked(event -> {
            Object src = event.getSource();
            if (!(src instanceof TableView)) {
                return;
            }

            if (event.getClickCount() == 2) {

                editObjectLinks();
            }
        });

        fldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                dataView.getItems().clear();
                searchNode = prevNode;
                prevNode = "";
                tableShowContent(searchNode, false);
            }
        });

        fldSearch.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                actionSearch();
            }
        });

        menuEditRow.setOnAction((ActionEvent event) -> {
            if (prevNode.equals("Заказчики")) {
                customerDetail();
            }
        });
    }

    private List<CalendarEntity> getCalendarData() {
        return calendarService.getCalendarBy(Integer.parseInt(yearBox.getValue()), (CalendarController.Months.returnMonth(monthBox.getValue()) + 1));
    }

    private void fillTreeView() {
        try {
            Image nodeImage = new Image("/static/images/folder.png");
            Image fullImage = new Image("/static/images/folderFull.png");
            Image inworkImage = new Image("/static/images/inwork.png");
            Image completeImage = new Image("/static/images/complete.png");
            Image checkedImage = new Image("/static/images/checked.png");
            Image approvedImage = new Image("/static/images/approved.png");
            Image timedImage = new Image("/static/images/timed.png");
            Image projectImage = new Image("/static/images/project.png");
            Image tweakNodeImage = new Image("/static/images/tweak.png");
            Image clockNodeImage = new Image("/static/images/clock.png");
            Image userImage = new Image("/static/images/user.png");

            TreeItem<String> rootItem = new TreeItem<String>(who + " (" + role + ")", new ImageView(userImage));
            rootItem.setExpanded(true);

            TreeItem<String> tiManagement = new TreeItem<String>("Менеджмент", new ImageView(projectImage));
            rootItem.getChildren().add(tiManagement);
            tiManagement.setExpanded(true);

            TreeItem<String> tiContracts = new TreeItem<String>("Контракты", new ImageView(nodeImage));
            tiManagement.getChildren().add(tiContracts);
            TreeItem<String> tiRequests = new TreeItem<String>("Заявки", new ImageView(nodeImage));
            tiManagement.getChildren().add(tiRequests);
            TreeItem<String> tiProjects = new TreeItem<String>("Мои проекты", new ImageView(nodeImage));
            tiManagement.getChildren().add(tiProjects);

            TreeItem<String> tiTasks = new TreeItem<String>("Мои задачи", new ImageView(inworkImage));
            rootItem.getChildren().add(tiTasks);
            tiTasks.setExpanded(true);

            TreeItem<String> tiTasksInWork = new TreeItem<String>("В работе", new ImageView(fullImage));
            tiTasks.getChildren().add(tiTasksInWork);
            TreeItem<String> tiTasksComplete = new TreeItem<String>("Выполнено", new ImageView(completeImage));
            tiTasks.getChildren().add(tiTasksComplete);
            TreeItem<String> tiTasksChecked = new TreeItem<String>("Проверено", new ImageView(checkedImage));
            tiTasks.getChildren().add(tiTasksChecked);
            TreeItem<String> tiTasksApproved = new TreeItem<String>("Утверждено", new ImageView(approvedImage));
            tiTasks.getChildren().add(tiTasksApproved);
            TreeItem<String> tiTasksIdle = new TreeItem<String>("В ожидании", new ImageView(timedImage));
            tiTasks.getChildren().add(tiTasksIdle);

            TreeItem<String> tiDicts = new TreeItem<String>("Справочники", new ImageView(tweakNodeImage));
            rootItem.getChildren().add(tiDicts);
            tiDicts.setExpanded(true);

            TreeItem<String> tiEmployees = new TreeItem<String>("Сотрудники", new ImageView(nodeImage));
            tiDicts.getChildren().add(tiEmployees);
            TreeItem<String> tiCustomers = new TreeItem<String>("Заказчики", new ImageView(nodeImage));
            tiDicts.getChildren().add(tiCustomers);
            TreeItem<String> tiSites = new TreeItem<String>("Площадки", new ImageView(nodeImage));
            tiDicts.getChildren().add(tiSites);

            TreeItem<String> tiTimeSheet = new TreeItem<String>("Табель", new ImageView(clockNodeImage));
            rootItem.getChildren().add(tiTimeSheet);

            treeView.setRoot(rootItem);

            // Выделяем корневой узел на старте
            treeView.getSelectionModel().select(treeView.getRow(rootItem));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editObjectLinks() {
        if (prevNode.equals("Табель")) {
            timeSheetDetail();
        } else if (prevNode.equals("Мои задачи") || prevNode.equals("В работе") || prevNode.equals("Выполнено") || prevNode.equals("Проверено") ||
                prevNode.equals("Утверждено") || prevNode.equals("В ожидании")) {
            if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                    role.equalsIgnoreCase("Менеджер проекта")) {
                executorDetail();
            } else {
                setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
            }
        } else if (prevNode.equals("Мои проекты")) {
            if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                    role.equalsIgnoreCase("Менеджер проекта")) {
                projectAccessDetail();
            } else {
                setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
            }
        } else if (prevNode.equals("Сотрудники")) {
            if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                    role.equalsIgnoreCase("Менеджер проекта")) {
                userAccessDetail();
            } else {
                setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
            }
        } else if (prevNode.equals("Заявки")) {
            if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                    role.equalsIgnoreCase("Менеджер проекта")) {
                if (!dataView.getSelectionModel().getSelectedItem().toString().contains("Согласовано")) {
                    requestLinkStage();
                } else {
                    setAlertStage("Ошибка редактирования", null, "Невозможно расширить согласованную заявку", INFO);
                }
            } else {
                setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
            }
        } else {
            setAlertStage("Нет подходящих объектов", null, "В данной перспективе связи не доступны", INFO);
        }
    }

    public void actionButtonPressed(ActionEvent actionEvent) throws SQLException {
        String node = "";

        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) {
            return;
        }
        Button clickedButton = (Button) source;
        switch (clickedButton.getId()) {
            case "btnOpenSheet":
                sheetDetail();
                break;
            case "btnEditValue":
                if (!dataView.getSelectionModel().isEmpty()) {
                    if (prevNode.equals("Контракты")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                            contractDetail();
                        } else {
                            setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
                        }
                    } else if (prevNode.equals("Заказчики")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                            customerDetail();
                        } else {
                            setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
                        }
                    } else if (prevNode.equals("Площадки")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                            siteDetail();
                        } else {
                            setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
                        }
                    } else if (prevNode.equals("Сотрудники")) {
                        if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                            userDetail();
                        } else {
                            setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
                        }
                    } else if (prevNode.equals("Мои задачи") || prevNode.equals("В работе") || prevNode.equals("Выполнено") || prevNode.equals("Проверено") ||
                            prevNode.equals("Утверждено") || prevNode.equals("В ожидании")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                                role.equalsIgnoreCase("Менеджер проекта")) {
                            taskDetail();
                        } else {
                            setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
                        }
                    } else if (prevNode.equalsIgnoreCase("Табель")) {
                        timeSheetDetail();
                    } else if (prevNode.equals("Заявки")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                                role.equalsIgnoreCase("Менеджер проекта")) {
                            requestDetail();
                        } else {
                            setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
                        }
                    } else if (prevNode.equals("Мои проекты")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                                role.equalsIgnoreCase("Менеджер проекта")) {
                            projectDetail();
                        } else {
                            setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
                        }
                    }
                } else {
                    setAlertStage(CHOOSE_ERROR, null, CHOOSE_VALUE_IN_TABLE, INFO);
                }
                break;
            case "btnStatusChange":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                        role.equalsIgnoreCase("Менеджер проекта") || role.equalsIgnoreCase("Начальник отдела")) {
                    if (!dataView.getSelectionModel().isEmpty() & !taskStatusBox.getSelectionModel().isEmpty()) {
                       /* dBconnection.openDB();
                        dBconnection.getStmt().executeUpdate("UPDATE public.task SET status_id = " +
                                "(SELECT s.status_id FROM public.status s " +
                                "join public.status_type st on st.status_type_id = s.status_type " +
                                "WHERE st.status_type_name = 'tasks' AND s.status_name = '" +
                                taskStatusBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "') " +
                                "WHERE task_number = '" + workNumber + "'");
                        dBconnection.getC().commit();
                        dBconnection.closeDB();*/
                    } else if (taskStatusBox.getSelectionModel().isEmpty() & !dataView.getSelectionModel().isEmpty()) {
                        setAlertStage(CHOOSE_ERROR, null, "Выберите статус", INFO);
                    } else if (dataView.getSelectionModel().isEmpty() & !taskStatusBox.getSelectionModel().isEmpty()) {
                        setAlertStage(CHOOSE_ERROR, null, CHOOSE_TASK, INFO);
                    } else {
                        setAlertStage(CHOOSE_ERROR, null, "Не выбраны данные для изменения статуса", INFO);
                    }
                    node = prevNode;
                    prevNode = "";
                    tableShowContent(node, false);
                } else {
                    setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
                }
                break;
            case "btnEditLinks":
                editObjectLinks();
                break;
            case "openViewButton":
                if (!dataView.getSelectionModel().isEmpty()) {
                    if (prevNode.equals("Заявки")) {
                        if (role.equalsIgnoreCase("Super_user") || role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("АУП") ||
                                role.equalsIgnoreCase("Менеджер проекта")) {
                            requestView();
                        }
                    } else if (prevNode.equals("Табель")) {
                        timeSheetDetail();
                    } else if (prevNode.equals("Мои задачи")) {
                        taskView();
                    }
                } else {
                    setAlertStage(CHOOSE_ERROR, null, CHOOSE_VALUE_IN_TABLE, INFO);
                }
                break;
            case "deleteButton":
                if (!dataView.getSelectionModel().isEmpty()) {
                    if (prevNode.equals("Мои задачи") || prevNode.equals("В работе") || prevNode.equals("Выполнено") || prevNode.equals("Проверено") ||
                            prevNode.equals("Утверждено") || prevNode.equals("В ожидании")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                                role.equalsIgnoreCase("Менеджер проекта")) {
                            deleteItem(workNumber);
                        } else {
                            setAlertStage(ACCESS_ERROR, null, NO_RIGHTS_FOR_ACTION, INFO);
                        }
                    }
                } else {
                    setAlertStage(CHOOSE_ERROR, null, CHOOSE_VALUE_IN_TABLE, INFO);
                }
                break;
        }
    }

    public void actionMenuItemPressed(ActionEvent actionEvent) throws SQLException {

        Object source = actionEvent.getSource();
        if (!(source instanceof MenuItem)) {
            return;
        }

        MenuItem clickedMenuItem = (MenuItem) source;

        switch (clickedMenuItem.getId()) {
            case "calendar":
                if (role.equalsIgnoreCase("super_user")) {
                    setCalendarStage();
                    //setAlertStage("e", "e", "e", AlertController.AlertType.ERROR);
                }
                break;
            case "access":
                if (role.equalsIgnoreCase("super_user")) {
                    setAccessStage();
                    //setAlertStage("e", "e", "e", AlertController.AlertType.ERROR);
                }
                break;
            case "menuClose":
                System.exit(0);
                break;
            case "menuProject":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                    setProjectStage("Добавить");
                    // projectController.addData();
                } else {
                    setAlertStage(NO_RIGHTS, null, NO_ROLE_FOR_MANAGE_DATA, INFO);
                }
                break;
            case "menuTask":
                taskStage();
                taskStage.setTitle("Добавить");
                //taskController.addData();
                taskStage.showAndWait();
                break;
            case "menuPeriodReport":
                if (periodReportStage == null) {
                    periodReportStage = new Stage();
                    periodReportStage.setTitle("Добавить");
                    //  periodReportStage.setScene(new Scene(fxmlPeriodReport));
                    periodReportStage.setMinHeight(100);
                    periodReportStage.setMinWidth(200);
                    periodReportStage.setResizable(false);
                    periodReportStage.initModality(Modality.WINDOW_MODAL);
                    periodReportStage.initOwner(mainStage);
                }
                //  periodReportController.addData();
                periodReportStage.showAndWait();
                break;
            case "menuResReport":
                if (resourceReportStage == null) {
                    resourceReportStage = new Stage();
                    resourceReportStage.setTitle("Добавить");
                    //    resourceReportStage.setScene(new Scene(fxmlResourceReport));
                    resourceReportStage.setMinHeight(100);
                    resourceReportStage.setMinWidth(200);
                    resourceReportStage.setResizable(false);
                    resourceReportStage.initModality(Modality.WINDOW_MODAL);
                    resourceReportStage.initOwner(mainStage);
                }
                //   resourceReportController.addData();
                resourceReportStage.showAndWait();
                break;
            case "menuEffyReport":
                if (!role.equalsIgnoreCase("сотрудник")) {
                    effectivityReportDetail();
                } else {
                    setAlertStage(NO_RIGHTS, null, NO_ROLE_FOR_LOOK_UP, INFO);
                }
                break;
            case "importOneCReport":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("super_user")) {
                    importOneCReportDetail();
                } else {
                    setAlertStage(NO_RIGHTS, null, NO_ROLE_FOR_LOOK_UP, INFO);
                }
                break;
            case "menuCustomer":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                    customerStage();
                    customerStage.setTitle("Добавить заказчика");
                    //    customerController.createData();
                    customerStage.showAndWait();
                } else {
                    setAlertStage(NO_RIGHTS, null, NO_ROLE_FOR_MANAGE_DATA, INFO);
                }
                break;
            case "menuSite":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                    siteStage();
                    siteStage.setTitle("Добавить площадку");
                    //   siteController.createData();
                    siteStage.showAndWait();
                } else {
                    setAlertStage(NO_RIGHTS, null, NO_ROLE_FOR_MANAGE_DATA, INFO);
                }
                break;
            case "menuEmployee":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                    userStage();
                    userStage.setTitle("Добавить сотрудника");
                    //   userController.addData();
                    userStage.showAndWait();
                } else {
                    setAlertStage(NO_RIGHTS, null, NO_ROLE_FOR_MANAGE_DATA, INFO);
                }
                break;
            case "menuContract":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                    contractStage();
                    contractStage.setTitle("Добавить контракт");
                    //   contractController.addData();
                    contractStage.showAndWait();
                } else {
                    setAlertStage(NO_RIGHTS, null, NO_ROLE_FOR_MANAGE_DATA, INFO);
                }
                break;
            case "menuRequest":
                setRequestStage("Добавить заявку");
                //requestController.addData();
                break;
            case "menuAccess":
                accessToolStage();
                accessToolStage.setTitle("Изменить права доступа");
                accessToolStage.showAndWait();
                break;
            case "menuChangePass":
                passChangeStage();
                passChangeStage.setTitle("Изменить пароль");
                passChangeStage.showAndWait();
                break;
        }

        //  updateTableView();
    }

    private void setAccessStage() {
        fxWeaver.loadController(CalendarController.class).show();
    }

    public void actionMenuAbout(ActionEvent actionEvent) {
        setAlertStage("О программе", "АСУ ПД, 2018-2022 г.", "v 1.0.6", AlertController.AlertType.INFO);
    }

    private void sheetDetail() {
       /* if (sheetStage == null) {
            sheetStage = new Stage();
            sheetStage.setTitle("Изменить");
            sheetStage.setScene(new Scene(fxmlSheet));
            sheetStage.setMinHeight(600);
            sheetStage.setMinWidth(1600);
            sheetStage.initModality(Modality.WINDOW_MODAL);
            sheetStage.initOwner(mainStage);
        }
        if (!dataView.getSelectionModel().isEmpty()) {
            if (prevNode.equals("Обменные файлы")) {
                sheetController.addData(workId);
            } else {
                sheetController.setData(workId);
            }
            sheetStage.showAndWait();
            updateTableView();
        } else {
        setAlertStage(CHOOSE_ERROR, null, "Выберите строку справочника", AlertController.AlertType.INFO);

            return;
        }       */
    }

    private void timeSheetDetail() {
      /*  if (timeSheetStage == null) {
            timeSheetStage = new Stage();
            timeSheetStage.setTitle("Табель учета рабочего времени");
            timeSheetStage.setScene(new Scene(fxmlTimeSheet));
            timeSheetStage.setMinHeight(700);
            timeSheetStage.setMinWidth(1300);
            timeSheetStage.initModality(Modality.NONE);
            timeSheetStage.initOwner(mainStage);
        }
        if (!dataView.getSelectionModel().isEmpty()) {
            Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();
            timeSheetController.initTimeSheet(dataView.getSelectionModel().getSelectedItem().toString(), date);
            timeSheetStage.setOnCloseRequest(arg0 -> timeSheetController.formClear());
            timeSheetStage.showAndWait();
            updateTableView();
            dataView.getSelectionModel().clearAndSelect(rowIndex);
        } else {
         setAlertStage(CHOOSE_ERROR, null, "Выберите сотрудника", AlertController.AlertType.INFO);
        }*/
    }

    private void executorDetail() {
        /*if (executorStage == null) {
            executorStage = new Stage();
            executorStage.setTitle("Исполнители");
            executorStage.setScene(new Scene(fxmlExecutor));
            executorStage.setMinHeight(400);
            executorStage.setMinWidth(800);
            executorStage.initModality(Modality.WINDOW_MODAL);
            executorStage.initOwner(mainStage);
        }
        if (!dataView.getSelectionModel().isEmpty()) {
            executorController.addData(workNumber);
            executorStage.setOnCloseRequest(arg0 -> executorController.formClear());
            executorStage.showAndWait();
        } else {
              setAlertStage(CHOOSE_ERROR, null, CHOOSE_TASK, INFO);

        }*/
    }

    private void customerStage() {
       /* if (customerStage == null) {
            customerStage = new Stage();
            customerStage.setScene(new Scene(fxmlCustomer));
            customerStage.setMinHeight(100);
            customerStage.setMinWidth(200);
            customerStage.setResizable(false);
            customerStage.initModality(Modality.WINDOW_MODAL);
            customerStage.initOwner(mainStage);
        }*/
    }

    private void customerDetail() {
      /*  customerStage();
        customerStage.setTitle("Изменить");
        customerController.setData(exemp);
        customerStage.showAndWait();
        updateTableView();*/
    }

    private void effectivityReportStage() {
       /* if (effectivityReportStage == null) {
            effectivityReportStage = new Stage();
            effectivityReportStage.setScene(new Scene(fxmlEffectivityReport));
            effectivityReportStage.setMinHeight(100);
            effectivityReportStage.setMinWidth(200);
            effectivityReportStage.setResizable(false);
            effectivityReportStage.initModality(Modality.WINDOW_MODAL);
            effectivityReportStage.initOwner(mainStage);
        }*/
    }

    private void importOneCReportStage() {
        /*if (importOneCReportStage == null) {
            importOneCReportStage = new Stage();
            importOneCReportStage.setScene(new Scene(fxmlImportOneCReport));
            importOneCReportStage.setMinHeight(100);
            importOneCReportStage.setMinWidth(200);
            importOneCReportStage.setResizable(false);
            importOneCReportStage.initModality(Modality.WINDOW_MODAL);
            importOneCReportStage.initOwner(mainStage);
        }*/
    }

    private void setCalendarStage() {
        fxWeaver.loadController(CalendarController.class).show();
    }

    private void effectivityReportDetail() {
       /* effectivityReportStage();

        effectivityReportStage.setTitle("Отчет по эффективности");
        effectivityReportController.addData();
        effectivityReportStage.showAndWait();*/
    }

    private void importOneCReportDetail() {
        ImportOneCReportController importOneCReportController = fxWeaver.loadController(ImportOneCReportController.class);
        importOneCReportController.addData();
        importOneCReportController.show();
    }

    private void siteStage() {
       /* if (siteStage == null) {
            siteStage = new Stage();
            siteStage.setScene(new Scene(fxmlSite));
            siteStage.setMinHeight(100);
            siteStage.setMinWidth(200);
            siteStage.setResizable(false);
            siteStage.initModality(Modality.WINDOW_MODAL);
            siteStage.initOwner(mainStage);
        }*/
    }

    private void siteDetail() {
       /* siteStage();
        siteStage.setTitle("Изменить");
        siteController.setData(exemp);
        siteStage.showAndWait();
        updateTableView();*/
    }

    private void userStage() {
       /* if (userStage == null) {
            userStage = new Stage();
            userStage.setScene(new Scene(fxmlUser));
            userStage.setMinHeight(100);
            userStage.setMinWidth(200);
            userStage.setResizable(false);
            userStage.initModality(Modality.WINDOW_MODAL);
            userStage.initOwner(mainStage);
        }     */
    }

    private void userDetail() {
        if (!dataView.getSelectionModel().isEmpty()) {
            userStage();
            userStage.setTitle("Редактировать сотрудника");
            //  userController.setData(exemp);
            userStage.showAndWait();
            updateTableView();
        } else {
            setAlertStage(CHOOSE_ERROR, null, "Выберите сотрудника", AlertController.AlertType.INFO);
        }
    }

    private void taskStage() {
        if (taskStage == null) {
            taskStage = new Stage();
            //  taskStage.setScene(new Scene(fxmlTask));
            taskStage.setMinHeight(100);
            taskStage.setMinWidth(200);
            taskStage.setResizable(false);
            taskStage.initModality(Modality.WINDOW_MODAL);
            taskStage.initOwner(mainStage);
        }
    }

    private void taskDetail() {
        Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();

        taskStage();
        taskStage.setTitle("Изменить");
        //  taskController.setData(exemp);
        taskStage.showAndWait();
        updateTableView();

        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void setProjectStage(String title) {
        if (projectStage == null) {
            fxWeaver.loadController(ProjectController.class).show(title);
        }
    }

    private void projectDetail() {
        Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();

        setProjectStage("Добавить");
        projectStage.setTitle("Изменить");
        //  projectController.setData(exemp);
        projectStage.showAndWait();
        updateTableView();

        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void contractStage() {
        if (contractStage == null) {
            contractStage = new Stage();
            //     contractStage.setScene(new Scene(fxmlContract));
            contractStage.setMinHeight(100);
            contractStage.setMinWidth(200);
            contractStage.setResizable(false);
            contractStage.initModality(Modality.WINDOW_MODAL);
            contractStage.initOwner(mainStage);
        }
    }

    private void contractDetail() {
        Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();

        contractStage();
        contractStage.setTitle("Изменить");
        // contractController.setData(exemp);
        contractStage.showAndWait();
        updateTableView();

        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void setRequestStage(String title) {
        if (requestStage == null) {
            fxWeaver.loadController(RequestController.class).show(title);
        }
    }

    private void requestDetail() {
        Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();
        setRequestStage("Изменить");
        // requestController.setData(exemp);
        updateTableView();
        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void projectAccessDetail() {
        if (projectAccessStage == null) {
            projectAccessStage = new Stage();
            projectAccessStage.setTitle("Доступ к проекту");
            //    projectAccessStage.setScene(new Scene(fxmlProjectAccess));
            projectAccessStage.setMinHeight(400);
            projectAccessStage.setMinWidth(800);
            projectAccessStage.initModality(Modality.WINDOW_MODAL);
            projectAccessStage.initOwner(mainStage);
        }
        if (!dataView.getSelectionModel().isEmpty()) {
            //    projectAccessController.addData(exemp);
            //    projectAccessStage.setOnCloseRequest(arg0 -> projectAccessController.formClear());
            projectAccessStage.showAndWait();
        } else {
            setAlertStage(CHOOSE_ERROR, null, "Выберите проект", AlertController.AlertType.INFO);
        }
    }

    private void userAccessDetail() {
        if (userAccessStage == null) {
            userAccessStage = new Stage();
            userAccessStage.setTitle("Подчиненность сотрудников");
            //   userAccessStage.setScene(new Scene(fxmlUserAccess));
            userAccessStage.setMinHeight(400);
            userAccessStage.setMinWidth(800);
            userAccessStage.initModality(Modality.WINDOW_MODAL);
            userAccessStage.initOwner(mainStage);
        }
        if (!dataView.getSelectionModel().isEmpty()) {
            //  userAccessController.addData(exemp);
            //  userAccessStage.setOnCloseRequest(arg0 -> projectAccessController.formClear());
            userAccessStage.showAndWait();
        } else {
            setAlertStage(CHOOSE_ERROR, null, "Выберите сотрудника", AlertController.AlertType.INFO);
        }
    }

    private void accessToolStage() {
        if (accessToolStage == null) {
            accessToolStage = new Stage();
            //   accessToolStage.setScene(new Scene(fxmlAccessTool));
            accessToolStage.setMinHeight(100);
            accessToolStage.setMinWidth(300);
            accessToolStage.initModality(Modality.WINDOW_MODAL);
            accessToolStage.initOwner(mainStage);
        }
    }

    private void requestLinkStage() {
        if (requestLinkStage == null) {
            requestLinkStage = new Stage();
            requestLinkStage.setTitle("Привязка задач");
            //   requestLinkStage.setScene(new Scene(fxmlRequestLink));
            requestLinkStage.setMinHeight(768);
            requestLinkStage.setMinWidth(1024);
            requestLinkStage.initModality(Modality.WINDOW_MODAL);
            requestLinkStage.initOwner(mainStage);
        }
        if (!dataView.getSelectionModel().isEmpty()) {
            //  requestLinkController.addData(exemp);
            //  requestLinkStage.setOnCloseRequest(arg0 -> requestLinkController.formClear());
            requestLinkStage.showAndWait();
        } else {
            setAlertStage(CHOOSE_ERROR, null, "Выберите заявку", AlertController.AlertType.INFO);
        }
    }

    private void passChangeStage() {
        if (passChangeStage == null) {
            passChangeStage = new Stage();
            // passChangeStage.setScene(new Scene(fxmlPassChange));
            passChangeStage.setMinHeight(100);
            passChangeStage.setMinWidth(300);
            passChangeStage.initModality(Modality.WINDOW_MODAL);
            passChangeStage.initOwner(mainStage);
        }
    }

    private void requestViewStage() {
        if (requestViewStage == null) {
            requestViewStage = new Stage();
            // requestViewStage.setScene(new Scene(fxmlRequestView));
            requestViewStage.setMinHeight(700);
            requestViewStage.setMinWidth(1300);
            requestViewStage.initModality(Modality.NONE);
            requestViewStage.initOwner(mainStage);
        }
    }

    private void requestView() {
        requestViewStage();

        Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();

        requestViewStage.setTitle("Перспектива 'Заявка'");
        // requestViewController.initRequestView(exemp);

        requestViewStage.show();
        // requestViewController.setScrollBarBinding();
        updateTableView();

        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void taskViewStage() {
        if (taskViewStage == null) {
            taskViewStage = new Stage();
            // taskViewStage.setScene(new Scene(fxmlTaskView));
            taskViewStage.setMinHeight(700);
            taskViewStage.setMinWidth(1300);
            taskViewStage.initModality(Modality.NONE);
            taskViewStage.initOwner(mainStage);
        }
    }

    private void taskView() {
        Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();

        taskViewStage();

        taskViewStage.setTitle("Перспектива 'Задача'");
        //  taskViewController.initTaskView(workNumber);

        taskViewStage.show();
        updateTableView();

        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void updateTableView() {
        prevNode = "";
        tableShowContent(treeView.getSelectionModel().getSelectedItem().getValue(), false);

        if (treeView.getSelectionModel().getSelectedItem().getValue().equalsIgnoreCase("Сотрудники")) {
            if (!siteFilterBox.getCheckModel().isEmpty() || !roleFilterBox.getCheckModel().isEmpty() || !stateFilterBox.getCheckModel().isEmpty()) {
                actionFilter();
            } else if (!fldSearch.getText().equalsIgnoreCase("")) {
                actionSearch();
            }
        } else if (treeView.getSelectionModel().getSelectedItem().getValue().equalsIgnoreCase("Мои задачи") ||
                treeView.getSelectionModel().getSelectedItem().getValue().equalsIgnoreCase("В работе") ||
                treeView.getSelectionModel().getSelectedItem().getValue().equalsIgnoreCase("Выполнено") ||
                treeView.getSelectionModel().getSelectedItem().getValue().equalsIgnoreCase("Проверено") ||
                treeView.getSelectionModel().getSelectedItem().getValue().equalsIgnoreCase("Утверждено") ||
                treeView.getSelectionModel().getSelectedItem().getValue().equalsIgnoreCase("В ожидании")) {

            if (!customerFilterBox.getCheckModel().isEmpty() || !contractFilterBox.getCheckModel().isEmpty() || !projectFilterBox.getCheckModel().isEmpty()) {
                actionFilter();
            } else if (!fldSearch.getText().equalsIgnoreCase("")) {
                actionSearch();
            }
        }
    }

    private void tableShowContent(String nodeName, boolean isNewRequest) {
        if (nodeName != prevNode || isNewRequest) {

            if (!nodeName.equals(who)) {
                prevNode = nodeName;

                //   dataView.getContextMenu().hide();

                if (nodeName.equalsIgnoreCase(root)) {
                    sql = null;
                }
                if (nodeName.equals("Табель") && inDetails.isSelected()) {
                    holidays.clear();
                    workdays.clear();
                    calendarResult = getCalendarData();
                    for (CalendarEntity c :
                            calendarResult) {
                        if (c.getType().equals("holiday")) {
                            holidays.add(c.getDay());
                        } else {
                            workdays.add(c.getDay());
                        }
                    }
                }
                // Проверка выделенного узла
                switch (nodeName) {
                    case "Менеджмент":
                        sql = null;
                        break;
                    case "Мои проекты":
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                            sql = projectRepository.getMyProjectsAll();
                        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
                            sql = projectRepository.getMyProjectsPM(who);
                        } else {
                            sql = null;
                        }
                        break;
                    case "Мои задачи":
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                            sql = taskRepository.getMyTasksAll();
                        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
                            sql = taskRepository.getMyTasksPM(who);
                        } else {
                            sql = taskRepository.getMyTasksByUser(who);
                        }
                        break;
                    case "В работе":
                        worksGen("в работе");
                        break;
                    case "Выполнено":
                        worksGen("выполнено");
                        break;
                    case "Проверено":
                        worksGen("проверено");
                        break;
                    case "Утверждено":
                        worksGen("утверждено");
                        break;
                    case "В ожидании":
                        worksGen("в ожидании");
                        break;
                    case "Заказчики":
                        if (role.equalsIgnoreCase("Сотрудник")) {
                            sql = null;
                        } else {
                            sql = customerRepository.getAllCustomersNames();
                        }
                        break;
                    case "Площадки":
                        if (role.equalsIgnoreCase("Сотрудник")) {
                            sql = null;
                        } else {
                            sql = siteRepository.getAllSites();
                        }
                        break;
                    case "Контракты":
                        if (role.equalsIgnoreCase("Super_user") || role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("АУП")) {
                            sql = contractRepository.getMyContractsAll();
                        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
                            sql = contractRepository.getMyContractsPM(who);
                        } else {
                            sql = null;
                        }
                        break;
                    case "Заявки":
                        if (role.equalsIgnoreCase("Super_user") || role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("АУП")) {
                            sql = requestRepository.getMyRequestsAll();
                        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
                            sql = requestRepository.getMyRequestsPM(who);
                        } else {
                            sql = null;
                        }
                        break;
                    case "Компания":
                        sql = null;
                        break;
                    case "Сотрудники":
                        if (role.equalsIgnoreCase("Super_user") || role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("АУП")) {
                            sql = userRepository.getEmployeeAll();
                        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
                            sql = userRepository.getEmployeeSubOrd(who);
                        } else if (role.equalsIgnoreCase("Начальник отдела") || role.equalsIgnoreCase("Ведущий специалист")) {
                            sql = userRepository.getEmployeeSubOrd(who);
                        } else {
                            sql = userRepository.getEmployeeByName(who);
                        }
                        break;
                    case "Табель":
                        if (role.equalsIgnoreCase("Super_user") || role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("АУП")) {
                            if (!inDetails.isSelected()) {
                                sql = timesheetViewRepository.getEmployeeAll();
                            } else {
                                sql = timesheetViewRepository.getEmployeeAllDetailed("2022-09-01", "2022-10-01");
                            }
                        } else if (role.equalsIgnoreCase("Сотрудник")) {
                            sql = timesheetViewRepository.getEmployeeByName(who);
                        } else {
                            if (!inDetails.isSelected()) {
                                sql = timesheetViewRepository.getEmployeeSubOrd(who);
                            } else {
                                sql = timesheetViewRepository.getEmployeeSubOrdDetailed(who, "2022-09-01", "2022-10-01");
                            }
                        }
                        break;
                    case "Справочники":
                        sql = null;
                        break;
                }
                // actionFilter();
                dataView.getItems().clear();
                if (!sql.equals("")) {
                    //                    tableGenerator(sql);
                    actionFilter();
                } else {
                    dataView.getColumns().clear();
                }
            }
        }
    }

    private void worksGen(String status) {
        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
            sql = taskRepository.genMyTasksAll(status);
        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
            sql = taskRepository.genMyTasksPM(status, who);
        } else {
            sql = taskRepository.genMyTasksByUser(status, who);
        }
    }

    private void tableGenerator(List<Object[]> sql) {
        dataView.setEditable(true);
        dataView.getSelectionModel().clearSelection();
        dataView.getColumns().clear();
        dataView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
        data.clear();
        metaData = sql.remove(0);
        try {
            for (int i = 0; i < metaData.length; i++) {
                final int j = i;
                if (prevNode.equals("Табель") && inDetails.isSelected() && date.getActualMaximum(Calendar.DAY_OF_MONTH) < j - 2) {
                    break;
                }
                tableColName = String.valueOf(metaData[i]);
                tableColName = TableColumnController.generateColName(tableColName);
                TableColumn tableColumn = new TableColumn(tableColName);

                tableColumn.setCellValueFactory(
                        (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(
                                (String) param.getValue().get(j)));
                tableColumn.setCellFactory(column -> EditCell.createStringEditCell());
                if (inDetails.isSelected() && prevNode.equalsIgnoreCase("Табель")) {
                    if (i > 2) {
                        CalendarController.date = date;
                        CalendarCell.setRes(calendarResult);
                        int d = i - 2;
                        if (CalendarController.checkDateForCurrent(d)) {
                            if ((CalendarController.checkDate(d) || CalendarCell.createCalendarCell().checkDateForHoliday(date.get(Calendar.MONTH), d)) &&
                                    !CalendarCell.createCalendarCell().checkDateForWorkday(date.get(Calendar.MONTH), d)) {
                                tableColumn.setCellFactory(column -> {
                                    WeekEndCell w = WeekEndCell.createStringEditCell();
                                    w.setStyle("-fx-alignment: CENTER;  -fx-background-color:#fce4d6;");
                                    return w;
                                });
                            } else {
                                tableColumn.setCellFactory(column -> CurrentDateCell.createStringCurrentDateCell());
                            }
                        } else if (CalendarController.checkDate(d) && !CalendarCell.createCalendarCell().checkDateForWorkday(date.get(Calendar.MONTH), d)) {
                            tableColumn.setCellFactory(column ->
                            {
                                WeekEndCell w = WeekEndCell.createStringEditCell();
                                return w;
                            });
                        } else if (CalendarCell.createCalendarCell().checkDateForHoliday(date.get(Calendar.MONTH), d) &&
                                !CalendarCell.createCalendarCell().checkDateForWorkday(date.get(Calendar.MONTH), d)) {
                            tableColumn.setCellFactory(column -> {
                                WeekEndCell w = WeekEndCell.createStringEditCell();
                                return w;
                            });
                        } else {
                            tableColumn.setCellFactory(column -> EditCell.createStringEditCell());
                        }
                    }
                }
                if (prevNode.equals("Мои задачи") || prevNode.equals("В работе") || prevNode.equals("Выполнено") || prevNode.equals("Проверено") ||
                        prevNode.equals("Утверждено") || prevNode.equals("В ожидании")) {
                    if (tableColName.equals("Наименование задачи")) {
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
                                };
                            }
                        });
                        tableColumn.setPrefWidth(550);
                    } else {
                        tableColumn.setStyle("-fx-alignment: CENTER;");
                    }
                } else {
                    if (tableColName.equals("ФИО") || tableColName.equals("Номер контракта") || tableColName.equals("Проект") ||
                            tableColName.equals("Название контракта")
                            || tableColName.equals("Полное наименование") || tableColName.equals("Номер заявки") ||
                            tableColName.equals("Описание заявки")) {

                    } else {
                        tableColumn.setStyle("-fx-alignment: CENTER;");
                    }
                }

            /*     tableColumn.setOnEditCommit(((EventHandler<TableColumn.CellEditEvent<ObservableList, String>>) event -> {
                  editCell(event);
                     }));*/

                dataView.setRowFactory(row -> new TableRow<ObservableList>() {

                    @Override
                    public void updateItem(ObservableList item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setStyle("");
                        } else {
                            if (item.toString().contains("утверждено")) {
                                setStyle("-fx-background-color: lightgreen;");
                            } else if (item.toString().contains("в ожидании")) {
                                setStyle("-fx-background-color: orange;");
                            } else if (item.toString().contains("проверено")) {
                                setStyle("-fx-background-color: turquoise;");
                            } else if (item.toString().contains("выполнено")) {
                                setStyle("-fx-background-color: powderblue;");
                            } else if (item.toString().contains("Уволен")) {
                                setStyle("-fx-background-color: LAVENDERBLUSH;");
                            } else {
                                setStyle("");
                            }
                        }
                    }

                    @Override
                    public void updateSelected(boolean selected) {
                        super.updateSelected(selected);
                        try {
                            if (selected) {
                                setStyle("");
                            } else {
                                if (getItem().toString().contains("утверждено")) {
                                    setStyle("-fx-background-color: lightgreen;");
                                } else if (getItem().toString().contains("в ожидании")) {
                                    setStyle("-fx-background-color: orange;");
                                } else if (getItem().toString().contains("проверено")) {
                                    setStyle("-fx-background-color: turquoise;");
                                } else if (getItem().toString().contains("выполнено")) {
                                    setStyle("-fx-background-color: powderblue;");
                                } else if (getItem().toString().contains("Уволен")) {
                                    setStyle("-fx-background-color: LAVENDERBLUSH;");
                                } else {
                                    setStyle("");
                                }
                            }
                        } catch (NullPointerException nle) {

                        }
                    }
                });

                dataView.getColumns().addAll(tableColumn);
            }

            //наполнение observableList данными из базы
            for (Object[] l : this.sql) {
                int passedWorkDays = 0;
                int filledWorkDays = 0;
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= l.length; i++) {
                    String value = String.valueOf(l[i - 1]);
                    if (inDetails.isSelected() && prevNode.equalsIgnoreCase("Табель")) {
                        if (i > 3) {
                            if (date.getActualMaximum(Calendar.DAY_OF_MONTH) >= (i - 3)) {
                                date.set(Calendar.DAY_OF_MONTH, i - 3);
                                if (date.before(current)) {
                                    if (!holidays.contains(i - 3) && date.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                                            date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                                        passedWorkDays++;
                                        if (value != null && !value.equals("") && !value.equals("null")) {
                                            filledWorkDays++;
                                        }
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    if (value == null) {
                        value = "";
                    }
                    row.add(value);
                }
                if (inDetails.isSelected() && prevNode.equalsIgnoreCase("Табель")) {
                    if (filledWorkDays == 0) {
                        row.set(2, "0 %");
                    } else {
                        double percentage = (double) passedWorkDays / filledWorkDays;
                        row.set(2, df.format(100 / percentage) + " %");
                    }
                }
                data.add(row);
            }

            //Добавление данных в TableView
            dataView.setItems(data);
            // dataView.setSelectionModel(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionChangeDate(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) {
            return;
        }
        Button clickedButton = (Button) source;

        switch (clickedButton.getId()) {
            case "backBtn":
                if (date.get(GregorianCalendar.MONTH) == 0) {
                    date.set(date.get(GregorianCalendar.YEAR) - 1, 11, 1);
                } else {
                    date.set(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH) - 1, 1);
                }
                monthBox.setValue(months[date.get(Calendar.MONTH)]);
                yearBox.setValue(String.valueOf(date.get(Calendar.YEAR)));
                tableShowContent("Табель", true);
                break;
            case "forwardBtn":
                if (date.get(GregorianCalendar.MONTH) == 11) {
                    date.set(date.get(GregorianCalendar.YEAR) + 1, 0, 1);
                } else {
                    date.set(date.get(GregorianCalendar.YEAR), date.get(GregorianCalendar.MONTH) + 1, 1);
                }

                monthBox.setValue(months[date.get(Calendar.MONTH)]);
                yearBox.setValue(String.valueOf(date.get(Calendar.YEAR)));
                tableShowContent("Табель", true);
                break;
        }
    }

    // генерация русскоязычных названий колонок таблицы

    public void actionSearch() {
        backupList.clear();
        data.clear();
        if (!sql.equals("")) {
            //  tableGenerator(sql);
            backupList.addAll(data);
            dataView.getItems().clear();
            for (ObservableList observableList : backupList) {
                if (observableList.toString().toLowerCase().contains(fldSearch.getText().toLowerCase())) {
                    dataView.getItems().add(observableList);
                }
            }
        }
    }

    public void actionFilter() {
        backupList.clear();
        filteredBackup.clear();
        tableGenerator(sql);
        backupList.addAll(data);
        dataView.getItems().clear();
        Boolean contains = false;
        systemLabel.setText("Текущий фильтр");
        systemLabel.setTextFill(Color.RED);

        if (prevNode.equalsIgnoreCase("Сотрудники") || prevNode.equalsIgnoreCase("Табель")) {

            if (!siteFilterBox.getCheckModel().getCheckedItems().isEmpty()) {

                filterKeys.clear();
                filterKeys.addAll(siteFilterBox.getCheckModel().getCheckedItems());

                for (ObservableList observableList : backupList) {
                    for (ObservableList siteFilter : filterKeys) {
                        if (observableList.toString().contains(siteFilter.toString().replace("[", "").replace("]", ""))) {
                            contains = true;
                        }
                    }

                    if (!contains) {
                        filteredBackup.add(observableList);
                    }

                    contains = false;
                }

                backupList.removeAll(filteredBackup);
                filteredBackup.clear();

                systemLabel.setText(
                        systemLabel.getText() + " / " + siteFilterBox.getCheckModel().getCheckedItems().toString().replace("[", "").replace("]", ""));
            }

            if (!roleFilterBox.getCheckModel().getCheckedItems().isEmpty()) {

                filterKeys.clear();
                filterKeys.addAll(roleFilterBox.getCheckModel().getCheckedItems());

                for (ObservableList observableList : backupList) {
                    for (ObservableList roleFilter : filterKeys) {
                        if (observableList.toString().contains(roleFilter.toString().replace("[", "").replace("]", ""))) {
                            contains = true;
                        }
                    }

                    if (!contains) {
                        filteredBackup.add(observableList);
                    }

                    contains = false;
                }

                backupList.removeAll(filteredBackup);
                filteredBackup.clear();

                systemLabel.setText(
                        systemLabel.getText() + " / " + roleFilterBox.getCheckModel().getCheckedItems().toString().replace("[", "").replace("]", ""));
            }

            if (!stateFilterBox.getCheckModel().getCheckedItems().isEmpty()) {

                filterKeys.clear();
                filterKeys.addAll(stateFilterBox.getCheckModel().getCheckedItems());

                for (ObservableList observableList : backupList) {
                    for (ObservableList stateFilter : filterKeys) {
                        if (observableList.toString().contains(stateFilter.toString().replace("[", "").replace("]", ""))) {
                            contains = true;
                        }
                    }

                    if (!contains) {
                        filteredBackup.add(observableList);
                    }

                    contains = false;
                }

                backupList.removeAll(filteredBackup);
                filteredBackup.clear();

                systemLabel.setText(
                        systemLabel.getText() + " / " + stateFilterBox.getCheckModel().getCheckedItems().toString().replace("[", "").replace("]", ""));
            }
        }

        if (prevNode.equals("Мои задачи") || prevNode.equals("В работе") || prevNode.equals("Выполнено") || prevNode.equals("Проверено") ||
                prevNode.equals("Утверждено") || prevNode.equals("В ожидании")) {
            if (!customerFilterBox.getCheckModel().getCheckedItems().isEmpty()) {

                filterKeys.clear();
                filterKeys.addAll(customerFilterBox.getCheckModel().getCheckedItems());

                for (ObservableList observableList : backupList) {
                    for (ObservableList siteFilter : filterKeys) {
                        if (observableList.toString().contains(siteFilter.toString().replace("[", "").replace("]", ""))) {
                            contains = true;
                        }
                    }

                    if (!contains) {
                        filteredBackup.add(observableList);
                    }

                    contains = false;
                }

                backupList.removeAll(filteredBackup);
                filteredBackup.clear();

                systemLabel.setText(
                        systemLabel.getText() + " / " +
                                customerFilterBox.getCheckModel().getCheckedItems().toString().replace("[", "").replace("]", ""));
            }

            if (!contractFilterBox.getCheckModel().getCheckedItems().isEmpty()) {

                filterKeys.clear();
                filterKeys.addAll(contractFilterBox.getCheckModel().getCheckedItems());

                for (ObservableList observableList : backupList) {
                    for (Object siteFilter : filterKeys) {
                        if (observableList.toString().contains(siteFilter.toString().replace("[", "").replace("]", ""))) {
                            contains = true;
                        }
                    }

                    if (!contains) {
                        filteredBackup.add(observableList);
                    }

                    contains = false;
                }

                backupList.removeAll(filteredBackup);
                filteredBackup.clear();

                systemLabel.setText(
                        systemLabel.getText() + " / " +
                                contractFilterBox.getCheckModel().getCheckedItems().toString().replace("[", "").replace("]", ""));
            }

            if (!projectFilterBox.getCheckModel().getCheckedItems().isEmpty()) {

                filterKeys.clear();
                filterKeys.addAll(projectFilterBox.getCheckModel().getCheckedItems());

                for (ObservableList observableList : backupList) {
                    for (Object siteFilter : filterKeys) {
                        if (observableList.toString().contains(siteFilter.toString().replace("[", "").replace("]", ""))) {
                            contains = true;
                        }
                    }

                    if (!contains) {
                        filteredBackup.add(observableList);
                    }

                    contains = false;
                }

                backupList.removeAll(filteredBackup);
                filteredBackup.clear();

                systemLabel.setText(
                        systemLabel.getText() + " / " +
                                projectFilterBox.getCheckModel().getCheckedItems().toString().replace("[", "").replace("]", ""));
            }
        }

        dataView.getItems().addAll(backupList);
    }

    public void actionEmplFilterClear() {
        if (prevNode.equalsIgnoreCase("Сотрудники") || prevNode.equalsIgnoreCase("Табель")) {
            siteFilterBox.getCheckModel().clearChecks();
            roleFilterBox.getCheckModel().clearChecks();
            stateFilterBox.getCheckModel().clearChecks();
        }

        if (prevNode.equals("Мои задачи") || prevNode.equals("В работе") || prevNode.equals("Выполнено") || prevNode.equals("Проверено") ||
                prevNode.equals("Утверждено") || prevNode.equals("В ожидании")) {
            customerFilterBox.getCheckModel().clearChecks();
            contractFilterBox.getCheckModel().clearChecks();
            projectFilterBox.getCheckModel().clearChecks();
        }
        actionFilter();
    }

    public void deleteItem(String itemID) {

        long res = taskRepository.deleteByTaskNumber(itemID);
        if (res > 0) {
            setAlertStage("Действие выполнено", "Удаление задачи", "Выбранная задача была удалена из системы.", AlertController.AlertType.INFO);
            updateTableView();
        } else {
            setAlertStage(DATA_DELETE_ERROR, "В системе присутствуют связанные данные.",
                    "Возможно на задачу назначены исполнители или списаны часы.",
                    AlertController.AlertType.WARN);
        }
    }

    private ObservableList data(List<Object[]> list) {
        try {
            dataSelect.clear();
            for (int j = 0; j < list.size(); j++) {

                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 0; i < metaData.length; i++) {
                    //Перебор колонок
                    row.add(String.valueOf(sql.get(j)[i]));
                }
                dataSelect.add(row);
            }
        } catch (Exception e) {
            setAlertStage(DATA_ERROR,
                    "",
                    e.getMessage(),
                    AlertController.AlertType.ERROR);
            e.printStackTrace();
        }
        return dataSelect;
    }

    private ObservableList filteredFill(ObservableList observableList, List<Object[]> list) {
        try {

            for (int j = 0; j < list.size(); j++) {

                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 0; i < metaData.length; i++) {
                    //Перебор колонок
                    row.add(String.valueOf(sql.get(j)[i]));
                }
                observableList.add(row);
            }
        } catch (Exception e) {
            setAlertStage(DATA_ERROR, "", e.getMessage(), AlertController.AlertType.ERROR);
            e.printStackTrace();
        }
        return observableList;
    }

    private void setAlertStage(String title, String header, String content, AlertController.AlertType type) {
        alertDialog = fxWeaver.load(AlertController.class);
        AlertController controller = alertDialog.getController();
        controller.setTitle(title);
        controller.setHeader(header);
        controller.setContent(content);
        controller.show();
    }
}