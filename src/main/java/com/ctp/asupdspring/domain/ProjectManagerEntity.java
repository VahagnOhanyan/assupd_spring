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
 * todo Document type ProjectManagerEntity
 */
@Getter
@Setter
@Entity
@Table(name = "project_manager", schema = "public", catalog = "pm_db_alpha")
public class ProjectManagerEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "project_manager_id")
    private int projectManagerId;
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private ProjectEntity projectId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectManagerEntity that = (ProjectManagerEntity) o;
        return projectManagerId == that.projectManagerId && projectId == that.projectId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectManagerId, projectId, userId);
    }
}
