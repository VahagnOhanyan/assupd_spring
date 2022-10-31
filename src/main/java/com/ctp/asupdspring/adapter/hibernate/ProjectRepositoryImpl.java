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
@Component("projectRepositoryImpl")
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {

    private final EntityManager entityManager;

    public ProjectRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Object[]> getMyProjectsAll() {
        return entityManager.createQuery(
                        "SELECT p.projectId, cr.customerName, c.contractNumber, p.projectName " +
                                "FROM ProjectEntity p " +
                                "join ContractProjectEntity cp on cp.projectId = p.projectId " +
                                "join ContractEntity c on c.contractId = cp.contractId " +
                                "join CustomerEntity cr on cr.customerId = p.customerId " +
                                "ORDER BY cr.customerName, c.contractNumber, p.projectName", Object[].class)
                .getResultList();
    }

    @Override
    public List<Object[]> getMyProjectsPM(String userFullname) {
        return entityManager.createQuery(
                "SELECT p.projectId, cr.customerName, c.contractNumber, p.projectName " +
                        "FROM ProjectEntity p " +
                        "join ContractProjectEntity cp on cp.projectId = p.projectId " +
                        "join ContractEntity c on c.contractId = cp.contractId " +
                        "join CustomerEntity cr on cr.customerId = p.customerId " +
                        "join ProjectManagerEntity pm on pm.projectId = p.projectId " +
                        "join UserEntity u on u.userId = pm.userId " +
                        "WHERE u.userId = (SELECT userId FROM UserEntity WHERE userFullname = '" + userFullname + "') " +
                        "ORDER BY cr.customerName, c.contractNumber, p.projectName " +
                        "ORDER BY cr.customerName, c.contractNumber, p.projectName", Object[].class).getResultList();
    }

    @Override
    public void addProject(String projectName, String customerId) {
        entityManager.createNativeQuery("INSERT INTO public.project (project_name, customer_id, status_id) VALUES " +
                "('" + projectName + "', '" + customerId + "', " +
                "(SELECT status_id FROM public.status s " +
                "join public.status_type st on st.status_type_id = s.status_type " +
                "WHERE st.status_type_name = 'projects' AND s.status_name = 'new'))");
    }

    @Override
    public void addContractProject(String contractId) {
        entityManager.createNativeQuery("INSERT INTO public.contract_project (contract_id, project_id, status_id, active_from, active_to) VALUES " +
                "('" + contractId + "', " +
                "(SELECT max(project_id) FROM public.project), " +
                "(SELECT status_id FROM public.status s " +
                "join public.status_type st on st.status_type_id = s.status_type " +
                "WHERE st.status_type_name = 'links' AND s.status_name = 'linked'), current_timestamp, '2099-10-11 00:00:00')");
    }

    @Override
    public void updateCustomerContractProject(String contractId, String projectId, String projectName, String customerId) {
        entityManager.createNativeQuery("UPDATE public.contract_project SET contract_id = '" + contractId + "' " +
                "WHERE project_id = '" + projectId + "'");
        entityManager.createNativeQuery("UPDATE public.project SET project_name = '" + projectName + "' " +
                "WHERE project_id = '" + projectId + "'");
        entityManager.createNativeQuery("UPDATE public.project SET customer_id = '" + customerId + "' " +
                "WHERE project_id = '" + projectId + "'");
    }

    @Override
    public void addProjectManager(String userFullname) {
        entityManager.createNativeQuery("INSERT INTO public.project_manager (project_id, user_id) VALUES " +
                "((SELECT max(project_id) FROM public.project), " +
                "(SELECT user_id FROM public.user " +
                "WHERE user_fullname = '" + userFullname + "'))");
    }

    @Override
    public List<Object[]> getCustomerContractProjectBy(String projectId) {
        List<Object[]> query = entityManager.createNativeQuery(
                "SELECT cr.customer_id, cr.customer_name, c.contract_id, c.contract_number, p.project_name from public.project p " +
                        "join public.contract_project cp on cp.project_id = p.project_id " +
                        "join public.contract c on c.contract_id = cp.contract_id " +
                        "join public.customer cr on cr.customer_id = c.customer_id " +
                        "WHERE p.project_id = '" + projectId + "'", Object[].class).getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("customer_id");
        columns.add("customer_name");
        columns.add("contract_id");
        columns.add("contract_number");
        columns.add("project_name");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }
    @Override
    public List<Object[]> getContracts(String customerId) {
        return entityManager.createNativeQuery("SELECT ct.contract_id, ct.contract_number FROM public.contract ct " +
                "join public.customer cr on cr.customer_id = ct.customer_id AND cr.customer_id = " +
                "'" + customerId + "'").getResultList();
        /*ArrayList<String> columns = new ArrayList<>();
        columns.add("customer_id");
        columns.add("contract_number");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;*/
    }
}










