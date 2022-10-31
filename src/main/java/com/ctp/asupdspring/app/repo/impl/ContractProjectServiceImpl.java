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

import com.ctp.asupdspring.app.repo.ContractProjectRepository;
import com.ctp.asupdspring.app.repo.ContractProjectService;
import com.ctp.asupdspring.domain.ContractProjectEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * todo Document type ContractProjectServiceImpl
 */
@RequiredArgsConstructor
@Service
public class ContractProjectServiceImpl implements ContractProjectService {

    private final ContractProjectRepository contractProjectRepository;

    @Override
    public List<ContractProjectEntity> getAllContractsProjects() {
        return contractProjectRepository.findAll();
    }
}
