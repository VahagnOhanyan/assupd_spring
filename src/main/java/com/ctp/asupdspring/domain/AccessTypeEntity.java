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
 * todo Document type AccessTypeEntity
 */
@Getter
@Setter
@Entity
@Table(name = "access_type", schema = "public", catalog = "pm_db_alpha")
public class AccessTypeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "access_type_id")
    private int accessTypeId;
    @Basic
    @Column(name = "access_type_name")
    private String accessTypeName;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccessTypeEntity that = (AccessTypeEntity) o;
        return accessTypeId == that.accessTypeId && Objects.equals(accessTypeName, that.accessTypeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessTypeId, accessTypeName);
    }

}
