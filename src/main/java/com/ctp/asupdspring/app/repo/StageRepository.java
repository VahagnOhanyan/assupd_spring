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

import com.ctp.asupdspring.domain.StageEntity;
import com.ctp.asupdspring.domain.StageTypeEntity;
import com.ctp.asupdspring.domain.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * todo Document type UserRepository
 */
@Repository
public interface StageRepository extends JpaRepository<StageEntity, Integer> {
    Optional<StageEntity> findByStageId(int stageId);

    List<StageEntity> findByStageTypeId(StageTypeEntity stageTypeId);

    List<StageEntity> findByTaskId(TaskEntity taskId);


}
