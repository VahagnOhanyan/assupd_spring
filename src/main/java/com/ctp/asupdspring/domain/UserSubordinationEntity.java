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
 * todo Document type UserSubordinationEntity
 */
@Getter
@Setter
@Entity
@Table(name = "user_subordination", schema = "public", catalog = "pm_db_alpha")
public class UserSubordinationEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_subordination_id")
    private int userSubordinationId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userId;
    @ManyToOne
    @JoinColumn(name = "user_sub_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userSubId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserSubordinationEntity that = (UserSubordinationEntity) o;
        return userSubordinationId == that.userSubordinationId && userId == that.userId && userSubId == that.userSubId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userSubordinationId, userId, userSubId);
    }
}
