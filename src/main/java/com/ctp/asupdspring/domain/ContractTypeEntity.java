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
 * todo Document type ContractTypeEntity
 */
@Getter
@Setter
@Entity
@Table(name = "contract_type", schema = "public", catalog = "pm_db_alpha")
public class ContractTypeEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "contract_type_id")
    private int contractTypeId;
    @Basic
    @Column(name = "contract_type_name")
    private String contractTypeName;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContractTypeEntity that = (ContractTypeEntity) o;
        return contractTypeId == that.contractTypeId && Objects.equals(contractTypeName, that.contractTypeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractTypeId, contractTypeName);
    }

}
