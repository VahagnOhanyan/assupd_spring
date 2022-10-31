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
 * todo Document type UserStageDailyEntity
 */
@Getter
@Setter
@Entity
@Table(name = "user_stage_daily", schema = "public", catalog = "pm_db_alpha")
public class UserStageDailyEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_stage_daily")
    private int userStageDaily;
    @ManyToOne
    @JoinColumn(name = "daily_work_id", referencedColumnName = "daily_work_id", nullable = false)
    private DailyWorkEntity dailyWorkId;
    @ManyToOne
    @JoinColumn(name = "user_stage_id", referencedColumnName = "user_stage_id", nullable = false)
    private UserStageEntity userStageId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserStageDailyEntity that = (UserStageDailyEntity) o;
        return userStageDaily == that.userStageDaily && dailyWorkId == that.dailyWorkId && userStageId == that.userStageId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userStageDaily, dailyWorkId, userStageId);
    }
}
