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

import lombok.*;

import javax.persistence.*;

/**
 * todo Document type User
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "privileges")
public class PrivilegeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "privilege_id")
    private int privilegeId;

    @Column(name = "privilege_name")
    private String privilegeName;
    @ManyToOne
    @JoinColumn(name = "access_type_id", referencedColumnName = "access_type_id")
    private AccessTypeEntity accessTypeId;

}
