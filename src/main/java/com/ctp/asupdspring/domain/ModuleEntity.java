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
 * todo Document type ModuleEntity
 */
@Getter
@Setter
@Entity
@Table(name = "module", schema = "public", catalog = "pm_db_alpha")
public class ModuleEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "module_id")
    private int moduleId;
    @Basic
    @Column(name = "module_name")
    private String moduleName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModuleEntity that = (ModuleEntity) o;
        return moduleId == that.moduleId && Objects.equals(moduleName, that.moduleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleId, moduleName);
    }
}
