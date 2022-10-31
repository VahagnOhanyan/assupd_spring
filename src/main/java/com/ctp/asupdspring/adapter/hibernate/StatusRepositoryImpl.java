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
@Component("statusRepositoryImpl")
public class StatusRepositoryImpl implements StatusRepositoryCustom {

    private final EntityManager entityManager;

    public StatusRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Object[]> getStatusByType(String type) {
        List<Object[]> query = entityManager.createQuery("SELECT s.statusName FROM StatusEntity s " +
                "join StatusTypeEntity st on st.statusTypeId = s.statusType " +
                "WHERE st.statusTypeName = '" + type + "'", Object[].class).getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("statusName");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }
}
