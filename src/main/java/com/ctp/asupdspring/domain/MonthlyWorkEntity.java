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
import java.util.Objects;

/**
 * todo Document type MonthlyWorkEntity
 */
@Getter
@Setter
@Entity
@Table(name = "monthly_work", schema = "public", catalog = "pm_db_alpha")
public class MonthlyWorkEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "monthly_work_id")
    private int monthlyWorkId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userId;
    @Basic
    @Column(name = "year")
    private String year;
    @Basic
    @Column(name = "month")
    private int month;
    @Basic
    @Column(name = "monthly_intensity")
    private BigInteger monthlyIntensity;
    @Basic
    @Column(name = "monthly_overtime")
    private BigInteger monthlyOvertime;
    @Basic
    @Column(name = "monthly_sum")
    private BigInteger monthlySum;
    @Basic
    @Column(name = "note")
    private String note;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MonthlyWorkEntity that = (MonthlyWorkEntity) o;
        return monthlyWorkId == that.monthlyWorkId && userId == that.userId && month == that.month && Objects.equals(year, that.year) &&
                Objects.equals(monthlyIntensity, that.monthlyIntensity) && Objects.equals(monthlyOvertime, that.monthlyOvertime) &&
                Objects.equals(monthlySum, that.monthlySum) && Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monthlyWorkId, userId, year, month, monthlyIntensity, monthlyOvertime, monthlySum, note);
    }
}
