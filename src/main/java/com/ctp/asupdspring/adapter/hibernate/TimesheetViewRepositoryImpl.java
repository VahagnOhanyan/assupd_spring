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

import com.ctp.asupdspring.domain.DailyWorkEntity;
import com.ctp.asupdspring.domain.UserEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

/**
 * todo Document type ContractRepositoryImpl
 */
@Component("timesheetViewRepositoryImpl")
public class TimesheetViewRepositoryImpl implements TimesheetViewRepositoryCustom {

    private final EntityManager entityManager;

    public TimesheetViewRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Secured("Super_user")
    public List<Object[]> getEmployeeAll() {
        List<Object[]> query = entityManager.createQuery(
                        "SELECT u.userIdNumber, u.userFullname, s.siteName, ur.userRoleName " +
                                "FROM UserEntity u " +
                                "join UserInfoEntity ui on ui.userInfoId = u.userInfoId " +
                                "join UserRoleEntity ur on ur.userRoleId = ui.userRoleId " +
                                "left join SiteEntity s on s.siteId = u.siteId " +
                                "WHERE u.userFullname != 'super_user' " +
                                "ORDER BY u.userFullname", Object[].class)
                .getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("userIdNumber");
        columns.add("userFullname");
        columns.add("siteName");
        columns.add("userRoleName");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    @Override
    public List<Object[]> getEmployeeAllDetailed(String startDate, String endDate) {
        Map<UserEntity, List<DailyWorkEntity>> userEntityListMap =
                entityManager.createQuery(
                                "SELECT u, 'percentage', dw " +
                                        "FROM UserEntity u " +
                                        "left join DailyWorkEntity as dw on u.userId = dw.userId " +
                                        "WHERE dw.dailyWorkDate >= '" + startDate + "' and dw.dailyWorkDate < '" + endDate + "' " +
                                        "and u.userIdNumber  != '1'", Tuple.class)
                        .getResultList()
                        .stream()
                        .collect(groupingBy(tuple -> ((UserEntity) tuple.get(0)),
                                Collectors.mapping(tuple -> ((DailyWorkEntity) tuple.get(2)), Collectors.toList())));
        Stream<String> stream = Stream.iterate(1, n -> n + 1).limit(30).map(k -> "day" + k);
        List<String> list = stream.collect(Collectors.toList());
        ArrayList<String> columns = new ArrayList<>();
        columns.add("userIdNumber");
        columns.add("userFullname");
        columns.add("percentage");
        columns.addAll(list);
        Map<UserEntity, List<Map<Integer, Integer>>> newUserEntityListMap = new HashMap<>();
        Map<UserEntity, List<Map<Integer, Integer>>> map = mapToProperties(userEntityListMap, newUserEntityListMap);
        List<Object[]> rows = fillRowsFromMap(new ArrayList<>(), map);
        Object[] metaData = columns.toArray();
        rows.set(0, metaData);
        return rows;
    }

    @Override
    public List<Object[]> getEmployeeByName(String userFullname) {
        List<Object[]> query = entityManager.createQuery(
                        "SELECT u.userIdNumber, u.userFullname, s.siteName, ur.userRoleName " +
                                "FROM UserEntity u " +
                                "left join UserActivityEntity ua on ua.userActivityId = u.userActivityId " +
                                "join UserInfoEntity ui on ui.userInfoId = u.userInfoId " +
                                "join UserRoleEntity ur on ur.userRoleId = ui.userRoleId " +
                                "left join SiteEntity s on s.siteId = u.siteId " +
                                "WHERE u.userId = (SELECT userId FROM UserEntity WHERE userFullname = '" + userFullname + "') " +
                                "ORDER BY u.userFullname", Object[].class)
                .getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("userIdNumber");
        columns.add("userFullname");
        columns.add("siteName");
        columns.add("userRoleName");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

    @Override
    public List<Object[]> getEmployeeSubOrd(String userFullname) {
        List<Object[]> query = entityManager.createQuery(
                        "SELECT u.userIdNumber, u.userFullname, s.siteName, ur.userRoleName " +
                                "FROM UserEntity u " +
                                "left join UserSubordinationEntity us on us.userSubId = u.userId " +
                                "left join UserActivityEntity ua on ua.userActivityId = u.userActivityId " +
                                "join UserInfoEntity ui on ui.userInfoId = u.userInfoId " +
                                "join UserRoleEntity ur on ur.userRoleId = ui.userRoleId " +
                                "left join SiteEntity s on s.siteId = u.siteId " +
                                "WHERE (us.userId = (SELECT userId FROM UserEntity WHERE userFullname = '" + userFullname +
                                "') OR u.userId = (SELECT userId FROM UserEntity WHERE userFullname = '" + userFullname +
                                "')) AND (ua.userActivityName != 'Уволен' OR ua.userActivityName IS NULL) " +
                                "GROUP BY u.userIdNumber, u.userFullname, s.siteName, ur.userRoleName " +
                                "ORDER BY u.userFullname", Object[].class)
                .getResultList();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("userIdNumber");
        columns.add("userFullname");
        columns.add("siteName");
        columns.add("userRoleName");
        Object[] metaData = columns.toArray();
        query.add(0, metaData);
        return query;
    }

/*    @Override
    public List<Object[]> getEmployeeSubOrdDetailed(String userFullname, String date) {
        return entityManager.createQuery(
                        "SELECT * FROM crosstab('select u.userIdNumber, u.userFullname, ''percentage'', dw.dayNum, sum(dw.dailyIntensity) " +
                                "FROM UserEntity u " +
                                "left join UserSubordinationEntity us on us.userSubId = u.userId " +
                                "left join UserActivityEntity ua on ua.userActivityId = u.userActivityId " +
                                "join UserInfoEntity ui on ui.userInfoId = u.userInfoId " +
                                "join UserRoleEntity ur on ur.userRoleId = ui.userRoleId " +
                                "left join (select * from DailyWorkEntity " +
                                "where to_char(dailyWorkDate,''yyyy-MM'') = to_char(date_trunc(''month'', ''" + date +
                                "'' - interval ''0'' month),''yyyy-MM'')) " +
                                "as dw on u.userId = dw.userId " +
                                "WHERE us.userId = (SELECT userId FROM UserEntity WHERE userFullname = ''" + userFullname + "'') " +
                                "AND (ua.userActivityName != ''Уволен'' OR ua.userActivityName IS NULL) " +
                                "GROUP BY u.userIdNumber, u.userFullname, dw.dayNum, ur.userRoleName " +
                                "ORDER BY u.userFullname', " +
                                "'SELECT d from generate_series(1,31) d')" +
                                "AS (userIdNumber text, userFullname text, percentage text, " +
                                "day1 numeric, day2 numeric, day3 numeric, day4 numeric, day5 numeric, " +
                                "day6 numeric, day7 numeric, day8 numeric, day9 numeric, day10 numeric, " +
                                "day11 numeric, day12 numeric, day13 numeric, day14 numeric, day15 numeric," +
                                "day16 numeric, day17 numeric, day18 numeric, day19 numeric, day20 numeric," +
                                "day21 numeric, day22 numeric, day23 numeric, day24 numeric, day25 numeric," +
                                "day26 numeric, day27 numeric, day28 numeric, day29 numeric, day30 numeric, day31 numeric)", Object[].class)
                .getResultList();
    }*/

    @Override
    public List<Object[]> getEmployeeSubOrdDetailed(String userFullname, String startDate, String endDate) {
        Map<UserEntity, List<DailyWorkEntity>> userEntityListMap =
                entityManager.createQuery("select u, 'percentage', dw " +
                                "FROM UserEntity u " +
                                "left join UserSubordinationEntity us on us.userSubId = u.userId " +
                                "left join UserActivityEntity ua on ua.userActivityId = u.userActivityId " +
                                "join UserInfoEntity ui on ui.userInfoId = u.userInfoId " +
                                "join UserRoleEntity ur on ur.userRoleId = ui.userRoleId " +
                                "left join DailyWorkEntity dw on u.userId = dw.userId " +
                                "WHERE dw.dailyWorkDate >= '" + startDate + "' and dw.dailyWorkDate < '" + endDate + "' " +
                                "AND us.userId = (SELECT userId FROM UserEntity WHERE userFullname ='" + userFullname + "') " +
                                "AND (ua.userActivityName != 'Уволен' OR ua.userActivityName IS NULL)", Tuple.class)
                        .getResultList()
                        .stream()
                        .collect(groupingBy(tuple -> ((UserEntity) tuple.get(0)),
                                Collectors.mapping(tuple -> ((DailyWorkEntity) tuple.get(2)), Collectors.toList())));
        Stream<String> stream = Stream.iterate(1, n -> n + 1).limit(30).map(k -> "day" + k);
        List<String> list = stream.collect(Collectors.toList());
        ArrayList<String> columns = new ArrayList<>();
        columns.add("userIdNumber");
        columns.add("userFullname");
        columns.add("percentage");
        columns.addAll(list);
        Object[] metaData = columns.toArray();
        Map<UserEntity, List<Map<Integer, Integer>>> newUserEntityListMap = new HashMap<>();
        Map<UserEntity, List<Map<Integer, Integer>>> map = mapToProperties(userEntityListMap, newUserEntityListMap);
        List<Object[]> rows = fillRowsFromMap(new ArrayList<>(), map);
        rows.set(0, metaData);
        return rows;
    }

    private List<Object[]> fillRowsFromMap(List<Object[]> rows, Map<UserEntity, List<Map<Integer, Integer>>> map) {

        for (UserEntity user : map.keySet()) {

            System.out.println(user.getUserFullname() + ": ");
            Object[] row = new Object[33];
            System.out.println(user.getUserFullname());
            row[0] = user.getUserIdNumber();
            row[1] = user.getUserFullname();
            row[2] = "";
            for (Map<Integer, Integer> daily : map.get(user)) {
                for (Map.Entry<Integer, Integer> dailyEntry : daily.entrySet()) {
                    System.out.println(dailyEntry.getKey());
                    Integer value = dailyEntry.getValue();
                    row[dailyEntry.getKey() + 2] = value;
                }
            }
            rows.add(row);
        }
        return rows;
    }

    private Map<UserEntity, List<Map<Integer, Integer>>> mapToProperties(Map<UserEntity, List<DailyWorkEntity>> userEntityListMap,
            Map<UserEntity, List<Map<Integer, Integer>>> newUserEntityListMap) {
        for (Map.Entry<UserEntity, List<DailyWorkEntity>> entry : userEntityListMap.entrySet()) {
            List<Map<Integer, Integer>> dailyIntensityListForUser = new ArrayList<>();
            List<DailyWorkEntity> dailyWorkEntityList = entry.getValue();
            Map<Integer, Integer> dailyIntensityForUser = dailyWorkEntityList.stream()
                    .collect(Collectors.groupingBy(DailyWorkEntity::getDayNum, Collectors.summingInt(DailyWorkEntity::getDailyIntensity)));
            dailyIntensityListForUser.add(dailyIntensityForUser);
            newUserEntityListMap.put(entry.getKey(), dailyIntensityListForUser);
        }
        return newUserEntityListMap;
    }
}
