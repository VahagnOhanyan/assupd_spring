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
 * todo Document type StageNoteEntity
 */
@Getter
@Setter
@Entity
@Table(name = "stage_note", schema = "public", catalog = "pm_db_alpha")
public class StageNoteEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "stage_note_id")
    private int stageNoteId;
    @Basic
    @Column(name = "stage_note_date")
    private Timestamp stageNoteDate;
    @Basic
    @Column(name = "stage_note_text")
    private String stageNoteText;
    @ManyToOne
    @JoinColumn(name = "stage_id", referencedColumnName = "stage_id", nullable = false)
    private StageEntity stageId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserEntity userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StageNoteEntity that = (StageNoteEntity) o;
        return stageNoteId == that.stageNoteId && stageId == that.stageId && Objects.equals(stageNoteDate, that.stageNoteDate) &&
                Objects.equals(stageNoteText, that.stageNoteText) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stageNoteId, stageNoteDate, stageNoteText, stageId, userId);
    }
}
