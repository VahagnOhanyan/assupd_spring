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
 * todo Document type RequestApproveEntity
 */
@Getter
@Setter
@Entity
@Table(name = "request_approve", schema = "public", catalog = "pm_db_alpha")
public class RequestApproveEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "request_approve_id")
    private int requestApproveId;
    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "request_id", nullable = false)
    private RequestEntity requestId;
    @Basic
    @Column(name = "mp_create")
    private boolean mpCreate;
    @Basic
    @Column(name = "mp_fullname")
    private String mpFullname;
    @Basic
    @Column(name = "aup_approve")
    private boolean aupApprove;
    @Basic
    @Column(name = "aup_fullname")
    private String aupFullname;
    @Basic
    @Column(name = "customer_approve")
    private boolean customerApprove;
    @Basic
    @Column(name = "employee_fullname")
    private String employeeFullname;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestApproveEntity that = (RequestApproveEntity) o;
        return requestApproveId == that.requestApproveId && requestId == that.requestId && mpCreate == that.mpCreate && aupApprove == that.aupApprove &&
                customerApprove == that.customerApprove && Objects.equals(mpFullname, that.mpFullname) &&
                Objects.equals(aupFullname, that.aupFullname) && Objects.equals(employeeFullname, that.employeeFullname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestApproveId, requestId, mpCreate, mpFullname, aupApprove, aupFullname, customerApprove, employeeFullname);
    }
}
