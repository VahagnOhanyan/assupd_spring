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
import java.util.List;

/**
 * todo Document type ContractRepositoryImpl
 */
@Component("contractRepositoryImpl")
public class ContractRepositoryImpl implements ContractRepositoryCustom {

    private final EntityManager entityManager;

    public ContractRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<String> getReportByContractNumber(String contractNumber) {
        return entityManager.createQuery(
                        "select cus.customerName " +
                                "from TaskEntity t " +
                                "join StageEntity st on st.taskId.taskId = t.taskId " +
                                "left join TaskUomEntity tuo on tuo.taskUomId = t.taskUomId.taskUomId " +
                                "join ProjectEntity p on p.projectId = t.projectId.projectId " +
                                "join ContractProjectEntity cp on cp.projectId.projectId = p.projectId " +
                                "join ContractEntity c on c.contractId = cp.contractId.contractId " +
                                "join StatusEntity sts on sts.statusId = t.statusId.statusId " +
                                "join StageDailyEntity sd on sd.stageId.stageId = st.stageId " +
                                "join DailyWorkEntity dw on dw.dailyWorkId = sd.dailyWorkId.dailyWorkId " +
                                "left join RequestEntity r on r.requestId = t.requestId.requestId " +
                                "join CustomerEntity cus on cus.customerId = c.customerId.customerId where c.contractNumber = '" + contractNumber + "'", String.class)
                .getResultList();
    }

    @Override
    public List<Object[]> getMyContractsAll() {
        return entityManager.createQuery(
                        "SELECT ct.contractId, cr.customerName, ct.contractNumber, ct.contractName " +
                                "FROM ContractEntity ct " +
                                "join CustomerEntity cr on cr.customerId = ct.customerId " +
                                "ORDER BY cr.customerName, ct.contractNumber", Object[].class)
                .getResultList();
    }

    @Override
    public List<Object[]> getMyContractsPM(String userFullname) {
        return entityManager.createQuery(
                        "SELECT ct.contractId, cr.customerName, ct.contractNumber, ct.contractName " +
                                "FROM ContractEntity ct " +
                                "join CustomerEntity cr on cr.customerId = ct.customerId " +
                                "join ContractProjectEntity cp on cp.contractId = ct.contractId " +
                                "join ProjectEntity p on p.projectId = cp.projectId " +
                                "join ProjectManagerEntity pm on pm.projectId = p.projectId " +
                                "join UserEntity u on u.userId = pm.userId " +
                                "WHERE u.userFullname = '" + userFullname + "' " +
                                "GROUP BY ct.contractId, cr.customerName, ct.contractNumber, ct.contractName " +
                                "ORDER BY cr.customerName, ct.contractNumber", Object[].class)
                .getResultList();
    }

    public List<Object[]> getContractsCustomers(String period) {
        return entityManager.createNativeQuery("SELECT c.contract_number, cr.customer_name FROM public.contract c " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "join public.contract_project cp on cp.contract_id = c.contract_id " +
                "join public.project p on p.project_id = cp.project_id " +
                "join public.task t on t.project_id = p.project_id " +
                "join public.stage s on s.task_id = t.task_id " +
                "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "WHERE " + period + " AND " +
                "u.user_id IN (SELECT user_id FROM public.user) " +
                "GROUP BY c.contract_number, cr.customer_name HAVING count(c.contract_id)>=1 " +
                "ORDER BY cr.customer_name, c.contract_number").getResultList();
    }
    public List<Object[]> getCustomersContractsProjects(String period) {
        return entityManager.createNativeQuery("SELECT cr.customer_name, c.contract_number, p.project_name, p.project_id  FROM public.contract c " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "join public.contract_project cp on cp.contract_id = c.contract_id " +
                "join public.project p on p.project_id = cp.project_id " +
                "join public.task t on t.project_id = p.project_id " +
                "join public.stage s on s.task_id = t.task_id " +
                "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "WHERE " + period + " AND " +
                "u.user_id IN (SELECT user_id FROM public.user) " +
                "GROUP BY c.contract_number, cr.customer_name, p.project_name, p.project_id HAVING count(p.project_id)>=1 " +
                "ORDER BY cr.customer_name, c.contract_number").getResultList();
    }

    public List<Object[]> getCustomersContractsRequests(String period) {
        return entityManager.createNativeQuery("SELECT cr.customer_name, c.contract_number, r.request_number, r.request_id, " +
                "CASE WHEN r.request_number ~ E'^\\\\d+$' THEN CAST (r.request_number AS INTEGER) " +
                "ELSE " +
                "0 " +
                "END as sort FROM public.request r " +
                "join public.contract c on c.contract_id = r.contract_id " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "join public.task t on t.request_id = r.request_id " +
                "join public.stage s on s.task_id = t.task_id " +
                "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "WHERE " + period + " AND " +
                "u.user_id IN (SELECT user_id FROM public.user) " +
                "GROUP BY c.contract_number, cr.customer_name, r.request_number, r.request_id HAVING count(r.request_id)>=1 " +
                "ORDER BY cr.customer_name, c.contract_number, sort, r.request_number").getResultList();
    }


