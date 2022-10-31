package com.ctp.asupdspring.app.objects;

import javafx.beans.property.SimpleStringProperty;

public class Works {
    private SimpleStringProperty id = new SimpleStringProperty("");
    private SimpleStringProperty user_id = new SimpleStringProperty("");
    private SimpleStringProperty year = new SimpleStringProperty("");
    private SimpleStringProperty month = new SimpleStringProperty("");
    private SimpleStringProperty num = new SimpleStringProperty("");
    private SimpleStringProperty customer = new SimpleStringProperty("");
    private SimpleStringProperty contract = new SimpleStringProperty("");
    private SimpleStringProperty request = new SimpleStringProperty("");
    private SimpleStringProperty designation = new SimpleStringProperty("");
    private SimpleStringProperty workName = new SimpleStringProperty("");
    private SimpleStringProperty intensity = new SimpleStringProperty("");
    private SimpleStringProperty overtime = new SimpleStringProperty("");
    private SimpleStringProperty sum_intensity = new SimpleStringProperty("");
    private SimpleStringProperty incomingDate = new SimpleStringProperty("");
    private SimpleStringProperty workStartDate = new SimpleStringProperty("");
    private SimpleStringProperty workEndDatePA = new SimpleStringProperty("");
    private SimpleStringProperty workEndDate = new SimpleStringProperty("");
    private SimpleStringProperty stage = new SimpleStringProperty("");
    private SimpleStringProperty note = new SimpleStringProperty("");
    private SimpleStringProperty project = new SimpleStringProperty("");
    private SimpleStringProperty sheet_id = new SimpleStringProperty("");
    private SimpleStringProperty status_id = new SimpleStringProperty("");
    private SimpleStringProperty uom = new SimpleStringProperty("");
    private SimpleStringProperty uom_plan = new SimpleStringProperty("");
    private SimpleStringProperty uom_fact = new SimpleStringProperty("");
    private SimpleStringProperty pa_intensity = new SimpleStringProperty("");
    private SimpleStringProperty tz_intensity = new SimpleStringProperty("");
    private SimpleStringProperty outsource = new SimpleStringProperty("");

    private SimpleStringProperty stage_recieve = new SimpleStringProperty("");
    private SimpleStringProperty stage_execute = new SimpleStringProperty("");
    private SimpleStringProperty stage_escort = new SimpleStringProperty("");
    private SimpleStringProperty stage_check = new SimpleStringProperty("");
    private SimpleStringProperty stage_approve = new SimpleStringProperty("");



    public Works(String customer, String contract, String request, String designation, String workName, String intensity, String overtime, String sum_intensity, String workStartDate,
                                  String workEndDatePA, String workEndDate, String stage, String note) {

        this.customer = new SimpleStringProperty(customer);
        this.contract = new SimpleStringProperty(contract);
        this.request = new SimpleStringProperty(request);
        this.designation = new SimpleStringProperty(designation);
        this.workName = new SimpleStringProperty(workName);
        this.intensity = new SimpleStringProperty(intensity);
        this.overtime = new SimpleStringProperty(overtime);
        this.sum_intensity = new SimpleStringProperty(sum_intensity);
        this.workStartDate = new SimpleStringProperty(workStartDate);
        this.workEndDatePA = new SimpleStringProperty(workEndDatePA);
        this.workEndDate = new SimpleStringProperty(workEndDate);
        this.stage = new SimpleStringProperty(stage);
        this.note = new SimpleStringProperty(note);
    }

    public Works(String id, String user_id, String year, String month, String designation, String stage, String sheet_id, String status_id) {
        this.id = new SimpleStringProperty(id);
        this.user_id = new SimpleStringProperty(user_id);
        this.year = new SimpleStringProperty(year);
        this.month = new SimpleStringProperty(month);
        this.designation = new SimpleStringProperty(designation);
        this.stage = new SimpleStringProperty(stage);
        this.sheet_id = new SimpleStringProperty(sheet_id);
        this.status_id = new SimpleStringProperty(status_id);
    }

    public Works(String customer, String contract, String project, String request, String incomingDate, String designation, String workName, String pa_intensity, String tz_intensity, String status_id, String outsource) {
        this.customer = new SimpleStringProperty(customer);
        this.contract = new SimpleStringProperty(contract);
        this.project = new SimpleStringProperty(project);
        this.request = new SimpleStringProperty(request);
        this.incomingDate = new SimpleStringProperty(incomingDate);
        this.designation = new SimpleStringProperty(designation);
        this.workName = new SimpleStringProperty(workName);
        this.pa_intensity = new SimpleStringProperty(pa_intensity);
        this.tz_intensity = new SimpleStringProperty(tz_intensity);
        this.status_id = new SimpleStringProperty(status_id);
        this.outsource = new SimpleStringProperty(outsource);

    }

    public Works(String num, String customer, String contract, String request, String designation, String pa_intensity, String tz_intensity, String uom, String uom_plan, String uom_fact, String stage_recieve, String stage_execute, String stage_escort,
    String stage_check, String stage_approve, String status_id, String outsource, String project) {
        this.num = new SimpleStringProperty(num);
        this.customer = new SimpleStringProperty(customer);
        this.contract = new SimpleStringProperty(contract);
        this.request = new SimpleStringProperty(request);
        this.designation = new SimpleStringProperty(designation);
        this.pa_intensity = new SimpleStringProperty(pa_intensity);
        this.tz_intensity = new SimpleStringProperty(tz_intensity);
        this.uom = new SimpleStringProperty(uom);
        this.uom_plan = new SimpleStringProperty(uom_plan);
        this.uom_fact = new SimpleStringProperty(uom_fact);
        this.stage_recieve = new SimpleStringProperty(stage_recieve);
        this.stage_execute = new SimpleStringProperty(stage_execute);
        this.stage_escort = new SimpleStringProperty(stage_escort);
        this.stage_check = new SimpleStringProperty(stage_check);
        this.stage_approve = new SimpleStringProperty(stage_approve);
        this.status_id = new SimpleStringProperty(status_id);
        this.outsource = new SimpleStringProperty(outsource);
        this.project = new SimpleStringProperty(project);
    }

