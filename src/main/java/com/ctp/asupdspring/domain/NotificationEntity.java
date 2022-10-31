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
 * todo Document type NotificationEntity
 */
@Getter
@Setter
@Entity
@Table(name = "notification", schema = "public", catalog = "pm_db_alpha")
public class NotificationEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "notification_id")
    private int notificationId;
    @ManyToOne
    @JoinColumn(name = "notification_type_id", referencedColumnName = "notification_type_id", nullable = false)
    private NotificationTypeEntity notificationTypeId;
    @Basic
    @Column(name = "table_name")
    private String tableName;

    @Basic
    @Column(name = "notify_entry")
    private int notifyEntry;
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
        NotificationEntity that = (NotificationEntity) o;
        return notificationId == that.notificationId && notificationTypeId == that.notificationTypeId && notifyEntry == that.notifyEntry &&
                statusId == that.statusId && Objects.equals(tableName, that.tableName) && Objects.equals(activeFrom, that.activeFrom) &&
                Objects.equals(activeTo, that.activeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, notificationTypeId, tableName, notifyEntry, statusId, activeFrom, activeTo);
    }
}
