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
 * todo Document type TaskUomEntity
 */
@Getter
@Setter
@Entity
@Table(name = "task_uom", schema = "public", catalog = "pm_db_alpha")
public class TaskUomEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "task_uom_id")
    private int taskUomId;
    @Basic
    @Column(name = "task_uom_name")
    private String taskUomName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskUomEntity that = (TaskUomEntity) o;
        return taskUomId == that.taskUomId && Objects.equals(taskUomName, that.taskUomName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskUomId, taskUomName);
    }
}
