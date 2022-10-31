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
 * todo Document type RequestApproveHistoryEntity
 */
@Getter
@Setter
@Entity
@Table(name = "request_approve_history", schema = "public", catalog = "pm_db_alpha")
public class RequestApproveHistoryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "request_approve_history_id")
    private int requestApproveHistoryId;
    @Basic
    @Column(name = "edit_user")
    private String editUser;
    @ManyToOne
    @JoinColumn(name = "user_role_id", referencedColumnName = "user_role_id", nullable = false)
    private UserRoleEntity userRoleId;
    @Basic
    @Column(name = "decision")
    private String decision;
    @Basic
    @Column(name = "approver_role")
    private String approverRole;
    @Basic
    @Column(name = "approve_date")
    private Timestamp approveDate;
    @ManyToOne
    @JoinColumn(name = "request_approve_id", referencedColumnName = "request_approve_id", nullable = false)
    private RequestApproveEntity requestApproveId;
    @Basic
    @Column(name = "computer_name")
    private String computerName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestApproveHistoryEntity that = (RequestApproveHistoryEntity) o;
        return requestApproveHistoryId == that.requestApproveHistoryId && userRoleId == that.userRoleId && requestApproveId == that.requestApproveId &&
                Objects.equals(editUser, that.editUser) && Objects.equals(decision, that.decision) &&
                Objects.equals(approverRole, that.approverRole) && Objects.equals(approveDate, that.approveDate) &&
                Objects.equals(computerName, that.computerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestApproveHistoryId, editUser, userRoleId, decision, approverRole, approveDate, requestApproveId, computerName);
    }
}
