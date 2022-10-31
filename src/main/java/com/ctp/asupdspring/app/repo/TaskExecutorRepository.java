package com.ctp.asupdspring.app.repo;

import com.ctp.asupdspring.adapter.hibernate.TaskExecutorRepositoryCustom;
import com.ctp.asupdspring.domain.TaskExecutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskExecutorRepository extends JpaRepository<TaskExecutorEntity, Integer>, TaskExecutorRepositoryCustom {

}