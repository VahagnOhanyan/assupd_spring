package com.ctp.asupdspring.app.repo;

import com.ctp.asupdspring.adapter.hibernate.DailyWorkRepositoryCustom;
import com.ctp.asupdspring.domain.DailyWorkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyWorkRepository extends JpaRepository<DailyWorkEntity, Integer>, DailyWorkRepositoryCustom {
}