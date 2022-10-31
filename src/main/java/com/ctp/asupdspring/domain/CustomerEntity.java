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
 * todo Document type CustomerEntity
 */
@Getter
@Setter
@Entity
@Table(name = "customer", schema = "public", catalog = "pm_db_alpha")
public class CustomerEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "customer_id")
    private int customerId;
    @Basic
    @Column(name = "customer_name")
    private String customerName;
    @Basic
    @Column(name = "customer_full_name")
    private String customerFullName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomerEntity that = (CustomerEntity) o;
        return customerId == that.customerId && Objects.equals(customerName, that.customerName) &&
                Objects.equals(customerFullName, that.customerFullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerName, customerFullName);
    }
}
