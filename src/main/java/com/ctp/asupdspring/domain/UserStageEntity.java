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
 * todo Document type UserStageEntity
 */
@Getter
@Setter
@Entity
@Table(name = "user_stage", schema = "public", catalog = "pm_db_alpha")
public class UserStageEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_stage_id")
    private int userStageId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userId;
    @ManyToOne
    @JoinColumn(name = "user_stage_type_id", referencedColumnName = "user_stage_type_id", nullable = false)
    private UserStageTypeEntity userStageTypeId;
    @Basic
    @Column(name = "user_stage_note")
    private String userStageNote;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserStageEntity that = (UserStageEntity) o;
        return userStageId == that.userStageId && userId == that.userId && userStageTypeId == that.userStageTypeId &&
                Objects.equals(userStageNote, that.userStageNote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userStageId, userId, userStageTypeId, userStageNote);
    }
}
