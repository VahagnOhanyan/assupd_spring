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
public interface TimesheetViewRepositoryCustom {
    List<Object[]> getEmployeeAll();
    List<Object[]> getEmployeeAllDetailed(String startDate, String endDate);
    List<Object[]> getEmployeeSubOrd(String user_fullnamee);
    List<Object[]> getEmployeeSubOrdDetailed(String userFullname, String startDate, String endDate);
    List<Object[]> getEmployeeByName(String user_fullname);


}