    public Works() {

    }

    public String getOutsource() {
        return outsource.get();
    }

    public SimpleStringProperty outsourceProperty() {
        return outsource;
    }

    public void setOutsource(String outsource) {
        this.outsource.set(outsource);
    }

    public String getStage_recieve() {
        return stage_recieve.get();
    }

    public SimpleStringProperty stage_recieveProperty() {
        return stage_recieve;
    }

    public void setStage_recieve(String stage_recieve) {
        this.stage_recieve.set(stage_recieve);
    }

    public String getStage_execute() {
        return stage_execute.get();
    }

    public SimpleStringProperty stage_executeProperty() {
        return stage_execute;
    }

    public void setStage_execute(String stage_execute) {
        this.stage_execute.set(stage_execute);
    }

    public String getStage_escort() {
        return stage_escort.get();
    }

    public SimpleStringProperty stage_escortProperty() {
        return stage_escort;
    }

    public void setStage_escort(String stage_escort) {
        this.stage_escort.set(stage_escort);
    }

    public String getStage_check() {
        return stage_check.get();
    }

    public SimpleStringProperty stage_checkProperty() {
        return stage_check;
    }

    public void setStage_check(String stage_check) {
        this.stage_check.set(stage_check);
    }

    public String getStage_approve() {
        return stage_approve.get();
    }

    public SimpleStringProperty stage_approveProperty() {
        return stage_approve;
    }

    public void setStage_approve(String stage_approve) {
        this.stage_approve.set(stage_approve);
    }

    public String getPa_intensity() {
        return pa_intensity.get();
    }

    public SimpleStringProperty pa_intensityProperty() {
        return pa_intensity;
    }

    public void setPa_intensity(String pa_intensity) {
        this.pa_intensity.set(pa_intensity);
    }

    public String getTz_intensity() {
        return tz_intensity.get();
    }

    public SimpleStringProperty tz_intensityProperty() {
        return tz_intensity;
    }

    public void setTz_intensity(String tz_intensity) {
        this.tz_intensity.set(tz_intensity);
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

    public String getSheet_id() {
        return sheet_id.get();
    }

    public SimpleStringProperty sheet_idProperty() {
        return sheet_id;
    }

    public void setSheet_id(String sheet_id) {
        this.sheet_id.set(sheet_id);
    }

    public String getIncomingDate() {
        return incomingDate.get();
    }

    public SimpleStringProperty incomingDateProperty() {
        return incomingDate;
    }

    public void setIncomingDate(String incomingDate) {
        this.incomingDate.set(incomingDate);
    }

    public String getProject() {
        return project.get();
    }

    public SimpleStringProperty projectProperty() {
        return project;
    }

    public void setProject(String project) {
        this.project.set(project);
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
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

    public String getSum_intensity() {
        return sum_intensity.get();
    }

    public SimpleStringProperty sum_intensityProperty() {
        return sum_intensity;
    }

    public void setSum_intensity(String sum_intensity) {
        this.sum_intensity.set(sum_intensity);
    }

    public String getNum() {
        return num.get();
    }

    public SimpleStringProperty numProperty() {
        return num;
    }

    public void setNum(String num) {
        this.num.set(num);
    }

    public String getCustomer() {
        return customer.get();
    }

    public SimpleStringProperty customerProperty() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer.set(customer);
    }

    public String getContract() {
        return contract.get();
    }

    public SimpleStringProperty contractProperty() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract.set(contract);
    }

    public String getRequest() {
        return request.get();
    }

    public SimpleStringProperty requestProperty() {
        return request;
    }

    public void setRequest(String request) {
        this.request.set(request);
    }

    public String getDesignation() {
        return designation.get();
    }

    public SimpleStringProperty designationProperty() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation.set(designation);
    }

    public String getWorkName() {
        return workName.get();
    }

    public SimpleStringProperty workNameProperty() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName.set(workName);
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

    public String getWorkStartDate() {
        return workStartDate.get();
    }

    public SimpleStringProperty workStartDateProperty() {
        return workStartDate;
    }

    public void setWorkStartDate(String workStartDate) {
        this.workStartDate.set(workStartDate);
    }

    public String getWorkEndDatePA() {
        return workEndDatePA.get();
    }

    public SimpleStringProperty workEndDatePAProperty() {
        return workEndDatePA;
    }

    public void setWorkEndDatePA(String workEndDatePA) {
        this.workEndDatePA.set(workEndDatePA);
    }

    public String getWorkEndDate() {
        return workEndDate.get();
    }

    public SimpleStringProperty workEndDateProperty() {
        return workEndDate;
    }

    public void setWorkEndDate(String workEndDate) {
        this.workEndDate.set(workEndDate);
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

    public String getUom() {
        return uom.get();
    }

    public SimpleStringProperty uomProperty() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom.set(uom);
    }

    public String getUom_plan() {
        return uom_plan.get();
    }

    public SimpleStringProperty uom_planProperty() {
        return uom_plan;
    }

    public void setUom_plan(String uom_plan) {
        this.uom_plan.set(uom_plan);
    }

    public String getUom_fact() {
        return uom_fact.get();
    }

    public SimpleStringProperty uom_factProperty() {
        return uom_fact;
    }

    public void setUom_fact(String uom_fact) {
        this.uom_fact.set(uom_fact);
    }
}
