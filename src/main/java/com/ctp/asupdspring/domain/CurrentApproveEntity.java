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
 * todo Document type CurrentApproveEntity
 */
@Getter
@Setter
@Entity
@Table(name = "current_approve", schema = "public", catalog = "pm_db_alpha")
public class CurrentApproveEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "current_approve_id")
    private int currentApproveId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userId;
    @Basic
    @Column(name = "sheet_month")
    private String sheetMonth;
    @Basic
    @Column(name = "sheet_year")
    private String sheetYear;
    @Basic
    @Column(name = "lead_approve")
    private boolean leadApprove;
    @Basic
    @Column(name = "lead_fullname")
    private String leadFullname;
    @Basic
    @Column(name = "dep_head_approve")
    private boolean depHeadApprove;
    @Basic
    @Column(name = "dep_head_fullname")
    private String depHeadFullname;
    @Basic
    @Column(name = "manager_approve")
    private boolean managerApprove;
    @Basic
    @Column(name = "manager_fullname")
    private String managerFullname;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CurrentApproveEntity that = (CurrentApproveEntity) o;
        return currentApproveId == that.currentApproveId && userId == that.userId && leadApprove == that.leadApprove && depHeadApprove == that.depHeadApprove &&
                managerApprove == that.managerApprove && Objects.equals(sheetMonth, that.sheetMonth) &&
                Objects.equals(sheetYear, that.sheetYear) && Objects.equals(leadFullname, that.leadFullname) &&
                Objects.equals(depHeadFullname, that.depHeadFullname) && Objects.equals(managerFullname, that.managerFullname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentApproveId, userId, sheetMonth, sheetYear, leadApprove, leadFullname, depHeadApprove, depHeadFullname, managerApprove,
                managerFullname);
    }
}
