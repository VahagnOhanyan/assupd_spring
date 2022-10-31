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
 * todo Document type DivisionEntity
 */
@Getter
@Setter
@Entity
@Table(name = "division", schema = "public", catalog = "pm_db_alpha")
public class DivisionEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "division_id")
    private int divisionId;
    @Basic
    @Column(name = "division_name")
    private String divisionName;
    @ManyToOne
    @JoinColumn(name = "division_head", referencedColumnName = "user_id", nullable = false)
    private UserEntity divisionHead;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DivisionEntity that = (DivisionEntity) o;
        return divisionId == that.divisionId && divisionHead == that.divisionHead && Objects.equals(divisionName, that.divisionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(divisionId, divisionName, divisionHead);
    }
}
