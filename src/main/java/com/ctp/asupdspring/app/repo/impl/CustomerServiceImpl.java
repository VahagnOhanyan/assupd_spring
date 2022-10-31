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

import com.ctp.asupdspring.app.repo.CustomerRepository;
import com.ctp.asupdspring.app.repo.CustomerService;
import com.ctp.asupdspring.domain.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * todo Document type CustomerServiceImpl
 */
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {
      private  final CustomerRepository customerRepository;
    @Override
    public List<CustomerEntity> getAllCustomers() {
        return customerRepository.findAll();
    }
}
