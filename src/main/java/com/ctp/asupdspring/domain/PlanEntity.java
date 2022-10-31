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
import java.math.BigInteger;
import java.sql.Date;
import java.util.Objects;

/**
 * todo Document type PlanEntity
 */
@Getter
@Setter
@Entity
@Table(name = "plan", schema = "public", catalog = "pm_db_alpha")
public class PlanEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "plan_id")
    private int planId;
    @Basic
    @Column(name = "plan_number")
    private String planNumber;
    @Basic
    @Column(name = "plan_name")
    private String planName;
    @Basic
    @Column(name = "plan_date_start")
    private Date planDateStart;
    @Basic
    @Column(name = "plan_date_end")
    private Date planDateEnd;
    @Basic
    @Column(name = "plan_intensity")
    private BigInteger planIntensity;
    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "task_id", nullable = false)
    private TaskEntity taskId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlanEntity that = (PlanEntity) o;
        return planId == that.planId && taskId == that.taskId && Objects.equals(planNumber, that.planNumber) &&
                Objects.equals(planName, that.planName) && Objects.equals(planDateStart, that.planDateStart) &&
                Objects.equals(planDateEnd, that.planDateEnd) && Objects.equals(planIntensity, that.planIntensity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planId, planNumber, planName, planDateStart, planDateEnd, planIntensity, taskId);
    }
}
