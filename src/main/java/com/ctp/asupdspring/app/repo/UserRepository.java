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

import com.ctp.asupdspring.adapter.hibernate.UserRepositoryCustom;
import com.ctp.asupdspring.domain.SiteEntity;
import com.ctp.asupdspring.domain.UserEntity;
import com.ctp.asupdspring.domain.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * todo Document type UserRepository
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>, UserRepositoryCustom {
    Optional<UserEntity> findByUserId(int userId);

    Optional<UserEntity> findByUserInfoId(UserInfoEntity userInfoId);

    List<UserEntity> findBySiteId(SiteEntity siteId);

}
