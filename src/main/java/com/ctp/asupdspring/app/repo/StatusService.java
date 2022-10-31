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

import com.ctp.asupdspring.domain.PrivilegeEntity;
import com.ctp.asupdspring.domain.StatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * todo Document type UserService
 */
public interface StatusService {

    List<StatusEntity> getAllStatuses();

    interface PrivilegeEntityRepository extends JpaRepository<PrivilegeEntity, Integer> {

    }
}
