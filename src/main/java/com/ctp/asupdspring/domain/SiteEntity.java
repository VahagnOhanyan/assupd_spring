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
package com.ctp.asupdspring.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/**
 * todo Document type SiteEntity
 */
@Getter
@Setter
@Entity
@Table(name = "site", schema = "public", catalog = "pm_db_alpha")
public class SiteEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "site_id")
    private int siteId;
    @Basic
    @Column(name = "site_name")
    private String siteName;
    @Basic
    @Column(name = "site_code")
    private String siteCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SiteEntity that = (SiteEntity) o;
        return siteId == that.siteId && Objects.equals(siteName, that.siteName) && Objects.equals(siteCode, that.siteCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(siteId, siteName, siteCode);
    }
}
