package com.ctp.asupdspring.app.repo;

import com.ctp.asupdspring.adapter.hibernate.StageNoteRepositoryCustom;
import com.ctp.asupdspring.domain.StageNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageNoteRepository extends JpaRepository<StageNoteEntity, Integer>, StageNoteRepositoryCustom {
}