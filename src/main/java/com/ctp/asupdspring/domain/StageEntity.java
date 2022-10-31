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
 * todo Document type StageEntity
 */
@Getter
@Setter
@Entity
@Table(name = "stage", schema = "public", catalog = "pm_db_alpha")
public class StageEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "stage_id")
    private int stageId;
    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "task_id", nullable = false)
    private TaskEntity taskId;
    @ManyToOne
    @JoinColumn(name = "stage_type_id", referencedColumnName = "stage_type_id", nullable = false)
    private StageTypeEntity stageTypeId;
    @Basic
    @Column(name = "stage_note")
    private String stageNote;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StageEntity that = (StageEntity) o;
        return stageId == that.stageId && taskId == that.taskId && stageTypeId == that.stageTypeId && Objects.equals(stageNote, that.stageNote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stageId, taskId, stageTypeId, stageNote);
    }
}
