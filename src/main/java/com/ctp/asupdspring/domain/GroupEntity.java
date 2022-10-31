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
 * todo Document type GroupEntity
 */
@Getter
@Setter
@Entity
@Table(name = "group", schema = "public", catalog = "pm_db_alpha")
public class GroupEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "group_id")
    private int groupId;
    @Basic
    @Column(name = "group_name")
    private String groupName;
    @ManyToOne
    @JoinColumn(name = "group_leader", referencedColumnName = "user_id", nullable = false)
    private UserEntity groupLeader;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupEntity that = (GroupEntity) o;
        return groupId == that.groupId && groupLeader == that.groupLeader && Objects.equals(groupName, that.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, groupName, groupLeader);
    }
}
