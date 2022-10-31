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
package com.ctp.asupdspring.adapter.hibernate;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * todo Document type ContractRepositoryImpl
 */
@Component("stageNoteRepositoryImpl")
public class StageNoteRepositoryImpl implements StageNoteRepositoryCustom {

    private final EntityManager entityManager;

    public StageNoteRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Object[]> getUserTaskStageComments(String task, String stage, String userIdNumber) {
        return entityManager.createNativeQuery(
                        "SELECT to_char(sn.stage_note_date, 'DD-MM-YYYY HH24:MI:SS'), sn.stage_note_text, u.user_fullname FROM public.stage_note sn " +
                                "left join public.user u on u.user_id = sn.user_id " +
                                "WHERE stage_id = (SELECT stage_id FROM public.stage s " +
                                "join public.task t on t.task_id = s.task_id " +
                                "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                                "WHERE t.task_number = '" + task + "' AND st.stage_type_name = '" + stage + "') AND u.user_id_number = '" + userIdNumber + "'")
                .getResultList();
    }

    @Override
    public List<Object[]> getUserStageComments(String stage, String userIdNumber) {
        return entityManager.createNativeQuery(
                "SELECT to_char(sn.user_stage_note_date, 'DD-MM-YYYY HH24:MI:SS'), sn.user_stage_note_text, u.user_fullname FROM public.user_stage_note sn " +
                                        "left join public.user u on u.user_id = sn.user_id " +
                                        "WHERE user_stage_id = (SELECT user_stage_id FROM public.user_stage s " +
                                        "join public.user ur on ur.user_id = s.user_id " +
                                        "join public.user_stage_type st on st.user_stage_type_id = s.user_stage_type_id " +
                                        "WHERE ur.user_id_number = '" + userIdNumber + "' AND st.user_stage_type_name = '" + stage + "')")
                .getResultList();
    }
}
