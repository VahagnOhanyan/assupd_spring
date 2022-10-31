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

import com.ctp.asupdspring.app.repo.StatusRepository;
import com.ctp.asupdspring.app.repo.TaskRepository;
import com.ctp.asupdspring.app.repo.TaskService;
import com.ctp.asupdspring.domain.StatusEntity;
import com.ctp.asupdspring.domain.TaskEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * todo Document type TaskServiceImpl
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;

    @Override
    public List<TaskEntity> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<TaskEntity> getAllTasksByStatus(String status) throws Exception {
        Optional<StatusEntity> statusEntity = statusRepository.findById(Integer.valueOf(status));
        if (statusEntity.isPresent()) {
            return taskRepository.findByStatusId(statusEntity.get());
        } else {
           return new ArrayList<>();
        }
    }
}
