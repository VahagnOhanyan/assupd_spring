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
 * todo Document type UserRoleEntity
 */
@Getter
@Setter
@Entity
@Table(name = "user_role", schema = "public", catalog = "pm_db_alpha")
public class UserRoleEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_role_id")
    private int userRoleId;
    @Basic
    @Column(name = "user_role_name")
    private String userRoleName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserRoleEntity that = (UserRoleEntity) o;
        return userRoleId == that.userRoleId && Objects.equals(userRoleName, that.userRoleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userRoleId, userRoleName);
    }
}
