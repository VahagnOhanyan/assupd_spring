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

import com.ctp.asupdspring.app.repo.StageRepository;
import com.ctp.asupdspring.app.repo.StageService;
import com.ctp.asupdspring.app.repo.TaskRepository;
import com.ctp.asupdspring.domain.StageEntity;
import com.ctp.asupdspring.domain.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * todo Document type StageServiceImpl
 */
@Service
@RequiredArgsConstructor
public class StageServiceImpl implements StageService {
    private final StageRepository stageRepository;
    private final TaskRepository taskRepository;

    @Override
    public List<StageEntity> getAllStages() {
        return stageRepository.findAll();
    }

    @Override
    public List<StageEntity> getAllStagesByTaskId(String taskId) {
        Optional<TaskEntity> task = taskRepository.findById(Integer.valueOf(taskId));
        if (task.isPresent()) {
            return stageRepository.findByTaskId(task.get());
        } else {
            return new ArrayList<>();
        }
    }
}
