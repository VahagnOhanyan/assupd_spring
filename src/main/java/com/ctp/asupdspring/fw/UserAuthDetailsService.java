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
package com.ctp.asupdspring.fw;

import com.ctp.asupdspring.app.repo.UserInfoRepository;
import com.ctp.asupdspring.domain.UserInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * todo Document type UserDetailsService
 */
@RequiredArgsConstructor
@Service
public class UserAuthDetailsService implements UserDetailsService {
    private final UserInfoRepository userInfoRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        Optional<UserInfoEntity> userInfo = userInfoRepository.findByUserLogin(username);
        if (userInfo.isPresent()) {
            UserInfoEntity user = userInfo.get();
            System.out.println("UserInfoEntity: " + user.getUserLogin());
            System.out.println("user.getPrivileges():" + user.getPrivileges());
            List<GrantedAuthority> grantedAuthorities = user.getPrivileges().stream().map(e -> new SimpleGrantedAuthority(e.getPrivilegeName() + ":" + e.getAccessTypeId().getAccessTypeId())).collect(
                    Collectors.toList());

            grantedAuthorities.add(new SimpleGrantedAuthority(user.getUserRoleId().getUserRoleName()));
            return new UserAuthDetails(user.getUserLogin(), user.getUserPass(), grantedAuthorities);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
