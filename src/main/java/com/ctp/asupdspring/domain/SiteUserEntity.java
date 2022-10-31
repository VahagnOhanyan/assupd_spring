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

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * todo Document type SiteUserEntity
 */
@Entity
@Table(name = "site_user", schema = "public", catalog = "pm_db_alpha")
public class SiteUserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "site_user_id")
    private int siteUserId;
    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "site_id", nullable = false)
    private SiteEntity siteId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userId;
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "status_id", nullable = false)
    private StatusEntity statusId;
    @Basic
    @Column(name = "active_from")
    private Timestamp activeFrom;
    @Basic
    @Column(name = "active_to")
    private Timestamp activeTo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SiteUserEntity that = (SiteUserEntity) o;
        return siteUserId == that.siteUserId && siteId == that.siteId && userId == that.userId && statusId == that.statusId &&
                Objects.equals(activeFrom, that.activeFrom) && Objects.equals(activeTo, that.activeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(siteUserId, siteId, userId, statusId, activeFrom, activeTo);
    }
}
