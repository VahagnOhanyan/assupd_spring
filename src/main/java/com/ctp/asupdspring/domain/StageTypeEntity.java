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
 * todo Document type StageTypeEntity
 */
@Getter
@Setter
@Entity
@Table(name = "stage_type", schema = "public", catalog = "pm_db_alpha")
public class StageTypeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "stage_type_id")
    private int stageTypeId;
    @Basic
    @Column(name = "stage_type_name")
    private String stageTypeName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StageTypeEntity that = (StageTypeEntity) o;
        return stageTypeId == that.stageTypeId && Objects.equals(stageTypeName, that.stageTypeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stageTypeId, stageTypeName);
    }

}
