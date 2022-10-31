package com.ctp.asupdspring.app.repo;

import com.ctp.asupdspring.domain.OnecImportedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OnecImportedEntityRepository extends JpaRepository<OnecImportedEntity, String> {
    @Transactional
    @Modifying
    @Query("update OnecImportedEntity o set o.monthyear = ?1")
    int update(String monthyear);
}