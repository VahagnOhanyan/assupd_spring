/*
 * Copyright (c) 2008-2022
 * LANIT
 * All rights reserved.
 *
 * This product and related documentation are protected by copyright and
 * distributed under licenses restricting its use, copying, distribution, and
 * decompilation. No part of this product or related documentation may be
 * reproduced in any form by any means without prior written authorization of
 * LANIT and its licensors, if any.
 *
 * $
 */
package com.ctp.asupdspring.adapter.hibernate;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * todo Document type ContractRepositoryImpl
 */
@Component("taskRepositoryImpl")
public class TaskRepositoryImpl implements TaskRepositoryCustom {

    private final EntityManager entityManager;

    public TaskRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Object[]> getMyTasksAll() {
        List<Object[]> query = entityManager.createQuery(
                        "SELECT t.taskId, cr.customerName, c.contractNumber, p.projectName, " +
                                "r.requestNumber, t.taskNumber, t.taskName, t.taskIncomeDate, " +
                                "t.taskPaIntensity, t.taskTzIntensity, tu.taskUomName, " +
                                "t.taskUnitPlan, t.taskUnitFact, t.taskOut, s.statusName " +
                                "FROM TaskEntity t " +
                                "join ProjectEntity p on p.projectId = t.projectId " +
                                "left join RequestEntity r on r.requestId = t.requestId " +
                                "join CustomerEntity cr on cr.customerId = p.customerId " +
                                "join ContractProjectEntity cp on cp.projectId = p.projectId " +
                                "join ContractEntity c on c.contractId = cp.contractId " +
                                "join StatusEntity s on s.statusId = t.statusId " +
                                "left join TaskUomEntity tu on tu.taskUomId = t.taskUomId " +
                                "ORDER BY cr.customerName, c.contractNumber, p.projectName, t.taskNumber", Object[].class)
                .getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("taskId");
        columns.add("customerName");
        columns.add("contractNumber");
        columns.add("projectName");
        columns.add("requestNumber");
        columns.add("taskNumber");
        columns.add("taskName");
        columns.add("taskIncomeDate");
        columns.add("taskPaIntensity");
        columns.add("taskTzIntensity");
        columns.add("taskUomName");
        columns.add("taskUnitPlan");
        columns.add("taskUnitFact");
        columns.add("taskOut");
        columns.add("statusName");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    @Override
    public List<Object[]> getMyTasksPM(String userFullname) {
        List<Object[]> query =  entityManager.createQuery(
                        "SELECT t.taskId, cr.customerName, c.contractNumber, p.projectName, " +
                                "r.requestNumber, t.taskNumber, t.taskName, t.taskIncomeDate, " +
                                "t.taskPaIntensity, t.taskTzIntensity, tu.taskUomName, t.taskUnitPlan, " +
                                "t.taskUnitFact, t.taskOut, s.statusName " +
                                "FROM TaskEntity t " +
                                "join ProjectEntity p on p.projectId = t.projectId " +
                                "left join RequestEntity r on r.requestId = t.requestId " +
                                "join CustomerEntity cr on cr.customerId = p.customerId " +
                                "join ContractProjectEntity cp on cp.projectId = p.projectId " +
                                "join ContractEntity c on c.contractId = cp.contractId " +
                                "join StatusEntity s on s.statusId = t.statusId " +
                                "left join ProjectManagerEntity pm on pm.projectId = p.projectId " +
                                "left join UserEntity u on u.userId = pm.userId " +
                                "left join TaskExecutorEntity te on te.taskId = t.taskId " +
                                "left join UserEntity ute on ute.userId = te.userId " +
                                "left join TaskUomEntity tu on tu.taskUomId = t.taskUomId " +
                                "WHERE u.userId = (SELECT userId FROM public.user WHERE userFullname = '" + userFullname +
                                "') OR ute.userId = (SELECT userId FROM public.user WHERE userFullname = '" + userFullname +
                                "') OR ute.userId IN (SELECT us_su.userSubId FROM UserSubordinationEntity us_su " +
                                "join UserEntity usr on usr.userId = us_su.userId WHERE usr.userFullname = '" +
                                userFullname + "') " +
                                "GROUP BY t.taskId, cr.customerName, c.contractNumber, p.projectName, r.requestNumber, " +
                                "t.taskNumber, t.taskName, t.taskIncomeDate, t.taskPaIntensity, t.taskTzIntensity, " +
                                "tu.taskUomName, t.taskUnitPlan, t.taskUnitFact, t.taskOut, s.statusName " +
                                "ORDER BY cr.customerName, c.contractNumber, p.projectName, t.taskNumber", Object[].class)
                .getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("taskId");
        columns.add("customerName");
        columns.add("contractNumber");
        columns.add("projectName");
        columns.add("requestNumber");
        columns.add("taskNumber");
        columns.add("taskName");
        columns.add("taskIncomeDate");
        columns.add("taskPaIntensity");
        columns.add("taskTzIntensity");
        columns.add("taskUomName");
        columns.add("taskUnitPlan");
        columns.add("taskUnitFact");
        columns.add("taskOut");
        columns.add("statusName");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    @Override
    public List<Object[]> getMyTasksByUser(String userFullname) {
        List<Object[]> query =  entityManager.createQuery(
                        "SELECT t.taskId, cr.customerName, t.taskNumber, t.taskName, t.taskPaIntensity, " +
                                "tu.taskUomName, t.taskUnitPlan, t.taskIncomeDate, s.statusName " +
                                "FROM TaskEntity t " +
                                "join ProjectEntity p on p.projectId = t.projectId " +
                                "left join RequestEntity r on r.requestId = t.requestId " +
                                "join CustomerEntity cr on cr.customerId = p.customerId " +
                                "join ContractProjectEntity cp on cp.projectId = p.projectId " +
                                "join ContractEntity c on c.contractId = cp.contractId " +
                                "join StatusEntity s on s.statusId = t.statusId " +
                                "join TaskExecutorEntity te on te.taskId = t.taskId " +
                                "join UserEntity u on u.userId = te.userId " +
                                "left join TaskUomEntity tu on tu.taskUomId = t.taskUomId " +
                                "WHERE u.userId = (SELECT userId FROM public.user WHERE userFullname = '" + userFullname + "') " +
                                "ORDER BY cr.customerName, c.contractNumber, p.projectName, t.taskNumber", Object[].class)
                .getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("taskId");
        columns.add("customerName");
        columns.add("taskNumber");
        columns.add("taskName");
        columns.add("taskPaIntensity");
        columns.add("taskUomName");
        columns.add("taskUnitPlan");
        columns.add("taskIncomeDate");
        columns.add("statusName");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    @Override
    public List<Object[]> genMyTasksAll(String status) {
        List<Object[]> query =   entityManager.createQuery(
                        "SELECT t.taskId, cr.customerName, c.contractNumber, p.projectName, " +
                                "r.requestNumber, t.taskNumber, t.taskName, t.taskIncomeDate, " +
                                "t.taskPaIntensity, t.taskTzIntensity, tu.taskUomName, t.taskUnitPlan, " +
                                "t.taskUnitFact, t.taskOut, s.statusName " +
                                "FROM TaskEntity t " +
                                "join ProjectEntity p on p.projectId = t.projectId " +
                                "left join RequestEntity r on r.requestId = t.requestId " +
                                "join CustomerEntity cr on cr.customerId = p.customerId " +
                                "join ContractProjectEntity cp on cp.projectId = p.projectId " +
                                "join ContractEntity c on c.contractId = cp.contractId " +
                                "join StatusEntity s on s.statusId = t.statusId " +
                                "join StatusTypeEntity st on st.statusTypeId = s.statusType " +
                                "left join TaskUomEntity tu on tu.taskUomId = t.taskUomId " +
                                "WHERE s.statusName = '" + status + "' " +
                                "ORDER BY cr.customerName, c.contractNumber, p.projectName, t.taskNumber", Object[].class)
                .getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("taskId");
        columns.add("customerName");
        columns.add("contractNumber");
        columns.add("projectName");
        columns.add("requestNumber");
        columns.add("taskNumber");
        columns.add("taskName");
        columns.add("taskIncomeDate");
        columns.add("taskPaIntensity");
        columns.add("taskTzIntensity");
        columns.add("taskUomName");
        columns.add("taskUnitPlan");
        columns.add("taskUnitFact");
        columns.add("taskOut");
        columns.add("statusName");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    @Override
    public List<Object[]> genMyTasksPM(String status, String userFullname) {
        List<Object[]> query =   entityManager.createQuery(
                        "SELECT t.taskId, cr.customerName, c.contractNumber, p.projectName, " +
                                "r.requestNumber, t.taskNumber, t.taskName, t.taskIncomeDate, " +
                                "t.taskPaIntensity, t.taskTzIntensity, tu.taskUomName, t.taskUnitPlan, " +
                                "t.taskUnitFact, t.taskOut, s.statusName " +
                                "FROM TaskEntity t " +
                                "join ProjectEntity p on p.projectId = t.projectId " +
                                "left join RequestEntity r on r.requestId = t.requestId " +
                                "join CustomerEntity cr on cr.customerId = p.customerId " +
                                "join ContractProjectEntity cp on cp.projectId = p.projectId " +
                                "join ContractEntity c on c.contractId = cp.contractId " +
                                "join StatusEntity s on s.statusId = t.statusId " +
                                "left join ProjectManagerEntity pm on pm.projectId = p.projectId " +
                                "left join UserEntity u on u.userId = pm.userId " +
                                "left join TaskExecutorEntity te on te.taskId = t.taskId " +
                                "left join UserEntity ute on ute.userId = te.userId " +
                                "left join TaskUomEntity tu on tu.taskUomId = t.taskUomId " +
                                "WHERE (u.userId = (SELECT userId FROM UserEntity WHERE userFullname = '" + userFullname +
                                "') OR ute.userId = (SELECT userId FROM UserEntity WHERE userFullname = '" + userFullname +
                                "') OR ute.userId IN (SELECT us_su.userSubId FROM UserSubordinationEntity us_su " +
                                "join UserEntity usr on usr.userId = us_su.userId WHERE usr.userFullname = '" +
                                userFullname + "')) AND s.statusName = '" + status + "' " +
                                "GROUP BY t.taskId, cr.customerName, c.contractNumber, p.projectName, r.requestNumber, " +
                                "t.taskNumber, t.taskName, t.taskIncomeDate, t.taskPaIntensity, t.taskTzIntensity, " +
                                "tu.taskUomName, t.taskUnitPlan, t.taskUnitFact, t.taskOut, s.statusName " +
                                "ORDER BY cr.customerName, c.contractNumber, p.projectName, t.taskNumber", Object[].class)
                .getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("taskId");
        columns.add("customerName");
        columns.add("contractNumber");
        columns.add("projectName");
        columns.add("requestNumber");
        columns.add("taskNumber");
        columns.add("taskName");
        columns.add("taskIncomeDate");
        columns.add("taskPaIntensity");
        columns.add("taskTzIntensity");
        columns.add("taskUomName");
        columns.add("taskUnitPlan");
        columns.add("taskUnitFact");
        columns.add("taskOut");
        columns.add("statusName");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    @Override
    public List<Object[]> genMyTasksByUser(String status, String userFullname) {
        List<Object[]> query =   entityManager.createQuery(
                        "SELECT t.taskId, cr.customerName, t.taskNumber, t.taskName, t.taskPaIntensity, " +
                                "tu.taskUomName, t.taskUnitPlan, t.taskIncomeDate, s.statusName " +
                                "FROM TaskEntity t " +
                                "join ProjectEntity p on p.projectId = t.projectId " +
                                "left join RequestEntity r on r.requestId = t.requestId " +
                                "join CustomerEntity cr on cr.customerId = p.customerId " +
                                "join ContractProjectEntity cp on cp.projectId = p.projectId " +
                                "join ContractEntity c on c.contractId = cp.contractId " +
                                "join StatusEntity s on s.statusId = t.statusId " +
                                "join StatusTypeEntity st on st.statusTypeId = s.statusType " +
                                "join TaskExecutorEntity te on te.taskId = t.taskId " +
                                "join UserEntity u on u.userId = te.userId " +
                                "left join TaskUomEntity tu on tu.taskUomId = t.taskUomId " +
                                "WHERE u.userId = (SELECT userId FROM UserEntity WHERE userFullname = '" + userFullname + "') " +
                                "AND s.statusName = '" + status + "' " +
                                "ORDER BY cr.customerName, c.contractNumber, p.projectName, t.taskNumber", Object[].class)
                .getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("taskId");
        columns.add("customerName");
        columns.add("taskNumber");
        columns.add("taskName");
        columns.add("taskPaIntensity");
        columns.add("taskUomName");
        columns.add("taskUnitPlan");
        columns.add("taskIncomeDate");
        columns.add("statusName");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }
}
