package ru.ctp.motyrev.objects;

import javafx.beans.property.SimpleStringProperty;

public class UserState {

    private SimpleStringProperty user_id = new SimpleStringProperty("");
    private SimpleStringProperty user_fio = new SimpleStringProperty("");
    private SimpleStringProperty year = new SimpleStringProperty("");
    private SimpleStringProperty month = new SimpleStringProperty("");
    private SimpleStringProperty intensity = new SimpleStringProperty("");
    private SimpleStringProperty overtime = new SimpleStringProperty("");
    private SimpleStringProperty sum_intensity = new SimpleStringProperty("");
    private SimpleStringProperty stage = new SimpleStringProperty("");
    private SimpleStringProperty note = new SimpleStringProperty("");
    private SimpleStringProperty sheet_id = new SimpleStringProperty("");
    private SimpleStringProperty status_id = new SimpleStringProperty("");

    private SimpleStringProperty stage_request = new SimpleStringProperty("");
    private SimpleStringProperty stage_project = new SimpleStringProperty("");
    private SimpleStringProperty stage_adm = new SimpleStringProperty("");
    private SimpleStringProperty stage_idle = new SimpleStringProperty("");
    private SimpleStringProperty stage_otpusk = new SimpleStringProperty("");
    private SimpleStringProperty stage_boln = new SimpleStringProperty("");
    private SimpleStringProperty stage_study = new SimpleStringProperty("");

    public UserState(String user_id, String user_fio, String stage_adm, String stage_idle, String stage_otpusk, String stage_boln, String stage_study, String stage_project, String stage_request) {

        this.user_id = new SimpleStringProperty(user_id);
        this.user_fio = new SimpleStringProperty(user_fio);
        this.stage_adm = new SimpleStringProperty(stage_adm);
        this.stage_idle = new SimpleStringProperty(stage_idle);
        this.stage_otpusk = new SimpleStringProperty(stage_otpusk);
        this.stage_boln = new SimpleStringProperty(stage_boln);
        this.stage_study = new SimpleStringProperty(stage_study);
        this.stage_project = new SimpleStringProperty(stage_project);
        this.stage_request = new SimpleStringProperty(stage_request);
    }

    public UserState() {

    }

    public String getStage_project() {
        return stage_project.get();
    }

    public SimpleStringProperty stage_projectProperty() {
        return stage_project;
    }

    public void setStage_project(String stage_project) {
        this.stage_project.set(stage_project);
    }

    public String getUser_id() {
        return user_id.get();
    }

    public SimpleStringProperty user_idProperty() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id.set(user_id);
    }

    public String getUser_fio() {
        return user_fio.get();
    }

    public SimpleStringProperty user_fioProperty() {
        return user_fio;
    }

    public void setUser_fio(String user_fio) {
        this.user_fio.set(user_fio);
    }

    public String getYear() {
        return year.get();
    }

    public SimpleStringProperty yearProperty() {
        return year;
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    public String getMonth() {
        return month.get();
    }

    public SimpleStringProperty monthProperty() {
        return month;
    }

    public void setMonth(String month) {
        this.month.set(month);
    }

    public String getIntensity() {
        return intensity.get();
    }

    public SimpleStringProperty intensityProperty() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity.set(intensity);
    }

    public String getOvertime() {
        return overtime.get();
    }

    public SimpleStringProperty overtimeProperty() {
        return overtime;
    }

    public void setOvertime(String overtime) {
        this.overtime.set(overtime);
    }

    public String getSum_intensity() {
        return sum_intensity.get();
    }

    public SimpleStringProperty sum_intensityProperty() {
        return sum_intensity;
    }

    public void setSum_intensity(String sum_intensity) {
        this.sum_intensity.set(sum_intensity);
    }

    public String getStage() {
        return stage.get();
    }

    public SimpleStringProperty stageProperty() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage.set(stage);
    }

    public String getNote() {
        return note.get();
    }

    public SimpleStringProperty noteProperty() {
        return note;
    }

    public void setNote(String note) {
        this.note.set(note);
    }

    public String getSheet_id() {
        return sheet_id.get();
    }

    public SimpleStringProperty sheet_idProperty() {
        return sheet_id;
    }

    public void setSheet_id(String sheet_id) {
        this.sheet_id.set(sheet_id);
    }

    public String getStatus_id() {
        return status_id.get();
    }

    public SimpleStringProperty status_idProperty() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id.set(status_id);
    }

    public String getStage_adm() {
        return stage_adm.get();
    }

    public SimpleStringProperty stage_admProperty() {
        return stage_adm;
    }

    public void setStage_adm(String stage_adm) {
        this.stage_adm.set(stage_adm);
    }

    public String getStage_idle() {
        return stage_idle.get();
    }

    public SimpleStringProperty stage_idleProperty() {
        return stage_idle;
    }

    public void setStage_idle(String stage_idle) {
        this.stage_idle.set(stage_idle);
    }

    public String getStage_otpusk() {
        return stage_otpusk.get();
    }

    public SimpleStringProperty stage_otpuskProperty() {
        return stage_otpusk;
    }

    public void setStage_otpusk(String stage_otpusk) {
        this.stage_otpusk.set(stage_otpusk);
    }

    public String getStage_boln() {
        return stage_boln.get();
    }

    public SimpleStringProperty stage_bolnProperty() {
        return stage_boln;
    }

    public void setStage_boln(String stage_boln) {
        this.stage_boln.set(stage_boln);
    }

    public String getStage_study() {
        return stage_study.get();
    }

    public SimpleStringProperty stage_studyProperty() {
        return stage_study;
    }

    public void setStage_study(String stage_study) {
        this.stage_study.set(stage_study);
    }

    public String getStage_request() {
        return stage_request.get();
    }

    public SimpleStringProperty stage_requestProperty() {
        return stage_request;
    }

    public void setStage_request(String stage_request) {
        this.stage_request.set(stage_request);
    }
}
