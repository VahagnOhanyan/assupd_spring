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
 * todo Document type StatusEntity
 */
@Getter
@Setter
@Entity
@Table(name = "status", schema = "public", catalog = "pm_db_alpha")
public class StatusEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "status_id")
    private int statusId;
    @ManyToOne
    @JoinColumn(name = "status_type", referencedColumnName = "status_type_id", nullable = false)
    private StatusTypeEntity statusType;
    @Basic
    @Column(name = "status_name")
    private String statusName;
    @Basic
    @Column(name = "status_hierarchy")
    private Integer statusHierarchy;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatusEntity that = (StatusEntity) o;
        return statusId == that.statusId && statusType == that.statusType && Objects.equals(statusName, that.statusName) &&
                Objects.equals(statusHierarchy, that.statusHierarchy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusId, statusType, statusName, statusHierarchy);
    }
}
