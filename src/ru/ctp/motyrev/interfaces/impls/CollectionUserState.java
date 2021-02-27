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
import ru.ctp.motyrev.interfaces.UserStateInterface;
import ru.ctp.motyrev.interfaces.WorksBook;
import ru.ctp.motyrev.objects.UserState;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class CollectionUserState implements UserStateInterface, Serializable {

    private String user_id;
    private String user_fio;
    private String stage_adm;
    private String stage_idle;
    private String stage_otpusk;
    private String stage_boln;
    private String stage_study;
    private String stage_project;
    private String stage_request;

    private ObservableList<UserState> userstatelist = FXCollections.observableArrayList();

    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy'_'HH-mm-ss");

    private PrintWriter writer;

    DBconnection dBconnection = new DBconnection();

    public void fillUserResourceReportCollectionDB(String period) throws SQLException {

        userstatelist.clear();

        dBconnection.openDB();
        if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") || MainController.role.equalsIgnoreCase("АУП")) {
            dBconnection.query("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                    "join public.daily_work dw on dw.user_id = u.user_id " +
                    "WHERE " + period + " " +
                    "GROUP BY u.user_id_number, u.user_fullname HAVING count(u.user_id)>=1 " +
                    "ORDER BY u.user_fullname");
        } else if (MainController.role.equalsIgnoreCase("Менеджер проекта")) {
            dBconnection.query("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                    "join public.daily_work dw on dw.user_id = u.user_id " +
                    "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                    "join public.stage s on s.stage_id = sd.stage_id " +
                    "join public.task t on t.task_id = s.task_id " +
                    "join public.project p on p.project_id = t.project_id " +
                    "join public.project_manager pm on pm.project_id = p.project_id " +
                    "WHERE " + period + " AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '"+ MainController.who +"') " +
                    "GROUP BY u.user_id_number, u.user_fullname HAVING count(u.user_id)>=1 " +
                    "ORDER BY u.user_fullname");
        } else if (MainController.role.equalsIgnoreCase("Начальник отдела") || MainController.role.equalsIgnoreCase("Ведущий специалист")) {
            dBconnection.query("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                    "join public.daily_work dw on dw.user_id = u.user_id " +
                    "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                    "WHERE " + period + " AND " +
                    "(us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"')) " +
                    "GROUP BY u.user_id_number, u.user_fullname HAVING count(u.user_id)>=1 " +
                    "ORDER BY u.user_fullname");
        } else {
            dBconnection.query("");
        }
        while (dBconnection.getRs().next()) {

            user_id = dBconnection.getRs().getString(1);
            user_fio = dBconnection.getRs().getString(2);

                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.user_stage_daily usd on usd.daily_work_id = dw.daily_work_id " +
                        "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                        "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                        "join public.user u on u.user_id = us.user_id " +
                        "WHERE u.user_id_number = '" + user_id + "' AND ust.user_stage_type_name = 'Работы по бэк-офису' AND " + period + "");
            while (dBconnection.getServiceRs().next()) {
                stage_adm = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                    "join public.user_stage_daily usd on usd.daily_work_id = dw.daily_work_id " +
                    "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                    "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                    "join public.user u on u.user_id = us.user_id " +
                    "WHERE u.user_id_number = '"+user_id+"' AND ust.user_stage_type_name = 'Отпуск' AND "+period+"");
            while (dBconnection.getServiceRs().next()) {
                stage_otpusk = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                    "join public.user_stage_daily usd on usd.daily_work_id = dw.daily_work_id " +
                    "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                    "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                    "join public.user u on u.user_id = us.user_id " +
                    "WHERE u.user_id_number = '"+user_id+"' AND ust.user_stage_type_name = 'Больничный' AND "+period+"");
            while (dBconnection.getServiceRs().next()) {
                stage_boln = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                    "join public.user_stage_daily usd on usd.daily_work_id = dw.daily_work_id " +
                    "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                    "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                    "join public.user u on u.user_id = us.user_id " +
                    "WHERE u.user_id_number = '"+user_id+"' AND ust.user_stage_type_name = 'Обучение' AND "+period+"");
            while (dBconnection.getServiceRs().next()) {
                stage_study = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                    "join public.user_stage_daily usd on usd.daily_work_id = dw.daily_work_id " +
                    "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                    "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                    "join public.user u on u.user_id = us.user_id " +
                    "WHERE u.user_id_number = '"+user_id+"' AND ust.user_stage_type_name = 'IDLE' AND "+period+"");
            while (dBconnection.getServiceRs().next()) {
                stage_idle = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            if (MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.user u on u.user_id = dw.user_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "join public.project p on p.project_id = t.project_id " +
                        "join public.project_manager pm on pm.project_id = p.project_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "WHERE u.user_id_number = '" + user_id + "' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' OR st.stage_type_name = 'Утверждение') " +
                        "AND " + period + " AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '"+ MainController.who +"')");
            } else {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.user u on u.user_id = dw.user_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "WHERE u.user_id_number = '" + user_id + "' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' OR st.stage_type_name = 'Утверждение') " +
                        "AND " + period + "");
            }
            while (dBconnection.getServiceRs().next()) {
                stage_project = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            if (MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.user u on u.user_id = dw.user_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "join public.project p on p.project_id = t.project_id " +
                        "join public.project_manager pm on pm.project_id = p.project_id " +
                        "join public.request r on r.request_id = t.request_id " +
                        "WHERE u.user_id_number = '" + user_id + "' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' OR st.stage_type_name = 'Утверждение') " +
                        "AND " + period + " AND r.request_id IN (SELECT request_id FROM public.request) AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '"+ MainController.who +"')");
            } else {
                dBconnection.serviceQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                        "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                        "join public.user u on u.user_id = dw.user_id " +
                        "join public.stage s on s.stage_id = sd.stage_id " +
                        "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                        "join public.task t on t.task_id = s.task_id " +
                        "join public.request r on r.request_id = t.request_id " +
                        "WHERE u.user_id_number = '" + user_id + "' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' OR st.stage_type_name = 'Утверждение') " +
                        "AND " + period + " AND r.request_id IN (SELECT request_id FROM public.request)");
            }
            while (dBconnection.getServiceRs().next()) {
                stage_request = dBconnection.getServiceRs().getString(1);
            }
            dBconnection.serviceQueryClose();

            if (stage_adm == null) {
                stage_adm = "0";
            }
            if (stage_otpusk == null) {
                stage_otpusk = "0";
            }
            if (stage_boln == null) {
                stage_boln = "0";
            }
            if (stage_study == null) {
                stage_study = "0";
            }
            if (stage_idle == null) {
                stage_idle = "0";
            }
            if (stage_project == null) {
                stage_project = "0";
            }
            if (stage_request == null) {
                stage_request = "0";
            }

            userstatelist.add(new UserState(user_id, user_fio, stage_adm, stage_idle, stage_otpusk, stage_boln, stage_study, stage_project, stage_request));
        }



    }

    public ObservableList<UserState> getUserstatelist() {
        return userstatelist;
    }

    public void clear() {
        userstatelist.clear();
    }

    @Override
    public void add(UserState userState) {

    }

    @Override
    public void delete(UserState userState) {

    }
}
