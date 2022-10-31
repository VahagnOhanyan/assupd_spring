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
package com.ctp.asupdspring.app.repo.impl;

import com.ctp.asupdspring.app.repo.CalendarRepository;
import com.ctp.asupdspring.app.repo.CalendarService;
import com.ctp.asupdspring.domain.CalendarEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * todo Document type UserServiceImpl
 */
@RequiredArgsConstructor
@Service
public class CalendarServiceImpl implements CalendarService {
    private final CalendarRepository calendarRepository;

    @Override
    public List<CalendarEntity> getAllCalendar() {
        return calendarRepository.findAll();
    }

    @Override
    public List<CalendarEntity> getCalendarBy(int year, int month) {
        return calendarRepository.findByYearAndMonth(year, month);
    }

    public long deleteCalendarBy(int year, int month, int day, String type) {
        return calendarRepository.deleteByYearAndMonthAndDayAndType(year,month,day,type);
    }
    public long insertCalendarBy(int year, int month, int day, String type) {
        return calendarRepository.insertYearAndMonthAndDayAndType(year,month,day,type);
    }
}
