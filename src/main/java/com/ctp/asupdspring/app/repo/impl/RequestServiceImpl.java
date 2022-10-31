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

import com.ctp.asupdspring.app.repo.RequestRepository;
import com.ctp.asupdspring.app.repo.RequestService;
import com.ctp.asupdspring.domain.RequestEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * todo Document type RequestServiceImpl
 */
@RequiredArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    @Override
    public List<RequestEntity> getAllRequests() {
        return requestRepository.findAll();
    }
}
