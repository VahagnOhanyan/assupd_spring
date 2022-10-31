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

import com.ctp.asupdspring.app.repo.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * todo Document type UserRestController
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("rest/sites")
public class SiteRestController {
    private final SiteService siteService;

   /* @GetMapping(value = "/all")
    public ResponseEntity<List<SiteEntity>> getAllUsers() {
        return new ResponseEntity<>(siteService.getAllSites(), HttpStatus.OK);
    }*/
}
