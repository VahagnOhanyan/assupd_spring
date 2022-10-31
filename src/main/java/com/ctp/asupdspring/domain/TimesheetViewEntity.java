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
 * todo Document type TimesheetViewEntity
 */
@Getter
@Setter
@Entity
@Table(name = "timesheet_view", schema = "public", catalog = "pm_db_alpha")
public class TimesheetViewEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "timesheet_view_id")
    private int timesheetViewId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userId;
    @Basic
    @Column(name = "edit_user")
    private String editUser;
    @Basic
    @Column(name = "edit_start")
    private Timestamp editStart;
    @Basic
    @Column(name = "edit_end")
    private Timestamp editEnd;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimesheetViewEntity that = (TimesheetViewEntity) o;
        return timesheetViewId == that.timesheetViewId && userId == that.userId && Objects.equals(editUser, that.editUser) &&
                Objects.equals(editStart, that.editStart) && Objects.equals(editEnd, that.editEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timesheetViewId, userId, editUser, editStart, editEnd);
    }
}
