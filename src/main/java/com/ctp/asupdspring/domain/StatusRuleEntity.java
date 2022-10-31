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
 * todo Document type StatusRuleEntity
 */
@Getter
@Setter
@Entity
@Table(name = "status_rule", schema = "public", catalog = "pm_db_alpha")
public class StatusRuleEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "status_rule_id")
    private int statusRuleId;
    @ManyToOne
    @JoinColumn(name = "status_rule_from", referencedColumnName = "status_id", nullable = false)
    private StatusEntity statusRuleFrom;
    @Basic
    @Column(name = "status_rule_cond")
    private int statusRuleCond;
    @ManyToOne
    @JoinColumn(name = "status_rule_to", referencedColumnName = "status_id", nullable = false)
    private StatusEntity statusRuleTo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatusRuleEntity that = (StatusRuleEntity) o;
        return statusRuleId == that.statusRuleId && statusRuleFrom == that.statusRuleFrom && statusRuleCond == that.statusRuleCond &&
                statusRuleTo == that.statusRuleTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusRuleId, statusRuleFrom, statusRuleCond, statusRuleTo);
    }
}
