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

import com.ctp.asupdspring.adapter.hibernate.TaskRepositoryCustom;
import com.ctp.asupdspring.domain.ProjectEntity;
import com.ctp.asupdspring.domain.RequestEntity;
import com.ctp.asupdspring.domain.StatusEntity;
import com.ctp.asupdspring.domain.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * todo Document type UserRepository
 */
@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer>, TaskRepositoryCustom {
    Optional<TaskEntity> findByTaskId(int taskId);

    List<TaskEntity> findByProjectId(ProjectEntity projectId);

    List<TaskEntity> findByRequestId(RequestEntity requestId);

    List<TaskEntity> findByStatusId(StatusEntity status);

    long deleteByTaskNumber(String taskNumber);




}
