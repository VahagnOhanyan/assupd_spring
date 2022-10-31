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
import java.io.Serializable;
import java.util.Objects;

/**
 * todo Document type OnecEntityPK
 */
@Getter
@Setter
@Entity
@Table
public class OnecEntityPK implements Serializable {
    @Column(name = "num")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String num;
    @Column(name = "monthyear")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String monthyear;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OnecEntityPK that = (OnecEntityPK) o;
        return Objects.equals(num, that.num) && Objects.equals(monthyear, that.monthyear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, monthyear);
    }
}
