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
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * todo Document type UserInfoEntity
 */
@Getter
@Setter
@Entity
@RequiredArgsConstructor
@Table(name = "user_info", schema = "public", catalog = "pm_db_alpha")
public class UserInfoEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_info_id")
    private int userInfoId;
    @Basic
    @Column(name = "user_login")
    private String userLogin;
    @Basic
    @Column(name = "user_pass")
    private String userPass;
    @ManyToOne
    @JoinColumn(name = "user_role_id", referencedColumnName = "user_role_id")
    private UserRoleEntity userRoleId;
    @ManyToMany
    @JoinTable(name = "user_privileges",
            joinColumns = @JoinColumn(name = "user_info_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    private Set<PrivilegeEntity> privileges;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserInfoEntity that = (UserInfoEntity) o;
        return userInfoId == that.userInfoId && Objects.equals(userLogin, that.userLogin) && Objects.equals(userPass, that.userPass) &&
                Objects.equals(userRoleId, that.userRoleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userInfoId, userLogin, userPass, userRoleId);
    }
}
