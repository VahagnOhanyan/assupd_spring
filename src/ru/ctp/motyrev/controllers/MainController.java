package ru.ctp.motyrev.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;
import ru.ctp.motyrev.code.DBconnection;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;
import static javafx.scene.control.TableView.UNCONSTRAINED_RESIZE_POLICY;

public class MainController {

    private Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);
    private Alert warnAlert = new Alert(Alert.AlertType.WARNING);

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
    private String sql = "";
    private String tableColName;
    private String workNumber;
    private String workName;
    private String workId;
    private String exemp;

    public static String role;
    public static String who;
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

    MenuItem menuEditRow = new MenuItem("Редактировать запись");

    DBconnection dBconnection = new DBconnection();

    private Parent fxmlSheet;
    private Parent fxmlProject;
    private Parent fxmlRequest;
    private Parent fxmlTask;
    private Parent fxmlPeriodReport;
    private Parent fxmlResourceReport;
    private Parent fxmlCustomer;
    private Parent fxmlTimeSheet;
    private Parent fxmlUser;
    private Parent fxmlExecutor;
    private Parent fxmlContract;
    private Parent fxmlProjectAccess;
    private Parent fxmlUserAccess;
    private Parent fxmlAccessTool;
    private Parent fxmlRequestLink;
    private Parent fxmlPassChange;
    private Parent fxmlRequestView;
    private Parent fxmlSite;
    private Parent fxmlTaskView;
    private Parent fxmlEffectivityReport;
    private Parent fileChooserView;
    private Parent calendarView;
    private FXMLLoader fxmlSheetLoader = new FXMLLoader();
    private FXMLLoader fxmlProjectLoader = new FXMLLoader();
    private FXMLLoader fxmlRequestLoader = new FXMLLoader();
    private FXMLLoader fxmlTaskLoader = new FXMLLoader();
    private FXMLLoader fxmlPeriodReportLoader = new FXMLLoader();
    private FXMLLoader fxmlResourceReportLoader = new FXMLLoader();
    private FXMLLoader fxmlCustomerLoader = new FXMLLoader();
    private FXMLLoader fxmlTimeSheetLoader = new FXMLLoader();
    private FXMLLoader fxmlUserLoader = new FXMLLoader();
    private FXMLLoader fxmlExecutorLoader = new FXMLLoader();
    private FXMLLoader fxmlContractLoader = new FXMLLoader();
    private FXMLLoader fxmlProjectAccessLoader = new FXMLLoader();
    private FXMLLoader fxmlUserAccessLoader = new FXMLLoader();
    private FXMLLoader fxmlAccessToolLoader = new FXMLLoader();
    private FXMLLoader fxmlRequestLinkLoader = new FXMLLoader();
    private FXMLLoader fxmlPassChangeLoader = new FXMLLoader();
    private FXMLLoader fxmlRequestViewLoader = new FXMLLoader();
    private FXMLLoader fxmlSiteLoader = new FXMLLoader();
    private FXMLLoader fxmlTaskViewLoader = new FXMLLoader();
    private FXMLLoader fxmlEffectivityReportLoader = new FXMLLoader();
    private FXMLLoader fileChooserViewLoader = new FXMLLoader();
    private FXMLLoader calendarViewLoader = new FXMLLoader();
    private SheetController sheetController;
    private ProjectController projectController;
    private RequestController requestController;
    private TaskController taskController;
    private PeriodReportController periodReportController;
    private ResourceReportController resourceReportController;
    private CustomerController customerController;
    private TimeSheetController timeSheetController;
    private UserController userController;
    private ExecutorController executorController;
    private ContractController contractController;
    private ProjectAccessController projectAccessController;
    private UserAccessController userAccessController;
    private AccessToolController accessToolController;
    private RequestLinkController requestLinkController;
    private PassChangeController passChangeController;
    private RequestViewController requestViewController;
    private SiteController siteController;
    private TaskViewController taskViewController;
    private EffectivityReportController effectivityReportController;
    private HolidaysController fileChooserController;
    private CalendarController calendarController;
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
    public Stage fileChooserStage;
    public Stage calendarStage;

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    private void initialize() {
        fillTreeView();
        initLoader();
        initListeners();

        /*ContextMenu menu = new ContextMenu();
        menuEditRow.setGraphic(new ImageView(new Image("/ru/ctp/motyrev/images/pensil.png")));
        menu.getItems().add(menuEditRow);
        dataView.setContextMenu(menu);
        menuEditRow.setVisible(false);*/

        sheetTools.setVisible(false);
        emplTools.setVisible(false);

        openViewButton.setDisable(true);
        deleteButton.setDisable(true);

        root = who + " (" + role + ")";

        Image createImage = new Image("/ru/ctp/motyrev/images/newObject.png");
        Image editImage = new Image("/ru/ctp/motyrev/images/edit.png");
        Image linksImage = new Image("/ru/ctp/motyrev/images/links.png");
        Image reportImage = new Image("/ru/ctp/motyrev/images/report.png");
        Image viewImage = new Image("/ru/ctp/motyrev/images/view.png");
        Image deleteImage = new Image("/ru/ctp/motyrev/images/delete.png");

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
    }

    private void initLoader() {
        try {

            fxmlSheetLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/sheetConfig.fxml"));
            fxmlSheet = fxmlSheetLoader.load();
            sheetController = fxmlSheetLoader.getController();

            fxmlProjectLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/project.fxml"));
            fxmlProject = fxmlProjectLoader.load();
            projectController = fxmlProjectLoader.getController();

            fxmlRequestLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/request.fxml"));
            fxmlRequest = fxmlRequestLoader.load();
            requestController = fxmlRequestLoader.getController();

            fxmlTaskLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/task.fxml"));
            fxmlTask = fxmlTaskLoader.load();
            taskController = fxmlTaskLoader.getController();

            fxmlPeriodReportLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/periodReport.fxml"));
            fxmlPeriodReport = fxmlPeriodReportLoader.load();
            periodReportController = fxmlPeriodReportLoader.getController();

            fxmlResourceReportLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/resourceReport.fxml"));
            fxmlResourceReport = fxmlResourceReportLoader.load();
            resourceReportController = fxmlResourceReportLoader.getController();

            fxmlCustomerLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/customer.fxml"));
            fxmlCustomer = fxmlCustomerLoader.load();
            customerController = fxmlCustomerLoader.getController();

            fxmlTimeSheetLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/timeSheet.fxml"));
            fxmlTimeSheet = fxmlTimeSheetLoader.load();
            timeSheetController = fxmlTimeSheetLoader.getController();

            fxmlUserLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/user.fxml"));
            fxmlUser = fxmlUserLoader.load();
            userController = fxmlUserLoader.getController();

            fxmlExecutorLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/taskExecutor.fxml"));
            fxmlExecutor = fxmlExecutorLoader.load();
            executorController = fxmlExecutorLoader.getController();

            fxmlContractLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/contract.fxml"));
            fxmlContract = fxmlContractLoader.load();
            contractController = fxmlContractLoader.getController();

            fxmlProjectAccessLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/projectAccess.fxml"));
            fxmlProjectAccess = fxmlProjectAccessLoader.load();
            projectAccessController = fxmlProjectAccessLoader.getController();

            fxmlUserAccessLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/userAccess.fxml"));
            fxmlUserAccess = fxmlUserAccessLoader.load();
            userAccessController = fxmlUserAccessLoader.getController();

            fxmlAccessToolLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/accessTool.fxml"));
            fxmlAccessTool = fxmlAccessToolLoader.load();
            accessToolController = fxmlAccessToolLoader.getController();

            fxmlRequestLinkLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/requestLink.fxml"));
            fxmlRequestLink = fxmlRequestLinkLoader.load();
            requestLinkController = fxmlRequestLinkLoader.getController();

            fxmlPassChangeLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/passChange.fxml"));
            fxmlPassChange = fxmlPassChangeLoader.load();
            passChangeController = fxmlPassChangeLoader.getController();

            fxmlRequestViewLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/requestView.fxml"));
            fxmlRequestView = fxmlRequestViewLoader.load();
            requestViewController = fxmlRequestViewLoader.getController();

            fxmlSiteLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/site.fxml"));
            fxmlSite = fxmlSiteLoader.load();
            siteController = fxmlSiteLoader.getController();

            fxmlTaskViewLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/taskView.fxml"));
            fxmlTaskView = fxmlTaskViewLoader.load();
            taskViewController = fxmlTaskViewLoader.getController();

            fxmlEffectivityReportLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/effectivityReport.fxml"));
            fxmlEffectivityReport = fxmlEffectivityReportLoader.load();
            effectivityReportController = fxmlEffectivityReportLoader.getController();

            fileChooserViewLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/holidaysCsvFile.fxml"));
            fileChooserView = fileChooserViewLoader.load();
            fileChooserController = fileChooserViewLoader.getController();

            calendarViewLoader.setLocation(getClass().getResource("/ru/ctp/motyrev/fxml/calendar.fxml"));
            calendarView = calendarViewLoader.load();
            calendarController = calendarViewLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {

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

                taskStatusBox.getItems().addAll(data("SELECT status_name FROM public.status s " +
                        "join public.status_type st on st.status_type_id = s.status_type " +
                        "WHERE st.status_type_name = 'tasks'"));

                customers = data("SELECT customer_name FROM public.customer ORDER BY customer_name");
                customerFilterBox.getItems().addAll(customers);

                filteredFill(contracts, "SELECT cr.customer_name, c.contract_number FROM public.contract c " +
                        "join public.customer cr on cr.customer_id = c.customer_id " +
                        "ORDER BY cr.customer_name, c.contract_number");

                for (ObservableList contract : contracts) {
                    contractFilterBox.getItems().add(contract.get(1));
                }

                filteredFill(projects, "SELECT cr.customer_name, c.contract_number, p.project_name FROM public.project p " +
                        "join public.contract_project cp on cp.project_id = p.project_id " +
                        "join public.contract c on c.contract_id = cp.contract_id " +
                        "join public.customer cr on cr.customer_id = c.customer_id " +
                        "ORDER BY cr.customer_name, c.contract_number, p.project_name");

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

                siteFilterBox.getItems().addAll(data("SELECT site_name FROM public.site ORDER BY site_id"));
                roleFilterBox.getItems().addAll(data(
                        "SELECT user_role_name FROM public.user_role WHERE user_role_name != 'Super_user' AND user_role_name != 'Admin' ORDER BY user_role_name"));
                stateFilterBox.getItems().addAll(data("SELECT user_activity_name FROM public.user_activity ORDER BY user_activity_id"));

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

            tableShowContent(newValue.getValue());
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

            /*if (newValue.equals(null)) {
                menuEditRow.setVisible(false);
            } else {
                menuEditRow.setVisible(true);
            }*/
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
                tableShowContent(searchNode);
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

    private void fillTreeView() {
        try {
            Image nodeImage = new Image("/ru/ctp/motyrev/images/folder.png");
            Image fullImage = new Image("/ru/ctp/motyrev/images/folderFull.png");
            Image inworkImage = new Image("/ru/ctp/motyrev/images/inwork.png");
            Image completeImage = new Image("/ru/ctp/motyrev/images/complete.png");
            Image checkedImage = new Image("/ru/ctp/motyrev/images/checked.png");
            Image approvedImage = new Image("/ru/ctp/motyrev/images/approved.png");
            Image timedImage = new Image("/ru/ctp/motyrev/images/timed.png");
            Image projectImage = new Image("/ru/ctp/motyrev/images/project.png");
            Image tweakNodeImage = new Image("/ru/ctp/motyrev/images/tweak.png");
            Image clockNodeImage = new Image("/ru/ctp/motyrev/images/clock.png");
            Image userImage = new Image("/ru/ctp/motyrev/images/user.png");

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
                infoAlert.setTitle("Ошибка доступа");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Недостаточно прав для выполнения действия");
                infoAlert.showAndWait();
            }
        } else if (prevNode.equals("Мои проекты")) {
            if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                    role.equalsIgnoreCase("Менеджер проекта")) {
                projectAccessDetail();
            } else {
                infoAlert.setTitle("Ошибка доступа");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Недостаточно прав для выполнения действия");
                infoAlert.showAndWait();
            }
        } else if (prevNode.equals("Сотрудники")) {
            if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                    role.equalsIgnoreCase("Менеджер проекта")) {
                userAccessDetail();
            } else {
                infoAlert.setTitle("Ошибка доступа");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Недостаточно прав для выполнения действия");
                infoAlert.showAndWait();
            }
        } else if (prevNode.equals("Заявки")) {
            if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                    role.equalsIgnoreCase("Менеджер проекта")) {
                if (!dataView.getSelectionModel().getSelectedItem().toString().contains("Согласовано")) {
                    requestLinkStage();
                } else {
                    infoAlert.setTitle("Ошибка редактирования");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Невозможно расширить согласованную заявку");
                    infoAlert.showAndWait();
                }
            } else {
                infoAlert.setTitle("Ошибка доступа");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Недостаточно прав для выполнения действия");
                infoAlert.showAndWait();
            }
        } else {
            infoAlert.setTitle("Нет подходящих объектов");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("В данной перспективе связи не доступны");
            infoAlert.showAndWait();
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
                            infoAlert.setTitle("Ошибка доступа");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("Недостаточно прав для выполнения действия");
                            infoAlert.showAndWait();
                        }
                    } else if (prevNode.equals("Заказчики")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                            customerDetail();
                        } else {
                            infoAlert.setTitle("Ошибка доступа");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("Недостаточно прав для выполнения действия");
                            infoAlert.showAndWait();
                        }
                    } else if (prevNode.equals("Площадки")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                            siteDetail();
                        } else {
                            infoAlert.setTitle("Ошибка доступа");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("Недостаточно прав для выполнения действия");
                            infoAlert.showAndWait();
                        }
                    } else if (prevNode.equals("Сотрудники")) {
                        if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                            userDetail();
                        } else {
                            infoAlert.setTitle("Ошибка доступа");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("Недостаточно прав для выполнения действия");
                            infoAlert.showAndWait();
                        }
                    } else if (prevNode.equals("Мои задачи") || prevNode.equals("В работе") || prevNode.equals("Выполнено") || prevNode.equals("Проверено") ||
                            prevNode.equals("Утверждено") || prevNode.equals("В ожидании")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                                role.equalsIgnoreCase("Менеджер проекта")) {
                            taskDetail();
                        } else {
                            infoAlert.setTitle("Ошибка доступа");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("Недостаточно прав для выполнения действия");
                            infoAlert.showAndWait();
                        }
                    } else if (prevNode.equalsIgnoreCase("Табель")) {
                        timeSheetDetail();
                    } else if (prevNode.equals("Заявки")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                                role.equalsIgnoreCase("Менеджер проекта")) {
                            requestDetail();
                        } else {
                            infoAlert.setTitle("Ошибка доступа");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("Недостаточно прав для выполнения действия");
                            infoAlert.showAndWait();
                        }
                    } else if (prevNode.equals("Мои проекты")) {
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                                role.equalsIgnoreCase("Менеджер проекта")) {
                            projectDetail();
                        } else {
                            infoAlert.setTitle("Ошибка доступа");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("Недостаточно прав для выполнения действия");
                            infoAlert.showAndWait();
                        }
                    }
                } else {
                    infoAlert.setTitle("Ошибка выбора");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Выберите значение в таблице");
                    infoAlert.showAndWait();
                }
                break;
            case "btnStatusChange":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user") ||
                        role.equalsIgnoreCase("Менеджер проекта") || role.equalsIgnoreCase("Начальник отдела")) {
                    if (!dataView.getSelectionModel().isEmpty() & !taskStatusBox.getSelectionModel().isEmpty()) {
                        dBconnection.openDB();
                        dBconnection.getStmt().executeUpdate("UPDATE public.task SET status_id = " +
                                "(SELECT s.status_id FROM public.status s " +
                                "join public.status_type st on st.status_type_id = s.status_type " +
                                "WHERE st.status_type_name = 'tasks' AND s.status_name = '" +
                                taskStatusBox.getSelectionModel().getSelectedItem().toString().replace("[", "").replace("]", "") + "') " +
                                "WHERE task_number = '" + workNumber + "'");
                        dBconnection.getC().commit();
                        dBconnection.closeDB();
                    } else if (taskStatusBox.getSelectionModel().isEmpty() & !dataView.getSelectionModel().isEmpty()) {
                        infoAlert.setTitle("Ошибка выбора");
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("Выберите статус");
                        infoAlert.showAndWait();
                    } else if (dataView.getSelectionModel().isEmpty() & !taskStatusBox.getSelectionModel().isEmpty()) {
                        infoAlert.setTitle("Ошибка выбора");
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("Выберите задачу");
                        infoAlert.showAndWait();
                    } else {
                        infoAlert.setTitle("Ошибка выбора");
                        infoAlert.setHeaderText(null);
                        infoAlert.setContentText("Не выбраны данные для изменения статуса");
                        infoAlert.showAndWait();
                    }
                    node = prevNode;
                    prevNode = "";
                    tableShowContent(node);
                } else {
                    infoAlert.setTitle("Ошибка доступа");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Недостаточно прав для выполнения действия");
                    infoAlert.showAndWait();
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
                    infoAlert.setTitle("Ошибка выбора");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Выберите значение в таблице");
                    infoAlert.showAndWait();
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
                            infoAlert.setTitle("Ошибка доступа");
                            infoAlert.setHeaderText(null);
                            infoAlert.setContentText("Недостаточно прав для выполнения действия");
                            infoAlert.showAndWait();
                        }
                    }
                } else {
                    infoAlert.setTitle("Ошибка выбора");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Выберите значение в таблице");
                    infoAlert.showAndWait();
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

                    calendarStage.show();
                }
                break;
            case "calendarImport":
                if (role.equalsIgnoreCase("super_user")) {
                    setFileChooserStage();
                    //FileChooser fileChooser = new FileChooser();
                    //fileChooser.setTitle("Open file")
                    // fileChooser.showOpenDialog(mainStage);
                    fileChooserStage.show();
                }
                break;
            case "menuClose":
                System.exit(0);
                break;
            case "menuProject":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                    projectStage();
                    projectStage.setTitle("Добавить");
                    projectController.addData();
                    projectStage.showAndWait();
                } else {
                    infoAlert.setTitle("Недостаточный уровень прав");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Ваша роль в системе не позволяет управлять выбранными данными.");
                    infoAlert.showAndWait();
                }
                break;
            case "menuTask":
                taskStage();
                taskStage.setTitle("Добавить");
                taskController.addData();
                taskStage.showAndWait();
                break;
            case "menuPeriodReport":
                if (periodReportStage == null) {
                    periodReportStage = new Stage();
                    periodReportStage.setTitle("Добавить");
                    periodReportStage.setScene(new Scene(fxmlPeriodReport));
                    periodReportStage.setMinHeight(100);
                    periodReportStage.setMinWidth(200);
                    periodReportStage.setResizable(false);
                    periodReportStage.initModality(Modality.WINDOW_MODAL);
                    periodReportStage.initOwner(mainStage);
                }
                periodReportController.addData();
                periodReportStage.showAndWait();
                break;
            case "menuResReport":
                if (resourceReportStage == null) {
                    resourceReportStage = new Stage();
                    resourceReportStage.setTitle("Добавить");
                    resourceReportStage.setScene(new Scene(fxmlResourceReport));
                    resourceReportStage.setMinHeight(100);
                    resourceReportStage.setMinWidth(200);
                    resourceReportStage.setResizable(false);
                    resourceReportStage.initModality(Modality.WINDOW_MODAL);
                    resourceReportStage.initOwner(mainStage);
                }
                resourceReportController.addData();
                resourceReportStage.showAndWait();
                break;
            case "menuEffyReport":
                if (!role.equalsIgnoreCase("сотрудник")) {
                    effectivityReportDetail();
                } else {
                    infoAlert.setTitle("Недостаточный уровень прав");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Ваша роль в системе не позволяет просматривать выбранные данные.");
                    infoAlert.showAndWait();
                }
                break;
            case "menuCustomer":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                    customerStage();
                    customerStage.setTitle("Добавить заказчика");
                    customerController.createData();
                    customerStage.showAndWait();
                } else {
                    infoAlert.setTitle("Недостаточный уровень прав");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Ваша роль в системе не позволяет управлять выбранными данными.");
                    infoAlert.showAndWait();
                }
                break;
            case "menuSite":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                    siteStage();
                    siteStage.setTitle("Добавить площадку");
                    siteController.createData();
                    siteStage.showAndWait();
                } else {
                    infoAlert.setTitle("Недостаточный уровень прав");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Ваша роль в системе не позволяет управлять выбранными данными.");
                    infoAlert.showAndWait();
                }
                break;
            case "menuEmployee":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                    userStage();
                    userStage.setTitle("Добавить сотрудника");
                    userController.addData();
                    userStage.showAndWait();
                } else {
                    infoAlert.setTitle("Недостаточный уровень прав");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Ваша роль в системе не позволяет управлять выбранными данными.");
                    infoAlert.showAndWait();
                }
                break;
            case "menuContract":
                if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                    contractStage();
                    contractStage.setTitle("Добавить контракт");
                    contractController.addData();
                    contractStage.showAndWait();
                } else {
                    infoAlert.setTitle("Недостаточный уровень прав");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Ваша роль в системе не позволяет управлять выбранными данными.");
                    infoAlert.showAndWait();
                }
                break;
            case "menuRequest":
                requestStage();
                requestStage.setTitle("Добавить заявку");
                requestController.addData();
                requestStage.showAndWait();
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

        updateTableView();
    }

    public void actionMenuAbout(ActionEvent actionEvent) {
        infoAlert.setTitle("О программе");
        infoAlert.setHeaderText("АСУ ПД, 2018-2020 г.");
        infoAlert.setContentText("v 1.0.4");
        infoAlert.showAndWait();
    }

    private void sheetDetail() {
        if (sheetStage == null) {
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
            infoAlert.setTitle("Ошибка выбора");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Выберите строку справочника");
            infoAlert.showAndWait();
            return;
        }
    }

    private void timeSheetDetail() {
        if (timeSheetStage == null) {
            timeSheetStage = new Stage();
            timeSheetStage.setTitle("Табель учета рабочего времени");
            timeSheetStage.setScene(new Scene(fxmlTimeSheet));
            timeSheetStage.setMinHeight(700);
            timeSheetStage.setMinWidth(1300);
            timeSheetStage.initModality(Modality.WINDOW_MODAL);
            timeSheetStage.initOwner(mainStage);
        }
        if (!dataView.getSelectionModel().isEmpty()) {
            Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();

            timeSheetController.initTimeSheet(dataView.getSelectionModel().getSelectedItem().toString());
            timeSheetStage.setOnCloseRequest(arg0 -> timeSheetController.formClear());
            timeSheetStage.showAndWait();
            updateTableView();
            dataView.getSelectionModel().clearAndSelect(rowIndex);
        } else {
            infoAlert.setTitle("Ошибка выбора");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Выберите сотрудника");
            infoAlert.showAndWait();
        }
    }

    private void executorDetail() {
        if (executorStage == null) {
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
            infoAlert.setTitle("Ошибка выбора");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Выберите задачу");
            infoAlert.showAndWait();
        }
    }

    private void customerStage() {
        if (customerStage == null) {
            customerStage = new Stage();
            customerStage.setScene(new Scene(fxmlCustomer));
            customerStage.setMinHeight(100);
            customerStage.setMinWidth(200);
            customerStage.setResizable(false);
            customerStage.initModality(Modality.WINDOW_MODAL);
            customerStage.initOwner(mainStage);
        }
    }

    private void customerDetail() {
        customerStage();
        customerStage.setTitle("Изменить");
        customerController.setData(exemp);
        customerStage.showAndWait();
        updateTableView();
    }

    private void effectivityReportStage() {
        if (effectivityReportStage == null) {
            effectivityReportStage = new Stage();
            effectivityReportStage.setScene(new Scene(fxmlEffectivityReport));
            effectivityReportStage.setMinHeight(100);
            effectivityReportStage.setMinWidth(200);
            effectivityReportStage.setResizable(false);
            effectivityReportStage.initModality(Modality.WINDOW_MODAL);
            effectivityReportStage.initOwner(mainStage);
        }
    }

    private void setFileChooserStage() {
        if (fileChooserStage == null) {
            fileChooserStage = new Stage();
            fileChooserStage.setScene(new Scene(fileChooserView));
            fileChooserStage.setMinHeight(100);
            fileChooserStage.setMinWidth(100);
            fileChooserStage.setResizable(false);
            fileChooserStage.initModality(Modality.WINDOW_MODAL);
            fileChooserStage.initOwner(mainStage);
        }
    }

    private void setCalendarStage() {
        if (calendarStage == null) {
            calendarStage = new Stage();
            calendarStage.setScene(new Scene(calendarView));
            calendarStage.setMinHeight(100);
            calendarStage.setMinWidth(100);
            calendarStage.setResizable(false);
            calendarStage.initModality(Modality.WINDOW_MODAL);
            calendarStage.initOwner(mainStage);
        }
    }

    private void effectivityReportDetail() {
        effectivityReportStage();

        effectivityReportStage.setTitle("Отчет по эффективности");
        effectivityReportController.addData();
        effectivityReportStage.showAndWait();
    }

    private void siteStage() {
        if (siteStage == null) {
            siteStage = new Stage();
            siteStage.setScene(new Scene(fxmlSite));
            siteStage.setMinHeight(100);
            siteStage.setMinWidth(200);
            siteStage.setResizable(false);
            siteStage.initModality(Modality.WINDOW_MODAL);
            siteStage.initOwner(mainStage);
        }
    }

    private void siteDetail() {
        siteStage();
        siteStage.setTitle("Изменить");
        siteController.setData(exemp);
        siteStage.showAndWait();
        updateTableView();
    }

    private void userStage() {
        if (userStage == null) {
            userStage = new Stage();
            userStage.setScene(new Scene(fxmlUser));
            userStage.setMinHeight(100);
            userStage.setMinWidth(200);
            userStage.setResizable(false);
            userStage.initModality(Modality.WINDOW_MODAL);
            userStage.initOwner(mainStage);
        }
    }

    private void userDetail() {
        if (!dataView.getSelectionModel().isEmpty()) {
            userStage();
            userStage.setTitle("Редактировать сотрудника");
            userController.setData(exemp);
            userStage.showAndWait();
            updateTableView();
        } else {
            infoAlert.setTitle("Ошибка выбора");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Выберите сотрудника");
            infoAlert.showAndWait();
        }
    }

    private void taskStage() {
        if (taskStage == null) {
            taskStage = new Stage();
            taskStage.setScene(new Scene(fxmlTask));
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
        taskController.setData(exemp);
        taskStage.showAndWait();
        updateTableView();

        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void projectStage() {
        if (projectStage == null) {
            projectStage = new Stage();
            projectStage.setScene(new Scene(fxmlProject));
            projectStage.setMinHeight(100);
            projectStage.setMinWidth(200);
            projectStage.setResizable(false);
            projectStage.initModality(Modality.WINDOW_MODAL);
            projectStage.initOwner(mainStage);
        }
    }

    private void projectDetail() {
        Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();

        projectStage();
        projectStage.setTitle("Изменить");
        projectController.setData(exemp);
        projectStage.showAndWait();
        updateTableView();

        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void contractStage() {
        if (contractStage == null) {
            contractStage = new Stage();
            contractStage.setScene(new Scene(fxmlContract));
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
        contractController.setData(exemp);
        contractStage.showAndWait();
        updateTableView();

        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void requestStage() {
        if (requestStage == null) {
            requestStage = new Stage();
            requestStage.setScene(new Scene(fxmlRequest));
            requestStage.setMinHeight(100);
            requestStage.setMinWidth(200);
            requestStage.setResizable(false);
            requestStage.initModality(Modality.WINDOW_MODAL);
            requestStage.initOwner(mainStage);
        }
    }

    private void requestDetail() {
        Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();

        requestStage();
        requestStage.setTitle("Изменить");
        requestController.setData(exemp);
        requestStage.showAndWait();
        updateTableView();

        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void projectAccessDetail() {
        if (projectAccessStage == null) {
            projectAccessStage = new Stage();
            projectAccessStage.setTitle("Доступ к проекту");
            projectAccessStage.setScene(new Scene(fxmlProjectAccess));
            projectAccessStage.setMinHeight(400);
            projectAccessStage.setMinWidth(800);
            projectAccessStage.initModality(Modality.WINDOW_MODAL);
            projectAccessStage.initOwner(mainStage);
        }
        if (!dataView.getSelectionModel().isEmpty()) {
            projectAccessController.addData(exemp);
            projectAccessStage.setOnCloseRequest(arg0 -> projectAccessController.formClear());
            projectAccessStage.showAndWait();
        } else {
            infoAlert.setTitle("Ошибка выбора");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Выберите проект");
            infoAlert.showAndWait();
        }
    }

    private void userAccessDetail() {
        if (userAccessStage == null) {
            userAccessStage = new Stage();
            userAccessStage.setTitle("Подчиненность сотрудников");
            userAccessStage.setScene(new Scene(fxmlUserAccess));
            userAccessStage.setMinHeight(400);
            userAccessStage.setMinWidth(800);
            userAccessStage.initModality(Modality.WINDOW_MODAL);
            userAccessStage.initOwner(mainStage);
        }
        if (!dataView.getSelectionModel().isEmpty()) {
            userAccessController.addData(exemp);
            userAccessStage.setOnCloseRequest(arg0 -> projectAccessController.formClear());
            userAccessStage.showAndWait();
        } else {
            infoAlert.setTitle("Ошибка выбора");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Выберите сотрудника");
            infoAlert.showAndWait();
        }
    }

    private void accessToolStage() {
        if (accessToolStage == null) {
            accessToolStage = new Stage();
            accessToolStage.setScene(new Scene(fxmlAccessTool));
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
            requestLinkStage.setScene(new Scene(fxmlRequestLink));
            requestLinkStage.setMinHeight(768);
            requestLinkStage.setMinWidth(1024);
            requestLinkStage.initModality(Modality.WINDOW_MODAL);
            requestLinkStage.initOwner(mainStage);
        }
        if (!dataView.getSelectionModel().isEmpty()) {
            requestLinkController.addData(exemp);
            requestLinkStage.setOnCloseRequest(arg0 -> requestLinkController.formClear());
            requestLinkStage.showAndWait();
        } else {
            infoAlert.setTitle("Ошибка выбора");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Выберите заявку");
            infoAlert.showAndWait();
        }
    }

    private void passChangeStage() {
        if (passChangeStage == null) {
            passChangeStage = new Stage();
            passChangeStage.setScene(new Scene(fxmlPassChange));
            passChangeStage.setMinHeight(100);
            passChangeStage.setMinWidth(300);
            passChangeStage.initModality(Modality.WINDOW_MODAL);
            passChangeStage.initOwner(mainStage);
        }
    }

    private void requestViewStage() {
        if (requestViewStage == null) {
            requestViewStage = new Stage();
            requestViewStage.setScene(new Scene(fxmlRequestView));
            requestViewStage.setMinHeight(700);
            requestViewStage.setMinWidth(1300);
            requestViewStage.initModality(Modality.WINDOW_MODAL);
            requestViewStage.initOwner(mainStage);
        }
    }

    private void requestView() {
        requestViewStage();

        Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();

        requestViewStage.setTitle("Перспектива 'Заявка'");
        requestViewController.initRequestView(exemp);

        requestViewStage.show();
        requestViewController.setScrollBarBinding();
        updateTableView();

        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void taskViewStage() {
        if (taskViewStage == null) {
            taskViewStage = new Stage();
            taskViewStage.setScene(new Scene(fxmlTaskView));
            taskViewStage.setMinHeight(700);
            taskViewStage.setMinWidth(1300);
            taskViewStage.initModality(Modality.WINDOW_MODAL);
            taskViewStage.initOwner(mainStage);
        }
    }

    private void taskView() {
        Integer rowIndex = dataView.getSelectionModel().getSelectedIndex();

        taskViewStage();

        taskViewStage.setTitle("Перспектива 'Задача'");
        taskViewController.initTaskView(workNumber);

        taskViewStage.show();
        updateTableView();

        dataView.getSelectionModel().clearAndSelect(rowIndex);
    }

    private void updateTableView() {
        prevNode = "";
        tableShowContent(treeView.getSelectionModel().getSelectedItem().getValue());

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

    private void tableShowContent(String nodeName) {

        if (nodeName != prevNode) {

            if (!nodeName.equals(who)) {
                prevNode = nodeName;

                /*dataView.getContextMenu().hide();*/

                if (nodeName.equalsIgnoreCase(root)) {
                    sql = "";
                }

                // Проверка выделенного узла
                switch (nodeName) {
                    case "Менеджмент":
                        sql = "";
                        break;
                    case "Мои проекты":
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                            sql = "SELECT p.project_id, cr.customer_name, c.contract_number, p.project_name FROM public.project p " +
                                    "join public.contract_project cp on cp.project_id = p.project_id " +
                                    "join public.contract c on c.contract_id = cp.contract_id " +
                                    "join public.customer cr on cr.customer_id = p.customer_id " +
                                    "ORDER BY cr.customer_name, c.contract_number, p.project_name";
                        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
                            sql = "SELECT p.project_id, cr.customer_name, c.contract_number, p.project_name FROM public.project p " +
                                    "join public.contract_project cp on cp.project_id = p.project_id " +
                                    "join public.contract c on c.contract_id = cp.contract_id " +
                                    "join public.customer cr on cr.customer_id = p.customer_id " +
                                    "join public.project_manager pm on pm.project_id = p.project_id " +
                                    "join public.user u on u.user_id = pm.user_id " +
                                    "WHERE u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who + "') " +
                                    "ORDER BY cr.customer_name, c.contract_number, p.project_name";
                        } else {
                            sql = "";
                        }
                        break;
                    case "Мои задачи":
                        if (role.equalsIgnoreCase("ауп") || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("super_user")) {
                            sql =
                                    "SELECT t.task_id, cr.customer_name, c.contract_number, p.project_name, r.request_number, t.task_number, t.task_name, t.task_income_date, t.task_pa_intensity, t.task_tz_intensity, tu.task_uom_name, t.task_unit_plan, t.task_unit_fact, t.task_out, s.status_name FROM public.task t " +
                                            "join public.project p on p.project_id = t.project_id " +
                                            "left join public.request r on r.request_id = t.request_id " +
                                            "join public.customer cr on cr.customer_id = p.customer_id " +
                                            "join public.contract_project cp on cp.project_id = p.project_id " +
                                            "join public.contract c on c.contract_id = cp.contract_id " +
                                            "join public.status s on s.status_id = t.status_id " +
                                            "left join public.task_uom tu on tu.task_uom_id = t.task_uom_id " +
                                            "ORDER BY cr.customer_name, c.contract_number, p.project_name, t.task_number";
                        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
                            sql =
                                    "SELECT t.task_id, cr.customer_name, c.contract_number, p.project_name, r.request_number, t.task_number, t.task_name, t.task_income_date, t.task_pa_intensity, t.task_tz_intensity, tu.task_uom_name, t.task_unit_plan, t.task_unit_fact, t.task_out, s.status_name FROM public.task t " +
                                            "join public.project p on p.project_id = t.project_id " +
                                            "left join public.request r on r.request_id = t.request_id " +
                                            "join public.customer cr on cr.customer_id = p.customer_id " +
                                            "join public.contract_project cp on cp.project_id = p.project_id " +
                                            "join public.contract c on c.contract_id = cp.contract_id " +
                                            "join public.status s on s.status_id = t.status_id " +
                                            "left join public.project_manager pm on pm.project_id = p.project_id " +
                                            "left join public.user u on u.user_id = pm.user_id " +
                                            "left join public.task_executor te on te.task_id = t.task_id " +
                                            "left join public.user ute on ute.user_id = te.user_id " +
                                            "left join public.task_uom tu on tu.task_uom_id = t.task_uom_id " +
                                            "WHERE u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who +
                                            "') OR ute.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who +
                                            "') OR ute.user_id IN (SELECT us_su.user_sub_id FROM public.user_subordination us_su join public.user usr on usr.user_id = us_su.user_id WHERE usr.user_fullname = '" +
                                            who + "') " +
                                            "GROUP BY t.task_id, cr.customer_name, c.contract_number, p.project_name, r.request_number, t.task_number, t.task_name, t.task_income_date, t.task_pa_intensity, t.task_tz_intensity, tu.task_uom_name, t.task_unit_plan, t.task_unit_fact, t.task_out, s.status_name " +
                                            "ORDER BY cr.customer_name, c.contract_number, p.project_name, t.task_number";
                        } else {
                            sql =
                                    "SELECT t.task_id, cr.customer_name, t.task_number, t.task_name, t.task_pa_intensity, tu.task_uom_name, t.task_unit_plan, t.task_income_date, s.status_name FROM public.task t " +
                                            "join public.project p on p.project_id = t.project_id " +
                                            "left join public.request r on r.request_id = t.request_id " +
                                            "join public.customer cr on cr.customer_id = p.customer_id " +
                                            "join public.contract_project cp on cp.project_id = p.project_id " +
                                            "join public.contract c on c.contract_id = cp.contract_id " +
                                            "join public.status s on s.status_id = t.status_id " +
                                            "join public.task_executor te on te.task_id = t.task_id " +
                                            "join public.user u on u.user_id = te.user_id " +
                                            "left join public.task_uom tu on tu.task_uom_id = t.task_uom_id " +
                                            "WHERE u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who + "') " +
                                            "ORDER BY cr.customer_name, c.contract_number, p.project_name, t.task_number";
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
                            sql = "";
                        } else {
                            sql = "SELECT * FROM public.customer ORDER BY customer_name";
                        }
                        break;
                    case "Площадки":
                        if (role.equalsIgnoreCase("Сотрудник")) {
                            sql = "";
                        } else {
                            sql = "SELECT * FROM public.site ORDER BY site_id";
                        }
                        break;
                    case "Контракты":
                        if (role.equalsIgnoreCase("Super_user") || role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("АУП")) {
                            sql = "SELECT ct.contract_id, cr.customer_name, ct.contract_number, ct.contract_name FROM public.contract ct " +
                                    "join public.customer cr on cr.customer_id = ct.customer_id " +
                                    "ORDER BY cr.customer_name, ct.contract_number";
                        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
                            sql = "SELECT ct.contract_id, cr.customer_name, ct.contract_number, ct.contract_name FROM public.contract ct " +
                                    "join public.customer cr on cr.customer_id = ct.customer_id " +
                                    "join public.contract_project cp on cp.contract_id = ct.contract_id " +
                                    "join public.project p on p.project_id = cp.project_id " +
                                    "join public.project_manager pm on pm.project_id = p.project_id " +
                                    "join public.user u on u.user_id = pm.user_id " +
                                    "WHERE u.user_fullname = '" + who + "' " +
                                    "GROUP BY ct.contract_id, cr.customer_name, ct.contract_number, ct.contract_name " +
                                    "ORDER BY cr.customer_name, ct.contract_number";
                        } else {
                            sql = "";
                        }
                        break;
                    case "Заявки":
                        if (role.equalsIgnoreCase("Super_user") || role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("АУП")) {
                            sql = "SELECT r.request_id, cr.customer_name, ct.contract_number, r.request_number, r.request_description, " +
                                    "(CASE WHEN min(s.status_hierarchy) = 1 THEN 'в ожидании' " +
                                    "WHEN min(s.status_hierarchy) = 2 THEN 'в работе' " +
                                    "WHEN min(s.status_hierarchy) = 3 THEN 'выполнено' " +
                                    "WHEN min(s.status_hierarchy) = 4 THEN 'проверено' " +
                                    "WHEN min(s.status_hierarchy) = 5 THEN 'утверждено' " +
                                    "ELSE 'задачи не назначены' " +
                                    "END) task_state, " +
                                    "(CASE WHEN ra.mp_create = 'true' THEN 'Создано' " +
                                    "ELSE '-' " +
                                    "END) МП, " +
                                    "(CASE WHEN ra.aup_approve = 'true' THEN 'Согласовано' " +
                                    "ELSE '-' " +
                                    "END) АУП, " +
                                    "(CASE WHEN ra.customer_approve = 'true' THEN 'Утверждено' " +
                                    "ELSE '-' " +
                                    "END) Заказчик " +
                                    "FROM public.request r " +
                                    "join public.contract ct on ct.contract_id = r.contract_id " +
                                    "join public.customer cr on cr.customer_id = ct.customer_id " +
                                    "left join public.task t on t.request_id = r.request_id " +
                                    "left join public.status s on s.status_id = t.status_id " +
                                    "left join public.request_approve ra on ra.request_id = r.request_id " +
                                    "GROUP BY r.request_id, cr.customer_name, ct.contract_number, r.request_number, r.request_description, ra.mp_create, ra.aup_approve, ra.customer_approve " +
                                    "ORDER BY cr.customer_name, ct.contract_number, r.request_number";
                        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
                            sql = "SELECT r.request_id, cr.customer_name, ct.contract_number, r.request_number, r.request_description, " +
                                    "(CASE WHEN min(s.status_hierarchy) = 1 THEN 'в ожидании' " +
                                    "WHEN min(s.status_hierarchy) = 2 THEN 'в работе' " +
                                    "WHEN min(s.status_hierarchy) = 3 THEN 'выполнено' " +
                                    "WHEN min(s.status_hierarchy) = 4 THEN 'проверено' " +
                                    "WHEN min(s.status_hierarchy) = 5 THEN 'утверждено' " +
                                    "ELSE 'задачи не назначены' " +
                                    "END) task_state, " +
                                    "(CASE WHEN ra.mp_create = 'true' THEN 'Создано' " +
                                    "ELSE '-' " +
                                    "END) МП, " +
                                    "(CASE WHEN ra.aup_approve = 'true' THEN 'Согласовано' " +
                                    "ELSE '-' " +
                                    "END) АУП, " +
                                    "(CASE WHEN ra.customer_approve = 'true' THEN 'Утверждено' " +
                                    "ELSE '-' " +
                                    "END) Заказчик " +
                                    "FROM public.request r " +
                                    "join public.contract ct on ct.contract_id = r.contract_id " +
                                    "join public.customer cr on cr.customer_id = ct.customer_id " +
                                    "left join public.task t on t.request_id = r.request_id " +
                                    "left join public.status s on s.status_id = t.status_id " +
                                    "left join public.request_approve ra on ra.request_id = r.request_id " +
                                    "join public.contract_project cp on cp.contract_id = ct.contract_id " +
                                    "join public.project p on p.project_id = cp.project_id " +
                                    "join public.project_manager pm on pm.project_id = p.project_id " +
                                    "join public.user u on u.user_id = pm.user_id " +
                                    "WHERE u.user_fullname = '" + who + "' " +
                                    "GROUP BY r.request_id, cr.customer_name, ct.contract_number, r.request_number, r.request_description, ra.mp_create, ra.aup_approve, ra.customer_approve " +
                                    "ORDER BY cr.customer_name, ct.contract_number, r.request_number";
                        } else {
                            sql = "";
                        }
                        break;
                    case "Компания":
                        sql = "";
                        break;
                    case "Сотрудники":
                        if (role.equalsIgnoreCase("Super_user") || role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("АУП")) {
                            sql =
                                    "SELECT u.user_id_number, u.user_fullname, u.user_tel, u.user_adress, u.user_email, s.site_name, ur.user_role_name, ua.user_activity_name FROM public.user u " +
                                            "join public.user_info ui on ui.user_info_id = u.user_info_id " +
                                            "join public.user_role ur on ur.user_role_id = ui.user_role_id " +
                                            "left join public.site s on s.site_id = u.site_id " +
                                            "left join public.user_activity ua on ua.user_activity_id = u.user_activity_id " +
                                            "WHERE u.user_fullname != 'super_user' " +
                                            "ORDER BY u.user_fullname";
                        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
                            sql =
                                    "SELECT u.user_id_number, u.user_fullname, u.user_tel, u.user_adress, u.user_email, s.site_name, ur.user_role_name, ua.user_activity_name FROM public.user u " +
                                            "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                                            "left join public.user_activity ua on ua.user_activity_id = u.user_activity_id " +
                                            "join public.user_info ui on ui.user_info_id = u.user_info_id " +
                                            "join public.user_role ur on ur.user_role_id = ui.user_role_id " +
                                            "left join public.site s on s.site_id = u.site_id " +
                                            "WHERE (us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who +
                                            "') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who + "')) " +
                                            "GROUP BY u.user_id_number, u.user_fullname, u.user_tel, u.user_adress, u.user_email, ur.user_role_name, s.site_name, ua.user_activity_name " +
                                            "ORDER BY u.user_fullname";
                        } else if (role.equalsIgnoreCase("Начальник отдела") || role.equalsIgnoreCase("Ведущий специалист")) {
                            sql =
                                    "SELECT u.user_id_number, u.user_fullname, u.user_tel, u.user_adress, u.user_email, s.site_name, ur.user_role_name, ua.user_activity_name FROM public.user u " +
                                            "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                                            "join public.user_info ui on ui.user_info_id = u.user_info_id " +
                                            "join public.user_role ur on ur.user_role_id = ui.user_role_id " +
                                            "left join public.site s on s.site_id = u.site_id " +
                                            "left join public.user_activity ua on ua.user_activity_id = u.user_activity_id " +
                                            "WHERE us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who +
                                            "') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who + "') " +
                                            "GROUP BY u.user_id_number, u.user_fullname, u.user_tel, u.user_adress, u.user_email, ur.user_role_name, s.site_name, ua.user_activity_name " +
                                            "ORDER BY u.user_fullname";
                        } else {
                            sql =
                                    "SELECT u.user_id_number, u.user_fullname, u.user_tel, u.user_adress, u.user_email, s.site_name, ur.user_role_name, ua.user_activity_name FROM public.user u " +
                                            "join public.user_info ui on ui.user_info_id = u.user_info_id " +
                                            "join public.user_role ur on ur.user_role_id = ui.user_role_id " +
                                            "left join public.site s on s.site_id = u.site_id " +
                                            "left join public.user_activity ua on ua.user_activity_id = u.user_activity_id " +
                                            "WHERE u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who + "') " +
                                            "ORDER BY u.user_fullname";
                        }
                        break;
                    case "Табель":
                        if (role.equalsIgnoreCase("Super_user") || role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("АУП")) {
                            sql = "SELECT u.user_id_number, u.user_fullname, s.site_name, ur.user_role_name FROM public.user u " +
                                    "join public.user_info ui on ui.user_info_id = u.user_info_id " +
                                    "join public.user_role ur on ur.user_role_id = ui.user_role_id " +
                                    "left join public.site s on s.site_id = u.site_id " +
                                    "WHERE user_fullname != 'super_user' " +
                                    "ORDER BY user_fullname";
                        } else if (role.equalsIgnoreCase("Сотрудник")) {
                            sql = "SELECT u.user_id_number, u.user_fullname, s.site_name, ur.user_role_name FROM public.user u " +
                                    "left join public.user_activity ua on ua.user_activity_id = u.user_activity_id " +
                                    "join public.user_info ui on ui.user_info_id = u.user_info_id " +
                                    "join public.user_role ur on ur.user_role_id = ui.user_role_id " +
                                    "left join public.site s on s.site_id = u.site_id " +
                                    "WHERE u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who + "') " +
                                    "ORDER BY u.user_fullname";
                        } else {
                            sql = "SELECT u.user_id_number, u.user_fullname, s.site_name, ur.user_role_name FROM public.user u " +
                                    "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                                    "left join public.user_activity ua on ua.user_activity_id = u.user_activity_id " +
                                    "join public.user_info ui on ui.user_info_id = u.user_info_id " +
                                    "join public.user_role ur on ur.user_role_id = ui.user_role_id " +
                                    "left join public.site s on s.site_id = u.site_id " +
                                    "WHERE (us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who +
                                    "') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who +
                                    "')) AND (ua.user_activity_name != 'Уволен' OR ua.user_activity_name IS NULL) " +
                                    "GROUP BY u.user_id_number, u.user_fullname, s.site_name, ur.user_role_name " +
                                    "ORDER BY u.user_fullname";
                        }
                        break;
                    case "Справочники":
                        sql = "";
                        break;
                }
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
            sql =
                    "SELECT t.task_id, cr.customer_name, c.contract_number, p.project_name, r.request_number, t.task_number, t.task_name, t.task_income_date, t.task_pa_intensity, t.task_tz_intensity, tu.task_uom_name, t.task_unit_plan, t.task_unit_fact, t.task_out, s.status_name FROM public.task t " +
                            "join public.project p on p.project_id = t.project_id " +
                            "left join public.request r on r.request_id = t.request_id " +
                            "join public.customer cr on cr.customer_id = p.customer_id " +
                            "join public.contract_project cp on cp.project_id = p.project_id " +
                            "join public.contract c on c.contract_id = cp.contract_id " +
                            "join public.status s on s.status_id = t.status_id " +
                            "join public.status_type st on st.status_type_id = s.status_type " +
                            "left join public.task_uom tu on tu.task_uom_id = t.task_uom_id " +
                            "WHERE s.status_name = '" + status + "' " +
                            "ORDER BY cr.customer_name, c.contract_number, p.project_name, t.task_number";
        } else if (role.equalsIgnoreCase("Менеджер проекта")) {
            sql =
                    "SELECT t.task_id, cr.customer_name, c.contract_number, p.project_name, r.request_number, t.task_number, t.task_name, t.task_income_date, t.task_pa_intensity, t.task_tz_intensity, tu.task_uom_name, t.task_unit_plan, t.task_unit_fact, t.task_out, s.status_name FROM public.task t " +
                            "join public.project p on p.project_id = t.project_id " +
                            "left join public.request r on r.request_id = t.request_id " +
                            "join public.customer cr on cr.customer_id = p.customer_id " +
                            "join public.contract_project cp on cp.project_id = p.project_id " +
                            "join public.contract c on c.contract_id = cp.contract_id " +
                            "join public.status s on s.status_id = t.status_id " +
                            "left join public.project_manager pm on pm.project_id = p.project_id " +
                            "left join public.user u on u.user_id = pm.user_id " +
                            "left join public.task_executor te on te.task_id = t.task_id " +
                            "left join public.user ute on ute.user_id = te.user_id " +
                            "left join public.task_uom tu on tu.task_uom_id = t.task_uom_id " +
                            "WHERE (u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who +
                            "') OR ute.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who +
                            "') OR ute.user_id IN (SELECT us_su.user_sub_id FROM public.user_subordination us_su join public.user usr on usr.user_id = us_su.user_id WHERE usr.user_fullname = '" +
                            who + "')) AND s.status_name = '" + status + "' " +
                            "GROUP BY t.task_id, cr.customer_name, c.contract_number, p.project_name, r.request_number, t.task_number, t.task_name, t.task_income_date, t.task_pa_intensity, t.task_tz_intensity, tu.task_uom_name, t.task_unit_plan, t.task_unit_fact, t.task_out, s.status_name " +
                            "ORDER BY cr.customer_name, c.contract_number, p.project_name, t.task_number";
        } else {
            sql =
                    "SELECT t.task_id, cr.customer_name, t.task_number, t.task_name, t.task_pa_intensity, tu.task_uom_name, t.task_unit_plan, t.task_income_date, s.status_name FROM public.task t " +
                            "join public.project p on p.project_id = t.project_id " +
                            "left join public.request r on r.request_id = t.request_id " +
                            "join public.customer cr on cr.customer_id = p.customer_id " +
                            "join public.contract_project cp on cp.project_id = p.project_id " +
                            "join public.contract c on c.contract_id = cp.contract_id " +
                            "join public.status s on s.status_id = t.status_id " +
                            "join public.status_type st on st.status_type_id = s.status_type " +
                            "join public.task_executor te on te.task_id = t.task_id " +
                            "join public.user u on u.user_id = te.user_id " +
                            "left join public.task_uom tu on tu.task_uom_id = t.task_uom_id " +
                            "WHERE u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + who + "') AND s.status_name = '" + status + "' " +
                            "ORDER BY cr.customer_name, c.contract_number, p.project_name, t.task_number";
        }
    }

    private void tableGenerator(String sql) {
        dataView.setEditable(true);
        dataView.getSelectionModel().clearSelection();
        dataView.getColumns().clear();
        dataView.setColumnResizePolicy(UNCONSTRAINED_RESIZE_POLICY);
        data.clear();
        dBconnection.openDB();
        dBconnection.query(sql);

        try {
            for (int i = 0; i < dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                final int j = i;
                tableColName = dBconnection.getRs().getMetaData().getColumnName(i + 1);
                generateColName(dBconnection.getRs().getMetaData().getColumnName(i + 1));
                TableColumn tableColumn = new TableColumn(tableColName);

                tableColumn.setCellValueFactory(
                        (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(
                                (String) param.getValue().get(j)));

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
                            || tableColName.equals("Полное наименование") || tableColName.equals("Номер заявки") || tableColName.equals("Описание заявки")) {

                    } else {
                        tableColumn.setStyle("-fx-alignment: CENTER;");
                    }
                }

                /*tableColumn.setOnEditCommit(((EventHandler<TableColumn.CellEditEvent<ObservableList, String>>) event -> {
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
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    row.add(dBconnection.getRs().getString(i));
                }
                data.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
            //Добавление данных в TableView
            dataView.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // генерация русскоязычных названий колонок таблицы
    private void generateColName(String bdColName) {
        switch (bdColName) {
            case "user_id_number":
                tableColName = "Таб. номер";
                break;
            case "task_name":
                tableColName = "Наименование задачи";
                break;
            case "task_id":
                tableColName = "ID задачи";
                break;
            case "customer_name":
                tableColName = "Заказчик";
                break;
            case "contract_number":
                tableColName = "Номер контракта";
                break;
            case "project_name":
                tableColName = "Проект";
                break;
            case "request_number":
                tableColName = "Номер заявки";
                break;
            case "task_number":
                tableColName = "Номер задачи";
                break;
            case "task_income_date":
                tableColName = "Дата получения";
                break;
            case "task_pa_intensity":
                tableColName = "Тр-ть план. внутр.";
                break;
            case "task_tz_intensity":
                tableColName = "Тр-ть план. внешн.";
                break;
            case "task_out":
                tableColName = "Аутсорс";
                break;
            case "status_name":
                tableColName = "Статус";
                break;
            case "project_id":
                tableColName = "ID проекта";
                break;
            case "contract_id":
                tableColName = "ID контракта";
                break;
            case "contract_name":
                tableColName = "Название контракта";
                break;
            case "user_fullname":
                tableColName = "ФИО";
                break;
            case "user_tel":
                tableColName = "Телефон";
                break;
            case "user_adress":
                tableColName = "Адрес";
                break;
            case "user_email":
                tableColName = "Email";
                break;
            case "customer_id":
                tableColName = "ID заказчика";
                break;
            case "customer_full_name":
                tableColName = "Полное наименование";
                break;
            case "user_role_name":
                tableColName = "Роль";
                break;
            case "site_name":
                tableColName = "Площадка";
                break;
            case "user_activity_name":
                tableColName = "Статус пользователя";
                break;
            case "request_id":
                tableColName = "ID заявки";
                break;
            case "request_description":
                tableColName = "Описание заявки";
                break;
            case "task_unit_plan":
                tableColName = "Ед. план";
                break;
            case "task_unit_fact":
                tableColName = "Ед. факт";
                break;
            case "task_uom_name":
                tableColName = "Ед. изм. задачи";
                break;
            case "task_state":
                tableColName = "Состояние задач";
                break;
            case "site_id":
                tableColName = "ID площадки";
                break;
            case "site_code":
                tableColName = "Код";
                break;
        }
    }

    public void actionSearch() {
        backupList.clear();
        data.clear();
        if (!sql.equals("")) {
            tableGenerator(sql);
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
                        systemLabel.getText() + " / " + customerFilterBox.getCheckModel().getCheckedItems().toString().replace("[", "").replace("]", ""));
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
                        systemLabel.getText() + " / " + contractFilterBox.getCheckModel().getCheckedItems().toString().replace("[", "").replace("]", ""));
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
                        systemLabel.getText() + " / " + projectFilterBox.getCheckModel().getCheckedItems().toString().replace("[", "").replace("]", ""));
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
        dBconnection.openDB();
        try {
            dBconnection.getStmt().executeUpdate("DELETE FROM public.task WHERE task_number = '" + itemID + "'");
            dBconnection.getC().commit();
            infoAlert.setTitle("Действие выполнено");
            infoAlert.setHeaderText("Удаление задачи");
            infoAlert.setContentText("Выбранная задача была удалена из системы.");
            infoAlert.showAndWait();
            updateTableView();
        } catch (SQLException throwables) {
            warnAlert.setTitle("Ошибка удаления данных");
            warnAlert.setHeaderText("В системе присутствуют связанные данные.");
            warnAlert.setContentText("Возможно на задачу назначены исполнители или списаны часы.");
            warnAlert.showAndWait();
            dBconnection.closeDB();
            return;
        }
        dBconnection.closeDB();
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

    private ObservableList filteredFill(ObservableList list, String k) {
        try {
            dBconnection.openDB();
            dBconnection.query(k);
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= dBconnection.getRs().getMetaData().getColumnCount(); i++) {
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                list.add(row);
            }
            dBconnection.queryClose();
            dBconnection.closeDB();
        } catch (Exception e) {
            errorAlert.setTitle("Ошибка data");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
        return list;
    }
}