    public List<Object[]> getUserContractsCustomers(String period, String userFullname) {
        return entityManager.createNativeQuery("SELECT c.contract_number, cr.customer_name FROM public.contract c " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "join public.contract_project cp on cp.contract_id = c.contract_id " +
                "join public.project p on p.project_id = cp.project_id " +
                "join public.task t on t.project_id = p.project_id " +
                "join public.project_manager pm on pm.project_id = p.project_id " +
                "join public.stage s on s.task_id = t.task_id " +
                "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "WHERE " + period + " AND " +
                "u.user_id IN (SELECT user_id FROM public.user) AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" +
                userFullname + "') " +
                "GROUP BY c.contract_number, cr.customer_name HAVING count(c.contract_id)>=1 " +
                "ORDER BY cr.customer_name, c.contract_number").getResultList();
    }

    public List<Object[]> getUserCustomersContractsProjects(String period, String userFullname) {
        return entityManager.createNativeQuery("SELECT cr.customer_name, c.contract_number, p.project_name, p.project_id  FROM public.contract c " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "join public.contract_project cp on cp.contract_id = c.contract_id " +
                "join public.project p on p.project_id = cp.project_id " +
                "join public.project_manager pm on pm.project_id = p.project_id " +
                "join public.task t on t.project_id = p.project_id " +
                "join public.stage s on s.task_id = t.task_id " +
                "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "WHERE " + period + " AND " +
                "u.user_id IN (SELECT user_id FROM public.user) AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" +
                userFullname + "') " +
                "GROUP BY c.contract_number, cr.customer_name, p.project_name, p.project_id HAVING count(p.project_id)>=1 " +
                "ORDER BY cr.customer_name, c.contract_number").getResultList();
    }

    public List<Object[]> getUserCustomersContractsRequests(String period, String userFullname) {
        return entityManager.createNativeQuery("SELECT cr.customer_name, c.contract_number, r.request_number, r.request_id, " +
                "CASE WHEN r.request_number ~ E'^\\\\d+$' THEN CAST (r.request_number AS INTEGER) " +
                "ELSE " +
                "0 " +
                "END as sort FROM public.request r " +
                "join public.contract c on c.contract_id = r.contract_id " +
                "join public.contract_project cp on cp.contract_id = c.contract_id " +
                "join public.project p on p.project_id = cp.project_id " +
                "join public.project_manager pm on pm.project_id = p.project_id " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "join public.task t on t.request_id = r.request_id " +
                "join public.stage s on s.task_id = t.task_id " +
                "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "WHERE " + period + " AND " +
                "u.user_id IN (SELECT user_id FROM public.user) AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" +
                userFullname + "') " +
                "GROUP BY c.contract_number, cr.customer_name, r.request_number, r.request_id HAVING count(r.request_id)>=1 " +
                "ORDER BY cr.customer_name, c.contract_number, sort, r.request_number").getResultList();
    }

    public List<Object[]> getContractsCustomersSubOrd(String period, String userFullname) {
        return entityManager.createNativeQuery("SELECT c.contract_number, cr.customer_name FROM public.contract c " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "join public.contract_project cp on cp.contract_id = c.contract_id " +
                "join public.project p on p.project_id = cp.project_id " +
                "join public.task t on t.project_id = p.project_id " +
                "join public.stage s on s.task_id = t.task_id " +
                "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                "WHERE " + period + " AND " +
                "(us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + userFullname +
                "') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + userFullname + "')) " +
                "GROUP BY c.contract_number, cr.customer_name HAVING count(c.contract_id)>=1 " +
                "ORDER BY cr.customer_name, c.contract_number").getResultList();
    }

    public List<Object[]> getCustomersContractsProjectsSubOrd(String period, String userFullname) {
        return entityManager.createNativeQuery("SELECT cr.customer_name, c.contract_number, p.project_name, p.project_id FROM public.contract c " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "join public.contract_project cp on cp.contract_id = c.contract_id " +
                "join public.project p on p.project_id = cp.project_id " +
                "join public.task t on t.project_id = p.project_id " +
                "join public.stage s on s.task_id = t.task_id " +
                "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                "WHERE " + period + " AND " +
                "(us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + userFullname +
                "') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + userFullname + "')) " +
                "GROUP BY c.contract_number, cr.customer_name, p.project_name, p.project_id HAVING count(p.project_id)>=1 " +
                "ORDER BY cr.customer_name, c.contract_number").getResultList();
    }

    public List<Object[]> getCustomersContractsRequestsSubOrd(String period, String userFullname) {
        return entityManager.createNativeQuery("SELECT cr.customer_name, c.contract_number, r.request_number, r.request_id, " +
                "CASE WHEN r.request_number ~ E'^\\\\d+$' THEN CAST (r.request_number AS INTEGER) " +
                "ELSE " +
                "0 " +
                "END as sort FROM public.request r " +
                "join public.contract c on c.contract_id = r.contract_id " +
                "join public.customer cr on cr.customer_id = c.customer_id " +
                "join public.task t on t.request_id = r.request_id " +
                "join public.stage s on s.task_id = t.task_id " +
                "join public.stage_daily sd on sd.stage_id = s.stage_id " +
                "join public.daily_work dw on dw.daily_work_id = sd.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                "WHERE " + period + " AND " +
                "(us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + userFullname +
                "') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + userFullname + "')) " +
                "GROUP BY c.contract_number, cr.customer_name, r.request_number, r.request_id HAVING count(r.request_id)>=1 " +
                "ORDER BY cr.customer_name, c.contract_number, sort, r.request_number").getResultList();
    }

}
