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
 * todo Document type TzEntity
 */
@Getter
@Setter
@Entity
@Table(name = "tz", schema = "public", catalog = "pm_db_alpha")
public class TzEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "tz_id")
    private int tzId;
    @Basic
    @Column(name = "tz_number")
    private String tzNumber;
    @Basic
    @Column(name = "tz_name")
    private String tzName;
    @Basic
    @Column(name = "tz_date_start")
    private Date tzDateStart;
    @Basic
    @Column(name = "tz_date_end")
    private Date tzDateEnd;
    @Basic
    @Column(name = "tz_intensity")
    private BigInteger tzIntensity;
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
        TzEntity tzEntity = (TzEntity) o;
        return tzId == tzEntity.tzId && taskId == tzEntity.taskId && Objects.equals(tzNumber, tzEntity.tzNumber) &&
                Objects.equals(tzName, tzEntity.tzName) && Objects.equals(tzDateStart, tzEntity.tzDateStart) &&
                Objects.equals(tzDateEnd, tzEntity.tzDateEnd) && Objects.equals(tzIntensity, tzEntity.tzIntensity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tzId, tzNumber, tzName, tzDateStart, tzDateEnd, tzIntensity, taskId);
    }
}
