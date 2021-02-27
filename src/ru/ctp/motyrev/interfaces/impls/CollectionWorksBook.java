package ru.ctp.motyrev.interfaces.impls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.ctp.motyrev.code.DBconnection;
import ru.ctp.motyrev.controllers.MainController;
import ru.ctp.motyrev.interfaces.WorksBook;
import ru.ctp.motyrev.objects.Works;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class CollectionWorksBook implements WorksBook, Serializable {
    private String id;
    private String user_id;
    private String year;
    private String month;
    private String num;
    private String customer;
    private String contract;
    private String request;
    private String designation;
    private String workName;
    private String intensity;
    private String overtime;
    private String sum_intensity;
    private String workStartDate;
    private String workEndDatePA;
    private String workEndDate;
    private String stage;
    private String note;
    private String project;
    private String incomingDate;
    private String sheet_id;
    private String status_id;
    private String pa_intensity;
    private String tz_intensity;
    private String uom;
    private String uom_plan;
    private String uom_fact;
    private String stage_recieve;
    private String stage_execute;
    private String stage_escort;
    private String stage_check;
    private String stage_approve;
    private String outsource;

    private ObservableList<Works> workslist = FXCollections.observableArrayList();

    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy'_'HH-mm-ss");

    private PrintWriter writer;

    DBconnection dBconnection = new DBconnection();


    //deprecated since 07.04.2018 in order to import modifying
    public void sheetParsingCollection(File file){
        try {
            writer = new PrintWriter(file.getAbsolutePath()+ sdf2.format(new Date()) + "_" + "parsing.log", "UTF-8");
            writer.println("[" + new Date(System.currentTimeMillis()) + "] файл " + file.getName() + ". Инициализация чтения.");
            FileInputStream fis = new FileInputStream(file);
            /*XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(2);
            Iterator< Row > it = sheet.iterator();*/
            HSSFWorkbook wb = new HSSFWorkbook(fis);
            HSSFSheet sheet = wb.getSheetAt(0);
            Iterator< Row > it = sheet.iterator();
            writer.println("[" + new Date(System.currentTimeMillis()) + "] файл " +file.getName() + ". Чтение начато успешно.");
                while (it.hasNext()) {
                    HSSFRow row = (HSSFRow) it.next();
                    if (row.getRowNum() > 2) {
                        /*Iterator <Cell> cellIterator = row.cellIterator();
                        while (cellIterator.hasNext()) {
                            XSSFCell cell = (XSSFCell) cellIterator.next();

                        }*/
                        writer.println("[" + new Date(System.currentTimeMillis()) + "] строка " +row.getRowNum() + " файла " + file.getName() + ". Начато чтение.");
                        customer = getCellStringValue(row.getCell(2));
                        contract = getCellStringValue(row.getCell(3));
                        request = getCellStringValue(row.getCell(4));
                        designation = getCellStringValue(row.getCell(5));
                        workName = getCellStringValue(row.getCell(6));
                        intensity = getCellStringValue(row.getCell(7));
                        overtime = getCellStringValue(row.getCell(8));
                        if (overtime.equals("") || overtime.equals("N") || overtime.equals("N/A")) {
                            overtime = "0";
                        }
                        sum_intensity = getCellStringValue(row.getCell(9));
                        workStartDate = getCellStringValue(row.getCell(10));
                        workEndDatePA = getCellStringValue(row.getCell(11));
                        workEndDate = getCellStringValue(row.getCell(12));
                        stage = getCellStringValue(row.getCell(13));
                        if (designation.equals("") || designation.length()<4) {
                            if (stage.equals("") && customer.equals("") && contract.equals("") && request.equals("")) {
                                writer.println("[" + new Date(System.currentTimeMillis()) + "] ОШИБКА: отсутствуют данные, строка считана не будет");
                                continue;
                            } else {
                                designation = "ОШИБКА";
                            }
                        }
                        if (intensity.equals("") || intensity.equals("N") || intensity.equals("N/A")) {
                            intensity = "0";
                        }
                        note = getCellStringValue(row.getCell(14));

                        workslist.add(new Works(customer, contract, request, designation, workName, intensity, overtime, sum_intensity, workStartDate, workEndDatePA, workEndDate, stage, note));
                        writer.println("[" + new Date(System.currentTimeMillis()) + "] чтение строки " +row.getRowNum() + " файла " + file.getName() + " завершено");


                    }
                }
            writer.println("[" + new Date(System.currentTimeMillis()) + "] Чтение файла " +file.getName() + " завершено");
            fis.close();
            wb.close();
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            infoAlert.setTitle("Файл не найден");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("В директории с программой не найден excel файл с показаниями, попробуйте указать путь к файлу или заполнить показания вручную");
            infoAlert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void dictDataParsingCollection(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);

            HSSFWorkbook wb = new HSSFWorkbook(fis);
//            HSSFSheet sheet = wb.getSheetAt(0);
//            Iterator< Row > it = sheet.iterator();
            Iterator <Sheet> sht = wb.iterator();

            while (sht.hasNext()) {
                Iterator< Row > it = sht.next().iterator();
                while (it.hasNext()) {
                    HSSFRow row = (HSSFRow) it.next();
                    if (row.getRowNum() > 2) {
                        customer = getCellStringValue(row.getCell(1));
                        contract = getCellStringValue(row.getCell(2));
                        project = getCellStringValue(row.getCell(3));
                        request = getCellStringValue(row.getCell(4));
                        incomingDate = getCellStringValue(row.getCell(6));
                        designation = getCellStringValue(row.getCell(7)).replace("Т","T");

                        if (customer.equals("") & contract.equals("") & designation.equals("")) {
                            continue;
                        }

                        workName = getCellStringValue(row.getCell(8));
                        outsource = getCellStringValue(row.getCell(10)).replace(" ", "");

                        if (outsource.equalsIgnoreCase("ереван") || outsource.equalsIgnoreCase("рига")){
                            outsource = "true";
                        }else{
                            outsource = "false";
                        }

                        pa_intensity = getCellStringValue(row.getCell(14));

                        if (incomingDate.equalsIgnoreCase("N/A") || incomingDate.equals("")) {
                            incomingDate = "";
                        }
                        if (pa_intensity.equals("") || pa_intensity.equals("N") || pa_intensity.equals("N/A")) {
                            pa_intensity = "0";
                        }
                        tz_intensity = getCellStringValue(row.getCell(15));
                        if (tz_intensity.equals("") || tz_intensity.equals("N") || tz_intensity.equals("N/A")) {
                            tz_intensity = "0";
                        }

                        status_id = getCellStringValue(row.getCell(23)).toLowerCase();

                        if (status_id.equals("")) {
                            status_id = "в работе";
                        }

                        workslist.add(new Works(customer, contract, project, request, incomingDate, designation, workName, pa_intensity, tz_intensity, status_id, outsource));


                    }
                }
            }
            fis.close();
            wb.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                infoAlert.setTitle("Файл не найден");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("В директории с программой не найден excel файл с показаниями, попробуйте указать путь к файлу или заполнить показания вручную");
                infoAlert.showAndWait();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
    }

    public void fillCollectionDB() throws SQLException {
        dBconnection.openDB();
        dBconnection.query("SELECT * FROM public.parsing_sheet WHERE status_id != " +
                "(SELECT s.status_id FROM public.status s " +
                "join public.status_type st on st.status_type_id = s.status_type " +
                "WHERE s.status_name = 'declined' AND st.status_type_name = 'incoming_works') " +
                "AND status_id != " +
                "(SELECT s.status_id FROM public.status s " +
                "join public.status_type st on st.status_type_id = s.status_type " +
                "WHERE s.status_name = 'approved' AND st.status_type_name = 'incoming_works')");
        while (dBconnection.getRs().next()) {
            id = dBconnection.getRs().getString(1);
            user_id = dBconnection.getRs().getString(2);
            year = dBconnection.getRs().getString(3);
            month = dBconnection.getRs().getString(4);
            designation = dBconnection.getRs().getString(5);
            stage = dBconnection.getRs().getString(6);
            sheet_id = dBconnection.getRs().getString(18);
            status_id = dBconnection.getRs().getString(17);

            workslist.add(new Works(id, user_id, year, month, designation, stage, sheet_id, status_id));
        }
        dBconnection.queryClose();
        dBconnection.closeDB();
    }

    public void fillPeriodReportCollectionDB(String contractNumber, String period) throws SQLException {

        workslist.clear();


        int number = 1;

        dBconnection.openDB();
        if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") || MainController.role.equalsIgnoreCase("АУП") || MainController.role.equalsIgnoreCase("Менеджер проекта")) {
            dBconnection.query("SELECT cr.customer_name, r.request_number, t.task_number, t.task_pa_intensity, t.task_tz_intensity, tu.task_uom_name, t.task_unit_plan, t.task_unit_fact, ss.status_name, t.task_out, p.project_name FROM public.task t " +
                    "join public.stage s on s.task_id = t.task_id " +
                    "left join public.task_uom tu on tu.task_uom_id = t.task_uom_id " +
                    "join public.project p on p.project_id = t.project_id " +
                    "join public.contract_project cp on cp.project_id = p.project_id " +
                    "join public.contract ct on ct.contract_id = cp.contract_id " +
                    "join public.status ss on ss.status_id = t.status_id " +
                    "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                    "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                    "left join public.request r on r.request_id = t.request_id " +
                    "join public.customer cr on cr.customer_id = ct.customer_id " +
                    "WHERE ct.contract_number = '" + contractNumber + "' AND " + period + " " +
                    "GROUP BY cr.customer_name, r.request_number, t.task_number, t.task_pa_intensity, t.task_tz_intensity, tu.task_uom_name, t.task_unit_plan, t.task_unit_fact, ss.status_name, t.task_out, p.project_name HAVING count(t.task_id)>=1 " +
                    "ORDER BY t.task_number");
        } else if (MainController.role.equalsIgnoreCase("Начальник отдела") || MainController.role.equalsIgnoreCase("Ведущий специалист")) {
            dBconnection.query("SELECT cr.customer_name, r.request_number, t.task_number, t.task_pa_intensity, t.task_tz_intensity, tu.task_uom_name, t.task_unit_plan, t.task_unit_fact, ss.status_name, t.task_out, p.project_name FROM public.task t " +
                    "join public.stage s on s.task_id = t.task_id " +
                    "left join public.task_uom tu on tu.task_uom_id = t.task_uom_id " +
                    "join public.project p on p.project_id = t.project_id " +
                    "join public.contract_project cp on cp.project_id = p.project_id " +
                    "join public.contract ct on ct.contract_id = cp.contract_id " +
                    "join public.status ss on ss.status_id = t.status_id " +
                    "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                    "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                    "left join public.request r on r.request_id = t.request_id " +
                    "join public.customer cr on cr.customer_id = ct.customer_id " +
                    "join public.user u on u.user_id = dw.user_id " +
                    "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                    "WHERE ct.contract_number = '" + contractNumber + "' AND " + period + " AND " +
                    "(us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"')) " +
                    "GROUP BY cr.customer_name, r.request_number, t.task_number, t.task_pa_intensity, t.task_tz_intensity, tu.task_uom_name, t.task_unit_plan, t.task_unit_fact, ss.status_name, t.task_out, p.project_name HAVING count(t.task_id)>=1 " +
                    "ORDER BY t.task_number");
        } else {
            dBconnection.query("");
        }
        while (dBconnection.getRs().next()) {

            num = "" + number;
            number++;

            customer = dBconnection.getRs().getString(1);
            contract = contractNumber;
            request = dBconnection.getRs().getString(2);
            designation = dBconnection.getRs().getString(3);
            pa_intensity = dBconnection.getRs().getString(4);
            tz_intensity = dBconnection.getRs().getString(5);
            uom = dBconnection.getRs().getString(6);
            uom_plan = dBconnection.getRs().getString(7);
            uom_fact = dBconnection.getRs().getString(8);

            if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") || MainController.role.equalsIgnoreCase("АУП") || MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "WHERE t.task_number = '" + designation + "' AND st.stage_type_name = 'Подготовка' AND " + period + "");
            } else if (MainController.role.equalsIgnoreCase("Начальник отдела") || MainController.role.equalsIgnoreCase("Ведущий специалист")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.user u on u.user_id = dw.user_id " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "WHERE t.task_number = '" + designation + "' AND st.stage_type_name = 'Подготовка' AND " + period + " AND " +
                        "u.user_id IN (SELECT ur.user_id FROM public.user ur " +
                        "left join public.user_subordination us on us.user_sub_id = ur.user_id " +
                        "WHERE (us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"') OR ur.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"')) " +
                        "GROUP BY ur.user_id HAVING count(ur.user_id) >= 1)");
            } else {
                dBconnection.serviceQuery("");
            }
            while (dBconnection.getServiceRs().next()) {
                stage_recieve = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") || MainController.role.equalsIgnoreCase("АУП") || MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "WHERE t.task_number = '"+designation+"' AND (st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление') AND "+period+"");
            } else if (MainController.role.equalsIgnoreCase("Начальник отдела") || MainController.role.equalsIgnoreCase("Ведущий специалист")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.user u on u.user_id = dw.user_id " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "WHERE t.task_number = '"+designation+"' AND (st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление') AND "+period+" AND " +
                        "u.user_id IN (SELECT ur.user_id FROM public.user ur " +
                        "left join public.user_subordination us on us.user_sub_id = ur.user_id " +
                        "WHERE (us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"') OR ur.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"')) " +
                        "GROUP BY ur.user_id HAVING count(ur.user_id) >= 1)");
            } else {
                dBconnection.serviceQuery("");
            }
            while (dBconnection.getServiceRs().next()) {
                stage_execute = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") || MainController.role.equalsIgnoreCase("АУП") || MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "WHERE t.task_number = '" + designation + "' AND st.stage_type_name = 'Сопровождение' AND " + period + "");
            } else if (MainController.role.equalsIgnoreCase("Начальник отдела") || MainController.role.equalsIgnoreCase("Ведущий специалист")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.user u on u.user_id = dw.user_id " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "WHERE t.task_number = '" + designation + "' AND st.stage_type_name = 'Сопровождение' AND " + period + " AND " +
                        "u.user_id IN (SELECT ur.user_id FROM public.user ur " +
                        "left join public.user_subordination us on us.user_sub_id = ur.user_id " +
                        "WHERE (us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"') OR ur.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"')) " +
                        "GROUP BY ur.user_id HAVING count(ur.user_id) >= 1)");
            } else {
                dBconnection.serviceQuery("");
            }
            while (dBconnection.getServiceRs().next()) {
                stage_escort = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") || MainController.role.equalsIgnoreCase("АУП") || MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "WHERE t.task_number = '" + designation + "' AND st.stage_type_name = 'Проверка' AND " + period + "");
            } else if (MainController.role.equalsIgnoreCase("Начальник отдела") || MainController.role.equalsIgnoreCase("Ведущий специалист")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.user u on u.user_id = dw.user_id " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "WHERE t.task_number = '" + designation + "' AND st.stage_type_name = 'Проверка' AND " + period + " AND " +
                        "u.user_id IN (SELECT ur.user_id FROM public.user ur " +
                        "left join public.user_subordination us on us.user_sub_id = ur.user_id " +
                        "WHERE (us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"') OR ur.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"')) " +
                        "GROUP BY ur.user_id HAVING count(ur.user_id) >= 1)");
            } else {
                dBconnection.serviceQuery("");
            }
            while (dBconnection.getServiceRs().next()) {
                stage_check = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") || MainController.role.equalsIgnoreCase("АУП") || MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "WHERE t.task_number = '" + designation + "' AND st.stage_type_name = 'Утверждение' AND " + period + "");
            } else if (MainController.role.equalsIgnoreCase("Начальник отдела") || MainController.role.equalsIgnoreCase("Ведущий специалист")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.user u on u.user_id = dw.user_id " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "WHERE t.task_number = '" + designation + "' AND st.stage_type_name = 'Утверждение' AND " + period + " AND " +
                        "u.user_id IN (SELECT ur.user_id FROM public.user ur " +
                        "left join public.user_subordination us on us.user_sub_id = ur.user_id " +
                        "WHERE (us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"') OR ur.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"')) " +
                        "GROUP BY ur.user_id HAVING count(ur.user_id) >= 1)");
            } else {
                dBconnection.serviceQuery("");
            }
            while (dBconnection.getServiceRs().next()) {
                stage_approve = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            if (stage_recieve == null) {
                stage_recieve = "0";
            }
            if (stage_execute == null) {
                stage_execute = "0";
            }
            if (stage_escort == null) {
                stage_escort = "0";
            }
            if (stage_check == null) {
                stage_check = "0";
            }
            if (stage_approve == null) {
                stage_approve = "0";
            }
            if (uom == null) {
                uom = "N/A";
            }
            if (uom_plan == null) {
                uom_plan = "0";
            }
            if (uom_fact == null) {
                uom_fact = "0";
            }

            status_id = dBconnection.getRs().getString(9);
            outsource = dBconnection.getRs().getString(10);
            project = dBconnection.getRs().getString(11);

            workslist.add(new Works(num, customer, contract, request, designation, pa_intensity, tz_intensity, uom, uom_plan, uom_fact, stage_recieve, stage_execute, stage_escort,
                    stage_check, stage_approve, status_id, outsource, project));
        }



    }

    private String getCellStringValue (HSSFCell cell) {
        String cellValue = "";
        try {
            if (!(cell==null)) {
                switch (cell.getCellTypeEnum()) {
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            cellValue = sdf.format(cell.getDateCellValue()).toString();
                        } else {
                            cellValue = "" + (int) cell.getNumericCellValue();
                        }
                        break;
                    case STRING:
                        cellValue = cell.getStringCellValue();
                        break;
                    case BLANK:
                        cellValue ="";
                        break;
                    case FORMULA:
                        cellValue ="" + (int) cell.getNumericCellValue();
                        break;
                }
            }
            return cellValue;
        } catch (NullPointerException e) {
            return null;
        }
    }


    @Override
    public void add(Works work) {workslist.add(work);}

    @Override
    public void delete(Works work) {workslist.remove(work);}

    public ObservableList<Works> getWorkslist() {
        return workslist;
    }

    public void clear() {
        workslist.clear();
    }
}
