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
 * todo Document type SystemBuildEntity
 */
@Getter
@Setter
@Entity
@Table(name = "system_build", schema = "public", catalog = "pm_db_alpha")
public class SystemBuildEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "system_build_id")
    private int systemBuildId;
    @Basic
    @Column(name = "system_build_version")
    private String systemBuildVersion;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SystemBuildEntity that = (SystemBuildEntity) o;
        return systemBuildId == that.systemBuildId && Objects.equals(systemBuildVersion, that.systemBuildVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(systemBuildId, systemBuildVersion);
    }
}
