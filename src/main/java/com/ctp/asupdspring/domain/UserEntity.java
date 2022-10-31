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
 * todo Document type UserEntity
 */
@Getter
@Setter
@Entity
@Table(name = "user", schema = "public", catalog = "pm_db_alpha")
public class UserEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id")
    private int userId;
    @Basic
    @Column(name = "user_id_number")
    private String userIdNumber;
    @Basic
    @Column(name = "user_surname")
    private String userSurname;
    @Basic
    @Column(name = "user_name")
    private String userName;
    @Basic
    @Column(name = "user_midname")
    private String userMidname;
    @Basic
    @Column(name = "user_fullname")
    private String userFullname;
    @Basic
    @Column(name = "user_tel")
    private String userTel;
    @Basic
    @Column(name = "user_adress")
    private String userAdress;
    @Basic
    @Column(name = "user_email")
    private String userEmail;
    @ManyToOne
    @JoinColumn(name = "user_info_id", referencedColumnName = "user_info_id")
    private UserInfoEntity userInfoId;
    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "site_id")
    private SiteEntity siteId;
    @Basic
    @Column(name = "user_activity_id")
    private Integer userActivityId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserEntity that = (UserEntity) o;
        return userId == that.userId && Objects.equals(userIdNumber, that.userIdNumber) && Objects.equals(userSurname, that.userSurname) &&
                Objects.equals(userName, that.userName) && Objects.equals(userMidname, that.userMidname) &&
                Objects.equals(userFullname, that.userFullname) && Objects.equals(userTel, that.userTel) &&
                Objects.equals(userAdress, that.userAdress) && Objects.equals(userEmail, that.userEmail) &&
                Objects.equals(userInfoId, that.userInfoId) && Objects.equals(siteId, that.siteId) &&
                Objects.equals(userActivityId, that.userActivityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userIdNumber, userSurname, userName, userMidname, userFullname, userTel, userAdress, userEmail, userInfoId, siteId,
                userActivityId);
    }
}
