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
 * todo Document type RequestEntity
 */
@Getter
@Setter
@Entity
@Table(name = "request", schema = "public", catalog = "pm_db_alpha")
public class RequestEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "request_id")
    private int requestId;
    @Basic
    @Column(name = "request_number")
    private String requestNumber;
    @Basic
    @Column(name = "request_description")
    private String requestDescription;
    @ManyToOne
    @JoinColumn(name = "contract_id", referencedColumnName = "contract_id", nullable = false)
    private ContractEntity contractId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestEntity that = (RequestEntity) o;
        return requestId == that.requestId && contractId == that.contractId && Objects.equals(requestNumber, that.requestNumber) &&
                Objects.equals(requestDescription, that.requestDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, requestNumber, requestDescription, contractId);
    }
}
