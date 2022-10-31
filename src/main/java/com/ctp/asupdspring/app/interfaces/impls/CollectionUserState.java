package com.ctp.asupdspring.app.interfaces.impls;

import com.ctp.asupdspring.app.interfaces.UserStateInterface;
import com.ctp.asupdspring.app.objects.UserState;
import com.ctp.asupdspring.app.repo.DailyWorkRepository;
import com.ctp.asupdspring.app.repo.UserInfoRepository;
import com.ctp.asupdspring.app.repo.UserRepository;
import com.ctp.asupdspring.controllers.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
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
    private final UserRepository userRepository;
    private final DailyWorkRepository dailyWorkRepository;
    //  Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy'_'HH-mm-ss");
    List<Object[]> sql;
    private PrintWriter writer;

    //  DBconnection dBconnection = new DBconnection();

    public void fillUserResourceReportCollectionDB(String period) throws SQLException {

        userstatelist.clear();

        if (MainController.role.equalsIgnoreCase("Super_user") || MainController.role.equalsIgnoreCase("Admin") ||
                MainController.role.equalsIgnoreCase("АУП")) {
            sql = userRepository.getUsersByPeriod(period);
        } else if (MainController.role.equalsIgnoreCase("Менеджер проекта")) {
            sql = userRepository.getUsersByPeriodPM(period, MainController.who);
        } else if (MainController.role.equalsIgnoreCase("Начальник отдела") || MainController.role.equalsIgnoreCase("Ведущий специалист")) {
            sql = userRepository.getUsersByPeriodSubOrd(period, MainController.who);
        } else {
            // dBconnection.query("");
        }
        for (int i = 0; i < sql.size(); i++) {
            user_id = String.valueOf(sql.get(i)[0]);
            user_fio = String.valueOf(sql.get(i)[1]);

            dailyWorkRepository.getUserBackOfficeSumByPeriod()
            while (dBconnection.getServiceRs().next()) {
                stage_adm = dBconnection.getServiceRs().getString(1);
            }
            //   dBconnection.serviceQueryClose();

            dailyWorkRepository.getUserVacationSumByPeriod()
            while (dBconnection.getServiceRs().next()) {
                stage_otpusk = dBconnection.getServiceRs().getString(1);
            }
            // dBconnection.serviceQueryClose();

            dailyWorkRepository.getUserHospitalSumByPeriod()
            while (dBconnection.getServiceRs().next()) {
                stage_boln = dBconnection.getServiceRs().getString(1);
            }
            // dBconnection.serviceQueryClose();

            dailyWorkRepository.getUserEducationSumByPeriod()
            while (dBconnection.getServiceRs().next()) {
                stage_study = dBconnection.getServiceRs().getString(1);
            }
            // dBconnection.serviceQueryClose();

            dailyWorkRepository.getUserIdleSumByPeriod()
            while (dBconnection.getServiceRs().next()) {
                stage_idle = dBconnection.getServiceRs().getString(1);
            }
            // dBconnection.serviceQueryClose();

            if (MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                dailyWorkRepository.getUserDailyIntensitySumByPeriodAndProjectPM()
            } else {
                dailyWorkRepository.getUserDailyIntensitySumByPeriod()
            }
            while (dBconnection.getServiceRs().next()) {
                stage_project = dBconnection.getServiceRs().getString(1);
            }
            //   dBconnection.serviceQueryClose();

            if (MainController.role.equalsIgnoreCase("Менеджер проекта")) {
                dailyWorkRepository.getUserDailyIntensitySumByPeriodAndRequestAndProjectPM()
            } else {
                dailyWorkRepository.getUserDailyIntensitySumByPeriodAndRequest();
            }
            while (dBconnection.getServiceRs().next()) {
                stage_request = dBconnection.getServiceRs().getString(1);
            }
            //  dBconnection.serviceQueryClose();

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
