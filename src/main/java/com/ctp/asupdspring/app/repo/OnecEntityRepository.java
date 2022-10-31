package com.ctp.asupdspring.app.repo;

import com.ctp.asupdspring.domain.OnecEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface OnecEntityRepository extends JpaRepository<OnecEntity, String> {
    @Transactional
    @Modifying
    @Query(value = "insert into OnecEntity (year, month, day, type) values (:year,:month,:day,:type)", nativeQuery = true)
    int insertYearAndMonthAndDayAndType(@Param("year") int year, @Param("month") int month, @Param("day") int day, @Param("type") String type);

    @Transactional
    @Modifying
    @Query("update OnecEntity o set o.num = ?1 where o.num = ?2")
    int updateNumByNum(String num, String num1);

    @Transactional
    @Modifying
    @Query("update OnecEntity o set o.num = ?7, o.fullname = ?1, o.monthyear = ?2, o.worked = ?3, o.hospital = ?4, o.vacation = ?5, o.total = ?6 " +
            "where o.num = ?7")
    void updateByNum(String fullname, String monthyear, Double worked, Double hospital,
            Double vacation, double total, String num);

    List<OnecEntity> findByMonthyear(String monthyear);





}