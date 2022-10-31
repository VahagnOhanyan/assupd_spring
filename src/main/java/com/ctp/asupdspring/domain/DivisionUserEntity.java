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
import java.sql.Timestamp;
import java.util.Objects;

/**
 * todo Document type DivisionUserEntity
 */
@Getter
@Setter
@Entity
@Table(name = "division_user", schema = "public", catalog = "pm_db_alpha")
public class DivisionUserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "division_user_id")
    private int divisionUserId;
    @ManyToOne
    @JoinColumn(name = "division_id", referencedColumnName = "division_id", nullable = false)
    private DivisionEntity divisionId;
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
        DivisionUserEntity that = (DivisionUserEntity) o;
        return divisionUserId == that.divisionUserId && divisionId == that.divisionId && userId == that.userId && statusId == that.statusId &&
                Objects.equals(activeFrom, that.activeFrom) && Objects.equals(activeTo, that.activeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(divisionUserId, divisionId, userId, statusId, activeFrom, activeTo);
    }
}
