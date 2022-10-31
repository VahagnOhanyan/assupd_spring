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

import java.util.List;

/**
 * todo Document type ContractRepositoryCustom
 */
public interface DailyWorkRepositoryCustom {
    List<Object[]> getUserBackOfficeSumByPeriod(String userId, String period);

    List<Object[]> getUserVacationSumByPeriod(String userId, String period);

    List<Object[]> getUserHospitalSumByPeriod(String user, String date);

    List<Object[]> getUserEducationSumByPeriod(String user_fullname, String date);

    List<Object[]> getUserIdleSumByPeriod(String user_fullname, String date);

    List<Object[]> getUserDailyIntensitySumByPeriodAndProjectPM(String userId, String period, String userFullname);

    List<Object[]> getUserDailyIntensitySumByPeriod(String user_fullname, String period);

    List<Object[]> getUserDailyIntensitySumByPeriodAndRequestAndProjectPM(String userId, String period, String userFullname);

    List<Object[]> getUserDailyIntensitySumByPeriodAndRequest(String userId, String period);
}
