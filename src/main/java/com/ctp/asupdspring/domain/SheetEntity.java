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
 * todo Document type SheetEntity
 */
@Getter
@Setter
@Entity
@Table(name = "sheet", schema = "public", catalog = "pm_db_alpha")
public class SheetEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "sheet_id")
    private int sheetId;
    @Basic
    @Column(name = "sheet_name")
    private String sheetName;
    @Basic
    @Column(name = "sheet_works_count")
    private int sheetWorksCount;
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "status_id", nullable = false)
    private StatusEntity statusId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SheetEntity that = (SheetEntity) o;
        return sheetId == that.sheetId && sheetWorksCount == that.sheetWorksCount && statusId == that.statusId &&
                Objects.equals(sheetName, that.sheetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetId, sheetName, sheetWorksCount, statusId);
    }
}
