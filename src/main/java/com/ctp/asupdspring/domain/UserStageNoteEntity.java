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
 * todo Document type UserStageNoteEntity
 */
@Getter
@Setter
@Entity
@Table(name = "user_stage_note", schema = "public", catalog = "pm_db_alpha")
public class UserStageNoteEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_stage_note_id")
    private int userStageNoteId;
    @Basic
    @Column(name = "user_stage_note_date")
    private Timestamp userStageNoteDate;
    @Basic
    @Column(name = "user_stage_note_text")
    private String userStageNoteText;
    @ManyToOne
    @JoinColumn(name = "user_stage_id", referencedColumnName = "user_stage_id", nullable = false)
    private UserStageEntity userStageId;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserStageNoteEntity that = (UserStageNoteEntity) o;
        return userStageNoteId == that.userStageNoteId && userStageId == that.userStageId && userId == that.userId &&
                Objects.equals(userStageNoteDate, that.userStageNoteDate) && Objects.equals(userStageNoteText, that.userStageNoteText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userStageNoteId, userStageNoteDate, userStageNoteText, userStageId, userId);
    }
}
