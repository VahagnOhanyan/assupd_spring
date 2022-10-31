package com.ctp.asupdspring.app.repo;

import com.ctp.asupdspring.domain.PrivilegeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Integer> {
}