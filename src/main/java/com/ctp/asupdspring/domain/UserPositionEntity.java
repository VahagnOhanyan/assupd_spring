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
 * todo Document type UserPositionEntity
 */
@Getter
@Setter
@Entity
@Table(name = "user_position", schema = "public", catalog = "pm_db_alpha")
public class UserPositionEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_position_id")
    private int userPositionId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userId;
    @ManyToOne
    @JoinColumn(name = "position_id", referencedColumnName = "position_id", nullable = false)
    private PositionEntity positionId;
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
        UserPositionEntity that = (UserPositionEntity) o;
        return userPositionId == that.userPositionId && userId == that.userId && positionId == that.positionId && statusId == that.statusId &&
                Objects.equals(activeFrom, that.activeFrom) && Objects.equals(activeTo, that.activeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPositionId, userId, positionId, statusId, activeFrom, activeTo);
    }
}
