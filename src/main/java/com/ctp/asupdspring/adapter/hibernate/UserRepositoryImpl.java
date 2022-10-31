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

import com.ctp.asupdspring.controllers.MainController;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * todo Document type ContractRepositoryImpl
 */
@Component("userRepositoryImpl")
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final EntityManager entityManager;

    public UserRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Object[]> getEmployeeAll() {
        return entityManager.createQuery(
                        "SELECT u.userIdNumber, u.userFullname, u.userTel, u.userAdress, u.userEmail, " +
                                "s.siteName, ur.userRoleName, ua.userActivityName " +
                                "FROM UserEntity u " +
                                "join UserInfoEntity ui on ui.userInfoId = u.userInfoId " +
                                "join UserRoleEntity ur on ur.userRoleId = ui.userRoleId " +
                                "left join SiteEntity s on s.siteId = u.siteId " +
                                "left join UserActivityEntity ua on ua.userActivityId = u.userActivityId " +
                                "WHERE u.userFullname != 'super_user' " +
                                "ORDER BY u.userFullname", Object[].class)
                .getResultList();
    }

    @Override
    public List<Object[]> getEmployeeSubOrd(String userFullname) {
        return entityManager.createQuery(
                        "SELECT u.userIdNumber, u.userFullname, u.userTel, u.userAdress, u.userEmail, " +
                                "s.siteName, ur.userRoleName, ua.userActivityName " +
                                "FROM UserEntity u " +
                                "left join UserSubordinationEntity us on us.userSubId = u.userId " +
                                "left join UserActivityEntity ua on ua.userActivityId = u.userActivityId " +
                                "join UserInfoEntity ui on ui.userInfoId = u.userInfoId " +
                                "join UserRoleEntity ur on ur.userRoleId = ui.userRoleId " +
                                "left join SiteEntity s on s.siteId = u.siteId " +
                                "WHERE (us.userId = (SELECT userId FROM public.user WHERE userFullname = '" + userFullname +
                                "') OR u.userId = (SELECT userId FROM UserEntity WHERE userFullname = '" + userFullname + "')) " +
                                "GROUP BY u.userIdNumber, u.userFullname, u.userTel, u.userAdress, u.userEmail, ur.userRoleName, s.siteName, ua.userActivityName " +
                                "ORDER BY u.userFullname", Object[].class)
                .getResultList();
    }

    @Override
    public List<Object[]> getEmployeeByName(String userFullname) {
        return entityManager.createQuery(
                        "SELECT u.userIdNumber, u.userFullname, u.userTel, u.userAdress, " +
                                "u.userEmail, s.siteName, ur.userRoleName, ua.userActivityName " +
                                "FROM UserEntity u " +
                                "join UserInfoEntity ui on ui.userInfoId = u.userInfoId " +
                                "join UserRoleEntity ur on ur.userRoleId = ui.userRoleId " +
                                "left join SiteEntity s on s.siteId = u.siteId " +
                                "left join UserActivityEntity ua on ua.userActivityId = u.userActivityId " +
                                "WHERE u.userId = (SELECT userId FROM UserEntity WHERE userFullname = '" + userFullname + "') " +
                                "ORDER BY u.userFullname", Object[].class)
                .getResultList();
    }
    @Override
    public List<Object[]> getEmployeeWorkedIntensity(String user, String date) {
        return entityManager.createQuery("select 'Явка', sum(dw.dailyIntensity) from TaskEntity t" +
                " join StageEntity s on s.taskId = t.taskId " +
                " join StageTypeEntity st on st.stageTypeId = s.stageTypeId" +
                " join StageDailyEntity sd on sd.stageId = s.stageId" +
                " join DailyWorkEntity dw on dw.dailyWorkId = sd.dailyWorkId" +
                " join UserEntity u on u.userId = dw.userId" +
                " where u.userIdNumber = '" + user +
                "' and dw.dailyWorkDate = '" + date + "'", Object[].class).getResultList();
    }
    @Override
    public List<Object[]> getEmployeeAllStagesIntensity(String user_fullname, String date) {
        return entityManager.createQuery("select st.user_stage_type_name, sum(dw.daily_intensity) from public.user_stage s\n" +
                " join public.user_stage_type st on st.user_stage_type_id = s.user_stage_type_id" +
                " join public.user_stage_daily sd on sd.user_stage_id = s.user_stage_id" +
                " join public.daily_work dw on dw.daily_work_id = sd.daily_work_id" +
                " join public.user u on u.user_id = dw.user_id where u.user_id_number = '" + user_fullname +
                "' and to_char(dw.daily_work_date,'MM-yyyy')= '" + date + "'" + " group by st.user_stage_type_name",Object[].class).getResultList();
    }

    @Override
    public List<Object[]> getUsersByPeriod(String period) {
        return entityManager.createNativeQuery("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                "join public.daily_work dw on dw.user_id = u.user_id " +
                "WHERE " + period + " " +
                "GROUP BY u.user_id_number, u.user_fullname HAVING count(u.user_id)>=1 " +
                "ORDER BY u.user_fullname").getResultList();
    }

    @Override
    public List<Object[]> getUsersByPeriodPM(String period, String user) {
        return entityManager.createNativeQuery("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                "join public.daily_work dw on dw.user_id = u.user_id " +
                "join public.stage_daily sd on sd.daily_work_id = dw.daily_work_id " +
                "join public.stage s on s.stage_id = sd.stage_id " +
                "join public.task t on t.task_id = s.task_id " +
                "join public.project p on p.project_id = t.project_id " +
                "join public.project_manager pm on pm.project_id = p.project_id " +
                "WHERE " + period + " AND pm.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '"+ MainController.who +"') " +
                "GROUP BY u.user_id_number, u.user_fullname HAVING count(u.user_id)>=1 " +
                "ORDER BY u.user_fullname").getResultList();
    }

    @Override
    public List<Object[]> getUsersByPeriodSubOrd(String period, String user) {
        return entityManager.createNativeQuery("SELECT u.user_id_number, u.user_fullname FROM public.user u " +
                "join public.daily_work dw on dw.user_id = u.user_id " +
                "left join public.user_subordination us on us.user_sub_id = u.user_id " +
                "WHERE " + period + " AND " +
                "(us.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"') OR u.user_id = (SELECT user_id FROM public.user WHERE user_fullname = '" + MainController.who +"')) " +
                "GROUP BY u.user_id_number, u.user_fullname HAVING count(u.user_id)>=1 " +
                "ORDER BY u.user_fullname").getResultList();
    }
}
