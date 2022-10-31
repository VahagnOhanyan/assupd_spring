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
import javax.persistence.Query;
import java.util.List;

/**
 * todo Document type TaskExecutorRepositoryImpl
 */
@Component("taskExecutorRepositoryImpl")
public class TaskExecutorRepositoryImpl implements TaskExecutorRepositoryCustom {
    private final EntityManager entityManager;

    public TaskExecutorRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Object[]> getNoTaskExecutors(String taskNum) {
        Query query = entityManager.createNativeQuery("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                "WHERE u.user_id NOT IN (SELECT ur.user_id FROM public.user ur " +
                "join public.task_executor te on te.user_id = ur.user_id " +
                "join public.task t on t.task_id = te.task_id " +
                "WHERE t.task_number = '" + taskNum + "') AND u.user_fullname != 'super_user'" +
                "ORDER BY u.user_fullname");
        return query.getResultList();
    }

    @Override
    public List<Object[]> getTaskExecutors(String taskNum) {
        Query query = entityManager.createNativeQuery("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                "WHERE u.user_id NOT IN (SELECT ur.user_id FROM public.user ur " +
                "join public.task_executor te on te.user_id = ur.user_id " +
                "join public.task t on t.task_id = te.task_id " +
                "WHERE t.task_number = '" + taskNum + "') AND u.user_fullname != 'super_user'" +
                "ORDER BY u.user_fullname");
        return query.getResultList();
    }

    @Override
    public void deleteTaskExecutors(String taskNum) {
        entityManager.createNativeQuery("DELETE FROM public.task_executor " +
                "WHERE task_id = (SELECT task_id FROM public.task WHERE task_number = '" + taskNum + "')");
    }

    @Override
    public void addTaskExecutor(String taskNum, String userIdNumber) {
        entityManager.createNativeQuery("INSERT INTO public.task_executor (task_id, user_id) VALUES " +
                "((SELECT task_id FROM public.task WHERE task_number = '" + taskNum +
                "'), (SELECT user_id FROM public.user WHERE user_id_number = '" + userIdNumber + "'))");
    }
}
