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
package com.ctp.asupdspring.app.repo;

import com.ctp.asupdspring.domain.CalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * todo Document type UserRepository
 */
@Repository
public interface CalendarRepository extends JpaRepository<CalendarEntity, Integer> {

    @Override
    List<CalendarEntity> findAll();

    long deleteByYearAndMonthAndDayAndType(int year, int month, int day, String type);

    List<CalendarEntity> findByYearAndMonth(int year, int month);



    @Transactional
    @Modifying
    @Query(value = "insert into CalendarEntity (year, month, day, type) values (:year,:month,:day,:type)", nativeQuery = true)
    int insertYearAndMonthAndDayAndType(@Param("year") int year, @Param("month") int month, @Param("day") int day, @Param("type") String type);
}
