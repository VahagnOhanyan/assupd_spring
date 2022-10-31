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
public interface ContractRepositoryCustom {
    List<String> getReportByContractNumber(String contractNumber);

    List<Object[]> getMyContractsAll();

    List<Object[]> getMyContractsPM(String user_fullname);
    List<Object[]> getContractsCustomers(String period);
    List<Object[]> getCustomersContractsProjects(String period);
    List<Object[]> getCustomersContractsRequests(String period);
    List<Object[]> getUserContractsCustomers(String period, String userFullname);
    List<Object[]> getUserCustomersContractsProjects(String period, String userFullname);
    List<Object[]> getUserCustomersContractsRequests(String period, String userFullname);
    List<Object[]> getContractsCustomersSubOrd(String period, String userFullname);
    List<Object[]> getCustomersContractsProjectsSubOrd(String period, String userFullname);
    List<Object[]> getCustomersContractsRequestsSubOrd(String period, String userFullname);
}
