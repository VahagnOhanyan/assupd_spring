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
 * todo Document type ContractEntity
 */
@Getter
@Setter
@Entity
@Table(name = "contract", schema = "public", catalog = "pm_db_alpha")
public class ContractEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "contract_id")
    private int contractId;
    @Basic
    @Column(name = "contract_number")
    private String contractNumber;
    @Basic
    @Column(name = "contract_name")
    private String contractName;
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private CustomerEntity customerId;
    @ManyToOne
    @JoinColumn(name = "contract_type_id", referencedColumnName = "contract_type_id")
    private ContractTypeEntity contractTypeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContractEntity that = (ContractEntity) o;
        return contractId == that.contractId && customerId == that.customerId && Objects.equals(contractNumber, that.contractNumber) &&
                Objects.equals(contractName, that.contractName) && Objects.equals(contractTypeId, that.contractTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractId, contractNumber, contractName, customerId, contractTypeId);
    }
}
