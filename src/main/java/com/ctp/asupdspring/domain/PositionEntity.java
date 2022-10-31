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
 * todo Document type PositionEntity
 */
@Getter
@Setter
@Entity
@Table(name = "position", schema = "public", catalog = "pm_db_alpha")
public class PositionEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "position_id")
    private int positionId;
    @Basic
    @Column(name = "position_name")
    private String positionName;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PositionEntity that = (PositionEntity) o;
        return positionId == that.positionId && Objects.equals(positionName, that.positionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionId, positionName);
    }

}
