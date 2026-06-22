package com.example.demo.repository;

import com.example.demo.model.Activity;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByUserOrderByDateDescIdDesc(User user);

    List<Activity> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    long countByUser(User user);

    @Query("select coalesce(sum(a.co2Kg), 0) from Activity a where a.user = :user")
    double totalCo2(@Param("user") User user);

    @Query("select coalesce(sum(a.co2Kg), 0) from Activity a "
            + "where a.user = :user and a.date between :start and :end")
    double totalCo2Between(@Param("user") User user,
                           @Param("start") LocalDate start,
                           @Param("end") LocalDate end);

    /** Distinct dates a user logged something, newest first (for streak calc). */
    @Query("select distinct a.date from Activity a where a.user = :user order by a.date desc")
    List<LocalDate> findDistinctDates(@Param("user") User user);

    long countByUserAndType(User user, com.example.demo.model.ActivityType type);
}
