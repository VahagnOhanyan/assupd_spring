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
import com.ctp.asupdspring.app.repo.StatusService;
import com.ctp.asupdspring.domain.StatusEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * todo Document type StatusServiceImpl
 */
@RequiredArgsConstructor
@Service
public class StatusServiceImpl implements StatusService {
    private final StatusRepository statusRepository;
    @Override
    public List<StatusEntity> getAllStatuses() {
        return statusRepository.findAll();
    }

}
