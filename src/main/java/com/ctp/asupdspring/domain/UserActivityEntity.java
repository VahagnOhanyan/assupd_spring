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
 * todo Document type UserActivityEntity
 */
@Getter
@Setter
@Entity
@Table(name = "user_activity", schema = "public", catalog = "pm_db_alpha")
public class UserActivityEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_activity_id")
    private int userActivityId;
    @Basic
    @Column(name = "user_activity_name")
    private String userActivityName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserActivityEntity that = (UserActivityEntity) o;
        return userActivityId == that.userActivityId && Objects.equals(userActivityName, that.userActivityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userActivityId, userActivityName);
    }
}
