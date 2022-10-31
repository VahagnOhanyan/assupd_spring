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
 * todo Document type RoleAccessEntity
 */
@Getter
@Setter
@Entity
@Table(name = "role_access", schema = "public", catalog = "pm_db_alpha")
public class RoleAccessEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "role_access_id")
    private int roleAccessId;
    @ManyToOne
    @JoinColumn(name = "user_role_id", referencedColumnName = "user_role_id", nullable = false)
    private UserRoleEntity userRoleId;
    @ManyToOne
    @JoinColumn(name = "module_id", referencedColumnName = "module_id", nullable = false)
    private ModuleEntity moduleId;

    @ManyToOne
    @JoinColumn(name = "access_type_id", referencedColumnName = "access_type_id", nullable = false)
    private AccessTypeEntity accessTypeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleAccessEntity that = (RoleAccessEntity) o;
        return roleAccessId == that.roleAccessId && userRoleId == that.userRoleId
                && moduleId == that.moduleId && accessTypeId == that.accessTypeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleAccessId, userRoleId, moduleId, accessTypeId);
    }
}
