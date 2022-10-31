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
import java.sql.Timestamp;
import java.util.Objects;

/**
 * todo Document type ContractProjectEntity
 */
@Getter
@Setter
@Entity
@Table(name = "contract_project", schema = "public", catalog = "pm_db_alpha")
public class ContractProjectEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "contract_project_id")
    private int contractProjectId;
    @ManyToOne
    @JoinColumn(name = "contract_id", referencedColumnName = "contract_id", nullable = false)
    private ContractEntity contractId;
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private ProjectEntity projectId;
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "status_id", nullable = false)
    private StatusEntity statusId;
    @Basic
    @Column(name = "active_from")
    private Timestamp activeFrom;
    @Basic
    @Column(name = "active_to")
    private Timestamp activeTo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContractProjectEntity that = (ContractProjectEntity) o;
        return contractProjectId == that.contractProjectId && contractId == that.contractId && projectId == that.projectId && statusId == that.statusId &&
                Objects.equals(activeFrom, that.activeFrom) && Objects.equals(activeTo, that.activeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractProjectId, contractId, projectId, statusId, activeFrom, activeTo);
    }
}
