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
 * todo Document type SiteRepositoryCustomImpl
 */
@Component("siteRepositoryImpl")
public class SiteRepositoryImpl implements SiteRepositoryCustom {
    private final EntityManager entityManager;

    public SiteRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Object[]> getAllSites() {
        return entityManager.createQuery("SELECT s.siteId, s.siteName, s.siteCode FROM SiteEntity s ORDER BY siteId", Object[].class).getResultList();
    }
}
