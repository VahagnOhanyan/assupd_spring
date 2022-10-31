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
 * todo Document type StageDailyEntity
 */
@Getter
@Setter
@Entity
@Table(name = "stage_daily", schema = "public", catalog = "pm_db_alpha")
public class StageDailyEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "stage_daily_id")
    private int stageDailyId;
    @ManyToOne
    @JoinColumn(name = "stage_id", referencedColumnName = "stage_id", nullable = false)
    private StageEntity stageId;
    @ManyToOne
    @JoinColumn(name = "daily_work_id", referencedColumnName = "daily_work_id", nullable = false)
    private DailyWorkEntity dailyWorkId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StageDailyEntity that = (StageDailyEntity) o;
        return stageDailyId == that.stageDailyId && stageId == that.stageId && dailyWorkId == that.dailyWorkId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stageDailyId, stageId, dailyWorkId);
    }
}
