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
public interface RequestRepositoryCustom {
    List<Object[]> getMyRequestsAll();
    List<Object[]> getMyRequestsPM(String user_fullname);
    List<Object[]> getContracts(String user_fullname);
    List<Object[]> getUserContracts(String customerId, String current);
    void addRequest(String requestNumber, String requestDesc, String contractId);
    void updateContract(String contractId, String requestId);
    void updateRequest(String requestNumber, String requestId);
    void updateRequestDesc(String requestDesc, String requestId);
    List<Object[]> getCustomers();
    List<Object[]> getContractsBy(String requestId);

}
