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
package com.ctp.asupdspring.adapter.rest;

import com.ctp.asupdspring.app.repo.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * todo Document type UserRestController
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("rest/report")
public class ReportRestController {
    private final ContractRepository contractRepository;

    @GetMapping(value = "/contract/{number}")
    public ResponseEntity<List<String>> getAllContractsProjects(@PathVariable("number") String contractNumber) {
        return new ResponseEntity<>(contractRepository.getReportByContractNumber(contractNumber), HttpStatus.OK);
    }
}
