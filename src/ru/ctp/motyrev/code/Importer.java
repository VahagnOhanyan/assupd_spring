package ru.ctp.motyrev.code;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import ru.ctp.motyrev.interfaces.impls.CollectionWorksBook;
import ru.ctp.motyrev.objects.Works;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Importer {

    @FXML
    public ProgressBar pgBar;

    @FXML
    public Label txtLabel;

    private Boolean hasCustomer = false;
    private Boolean hasContract = false;
    private Boolean hasRequest = false;
    private Boolean hasProject = false;
    private Boolean hasTask = false;

    private Boolean userTaskValidated = false;
    private Boolean taskValidated = false;
    private Boolean customerValidated = false;
    private String validStatus = "";
    private String sheetStatus = "";
    private String errors = "";

    private String incomeDate ="";

    private File file;

    private PrintWriter writer;

    ObservableList<ObservableList> customerArray = FXCollections.observableArrayList();
    ObservableList<ObservableList> contractArray = FXCollections.observableArrayList();
    ObservableList<ObservableList> requestArray = FXCollections.observableArrayList();
    ObservableList<ObservableList> projectArray = FXCollections.observableArrayList();
    ObservableList<ObservableList> taskArray = FXCollections.observableArrayList();
    ObservableList<ObservableList> stageArray = FXCollections.observableArrayList();

    private CollectionWorksBook worksBook = new CollectionWorksBook();

    private Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    DBconnection dBconnection = new DBconnection();
    Validator validator = new Validator();

    SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy'_'HH-mm-ss");

    public void importData (File file) {

        txtLabel.setText("Производится импорт, пожалуйста подождите");

        worksBook.dictDataParsingCollection(file);

        fillComparisonLists();

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                double size = worksBook.getWorkslist().size();
                double counter = 0;

                for (Works work : worksBook.getWorkslist()) {

                    if (work.getRequest().equals("N/A")) {
                        work.setRequest("N");
                    }

                    for (int i = 0; i < customerArray.size(); i++) {
                        if (customerArray.get(i).toString().contains(work.getCustomer())) {
                            hasCustomer = true;
                        }
                    }

                    for (int i = 0; i < contractArray.size(); i++) {
                        if (contractArray.get(i).toString().contains(work.getContract())) {
                            hasContract = true;
                        }
                    }

                    for (int i = 0; i < projectArray.size(); i++) {
                        if (projectArray.get(i).toString().contains(work.getContract()) && projectArray.get(i).toString().contains(work.getProject())) {
                            hasProject = true;
                        }
                    }

                    for (int i = 0; i < requestArray.size(); i++) {
                        if (requestArray.get(i).toString().contains(work.getContract()) && requestArray.get(i).toString().substring(requestArray.get(i).toString().lastIndexOf(" ") + 1, requestArray.get(i).toString().length() - 1).equals(work.getRequest())) {
                            hasRequest = true;
                        }
                    }

                    for (int i = 0; i < taskArray.size(); i++) {
                        if (taskArray.get(i).toString().contains(work.getDesignation())) {
                            hasTask = true;
                        }
                    }

                    dBconnection.openDB();
                    try {
                        if (!hasCustomer) {
                            dBconnection.getStmt().executeUpdate("INSERT INTO public.customer (customer_name) VALUES ('" + work.getCustomer() + "')");
                            dBconnection.getC().commit();
                        }

                        if (!hasContract) {
                            dBconnection.getStmt().executeUpdate("INSERT INTO public.contract (contract_number, customer_id) VALUES ('" + work.getContract() + "', " +
                                    "(SELECT customer_id FROM public.customer WHERE customer_name = '" + work.getCustomer() + "'))");
                            dBconnection.getC().commit();
                        }

                        if (!hasProject) {
                            dBconnection.getStmt().executeUpdate("INSERT INTO public.project (project_name, status_id, customer_id) VALUES ('" + work.getProject() + "', " +
                                    "(SELECT s.status_id FROM public.status s " +
                                    "join public.status_type st on st.status_type_id = s.status_type " +
                                    "WHERE s.status_name = 'in-work' AND st.status_type_name = 'projects'), " +
                                    "(SELECT customer_id FROM public.customer WHERE customer_name = '" + work.getCustomer() + "'))");
                            dBconnection.getStmt().executeUpdate("INSERT INTO public.contract_project (contract_id, project_id, status_id, active_from, active_to) VALUES " +
                                    "((SELECT contract_id FROM public.contract WHERE contract_number = '" + work.getContract() + "'), " +
                                    "(SELECT max(project_id) FROM public.project), " +
                                    "(SELECT s.status_id FROM public.status s " +
                                    "join public.status_type st on st.status_type_id = s.status_type " +
                                    "WHERE s.status_name = 'linked' AND st.status_type_name = 'links'), " +
                                    "current_timestamp, '2099-10-11 00:00:00')");
                            dBconnection.getC().commit();
                        }

                        if (!hasRequest) {
                            dBconnection.getStmt().executeUpdate("INSERT INTO public.request (request_number, contract_id) VALUES ('" + work.getRequest() + "', " +
                                    "(SELECT contract_id FROM public.contract WHERE contract_number = '" + work.getContract() + "'))");
                            dBconnection.getC().commit();
                        }

                        if (!hasTask) {
                            if (work.getIncomingDate().equals("")) {
                                incomeDate = "2016-01-01";
                            } else {
                                incomeDate = work.getIncomingDate();
                            }
                            dBconnection.getStmt().executeUpdate("INSERT INTO public.task (task_number, task_name, task_income_date, status_id, project_id, request_id, task_pa_intensity, task_tz_intensity, task_out) VALUES " +
                                    "('" + work.getDesignation() + "', '" + work.getWorkName() + "', '" + incomeDate + "', " +
                                    "(SELECT s.status_id FROM public.status s " +
                                    "join public.status_type st on st.status_type_id = s.status_type " +
                                    "WHERE s.status_name = '" + work.getStatus_id() + "' AND st.status_type_name = 'tasks'), " +
                                    "(SELECT p.project_id FROM public.project p " +
                                    "join public.contract_project cp on cp.project_id = p.project_id " +
                                    "join public.contract c on c.contract_id = cp.contract_id " +
                                    "WHERE p.project_name = '" + work.getProject() + "' AND c.contract_number = '" + work.getContract() + "'), " +
                                    "(SELECT r.request_id FROM public.request r " +
                                    "join public.contract c on c.contract_id = r.contract_id " +
                                    "WHERE r.request_number = '" + work.getRequest() + "' AND c.contract_number = '" + work.getContract() + "'), " +
                                    "'" + work.getPa_intensity() + "', '" + work.getTz_intensity() + "', '"+work.getOutsource()+"')");

                            for (int i = 0; i < stageArray.size(); i++) {
                                dBconnection.getStmt().executeUpdate("INSERT INTO public.stage (task_id, stage_type_id, stage_intensity, stage_overtime) VALUES " +
                                        "((SELECT max(task_id) FROM public.task), " +
                                        "'" + stageArray.get(i).toString().replace("[", "").replace("]", "") + "', " +
                                        "'0', '0')");
                            }

                            dBconnection.getC().commit();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                        counter += 1;
                        errors += "\n" + e.getMessage();
                    }
                    dBconnection.closeDB();

                    hasCustomer = false;
                    hasContract = false;
                    hasProject = false;
                    hasRequest = false;
                    hasTask = false;

                    incomeDate ="";

                    customerArray.clear();
                    contractArray.clear();
                    projectArray.clear();
                    requestArray.clear();
                    taskArray.clear();
                    stageArray.clear();

                    fillComparisonLists();
                    counter +=1;
                    updateProgress(counter,size);
                    Thread.sleep(20);
                }

                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        labelChange();
                        if (!errors.equals("")) {
                            errorAlert.setTitle("Ошибка data");
                            errorAlert.setContentText(errors);
                            errorAlert.showAndWait();
                            errors = "";
                        }
                    }
                });
                worksBook.getWorkslist().clear();
                return  0;
            }
        };

        pgBar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }

    private void labelChange() {
        txtLabel.setText("Импорт завершен! Окно импорта можно закрыть.");
    }


    private void fillComparisonLists () {
        dBconnection.openDB();
        try {
            dBconnection.query("SELECT customer_name FROM public.customer");
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                customerArray.add(row);
            }

            dBconnection.query("SELECT c.contract_number FROM public.contract c " +
                    "join public.customer cr on cr.customer_id = c.customer_id");
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                contractArray.add(row);
            }

            dBconnection.query("SELECT c.contract_number, p.project_name FROM public.project p " +
                    "join public.contract_project cp on cp.project_id = p.project_id " +
                    "join public.contract c on c.contract_id = cp.contract_id " +
                    "join public.status s on s.status_id = cp.status_id " +
                    "join public.status_type st on st.status_type_id = s.status_type " +
                    "WHERE s.status_name = 'linked' AND st.status_type_name = 'links' AND cp.active_from < current_timestamp AND cp.active_to > current_timestamp AND p.customer_id = c.customer_id");
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                projectArray.add(row);
            }

            dBconnection.query("SELECT c.contract_number, r.request_number FROM public.request r " +
                    "join public.contract c on c.contract_id = r.contract_id");
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                requestArray.add(row);
            }

            dBconnection.query("SELECT task_number FROM public.task t " +
                    "join public.project p on p.project_id = t.project_id " +
                    "left join public.request r on r.request_id = t.request_id ");
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                taskArray.add(row);
            }

            dBconnection.query("SELECT stage_type_id FROM public.stage_type ORDER BY stage_type_id");
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                stageArray.add(row);
            }

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        dBconnection.queryClose();
        dBconnection.closeDB();
    }

    public void processFilesFromFolder(File folder) {

        txtLabel.setText("Производится импорт, пожалуйста подождите");

        Task<Integer> task1 = new Task<Integer>() {
                    @Override
                    protected Integer call() throws Exception {

                        try {
                            writer = new PrintWriter(folder.getAbsolutePath() + "\\" + sdf2.format(new Date()) + "_system_parsing.log", "UTF-8");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        fillComparisonLists();



                        File[] folderEntries = folder.listFiles();

                        double size = folderEntries.length;
                        double counter = 0;

                        writer.println("[" + new Date(System.currentTimeMillis()) + "] [Процедура считывания данных начата.]");
                        for (File entry : folderEntries) {
                            try {
                                if (entry.isDirectory()) {
                                    counter +=1;
                                    updateProgress(counter,size);
                                    Thread.sleep(20);
                                    continue;
                                } else if (entry.getName().substring(entry.getName().lastIndexOf("."), entry.getName().length()).equalsIgnoreCase(".xls")){
                                    file = entry;
                                    worksBook.sheetParsingCollection(file);
                                    writer.println("[" + new Date(System.currentTimeMillis()) + "] [данные файла '"+ entry.getName()+"' считаны в память.]");
                                    dBconnection.openDB();

                                    dBconnection.getStmt().executeUpdate(
                                            "INSERT INTO public.sheet (sheet_name, sheet_works_count, status_id) VALUES " +
                                                    "('"+entry.getName()+"', '"+worksBook.getWorkslist().size()+"', (SELECT status_id FROM public.status s " +
                                                    "join public.status_type st on st.status_type_id = s.status_type " +
                                                    "WHERE s.status_name = 'new' AND st.status_type_name = 'incoming_sheets'))");

                                    for (Works work : worksBook.getWorkslist()) {

                                        if (work.getDesignation().equalsIgnoreCase("Админ. деятельность") || work.getDesignation().equalsIgnoreCase("Отпуск")
                                                || work.getDesignation().equalsIgnoreCase("Больничный")
                                                || work.getDesignation().equalsIgnoreCase("Обучение") || work.getDesignation().equalsIgnoreCase("IDLE")) {
                                            userTaskValidated = true;
                                        } else {
                                            for (int i = 0; i <= taskArray.size() - 1; i++) {
                                                if (taskArray.get(i).toString().substring(1, taskArray.get(i).toString().length() - 1).equals(work.getDesignation())) {
                                                    taskValidated = true;
                                                    break;
                                                }
                                            }
                                        }

                                        if (taskValidated) {
                                            for (int i = 0; i <= customerArray.size() - 1; i++) {
                                                if (customerArray.get(i).toString().substring(1, customerArray.get(i).toString().length() - 1).equals(work.getCustomer())) {
                                                    customerValidated = true;
                                                    break;
                                                }
                                            }
                                        }

                                        if (userTaskValidated || ((taskValidated) & (customerValidated))) {
                                            validStatus = "validated";
                                        } else {
                                            validStatus = "invalidated";
                                            sheetStatus = "incorrect";
                                        }

                                        dBconnection.getStmt().executeUpdate(
                                                "INSERT INTO public.parsing_sheet " +
                                                        "(user_id_number, year, month, work_number, stage, work_name, customer, contract, request, " +
                                                        "intensity, overtime, work_start_date, work_end_date_pa, work_end_date, note, status_id, sheet_id) " +
                                                        "VALUES " +
                                                        "('" + (file.getName().substring(file.getName().lastIndexOf("_") + 1, file.getName().indexOf(" "))) + "', " +
                                                        "'" + (file.getName().substring(0, file.getName().indexOf("-"))) + "', " +
                                                        "'" + (file.getName().substring(file.getName().indexOf("-") + 1, file.getName().indexOf("_"))) + "', '" + work.getDesignation().replace("Т","T") + "', '" + work.getStage().replace(" ","") + "', '" + work.getWorkName() + "', '" + work.getCustomer() + "', '" + work.getContract() + "', " +
                                                        "'" + work.getRequest() + "', '" + work.getIntensity() + "', '" + work.getOvertime() + "', '" + work.getWorkStartDate() + "', '" + work.getWorkEndDatePA() + "', '" + work.getWorkEndDate() + "', " +
                                                        "'" + work.getNote() + "', (SELECT status_id FROM public.status s " +
                                                        "join public.status_type st on st.status_type_id = s.status_type " +
                                                        "WHERE s.status_name = '" + validStatus + "' AND st.status_type_name = 'incoming_works'), " +
                                                        "(SELECT sheet_id FROM public.sheet WHERE sheet_name = '"+entry.getName()+"'))");

                                        taskValidated = false;
                                        customerValidated = false;
                                        userTaskValidated = false;
                                    }
                                    if (sheetStatus.equals("incorrect")) {
                                        dBconnection.getStmt().executeUpdate("UPDATE public.sheet SET status_id = (SELECT status_id FROM public.status s " +
                                                "join public.status_type st on st.status_type_id = s.status_type " +
                                                "WHERE s.status_name = '" + sheetStatus + "' AND st.status_type_name = 'incoming_sheets') " +
                                                "WHERE sheet_id = (SELECT max(sheet_id) FROM public.sheet)");
                                    }
                                    dBconnection.getC().commit();
                                    writer.println("[" + new Date(System.currentTimeMillis()) + "] [данные файла '"+ entry.getName()+"' внесены в базу.]");
                                    worksBook.clear();
                                    dBconnection.closeDB();
                                } else {
                                    counter +=1;
                                    updateProgress(counter,size);
                                    Thread.sleep(20);
                                    continue;
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                writer.println("[" + new Date(System.currentTimeMillis()) + "] [ОШИБКА: ошибка ввода в базу данных файла '"+ entry.getName()+"'.]");
                                taskValidated = false;
                                customerValidated = false;
                                userTaskValidated = false;
                                worksBook.clear();
                                dBconnection.closeDB();
                                counter +=1;
                                updateProgress(counter,size);
                                Thread.sleep(20);
                                continue;
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                                writer.println("[" + new Date(System.currentTimeMillis()) + "] [ОШИБКА: Не удается идентифицировать параметры отчета. Ошибка в имени файла '"+ entry.getName()+"'.]");
                                taskValidated = false;
                                customerValidated = false;
                                userTaskValidated = false;
                                worksBook.clear();
                                dBconnection.closeDB();
                                counter +=1;
                                updateProgress(counter,size);
                                Thread.sleep(20);
                                continue;
                            }
                            taskArray.clear();
                            customerArray.clear();

                            counter +=1;
                            updateProgress(counter,size);
                            Thread.sleep(20);
                        }
                        writer.println("[" + new Date(System.currentTimeMillis()) + "] [Процедура считывания данных завершена.]");
                        writer.close();
                        validator.validate();

                        Platform.runLater(new Runnable() {

                            @Override
                            public void run() {
                                labelChange();
                            }
                        });

                        worksBook.getWorkslist().clear();
                        return  0;
                    }
        };

        pgBar.progressProperty().bind(task1.progressProperty());
        new Thread(task1).start();
    }
}
