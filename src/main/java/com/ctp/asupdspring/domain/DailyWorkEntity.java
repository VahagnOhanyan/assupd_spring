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
 * todo Document type DailyWorkEntity
 */
@Getter
@Setter
@Entity
@Table(name = "daily_work", schema = "public", catalog = "pm_db_alpha")
public class DailyWorkEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "daily_work_id")
    private int dailyWorkId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userId;
    @Basic
    @Column(name = "daily_work_date")
    private Date dailyWorkDate;
    @Basic
    @Column(name = "daily_intensity")
    private Integer dailyIntensity;
    @Basic
    @Column(name = "daily_overtime")
    private BigInteger dailyOvertime;
    @Basic
    @Column(name = "day_num")
    private Integer dayNum;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DailyWorkEntity that = (DailyWorkEntity) o;
        return dailyWorkId == that.dailyWorkId && userId == that.userId && Objects.equals(dailyWorkDate, that.dailyWorkDate) &&
                Objects.equals(dailyIntensity, that.dailyIntensity) && Objects.equals(dailyOvertime, that.dailyOvertime) &&
                Objects.equals(dayNum, that.dayNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dailyWorkId, userId, dailyWorkDate, dailyIntensity, dailyOvertime, dayNum);
    }
}
