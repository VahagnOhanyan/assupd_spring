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
@Component("dailyWorkRepositoryImpl")
public class DailyWorkRepositoryImpl implements DailyWorkRepositoryCustom {

    private final EntityManager entityManager;

    public DailyWorkRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Object[]> getUserBackOfficeSumByPeriod(String userId, String period) {
        return entityManager.createNativeQuery(
                        "SELECT sum(daily_intensity) FROM public.daily_work dw " +
                                "join public.user_stage_daily usd on usd.daily_work_id = dw.daily_work_id " +
                                "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                                "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                                "join public.user u on u.user_id = us.user_id " +
                                "WHERE u.user_id_number = '" + userId + "' AND ust.user_stage_type_name = 'Работы по бэк-офису' AND " + period + "")
                .getResultList();
    }



    @Override
    public List<Object[]> getUserVacationSumByPeriod(String userId, String period) {
        return entityManager.createNativeQuery(
                        "SELECT sum(daily_intensity) FROM public.daily_work dw " +
                                "join public.user_stage_daily usd on usd.daily_work_id = dw.daily_work_id " +
                                "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                                "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                                "join public.user u on u.user_id = us.user_id " +
                                "WHERE u.user_id_number = '" + userId + "' AND ust.user_stage_type_name = 'Отпуск' AND " + period + "")
                .getResultList();
    }

    @Override
    public List<Object[]> getUserHospitalSumByPeriod(String userId, String period) {
        return entityManager.createNativeQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                "join public.user_stage_daily usd on usd.daily_work_id = dw.daily_work_id " +
                "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                "join public.user u on u.user_id = us.user_id " +
                "WHERE u.user_id_number = '" + userId + "' AND ust.user_stage_type_name = 'Больничный' AND " + period + "").getResultList();
    }

    @Override
    public List<Object[]> getUserEducationSumByPeriod(String userId, String period) {
        return entityManager.createNativeQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                "join public.user_stage_daily usd on usd.daily_work_id = dw.daily_work_id " +
                "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                "join public.user u on u.user_id = us.user_id " +
                "WHERE u.user_id_number = '" + userId + "' AND ust.user_stage_type_name = 'Обучение' AND " + period + "").getResultList();
    }

    @Override
    public List<Object[]> getUserIdleSumByPeriod(String userId, String period) {
        return entityManager.createNativeQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                "join public.user_stage_daily usd on usd.daily_work_id = dw.daily_work_id " +
                "join public.user_stage us on us.user_stage_id = usd.user_stage_id " +
                "join public.user_stage_type ust on ust.user_stage_type_id = us.user_stage_type_id " +
                "join public.user u on u.user_id = us.user_id " +
                "WHERE u.user_id_number = '" + userId + "' AND ust.user_stage_type_name = 'IDLE' AND " + period + "").getResultList();
    }

    @Override
    public List<Object[]> getUserDailyIntensitySumByPeriodAndProjectPM(String userId, String period, String userFullName) {
        return entityManager.createNativeQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "join public.stage s on s.stage_id = sd.stage_id " +
                "join public.task t on t.task_id = s.task_id " +
                "join public.project p on p.project_id = t.project_id " +
                "join public.project_manager pm on pm.project_id = p.project_id " +
                "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                "WHERE u.user_id_number = '" + userId + "' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' OR st.stage_type_name = 'Утверждение') " +
                "AND " + period + " AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '"+ userFullName +"')").getResultList();
    }

    @Override
    public List<Object[]> getUserDailyIntensitySumByPeriod(String userId, String period) {
        return entityManager.createNativeQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "join public.stage s on s.stage_id = sd.stage_id " +
                "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                "WHERE u.user_id_number = '" + userId + "' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' OR st.stage_type_name = 'Утверждение') " +
                "AND " + period + "").getResultList();
    }

    @Override
    public List<Object[]> getUserDailyIntensitySumByPeriodAndRequestAndProjectPM(String userId, String period, String userFullname) {
        return entityManager.createNativeQuery(
                        "SELECT sum(daily_intensity) FROM public.daily_work dw " +
                                "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                                "join public.user u on u.user_id = dw.user_id " +
                                "join public.stage s on s.stage_id = sd.stage_id " +
                                "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                                "join public.task t on t.task_id = s.task_id " +
                                "join public.project p on p.project_id = t.project_id " +
                                "join public.project_manager pm on pm.project_id = p.project_id " +
                                "join public.request r on r.request_id = t.request_id " +
                                "WHERE u.user_id_number = '" + userId + "' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' OR st.stage_type_name = 'Утверждение') " +
                                "AND " + period + " AND r.request_id IN (SELECT request_id FROM public.request) AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '"+ userFullname +"')")
                .getResultList();
    }

    @Override
    public List<Object[]> getUserDailyIntensitySumByPeriodAndRequest(String userId, String period) {
        return entityManager.createNativeQuery("SELECT sum(daily_intensity) FROM public.daily_work dw " +
                "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                "join public.user u on u.user_id = dw.user_id " +
                "join public.stage s on s.stage_id = sd.stage_id " +
                "join public.stage_type st on st.stage_type_id = s.stage_type_id " +
                "join public.task t on t.task_id = s.task_id " +
                "join public.request r on r.request_id = t.request_id " +
                "WHERE u.user_id_number = '" + userId + "' AND (st.stage_type_name = 'Подготовка' OR st.stage_type_name = 'ПА' OR st.stage_type_name = 'КЭМ' OR st.stage_type_name = 'Расчет' OR st.stage_type_name = 'Оформление' OR st.stage_type_name = 'Сопровождение' OR st.stage_type_name = 'Проверка' OR st.stage_type_name = 'Утверждение') " +
                "AND " + period + " AND r.request_id IN (SELECT request_id FROM public.request)").getResultList();
    }



}
