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
@Component("requestRepositoryImpl")
public class RequestRepositoryImpl implements RequestRepositoryCustom {

    private final EntityManager entityManager;

    public RequestRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Object[]> getMyRequestsAll() {
        List<Object[]> query = entityManager.createNativeQuery(
                        "SELECT r.request_id, cr.customer_name, ct.contract_number, r.request_number, r.request_description, " +
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
                                "ORDER BY cr.customer_name, ct.contract_number, r.request_number")
                .getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("requestId");
        columns.add("customerName");
        columns.add("contractNumber");
        columns.add("requestNumber");
        columns.add("requestDescription");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    @Override
    public List<Object[]> getMyRequestsPM(String userFullname) {
        List<Object[]> query = entityManager.createNativeQuery(
                        "SELECT r.request_id, cr.customer_name, ct.contract_number, r.request_number, r.request_description, " +
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
                                "WHERE u.user_fullname = '" + userFullname + "' " +
                                "GROUP BY r.request_id, cr.customer_name, ct.contract_number, r.request_number, r.request_description, ra.mp_create, ra.aup_approve, ra.customer_approve " +
                                "ORDER BY cr.customer_name, ct.contract_number, r.request_number")
                .getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("requestId");
        columns.add("customerName");
        columns.add("contractNumber");
        columns.add("requestNumber");
        columns.add("requestDescription");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    public List<Object[]> getContracts(String customerId) {
        List<Object[]> query = entityManager.createNativeQuery("SELECT ct.contract_id, ct.contract_number FROM public.contract ct " +
                "join public.customer cr on cr.customer_id = ct.customer_id AND cr.customer_id = " +
                "'" + customerId + "' " +
                "ORDER BY ct.contract_id").getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("customer_id");
        columns.add("contract_number");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    public List<Object[]> getUserContracts(String customerId, String current) {
        List<Object[]> query = entityManager.createNativeQuery("SELECT ct.contract_id, ct.contract_number FROM public.contract ct " +
                "join public.customer cr on cr.customer_id = ct.customer_id AND cr.customer_id = " +
                "'" + customerId + "' " +
                "join public.contract_project cp on cp.contract_id = ct.contract_id " +
                "join public.project p on p.project_id = cp.project_id " +
                "join public.project_manager pm on pm.project_id = p.project_id " +
                "join public.user u on u.user_id = pm.user_id " +
                "WHERE u.user_fullname = '" + current + "' " +
                "GROUP BY ct.contract_id, ct.contract_number " +
                "ORDER BY ct.contract_id").getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("customer_id");
        columns.add("contract_number");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    public void addRequest(String requestNumber, String requestDesc, String contractId) {
        entityManager.createNativeQuery("INSERT INTO public.request (request_number, request_description, contract_id) VALUES " +
                "('" + requestNumber + "', " + requestDesc + "', " +
                "'" + contractId + "')").getResultList();
    }

    public void updateContract(String contractId, String requestId) {
        entityManager.createNativeQuery("UPDATE public.request SET contract_id = '" +
                contractId + "' " +
                "WHERE request_id = '" + requestId + "'").getResultList();
    }

    public void updateRequest(String requestNumber, String requestId) {
        entityManager.createNativeQuery("UPDATE public.request SET request_number = '" + requestNumber + "' " +
                "WHERE request_id = '" + requestId + "'").getResultList();

    }

    public void updateRequestDesc(String requestDesc, String requestId) {
        entityManager.createNativeQuery("UPDATE public.request SET request_description = '" + requestDesc + "' " +
                "WHERE request_id = '" + requestId + "'").getResultList();
    }

    public List<Object[]> getCustomers() {
        return entityManager.createNativeQuery("SELECT customer_id, customer_name FROM public.customer").getResultList();
       /* ArrayList<String> columns = new ArrayList<>();
        columns.add("customer_id");
        columns.add("customer_name");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;*/
    }

    public List<Object[]> getContractsBy(String requestId) {
        List<Object[]> query = entityManager.createNativeQuery("SELECT r.request_number, r.request_description, c.contract_id FROM public.request r " +
                "join public.contract c on c.contract_id = r.contract_id " +
                "WHERE r.request_id = '" + requestId + "'").getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("request_number");
        columns.add("request_description");
        columns.add("contract_id");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }
}
