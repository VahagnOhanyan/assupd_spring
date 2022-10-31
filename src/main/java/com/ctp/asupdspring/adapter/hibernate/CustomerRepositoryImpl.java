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
 * todo Document type CustomerRepositoryImpl
 */
@Component("customerRepositoryImpl")
public class CustomerRepositoryImpl implements CustomerRepositoryCustom {
    private final EntityManager entityManager;

    public CustomerRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Object[]> getAllCustomersNames() {
        List<Object[]> query =  entityManager.createQuery("SELECT customerName FROM CustomerEntity ORDER BY customerName", Object[].class).getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("customerName");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    @Override
    public List<Object[]> getCustomersWithContracts() {
        List<Object[]> query = entityManager.createQuery("SELECT cr.customerName, c.contractNumber FROM ContractEntity c " +
                "join CustomerEntity cr on cr.customerId = c.customerId " +
                "ORDER BY cr.customerName, c.contractNumber", Object[].class).getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("customerName");
        columns.add("contractNumber");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }
}
