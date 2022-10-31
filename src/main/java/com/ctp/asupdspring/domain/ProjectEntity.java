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
 * todo Document type ProjectEntity
 */
@Getter
@Setter
@Entity
@Table(name = "project", schema = "public", catalog = "pm_db_alpha")
public class ProjectEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "project_id")
    private int projectId;
    @Basic
    @Column(name = "project_name")
    private String projectName;
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private CustomerEntity customerId;
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    private StatusEntity statusId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectEntity that = (ProjectEntity) o;
        return projectId == that.projectId && Objects.equals(projectName, that.projectName) && Objects.equals(customerId, that.customerId) &&
                Objects.equals(statusId, that.statusId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, projectName, customerId, statusId);
    }
}
